-- ================================================
-- 校园管理系统数据库索引创建脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- ================================================

-- 使用数据库
USE campus_management_db;

-- ================================================
-- 用户表索引 (tb_user)
-- ================================================
CREATE INDEX idx_user_username ON tb_user(username);
CREATE INDEX idx_user_email ON tb_user(email);
CREATE INDEX idx_user_status ON tb_user(status);
CREATE INDEX idx_user_deleted ON tb_user(deleted);
CREATE INDEX idx_user_created_at ON tb_user(created_at);

-- ================================================
-- 角色表索引 (tb_role)
-- ================================================
CREATE INDEX idx_role_name ON tb_role(role_name);
CREATE INDEX idx_role_key ON tb_role(role_key);
CREATE INDEX idx_role_status ON tb_role(status);
CREATE INDEX idx_role_deleted ON tb_role(deleted);

-- ================================================
-- 权限表索引 (tb_permission)
-- ================================================
CREATE INDEX idx_permission_code ON tb_permission(permission_code);
CREATE INDEX idx_permission_type ON tb_permission(resource_type);
CREATE INDEX idx_permission_status ON tb_permission(status);

-- ================================================
-- 用户角色关联表索引 (tb_user_role)
-- ================================================
CREATE INDEX idx_user_role_user_id ON tb_user_role(user_id);
CREATE INDEX idx_user_role_role_id ON tb_user_role(role_id);

-- ================================================
-- 角色权限关联表索引 (tb_role_permission)
-- ================================================
CREATE INDEX idx_role_permission_role_id ON tb_role_permission(role_id);
CREATE INDEX idx_role_permission_permission_id ON tb_role_permission(permission_id);

-- ================================================
-- 班级表索引 (tb_class)
-- ================================================
CREATE INDEX idx_class_code ON tb_class(class_code);
CREATE INDEX idx_class_grade ON tb_class(grade);
CREATE INDEX idx_class_department_id ON tb_class(department_id);
CREATE INDEX idx_class_head_teacher_id ON tb_class(head_teacher_id);
CREATE INDEX idx_class_status ON tb_class(status);
CREATE INDEX idx_class_deleted ON tb_class(deleted);

-- ================================================
-- 学生表索引 (tb_student)
-- ================================================
CREATE INDEX idx_student_user_id ON tb_student(user_id);
CREATE INDEX idx_student_no ON tb_student(student_no);
CREATE INDEX idx_student_grade ON tb_student(grade);
CREATE INDEX idx_student_class_id ON tb_student(class_id);
CREATE INDEX idx_student_status ON tb_student(status);
CREATE INDEX idx_student_deleted ON tb_student(deleted);
CREATE INDEX idx_student_enrollment_date ON tb_student(enrollment_date);

-- ================================================
-- 课程表索引 (tb_course)
-- ================================================
CREATE INDEX idx_course_code ON tb_course(course_code);
CREATE INDEX idx_course_name ON tb_course(course_name);
CREATE INDEX idx_course_teacher_id ON tb_course(teacher_id);
CREATE INDEX idx_course_department_id ON tb_course(department_id);
CREATE INDEX idx_course_type ON tb_course(course_type);
CREATE INDEX idx_course_semester ON tb_course(semester);
CREATE INDEX idx_course_status ON tb_course(status);
CREATE INDEX idx_course_deleted ON tb_course(deleted);

-- ================================================
-- 课程表排课表索引 (tb_course_schedule)
-- ================================================
CREATE INDEX idx_schedule_course_id ON tb_course_schedule(course_id);
CREATE INDEX idx_schedule_teacher_id ON tb_course_schedule(teacher_id);
CREATE INDEX idx_schedule_class_id ON tb_course_schedule(class_id);
CREATE INDEX idx_schedule_semester ON tb_course_schedule(semester);
CREATE INDEX idx_schedule_day_of_week ON tb_course_schedule(day_of_week);
CREATE INDEX idx_schedule_classroom ON tb_course_schedule(classroom);
CREATE INDEX idx_schedule_status ON tb_course_schedule(status);
CREATE INDEX idx_schedule_deleted ON tb_course_schedule(deleted);
CREATE INDEX idx_schedule_time ON tb_course_schedule(start_time, end_time);

-- ================================================
-- 选课表索引 (tb_course_selection)
-- ================================================
CREATE INDEX idx_selection_student_id ON tb_course_selection(student_id);
CREATE INDEX idx_selection_course_id ON tb_course_selection(course_id);
CREATE INDEX idx_selection_schedule_id ON tb_course_selection(schedule_id);
CREATE INDEX idx_selection_semester ON tb_course_selection(semester);
CREATE INDEX idx_selection_time ON tb_course_selection(selection_time);
CREATE INDEX idx_selection_status ON tb_course_selection(status);
CREATE INDEX idx_selection_deleted ON tb_course_selection(deleted);

-- ================================================
-- 成绩表索引 (tb_grade)
-- ================================================
CREATE INDEX idx_grade_student_id ON tb_grade(student_id);
CREATE INDEX idx_grade_course_id ON tb_grade(course_id);
CREATE INDEX idx_grade_schedule_id ON tb_grade(schedule_id);
CREATE INDEX idx_grade_selection_id ON tb_grade(selection_id);
CREATE INDEX idx_grade_teacher_id ON tb_grade(teacher_id);
CREATE INDEX idx_grade_semester ON tb_grade(semester);
CREATE INDEX idx_grade_level ON tb_grade(level);
CREATE INDEX idx_grade_status ON tb_grade(status);
CREATE INDEX idx_grade_deleted ON tb_grade(deleted);
CREATE INDEX idx_grade_score ON tb_grade(score);

-- ================================================
-- 缴费项目表索引 (tb_fee_item)
-- ================================================
CREATE INDEX idx_fee_item_code ON tb_fee_item(item_code);
CREATE INDEX idx_fee_item_type ON tb_fee_item(fee_type);
CREATE INDEX idx_fee_item_grade ON tb_fee_item(applicable_grade);
CREATE INDEX idx_fee_item_due_date ON tb_fee_item(due_date);
CREATE INDEX idx_fee_item_status ON tb_fee_item(status);
CREATE INDEX idx_fee_item_deleted ON tb_fee_item(deleted);

-- ================================================
-- 缴费记录表索引 (tb_payment_record)
-- ================================================
CREATE INDEX idx_payment_student_id ON tb_payment_record(student_id);
CREATE INDEX idx_payment_fee_item_id ON tb_payment_record(fee_item_id);
CREATE INDEX idx_payment_operator_id ON tb_payment_record(operator_id);
CREATE INDEX idx_payment_method ON tb_payment_record(payment_method);
CREATE INDEX idx_payment_time ON tb_payment_record(payment_time);
CREATE INDEX idx_payment_transaction_no ON tb_payment_record(transaction_no);
CREATE INDEX idx_payment_status ON tb_payment_record(status);
CREATE INDEX idx_payment_deleted ON tb_payment_record(deleted);

-- ================================================
-- 外键约束创建
-- ================================================

-- 用户角色关联表外键
ALTER TABLE tb_user_role 
ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE;

-- 角色权限关联表外键
ALTER TABLE tb_role_permission 
ADD CONSTRAINT fk_role_permission_role_id FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES tb_permission(id) ON DELETE CASCADE;

-- 学生表外键
ALTER TABLE tb_student 
ADD CONSTRAINT fk_student_user_id FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_student_class_id FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL;

-- 课程表外键
ALTER TABLE tb_course 
ADD CONSTRAINT fk_course_teacher_id FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL;

-- 课程表排课表外键
ALTER TABLE tb_course_schedule 
ADD CONSTRAINT fk_schedule_course_id FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_schedule_teacher_id FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_schedule_class_id FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL;

-- 选课表外键
ALTER TABLE tb_course_selection 
ADD CONSTRAINT fk_selection_student_id FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_selection_course_id FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_selection_schedule_id FOREIGN KEY (schedule_id) REFERENCES tb_course_schedule(id) ON DELETE CASCADE;

-- 成绩表外键
ALTER TABLE tb_grade 
ADD CONSTRAINT fk_grade_student_id FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_grade_course_id FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_grade_schedule_id FOREIGN KEY (schedule_id) REFERENCES tb_course_schedule(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_grade_selection_id FOREIGN KEY (selection_id) REFERENCES tb_course_selection(id) ON DELETE SET NULL,
ADD CONSTRAINT fk_grade_teacher_id FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL;

-- 缴费记录表外键
ALTER TABLE tb_payment_record 
ADD CONSTRAINT fk_payment_student_id FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_payment_fee_item_id FOREIGN KEY (fee_item_id) REFERENCES tb_fee_item(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_payment_operator_id FOREIGN KEY (operator_id) REFERENCES tb_user(id) ON DELETE SET NULL;

-- 显示索引创建完成信息
SELECT 'All indexes and foreign keys created successfully!' AS result;