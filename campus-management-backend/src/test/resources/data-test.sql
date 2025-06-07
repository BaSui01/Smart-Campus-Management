-- 测试环境初始数据
-- 这个文件包含测试时需要的基础数据

-- 插入测试角色
INSERT INTO roles (id, role_name, role_key, description, status, deleted) VALUES
(1, '系统管理员', 'ADMIN', '系统管理员角色，拥有所有权限', 1, 0),
(2, '教师', 'TEACHER', '教师角色，可以管理课程和学生', 1, 0),
(3, '学生', 'STUDENT', '学生角色，可以查看课程和成绩', 1, 0),
(4, '教务管理员', 'ACADEMIC_ADMIN', '教务管理员角色，管理教学相关事务', 1, 0),
(5, '财务管理员', 'FINANCE_ADMIN', '财务管理员角色，管理费用相关事务', 1, 0);

-- 插入测试权限
INSERT INTO permissions (id, permission_name, permission_key, resource_type, resource_path, description, status, deleted) VALUES
(1, '用户管理', 'user:manage', 'MENU', '/admin/users', '用户管理权限', 1, 0),
(2, '角色管理', 'role:manage', 'MENU', '/admin/roles', '角色管理权限', 1, 0),
(3, '权限管理', 'permission:manage', 'MENU', '/admin/permissions', '权限管理权限', 1, 0),
(4, '学生管理', 'student:manage', 'MENU', '/admin/students', '学生管理权限', 1, 0),
(5, '课程管理', 'course:manage', 'MENU', '/admin/courses', '课程管理权限', 1, 0),
(6, '成绩管理', 'grade:manage', 'MENU', '/admin/grades', '成绩管理权限', 1, 0),
(7, '费用管理', 'fee:manage', 'MENU', '/admin/fees', '费用管理权限', 1, 0),
(8, '系统设置', 'system:setting', 'MENU', '/admin/settings', '系统设置权限', 1, 0);

-- 插入角色权限关联
INSERT INTO role_permissions (role_id, permission_id) VALUES
-- 系统管理员拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
-- 教师拥有学生管理、课程管理、成绩管理权限
(2, 4), (2, 5), (2, 6),
-- 学生只有查看权限（这里暂不设置具体权限）
-- 教务管理员拥有学生管理、课程管理、成绩管理权限
(4, 4), (4, 5), (4, 6),
-- 财务管理员拥有费用管理权限
(5, 7);

-- 插入测试用户（密码都是对应的明文加密后的结果）
-- admin123, teacher123, student123 的BCrypt加密结果
INSERT INTO users (id, username, password, real_name, email, phone, status, deleted) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pMzxKw6', '系统管理员', 'admin@test.com', '13800000001', 1, 0),
(2, 'teacher', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pMzxKw6', '测试教师', 'teacher@test.com', '13800000002', 1, 0),
(3, 'student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pMzxKw6', '测试学生', 'student@test.com', '13800000003', 1, 0),
(4, 'academic', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pMzxKw6', '教务管理员', 'academic@test.com', '13800000004', 1, 0),
(5, 'finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLIU8pMzxKw6', '财务管理员', 'finance@test.com', '13800000005', 1, 0);

-- 插入用户角色关联
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- teacher -> TEACHER
(3, 3), -- student -> STUDENT
(4, 4), -- academic -> ACADEMIC_ADMIN
(5, 5); -- finance -> FINANCE_ADMIN

-- 插入测试院系
INSERT INTO departments (id, dept_name, dept_code, dept_type, dept_level, parent_id, sort_order, status, deleted) VALUES
(1, '计算机学院', 'CS', 'COLLEGE', 1, NULL, 1, 1, 0),
(2, '软件工程系', 'SE', 'DEPARTMENT', 2, 1, 1, 1, 0),
(3, '计算机科学与技术系', 'CST', 'DEPARTMENT', 2, 1, 2, 1, 0),
(4, '信息管理学院', 'IM', 'COLLEGE', 1, NULL, 2, 1, 0),
(5, '信息管理与信息系统系', 'IMIS', 'DEPARTMENT', 2, 4, 1, 1, 0);

-- 插入测试班级
INSERT INTO school_classes (id, class_name, class_code, grade, department_id, head_teacher_id, max_students, current_students, status, deleted) VALUES
(1, '软件工程2021级1班', 'SE2021-1', '2021级', 2, 2, 50, 30, 1, 0),
(2, '软件工程2021级2班', 'SE2021-2', '2021级', 2, 2, 50, 28, 1, 0),
(3, '计算机科学2021级1班', 'CS2021-1', '2021级', 3, 2, 45, 35, 1, 0),
(4, '信息管理2021级1班', 'IM2021-1', '2021级', 5, 2, 40, 25, 1, 0);

-- 插入测试学生
INSERT INTO students (id, student_number, user_id, class_id, grade, enrollment_year, major, status, deleted) VALUES
(1, '2021001001', 3, 1, '2021级', 2021, '软件工程', 1, 0);

-- 插入测试课程
INSERT INTO courses (id, course_name, course_code, course_type, credits, hours, department_id, teacher_id, semester, status, deleted) VALUES
(1, 'Java程序设计', 'CS001', '专业必修', 4.0, 64, 2, 2, '2024-1', 1, 0),
(2, '数据结构与算法', 'CS002', '专业必修', 4.0, 64, 2, 2, '2024-1', 1, 0),
(3, '数据库原理', 'CS003', '专业必修', 3.0, 48, 2, 2, '2024-1', 1, 0),
(4, '软件工程', 'SE001', '专业必修', 3.0, 48, 2, 2, '2024-2', 1, 0),
(5, '计算机网络', 'CS004', '专业必修', 3.0, 48, 3, 2, '2024-2', 1, 0);

-- 插入测试系统设置
INSERT INTO system_settings (setting_key, setting_value, setting_type, setting_description) VALUES
('system.name', 'Smart Campus Management Test', 'BASIC', '系统名称'),
('system.version', '1.0.0-TEST', 'BASIC', '系统版本'),
('system.logo', '/images/logo-test.png', 'BASIC', '系统Logo'),
('contact.email', 'admin@test.com', 'CONTACT', '联系邮箱'),
('contact.phone', '400-000-0000', 'CONTACT', '联系电话'),
('security.password.min.length', '6', 'SECURITY', '密码最小长度'),
('security.login.max.attempts', '5', 'SECURITY', '最大登录尝试次数'),
('notification.email.enabled', 'true', 'NOTIFICATION', '是否启用邮件通知'),
('notification.sms.enabled', 'false', 'NOTIFICATION', '是否启用短信通知'),
('cache.enabled', 'true', 'CACHE', '是否启用缓存');

-- 重置自增ID序列（H2数据库）
ALTER SEQUENCE USERS_SEQ RESTART WITH 100;
ALTER SEQUENCE ROLES_SEQ RESTART WITH 100;
ALTER SEQUENCE PERMISSIONS_SEQ RESTART WITH 100;
ALTER SEQUENCE DEPARTMENTS_SEQ RESTART WITH 100;
ALTER SEQUENCE SCHOOL_CLASSES_SEQ RESTART WITH 100;
ALTER SEQUENCE STUDENTS_SEQ RESTART WITH 100;
ALTER SEQUENCE COURSES_SEQ RESTART WITH 100;
ALTER SEQUENCE SYSTEM_SETTINGS_SEQ RESTART WITH 100;
