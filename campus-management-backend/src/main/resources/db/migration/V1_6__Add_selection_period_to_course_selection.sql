-- 为选课表添加选课时间段关联字段
-- 版本: V1.6
-- 作者: Campus Management Team
-- 日期: 2025-06-08

-- 添加选课时间段ID字段到选课表
ALTER TABLE tb_course_selection 
ADD COLUMN selection_period_id BIGINT NULL COMMENT '选课时间段ID';

-- 添加外键约束
ALTER TABLE tb_course_selection 
ADD CONSTRAINT fk_course_selection_period 
FOREIGN KEY (selection_period_id) REFERENCES course_selection_periods(id);

-- 添加索引以提高查询性能
CREATE INDEX idx_course_selection_period_id ON tb_course_selection(selection_period_id);

-- 添加复合索引
CREATE INDEX idx_course_selection_period_student ON tb_course_selection(selection_period_id, student_id);
CREATE INDEX idx_course_selection_period_course ON tb_course_selection(selection_period_id, course_id);
