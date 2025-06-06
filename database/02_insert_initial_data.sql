-- ================================================
-- 校园管理系统初始数据插入脚本
-- 创建时间: 2025-06-06
-- 编码: UTF-8
-- 版本: 2.0
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 插入院系数据
-- ================================================
INSERT INTO tb_department (dept_name, dept_code, parent_id, dept_type, dept_level, sort_order, phone, email, address, description, status, created_at, updated_at, deleted) VALUES
-- 学院级
('计算机科学与技术学院', 'CS', NULL, 'college', 1, 1, '010-12345001', 'cs@university.edu.cn', '计算机楼A座', '培养计算机科学与技术专业人才', 1, NOW(), NOW(), 0),
('软件学院', 'SE', NULL, 'college', 1, 2, '010-12345002', 'se@university.edu.cn', '软件楼B座', '培养软件工程专业人才', 1, NOW(), NOW(), 0),
('信息安全学院', 'IS', NULL, 'college', 1, 3, '010-12345003', 'is@university.edu.cn', '信息楼C座', '培养信息安全专业人才', 1, NOW(), NOW(), 0),
('人工智能学院', 'AI', NULL, 'college', 1, 4, '010-12345004', 'ai@university.edu.cn', 'AI楼D座', '培养人工智能专业人才', 1, NOW(), NOW(), 0),

-- 系级（计算机科学与技术学院下）
('计算机科学与技术系', 'CST', 1, 'department', 2, 1, '010-12345011', 'cst@university.edu.cn', '计算机楼A座201', '计算机科学与技术专业教学', 1, NOW(), NOW(), 0),
('网络工程系', 'NE', 1, 'department', 2, 2, '010-12345012', 'ne@university.edu.cn', '计算机楼A座202', '网络工程专业教学', 1, NOW(), NOW(), 0),
('物联网工程系', 'IOT', 1, 'department', 2, 3, '010-12345013', 'iot@university.edu.cn', '计算机楼A座203', '物联网工程专业教学', 1, NOW(), NOW(), 0),

-- 系级（软件学院下）
('软件工程系', 'SWE', 2, 'department', 2, 1, '010-12345021', 'swe@university.edu.cn', '软件楼B座201', '软件工程专业教学', 1, NOW(), NOW(), 0),
('数据科学与大数据技术系', 'DS', 2, 'department', 2, 2, '010-12345022', 'ds@university.edu.cn', '软件楼B座202', '数据科学专业教学', 1, NOW(), NOW(), 0),

-- 系级（人工智能学院下）
('机器学习系', 'ML', 4, 'department', 2, 1, '010-12345041', 'ml@university.edu.cn', 'AI楼D座201', '机器学习专业教学', 1, NOW(), NOW(), 0),
('智能科学与技术系', 'IST', 4, 'department', 2, 2, '010-12345042', 'ist@university.edu.cn', 'AI楼D座202', '智能科学与技术专业教学', 1, NOW(), NOW(), 0);

-- ================================================
-- 2. 插入角色数据
-- ================================================
INSERT INTO tb_role (role_name, role_key, role_sort, data_scope, remark, status, created_at, updated_at, deleted) VALUES
('系统管理员', 'ADMIN', 1, 1, '系统管理员，拥有所有权限', 1, NOW(), NOW(), 0),
('教师', 'TEACHER', 2, 4, '教师角色，可以管理课程和成绩', 1, NOW(), NOW(), 0),
('学生', 'STUDENT', 3, 4, '学生角色，可以选课和查看成绩', 1, NOW(), NOW(), 0),
('教务员', 'ACADEMIC_STAFF', 4, 2, '教务员，可以管理教务相关事项', 1, NOW(), NOW(), 0),
('财务员', 'FINANCE_STAFF', 5, 2, '财务员，可以管理缴费相关事项', 1, NOW(), NOW(), 0);

-- ================================================
-- 3. 插入权限数据
-- ================================================
INSERT INTO tb_permission (permission_code, permission_name, resource_type, resource_url, permission_desc, status, created_at, updated_at) VALUES
-- 系统管理权限
('system:user:list', '用户列表', 'api', '/api/users', '查看用户列表', 1, NOW(), NOW()),
('system:user:create', '创建用户', 'api', '/api/users', '创建新用户', 1, NOW(), NOW()),
('system:user:update', '更新用户', 'api', '/api/users/*', '更新用户信息', 1, NOW(), NOW()),
('system:user:delete', '删除用户', 'api', '/api/users/*', '删除用户', 1, NOW(), NOW()),
('system:role:list', '角色列表', 'api', '/api/roles', '查看角色列表', 1, NOW(), NOW()),
('system:role:create', '创建角色', 'api', '/api/roles', '创建新角色', 1, NOW(), NOW()),
('system:role:update', '更新角色', 'api', '/api/roles/*', '更新角色信息', 1, NOW(), NOW()),
('system:role:delete', '删除角色', 'api', '/api/roles/*', '删除角色', 1, NOW(), NOW()),

-- 院系管理权限
('department:list', '院系列表', 'api', '/api/departments', '查看院系列表', 1, NOW(), NOW()),
('department:create', '创建院系', 'api', '/api/departments', '创建新院系', 1, NOW(), NOW()),
('department:update', '更新院系', 'api', '/api/departments/*', '更新院系信息', 1, NOW(), NOW()),
('department:delete', '删除院系', 'api', '/api/departments/*', '删除院系', 1, NOW(), NOW()),

-- 班级管理权限
('class:list', '班级列表', 'api', '/api/classes', '查看班级列表', 1, NOW(), NOW()),
('class:create', '创建班级', 'api', '/api/classes', '创建新班级', 1, NOW(), NOW()),
('class:update', '更新班级', 'api', '/api/classes/*', '更新班级信息', 1, NOW(), NOW()),
('class:delete', '删除班级', 'api', '/api/classes/*', '删除班级', 1, NOW(), NOW()),

-- 学生管理权限
('student:list', '学生列表', 'api', '/api/students', '查看学生列表', 1, NOW(), NOW()),
('student:create', '创建学生', 'api', '/api/students', '创建新学生', 1, NOW(), NOW()),
('student:update', '更新学生', 'api', '/api/students/*', '更新学生信息', 1, NOW(), NOW()),
('student:delete', '删除学生', 'api', '/api/students/*', '删除学生', 1, NOW(), NOW()),

-- 课程管理权限
('course:list', '课程列表', 'api', '/api/courses', '查看课程列表', 1, NOW(), NOW()),
('course:create', '创建课程', 'api', '/api/courses', '创建新课程', 1, NOW(), NOW()),
('course:update', '更新课程', 'api', '/api/courses/*', '更新课程信息', 1, NOW(), NOW()),
('course:delete', '删除课程', 'api', '/api/courses/*', '删除课程', 1, NOW(), NOW()),

-- 选课管理权限
('course:selection:list', '选课列表', 'api', '/api/course-selections', '查看选课列表', 1, NOW(), NOW()),
('course:selection:create', '学生选课', 'api', '/api/course-selections', '学生选课', 1, NOW(), NOW()),
('course:selection:delete', '退选课程', 'api', '/api/course-selections/*', '学生退选课程', 1, NOW(), NOW()),

-- 成绩管理权限
('grade:list', '成绩列表', 'api', '/api/grades', '查看成绩列表', 1, NOW(), NOW()),
('grade:create', '录入成绩', 'api', '/api/grades', '录入学生成绩', 1, NOW(), NOW()),
('grade:update', '更新成绩', 'api', '/api/grades/*', '更新学生成绩', 1, NOW(), NOW()),
('grade:delete', '删除成绩', 'api', '/api/grades/*', '删除成绩记录', 1, NOW(), NOW()),

-- 缴费管理权限
('payment:list', '缴费列表', 'api', '/api/payments', '查看缴费列表', 1, NOW(), NOW()),
('payment:create', '记录缴费', 'api', '/api/payments', '记录学生缴费', 1, NOW(), NOW()),
('payment:update', '更新缴费', 'api', '/api/payments/*', '更新缴费记录', 1, NOW(), NOW()),
('payment:delete', '删除缴费', 'api', '/api/payments/*', '删除缴费记录', 1, NOW(), NOW());

-- ================================================
-- 4. 插入角色权限关联数据
-- ================================================
-- 管理员拥有所有权限
INSERT INTO tb_role_permission (role_id, permission_id, created_at)
SELECT 1, id, NOW() FROM tb_permission;

-- 教师权限
INSERT INTO tb_role_permission (role_id, permission_id, created_at)
SELECT 2, id, NOW() FROM tb_permission 
WHERE permission_code IN (
    'course:list', 'course:create', 'course:update',
    'grade:list', 'grade:create', 'grade:update',
    'student:list', 'class:list'
);

-- 学生权限
INSERT INTO tb_role_permission (role_id, permission_id, created_at)
SELECT 3, id, NOW() FROM tb_permission 
WHERE permission_code IN (
    'course:list', 'course:selection:list', 'course:selection:create', 'course:selection:delete',
    'grade:list'
);

-- 教务员权限
INSERT INTO tb_role_permission (role_id, permission_id, created_at)
SELECT 4, id, NOW() FROM tb_permission 
WHERE permission_code LIKE 'student:%' 
   OR permission_code LIKE 'class:%' 
   OR permission_code LIKE 'course:%'
   OR permission_code LIKE 'grade:%';

-- 财务员权限
INSERT INTO tb_role_permission (role_id, permission_id, created_at)
SELECT 5, id, NOW() FROM tb_permission 
WHERE permission_code LIKE 'payment:%' 
   OR permission_code = 'student:list';

-- ================================================
-- 5. 插入默认用户数据
-- ================================================
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, created_at, updated_at, deleted) VALUES
-- 管理员用户（密码: admin123）
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@university.edu.cn', '系统管理员', '13800000001', '男', 1, NOW(), NOW(), 0),
-- 教师用户（密码: teacher123）
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'teacher001@university.edu.cn', '张教授', '13800000002', '男', 1, NOW(), NOW(), 0),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'teacher002@university.edu.cn', '李教授', '13800000003', '女', 1, NOW(), NOW(), 0),
-- 学生用户（密码: student123）
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'student001@university.edu.cn', '王小明', '13800000004', '男', 1, NOW(), NOW(), 0),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'student002@university.edu.cn', '李小红', '13800000005', '女', 1, NOW(), NOW(), 0),
-- 教务员用户（密码: staff123）
('academic001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'academic001@university.edu.cn', '陈教务', '13800000006', '女', 1, NOW(), NOW(), 0),
-- 财务员用户（密码: finance123）
('finance001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'finance001@university.edu.cn', '刘财务', '13800000007', '男', 1, NOW(), NOW(), 0);

-- ================================================
-- 6. 插入用户角色关联数据
-- ================================================
INSERT INTO tb_user_role (user_id, role_id, created_at) VALUES
(1, 1, NOW()), -- admin -> 管理员
(2, 2, NOW()), -- teacher001 -> 教师
(3, 2, NOW()), -- teacher002 -> 教师
(4, 3, NOW()), -- student001 -> 学生
(5, 3, NOW()), -- student002 -> 学生
(6, 4, NOW()), -- academic001 -> 教务员
(7, 5, NOW()); -- finance001 -> 财务员

-- ================================================
-- 7. 插入班级数据
-- ================================================
INSERT INTO tb_class (class_name, class_code, grade, major, department_id, head_teacher_id, enrollment_year, enrollment_date, expected_graduation_date, class_type, academic_system, max_students, status, created_at, updated_at, deleted) VALUES
('计科2021级1班', 'CS2021-1', '2021级', '计算机科学与技术', 5, 2, 2021, '2021-09-01', '2025-06-30', 'normal', 4, 30, 1, NOW(), NOW(), 0),
('计科2021级2班', 'CS2021-2', '2021级', '计算机科学与技术', 5, 3, 2021, '2021-09-01', '2025-06-30', 'normal', 4, 30, 1, NOW(), NOW(), 0),
('软工2021级1班', 'SE2021-1', '2021级', '软件工程', 8, 2, 2021, '2021-09-01', '2025-06-30', 'normal', 4, 35, 1, NOW(), NOW(), 0),
('网工2021级1班', 'NE2021-1', '2021级', '网络工程', 6, 3, 2021, '2021-09-01', '2025-06-30', 'normal', 4, 25, 1, NOW(), NOW(), 0),
('数科2022级1班', 'DS2022-1', '2022级', '数据科学与大数据技术', 9, 2, 2022, '2022-09-01', '2026-06-30', 'normal', 4, 30, 1, NOW(), NOW(), 0);

-- ================================================
-- 8. 插入学生数据
-- ================================================
INSERT INTO tb_student (user_id, student_no, grade, major, class_id, enrollment_year, enrollment_date, academic_status, student_type, training_mode, academic_system, current_semester, parent_name, parent_phone, emergency_contact, emergency_phone, status, created_at, updated_at, deleted) VALUES
(4, '2021001001', '2021级', '计算机科学与技术', 1, 2021, '2021-09-01', 1, '本科生', '全日制', 4, '2024-2025-1', '王大明', '13900000001', '王大明', '13900000001', 1, NOW(), NOW(), 0),
(5, '2021001002', '2021级', '计算机科学与技术', 1, 2021, '2021-09-01', 1, '本科生', '全日制', 4, '2024-2025-1', '李大华', '13900000002', '李大华', '13900000002', 1, NOW(), NOW(), 0);

-- ================================================
-- 9. 插入课程数据
-- ================================================
INSERT INTO tb_course (course_name, course_code, credits, hours, theory_hours, lab_hours, department_id, teacher_id, course_type, course_nature, semester, academic_year, max_students, assessment_method, regular_score_ratio, midterm_score_ratio, final_score_ratio, status, created_at, updated_at, deleted) VALUES
('数据结构与算法', 'CS101', 4.0, 64, 48, 16, 5, 2, 'required', 'major_core', '2024-2025-1', 2024, 100, '考试', 0.30, 0.30, 0.40, 1, NOW(), NOW(), 0),
('计算机网络', 'CS102', 3.0, 48, 40, 8, 6, 3, 'required', 'major_core', '2024-2025-1', 2024, 80, '考试', 0.30, 0.30, 0.40, 1, NOW(), NOW(), 0),
('软件工程', 'SE101', 3.5, 56, 40, 16, 8, 2, 'required', 'major_core', '2024-2025-1', 2024, 60, '考查', 0.40, 0.20, 0.40, 1, NOW(), NOW(), 0),
('数据库系统', 'CS103', 3.0, 48, 32, 16, 5, 3, 'required', 'major_core', '2024-2025-1', 2024, 70, '考试', 0.30, 0.30, 0.40, 1, NOW(), NOW(), 0),
('机器学习基础', 'AI101', 3.0, 48, 40, 8, 10, 2, 'elective', 'major_elective', '2024-2025-1', 2024, 50, '考查', 0.50, 0.00, 0.50, 1, NOW(), NOW(), 0);

-- ================================================
-- 10. 插入缴费项目数据
-- ================================================
INSERT INTO tb_fee_item (item_name, item_code, amount, fee_type, applicable_grade, due_date, description, status, created_time, updated_time, deleted) VALUES
('学费', 'TUITION_2024', 5000.00, '学费', '2021级,2022级,2023级,2024级', '2024-09-30', '2024-2025学年学费', 1, NOW(), NOW(), 0),
('住宿费', 'DORMITORY_2024', 1200.00, '住宿费', '2021级,2022级,2023级,2024级', '2024-09-30', '2024-2025学年住宿费', 1, NOW(), NOW(), 0),
('教材费', 'TEXTBOOK_2024', 500.00, '教材费', '2021级,2022级,2023级,2024级', '2024-10-15', '2024-2025学年教材费', 1, NOW(), NOW(), 0),
('实验费', 'LAB_2024', 300.00, '实验费', '2021级,2022级,2023级,2024级', '2024-10-31', '2024-2025学年实验费', 1, NOW(), NOW(), 0);

-- ================================================
-- 11. 插入系统设置数据
-- ================================================
INSERT INTO tb_system_settings (setting_key, setting_value, setting_type, category, description, is_system, status, created_at, updated_at) VALUES
('system.name', '智慧校园管理系统', 'string', 'system', '系统名称', 1, 1, NOW(), NOW()),
('system.version', '1.0.0', 'string', 'system', '系统版本', 1, 1, NOW(), NOW()),
('system.company', 'Campus Management Team', 'string', 'system', '开发团队', 1, 1, NOW(), NOW()),
('course.max_selections_per_student', '10', 'int', 'course', '每个学生最多选课数', 0, 1, NOW(), NOW()),
('course.selection_enabled', 'true', 'boolean', 'course', '是否开启选课功能', 0, 1, NOW(), NOW()),
('grade.pass_score', '60', 'int', 'grade', '及格分数', 0, 1, NOW(), NOW()),
('grade.gpa_scale', '4.0', 'string', 'grade', 'GPA制度', 0, 1, NOW(), NOW()),
('payment.reminder_days', '7', 'int', 'payment', '缴费提醒天数', 0, 1, NOW(), NOW()),
('security.password_min_length', '6', 'int', 'security', '密码最小长度', 1, 1, NOW(), NOW()),
('security.login_max_attempts', '5', 'int', 'security', '最大登录尝试次数', 1, 1, NOW(), NOW());

-- 更新班级学生数量
UPDATE tb_class SET student_count = (
    SELECT COUNT(*) FROM tb_student WHERE class_id = tb_class.id AND deleted = 0
);

-- 显示插入完成信息
SELECT 'Initial data inserted successfully!' AS result;
SELECT '管理员账号: admin, 密码: admin123' AS admin_info;
SELECT '教师账号: teacher001, 密码: teacher123' AS teacher_info;
SELECT '学生账号: student001, 密码: student123' AS student_info;