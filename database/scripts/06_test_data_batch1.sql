-- =====================================================
-- 智慧校园管理平台 - 基础测试数据脚本 (第1批)
-- 版本: 2.0.0
-- 创建时间: 2025-01-27
-- 描述: 插入基础用户数据 - 教师和班级数据
-- =====================================================

USE campus_management;
SET FOREIGN_KEY_CHECKS = 0;

-- 清空现有测试数据（保留管理员）
DELETE FROM tb_user WHERE id > 1;

-- ===========================================
-- 批量生成教师用户数据 (50人)
-- ===========================================

-- 直接插入教师用户数据
INSERT INTO tb_user (username, password, email, real_name, phone, status, last_login_time) VALUES
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher001@campus.edu', '张伟', '13800000001', 1, '2024-01-15 09:30:00'),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher002@campus.edu', '李明', '13800000002', 1, '2024-01-15 10:15:00'),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher003@campus.edu', '王磊', '13800000003', 1, '2024-01-15 11:20:00'),
('teacher004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher004@campus.edu', '陈慧', '13800000004', 1, '2024-01-15 14:30:00'),
('teacher005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher005@campus.edu', '赵强', '13800000005', 1, '2024-01-15 15:45:00'),
('teacher006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher006@campus.edu', '刘燕', '13800000006', 1, '2024-01-15 16:20:00'),
('teacher007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher007@campus.edu', '孙浩', '13800000007', 1, '2024-01-16 08:30:00'),
('teacher008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher008@campus.edu', '周美', '13800000008', 1, '2024-01-16 09:15:00'),
('teacher009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher009@campus.edu', '吴斌', '13800000009', 1, '2024-01-16 10:45:00'),
('teacher010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher010@campus.edu', '徐玲', '13800000010', 1, '2024-01-16 11:30:00'),
('teacher011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher011@campus.edu', '马军', '13800000011', 1, '2024-01-16 13:20:00'),
('teacher012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher012@campus.edu', '冯霞', '13800000012', 1, '2024-01-16 14:15:00'),
('teacher013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher013@campus.edu', '郭涛', '13800000013', 1, '2024-01-16 15:40:00'),
('teacher014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher014@campus.edu', '何娜', '13800000014', 1, '2024-01-16 16:25:00'),
('teacher015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher015@campus.edu', '罗刚', '13800000015', 1, '2024-01-17 08:45:00'),
('teacher016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher016@campus.edu', '宋丽', '13800000016', 1, '2024-01-17 09:30:00'),
('teacher017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher017@campus.edu', '唐宇', '13800000017', 1, '2024-01-17 10:20:00'),
('teacher018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher018@campus.edu', '邓芳', '13800000018', 1, '2024-01-17 11:15:00'),
('teacher019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher019@campus.edu', '曹鹏', '13800000019', 1, '2024-01-17 13:45:00'),
('teacher020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher020@campus.edu', '韩静', '13800000020', 1, '2024-01-17 14:30:00'),
('teacher021', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher021@campus.edu', '钱伟', '13800000021', 1, '2024-01-17 15:20:00'),
('teacher022', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher022@campus.edu', '蒋敏', '13800000022', 1, '2024-01-17 16:10:00'),
('teacher023', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher023@campus.edu', '沈浩', '13800000023', 1, '2024-01-18 08:25:00'),
('teacher024', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher024@campus.edu', '于欣', '13800000024', 1, '2024-01-18 09:40:00'),
('teacher025', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher025@campus.edu', '陆晨', '13800000025', 1, '2024-01-18 10:35:00'),
('teacher026', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher026@campus.edu', '潘瑞', '13800000026', 1, '2024-01-18 11:20:00'),
('teacher027', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher027@campus.edu', '杜兰', '13800000027', 1, '2024-01-18 13:15:00'),
('teacher028', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher028@campus.edu', '戴峰', '13800000028', 1, '2024-01-18 14:45:00'),
('teacher029', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher029@campus.edu', '任慧', '13800000029', 1, '2024-01-18 15:30:00'),
('teacher030', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher030@campus.edu', '梁波', '13800000030', 1, '2024-01-18 16:25:00'),
('teacher031', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher031@campus.edu', '谢阳', '13800000031', 1, '2024-01-19 08:15:00'),
('teacher032', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher032@campus.edu', '孔美', '13800000032', 1, '2024-01-19 09:30:00'),
('teacher033', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher033@campus.edu', '白军', '13800000033', 1, '2024-01-19 10:45:00'),
('teacher034', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher034@campus.edu', '孟霞', '13800000034', 1, '2024-01-19 11:20:00'),
('teacher035', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher035@campus.edu', '石涛', '13800000035', 1, '2024-01-19 13:40:00'),
('teacher036', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher036@campus.edu', '万丽', '13800000036', 1, '2024-01-19 14:25:00'),
('teacher037', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher037@campus.edu', '顾鹏', '13800000037', 1, '2024-01-19 15:15:00'),
('teacher038', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher038@campus.edu', '严娜', '13800000038', 1, '2024-01-19 16:30:00'),
('teacher039', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher039@campus.edu', '傅刚', '13800000039', 1, '2024-01-20 08:20:00'),
('teacher040', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher040@campus.edu', '常慧', '13800000040', 1, '2024-01-20 09:35:00'),
('teacher041', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher041@campus.edu', '蔡伟', '13800000041', 1, '2024-01-20 10:15:00'),
('teacher042', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher042@campus.edu', '田敏', '13800000042', 1, '2024-01-20 11:40:00'),
('teacher043', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher043@campus.edu', '范浩', '13800000043', 1, '2024-01-20 13:25:00'),
('teacher044', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher044@campus.edu', '胡欣', '13800000044', 1, '2024-01-20 14:50:00'),
('teacher045', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher045@campus.edu', '林晨', '13800000045', 1, '2024-01-20 15:30:00'),
('teacher046', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher046@campus.edu', '高瑞', '13800000046', 1, '2024-01-20 16:20:00'),
('teacher047', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher047@campus.edu', '夏兰', '13800000047', 1, '2024-01-21 08:45:00'),
('teacher048', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher048@campus.edu', '董峰', '13800000048', 1, '2024-01-21 09:20:00'),
('teacher049', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher049@campus.edu', '方慧', '13800000049', 1, '2024-01-21 10:35:00'),
('teacher050', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'teacher050@campus.edu', '金波', '13800000050', 1, '2024-01-21 11:15:00');

-- 为教师用户分配角色
INSERT INTO tb_user_role (user_id, role_id)
SELECT id, 2 FROM tb_user WHERE username LIKE 'teacher%';

-- 生成教师详细信息
INSERT INTO tb_teacher (user_id, teacher_no, department, title, hire_date, status) VALUES
((SELECT id FROM tb_user WHERE username = 'teacher001'), 'T2024001', '计算机科学与技术学院', '副教授', '2020-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher002'), 'T2024002', '计算机科学与技术学院', '讲师', '2021-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher003'), 'T2024003', '计算机科学与技术学院', '助教', '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher004'), 'T2024004', '计算机科学与技术学院', '教授', '2018-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher005'), 'T2024005', '数学与应用数学学院', '副教授', '2019-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher006'), 'T2024006', '数学与应用数学学院', '讲师', '2020-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher007'), 'T2024007', '数学与应用数学学院', '助教', '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher008'), 'T2024008', '数学与应用数学学院', '教授', '2017-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher009'), 'T2024009', '英语语言文学学院', '副教授', '2019-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher010'), 'T2024010', '英语语言文学学院', '讲师', '2020-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher011'), 'T2024011', '英语语言文学学院', '助教', '2022-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher012'), 'T2024012', '英语语言文学学院', '教授', '2016-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher013'), 'T2024013', '物理学院', '副教授', '2018-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher014'), 'T2024014', '物理学院', '讲师', '2019-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher015'), 'T2024015', '物理学院', '助教', '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher016'), 'T2024016', '物理学院', '教授', '2015-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher017'), 'T2024017', '化学学院', '副教授', '2018-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher018'), 'T2024018', '化学学院', '讲师', '2020-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher019'), 'T2024019', '化学学院', '助教', '2022-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher020'), 'T2024020', '化学学院', '教授', '2014-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher021'), 'T2024021', '生物学院', '副教授', '2017-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher022'), 'T2024022', '生物学院', '讲师', '2019-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher023'), 'T2024023', '生物学院', '助教', '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher024'), 'T2024024', '生物学院', '教授', '2013-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher025'), 'T2024025', '经济管理学院', '副教授', '2017-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher026'), 'T2024026', '经济管理学院', '讲师', '2019-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher027'), 'T2024027', '经济管理学院', '助教', '2021-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher028'), 'T2024028', '经济管理学院', '教授', '2012-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher029'), 'T2024029', '机械工程学院', '副教授', '2016-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher030'), 'T2024030', '机械工程学院', '讲师', '2018-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher031'), 'T2024031', '机械工程学院', '助教', '2020-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher032'), 'T2024032', '机械工程学院', '教授', '2011-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher033'), 'T2024033', '电子信息学院', '副教授', '2016-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher034'), 'T2024034', '电子信息学院', '讲师', '2018-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher035'), 'T2024035', '电子信息学院', '助教', '2020-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher036'), 'T2024036', '电子信息学院', '教授', '2010-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher037'), 'T2024037', '土木工程学院', '副教授', '2015-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher038'), 'T2024038', '土木工程学院', '讲师', '2017-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher039'), 'T2024039', '土木工程学院', '助教', '2019-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher040'), 'T2024040', '土木工程学院', '教授', '2009-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher041'), 'T2024041', '计算机科学与技术学院', '副教授', '2015-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher042'), 'T2024042', '计算机科学与技术学院', '讲师', '2017-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher043'), 'T2024043', '计算机科学与技术学院', '助教', '2019-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher044'), 'T2024044', '计算机科学与技术学院', '教授', '2008-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher045'), 'T2024045', '数学与应用数学学院', '副教授', '2014-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher046'), 'T2024046', '数学与应用数学学院', '讲师', '2016-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher047'), 'T2024047', '数学与应用数学学院', '助教', '2018-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher048'), 'T2024048', '数学与应用数学学院', '教授', '2007-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher049'), 'T2024049', '英语语言文学学院', '副教授', '2014-03-01', 1),
((SELECT id FROM tb_user WHERE username = 'teacher050'), 'T2024050', '英语语言文学学院', '讲师', '2016-09-01', 1);

-- ===========================================
-- 批量生成班级数据 (20个班级)
-- ===========================================

INSERT INTO tb_class (class_name, class_no, grade, head_teacher_id, max_students, current_students, status) VALUES
('计算机科学2021级-1班', '2021001', '2021级', (SELECT id FROM tb_user WHERE username = 'teacher001'), 45, 0, 1),
('计算机科学2021级-2班', '2021002', '2021级', (SELECT id FROM tb_user WHERE username = 'teacher002'), 42, 0, 1),
('软件工程2021级-1班', '2021003', '2021级', (SELECT id FROM tb_user WHERE username = 'teacher003'), 48, 0, 1),
('软件工程2021级-2班', '2021004', '2021级', (SELECT id FROM tb_user WHERE username = 'teacher004'), 46, 0, 1),
('数学2021级-1班', '2021005', '2021级', (SELECT id FROM tb_user WHERE username = 'teacher005'), 40, 0, 1),
('计算机科学2022级-1班', '2022001', '2022级', (SELECT id FROM tb_user WHERE username = 'teacher006'), 44, 0, 1),
('计算机科学2022级-2班', '2022002', '2022级', (SELECT id FROM tb_user WHERE username = 'teacher007'), 43, 0, 1),
('软件工程2022级-1班', '2022003', '2022级', (SELECT id FROM tb_user WHERE username = 'teacher008'), 47, 0, 1),
('软件工程2022级-2班', '2022004', '2022级', (SELECT id FROM tb_user WHERE username = 'teacher009'), 45, 0, 1),
('数学2022级-1班', '2022005', '2022级', (SELECT id FROM tb_user WHERE username = 'teacher010'), 41, 0, 1),
('计算机科学2023级-1班', '2023001', '2023级', (SELECT id FROM tb_user WHERE username = 'teacher011'), 46, 0, 1),
('计算机科学2023级-2班', '2023002', '2023级', (SELECT id FROM tb_user WHERE username = 'teacher012'), 44, 0, 1),
('软件工程2023级-1班', '2023003', '2023级', (SELECT id FROM tb_user WHERE username = 'teacher013'), 48, 0, 1),
('软件工程2023级-2班', '2023004', '2023级', (SELECT id FROM tb_user WHERE username = 'teacher014'), 47, 0, 1),
('数学2023级-1班', '2023005', '2023级', (SELECT id FROM tb_user WHERE username = 'teacher015'), 42, 0, 1),
('计算机科学2024级-1班', '2024001', '2024级', (SELECT id FROM tb_user WHERE username = 'teacher016'), 45, 0, 1),
('计算机科学2024级-2班', '2024002', '2024级', (SELECT id FROM tb_user WHERE username = 'teacher017'), 43, 0, 1),
('软件工程2024级-1班', '2024003', '2024级', (SELECT id FROM tb_user WHERE username = 'teacher018'), 46, 0, 1),
('软件工程2024级-2班', '2024004', '2024级', (SELECT id FROM tb_user WHERE username = 'teacher019'), 44, 0, 1),
('数学2024级-1班', '2024005', '2024级', (SELECT id FROM tb_user WHERE username = 'teacher020'), 40, 0, 1);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================
-- 数据统计和验证
-- ===========================================

SELECT '=== 第1批基础测试数据生成完成 ===' AS message;

-- 显示各表的记录数量
SELECT 'tb_user' AS table_name, COUNT(*) AS record_count FROM tb_user
UNION ALL SELECT 'tb_teacher', COUNT(*) FROM tb_teacher
UNION ALL SELECT 'tb_class', COUNT(*) FROM tb_class
UNION ALL SELECT 'tb_user_role', COUNT(*) FROM tb_user_role
ORDER BY table_name;

-- 用户角色分布统计
SELECT '=== 用户角色分布 ===' AS info;
SELECT r.role_name AS 角色名称, COUNT(ur.user_id) AS 用户数量
FROM tb_role r
LEFT JOIN tb_user_role ur ON r.id = ur.role_id
GROUP BY r.id, r.role_name
ORDER BY COUNT(ur.user_id) DESC;

SELECT '=== 第1批数据生成完成，请继续执行第2批脚本 ===' AS final_message;
