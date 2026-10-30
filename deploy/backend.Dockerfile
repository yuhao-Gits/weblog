# ============================================================
# Weblog 后端镜像（多阶段构建）
# 构建：docker build -f deploy/backend.Dockerfile -t weblog-server .
# 说明：阶段一用 Maven 编译打包，阶段二用 JRE 精简运行
# ============================================================

# ---------- 阶段一：构建 ----------
FROM maven:3.9-eclipse-temurin-8 AS builder
WORKDIR /build

# 先拷贝 pom 以利用 Docker 层缓存，加速依赖下载
COPY weblog-server/pom.xml ./weblog-server/pom.xml
# 离线依赖缓存（首次构建需联网，之后命中缓存）
RUN cd weblog-server && mvn -B dependency:go-offline -q || true

# 拷贝源码并打包（跳过测试以加快 CI 构建，测试由流水线单独执行）
COPY weblog-server/src ./weblog-server/src
RUN cd weblog-server && mvn -B clean package -DskipTests -q

# ---------- 阶段二：运行 ----------
FROM eclipse-temurin:8-jre
LABEL maintainer="weblog" \
      description="Weblog backend (Spring Boot 2.7)"

WORKDIR /app

# 运行专用用户（降低权限，提升安全性）
RUN groupadd -r weblog && useradd -r -g weblog weblog

# 数据/上传目录（可由 volume 挂载覆盖）
RUN mkdir -p /app/uploads /app/logs && chown -R weblog:weblog /app

# 从构建阶段拷贝可执行 jar
COPY --from=builder /build/weblog-server/target/weblog-server-1.0.0.jar /app/app.jar

USER weblog
EXPOSE 8080

# 通过环境变量注入配置；健康检查依赖 Actuator
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=8080"]
