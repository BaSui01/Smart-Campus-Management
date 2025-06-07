/**
 * 登录页面管理器
 * 处理登录表单提交和认证逻辑
 * 智慧校园管理系统
 */
class LoginManager {
    constructor() {
        this.loginForm = null;
        this.captchaImage = null;
        this.submitButton = null;
        this.init();
    }

    /**
     * 初始化登录管理器
     */
    init() {
        console.log('🔐 初始化登录管理器...');
        
        // 等待DOM加载完成
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.setupLoginPage());
        } else {
            this.setupLoginPage();
        }
    }

    /**
     * 设置登录页面
     */
    setupLoginPage() {
        console.log('📝 设置登录页面...');
        
        // 获取页面元素
        this.loginForm = document.getElementById('loginForm');
        this.captchaImage = document.getElementById('captchaImage');
        this.submitButton = this.loginForm?.querySelector('button[type="submit"]');
        
        if (!this.loginForm) {
            console.warn('未找到登录表单');
            return;
        }

        // 设置事件监听器
        this.setupEventListeners();
        
        // 恢复记住的登录信息
        this.loadRememberedCredentials();
        
        // 自动聚焦
        this.setupAutoFocus();
        
        console.log('✅ 登录页面设置完成');
    }

    /**
     * 设置事件监听器
     */
    setupEventListeners() {
        // 表单提交事件
        this.loginForm.addEventListener('submit', (e) => this.handleFormSubmit(e));
        
        // 验证码图片点击刷新
        if (this.captchaImage) {
            this.captchaImage.addEventListener('click', () => this.refreshCaptcha());
        }
        
        // 回车键快捷登录
        document.addEventListener('keypress', (e) => {
            if (e.key === 'Enter' && !this.submitButton?.disabled) {
                this.loginForm.submit();
            }
        });
    }

    /**
     * 处理表单提交
     */
    async handleFormSubmit(e) {
        e.preventDefault();
        
        console.log('🚀 开始处理登录...');
        
        // 显示加载状态
        this.showLoadingState();
        
        try {
            // 表单验证
            const formData = this.validateForm();
            if (!formData) {
                this.resetSubmitButton();
                return;
            }

            // 保存登录凭据（如果选择了记住我）
            this.saveCredentials(formData);

            // 使用认证管理器登录
            let result;
            if (window.authManager) {
                console.log('使用认证管理器登录');
                result = await window.authManager.login(formData);
            } else {
                console.log('使用传统方式登录');
                result = await this.traditionalLogin(formData);
            }

            // 处理登录结果
            if (result.success) {
                this.showAlert('登录成功，正在跳转...', 'success');
                
                // 延迟跳转，让用户看到成功提示
                setTimeout(() => {
                    window.location.href = result.redirectUrl || '/admin/dashboard';
                }, 1000);
            } else {
                this.showAlert(result.message || '登录失败', 'danger');
                this.resetSubmitButton();
                this.refreshCaptcha();
            }

        } catch (error) {
            console.error('❌ 登录过程中发生错误:', error);
            this.showAlert('登录过程中发生错误，请稍后重试', 'danger');
            this.resetSubmitButton();
            this.refreshCaptcha();
        }
    }

    /**
     * 传统登录方式（备用）
     */
    async traditionalLogin(formData) {
        const response = await fetch('/admin/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(formData),
            credentials: 'include'
        });

        if (response.ok && response.url.includes('/admin/dashboard')) {
            return {
                success: true,
                redirectUrl: '/admin/dashboard'
            };
        } else {
            return {
                success: false,
                message: '用户名、密码或验证码错误'
            };
        }
    }

    /**
     * 表单验证
     */
    validateForm() {
        const username = document.getElementById('username')?.value.trim();
        const password = document.getElementById('password')?.value.trim();
        const captcha = document.getElementById('captcha')?.value.trim();

        if (!username || !password || !captcha) {
            this.showAlert('请填写完整的登录信息', 'danger');
            return null;
        }

        if (captcha.length !== 6) {
            this.showAlert('验证码必须是6位', 'danger');
            return null;
        }

        return { username, password, captcha };
    }

    /**
     * 显示加载状态
     */
    showLoadingState() {
        if (this.submitButton) {
            this.submitButton.disabled = true;
            const loginText = this.submitButton.querySelector('.login-text');
            const loading = this.submitButton.querySelector('.loading');
            
            if (loginText) loginText.style.display = 'none';
            if (loading) loading.style.display = 'inline';
        }
    }

    /**
     * 重置提交按钮状态
     */
    resetSubmitButton() {
        if (this.submitButton) {
            this.submitButton.disabled = false;
            const loginText = this.submitButton.querySelector('.login-text');
            const loading = this.submitButton.querySelector('.loading');
            
            if (loginText) loginText.style.display = 'inline';
            if (loading) loading.style.display = 'none';
        }
    }

    /**
     * 刷新验证码
     */
    refreshCaptcha() {
        if (this.captchaImage) {
            this.captchaImage.src = '/admin/captcha?' + new Date().getTime();
        }
    }

    /**
     * 显示提示信息
     */
    showAlert(message, type) {
        // 移除现有提示
        const existingAlert = document.querySelector('.alert:not(.alert-static)');
        if (existingAlert) {
            existingAlert.remove();
        }
        
        // 创建新提示
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type}`;
        alertDiv.innerHTML = `
            <i class="fas fa-${type === 'danger' ? 'exclamation-triangle' : 'check-circle'} me-2"></i>
            ${message}
        `;
        
        // 插入到表单前面
        this.loginForm.parentNode.insertBefore(alertDiv, this.loginForm);
        
        // 3秒后自动消失（成功提示除外）
        if (type !== 'success') {
            setTimeout(() => {
                if (alertDiv.parentElement) {
                    alertDiv.remove();
                }
            }, 3000);
        }
    }

    /**
     * 保存登录凭据
     */
    saveCredentials(formData) {
        const rememberMe = document.getElementById('rememberMe')?.checked;
        
        if (rememberMe) {
            localStorage.setItem('rememberedUsername', formData.username);
            localStorage.setItem('rememberMe', 'true');
        } else {
            localStorage.removeItem('rememberedUsername');
            localStorage.removeItem('rememberedPassword');
            localStorage.removeItem('rememberMe');
        }
    }

    /**
     * 加载记住的登录凭据
     */
    loadRememberedCredentials() {
        const rememberedUsername = localStorage.getItem('rememberedUsername');
        const rememberMe = localStorage.getItem('rememberMe');

        if (rememberMe === 'true' && rememberedUsername) {
            const usernameInput = document.getElementById('username');
            const rememberMeCheckbox = document.getElementById('rememberMe');
            
            if (usernameInput) usernameInput.value = rememberedUsername;
            if (rememberMeCheckbox) rememberMeCheckbox.checked = true;
        }
    }

    /**
     * 设置自动聚焦
     */
    setupAutoFocus() {
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        
        if (usernameInput && !usernameInput.value) {
            usernameInput.focus();
        } else if (passwordInput) {
            passwordInput.focus();
        }
    }
}

// 创建全局登录管理器实例
const loginManager = new LoginManager();

// 将登录管理器暴露到全局作用域
window.loginManager = loginManager;

console.log('🔐 登录管理器已加载');
