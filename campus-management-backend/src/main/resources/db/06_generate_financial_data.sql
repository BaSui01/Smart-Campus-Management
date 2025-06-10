-- =====================================================
-- Smart Campus Management System - 财务数据生成脚本
-- 文件: 06_generate_financial_data.sql
-- 描述: 生成缴费记录、家长关系和通知数据
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
-- 高性能批量生成家长学生关系
-- =====================================================

-- 为每个学生分配1-2个家长（父亲）
INSERT INTO tb_parent_student_relation (parent_id, student_id, relationship, is_primary, status, deleted)
SELECT
    parents.parent_id,
    s.id as student_id,
    '父亲' as relationship,
    1 as is_primary,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN (
    SELECT
        u.id as parent_id,
        ROW_NUMBER() OVER (ORDER BY u.id) as parent_row
    FROM tb_user u
    JOIN tb_user_role ur ON u.id = ur.user_id
    JOIN tb_role r ON ur.role_id = r.id
    WHERE r.role_key = 'ROLE_PARENT'
    AND u.deleted = 0 AND u.status = 1
) parents
WHERE s.deleted = 0 AND s.status = 1
AND parents.parent_row = ((s.id - 1) % 35000) + 1;  -- 循环分配家长

-- 为部分学生分配第二个家长（母亲）
INSERT INTO tb_parent_student_relation (parent_id, student_id, relationship, is_primary, status, deleted)
SELECT
    parents.parent_id,
    s.id as student_id,
    '母亲' as relationship,
    0 as is_primary,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN (
    SELECT
        u.id as parent_id,
        ROW_NUMBER() OVER (ORDER BY u.id) as parent_row
    FROM tb_user u
    JOIN tb_user_role ur ON u.id = ur.user_id
    JOIN tb_role r ON ur.role_id = r.id
    WHERE r.role_key = 'ROLE_PARENT'
    AND u.deleted = 0 AND u.status = 1
) parents
WHERE s.deleted = 0 AND s.status = 1
AND s.id % 3 = 0  -- 约1/3的学生有两个家长
AND parents.parent_row = ((s.id - 1) % 35000) + 17500  -- 使用不同的家长
AND NOT EXISTS (
    SELECT 1 FROM tb_parent_student_relation psr
    WHERE psr.student_id = s.id AND psr.parent_id = parents.parent_id AND psr.deleted = 0
);

-- =====================================================
-- 高性能批量生成缴费记录
-- =====================================================

-- 批量生成缴费记录（学生 × 费用项目）
INSERT INTO tb_payment_record (
    student_id, fee_item_id, payment_amount, payment_method,
    payment_time, payment_status, transaction_id, payment_channel,
    receipt_number, operator_id, status, deleted
)
SELECT
    s.id as student_id,
    f.id as fee_item_id,
    f.amount as payment_amount,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN  -- 90%已缴费
            CASE (s.id + f.id) % 5
                WHEN 0 THEN '银行转账'
                WHEN 1 THEN '支付宝'
                WHEN 2 THEN '微信支付'
                WHEN 3 THEN '现金'
                ELSE '银行卡'
            END
        ELSE NULL
    END as payment_method,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN
            DATE_ADD('2024-08-01', INTERVAL ((s.id + f.id) % 120) DAY)
        ELSE NULL
    END as payment_time,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN 'paid'
        ELSE 'pending'
    END as payment_status,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN
            CONCAT('TXN', DATE_FORMAT(DATE_ADD('2024-08-01', INTERVAL ((s.id + f.id) % 120) DAY), '%Y%m%d'), LPAD((s.id + f.id) % 999999, 6, '0'))
        ELSE NULL
    END as transaction_id,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN '线上支付'
        ELSE NULL
    END as payment_channel,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN
            CONCAT('RCP', 'TXN', DATE_FORMAT(DATE_ADD('2024-08-01', INTERVAL ((s.id + f.id) % 120) DAY), '%Y%m%d'), LPAD((s.id + f.id) % 999999, 6, '0'))
        ELSE NULL
    END as receipt_number,
    CASE
        WHEN (s.id + f.id) % 10 < 9 THEN 1
        ELSE NULL
    END as operator_id,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN tb_fee_item f
WHERE s.deleted = 0 AND s.status = 1
AND f.deleted = 0 AND f.status = 1;

-- =====================================================
-- 高性能批量生成通知数据
-- =====================================================

-- 创建通知标题临时表
DROP TEMPORARY TABLE IF EXISTS temp_notification_titles;
CREATE TEMPORARY TABLE temp_notification_titles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    notification_type VARCHAR(50),
    title VARCHAR(200)
);

INSERT INTO temp_notification_titles (notification_type, title) VALUES
-- 学术教务类通知（20项）
('academic', '期末考试安排通知'),('academic', '选课系统开放通知'),('academic', '成绩查询通知'),('academic', '学期课程表发布'),
('academic', '实习安排通知'),('academic', '毕业设计答辩通知'),('academic', '奖学金评选通知'),('academic', '学术讲座通知'),
('academic', '期中考试时间安排'),('academic', '新学期开学典礼通知'),('academic', '学业预警通知'),('academic', '转专业申请通知'),
('academic', '研究生招生宣讲会'),('academic', '本科生导师制实施通知'),('academic', '学术论文写作培训'),('academic', '创新创业大赛通知'),
('academic', '学科竞赛报名通知'),('academic', '暑期社会实践安排'),('academic', '交换生项目申请'),('academic', '毕业典礼安排通知'),

-- 缴费财务类通知（16项）
('payment', '学费缴费提醒'),('payment', '住宿费缴费通知'),('payment', '教材费缴费通知'),('payment', '实验费缴费提醒'),
('payment', '保险费缴费通知'),('payment', '体检费缴费通知'),('payment', '补考费缴费通知'),('payment', '重修费缴费通知'),
('payment', '奖学金发放通知'),('payment', '助学金申请通知'),('payment', '勤工助学岗位招聘'),('payment', '学费减免申请通知'),
('payment', '缴费截止日期提醒'),('payment', '退费办理通知'),('payment', '财务报销流程通知'),('payment', '银行卡绑定提醒'),

-- 考试相关通知（16项）
('exam', '期中考试安排'),('exam', '期末考试时间表'),('exam', '补考安排通知'),('exam', '重修考试通知'),
('exam', '英语四六级考试报名'),('exam', '计算机等级考试通知'),('exam', '专业资格考试通知'),('exam', '考试违纪处理通知'),
('exam', '考试诚信教育'),('exam', '考场规则宣讲'),('exam', '考试成绩复核申请'),('exam', '毕业综合考试安排'),
('exam', '研究生入学考试通知'),('exam', '教师资格证考试'),('exam', '托福雅思考试安排'),('exam', '专升本考试通知'),

-- 课程相关通知（16项）
('course', '新学期课程安排'),('course', '课程调整通知'),('course', '停课通知'),('course', '补课安排'),
('course', '实验课安排'),('course', '课程设计安排'),('course', '实习课程通知'),('course', '选修课开课通知'),
('course', '网络课程开放通知'),('course', '双语课程招生'),('course', '暑期课程安排'),('course', '重修课程安排'),
('course', '课程评估调查'),('course', '教学质量反馈'),('course', '课程退选通知'),('course', '跨校选课通知'),

-- 系统技术类通知（12项）
('system', '系统维护通知'),('system', '密码安全提醒'),('system', '账户安全通知'),('system', '系统升级公告'),
('system', '功能更新通知'),('system', '服务器维护通知'),('system', '网络故障通知'),('system', '系统使用指南'),
('system', '移动应用更新'),('system', '数据备份通知'),('system', '网络安全培训'),('system', '信息系统故障报告'),

-- 活动赛事类通知（14项）
('activity', '社团招新通知'),('activity', '文艺晚会筹备'),('activity', '体育比赛安排'),('activity', '志愿服务活动'),
('activity', '学生会选举通知'),('activity', '校园文化节活动'),('activity', '运动会报名通知'),('activity', '社会实践活动'),
('activity', '学术沙龙活动'),('activity', '校友返校活动'),('activity', '新生迎新活动'),('activity', '毕业生晚会'),
('activity', '国际文化交流节'),('activity', '创业大赛宣讲会'),

-- 生活服务类通知（18项）
('service', '宿舍调整通知'),('service', '食堂菜谱更新'),('service', '校医院开放时间'),('service', '图书馆开放通知'),
('service', '校车时刻表调整'),('service', '洗衣房服务通知'),('service', '快递代收点通知'),('service', '校园卡充值优惠'),
('service', '宿舍安全检查'),('service', '水电维修通知'),('service', '网络故障报修'),('service', '失物招领通知'),
('service', '校园环境整治'),('service', '垃圾分类宣传'),('service', '节能减排倡议'),('service', '消防安全演练'),
('service', '心理健康咨询'),('service', '就业指导服务'),

-- 管理制度类通知（12项）
('management', '学生手册更新'),('management', '校纪校规宣讲'),('management', '请假制度说明'),('management', '考勤管理规定'),
('management', '奖惩制度公布'),('management', '宿舍管理规定'),('management', '校园秩序维护'),('management', '学术诚信教育'),
('management', '知识产权保护'),('management', '隐私信息保护'),('management', '应急预案演练'),('management', '疫情防控措施'),

-- 招生就业类通知（10项）
('enrollment', '研究生招生简章'),('enrollment', '本科招生政策'),('enrollment', '专业介绍宣讲'),('enrollment', '校园开放日安排'),
('enrollment', '就业招聘会'),('enrollment', '实习岗位发布'),('enrollment', '升学指导讲座'),('enrollment', '职业规划培训'),
('enrollment', '企业宣讲会'),('enrollment', '毕业生档案办理'),

-- 国际交流类通知（8项）
('international', '留学项目宣讲'),('international', '国际会议通知'),('international', '外籍教师讲座'),('international', '语言交换活动'),
('international', '文化交流项目'),('international', '国际学生联谊'),('international', '海外实习项目'),('international', '签证办理指导'),

-- 健康安全类通知（12项）
('health', '体检安排通知'),('health', '疫苗接种通知'),('health', '健康知识讲座'),('health', '食品安全教育'),
('health', '运动健身指导'),('health', '心理健康周'),('health', '急救知识培训'),('health', '安全教育讲座'),
('health', '消防逃生演练'),('health', '交通安全宣传'),('health', '网络安全防护'),('health', '防诈骗宣传'),

-- 科研学术类通知（10项）
('research', '科研项目申报'),('research', '学术会议征文'),('research', '实验室开放日'),('research', '科研成果展示'),
('research', '学术期刊投稿'),('research', '专利申请指导'),('research', '科研伦理培训'),('research', '学术规范教育'),
('research', '创新项目孵化'),('research', '产学研合作'),

-- 图书档案类通知（8项）
('library', '图书馆新书推荐'),('library', '数据库使用培训'),('library', '借阅规则更新'),('library', '阅读推广活动'),
('library', '档案查询服务'),('library', '文献检索指导'),('library', '知识产权讲座'),('library', '学位论文提交');

-- 批量生成100条通知
INSERT INTO tb_notification (
    title, content, notification_type, priority_level,
    sender_id, target_type, send_time, expire_time,
    is_published, read_count, status, deleted
)
SELECT
    tnt.title,
    CONCAT('关于', tnt.title, '的详细说明：\n\n',
           '各位同学：\n\n',
           '根据学校安排，现将相关事项通知如下：\n\n',
           '1. 请各位同学认真阅读本通知内容；\n',
           '2. 按照要求及时完成相关事项；\n',
           '3. 如有疑问，请及时联系相关部门。\n\n',
           '特此通知。\n\n',
           '教务处\n',
           DATE_FORMAT(NOW(), '%Y年%m月%d日')) as content,
    tnt.notification_type,
    CASE
        WHEN (tnt.id % 10) = 0 THEN 'high'
        WHEN (tnt.id % 10) < 3 THEN 'low'
        ELSE 'normal'
    END as priority_level,
    admin_user.admin_id as sender_id,
    CASE (tnt.id % 4)
        WHEN 0 THEN 'all_students'
        WHEN 1 THEN 'grade_students'
        WHEN 2 THEN 'department_students'
        ELSE 'class_students'
    END as target_type,
    DATE_ADD('2024-09-01', INTERVAL (tnt.id % 150) DAY) as send_time,
    DATE_ADD(DATE_ADD('2024-09-01', INTERVAL (tnt.id % 150) DAY), INTERVAL 30 DAY) as expire_time,
    1 as is_published,
    (tnt.id * 37) % 1000 as read_count,  -- 伪随机阅读数
    1 as status,
    0 as deleted
FROM temp_notification_titles tnt
CROSS JOIN (
    SELECT u.id as admin_id
    FROM tb_user u
    JOIN tb_user_role ur ON u.id = ur.user_id
    JOIN tb_role r ON ur.role_id = r.id
    WHERE r.role_key = 'ROLE_ADMIN'
    AND u.deleted = 0 AND u.status = 1
    LIMIT 1
) admin_user
WHERE tnt.id <= 100;

-- =====================================================
-- 高性能批量生成考勤记录
-- =====================================================

-- 为选课记录批量生成考勤数据（限制数量以避免数据过多）
INSERT INTO tb_attendance (
    student_id, course_id, attendance_date, time_slot_id,
    attendance_status, check_in_time, location,
    device_info, recorded_by, status, deleted
)
SELECT
    cs.student_id,
    cs.course_id,
    DATE_ADD('2024-09-01', INTERVAL ((cs.student_id + cs.course_id + days.day_offset) % 100) DAY) as attendance_date,
    csch.time_slot_id,
    CASE
        WHEN ((cs.student_id + cs.course_id + days.day_offset) % 100) < 90 THEN 'present'  -- 90%出勤
        WHEN ((cs.student_id + cs.course_id + days.day_offset) % 100) < 95 THEN 'late'     -- 5%迟到
        WHEN ((cs.student_id + cs.course_id + days.day_offset) % 100) < 98 THEN 'leave'    -- 3%请假
        ELSE 'absent'  -- 2%缺勤
    END as attendance_status,
    CASE
        WHEN ((cs.student_id + cs.course_id + days.day_offset) % 100) < 90 THEN
            CONCAT(DATE_ADD('2024-09-01', INTERVAL ((cs.student_id + cs.course_id + days.day_offset) % 100) DAY), ' 08:00:00')
        WHEN ((cs.student_id + cs.course_id + days.day_offset) % 100) < 95 THEN
            CONCAT(DATE_ADD('2024-09-01', INTERVAL ((cs.student_id + cs.course_id + days.day_offset) % 100) DAY), ' 08:', LPAD(5 + ((cs.student_id + days.day_offset) % 25), 2, '0'), ':00')
        ELSE NULL
    END as check_in_time,
    CASE ((cs.student_id + cs.course_id) % 5)
        WHEN 0 THEN '教学楼A101'
        WHEN 1 THEN '教学楼A102'
        WHEN 2 THEN '教学楼B101'
        WHEN 3 THEN '实验楼C101'
        ELSE '图书馆D001'
    END as location,
    'Mobile App' as device_info,
    1 as recorded_by,
    1 as status,
    0 as deleted
FROM (
    SELECT student_id, course_id
    FROM tb_course_selection
    WHERE deleted = 0 AND status = 1
    LIMIT 5000  -- 限制选课记录数量
) cs
JOIN tb_course_schedule csch ON cs.course_id = csch.course_id AND csch.semester = '第一学期'
CROSS JOIN (
    SELECT 0 as day_offset UNION SELECT 7 UNION SELECT 14 UNION SELECT 21 UNION SELECT 28
    UNION SELECT 35 UNION SELECT 42 UNION SELECT 49 UNION SELECT 56 UNION SELECT 63
    UNION SELECT 70 UNION SELECT 77 UNION SELECT 84 UNION SELECT 91 UNION SELECT 98
) days  -- 生成15天的考勤记录
WHERE csch.deleted = 0;

-- =====================================================
-- 执行数据生成
-- =====================================================

START TRANSACTION;

SELECT '=== 开始生成财务和关联数据 ===' as '状态', NOW() as '时间';

-- 步骤1: 生成家长学生关系
SELECT '步骤1: 生成家长学生关系...' as '状态', NOW() as '时间';
-- 家长学生关系已在上面批量生成
SELECT '✓ 家长学生关系生成完成' as '状态', NOW() as '时间';

-- 步骤2: 生成缴费记录
SELECT '步骤2: 生成缴费记录...' as '状态', NOW() as '时间';
-- 缴费记录已在上面批量生成
SELECT '✓ 缴费记录生成完成' as '状态', NOW() as '时间';

-- 步骤3: 生成通知数据
SELECT '步骤3: 生成通知数据...' as '状态', NOW() as '时间';
-- 通知数据已在上面批量生成
SELECT '✓ 通知数据生成完成' as '状态', NOW() as '时间';

-- 步骤4: 生成考勤记录
SELECT '步骤4: 生成考勤记录...' as '状态', NOW() as '时间';
-- 考勤记录已在上面批量生成
SELECT '✓ 考勤记录生成完成' as '状态', NOW() as '时间';

-- 恢复设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

COMMIT;

-- 显示统计信息
SELECT '=== 财务和关联数据生成完成 ===' as '状态', NOW() as '时间';

SELECT 
    '家长学生关系' as '统计项目',
    COUNT(*) as '数量'
FROM tb_parent_student_relation WHERE deleted = 0
UNION ALL
SELECT 
    '缴费记录总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_payment_record WHERE deleted = 0
UNION ALL
SELECT 
    '通知总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_notification WHERE deleted = 0
UNION ALL
SELECT 
    '考勤记录总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_attendance WHERE deleted = 0;

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS temp_notification_titles;

SELECT '✓ 财务和关联数据生成完成' as '状态', NOW() as '时间';
