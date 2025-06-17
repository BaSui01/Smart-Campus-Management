/**
 * 仪表盘组件
 * Dashboard Component
 */

class DashboardComponent {
    constructor() {
        this.charts = {};
        this.refreshInterval = null;
        this.isInitialized = false;
        this.config = {
            refreshInterval: 300000, // 5分钟
            animationDuration: 1000,
            chartColors: {
                primary: '#007bff',
                success: '#28a745',
                warning: '#ffc107',
                danger: '#dc3545',
                info: '#17a2b8'
            }
        };
    }

    /**
     * 初始化仪表盘
     */
    async init() {
        try {
            console.log('初始化仪表盘组件...');
            
            // 初始化图表
            await this.initCharts();
            
            // 初始化统计卡片动画
            this.initStatsCards();
            
            // 初始化通知系统
            this.initNotifications();
            
            // 初始化活动时间线
            this.initActivityTimeline();
            
            // 设置自动刷新
            this.setupAutoRefresh();
            
            // 绑定事件监听器
            this.bindEvents();
            
            this.isInitialized = true;
            console.log('仪表盘组件初始化完成');
            
            // 触发初始化完成事件
            this.dispatchEvent('dashboard:initialized');
            
        } catch (error) {
            console.error('仪表盘初始化失败:', error);
            this.showError('仪表盘初始化失败，请刷新页面重试');
        }
    }

    /**
     * 初始化图表
     */
    async initCharts() {
        // 检查Chart.js是否已加载
        if (typeof Chart === 'undefined') {
            await this.loadChartJS();
        }

        // 初始化学生统计图表
        this.initStudentChart();
        
        // 初始化课程分布图表
        this.initCourseChart();
        
        // 初始化年级分布图表
        this.initGradeChart();
        
        // 初始化收入趋势图表
        this.initRevenueChart();
    }

    /**
     * 动态加载Chart.js
     */
    loadChartJS() {
        return new Promise((resolve, reject) => {
            if (typeof Chart !== 'undefined') {
                resolve();
                return;
            }

            const script = document.createElement('script');
            script.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.min.js';
            script.onload = resolve;
            script.onerror = reject;
            document.head.appendChild(script);
        });
    }

    /**
     * 初始化学生统计图表
     */
    initStudentChart() {
        const ctx = document.getElementById('studentChart');
        if (!ctx) return;

        this.charts.student = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.getMonthLabels(),
                datasets: [{
                    label: '学生注册数',
                    data: [120, 135, 150, 165, 180, 195, 210, 225, 240, 255, 270, 285],
                    borderColor: this.config.chartColors.primary,
                    backgroundColor: this.config.chartColors.primary + '20',
                    tension: 0.4,
                    fill: true
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
                        mode: 'index',
                        intersect: false
                    }
                },
                scales: {
                    x: {
                        display: true,
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        display: true,
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0'
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
    initCourseChart() {
        const ctx = document.getElementById('courseChart');
        if (!ctx) return;

        this.charts.course = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['必修课', '选修课', '实践课'],
                datasets: [{
                    data: [45, 30, 25],
                    backgroundColor: [
                        this.config.chartColors.primary,
                        this.config.chartColors.success,
                        this.config.chartColors.info
                    ],
                    borderWidth: 0
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
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ' + context.parsed + '%';
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化年级分布图表
     */
    initGradeChart() {
        const ctx = document.getElementById('gradeChart');
        if (!ctx) return;

        this.charts.grade = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['一年级', '二年级', '三年级', '四年级'],
                datasets: [{
                    label: '学生人数',
                    data: [320, 285, 310, 275],
                    backgroundColor: [
                        this.config.chartColors.primary,
                        this.config.chartColors.success,
                        this.config.chartColors.warning,
                        this.config.chartColors.info
                    ],
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
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0'
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化收入趋势图表
     */
    initRevenueChart() {
        const ctx = document.getElementById('revenueChart');
        if (!ctx) return;

        this.charts.revenue = new Chart(ctx, {
            type: 'line',
            data: {
                labels: this.getRecentMonths(6),
                datasets: [{
                    label: '收入金额',
                    data: [85000, 92000, 88000, 95000, 102000, 98000],
                    borderColor: this.config.chartColors.success,
                    backgroundColor: this.config.chartColors.success + '20',
                    tension: 0.4,
                    fill: true
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
                        callbacks: {
                            label: function(context) {
                                return '收入: ¥' + context.parsed.y.toLocaleString();
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        grid: {
                            display: false
                        }
                    },
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: '#f0f0f0'
                        },
                        ticks: {
                            callback: function(value) {
                                return '¥' + (value / 1000) + 'K';
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 初始化统计卡片动画
     */
    initStatsCards() {
        const counters = document.querySelectorAll('.counter');
        
        counters.forEach(counter => {
            const target = parseInt(counter.getAttribute('data-target') || counter.textContent);
            this.animateCounter(counter, target);
        });
    }

    /**
     * 数字动画效果
     */
    animateCounter(element, target) {
        let current = 0;
        const increment = target / 100;
        const duration = this.config.animationDuration;
        const stepTime = duration / 100;

        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                current = target;
                clearInterval(timer);
            }
            element.textContent = Math.floor(current).toLocaleString();
        }, stepTime);
    }

    /**
     * 获取月份标签
     */
    getMonthLabels() {
        const months = [];
        const now = new Date();
        
        for (let i = 11; i >= 0; i--) {
            const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
            months.push(date.toLocaleDateString('zh-CN', { month: 'short' }));
        }
        
        return months;
    }

    /**
     * 获取最近几个月
     */
    getRecentMonths(count) {
        const months = [];
        const now = new Date();
        
        for (let i = count - 1; i >= 0; i--) {
            const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
            months.push(date.toLocaleDateString('zh-CN', { month: 'short' }));
        }
        
        return months;
    }

    /**
     * 初始化通知系统
     */
    initNotifications() {
        // 标记通知为已读
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-action="mark-read"]')) {
                this.markNotificationAsRead(e.target.closest('.notification-item'));
            }
        });
    }

    /**
     * 初始化活动时间线
     */
    initActivityTimeline() {
        // 活动详情展示
        document.addEventListener('click', (e) => {
            if (e.target.closest('[data-action="show-activity"]')) {
                this.showActivityDetails(e.target.closest('.timeline-item'));
            }
        });
    }

    /**
     * 设置自动刷新
     */
    setupAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }

        this.refreshInterval = setInterval(() => {
            this.refreshData();
        }, this.config.refreshInterval);
    }

    /**
     * 绑定事件监听器
     */
    bindEvents() {
        // 窗口大小改变时重新调整图表
        window.addEventListener('resize', Utils.debounce(() => {
            this.resizeCharts();
        }, 250));

        // 页面可见性改变时处理自动刷新
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.pauseAutoRefresh();
            } else {
                this.resumeAutoRefresh();
            }
        });
    }

    /**
     * 刷新数据
     */
    async refreshData() {
        try {
            console.log('刷新仪表盘数据...');
            
            // 显示加载状态
            this.showLoading();
            
            // 这里应该调用API获取最新数据
            // const data = await API.get('/admin/dashboard/stats');
            
            // 模拟API调用
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            // 更新图表数据
            this.updateCharts();
            
            // 更新统计卡片
            this.updateStatsCards();
            
            // 更新最后更新时间
            this.updateLastUpdateTime();
            
            // 隐藏加载状态
            this.hideLoading();
            
            this.showSuccess('数据刷新成功');
            
        } catch (error) {
            console.error('刷新数据失败:', error);
            this.hideLoading();
            this.showError('数据刷新失败，请稍后重试');
        }
    }

    /**
     * 运行系统诊断
     */
    async runDiagnostics() {
        try {
            this.showInfo('正在运行系统诊断...');
            
            // 模拟诊断过程
            await new Promise(resolve => setTimeout(resolve, 2000));
            
            this.showSuccess('系统诊断完成，一切正常');
            
        } catch (error) {
            console.error('系统诊断失败:', error);
            this.showError('系统诊断失败');
        }
    }

    /**
     * 更新图表
     */
    updateCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart && typeof chart.update === 'function') {
                chart.update();
            }
        });
    }

    /**
     * 调整图表大小
     */
    resizeCharts() {
        Object.values(this.charts).forEach(chart => {
            if (chart && typeof chart.resize === 'function') {
                chart.resize();
            }
        });
    }

    /**
     * 更新统计卡片
     */
    updateStatsCards() {
        // 这里可以更新统计卡片的数据
        console.log('更新统计卡片数据');
    }

    /**
     * 更新最后更新时间
     */
    updateLastUpdateTime() {
        const timeElement = document.getElementById('lastUpdateTime');
        if (timeElement) {
            timeElement.textContent = new Date().toLocaleTimeString('zh-CN');
        }
    }

    /**
     * 显示/隐藏加载状态
     */
    showLoading() {
        const indicator = document.getElementById('dataLoadingIndicator');
        if (indicator) {
            indicator.style.display = 'block';
        }
    }

    hideLoading() {
        const indicator = document.getElementById('dataLoadingIndicator');
        if (indicator) {
            indicator.style.display = 'none';
        }
    }

    /**
     * 暂停/恢复自动刷新
     */
    pauseAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
    }

    resumeAutoRefresh() {
        if (!this.refreshInterval) {
            this.setupAutoRefresh();
        }
    }

    /**
     * 标记通知为已读
     */
    markNotificationAsRead(item) {
        if (item) {
            item.classList.remove('unread');
            item.classList.add('read');
        }
    }

    /**
     * 显示活动详情
     */
    showActivityDetails(item) {
        if (item) {
            const activity = item.querySelector('.timeline-content h6').textContent;
            this.showInfo(`活动详情: ${activity}`);
        }
    }

    /**
     * 消息提示方法
     */
    showSuccess(message) {
        this.showToast(message, 'success');
    }

    showError(message) {
        this.showToast(message, 'error');
    }

    showInfo(message) {
        this.showToast(message, 'info');
    }

    showToast(message, type = 'info') {
        // 这里应该调用全局的Toast组件
        if (window.Toast) {
            window.Toast.show(message, type);
        } else {
            console.log(`[${type.toUpperCase()}] ${message}`);
        }
    }

    /**
     * 触发自定义事件
     */
    dispatchEvent(eventName, detail = {}) {
        const event = new CustomEvent(eventName, { detail });
        document.dispatchEvent(event);
    }

    /**
     * 销毁组件
     */
    destroy() {
        // 清理定时器
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }

        // 销毁图表
        Object.values(this.charts).forEach(chart => {
            if (chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });

        // 清理事件监听器
        // 这里应该移除所有绑定的事件监听器

        this.isInitialized = false;
        console.log('仪表盘组件已销毁');
    }
}

// 创建全局实例
window.Dashboard = new DashboardComponent();

// 页面加载完成后自动初始化
document.addEventListener('DOMContentLoaded', () => {
    if (document.body.getAttribute('data-current-page') === 'dashboard') {
        window.Dashboard.init();
    }
});
