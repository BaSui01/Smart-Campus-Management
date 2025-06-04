-- =====================================================
-- 智慧校园管理平台 - 初始化数据脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-03
-- 描述: 插入系统运行必需的基础数据
-- =====================================================

USE campus_management;

-- ===========================================
-- 基础角色数据
-- ===========================================

INSERT INTO tb_role (role_code, role_name, description) VALUES
('ADMIN', 'Admin', 'System administrator with all permissions'),
('TEACHER', 'Teacher', 'Teacher role for teaching management'),
('STUDENT', 'Student', 'Student role for learning activities'),
('PARENT', 'Parent', 'Parent role for monitoring student activities');

-- ===========================================
-- 系统配置数据
-- ===========================================

INSERT INTO tb_system_config (config_key, config_value, config_desc, config_type, is_system) VALUES
('system.name', 'Smart Campus Management', 'System name', 'STRING', 1),
('system.version', '1.0.0', 'System version', 'STRING', 1),
('system.logo', '/assets/logo.png', 'System logo', 'STRING', 1),
('system.copyright', '© 2024 Smart Campus Management', 'Copyright info', 'STRING', 1),
('system.icp', 'ICP No. 12345678', 'ICP registration number', 'STRING', 1),

-- 业务配置
('course.max_selection', '10', 'Maximum course selection per student', 'INT', 1),
('course.selection_start_time', '08:00', 'Course selection start time', 'STRING', 1),
('course.selection_end_time', '22:00', 'Course selection end time', 'STRING', 1),
('exam.auto_grading', 'true', 'Enable automatic grading', 'BOOLEAN', 1),
('exam.max_duration', '180', 'Maximum exam duration in minutes', 'INT', 1),
('exam.late_submit_penalty', '0.1', 'Late submission penalty ratio', 'DECIMAL', 1),

-- 邮件配置
('email.enabled', 'true', 'Email function enabled', 'BOOLEAN', 1),
('email.sender', 'noreply@campus.edu', 'System email sender address', 'STRING', 1),
('email.sender_name', 'Smart Campus Management', 'Email sender name', 'STRING', 1),
('email.smtp_host', 'smtp.campus.edu', 'SMTP server address', 'STRING', 1),
('email.smtp_port', '587', 'SMTP server port', 'INT', 1),

-- 通知配置
('notification.batch_size', '100', 'Batch notification processing size', 'INT', 1),
('notification.retry_times', '3', 'Notification sending retry times', 'INT', 1),
('notification.queue_enabled', 'true', 'Enable notification queue', 'BOOLEAN', 1),

-- 文件上传配置
('file.upload.max_size', '10485760', 'Maximum file upload size in bytes', 'INT', 1),
('file.upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'Allowed file types', 'STRING', 1),
('file.upload.path', '/uploads', 'File upload path', 'STRING', 1),

-- 安全配置
('security.password_min_length', '6', 'Minimum password length', 'INT', 1),
('security.password_complexity', 'true', 'Enable password complexity check', 'BOOLEAN', 1),
('security.login_max_attempts', '5', 'Maximum login attempts', 'INT', 1),
('security.session_timeout', '7200', 'Session timeout in seconds', 'INT', 1),
('security.captcha_enabled', 'true', 'Enable captcha verification', 'BOOLEAN', 1),

-- 缓存配置
('cache.enabled', 'true', 'Enable cache', 'BOOLEAN', 1),
('cache.default_timeout', '3600', 'Default cache timeout in seconds', 'INT', 1),
('cache.user_session_timeout', '1800', 'User session cache timeout in seconds', 'INT', 1);

-- ===========================================
-- 消息模板数据
-- ===========================================

INSERT INTO tb_message_template (template_code, template_name, template_type, message_type, title_template, content_template, variables, send_channels, description) VALUES
-- 成绩相关模板
('GRADE_PUBLISHED', 'Grade Published Notice', 'SYSTEM', 'NOTICE',
 'Grade Published for ${courseName}',
 'Dear ${studentName},\n\nYour grade for ${courseName} has been published:\nFinal Score: ${finalScore}\nGrade Level: ${gradeLevel}\n\nPlease login to view detailed information.\n\nBest regards,\nSmart Campus Management',
 '["studentName","courseName","finalScore","gradeLevel"]',
 '["SYSTEM","EMAIL"]',
 'Automatic notification when student grades are published'),

('GRADE_INQUIRY', 'Grade Inquiry Notice', 'SYSTEM', 'NOTICE',
 'Grade Inquiry Result',
 'Dear ${parentName},\n\nGrade information for student ${studentName} in ${semester}:\n${gradeDetails}\n\nIf you have any questions, please contact the class teacher.\n\nBest regards,\nSmart Campus Management',
 '["parentName","studentName","semester","gradeDetails"]',
 '["SYSTEM","EMAIL"]',
 'Notification sent when parents inquire about student grades'),

-- 考试相关模板
('EXAM_REMINDER', 'Exam Reminder', 'SYSTEM', 'REMINDER',
 'Exam Reminder: ${examName}',
 'Dear ${studentName},\n\nReminder for your upcoming exam:\nExam: ${examName}\nDate & Time: ${examDate} ${startTime}-${endTime}\nRoom: ${roomName}\nSeat: ${seatNumber}\n\nPlease arrive 30 minutes early and bring your student ID.\n\nGood luck!\n\nBest regards,\nSmart Campus Management',
 '["studentName","examName","examDate","startTime","endTime","roomName","seatNumber"]',
 '["SYSTEM","EMAIL"]',
 'Automatic exam reminder notification'),

('EXAM_RESULT', 'Exam Result Notice', 'SYSTEM', 'NOTICE',
 'Exam Result: ${examName}',
 'Dear ${studentName},\n\nYour exam result for ${examName}:\nScore: ${examScore}\nStatus: ${examStatus}\n\nPlease login for detailed information.\n\nBest regards,\nSmart Campus Management',
 '["studentName","examName","examScore","examStatus"]',
 '["SYSTEM","EMAIL"]',
 'Notification when exam results are published'),

-- 缴费相关模板
('PAYMENT_DUE', 'Payment Due Reminder', 'SYSTEM', 'REMINDER',
 'Payment Due: ${itemName}',
 'Dear Parent of ${studentName},\n\nPayment reminder:\nItem: ${itemName}\nAmount: ${amount} CNY\nDue Date: ${dueDate}\n\nPlease complete payment on time.\n\nPayment Methods:\n1. Online payment through system\n2. On-site payment at finance office\n\nBest regards,\nSmart Campus Management',
 '["studentName","itemName","amount","dueDate"]',
 '["SYSTEM","EMAIL"]',
 'Automatic payment due reminder'),

('PAYMENT_SUCCESS', 'Payment Success Notice', 'SYSTEM', 'NOTICE',
 'Payment Successful: ${itemName}',
 'Dear Parent of ${studentName},\n\nPayment successful:\nItem: ${itemName}\nAmount: ${amount} CNY\nTime: ${paymentTime}\nTransaction No: ${transactionNo}\n\nPlease keep this receipt.\n\nBest regards,\nSmart Campus Management',
 '["studentName","itemName","amount","paymentTime","transactionNo"]',
 '["SYSTEM","EMAIL"]',
 'Payment success confirmation'),

-- 选课相关模板
('COURSE_SELECTION', 'Course Selection Notice', 'SYSTEM', 'NOTICE',
 'Course Selection Started',
 'Dear ${studentName},\n\nCourse selection has started:\nStart: ${startTime}\nEnd: ${endTime}\nMax Courses: ${maxCourses}\n\nPlease complete your selection on time.\n\nBest regards,\nSmart Campus Management',
 '["studentName","startTime","endTime","maxCourses"]',
 '["SYSTEM","EMAIL"]',
 'Course selection start notification'),

('COURSE_DROPPED', 'Course Drop Notice', 'SYSTEM', 'NOTICE',
 'Course Drop Confirmation',
 'Dear ${studentName},\n\nYou have successfully dropped:\nCourse: ${courseName}\nCode: ${courseCode}\nTime: ${dropTime}\n\nYou may re-select during the selection period.\n\nBest regards,\nSmart Campus Management',
 '["studentName","courseName","courseCode","dropTime"]',
 '["SYSTEM","EMAIL"]',
 'Course drop confirmation'),

-- 用户相关模板
('WELCOME_STUDENT', 'Welcome Student', 'SYSTEM', 'NOTICE',
 'Welcome to Smart Campus Management',
 'Dear ${studentName},\n\nWelcome to Smart Campus Management!\n\nYour account:\nStudent No: ${studentNo}\nClass: ${className}\nUsername: ${username}\n\nFeatures:\n1. Course Selection\n2. Grade Inquiry\n3. Exam Management\n4. Payment Management\n5. Notifications\n\nBest regards,\nSmart Campus Management',
 '["studentName","studentNo","className","username"]',
 '["SYSTEM","EMAIL"]',
 'Welcome message for new students'),

('WELCOME_TEACHER', 'Welcome Teacher', 'SYSTEM', 'NOTICE',
 'Welcome to Smart Campus Management',
 'Dear ${teacherName},\n\nWelcome to Smart Campus Management!\n\nYour account:\nTeacher No: ${teacherNo}\nDepartment: ${department}\nUsername: ${username}\n\nFeatures:\n1. Course Management\n2. Grade Management\n3. Exam Management\n4. Student Management\n5. Notifications\n\nBest regards,\nSmart Campus Management',
 '["teacherName","teacherNo","department","username"]',
 '["SYSTEM","EMAIL"]',
 'Welcome message for new teachers'),

('PASSWORD_RESET', 'Password Reset Notice', 'SYSTEM', 'NOTICE',
 'Password Reset Successful',
 'Dear ${realName},\n\nYour password has been reset:\nUsername: ${username}\nReset Time: ${resetTime}\n\nFor security:\n1. Login and change password immediately\n2. Use strong password\n3. Change password regularly\n\nIf this was not you, contact admin immediately.\n\nBest regards,\nSmart Campus Management',
 '["realName","username","resetTime"]',
 '["SYSTEM","EMAIL"]',
 'Password reset notification'),

-- 系统相关模板
('SYSTEM_MAINTENANCE', 'System Maintenance Notice', 'SYSTEM', 'WARNING',
 'System Maintenance Notice',
 'Dear Users,\n\nSystem maintenance scheduled:\nTime: ${maintenanceTime}\nDuration: ${duration}\nContent: ${content}\n\nService will be temporarily unavailable. We apologize for any inconvenience.\n\nBest regards,\nSmart Campus Management',
 '["maintenanceTime","duration","content"]',
 '["SYSTEM","EMAIL"]',
 'System maintenance notification'),

('SYSTEM_UPGRADE', 'System Upgrade Notice', 'SYSTEM', 'NOTICE',
 'System Upgrade Complete',
 'Dear Users,\n\nSystem upgrade completed:\nVersion: ${version}\nNew Features: ${features}\nImprovements: ${improvements}\n\nThank you for your patience. Welcome to try new features!\n\nBest regards,\nSmart Campus Management',
 '["version","features","improvements"]',
 '["SYSTEM","EMAIL"]',
 'System upgrade completion notification');

-- ===========================================
-- 默认管理员账户
-- ===========================================

-- 创建默认管理员用户（密码: admin123，已加密）
INSERT INTO tb_user (username, password, email, real_name, phone, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'admin@campus.edu', 'System Administrator', '13800000000', 1);

-- 绑定管理员角色
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);

-- ===========================================
-- 基础缴费项目数据
-- ===========================================

INSERT INTO tb_fee_item (item_name, item_code, amount, fee_type, applicable_grade, due_date, description, status) VALUES
('Tuition Fee', 'TUITION_2024_SPRING', 5000.00, 'Tuition', 'All', '2024-03-01', '2024 Spring Semester Tuition', 1),
('Accommodation Fee', 'ACCOMMODATION_2024_SPRING', 1200.00, 'Accommodation', 'All', '2024-03-01', '2024 Spring Semester Accommodation', 1),
('Textbook Fee', 'TEXTBOOK_2024_SPRING', 300.00, 'Books', 'All', '2024-03-15', '2024 Spring Semester Textbooks', 1),
('Medical Check Fee', 'MEDICAL_CHECK_2024', 50.00, 'Misc', 'Grade 1', '2024-09-30', 'Freshman Medical Check', 1),
('Insurance Fee', 'INSURANCE_2024', 100.00, 'Misc', 'All', '2024-09-30', 'Student Insurance', 1);

-- ===========================================
-- 验证数据插入结果
-- ===========================================

SELECT 'Basic data initialization completed' AS message;
SELECT 'tb_role' AS table_name, COUNT(*) AS record_count FROM tb_role
UNION ALL
SELECT 'tb_system_config', COUNT(*) FROM tb_system_config
UNION ALL
SELECT 'tb_message_template', COUNT(*) FROM tb_message_template
UNION ALL
SELECT 'tb_user', COUNT(*) FROM tb_user
UNION ALL
SELECT 'tb_user_role', COUNT(*) FROM tb_user_role
UNION ALL
SELECT 'tb_fee_item', COUNT(*) FROM tb_fee_item;
