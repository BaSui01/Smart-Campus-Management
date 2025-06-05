-- ================================================
-- 校园管理系统用户和学生数据插入脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 插入大量用户和学生数据（500+条记录）
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 插入教师用户数据 (tb_user)
-- ================================================
-- 插入50个教师用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status) VALUES
-- 计算机学院教师
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher001@campus.edu.cn', '张明华', '13901234001', '男', 1),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher002@campus.edu.cn', '李秀英', '13901234002', '女', 1),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher003@campus.edu.cn', '王建国', '13901234003', '男', 1),
('teacher004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher004@campus.edu.cn', '陈美丽', '13901234004', '女', 1),
('teacher005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher005@campus.edu.cn', '刘强', '13901234005', '男', 1),
('teacher006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher006@campus.edu.cn', '杨丽娜', '13901234006', '女', 1),
('teacher007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher007@campus.edu.cn', '赵志强', '13901234007', '男', 1),
('teacher008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher008@campus.edu.cn', '孙小红', '13901234008', '女', 1),
('teacher009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher009@campus.edu.cn', '周天明', '13901234009', '男', 1),
('teacher010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher010@campus.edu.cn', '吴雪梅', '13901234010', '女', 1),
('teacher011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher011@campus.edu.cn', '郑大伟', '13901234011', 1),
('teacher012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher012@campus.edu.cn', '马文静', '13901234012', 1),
('teacher013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher013@campus.edu.cn', '朱永辉', '13901234013', 1),
('teacher014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher014@campus.edu.cn', '胡晓燕', '13901234014', 1),
('teacher015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher015@campus.edu.cn', '林国庆', '13901234015', 1),
('teacher016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher016@campus.edu.cn', '何春花', '13901234016', 1),
('teacher017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher017@campus.edu.cn', '高建军', '13901234017', 1),
('teacher018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher018@campus.edu.cn', '罗敏', '13901234018', 1),
('teacher019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher019@campus.edu.cn', '宋振华', '13901234019', 1),
('teacher020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher020@campus.edu.cn', '唐丽华', '13901234020', 1),
('teacher021', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher021@campus.edu.cn', '韩东升', '13901234021', 1),
('teacher022', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher022@campus.edu.cn', '冯小娟', '13901234022', 1),
('teacher023', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher023@campus.edu.cn', '邓文斌', '13901234023', 1),
('teacher024', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher024@campus.edu.cn', '曹慧敏', '13901234024', 1),
('teacher025', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher025@campus.edu.cn', '彭德华', '13901234025', 1),
('teacher026', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher026@campus.edu.cn', '曾玉兰', '13901234026', 1),
('teacher027', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher027@campus.edu.cn', '萧建平', '13901234027', 1),
('teacher028', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher028@campus.edu.cn', '范红梅', '13901234028', 1),
('teacher029', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher029@campus.edu.cn', '戴志勇', '13901234029', 1),
('teacher030', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher030@campus.edu.cn', '汪秀珍', '13901234030', 1),
('teacher031', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher031@campus.edu.cn', '蒋伟', '13901234031', 1),
('teacher032', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher032@campus.edu.cn', '薛莉莉', '13901234032', 1),
('teacher033', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher033@campus.edu.cn', '顾明亮', '13901234033', 1),
('teacher034', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher034@campus.edu.cn', '毛艳芳', '13901234034', 1),
('teacher035', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher035@campus.edu.cn', '谭锦华', '13901234035', 1),
('teacher036', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher036@campus.edu.cn', '廖春玲', '13901234036', 1),
('teacher037', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher037@campus.edu.cn', '余华东', '13901234037', 1),
('teacher038', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher038@campus.edu.cn', '温丽萍', '13901234038', 1),
('teacher039', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher039@campus.edu.cn', '莫建新', '13901234039', 1),
('teacher040', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher040@campus.edu.cn', '江雪花', '13901234040', 1),
('teacher041', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher041@campus.edu.cn', '段庆国', '13901234041', 1),
('teacher042', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher042@campus.edu.cn', '雷美玲', '13901234042', 1),
('teacher043', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher043@campus.edu.cn', '侯文龙', '13901234043', 1),
('teacher044', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher044@campus.edu.cn', '史春燕', '13901234044', 1),
('teacher045', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher045@campus.edu.cn', '钱志明', '13901234045', 1),
('teacher046', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher046@campus.edu.cn', '施丽君', '13901234046', 1),
('teacher047', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher047@campus.edu.cn', '严国强', '13901234047', 1),
('teacher048', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher048@campus.edu.cn', '尹秀云', '13901234048', 1),
('teacher049', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher049@campus.edu.cn', '姚建设', '13901234049', 1),
('teacher050', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', 'teacher050@campus.edu.cn', '卢春香', '13901234050', 1);

-- ================================================
-- 2. 为教师分配教师角色 (tb_user_role)
-- ================================================
INSERT INTO tb_user_role (user_id, role_id)
SELECT id, 5 FROM tb_user WHERE username LIKE 'teacher%';

-- ================================================
-- 3. 批量插入学生用户数据（500+条记录）
-- ================================================

-- 使用存储过程批量生成学生数据
DELIMITER //

DROP PROCEDURE IF EXISTS GenerateStudentData//

CREATE PROCEDURE GenerateStudentData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE year_suffix VARCHAR(4);
    DECLARE student_no VARCHAR(20);
    DECLARE username VARCHAR(50);
    DECLARE email VARCHAR(100);
    DECLARE real_name VARCHAR(50);
    DECLARE phone VARCHAR(20);
    DECLARE grade VARCHAR(20);
    DECLARE class_id INT;
    DECLARE enrollment_date DATE;
    DECLARE user_id BIGINT;
    
    -- 定义姓氏和名字数组
    DECLARE surnames TEXT DEFAULT '李,王,张,刘,陈,杨,黄,赵,周,吴,徐,孙,朱,马,胡,郭,林,何,高,梁,郑,罗,宋,谢,唐,韩,曹,许,邓,萧,冯,曾,程,蔡,彭,潘,袁,于,董,余,苏,叶,吕,魏,蒋,田,杜,丁,沈,姜,范,江,傅,钟,卢,汪,戴,崔,任,陆,廖,姚,方,金,邱,夏,谭,韦,贾,邹,石,熊,孟,秦,阎,薛,侯,雷,白,龙,段,郝,孔,邵,史,毛,常,万,顾,赖,武,康,贺,严,尹,钱,施,牛,洪,龚,汤,黎,易,常,武,乔,贺,赖,龚,文,庞,樊,兰,殷,施,陶,洪,翟,安,颜,倪,严,牛,温,芦,季,俞,章,鲁,葛,伍,韦,申,尤,毕,聂,丛,焦,向,柳,邢,路,岳,齐,沿,梅,莫,庄,辛,管,祝,左,涂,谷,祁,时,舒,耿,牟,卜,路,詹,关,苗,凌,费,纪,靳,盛,童,欧,甄,项,曲,成,游,阳,裴,席,卫,查,屈,鲍,位,覃,霍,翁,隋,植,甘,景,薄,单,包,司,柏,宁,柯,阮';
    DECLARE given_names TEXT DEFAULT '伟,芳,娜,秀英,敏,静,丽,强,磊,军,洋,勇,艳,杰,娟,涛,明,超,秀兰,霞,平,刚,桂英,燕,辉,鹏,玲,颖,红,君,华,建华,建国,志强,志明,东,亮,建军,春梅,金凤,玉兰,玉梅,玉华,慧,秀珍,惠,俊,凯,浩,宇,博,睿,哲,鸣,昊,瑞,亚,希,佳,雪,晨,悦,萍,婷,欣,怡,蕾,诗,梦,晴,语,雯,薇,琳,彤,妍,菲,嘉,思,雨,露,柔,媛,瑶,馨,子轩,子涵,子豪,子墨,子航,子恒,梓涵,梓豪,梓轩,梓航,梓恒,雨泽,雨轩,雨航,雨豪,雨恒,雨涵,浩然,浩宇,浩轩,浩天,浩博,浩瀚,志远,志豪,志轩,志航,志恒,志涵,俊杰,俊豪,俊轩,俊航,俊恒,俊涵,文轩,文豪,文航,文恒,文涵,文博,天宇,天轩,天豪,天航,天恒,天涵,宇轩,宇豪,宇航,宇恒,宇涵,宇博,晨曦,晨阳,晨光,晨辉,晨宇,晨轩,思远,思豪,思轩,思航,思恒,思涵,梦琪,梦瑶,梦涵,梦轩,梦航,梦恒,雅琪,雅瑶,雅涵,雅轩,雅航,雅恒,诗琪,诗瑶,诗涵,诗轩,诗航,诗恒,语琪,语瑶,语涵,语轩,语航,语恒,欣怡,欣悦,欣然,欣宜,欣妍,欣颜,心怡,心悦,心然,心宜,心妍,心颜,若汐,若涵,若轩,若航,若恒,若瑶,艺涵,艺轩,艺航,艺恒,艺瑶,艺琪,可馨,可欣,可心,可爱,可儿,可人,佳怡,佳悦,佳然,佳宜,佳妍,佳颜,美琪,美瑶,美涵,美轩,美航,美恒,智慧,智勇,智敏,智华,智强,智明,智杰,智超,智辉,智鹏,智轩,智航,智恒,智涵,智博,智宇,智天,智远,智豪,智文,安琪,安妮,安娜,安然,安宁,安静,安怡,安悦,安心,安康,安乐,安平,安和,安泰,安福,安祥,安顺,安达,安成,安全,博文,博雅,博学,博识,博闻,博览,博通,博达,博远,博深,博厚,博大,博爱,博仁,博义,博礼,博智,博信,博诚,博真';
    
    WHILE i <= 1500 DO
        -- 根据序号确定年级和班级（2021-2025级）
        CASE
            -- 2021级 (1-300)
            WHEN i <= 300 THEN
                SET year_suffix = '2021';
                SET grade = '2021级';
                SET enrollment_date = '2021-09-01';
                SET class_id = CASE
                    WHEN i <= 45 THEN 1   -- CS2021-1
                    WHEN i <= 88 THEN 2   -- CS2021-2
                    WHEN i <= 132 THEN 3  -- CS2021-3
                    WHEN i <= 174 THEN 10 -- SE2021-1
                    WHEN i <= 218 THEN 11 -- SE2021-2
                    WHEN i <= 261 THEN 12 -- SE2021-3
                    WHEN i <= 299 THEN 19 -- IS2021-1
                    ELSE 20               -- IS2021-2
                END;

            -- 2022级 (301-600)
            WHEN i <= 600 THEN
                SET year_suffix = '2022';
                SET grade = '2022级';
                SET enrollment_date = '2022-09-01';
                SET class_id = CASE
                    WHEN i <= 348 THEN 4  -- CS2022-1
                    WHEN i <= 394 THEN 5  -- CS2022-2
                    WHEN i <= 441 THEN 6  -- CS2022-3
                    WHEN i <= 488 THEN 13 -- SE2022-1
                    WHEN i <= 533 THEN 14 -- SE2022-2
                    WHEN i <= 579 THEN 15 -- SE2022-3
                    ELSE 21               -- IS2022-1
                END;

            -- 2023级 (601-900)
            WHEN i <= 900 THEN
                SET year_suffix = '2023';
                SET grade = '2023级';
                SET enrollment_date = '2023-09-01';
                SET class_id = CASE
                    WHEN i <= 650 THEN 7  -- CS2023-1
                    WHEN i <= 699 THEN 8  -- CS2023-2
                    WHEN i <= 747 THEN 9  -- CS2023-3
                    WHEN i <= 795 THEN 16 -- SE2023-1
                    WHEN i <= 841 THEN 17 -- SE2023-2
                    WHEN i <= 888 THEN 18 -- SE2023-3
                    ELSE 23               -- IS2023-1
                END;

            -- 2024级 (901-1200)
            WHEN i <= 1200 THEN
                SET year_suffix = '2024';
                SET grade = '2024级';
                SET enrollment_date = '2024-09-01';
                -- 使用动态计算班级ID（从班级表中获取2024级班级的起始ID）
                SET class_id = (SELECT MIN(id) FROM tb_class WHERE grade = '2024级') + ((i - 901) % 13);

            -- 2025级 (1201-1500)
            ELSE
                SET year_suffix = '2025';
                SET grade = '2025级';
                SET enrollment_date = '2025-09-01';
                -- 使用动态计算班级ID（从班级表中获取2025级班级的起始ID）
                SET class_id = (SELECT MIN(id) FROM tb_class WHERE grade = '2025级') + ((i - 1201) % 14);
        END CASE;
        
        -- 生成学号
        SET student_no = CONCAT(year_suffix, LPAD(i, 4, '0'));
        
        -- 生成用户名
        SET username = CONCAT('student', LPAD(i, 4, '0'));
        
        -- 生成邮箱
        SET email = CONCAT(username, '@student.campus.edu.cn');
        
        -- 生成随机姓名
        SET real_name = CONCAT(
            TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(surnames, ',', 1 + (i % 60)), ',', -1)),
            TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(given_names, ',', 1 + ((i * 7) % 80)), ',', -1))
        );
        
        -- 生成手机号
        SET phone = CONCAT('139', LPAD(FLOOR(RAND() * 100000000), 8, '0'));
        
        -- 插入用户数据（包含性别字段）
        INSERT INTO tb_user (username, password, email, real_name, phone, gender, status)
        VALUES (username, '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKi85KQ9e1.dMjL6VJNlZLWsS', email, real_name, phone,
                CASE
                    WHEN real_name LIKE '%秀%' OR real_name LIKE '%丽%' OR real_name LIKE '%娜%' OR real_name LIKE '%芳%' OR
                         real_name LIKE '%静%' OR real_name LIKE '%艳%' OR real_name LIKE '%娟%' OR real_name LIKE '%霞%' OR
                         real_name LIKE '%华%' OR real_name LIKE '%玲%' OR real_name LIKE '%颖%' OR real_name LIKE '%红%' OR
                         real_name LIKE '%慧%' OR real_name LIKE '%惠%' OR real_name LIKE '%雪%' OR real_name LIKE '%萍%' OR
                         real_name LIKE '%婷%' OR real_name LIKE '%欣%' OR real_name LIKE '%怡%' OR real_name LIKE '%蕾%' OR
                         real_name LIKE '%诗%' OR real_name LIKE '%梦%' OR real_name LIKE '%晴%' OR real_name LIKE '%语%' OR
                         real_name LIKE '%雯%' OR real_name LIKE '%薇%' OR real_name LIKE '%琳%' OR real_name LIKE '%彤%' OR
                         real_name LIKE '%妍%' OR real_name LIKE '%菲%' OR real_name LIKE '%嘉%' OR real_name LIKE '%思%' OR
                         real_name LIKE '%雨%' OR real_name LIKE '%露%' OR real_name LIKE '%柔%' OR real_name LIKE '%媛%' OR
                         real_name LIKE '%瑶%' OR real_name LIKE '%馨%' OR real_name LIKE '%琪%' OR real_name LIKE '%涵%' OR
                         real_name LIKE '%轩%' OR real_name LIKE '%航%' OR real_name LIKE '%恒%' OR real_name LIKE '%瑞%' OR
                         real_name LIKE '%希%' OR real_name LIKE '%佳%' OR real_name LIKE '%晨%' OR real_name LIKE '%悦%'
                    THEN '女'
                    WHEN real_name LIKE '%伟%' OR real_name LIKE '%强%' OR real_name LIKE '%磊%' OR real_name LIKE '%军%' OR
                         real_name LIKE '%洋%' OR real_name LIKE '%勇%' OR real_name LIKE '%杰%' OR real_name LIKE '%涛%' OR
                         real_name LIKE '%明%' OR real_name LIKE '%超%' OR real_name LIKE '%平%' OR real_name LIKE '%刚%' OR
                         real_name LIKE '%辉%' OR real_name LIKE '%鹏%' OR real_name LIKE '%君%' OR real_name LIKE '%东%' OR
                         real_name LIKE '%亮%' OR real_name LIKE '%凯%' OR real_name LIKE '%浩%' OR real_name LIKE '%宇%' OR
                         real_name LIKE '%博%' OR real_name LIKE '%睿%' OR real_name LIKE '%哲%' OR real_name LIKE '%鸣%' OR
                         real_name LIKE '%昊%' OR real_name LIKE '%俊%' OR real_name LIKE '%豪%' OR real_name LIKE '%然%' OR
                         real_name LIKE '%天%' OR real_name LIKE '%远%' OR real_name LIKE '%文%' OR real_name LIKE '%智%' OR
                         real_name LIKE '%安%' OR real_name LIKE '%成%' OR real_name LIKE '%国%' OR real_name LIKE '%建%' OR
                         real_name LIKE '%志%' OR real_name LIKE '%华%' OR real_name LIKE '%德%' OR real_name LIKE '%庆%'
                    THEN '男'
                    ELSE CASE (i % 3) WHEN 0 THEN '女' WHEN 1 THEN '男' ELSE '其他' END
                END, 1);
        
        -- 获取插入的用户ID
        SET user_id = LAST_INSERT_ID();
        
        -- 插入学生数据
        INSERT INTO tb_student (user_id, student_no, grade, class_id, enrollment_date, status) 
        VALUES (user_id, student_no, grade, class_id, enrollment_date, 1);
        
        -- 为学生分配学生角色
        INSERT INTO tb_user_role (user_id, role_id) VALUES (user_id, 6);
        
        SET i = i + 1;
    END WHILE;
END//

DELIMITER ;

-- 执行存储过程
CALL GenerateStudentData();

-- 删除存储过程
DROP PROCEDURE GenerateStudentData;

-- 显示插入结果
SELECT '用户和学生数据插入完成！' AS result;
SELECT '用户总数:', COUNT(*) FROM tb_user;
SELECT '学生总数:', COUNT(*) FROM tb_student;
SELECT '教师总数:', COUNT(*) FROM tb_user WHERE username LIKE 'teacher%';

-- 按年级统计学生数量
SELECT grade, COUNT(*) as student_count FROM tb_student GROUP BY grade ORDER BY grade;

-- 按班级统计学生数量
SELECT c.class_name, COUNT(s.id) as student_count
FROM tb_class c
LEFT JOIN tb_student s ON c.id = s.class_id
GROUP BY c.id, c.class_name
ORDER BY c.id;