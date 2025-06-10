#!/bin/bash

# 智慧校园管理系统 API 测试执行器
# 支持 Linux 和 macOS

set -e

# 颜色定义
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

# 打印标题
print_title() {
    echo
    echo "========================================"
    print_message $BLUE "🚀 智慧校园管理系统 API 测试执行器"
    echo "========================================"
    echo
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        print_message $RED "❌ $1 命令未找到，请先安装"
        exit 1
    fi
}

# 进入项目目录
cd "$(dirname "$0")/.."

print_title

# 检查必要的命令
print_message $BLUE "🔍 检查环境..."
check_command mvn
check_command java
print_message $GREEN "✅ 环境检查通过"

echo
print_message $BLUE "📋 测试执行计划:"
echo "   1. 编译项目"
echo "   2. 运行单元测试"
echo "   3. 运行集成测试"
echo "   4. 生成测试报告"
echo

# 编译项目
print_message $BLUE "⏳ 正在编译项目..."
if mvn clean compile -q; then
    print_message $GREEN "✅ 项目编译成功"
else
    print_message $RED "❌ 项目编译失败！"
    exit 1
fi

echo

# 运行API测试
print_message $BLUE "⏳ 正在运行API测试..."
if mvn test -Dtest="com.campus.interfaces.rest.v1.*Test" -Dspring.profiles.active=test; then
    print_message $GREEN "✅ 所有API测试通过"
    TEST_SUCCESS=true
else
    print_message $YELLOW "⚠️  部分测试失败，请查看详细报告"
    TEST_SUCCESS=false
fi

echo

# 生成测试报告
print_message $BLUE "⏳ 正在生成测试报告..."
if mvn surefire-report:report -q; then
    print_message $GREEN "✅ 测试报告生成成功"
    print_message $BLUE "📊 报告位置: target/site/surefire-report.html"
else
    print_message $YELLOW "⚠️  测试报告生成失败"
fi

echo

# 生成代码覆盖率报告
print_message $BLUE "⏳ 正在生成代码覆盖率报告..."
if mvn jacoco:report -q; then
    print_message $GREEN "✅ 覆盖率报告生成成功"
    print_message $BLUE "📊 报告位置: target/site/jacoco/index.html"
else
    print_message $YELLOW "⚠️  覆盖率报告生成失败"
fi

echo
echo "========================================"
print_message $BLUE "📊 测试执行完成"
echo "========================================"
echo

print_message $BLUE "📁 查看测试报告:"
echo "   - 测试结果: target/site/surefire-report.html"
echo "   - 覆盖率: target/site/jacoco/index.html"
echo "   - 详细日志: target/surefire-reports/"
echo

# 询问是否打开报告
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    read -p "是否打开测试报告? (y/n): " choice
    if [[ $choice == "y" || $choice == "Y" ]]; then
        if [[ -f "target/site/surefire-report.html" ]]; then
            open "target/site/surefire-report.html"
        fi
        if [[ -f "target/site/jacoco/index.html" ]]; then
            open "target/site/jacoco/index.html"
        fi
    fi
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    read -p "是否打开测试报告? (y/n): " choice
    if [[ $choice == "y" || $choice == "Y" ]]; then
        if command -v xdg-open &> /dev/null; then
            if [[ -f "target/site/surefire-report.html" ]]; then
                xdg-open "target/site/surefire-report.html"
            fi
            if [[ -f "target/site/jacoco/index.html" ]]; then
                xdg-open "target/site/jacoco/index.html"
            fi
        else
            print_message $YELLOW "⚠️  无法自动打开浏览器，请手动打开报告文件"
        fi
    fi
fi

# 返回适当的退出码
if [[ $TEST_SUCCESS == true ]]; then
    exit 0
else
    exit 1
fi
