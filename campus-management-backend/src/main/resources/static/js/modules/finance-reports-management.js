/**
 * 财务报表管理模块
 */

class FinanceReportsManager {
    constructor() {
        this.apiBase = '/admin/api/finance/reports';
        this.currentReportType = 'month';
        this.currentYear = new Date().getFullYear();
        this.currentMonth = new Date().getMonth() + 1;
        this.charts = {};
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadReportData();
        this.initializeCharts();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 报表类型选择
        const reportTypeSelect = document.getElementById('reportType');
        if (reportTypeSelect) {
            reportTypeSelect.addEventListener('change', (e) => this.handleReportTypeChange(e.target.value));
        }

        // 年份选择
        const yearSelect = document.getElementById('year');
        if (yearSelect) {
            yearSelect.addEventListener('change', (e) => this.handleYearChange(e.target.value));
        }

        // 月份选择
        const monthSelect = document.getElementById('month');
        if (monthSelect) {
            monthSelect.addEventListener('change', (e) => this.handleMonthChange(e.target.value));
        }

        // 生成报表按钮
        const generateBtn = document.querySelector('button[onclick="generateReport()"]');
        if (generateBtn) {
            generateBtn.onclick = () => this.generateReport();
        }

        // 导出按钮
        const exportExcelBtn = document.querySelector('button[onclick="exportReport(\'excel\')"]');
        if (exportExcelBtn) {
            exportExcelBtn.onclick = () => this.exportReport('excel');
        }

        const exportPdfBtn = document.querySelector('button[onclick="exportReport(\'pdf\')"]');
        if (exportPdfBtn) {
            exportPdfBtn.onclick = () => this.exportReport('pdf');
        }

        // 刷新按钮
        const refreshBtn = document.getElementById('refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => this.refreshData());
        }
    }

    /**
     * 加载报表数据
     */
    async loadReportData() {
        try {
            this.showLoading();
            const params = new URLSearchParams({
                type: this.currentReportType,
                year: this.currentYear,
                month: this.currentMonth
            });

            const response = await fetch(`${this.apiBase}?${params}`);
            if (!response.ok) {
                throw new Error('获取报表数据失败');
            }

            const data = await response.json();
            this.renderStatistics(data.statistics);
            this.updateCharts(data.charts);
            this.renderFeeTypeStats(data.feeTypeStats);
        } catch (error) {
            console.error('Error loading report data:', error);
            this.showError('加载报表数据失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染统计数据
     */
    renderStatistics(stats) {
        if (!stats) return;

        // 更新统计卡片
        this.updateStatCard('totalIncome', stats.totalIncome || 0);
        this.updateStatCard('monthlyIncome', stats.monthlyIncome || 0);
        this.updateStatCard('paymentRecords', stats.paymentRecords || 0);
        this.updateStatCard('unpaidAmount', stats.unpaidAmount || 0);
    }

    /**
     * 更新统计卡片
     */
    updateStatCard(type, value) {
        const cards = {
            totalIncome: { selector: '.stats-card:nth-child(1) .text-lg', formatter: this.formatCurrency },
            monthlyIncome: { selector: '.stats-card:nth-child(2) .text-lg', formatter: this.formatCurrency },
            paymentRecords: { selector: '.stats-card:nth-child(3) .text-lg', formatter: this.formatNumber },
            unpaidAmount: { selector: '.stats-card:nth-child(4) .text-lg', formatter: this.formatNumber }
        };

        const card = cards[type];
        if (card) {
            const element = document.querySelector(card.selector);
            if (element) {
                element.textContent = card.formatter(value);
            }
        }
    }

    /**
     * 初始化图表
     */
    initializeCharts() {
        this.initMonthlyRevenueChart();
        this.initFeeTypeChart();
    }

    /**
     * 初始化月度收入趋势图
     */
    initMonthlyRevenueChart() {
        const ctx = document.getElementById('monthlyRevenueChart');
        if (!ctx) return;

        const chartData = {
            labels: [],
            datasets: [{
                label: '月度收入',
                data: [],
                borderColor: '#4e73df',
                backgroundColor: 'rgba(78, 115, 223, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.3
            }]
        };

        this.charts.monthlyRevenue = new Chart(ctx, {
            type: 'line',
            data: chartData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                return `收入: ¥${this.formatNumber(context.parsed.y)}`;
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
                        ticks: {
                            callback: (value) => {
                                return '¥' + this.formatNumber(value);
                            }
                        }
                    }
                },
                elements: {
                    point: {
                        radius: 4,
                        hoverRadius: 6
                    }
                }
            }
        });
    }

    /**
     * 初始化费用类型分布图
     */
    initFeeTypeChart() {
        const ctx = document.getElementById('feeTypeChart');
        if (!ctx) return;

        const chartData = {
            labels: [],
            datasets: [{
                data: [],
                backgroundColor: [
                    '#4e73df',
                    '#1cc88a',
                    '#36b9cc',
                    '#f6c23e',
                    '#e74a3b'
                ],
                borderWidth: 2,
                borderColor: '#ffffff'
            }]
        };

        this.charts.feeType = new Chart(ctx, {
            type: 'doughnut',
            data: chartData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                const label = context.label || '';
                                const value = context.parsed;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((value / total) * 100).toFixed(1);
                                return `${label}: ¥${this.formatNumber(value)} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 更新图表数据
     */
    updateCharts(chartData) {
        if (!chartData) return;

        // 更新月度收入趋势图
        if (chartData.monthlyData && this.charts.monthlyRevenue) {
            this.charts.monthlyRevenue.data.labels = chartData.monthlyData.map(item => item.month);
            this.charts.monthlyRevenue.data.datasets[0].data = chartData.monthlyData.map(item => item.revenue);
            this.charts.monthlyRevenue.update();
        }

        // 更新费用类型分布图
        if (chartData.feeTypeData && this.charts.feeType) {
            this.charts.feeType.data.labels = chartData.feeTypeData.map(item => item.type);
            this.charts.feeType.data.datasets[0].data = chartData.feeTypeData.map(item => item.amount);
            this.charts.feeType.update();
        }
    }

    /**
     * 渲染费用类型统计表格
     */
    renderFeeTypeStats(stats) {
        const tbody = document.querySelector('.table tbody');
        if (!tbody || !stats) return;

        tbody.innerHTML = stats.map(stat => `
            <tr>
                <td>${this.escapeHtml(stat.type)}</td>
                <td>¥${this.formatNumber(stat.amount)}</td>
                <td>
                    <span>${stat.percentage}%</span>
                    <div class="progress mt-1" style="height: 5px;">
                        <div class="progress-bar bg-primary" style="width: ${stat.percentage}%"></div>
                    </div>
                </td>
                <td>
                    <i class="fas fa-arrow-${stat.trend > 0 ? 'up text-success' : stat.trend < 0 ? 'down text-danger' : 'right text-muted'}"></i>
                    <small class="text-muted">${stat.trend > 0 ? '+' : ''}${stat.trend}%</small>
                </td>
            </tr>
        `).join('');
    }

    /**
     * 处理报表类型变化
     */
    handleReportTypeChange(type) {
        this.currentReportType = type;
        
        // 根据报表类型显示/隐藏月份选择
        const monthSelect = document.getElementById('month');
        if (monthSelect) {
            monthSelect.closest('.col-md-3').style.display = type === 'month' ? 'block' : 'none';
        }
    }

    /**
     * 处理年份变化
     */
    handleYearChange(year) {
        this.currentYear = parseInt(year);
    }

    /**
     * 处理月份变化
     */
    handleMonthChange(month) {
        this.currentMonth = parseInt(month);
    }

    /**
     * 生成报表
     */
    async generateReport() {
        try {
            this.showLoading();
            
            // 更新当前参数
            const reportTypeSelect = document.getElementById('reportType');
            const yearSelect = document.getElementById('year');
            const monthSelect = document.getElementById('month');
            
            if (reportTypeSelect) this.currentReportType = reportTypeSelect.value;
            if (yearSelect) this.currentYear = parseInt(yearSelect.value);
            if (monthSelect) this.currentMonth = parseInt(monthSelect.value);

            // 重新加载数据
            await this.loadReportData();
            
            this.showSuccess('报表生成成功');
        } catch (error) {
            console.error('Error generating report:', error);
            this.showError('生成报表失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 导出报表
     */
    async exportReport(format) {
        try {
            this.showLoading();
            
            const params = new URLSearchParams({
                format: format,
                type: this.currentReportType,
                year: this.currentYear,
                month: this.currentMonth
            });

            const response = await fetch(`${this.apiBase}/export?${params}`, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('导出失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            
            // 生成文件名
            const fileName = this.generateFileName(format);
            a.download = fileName;
            
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            this.showSuccess(`${format.toUpperCase()}报表导出成功`);
        } catch (error) {
            console.error('Error exporting report:', error);
            this.showError('导出失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 生成文件名
     */
    generateFileName(format) {
        const typeNames = {
            'month': '月度',
            'quarter': '季度',
            'year': '年度'
        };
        
        const typeName = typeNames[this.currentReportType] || '';
        const extension = format === 'pdf' ? 'pdf' : 'xlsx';
        
        let fileName = `财务报表_${typeName}_${this.currentYear}`;
        if (this.currentReportType === 'month') {
            fileName += `_${this.currentMonth}月`;
        }
        fileName += `.${extension}`;
        
        return fileName;
    }

    /**
     * 刷新数据
     */
    async refreshData() {
        try {
            await this.loadReportData();
            this.showSuccess('数据已刷新');
        } catch (error) {
            this.showError('刷新失败：' + error.message);
        }
    }

    /**
     * 打印报表
     */
    printReport() {
        const printContent = this.generatePrintContent();
        const printWindow = window.open('', '_blank', 'width=800,height=600');
        
        printWindow.document.write(`
            <!DOCTYPE html>
            <html>
            <head>
                <title>财务报表</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .stats { display: flex; justify-content: space-around; margin-bottom: 30px; }
                    .stat-card { text-align: center; padding: 20px; border: 1px solid #ddd; }
                    .table { width: 100%; border-collapse: collapse; }
                    .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .table th { background-color: #f2f2f2; }
                    @media print { body { margin: 0; } }
                </style>
            </head>
            <body>
                ${printContent}
            </body>
            </html>
        `);
        
        printWindow.document.close();
        printWindow.focus();
        printWindow.print();
    }

    /**
     * 生成打印内容
     */
    generatePrintContent() {
        const typeNames = {
            'month': '月度',
            'quarter': '季度',
            'year': '年度'
        };
        
        const title = `${typeNames[this.currentReportType] || ''}财务报表 - ${this.currentYear}年${this.currentReportType === 'month' ? this.currentMonth + '月' : ''}`;
        
        return `
            <div class="header">
                <h1>${title}</h1>
                <p>生成时间：${new Date().toLocaleString('zh-CN')}</p>
            </div>
            <div class="stats">
                <!-- 这里可以添加统计数据 -->
            </div>
            <div class="content">
                <!-- 这里可以添加详细数据表格 -->
            </div>
        `;
    }

    /**
     * 显示图表详情
     */
    showChartDetails(chartType) {
        // 创建模态框显示图表详细信息
        const modal = this.createChartDetailsModal(chartType);
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        // 模态框关闭后移除
        modal.addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modal);
        });
    }

    /**
     * 创建图表详情模态框
     */
    createChartDetailsModal(chartType) {
        const modal = document.createElement('div');
        modal.className = 'modal fade modal-finance';
        modal.innerHTML = `
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${chartType === 'revenue' ? '收入趋势详情' : '费用类型详情'}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="chart-container" style="height: 400px;">
                            <canvas id="detailChart_${chartType}"></canvas>
                        </div>
                        <div class="mt-3">
                            <h6>数据分析</h6>
                            <p>这里可以显示详细的数据分析和说明...</p>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" onclick="financeReportsManager.exportChartData('${chartType}')">导出数据</button>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    /**
     * 导出图表数据
     */
    async exportChartData(chartType) {
        try {
            this.showLoading();
            
            const response = await fetch(`${this.apiBase}/chart-data/${chartType}`, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('导出图表数据失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `图表数据_${chartType}_${new Date().toISOString().slice(0, 10)}.xlsx`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            this.showSuccess('图表数据导出成功');
        } catch (error) {
            console.error('Error exporting chart data:', error);
            this.showError('导出图表数据失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    // 工具函数
    formatCurrency(amount) {
        return '¥' + this.formatNumber(amount);
    }

    formatNumber(num) {
        return parseFloat(num || 0).toLocaleString('zh-CN', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        });
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('zh-CN');
    }

    formatDateTime(dateString) {
        return new Date(dateString).toLocaleString('zh-CN');
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    showLoading() {
        const loading = document.createElement('div');
        loading.className = 'loading-overlay';
        loading.innerHTML = '<div class="loading-spinner"></div>';
        loading.id = 'loadingOverlay';
        document.body.appendChild(loading);
    }

    hideLoading() {
        const loading = document.getElementById('loadingOverlay');
        if (loading) {
            document.body.removeChild(loading);
        }
    }

    showSuccess(message) {
        this.showAlert(message, 'success');
    }

    showError(message) {
        this.showAlert(message, 'danger');
    }

    showAlert(message, type) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        const container = document.querySelector('.container-fluid') || document.body;
        container.insertAdjacentHTML('afterbegin', alertHtml);

        // 自动移除
        setTimeout(() => {
            const alert = container.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 5000);
    }
}

// 全局实例
let financeReportsManager;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    financeReportsManager = new FinanceReportsManager();
});

// 全局函数（用于模板中的onclick事件）
window.generateReport = () => financeReportsManager?.generateReport();
window.exportReport = (format) => financeReportsManager?.exportReport(format);
window.financeReportsManager = {
    showChartDetails: (type) => financeReportsManager?.showChartDetails(type),
    exportChartData: (type) => financeReportsManager?.exportChartData(type),
    printReport: () => financeReportsManager?.printReport()
};