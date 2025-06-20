# 智慧校园管理系统部署指南

## 📋 概述

本文档详细介绍了智慧校园管理系统的部署流程，包括环境准备、配置说明、部署步骤和运维监控。

---

## 🔧 环境要求

### 基础环境

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Java | JDK 21+ | 推荐使用 OpenJDK 21 |
| Maven | 3.8+ | 构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存和会话存储 |
| Node.js | 18+ | 前端构建（可选） |

### 服务器配置

**最小配置**:
- CPU: 2核
- 内存: 4GB
- 存储: 50GB
- 网络: 10Mbps

**推荐配置**:
- CPU: 4核
- 内存: 8GB
- 存储: 100GB SSD
- 网络: 100Mbps

---

## 🗄️ 数据库部署

### 1. MySQL 安装配置

```bash
# CentOS/RHEL
sudo yum install mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 2. 创建数据库和用户

```sql
-- 创建数据库
CREATE DATABASE campus_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'campus_user'@'%' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON campus_management_db.* TO 'campus_user'@'%';
FLUSH PRIVILEGES;
```

### 3. 导入初始数据

```bash
# 进入项目目录
cd campus-management-backend

# 导入数据库结构
mysql -u campus_user -p campus_management_db < src/main/resources/db/migration/01_create_tables.sql

# 导入基础数据
mysql -u campus_user -p campus_management_db < src/main/resources/db/migration/02_insert_basic_data.sql

# 导入测试数据（可选）
mysql -u campus_user -p campus_management_db < src/main/resources/db/migration/03_insert_large_data.sql
```

---

## 🔴 Redis 部署

### 1. Redis 安装

```bash
# CentOS/RHEL
sudo yum install redis
sudo systemctl start redis
sudo systemctl enable redis

# Ubuntu/Debian
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### 2. Redis 配置

编辑 `/etc/redis/redis.conf`:

```conf
# 绑定地址
bind 127.0.0.1

# 端口
port 6379

# 密码（推荐设置）
requirepass your_redis_password

# 持久化
save 900 1
save 300 10
save 60 10000

# 内存策略
maxmemory 2gb
maxmemory-policy allkeys-lru
```

---

## 🚀 应用部署

### 方式一：传统部署

#### 1. 构建应用

```bash
# 克隆代码
git clone https://github.com/your-org/campus-management.git
cd campus-management/campus-management-backend

# 构建项目
mvn clean package -DskipTests

# 生成的JAR文件位于 target/campus-management-backend-1.0.0.jar
```

#### 2. 配置文件

创建生产环境配置文件 `application-prod.yml`:

```yaml
server:
  port: 8889

spring:
  profiles:
    active: prod
  
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: campus_user
    password: your_secure_password
    
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0

campus:
  jwt:
    secret: your_production_jwt_secret_key_here
    expiration: 7200000
    refresh-expiration: 604800000

logging:
  level:
    com.campus: INFO
    org.springframework: WARN
  file:
    name: /var/log/campus-management/application.log
```

#### 3. 启动应用

```bash
# 创建日志目录
sudo mkdir -p /var/log/campus-management
sudo chown $USER:$USER /var/log/campus-management

# 启动应用
java -jar -Dspring.profiles.active=prod target/campus-management-backend-1.0.0.jar
```

#### 4. 创建系统服务

创建 `/etc/systemd/system/campus-management.service`:

```ini
[Unit]
Description=Campus Management System
After=network.target

[Service]
Type=simple
User=campus
WorkingDirectory=/opt/campus-management
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod campus-management-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务:

```bash
sudo systemctl daemon-reload
sudo systemctl enable campus-management
sudo systemctl start campus-management
```

### 方式二：Docker 部署

#### 1. 创建 Dockerfile

```dockerfile
FROM openjdk:21-jdk-slim

LABEL maintainer="Campus Management Team"

# 设置工作目录
WORKDIR /app

# 复制JAR文件
COPY target/campus-management-backend-1.0.0.jar app.jar

# 创建非root用户
RUN addgroup --system campus && adduser --system --group campus
RUN chown -R campus:campus /app
USER campus

# 暴露端口
EXPOSE 8889

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8889/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: campus-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: campus_management_db
      MYSQL_USER: campus_user
      MYSQL_PASSWORD: user_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/db/migration:/docker-entrypoint-initdb.d
    networks:
      - campus-network

  redis:
    image: redis:6.2-alpine
    container_name: campus-redis
    command: redis-server --requirepass redis_password
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - campus-network

  app:
    build: .
    container_name: campus-app
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/campus_management_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: campus_user
      SPRING_DATASOURCE_PASSWORD: user_password
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PASSWORD: redis_password
    ports:
      - "8889:8889"
    depends_on:
      - mysql
      - redis
    networks:
      - campus-network
    volumes:
      - app_logs:/app/logs

volumes:
  mysql_data:
  redis_data:
  app_logs:

networks:
  campus-network:
    driver: bridge
```

#### 3. 部署命令

```bash
# 构建并启动
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down
```

---

## 🔒 安全配置

### 1. 防火墙设置

```bash
# 开放必要端口
sudo firewall-cmd --permanent --add-port=8889/tcp
sudo firewall-cmd --reload

# 或使用 ufw (Ubuntu)
sudo ufw allow 8889/tcp
```

### 2. SSL/TLS 配置

#### 使用 Nginx 反向代理

安装 Nginx:

```bash
sudo yum install nginx  # CentOS/RHEL
sudo apt install nginx  # Ubuntu/Debian
```

配置 `/etc/nginx/sites-available/campus-management`:

```nginx
server {
    listen 80;
    server_name your-domain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    ssl_prefer_server_ciphers off;

    location / {
        proxy_pass http://localhost:8889;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    location /actuator {
        deny all;
        return 404;
    }
}
```

---

## 📊 监控配置

### 1. 应用监控

访问监控端点:

- 健康检查: `http://localhost:8889/actuator/health`
- 应用信息: `http://localhost:8889/actuator/info`
- 监控指标: `http://localhost:8889/actuator/metrics`
- Prometheus指标: `http://localhost:8889/actuator/prometheus`

### 2. 日志监控

配置日志轮转 `/etc/logrotate.d/campus-management`:

```
/var/log/campus-management/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 644 campus campus
    postrotate
        systemctl reload campus-management
    endscript
}
```

### 3. 系统监控脚本

创建监控脚本 `scripts/health-check.sh`:

```bash
#!/bin/bash

APP_URL="http://localhost:8889"
LOG_FILE="/var/log/campus-management/health-check.log"

# 检查应用健康状态
check_health() {
    response=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL/actuator/health)
    if [ $response -eq 200 ]; then
        echo "$(date): Application is healthy" >> $LOG_FILE
        return 0
    else
        echo "$(date): Application health check failed (HTTP $response)" >> $LOG_FILE
        return 1
    fi
}

# 检查数据库连接
check_database() {
    mysql -u campus_user -p$DB_PASSWORD -e "SELECT 1" campus_management_db > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "$(date): Database connection OK" >> $LOG_FILE
        return 0
    else
        echo "$(date): Database connection failed" >> $LOG_FILE
        return 1
    fi
}

# 主检查逻辑
main() {
    if ! check_health; then
        echo "$(date): Attempting to restart application" >> $LOG_FILE
        systemctl restart campus-management
        sleep 30
        if ! check_health; then
            echo "$(date): Application restart failed, sending alert" >> $LOG_FILE
            # 这里可以添加告警逻辑
        fi
    fi
    
    check_database
}

main
```

设置定时任务:

```bash
# 添加到 crontab
*/5 * * * * /opt/campus-management/scripts/health-check.sh
```

---

## 🔄 备份策略

### 1. 数据库备份

创建备份脚本 `scripts/backup-database.sh`:

```bash
#!/bin/bash

BACKUP_DIR="/backup/campus-management"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="campus_management_db"
DB_USER="campus_user"
DB_PASSWORD="your_password"

mkdir -p $BACKUP_DIR

# 创建备份
mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/campus_db_$DATE.sql

# 压缩备份文件
gzip $BACKUP_DIR/campus_db_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "campus_db_*.sql.gz" -mtime +7 -delete

echo "Database backup completed: campus_db_$DATE.sql.gz"
```

### 2. 应用备份

```bash
#!/bin/bash

BACKUP_DIR="/backup/campus-management"
DATE=$(date +%Y%m%d_%H%M%S)
APP_DIR="/opt/campus-management"

mkdir -p $BACKUP_DIR

# 备份应用文件
tar -czf $BACKUP_DIR/campus_app_$DATE.tar.gz -C $APP_DIR .

# 备份配置文件
cp /etc/systemd/system/campus-management.service $BACKUP_DIR/

echo "Application backup completed: campus_app_$DATE.tar.gz"
```

---

## 🚨 故障排除

### 常见问题

1. **应用启动失败**
   - 检查Java版本: `java -version`
   - 检查端口占用: `netstat -tlnp | grep 8889`
   - 查看日志: `journalctl -u campus-management -f`

2. **数据库连接失败**
   - 检查MySQL状态: `systemctl status mysqld`
   - 测试连接: `mysql -u campus_user -p -h localhost`
   - 检查防火墙: `firewall-cmd --list-ports`

3. **Redis连接失败**
   - 检查Redis状态: `systemctl status redis`
   - 测试连接: `redis-cli ping`

4. **内存不足**
   - 调整JVM参数: `-Xmx2g -Xms1g`
   - 监控内存使用: `free -h`

### 日志位置

- 应用日志: `/var/log/campus-management/application.log`
- 系统日志: `journalctl -u campus-management`
- MySQL日志: `/var/log/mysqld.log`
- Redis日志: `/var/log/redis/redis-server.log`

---

## 📈 性能优化

### 1. JVM 调优

```bash
java -jar \
  -Xmx4g \
  -Xms2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/campus-management/ \
  campus-management-backend-1.0.0.jar
```

### 2. 数据库优化

```sql
-- 添加索引
CREATE INDEX idx_user_username ON tb_user(username);
CREATE INDEX idx_student_number ON tb_student(student_number);
CREATE INDEX idx_course_semester ON tb_course(semester);

-- 配置优化
SET GLOBAL innodb_buffer_pool_size = 2147483648;  -- 2GB
SET GLOBAL query_cache_size = 268435456;          -- 256MB
```

### 3. Redis 优化

```conf
# redis.conf
maxmemory 2gb
maxmemory-policy allkeys-lru
tcp-keepalive 300
timeout 300
```

---

## 📞 技术支持

如遇到部署问题，请联系：

- 技术支持邮箱: support@campus.edu
- 文档地址: https://docs.campus.edu
- 问题反馈: https://github.com/your-org/campus-management/issues

---

**注意**: 请根据实际环境调整配置参数，确保系统安全和稳定运行。
