/**
 * 仪表盘诊断工具
 * 用于检测后端API状态和数据可用性
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */

class DashboardDiagnostics {
    constructor() {
        this.apiEndpoints = [
            { 
                name: '统计数据API', 
                endpoint: '/api/dashboard/stats',
                description: '获取学生、课程、班级等基础统计数据',
                required: true
            },
            { 
                name: '图表数据API', 
                endpoint: '/api/dashboard/chart-data',
                description: '获取各种图表所需的数据',
                required: true
            },
            { 
                name: '通知数据API', 
                endpoint: '/api/dashboard/notifications',
                description: '获取系统通知列表',
                required: false
            },
            { 
                name: '活动数据API', 
                endpoint: '/api/dashboard/activities',
                description: '获取最近活动记录',
                required: false
            },
            { 
                name: '健康检查API', 
                endpoint: '/api/health',
                description: '检查后端服务状态',
                required: true
            }
        ];
    }

    /**
     * 运行完整诊断
     */
    async runFullDiagnostics() {
        console.log('🔍 开始仪表盘诊断...');
        
        const results = {
            overall: 'unknown',
            timestamp: new Date().toISOString(),
            authentication: await this.checkAuthentication(),
            apis: await this.checkAllAPIs(),
            frontend: this.checkFrontendDependencies(),
            recommendations: []
        };

        // 分析结果
        this.analyzeResults(results);
        
        // 显示诊断报告
        this.displayDiagnosticsReport(results);
        
        return results;
    }

    /**
     * 检查认证状态
     */
    async checkAuthentication() {
        try {
            const token = apiClient.getToken();
            if (!token) {
                return {
                    status: 'failed',
                    message: '未找到认证token',
                    details: '用户可能未登录或token已过期'
                };
            }

            // 验证token有效性
            if (window.jwtManager) {
                const tokenInfo = await window.jwtManager.getTokenInfo();
                if (!tokenInfo || !tokenInfo.valid) {
                    return {
                        status: 'failed',
                        message: 'Token无效或已过期',
                        details: tokenInfo
                    };
                }
            }

            return {
                status: 'success',
                message: '认证状态正常',
                details: { hasToken: true, tokenValid: true }
            };
        } catch (error) {
            return {
                status: 'error',
                message: '认证检查失败',
                details: error.message
            };
        }
    }

    /**
     * 检查所有API
     */
    async checkAllAPIs() {
        const results = [];
        
        for (const api of this.apiEndpoints) {
            console.log(`📡 检查 ${api.name}...`);
            const result = await this.checkSingleAPI(api);
            results.push(result);
        }
        
        return results;
    }

    /**
     * 检查单个API
     */
    async checkSingleAPI(api) {
        try {
            const startTime = Date.now();
            const response = await apiClient.get(api.endpoint);
            const responseTime = Date.now() - startTime;

            let status = 'success';
            let details = {};

            // 检查响应内容
            if (response) {
                if (response.success === false) {
                    status = 'warning';
                    details.warning = 'API返回success=false';
                }

                // 检查数据完整性
                if (api.endpoint === '/api/dashboard/stats') {
                    details.dataCheck = this.validateStatsData(response.data || response);
                } else if (api.endpoint === '/api/dashboard/chart-data') {
                    details.dataCheck = this.validateChartData(response.data || response);
                }
            }

            return {
                name: api.name,
                endpoint: api.endpoint,
                status: status,
                responseTime: responseTime,
                message: `响应正常 (${responseTime}ms)`,
                required: api.required,
                details: details,
                response: response
            };
        } catch (error) {
            return {
                name: api.name,
                endpoint: api.endpoint,
                status: 'error',
                message: error.message,
                required: api.required,
                details: { error: error.message }
            };
        }
    }

    /**
     * 验证统计数据
     */
    validateStatsData(data) {
        const requiredFields = [
            'totalStudents', 'totalCourses', 'totalClasses', 
            'totalUsers', 'totalTeachers', 'activeSchedules'
        ];
        
        const missing = requiredFields.filter(field => 
            data[field] === undefined || data[field] === null
        );
        
        return {
            valid: missing.length === 0,
            missingFields: missing,
            hasQuickStats: !!data.quickStats
        };
    }

    /**
     * 验证图表数据
     */
    validateChartData(data) {
        const requiredCharts = [
            'studentTrendData', 'courseDistribution', 
            'gradeDistribution', 'revenueTrendData'
        ];
        
        const missing = requiredCharts.filter(chart => 
            !data[chart] || !Array.isArray(data[chart])
        );
        
        const empty = requiredCharts.filter(chart => 
            data[chart] && Array.isArray(data[chart]) && data[chart].length === 0
        );
        
        return {
            valid: missing.length === 0 && empty.length === 0,
            missingCharts: missing,
            emptyCharts: empty
        };
    }

    /**
     * 检查前端依赖
     */
    checkFrontendDependencies() {
        const dependencies = [
            { name: 'Chart.js', check: () => typeof Chart !== 'undefined' },
            { name: 'API Client', check: () => typeof apiClient !== 'undefined' },
            { name: 'Bootstrap', check: () => typeof bootstrap !== 'undefined' },
            { name: 'jQuery', check: () => typeof $ !== 'undefined' }
        ];

        return dependencies.map(dep => ({
            name: dep.name,
            available: dep.check(),
            status: dep.check() ? 'success' : 'missing'
        }));
    }

    /**
     * 分析诊断结果
     */
    analyzeResults(results) {
        const recommendations = [];
        
        // 检查认证问题
        if (results.authentication.status !== 'success') {
            recommendations.push({
                type: 'error',
                title: '认证问题',
                message: '请检查用户登录状态和token有效性',
                action: '重新登录或刷新页面'
            });
        }

        // 检查必需的API
        const failedRequiredAPIs = results.apis.filter(api => 
            api.required && api.status === 'error'
        );
        
        if (failedRequiredAPIs.length > 0) {
            recommendations.push({
                type: 'error',
                title: '关键API不可用',
                message: `以下关键API无法访问: ${failedRequiredAPIs.map(api => api.name).join(', ')}`,
                action: '检查后端服务是否正常运行'
            });
        }

        // 检查数据完整性
        const dataIssues = results.apis.filter(api => 
            api.details.dataCheck && !api.details.dataCheck.valid
        );
        
        if (dataIssues.length > 0) {
            recommendations.push({
                type: 'warning',
                title: '数据不完整',
                message: '某些API返回的数据结构不完整',
                action: '检查数据库数据和后端业务逻辑'
            });
        }

        // 检查前端依赖
        const missingDeps = results.frontend.filter(dep => !dep.available);
        if (missingDeps.length > 0) {
            recommendations.push({
                type: 'warning',
                title: '前端依赖缺失',
                message: `缺少依赖: ${missingDeps.map(dep => dep.name).join(', ')}`,
                action: '检查JavaScript库是否正确加载'
            });
        }

        // 设置整体状态
        if (results.authentication.status === 'success' && 
            failedRequiredAPIs.length === 0 && 
            missingDeps.length === 0) {
            results.overall = 'healthy';
        } else if (failedRequiredAPIs.length > 0) {
            results.overall = 'critical';
        } else {
            results.overall = 'warning';
        }

        results.recommendations = recommendations;
    }

    /**
     * 显示诊断报告
     */
    displayDiagnosticsReport(results) {
        const reportHtml = this.generateReportHTML(results);
        
        // 创建诊断窗口
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.id = 'diagnosticsModal';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-stethoscope me-2"></i>仪表盘诊断报告
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        ${reportHtml}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" onclick="dashboardDiagnostics.runFullDiagnostics()">重新诊断</button>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const modalInstance = new bootstrap.Modal(modal);
        modalInstance.show();
        
        // 模态框关闭时清理
        modal.addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modal);
        });
    }

    /**
     * 生成报告HTML
     */
    generateReportHTML(results) {
        const statusColors = {
            healthy: 'success',
            warning: 'warning',
            critical: 'danger'
        };

        const statusIcons = {
            healthy: 'check-circle',
            warning: 'exclamation-triangle',
            critical: 'times-circle'
        };

        return `
            <div class="diagnostics-report">
                <!-- 整体状态 -->
                <div class="alert alert-${statusColors[results.overall]} mb-4">
                    <h6 class="alert-heading">
                        <i class="fas fa-${statusIcons[results.overall]} me-2"></i>
                        整体状态: ${this.getStatusText(results.overall)}
                    </h6>
                    <small class="text-muted">诊断时间: ${new Date(results.timestamp).toLocaleString('zh-CN')}</small>
                </div>

                <!-- 认证状态 -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="fas fa-shield-alt me-2"></i>认证状态</h6>
                    </div>
                    <div class="card-body">
                        <div class="d-flex align-items-center">
                            <span class="badge bg-${results.authentication.status === 'success' ? 'success' : 'danger'} me-2">
                                ${results.authentication.status.toUpperCase()}
                            </span>
                            <span>${results.authentication.message}</span>
                        </div>
                    </div>
                </div>

                <!-- API状态 -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="fas fa-server me-2"></i>API状态</h6>
                    </div>
                    <div class="card-body">
                        ${results.apis.map(api => `
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <div>
                                    <strong>${api.name}</strong>
                                    ${api.required ? '<span class="badge bg-primary ms-1">必需</span>' : ''}
                                    <br>
                                    <small class="text-muted">${api.endpoint}</small>
                                </div>
                                <div class="text-end">
                                    <span class="badge bg-${api.status === 'success' ? 'success' : (api.status === 'warning' ? 'warning' : 'danger')}">
                                        ${api.status.toUpperCase()}
                                    </span>
                                    ${api.responseTime ? `<br><small class="text-muted">${api.responseTime}ms</small>` : ''}
                                </div>
                            </div>
                        `).join('')}
                    </div>
                </div>

                <!-- 前端依赖 -->
                <div class="card mb-3">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="fas fa-code me-2"></i>前端依赖</h6>
                    </div>
                    <div class="card-body">
                        ${results.frontend.map(dep => `
                            <div class="d-flex justify-content-between align-items-center mb-1">
                                <span>${dep.name}</span>
                                <span class="badge bg-${dep.available ? 'success' : 'danger'}">
                                    ${dep.available ? '可用' : '缺失'}
                                </span>
                            </div>
                        `).join('')}
                    </div>
                </div>

                <!-- 建议 -->
                ${results.recommendations.length > 0 ? `
                    <div class="card">
                        <div class="card-header">
                            <h6 class="mb-0"><i class="fas fa-lightbulb me-2"></i>建议措施</h6>
                        </div>
                        <div class="card-body">
                            ${results.recommendations.map(rec => `
                                <div class="alert alert-${rec.type === 'error' ? 'danger' : 'warning'} mb-2">
                                    <strong>${rec.title}</strong><br>
                                    ${rec.message}<br>
                                    <small><strong>建议:</strong> ${rec.action}</small>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                ` : ''}
            </div>
        `;
    }

    /**
     * 获取状态文本
     */
    getStatusText(status) {
        const texts = {
            healthy: '健康',
            warning: '有问题',
            critical: '严重问题'
        };
        return texts[status] || '未知';
    }
}

// 全局实例
window.dashboardDiagnostics = new DashboardDiagnostics();

// 全局诊断函数
window.runDashboardDiagnostics = function() {
    return window.dashboardDiagnostics.runFullDiagnostics();
};

console.log('🔧 仪表盘诊断工具已加载');