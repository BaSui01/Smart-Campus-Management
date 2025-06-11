/**
 * 仪表盘管理模块
 * 提供仪表盘的数据刷新、图表管理、快速操作等功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */

class DashboardManagement {
    constructor() {
        this.chartInstances = {};
        this.refreshInterval = null;
        this.autoRefreshEnabled = false;
        this.lastUpdateTime = new Date();
        
        this.init();
    }

    /**
     * 初始化仪表盘
     */
    init() {
        this.bindEvents();
        this.initializeCounters();
        this.initializeCharts();
        this.updateLastRefreshTime();
        this.startAutoRefresh();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 刷新数据按钮
        const refreshBtn = document.querySelector('[onclick="refreshDashboardData()"]');
        if (refreshBtn) {
            refreshBtn.onclick = () => this.refreshDashboardData();
        }

        // 图表操作按钮
        document.addEventListener('click', (e) => {
            if (e.target.closest('[onclick*="refreshCharts"]')) {
                e.preventDefault();
                this.refreshCharts();
            }
            if (e.target.closest('[onclick*="exportChartData"]')) {
                e.preventDefault();
                const chartType = e.target.closest('[onclick*="exportChartData"]').getAttribute('onclick').match(/'([^']+)'/)[1];
                this.exportChartData(chartType);
            }
            if (e.target.closest('[onclick*="toggleChartType"]')) {
                e.preventDefault();
                const chartId = e.target.closest('[onclick*="toggleChartType"]').getAttribute('onclick').match(/'([^']+)'/)[1];
                this.toggleChartType(chartId);
            }
        });

        // 快速操作按钮
        this.bindQuickActionEvents();

        // 通知操作
        this.bindNotificationEvents();

        // 活动操作
        this.bindActivityEvents();
    }

    /**
     * 绑定快速操作事件
     */
    bindQuickActionEvents() {
        const quickActions = {
            'openStudentModal': () => window.location.href = '/admin/students',
            'openCourseModal': () => window.location.href = '/admin/academic/courses',
            'openClassModal': () => window.location.href = '/admin/academic/classes',
            'openScheduleModal': () => window.location.href = '/admin/academic/schedules',
            'openPaymentModal': () => window.location.href = '/admin/payments',
            'openReportModal': () => window.location.href = '/admin/reports',
            'showBatchOperations': () => this.showBatchOperations()
        };

        Object.keys(quickActions).forEach(action => {
            const elements = document.querySelectorAll(`[onclick="${action}()"]`);
            elements.forEach(element => {
                element.onclick = (e) => {
                    e.preventDefault();
                    quickActions[action]();
                };
            });
        });
    }

    /**
     * 绑定通知事件
     */
    bindNotificationEvents() {
        const notificationActions = {
            'markAllAsRead': () => this.markAllNotificationsAsRead(),
            'refreshNotifications': () => this.refreshNotifications(),
            'markAsRead': (element) => this.markNotificationAsRead(element),
            'deleteNotification': (element) => this.deleteNotification(element)
        };

        Object.keys(notificationActions).forEach(action => {
            const elements = document.querySelectorAll(`[onclick*="${action}"]`);
            elements.forEach(element => {
                element.onclick = (e) => {
                    e.preventDefault();
                    if (action === 'markAsRead' || action === 'deleteNotification') {
                        notificationActions[action](element);
                    } else {
                        notificationActions[action]();
                    }
                };
            });
        });
    }

    /**
     * 绑定活动事件
     */
    bindActivityEvents() {
        const activityActions = {
            'refreshActivities': () => this.refreshActivities(),
            'exportActivities': () => this.exportActivities(),
            'loadMoreActivities': () => this.loadMoreActivities(),
            'showActivityDetails': (element) => this.showActivityDetails(element)
        };

        Object.keys(activityActions).forEach(action => {
            const elements = document.querySelectorAll(`[onclick*="${action}"]`);
            elements.forEach(element => {
                element.onclick = (e) => {
                    e.preventDefault();
                    if (action === 'showActivityDetails') {
                        activityActions[action](element);
                    } else {
                        activityActions[action]();
                    }
                };
            });
        });
    }

    /**
     * 初始化数字动画
     */
    initializeCounters() {
        const counters = document.querySelectorAll('.counter');
        counters.forEach(counter => {
            const target = parseInt(counter.getAttribute('data-target')) || 0;
            const duration = 2000;
            const step = target / (duration / 16);
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
     * 初始化图表
     */
    async initializeCharts() {
        try {
            showLoading('正在加载图表数据...');
            
            // 获取图表数据
            const chartData = await this.fetchChartData();
            
            // 初始化各个图表
            this.initializeStudentChart(chartData.studentTrendData);
            this.initializeCourseChart(chartData.courseDistribution);
            this.initializeGradeChart(chartData.gradeDistribution);
            this.initializeRevenueChart(chartData.revenueTrendData);
            
            hideLoading();
        } catch (error) {
            console.error('图表初始化失败:', error);
            this.showChartError();
            hideLoading();
        }
    }

    /**
     * 获取图表数据
     */
    async fetchChartData() {
        try {
            const response = await apiClient.get('/api/v1/dashboard/chart-data');
            if (response.success) {
                return response.data;
            } else {
                throw new Error(response.message || '获取图表数据失败');
            }
        } catch (error) {
            console.warn('获取图表数据失败，使用模拟数据:', error);

            // 如果主接口失败，尝试获取统计数据
            try {
                const statsResponse = await apiClient.get('/api/v1/dashboard/stats');
                if (statsResponse.success) {
                    const stats = statsResponse.data;
                    return {
                        studentTrendData: stats.studentTrendData || this.getMockStudentTrendData(),
                        courseDistribution: stats.courseDistribution || this.getMockCourseDistribution(),
                        gradeDistribution: stats.gradeDistribution || this.getMockGradeDistribution(),
                        revenueTrendData: stats.revenueTrendData || this.getMockRevenueTrendData()
                    };
                }
            } catch (fallbackError) {
                console.warn('备用数据获取失败，使用模拟数据:', fallbackError);
            }

            // 返回模拟数据
            return {
                studentTrendData: this.getMockStudentTrendData(),
                courseDistribution: this.getMockCourseDistribution(),
                gradeDistribution: this.getMockGradeDistribution(),
                revenueTrendData: this.getMockRevenueTrendData()
            };
        }
    }

    /**
     * 获取模拟学生趋势数据
     */
    getMockStudentTrendData() {
        const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
        return months.map((month, index) => ({
            label: month,
            value: Math.floor(Math.random() * 50) + 20 + index * 5
        }));
    }

    /**
     * 获取模拟课程分布数据
     */
    getMockCourseDistribution() {
        return [
            { label: '计算机科学', value: 45, color: '#4e73df' },
            { label: '数学', value: 35, color: '#1cc88a' },
            { label: '英语', value: 30, color: '#36b9cc' },
            { label: '物理', value: 25, color: '#f6c23e' },
            { label: '化学', value: 20, color: '#e74a3b' }
        ];
    }

    /**
     * 获取模拟年级分布数据
     */
    getMockGradeDistribution() {
        return [
            { label: '2021级', value: 120 },
            { label: '2022级', value: 135 },
            { label: '2023级', value: 145 },
            { label: '2024级', value: 160 },
            { label: '2025级', value: 95 }
        ];
    }

    /**
     * 获取模拟收入趋势数据
     */
    getMockRevenueTrendData() {
        const months = ['1月', '2月', '3月', '4月', '5月', '6月'];
        return months.map((month, index) => ({
            label: month,
            value: (Math.floor(Math.random() * 50000) + 30000 + index * 5000).toString()
        }));
    }

    /**
     * 初始化学生统计图表
     */
    initializeStudentChart(data) {
        const ctx = document.getElementById('studentChart');
        if (!ctx) {
            console.warn('学生图表容器未找到');
            return;
        }

        if (this.chartInstances.studentChart) {
            this.chartInstances.studentChart.destroy();
        }

        // 确保数据是数组
        if (!Array.isArray(data)) {
            console.warn('学生图表数据格式错误，使用默认数据');
            data = this.getMockStudentTrendData();
        }

        const labels = data.map(item => item.label || '未知');
        const values = data.map(item => parseInt(item.value) || 0);

        this.chartInstances.studentChart = new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '新增学生',
                    data: values,
                    borderColor: '#4e73df',
                    backgroundColor: 'rgba(78, 115, 223, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#4e73df',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    intersect: false,
                    mode: 'index'
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        borderColor: '#4e73df',
                        borderWidth: 1,
                        cornerRadius: 8
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化课程分布图表
     */
    initializeCourseChart(data) {
        const ctx = document.getElementById('courseChart');
        if (!ctx) {
            console.warn('课程图表容器未找到');
            return;
        }

        if (this.chartInstances.courseChart) {
            this.chartInstances.courseChart.destroy();
        }

        // 确保数据是数组
        if (!Array.isArray(data)) {
            console.warn('课程图表数据格式错误，使用默认数据');
            data = this.getMockCourseDistribution();
        }

        const labels = data.map(item => item.label || '未知');
        const values = data.map(item => parseInt(item.value) || 0);
        const colors = data.map(item => item.color || '#4e73df');

        this.chartInstances.courseChart = new Chart(ctx.getContext('2d'), {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: values,
                    backgroundColor: colors,
                    borderWidth: 2,
                    borderColor: '#ffffff'
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
                        titleColor: 'white',
                        bodyColor: 'white',
                        cornerRadius: 8,
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
    initializeGradeChart(data) {
        const ctx = document.getElementById('gradeChart');
        if (!ctx) {
            console.warn('年级图表容器未找到');
            return;
        }

        if (this.chartInstances.gradeChart) {
            this.chartInstances.gradeChart.destroy();
        }

        // 确保数据是数组
        if (!Array.isArray(data)) {
            console.warn('年级图表数据格式错误，使用默认数据');
            data = this.getMockGradeDistribution();
        }

        const labels = data.map(item => item.label || '未知');
        const values = data.map(item => parseInt(item.value) || 0);

        this.chartInstances.gradeChart = new Chart(ctx.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '学生人数',
                    data: values,
                    backgroundColor: 'rgba(78, 115, 223, 0.8)',
                    borderColor: '#4e73df',
                    borderWidth: 1,
                    borderRadius: 4
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
                        titleColor: 'white',
                        bodyColor: 'white',
                        cornerRadius: 8
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化收入趋势图表
     */
    initializeRevenueChart(data) {
        const ctx = document.getElementById('revenueChart');
        if (!ctx) {
            console.warn('收入图表容器未找到');
            return;
        }

        if (this.chartInstances.revenueChart) {
            this.chartInstances.revenueChart.destroy();
        }

        // 确保数据是数组
        if (!Array.isArray(data)) {
            console.warn('收入图表数据格式错误，使用默认数据');
            data = this.getMockRevenueTrendData();
        }

        const labels = data.map(item => item.label || '未知');
        const values = data.map(item => parseFloat(item.value) || 0);

        this.chartInstances.revenueChart = new Chart(ctx.getContext('2d'), {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '收入金额',
                    data: values,
                    borderColor: '#1cc88a',
                    backgroundColor: 'rgba(28, 200, 138, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#1cc88a',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 5,
                    pointHoverRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: {
                    intersect: false,
                    mode: 'index'
                },
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        titleColor: 'white',
                        bodyColor: 'white',
                        borderColor: '#1cc88a',
                        borderWidth: 1,
                        cornerRadius: 8,
                        callbacks: {
                            label: function(context) {
                                return `收入: ¥${context.parsed.y.toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0,0,0,0.1)'
                        },
                        ticks: {
                            callback: function(value) {
                                return '¥' + value.toLocaleString();
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    /**
     * 刷新仪表盘数据
     */
    async refreshDashboardData() {
        try {
            showLoading('正在刷新数据...');

            // 刷新统计数据
            await this.refreshStats();

            // 刷新图表
            await this.refreshCharts();

            // 刷新通知
            await this.refreshNotifications();

            // 刷新活动
            await this.refreshActivities();

            // 更新时间
            this.updateLastRefreshTime();

            showAlert('数据刷新成功', 'success');
            hideLoading();
        } catch (error) {
            console.error('刷新数据失败:', error);
            showAlert('刷新数据失败: ' + error.message, 'error');
            hideLoading();
        }
    }

    /**
     * 刷新统计数据
     */
    async refreshStats() {
        try {
            const response = await apiClient.get('/api/v1/dashboard/stats');
            if (response.success) {
                this.updateStatsCards(response.data);
            } else {
                throw new Error(response.message || '获取统计数据失败');
            }
        } catch (error) {
            console.warn('刷新统计数据失败，使用模拟数据:', error);
            // 使用模拟数据
            this.updateStatsCards(this.getMockStatsData());
        }
    }

    /**
     * 获取模拟统计数据
     */
    getMockStatsData() {
        return {
            totalStudents: 1250,
            totalCourses: 85,
            totalClasses: 42,
            totalUsers: 156,
            totalTeachers: 78,
            activeSchedules: 24,
            pendingPayments: 15,
            quickStats: {
                todayPayments: 8,
                onlineUsers: 23,
                systemAlerts: 3
            }
        };
    }

    /**
     * 更新统计卡片
     */
    updateStatsCards(stats) {
        // 更新基础统计
        this.updateCounterElement('totalStudents', stats.totalStudents);
        this.updateCounterElement('totalCourses', stats.totalCourses);
        this.updateCounterElement('totalClasses', stats.totalClasses);
        this.updateCounterElement('totalUsers', stats.totalUsers);
        this.updateCounterElement('totalTeachers', stats.totalTeachers);
        this.updateCounterElement('activeSchedules', stats.activeSchedules);
        this.updateCounterElement('pendingPayments', stats.pendingPayments);

        // 更新快速统计
        if (stats.quickStats) {
            this.updateCounterElement('todayPayments', stats.quickStats.todayPayments);
            this.updateCounterElement('onlineUsers', stats.quickStats.onlineUsers);
            this.updateCounterElement('systemAlerts', stats.quickStats.systemAlerts);
        }
    }

    /**
     * 更新计数器元素
     */
    updateCounterElement(id, value) {
        const element = document.getElementById(id);
        if (element) {
            const target = parseInt(value) || 0;
            element.setAttribute('data-target', target);
            this.animateCounter(element, target);
        }
    }

    /**
     * 动画更新计数器
     */
    animateCounter(element, target) {
        const current = parseInt(element.textContent.replace(/,/g, '')) || 0;
        const duration = 1000;
        const step = (target - current) / (duration / 16);
        let currentValue = current;

        const timer = setInterval(() => {
            currentValue += step;
            if ((step > 0 && currentValue >= target) || (step < 0 && currentValue <= target)) {
                currentValue = target;
                clearInterval(timer);
            }
            element.textContent = Math.floor(currentValue).toLocaleString();
        }, 16);
    }

    /**
     * 刷新图表
     */
    async refreshCharts() {
        try {
            const chartData = await this.fetchChartData();

            // 重新初始化图表
            this.initializeStudentChart(chartData.studentTrendData);
            this.initializeCourseChart(chartData.courseDistribution);
            this.initializeGradeChart(chartData.gradeDistribution);
            this.initializeRevenueChart(chartData.revenueTrendData);

        } catch (error) {
            console.error('刷新图表失败:', error);
            this.showChartError();
        }
    }

    /**
     * 导出图表数据
     */
    async exportChartData(chartType) {
        try {
            showLoading('正在导出数据...');

            // 模拟导出功能
            const chartData = await this.fetchChartData();
            const data = chartData[chartType + 'Data'] || [];

            // 创建下载链接
            const blob = new Blob([JSON.stringify(data, null, 2)], {
                type: 'application/json'
            });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${chartType}_data_${new Date().toISOString().split('T')[0]}.json`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

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
        const chart = this.chartInstances[chartId];
        if (!chart) return;

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
                return;
        }

        // 保存数据
        const data = chart.data;
        const options = chart.options;

        // 销毁旧图表
        chart.destroy();

        // 创建新图表
        const ctx = document.getElementById(chartId);
        this.chartInstances[chartId] = new Chart(ctx.getContext('2d'), {
            type: newType,
            data: data,
            options: options
        });

        showAlert(`图表类型已切换为 ${newType}`, 'info');
    }

    /**
     * 显示图表错误
     */
    showChartError() {
        const charts = document.querySelectorAll('.chart-area, .chart-pie, .chart-bar');
        charts.forEach(chart => {
            chart.innerHTML = `
                <div class="text-center p-4">
                    <i class="fas fa-exclamation-triangle fa-2x text-warning mb-2"></i>
                    <br>
                    <small class="text-muted">图表加载失败，已使用模拟数据</small>
                    <br>
                    <button class="btn btn-sm btn-outline-primary mt-2" onclick="window.dashboardManagement.refreshCharts()">
                        <i class="fas fa-redo"></i> 重试
                    </button>
                </div>
            `;
        });
    }

    /**
     * 刷新图表
     */
    async refreshCharts() {
        try {
            showLoading('正在刷新图表数据...');
            await this.initializeCharts();
            showAlert('图表数据已刷新', 'success');
        } catch (error) {
            console.error('刷新图表失败:', error);
            showAlert('刷新图表失败', 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 刷新通知
     */
    async refreshNotifications() {
        try {
            const response = await apiClient.get('/api/v1/dashboard/notifications');
            if (response.success) {
                this.updateNotificationsList(response.data);
            } else {
                // 使用模拟通知数据
                this.updateNotificationsList(this.getMockNotifications());
            }
        } catch (error) {
            console.warn('刷新通知失败，使用模拟数据:', error);
            this.updateNotificationsList(this.getMockNotifications());
        }
    }

    /**
     * 获取模拟通知数据
     */
    getMockNotifications() {
        return [
            {
                id: 1,
                title: '系统维护通知',
                content: '系统将于今晚22:00-24:00进行维护，请提前保存数据。',
                sender: '系统管理员',
                createTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
                isRead: false
            },
            {
                id: 2,
                title: '新用户注册',
                content: '用户"张三"已成功注册，请及时审核。',
                sender: '用户管理系统',
                createTime: new Date(Date.now() - 4 * 60 * 60 * 1000).toISOString(),
                isRead: true
            },
            {
                id: 3,
                title: '课程安排更新',
                content: '计算机科学课程时间已调整，请查看最新安排。',
                sender: '教务处',
                createTime: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(),
                isRead: false
            }
        ];
    }

    /**
     * 更新通知列表
     */
    updateNotificationsList(notifications) {
        const container = document.querySelector('.notifications-list, #notificationsList');
        if (!container) return;

        if (!notifications || notifications.length === 0) {
            container.innerHTML = `
                <div class="text-center p-4">
                    <i class="fas fa-bell-slash fa-2x text-muted mb-2"></i>
                    <br>
                    <small class="text-muted">暂无新通知</small>
                </div>
            `;
            return;
        }

        container.innerHTML = notifications.map(notification => `
            <div class="list-group-item notification-item ${notification.isRead ? 'read' : 'unread'}">
                <div class="d-flex w-100 justify-content-between align-items-start">
                    <div class="flex-grow-1">
                        <h6 class="mb-1 d-flex align-items-center">
                            <i class="fas fa-circle ${notification.isRead ? 'text-muted' : 'text-primary'} me-2" style="font-size: 0.5rem;"></i>
                            ${notification.title}
                        </h6>
                        <p class="mb-1 text-muted">${notification.content}</p>
                        <small class="text-muted">
                            <i class="fas fa-user me-1"></i>${notification.sender}
                            <span class="mx-2">•</span>
                            <i class="fas fa-clock me-1"></i>${this.formatTime(notification.createTime)}
                        </small>
                    </div>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-link text-muted" type="button" data-bs-toggle="dropdown">
                            <i class="fas fa-ellipsis-v"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="#" onclick="dashboardManagement.markNotificationAsRead(this, ${notification.id})">
                                <i class="fas fa-check me-2"></i>标记已读
                            </a></li>
                            <li><a class="dropdown-item" href="#" onclick="dashboardManagement.deleteNotification(this, ${notification.id})">
                                <i class="fas fa-trash me-2"></i>删除
                            </a></li>
                        </ul>
                    </div>
                </div>
            </div>
        `).join('');
    }

    /**
     * 标记通知为已读
     */
    async markNotificationAsRead(element, notificationId) {
        try {
            const response = await apiClient.put(`/api/v1/dashboard/notifications/${notificationId}/read`);
            if (response.success) {
                const item = element.closest('.notification-item');
                item.classList.remove('unread');
                item.classList.add('read');
                const icon = item.querySelector('.fa-circle');
                icon.classList.remove('text-primary');
                icon.classList.add('text-muted');
                showAlert('通知已标记为已读', 'success');
            }
        } catch (error) {
            console.error('标记通知失败:', error);
            showAlert('操作失败', 'error');
        }
    }

    /**
     * 删除通知
     */
    async deleteNotification(element, notificationId) {
        if (!confirm('确定要删除这条通知吗？')) return;

        try {
            const response = await apiClient.delete(`/api/v1/dashboard/notifications/${notificationId}`);
            if (response.success) {
                const item = element.closest('.notification-item');
                item.remove();
                showAlert('通知已删除', 'success');
            }
        } catch (error) {
            console.error('删除通知失败:', error);
            showAlert('删除失败', 'error');
        }
    }

    /**
     * 标记所有通知为已读
     */
    async markAllNotificationsAsRead() {
        try {
            const response = await apiClient.put('/api/v1/dashboard/notifications/read-all');
            if (response.success) {
                const items = document.querySelectorAll('.notification-item.unread');
                items.forEach(item => {
                    item.classList.remove('unread');
                    item.classList.add('read');
                    const icon = item.querySelector('.fa-circle');
                    icon.classList.remove('text-primary');
                    icon.classList.add('text-muted');
                });
                showAlert('所有通知已标记为已读', 'success');
            }
        } catch (error) {
            console.error('标记所有通知失败:', error);
            showAlert('操作失败', 'error');
        }
    }

    /**
     * 刷新活动
     */
    async refreshActivities() {
        try {
            const response = await apiClient.get('/api/v1/dashboard/activities');
            if (response.success) {
                this.updateActivitiesList(response.data);
            } else {
                // 使用模拟活动数据
                this.updateActivitiesList(this.getMockActivities());
            }
        } catch (error) {
            console.warn('刷新活动失败，使用模拟数据:', error);
            this.updateActivitiesList(this.getMockActivities());
        }
    }

    /**
     * 获取模拟活动数据
     */
    getMockActivities() {
        return [
            {
                id: 1,
                title: '用户登录',
                description: '管理员admin登录系统',
                operator: 'admin',
                type: 'LOGIN',
                createTime: new Date(Date.now() - 10 * 60 * 1000).toISOString()
            },
            {
                id: 2,
                title: '创建用户',
                description: '创建了新用户"李四"',
                operator: 'admin',
                type: 'CREATE',
                createTime: new Date(Date.now() - 30 * 60 * 1000).toISOString()
            },
            {
                id: 3,
                title: '更新课程',
                description: '更新了"Java程序设计"课程信息',
                operator: '张老师',
                type: 'UPDATE',
                createTime: new Date(Date.now() - 60 * 60 * 1000).toISOString()
            },
            {
                id: 4,
                title: '学费缴纳',
                description: '学生王五完成学费缴纳',
                operator: '财务系统',
                type: 'PAYMENT',
                createTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
            },
            {
                id: 5,
                title: '系统备份',
                description: '系统自动备份完成',
                operator: '系统',
                type: 'SYSTEM',
                createTime: new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString()
            }
        ];
    }

    /**
     * 更新活动列表
     */
    updateActivitiesList(activities) {
        const container = document.querySelector('.activities-list, #activitiesList');
        if (!container) return;

        if (!activities || activities.length === 0) {
            container.innerHTML = `
                <div class="text-center p-4">
                    <i class="fas fa-history fa-2x text-muted mb-2"></i>
                    <br>
                    <small class="text-muted">暂无活动记录</small>
                </div>
            `;
            return;
        }

        container.innerHTML = activities.map(activity => `
            <div class="list-group-item activity-item">
                <div class="d-flex align-items-start">
                    <div class="activity-icon me-3">
                        <i class="fas ${this.getActivityIcon(activity.type)} text-${this.getActivityColor(activity.type)}"></i>
                    </div>
                    <div class="flex-grow-1">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h6 class="mb-1">${activity.title}</h6>
                                <p class="mb-1 text-muted">${activity.description}</p>
                                <small class="text-muted">
                                    <i class="fas fa-user me-1"></i>${activity.operator}
                                    <span class="mx-2">•</span>
                                    <i class="fas fa-clock me-1"></i>${this.formatTime(activity.createTime)}
                                </small>
                            </div>
                            <span class="badge bg-${this.getActivityColor(activity.type)}">${activity.type}</span>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    /**
     * 获取活动图标
     */
    getActivityIcon(type) {
        const icons = {
            'LOGIN': 'sign-in-alt',
            'CREATE': 'plus',
            'UPDATE': 'edit',
            'DELETE': 'trash',
            'PAYMENT': 'credit-card',
            'SYSTEM': 'cog',
            'DEFAULT': 'info-circle'
        };
        return icons[type] || icons.DEFAULT;
    }

    /**
     * 获取活动颜色
     */
    getActivityColor(type) {
        const colors = {
            'LOGIN': 'success',
            'CREATE': 'primary',
            'UPDATE': 'warning',
            'DELETE': 'danger',
            'PAYMENT': 'info',
            'SYSTEM': 'secondary',
            'DEFAULT': 'light'
        };
        return colors[type] || colors.DEFAULT;
    }

    /**
     * 显示批量操作
     */
    showBatchOperations() {
        showAlert('批量操作功能开发中...', 'info');
    }

    /**
     * 导出活动
     */
    async exportActivities() {
        try {
            showLoading('正在导出活动数据...');
            showAlert('导出功能开发中...', 'info');
        } catch (error) {
            console.error('导出活动失败:', error);
            showAlert('导出失败', 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 加载更多活动
     */
    async loadMoreActivities() {
        try {
            showLoading('正在加载更多活动...');
            showAlert('加载更多功能开发中...', 'info');
        } catch (error) {
            console.error('加载更多活动失败:', error);
            showAlert('加载失败', 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 显示活动详情
     */
    showActivityDetails(element) {
        const activityItem = element.closest('.activity-item');
        const title = activityItem.querySelector('h6').textContent;
        showAlert(`活动详情: ${title}`, 'info');
    }

    /**
     * 开始自动刷新
     */
    startAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }

        // 每5分钟自动刷新一次
        this.refreshInterval = setInterval(() => {
            if (this.autoRefreshEnabled) {
                this.refreshDashboardData();
            }
        }, 5 * 60 * 1000);

        this.autoRefreshEnabled = true;
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
    }

    /**
     * 更新最后刷新时间
     */
    updateLastRefreshTime() {
        this.lastUpdateTime = new Date();
        const timeElement = document.querySelector('.last-update-time');
        if (timeElement) {
            timeElement.textContent = this.formatTime(this.lastUpdateTime);
        }
    }

    /**
     * 格式化时间
     */
    formatTime(dateString) {
        if (!dateString) return '未知';
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;

        if (diff < 60000) { // 1分钟内
            return '刚刚';
        } else if (diff < 3600000) { // 1小时内
            return Math.floor(diff / 60000) + '分钟前';
        } else if (diff < 86400000) { // 24小时内
            return Math.floor(diff / 3600000) + '小时前';
        } else {
            return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    }

    /**
     * 销毁实例
     */
    destroy() {
        // 销毁图表实例
        Object.values(this.chartInstances).forEach(chart => {
            if (chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });

        // 清除定时器
        this.stopAutoRefresh();

        // 清空数据
        this.chartInstances = {};
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    window.dashboardManagement = new DashboardManagement();
});

// 页面卸载时清理资源
window.addEventListener('beforeunload', function() {
    if (window.dashboardManagement) {
        window.dashboardManagement.destroy();
    }
});
