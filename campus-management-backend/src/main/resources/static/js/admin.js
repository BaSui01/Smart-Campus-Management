/**
 * 智慧校园管理系统 - 管理后台JavaScript
 */

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化侧边栏切换功能
    initSidebarToggle();
    
    // 初始化工具提示
    initTooltips();
    
    // 初始化确认对话框
    initConfirmDialogs();
    
    // 初始化表格功能
    initTableFeatures();
    
    // 初始化表单验证
    initFormValidation();
});

/**
 * 初始化侧边栏切换功能
 */
function initSidebarToggle() {
    const sidebarCollapse = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    const content = document.getElementById('content');
    
    if (sidebarCollapse) {
        sidebarCollapse.addEventListener('click', function() {
            sidebar.classList.toggle('active');
            content.classList.toggle('active');
        });
    }
    
    // 移动端自动收起侧边栏
    if (window.innerWidth <= 768) {
        if (sidebar) sidebar.classList.add('active');
        if (content) content.classList.add('active');
    }
    
    // 窗口大小改变时调整布局
    window.addEventListener('resize', function() {
        if (window.innerWidth <= 768) {
            if (sidebar && !sidebar.classList.contains('active')) {
                sidebar.classList.add('active');
            }
            if (content && !content.classList.contains('active')) {
                content.classList.add('active');
            }
        } else {
            if (sidebar && sidebar.classList.contains('active')) {
                sidebar.classList.remove('active');
            }
            if (content && content.classList.contains('active')) {
                content.classList.remove('active');
            }
        }
    });
}

/**
 * 初始化工具提示
 */
function initTooltips() {
    // 如果Bootstrap的tooltip可用，则初始化
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
}

/**
 * 初始化确认对话框
 */
function initConfirmDialogs() {
    // 为所有带有data-confirm属性的元素添加确认对话框
    const confirmElements = document.querySelectorAll('[data-confirm]');
    confirmElements.forEach(function(element) {
        element.addEventListener('click', function(e) {
            const message = this.getAttribute('data-confirm');
            if (!confirm(message)) {
                e.preventDefault();
                return false;
            }
        });
    });
}

/**
 * 初始化表格功能
 */
function initTableFeatures() {
    // 表格行点击高亮
    const tableRows = document.querySelectorAll('table tbody tr');
    tableRows.forEach(function(row) {
        row.addEventListener('click', function() {
            // 移除其他行的高亮
            tableRows.forEach(function(r) {
                r.classList.remove('table-active');
            });
            // 添加当前行高亮
            this.classList.add('table-active');
        });
    });
    
    // 全选功能
    const selectAllCheckbox = document.querySelector('input[type="checkbox"][data-select-all]');
    if (selectAllCheckbox) {
        const targetCheckboxes = document.querySelectorAll('input[type="checkbox"][data-select-item]');
        
        selectAllCheckbox.addEventListener('change', function() {
            targetCheckboxes.forEach(function(checkbox) {
                checkbox.checked = selectAllCheckbox.checked;
            });
        });
        
        // 监听单个复选框变化
        targetCheckboxes.forEach(function(checkbox) {
            checkbox.addEventListener('change', function() {
                const checkedCount = document.querySelectorAll('input[type="checkbox"][data-select-item]:checked').length;
                selectAllCheckbox.checked = checkedCount === targetCheckboxes.length;
                selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < targetCheckboxes.length;
            });
        });
    }
}

/**
 * 初始化表单验证
 */
function initFormValidation() {
    // 获取所有需要验证的表单
    const forms = document.querySelectorAll('.needs-validation');
    
    // 为每个表单添加验证
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}

/**
 * 显示成功消息
 */
function showSuccessMessage(message) {
    showMessage(message, 'success');
}

/**
 * 显示错误消息
 */
function showErrorMessage(message) {
    showMessage(message, 'danger');
}

/**
 * 显示警告消息
 */
function showWarningMessage(message) {
    showMessage(message, 'warning');
}

/**
 * 显示信息消息
 */
function showInfoMessage(message) {
    showMessage(message, 'info');
}

/**
 * 显示消息
 */
function showMessage(message, type = 'info') {
    // 创建消息容器
    const alertContainer = document.getElementById('alert-container') || createAlertContainer();
    
    // 创建警告框
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    // 添加到容器
    alertContainer.appendChild(alert);
    
    // 自动消失
    setTimeout(function() {
        if (alert.parentNode) {
            alert.classList.remove('show');
            setTimeout(function() {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 150);
        }
    }, 5000);
}

/**
 * 创建消息容器
 */
function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alert-container';
    container.style.position = 'fixed';
    container.style.top = '20px';
    container.style.right = '20px';
    container.style.zIndex = '9999';
    container.style.maxWidth = '400px';
    document.body.appendChild(container);
    return container;
}

/**
 * AJAX请求封装
 */
function ajaxRequest(url, options = {}) {
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    };
    
    const finalOptions = Object.assign(defaultOptions, options);
    
    return fetch(url, finalOptions)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .catch(error => {
            console.error('AJAX request failed:', error);
            showErrorMessage('请求失败：' + error.message);
            throw error;
        });
}

/**
 * 格式化日期
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return '';
    
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');
    
    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 格式化文件大小
 */
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

/**
 * 防抖函数
 */
function debounce(func, wait, immediate) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            timeout = null;
            if (!immediate) func(...args);
        };
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func(...args);
    };
}

/**
 * 节流函数
 */
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

/**
 * 导出功能
 */
function exportData(url, filename) {
    const link = document.createElement('a');
    link.href = url;
    link.download = filename || 'export.xlsx';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

/**
 * 打印功能
 */
function printPage() {
    window.print();
}

/**
 * 全屏功能
 */
function toggleFullscreen() {
    if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen();
    } else {
        if (document.exitFullscreen) {
            document.exitFullscreen();
        }
    }
}

/**
 * 处理表格操作按钮点击
 */
function handleAction(button) {
    const functionName = button.getAttribute('data-function');
    const id = button.getAttribute('data-id');

    if (functionName && id) {
        // 动态调用函数
        if (typeof window[functionName] === 'function') {
            window[functionName](id);
        } else {
            console.error('Function not found:', functionName);
            showErrorMessage('操作函数未找到：' + functionName);
        }
    }
}

/**
 * 用户管理相关函数
 */
function editUser(id) {
    console.log('编辑用户:', id);
    showInfoMessage('编辑用户功能开发中...');
}

function deleteUser(id) {
    if (confirm('确定要删除这个用户吗？')) {
        console.log('删除用户:', id);
        showInfoMessage('删除用户功能开发中...');
    }
}

function resetPassword(id) {
    if (confirm('确定要重置这个用户的密码吗？')) {
        console.log('重置密码:', id);
        showInfoMessage('重置密码功能开发中...');
    }
}
