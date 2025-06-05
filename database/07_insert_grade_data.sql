-- ================================================
-- 校园管理系统成绩数据插入脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 插入成绩数据（500+条记录）
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 插入成绩数据 (tb_grade)
-- ================================================

-- 使用存储过程批量生成成绩数据
DELIMITER //

DROP PROCEDURE IF EXISTS GenerateGradeData//

CREATE PROCEDURE GenerateGradeData()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE selection_id_var BIGINT;
    DECLARE student_id_var BIGINT;
    DECLARE course_id_var BIGINT;
    DECLARE schedule_id_var BIGINT;
    DECLARE semester_var VARCHAR(50);
    DECLARE teacher_id_var BIGINT;
    
    DECLARE regular_score_var DECIMAL(5,2);
    DECLARE midterm_score_var DECIMAL(5,2);
    DECLARE final_score_var DECIMAL(5,2);
    DECLARE total_score_var DECIMAL(5,2);
    DECLARE grade_point_var DECIMAL(3,1);
    DECLARE level_var VARCHAR(10);
    
    -- 声明选课记录游标
    DECLARE selection_cursor CURSOR FOR 
        SELECT cs.id, cs.student_id, cs.course_id, cs.schedule_id, cs.semester, sch.teacher_id
        FROM tb_course_selection cs
        INNER JOIN tb_course_schedule sch ON cs.schedule_id = sch.id
        WHERE cs.status = 1
        ORDER BY cs.id;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 打开游标
    OPEN selection_cursor;
    
    selection_loop: LOOP
        FETCH selection_cursor INTO selection_id_var, student_id_var, course_id_var, 
                                   schedule_id_var, semester_var, teacher_id_var;
        
        IF done THEN
            LEAVE selection_loop;
        END IF;
        
        -- 生成随机成绩（正态分布，平均分75-85）
        -- 平时成绩：70-95分
        SET regular_score_var = 70 + RAND() * 25;
        
        -- 期中成绩：60-95分
        SET midterm_score_var = 60 + RAND() * 35;
        
        -- 期末成绩：50-98分
        SET final_score_var = 50 + RAND() * 48;
        
        -- 总成绩计算：平时30% + 期中30% + 期末40%
        SET total_score_var = regular_score_var * 0.3 + midterm_score_var * 0.3 + final_score_var * 0.4;
        
        -- 计算绩点和等级
        CASE 
            WHEN total_score_var >= 95 THEN 
                SET grade_point_var = 4.0;
                SET level_var = 'A+';
            WHEN total_score_var >= 90 THEN 
                SET grade_point_var = 3.7;
                SET level_var = 'A';
            WHEN total_score_var >= 85 THEN 
                SET grade_point_var = 3.3;
                SET level_var = 'A-';
            WHEN total_score_var >= 82 THEN 
                SET grade_point_var = 3.0;
                SET level_var = 'B+';
            WHEN total_score_var >= 78 THEN 
                SET grade_point_var = 2.7;
                SET level_var = 'B';
            WHEN total_score_var >= 75 THEN 
                SET grade_point_var = 2.3;
                SET level_var = 'B-';
            WHEN total_score_var >= 72 THEN 
                SET grade_point_var = 2.0;
                SET level_var = 'C+';
            WHEN total_score_var >= 68 THEN 
                SET grade_point_var = 1.7;
                SET level_var = 'C';
            WHEN total_score_var >= 64 THEN 
                SET grade_point_var = 1.3;
                SET level_var = 'C-';
            WHEN total_score_var >= 60 THEN 
                SET grade_point_var = 1.0;
                SET level_var = 'D';
            ELSE 
                SET grade_point_var = 0.0;
                SET level_var = 'F';
        END CASE;
        
        -- 插入成绩记录
        INSERT INTO tb_grade (
            student_id, course_id, schedule_id, selection_id, semester,
            score, regular_score, midterm_score, final_score,
            grade_point, level, teacher_id, status
        ) VALUES (
            student_id_var, course_id_var, schedule_id_var, selection_id_var, semester_var,
            total_score_var, regular_score_var, midterm_score_var, final_score_var,
            grade_point_var, level_var, teacher_id_var, 1
        );
        
    END LOOP;
    
    CLOSE selection_cursor;
END//

DELIMITER ;

-- 执行存储过程
CALL GenerateGradeData();

-- 删除存储过程
DROP PROCEDURE GenerateGradeData;

-- ================================================
-- 2. 生成部分补考和重修记录
-- ================================================

-- 为不及格的学生生成补考记录（随机选择一部分）
INSERT INTO tb_grade (
    student_id, course_id, schedule_id, selection_id, semester,
    score, regular_score, midterm_score, final_score,
    grade_point, level, is_makeup, teacher_id, status
)
SELECT 
    g.student_id, g.course_id, g.schedule_id, g.selection_id, g.semester,
    60 + RAND() * 25, -- 补考成绩60-85分
    g.regular_score,
    g.midterm_score,
    60 + RAND() * 25, -- 补考期末成绩
    CASE 
        WHEN (60 + RAND() * 25) >= 85 THEN 3.3
        WHEN (60 + RAND() * 25) >= 82 THEN 3.0
        WHEN (60 + RAND() * 25) >= 78 THEN 2.7
        WHEN (60 + RAND() * 25) >= 75 THEN 2.3
        WHEN (60 + RAND() * 25) >= 72 THEN 2.0
        WHEN (60 + RAND() * 25) >= 68 THEN 1.7
        WHEN (60 + RAND() * 25) >= 64 THEN 1.3
        ELSE 1.0
    END,
    CASE 
        WHEN (60 + RAND() * 25) >= 85 THEN 'A-'
        WHEN (60 + RAND() * 25) >= 82 THEN 'B+'
        WHEN (60 + RAND() * 25) >= 78 THEN 'B'
        WHEN (60 + RAND() * 25) >= 75 THEN 'B-'
        WHEN (60 + RAND() * 25) >= 72 THEN 'C+'
        WHEN (60 + RAND() * 25) >= 68 THEN 'C'
        WHEN (60 + RAND() * 25) >= 64 THEN 'C-'
        ELSE 'D'
    END,
    1, -- 是补考
    g.teacher_id,
    1
FROM tb_grade g
WHERE g.level = 'F' AND g.is_makeup = 0 AND RAND() < 0.7; -- 70%的不及格学生参加补考

-- 显示插入结果
SELECT '成绩数据插入完成！' AS result;
SELECT '成绩记录总数:', COUNT(*) FROM tb_grade;
SELECT '补考记录数:', COUNT(*) FROM tb_grade WHERE is_makeup = 1;

-- 按等级统计成绩分布
SELECT level, COUNT(*) as grade_count, 
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_grade WHERE is_makeup = 0), 2) as percentage
FROM tb_grade 
WHERE is_makeup = 0
GROUP BY level 
ORDER BY FIELD(level, 'A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D', 'F');

-- 按学期统计成绩
SELECT semester, COUNT(*) as grade_count,
       ROUND(AVG(score), 2) as avg_score
FROM tb_grade 
WHERE is_makeup = 0
GROUP BY semester 
ORDER BY semester;

-- 统计各课程平均分（前10名）
SELECT c.course_name, c.course_code, 
       COUNT(g.id) as student_count,
       ROUND(AVG(g.score), 2) as avg_score,
       ROUND(MIN(g.score), 2) as min_score,
       ROUND(MAX(g.score), 2) as max_score
FROM tb_course c
INNER JOIN tb_grade g ON c.id = g.course_id
WHERE g.is_makeup = 0
GROUP BY c.id, c.course_name, c.course_code
HAVING student_count >= 10
ORDER BY avg_score DESC
LIMIT 10;
