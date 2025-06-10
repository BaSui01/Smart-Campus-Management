-- =====================================================
-- Smart Campus Management System - 选课和成绩数据生成脚本
-- 文件: 05_generate_course_selections_and_grades.sql
-- 描述: 生成选课记录、成绩数据和课程调度
-- 版本: 2.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

-- 设置优化参数
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;

-- =====================================================
-- 高性能批量生成选课时间段数据
-- =====================================================

-- 插入选课时间段
INSERT IGNORE INTO tb_course_selection_period (
    period_name, academic_year, semester, start_time, end_time,
    target_grades, selection_type, max_credits, min_credits, description
) VALUES
-- 2024-2025学年选课时间段
('2024-2025学年第一学期选课', '2024-2025', '第一学期', '2024-08-15 09:00:00', '2024-08-25 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'normal', 25.0, 15.0, '2024-2025学年第一学期正常选课'),
('2024-2025学年第二学期选课', '2024-2025', '第二学期', '2025-01-10 09:00:00', '2025-01-20 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'normal', 25.0, 15.0, '2024-2025学年第二学期正常选课'),
('2024-2025学年第一学期补选', '2024-2025', '第一学期', '2024-09-01 09:00:00', '2024-09-05 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'makeup', 5.0, 0.0, '2024-2025学年第一学期补选'),
('2024-2025学年第二学期补选', '2024-2025', '第二学期', '2025-02-20 09:00:00', '2025-02-25 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'makeup', 5.0, 0.0, '2024-2025学年第二学期补选'),

-- 新生专门选课时间段
('2024-2025学年新生第一学期选课', '2024-2025', '第一学期', '2024-08-28 09:00:00', '2024-09-03 18:00:00',
 '2024级', 'freshman', 20.0, 15.0, '2024-2025学年新生第一学期专门选课'),
('2024-2025学年新生第二学期选课', '2024-2025', '第二学期', '2025-02-15 09:00:00', '2025-02-22 18:00:00',
 '2024级', 'freshman', 22.0, 15.0, '2024-2025学年新生第二学期专门选课'),

-- 研究生选课时间段
('2024-2025学年研究生第一学期选课', '2024-2025', '第一学期', '2024-08-20 09:00:00', '2024-08-30 18:00:00',
 '2022级,2023级,2024级', 'graduate', 15.0, 8.0, '2024-2025学年研究生第一学期选课'),
('2024-2025学年研究生第二学期选课', '2024-2025', '第二学期', '2025-01-15 09:00:00', '2025-01-25 18:00:00',
 '2022级,2023级,2024级', 'graduate', 15.0, 8.0, '2024-2025学年研究生第二学期选课'),

-- 重修选课时间段
('2024-2025学年第一学期重修选课', '2024-2025', '第一学期', '2024-09-10 09:00:00', '2024-09-15 18:00:00',
 '2021级,2022级,2023级', 'retake', 12.0, 0.0, '2024-2025学年第一学期重修选课'),
('2024-2025学年第二学期重修选课', '2024-2025', '第二学期', '2025-03-01 09:00:00', '2025-03-06 18:00:00',
 '2021级,2022级,2023级', 'retake', 12.0, 0.0, '2024-2025学年第二学期重修选课'),

-- 公共选修课选课时间段
('2024-2025学年第一学期公选课选课', '2024-2025', '第一学期', '2024-08-26 09:00:00', '2024-08-31 18:00:00',
 '2021级,2022级,2023级,2024级', 'elective', 8.0, 2.0, '2024-2025学年第一学期公共选修课选课'),
('2024-2025学年第二学期公选课选课', '2024-2025', '第二学期', '2025-01-25 09:00:00', '2025-01-30 18:00:00',
 '2021级,2022级,2023级,2024级', 'elective', 8.0, 2.0, '2024-2025学年第二学期公共选修课选课'),

-- 体育课选课时间段
('2024-2025学年第一学期体育课选课', '2024-2025', '第一学期', '2024-08-18 09:00:00', '2024-08-22 18:00:00',
 '2022级,2023级,2024级', 'sports', 2.0, 1.0, '2024-2025学年第一学期体育课选课'),
('2024-2025学年第二学期体育课选课', '2024-2025', '第二学期', '2025-01-12 09:00:00', '2025-01-16 18:00:00',
 '2022级,2023级,2024级', 'sports', 2.0, 1.0, '2024-2025学年第二学期体育课选课'),

-- 实验课选课时间段
('2024-2025学年第一学期实验课选课', '2024-2025', '第一学期', '2024-08-22 09:00:00', '2024-08-27 18:00:00',
 '2021级,2022级,2023级,2024级', 'lab', 6.0, 2.0, '2024-2025学年第一学期实验课选课'),
('2024-2025学年第二学期实验课选课', '2024-2025', '第二学期', '2025-01-18 09:00:00', '2025-01-23 18:00:00',
 '2021级,2022级,2023级,2024级', 'lab', 6.0, 2.0, '2024-2025学年第二学期实验课选课'),

-- 暑期课程选课时间段
('2024年暑期课程选课', '2024-2025', '暑期', '2024-05-15 09:00:00', '2024-05-25 18:00:00',
 '2021级,2022级,2023级', 'summer', 6.0, 2.0, '2024年暑期课程选课'),
('2025年暑期课程选课', '2024-2025', '暑期', '2025-05-15 09:00:00', '2025-05-25 18:00:00',
 '2021级,2022级,2023级,2024级', 'summer', 6.0, 2.0, '2025年暑期课程选课'),

-- 国际交流课程选课时间段
('2024-2025学年国际交流课程选课', '2024-2025', '第一学期', '2024-08-10 09:00:00', '2024-08-20 18:00:00',
 '2021级,2022级,2023级', 'international', 12.0, 6.0, '2024-2025学年国际交流课程选课'),

-- 辅修课程选课时间段
('2024-2025学年第一学期辅修课程选课', '2024-2025', '第一学期', '2024-09-05 09:00:00', '2024-09-10 18:00:00',
 '2022级,2023级', 'minor', 15.0, 6.0, '2024-2025学年第一学期辅修课程选课'),
('2024-2025学年第二学期辅修课程选课', '2024-2025', '第二学期', '2025-02-25 09:00:00', '2025-03-02 18:00:00',
 '2022级,2023级', 'minor', 15.0, 6.0, '2024-2025学年第二学期辅修课程选课'),

-- 毕业设计选课时间段
('2024-2025学年毕业设计选题', '2024-2025', '第一学期', '2024-09-15 09:00:00', '2024-09-25 18:00:00',
 '2021级', 'thesis', 8.0, 8.0, '2024-2025学年毕业设计选题'),

-- 2025-2026学年预选课时间段
('2025-2026学年第一学期预选课', '2025-2026', '第一学期', '2025-05-01 09:00:00', '2025-05-10 18:00:00',
 '2022级,2023级,2024级,2025级', 'preview', 25.0, 15.0, '2025-2026学年第一学期预选课'),

-- 专业课程选课时间段（按学院分别开放）
('2024-2025学年理工科专业课选课', '2024-2025', '第一学期', '2024-08-16 09:00:00', '2024-08-24 18:00:00',
 '2021级,2022级,2023级,2024级', 'major_science', 22.0, 12.0, '2024-2025学年理工科专业课选课'),
('2024-2025学年文科专业课选课', '2024-2025', '第一学期', '2024-08-17 09:00:00', '2024-08-26 18:00:00',
 '2021级,2022级,2023级,2024级', 'major_liberal', 20.0, 12.0, '2024-2025学年文科专业课选课'),
('2024-2025学年医学专业课选课', '2024-2025', '第一学期', '2024-08-19 09:00:00', '2024-08-28 18:00:00',
 '2021级,2022级,2023级,2024级', 'major_medical', 28.0, 18.0, '2024-2025学年医学专业课选课'),
('2024-2025学年艺术专业课选课', '2024-2025', '第一学期', '2024-08-21 09:00:00', '2024-08-29 18:00:00',
 '2021级,2022级,2023级,2024级', 'major_arts', 18.0, 10.0, '2024-2025学年艺术专业课选课'),

-- 特殊需求选课时间段
('2024-2025学年交换生选课', '2024-2025', '第一学期', '2024-08-12 09:00:00', '2024-08-18 18:00:00',
 '交换生', 'exchange', 20.0, 12.0, '2024-2025学年交换生专门选课'),
('2024-2025学年留学生选课', '2024-2025', '第一学期', '2024-08-14 09:00:00', '2024-08-21 18:00:00',
 '留学生', 'international_student', 18.0, 10.0, '2024-2025学年留学生专门选课'),

-- 补退选时间段
('2024-2025学年第一学期退课', '2024-2025', '第一学期', '2024-09-20 09:00:00', '2024-09-25 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'withdraw', 0.0, 0.0, '2024-2025学年第一学期退课时间'),
('2024-2025学年第二学期退课', '2024-2025', '第二学期', '2025-03-10 09:00:00', '2025-03-15 18:00:00',
 '2021级,2022级,2023级,2024级,2025级', 'withdraw', 0.0, 0.0, '2024-2025学年第二学期退课时间');

-- =====================================================
-- 高性能批量生成课程调度数据
-- =====================================================

-- 批量生成课程调度（第一学期）
INSERT INTO tb_course_schedule (
    course_id, teacher_id, classroom_id, time_slot_id, day_of_week,
    week_start, week_end, academic_year, semester, schedule_type,
    status, deleted
)
SELECT
    c.id as course_id,
    teachers.teacher_id,
    classrooms.classroom_id,
    time_slots.time_slot_id,
    1 + (c.id % 5) as day_of_week,  -- 周一到周五循环
    1 as week_start,
    18 as week_end,
    '2024-2025' as academic_year,
    '第一学期' as semester,
    'regular' as schedule_type,
    1 as status,
    0 as deleted
FROM tb_course c
CROSS JOIN (
    SELECT
        u.id as teacher_id,
        ROW_NUMBER() OVER (ORDER BY u.id) as teacher_row
    FROM tb_user u
    JOIN tb_user_role ur ON u.id = ur.user_id
    JOIN tb_role r ON ur.role_id = r.id
    WHERE r.role_key IN ('ROLE_TEACHER', 'ROLE_CLASS_TEACHER')
    AND u.deleted = 0 AND u.status = 1
) teachers
CROSS JOIN (
    SELECT
        id as classroom_id,
        ROW_NUMBER() OVER (ORDER BY id) as classroom_row
    FROM tb_classroom
    WHERE deleted = 0 AND status = 1
) classrooms
CROSS JOIN (
    SELECT
        id as time_slot_id,
        ROW_NUMBER() OVER (ORDER BY id) as time_slot_row
    FROM tb_time_slot
    WHERE deleted = 0 AND status = 1
) time_slots
WHERE c.deleted = 0 AND c.status = 1
AND teachers.teacher_row = ((c.id - 1) % 2800) + 1  -- 循环分配教师
AND classrooms.classroom_row = ((c.id - 1) % 25) + 1  -- 循环分配教室
AND time_slots.time_slot_row = ((c.id - 1) % 10) + 1;  -- 循环分配时间段

-- 批量生成课程调度（第二学期）
INSERT INTO tb_course_schedule (
    course_id, teacher_id, classroom_id, time_slot_id, day_of_week,
    week_start, week_end, academic_year, semester, schedule_type,
    status, deleted
)
SELECT
    course_id, teacher_id, classroom_id, time_slot_id, day_of_week,
    week_start, week_end, academic_year, '第二学期', schedule_type,
    status, deleted
FROM tb_course_schedule
WHERE semester = '第一学期' AND deleted = 0;

-- 批量插入教师课程权限
INSERT IGNORE INTO tb_teacher_course_permission (
    teacher_id, course_id, permission_type, academic_year, semester,
    status, deleted
)
SELECT
    teacher_id, course_id, 'teaching', academic_year, semester, 1, 0
FROM tb_course_schedule
WHERE deleted = 0 AND status = 1;

-- =====================================================
-- 高性能批量生成选课记录
-- =====================================================

-- 为学生批量选择必修课（每个学生选择本学院的必修课）
INSERT INTO tb_course_selection (
    student_id, course_id, semester, academic_year,
    selection_time, selection_status, selection_type,
    priority_level, is_retake, status, deleted
)
SELECT
    s.id as student_id,
    c.id as course_id,
    '第一学期' as semester,
    '2024-2025' as academic_year,
    DATE_ADD('2024-08-15', INTERVAL FLOOR(RAND() * 10) DAY) as selection_time,
    'selected' as selection_status,
    'normal' as selection_type,
    1 as priority_level,
    0 as is_retake,
    1 as status,
    0 as deleted
FROM tb_student s
JOIN tb_school_class sc ON s.class_id = sc.id
JOIN tb_course c ON c.department_id = sc.department_id
WHERE s.deleted = 0 AND s.status = 1
AND c.deleted = 0 AND c.status = 1
AND c.course_type = '必修课'
AND (s.id + c.id) % 10 < 6;  -- 每个学生选择约60%的本学院必修课

-- 为学生批量选择选修课（跨学院选择）
INSERT INTO tb_course_selection (
    student_id, course_id, semester, academic_year,
    selection_time, selection_status, selection_type,
    priority_level, is_retake, status, deleted
)
SELECT
    s.id as student_id,
    c.id as course_id,
    '第一学期' as semester,
    '2024-2025' as academic_year,
    DATE_ADD('2024-08-15', INTERVAL FLOOR(RAND() * 10) DAY) as selection_time,
    'selected' as selection_status,
    'elective' as selection_type,
    2 as priority_level,
    0 as is_retake,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN tb_course c
WHERE s.deleted = 0 AND s.status = 1
AND c.deleted = 0 AND c.status = 1
AND c.course_type = '选修课'
AND (s.id + c.id) % 20 < 3  -- 每个学生选择约15%的选修课
AND NOT EXISTS (
    SELECT 1 FROM tb_course_selection cs
    WHERE cs.student_id = s.id AND cs.course_id = c.id AND cs.deleted = 0
);

-- 批量更新课程选课人数
UPDATE tb_course c
SET current_students = (
    SELECT COUNT(*)
    FROM tb_course_selection cs
    WHERE cs.course_id = c.id AND cs.deleted = 0
)
WHERE c.deleted = 0;

-- =====================================================
-- 高性能批量生成成绩数据
-- =====================================================

-- 为已完成的选课记录批量生成成绩（仅为高年级学生）
INSERT INTO tb_grade (
    student_id, course_id, semester, academic_year,
    regular_score, midterm_score, final_score, total_score,
    letter_grade, gpa_points, grade_status, is_passed,
    record_time, teacher_id, status, deleted
)
SELECT
    cs.student_id,
    cs.course_id,
    cs.semester,
    cs.academic_year,
    60 + (cs.student_id % 35) as regular_score,  -- 60-95分
    55 + (cs.course_id % 40) as midterm_score,   -- 55-95分
    50 + ((cs.student_id + cs.course_id) % 45) as final_score,  -- 50-95分
    ROUND(
        (60 + (cs.student_id % 35)) * 0.3 +
        (55 + (cs.course_id % 40)) * 0.3 +
        (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
        2
    ) as total_score,
    CASE
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 95 THEN 'A+'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 90 THEN 'A'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 85 THEN 'A-'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 82 THEN 'B+'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 78 THEN 'B'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 75 THEN 'B-'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 72 THEN 'C+'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 68 THEN 'C'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 64 THEN 'C-'
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 60 THEN 'D'
        ELSE 'F'
    END as letter_grade,
    CASE
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 95 THEN 4.0
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 90 THEN 3.7
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 85 THEN 3.3
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 82 THEN 3.0
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 78 THEN 2.7
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 75 THEN 2.3
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 72 THEN 2.0
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 68 THEN 1.7
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 64 THEN 1.3
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 60 THEN 1.0
        ELSE 0.0
    END as gpa_points,
    'published' as grade_status,
    CASE
        WHEN ROUND(
            (60 + (cs.student_id % 35)) * 0.3 +
            (55 + (cs.course_id % 40)) * 0.3 +
            (50 + ((cs.student_id + cs.course_id) % 45)) * 0.4,
            2
        ) >= 60 THEN 1 ELSE 0
    END as is_passed,
    DATE_ADD('2024-12-15', INTERVAL FLOOR(RAND() * 15) DAY) as record_time,
    csch.teacher_id,
    1 as status,
    0 as deleted
FROM tb_course_selection cs
JOIN tb_student s ON cs.student_id = s.id
JOIN tb_course_schedule csch ON cs.course_id = csch.course_id AND cs.semester = csch.semester
WHERE cs.deleted = 0 AND cs.status = 1
AND s.grade IN ('2021级', '2022级', '2023级')  -- 仅为高年级学生生成成绩
AND s.deleted = 0;

-- =====================================================
-- 执行数据生成
-- =====================================================

START TRANSACTION;

SELECT '=== 开始生成选课和成绩数据 ===' as '状态', NOW() as '时间';

-- 步骤1: 生成选课时间段
SELECT '步骤1: 生成选课时间段...' as '状态', NOW() as '时间';
-- 选课时间段已在上面批量生成
SELECT '✓ 选课时间段生成完成' as '状态', NOW() as '时间';

-- 步骤2: 生成课程调度
SELECT '步骤2: 生成课程调度...' as '状态', NOW() as '时间';
-- 课程调度已在上面批量生成
SELECT '✓ 课程调度生成完成' as '状态', NOW() as '时间';

-- 步骤3: 生成选课记录
SELECT '步骤3: 生成选课记录...' as '状态', NOW() as '时间';
-- 选课记录已在上面批量生成
SELECT '✓ 选课记录生成完成' as '状态', NOW() as '时间';

-- 步骤4: 生成成绩数据
SELECT '步骤4: 生成成绩数据...' as '状态', NOW() as '时间';
-- 成绩数据已在上面批量生成
SELECT '✓ 成绩数据生成完成' as '状态', NOW() as '时间';

-- 恢复设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

COMMIT;

-- 显示统计信息
SELECT '=== 选课和成绩数据生成完成 ===' as '状态', NOW() as '时间';

SELECT 
    '选课记录总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_course_selection WHERE deleted = 0
UNION ALL
SELECT 
    '成绩记录总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_grade WHERE deleted = 0
UNION ALL
SELECT 
    '课程调度总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_course_schedule WHERE deleted = 0;

SELECT '✓ 选课和成绩数据生成完成' as '状态', NOW() as '时间';
