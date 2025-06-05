-- 创建系统设置表
CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    setting_description VARCHAR(500) COMMENT '设置描述',
    setting_type VARCHAR(50) DEFAULT 'STRING' COMMENT '设置类型: STRING, NUMBER, BOOLEAN, JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- 创建索引
CREATE INDEX idx_system_settings_key ON system_settings(setting_key);
CREATE INDEX idx_system_settings_type ON system_settings(setting_type);

-- 插入默认系统设置
INSERT INTO system_settings (setting_key, setting_value, setting_description, setting_type) VALUES
-- 基本设置
('system.name', '智慧校园管理系统', '系统名称', 'STRING'),
('system.version', '1.0.0', '系统版本', 'STRING'),
('system.logo', '/images/logo.png', '系统Logo路径', 'STRING'),
('contact.email', 'admin@campus.edu.cn', '联系邮箱', 'STRING'),
('contact.phone', '400-000-0000', '联系电话', 'STRING'),

-- 安全设置
('security.max_login_attempts', '5', '最大登录尝试次数', 'NUMBER'),
('security.session_timeout', '30', '会话超时时间（分钟）', 'NUMBER'),
('security.password_min_length', '6', '密码最小长度', 'NUMBER'),
('security.enable_captcha', 'true', '启用验证码', 'BOOLEAN'),

-- 通知设置
('notification.email.enabled', 'true', '启用邮件通知', 'BOOLEAN'),
('notification.sms.enabled', 'false', '启用短信通知', 'BOOLEAN'),
('notification.email.address', '', '通知邮箱地址', 'STRING'),
('notification.sms.phone', '', '通知手机号码', 'STRING'),

-- 其他设置
('system.maintenance_mode', 'false', '维护模式', 'BOOLEAN'),
('system.backup_enabled', 'true', '启用自动备份', 'BOOLEAN'),
('system.log_level', 'INFO', '日志级别', 'STRING')
ON DUPLICATE KEY UPDATE 
    setting_description = VALUES(setting_description),
    setting_type = VALUES(setting_type);

-- 查看插入结果
SELECT COUNT(*) as '系统设置数量' FROM system_settings;
SELECT setting_key, setting_value, setting_description FROM system_settings ORDER BY setting_key;