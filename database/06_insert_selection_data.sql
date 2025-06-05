-- ================================================
-- 校园管理系统选课数据插入脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 插入选课数据（500+条记录）
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 插入选课数据 (tb_course_selection)
-- ================================================

-- 使用存储过程批量生成选课数据
DELIMITER //

DROP PROCEDURE IF EXISTS GenerateCourseSelectionData//

CREATE PROCEDURE GenerateCourseSelectionData()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE student_id_var BIGINT;
    DECLARE student_grade_var VARCHAR(20);
    DECLARE selection_count INT;
    
    -- 声明学生游标
    DECLARE student_cursor CURSOR FOR
        SELECT id, grade FROM tb_student WHERE status = 1 ORDER BY id;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 打开学生游标
    OPEN student_cursor;
    
    student_loop: LOOP
        FETCH student_cursor INTO student_id_var, student_grade_var;
        
        IF done THEN
            LEAVE student_loop;
        END IF;
        
        -- 每个学生随机选择5-12门课程
        SET selection_count = 5 + FLOOR(RAND() * 8);

        -- 为当前学生随机选择课程（优化版本，避免ORDER BY RAND()）
        INSERT INTO tb_course_selection (
            student_id, course_id, schedule_id, semester,
            selection_time, status
        )
        SELECT
            student_id_var,
            cs.course_id,
            cs.id,
            cs.semester,
            DATE_ADD('2024-08-15', INTERVAL ((student_id_var + cs.id) % 30) DAY),
            1
        FROM tb_course_schedule cs
        INNER JOIN tb_course c ON cs.course_id = c.id
        WHERE cs.status = 1 AND c.status = 1
          AND cs.enrolled_students < cs.max_students
          AND (student_id_var + cs.id) % 10 < 3  -- 30%概率选择
          AND NOT EXISTS (
              SELECT 1 FROM tb_course_selection
              WHERE student_id = student_id_var AND schedule_id = cs.id
          )
        LIMIT selection_count;

        
    END LOOP student_loop;

    CLOSE student_cursor;
END//

DELIMITER ;

-- 执行存储过程
CALL GenerateCourseSelectionData();

-- 删除存储过程
DROP PROCEDURE GenerateCourseSelectionData;

-- ================================================
-- 2. 更新课程和课程表的选课统计
-- ================================================

-- 更新课程表的已选学生数统计
UPDATE tb_course_schedule cs 
SET enrolled_students = (
    SELECT COUNT(*) 
    FROM tb_course_selection sel 
    WHERE sel.schedule_id = cs.id AND sel.status = 1
);

-- 更新课程的已选学生数统计
UPDATE tb_course c 
SET enrolled_students = (
    SELECT COUNT(*) 
    FROM tb_course_selection sel 
    WHERE sel.course_id = c.id AND sel.status = 1
);

-- 显示插入结果
SELECT '选课数据插入完成！' AS result;
SELECT '选课记录总数:', COUNT(*) FROM tb_course_selection;

-- 按学期统计选课数量
SELECT semester, COUNT(*) as selection_count 
FROM tb_course_selection 
GROUP BY semester 
ORDER BY semester;

-- 按年级统计选课数量
SELECT s.grade, COUNT(cs.id) as selection_count
FROM tb_student s
INNER JOIN tb_course_selection cs ON s.id = cs.student_id
GROUP BY s.grade
ORDER BY s.grade;

-- 统计热门课程（选课人数前10）
SELECT c.course_name, c.course_code, COUNT(cs.id) as selection_count
FROM tb_course c
INNER JOIN tb_course_selection cs ON c.id = cs.course_id
GROUP BY c.id, c.course_name, c.course_code
ORDER BY selection_count DESC
LIMIT 10;

-- 统计每个学生的选课数量分布
SELECT selection_count, COUNT(*) as student_count
FROM (
    SELECT student_id, COUNT(*) as selection_count
    FROM tb_course_selection
    GROUP BY student_id
) t
GROUP BY selection_count
ORDER BY selection_count;
