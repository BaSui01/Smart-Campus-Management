#!/bin/bash

# 智慧校园管理系统 - 开发环境SSL证书生成脚本
# 用于生成开发环境的自签名SSL证书

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
KEYSTORE_DIR="src/main/resources/keystore"
KEYSTORE_FILE="campus-keystore.p12"
KEYSTORE_PATH="${KEYSTORE_DIR}/${KEYSTORE_FILE}"
KEYSTORE_PASSWORD="campus-secure-2024"
CERT_ALIAS="campus-management"
VALIDITY_DAYS=365

# 证书信息
CERT_CN="localhost"
CERT_OU="Campus Management"
CERT_O="Smart Campus"
CERT_L="Beijing"
CERT_ST="Beijing"
CERT_C="CN"

echo -e "${BLUE}=== 智慧校园管理系统 SSL证书生成工具 ===${NC}"
echo ""

# 检查Java环境
if ! command -v keytool &> /dev/null; then
    echo -e "${RED}错误: 未找到keytool命令，请确保已安装Java JDK${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Java环境检查通过${NC}"

# 创建证书目录
if [ ! -d "$KEYSTORE_DIR" ]; then
    echo -e "${YELLOW}创建证书目录: $KEYSTORE_DIR${NC}"
    mkdir -p "$KEYSTORE_DIR"
fi

# 检查是否已存在证书
if [ -f "$KEYSTORE_PATH" ]; then
    echo -e "${YELLOW}警告: 证书文件已存在 ($KEYSTORE_PATH)${NC}"
    read -p "是否覆盖现有证书? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${BLUE}操作已取消${NC}"
        exit 0
    fi
    rm -f "$KEYSTORE_PATH"
fi

echo -e "${BLUE}开始生成SSL证书...${NC}"

# 生成自签名证书
keytool -genkeypair \
    -alias "$CERT_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore "$KEYSTORE_PATH" \
    -validity $VALIDITY_DAYS \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEYSTORE_PASSWORD" \
    -dname "CN=$CERT_CN, OU=$CERT_OU, O=$CERT_O, L=$CERT_L, ST=$CERT_ST, C=$CERT_C" \
    -ext SAN=dns:localhost,dns:127.0.0.1,ip:127.0.0.1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ SSL证书生成成功${NC}"
else
    echo -e "${RED}✗ SSL证书生成失败${NC}"
    exit 1
fi

# 显示证书信息
echo ""
echo -e "${BLUE}=== 证书信息 ===${NC}"
keytool -list -v -keystore "$KEYSTORE_PATH" -storepass "$KEYSTORE_PASSWORD" -alias "$CERT_ALIAS"

# 生成配置信息
echo ""
echo -e "${BLUE}=== 配置信息 ===${NC}"
echo -e "${GREEN}证书文件路径:${NC} $KEYSTORE_PATH"
echo -e "${GREEN}证书密码:${NC} $KEYSTORE_PASSWORD"
echo -e "${GREEN}证书别名:${NC} $CERT_ALIAS"
echo -e "${GREEN}有效期:${NC} $VALIDITY_DAYS 天"

# 生成环境变量配置
ENV_FILE="scripts/dev-ssl-env.sh"
cat > "$ENV_FILE" << EOF
#!/bin/bash
# 开发环境SSL配置环境变量
export SSL_KEYSTORE_PASSWORD="$KEYSTORE_PASSWORD"
export SPRING_PROFILES_ACTIVE="dev"
export SERVER_SSL_ENABLED="true"
export SERVER_PORT="8443"

echo "开发环境SSL配置已加载"
echo "HTTPS端口: 8443"
echo "HTTP端口已禁用"
EOF

chmod +x "$ENV_FILE"

echo ""
echo -e "${BLUE}=== 使用说明 ===${NC}"
echo -e "${GREEN}1. 加载环境变量:${NC}"
echo -e "   source scripts/dev-ssl-env.sh"
echo ""
echo -e "${GREEN}2. 启动应用:${NC}"
echo -e "   mvn spring-boot:run"
echo ""
echo -e "${GREEN}3. 访问应用:${NC}"
echo -e "   https://localhost:8443/api/v1/health"
echo ""
echo -e "${YELLOW}注意: 浏览器会显示安全警告，这是正常的（自签名证书）${NC}"
echo -e "${YELLOW}在浏览器中点击'高级' -> '继续访问localhost'${NC}"

# 生成测试脚本
TEST_SCRIPT="scripts/test-ssl.sh"
cat > "$TEST_SCRIPT" << EOF
#!/bin/bash
# SSL连接测试脚本

echo "测试SSL连接..."
echo ""

# 测试HTTPS连接
echo "1. 测试HTTPS健康检查端点:"
curl -k -s https://localhost:8443/api/v1/health || echo "连接失败"

echo ""
echo "2. 测试SSL证书信息:"
echo | openssl s_client -connect localhost:8443 -servername localhost 2>/dev/null | openssl x509 -noout -dates

echo ""
echo "3. 测试TLS版本:"
nmap --script ssl-enum-ciphers -p 8443 localhost 2>/dev/null | grep -E "(TLS|SSL)"

echo ""
echo "测试完成"
EOF

chmod +x "$TEST_SCRIPT"

echo ""
echo -e "${GREEN}✓ 证书生成完成！${NC}"
echo -e "${BLUE}环境变量脚本:${NC} $ENV_FILE"
echo -e "${BLUE}SSL测试脚本:${NC} $TEST_SCRIPT"
echo ""
echo -e "${YELLOW}下一步: 运行 'source $ENV_FILE' 加载SSL配置${NC}"
