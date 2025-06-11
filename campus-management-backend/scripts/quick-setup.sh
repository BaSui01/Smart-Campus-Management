#!/bin/bash

# 智慧校园管理系统 - 快速初始化脚本
# Quick Database Setup for Smart Campus System

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}====================================================="
echo -e "   智慧校园管理系统 - 快速初始化"
echo -e "   Quick Database Setup for Smart Campus System"
echo -e "=====================================================${NC}"
echo

# 快速设置（使用默认配置）
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_NAME=${DB_NAME:-"campus_management_db"}
DB_USER=${DB_USER:-"root"}

echo -e "${YELLOW}🚀 快速初始化模式${NC}"
echo
echo -e "${BLUE}使用默认配置:${NC}"
echo -e "  主机: ${DB_HOST}:${DB_PORT}"
echo -e "  数据库: ${DB_NAME}"
echo -e "  用户: ${DB_USER}"
echo

# 获取数据库密码
if [ -z "$DB_PASSWORD" ]; then
    echo -n "请输入数据库密码: "
    read -s DB_PASSWORD
    echo
fi

MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"
SQL_DIR="../src/main/resources/db"

echo
echo -e "${BLUE}🔍 测试数据库连接...${NC}"
if ! $MYSQL_CMD -e "SELECT 'Connection OK' as status;" 2>/dev/null; then
    echo -e "${RED}❌ 数据库连接失败！请检查配置。${NC}"
    exit 1
fi
echo -e "${GREEN}✅ 数据库连接成功！${NC}"

echo
echo -e "${YELLOW}📦 开始快速初始化...${NC}"
echo

# SQL脚本文件列表
sql_files=(
    "01_create_tables.sql"
    "02_insert_basic_data.sql"
    "03_insert_large_data.sql"
    "04_complete_all_tables.sql"
    "05_data_validation.sql"
    "06_data_analysis.sql"
)

# 执行所有SQL脚本
for file in "${sql_files[@]}"; do
    echo -e "${BLUE}📝 执行 ${file}...${NC}"
    
    if ! $MYSQL_CMD < "$SQL_DIR/$file" 2>/dev/null; then
        echo -e "${RED}❌ ${file} 执行失败！${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ ${file} 完成${NC}"
done

echo
echo -e "${GREEN}🎉 快速初始化完成！${NC}"
echo
echo -e "${YELLOW}📊 数据库统计:${NC}"
$MYSQL_CMD -e "
    USE $DB_NAME;
    SELECT '用户总数' as 项目, COUNT(*) as 数量 FROM tb_user WHERE deleted=0
    UNION SELECT '学生总数', COUNT(*) FROM tb_student WHERE deleted=0
    UNION SELECT '课程总数', COUNT(*) FROM tb_course WHERE deleted=0;
" 2>/dev/null

echo
echo -e "${YELLOW}🔑 默认管理员账号:${NC}"
echo -e "  用户名: admin"
echo -e "  密码: admin123"
echo
echo -e "${GREEN}✨ 系统已准备就绪！${NC}"