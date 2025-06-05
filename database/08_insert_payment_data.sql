-- ================================================
-- 校园管理系统缴费记录数据插入脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 插入缴费记录数据（500+条记录）
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 插入缴费记录数据 (tb_payment_record)
-- ================================================

-- 直接使用批量INSERT语句生成缴费记录数据（优化版本）
-- 为了提高性能，我们将分批插入数据

-- 临时禁用外键检查和自动提交以提高性能
SET FOREIGN_KEY_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- 批量插入缴费记录
INSERT INTO tb_payment_record (
    student_id, fee_item_id, amount, payment_method,
    payment_time, transaction_no, operator_id, status
)
SELECT
    s.id as student_id,
    f.id as fee_item_id,
    f.amount,
    CASE (s.id + f.id) % 5
        WHEN 0 THEN '支付宝'
        WHEN 1 THEN '微信'
        WHEN 2 THEN '银行卡'
        WHEN 3 THEN '现金'
        ELSE '网银'
    END as payment_method,
    DATE_SUB(f.due_date, INTERVAL ((s.id + f.id) % 30) DAY) as payment_time,
    CONCAT(
        DATE_FORMAT(f.due_date, '%Y%m%d'),
        LPAD(s.id, 6, '0'),
        LPAD(f.id, 3, '0'),
        LPAD((s.id * f.id) % 10000, 4, '0')
    ) as transaction_no,
    3 as operator_id, -- 财务管理员ID
    CASE WHEN (s.id + f.id) % 20 = 0 THEN 2 ELSE 1 END as status -- 5%退费，95%成功
FROM tb_student s
CROSS JOIN tb_fee_item f
WHERE s.status = 1
  AND f.status = 1
  AND (f.applicable_grade = '全部年级' OR f.applicable_grade = s.grade)
  AND (s.id + f.id) % 10 < 9; -- 90%的学生会缴费

-- 提交事务
COMMIT;

-- 恢复设置
SET AUTOCOMMIT = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- 2. 生成部分逾期缴费记录
-- ================================================

-- 为部分学生生成逾期缴费记录（简化版本）
INSERT INTO tb_payment_record (
    student_id, fee_item_id, amount, payment_method,
    payment_time, transaction_no, operator_id, status, remarks
)
SELECT
    s.id,
    f.id,
    f.amount,
    CASE (s.id + f.id) % 3
        WHEN 0 THEN '支付宝'
        WHEN 1 THEN '微信'
        ELSE '银行卡'
    END,
    DATE_ADD(f.due_date, INTERVAL ((s.id + f.id) % 15) + 1 DAY), -- 逾期1-15天
    CONCAT(
        DATE_FORMAT(DATE_ADD(f.due_date, INTERVAL ((s.id + f.id) % 15) + 1 DAY), '%Y%m%d'),
        LPAD(s.id, 6, '0'),
        LPAD(f.id, 3, '0'),
        LPAD((s.id * f.id + 1000) % 10000, 4, '0')
    ),
    3, -- 财务管理员
    1, -- 已缴费
    '逾期缴费'
FROM tb_student s
CROSS JOIN tb_fee_item f
WHERE s.status = 1 AND f.status = 1
  AND (f.applicable_grade = '全部年级' OR f.applicable_grade = s.grade)
  AND NOT EXISTS (
      SELECT 1 FROM tb_payment_record pr
      WHERE pr.student_id = s.id AND pr.fee_item_id = f.id
  )
  AND (s.id + f.id) % 10 = 0; -- 10%的未缴费学生逾期缴费

-- 显示插入结果
SELECT '缴费记录数据插入完成！' AS result;
SELECT '缴费记录总数:', COUNT(*) FROM tb_payment_record;
SELECT '已缴费记录数:', COUNT(*) FROM tb_payment_record WHERE status = 1;
SELECT '已退费记录数:', COUNT(*) FROM tb_payment_record WHERE status = 2;

-- 按缴费方式统计
SELECT payment_method, COUNT(*) as payment_count,
       ROUND(SUM(amount), 2) as total_amount
FROM tb_payment_record 
WHERE status = 1
GROUP BY payment_method 
ORDER BY payment_count DESC;

-- 按缴费项目统计
SELECT f.item_name, f.fee_type, 
       COUNT(pr.id) as payment_count,
       ROUND(SUM(pr.amount), 2) as total_amount,
       ROUND(AVG(pr.amount), 2) as avg_amount
FROM tb_fee_item f
LEFT JOIN tb_payment_record pr ON f.id = pr.fee_item_id AND pr.status = 1
GROUP BY f.id, f.item_name, f.fee_type
ORDER BY total_amount DESC;

-- 按月份统计缴费情况
SELECT DATE_FORMAT(payment_time, '%Y-%m') as payment_month,
       COUNT(*) as payment_count,
       ROUND(SUM(amount), 2) as total_amount
FROM tb_payment_record 
WHERE status = 1
GROUP BY DATE_FORMAT(payment_time, '%Y-%m')
ORDER BY payment_month;

-- 统计缴费率
SELECT f.item_name,
       COUNT(DISTINCT s.id) as total_students,
       COUNT(DISTINCT pr.student_id) as paid_students,
       ROUND(COUNT(DISTINCT pr.student_id) * 100.0 / COUNT(DISTINCT s.id), 2) as payment_rate
FROM tb_fee_item f
CROSS JOIN tb_student s
LEFT JOIN tb_payment_record pr ON f.id = pr.fee_item_id AND s.id = pr.student_id AND pr.status = 1
WHERE f.status = 1 AND s.status = 1
  AND (f.applicable_grade = '全部年级' OR f.applicable_grade = s.grade)
GROUP BY f.id, f.item_name
ORDER BY payment_rate DESC;
