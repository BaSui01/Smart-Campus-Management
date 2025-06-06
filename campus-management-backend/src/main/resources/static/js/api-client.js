/**
 * API客户端工具类
 * 统一处理API请求、响应和错误处理
 */
class ApiClient {
    constructor() {
        this.baseURL = '';
        this.defaultHeaders = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
    }

    /**
     * 发送GET请求
     */
    async get(url, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        
        return this.request(fullUrl, {
            method: 'GET',
            headers: this.defaultHeaders
        });
    }

    /**
     * 发送POST请求
     */
    async post(url, data = {}) {
        return this.request(url, {
            method: 'POST',
            headers: this.defaultHeaders,
            body: JSON.stringify(data)
        });
    }

    /**
     * 发送PUT请求
     */
    async put(url, data = {}) {
        return this.request(url, {
            method: 'PUT',
            headers: this.defaultHeaders,
            body: JSON.stringify(data)
        });
    }

    /**
     * 发送DELETE请求
     */
    async delete(url) {
        return this.request(url, {
            method: 'DELETE',
            headers: this.defaultHeaders
        });
    }

    /**
     * 统一请求处理
     */
    async request(url, options) {
        try {
            const response = await fetch(url, options);
            
            // 检查HTTP状态码
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            // 尝试解析JSON响应
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const data = await response.json();
                
                // 检查业务状态码
                if (data.success === false) {
                    throw new Error(data.message || '操作失败');
                }
                
                return data;
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }

    /**
     * 上传文件
     */
    async upload(url, formData) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('文件上传失败:', error);
            throw error;
        }
    }
}

// 创建全局API客户端实例
const apiClient = new ApiClient();

/**
 * 消息提示工具类
 */
class MessageUtils {
    /**
     * 显示成功消息
     */
    static success(message, duration = 3000) {
        this.showMessage(message, 'success', duration);
    }

    /**
     * 显示错误消息
     */
    static error(message, duration = 5000) {
        this.showMessage(message, 'error', duration);
    }

    /**
     * 显示警告消息
     */
    static warning(message, duration = 4000) {
        this.showMessage(message, 'warning', duration);
    }

    /**
     * 显示信息消息
     */
    static info(message, duration = 3000) {
        this.showMessage(message, 'info', duration);
    }

    /**
     * 显示消息
     */
    static showMessage(message, type = 'info', duration = 3000) {
        // 移除现有的消息
        const existingAlert = document.querySelector('.alert-message');
        if (existingAlert) {
            existingAlert.remove();
        }

        // 创建消息元素
        const alertClass = this.getAlertClass(type);
        const alertHtml = `
            <div class="alert ${alertClass} alert-dismissible fade show alert-message" role="alert" style="position: fixed; top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
                <i class="${this.getIcon(type)} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        // 添加到页面
        document.body.insertAdjacentHTML('afterbegin', alertHtml);

        // 自动消失
        if (duration > 0) {
            setTimeout(() => {
                const alert = document.querySelector('.alert-message');
                if (alert) {
                    alert.remove();
                }
            }, duration);
        }
    }

    /**
     * 获取Bootstrap警告类
     */
    static getAlertClass(type) {
        const classes = {
            success: 'alert-success',
            error: 'alert-danger',
            warning: 'alert-warning',
            info: 'alert-info'
        };
        return classes[type] || 'alert-info';
    }

    /**
     * 获取图标类
     */
    static getIcon(type) {
        const icons = {
            success: 'fas fa-check-circle',
            error: 'fas fa-exclamation-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        };
        return icons[type] || 'fas fa-info-circle';
    }

    /**
     * 确认对话框
     */
    static confirm(message, title = '确认操作') {
        return new Promise((resolve) => {
            if (window.confirm(`${title}\n\n${message}`)) {
                resolve(true);
            } else {
                resolve(false);
            }
        });
    }
}

/**
 * 表单验证工具类
 */
class FormValidator {
    constructor(form) {
        this.form = typeof form === 'string' ? document.getElementById(form) : form;
        this.rules = {};
        this.messages = {};
    }

    /**
     * 添加验证规则
     */
    addRule(fieldName, rules, message = '') {
        this.rules[fieldName] = rules;
        if (message) {
            this.messages[fieldName] = message;
        }
        return this;
    }

    /**
     * 验证表单
     */
    validate() {
        let isValid = true;
        const errors = {};

        for (const [fieldName, rules] of Object.entries(this.rules)) {
            const field = this.form.querySelector(`[name="${fieldName}"]`);
            if (!field) continue;

            const value = field.value.trim();
            const fieldErrors = [];

            // 必填验证
            if (rules.required && !value) {
                fieldErrors.push(this.messages[fieldName] || `${fieldName}不能为空`);
            }

            // 长度验证
            if (value && rules.minLength && value.length < rules.minLength) {
                fieldErrors.push(`${fieldName}长度不能少于${rules.minLength}个字符`);
            }
            if (value && rules.maxLength && value.length > rules.maxLength) {
                fieldErrors.push(`${fieldName}长度不能超过${rules.maxLength}个字符`);
            }

            // 正则验证
            if (value && rules.pattern && !rules.pattern.test(value)) {
                fieldErrors.push(this.messages[fieldName] || `${fieldName}格式不正确`);
            }

            // 自定义验证
            if (value && rules.custom && typeof rules.custom === 'function') {
                const customResult = rules.custom(value);
                if (customResult !== true) {
                    fieldErrors.push(customResult || `${fieldName}验证失败`);
                }
            }

            if (fieldErrors.length > 0) {
                isValid = false;
                errors[fieldName] = fieldErrors;
                this.showFieldError(field, fieldErrors[0]);
            } else {
                this.clearFieldError(field);
            }
        }

        return { isValid, errors };
    }

    /**
     * 显示字段错误
     */
    showFieldError(field, message) {
        field.classList.add('is-invalid');
        
        let feedback = field.parentNode.querySelector('.invalid-feedback');
        if (!feedback) {
            feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            field.parentNode.appendChild(feedback);
        }
        feedback.textContent = message;
    }

    /**
     * 清除字段错误
     */
    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const feedback = field.parentNode.querySelector('.invalid-feedback');
        if (feedback) {
            feedback.remove();
        }
    }

    /**
     * 清除所有错误
     */
    clearAllErrors() {
        const invalidFields = this.form.querySelectorAll('.is-invalid');
        invalidFields.forEach(field => this.clearFieldError(field));
    }
}

/**
 * 加载状态管理
 */
class LoadingManager {
    /**
     * 显示按钮加载状态
     */
    static showButtonLoading(button, text = '处理中...') {
        const btn = typeof button === 'string' ? document.getElementById(button) : button;
        if (!btn) return;

        btn.disabled = true;
        btn.dataset.originalText = btn.innerHTML;
        btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2"></span>${text}`;
    }

    /**
     * 隐藏按钮加载状态
     */
    static hideButtonLoading(button) {
        const btn = typeof button === 'string' ? document.getElementById(button) : button;
        if (!btn) return;

        btn.disabled = false;
        if (btn.dataset.originalText) {
            btn.innerHTML = btn.dataset.originalText;
            delete btn.dataset.originalText;
        }
    }

    /**
     * 显示页面加载状态
     */
    static showPageLoading(container = 'body') {
        const element = typeof container === 'string' ? document.querySelector(container) : container;
        if (!element) return;

        const loadingHtml = `
            <div class="loading-overlay" style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(255,255,255,0.8); display: flex; align-items: center; justify-content: center; z-index: 1000;">
                <div class="text-center">
                    <div class="spinner-border text-primary mb-2"></div>
                    <div>加载中...</div>
                </div>
            </div>
        `;
        
        element.style.position = 'relative';
        element.insertAdjacentHTML('beforeend', loadingHtml);
    }

    /**
     * 隐藏页面加载状态
     */
    static hidePageLoading(container = 'body') {
        const element = typeof container === 'string' ? document.querySelector(container) : container;
        if (!element) return;

        const overlay = element.querySelector('.loading-overlay');
        if (overlay) {
            overlay.remove();
        }
    }
}
