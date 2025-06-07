/**
 * 通知管理页面JavaScript
 */

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('通知管理页面已加载');
    initNotificationPage();
});

/**
 * 初始化通知页面
 */
function initNotificationPage() {
    // 初始化数据表格
    if (typeof $.fn.DataTable !== 'undefined') {
        $('#dataTable').DataTable({
            language: {
                url: '/js/lib/datatables-zh.json'
            },
            pageLength: 10,
            order: [[6, 'desc']], // 按创建时间降序排列
            columnDefs: [
                { orderable: false, targets: [7] } // 操作列不可排序
            ]
        });
    }

    // 绑定事件
    bindNotificationEvents();
}

/**
 * 绑定通知相关事件
 */
function bindNotificationEvents() {
    // 添加通知按钮
    const addBtn = document.querySelector('a[href="/admin/notifications/add"]');
    if (addBtn) {
        addBtn.addEventListener('click', function(e) {
            console.log('跳转到添加通知页面');
        });
    }
}

/**
 * 删除通知
 */
function deleteNotification(id) {
    if (confirm('确定要删除这条通知吗？此操作不可恢复！')) {
        fetch('/admin/notifications/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'id=' + id
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('删除成功');
                location.reload();
            } else {
                alert('删除失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('操作失败');
        });
    }
}

/**
 * 发布通知
 */
function publishNotification(id) {
    if (confirm('确定要发布这条通知吗？')) {
        fetch('/admin/notifications/publish', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'id=' + id
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('发布成功');
                location.reload();
            } else {
                alert('发布失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('操作失败');
        });
    }
}

/**
 * 撤回通知
 */
function withdrawNotification(id) {
    if (confirm('确定要撤回这条通知吗？')) {
        fetch('/admin/notifications/withdraw', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'id=' + id
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('撤回成功');
                location.reload();
            } else {
                alert('撤回失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('操作失败');
        });
    }
}

/**
 * 置顶/取消置顶通知
 */
function pinNotification(id, pinned) {
    const action = pinned ? '置顶' : '取消置顶';
    if (confirm('确定要' + action + '这条通知吗？')) {
        fetch('/admin/notifications/pin', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'id=' + id + '&pinned=' + pinned
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(data.message);
                location.reload();
            } else {
                alert(action + '失败：' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('操作失败');
        });
    }
}

/**
 * 删除通知
 */
function deleteNotification(id) {
    if (!id) {
        showAlert('通知ID无效', 'error');
        return;
    }

    // 确认删除
    if (!confirm('确定要删除这条通知吗？此操作不可恢复！')) {
        return;
    }

    showLoading('正在删除通知...');

    // 模拟删除操作
    setTimeout(() => {
        hideLoading();
        showAlert('通知删除成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 发布通知
 */
function publishNotification(id) {
    if (!id) {
        showAlert('通知ID无效', 'error');
        return;
    }

    if (!confirm('确定要发布这条通知吗？')) {
        return;
    }

    showLoading('正在发布通知...');

    // 模拟发布操作
    setTimeout(() => {
        hideLoading();
        showAlert('通知发布成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 撤回通知
 */
function withdrawNotification(id) {
    if (!id) {
        showAlert('通知ID无效', 'error');
        return;
    }

    if (!confirm('确定要撤回这条通知吗？')) {
        return;
    }

    showLoading('正在撤回通知...');

    // 模拟撤回操作
    setTimeout(() => {
        hideLoading();
        showAlert('通知撤回成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 批量删除通知
 */
function batchDeleteNotifications() {
    const checkboxes = document.querySelectorAll('input[name="notificationIds"]:checked');
    
    if (checkboxes.length === 0) {
        showAlert('请选择要删除的通知', 'warning');
        return;
    }

    if (!confirm(`确定要删除选中的 ${checkboxes.length} 条通知吗？此操作不可恢复！`)) {
        return;
    }

    const ids = Array.from(checkboxes).map(cb => cb.value);
    
    showLoading('正在批量删除通知...');

    // 模拟批量删除操作
    setTimeout(() => {
        hideLoading();
        showAlert(`成功删除 ${ids.length} 条通知`, 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1500);
}

/**
 * 导出通知数据
 */
function exportNotifications() {
    showLoading('正在导出通知数据...');

    // 模拟导出操作
    setTimeout(() => {
        hideLoading();
        
        // 创建模拟的CSV数据
        const csvData = [
            ['ID', '标题', '类型', '目标受众', '发送者', '状态', '创建时间'],
            ['1', '系统维护通知', '系统通知', '全体用户', '系统管理员', '已发布', '2024-01-01 10:00'],
            ['2', '课程安排更新', '教务通知', '学生', '教务处', '已发布', '2024-01-01 09:00'],
            ['3', '学费缴纳提醒', '财务通知', '学生', '财务处', '草稿', '2024-01-01 08:00']
        ];

        // 转换为CSV格式
        const csvContent = csvData.map(row => row.join(',')).join('\n');
        
        // 下载文件
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', '通知数据_' + new Date().toISOString().slice(0, 10) + '.csv');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showAlert('通知数据导出成功', 'success');
    }, 1000);
}

/**
 * 全选/取消全选
 */
function toggleSelectAll() {
    const selectAllCheckbox = document.getElementById('selectAll');
    const checkboxes = document.querySelectorAll('input[name="notificationIds"]');
    
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
    
    updateBatchOperationButtons();
}

/**
 * 更新批量操作按钮状态
 */
function updateBatchOperationButtons() {
    const checkedBoxes = document.querySelectorAll('input[name="notificationIds"]:checked');
    const batchButtons = document.querySelectorAll('.batch-operation');
    
    batchButtons.forEach(button => {
        button.disabled = checkedBoxes.length === 0;
    });
}

/**
 * 搜索通知
 */
function searchNotifications() {
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.submit();
    }
}

/**
 * 重置搜索条件
 */
function resetSearch() {
    const searchInputs = document.querySelectorAll('#searchForm input, #searchForm select');
    searchInputs.forEach(input => {
        if (input.type === 'text' || input.type === 'search') {
            input.value = '';
        } else if (input.tagName === 'SELECT') {
            input.selectedIndex = 0;
        }
    });
    
    // 提交表单
    searchNotifications();
}

// 工具函数
function showAlert(message, type = 'info') {
    // 这里可以使用更好的提示组件，比如 SweetAlert2
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

    // 插入到页面顶部
    const container = document.querySelector('.container-fluid');
    if (container) {
        container.insertAdjacentHTML('afterbegin', alertHtml);
        
        // 3秒后自动消失
        setTimeout(() => {
            const alert = container.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 3000);
    }
}

function showLoading(message = '加载中...') {
    // 简单的加载提示
    console.log('Loading:', message);
}

function hideLoading() {
    console.log('Loading hidden');
}
