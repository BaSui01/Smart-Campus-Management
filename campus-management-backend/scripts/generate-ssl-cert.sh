#!/bin/bash

# 智慧校园管理系统 - SSL证书生成脚本
# 用于开发和测试环境的自签名证书生成

set -e

# 配置变量
KEYSTORE_DIR="src/main/resources/keystore"
KEYSTORE_FILE="campus-keystore.p12"
TRUSTSTORE_FILE="campus-truststore.p12"
KEYSTORE_PASSWORD="campus-ssl-2024"
CERT_ALIAS="campus-management"
VALIDITY_DAYS=365

# 证书信息
CERT_CN="localhost"
CERT_OU="Campus Management Team"
CERT_O="Smart Campus Management System"
CERT_L="Beijing"
CERT_ST="Beijing"
CERT_C="CN"

echo "=== 智慧校园管理系统 SSL证书生成工具 ==="
echo "正在生成开发环境SSL证书..."

# 创建keystore目录
mkdir -p "$KEYSTORE_DIR"

# 生成自签名证书和密钥库
echo "1. 生成自签名证书和密钥库..."
keytool -genkeypair \
    -alias "$CERT_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore "$KEYSTORE_DIR/$KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEYSTORE_PASSWORD" \
    -validity "$VALIDITY_DAYS" \
    -dname "CN=$CERT_CN, OU=$CERT_OU, O=$CERT_O, L=$CERT_L, ST=$CERT_ST, C=$CERT_C" \
    -ext SAN=dns:localhost,dns:127.0.0.1,ip:127.0.0.1,ip:0.0.0.0

# 导出证书
echo "2. 导出证书文件..."
keytool -export \
    -alias "$CERT_ALIAS" \
    -keystore "$KEYSTORE_DIR/$KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -file "$KEYSTORE_DIR/campus-cert.crt"

# 创建信任库
echo "3. 创建信任库..."
keytool -import \
    -alias "$CERT_ALIAS" \
    -file "$KEYSTORE_DIR/campus-cert.crt" \
    -keystore "$KEYSTORE_DIR/$TRUSTSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -noprompt

# 显示证书信息
echo "4. 证书信息："
keytool -list -v \
    -keystore "$KEYSTORE_DIR/$KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -alias "$CERT_ALIAS"

echo ""
echo "=== SSL证书生成完成 ==="
echo "密钥库文件: $KEYSTORE_DIR/$KEYSTORE_FILE"
echo "信任库文件: $KEYSTORE_DIR/$TRUSTSTORE_FILE"
echo "证书文件: $KEYSTORE_DIR/campus-cert.crt"
echo "密钥库密码: $KEYSTORE_PASSWORD"
echo ""
echo "使用说明："
echo "1. 开发环境: 在application.yml中设置 server.ssl.enabled=true"
echo "2. 生产环境: 使用正式CA签发的证书替换自签名证书"
echo "3. 浏览器访问: https://localhost:8889"
echo ""
echo "注意: 自签名证书会在浏览器中显示安全警告，这是正常现象"
