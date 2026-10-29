#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Weblog 1.0 后端接口全量测试脚本"""
import urllib.request
import urllib.error
import json
import sys
import time

BASE = "http://localhost:8080"
PASS = 0
FAIL = 0
FAILED = []


def req(method, path, data=None, token=None, expect_code=None):
    url = BASE + path
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = "Bearer " + token
    body = json.dumps(data).encode() if data is not None else None
    r = urllib.request.Request(url, data=body, headers=headers, method=method)
    try:
        resp = urllib.request.urlopen(r, timeout=10)
        status = resp.status
        payload = json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        status = e.code
        try:
            payload = json.loads(e.read().decode())
        except Exception:
            payload = {}
    if expect_code is not None:
        ok = status == expect_code
    else:
        ok = status == 200
    return status, payload, ok


def check(name, ok, extra=""):
    global PASS, FAIL
    if ok:
        PASS += 1
        print(f"  ✅ {name} {extra}")
    else:
        FAIL += 1
        FAILED.append(name)
        print(f"  ❌ {name} {extra}")


def api_ok(status, payload):
    return status == 200 and payload.get("code") == "10000"


def get_pass_token():
    """走完整滑块验证码流程，返回 passToken（供登录使用）"""
    # 1. 生成验证码
    s, p, ok = req("GET", "/captcha/generate")
    if not api_ok(s, p):
        return None
    token = p["data"]["token"]
    # 2. 遍历缺口 x（范围 [48, 272]，容差 6px）找到正确位置，获取 passToken
    import urllib.parse
    for x in range(48, 273):
        url = BASE + "/captcha/verify?token=" + token + "&x=" + str(x)
        r = urllib.request.Request(url, method="POST")
        try:
            resp = urllib.request.urlopen(r, timeout=10)
            payload = json.loads(resp.read().decode())
        except Exception:
            continue
        if payload.get("code") == "10000":
            return payload.get("data", {}).get("passToken")
    return None


def login_with_captcha(username, password):
    """带验证码登录，返回 (status, payload, ok)"""
    pass_token = get_pass_token()
    if pass_token is None:
        return 200, {"code": "20000", "message": "无法获取验证码"}, False
    return req("POST", "/admin/login", {"username": username, "password": password, "captchaToken": pass_token})


print("=" * 60)
print("1. 登录模块")
print("=" * 60)
s, p, ok = login_with_captcha("admin", "123456")
check("管理员登录(带验证码)", api_ok(s, p), f"code={p.get('code')}")
token_admin = p.get("data", {}).get("token", "") if api_ok(s, p) else ""
check("登录返回 token", bool(token_admin))

# 密码错误：先用正确验证码走流程，密码错误应返回 20002
s, p, ok = login_with_captcha("admin", "wrong")
check("密码错误被拒(20002)", api_ok(s, p) is False and p.get("code") == "20002", f"code={p.get('code')}")

s, p, ok = req("POST", "/admin/login", {"username": "", "password": ""})
check("空参数校验失败(400)", s == 400, f"status={s}")

s, p, ok = req("GET", "/admin/user/info", token=token_admin)
check("获取当前用户信息", api_ok(s, p), f"username={p.get('data',{}).get('username')}")

s, p, ok = login_with_captcha("test", "test")
check("游客登录(带验证码)", api_ok(s, p), f"code={p.get('code')}")
token_guest = p.get("data", {}).get("token", "") if api_ok(s, p) else ""

print()
print("=" * 60)
print("2. 角色鉴权（游客只读）")
print("=" * 60)
s, p, ok = req("POST", "/admin/category/add", {"name": "游客测试分类"}, token=token_guest, expect_code=403)
check("游客新增分类被拒(403)", ok, f"status={s}")
s, p, ok = req("POST", "/admin/article/publish",
               {"title": "游客文章", "content": "x", "categoryId": 1}, token=token_guest, expect_code=403)
check("游客发布文章被拒(403)", ok, f"status={s}")
s, p, ok = req("POST", "/admin/category/list", {"current": 1, "size": 10}, token=token_guest)
check("游客查询分类放行", api_ok(s, p), f"status={s}")

print()
print("=" * 60)
print("3. 分类模块")
print("=" * 60)
s, p, ok = req("POST", "/admin/category/add", {"name": "测试分类A"}, token=token_admin)
check("新增分类", api_ok(s, p))
s, p, ok = req("POST", "/admin/category/add", {"name": "测试分类A"}, token=token_admin)
check("重复分类被拒", api_ok(s, p) is False)
s, p, ok = req("POST", "/admin/category/list", {"current": 1, "size": 10}, token=token_admin)
check("分类分页列表", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
cat_total = p.get("data", {}).get("total", 0)
s, p, ok = req("POST", "/admin/category/select/list", token=token_admin)
check("分类 select 列表", api_ok(s, p))
# 找到测试分类A的id（SelectRspVO: {value, label}）
cat_a_id = None
for c in p.get("data", []):
    if c.get("label") == "测试分类A":
        cat_a_id = c.get("value")
s, p, ok = req("POST", "/admin/category/delete?categoryId=" + str(cat_a_id), token=token_admin)
check("删除空分类", api_ok(s, p), f"id={cat_a_id}")

# 删除有文章的分类（分类1有文章）
s, p, ok = req("POST", "/admin/category/delete?categoryId=1", token=token_admin)
check("删除有文章分类被拒", api_ok(s, p) is False)

print()
print("=" * 60)
print("4. 标签模块")
print("=" * 60)
s, p, ok = req("POST", "/admin/tag/add", {"name": "测试标签B"}, token=token_admin)
check("新增标签", api_ok(s, p))
s, p, ok = req("POST", "/admin/tag/add", {"name": "测试标签B"}, token=token_admin)
check("重复标签被拒", api_ok(s, p) is False)
s, p, ok = req("POST", "/admin/tag/list", {"current": 1, "size": 10, "name": "Java"}, token=token_admin)
check("标签模糊查询", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
s, p, ok = req("POST", "/admin/tag/select/list", token=token_admin)
check("标签 select 列表", api_ok(s, p))
tag_b_id = None
for t in p.get("data", []):
    if t.get("label") == "测试标签B":
        tag_b_id = t.get("value")
s, p, ok = req("POST", "/admin/tag/delete?tagId=" + str(tag_b_id), token=token_admin)
check("删除空标签", api_ok(s, p), f"id={tag_b_id}")
# 标签2(Spring Boot) 关联文章1
s, p, ok = req("POST", "/admin/tag/delete?tagId=2", token=token_admin)
check("删除有文章标签被拒", api_ok(s, p) is False)

print()
print("=" * 60)
print("5. 博客设置模块")
print("=" * 60)
s, p, ok = req("GET", "/admin/blog/settings/detail", token=token_admin)
check("获取设置详情", api_ok(s, p), f"blogName={p.get('data',{}).get('blogName')}")
s, p, ok = req("POST", "/admin/blog/settings/update",
               {"blogName": "Weblog测试", "authorName": "吴八哥", "introduction": "测试简介"},
               token=token_admin)
check("更新设置", api_ok(s, p))
s, p, ok = req("GET", "/blog/settings/detail")
check("前台获取设置", api_ok(s, p), f"authorName={p.get('data',{}).get('authorName')}")

print()
print("=" * 60)
print("6. 文章模块")
print("=" * 60)
s, p, ok = req("POST", "/admin/article/publish",
               {"title": "测试文章标题", "content": "# 测试\n\n内容", "categoryId": 2, "tagIds": [3, 4], "isTop": 0},
               token=token_admin)
check("发布文章", api_ok(s, p))
s, p, ok = req("POST", "/admin/article/list", {"current": 1, "size": 10}, token=token_admin)
check("文章分页列表", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
# 找测试文章id
art_id = None
for a in p.get("data", {}).get("records", []):
    if a.get("title") == "测试文章标题":
        art_id = a.get("id")
check("列表含新文章", art_id is not None)

s, p, ok = req("GET", "/admin/article/detail?articleId=" + str(art_id), token=token_admin)
check("文章详情", api_ok(s, p), f"tagNames={p.get('data',{}).get('tagNames')}")
s, p, ok = req("POST", "/admin/article/publish",
               {"id": art_id, "title": "测试文章标题-已更新", "content": "# 测试\n\n更新内容", "categoryId": 2, "tagIds": [3]},
               token=token_admin)
check("更新文章", api_ok(s, p))
s, p, ok = req("POST", "/admin/article/top/update?articleId=" + str(art_id) + "&isTop=1", token=token_admin)
check("文章置顶", api_ok(s, p))

print()
print("=" * 60)
print("7. 前台接口")
print("=" * 60)
s, p, ok = req("POST", "/article/list", {"current": 1, "size": 10})
check("首页文章分页", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
# 置顶优先：第一条应是置顶文章
first = p.get("data", {}).get("records", [])
if first:
    check("置顶文章排最前", first[0].get("isTop") == 1, f"isTop={first[0].get('isTop')}")

s, p, ok = req("GET", "/article/detail?articleId=" + str(art_id))
check("前台文章详情", api_ok(s, p))
s, p, ok = req("POST", "/article/archives")
check("归档分组", api_ok(s, p))
s, p, ok = req("POST", "/article/archive", {"current": 1, "size": 5})
check("归档分页", api_ok(s, p))
s, p, ok = req("GET", "/category/list")
check("前台分类列表(含文章数)", api_ok(s, p))
s, p, ok = req("GET", "/tag/list")
check("前台标签列表(含文章数)", api_ok(s, p))
s, p, ok = req("POST", "/category/article/list", {"current": 1, "size": 5, "categoryName": "Spring Boot"})
check("分类下文章分页", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
s, p, ok = req("POST", "/tag/article/list", {"current": 1, "size": 5, "tagName": "Vue 3"})
check("标签下文章分页", api_ok(s, p), f"total={p.get('data',{}).get('total')}")
s, p, ok = req("GET", "/blog/statistics")
check("博主卡片统计", api_ok(s, p), f"articleTotal={p.get('data',{}).get('articleTotal')}")
s, p, ok = req("GET", "/article/detail?articleId=99999999")
check("不存在文章返回失败", api_ok(s, p) is False)

print()
print("=" * 60)
print("8. 仪表盘")
print("=" * 60)
s, p, ok = req("GET", "/admin/dashboard/article/category/tag/statistics", token=token_admin)
check("统计数据", api_ok(s, p), f"article={p.get('data',{}).get('articleTotal')}")
s, p, ok = req("GET", "/admin/dashboard/article/publish/hot", token=token_admin)
check("发布热点", api_ok(s, p))
s, p, ok = req("GET", "/admin/dashboard/pv/statistics", token=token_admin)
check("PV 统计", api_ok(s, p), f"dates={p.get('data',{}).get('dates')}")

print()
print("=" * 60)
print("9. 修改密码 + 阅读量")
print("=" * 60)
s, p, ok = req("PUT", "/admin/user/password/update", {"password": "wrong", "newPassword": "new123456"}, token=token_admin)
check("原密码错误被拒", api_ok(s, p) is False)
# 阅读量自增验证（读两次）
s, p, ok = req("GET", "/article/detail?articleId=" + str(art_id))
rc1 = p.get("data", {}).get("readCount", 0)
time.sleep(1.5)
s, p, ok = req("GET", "/article/detail?articleId=" + str(art_id))
rc2 = p.get("data", {}).get("readCount", 0)
check("阅读量异步自增", rc2 > rc1, f"{rc1} -> {rc2}")

print()
print("=" * 60)
print(f"结果：通过 {PASS}，失败 {FAIL}")
if FAILED:
    print("失败项：")
    for f in FAILED:
        print("  -", f)
    sys.exit(1)
else:
    print("全部通过 ✅")
    sys.exit(0)
