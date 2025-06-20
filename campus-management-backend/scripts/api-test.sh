#!/bin/bash

# 智慧校园管理系统 API 测试脚本
# 用于快速测试主要API功能

set -e

# 配置
BASE_URL="http://localhost:8889/api/v1"
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="123456"
TOKEN=""

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查服务是否启动
check_service() {
    log_info "检查服务状态..."
    
    if curl -s -f "${BASE_URL%/api/v1}/actuator/health" > /dev/null; then
        log_success "服务正常运行"
    else
        log_error "服务未启动或无法访问"
        exit 1
    fi
}

# 用户登录
login() {
    log_info "执行用户登录..."
    
    response=$(curl -s -X POST "${BASE_URL}/auth/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"${ADMIN_USERNAME}\",
            \"password\": \"${ADMIN_PASSWORD}\"
        }")
    
    if echo "$response" | grep -q '"code":200'; then
        TOKEN=$(echo "$response" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
        log_success "登录成功，Token: ${TOKEN:0:20}..."
    else
        log_error "登录失败: $response"
        exit 1
    fi
}

# 测试用户管理API
test_user_api() {
    log_info "测试用户管理API..."
    
    # 获取用户列表
    response=$(curl -s -X GET "${BASE_URL}/users?page=0&size=5" \
        -H "Authorization: Bearer ${TOKEN}")
    
    if echo "$response" | grep -q '"code":200'; then
        user_count=$(echo "$response" | grep -o '"totalElements":[0-9]*' | cut -d':' -f2)
        log_success "获取用户列表成功，共 ${user_count} 个用户"
    else
        log_warning "获取用户列表失败: $response"
    fi
    
    # 创建测试用户
    test_username="test_user_$(date +%s)"
    response=$(curl -s -X POST "${BASE_URL}/users" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"${test_username}\",
            \"password\": \"123456\",
            \"realName\": \"测试用户\",
            \"email\": \"${test_username}@test.com\",
            \"phone\": \"13800138000\",
            \"userType\": \"STUDENT\"
        }")
    
    if echo "$response" | grep -q '"code":200'; then
        test_user_id=$(echo "$response" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        log_success "创建测试用户成功，ID: ${test_user_id}"
        
        # 删除测试用户
        delete_response=$(curl -s -X DELETE "${BASE_URL}/users/${test_user_id}" \
            -H "Authorization: Bearer ${TOKEN}")
        
        if echo "$delete_response" | grep -q '"code":200'; then
            log_success "删除测试用户成功"
        else
            log_warning "删除测试用户失败: $delete_response"
        fi
    else
        log_warning "创建测试用户失败: $response"
    fi
}

# 测试学生管理API
test_student_api() {
    log_info "测试学生管理API..."
    
    # 获取学生列表
    response=$(curl -s -X GET "${BASE_URL}/students?page=0&size=5" \
        -H "Authorization: Bearer ${TOKEN}")
    
    if echo "$response" | grep -q '"code":200'; then
        student_count=$(echo "$response" | grep -o '"totalElements":[0-9]*' | cut -d':' -f2)
        log_success "获取学生列表成功，共 ${student_count} 个学生"
    else
        log_warning "获取学生列表失败: $response"
    fi
}

# 测试课程管理API
test_course_api() {
    log_info "测试课程管理API..."
    
    # 获取课程列表
    response=$(curl -s -X GET "${BASE_URL}/courses?page=0&size=5" \
        -H "Authorization: Bearer ${TOKEN}")
    
    if echo "$response" | grep -q '"code":200'; then
        course_count=$(echo "$response" | grep -o '"totalElements":[0-9]*' | cut -d':' -f2)
        log_success "获取课程列表成功，共 ${course_count} 个课程"
    else
        log_warning "获取课程列表失败: $response"
    fi
}

# 测试监控API
test_monitoring_api() {
    log_info "测试监控API..."
    
    # 健康检查
    health_response=$(curl -s "${BASE_URL%/api/v1}/actuator/health")
    if echo "$health_response" | grep -q '"status":"UP"'; then
        log_success "健康检查通过"
    else
        log_warning "健康检查失败: $health_response"
    fi
    
    # 应用信息
    info_response=$(curl -s "${BASE_URL%/api/v1}/actuator/info")
    if [ -n "$info_response" ]; then
        log_success "获取应用信息成功"
    else
        log_warning "获取应用信息失败"
    fi
    
    # 监控指标
    metrics_response=$(curl -s "${BASE_URL%/api/v1}/actuator/metrics")
    if echo "$metrics_response" | grep -q '"names"'; then
        metrics_count=$(echo "$metrics_response" | grep -o '"[^"]*"' | wc -l)
        log_success "获取监控指标成功，共 ${metrics_count} 个指标"
    else
        log_warning "获取监控指标失败"
    fi
}

# 性能测试
performance_test() {
    log_info "执行简单性能测试..."
    
    # 测试登录接口性能
    start_time=$(date +%s%N)
    for i in {1..10}; do
        curl -s -X POST "${BASE_URL}/auth/login" \
            -H "Content-Type: application/json" \
            -d "{
                \"username\": \"${ADMIN_USERNAME}\",
                \"password\": \"${ADMIN_PASSWORD}\"
            }" > /dev/null
    done
    end_time=$(date +%s%N)
    
    duration=$(( (end_time - start_time) / 1000000 ))
    avg_time=$(( duration / 10 ))
    
    log_success "登录接口平均响应时间: ${avg_time}ms"
    
    if [ $avg_time -lt 500 ]; then
        log_success "性能测试通过 (< 500ms)"
    elif [ $avg_time -lt 1000 ]; then
        log_warning "性能一般 (500-1000ms)"
    else
        log_warning "性能较差 (> 1000ms)"
    fi
}

# 生成测试报告
generate_report() {
    log_info "生成测试报告..."
    
    report_file="api_test_report_$(date +%Y%m%d_%H%M%S).txt"
    
    cat > "$report_file" << EOF
智慧校园管理系统 API 测试报告
========================================
测试时间: $(date)
测试环境: ${BASE_URL}

测试结果:
- 服务状态: 正常
- 用户登录: 成功
- 用户管理API: 测试完成
- 学生管理API: 测试完成
- 课程管理API: 测试完成
- 监控API: 测试完成
- 性能测试: 测试完成

详细日志请查看控制台输出。
EOF
    
    log_success "测试报告已生成: $report_file"
}

# 主函数
main() {
    echo "========================================"
    echo "智慧校园管理系统 API 测试脚本"
    echo "========================================"
    
    check_service
    login
    test_user_api
    test_student_api
    test_course_api
    test_monitoring_api
    performance_test
    generate_report
    
    echo "========================================"
    log_success "所有测试完成！"
    echo "========================================"
}

# 帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示帮助信息"
    echo "  -u, --url      指定API基础地址 (默认: http://localhost:8889/api/v1)"
    echo "  -n, --username 指定管理员用户名 (默认: admin)"
    echo "  -p, --password 指定管理员密码 (默认: 123456)"
    echo ""
    echo "示例:"
    echo "  $0                                    # 使用默认配置"
    echo "  $0 -u http://test.campus.edu/api/v1   # 指定测试环境"
    echo "  $0 -n admin -p newpassword            # 指定用户名密码"
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -u|--url)
            BASE_URL="$2"
            shift 2
            ;;
        -n|--username)
            ADMIN_USERNAME="$2"
            shift 2
            ;;
        -p|--password)
            ADMIN_PASSWORD="$2"
            shift 2
            ;;
        *)
            log_error "未知参数: $1"
            show_help
            exit 1
            ;;
    esac
done

# 检查依赖
if ! command -v curl &> /dev/null; then
    log_error "curl 命令未找到，请先安装 curl"
    exit 1
fi

# 执行主函数
main
