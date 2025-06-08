    -- =====================================================
    -- Smart Campus Management System - 基础数据插入脚本
    -- =====================================================
    -- 文件名: 03_insert_basic_data.sql
    -- 描述: 插入系统基础数据（权限、角色、系统配置等）
    -- 版本: 1.0.0
    -- 创建时间: 2025-06-08
    -- 兼容性: MySQL 8.0+
    -- =====================================================

    USE campus_management_db;

    -- 设置字符集
    SET NAMES utf8mb4;

    -- 开始事务
    START TRANSACTION;

    -- =====================================================
    -- 1. 权限数据
    -- =====================================================

    -- 插入系统权限
    INSERT INTO tb_permission (permission_name, permission_key, permission_type, parent_id, path, component, icon, sort_order, is_system, description) VALUES
    -- 系统管理
    ('系统管理', 'SYSTEM', 'MENU', NULL, '/system', NULL, 'system', 1, 1, '系统管理模块'),
    ('用户管理', 'SYSTEM:USER', 'MENU', 1, '/system/user', 'system/user/index', 'user', 1, 1, '用户管理'),
    ('用户查询', 'SYSTEM:USER:LIST', 'BUTTON', 2, NULL, NULL, NULL, 1, 1, '用户查询权限'),
    ('用户新增', 'SYSTEM:USER:ADD', 'BUTTON', 2, NULL, NULL, NULL, 2, 1, '用户新增权限'),
    ('用户修改', 'SYSTEM:USER:EDIT', 'BUTTON', 2, NULL, NULL, NULL, 3, 1, '用户修改权限'),
    ('用户删除', 'SYSTEM:USER:DELETE', 'BUTTON', 2, NULL, NULL, NULL, 4, 1, '用户删除权限'),
    ('用户导出', 'SYSTEM:USER:EXPORT', 'BUTTON', 2, NULL, NULL, NULL, 5, 1, '用户导出权限'),

    -- 角色管理
    ('角色管理', 'SYSTEM:ROLE', 'MENU', 1, '/system/role', 'system/role/index', 'role', 2, 1, '角色管理'),
    ('角色查询', 'SYSTEM:ROLE:LIST', 'BUTTON', 8, NULL, NULL, NULL, 1, 1, '角色查询权限'),
    ('角色新增', 'SYSTEM:ROLE:ADD', 'BUTTON', 8, NULL, NULL, NULL, 2, 1, '角色新增权限'),
    ('角色修改', 'SYSTEM:ROLE:EDIT', 'BUTTON', 8, NULL, NULL, NULL, 3, 1, '角色修改权限'),
    ('角色删除', 'SYSTEM:ROLE:DELETE', 'BUTTON', 8, NULL, NULL, NULL, 4, 1, '角色删除权限'),
    ('权限分配', 'SYSTEM:ROLE:PERMISSION', 'BUTTON', 8, NULL, NULL, NULL, 5, 1, '权限分配'),

    -- 权限管理
    ('权限管理', 'SYSTEM:PERMISSION', 'MENU', 1, '/system/permission', 'system/permission/index', 'permission', 3, 1, '权限管理'),
    ('权限查询', 'SYSTEM:PERMISSION:LIST', 'BUTTON', 13, NULL, NULL, NULL, 1, 1, '权限查询'),
    ('权限新增', 'SYSTEM:PERMISSION:ADD', 'BUTTON', 13, NULL, NULL, NULL, 2, 1, '权限新增'),
    ('权限修改', 'SYSTEM:PERMISSION:EDIT', 'BUTTON', 13, NULL, NULL, NULL, 3, 1, '权限修改'),
    ('权限删除', 'SYSTEM:PERMISSION:DELETE', 'BUTTON', 13, NULL, NULL, NULL, 4, 1, '权限删除'),

    -- 学生管理
    ('学生管理', 'STUDENT', 'MENU', NULL, '/student', NULL, 'student', 2, 1, '学生管理模块'),
    ('学生信息', 'STUDENT:INFO', 'MENU', 18, '/student/info', 'student/info/index', 'info', 1, 1, '学生信息管理'),
    ('学生查询', 'STUDENT:INFO:LIST', 'BUTTON', 19, NULL, NULL, NULL, 1, 1, '学生查询'),
    ('学生新增', 'STUDENT:INFO:ADD', 'BUTTON', 19, NULL, NULL, NULL, 2, 1, '学生新增'),
    ('学生修改', 'STUDENT:INFO:EDIT', 'BUTTON', 19, NULL, NULL, NULL, 3, 1, '学生修改'),
    ('学生删除', 'STUDENT:INFO:DELETE', 'BUTTON', 19, NULL, NULL, NULL, 4, 1, '学生删除'),
    ('学生导出', 'STUDENT:INFO:EXPORT', 'BUTTON', 19, NULL, NULL, NULL, 5, 1, '学生导出'),

    -- 课程管理
    ('课程管理', 'COURSE', 'MENU', NULL, '/course', NULL, 'course', 3, 1, '课程管理模块'),
    ('课程信息', 'COURSE:INFO', 'MENU', 24, '/course/info', 'course/info/index', 'info', 1, 1, '课程信息管理'),
    ('课程查询', 'COURSE:INFO:LIST', 'BUTTON', 25, NULL, NULL, NULL, 1, 1, '课程查询'),
    ('课程新增', 'COURSE:INFO:ADD', 'BUTTON', 25, NULL, NULL, NULL, 2, 1, '课程新增'),
    ('课程修改', 'COURSE:INFO:EDIT', 'BUTTON', 25, NULL, NULL, NULL, 3, 1, '课程修改'),
    ('课程删除', 'COURSE:INFO:DELETE', 'BUTTON', 25, NULL, NULL, NULL, 4, 1, '课程删除'),

    -- 选课管理
    ('选课管理', 'COURSE:SELECTION', 'MENU', 24, '/course/selection', 'course/selection/index', 'selection', 2, 1, '选课管理'),
    ('选课查询', 'COURSE:SELECTION:LIST', 'BUTTON', 30, NULL, NULL, NULL, 1, 1, '选课查询'),
    ('选课审核', 'COURSE:SELECTION:AUDIT', 'BUTTON', 30, NULL, NULL, NULL, 2, 1, '选课审核'),

    -- 成绩管理
    ('成绩管理', 'GRADE', 'MENU', NULL, '/grade', NULL, 'grade', 4, 1, '成绩管理模块'),
    ('成绩录入', 'GRADE:INPUT', 'MENU', 33, '/grade/input', 'grade/input/index', 'input', 1, 1, '成绩录入'),
    ('成绩查询', 'GRADE:QUERY', 'MENU', 33, '/grade/query', 'grade/query/index', 'query', 2, 1, '成绩查询'),
    ('成绩统计', 'GRADE:STATISTICS', 'MENU', 33, '/grade/statistics', 'grade/statistics/index', 'statistics', 3, 1, '成绩统计'),

    -- 财务管理
    ('财务管理', 'FINANCE', 'MENU', NULL, '/finance', NULL, 'finance', 5, 1, '财务管理模块'),
    ('缴费项目', 'FINANCE:FEE_ITEM', 'MENU', 37, '/finance/fee-item', 'finance/fee-item/index', 'fee-item', 1, 1, '缴费项目管理'),
    ('缴费记录', 'FINANCE:PAYMENT', 'MENU', 37, '/finance/payment', 'finance/payment/index', 'payment', 2, 1, '缴费记录管理'),
    ('财务统计', 'FINANCE:STATISTICS', 'MENU', 37, '/finance/statistics', 'finance/statistics/index', 'statistics', 3, 1, '财务统计'),
    ('缴费项目查询', 'FINANCE:FEE_ITEM:LIST', 'BUTTON', 38, NULL, NULL, NULL, 1, 1, '缴费项目查询'),
    ('缴费项目新增', 'FINANCE:FEE_ITEM:ADD', 'BUTTON', 38, NULL, NULL, NULL, 2, 1, '缴费项目新增'),
    ('缴费项目修改', 'FINANCE:FEE_ITEM:EDIT', 'BUTTON', 38, NULL, NULL, NULL, 3, 1, '缴费项目修改'),
    ('缴费项目删除', 'FINANCE:FEE_ITEM:DELETE', 'BUTTON', 38, NULL, NULL, NULL, 4, 1, '缴费项目删除'),
    ('缴费记录查询', 'FINANCE:PAYMENT:LIST', 'BUTTON', 39, NULL, NULL, NULL, 1, 1, '缴费记录查询'),
    ('缴费记录导出', 'FINANCE:PAYMENT:EXPORT', 'BUTTON', 39, NULL, NULL, NULL, 2, 1, '缴费记录导出'),

    -- 教学管理
    ('教学管理', 'TEACHING', 'MENU', NULL, '/teaching', NULL, 'teaching', 6, 1, '教学管理模块'),
    ('课表管理', 'TEACHING:SCHEDULE', 'MENU', 47, '/teaching/schedule', 'teaching/schedule/index', 'schedule', 1, 1, '课表管理'),
    ('考试管理', 'TEACHING:EXAM', 'MENU', 47, '/teaching/exam', 'teaching/exam/index', 'exam', 2, 1, '考试管理'),
    ('作业管理', 'TEACHING:ASSIGNMENT', 'MENU', 47, '/teaching/assignment', 'teaching/assignment/index', 'assignment', 3, 1, '作业管理'),
    ('出勤管理', 'TEACHING:ATTENDANCE', 'MENU', 47, '/teaching/attendance', 'teaching/attendance/index', 'attendance', 4, 1, '出勤管理'),

    -- 通知管理
    ('通知管理', 'NOTIFICATION', 'MENU', NULL, '/notification', NULL, 'notification', 7, 1, '通知管理模块'),
    ('通知发布', 'NOTIFICATION:PUBLISH', 'MENU', 52, '/notification/publish', 'notification/publish/index', 'publish', 1, 1, '通知发布'),
    ('通知查询', 'NOTIFICATION:LIST', 'BUTTON', 52, NULL, NULL, NULL, 1, 1, '通知查询'),
    ('通知新增', 'NOTIFICATION:ADD', 'BUTTON', 52, NULL, NULL, NULL, 2, 1, '通知新增'),
    ('通知修改', 'NOTIFICATION:EDIT', 'BUTTON', 52, NULL, NULL, NULL, 3, 1, '通知修改'),
    ('通知删除', 'NOTIFICATION:DELETE', 'BUTTON', 52, NULL, NULL, NULL, 4, 1, '通知删除'),

    -- 系统监控
    ('系统监控', 'MONITOR', 'MENU', NULL, '/monitor', NULL, 'monitor', 8, 1, '系统监控模块'),
    ('在线用户', 'MONITOR:ONLINE', 'MENU', 57, '/monitor/online', 'monitor/online/index', 'online', 1, 1, '在线用户监控'),
    ('系统日志', 'MONITOR:LOG', 'MENU', 57, '/monitor/log', 'monitor/log/index', 'log', 2, 1, '系统日志'),
    ('性能监控', 'MONITOR:PERFORMANCE', 'MENU', 57, '/monitor/performance', 'monitor/performance/index', 'performance', 3, 1, '性能监控'),

    -- API权限
    ('API接口', 'API', 'MENU', NULL, '/api', NULL, 'api', 9, 1, 'API接口权限'),
    ('用户API', 'API:USER', 'API', 61, '/api/user/**', NULL, NULL, 1, 1, '用户相关API'),
    ('学生API', 'API:STUDENT', 'API', 61, '/api/student/**', NULL, NULL, 2, 1, '学生相关API'),
    ('课程API', 'API:COURSE', 'API', 61, '/api/course/**', NULL, NULL, 3, 1, '课程相关API'),
    ('成绩API', 'API:GRADE', 'API', 61, '/api/grade/**', NULL, NULL, 4, 1, '成绩相关API'),
    ('财务API', 'API:FINANCE', 'API', 61, '/api/finance/**', NULL, NULL, 5, 1, '财务相关API');

    -- =====================================================
    -- 2. 角色数据
    -- =====================================================

    -- 插入系统角色
    INSERT INTO tb_role (role_name, role_key, description, sort_order, is_system, role_level) VALUES
    ('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1, 1),
    ('系统管理员', 'ADMIN', '系统管理员，负责系统管理和维护', 2, 1, 2),
    ('教务管理员', 'ACADEMIC_ADMIN', '教务管理员，负责教学管理', 3, 1, 3),
    ('财务管理员', 'FINANCE_ADMIN', '财务管理员，负责财务管理', 4, 1, 3),
    ('院系管理员', 'DEPT_ADMIN', '院系管理员，负责院系事务管理', 5, 1, 3),
    ('教师', 'TEACHER', '教师角色，负责教学工作', 6, 1, 4),
    ('班主任', 'HEAD_TEACHER', '班主任，负责班级管理', 7, 1, 4),
    ('辅导员', 'COUNSELOR', '辅导员，负责学生思想教育', 8, 1, 4),
    ('实验员', 'LAB_ASSISTANT', '实验员，负责实验室管理', 9, 1, 5),
    ('学生', 'STUDENT', '学生角色，进行学习活动', 10, 1, 6),
    ('家长', 'PARENT', '家长角色，查看学生信息', 11, 1, 7),
    ('访客', 'GUEST', '访客角色，仅有基本查看权限', 12, 1, 8);

    -- =====================================================
    -- 3. 超级管理员用户
    -- =====================================================

    -- 插入超级管理员用户（密码：123456，已加密）
    INSERT INTO tb_user (username, password, email, real_name, phone, gender, account_non_expired, account_non_locked, credentials_non_expired) VALUES
    ('admin', '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKH0E2cxHxhXktHtmwqpxYfFOxTq', 'admin@campus.edu.cn', '系统管理员', '13800138000', '男', 1, 1, 1),
    ('system', '$2a$10$7JB720yubVSOfvVWdBYoOe.PuiKH0E2cxHxhXktHtmwqpxYfFOxTq', 'system@campus.edu.cn', '系统用户', '13800138001', '男', 1, 1, 1);

    -- =====================================================
    -- 4. 用户角色关联
    -- =====================================================

    -- 为超级管理员分配角色
    INSERT INTO tb_user_role (user_id, role_id, assigned_by, assigned_time) VALUES
    (1, 1, 1, NOW()),  -- admin用户分配超级管理员角色
    (2, 2, 1, NOW());  -- system用户分配系统管理员角色

    -- =====================================================
    -- 5. 角色权限关联
    -- =====================================================

    -- 为超级管理员分配所有权限
    INSERT INTO tb_role_permission (role_id, permission_id)
    SELECT 1, id FROM tb_permission WHERE is_system = 1;

    -- 为系统管理员分配系统管理权限
    INSERT INTO tb_role_permission (role_id, permission_id)
    SELECT 2, id FROM tb_permission WHERE permission_key LIKE 'SYSTEM%';

    -- 为教务管理员分配教学相关权限
    INSERT INTO tb_role_permission (role_id, permission_id)
    SELECT 3, id FROM tb_permission WHERE permission_key LIKE 'STUDENT%' OR permission_key LIKE 'COURSE%' OR permission_key LIKE 'GRADE%';

    -- 为财务管理员分配财务权限
    INSERT INTO tb_role_permission (role_id, permission_id)
    SELECT 4, id FROM tb_permission WHERE permission_key LIKE 'FINANCE%';

    -- =====================================================
    -- 6. 系统配置数据
    -- =====================================================

    -- 插入系统配置
    INSERT INTO tb_system_config (config_key, config_value, config_name, config_type, config_group, is_system, description) VALUES
    -- 系统基础配置
    ('system.name', '智慧校园管理系统', '系统名称', 'STRING', 'SYSTEM', 1, '系统显示名称'),
    ('system.version', '1.0.0', '系统版本', 'STRING', 'SYSTEM', 1, '当前系统版本号'),
    ('system.copyright', '© 2025 Campus Management Team', '版权信息', 'STRING', 'SYSTEM', 1, '系统版权信息'),
    ('system.logo', '/static/images/logo.png', '系统Logo', 'STRING', 'SYSTEM', 1, '系统Logo路径'),
    ('system.favicon', '/static/images/favicon.ico', '网站图标', 'STRING', 'SYSTEM', 1, '网站Favicon路径'),

    -- 安全配置
    ('security.password.min_length', '6', '密码最小长度', 'INTEGER', 'SECURITY', 1, '用户密码最小长度'),
    ('security.password.max_length', '20', '密码最大长度', 'INTEGER', 'SECURITY', 1, '用户密码最大长度'),
    ('security.login.max_attempts', '5', '最大登录尝试次数', 'INTEGER', 'SECURITY', 1, '用户最大登录失败次数'),
    ('security.login.lock_time', '30', '账户锁定时间(分钟)', 'INTEGER', 'SECURITY', 1, '账户锁定时间'),
    ('security.session.timeout', '30', '会话超时时间(分钟)', 'INTEGER', 'SECURITY', 1, '用户会话超时时间'),

    -- 文件上传配置
    ('upload.max_file_size', '10485760', '最大文件大小(字节)', 'LONG', 'UPLOAD', 1, '单个文件最大上传大小'),
    ('upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许的文件类型', 'STRING', 'UPLOAD', 1, '允许上传的文件扩展名'),
    ('upload.path', '/uploads', '上传路径', 'STRING', 'UPLOAD', 1, '文件上传保存路径'),

    -- 邮件配置
    ('email.smtp.host', 'smtp.qq.com', 'SMTP服务器', 'STRING', 'EMAIL', 1, '邮件SMTP服务器地址'),
    ('email.smtp.port', '587', 'SMTP端口', 'INTEGER', 'EMAIL', 1, '邮件SMTP服务器端口'),
    ('email.smtp.username', '', 'SMTP用户名', 'STRING', 'EMAIL', 1, '邮件发送账户用户名'),
    ('email.smtp.password', '', 'SMTP密码', 'STRING', 'EMAIL', 1, '邮件发送账户密码'),
    ('email.from.name', '智慧校园管理系统', '发件人名称', 'STRING', 'EMAIL', 1, '邮件发件人显示名称'),

    -- 学期配置
    ('academic.current_year', '2024', '当前学年', 'INTEGER', 'ACADEMIC', 1, '当前学年'),
    ('academic.current_semester', '2', '当前学期', 'INTEGER', 'ACADEMIC', 1, '当前学期(1春季,2秋季)'),
    ('academic.semester_start_date', '2024-09-01', '学期开始日期', 'DATE', 'ACADEMIC', 1, '当前学期开始日期'),
    ('academic.semester_end_date', '2025-06-07', '学期结束日期', 'DATE', 'ACADEMIC', 1, '当前学期结束日期'),

    -- 选课配置
    ('course.selection.max_courses', '8', '最大选课数量', 'INTEGER', 'COURSE', 1, '学生每学期最大选课数量'),
    ('course.selection.min_credits', '12', '最小学分要求', 'INTEGER', 'COURSE', 1, '学生每学期最小学分要求'),
    ('course.selection.max_credits', '25', '最大学分限制', 'INTEGER', 'COURSE', 1, '学生每学期最大学分限制'),

    -- 成绩配置
    ('grade.pass_score', '60', '及格分数', 'INTEGER', 'GRADE', 1, '课程及格分数线'),
    ('grade.excellent_score', '90', '优秀分数', 'INTEGER', 'GRADE', 1, '课程优秀分数线'),
    ('grade.gpa.scale', '4.0', 'GPA满分', 'DECIMAL', 'GRADE', 1, 'GPA计算满分值'),

    -- 缴费配置
    ('payment.late_fee_rate', '0.05', '逾期滞纳金比例', 'DECIMAL', 'PAYMENT', 1, '缴费逾期滞纳金比例'),
    ('payment.installment.max_count', '6', '最大分期数', 'INTEGER', 'PAYMENT', 1, '缴费最大分期数量'),
    ('payment.methods', 'CASH,CARD,ALIPAY,WECHAT,ONLINE', '支持的缴费方式', 'STRING', 'PAYMENT', 1, '系统支持的缴费方式'),
    ('payment.auto_reminder_days', '7', '自动提醒天数', 'INTEGER', 'PAYMENT', 1, '缴费到期前自动提醒天数'),
    ('payment.receipt_template', 'default', '收据模板', 'STRING', 'PAYMENT', 1, '缴费收据模板'),

    -- 通知配置
    ('notification.email.enabled', 'true', '邮件通知启用', 'BOOLEAN', 'NOTIFICATION', 1, '是否启用邮件通知'),
    ('notification.sms.enabled', 'false', '短信通知启用', 'BOOLEAN', 'NOTIFICATION', 1, '是否启用短信通知'),
    ('notification.push.enabled', 'true', '推送通知启用', 'BOOLEAN', 'NOTIFICATION', 1, '是否启用推送通知'),
    ('notification.batch_size', '100', '批量发送数量', 'INTEGER', 'NOTIFICATION', 1, '批量发送通知的数量'),
    ('notification.retry_times', '3', '重试次数', 'INTEGER', 'NOTIFICATION', 1, '通知发送失败重试次数'),

    -- 考试配置
    ('exam.auto_submit_time', '5', '自动提交时间(分钟)', 'INTEGER', 'EXAM', 1, '考试结束后自动提交时间'),
    ('exam.allow_review', 'true', '允许查看试卷', 'BOOLEAN', 'EXAM', 1, '考试结束后是否允许查看试卷'),
    ('exam.screenshot_detection', 'true', '截图检测', 'BOOLEAN', 'EXAM', 1, '是否启用截图检测'),
    ('exam.tab_switch_detection', 'true', '切换标签检测', 'BOOLEAN', 'EXAM', 1, '是否检测切换浏览器标签'),
    ('exam.face_recognition', 'false', '人脸识别', 'BOOLEAN', 'EXAM', 1, '是否启用人脸识别验证'),

    -- 选课配置
    ('course.selection.conflict_check', 'true', '时间冲突检查', 'BOOLEAN', 'COURSE', 1, '选课时是否检查时间冲突'),
    ('course.selection.prerequisite_check', 'true', '先修课程检查', 'BOOLEAN', 'COURSE', 1, '是否检查先修课程'),
    ('course.selection.auto_waitlist', 'true', '自动候补', 'BOOLEAN', 'COURSE', 1, '课程满员时是否自动加入候补'),
    ('course.selection.waitlist_size', '20', '候补队列大小', 'INTEGER', 'COURSE', 1, '每门课程的候补队列大小'),

    -- 数据备份配置
    ('backup.auto_enabled', 'true', '自动备份启用', 'BOOLEAN', 'BACKUP', 1, '是否启用自动数据备份'),
    ('backup.schedule', '0 2 * * *', '备份计划', 'STRING', 'BACKUP', 1, '自动备份的Cron表达式'),
    ('backup.retention_days', '30', '备份保留天数', 'INTEGER', 'BACKUP', 1, '备份文件保留天数'),
    ('backup.compress', 'true', '备份压缩', 'BOOLEAN', 'BACKUP', 1, '是否压缩备份文件'),

    -- 日志配置
    ('log.level', 'INFO', '日志级别', 'STRING', 'LOG', 1, '系统日志记录级别'),
    ('log.retention_days', '90', '日志保留天数', 'INTEGER', 'LOG', 1, '系统日志保留天数'),
    ('log.max_file_size', '100MB', '日志文件最大大小', 'STRING', 'LOG', 1, '单个日志文件最大大小'),
    ('log.audit_enabled', 'true', '审计日志启用', 'BOOLEAN', 'LOG', 1, '是否启用审计日志'),

    -- 缓存配置
    ('cache.redis.enabled', 'true', 'Redis缓存启用', 'BOOLEAN', 'CACHE', 1, '是否启用Redis缓存'),
    ('cache.default_ttl', '3600', '默认缓存时间(秒)', 'INTEGER', 'CACHE', 1, '默认缓存过期时间'),
    ('cache.user_session_ttl', '1800', '用户会话缓存时间(秒)', 'INTEGER', 'CACHE', 1, '用户会话缓存过期时间'),
    ('cache.course_info_ttl', '7200', '课程信息缓存时间(秒)', 'INTEGER', 'CACHE', 1, '课程信息缓存过期时间');

    -- =====================================================
    -- 7. 通知模板数据
    -- =====================================================

    -- 插入通知模板
    INSERT INTO tb_notification_template (template_name, template_code, template_type, title_template, content_template, variables, is_system, description) VALUES
    ('用户注册通知', 'USER_REGISTER', 'SYSTEM', '欢迎加入智慧校园管理系统', '亲爱的${realName}，欢迎您加入智慧校园管理系统！您的用户名是：${username}，请妥善保管您的账户信息。', 'realName:真实姓名,username:用户名', 1, '用户注册成功通知模板'),
    ('密码重置通知', 'PASSWORD_RESET', 'SYSTEM', '密码重置通知', '您好，${realName}！您的密码重置请求已处理，新密码为：${newPassword}，请及时登录系统修改密码。', 'realName:真实姓名,newPassword:新密码', 1, '密码重置通知模板'),
    ('选课成功通知', 'COURSE_SELECTION_SUCCESS', 'ACADEMIC', '选课成功通知', '恭喜您成功选择课程：${courseName}（${courseCode}），授课教师：${teacherName}，上课时间：${scheduleTime}。', 'courseName:课程名称,courseCode:课程编码,teacherName:教师姓名,scheduleTime:上课时间', 1, '选课成功通知模板'),
    ('选课失败通知', 'COURSE_SELECTION_FAILED', 'ACADEMIC', '选课失败通知', '很抱歉，您选择的课程：${courseName}（${courseCode}）选课失败，原因：${reason}。请重新选择其他课程。', 'courseName:课程名称,courseCode:课程编码,reason:失败原因', 1, '选课失败通知模板'),
    ('退课成功通知', 'COURSE_DROP_SUCCESS', 'ACADEMIC', '退课成功通知', '您已成功退选课程：${courseName}（${courseCode}），如有疑问请联系教务处。', 'courseName:课程名称,courseCode:课程编码', 1, '退课成功通知模板'),
    ('成绩发布通知', 'GRADE_PUBLISHED', 'ACADEMIC', '成绩发布通知', '您好，${studentName}！您的${courseName}课程成绩已发布，总成绩：${totalScore}分，等级：${letterGrade}。', 'studentName:学生姓名,courseName:课程名称,totalScore:总成绩,letterGrade:等级成绩', 1, '成绩发布通知模板'),
    ('考试安排通知', 'EXAM_SCHEDULE', 'ACADEMIC', '考试安排通知', '您好，${studentName}！${courseName}课程考试安排如下：考试时间：${examTime}，考试地点：${examLocation}，请准时参加。', 'studentName:学生姓名,courseName:课程名称,examTime:考试时间,examLocation:考试地点', 1, '考试安排通知模板'),
    ('作业发布通知', 'ASSIGNMENT_PUBLISHED', 'ACADEMIC', '作业发布通知', '${courseName}课程有新作业发布：${assignmentTitle}，截止时间：${deadline}，请及时完成并提交。', 'courseName:课程名称,assignmentTitle:作业标题,deadline:截止时间', 1, '作业发布通知模板'),
    ('作业提醒通知', 'ASSIGNMENT_REMINDER', 'ACADEMIC', '作业提醒', '提醒：${courseName}课程作业"${assignmentTitle}"即将到期，截止时间：${deadline}，请尽快提交。', 'courseName:课程名称,assignmentTitle:作业标题,deadline:截止时间', 1, '作业提醒通知模板'),
    ('缴费提醒通知', 'PAYMENT_REMINDER', 'FINANCE', '缴费提醒', '亲爱的${studentName}，您有一项缴费项目"${feeItemName}"即将到期，金额：${amount}元，截止日期：${dueDate}，请及时缴费。', 'studentName:学生姓名,feeItemName:缴费项目,amount:金额,dueDate:截止日期', 1, '缴费提醒通知模板'),
    ('缴费成功通知', 'PAYMENT_SUCCESS', 'FINANCE', '缴费成功通知', '您好，${studentName}！您的"${feeItemName}"缴费已成功，金额：${amount}元，交易流水号：${transactionNo}。', 'studentName:学生姓名,feeItemName:缴费项目,amount:金额,transactionNo:交易流水号', 1, '缴费成功通知模板'),
    ('逾期缴费通知', 'PAYMENT_OVERDUE', 'FINANCE', '逾期缴费通知', '${studentName}同学，您的"${feeItemName}"已逾期未缴费，将产生滞纳金，请尽快处理。', 'studentName:学生姓名,feeItemName:缴费项目', 1, '逾期缴费通知模板'),
    ('学期开始通知', 'SEMESTER_START', 'ACADEMIC', '新学期开始通知', '亲爱的同学们，新学期即将开始！开学时间：${startDate}，请做好开学准备。', 'startDate:开学时间', 1, '学期开始通知模板'),
    ('放假通知', 'HOLIDAY_NOTICE', 'ACADEMIC', '放假通知', '根据学校安排，${holidayName}放假时间为：${startDate}至${endDate}，请合理安排时间。', 'holidayName:假期名称,startDate:开始时间,endDate:结束时间', 1, '放假通知模板'),
    ('系统维护通知', 'SYSTEM_MAINTENANCE', 'SYSTEM', '系统维护通知', '系统将于${maintenanceTime}进行维护，预计维护时间：${duration}，维护期间系统暂停服务，请谅解。', 'maintenanceTime:维护时间,duration:维护时长', 1, '系统维护通知模板'),
    ('账户异常通知', 'ACCOUNT_ABNORMAL', 'SECURITY', '账户异常通知', '检测到您的账户存在异常登录行为，登录时间：${loginTime}，登录IP：${loginIp}，如非本人操作请及时修改密码。', 'loginTime:登录时间,loginIp:登录IP', 1, '账户异常通知模板');

    -- 提交事务
    COMMIT;

    -- 显示完成信息
    SELECT '基础数据插入完成！' as '执行结果',
        (SELECT COUNT(*) FROM tb_permission) as '权限数量',
        (SELECT COUNT(*) FROM tb_role) as '角色数量',
        (SELECT COUNT(*) FROM tb_user) as '用户数量',
        (SELECT COUNT(*) FROM tb_system_config) as '配置数量',
        (SELECT COUNT(*) FROM tb_notification_template) as '模板数量';
