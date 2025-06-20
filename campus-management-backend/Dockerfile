# 智慧校园管理系统 - Docker镜像构建文件
# 基于OpenJDK 17的多阶段构建

# 第一阶段：构建阶段
FROM maven:3.9.6-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制pom.xml文件
COPY pom.xml .

# 下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -B

# 第二阶段：运行阶段
FROM openjdk:17-jre-slim

# 设置维护者信息
LABEL maintainer="Campus Management Team <admin@campus.edu>"
LABEL version="1.0.0"
LABEL description="智慧校园管理系统后端服务"

# 创建应用用户
RUN groupadd -r campus && useradd -r -g campus campus

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    dumb-init \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制jar文件
COPY --from=builder /app/target/campus-management-backend-*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R campus:campus /app

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 设置应用参数
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 切换到应用用户
USER campus

# 启动应用
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
