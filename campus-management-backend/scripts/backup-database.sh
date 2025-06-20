#!/bin/bash

# 智慧校园管理系统数据库备份脚本
# 支持全量备份、增量备份和自动清理

set -e

# 默认配置
DB_HOST="localhost"
DB_PORT="3306"
DB_NAME="campus_management_db"
DB_USER="root"
DB_PASSWORD=""
BACKUP_DIR="/backup/campus-management"
RETENTION_DAYS=7
BACKUP_TYPE="full"
COMPRESS=true

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

# 检查依赖
check_dependencies() {
    log_info "检查依赖..."
    
    if ! command -v mysqldump &> /dev/null; then
        log_error "mysqldump 命令未找到，请安装 MySQL 客户端"
        exit 1
    fi
    
    if ! command -v mysql &> /dev/null; then
        log_error "mysql 命令未找到，请安装 MySQL 客户端"
        exit 1
    fi
    
    if [ "$COMPRESS" = true ] && ! command -v gzip &> /dev/null; then
        log_error "gzip 命令未找到，请安装 gzip"
        exit 1
    fi
    
    log_success "依赖检查通过"
}

# 检查数据库连接
check_database_connection() {
    log_info "检查数据库连接..."
    
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "USE $DB_NAME;" 2>/dev/null; then
        log_success "数据库连接正常"
    else
        log_error "无法连接到数据库，请检查连接参数"
        exit 1
    fi
}

# 创建备份目录
create_backup_directory() {
    log_info "创建备份目录..."
    
    if [ ! -d "$BACKUP_DIR" ]; then
        mkdir -p "$BACKUP_DIR"
        log_success "备份目录已创建: $BACKUP_DIR"
    else
        log_info "备份目录已存在: $BACKUP_DIR"
    fi
}

# 全量备份
full_backup() {
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_file="$BACKUP_DIR/campus_db_full_$timestamp.sql"
    
    log_info "开始全量备份..."
    
    # 执行备份
    mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --hex-blob \
        --default-character-set=utf8mb4 \
        "$DB_NAME" > "$backup_file"
    
    if [ $? -eq 0 ]; then
        log_success "数据库备份完成: $backup_file"
        
        # 压缩备份文件
        if [ "$COMPRESS" = true ]; then
            log_info "压缩备份文件..."
            gzip "$backup_file"
            backup_file="${backup_file}.gz"
            log_success "备份文件已压缩: $backup_file"
        fi
        
        # 显示备份文件信息
        local file_size=$(du -h "$backup_file" | cut -f1)
        log_info "备份文件大小: $file_size"
        
        echo "$backup_file"
    else
        log_error "数据库备份失败"
        exit 1
    fi
}

# 增量备份（基于binlog）
incremental_backup() {
    log_info "增量备份功能需要启用MySQL binlog"
    log_warning "当前版本暂不支持增量备份，执行全量备份"
    full_backup
}

# 结构备份（仅备份表结构）
structure_backup() {
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_file="$BACKUP_DIR/campus_db_structure_$timestamp.sql"
    
    log_info "开始结构备份..."
    
    mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" \
        --no-data \
        --routines \
        --triggers \
        --events \
        --default-character-set=utf8mb4 \
        "$DB_NAME" > "$backup_file"
    
    if [ $? -eq 0 ]; then
        log_success "数据库结构备份完成: $backup_file"
        
        if [ "$COMPRESS" = true ]; then
            gzip "$backup_file"
            backup_file="${backup_file}.gz"
            log_success "备份文件已压缩: $backup_file"
        fi
        
        echo "$backup_file"
    else
        log_error "数据库结构备份失败"
        exit 1
    fi
}

# 数据备份（仅备份数据）
data_backup() {
    local timestamp=$(date +%Y%m%d_%H%M%S)
    local backup_file="$BACKUP_DIR/campus_db_data_$timestamp.sql"
    
    log_info "开始数据备份..."
    
    mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" \
        --no-create-info \
        --single-transaction \
        --hex-blob \
        --default-character-set=utf8mb4 \
        "$DB_NAME" > "$backup_file"
    
    if [ $? -eq 0 ]; then
        log_success "数据备份完成: $backup_file"
        
        if [ "$COMPRESS" = true ]; then
            gzip "$backup_file"
            backup_file="${backup_file}.gz"
            log_success "备份文件已压缩: $backup_file"
        fi
        
        echo "$backup_file"
    else
        log_error "数据备份失败"
        exit 1
    fi
}

# 清理旧备份
cleanup_old_backups() {
    log_info "清理 $RETENTION_DAYS 天前的备份文件..."
    
    local deleted_count=0
    
    # 查找并删除旧文件
    while IFS= read -r -d '' file; do
        rm "$file"
        log_info "已删除: $(basename "$file")"
        ((deleted_count++))
    done < <(find "$BACKUP_DIR" -name "campus_db_*.sql*" -mtime +$RETENTION_DAYS -print0 2>/dev/null)
    
    if [ $deleted_count -gt 0 ]; then
        log_success "已清理 $deleted_count 个旧备份文件"
    else
        log_info "没有需要清理的旧备份文件"
    fi
}

# 验证备份文件
verify_backup() {
    local backup_file="$1"
    
    log_info "验证备份文件..."
    
    if [ ! -f "$backup_file" ]; then
        log_error "备份文件不存在: $backup_file"
        return 1
    fi
    
    # 检查文件大小
    local file_size=$(stat -f%z "$backup_file" 2>/dev/null || stat -c%s "$backup_file" 2>/dev/null)
    if [ "$file_size" -lt 1024 ]; then
        log_error "备份文件太小，可能备份失败"
        return 1
    fi
    
    # 检查文件内容（如果是压缩文件）
    if [[ "$backup_file" == *.gz ]]; then
        if ! gzip -t "$backup_file" 2>/dev/null; then
            log_error "压缩文件损坏"
            return 1
        fi
    fi
    
    log_success "备份文件验证通过"
    return 0
}

# 生成备份报告
generate_report() {
    local backup_file="$1"
    local report_file="$BACKUP_DIR/backup_report_$(date +%Y%m%d_%H%M%S).txt"
    
    log_info "生成备份报告..."
    
    cat > "$report_file" << EOF
智慧校园管理系统数据库备份报告
========================================
备份时间: $(date)
备份类型: $BACKUP_TYPE
数据库: $DB_NAME
备份文件: $(basename "$backup_file")
文件大小: $(du -h "$backup_file" | cut -f1)
备份目录: $BACKUP_DIR

数据库统计:
EOF
    
    # 添加表统计信息
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "
        SELECT 
            table_name as '表名',
            table_rows as '记录数',
            ROUND(((data_length + index_length) / 1024 / 1024), 2) as '大小(MB)'
        FROM information_schema.tables 
        WHERE table_schema = '$DB_NAME'
        ORDER BY (data_length + index_length) DESC;
    " >> "$report_file" 2>/dev/null || echo "无法获取表统计信息" >> "$report_file"
    
    log_success "备份报告已生成: $report_file"
}

# 显示帮助信息
show_help() {
    echo "智慧校园管理系统数据库备份脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help              显示帮助信息"
    echo "  -H, --host HOST         数据库主机 (默认: localhost)"
    echo "  -P, --port PORT         数据库端口 (默认: 3306)"
    echo "  -d, --database DB       数据库名称 (默认: campus_management_db)"
    echo "  -u, --user USER         数据库用户名 (默认: root)"
    echo "  -p, --password PASS     数据库密码"
    echo "  -b, --backup-dir DIR    备份目录 (默认: /backup/campus-management)"
    echo "  -t, --type TYPE         备份类型: full|incremental|structure|data (默认: full)"
    echo "  -r, --retention DAYS    保留天数 (默认: 7)"
    echo "  -c, --compress          压缩备份文件 (默认: true)"
    echo "  --no-compress           不压缩备份文件"
    echo "  --cleanup-only          仅清理旧备份文件"
    echo ""
    echo "示例:"
    echo "  $0                                    # 使用默认配置进行全量备份"
    echo "  $0 -t structure                      # 仅备份表结构"
    echo "  $0 -p mypassword -r 30               # 指定密码，保留30天"
    echo "  $0 --cleanup-only                    # 仅清理旧备份"
}

# 主函数
main() {
    echo "========================================"
    echo "智慧校园管理系统数据库备份"
    echo "========================================"
    
    check_dependencies
    check_database_connection
    create_backup_directory
    
    local backup_file=""
    
    case "$BACKUP_TYPE" in
        "full")
            backup_file=$(full_backup)
            ;;
        "incremental")
            backup_file=$(incremental_backup)
            ;;
        "structure")
            backup_file=$(structure_backup)
            ;;
        "data")
            backup_file=$(data_backup)
            ;;
        *)
            log_error "不支持的备份类型: $BACKUP_TYPE"
            exit 1
            ;;
    esac
    
    if [ -n "$backup_file" ]; then
        verify_backup "$backup_file"
        generate_report "$backup_file"
    fi
    
    cleanup_old_backups
    
    echo "========================================"
    log_success "备份任务完成！"
    echo "========================================"
}

# 解析命令行参数
CLEANUP_ONLY=false

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
        -d|--database)
            DB_NAME="$2"
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
        -b|--backup-dir)
            BACKUP_DIR="$2"
            shift 2
            ;;
        -t|--type)
            BACKUP_TYPE="$2"
            shift 2
            ;;
        -r|--retention)
            RETENTION_DAYS="$2"
            shift 2
            ;;
        -c|--compress)
            COMPRESS=true
            shift
            ;;
        --no-compress)
            COMPRESS=false
            shift
            ;;
        --cleanup-only)
            CLEANUP_ONLY=true
            shift
            ;;
        *)
            log_error "未知参数: $1"
            show_help
            exit 1
            ;;
    esac
done

# 如果没有提供密码，提示输入
if [ -z "$DB_PASSWORD" ]; then
    read -s -p "请输入数据库密码: " DB_PASSWORD
    echo
fi

# 执行清理或备份
if [ "$CLEANUP_ONLY" = true ]; then
    log_info "仅执行清理任务..."
    create_backup_directory
    cleanup_old_backups
else
    main
fi
