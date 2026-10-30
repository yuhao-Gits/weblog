#!/usr/bin/env bash
# ============================================================
# smoke-test.sh —— 冒烟测试（部署后健康校验）
# 校验项：
#   1) /health        -> 200（后端 Actuator 经 Nginx 透传）
#   2) /              -> 200（前端首页静态资源）
#   3) /api/chat/online -> 200 且返回 JSON（后端接口 + 去前缀反代）
#   4) /files/        -> 非 502/504（Nginx 能连通 Minio，即路由可达）
#   5) /api/chat/ws   -> 101（WebSocket 握手升级）
# 用法：BASE_URL=http://localhost:80 ./deploy/scripts/smoke-test.sh
# 退出码：0 全部通过；1 任一失败
# ============================================================
set -uo pipefail

BASE_URL="${BASE_URL:-http://localhost:80}"
PASS=0
FAIL=0

# 颜色（无终端时自动退化）
if [[ -t 1 ]]; then
  RED=$'\033[31m'; GREEN=$'\033[32m'; NC=$'\033[0m'
else
  RED=""; GREEN=""; NC=""
fi

check() {
  local name="$1"; local expect="$2"; local got="$3"
  if [[ "${got}" == "${expect}" ]]; then
    echo -e "${GREEN}[PASS]${NC} ${name} (${got})"
    PASS=$((PASS + 1))
  else
    echo -e "${RED}[FAIL]${NC} ${name}：期望 ${expect}，实际 ${got}"
    FAIL=$((FAIL + 1))
  fi
}

echo ">> 冒烟测试开始：${BASE_URL}"

# 1) 健康检查
code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "${BASE_URL}/health" || true)
check "GET /health -> 200" "200" "${code}"

# 2) 首页
code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "${BASE_URL}/" || true)
check "GET / -> 200" "200" "${code}"

# 3) API 返回 JSON（/api/chat/online 为公开接口）
api_out=$(curl -s --max-time 10 "${BASE_URL}/api/chat/online" || true)
api_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "${BASE_URL}/api/chat/online" || true)
if [[ "${api_code}" == "200" && "${api_out}" == *"\"code\""* ]]; then
  check "GET /api/chat/online -> 200+JSON" "200" "${api_code}"
else
  echo -e "${RED}[FAIL]${NC} GET /api/chat/online 未返回预期 JSON（HTTP ${api_code}）"
  FAIL=$((FAIL + 1))
fi

# 4) /files 路由可达（Minio 匿名读；不存在的对象返回 404/403 均视为可达，502/504 视为 Minio 不可达）
files_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "${BASE_URL}/files/__smoke_probe__" || true)
if [[ "${files_code}" != "502" && "${files_code}" != "504" && "${files_code}" != "000" ]]; then
  echo -e "${GREEN}[PASS]${NC} GET /files/ -> Minio 可达 (${files_code})"
  PASS=$((PASS + 1))
else
  echo -e "${RED}[FAIL]${NC} GET /files/ Minio 不可达（HTTP ${files_code}）"
  FAIL=$((FAIL + 1))
fi

# 5) WebSocket 握手（期望 101 Switching Protocols）
ws_code=$(curl --http1.1 -s -o /dev/null -w "%{http_code}" --max-time 10 \
  -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  -H "Sec-WebSocket-Version: 13" \
  -H "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==" \
  "${BASE_URL}/api/chat/ws" || true)
check "GET /api/chat/ws -> 101(WS)" "101" "${ws_code}"

echo ""
echo ">> 冒烟测试结果：PASS=${PASS} FAIL=${FAIL}"

if [[ ${FAIL} -gt 0 ]]; then
  exit 1
fi
exit 0
