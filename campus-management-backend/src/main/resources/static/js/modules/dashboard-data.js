/**
 * 仪表盘数据管理模块
 * 负责获取和处理真实的数据库数据，生成图表和统计信息
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
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
            
            // 加载统计数据
            await this.loadDashboardStats();
            
            // 初始化图表
            await this.initializeAllCharts();
            
            // 绑定事件
            this.bindEvents();
            
            // 启动自动刷新
            this.startAutoRefresh();
            
            console.log('仪表盘数据管理器初始化完成');
        } catch (error) {
            console.error('仪表盘数据管理器初始化失败:', error);
            this.showErrorMessage('仪表盘初始化失败: ' + error.message);
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

        // 图表操作事件
        document.addEventListener('click', (e) => {
            if (e.target.closest('[onclick*="refreshCharts"]')) {
                e.preventDefault();
                this.refreshAllCharts();
            }
            if (e.target.closest('[onclick*="exportChartData"]')) {
                e.preventDefault();
                const chartType = this.extractChartType(e.target.getAttribute('onclick'));
                this.exportChartData(chartType);
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
            
            const response = await apiClient.get('/api/dashboard/stats');
            
            if (response.success) {
                this.updateStatsCards(response.data);
                this.updateLastRefreshTime();
                console.log('统计数据加载成功');
            } else {
                throw new Error(response.message || '获取统计数据失败');
            }
        } catch (error) {
            console.error('加载统计数据失败:', error);
            this.showErrorMessage('加载统计数据失败: ' + error.message);
        } finally {
            hideLoading();
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

            const response = await apiClient.get('/api/dashboard/chart-data');

            if (response.success) {
                const chartData = response.data;

                // 确保数据格式正确
                const studentData = this.processChartData(chartData.studentTrendData);
                const courseData = this.processChartData(chartData.courseDistribution);
                const gradeData = this.processChartData(chartData.gradeDistribution);
                const revenueData = this.processChartData(chartData.revenueTrendData);

                // 初始化各个图表
                await Promise.all([
                    this.initializeStudentChart(studentData),
                    this.initializeCourseChart(courseData),
                    this.initializeGradeChart(gradeData),
                    this.initializeRevenueChart(revenueData)
                ]);

                console.log('所有图表初始化完成');
            } else {
                throw new Error(response.message || '获取图表数据失败');
            }
        } catch (error) {
            console.error('初始化图表失败:', error);
            this.showChartError();
        } finally {
            hideLoading();
        }
    }

    /**
     * 处理图表数据格式
     */
    processChartData(data) {
        if (!Array.isArray(data)) {
            console.warn('图表数据格式错误，返回空数组');
            return [];
        }

        return data.map(item => ({
            label: item.label || item.name || '未知',
            value: item.value || item.count || 0,
            color: item.color || '#4e73df'
        }));
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

            // 清除缓存并刷新数据
            const response = await apiClient.post('/api/dashboard/refresh');

            if (response.success) {
                // 重新加载统计数据
                await this.loadDashboardStats();

                // 重新初始化图表
                await this.initializeAllCharts();

                showAlert('仪表盘数据刷新成功', 'success');
            } else {
                throw new Error(response.message || '刷新数据失败');
            }
        } catch (error) {
            console.error('刷新数据失败:', error);
            showAlert('刷新数据失败: ' + error.message, 'error');
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
            showLoading('正在导出数据...');

            let endpoint = '';
            let filename = '';

            switch (chartType) {
                case 'student':
                    endpoint = '/api/dashboard/charts/student-trend';
                    filename = '学生注册趋势数据.xlsx';
                    break;
                case 'course':
                    endpoint = '/api/dashboard/charts/course-distribution';
                    filename = '课程分布数据.xlsx';
                    break;
                case 'grade':
                    endpoint = '/api/dashboard/charts/grade-distribution';
                    filename = '年级分布数据.xlsx';
                    break;
                case 'revenue':
                    endpoint = '/api/dashboard/charts/revenue-trend';
                    filename = '收入趋势数据.xlsx';
                    break;
                default:
                    throw new Error('不支持的图表类型');
            }

            const response = await apiClient.get(endpoint);

            if (response.success) {
                // 创建CSV数据
                const csvData = this.convertToCSV(response.data);
                this.downloadCSV(csvData, filename);
                showAlert('数据导出成功', 'success');
            } else {
                throw new Error(response.message || '导出数据失败');
            }
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
        if (!chart) {
            console.warn('图表实例未找到:', chartId);
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
                return;
        }

        // 保存数据和配置
        const data = chart.data;
        const options = chart.options;

        // 销毁旧图表
        chart.destroy();

        // 创建新图表
        const ctx = document.getElementById(chartId);
        if (ctx) {
            this.chartInstances[chartId] = new Chart(ctx.getContext('2d'), {
                type: newType,
                data: data,
                options: options
            });

            showAlert(`图表类型已切换为 ${newType}`, 'info');
        }
    }

    /**
     * 显示图表错误
     */
    showChartError() {
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
                    <div class="text-center p-4">
                        <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                        <h6 class="text-muted">图表加载失败</h6>
                        <p class="text-muted small">无法获取图表数据，请检查网络连接或稍后重试</p>
                        <button class="btn btn-sm btn-outline-primary" onclick="dashboardData.refreshAllCharts()">
                            <i class="fas fa-redo"></i> 重新加载
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
            return '';
        }

        const headers = Object.keys(data[0]);
        const csvContent = [
            headers.join(','),
            ...data.map(row =>
                headers.map(header => {
                    const value = row[header];
                    return typeof value === 'string' && value.includes(',')
                        ? `"${value}"`
                        : value;
                }).join(',')
            )
        ].join('\n');

        return csvContent;
    }

    /**
     * 下载CSV文件
     */
    downloadCSV(csvContent, filename) {
        const blob = new Blob(['\ufeff' + csvContent], {
            type: 'text/csv;charset=utf-8;'
        });
        const link = document.createElement('a');

        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', filename);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
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

// 全局函数，供HTML模板调用
function refreshDashboardData() {
    if (window.dashboardData) {
        window.dashboardData.refreshAllData();
    }
}

function refreshCharts() {
    if (window.dashboardData) {
        window.dashboardData.refreshAllCharts();
    }
}

function exportChartData(chartType) {
    if (window.dashboardData) {
        window.dashboardData.exportChartData(chartType);
    }
}

function toggleChartType(chartId) {
    if (window.dashboardData) {
        window.dashboardData.toggleChartType(chartId);
    }
}
