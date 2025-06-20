-- 智慧校园管理系统 - 文件管理表结构
-- 创建时间: 2025-06-20
-- 版本: V1.1

-- 文件信息表
CREATE TABLE IF NOT EXISTS tb_file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(50) NOT NULL COMMENT '文件类型',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    md5_hash CHAR(32) NOT NULL COMMENT '文件MD5值',
    sha256_hash CHAR(64) COMMENT '文件SHA256值',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    business_type VARCHAR(50) COMMENT '业务类型',
    business_id VARCHAR(100) COMMENT '业务ID',
    upload_user_id BIGINT NOT NULL COMMENT '上传用户ID',
    upload_ip VARCHAR(50) COMMENT '上传IP',
    upload_user_agent TEXT COMMENT '上传用户代理',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    last_access_time DATETIME COMMENT '最后访问时间',
    file_status TINYINT DEFAULT 1 COMMENT '文件状态：1-正常，2-隔离，3-删除',
    virus_scan_status TINYINT DEFAULT 0 COMMENT '病毒扫描状态：0-未扫描，1-安全，2-威胁',
    virus_scan_time DATETIME COMMENT '病毒扫描时间',
    storage_type VARCHAR(20) DEFAULT 'local' COMMENT '存储类型：local,oss,minio',
    storage_bucket VARCHAR(100) COMMENT '存储桶名称',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_md5_hash (md5_hash),
    INDEX idx_business (business_type, business_id),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_file_status (file_status, deleted),
    INDEX idx_virus_scan (virus_scan_status, deleted),
    INDEX idx_created_at (created_at),
    INDEX idx_file_type (file_type),
    INDEX idx_storage_type (storage_type),
    
    -- 外键约束
    FOREIGN KEY (upload_user_id) REFERENCES tb_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- 文件版本表
CREATE TABLE IF NOT EXISTS tb_file_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    version_number INT NOT NULL COMMENT '版本号',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    stored_file_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小',
    md5_hash CHAR(32) NOT NULL COMMENT 'MD5值',
    sha256_hash CHAR(64) COMMENT 'SHA256值',
    upload_user_id BIGINT NOT NULL COMMENT '上传用户ID',
    version_comment TEXT COMMENT '版本说明',
    is_current TINYINT DEFAULT 0 COMMENT '是否当前版本',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_version_number (file_id, version_number),
    INDEX idx_is_current (file_id, is_current),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES tb_file_info(id) ON DELETE CASCADE,
    FOREIGN KEY (upload_user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    
    -- 唯一约束
    UNIQUE KEY uk_file_version (file_id, version_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件版本表';

-- 文件权限表
CREATE TABLE IF NOT EXISTS tb_file_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    user_id BIGINT COMMENT '用户ID',
    role_id BIGINT COMMENT '角色ID',
    permission_type VARCHAR(20) NOT NULL COMMENT '权限类型：read,write,delete,share',
    granted_by BIGINT NOT NULL COMMENT '授权人ID',
    granted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    expire_at DATETIME COMMENT '过期时间',
    is_active TINYINT DEFAULT 1 COMMENT '是否有效',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_type (permission_type),
    INDEX idx_granted_by (granted_by),
    INDEX idx_expire_at (expire_at),
    INDEX idx_is_active (is_active, deleted),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES tb_file_info(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    FOREIGN KEY (granted_by) REFERENCES tb_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件权限表';

-- 文件访问日志表
CREATE TABLE IF NOT EXISTS tb_file_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    user_id BIGINT COMMENT '用户ID',
    access_type VARCHAR(20) NOT NULL COMMENT '访问类型：view,download,upload,delete',
    client_ip VARCHAR(50) COMMENT '客户端IP',
    user_agent TEXT COMMENT '用户代理',
    access_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    access_result VARCHAR(20) DEFAULT 'success' COMMENT '访问结果：success,failed,denied',
    error_message TEXT COMMENT '错误信息',
    file_size BIGINT COMMENT '文件大小',
    transfer_time INT COMMENT '传输时间（毫秒）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_user_id (user_id),
    INDEX idx_access_type (access_type),
    INDEX idx_access_time (access_time),
    INDEX idx_client_ip (client_ip),
    INDEX idx_access_result (access_result),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES tb_file_info(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件访问日志表';

-- 文件分享表
CREATE TABLE IF NOT EXISTS tb_file_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分享ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    share_code VARCHAR(32) NOT NULL UNIQUE COMMENT '分享码',
    share_user_id BIGINT NOT NULL COMMENT '分享用户ID',
    share_type VARCHAR(20) NOT NULL COMMENT '分享类型：public,private,password',
    share_password VARCHAR(100) COMMENT '分享密码',
    access_count INT DEFAULT 0 COMMENT '访问次数',
    max_access_count INT COMMENT '最大访问次数',
    expire_at DATETIME COMMENT '过期时间',
    is_active TINYINT DEFAULT 1 COMMENT '是否有效',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_share_code (share_code),
    INDEX idx_share_user (share_user_id),
    INDEX idx_share_type (share_type),
    INDEX idx_expire_at (expire_at),
    INDEX idx_is_active (is_active, deleted),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES tb_file_info(id) ON DELETE CASCADE,
    FOREIGN KEY (share_user_id) REFERENCES tb_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件分享表';

-- 文件标签表
CREATE TABLE IF NOT EXISTS tb_file_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    tag_color VARCHAR(7) DEFAULT '#1890ff' COMMENT '标签颜色',
    tag_description VARCHAR(200) COMMENT '标签描述',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_tag_name (tag_name),
    INDEX idx_created_by (created_by),
    INDEX idx_use_count (use_count),
    
    -- 外键约束
    FOREIGN KEY (created_by) REFERENCES tb_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件标签表';

-- 文件标签关联表
CREATE TABLE IF NOT EXISTS tb_file_tag_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    file_id BIGINT NOT NULL COMMENT '文件ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    tagged_by BIGINT NOT NULL COMMENT '标记者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_file_id (file_id),
    INDEX idx_tag_id (tag_id),
    INDEX idx_tagged_by (tagged_by),
    
    -- 外键约束
    FOREIGN KEY (file_id) REFERENCES tb_file_info(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tb_file_tag(id) ON DELETE CASCADE,
    FOREIGN KEY (tagged_by) REFERENCES tb_user(id) ON DELETE RESTRICT,
    
    -- 唯一约束
    UNIQUE KEY uk_file_tag (file_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件标签关联表';

-- 插入初始数据
INSERT INTO tb_file_tag (tag_name, tag_color, tag_description, created_by) VALUES
('重要', '#ff4d4f', '重要文件', 1),
('草稿', '#faad14', '草稿文件', 1),
('已完成', '#52c41a', '已完成文件', 1),
('待审核', '#1890ff', '待审核文件', 1),
('归档', '#8c8c8c', '归档文件', 1);

COMMIT;
