#!/bin/bash

# =====================================================
# 智慧校园管理平台 - 数据库一键部署脚本
# 版本: 1.0.0
# 描述: 自动执行所有数据库脚本
# =====================================================

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 数据库连接配置
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="root"
DB_PASSWORD=""
DB_NAME="campus_management"

# 脚本目录
SCRIPT_DIR="./scripts"
DEPLOY_DIR="./deploy"

# 日志文件
LOG_FILE="./deployment.log"

# 输出函数
print_banner() {
    echo -e "${CYAN}"
    echo "======================================================="
    echo "           智慧校园管理平台 - 数据库部署工具"
    echo "======================================================="
    echo -e "${NC}"
}

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1" | tee -a $LOG_FILE
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a $LOG_FILE
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a $LOG_FILE
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1" | tee -a $LOG_FILE
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a $LOG_FILE
}

# 显示帮助信息
show_help() {
    echo "使用方法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示帮助信息"
    echo "  -H, --host          数据库主机地址 (默认: localhost)"
    echo "  -P, --port          数据库端口 (默认: 3306)"
    echo "  -u, --user          数据库用户名 (默认: root)"
    echo "  -p, --password      数据库密码"
    echo "  -d, --database      数据库名称 (默认: campus_management)"
    echo "  --init-only         只执行初始化脚本(不包含测试数据)"
    echo "  --test-only         只执行测试数据脚本"
    echo "  --docker            使用Docker部署"
    echo "  --clean             清理并重新部署"
    echo ""
    echo "示例:"
    echo "  $0 -p mypassword                    # 使用密码部署"
    echo "  $0 --docker                         # 使用Docker部署"
    echo "  $0 --init-only -p mypassword        # 只部署基础数据"
    echo "  $0 --clean -p mypassword            # 清理重新部署"
}

# 解析命令行参数
parse_args() {
    INIT_ONLY=false
    TEST_ONLY=false
    USE_DOCKER=false
    CLEAN_DEPLOY=false

    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -H|--host)
                DB_HOST="$2"
                shift 2
                ;;
            -P|--port)
                DB_PORT="$2"
                shift 2
                ;;
            -u|--user)
                DB_USER="$2"
                shift 2
                ;;
            -p|--password)
                DB_PASSWORD="$2"
                shift 2
                ;;
            -d|--database)
                DB_NAME="$2"
                shift 2
                ;;
            --init-only)
                INIT_ONLY=true
                shift
                ;;
            --test-only)
                TEST_ONLY=true
                shift
                ;;
            --docker)
                USE_DOCKER=true
                shift
                ;;
            --clean)
                CLEAN_DEPLOY=true
                shift
                ;;
            *)
                print_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done

    # 如果没有提供密码，提示输入
    if [[ -z "$DB_PASSWORD" && "$USE_DOCKER" == false ]]; then
        echo -n "请输入数据库密码: "
        read -s DB_PASSWORD
        echo
    fi
}

# 检查依赖
check_dependencies() {
    print_step "检查依赖..."

    if [[ "$USE_DOCKER" == true ]]; then
        if ! command -v docker &> /dev/null; then
            print_error "Docker未安装，请先安装Docker"
            exit 1
        fi

        if ! command -v docker-compose &> /dev/null; then
            print_error "Docker Compose未安装，请先安装Docker Compose"
            exit 1
        fi
        print_info "Docker环境检查通过"
    else
        if ! command -v mysql &> /dev/null; then
            print_error "MySQL客户端未安装，请先安装MySQL"
            exit 1
        fi
        print_info "MySQL客户端检查通过"
    fi
}

# 检查MySQL连接
check_mysql_connection() {
    print_step "检查MySQL连接..."

    if [[ -n "$DB_PASSWORD" ]]; then
        mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD -e "SELECT 1;" > /dev/null 2>&1
    else
        mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -e "SELECT 1;" > /dev/null 2>&1
    fi

    if [ $? -eq 0 ]; then
        print_info "MySQL连接成功"
        return 0
    else
        print_error "MySQL连接失败，请检查配置"
        return 1
    fi
}

# 执行SQL脚本
execute_sql_script() {
    local script_file=$1
    local script_name=$(basename $script_file)

    print_step "执行脚本: $script_name"

    if [[ ! -f "$script_file" ]]; then
        print_error "脚本文件不存在: $script_file"
        return 1
    fi

    if [[ -n "$DB_PASSWORD" ]]; then
        mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD < $script_file >> $LOG_FILE 2>&1
    else
        mysql -h $DB_HOST -P $DB_PORT -u $DB_USER < $script_file >> $LOG_FILE 2>&1
    fi

    if [ $? -eq 0 ]; then
        print_success "脚本 $script_name 执行成功"
        return 0
    else
        print_error "脚本 $script_name 执行失败，详情请查看日志文件: $LOG_FILE"
        return 1
    fi
}

# Docker部署
deploy_with_docker() {
    print_step "使用Docker部署数据库环境..."

    cd $DEPLOY_DIR

    if [[ "$CLEAN_DEPLOY" == true ]]; then
        print_info "清理现有Docker容器和数据..."
        docker-compose down -v
        docker volume prune -f
    fi

    print_info "启动Docker容器..."
    docker-compose up -d

    if [ $? -eq 0 ]; then
        print_success "Docker容器启动成功"

        # 等待MySQL启动完成
        print_info "等待MySQL服务启动..."
        for i in {1..60}; do
            if docker-compose exec mysql mysqladmin ping -h localhost --silent; then
                print_success "MySQL服务已就绪"
                break
            fi
            echo -n "."
            sleep 1
        done

        print_info "数据库初始化脚本将自动执行..."
        print_info "可以通过以下方式访问:"
        print_info "  MySQL: localhost:3306"
        print_info "  Redis: localhost:6379"
        print_info "  Adminer: http://localhost:8080"
        print_info ""
        print_info "数据库连接信息:"
        print_info "  Host: localhost"
        print_info "  Port: 3306"
        print_info "  Database: campus_management"
        print_info "  Username: campus_user"
        print_info "  Password: campus_password"

    else
        print_error "Docker容器启动失败"
        return 1
    fi

    cd ..
}

# 本地部署
deploy_locally() {
    print_step "本地部署数据库..."

    # 检查MySQL连接
    check_mysql_connection
    if [ $? -ne 0 ]; then
        exit 1
    fi

    # 确定要执行的脚本
    scripts=()

    if [[ "$TEST_ONLY" == true ]]; then
        scripts=("06_test_data.sql")
    elif [[ "$INIT_ONLY" == true ]]; then
        scripts=(
            "01_create_database.sql"
            "02_create_tables.sql"
            "03_create_indexes.sql"
            "04_create_views.sql"
            "05_init_data.sql"
        )
    else
        scripts=(
            "01_create_database.sql"
            "02_create_tables.sql"
            "03_create_indexes.sql"
            "04_create_views.sql"
            "05_init_data.sql"
            "06_test_data.sql"
        )
    fi

    # 按顺序执行脚本
    for script in "${scripts[@]}"; do
        script_path="$SCRIPT_DIR/$script"
        execute_sql_script "$script_path"
        if [ $? -ne 0 ]; then
            print_error "部署失败，请检查错误信息"
            exit 1
        fi
    done
}

# 验证部署结果
verify_deployment() {
    print_step "验证部署结果..."

    if [[ "$USE_DOCKER" == true ]]; then
        # Docker环境验证
        DB_CMD="docker-compose -f $DEPLOY_DIR/docker-compose.yml exec mysql mysql -u campus_user -pcampus_password campus_management"
    else
        # 本地环境验证
        if [[ -n "$DB_PASSWORD" ]]; then
            DB_CMD="mysql -h $DB_HOST -P $DB_PORT -u campus_user -pcampus_password $DB_NAME"
        else
            DB_CMD="mysql -h $DB_HOST -P $DB_PORT -u campus_user -pcampus_password $DB_NAME"
        fi
    fi

    # 检查表数量
    table_count=$($DB_CMD -e "SHOW TABLES;" 2>/dev/null | wc -l)
    if [[ $table_count -gt 20 ]]; then
        print_success "数据库表创建验证通过 ($((table_count-1))张表)"
    else
        print_warning "数据库表数量异常: $table_count"
    fi

    # 检查用户数据
    user_count=$($DB_CMD -e "SELECT COUNT(*) FROM tb_user;" 2>/dev/null | tail -1)
    if [[ $user_count -gt 0 ]]; then
        print_success "用户数据验证通过 ($user_count个用户)"
    else
        print_warning "用户数据为空"
    fi

    print_success "部署验证完成"
}

# 清理函数
cleanup() {
    if [[ -f "$LOG_FILE" ]]; then
        print_info "部署日志已保存到: $LOG_FILE"
    fi
}

# 主函数
main() {
    # 设置错误处理
    set -e
    trap cleanup EXIT

    # 显示横幅
    print_banner

    # 解析参数
    parse_args "$@"

    # 初始化日志
    echo "=== 智慧校园管理平台数据库部署日志 ===" > $LOG_FILE
    echo "开始时间: $(date)" >> $LOG_FILE
    echo "配置信息: Host=$DB_HOST, Port=$DB_PORT, User=$DB_USER" >> $LOG_FILE
    echo "" >> $LOG_FILE

    # 检查依赖
    check_dependencies

    # 根据部署方式执行
    if [[ "$USE_DOCKER" == true ]]; then
        deploy_with_docker
    else
        deploy_locally
    fi

    # 验证部署结果
    verify_deployment

    print_success "数据库部署完成！"

    if [[ "$USE_DOCKER" == false ]]; then
        print_info "默认管理员账户:"
        print_info "  用户名: admin"
        print_info "  密码: admin123"
        print_info ""
        print_info "数据库连接信息:"
        print_info "  Host: $DB_HOST"
        print_info "  Port: $DB_PORT"
        print_info "  Database: $DB_NAME"
        print_info "  Username: campus_user"
        print_info "  Password: campus_password"
    fi
}

# 执行主函数
main "$@"
