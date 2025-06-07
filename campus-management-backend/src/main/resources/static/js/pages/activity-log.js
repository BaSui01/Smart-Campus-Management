/**
 * 活动日志管理页面JavaScript
 */

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    initActivityLogPage();
});

/**
 * 初始化活动日志页面
 */
function initActivityLogPage() {
    console.log('活动日志页面初始化');
    
    // 绑定事件
    bindEvents();
    
    // 初始化工具提示
    initTooltips();
}

/**
 * 绑定事件
 */
function bindEvents() {
    // 全选/取消全选
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', toggleSelectAll);
    }
    
    // 单个复选框变化
    const checkboxes = document.querySelectorAll('.log-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateSelectAllState);
    });
}

/**
 * 初始化工具提示
 */
function initTooltips() {
    // 如果使用Bootstrap 5
    if (typeof bootstrap !== 'undefined') {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
}

/**
 * 全选/取消全选
 */
function toggleSelectAll() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.log-checkbox');
    
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

/**
 * 更新全选状态
 */
function updateSelectAllState() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('.log-checkbox');
    const checkedCount = document.querySelectorAll('.log-checkbox:checked').length;
    
    if (checkedCount === 0) {
        selectAllCheckbox.indeterminate = false;
        selectAllCheckbox.checked = false;
    } else if (checkedCount === checkboxes.length) {
        selectAllCheckbox.indeterminate = false;
        selectAllCheckbox.checked = true;
    } else {
        selectAllCheckbox.indeterminate = true;
        selectAllCheckbox.checked = false;
    }
}

/**
 * 删除单个日志
 */
function deleteLog(id) {
    if (confirm('确定要删除这条活动日志吗？此操作不可恢复！')) {
        fetch('/admin/activity-log/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'id=' + id
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('删除成功', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                showAlert('删除失败：' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('操作失败', 'error');
        });
    }
}

/**
 * 批量删除日志
 */
function batchDelete() {
    const checkedBoxes = document.querySelectorAll('.log-checkbox:checked');
    
    if (checkedBoxes.length === 0) {
        showAlert('请选择要删除的日志记录', 'warning');
        return;
    }
    
    if (confirm(`确定要删除选中的 ${checkedBoxes.length} 条活动日志吗？此操作不可恢复！`)) {
        const ids = Array.from(checkedBoxes).map(checkbox => checkbox.value);
        
        fetch('/admin/activity-log/batch-delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'ids=' + ids.join(',')
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('批量删除成功', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                showAlert('批量删除失败：' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('操作失败', 'error');
        });
    }
}

/**
 * 清理过期日志
 */
function cleanExpiredLogs() {
    const days = prompt('请输入要保留的天数（默认30天）：', '30');
    
    if (days === null) {
        return; // 用户取消
    }
    
    const daysToKeep = parseInt(days);
    if (isNaN(daysToKeep) || daysToKeep < 1) {
        showAlert('请输入有效的天数', 'warning');
        return;
    }
    
    if (confirm(`确定要清理 ${daysToKeep} 天前的日志吗？此操作不可恢复！`)) {
        fetch('/admin/activity-log/clean', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'daysToKeep=' + daysToKeep
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showAlert('清理过期日志成功', 'success');
                setTimeout(() => {
                    location.reload();
                }, 1000);
            } else {
                showAlert('清理过期日志失败：' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('操作失败', 'error');
        });
    }
}

/**
 * 获取最近活动（AJAX）
 */
function getRecentActivities(limit = 10) {
    return fetch(`/admin/activity-log/recent?limit=${limit}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                return data.data;
            } else {
                console.error('获取最近活动失败：', data.message);
                return [];
            }
        })
        .catch(error => {
            console.error('Error:', error);
            return [];
        });
}

/**
 * 获取活动统计（AJAX）
 */
function getActivityStats() {
    return fetch('/admin/activity-log/stats')
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                return data.data;
            } else {
                console.error('获取活动统计失败：', data.message);
                return {};
            }
        })
        .catch(error => {
            console.error('Error:', error);
            return {};
        });
}

/**
 * 获取每日活动统计（AJAX）
 */
function getDailyStats(days = 7) {
    return fetch(`/admin/activity-log/daily-stats?days=${days}`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                return data.data;
            } else {
                console.error('获取每日统计失败：', data.message);
                return [];
            }
        })
        .catch(error => {
            console.error('Error:', error);
            return [];
        });
}

/**
 * 显示提示信息
 */
function showAlert(message, type = 'info') {
    // 创建提示框
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 
                           type === 'warning' ? 'exclamation-triangle' : 
                           type === 'error' ? 'times-circle' : 'info-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(alertDiv);
    
    // 3秒后自动移除
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.parentNode.removeChild(alertDiv);
        }
    }, 3000);
}

/**
 * 格式化时间
 */
function formatDateTime(dateTime) {
    if (!dateTime) return '';
    
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

/**
 * 格式化活动类型
 */
function formatActivityType(type) {
    const typeMap = {
        'LOGIN': '登录',
        'LOGOUT': '登出',
        'OPERATION': '操作',
        'ERROR': '错误'
    };
    return typeMap[type] || type;
}

/**
 * 格式化操作结果
 */
function formatResult(result) {
    const resultMap = {
        'SUCCESS': '成功',
        'FAILED': '失败',
        'PARTIAL': '部分成功'
    };
    return resultMap[result] || result;
}

/**
 * 格式化日志级别
 */
function formatLevel(level) {
    const levelMap = {
        'INFO': '信息',
        'WARN': '警告',
        'ERROR': '错误'
    };
    return levelMap[level] || level;
}

/**
 * 导出功能（预留）
 */
function exportLogs() {
    showAlert('导出功能开发中...', 'info');
}

/**
 * 刷新页面数据
 */
function refreshData() {
    location.reload();
}
