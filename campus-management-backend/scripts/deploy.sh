#!/bin/bash

# 智慧校园管理系统部署脚本
# 使用方法: ./deploy.sh [环境] [版本]
# 示例: ./deploy.sh dev v1.0.0

set -e

# 颜色定义
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

# 检查参数
if [ $# -lt 2 ]; then
    log_error "使用方法: $0 <环境> <版本>"
    log_info "环境: dev, test, prod"
    log_info "版本: v1.0.0, latest, etc."
    exit 1
fi

ENVIRONMENT=$1
VERSION=$2

# 验证环境参数
case $ENVIRONMENT in
    dev|test|prod)
        log_info "部署环境: $ENVIRONMENT"
        ;;
    *)
        log_error "无效的环境: $ENVIRONMENT"
        log_info "支持的环境: dev, test, prod"
        exit 1
        ;;
esac

# 配置变量
PROJECT_NAME="campus-management"
DOCKER_REGISTRY="registry.campus.edu"
IMAGE_NAME="$DOCKER_REGISTRY/campus/management:$VERSION"
NAMESPACE="campus-system"

if [ "$ENVIRONMENT" != "prod" ]; then
    NAMESPACE="$NAMESPACE-$ENVIRONMENT"
fi

# 检查必要工具
check_tools() {
    log_info "检查必要工具..."
    
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装"
        exit 1
    fi
    
    if ! command -v kubectl &> /dev/null; then
        log_error "kubectl 未安装"
        exit 1
    fi
    
    if ! command -v helm &> /dev/null; then
        log_warning "Helm 未安装，将使用 kubectl 部署"
    fi
    
    log_success "工具检查完成"
}

# 检查Kubernetes连接
check_k8s_connection() {
    log_info "检查Kubernetes连接..."
    
    if ! kubectl cluster-info &> /dev/null; then
        log_error "无法连接到Kubernetes集群"
        exit 1
    fi
    
    log_success "Kubernetes连接正常"
}

# 创建命名空间
create_namespace() {
    log_info "创建命名空间: $NAMESPACE"
    
    kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -
    
    log_success "命名空间创建完成"
}

# 部署配置文件
deploy_configs() {
    log_info "部署配置文件..."
    
    # 创建ConfigMap
    kubectl apply -f k8s/configmap.yaml -n $NAMESPACE
    
    # 创建Secret
    kubectl apply -f k8s/secret.yaml -n $NAMESPACE
    
    # 创建PVC
    kubectl apply -f k8s/pvc.yaml -n $NAMESPACE
    
    log_success "配置文件部署完成"
}

# 部署应用
deploy_application() {
    log_info "部署应用: $IMAGE_NAME"
    
    # 更新镜像版本
    sed -i.bak "s|image: .*|image: $IMAGE_NAME|g" k8s/deployment.yaml
    
    # 应用部署文件
    kubectl apply -f k8s/deployment.yaml -n $NAMESPACE
    
    # 等待部署完成
    log_info "等待部署完成..."
    kubectl rollout status deployment/$PROJECT_NAME-app -n $NAMESPACE --timeout=600s
    
    # 恢复原始文件
    mv k8s/deployment.yaml.bak k8s/deployment.yaml
    
    log_success "应用部署完成"
}

# 部署服务
deploy_service() {
    log_info "部署服务..."
    
    kubectl apply -f k8s/service.yaml -n $NAMESPACE
    
    log_success "服务部署完成"
}

# 部署Ingress
deploy_ingress() {
    log_info "部署Ingress..."
    
    # 根据环境更新域名
    case $ENVIRONMENT in
        dev)
            sed -i.bak "s|host: .*|host: campus-dev.example.com|g" k8s/ingress.yaml
            ;;
        test)
            sed -i.bak "s|host: .*|host: campus-test.example.com|g" k8s/ingress.yaml
            ;;
        prod)
            sed -i.bak "s|host: .*|host: campus.example.com|g" k8s/ingress.yaml
            ;;
    esac
    
    kubectl apply -f k8s/ingress.yaml -n $NAMESPACE
    
    # 恢复原始文件
    mv k8s/ingress.yaml.bak k8s/ingress.yaml
    
    log_success "Ingress部署完成"
}

# 健康检查
health_check() {
    log_info "执行健康检查..."
    
    # 等待Pod就绪
    kubectl wait --for=condition=ready pod -l app=$PROJECT_NAME -n $NAMESPACE --timeout=300s
    
    # 获取服务端点
    SERVICE_IP=$(kubectl get service $PROJECT_NAME-service -n $NAMESPACE -o jsonpath='{.spec.clusterIP}')
    
    # 健康检查
    if kubectl run health-check --image=curlimages/curl:latest --rm -i --restart=Never -- \
        curl -f http://$SERVICE_IP/actuator/health; then
        log_success "健康检查通过"
    else
        log_error "健康检查失败"
        exit 1
    fi
}

# 显示部署信息
show_deployment_info() {
    log_info "部署信息:"
    echo "----------------------------------------"
    echo "环境: $ENVIRONMENT"
    echo "版本: $VERSION"
    echo "命名空间: $NAMESPACE"
    echo "镜像: $IMAGE_NAME"
    echo "----------------------------------------"
    
    # 显示Pod状态
    kubectl get pods -n $NAMESPACE -l app=$PROJECT_NAME
    
    # 显示服务信息
    kubectl get services -n $NAMESPACE
    
    # 显示Ingress信息
    kubectl get ingress -n $NAMESPACE
}

# 回滚函数
rollback() {
    log_warning "开始回滚..."
    
    kubectl rollout undo deployment/$PROJECT_NAME-app -n $NAMESPACE
    kubectl rollout status deployment/$PROJECT_NAME-app -n $NAMESPACE --timeout=300s
    
    log_success "回滚完成"
}

# 清理函数
cleanup() {
    log_info "清理临时文件..."
    rm -f k8s/*.bak
}

# 主函数
main() {
    log_info "开始部署智慧校园管理系统"
    log_info "环境: $ENVIRONMENT, 版本: $VERSION"
    
    # 设置错误处理
    trap cleanup EXIT
    trap 'log_error "部署失败，正在回滚..."; rollback; exit 1' ERR
    
    # 执行部署步骤
    check_tools
    check_k8s_connection
    create_namespace
    deploy_configs
    deploy_application
    deploy_service
    deploy_ingress
    health_check
    show_deployment_info
    
    log_success "部署完成！"
}

# 如果直接运行脚本，执行主函数
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
