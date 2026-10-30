# ============================================================
# Weblog Jenkins 镜像
# 构建：docker build -f deploy/jenkins.Dockerfile -t weblog-jenkins .
# 说明：
#   - 基于官方 Jenkins LTS（JDK 17），长期支持、插件生态成熟。
#   - 注入 Docker CLI（从 docker:24-cli 拷贝），复用宿主 /var/run/docker.sock，
#     免 DinD（Docker-in-Docker）以节省 2C4G 单机的内存。
#   - 宿主 docker.sock 的 group id 需与容器内 docker 组 gid 对齐，
#     可通过构建参数 DOCKER_GID 覆盖（Ubuntu 常见为 999/998，可用
#     `getent group docker | cut -d: -f3` 在宿主查询）。
# ============================================================

FROM jenkins/jenkins:lts-jdk17

# 宿主 docker 组 gid（默认 999，Debian/Ubuntu 常见值；按需覆盖）
ARG DOCKER_GID=999

USER root

# 从官方 docker CLI 镜像拷贝 docker 二进制（版本 24，稳定）
COPY --from=docker:24-cli /usr/local/bin/docker /usr/local/bin/docker

# 补充常用工具：curl（健康检查）、openssh-client（SSH 部署）、ca-certificates
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        openssh-client \
        ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# 创建与宿主 docker.sock 对齐 gid 的 docker 组，并将 jenkins 用户加入
RUN if getent group docker >/dev/null 2>&1; then \
        echo "docker 组已存在，跳过创建"; \
    else \
        groupadd -g "${DOCKER_GID}" docker; \
    fi \
    && usermod -aG docker jenkins

USER jenkins

# Jenkins JVM 内存上限（2C4G 单机，经验值；OOM 时下调）
ENV JAVA_OPTS="-Xmx768m"

EXPOSE 8080
