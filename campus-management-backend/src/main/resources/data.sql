-- 智慧校园管理系统初始化数据
-- 注意：这些是测试数据，生产环境请修改密码

-- 清理现有数据（可选）
-- DELETE FROM users WHERE username IN ('admin', 'teacher001', 'student001', 'parent001');

-- 插入管理员用户
INSERT INTO users (username, password, real_name, email, phone, gender, status, created_time, updated_time) 
VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBZWvsq7wMqKHu', '系统管理员', 'admin@campus.edu', '13800138000', 'MALE', 1, NOW(), NOW()),
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBZWvsq7wMqKHu', '张老师', 'teacher001@campus.edu', '13800138001', 'FEMALE', 1, NOW(), NOW()),
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBZWvsq7wMqKHu', '李同学', 'student001@campus.edu', '13800138002', 'MALE', 1, NOW(), NOW()),
('parent001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBZWvsq7wMqKHu', '王家长', 'parent001@campus.edu', '13800138003', 'FEMALE', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    real_name = VALUES(real_name),
    email = VALUES(email),
    phone = VALUES(phone),
    gender = VALUES(gender),
    status = VALUES(status),
    updated_time = NOW();

-- 注意：上面的密码是 'admin123' 经过 BCrypt 加密后的结果
-- 如果需要其他密码，请使用 BCrypt 工具生成对应的哈希值

-- 插入部门数据
INSERT INTO departments (name, description, status, created_time, updated_time)
VALUES 
('计算机科学与技术学院', '负责计算机相关专业的教学和管理', 1, NOW(), NOW()),
('数学与统计学院', '负责数学和统计学相关专业的教学和管理', 1, NOW(), NOW()),
('外国语学院', '负责外语专业的教学和管理', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    description = VALUES(description),
    status = VALUES(status),
    updated_time = NOW();

-- 插入课程数据
INSERT INTO courses (course_code, course_name, credits, hours, department_id, description, status, created_time, updated_time)
VALUES 
('CS101', 'Java程序设计', 3, 48, 1, 'Java编程语言基础课程', 1, NOW(), NOW()),
('CS102', '数据结构与算法', 4, 64, 1, '计算机科学核心课程', 1, NOW(), NOW()),
('MATH101', '高等数学', 4, 64, 2, '数学基础课程', 1, NOW(), NOW()),
('ENG101', '大学英语', 2, 32, 3, '英语基础课程', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    course_name = VALUES(course_name),
    credits = VALUES(credits),
    hours = VALUES(hours),
    description = VALUES(description),
    status = VALUES(status),
    updated_time = NOW();

-- 插入班级数据
INSERT INTO classes (class_name, grade, major, department_id, head_teacher_id, student_count, status, created_time, updated_time)
VALUES 
('计算机2024-1班', '2024', '计算机科学与技术', 1, 2, 30, 1, NOW(), NOW()),
('计算机2024-2班', '2024', '计算机科学与技术', 1, 2, 28, 1, NOW(), NOW()),
('数学2024-1班', '2024', '数学与应用数学', 2, 2, 25, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    grade = VALUES(grade),
    major = VALUES(major),
    head_teacher_id = VALUES(head_teacher_id),
    student_count = VALUES(student_count),
    status = VALUES(status),
    updated_time = NOW();

-- 插入学生信息
INSERT INTO students (user_id, student_number, class_id, enrollment_year, status, created_time, updated_time)
VALUES 
(3, '2024001001', 1, '2024', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    class_id = VALUES(class_id),
    enrollment_year = VALUES(enrollment_year),
    status = VALUES(status),
    updated_time = NOW();

-- 插入教师信息
INSERT INTO teachers (user_id, employee_number, department_id, title, status, created_time, updated_time)
VALUES 
(2, 'T2024001', 1, '副教授', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    department_id = VALUES(department_id),
    title = VALUES(title),
    status = VALUES(status),
    updated_time = NOW();

-- 插入通知公告
INSERT INTO announcements (title, content, type, priority, publisher_id, target_audience, status, publish_time, created_time, updated_time)
VALUES 
('欢迎使用智慧校园管理系统', '欢迎大家使用智慧校园管理系统！本系统提供完整的校园管理功能，包括学生管理、教师管理、课程管理等。', 'SYSTEM', 'HIGH', 1, 'ALL', 1, NOW(), NOW(), NOW()),
('系统维护通知', '系统将于本周末进行例行维护，维护期间可能会影响部分功能的使用，请大家提前做好准备。', 'MAINTENANCE', 'MEDIUM', 1, 'ALL', 1, NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    content = VALUES(content),
    type = VALUES(type),
    priority = VALUES(priority),
    target_audience = VALUES(target_audience),
    status = VALUES(status),
    updated_time = NOW();

-- 插入系统配置
INSERT INTO system_configs (config_key, config_value, description, created_time, updated_time)
VALUES 
('system.name', '智慧校园管理系统', '系统名称', NOW(), NOW()),
('system.version', '1.0.0', '系统版本', NOW(), NOW()),
('system.maintenance.enabled', 'false', '维护模式开关', NOW(), NOW()),
('auth.jwt.expiration', '86400', 'JWT令牌过期时间（秒）', NOW(), NOW()),
('auth.password.min.length', '6', '密码最小长度', NOW(), NOW()),
('upload.max.file.size', '10485760', '文件上传最大大小（字节）', NOW(), NOW())
ON DUPLICATE KEY UPDATE 
    config_value = VALUES(config_value),
    description = VALUES(description),
    updated_time = NOW();

-- 提交事务
COMMIT;
