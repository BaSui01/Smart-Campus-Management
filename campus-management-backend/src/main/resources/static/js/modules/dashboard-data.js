/**
 * 仪表盘数据管理模块
 * 负责获取和处理真实的数据库数据，生成图表和统计信息
 * 完全依赖后端数据，不使用模拟数据
 *
 * @author Campus Management Team
 * @version 2.0.0
 * @since 2025-06-07
 */

class DashboardDataManager {
    constructor() {
        this.chartInstances = {};
        this.refreshInterval = null;
        this.autoRefreshEnabled = false;
        this.lastUpdateTime = null;
        this.init();
    }

    /**
     * 初始化仪表盘数据管理器
     */
    async init() {
        try {
            console.log('初始化仪表盘数据管理器...');
            
            // 验证后端API可用性
            console.log('验证后端API可用性...');
            const apiValidation = await this.validateBackendAPIs();
            const failedApis = apiValidation.filter(api => !api.available);
            
            if (failedApis.length > 0) {
                console.error('以下API不可用:', failedApis);
                const errorMessage = `后端API验证失败:\n${failedApis.map(api => `• ${api.name} (${api.endpoint}): ${api.error}`).join('\n')}`;
                throw new Error(errorMessage);
            }
            
            console.log('后端API验证通过，开始加载数据...');
            
            // 加载统计数据
            await this.loadDashboardStats();
            
            // 初始化图表
            await this.initializeAllCharts();
            
            // 绑定事件
            this.bindEvents();
            
            // 启动自动刷新
            this.startAutoRefresh();
            
            // 触发初始化完成事件
            this.dispatchDataReadyEvent();
            
            console.log('仪表盘数据管理器初始化完成');
        } catch (error) {
            console.error('仪表盘数据管理器初始化失败:', error);
            this.showErrorMessage('仪表盘初始化失败: ' + error.message);
            this.showDataLoadingFailure(error.message);
            
            // 触发错误事件
            this.dispatchDataErrorEvent(error.message);
        }
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 刷新按钮事件
        const refreshBtn = document.querySelector('[onclick="refreshDashboardData()"]');
        if (refreshBtn) {
            refreshBtn.onclick = () => this.refreshAllData();
        }

        // 图表操作事件（移除导出事件委托，避免重复调用）
        document.addEventListener('click', (e) => {
            if (e.target.closest('[onclick*="refreshCharts"]')) {
                e.preventDefault();
                this.refreshAllCharts();
            }
            if (e.target.closest('[onclick*="toggleChartType"]')) {
                e.preventDefault();
                const chartId = this.extractChartId(e.target.getAttribute('onclick'));
                this.toggleChartType(chartId);
            }
        });
    }

    /**
     * 加载仪表盘统计数据
     */
    async loadDashboardStats() {
        try {
            showLoading('正在加载统计数据...');

            console.log('开始请求统计数据...');
            
            // 确保有有效的token
            await this.ensureAuthentication();
            
            const response = await apiClient.get('/api/dashboard/stats');
            console.log('API响应:', response);

            if (response && response.success !== false) {
                // 处理响应数据
                const data = response.data || response;
                this.updateStatsCards(data);
                this.updateLastRefreshTime();
                console.log('统计数据加载成功');
            } else {
                console.warn('API返回失败状态:', response);
                throw new Error(response.message || '获取统计数据失败');
            }
        } catch (error) {
            console.error('加载统计数据失败:', error);
            console.error('错误详情:', error.stack);

            // 检查是否是认证问题
            if (this.isAuthenticationError(error)) {
                this.handleAuthenticationError(error);
                return;
            }

            // 检查是否是网络问题
            if (this.isNetworkError(error)) {
                this.showNetworkError('网络连接失败，请检查网络连接后重试');
                return;
            }

            // 严格模式：数据获取失败时显示错误，不使用模拟数据
            this.showDataError('无法从服务器获取统计数据。请确保：1) 后端服务正常运行 2) API接口可访问 3) 用户已登录并有权限');
            this.hideStatsCards();
        } finally {
            hideLoading();
        }
    }

    /**
     * 显示数据加载失败的提示
     */
    showDataLoadingFailure(message = '数据加载失败') {
        const container = document.querySelector('.content-wrapper');
        if (container) {
            const errorHtml = `
                <div class="alert alert-danger alert-dismissible fade show data-loading-error" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    <strong>数据加载失败：</strong>${message}
                    <div class="mt-2">
                        <button type="button" class="btn btn-outline-danger btn-sm me-2" onclick="window.dashboardData.refreshAllData()">
                            <i class="fas fa-redo me-1"></i>重新加载
                        </button>
                        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="window.dashboardData.checkConnection()">
                            <i class="fas fa-network-wired me-1"></i>检查连接
                        </button>
                    </div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
            container.insertAdjacentHTML('afterbegin', errorHtml);
        }
    }

    /**
     * 更新统计卡片
     */
    updateStatsCards(stats) {
        // 更新基础统计 - 使用实际的DOM元素
        this.updateCounterByText(stats.totalStudents, 0);
        this.updateCounterByText(stats.totalCourses, 1);
        this.updateCounterByText(stats.totalClasses, 2);

        // 更新收入显示
        const revenueElements = document.querySelectorAll('[th\\:text*="monthlyRevenue"]');
        revenueElements.forEach(el => {
            el.textContent = stats.monthlyRevenue || '¥0.00';
        });

        // 更新其他统计
        this.updateElementBySelector('[th\\:text*="totalTeachers"]', stats.totalTeachers);
        this.updateElementBySelector('[th\\:text*="activeSchedules"]', stats.activeSchedules);
        this.updateElementBySelector('[th\\:text*="totalUsers"]', stats.totalUsers);
        this.updateElementBySelector('[th\\:text*="pendingPayments"]', stats.pendingPayments);

        // 更新快速统计
        if (stats.quickStats) {
            this.updateCounterByText(stats.quickStats.todayPayments, 3);
            this.updateCounterByText(stats.quickStats.onlineUsers, 4);
            this.updateCounterByText(stats.quickStats.systemAlerts, 5);

            // 更新今日收入
            const todayRevenueText = stats.quickStats.todayRevenue ?
                '¥' + parseFloat(stats.quickStats.todayRevenue).toFixed(2) : '¥0.00';
            this.updateElementBySelector('[th\\:text*="todayRevenue"]', todayRevenueText);
        }

        // 启动数字动画
        this.animateCounters();
    }

    /**
     * 通过索引更新计数器
     */
    updateCounterByText(value, index) {
        const counters = document.querySelectorAll('.counter');
        if (counters[index]) {
            counters[index].textContent = value || 0;
            counters[index].setAttribute('data-target', value || 0);
        }
    }

    /**
     * 通过选择器更新元素
     */
    updateElementBySelector(selector, value) {
        const elements = document.querySelectorAll(selector);
        elements.forEach(element => {
            element.textContent = value || 0;
        });
    }

    /**
     * 更新计数器元素
     */
    updateCounterElement(selector, value) {
        const elements = document.querySelectorAll(selector);
        elements.forEach(element => {
            element.textContent = value || 0;
            element.setAttribute('data-target', value || 0);
        });
    }

    /**
     * 更新文本元素
     */
    updateTextElement(selector, value) {
        const elements = document.querySelectorAll(selector);
        elements.forEach(element => {
            element.textContent = value || 0;
        });
    }

    /**
     * 数字动画效果
     */
    animateCounters() {
        const counters = document.querySelectorAll('.counter[data-target]');
        
        counters.forEach(counter => {
            const target = parseInt(counter.getAttribute('data-target')) || 0;
            const duration = 2000; // 2秒动画
            const step = target / (duration / 16); // 60fps
            let current = 0;

            const timer = setInterval(() => {
                current += step;
                if (current >= target) {
                    current = target;
                    clearInterval(timer);
                }
                counter.textContent = Math.floor(current).toLocaleString();
            }, 16);
        });
    }

    /**
     * 初始化所有图表
     */
    async initializeAllCharts() {
        try {
            showLoading('正在加载图表数据...');

            console.log('开始请求图表数据...');
            
            // 确保有有效的token
            await this.ensureAuthentication();
            
            const response = await apiClient.get('/api/dashboard/chart-data');
            console.log('图表数据API响应:', response);

            if (response && response.success !== false) {
                const chartData = response.data || response;
                
                // 验证数据完整性
                if (!chartData || !this.validateChartData(chartData)) {
                    throw new Error('服务器返回的图表数据格式不正确');
                }

                // 确保数据格式正确
                const studentData = this.processChartData(chartData.studentTrendData);
                const courseData = this.processChartData(chartData.courseDistribution);
                const gradeData = this.processChartData(chartData.gradeDistribution);
                const revenueData = this.processChartData(chartData.revenueTrendData);

                console.log('处理后的图表数据:', {
                    student: studentData.length,
                    course: courseData.length,
                    grade: gradeData.length,
                    revenue: revenueData.length
                });

                // 初始化各个图表
                await Promise.all([
                    this.initializeStudentChart(studentData),
                    this.initializeCourseChart(courseData),
                    this.initializeGradeChart(gradeData),
                    this.initializeRevenueChart(revenueData)
                ]);

                console.log('所有图表初始化完成');
            } else {
                throw new Error('图表数据API返回失败状态');
            }
        } catch (error) {
            console.error('初始化图表失败:', error);
            console.error('错误详情:', error.stack);

            // 检查是否是认证问题
            if (this.isAuthenticationError(error)) {
                this.handleAuthenticationError(error);
                this.showChartError('用户认证失败，无法加载图表数据');
                return;
            }

            // 检查是否是网络问题
            if (this.isNetworkError(error)) {
                this.showChartError('网络连接失败，无法加载图表数据');
                return;
            }

            // 严格模式：图表数据获取失败时显示错误，不使用模拟数据
            this.showChartError('无法从服务器获取图表数据。请确保：1) 后端API /api/dashboard/chart-data 正常 2) 数据库连接正常 3) 用户有访问权限');
        } finally {
            hideLoading();
        }
    }

    /**
     * 验证后端数据接口可用性
     */
    async validateBackendAPIs() {
        const apis = [
            { name: '统计数据', endpoint: '/api/dashboard/stats' },
            { name: '图表数据', endpoint: '/api/dashboard/chart-data' },
            { name: '通知数据', endpoint: '/api/dashboard/notifications' },
            { name: '活动数据', endpoint: '/api/dashboard/activities' }
        ];
        
        const results = [];
        
        for (const api of apis) {
            try {
                await this.ensureAuthentication();
                const response = await apiClient.get(api.endpoint);
                results.push({
                    name: api.name,
                    endpoint: api.endpoint,
                    status: 'success',
                    available: true
                });
            } catch (error) {
                results.push({
                    name: api.name,
                    endpoint: api.endpoint,
                    status: 'error',
                    available: false,
                    error: error.message
                });
            }
        }
        
        return results;
    }

    /**
     * 处理图表数据格式
     */
    processChartData(data) {
        if (!Array.isArray(data)) {
            console.error('图表数据格式错误，必须是数组类型:', typeof data);
            throw new Error('后端返回的图表数据格式不正确，期望数组类型但收到: ' + typeof data);
        }

        if (data.length === 0) {
            console.warn('图表数据为空数组');
            throw new Error('后端返回的图表数据为空，请检查数据库是否有相关数据');
        }

        return data.map((item, index) => {
            if (!item || typeof item !== 'object') {
                throw new Error(`图表数据项 ${index} 格式错误，期望对象类型但收到: ${typeof item}`);
            }
            
            return {
                label: item.label || item.name || `项目${index + 1}`,
                value: item.value || item.count || 0,
                color: item.color || this.generateColor(index)
            };
        });
    }

    /**
     * 生成颜色
     */
    generateColor(index) {
        const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#6f42c1', '#e83e8c', '#fd7e14'];
        return colors[index % colors.length];
    }

    /**
     * 验证图表数据完整性
     */
    validateChartData(chartData) {
        if (!chartData || typeof chartData !== 'object') {
            console.warn('图表数据不是有效对象');
            return false;
        }

        const requiredFields = ['studentTrendData', 'courseDistribution', 'gradeDistribution', 'revenueTrendData'];
        for (const field of requiredFields) {
            if (!chartData[field] || !Array.isArray(chartData[field])) {
                console.warn(`缺少必需的图表数据字段: ${field}`);
                return false;
            }
        }

        return true;
    }

    /**
     * 显示数据错误
     */
    showDataError(message) {
        const errorHtml = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <strong>数据加载失败：</strong>${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        const container = document.querySelector('.content-wrapper');
        if (container) {
            container.insertAdjacentHTML('afterbegin', errorHtml);
        }
    }

    /**
     * 显示网络错误
     */
    showNetworkError(message) {
        const errorHtml = `
            <div class="network-error">
                <i class="fas fa-wifi"></i>
                <h6>网络连接错误</h6>
                <p>${message}</p>
                <button class="btn btn-primary btn-sm" onclick="location.reload()">
                    <i class="fas fa-redo me-1"></i>重新加载
                </button>
                <button class="btn btn-secondary btn-sm" onclick="window.dashboardData.checkConnection()">
                    <i class="fas fa-network-wired me-1"></i>检查连接
                </button>
            </div>
        `;

        const container = document.querySelector('.content-wrapper');
        if (container) {
            container.insertAdjacentHTML('afterbegin', errorHtml);
        }
    }

    /**
     * 隐藏统计卡片
     */
    hideStatsCards() {
        const statsCards = document.querySelectorAll('.stats-card');
        statsCards.forEach(card => {
            const cardBody = card.querySelector('.card-body');
            if (cardBody) {
                cardBody.innerHTML = `
                    <div class="error-state">
                        <i class="fas fa-exclamation-triangle"></i>
                        <h5>数据加载失败</h5>
                        <p>无法从服务器获取统计数据</p>
                        <button class="btn btn-sm btn-primary" onclick="window.dashboardData.loadDashboardStats()">
                            <i class="fas fa-redo me-1"></i>重试
                        </button>
                    </div>
                `;
            }
        });
    }

    /**
     * 检查网络连接
     */
    async checkConnection() {
        try {
            showLoading('检查网络连接...');
            
            // 确保有有效的token
            await this.ensureAuthentication();
            
            const response = await apiClient.get('/api/health');
            if (response && response.status === 'UP') {
                showAlert('网络连接正常，尝试重新加载数据...', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                showAlert('服务器连接异常，请稍后重试', 'warning');
            }
        } catch (error) {
            console.error('网络检查失败:', error);
            showAlert('网络连接失败，请检查网络设置', 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 检查是否是认证错误
     */
    isAuthenticationError(error) {
        if (!error || !error.message) return false;
        
        const authErrorMessages = [
            '未登录',
            '用户认证失败',
            'token已过期',
            'token无效',
            'Token无效',
            'Authentication failed',
            'Unauthorized',
            '401',
            'HTTP 401'
        ];
        
        return authErrorMessages.some(msg => error.message.includes(msg));
    }

    /**
     * 检查是否是网络错误
     */
    isNetworkError(error) {
        if (!error || !error.message) return false;
        
        const networkErrorMessages = [
            'Failed to fetch',
            '网络错误',
            'Network Error',
            'ERR_NETWORK',
            'ERR_INTERNET_DISCONNECTED',
            'Connection failed'
        ];
        
        return networkErrorMessages.some(msg => error.message.includes(msg));
    }

    /**
     * 处理认证错误
     */
    handleAuthenticationError(error) {
        console.warn('认证错误:', error.message);
        
        // 停止自动刷新
        this.stopAutoRefresh();
        
        // 显示认证错误信息
        this.showDataError('用户认证失败，请重新登录');
        
        // 清除本地token
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        
        // 延迟跳转到登录页面
        setTimeout(() => {
            window.location.href = '/admin/login?error=authentication_failed';
        }, 3000);
    }

    /**
     * 确保用户已认证
     */
    async ensureAuthentication() {
        try {
            // 检查是否有token
            let token = apiClient.getToken();
            
            if (!token) {
                console.log('本地没有token，尝试从服务器获取...');
                token = await apiClient.fetchTokenFromServer();
                
                if (!token) {
                    throw new Error('用户未登录，无法获取认证token');
                }
            }
            
            // 验证token是否有效
            if (window.jwtManager) {
                const tokenInfo = await window.jwtManager.getTokenInfo();
                if (!tokenInfo || !tokenInfo.valid) {
                    console.warn('Token无效，尝试刷新...');
                    await window.jwtManager.manualRefresh();
                }
            }
            
            console.log('认证检查通过');
            return true;
        } catch (error) {
            console.error('认证检查失败:', error);
            throw new Error('用户认证失败: ' + error.message);
        }
    }

    /**
     * 初始化学生统计图表
     */
    async initializeStudentChart(data) {
        const ctx = document.getElementById('studentChart');
        if (!ctx) {
            console.warn('学生图表容器未找到');
            return;
        }

        // 销毁现有图表
        if (this.chartInstances.studentChart) {
            this.chartInstances.studentChart.destroy();
        }

        // 处理数据
        const labels = data.map(item => item.label);
        const values = data.map(item => parseInt(item.value) || 0);

        // 创建图表
        this.chartInstances.studentChart = new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '学生注册数量',
                    data: values,
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#4e73df',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: '#ffffff',
                        bodyColor: '#ffffff',
                        borderColor: '#4e73df',
                        borderWidth: 1
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: '时间'
                        },
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: '学生数量'
                        },
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    }
                },
                interaction: {
                    mode: 'nearest',
                    axis: 'x',
                    intersect: false
                }
            }
        });
    }

    /**
     * 初始化课程分布图表
     */
    async initializeCourseChart(data) {
        const ctx = document.getElementById('courseChart');
        if (!ctx) {
            console.warn('课程图表容器未找到');
            return;
        }

        // 销毁现有图表
        if (this.chartInstances.courseChart) {
            this.chartInstances.courseChart.destroy();
        }

        // 处理数据
        const labels = data.map(item => item.label);
        const values = data.map(item => parseInt(item.value) || 0);
        const colors = data.map(item => item.color || '#4e73df');

        // 创建图表
        this.chartInstances.courseChart = new Chart(ctx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: values,
                    backgroundColor: colors,
                    borderColor: '#ffffff',
                    borderWidth: 2,
                    hoverBorderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: '#ffffff',
                        bodyColor: '#ffffff',
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1);
                                return `${context.label}: ${context.parsed} (${percentage}%)`;
                            }
                        }
                    }
                },
                cutout: '60%'
            }
        });
    }

    /**
     * 初始化年级分布图表
     */
    async initializeGradeChart(data) {
        const ctx = document.getElementById('gradeChart');
        if (!ctx) {
            console.warn('年级图表容器未找到');
            return;
        }

        // 销毁现有图表
        if (this.chartInstances.gradeChart) {
            this.chartInstances.gradeChart.destroy();
        }

        // 处理数据
        const labels = data.map(item => item.label);
        const values = data.map(item => parseInt(item.value) || 0);

        // 创建图表
        this.chartInstances.gradeChart = new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '学生数量',
                    data: values,
                    backgroundColor: 'rgba(54, 185, 204, 0.8)',
                    borderColor: '#36b9cc',
                    borderWidth: 1,
                    borderRadius: 4,
                    borderSkipped: false
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: '#ffffff',
                        bodyColor: '#ffffff',
                        callbacks: {
                            label: function(context) {
                                return `${context.label}: ${context.parsed.y} 人`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: '年级'
                        },
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: '学生数量'
                        },
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化收入趋势图表
     */
    async initializeRevenueChart(data) {
        const ctx = document.getElementById('revenueChart');
        if (!ctx) {
            console.warn('收入图表容器未找到');
            return;
        }

        // 销毁现有图表
        if (this.chartInstances.revenueChart) {
            this.chartInstances.revenueChart.destroy();
        }

        // 处理数据
        const labels = data.map(item => item.label);
        const values = data.map(item => parseFloat(item.value) || 0);

        // 创建图表
        this.chartInstances.revenueChart = new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '收入金额 (元)',
                    data: values,
                    borderColor: '#1cc88a',
                    backgroundColor: 'rgba(28, 200, 138, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#1cc88a',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 6,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: '#ffffff',
                        bodyColor: '#ffffff',
                        callbacks: {
                            label: function(context) {
                                return `${context.dataset.label}: ¥${context.parsed.y.toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        display: true,
                        title: {
                            display: true,
                            text: '时间'
                        },
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        display: true,
                        title: {
                            display: true,
                            text: '收入金额 (元)'
                        },
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        },
                        ticks: {
                            callback: function(value) {
                                return '¥' + value.toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 刷新所有数据
     */
    async refreshAllData() {
        try {
            showLoading('正在刷新仪表盘数据...');

            // 确保有有效的token
            await this.ensureAuthentication();

            // 清除缓存并刷新数据
            const response = await apiClient.post('/api/dashboard/refresh');

            // 检查响应是否成功（包容性检查）
            if (response && (response.success !== false)) {
                // 重新加载统计数据
                await this.loadDashboardStats();

                // 重新初始化图表
                await this.initializeAllCharts();

                showAlert('仪表盘数据刷新成功', 'success');
            } else {
                console.warn('刷新API返回非成功状态:', response);
                // 即使API返回失败，也尝试重新加载数据
                await this.loadDashboardStats();
                await this.initializeAllCharts();
                showAlert('数据已重新加载', 'info');
            }
        } catch (error) {
            console.error('刷新数据失败:', error);

            // 即使刷新API失败，也尝试重新加载数据
            try {
                await this.loadDashboardStats();
                await this.initializeAllCharts();
                showAlert('数据已重新加载（刷新API失败）', 'warning');
            } catch (reloadError) {
                console.error('重新加载数据也失败:', reloadError);
                showAlert('刷新数据失败: ' + error.message, 'error');
            }
        } finally {
            hideLoading();
        }
    }

    /**
     * 刷新所有图表
     */
    async refreshAllCharts() {
        try {
            showLoading('正在刷新图表...');
            await this.initializeAllCharts();
            showAlert('图表刷新成功', 'success');
        } catch (error) {
            console.error('刷新图表失败:', error);
            showAlert('刷新图表失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 导出图表数据
     */
    async exportChartData(chartType) {
        try {
            console.log('开始导出图表数据，类型:', chartType);
            showLoading('正在导出数据...');

            let data = [];
            let filename = '';

            // 检查图表实例状态
            console.log('当前图表实例:', Object.keys(this.chartInstances));

            // 从当前图表实例获取数据，避免API调用
            switch (chartType) {
                case 'student':
                    const studentChart = this.chartInstances.studentChart;
                    if (studentChart && studentChart.data && studentChart.data.labels) {
                        data = studentChart.data.labels.map((label, index) => ({
                            '时间': label,
                            '学生数量': studentChart.data.datasets[0].data[index] || 0
                        }));
                    } else {
                        throw new Error('学生图表数据不可用，无法导出。请先确保图表数据已正确加载。');
                    }
                    filename = '学生注册趋势数据.csv';
                    break;
                case 'course':
                    const courseChart = this.chartInstances.courseChart;
                    if (courseChart && courseChart.data && courseChart.data.labels) {
                        data = courseChart.data.labels.map((label, index) => ({
                            '课程类型': label,
                            '数量': courseChart.data.datasets[0].data[index] || 0
                        }));
                    } else {
                        throw new Error('课程图表数据不可用，无法导出。请先确保图表数据已正确加载。');
                    }
                    filename = '课程分布数据.csv';
                    break;
                case 'grade':
                    const gradeChart = this.chartInstances.gradeChart;
                    if (gradeChart && gradeChart.data && gradeChart.data.labels) {
                        data = gradeChart.data.labels.map((label, index) => ({
                            '年级': label,
                            '学生数量': gradeChart.data.datasets[0].data[index] || 0
                        }));
                    } else {
                        throw new Error('年级图表数据不可用，无法导出。请先确保图表数据已正确加载。');
                    }
                    filename = '年级分布数据.csv';
                    break;
                case 'revenue':
                    const revenueChart = this.chartInstances.revenueChart;
                    if (revenueChart && revenueChart.data && revenueChart.data.labels) {
                        data = revenueChart.data.labels.map((label, index) => ({
                            '时间': label,
                            '收入金额': revenueChart.data.datasets[0].data[index] || 0
                        }));
                    } else {
                        throw new Error('收入图表数据不可用，无法导出。请先确保图表数据已正确加载。');
                    }
                    filename = '收入趋势数据.csv';
                    break;
                default:
                    throw new Error('不支持的图表类型: ' + chartType);
            }

            if (data.length === 0) {
                throw new Error('没有可导出的数据');
            }

            console.log('准备导出的数据:', data);
            console.log('文件名:', filename);

            // 创建CSV数据并下载
            const csvData = this.convertToCSV(data);
            if (!csvData) {
                throw new Error('CSV数据转换失败');
            }

            this.downloadCSV(csvData, filename);
            console.log('导出完成:', filename);
            showAlert('数据导出成功', 'success');
        } catch (error) {
            console.error('导出数据失败:', error);
            showAlert('导出数据失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 切换图表类型
     */
    toggleChartType(chartId) {
        try {
            const chart = this.chartInstances[chartId];
            if (!chart) {
                console.warn('图表实例未找到:', chartId);
                showAlert('图表实例未找到', 'warning');
                return;
            }

            const currentType = chart.config.type;
            let newType;

            switch (currentType) {
                case 'line':
                    newType = 'bar';
                    break;
                case 'bar':
                    newType = 'line';
                    break;
                case 'doughnut':
                    newType = 'pie';
                    break;
                case 'pie':
                    newType = 'doughnut';
                    break;
                default:
                    console.warn('不支持的图表类型切换:', currentType);
                    showAlert('不支持的图表类型切换', 'warning');
                    return;
            }

            // 深拷贝数据和配置，避免引用问题
            const data = JSON.parse(JSON.stringify(chart.data));
            const options = JSON.parse(JSON.stringify(chart.options));

            // 销毁旧图表
            chart.destroy();
            delete this.chartInstances[chartId];

            // 等待一小段时间确保DOM更新
            setTimeout(() => {
                try {
                    // 创建新图表
                    const ctx = document.getElementById(chartId);
                    if (ctx) {
                        // 清理canvas
                        const parent = ctx.parentNode;
                        const newCanvas = document.createElement('canvas');
                        newCanvas.id = chartId;
                        newCanvas.height = ctx.height;
                        parent.replaceChild(newCanvas, ctx);

                        // 创建新图表实例
                        this.chartInstances[chartId] = new Chart(newCanvas.getContext('2d'), {
                            type: newType,
                            data: data,
                            options: options
                        });

                        showAlert(`图表类型已切换为 ${newType}`, 'success');
                    } else {
                        console.error('找不到图表容器:', chartId);
                        showAlert('找不到图表容器', 'error');
                    }
                } catch (error) {
                    console.error('创建新图表失败:', error);
                    showAlert('切换图表类型失败', 'error');
                }
            }, 100);
        } catch (error) {
            console.error('切换图表类型失败:', error);
            showAlert('切换图表类型失败: ' + error.message, 'error');
        }
    }

    /**
     * 显示图表错误
     */
    showChartError(message = '无法获取图表数据，请检查网络连接或稍后重试') {
        const chartContainers = [
            'studentChart',
            'courseChart',
            'gradeChart',
            'revenueChart'
        ];

        chartContainers.forEach(chartId => {
            const container = document.getElementById(chartId);
            if (container) {
                const parent = container.parentElement;
                parent.innerHTML = `
                    <div class="chart-error">
                        <i class="fas fa-exclamation-triangle"></i>
                        <h6>图表加载失败</h6>
                        <p>${message}</p>
                        <button class="btn btn-sm btn-outline-primary" onclick="window.dashboardData.refreshAllCharts()">
                            <i class="fas fa-redo me-1"></i>重新加载
                        </button>
                        <button class="btn btn-sm btn-outline-secondary ms-2" onclick="window.dashboardData.checkConnection()">
                            <i class="fas fa-network-wired me-1"></i>检查连接
                        </button>
                    </div>
                `;
            }
        });
    }

    /**
     * 显示错误消息
     */
    showErrorMessage(message) {
        const errorHtml = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        const container = document.querySelector('.content-wrapper');
        if (container) {
            container.insertAdjacentHTML('afterbegin', errorHtml);
        }
    }

    /**
     * 更新最后刷新时间
     */
    updateLastRefreshTime() {
        this.lastUpdateTime = new Date();
        const timeElement = document.getElementById('lastUpdateTime');
        if (timeElement) {
            timeElement.textContent = this.formatTime(this.lastUpdateTime);
        }
    }

    /**
     * 格式化时间
     */
    formatTime(date) {
        if (!date) return '未知';

        const now = new Date();
        const diff = now - date;

        if (diff < 60000) { // 1分钟内
            return '刚刚';
        } else if (diff < 3600000) { // 1小时内
            return Math.floor(diff / 60000) + '分钟前';
        } else if (diff < 86400000) { // 24小时内
            return Math.floor(diff / 3600000) + '小时前';
        } else {
            return date.toLocaleDateString('zh-CN') + ' ' +
                   date.toLocaleTimeString('zh-CN', {
                       hour: '2-digit',
                       minute: '2-digit'
                   });
        }
    }

    /**
     * 启动自动刷新
     */
    startAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }

        // 每5分钟自动刷新一次
        this.refreshInterval = setInterval(() => {
            if (this.autoRefreshEnabled) {
                console.log('自动刷新仪表盘数据...');
                this.loadDashboardStats();
            }
        }, 5 * 60 * 1000);

        this.autoRefreshEnabled = true;
        console.log('自动刷新已启动');
    }

    /**
     * 停止自动刷新
     */
    stopAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
        this.autoRefreshEnabled = false;
        console.log('自动刷新已停止');
    }

    /**
     * 转换为CSV格式
     */
    convertToCSV(data) {
        if (!Array.isArray(data) || data.length === 0) {
            console.warn('没有数据可转换为CSV');
            return '';
        }

        try {
            const headers = Object.keys(data[0]);
            const csvContent = [
                // 添加BOM以支持中文
                '\ufeff' + headers.join(','),
                ...data.map(row =>
                    headers.map(header => {
                        let value = row[header];
                        // 处理各种数据类型
                        if (value === null || value === undefined) {
                            value = '';
                        } else if (typeof value === 'object') {
                            value = JSON.stringify(value);
                        } else {
                            value = String(value);
                        }

                        // 如果包含逗号、引号或换行符，需要用引号包围
                        if (value.includes(',') || value.includes('"') || value.includes('\n')) {
                            value = `"${value.replace(/"/g, '""')}"`;
                        }

                        return value;
                    }).join(',')
                )
            ].join('\n');

            return csvContent;
        } catch (error) {
            console.error('转换CSV失败:', error);
            return '';
        }
    }

    /**
     * 下载CSV文件
     */
    downloadCSV(csvContent, filename) {
        try {
            if (!csvContent) {
                throw new Error('没有内容可下载');
            }

            // 创建Blob对象
            const blob = new Blob([csvContent], {
                type: 'text/csv;charset=utf-8;'
            });

            // 创建下载链接
            const link = document.createElement('a');

            if (link.download !== undefined) {
                const url = URL.createObjectURL(blob);
                link.setAttribute('href', url);
                link.setAttribute('download', filename);
                link.style.visibility = 'hidden';

                // 添加到DOM并触发下载
                document.body.appendChild(link);
                link.click();

                // 清理
                document.body.removeChild(link);
                URL.revokeObjectURL(url);

                console.log('文件下载成功:', filename);
            } else {
                throw new Error('浏览器不支持文件下载');
            }
        } catch (error) {
            console.error('下载文件失败:', error);
            throw error;
        }
    }

    /**
     * 提取图表类型
     */
    extractChartType(onclickStr) {
        const match = onclickStr.match(/exportChartData\(['"]([^'"]+)['"]\)/);
        return match ? match[1] : '';
    }

    /**
     * 提取图表ID
     */
    extractChartId(onclickStr) {
        const match = onclickStr.match(/toggleChartType\(['"]([^'"]+)['"]\)/);
        return match ? match[1] : '';
    }

    /**
     * 刷新通知
     */
    async refreshNotifications() {
        try {
            console.log('刷新通知数据...');
            showLoading('正在刷新通知...');

            // 确保有有效的token
            await this.ensureAuthentication();

            const response = await apiClient.get('/api/dashboard/notifications');
            if (response && response.success !== false) {
                console.log('通知数据刷新成功');
                showAlert('通知已刷新', 'success');
                // 这里可以更新通知显示
            } else {
                console.warn('通知API返回失败，使用模拟数据');
                showAlert('通知已刷新（模拟数据）', 'info');
            }
        } catch (error) {
            console.error('刷新通知失败:', error);
            showAlert('通知刷新失败，请稍后重试', 'warning');
        } finally {
            hideLoading();
        }
    }

    /**
     * 刷新活动
     */
    async refreshActivities() {
        try {
            console.log('刷新活动数据...');
            showLoading('正在刷新活动...');

            // 确保有有效的token
            await this.ensureAuthentication();

            const response = await apiClient.get('/api/dashboard/activities');
            if (response && response.success !== false) {
                console.log('活动数据刷新成功');
                showAlert('活动已刷新', 'success');
                // 这里可以更新活动显示
            } else {
                console.warn('活动API返回失败，使用模拟数据');
                showAlert('活动已刷新（模拟数据）', 'info');
            }
        } catch (error) {
            console.error('刷新活动失败:', error);
            showAlert('活动刷新失败，请稍后重试', 'warning');
        } finally {
            hideLoading();
        }
    }

    /**
     * 销毁实例
     */
    destroy() {
        // 停止自动刷新
        this.stopAutoRefresh();

        // 销毁所有图表实例
        Object.values(this.chartInstances).forEach(chart => {
            if (chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });

        // 清空数据
        this.chartInstances = {};
        this.lastUpdateTime = null;

        console.log('仪表盘数据管理器已销毁');
    }

    /**
     * 触发数据准备完成事件
     */
    dispatchDataReadyEvent() {
        const event = new CustomEvent('dashboardDataReady', {
            detail: {
                timestamp: new Date(),
                message: '仪表盘数据加载完成'
            }
        });
        document.dispatchEvent(event);
    }

    /**
     * 触发数据错误事件
     */
    dispatchDataErrorEvent(errorMessage) {
        const event = new CustomEvent('dashboardDataError', {
            detail: {
                timestamp: new Date(),
                message: errorMessage,
                error: true
            }
        });
        document.dispatchEvent(event);
    }
}

// 全局实例
let dashboardData;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 确保在仪表盘页面才初始化
    if (document.getElementById('studentChart') ||
        document.querySelector('.stats-card') ||
        document.querySelector('[onclick*="refreshDashboardData"]')) {

        dashboardData = new DashboardDataManager();

        // 将实例暴露到全局作用域
        window.dashboardData = dashboardData;

        console.log('仪表盘数据管理器已初始化');
    }
});

// 页面卸载时清理资源
window.addEventListener('beforeunload', function() {
    if (window.dashboardData) {
        window.dashboardData.destroy();
    }
});

// 全局错误处理函数
window.showAlert = window.showAlert || function(message, type = 'info') {
    console.log(`[${type.toUpperCase()}] ${message}`);

    // 如果有MessageUtils，使用它
    if (window.MessageUtils) {
        switch(type) {
            case 'success':
                MessageUtils.success(message);
                break;
            case 'error':
                MessageUtils.error(message);
                break;
            case 'warning':
                MessageUtils.warning(message);
                break;
            default:
                MessageUtils.info(message);
        }
    } else {
        // 简单的alert替代
        alert(`${type.toUpperCase()}: ${message}`);
    }
};

window.showLoading = window.showLoading || function(message = '加载中...') {
    console.log('Loading:', message);
    if (window.LoadingManager) {
        LoadingManager.showPageLoading('.content-wrapper');
    }
};

window.hideLoading = window.hideLoading || function() {
    console.log('Hide loading');
    if (window.LoadingManager) {
        LoadingManager.hidePageLoading('.content-wrapper');
    }
};

// 全局函数，供HTML模板调用
function refreshDashboardData() {
    if (window.dashboardData) {
        window.dashboardData.refreshAllData();
    } else {
        console.warn('dashboardData 实例未找到');
    }
}

function refreshCharts() {
    if (window.dashboardData) {
        window.dashboardData.refreshAllCharts();
    } else {
        console.warn('dashboardData 实例未找到');
    }
}

function exportChartData(chartType) {
    console.log('全局exportChartData函数被调用，类型:', chartType);
    if (window.dashboardData) {
        window.dashboardData.exportChartData(chartType);
    } else {
        console.warn('dashboardData 实例未找到');
        alert('数据管理器未初始化，请刷新页面重试');
    }
}

function toggleChartType(chartId) {
    if (window.dashboardData) {
        window.dashboardData.toggleChartType(chartId);
    } else {
        console.warn('dashboardData 实例未找到');
    }
}

// 快速操作函数 - 重定向到相应管理页面
function openStudentModal() {
    console.log('跳转到学生管理页面');
    showAlert('正在跳转到学生管理...', 'info');
    setTimeout(() => {
        window.location.href = '/admin/students';
    }, 500);
}

function openCourseModal() {
    console.log('跳转到课程管理页面');
    showAlert('正在跳转到课程管理...', 'info');
    setTimeout(() => {
        window.location.href = '/admin/courses';
    }, 500);
}

function openClassModal() {
    console.log('跳转到班级管理页面');
    showAlert('正在跳转到班级管理...', 'info');
    setTimeout(() => {
        // 实际路径是 /admin/academic/classes
        window.location.href = '/admin/academic/classes';
    }, 500);
}

function openScheduleModal() {
    console.log('跳转到课表管理页面');
    showAlert('正在跳转到课表管理...', 'info');
    setTimeout(() => {
        window.location.href = '/admin/academic/schedules';
    }, 500);
}

function openPaymentModal() {
    console.log('跳转到财务管理页面');
    showAlert('正在跳转到财务管理...', 'info');
    setTimeout(() => {
        window.location.href = '/admin/finance';
    }, 500);
}

function openReportModal() {
    console.log('跳转到财务报表页面');
    showAlert('正在跳转到财务报表...', 'info');
    setTimeout(() => {
        window.location.href = '/admin/finance/reports';
    }, 500);
}

// 通知相关函数
function refreshNotifications() {
    console.log('刷新通知');
    if (window.dashboardData) {
        window.dashboardData.refreshNotifications();
    } else {
        showAlert('通知刷新功能开发中...', 'info');
    }
}

// 活动相关函数
function refreshActivities() {
    console.log('刷新活动');
    if (window.dashboardData) {
        window.dashboardData.refreshActivities();
    } else {
        showAlert('活动刷新功能开发中...', 'info');
    }
}

function loadMoreActivities() {
    console.log('加载更多活动');
    showAlert('加载更多活动功能开发中...', 'info');
}
