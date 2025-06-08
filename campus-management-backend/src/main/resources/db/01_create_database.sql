-- =====================================================
-- Smart Campus Management System - 数据库创建脚本
-- =====================================================
-- 文件名: 01_create_database.sql
-- 描述: 创建智慧校园管理系统数据库
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除数据库（如果存在）
DROP DATABASE IF EXISTS campus_management_db;

-- 创建数据库 (智慧校园管理系统数据库)
CREATE DATABASE campus_management_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_management_db;

-- 显示创建结果
SELECT 
    SCHEMA_NAME as '数据库名称',
    DEFAULT_CHARACTER_SET_NAME as '字符集',
    DEFAULT_COLLATION_NAME as '排序规则'
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME = 'campus_management_db';

-- 创建数据库用户（可选）
-- CREATE USER IF NOT EXISTS 'campus_user'@'localhost' IDENTIFIED BY 'campus_password';
-- GRANT ALL PRIVILEGES ON campus_management_db.* TO 'campus_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 设置时区
SET time_zone = '+08:00';

-- 显示完成信息
SELECT '数据库 campus_management_db 创建成功！' as '执行结果';
