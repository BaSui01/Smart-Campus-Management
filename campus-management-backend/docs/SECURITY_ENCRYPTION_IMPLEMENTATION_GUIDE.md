# 🔐 智慧校园管理系统安全加密实施指南

## 📋 概述

本文档提供了智慧校园管理系统后端的完整安全加密实施方案，包括传输层加密（HTTPS/TLS）、网络层安全、数据库加密和字段级加密的详细实施步骤。

## 🎯 安全评估结果

### ✅ 现有安全措施
- Spring Security + JWT认证
- BCrypt密码加密
- 基于角色的访问控制（RBAC）
- SQL注入防护
- 输入验证

### ❌ 需要实施的安全措施
- **传输层加密（HTTPS/TLS）** - 高优先级
- **数据库连接加密** - 高优先级
- **字段级数据加密** - 中优先级
- **Redis连接加密** - 中优先级
- **安全头配置** - 中优先级

## 🛡️ 实施方案

### 1. 传输层安全（HTTPS/TLS）实施

#### 1.1 生成SSL证书

**开发环境 - 自签名证书：**
```bash
# 创建证书目录
mkdir -p src/main/resources/keystore

# 生成自签名证书
keytool -genkeypair -alias campus-management \
  -keyalg RSA -keysize 2048 -storetype PKCS12 \
  -keystore src/main/resources/keystore/campus-keystore.p12 \
  -validity 365 \
  -dname "CN=localhost, OU=Campus Management, O=Campus, L=City, ST=State, C=CN"

# 输入密码：campus-secure-2024
```

**生产环境 - Let's Encrypt证书：**
```bash
# 安装Certbot
sudo apt-get install certbot

# 获取证书
sudo certbot certonly --standalone -d yourdomain.com

# 转换为PKCS12格式
sudo openssl pkcs12 -export \
  -in /etc/letsencrypt/live/yourdomain.com/fullchain.pem \
  -inkey /etc/letsencrypt/live/yourdomain.com/privkey.pem \
  -out /opt/campus/keystore/campus-keystore.p12 \
  -name campus-management
```

#### 1.2 配置应用程序

使用提供的 `application-prod.yml` 配置文件，包含完整的SSL/TLS配置。

#### 1.3 环境变量设置

```bash
# 生产环境环境变量
export SSL_KEYSTORE_PASSWORD=your-secure-keystore-password
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=campus_management_db
export DB_USERNAME=campus_user
export DB_PASSWORD=your-secure-db-password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6380
export REDIS_PASSWORD=your-secure-redis-password
export JWT_SECRET=your-base64-encoded-jwt-secret
export ENCRYPTION_SECRET_KEY=your-base64-encoded-encryption-key
export MAIL_HOST=your-mail-host
export MAIL_USERNAME=your-mail-username
export MAIL_PASSWORD=your-mail-password
export ADMIN_EMAIL=admin@yourdomain.com
export UPLOAD_PATH=/opt/campus/uploads/
```

### 2. 数据库加密配置

#### 2.1 MySQL SSL配置

**服务器端配置（my.cnf）：**
```ini
[mysqld]
# SSL配置
ssl-ca=/etc/mysql/ssl/ca-cert.pem
ssl-cert=/etc/mysql/ssl/server-cert.pem
ssl-key=/etc/mysql/ssl/server-key.pem
require_secure_transport=ON

# 加密配置
default_table_encryption=ON
innodb_redo_log_encrypt=ON
innodb_undo_log_encrypt=ON
binlog_encryption=ON
```

**生成MySQL SSL证书：**
```bash
# 使用MySQL自带工具生成SSL证书
mysql_ssl_rsa_setup --datadir=/var/lib/mysql
```

#### 2.2 应用程序数据库连接

数据库连接URL已在 `application-prod.yml` 中配置为启用SSL：
```yaml
url: jdbc:mysql://localhost:3306/campus_management_db?useSSL=true&requireSSL=true&verifyServerCertificate=true
```

### 3. Redis加密配置

#### 3.1 Redis服务器SSL配置

**redis.conf配置：**
```conf
# 启用TLS
port 0
tls-port 6380
tls-cert-file /etc/redis/tls/redis.crt
tls-key-file /etc/redis/tls/redis.key
tls-ca-cert-file /etc/redis/tls/ca.crt
tls-protocols "TLSv1.2 TLSv1.3"
```

### 4. 字段级数据加密实施

#### 4.1 生成加密密钥

```java
// 生成新的AES-256密钥
String encryptionKey = DataEncryptionService.generateNewKey();
System.out.println("Encryption Key: " + encryptionKey);
```

#### 4.2 实体类加密注解

```java
@Entity
@EntityListeners(EncryptionEntityListener.class)
public class Student extends BaseEntity {
    
    @EncryptedField  // 加密敏感字段
    @Column(name = "phone")
    private String phone;
    
    @EncryptedField  // 加密身份证号
    @Column(name = "id_card")
    private String idCard;
    
    @EncryptedField  // 加密家长电话
    @Column(name = "parent_phone")
    private String parentPhone;
    
    // 其他字段...
}
```

#### 4.3 需要加密的敏感字段

**用户表（User）：**
- phone（手机号）
- email（邮箱）
- idCard（身份证号）

**学生表（Student）：**
- parentPhone（家长电话）
- emergencyPhone（紧急联系电话）
- idCard（身份证号）

**缴费记录表（PaymentRecord）：**
- bankAccount（银行账号）
- transactionNo（交易流水号）

### 5. 网络层安全建议

#### 5.1 防火墙配置

```bash
# 只开放必要端口
sudo ufw allow 8443/tcp  # HTTPS
sudo ufw allow 22/tcp    # SSH
sudo ufw deny 8889/tcp   # 禁用HTTP端口
sudo ufw enable
```

#### 5.2 反向代理配置（Nginx）

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;
    
    # SSL配置
    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256;
    ssl_prefer_server_ciphers off;
    
    # 安全头
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options DENY always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    location /api/ {
        proxy_pass https://localhost:8443;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# HTTP重定向到HTTPS
server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

## 🚀 部署步骤

### 1. 开发环境部署

```bash
# 1. 生成开发证书
./scripts/generate-dev-cert.sh

# 2. 设置环境变量
export SPRING_PROFILES_ACTIVE=dev
export SSL_KEYSTORE_PASSWORD=campus-secure-2024

# 3. 启动应用
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. 生产环境部署

```bash
# 1. 设置所有必要的环境变量
source /opt/campus/config/env.sh

# 2. 启动应用
java -jar -Dspring.profiles.active=prod campus-management-backend.jar
```

## 🔍 安全验证

### 1. SSL/TLS验证

```bash
# 检查SSL证书
openssl s_client -connect yourdomain.com:443 -servername yourdomain.com

# 检查SSL评级
curl -s "https://api.ssllabs.com/api/v3/analyze?host=yourdomain.com"
```

### 2. 安全头验证

```bash
# 检查安全头
curl -I https://yourdomain.com/api/v1/health
```

### 3. 加密功能测试

```java
@Test
public void testDataEncryption() {
    DataEncryptionService service = new DataEncryptionService(encryptionKey);
    
    String originalData = "13800138000";
    String encryptedData = service.encrypt(originalData);
    String decryptedData = service.decrypt(encryptedData);
    
    assertNotEquals(originalData, encryptedData);
    assertEquals(originalData, decryptedData);
}
```

## 📊 性能影响评估

### 加密性能开销
- **HTTPS/TLS**: ~5-10% CPU开销
- **数据库SSL**: ~2-5% 延迟增加
- **字段级加密**: ~1-3% 数据库操作开销
- **Redis SSL**: ~3-7% 缓存操作开销

### 优化建议
1. 使用硬件加速（AES-NI）
2. 启用HTTP/2
3. 配置适当的SSL会话缓存
4. 选择性加密敏感字段

## 🔧 故障排除

### 常见问题

1. **SSL握手失败**
   - 检查证书有效期
   - 验证证书链完整性
   - 确认TLS版本兼容性

2. **数据库连接失败**
   - 验证SSL证书配置
   - 检查防火墙规则
   - 确认数据库SSL配置

3. **加密/解密错误**
   - 验证密钥格式
   - 检查环境变量设置
   - 确认加密算法配置

## 📝 维护建议

1. **定期更新证书**（Let's Encrypt每90天）
2. **监控SSL评级**（SSL Labs）
3. **定期轮换加密密钥**（建议每年）
4. **安全审计日志**（记录所有加密操作）
5. **备份加密密钥**（安全存储）

---

*文档版本: v1.0 | 最后更新: 2025年6月18日*
