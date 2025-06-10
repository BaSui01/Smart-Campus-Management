#!/bin/bash

# 智慧校园管理系统 API 端点测试脚本
# 支持 Linux 和 macOS

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
BASE_URL="http://localhost:8082"
ADMIN_TOKEN="Bearer_admin_token_here"

# 打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 打印标题
print_title() {
    echo
    echo "========================================"
    print_message $BLUE "🚀 智慧校园管理系统 API 端点测试"
    echo "========================================"
    echo
}

# 测试API端点
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    
    print_message $BLUE "📍 $method $endpoint"
    echo "   $description"
    
    if [ "$method" = "POST" ] && [ -n "$data" ]; then
        response=$(curl -X $method "$BASE_URL$endpoint" \
                       -H "Content-Type: application/json" \
                       -H "Authorization: $ADMIN_TOKEN" \
                       -d "$data" \
                       -w "\n状态码: %{http_code}" \
                       -s)
    else
        response=$(curl -X $method "$BASE_URL$endpoint" \
                       -H "Authorization: $ADMIN_TOKEN" \
                       -w "\n状态码: %{http_code}" \
                       -s)
    fi
    
    echo "$response"
    echo
}

# 检查服务器状态
check_server() {
    print_message $BLUE "⏳ 检查服务器状态..."
    
    if curl -s -f "$BASE_URL/actuator/health" > /dev/null; then
        print_message $GREEN "✅ 服务器运行正常"
    else
        print_message $RED "❌ 服务器未启动，请先启动应用"
        print_message $YELLOW "💡 运行命令: mvn spring-boot:run"
        exit 1
    fi
    echo
}

print_title

print_message $BLUE "📋 测试计划:"
echo "   1. 检查服务器状态"
echo "   2. 测试认证接口"
echo "   3. 测试核心API接口"
echo "   4. 生成测试报告"
echo

# 检查服务器状态
check_server

# 测试认证接口
print_message $YELLOW "⏳ 测试认证接口..."
test_endpoint "POST" "/api/auth/login" "用户登录" '{"username":"admin","password":"admin123"}'
test_endpoint "GET" "/api/auth/me" "获取当前用户信息"

# 测试用户管理接口
print_message $YELLOW "⏳ 测试用户管理接口..."
test_endpoint "GET" "/api/users?page=1&size=5" "获取用户列表"
test_endpoint "GET" "/api/users/stats" "获取用户统计信息"

# 测试学生管理接口
print_message $YELLOW "⏳ 测试学生管理接口..."
test_endpoint "GET" "/api/students?page=1&size=5" "获取学生列表"
test_endpoint "GET" "/api/students/statistics" "获取学生统计信息"

# 测试课程管理接口
print_message $YELLOW "⏳ 测试课程管理接口..."
test_endpoint "GET" "/api/courses?page=1&size=5" "获取课程列表"

# 测试系统管理接口
print_message $YELLOW "⏳ 测试系统管理接口..."
test_endpoint "GET" "/api/dashboard" "获取仪表盘数据"
test_endpoint "GET" "/api/system/info" "获取系统信息"

# 测试权限管理接口
print_message $YELLOW "⏳ 测试权限管理接口..."
test_endpoint "GET" "/api/roles" "获取角色列表"
test_endpoint "GET" "/api/permissions" "获取权限列表"

# 测试学术管理接口
print_message $YELLOW "⏳ 测试学术管理接口..."
test_endpoint "GET" "/api/assignments" "获取作业列表"
test_endpoint "GET" "/api/exams" "获取考试列表"
test_endpoint "GET" "/api/grades" "获取成绩列表"

# 测试特殊功能接口
print_message $YELLOW "⏳ 测试特殊功能接口..."
test_endpoint "GET" "/api/auto-schedule" "获取自动排课信息"
test_endpoint "GET" "/api/cache/stats" "获取缓存统计"

# 测试完成
echo "========================================"
print_message $BLUE "📊 API端点测试完成"
echo "========================================"
echo

print_message $BLUE "💡 测试说明:"
echo "   - 200: 请求成功"
echo "   - 401: 未授权（需要登录）"
echo "   - 403: 权限不足"
echo "   - 404: 接口不存在"
echo "   - 500: 服务器错误"
echo

print_message $BLUE "📁 更多测试选项:"
echo "   - 使用Postman集合: src/test/resources/postman/"
echo "   - 运行单元测试: mvn test"
echo "   - 查看API文档: http://localhost:8082/swagger-ui.html"
echo

print_message $GREEN "🎉 测试脚本执行完成！"
