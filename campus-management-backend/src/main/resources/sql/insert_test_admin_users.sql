-- 插入测试管理员用户
-- 密码都是 123456

-- 1. 超级管理员 (ADMIN)
INSERT INTO users (username, password, real_name, email, phone, status, user_type, created_at, updated_at) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i', '系统管理员', 'admin@campus.edu.cn', '13800000001', 1, 'ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
password = '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i',
real_name = '系统管理员',
email = 'admin@campus.edu.cn',
phone = '13800000001',
status = 1,
user_type = 'ADMIN',
updated_at = NOW();

-- 2. 系统管理员 (SYSTEM_ADMIN)
INSERT INTO users (username, password, real_name, email, phone, status, user_type, created_at, updated_at) 
VALUES ('sysadmin', '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i', '系统管理员', 'sysadmin@campus.edu.cn', '13800000002', 1, 'ADMIN', NOW(), NOW());

-- 3. 教务管理员 (ACADEMIC_ADMIN)
INSERT INTO users (username, password, real_name, email, phone, status, user_type, created_at, updated_at) 
VALUES ('academic', '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i', '教务管理员', 'academic@campus.edu.cn', '13800000003', 1, 'ADMIN', NOW(), NOW());

-- 4. 财务管理员 (FINANCE_ADMIN)
INSERT INTO users (username, password, real_name, email, phone, status, user_type, created_at, updated_at) 
VALUES ('finance', '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i', '财务管理员', 'finance@campus.edu.cn', '13800000004', 1, 'ADMIN', NOW(), NOW());

-- 5. 教师 (TEACHER)
INSERT INTO users (username, password, real_name, email, phone, status, user_type, created_at, updated_at) 
VALUES ('teacher', '$2a$10$N.zmdr9k7uOCQb97.WnHZOd7BkTNiIa3tpZK2lIqKpjstQy.UDW6i', '教师用户', 'teacher@campus.edu.cn', '13800000005', 1, 'TEACHER', NOW(), NOW());

-- 确保角色存在
INSERT INTO roles (role_name, role_key, description, status, created_at, updated_at) 
VALUES 
('超级管理员', 'ADMIN', '系统超级管理员，拥有所有权限', 1, NOW(), NOW()),
('系统管理员', 'SYSTEM_ADMIN', '系统管理员，负责用户和系统管理', 1, NOW(), NOW()),
('教务管理员', 'ACADEMIC_ADMIN', '教务管理员，负责教学相关管理', 1, NOW(), NOW()),
('财务管理员', 'FINANCE_ADMIN', '财务管理员，负责财务相关管理', 1, NOW(), NOW()),
('教师', 'TEACHER', '教师角色，可以查看教学相关信息', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
description = VALUES(description),
status = VALUES(status),
updated_at = NOW();

-- 分配角色给用户
-- 超级管理员
INSERT INTO user_roles (user_id, role_id, created_at) 
SELECT u.id, r.id, NOW() 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.role_key = 'ADMIN'
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 系统管理员
INSERT INTO user_roles (user_id, role_id, created_at) 
SELECT u.id, r.id, NOW() 
FROM users u, roles r 
WHERE u.username = 'sysadmin' AND r.role_key = 'SYSTEM_ADMIN'
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 教务管理员
INSERT INTO user_roles (user_id, role_id, created_at) 
SELECT u.id, r.id, NOW() 
FROM users u, roles r 
WHERE u.username = 'academic' AND r.role_key = 'ACADEMIC_ADMIN'
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 财务管理员
INSERT INTO user_roles (user_id, role_id, created_at) 
SELECT u.id, r.id, NOW() 
FROM users u, roles r 
WHERE u.username = 'finance' AND r.role_key = 'FINANCE_ADMIN'
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 教师
INSERT INTO user_roles (user_id, role_id, created_at) 
SELECT u.id, r.id, NOW() 
FROM users u, roles r 
WHERE u.username = 'teacher' AND r.role_key = 'TEACHER'
ON DUPLICATE KEY UPDATE created_at = NOW();

-- 验证插入结果
SELECT 
    u.username,
    u.real_name,
    u.email,
    r.role_name,
    r.role_key
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE u.username IN ('admin', 'sysadmin', 'academic', 'finance', 'teacher')
ORDER BY u.username;
