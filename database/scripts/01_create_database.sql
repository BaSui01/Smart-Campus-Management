-- =====================================================
-- 智慧校园管理平台 - 数据库创建脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-04
-- 描述: 创建数据库、用户和基本配置
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
DROP DATABASE IF EXISTS campus_management;
CREATE DATABASE campus_management
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_management;

-- 设置时区
SET time_zone = '+08:00';

-- 创建数据库用户
DROP USER IF EXISTS 'campus_user'@'%';
CREATE USER 'campus_user'@'%' IDENTIFIED BY 'campus_password';

-- 授权
GRANT SELECT, INSERT, UPDATE, DELETE ON campus_management.* TO 'campus_user'@'%';
FLUSH PRIVILEGES;

-- 显示创建结果
SELECT '数据库创建成功' AS message;
SHOW DATABASES LIKE 'campus_management';
SELECT USER() AS current_user_info;
