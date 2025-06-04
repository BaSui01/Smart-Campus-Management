-- =====================================================
-- 智慧校园管理平台 - 索引创建脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-03
-- 描述: 创建性能优化索引
-- =====================================================

USE campus_management;

-- ===========================================
-- 用户管理模块索引
-- ===========================================

-- 用户表索引
ALTER TABLE tb_user ADD INDEX idx_username (username);
ALTER TABLE tb_user ADD INDEX idx_email (email);
ALTER TABLE tb_user ADD INDEX idx_phone (phone);
ALTER TABLE tb_user ADD INDEX idx_status (status);
ALTER TABLE tb_user ADD INDEX idx_created_time (created_time);

-- 角色表索引
ALTER TABLE tb_role ADD INDEX idx_role_code (role_code);
ALTER TABLE tb_role ADD INDEX idx_status (status);

-- 用户角色关联表索引
ALTER TABLE tb_user_role ADD INDEX idx_user_id (user_id);
ALTER TABLE tb_user_role ADD INDEX idx_role_id (role_id);

-- 学生表索引
ALTER TABLE tb_student ADD INDEX idx_student_no (student_no);
ALTER TABLE tb_student ADD INDEX idx_class_id (class_id);
ALTER TABLE tb_student ADD INDEX idx_grade (grade);
ALTER TABLE tb_student ADD INDEX idx_status (status);

-- 教师表索引
ALTER TABLE tb_teacher ADD INDEX idx_teacher_no (teacher_no);
ALTER TABLE tb_teacher ADD INDEX idx_department (department);
ALTER TABLE tb_teacher ADD INDEX idx_status (status);

-- 家长表索引
ALTER TABLE tb_parent ADD INDEX idx_user_id (user_id);

-- 学生家长关联表索引
ALTER TABLE tb_student_parent ADD INDEX idx_student_id (student_id);
ALTER TABLE tb_student_parent ADD INDEX idx_parent_id (parent_id);

-- ===========================================
-- 教务管理模块索引
-- ===========================================

-- 班级表索引
ALTER TABLE tb_class ADD INDEX idx_class_no (class_no);
ALTER TABLE tb_class ADD INDEX idx_grade (grade);
ALTER TABLE tb_class ADD INDEX idx_head_teacher_id (head_teacher_id);
ALTER TABLE tb_class ADD INDEX idx_status (status);

-- 课程表索引
ALTER TABLE tb_course ADD INDEX idx_course_code (course_code);
ALTER TABLE tb_course ADD INDEX idx_course_type (course_type);
ALTER TABLE tb_course ADD INDEX idx_department (department);
ALTER TABLE tb_course ADD INDEX idx_status (status);

-- 课程安排表索引
ALTER TABLE tb_course_schedule ADD INDEX idx_course_id (course_id);
ALTER TABLE tb_course_schedule ADD INDEX idx_teacher_id (teacher_id);
ALTER TABLE tb_course_schedule ADD INDEX idx_class_id (class_id);
ALTER TABLE tb_course_schedule ADD INDEX idx_semester (semester);
ALTER TABLE tb_course_schedule ADD INDEX idx_day_time (day_of_week, start_time);
ALTER TABLE tb_course_schedule ADD INDEX idx_status (status);

-- 选课记录表索引
ALTER TABLE tb_course_selection ADD INDEX idx_student_id (student_id);
ALTER TABLE tb_course_selection ADD INDEX idx_schedule_id (schedule_id);
ALTER TABLE tb_course_selection ADD INDEX idx_selection_time (selection_time);
ALTER TABLE tb_course_selection ADD INDEX idx_status (status);

-- 成绩表索引
ALTER TABLE tb_grade ADD INDEX idx_student_id (student_id);
ALTER TABLE tb_grade ADD INDEX idx_course_id (course_id);
ALTER TABLE tb_grade ADD INDEX idx_semester (semester);
ALTER TABLE tb_grade ADD INDEX idx_final_score (final_score);
ALTER TABLE tb_grade ADD INDEX idx_teacher_id (teacher_id);
ALTER TABLE tb_grade ADD INDEX idx_is_pass (is_pass);

-- ===========================================
-- 考试管理模块索引
-- ===========================================

-- 考试表索引
ALTER TABLE tb_exam ADD INDEX idx_exam_code (exam_code);
ALTER TABLE tb_exam ADD INDEX idx_course_id (course_id);
ALTER TABLE tb_exam ADD INDEX idx_exam_date (exam_date);
ALTER TABLE tb_exam ADD INDEX idx_semester (semester);
ALTER TABLE tb_exam ADD INDEX idx_exam_status (exam_status);
ALTER TABLE tb_exam ADD INDEX idx_teacher_id (teacher_id);
ALTER TABLE tb_exam ADD INDEX idx_exam_type (exam_type);

-- 题库表索引
ALTER TABLE tb_question_bank ADD INDEX idx_question_code (question_code);
ALTER TABLE tb_question_bank ADD INDEX idx_course_id (course_id);
ALTER TABLE tb_question_bank ADD INDEX idx_question_type (question_type);
ALTER TABLE tb_question_bank ADD INDEX idx_difficulty_level (difficulty_level);
ALTER TABLE tb_question_bank ADD INDEX idx_chapter (chapter);
ALTER TABLE tb_question_bank ADD INDEX idx_creator_id (creator_id);
ALTER TABLE tb_question_bank ADD INDEX idx_status (status);

-- ===========================================
-- 财务管理模块索引
-- ===========================================

-- 缴费项目表索引
ALTER TABLE tb_fee_item ADD INDEX idx_item_code (item_code);
ALTER TABLE tb_fee_item ADD INDEX idx_fee_type (fee_type);
ALTER TABLE tb_fee_item ADD INDEX idx_applicable_grade (applicable_grade);
ALTER TABLE tb_fee_item ADD INDEX idx_due_date (due_date);
ALTER TABLE tb_fee_item ADD INDEX idx_status (status);

-- 缴费记录表索引
ALTER TABLE tb_payment_record ADD INDEX idx_student_id (student_id);
ALTER TABLE tb_payment_record ADD INDEX idx_fee_item_id (fee_item_id);
ALTER TABLE tb_payment_record ADD INDEX idx_payment_time (payment_time);
ALTER TABLE tb_payment_record ADD INDEX idx_transaction_no (transaction_no);
ALTER TABLE tb_payment_record ADD INDEX idx_operator_id (operator_id);
ALTER TABLE tb_payment_record ADD INDEX idx_status (status);

-- ===========================================
-- 消息通知模块索引
-- ===========================================

-- 消息模板表索引
ALTER TABLE tb_message_template ADD INDEX idx_template_code (template_code);
ALTER TABLE tb_message_template ADD INDEX idx_template_type (template_type);
ALTER TABLE tb_message_template ADD INDEX idx_message_type (message_type);
ALTER TABLE tb_message_template ADD INDEX idx_creator_id (creator_id);
ALTER TABLE tb_message_template ADD INDEX idx_is_enabled (is_enabled);

-- 消息表索引
ALTER TABLE tb_message ADD INDEX idx_message_code (message_code);
ALTER TABLE tb_message ADD INDEX idx_template_id (template_id);
ALTER TABLE tb_message ADD INDEX idx_sender_id (sender_id);
ALTER TABLE tb_message ADD INDEX idx_message_type (message_type);
ALTER TABLE tb_message ADD INDEX idx_status (status);
ALTER TABLE tb_message ADD INDEX idx_scheduled_time (scheduled_time);
ALTER TABLE tb_message ADD INDEX idx_sent_time (sent_time);
ALTER TABLE tb_message ADD INDEX idx_target_type (target_type);
ALTER TABLE tb_message ADD INDEX idx_business_type (business_type);
ALTER TABLE tb_message ADD INDEX idx_business_id (business_id);
ALTER TABLE tb_message ADD INDEX idx_created_time (created_time);

-- 消息阅读状态表索引
ALTER TABLE tb_message_read ADD INDEX idx_message_id (message_id);
ALTER TABLE tb_message_read ADD INDEX idx_user_id (user_id);
ALTER TABLE tb_message_read ADD INDEX idx_is_read (is_read);
ALTER TABLE tb_message_read ADD INDEX idx_is_starred (is_starred);
ALTER TABLE tb_message_read ADD INDEX idx_is_deleted (is_deleted);
ALTER TABLE tb_message_read ADD INDEX idx_read_time (read_time);

-- ===========================================
-- 系统管理模块索引
-- ===========================================

-- 系统配置表索引
ALTER TABLE tb_system_config ADD INDEX idx_config_key (config_key);
ALTER TABLE tb_system_config ADD INDEX idx_config_type (config_type);
ALTER TABLE tb_system_config ADD INDEX idx_is_system (is_system);

-- 操作日志表索引
ALTER TABLE tb_operation_log ADD INDEX idx_user_id (user_id);
ALTER TABLE tb_operation_log ADD INDEX idx_username (username);
ALTER TABLE tb_operation_log ADD INDEX idx_operation (operation);
ALTER TABLE tb_operation_log ADD INDEX idx_status (status);
ALTER TABLE tb_operation_log ADD INDEX idx_created_time (created_time);
ALTER TABLE tb_operation_log ADD INDEX idx_ip_address (ip_address);

-- ===========================================
-- 复合索引（多条件查询优化）
-- ===========================================

-- 学生成绩查询复合索引
ALTER TABLE tb_grade ADD INDEX idx_student_semester_course (student_id, semester, course_id);
ALTER TABLE tb_grade ADD INDEX idx_semester_course_score (semester, course_id, final_score);

-- 消息查询复合索引
ALTER TABLE tb_message ADD INDEX idx_target_status_time (target_type, status, created_time);
ALTER TABLE tb_message ADD INDEX idx_sender_type_time (sender_id, message_type, created_time);

-- 考试安排查询复合索引
ALTER TABLE tb_course_schedule ADD INDEX idx_semester_day_time (semester, day_of_week, start_time);
ALTER TABLE tb_course_schedule ADD INDEX idx_teacher_semester (teacher_id, semester);

-- 缴费记录查询复合索引
ALTER TABLE tb_payment_record ADD INDEX idx_student_time_status (student_id, payment_time, status);
ALTER TABLE tb_payment_record ADD INDEX idx_time_status (payment_time, status);

-- 选课查询复合索引
ALTER TABLE tb_course_selection ADD INDEX idx_student_status_time (student_id, status, selection_time);

-- 用户角色查询复合索引
ALTER TABLE tb_user_role ADD INDEX idx_user_role_time (user_id, role_id, created_time);

-- 消息阅读状态复合索引
ALTER TABLE tb_message_read ADD INDEX idx_user_read_time (user_id, is_read, created_time);
ALTER TABLE tb_message_read ADD INDEX idx_message_read_deleted (message_id, is_read, is_deleted);

-- 学生班级复合索引
ALTER TABLE tb_student ADD INDEX idx_class_grade_status (class_id, grade, status);

-- 课程教师复合索引
ALTER TABLE tb_course_schedule ADD INDEX idx_course_teacher_semester (course_id, teacher_id, semester);

-- 考试学生复合索引
ALTER TABLE tb_exam ADD INDEX idx_course_date_status (course_id, exam_date, exam_status);

-- 题库课程复合索引
ALTER TABLE tb_question_bank ADD INDEX idx_course_type_difficulty (course_id, question_type, difficulty_level);

-- 缴费项目复合索引
ALTER TABLE tb_fee_item ADD INDEX idx_type_grade_status (fee_type, applicable_grade, status);

-- 操作日志复合索引
ALTER TABLE tb_operation_log ADD INDEX idx_user_time_status (user_id, created_time, status);

SELECT '所有索引创建成功' AS message;
SHOW INDEX FROM tb_user;
