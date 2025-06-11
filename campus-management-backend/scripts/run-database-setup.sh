#!/bin/bash

# =====================================================
# 智慧校园管理系统 - 数据库初始化脚本
# Smart Campus Management System - Database Setup
# =====================================================

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

print_success() {
    print_message "$GREEN" "✅ $1"
}

print_error() {
    print_message "$RED" "❌ $1"
}

print_info() {
    print_message "$BLUE" "ℹ️  $1"
}

print_warning() {
    print_message "$YELLOW" "⚠️  $1"
}

# 脚本开始
clear
print_message "$BLUE" "====================================================="
print_message "$BLUE" "   智慧校园管理系统 - 数据库初始化脚本"
print_message "$BLUE" "   Smart Campus Management System - Database Setup"
print_message "$BLUE" "====================================================="
echo

# 设置变量
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_NAME=${DB_NAME:-"campus_management_db"}
DB_USER=${DB_USER:-"root"}

# 获取数据库密码
if [ -z "$DB_PASSWORD" ]; then
    echo -n "请输入数据库密码 (Enter database password): "
    read -s DB_PASSWORD
    echo
fi

MYSQL_CMD="mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASSWORD}"
SQL_DIR="../src/main/resources/db"

echo
print_info "数据库连接信息:"
print_info "  主机: ${DB_HOST}:${DB_PORT}"
print_info "  用户: ${DB_USER}"
print_info "  数据库: ${DB_NAME}"
echo

# 检查MySQL客户端是否存在
if ! command -v mysql &> /dev/null; then
    print_error "MySQL客户端未找到，请确保已安装MySQL客户端！"
    exit 1
fi

# 检查SQL文件是否存在
if [ ! -d "$SQL_DIR" ]; then
    print_error "SQL文件目录不存在: $SQL_DIR"
    exit 1
fi

sql_files=(
    "01_create_tables.sql"
    "02_insert_basic_data.sql"
    "03_insert_large_data.sql"
    "04_complete_all_tables.sql"
    "05_data_validation.sql"
    "06_data_analysis.sql"
)

for file in "${sql_files[@]}"; do
    if [ ! -f "$SQL_DIR/$file" ]; then
        print_error "SQL文件不存在: $SQL_DIR/$file"
        exit 1
    fi
done

# 测试数据库连接
print_info "[1/7] 测试数据库连接..."
if ! $MYSQL_CMD -e "SELECT 'Database connection successful!' as status;" 2>/dev/null; then
    print_error "数据库连接失败，请检查连接信息！"
    exit 1
fi
print_success "数据库连接成功！"
echo

# 执行SQL脚本
execute_sql() {
    local step=$1
    local description=$2
    local file=$3
    
    print_info "[$step/7] $description..."
    
    if ! $MYSQL_CMD < "$SQL_DIR/$file" 2>/dev/null; then
        print_error "$description失败！"
        exit 1
    fi
    
    print_success "$description完成"
    echo
}

# 执行各个步骤
execute_sql "2" "创建数据库表结构" "01_create_tables.sql"
execute_sql "3" "插入基础数据" "02_insert_basic_data.sql"
execute_sql "4" "生成大规模测试数据" "03_insert_large_data.sql"
execute_sql "5" "补充完整表数据" "04_complete_all_tables.sql"
execute_sql "6" "执行数据验证" "05_data_validation.sql"
execute_sql "7" "执行数据分析" "06_data_analysis.sql"

# 完成提示
print_message "$GREEN" "====================================================="
print_message "$GREEN" "         🎉 数据库初始化完成！"
print_message "$GREEN" "====================================================="
echo
print_info "执行结果:"
print_success "1. 表结构创建 - 35张表"
print_success "2. 基础数据插入 - 角色权限、学院、教室等"
print_success "3. 大规模数据生成 - 15,000用户 + 完整业务数据"
print_success "4. 完整表数据补充 - 确保所有表都有数据"
print_success "5. 数据验证 - 完整性和一致性检查"
print_success "6. 数据分析 - 性能和质量分析"
echo
print_info "默认管理员账号:"
print_info "  用户名: admin"
print_info "  密码: admin123"
echo
print_success "系统现在可以正常使用了！"
print_message "$GREEN" "====================================================="

# 询问是否查看数据库状态
echo
read -p "是否查看数据库状态？(y/N): " show_status
if [[ $show_status =~ ^[Yy]$ ]]; then
    echo
    print_info "数据库状态概览:"
    $MYSQL_CMD -e "
        USE $DB_NAME;
        SELECT 
            '用户总数' as '统计项目',
            COUNT(*) as '数量'
        FROM tb_user WHERE deleted = 0
        UNION ALL
        SELECT 
            '学生总数' as '统计项目',
            COUNT(*) as '数量'
        FROM tb_student WHERE deleted = 0
        UNION ALL
        SELECT 
            '课程总数' as '统计项目',
            COUNT(*) as '数量'
        FROM tb_course WHERE deleted = 0
        UNION ALL
        SELECT 
            '班级总数' as '统计项目',
            COUNT(*) as '数量'
        FROM tb_class WHERE deleted = 0;
    " 2>/dev/null
fi

echo
print_success "数据库初始化脚本执行完毕！"