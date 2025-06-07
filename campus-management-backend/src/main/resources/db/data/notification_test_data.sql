-- 插入测试通知数据
INSERT INTO notifications (title, content, type, target_audience, sender_name, status, priority, is_pinned, publish_time, read_count, create_time, update_time) VALUES
('系统维护通知', '系统将于本周六晚上22:00-24:00进行维护升级，期间可能无法正常访问，请提前做好相关准备。', 'SYSTEM', 'ALL', '系统管理员', 'PUBLISHED', 'HIGH', true, NOW(), 15, NOW(), NOW()),
('期末考试安排通知', '2024年春季学期期末考试将于6月10日-6月20日举行，请各位同学做好复习准备，具体考试时间安排请查看教务系统。', 'ACADEMIC', 'STUDENTS', '教务处', 'PUBLISHED', 'URGENT', true, NOW(), 89, NOW(), NOW()),
('学费缴费提醒', '2024年秋季学期学费缴费截止日期为8月31日，请尚未缴费的同学及时缴费，避免影响正常入学。', 'FINANCE', 'STUDENTS', '财务处', 'PUBLISHED', 'HIGH', false, NOW(), 45, NOW(), NOW()),
('教师节活动通知', '为庆祝第40个教师节，学校将举办系列庆祝活动，欢迎全校师生积极参与。活动详情请关注学校官网。', 'ACTIVITY', 'ALL', '学生处', 'PUBLISHED', 'NORMAL', false, NOW(), 23, NOW(), NOW()),
('新生入学指南', '2024级新生入学报到时间为9月1日-9月3日，请新生按时报到并携带相关材料。详细入学指南已发送至邮箱。', 'ACADEMIC', 'STUDENTS', '招生办', 'DRAFT', 'NORMAL', false, NULL, 0, NOW(), NOW()),
('图书馆开放时间调整', '自下周一起，图书馆开放时间调整为：周一至周五 8:00-22:00，周末 9:00-21:00。', 'SYSTEM', 'ALL', '图书馆', 'PUBLISHED', 'LOW', false, NOW(), 12, NOW(), NOW()),
('奖学金评选通知', '2023-2024学年奖学金评选工作即将开始，符合条件的同学请于本月底前提交申请材料。', 'ACADEMIC', 'STUDENTS', '学生处', 'PUBLISHED', 'NORMAL', false, NOW(), 67, NOW(), NOW()),
('校园网络升级通知', '为提升校园网络质量，将于本周末对网络设备进行升级，期间可能出现短暂断网，敬请谅解。', 'SYSTEM', 'ALL', '网络中心', 'PUBLISHED', 'NORMAL', false, NOW(), 8, NOW(), NOW());
