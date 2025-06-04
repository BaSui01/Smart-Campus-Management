#!/bin/bash

# =====================================================
# 智慧校园管理平台 - 数据库备份脚本
# 版本: 1.0.0
# 描述: 自动备份数据库
# =====================================================

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置信息
DB_HOST="localhost"
DB_PORT="3306"
DB_USER="campus_user"
DB_PASSWORD="campus_password"
DB_NAME="campus_management"

# 备份目录
BACKUP_DIR="./backups"
LOG_FILE="$BACKUP_DIR/backup.log"

# 备份保留天数
KEEP_DAYS=30

# 输出函数
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

# 显示帮助信息
show_help() {
    echo "使用方法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示帮助信息"
    echo "  -H, --host          数据库主机地址 (默认: localhost)"
    echo "  -P, --port          数据库端口 (默认: 3306)"
    echo "  -u, --user          数据库用户名 (默认: campus_user)"
    echo "  -p, --password      数据库密码 (默认: campus_password)"
    echo "  -d, --database      数据库名称 (默认: campus_management)"
    echo "  -o, --output        备份输出目录 (默认: ./backups)"
    echo "  --keep-days         备份保留天数 (默认: 30天)"
    echo "  --structure-only    只备份表结构"
    echo "  --data-only         只备份数据"
    echo "  --compress          压缩备份文件"
    echo "  --docker            从Docker容器备份"
    echo ""
    echo "示例:"
    echo "  $0                           # 完整备份"
    echo "  $0 --structure-only          # 只备份结构"
    echo "  $0 --compress                # 压缩备份"
    echo "  $0 --docker                  # 从Docker容器备份"
}

# 解析命令行参数
parse_args() {
    STRUCTURE_ONLY=false
    DATA_ONLY=false
    COMPRESS=false
    USE_DOCKER=false

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
            -o|--output)
                BACKUP_DIR="$2"
                shift 2
                ;;
            --keep-days)
                KEEP_DAYS="$2"
                shift 2
                ;;
            --structure-only)
                STRUCTURE_ONLY=true
                shift
                ;;
            --data-only)
                DATA_ONLY=true
                shift
                ;;
            --compress)
                COMPRESS=true
                shift
                ;;
            --docker)
                USE_DOCKER=true
                shift
                ;;
            *)
                print_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done
}

# 创建备份目录
create_backup_dir() {
    if [[ ! -d "$BACKUP_DIR" ]]; then
        mkdir -p "$BACKUP_DIR"
        print_info "创建备份目录: $BACKUP_DIR"
    fi
}

# 检查依赖
check_dependencies() {
    if [[ "$USE_DOCKER" == true ]]; then
        if ! command -v docker &> /dev/null; then
            print_error "Docker未安装"
            exit 1
        fi
    else
        if ! command -v mysqldump &> /dev/null; then
            print_error "mysqldump未安装"
            exit 1
        fi
    fi

    if [[ "$COMPRESS" == true ]]; then
        if ! command -v gzip &> /dev/null; then
            print_error "gzip未安装"
            exit 1
        fi
    fi
}

# 执行备份
perform_backup() {
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local backup_type=""
    local mysqldump_options=""

    # 确定备份类型和文件名
    if [[ "$STRUCTURE_ONLY" == true ]]; then
        backup_type="structure"
        mysqldump_options="--no-data"
    elif [[ "$DATA_ONLY" == true ]]; then
        backup_type="data"
        mysqldump_options="--no-create-info"
    else
        backup_type="full"
        mysqldump_options=""
    fi

    local backup_file="$BACKUP_DIR/${DB_NAME}_${backup_type}_${timestamp}.sql"

    print_step "开始备份数据库..."
    print_info "备份类型: $backup_type"
    print_info "备份文件: $backup_file"

    # 构建mysqldump命令
    local dump_cmd=""
    if [[ "$USE_DOCKER" == true ]]; then
        dump_cmd="docker exec campus_mysql mysqldump"
    else
        dump_cmd="mysqldump"
    fi

    # 添加连接参数
    if [[ "$USE_DOCKER" == false ]]; then
        dump_cmd="$dump_cmd -h $DB_HOST -P $DB_PORT"
    fi
    dump_cmd="$dump_cmd -u $DB_USER -p$DB_PASSWORD"

    # 添加mysqldump选项
    dump_cmd="$dump_cmd --single-transaction --routines --triggers --events"
    dump_cmd="$dump_cmd --set-gtid-purged=OFF --default-character-set=utf8mb4"
    dump_cmd="$dump_cmd $mysqldump_options $DB_NAME"

    # 执行备份
    if [[ "$COMPRESS" == true ]]; then
        backup_file="${backup_file}.gz"
        print_info "启用压缩备份"
        eval "$dump_cmd" | gzip > "$backup_file"
    else
        eval "$dump_cmd" > "$backup_file"
    fi

    if [[ $? -eq 0 ]]; then
        local file_size=$(du -h "$backup_file" | cut -f1)
        print_info "备份完成，文件大小: $file_size"

        # 验证备份文件
        if [[ "$COMPRESS" == true ]]; then
            if gzip -t "$backup_file" 2>/dev/null; then
                print_info "压缩文件验证通过"
            else
                print_error "压缩文件验证失败"
                return 1
            fi
        else
            if [[ -s "$backup_file" ]]; then
                print_info "备份文件验证通过"
            else
                print_error "备份文件为空"
                return 1
            fi
        fi

        return 0
    else
        print_error "备份失败"
        return 1
    fi
}

# 清理旧备份
cleanup_old_backups() {
    print_step "清理旧备份文件..."

    local deleted_count=0
    while IFS= read -r -d '' file; do
        rm "$file"
        deleted_count=$((deleted_count + 1))
        print_info "删除旧备份: $(basename "$file")"
    done < <(find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f -mtime +$KEEP_DAYS -print0 2>/dev/null)

    if [[ $deleted_count -eq 0 ]]; then
        print_info "没有需要清理的旧备份文件"
    else
        print_info "已清理 $deleted_count 个旧备份文件"
    fi
}

# 显示备份统计
show_backup_stats() {
    print_step "备份统计信息..."

    local total_backups=$(find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f 2>/dev/null | wc -l)
    local total_size=$(find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f -exec du -ch {} + 2>/dev/null | tail -n1 | cut -f1)

    print_info "备份文件总数: $total_backups"
    print_info "备份总大小: ${total_size:-0}"

    # 显示最近的备份文件
    print_info "最近的备份文件:"
    find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f -printf "%T@ %Tc %p\n" 2>/dev/null | \
    sort -nr | head -5 | while read timestamp date time file; do
        local size=$(du -h "$file" | cut -f1)
        print_info "  $(basename "$file") ($size) - $date $time"
    done
}

# 主函数
main() {
    echo -e "${BLUE}=======================================================${NC}"
    echo -e "${BLUE}           智慧校园管理平台 - 数据库备份工具${NC}"
    echo -e "${BLUE}=======================================================${NC}"
    echo ""

    # 解析参数
    parse_args "$@"

    # 创建备份目录
    create_backup_dir

    # 初始化日志
    echo "=== 数据库备份日志 ===" > $LOG_FILE
    echo "开始时间: $(date)" >> $LOG_FILE
    echo "配置信息: Host=$DB_HOST, Port=$DB_PORT, User=$DB_USER, Database=$DB_NAME" >> $LOG_FILE
    echo "" >> $LOG_FILE

    # 检查依赖
    check_dependencies

    # 执行备份
    if perform_backup; then
        print_info "备份成功完成"

        # 清理旧备份
        cleanup_old_backups

        # 显示统计信息
        show_backup_stats

        echo ""
        print_info "备份操作完成！"
    else
        print_error "备份操作失败！"
        exit 1
    fi

    print_info "日志文件: $LOG_FILE"
}

# 执行主函数
main "$@"
