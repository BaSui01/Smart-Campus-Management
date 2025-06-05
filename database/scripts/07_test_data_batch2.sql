-- =====================================================
-- 智慧校园管理平台 - 学生测试数据脚本 (第2批)
-- 版本: 2.0.0
-- 创建时间: 2025-01-27
-- 描述: 插入学生用户数据和相关信息
-- =====================================================

USE campus_management;
SET FOREIGN_KEY_CHECKS = 0;

-- ===========================================
-- 批量生成学生用户数据 (200人)
-- ===========================================

-- 直接插入学生用户数据
INSERT INTO tb_user (username, password, email, real_name, phone, status, last_login_time) VALUES
('student0001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0001@stu.campus.edu', '张三', '13900000001', 1, '2024-01-15 09:30:00'),
('student0002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0002@stu.campus.edu', '李四', '13900000002', 1, '2024-01-15 10:15:00'),
('student0003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0003@stu.campus.edu', '王五', '13900000003', 1, '2024-01-15 11:20:00'),
('student0004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0004@stu.campus.edu', '赵六', '13900000004', 1, '2024-01-15 14:30:00'),
('student0005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0005@stu.campus.edu', '钱七', '13900000005', 1, '2024-01-15 15:45:00'),
('student0006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0006@stu.campus.edu', '孙八', '13900000006', 1, '2024-01-15 16:20:00'),
('student0007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0007@stu.campus.edu', '周九', '13900000007', 1, '2024-01-16 08:30:00'),
('student0008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0008@stu.campus.edu', '吴十', '13900000008', 1, '2024-01-16 09:15:00'),
('student0009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0009@stu.campus.edu', '郑一', '13900000009', 1, '2024-01-16 10:45:00'),
('student0010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0010@stu.campus.edu', '王二', '13900000010', 1, '2024-01-16 11:30:00'),
('student0011', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0011@stu.campus.edu', '李三', '13900000011', 1, '2024-01-16 13:20:00'),
('student0012', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0012@stu.campus.edu', '张四', '13900000012', 1, '2024-01-16 14:15:00'),
('student0013', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0013@stu.campus.edu', '刘五', '13900000013', 1, '2024-01-16 15:40:00'),
('student0014', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0014@stu.campus.edu', '陈六', '13900000014', 1, '2024-01-16 16:25:00'),
('student0015', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0015@stu.campus.edu', '杨七', '13900000015', 1, '2024-01-17 08:45:00'),
('student0016', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0016@stu.campus.edu', '黄八', '13900000016', 1, '2024-01-17 09:30:00'),
('student0017', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0017@stu.campus.edu', '赵九', '13900000017', 1, '2024-01-17 10:20:00'),
('student0018', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0018@stu.campus.edu', '周十', '13900000018', 1, '2024-01-17 11:15:00'),
('student0019', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0019@stu.campus.edu', '吴一', '13900000019', 1, '2024-01-17 13:45:00'),
('student0020', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0020@stu.campus.edu', '徐二', '13900000020', 1, '2024-01-17 14:30:00'),
('student0021', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0021@stu.campus.edu', '孙三', '13900000021', 1, '2024-01-17 15:20:00'),
('student0022', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0022@stu.campus.edu', '胡四', '13900000022', 1, '2024-01-17 16:10:00'),
('student0023', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0023@stu.campus.edu', '朱五', '13900000023', 1, '2024-01-18 08:25:00'),
('student0024', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0024@stu.campus.edu', '高六', '13900000024', 1, '2024-01-18 09:40:00'),
('student0025', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0025@stu.campus.edu', '林七', '13900000025', 1, '2024-01-18 10:35:00'),
('student0026', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0026@stu.campus.edu', '何八', '13900000026', 1, '2024-01-18 11:20:00'),
('student0027', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0027@stu.campus.edu', '郭九', '13900000027', 1, '2024-01-18 13:15:00'),
('student0028', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0028@stu.campus.edu', '马十', '13900000028', 1, '2024-01-18 14:45:00'),
('student0029', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0029@stu.campus.edu', '罗一', '13900000029', 1, '2024-01-18 15:30:00'),
('student0030', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0030@stu.campus.edu', '梁二', '13900000030', 1, '2024-01-18 16:25:00'),
('student0031', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0031@stu.campus.edu', '宋三', '13900000031', 1, '2024-01-19 08:15:00'),
('student0032', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0032@stu.campus.edu', '郑四', '13900000032', 1, '2024-01-19 09:30:00'),
('student0033', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0033@stu.campus.edu', '谢五', '13900000033', 1, '2024-01-19 10:45:00'),
('student0034', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0034@stu.campus.edu', '韩六', '13900000034', 1, '2024-01-19 11:20:00'),
('student0035', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0035@stu.campus.edu', '唐七', '13900000035', 1, '2024-01-19 13:40:00'),
('student0036', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0036@stu.campus.edu', '冯八', '13900000036', 1, '2024-01-19 14:25:00'),
('student0037', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0037@stu.campus.edu', '于九', '13900000037', 1, '2024-01-19 15:15:00'),
('student0038', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0038@stu.campus.edu', '董十', '13900000038', 1, '2024-01-19 16:30:00'),
('student0039', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0039@stu.campus.edu', '萧一', '13900000039', 1, '2024-01-20 08:20:00'),
('student0040', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0040@stu.campus.edu', '程二', '13900000040', 1, '2024-01-20 09:35:00'),
('student0041', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0041@stu.campus.edu', '曹三', '13900000041', 1, '2024-01-20 10:15:00'),
('student0042', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0042@stu.campus.edu', '袁四', '13900000042', 1, '2024-01-20 11:40:00'),
('student0043', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0043@stu.campus.edu', '邓五', '13900000043', 1, '2024-01-20 13:25:00'),
('student0044', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0044@stu.campus.edu', '许六', '13900000044', 1, '2024-01-20 14:50:00'),
('student0045', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0045@stu.campus.edu', '傅七', '13900000045', 1, '2024-01-20 15:30:00'),
('student0046', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0046@stu.campus.edu', '沈八', '13900000046', 1, '2024-01-20 16:20:00'),
('student0047', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0047@stu.campus.edu', '曾九', '13900000047', 1, '2024-01-21 08:45:00'),
('student0048', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0048@stu.campus.edu', '彭十', '13900000048', 1, '2024-01-21 09:20:00'),
('student0049', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0049@stu.campus.edu', '吕一', '13900000049', 1, '2024-01-21 10:35:00'),
('student0050', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0050@stu.campus.edu', '苏二', '13900000050', 1, '2024-01-21 11:15:00'),
-- 继续添加更多学生
('student0051', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0051@stu.campus.edu', '卢三', '13900000051', 1, '2024-01-21 12:15:00'),
('student0052', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0052@stu.campus.edu', '蒋四', '13900000052', 1, '2024-01-21 13:15:00'),
('student0053', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0053@stu.campus.edu', '蔡五', '13900000053', 1, '2024-01-21 14:15:00'),
('student0054', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0054@stu.campus.edu', '贾六', '13900000054', 1, '2024-01-21 15:15:00'),
('student0055', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0055@stu.campus.edu', '丁七', '13900000055', 1, '2024-01-21 16:15:00'),
('student0056', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0056@stu.campus.edu', '魏八', '13900000056', 1, '2024-01-22 08:15:00'),
('student0057', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0057@stu.campus.edu', '薛九', '13900000057', 1, '2024-01-22 09:15:00'),
('student0058', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0058@stu.campus.edu', '叶十', '13900000058', 1, '2024-01-22 10:15:00'),
('student0059', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0059@stu.campus.edu', '阎一', '13900000059', 1, '2024-01-22 11:15:00'),
('student0060', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0060@stu.campus.edu', '余二', '13900000060', 1, '2024-01-22 12:15:00'),
('student0061', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0061@stu.campus.edu', '潘三', '13900000061', 1, '2024-01-22 13:15:00'),
('student0062', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0062@stu.campus.edu', '杜四', '13900000062', 1, '2024-01-22 14:15:00'),
('student0063', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0063@stu.campus.edu', '戴五', '13900000063', 1, '2024-01-22 15:15:00'),
('student0064', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0064@stu.campus.edu', '夏六', '13900000064', 1, '2024-01-22 16:15:00'),
('student0065', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0065@stu.campus.edu', '钟七', '13900000065', 1, '2024-01-23 08:15:00'),
('student0066', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0066@stu.campus.edu', '汪八', '13900000066', 1, '2024-01-23 09:15:00'),
('student0067', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0067@stu.campus.edu', '田九', '13900000067', 1, '2024-01-23 10:15:00'),
('student0068', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0068@stu.campus.edu', '任十', '13900000068', 1, '2024-01-23 11:15:00'),
('student0069', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0069@stu.campus.edu', '姜一', '13900000069', 1, '2024-01-23 12:15:00'),
('student0070', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0070@stu.campus.edu', '范二', '13900000070', 1, '2024-01-23 13:15:00'),
('student0071', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0071@stu.campus.edu', '方三', '13900000071', 1, '2024-01-23 14:15:00'),
('student0072', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0072@stu.campus.edu', '石四', '13900000072', 1, '2024-01-23 15:15:00'),
('student0073', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0073@stu.campus.edu', '姚五', '13900000073', 1, '2024-01-23 16:15:00'),
('student0074', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0074@stu.campus.edu', '谭六', '13900000074', 1, '2024-01-24 08:15:00'),
('student0075', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0075@stu.campus.edu', '廖七', '13900000075', 1, '2024-01-24 09:15:00'),
('student0076', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0076@stu.campus.edu', '邹八', '13900000076', 1, '2024-01-24 10:15:00'),
('student0077', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0077@stu.campus.edu', '熊九', '13900000077', 1, '2024-01-24 11:15:00'),
('student0078', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0078@stu.campus.edu', '金十', '13900000078', 1, '2024-01-24 12:15:00'),
('student0079', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0079@stu.campus.edu', '陆一', '13900000079', 1, '2024-01-24 13:15:00'),
('student0080', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0080@stu.campus.edu', '郝二', '13900000080', 1, '2024-01-24 14:15:00'),
('student0081', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0081@stu.campus.edu', '孔三', '13900000081', 1, '2024-01-24 15:15:00'),
('student0082', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0082@stu.campus.edu', '白四', '13900000082', 1, '2024-01-24 16:15:00'),
('student0083', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0083@stu.campus.edu', '崔五', '13900000083', 1, '2024-01-25 08:15:00'),
('student0084', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0084@stu.campus.edu', '康六', '13900000084', 1, '2024-01-25 09:15:00'),
('student0085', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0085@stu.campus.edu', '毛七', '13900000085', 1, '2024-01-25 10:15:00'),
('student0086', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0086@stu.campus.edu', '邱八', '13900000086', 1, '2024-01-25 11:15:00'),
('student0087', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0087@stu.campus.edu', '秦九', '13900000087', 1, '2024-01-25 12:15:00'),
('student0088', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0088@stu.campus.edu', '江十', '13900000088', 1, '2024-01-25 13:15:00'),
('student0089', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0089@stu.campus.edu', '史一', '13900000089', 1, '2024-01-25 14:15:00'),
('student0090', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0090@stu.campus.edu', '顾二', '13900000090', 1, '2024-01-25 15:15:00'),
('student0091', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0091@stu.campus.edu', '侯三', '13900000091', 1, '2024-01-25 16:15:00'),
('student0092', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0092@stu.campus.edu', '邵四', '13900000092', 1, '2024-01-26 08:15:00'),
('student0093', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0093@stu.campus.edu', '孟五', '13900000093', 1, '2024-01-26 09:15:00'),
('student0094', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0094@stu.campus.edu', '龙六', '13900000094', 1, '2024-01-26 10:15:00'),
('student0095', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0095@stu.campus.edu', '万七', '13900000095', 1, '2024-01-26 11:15:00'),
('student0096', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0096@stu.campus.edu', '段八', '13900000096', 1, '2024-01-26 12:15:00'),
('student0097', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0097@stu.campus.edu', '雷九', '13900000097', 1, '2024-01-26 13:15:00'),
('student0098', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0098@stu.campus.edu', '钱十', '13900000098', 1, '2024-01-26 14:15:00'),
('student0099', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0099@stu.campus.edu', '汤一', '13900000099', 1, '2024-01-26 15:15:00'),
('student0100', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'student0100@stu.campus.edu', '尹二', '13900000100', 1, '2024-01-26 16:15:00');

-- 为学生用户分配角色
INSERT INTO tb_user_role (user_id, role_id)
SELECT id, 3 FROM tb_user WHERE username LIKE 'student%';

-- ===========================================
-- 生成学生详细信息
-- ===========================================

INSERT INTO tb_student (user_id, student_no, grade, class_id, enrollment_date, status) VALUES
-- 2021级学生 (20人)
((SELECT id FROM tb_user WHERE username = 'student0001'), '202100001', '2021级', 1, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0002'), '202100002', '2021级', 1, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0003'), '202100003', '2021级', 1, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0004'), '202100004', '2021级', 1, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0005'), '202100005', '2021级', 2, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0006'), '202100006', '2021级', 2, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0007'), '202100007', '2021级', 2, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0008'), '202100008', '2021级', 2, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0009'), '202100009', '2021级', 3, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0010'), '202100010', '2021级', 3, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0011'), '202100011', '2021级', 3, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0012'), '202100012', '2021级', 3, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0013'), '202100013', '2021级', 4, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0014'), '202100014', '2021级', 4, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0015'), '202100015', '2021级', 4, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0016'), '202100016', '2021级', 4, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0017'), '202100017', '2021级', 5, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0018'), '202100018', '2021级', 5, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0019'), '202100019', '2021级', 5, '2021-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0020'), '202100020', '2021级', 5, '2021-09-01', 1),
-- 2022级学生 (20人)
((SELECT id FROM tb_user WHERE username = 'student0021'), '202200001', '2022级', 6, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0022'), '202200002', '2022级', 6, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0023'), '202200003', '2022级', 6, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0024'), '202200004', '2022级', 6, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0025'), '202200005', '2022级', 7, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0026'), '202200006', '2022级', 7, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0027'), '202200007', '2022级', 7, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0028'), '202200008', '2022级', 7, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0029'), '202200009', '2022级', 8, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0030'), '202200010', '2022级', 8, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0031'), '202200011', '2022级', 8, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0032'), '202200012', '2022级', 8, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0033'), '202200013', '2022级', 9, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0034'), '202200014', '2022级', 9, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0035'), '202200015', '2022级', 9, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0036'), '202200016', '2022级', 9, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0037'), '202200017', '2022级', 10, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0038'), '202200018', '2022级', 10, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0039'), '202200019', '2022级', 10, '2022-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0040'), '202200020', '2022级', 10, '2022-09-01', 1),
-- 2023级学生 (30人)
((SELECT id FROM tb_user WHERE username = 'student0041'), '202300001', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0042'), '202300002', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0043'), '202300003', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0044'), '202300004', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0045'), '202300005', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0046'), '202300006', '2023级', 11, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0047'), '202300007', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0048'), '202300008', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0049'), '202300009', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0050'), '202300010', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0051'), '202300011', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0052'), '202300012', '2023级', 12, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0053'), '202300013', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0054'), '202300014', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0055'), '202300015', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0056'), '202300016', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0057'), '202300017', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0058'), '202300018', '2023级', 13, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0059'), '202300019', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0060'), '202300020', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0061'), '202300021', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0062'), '202300022', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0063'), '202300023', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0064'), '202300024', '2023级', 14, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0065'), '202300025', '2023级', 15, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0066'), '202300026', '2023级', 15, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0067'), '202300027', '2023级', 15, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0068'), '202300028', '2023级', 15, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0069'), '202300029', '2023级', 15, '2023-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0070'), '202300030', '2023级', 15, '2023-09-01', 1),
-- 2024级学生 (30人)
((SELECT id FROM tb_user WHERE username = 'student0071'), '202400001', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0072'), '202400002', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0073'), '202400003', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0074'), '202400004', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0075'), '202400005', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0076'), '202400006', '2024级', 16, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0077'), '202400007', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0078'), '202400008', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0079'), '202400009', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0080'), '202400010', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0081'), '202400011', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0082'), '202400012', '2024级', 17, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0083'), '202400013', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0084'), '202400014', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0085'), '202400015', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0086'), '202400016', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0087'), '202400017', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0088'), '202400018', '2024级', 18, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0089'), '202400019', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0090'), '202400020', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0091'), '202400021', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0092'), '202400022', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0093'), '202400023', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0094'), '202400024', '2024级', 19, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0095'), '202400025', '2024级', 20, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0096'), '202400026', '2024级', 20, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0097'), '202400027', '2024级', 20, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0098'), '202400028', '2024级', 20, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0099'), '202400029', '2024级', 20, '2024-09-01', 1),
((SELECT id FROM tb_user WHERE username = 'student0100'), '202400030', '2024级', 20, '2024-09-01', 1);

-- 更新班级学生数量
UPDATE tb_class c SET current_students = (
    SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id
);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================
-- 数据统计和验证
-- ===========================================

SELECT '=== 第2批学生测试数据生成完成 ===' AS message;

-- 显示各表的记录数量
SELECT 'tb_user' AS table_name, COUNT(*) AS record_count FROM tb_user
UNION ALL SELECT 'tb_student', COUNT(*) FROM tb_student
UNION ALL SELECT 'tb_class', COUNT(*) FROM tb_class
UNION ALL SELECT 'tb_user_role', COUNT(*) FROM tb_user_role
ORDER BY table_name;

-- 班级学生分布统计
SELECT '=== 班级学生分布 ===' AS info;
SELECT c.class_name AS 班级名称, c.current_students AS 当前学生数, c.max_students AS 最大容量,
       ROUND(c.current_students / c.max_students * 100, 2) AS 使用率
FROM tb_class c
ORDER BY c.current_students DESC;

SELECT '=== 第2批数据生成完成，请继续执行第3批脚本 ===' AS final_message;
