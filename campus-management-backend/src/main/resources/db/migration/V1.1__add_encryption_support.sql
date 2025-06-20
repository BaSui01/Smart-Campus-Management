-- 数据库迁移脚本：添加加密支持
-- 版本：V1.1
-- 描述：为敏感字段增加长度以支持加密存储
-- 作者：Campus Management Team
-- 日期：2025-06-18

-- 修改用户表敏感字段长度
ALTER TABLE tb_user 
MODIFY COLUMN email VARCHAR(255) COMMENT '邮箱（加密存储）',
MODIFY COLUMN phone VARCHAR(255) COMMENT '手机号（加密存储）',
MODIFY COLUMN id_card VARCHAR(255) COMMENT '身份证号（加密存储）';

-- 修改学生表敏感字段长度
ALTER TABLE tb_student 
MODIFY COLUMN parent_phone VARCHAR(255) COMMENT '家长电话（加密存储）',
MODIFY COLUMN emergency_phone VARCHAR(255) COMMENT '紧急联系人电话（加密存储）';

-- 修改缴费记录表敏感字段长度
ALTER TABLE tb_payment_record 
MODIFY COLUMN transaction_no VARCHAR(255) COMMENT '交易流水号（加密存储）';

-- 添加加密配置表
CREATE TABLE IF NOT EXISTS tb_encryption_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(500) COMMENT '配置描述',
    is_encrypted TINYINT DEFAULT 0 COMMENT '是否加密存储',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：1-已删除，0-未删除',
    INDEX idx_config_key (config_key),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加密配置表';

-- 插入默认加密配置
INSERT INTO tb_encryption_config (config_key, config_value, description, is_encrypted) VALUES
('encryption.enabled', 'true', '是否启用数据加密', 0),
('encryption.algorithm', 'AES-256-GCM', '加密算法', 0),
('encryption.key.rotation.enabled', 'false', '是否启用密钥轮换', 0),
('encryption.key.rotation.interval', '365', '密钥轮换间隔（天）', 0);

-- 添加数据加密审计日志表
CREATE TABLE IF NOT EXISTS tb_encryption_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型：ENCRYPT, DECRYPT, KEY_ROTATION',
    table_name VARCHAR(100) COMMENT '表名',
    field_name VARCHAR(100) COMMENT '字段名',
    record_id BIGINT COMMENT '记录ID',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    operator_id BIGINT COMMENT '操作员ID',
    operator_ip VARCHAR(50) COMMENT '操作员IP',
    success TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功：1-成功，0-失败',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_operation_type (operation_type),
    INDEX idx_table_field (table_name, field_name),
    INDEX idx_operation_time (operation_time),
    INDEX idx_operator_id (operator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据加密审计日志表';

-- 创建加密密钥管理表（仅存储密钥元数据，不存储实际密钥）
CREATE TABLE IF NOT EXISTS tb_encryption_key_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    key_id VARCHAR(100) NOT NULL UNIQUE COMMENT '密钥ID',
    key_version INT NOT NULL DEFAULT 1 COMMENT '密钥版本',
    algorithm VARCHAR(50) NOT NULL COMMENT '加密算法',
    key_size INT NOT NULL COMMENT '密钥长度',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    activated_at DATETIME COMMENT '激活时间',
    deactivated_at DATETIME COMMENT '停用时间',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE, INACTIVE, ROTATED',
    description VARCHAR(500) COMMENT '描述',
    INDEX idx_key_id (key_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加密密钥元数据表';

-- 插入默认密钥元数据
INSERT INTO tb_encryption_key_metadata (key_id, algorithm, key_size, description, activated_at) VALUES
('campus-encryption-key-v1', 'AES-256-GCM', 256, '智慧校园系统默认加密密钥', NOW());

-- 添加数据备份表（用于加密迁移时的数据备份）
CREATE TABLE IF NOT EXISTS tb_data_backup_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    backup_type VARCHAR(50) NOT NULL COMMENT '备份类型：ENCRYPTION_MIGRATION, KEY_ROTATION',
    table_name VARCHAR(100) NOT NULL COMMENT '表名',
    backup_file_path VARCHAR(500) COMMENT '备份文件路径',
    record_count BIGINT COMMENT '备份记录数',
    backup_size BIGINT COMMENT '备份文件大小（字节）',
    backup_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '备份时间',
    restore_time DATETIME COMMENT '恢复时间',
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED' COMMENT '状态：COMPLETED, FAILED, RESTORED',
    checksum VARCHAR(64) COMMENT '文件校验和',
    description VARCHAR(500) COMMENT '备份描述',
    created_by BIGINT COMMENT '创建者ID',
    INDEX idx_backup_type (backup_type),
    INDEX idx_table_name (table_name),
    INDEX idx_backup_time (backup_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据备份日志表';

-- 创建系统安全配置表
CREATE TABLE IF NOT EXISTS tb_security_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_category VARCHAR(50) NOT NULL COMMENT '配置分类：ENCRYPTION, SSL, JWT, PASSWORD',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    is_sensitive TINYINT DEFAULT 0 COMMENT '是否敏感配置',
    description VARCHAR(500) COMMENT '配置描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：1-已删除，0-未删除',
    UNIQUE KEY uk_category_key (config_category, config_key),
    INDEX idx_config_category (config_category),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统安全配置表';

-- 插入默认安全配置
INSERT INTO tb_security_config (config_category, config_key, config_value, description, is_sensitive) VALUES
('ENCRYPTION', 'field.encryption.enabled', 'true', '字段级加密是否启用', 0),
('ENCRYPTION', 'database.encryption.enabled', 'false', '数据库级加密是否启用', 0),
('SSL', 'https.enabled', 'false', 'HTTPS是否启用', 0),
('SSL', 'ssl.key.store.type', 'PKCS12', 'SSL密钥库类型', 0),
('JWT', 'token.expiration.hours', '2', 'JWT令牌过期时间（小时）', 0),
('JWT', 'refresh.token.expiration.days', '7', '刷新令牌过期时间（天）', 0),
('PASSWORD', 'min.length', '8', '密码最小长度', 0),
('PASSWORD', 'max.attempts', '3', '密码最大尝试次数', 0),
('PASSWORD', 'lockout.duration.minutes', '15', '账户锁定时长（分钟）', 0);

-- 添加索引优化
ALTER TABLE tb_user ADD INDEX idx_email_encrypted (email(50));
ALTER TABLE tb_user ADD INDEX idx_phone_encrypted (phone(50));
ALTER TABLE tb_student ADD INDEX idx_parent_phone_encrypted (parent_phone(50));
ALTER TABLE tb_payment_record ADD INDEX idx_transaction_no_encrypted (transaction_no(50));

-- 更新表注释
ALTER TABLE tb_user COMMENT='用户表（支持字段级加密）';
ALTER TABLE tb_student COMMENT='学生表（支持字段级加密）';
ALTER TABLE tb_payment_record COMMENT='缴费记录表（支持字段级加密）';

-- 记录迁移日志
INSERT INTO tb_data_backup_log (backup_type, table_name, description, created_by) VALUES
('ENCRYPTION_MIGRATION', 'tb_user', '用户表加密字段长度调整', 1),
('ENCRYPTION_MIGRATION', 'tb_student', '学生表加密字段长度调整', 1),
('ENCRYPTION_MIGRATION', 'tb_payment_record', '缴费记录表加密字段长度调整', 1);
