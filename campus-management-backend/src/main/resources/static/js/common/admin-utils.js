/**
 * 管理后台通用工具函数
 * 提供统一的UI交互、数据处理和业务逻辑支持
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */

window.AdminUtils = (function() {
    'use strict';

    /**
     * 显示提示消息
     */
    function showAlert(message, type = 'info', containerId = 'alertContainer', autoHide = true) {
        const alertTypes = {
            success: { icon: 'check-circle', class: 'alert-success' },
            danger: { icon: 'exclamation-triangle', class: 'alert-danger' },
            warning: { icon: 'exclamation-triangle', class: 'alert-warning' },
            info: { icon: 'info-circle', class: 'alert-info' }
        };

        const alertConfig = alertTypes[type] || alertTypes.info;
        
        const alertHtml = `
            <div class="alert ${alertConfig.class} alert-dismissible fade show" role="alert">
                <i class="fas fa-${alertConfig.icon} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = alertHtml;
            
            // 自动隐藏成功和信息提示
            if (autoHide && (type === 'success' || type === 'info')) {
                setTimeout(() => {
                    const alert = container.querySelector('.alert');
                    if (alert) {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    }
                }, 3000);
            }
        }
    }

    /**
     * 显示确认对话框
     */
    function showConfirm(message, title = '确认操作') {
        return new Promise((resolve) => {
            const confirmed = confirm(`${title}\n\n${message}`);
            resolve(confirmed);
        });
    }

    /**
     * 显示加载状态
     */
    function showLoading(element, text = '加载中...') {
        if (typeof element === 'string') {
            element = document.querySelector(element);
        }
        
        if (element) {
            element.disabled = true;
            const originalText = element.textContent;
            element.setAttribute('data-original-text', originalText);
            element.innerHTML = `<i class="fas fa-spinner fa-spin me-2"></i>${text}`;
        }
    }

    /**
     * 隐藏加载状态
     */
    function hideLoading(element) {
        if (typeof element === 'string') {
            element = document.querySelector(element);
        }
        
        if (element) {
            element.disabled = false;
            const originalText = element.getAttribute('data-original-text');
            if (originalText) {
                element.textContent = originalText;
                element.removeAttribute('data-original-text');
            }
        }
    }

    /**
     * 格式化日期时间
     */
    function formatDateTime(dateTime, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!dateTime) return '-';
        
        const date = new Date(dateTime);
        if (isNaN(date.getTime())) return '-';
        
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');
        
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
    function debounce(func, wait, immediate = false) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                timeout = null;
                if (!immediate) func.apply(this, args);
            };
            const callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(this, args);
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
     * 深拷贝对象
     */
    function deepClone(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => deepClone(item));
        if (typeof obj === 'object') {
            const clonedObj = {};
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) {
                    clonedObj[key] = deepClone(obj[key]);
                }
            }
            return clonedObj;
        }
    }

    /**
     * 验证表单
     */
    function validateForm(formElement) {
        if (typeof formElement === 'string') {
            formElement = document.querySelector(formElement);
        }
        
        if (!formElement) return false;
        
        const isValid = formElement.checkValidity();
        formElement.classList.add('was-validated');
        
        return isValid;
    }

    /**
     * 重置表单
     */
    function resetForm(formElement) {
        if (typeof formElement === 'string') {
            formElement = document.querySelector(formElement);
        }
        
        if (formElement) {
            formElement.reset();
            formElement.classList.remove('was-validated');
        }
    }

    /**
     * 获取表单数据
     */
    function getFormData(formElement) {
        if (typeof formElement === 'string') {
            formElement = document.querySelector(formElement);
        }
        
        if (!formElement) return {};
        
        const formData = new FormData(formElement);
        const data = {};
        
        for (const [key, value] of formData.entries()) {
            if (data[key]) {
                // 处理多选情况
                if (Array.isArray(data[key])) {
                    data[key].push(value);
                } else {
                    data[key] = [data[key], value];
                }
            } else {
                data[key] = value;
            }
        }
        
        return data;
    }

    /**
     * 设置表单数据
     */
    function setFormData(formElement, data) {
        if (typeof formElement === 'string') {
            formElement = document.querySelector(formElement);
        }
        
        if (!formElement || !data) return;
        
        Object.keys(data).forEach(key => {
            const element = formElement.querySelector(`[name="${key}"]`);
            if (element) {
                if (element.type === 'checkbox' || element.type === 'radio') {
                    element.checked = element.value === String(data[key]);
                } else {
                    element.value = data[key] || '';
                }
            }
        });
    }

    /**
     * 导出数据为CSV
     */
    function exportToCSV(data, filename = 'export.csv') {
        if (!data || data.length === 0) {
            showAlert('没有数据可导出', 'warning');
            return;
        }
        
        const headers = Object.keys(data[0]);
        const csvContent = [
            headers.join(','),
            ...data.map(row => headers.map(header => {
                const value = row[header] || '';
                return `"${String(value).replace(/"/g, '""')}"`;
            }).join(','))
        ].join('\n');
        
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        
        link.setAttribute('href', url);
        link.setAttribute('download', filename);
        link.style.visibility = 'hidden';
        
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        URL.revokeObjectURL(url);
    }

    /**
     * 生成随机ID
     */
    function generateId(prefix = 'id') {
        return `${prefix}_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    }

    /**
     * 检查权限
     */
    function hasPermission(permission) {
        // 这里可以根据实际的权限系统实现
        const userPermissions = window.userPermissions || [];
        return userPermissions.includes(permission) || userPermissions.includes('*');
    }

    /**
     * 获取URL参数
     */
    function getUrlParams() {
        const params = {};
        const urlSearchParams = new URLSearchParams(window.location.search);
        for (const [key, value] of urlSearchParams) {
            params[key] = value;
        }
        return params;
    }

    /**
     * 设置URL参数
     */
    function setUrlParams(params, replace = false) {
        const url = new URL(window.location);
        
        Object.keys(params).forEach(key => {
            if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
                url.searchParams.set(key, params[key]);
            } else {
                url.searchParams.delete(key);
            }
        });
        
        if (replace) {
            window.history.replaceState({}, '', url);
        } else {
            window.history.pushState({}, '', url);
        }
    }

    /**
     * 初始化工具提示
     */
    function initTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    /**
     * 初始化弹出框
     */
    function initPopovers() {
        const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
        popoverTriggerList.map(function (popoverTriggerEl) {
            return new bootstrap.Popover(popoverTriggerEl);
        });
    }

    // 公开API
    return {
        // UI交互
        showAlert,
        showConfirm,
        showLoading,
        hideLoading,
        
        // 数据处理
        formatDateTime,
        formatFileSize,
        deepClone,
        
        // 工具函数
        debounce,
        throttle,
        generateId,
        
        // 表单处理
        validateForm,
        resetForm,
        getFormData,
        setFormData,
        
        // 数据导出
        exportToCSV,
        
        // 权限检查
        hasPermission,
        
        // URL处理
        getUrlParams,
        setUrlParams,
        
        // Bootstrap组件初始化
        initTooltips,
        initPopovers
    };

})();

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化Bootstrap组件
    AdminUtils.initTooltips();
    AdminUtils.initPopovers();
    
    // 全局错误处理
    window.addEventListener('error', function(event) {
        console.error('页面错误:', event.error);
        AdminUtils.showAlert('页面发生错误，请刷新重试', 'danger');
    });
    
    // 全局未处理的Promise拒绝
    window.addEventListener('unhandledrejection', function(event) {
        console.error('未处理的Promise拒绝:', event.reason);
        AdminUtils.showAlert('操作失败，请重试', 'danger');
    });
});
