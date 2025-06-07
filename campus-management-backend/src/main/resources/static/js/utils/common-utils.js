/**
 * 通用工具类
 * 提供所有管理页面共用的功能
 * @version 1.0.0
 */

// ==================== 消息提示工具 ====================
class MessageUtils {
    /**
     * 显示成功消息
     */
    static success(message, title = '成功') {
        this.showAlert(message, 'success', title);
    }

    /**
     * 显示错误消息
     */
    static error(message, title = '错误') {
        this.showAlert(message, 'error', title);
    }

    /**
     * 显示警告消息
     */
    static warning(message, title = '警告') {
        this.showAlert(message, 'warning', title);
    }

    /**
     * 显示信息消息
     */
    static info(message, title = '信息') {
        this.showAlert(message, 'info', title);
    }

    /**
     * 显示确认对话框
     */
    static confirm(message, title = '确认') {
        return new Promise((resolve) => {
            const result = window.confirm(`${title}\n\n${message}`);
            resolve(result);
        });
    }

    /**
     * 显示提示框
     */
    static showAlert(message, type = 'info', title = '') {
        // 创建提示框元素
        const alertId = 'alert-' + Date.now();
        const alertHtml = `
            <div id="${alertId}" class="alert alert-${this.getBootstrapType(type)} alert-dismissible fade show position-fixed" 
                 style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
                <strong>${title}</strong> ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        // 添加到页面
        document.body.insertAdjacentHTML('beforeend', alertHtml);

        // 3秒后自动消失
        setTimeout(() => {
            const alertElement = document.getElementById(alertId);
            if (alertElement) {
                alertElement.remove();
            }
        }, 3000);
    }

    /**
     * 获取Bootstrap对应的类型
     */
    static getBootstrapType(type) {
        const typeMap = {
            'success': 'success',
            'error': 'danger',
            'warning': 'warning',
            'info': 'info'
        };
        return typeMap[type] || 'info';
    }
}

// ==================== 加载状态管理 ====================
class LoadingManager {
    /**
     * 显示页面加载状态
     */
    static showPageLoading(selector = 'body') {
        const container = document.querySelector(selector);
        if (!container) return;

        // 移除已存在的加载层
        this.hidePageLoading(selector);

        // 创建加载层
        const loadingHtml = `
            <div class="loading-overlay">
                <div class="loading-spinner"></div>
            </div>
        `;

        container.style.position = 'relative';
        container.insertAdjacentHTML('beforeend', loadingHtml);
    }

    /**
     * 隐藏页面加载状态
     */
    static hidePageLoading(selector = 'body') {
        const container = document.querySelector(selector);
        if (!container) return;

        const loadingOverlay = container.querySelector('.loading-overlay');
        if (loadingOverlay) {
            loadingOverlay.remove();
        }
    }

    /**
     * 显示按钮加载状态
     */
    static showButtonLoading(button, text = '加载中...') {
        if (!button) return;

        button.disabled = true;
        button.dataset.originalText = button.textContent;
        button.innerHTML = `
            <span class="spinner-border spinner-border-sm me-2" role="status"></span>
            ${text}
        `;
    }

    /**
     * 隐藏按钮加载状态
     */
    static hideButtonLoading(button) {
        if (!button) return;

        button.disabled = false;
        button.textContent = button.dataset.originalText || '确定';
    }
}

// ==================== 表单验证工具 ====================
class FormValidator {
    constructor(form) {
        this.form = form;
        this.rules = new Map();
    }

    /**
     * 添加验证规则
     */
    addRule(fieldName, rules, message) {
        this.rules.set(fieldName, { rules, message });
        return this;
    }

    /**
     * 验证表单
     */
    validate() {
        let isValid = true;
        const errors = [];

        this.rules.forEach((rule, fieldName) => {
            const field = this.form.querySelector(`[name="${fieldName}"]`);
            if (!field) return;

            const value = field.value.trim();
            let fieldValid = true;

            // 必填验证
            if (rule.rules.required && !value) {
                fieldValid = false;
            }

            // 最大长度验证
            if (rule.rules.maxLength && value.length > rule.rules.maxLength) {
                fieldValid = false;
            }

            // 最小长度验证
            if (rule.rules.minLength && value.length < rule.rules.minLength) {
                fieldValid = false;
            }

            // 自定义验证
            if (rule.rules.custom && typeof rule.rules.custom === 'function') {
                const customResult = rule.rules.custom(value);
                if (customResult !== true) {
                    fieldValid = false;
                }
            }

            if (!fieldValid) {
                isValid = false;
                errors.push({ field: fieldName, message: rule.message });
                this.showFieldError(field, rule.message);
            } else {
                this.clearFieldError(field);
            }
        });

        return { isValid, errors };
    }

    /**
     * 显示字段错误
     */
    showFieldError(field, message) {
        this.clearFieldError(field);
        field.classList.add('is-invalid');
        
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);
    }

    /**
     * 清除字段错误
     */
    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    /**
     * 清除所有错误
     */
    clearAllErrors() {
        this.rules.forEach((rule, fieldName) => {
            const field = this.form.querySelector(`[name="${fieldName}"]`);
            if (field) {
                this.clearFieldError(field);
            }
        });
    }
}

// ==================== 数据格式化工具 ====================
class DataFormatter {
    /**
     * 格式化日期
     */
    static formatDate(dateString, format = 'YYYY-MM-DD') {
        if (!dateString) return '-';
        
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return '-';

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        switch (format) {
            case 'YYYY-MM-DD':
                return `${year}-${month}-${day}`;
            case 'YYYY-MM-DD HH:mm':
                return `${year}-${month}-${day} ${hours}:${minutes}`;
            case 'YYYY-MM-DD HH:mm:ss':
                return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
            case 'MM-DD':
                return `${month}-${day}`;
            default:
                return `${year}-${month}-${day}`;
        }
    }

    /**
     * 格式化数字
     */
    static formatNumber(number, decimals = 0) {
        if (number === null || number === undefined || isNaN(number)) return '-';
        return Number(number).toFixed(decimals);
    }

    /**
     * 格式化货币
     */
    static formatCurrency(amount, currency = '¥') {
        if (amount === null || amount === undefined || isNaN(amount)) return '-';
        return `${currency}${Number(amount).toFixed(2)}`;
    }

    /**
     * 格式化状态
     */
    static formatStatus(status, statusMap = {}) {
        const defaultMap = {
            1: { text: '正常', class: 'status-active' },
            0: { text: '停用', class: 'status-inactive' },
            '-1': { text: '已删除', class: 'status-inactive' }
        };

        const map = { ...defaultMap, ...statusMap };
        const statusInfo = map[status] || { text: '未知', class: 'status-inactive' };

        return `<span class="status-badge ${statusInfo.class}">${statusInfo.text}</span>`;
    }
}

// ==================== 分页工具 ====================
class PaginationUtils {
    /**
     * 渲染分页组件
     */
    static render(container, pageData, onPageChange) {
        if (!container || !pageData) return;

        const { number: currentPage, totalPages, totalElements } = pageData;

        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let paginationHtml = `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">上一页</a>
            </li>
        `;

        // 计算显示的页码范围
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);

        // 如果不是从第一页开始，显示第一页和省略号
        if (startPage > 0) {
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" data-page="0">1</a>
                </li>
            `;
            if (startPage > 1) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        // 显示页码
        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        // 如果不是到最后一页，显示省略号和最后一页
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" data-page="${totalPages - 1}">${totalPages}</a>
                </li>
            `;
        }

        paginationHtml += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">下一页</a>
            </li>
        `;

        container.innerHTML = paginationHtml;

        // 绑定点击事件
        container.addEventListener('click', (e) => {
            if (e.target.classList.contains('page-link') && !e.target.closest('.disabled')) {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                if (!isNaN(page) && typeof onPageChange === 'function') {
                    onPageChange(page);
                }
            }
        });
    }
}

// ==================== 导出工具 ====================
class ExportUtils {
    /**
     * 导出为CSV
     */
    static exportToCSV(data, filename = 'export.csv', headers = []) {
        if (!data || data.length === 0) {
            MessageUtils.warning('没有数据可导出');
            return;
        }

        let csvContent = '';

        // 添加表头
        if (headers.length > 0) {
            csvContent += headers.join(',') + '\n';
        }

        // 添加数据行
        data.forEach(row => {
            const values = Object.values(row).map(value => {
                // 处理包含逗号或引号的值
                if (typeof value === 'string' && (value.includes(',') || value.includes('"'))) {
                    return `"${value.replace(/"/g, '""')}"`;
                }
                return value || '';
            });
            csvContent += values.join(',') + '\n';
        });

        // 创建下载链接
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = filename;
        link.style.display = 'none';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
}

// 将工具类暴露到全局
window.MessageUtils = MessageUtils;
window.LoadingManager = LoadingManager;
window.FormValidator = FormValidator;
window.DataFormatter = DataFormatter;
window.PaginationUtils = PaginationUtils;
window.ExportUtils = ExportUtils;
