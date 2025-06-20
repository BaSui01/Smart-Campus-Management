#!/bin/bash

# 智慧校园管理系统 - 生产环境部署脚本
# 用于自动化部署生产环境

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
APP_NAME="campus-management-backend"
APP_VERSION="1.0.0"
DEPLOY_DIR="/opt/campus"
BACKUP_DIR="/opt/campus/backups"
LOG_DIR="/var/log/campus"
SERVICE_NAME="campus-management"
JAR_NAME="${APP_NAME}-${APP_VERSION}.jar"
CONFIG_DIR="/opt/campus/config"

# 数据库配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-campus_management_db}"
DB_USERNAME="${DB_USERNAME:-campus_user}"

# 服务端口
SERVER_PORT="${SERVER_PORT:-8443}"
MANAGEMENT_PORT="${MANAGEMENT_PORT:-8444}"

echo -e "${BLUE}=== 智慧校园管理系统生产环境部署脚本 ===${NC}"
echo -e "${BLUE}版本: ${APP_VERSION}${NC}"
echo -e "${BLUE}部署目录: ${DEPLOY_DIR}${NC}"
echo ""

# 检查运行权限
if [[ $EUID -ne 0 ]]; then
   echo -e "${RED}错误: 此脚本需要root权限运行${NC}"
   exit 1
fi

# 检查必要的环境变量
check_environment() {
    echo -e "${YELLOW}检查环境变量...${NC}"
    
    local missing_vars=()
    
    # 检查必要的环境变量
    [ -z "$DB_PASSWORD" ] && missing_vars+=("DB_PASSWORD")
    [ -z "$REDIS_PASSWORD" ] && missing_vars+=("REDIS_PASSWORD")
    [ -z "$JWT_SECRET" ] && missing_vars+=("JWT_SECRET")
    [ -z "$ENCRYPTION_SECRET_KEY" ] && missing_vars+=("ENCRYPTION_SECRET_KEY")
    [ -z "$SSL_KEYSTORE_PASSWORD" ] && missing_vars+=("SSL_KEYSTORE_PASSWORD")
    
    if [ ${#missing_vars[@]} -ne 0 ]; then
        echo -e "${RED}错误: 缺少必要的环境变量:${NC}"
        printf '%s\n' "${missing_vars[@]}"
        echo ""
        echo -e "${YELLOW}请设置以下环境变量:${NC}"
        echo "export DB_PASSWORD='your-db-password'"
        echo "export REDIS_PASSWORD='your-redis-password'"
        echo "export JWT_SECRET='your-jwt-secret'"
        echo "export ENCRYPTION_SECRET_KEY='your-encryption-key'"
        echo "export SSL_KEYSTORE_PASSWORD='your-keystore-password'"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 环境变量检查通过${NC}"
}

# 检查系统依赖
check_dependencies() {
    echo -e "${YELLOW}检查系统依赖...${NC}"
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        echo -e "${RED}错误: 未找到Java，请安装Java 17或更高版本${NC}"
        exit 1
    fi
    
    local java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" -lt 17 ]; then
        echo -e "${RED}错误: Java版本过低，需要Java 17或更高版本${NC}"
        exit 1
    fi
    
    # 检查MySQL客户端
    if ! command -v mysql &> /dev/null; then
        echo -e "${YELLOW}警告: 未找到MySQL客户端，数据库连接测试将跳过${NC}"
    fi
    
    # 检查Redis客户端
    if ! command -v redis-cli &> /dev/null; then
        echo -e "${YELLOW}警告: 未找到Redis客户端，Redis连接测试将跳过${NC}"
    fi
    
    echo -e "${GREEN}✓ 系统依赖检查通过${NC}"
}

# 创建必要的目录
create_directories() {
    echo -e "${YELLOW}创建部署目录...${NC}"
    
    mkdir -p "$DEPLOY_DIR"
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOG_DIR"
    mkdir -p "$CONFIG_DIR"
    mkdir -p "$DEPLOY_DIR/uploads"
    mkdir -p "$DEPLOY_DIR/keystore"
    
    # 设置目录权限
    chown -R campus:campus "$DEPLOY_DIR" 2>/dev/null || true
    chown -R campus:campus "$LOG_DIR" 2>/dev/null || true
    chmod 755 "$DEPLOY_DIR"
    chmod 755 "$LOG_DIR"
    chmod 700 "$DEPLOY_DIR/keystore"
    
    echo -e "${GREEN}✓ 目录创建完成${NC}"
}

# 备份当前版本
backup_current_version() {
    if [ -f "$DEPLOY_DIR/$JAR_NAME" ]; then
        echo -e "${YELLOW}备份当前版本...${NC}"
        
        local backup_timestamp=$(date +%Y%m%d_%H%M%S)
        local backup_file="$BACKUP_DIR/${APP_NAME}_${backup_timestamp}.jar"
        
        cp "$DEPLOY_DIR/$JAR_NAME" "$backup_file"
        echo -e "${GREEN}✓ 当前版本已备份到: $backup_file${NC}"
    fi
}

# 停止服务
stop_service() {
    echo -e "${YELLOW}停止服务...${NC}"
    
    if systemctl is-active --quiet "$SERVICE_NAME"; then
        systemctl stop "$SERVICE_NAME"
        echo -e "${GREEN}✓ 服务已停止${NC}"
    else
        echo -e "${BLUE}服务未运行${NC}"
    fi
}

# 部署应用
deploy_application() {
    echo -e "${YELLOW}部署应用...${NC}"
    
    # 检查JAR文件是否存在
    if [ ! -f "target/$JAR_NAME" ]; then
        echo -e "${RED}错误: 未找到JAR文件 target/$JAR_NAME${NC}"
        echo -e "${YELLOW}请先运行: mvn clean package -Dmaven.test.skip=true${NC}"
        exit 1
    fi
    
    # 复制JAR文件
    cp "target/$JAR_NAME" "$DEPLOY_DIR/"
    chmod 755 "$DEPLOY_DIR/$JAR_NAME"
    
    # 复制配置文件
    if [ -f "src/main/resources/application-prod.yml" ]; then
        cp "src/main/resources/application-prod.yml" "$CONFIG_DIR/"
    fi
    
    # 复制SSL证书（如果存在）
    if [ -f "src/main/resources/keystore/campus-keystore.p12" ]; then
        cp "src/main/resources/keystore/campus-keystore.p12" "$DEPLOY_DIR/keystore/"
        chmod 600 "$DEPLOY_DIR/keystore/campus-keystore.p12"
    fi
    
    echo -e "${GREEN}✓ 应用部署完成${NC}"
}

# 创建systemd服务文件
create_systemd_service() {
    echo -e "${YELLOW}创建systemd服务...${NC}"
    
    cat > "/etc/systemd/system/${SERVICE_NAME}.service" << EOF
[Unit]
Description=Smart Campus Management System
After=network.target mysql.service redis.service

[Service]
Type=simple
User=campus
Group=campus
WorkingDirectory=${DEPLOY_DIR}
ExecStart=/usr/bin/java \\
    -Xms512m \\
    -Xmx2g \\
    -XX:+UseG1GC \\
    -XX:+UseStringDeduplication \\
    -Dspring.profiles.active=prod \\
    -Dspring.config.location=classpath:/application.yml,${CONFIG_DIR}/application-prod.yml \\
    -Dlogging.file.name=${LOG_DIR}/campus-management.log \\
    -jar ${DEPLOY_DIR}/${JAR_NAME}

ExecStop=/bin/kill -TERM \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=${SERVICE_NAME}

# 环境变量
Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk
Environment=DB_HOST=${DB_HOST}
Environment=DB_PORT=${DB_PORT}
Environment=DB_NAME=${DB_NAME}
Environment=DB_USERNAME=${DB_USERNAME}
Environment=DB_PASSWORD=${DB_PASSWORD}
Environment=REDIS_PASSWORD=${REDIS_PASSWORD}
Environment=JWT_SECRET=${JWT_SECRET}
Environment=ENCRYPTION_SECRET_KEY=${ENCRYPTION_SECRET_KEY}
Environment=SSL_KEYSTORE_PASSWORD=${SSL_KEYSTORE_PASSWORD}
Environment=SERVER_PORT=${SERVER_PORT}

# 安全设置
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=${DEPLOY_DIR} ${LOG_DIR}

[Install]
WantedBy=multi-user.target
EOF

    systemctl daemon-reload
    systemctl enable "$SERVICE_NAME"
    
    echo -e "${GREEN}✓ systemd服务创建完成${NC}"
}

# 测试数据库连接
test_database_connection() {
    echo -e "${YELLOW}测试数据库连接...${NC}"
    
    if command -v mysql &> /dev/null; then
        if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USERNAME" -p"$DB_PASSWORD" -e "SELECT 1;" &> /dev/null; then
            echo -e "${GREEN}✓ 数据库连接测试成功${NC}"
        else
            echo -e "${RED}警告: 数据库连接测试失败${NC}"
        fi
    else
        echo -e "${BLUE}跳过数据库连接测试（未安装MySQL客户端）${NC}"
    fi
}

# 启动服务
start_service() {
    echo -e "${YELLOW}启动服务...${NC}"
    
    systemctl start "$SERVICE_NAME"
    
    # 等待服务启动
    echo -e "${BLUE}等待服务启动...${NC}"
    sleep 10
    
    # 检查服务状态
    if systemctl is-active --quiet "$SERVICE_NAME"; then
        echo -e "${GREEN}✓ 服务启动成功${NC}"
    else
        echo -e "${RED}错误: 服务启动失败${NC}"
        echo -e "${YELLOW}查看日志: journalctl -u $SERVICE_NAME -f${NC}"
        exit 1
    fi
}

# 健康检查
health_check() {
    echo -e "${YELLOW}执行健康检查...${NC}"
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -k -s "https://localhost:$SERVER_PORT/api/v1/system/health" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 健康检查通过${NC}"
            return 0
        fi
        
        echo -e "${BLUE}健康检查尝试 $attempt/$max_attempts...${NC}"
        sleep 5
        ((attempt++))
    done
    
    echo -e "${RED}警告: 健康检查失败，请检查应用状态${NC}"
    return 1
}

# 显示部署信息
show_deployment_info() {
    echo ""
    echo -e "${GREEN}=== 部署完成 ===${NC}"
    echo -e "${GREEN}应用名称:${NC} $APP_NAME"
    echo -e "${GREEN}版本:${NC} $APP_VERSION"
    echo -e "${GREEN}部署目录:${NC} $DEPLOY_DIR"
    echo -e "${GREEN}配置目录:${NC} $CONFIG_DIR"
    echo -e "${GREEN}日志目录:${NC} $LOG_DIR"
    echo -e "${GREEN}服务名称:${NC} $SERVICE_NAME"
    echo ""
    echo -e "${GREEN}访问地址:${NC}"
    echo -e "  HTTPS: https://localhost:$SERVER_PORT"
    echo -e "  健康检查: https://localhost:$SERVER_PORT/api/v1/system/health"
    echo -e "  API文档: https://localhost:$SERVER_PORT/api/v1/swagger-ui.html"
    echo ""
    echo -e "${GREEN}常用命令:${NC}"
    echo -e "  查看服务状态: systemctl status $SERVICE_NAME"
    echo -e "  查看日志: journalctl -u $SERVICE_NAME -f"
    echo -e "  重启服务: systemctl restart $SERVICE_NAME"
    echo -e "  停止服务: systemctl stop $SERVICE_NAME"
    echo ""
}

# 主函数
main() {
    echo -e "${BLUE}开始部署...${NC}"
    
    check_environment
    check_dependencies
    create_directories
    backup_current_version
    stop_service
    deploy_application
    create_systemd_service
    test_database_connection
    start_service
    health_check
    show_deployment_info
    
    echo -e "${GREEN}部署完成！${NC}"
}

# 执行主函数
main "$@"
