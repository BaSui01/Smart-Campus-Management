/**
 * API客户端工具类
 * 统一处理API请求、响应和错误处理
 */

// 避免重复声明
if (typeof window.ApiClient === 'undefined') {

class ApiClient {
    constructor() {
        this.baseURL = '';
        this.defaultHeaders = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
    }

    /**
     * 获取JWT token
     */
    getToken() {
        // 优先使用认证管理器的token
        if (window.authManager && window.authManager.getToken()) {
            return window.authManager.getToken();
        }

        // 备用方案：从localStorage获取token
        let token = localStorage.getItem('campus_auth_token') || localStorage.getItem('token');

        // 如果localStorage没有，尝试从sessionStorage获取
        if (!token) {
            token = sessionStorage.getItem('token');
        }

        // 如果还是没有，尝试从cookie获取
        if (!token) {
            const cookies = document.cookie.split(';');
            for (let cookie of cookies) {
                const [name, value] = cookie.trim().split('=');
                if (name === 'token' || name === 'jwt' || name === 'authToken') {
                    token = value;
                    break;
                }
            }
        }

        return token;
    }

    /**
     * 从服务器获取当前session的JWT token
     */
    async fetchTokenFromServer() {
        try {
            // 优先使用认证管理器获取认证信息
            if (window.authManager) {
                await window.authManager.fetchAuthInfo();
                return window.authManager.getToken();
            }

            // 备用方案：直接请求token状态
            const response = await fetch('/admin/token-status', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'include' // 包含session cookie
            });

            if (response.ok) {
                const data = await response.json();
                if (data.valid && data.token) {
                    // 将token存储到localStorage以供后续使用
                    localStorage.setItem('campus_auth_token', data.token);
                    localStorage.setItem('tokenType', 'Bearer');
                    if (data.username) {
                        localStorage.setItem('username', data.username);
                    }
                    return data.token;
                }
            }
        } catch (error) {
            console.warn('从服务器获取token失败:', error);
        }
        return null;
    }

    /**
     * 获取请求头（包含认证信息）
     */
    async getHeaders(additionalHeaders = {}) {
        const headers = { ...this.defaultHeaders, ...additionalHeaders };

        // 添加JWT token
        let token = this.getToken();

        // 如果本地没有token，尝试从服务器获取
        if (!token) {
            token = await this.fetchTokenFromServer();
        }

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        return headers;
    }

    /**
     * 发送GET请求
     */
    async get(url, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;

        return this.request(fullUrl, {
            method: 'GET',
            headers: await this.getHeaders()
        });
    }

    /**
     * 发送POST请求
     */
    async post(url, data = {}) {
        const options = {
            method: 'POST',
            headers: await this.getHeaders()
        };

        // 只有当data不为null且不为undefined时才添加body
        if (data !== null && data !== undefined) {
            options.body = JSON.stringify(data);
        }

        return this.request(url, options);
    }

    /**
     * 发送PUT请求
     */
    async put(url, data = {}) {
        return this.request(url, {
            method: 'PUT',
            headers: await this.getHeaders(),
            body: JSON.stringify(data)
        });
    }

    /**
     * 发送DELETE请求
     */
    async delete(url, data = null) {
        const options = {
            method: 'DELETE',
            headers: await this.getHeaders()
        };

        // 如果有数据，添加到请求体
        if (data !== null && data !== undefined) {
            options.body = JSON.stringify(data);
        }

        return this.request(url, options);
    }

    /**
     * 统一请求处理
     */
    async request(url, options) {
        try {
            // 添加credentials以包含session cookie
            options.credentials = 'include';

            const response = await fetch(url, options);

            // 检查是否被重定向到登录页面
            if (response.url && response.url.includes('/admin/login')) {
                console.warn('用户未登录，被重定向到登录页面');
                throw new Error('用户未登录，请先登录');
            }

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
                const text = await response.text();

                // 检查是否返回了HTML登录页面
                if (text.includes('<title>管理员登录') || text.includes('login-container')) {
                    console.warn('返回了登录页面HTML，用户可能未登录');
                    throw new Error('用户未登录，请先登录');
                }

                return text;
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
            // 获取认证头，但不包含Content-Type（让浏览器自动设置multipart/form-data）
            const headers = {};
            let token = this.getToken();

            // 如果本地没有token，尝试从服务器获取
            if (!token) {
                token = await this.fetchTokenFromServer();
            }

            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const response = await fetch(url, {
                method: 'POST',
                headers: headers,
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

// 将API客户端暴露到全局作用域
window.apiClient = apiClient;
window.ApiClient = ApiClient;

// 页面加载时初始化token
document.addEventListener('DOMContentLoaded', async function() {
    try {
        // 尝试从服务器获取token
        await apiClient.fetchTokenFromServer();
        console.log('API客户端初始化完成');
    } catch (error) {
        console.warn('API客户端初始化失败:', error);
    }
});

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

// 全局工具函数
window.showLoading = function(message = '加载中...') {
    LoadingManager.showPageLoading('.content-wrapper');
};

window.hideLoading = function() {
    LoadingManager.hidePageLoading('.content-wrapper');
};

window.showAlert = function(message, type = 'info') {
    MessageUtils[type] ? MessageUtils[type](message) : MessageUtils.info(message);
};

// 将工具类暴露到全局作用域
window.MessageUtils = MessageUtils;
window.FormValidator = FormValidator;
window.LoadingManager = LoadingManager;

} // 关闭 if (typeof window.ApiClient === 'undefined') 条件
