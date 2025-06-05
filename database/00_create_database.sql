-- ================================================
-- 校园管理系统数据库创建脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 创建数据库（如果不存在）
-- ================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS campus_management_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_management_db;

-- 显示数据库创建结果
SELECT 'Database campus_management_db created successfully or already exists' AS result;
SHOW DATABASES LIKE 'campus_management_db';