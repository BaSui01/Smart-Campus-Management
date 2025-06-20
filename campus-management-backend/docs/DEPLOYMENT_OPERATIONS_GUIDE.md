# 智慧校园管理系统部署运维指南

## 📋 概述

本文档提供了智慧校园管理系统的完整部署和运维指南，包括环境配置、部署流程、监控告警、故障排除等内容。

**适用环境**: 生产环境、测试环境、开发环境  
**更新时间**: 2025-06-20

---

## 🏗️ 系统架构

### 整体架构图
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │   Web Server    │    │   App Server    │
│    (Nginx)      │────│    (Nginx)      │────│  (Spring Boot)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                       ┌─────────────────┐    ┌─────────────────┐
                       │     Cache       │    │    Database     │
                       │    (Redis)      │    │    (MySQL)      │
                       └─────────────────┘    └─────────────────┘
```

### 技术栈
- **应用服务器**: Spring Boot 2.7.x + JDK 17
- **数据库**: MySQL 8.0
- **缓存**: Redis 6.0
- **Web服务器**: Nginx 1.20
- **容器化**: Docker + Docker Compose
- **监控**: Prometheus + Grafana

---

## 🚀 部署环境要求

### 硬件要求

#### 生产环境
| 组件 | CPU | 内存 | 磁盘 | 网络 |
|------|-----|------|------|------|
| 应用服务器 | 4核 | 8GB | 100GB SSD | 1Gbps |
| 数据库服务器 | 8核 | 16GB | 500GB SSD | 1Gbps |
| 缓存服务器 | 2核 | 4GB | 50GB SSD | 1Gbps |
| Web服务器 | 2核 | 4GB | 50GB SSD | 1Gbps |

#### 测试环境
| 组件 | CPU | 内存 | 磁盘 | 网络 |
|------|-----|------|------|------|
| 应用服务器 | 2核 | 4GB | 50GB | 100Mbps |
| 数据库服务器 | 2核 | 4GB | 100GB | 100Mbps |
| 缓存服务器 | 1核 | 2GB | 20GB | 100Mbps |

### 软件要求
- **操作系统**: Ubuntu 20.04 LTS / CentOS 8
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Git**: 2.25+

---

## 📦 部署流程

### 1. 环境准备

#### 1.1 安装Docker
```bash
# Ubuntu
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.12.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### 1.2 创建部署目录
```bash
mkdir -p /opt/campus-management
cd /opt/campus-management
git clone https://github.com/your-org/Smart-Campus-Management.git
cd Smart-Campus-Management
```

### 2. 配置文件设置

#### 2.1 环境变量配置
```bash
# 创建环境变量文件
cp .env.example .env

# 编辑环境变量
vim .env
```

```bash
# .env 文件内容
# 数据库配置
DB_HOST=mysql
DB_PORT=3306
DB_NAME=campus_management
DB_USERNAME=campus_user
DB_PASSWORD=your_secure_password

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# 应用配置
APP_ENV=production
APP_PORT=8080
JWT_SECRET=your_jwt_secret_key

# 邮件配置
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=noreply@example.com
MAIL_PASSWORD=your_mail_password
```

#### 2.2 Docker Compose配置
```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  app:
    build:
      context: ./campus-management-backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=${DB_HOST}
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - REDIS_HOST=${REDIS_HOST}
    depends_on:
      - mysql
      - redis
    volumes:
      - ./logs:/app/logs
      - ./uploads:/app/uploads
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: ${DB_NAME}
      MYSQL_USER: ${DB_USERNAME}
      MYSQL_PASSWORD: ${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./campus-management-backend/src/main/resources/db:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"
    restart: unless-stopped

  redis:
    image: redis:6.0-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    restart: unless-stopped

  nginx:
    image: nginx:1.20-alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
      - ./campus-management-frontend/dist:/usr/share/nginx/html
    depends_on:
      - app
    restart: unless-stopped

volumes:
  mysql_data:
  redis_data:
```

### 3. 应用部署

#### 3.1 构建应用
```bash
# 构建后端应用
cd campus-management-backend
mvn clean package -DskipTests

# 构建前端应用
cd ../campus-management-frontend
npm install
npm run build

# 返回根目录
cd ..
```

#### 3.2 启动服务
```bash
# 启动所有服务
docker-compose -f docker-compose.prod.yml up -d

# 查看服务状态
docker-compose -f docker-compose.prod.yml ps

# 查看日志
docker-compose -f docker-compose.prod.yml logs -f app
```

#### 3.3 数据库初始化
```bash
# 等待MySQL启动完成
sleep 30

# 执行数据库初始化脚本
docker-compose -f docker-compose.prod.yml exec mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} < /docker-entrypoint-initdb.d/01_create_tables.sql
```

---

## 🔧 Nginx配置

### nginx.conf
```nginx
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server app:8080;
    }

    server {
        listen 80;
        server_name your-domain.com;
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name your-domain.com;

        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;

        # 前端静态文件
        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;
        }

        # API代理
        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # 文件上传
        location /uploads/ {
            alias /app/uploads/;
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

---

## 📊 监控配置

### 1. Prometheus配置
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'campus-management'
    static_configs:
      - targets: ['app:8080']
    metrics_path: '/actuator/prometheus'
```

### 2. Grafana仪表板
```json
{
  "dashboard": {
    "title": "Campus Management System",
    "panels": [
      {
        "title": "JVM Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes{application=\"campus-management\"}"
          }
        ]
      },
      {
        "title": "HTTP Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total{application=\"campus-management\"}[5m])"
          }
        ]
      }
    ]
  }
}
```

### 3. 告警规则
```yaml
# alert.rules.yml
groups:
  - name: campus-management
    rules:
      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes / jvm_memory_max_bytes > 0.8
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage detected"

      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
```

---

## 🔍 运维操作

### 日常维护

#### 1. 日志管理
```bash
# 查看应用日志
docker-compose logs -f app

# 清理旧日志
find ./logs -name "*.log" -mtime +30 -delete

# 日志轮转配置
logrotate -d /etc/logrotate.d/campus-management
```

#### 2. 数据库维护
```bash
# 数据库备份
docker-compose exec mysql mysqldump -u${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} > backup_$(date +%Y%m%d).sql

# 数据库恢复
docker-compose exec -i mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} < backup_20250620.sql

# 数据库优化
docker-compose exec mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} -e "OPTIMIZE TABLE ${DB_NAME}.tb_user;"
```

#### 3. 缓存管理
```bash
# 清理Redis缓存
docker-compose exec redis redis-cli -a ${REDIS_PASSWORD} FLUSHALL

# 查看缓存使用情况
docker-compose exec redis redis-cli -a ${REDIS_PASSWORD} INFO memory
```

### 应用更新

#### 1. 滚动更新
```bash
# 拉取最新代码
git pull origin main

# 重新构建应用
mvn clean package -DskipTests

# 重启应用服务
docker-compose -f docker-compose.prod.yml up -d --no-deps app
```

#### 2. 蓝绿部署
```bash
# 启动新版本
docker-compose -f docker-compose.blue.yml up -d

# 切换流量
# 更新Nginx配置指向新版本

# 停止旧版本
docker-compose -f docker-compose.green.yml down
```

---

## 🚨 故障排除

### 常见问题

#### 1. 应用启动失败
```bash
# 检查日志
docker-compose logs app

# 常见原因：
# - 数据库连接失败
# - 端口被占用
# - 配置文件错误
# - 内存不足
```

#### 2. 数据库连接问题
```bash
# 检查数据库状态
docker-compose exec mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} -e "SELECT 1"

# 检查网络连接
docker-compose exec app ping mysql
```

#### 3. 性能问题
```bash
# 检查系统资源
docker stats

# 检查JVM状态
docker-compose exec app jstat -gc 1

# 检查数据库性能
docker-compose exec mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} -e "SHOW PROCESSLIST"
```

### 应急处理

#### 1. 服务重启
```bash
# 重启单个服务
docker-compose restart app

# 重启所有服务
docker-compose restart
```

#### 2. 数据恢复
```bash
# 从备份恢复数据库
docker-compose exec -i mysql mysql -u${DB_USERNAME} -p${DB_PASSWORD} ${DB_NAME} < latest_backup.sql

# 恢复文件上传目录
tar -xzf uploads_backup.tar.gz -C ./uploads/
```

---

## 📋 运维检查清单

### 日常检查 (每日)
- [ ] 检查服务运行状态
- [ ] 查看错误日志
- [ ] 检查磁盘空间
- [ ] 验证关键功能

### 周期检查 (每周)
- [ ] 数据库备份验证
- [ ] 性能指标分析
- [ ] 安全日志审查
- [ ] 系统更新检查

### 月度检查 (每月)
- [ ] 容量规划评估
- [ ] 安全漏洞扫描
- [ ] 灾难恢复演练
- [ ] 文档更新维护

通过本指南的系统性实施，可以确保智慧校园管理系统的稳定运行和高效维护。
