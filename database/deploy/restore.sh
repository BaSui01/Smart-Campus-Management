#!/bin/bash

# =====================================================
# 智慧校园管理平台 - 数据库恢复脚本
# 版本: 1.0.0
# 描述: 从备份文件恢复数据库
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
LOG_FILE="$BACKUP_DIR/restore.log"

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
    echo "使用方法: $0 [选项] <备份文件>"
    echo ""
    echo "选项:"
    echo "  -h, --help          显示帮助信息"
    echo "  -H, --host          数据库主机地址 (默认: localhost)"
    echo "  -P, --port          数据库端口 (默认: 3306)"
    echo "  -u, --user          数据库用户名 (默认: campus_user)"
    echo "  -p, --password      数据库密码 (默认: campus_password)"
    echo "  -d, --database      数据库名称 (默认: campus_management)"
    echo "  -f, --file          备份文件路径"
    echo "  --list              列出可用的备份文件"
    echo "  --latest            使用最新的备份文件"
    echo "  --docker            恢复到Docker容器"
    echo "  --force             强制恢复（不询问确认）"
    echo "  --drop-first        恢复前先删除数据库"
    echo ""
    echo "示例:"
    echo "  $0 -f backup.sql                    # 从指定文件恢复"
    echo "  $0 --latest                         # 使用最新备份恢复"
    echo "  $0 --list                           # 列出备份文件"
    echo "  $0 --docker --latest                # 恢复到Docker容器"
}

# 解析命令行参数
parse_args() {
    BACKUP_FILE=""
    USE_LATEST=false
    LIST_BACKUPS=false
    USE_DOCKER=false
    FORCE=false
    DROP_FIRST=false

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
            -f|--file)
                BACKUP_FILE="$2"
                shift 2
                ;;
            --latest)
                USE_LATEST=true
                shift
                ;;
            --list)
                LIST_BACKUPS=true
                shift
                ;;
            --docker)
                USE_DOCKER=true
                shift
                ;;
            --force)
                FORCE=true
                shift
                ;;
            --drop-first)
                DROP_FIRST=true
                shift
                ;;
            *)
                if [[ -z "$BACKUP_FILE" && -f "$1" ]]; then
                    BACKUP_FILE="$1"
                else
                    print_error "未知参数: $1"
                    show_help
                    exit 1
                fi
                shift
                ;;
        esac
    done
}

# 列出可用的备份文件
list_backup_files() {
    print_step "可用的备份文件:"
    echo ""

    if [[ ! -d "$BACKUP_DIR" ]]; then
        print_warning "备份目录不存在: $BACKUP_DIR"
        return 1
    fi

    local backup_files=($(find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f 2>/dev/null | sort -r))

    if [[ ${#backup_files[@]} -eq 0 ]]; then
        print_warning "没有找到备份文件"
        return 1
    fi

    local count=0
    for file in "${backup_files[@]}"; do
        count=$((count + 1))
        local basename=$(basename "$file")
        local size=$(du -h "$file" | cut -f1)
        local date=$(stat -c '%y' "$file" 2>/dev/null | cut -d' ' -f1,2 | cut -d'.' -f1)

        # 解析备份类型
        local backup_type="unknown"
        if [[ $basename == *"_full_"* ]]; then
            backup_type="完整备份"
        elif [[ $basename == *"_structure_"* ]]; then
            backup_type="结构备份"
        elif [[ $basename == *"_data_"* ]]; then
            backup_type="数据备份"
        fi

        echo -e "${BLUE}[$count]${NC} $basename"
        echo "    类型: $backup_type"
        echo "    大小: $size"
        echo "    时间: $date"
        echo ""
    done

    return 0
}

# 获取最新的备份文件
get_latest_backup() {
    local latest_file=$(find "$BACKUP_DIR" -name "${DB_NAME}_*.sql*" -type f 2>/dev/null | sort -r | head -1)

    if [[ -n "$latest_file" && -f "$latest_file" ]]; then
        echo "$latest_file"
        return 0
    else
        return 1
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
        if ! command -v mysql &> /dev/null; then
            print_error "mysql客户端未安装"
            exit 1
        fi
    fi

    # 检查备份文件是否为压缩文件
    if [[ "$BACKUP_FILE" == *.gz ]]; then
        if ! command -v gunzip &> /dev/null; then
            print_error "gunzip未安装，无法处理压缩文件"
            exit 1
        fi
    fi
}

# 验证备份文件
validate_backup_file() {
    if [[ ! -f "$BACKUP_FILE" ]]; then
        print_error "备份文件不存在: $BACKUP_FILE"
        return 1
    fi

    print_step "验证备份文件..."

    # 检查文件大小
    local file_size=$(stat -c%s "$BACKUP_FILE" 2>/dev/null || echo "0")
    if [[ $file_size -eq 0 ]]; then
        print_error "备份文件为空"
        return 1
    fi

    # 验证压缩文件
    if [[ "$BACKUP_FILE" == *.gz ]]; then
        if ! gzip -t "$BACKUP_FILE" 2>/dev/null; then
            print_error "压缩文件损坏"
            return 1
        fi
        print_info "压缩文件验证通过"
    fi

    # 简单验证SQL文件内容
    local first_line=""
    if [[ "$BACKUP_FILE" == *.gz ]]; then
        first_line=$(gunzip -c "$BACKUP_FILE" | head -1)
    else
        first_line=$(head -1 "$BACKUP_FILE")
    fi

    if [[ ! "$first_line" =~ ^(--|/\*|CREATE|DROP|USE|INSERT) ]]; then
        print_warning "备份文件可能不是有效的SQL文件"
    fi

    print_info "备份文件验证通过"
    local readable_size=$(du -h "$BACKUP_FILE" | cut -f1)
    print_info "文件大小: $readable_size"

    return 0
}

# 确认恢复操作
confirm_restore() {
    if [[ "$FORCE" == true ]]; then
        return 0
    fi

    echo ""
    print_warning "====== 数据库恢复确认 ======"
    echo -e "${YELLOW}目标数据库:${NC} $DB_NAME"
    echo -e "${YELLOW}目标主机:${NC} $DB_HOST:$DB_PORT"
    echo -e "${YELLOW}备份文件:${NC} $(basename "$BACKUP_FILE")"
    if [[ "$DROP_FIRST" == true ]]; then
        echo -e "${RED}警告: 将先删除现有数据库！${NC}"
    fi
    echo ""
    echo -e "${RED}注意: 此操作将覆盖现有数据，且不可逆！${NC}"
    echo ""

    read -p "确认要继续恢复操作吗？(yes/no): " confirm
    case $confirm in
        [Yy]es|[Yy]|YES)
            return 0
            ;;
        *)
            print_info "取消恢复操作"
            exit 0
            ;;
    esac
}

# 备份当前数据库
backup_current_database() {
    print_step "备份当前数据库..."

    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local current_backup="$BACKUP_DIR/${DB_NAME}_pre_restore_${timestamp}.sql"

    # 构建备份命令
    local backup_cmd=""
    if [[ "$USE_DOCKER" == true ]]; then
        backup_cmd="docker exec campus_mysql mysqldump -u $DB_USER -p$DB_PASSWORD --single-transaction --routines --triggers --events $DB_NAME"
    else
        backup_cmd="mysqldump -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD --single-transaction --routines --triggers --events $DB_NAME"
    fi

    eval "$backup_cmd" > "$current_backup" 2>/dev/null

    if [[ $? -eq 0 && -s "$current_backup" ]]; then
        local backup_size=$(du -h "$current_backup" | cut -f1)
        print_info "当前数据库已备份: $(basename "$current_backup") ($backup_size)"
    else
        print_warning "当前数据库备份失败或数据库为空"
        rm -f "$current_backup" 2>/dev/null
    fi
}

# 删除数据库
drop_database() {
    print_step "删除现有数据库..."

    local drop_cmd=""
    if [[ "$USE_DOCKER" == true ]]; then
        drop_cmd="docker exec campus_mysql mysql -u $DB_USER -p$DB_PASSWORD -e"
    else
        drop_cmd="mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD -e"
    fi

    eval "$drop_cmd \"DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\""

    if [[ $? -eq 0 ]]; then
        print_info "数据库重建完成"
    else
        print_error "数据库重建失败"
        return 1
    fi
}

# 执行恢复
perform_restore() {
    print_step "开始恢复数据库..."

    # 构建恢复命令
    local restore_cmd=""
    if [[ "$USE_DOCKER" == true ]]; then
        restore_cmd="docker exec -i campus_mysql mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME"
    else
        restore_cmd="mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME"
    fi

    # 执行恢复
    if [[ "$BACKUP_FILE" == *.gz ]]; then
        print_info "解压并恢复压缩备份文件..."
        gunzip -c "$BACKUP_FILE" | eval "$restore_cmd"
    else
        print_info "恢复备份文件..."
        eval "$restore_cmd" < "$BACKUP_FILE"
    fi

    if [[ $? -eq 0 ]]; then
        print_info "数据库恢复成功"
        return 0
    else
        print_error "数据库恢复失败"
        return 1
    fi
}

# 验证恢复结果
verify_restore() {
    print_step "验证恢复结果..."

    # 构建查询命令
    local query_cmd=""
    if [[ "$USE_DOCKER" == true ]]; then
        query_cmd="docker exec campus_mysql mysql -u $DB_USER -p$DB_PASSWORD $DB_NAME -e"
    else
        query_cmd="mysql -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME -e"
    fi

    # 检查表数量
    local table_count=$(eval "$query_cmd \"SHOW TABLES;\"" 2>/dev/null | wc -l)
    if [[ $table_count -gt 1 ]]; then
        print_info "数据库表验证通过 ($((table_count-1))张表)"
    else
        print_warning "数据库表数量异常: $table_count"
    fi

    # 检查用户数据
    local user_count=$(eval "$query_cmd \"SELECT COUNT(*) FROM tb_user;\"" 2>/dev/null | tail -1)
    if [[ -n "$user_count" && "$user_count" -gt 0 ]]; then
        print_info "用户数据验证通过 ($user_count个用户)"
    else
        print_warning "用户数据验证失败或为空"
    fi

    print_info "恢复验证完成"
}

# 主函数
main() {
    echo -e "${BLUE}=======================================================${NC}"
    echo -e "${BLUE}           智慧校园管理平台 - 数据库恢复工具${NC}"
    echo -e "${BLUE}=======================================================${NC}"
    echo ""

    # 解析参数
    parse_args "$@"

    # 如果只是列出备份文件
    if [[ "$LIST_BACKUPS" == true ]]; then
        list_backup_files
        exit 0
    fi

    # 确定备份文件
    if [[ "$USE_LATEST" == true ]]; then
        BACKUP_FILE=$(get_latest_backup)
        if [[ $? -ne 0 || -z "$BACKUP_FILE" ]]; then
            print_error "没有找到可用的备份文件"
            exit 1
        fi
        print_info "使用最新备份文件: $(basename "$BACKUP_FILE")"
    elif [[ -z "$BACKUP_FILE" ]]; then
        print_error "请指定备份文件"
        echo ""
        show_help
        exit 1
    fi

    # 创建日志目录
    mkdir -p "$BACKUP_DIR"

    # 初始化日志
    echo "=== 数据库恢复日志 ===" > $LOG_FILE
    echo "开始时间: $(date)" >> $LOG_FILE
    echo "配置信息: Host=$DB_HOST, Port=$DB_PORT, User=$DB_USER, Database=$DB_NAME" >> $LOG_FILE
    echo "备份文件: $BACKUP_FILE" >> $LOG_FILE
    echo "" >> $LOG_FILE

    # 检查依赖
    check_dependencies

    # 验证备份文件
    validate_backup_file
    if [[ $? -ne 0 ]]; then
        exit 1
    fi

    # 确认恢复操作
    confirm_restore

    # 备份当前数据库
    backup_current_database

    # 如果需要，先删除数据库
    if [[ "$DROP_FIRST" == true ]]; then
        drop_database
        if [[ $? -ne 0 ]]; then
            exit 1
        fi
    fi

    # 执行恢复
    if perform_restore; then
        # 验证恢复结果
        verify_restore

        echo ""
        print_info "数据库恢复操作完成！"
        print_info "恢复文件: $(basename "$BACKUP_FILE")"
        print_info "目标数据库: $DB_NAME"
    else
        print_error "数据库恢复操作失败！"
        exit 1
    fi

    print_info "日志文件: $LOG_FILE"
}

# 执行主函数
main "$@"
