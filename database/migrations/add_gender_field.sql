-- 添加性别字段到用户表
-- 执行时间：2025-06-06
-- 描述：为tb_user表添加gender字段，用于存储用户性别信息

USE campus_management_db;

-- 添加性别字段
ALTER TABLE tb_user 
ADD COLUMN gender VARCHAR(10) DEFAULT NULL COMMENT '性别：男、女、其他' 
AFTER phone;

-- 为现有用户随机分配性别（用于测试数据）
UPDATE tb_user 
SET gender = CASE 
    WHEN id % 3 = 0 THEN '女'
    WHEN id % 3 = 1 THEN '男'
    ELSE '其他'
END
WHERE gender IS NULL;

-- 验证更新结果
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN gender = '男' THEN 1 END) as male_count,
    COUNT(CASE WHEN gender = '女' THEN 1 END) as female_count,
    COUNT(CASE WHEN gender = '其他' THEN 1 END) as other_count,
    COUNT(CASE WHEN gender IS NULL THEN 1 END) as null_count
FROM tb_user;

-- 显示表结构确认字段已添加
DESCRIBE tb_user;
