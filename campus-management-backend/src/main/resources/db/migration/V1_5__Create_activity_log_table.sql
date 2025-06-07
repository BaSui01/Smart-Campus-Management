-- 创建活动日志表
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(100) COMMENT '操作用户名',
    real_name VARCHAR(100) COMMENT '操作用户真实姓名',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型',
    module VARCHAR(100) NOT NULL COMMENT '操作模块',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    description VARCHAR(500) NOT NULL COMMENT '活动描述',
    target_type VARCHAR(100) COMMENT '目标对象类型',
    target_id BIGINT COMMENT '目标对象ID',
    target_name VARCHAR(200) COMMENT '目标对象名称',
    result VARCHAR(20) DEFAULT 'SUCCESS' COMMENT '操作结果',
    error_message TEXT COMMENT '错误信息',
    ip_address VARCHAR(45) COMMENT '请求IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_path VARCHAR(500) COMMENT '请求路径',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_params TEXT COMMENT '请求参数',
    response_time BIGINT COMMENT '响应时间（毫秒）',
    level VARCHAR(20) DEFAULT 'INFO' COMMENT '操作级别',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    remarks VARCHAR(1000) COMMENT '备注',
    
    INDEX idx_user_id (user_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_module (module),
    INDEX idx_level (level),
    INDEX idx_result (result),
    INDEX idx_create_time (create_time),
    INDEX idx_username (username),
    INDEX idx_target_type_id (target_type, target_id),
    
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动日志表';

-- 插入一些测试数据
INSERT INTO activity_logs (user_id, username, real_name, activity_type, module, action, description, result, level, ip_address, create_time) VALUES
(1, 'admin', '系统管理员', 'LOGIN', '用户管理', '登录', '管理员登录系统', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 1 HOUR),
(1, 'admin', '系统管理员', 'OPERATION', '学生管理', '查看', '查看学生列表', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 50 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '课程管理', '添加', '添加新课程：高等数学', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 45 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '财务管理', '查看', '查看缴费记录', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 40 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '通知管理', '发布', '发布系统维护通知', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 35 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '用户管理', '编辑', '修改用户权限', 'SUCCESS', 'WARN', '127.0.0.1', NOW() - INTERVAL 30 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '系统管理', '配置', '修改系统配置', 'SUCCESS', 'WARN', '127.0.0.1', NOW() - INTERVAL 25 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '学生管理', '导入', '批量导入学生数据', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 20 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '课程管理', '删除', '删除过期课程', 'SUCCESS', 'WARN', '127.0.0.1', NOW() - INTERVAL 15 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '财务管理', '审核', '审核缴费记录', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 10 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '通知管理', '编辑', '编辑通知内容', 'SUCCESS', 'INFO', '127.0.0.1', NOW() - INTERVAL 5 MINUTE),
(1, 'admin', '系统管理员', 'OPERATION', '活动日志', '查看', '查看系统活动日志', 'SUCCESS', 'INFO', '127.0.0.1', NOW());
