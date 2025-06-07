/**
 * 财务管理页面JavaScript
 */

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('财务管理页面已加载');
    initFinancePage();
});

/**
 * 初始化财务页面
 */
function initFinancePage() {
    // 初始化图表
    initIncomeChart();
    initFeeTypeChart();
    
    // 绑定事件
    bindFinanceEvents();
}

/**
 * 绑定财务相关事件
 */
function bindFinanceEvents() {
    // 快速操作按钮
    const quickActionBtns = document.querySelectorAll('.btn-icon-split');
    quickActionBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            console.log('快速操作:', this.querySelector('.text').textContent);
        });
    });
}

/**
 * 初始化收入趋势图表
 */
function initIncomeChart() {
    const ctx = document.getElementById('incomeChart');
    if (!ctx) return;

    // 模拟收入数据
    const incomeData = {
        labels: ['1月', '2月', '3月', '4月', '5月', '6月'],
        datasets: [{
            label: '月收入',
            data: [100000, 110000, 120000, 115000, 125000, 130000],
            backgroundColor: 'rgba(78, 115, 223, 0.1)',
            borderColor: 'rgba(78, 115, 223, 1)',
            borderWidth: 2,
            fill: true,
            tension: 0.3
        }]
    };

    new Chart(ctx, {
        type: 'line',
        data: incomeData,
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
                    ticks: {
                        callback: function(value) {
                            return '¥' + value.toLocaleString();
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
 * 初始化费用类型分布图表
 */
function initFeeTypeChart() {
    const ctx = document.getElementById('feeTypeChart');
    if (!ctx) return;

    // 模拟费用类型数据
    const feeTypeData = {
        labels: ['学费', '住宿费', '教材费', '其他费用'],
        datasets: [{
            data: [800000, 300000, 100000, 50000],
            backgroundColor: [
                '#4e73df',
                '#1cc88a',
                '#36b9cc',
                '#f6c23e'
            ],
            borderWidth: 2,
            borderColor: '#ffffff'
        }]
    };

    new Chart(ctx, {
        type: 'doughnut',
        data: feeTypeData,
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
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = ((value / total) * 100).toFixed(1);
                            return `${label}: ¥${value.toLocaleString()} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

/**
 * 导出财务数据
 */
function exportFinanceData() {
    showLoading('正在导出财务数据...');

    // 模拟导出操作
    setTimeout(() => {
        hideLoading();
        
        // 创建模拟的CSV数据
        const csvData = [
            ['类型', '金额', '百分比'],
            ['学费', '800000', '64%'],
            ['住宿费', '300000', '24%'],
            ['教材费', '100000', '8%'],
            ['其他费用', '50000', '4%']
        ];

        // 转换为CSV格式
        const csvContent = csvData.map(row => row.join(',')).join('\n');
        
        // 下载文件
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', '财务数据_' + new Date().toISOString().slice(0, 10) + '.csv');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showAlert('财务数据导出成功', 'success');
    }, 1000);
}

/**
 * 刷新财务统计
 */
function refreshFinanceStats() {
    showLoading('正在刷新财务统计...');

    // 模拟刷新操作
    setTimeout(() => {
        hideLoading();
        showAlert('财务统计已刷新', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1500);
}

/**
 * 生成财务报告
 */
function generateFinanceReport() {
    showLoading('正在生成财务报告...');

    // 模拟报告生成
    setTimeout(() => {
        hideLoading();
        showAlert('财务报告生成成功', 'success');
        
        // 可以跳转到报告页面或下载报告
        setTimeout(() => {
            window.location.href = '/admin/finance/reports';
        }, 1000);
    }, 2000);
}

/**
 * 查看收支明细
 */
function viewTransactionDetails(type) {
    console.log('查看收支明细:', type);
    
    // 根据类型跳转到相应页面
    switch (type) {
        case 'income':
            window.location.href = '/admin/finance/payment-records?status=已缴费';
            break;
        case 'unpaid':
            window.location.href = '/admin/finance/payment-records?status=待缴费';
            break;
        case 'overdue':
            window.location.href = '/admin/finance/payment-records?status=逾期';
            break;
        default:
            window.location.href = '/admin/finance/payment-records';
    }
}

/**
 * 发送缴费提醒
 */
function sendPaymentReminder() {
    if (!confirm('确定要发送缴费提醒给所有未缴费学生吗？')) {
        return;
    }

    showLoading('正在发送缴费提醒...');

    // 模拟发送提醒
    setTimeout(() => {
        hideLoading();
        showAlert('缴费提醒发送成功', 'success');
    }, 2000);
}

/**
 * 批量导入缴费记录
 */
function batchImportPayments() {
    // 创建文件输入元素
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = '.xlsx,.xls,.csv';
    fileInput.style.display = 'none';
    
    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (!file) return;
        
        showLoading('正在导入缴费记录...');
        
        // 模拟导入过程
        setTimeout(() => {
            hideLoading();
            showAlert(`成功导入缴费记录：${file.name}`, 'success');
            
            // 刷新页面
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }, 2000);
    });
    
    document.body.appendChild(fileInput);
    fileInput.click();
    document.body.removeChild(fileInput);
}

/**
 * 查看财务趋势分析
 */
function viewTrendAnalysis() {
    showLoading('正在加载趋势分析...');
    
    setTimeout(() => {
        hideLoading();
        // 可以打开模态框或跳转到分析页面
        showAlert('趋势分析功能开发中...', 'info');
    }, 1000);
}

// 工具函数
function showAlert(message, type = 'info') {
    const alertClass = {
        'success': 'alert-success',
        'error': 'alert-danger',
        'warning': 'alert-warning',
        'info': 'alert-info'
    };

    const alertHtml = `
        <div class="alert ${alertClass[type]} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    const container = document.querySelector('.container-fluid');
    if (container) {
        container.insertAdjacentHTML('afterbegin', alertHtml);
        
        setTimeout(() => {
            const alert = container.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 3000);
    }
}

function showLoading(message = '加载中...') {
    console.log('Loading:', message);
}

function hideLoading() {
    console.log('Loading hidden');
}

// 格式化货币
function formatCurrency(amount) {
    return '¥' + parseFloat(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// 格式化百分比
function formatPercentage(value, total) {
    return ((value / total) * 100).toFixed(1) + '%';
}
