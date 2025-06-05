/**
 * 登录页面增强功能
 * 提供更好的用户体验和交互效果
 */

document.addEventListener('DOMContentLoaded', function() {
    // 初始化登录页面增强功能
    initLoginEnhancements();
});

function initLoginEnhancements() {
    // 添加输入框焦点效果
    addInputFocusEffects();
    
    // 添加密码显示/隐藏功能
    addPasswordToggle();
    
    // 添加键盘快捷键
    addKeyboardShortcuts();
    
    // 添加表单验证增强
    addFormValidationEnhancements();
    
    // 添加加载动画
    addLoadingAnimations();
    
    // 添加主题切换功能
    addThemeToggle();
    
    // 添加网络状态检测
    addNetworkStatusDetection();
}

/**
 * 添加输入框焦点效果
 */
function addInputFocusEffects() {
    const inputs = document.querySelectorAll('.form-floating input');
    
    inputs.forEach(input => {
        // 添加波纹效果
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
            createRippleEffect(this);
        });
        
        input.addEventListener('blur', function() {
            this.parentElement.classList.remove('focused');
        });
        
        // 添加输入验证视觉反馈
        input.addEventListener('input', function() {
            validateInputRealTime(this);
        });
    });
}

/**
 * 创建波纹效果
 */
function createRippleEffect(element) {
    const ripple = document.createElement('span');
    ripple.classList.add('input-ripple');
    element.parentElement.appendChild(ripple);
    
    setTimeout(() => {
        ripple.remove();
    }, 600);
}

/**
 * 添加密码显示/隐藏功能
 */
function addPasswordToggle() {
    const passwordInput = document.getElementById('password');
    if (!passwordInput) return;
    
    // 创建切换按钮
    const toggleButton = document.createElement('button');
    toggleButton.type = 'button';
    toggleButton.className = 'password-toggle';
    toggleButton.innerHTML = '<i class="fas fa-eye"></i>';
    toggleButton.title = '显示/隐藏密码';
    
    // 添加样式
    toggleButton.style.cssText = `
        position: absolute;
        right: 15px;
        top: 50%;
        transform: translateY(-50%);
        background: none;
        border: none;
        color: #666;
        cursor: pointer;
        z-index: 10;
        padding: 5px;
        border-radius: 4px;
        transition: color 0.3s ease;
    `;
    
    // 添加到密码输入框容器
    passwordInput.parentElement.style.position = 'relative';
    passwordInput.parentElement.appendChild(toggleButton);
    
    // 调整密码输入框右边距
    passwordInput.style.paddingRight = '50px';
    
    // 切换功能
    toggleButton.addEventListener('click', function() {
        const isPassword = passwordInput.type === 'password';
        passwordInput.type = isPassword ? 'text' : 'password';
        
        const icon = this.querySelector('i');
        icon.className = isPassword ? 'fas fa-eye-slash' : 'fas fa-eye';
        
        this.title = isPassword ? '隐藏密码' : '显示密码';
        
        // 保持焦点在密码输入框
        passwordInput.focus();
    });
    
    // 悬停效果
    toggleButton.addEventListener('mouseenter', function() {
        this.style.color = '#667eea';
    });
    
    toggleButton.addEventListener('mouseleave', function() {
        this.style.color = '#666';
    });
}

/**
 * 添加键盘快捷键
 */
function addKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        // Ctrl + Enter 快速登录
        if (e.ctrlKey && e.key === 'Enter') {
            e.preventDefault();
            document.getElementById('loginForm').submit();
        }
        
        // F5 刷新验证码
        if (e.key === 'F5') {
            e.preventDefault();
            refreshCaptcha();
        }
        
        // Escape 清空表单
        if (e.key === 'Escape') {
            clearForm();
        }
    });
}

/**
 * 刷新验证码
 */
function refreshCaptcha() {
    const captchaImage = document.getElementById('captchaImage');
    if (captchaImage) {
        captchaImage.src = '/admin/captcha?' + new Date().getTime();
        
        // 添加刷新动画
        captchaImage.style.transform = 'rotate(360deg)';
        setTimeout(() => {
            captchaImage.style.transform = 'rotate(0deg)';
        }, 300);
    }
}

/**
 * 清空表单
 */
function clearForm() {
    const inputs = document.querySelectorAll('#loginForm input[type="text"], #loginForm input[type="password"]');
    inputs.forEach(input => {
        input.value = '';
        input.dispatchEvent(new Event('input'));
    });
    
    // 聚焦到用户名输入框
    document.getElementById('username').focus();
}

/**
 * 添加表单验证增强
 */
function addFormValidationEnhancements() {
    const form = document.getElementById('loginForm');
    
    // 实时验证
    form.addEventListener('input', function(e) {
        validateInputRealTime(e.target);
    });
    
    // 提交前验证
    form.addEventListener('submit', function(e) {
        if (!validateForm()) {
            e.preventDefault();
        }
    });
}

/**
 * 实时输入验证
 */
function validateInputRealTime(input) {
    const value = input.value.trim();
    const inputGroup = input.parentElement;
    
    // 移除之前的验证状态
    inputGroup.classList.remove('valid', 'invalid');
    
    switch (input.id) {
        case 'username':
            if (value.length >= 3) {
                inputGroup.classList.add('valid');
            } else if (value.length > 0) {
                inputGroup.classList.add('invalid');
            }
            break;
            
        case 'password':
            if (value.length >= 6) {
                inputGroup.classList.add('valid');
            } else if (value.length > 0) {
                inputGroup.classList.add('invalid');
            }
            break;
            
        case 'captcha':
            if (value.length === 6) {
                inputGroup.classList.add('valid');
            } else if (value.length > 0) {
                inputGroup.classList.add('invalid');
            }
            break;
    }
}

/**
 * 表单验证
 */
function validateForm() {
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const captcha = document.getElementById('captcha').value.trim();
    
    if (!username) {
        showValidationError('请输入用户名');
        document.getElementById('username').focus();
        return false;
    }
    
    if (username.length < 3) {
        showValidationError('用户名至少需要3个字符');
        document.getElementById('username').focus();
        return false;
    }
    
    if (!password) {
        showValidationError('请输入密码');
        document.getElementById('password').focus();
        return false;
    }
    
    if (password.length < 6) {
        showValidationError('密码至少需要6个字符');
        document.getElementById('password').focus();
        return false;
    }
    
    if (!captcha) {
        showValidationError('请输入验证码');
        document.getElementById('captcha').focus();
        return false;
    }
    
    if (captcha.length !== 6) {
        showValidationError('验证码必须是6位');
        document.getElementById('captcha').focus();
        return false;
    }
    
    return true;
}

/**
 * 显示验证错误
 */
function showValidationError(message) {
    // 创建或更新错误提示
    let errorDiv = document.querySelector('.validation-error');
    if (!errorDiv) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'alert alert-danger validation-error';
        document.getElementById('loginForm').parentNode.insertBefore(errorDiv, document.getElementById('loginForm'));
    }
    
    errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${message}`;
    
    // 添加震动效果
    errorDiv.style.animation = 'shake 0.5s ease-in-out';
    
    // 自动消失
    setTimeout(() => {
        if (errorDiv.parentElement) {
            errorDiv.remove();
        }
    }, 3000);
}

/**
 * 添加加载动画
 */
function addLoadingAnimations() {
    const submitButton = document.querySelector('.btn-login');
    
    // 添加提交动画
    submitButton.addEventListener('click', function() {
        if (validateForm()) {
            this.classList.add('loading');
            
            // 添加进度条
            const progressBar = document.createElement('div');
            progressBar.className = 'login-progress';
            progressBar.style.cssText = `
                position: absolute;
                bottom: 0;
                left: 0;
                height: 3px;
                background: rgba(255, 255, 255, 0.8);
                width: 0%;
                transition: width 2s ease;
            `;
            
            this.appendChild(progressBar);
            
            // 模拟进度
            setTimeout(() => {
                progressBar.style.width = '100%';
            }, 100);
        }
    });
}

/**
 * 添加主题切换功能
 */
function addThemeToggle() {
    // 创建主题切换按钮
    const themeToggle = document.createElement('button');
    themeToggle.type = 'button';
    themeToggle.className = 'theme-toggle';
    themeToggle.innerHTML = '<i class="fas fa-moon"></i>';
    themeToggle.title = '切换主题';
    
    // 添加样式
    themeToggle.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: rgba(255, 255, 255, 0.2);
        border: none;
        border-radius: 50%;
        width: 50px;
        height: 50px;
        color: white;
        cursor: pointer;
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;
        z-index: 1000;
    `;
    
    document.body.appendChild(themeToggle);
    
    // 切换主题
    themeToggle.addEventListener('click', function() {
        document.body.classList.toggle('dark-theme');
        const isDark = document.body.classList.contains('dark-theme');
        
        this.innerHTML = isDark ? '<i class="fas fa-sun"></i>' : '<i class="fas fa-moon"></i>';
        this.title = isDark ? '切换到亮色主题' : '切换到暗色主题';
        
        // 保存主题偏好
        localStorage.setItem('theme', isDark ? 'dark' : 'light');
    });
    
    // 加载保存的主题
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
        themeToggle.innerHTML = '<i class="fas fa-sun"></i>';
        themeToggle.title = '切换到亮色主题';
    }
}

/**
 * 添加网络状态检测
 */
function addNetworkStatusDetection() {
    // 检测网络状态
    function updateNetworkStatus() {
        const isOnline = navigator.onLine;
        
        if (!isOnline) {
            showNetworkError();
        } else {
            hideNetworkError();
        }
    }
    
    // 监听网络状态变化
    window.addEventListener('online', updateNetworkStatus);
    window.addEventListener('offline', updateNetworkStatus);
    
    // 初始检查
    updateNetworkStatus();
}

/**
 * 显示网络错误
 */
function showNetworkError() {
    let networkError = document.querySelector('.network-error');
    if (!networkError) {
        networkError = document.createElement('div');
        networkError.className = 'alert alert-warning network-error';
        networkError.innerHTML = '<i class="fas fa-wifi me-2"></i>网络连接已断开，请检查网络设置';
        document.body.appendChild(networkError);
        
        networkError.style.cssText = `
            position: fixed;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 10000;
            margin: 0;
        `;
    }
}

/**
 * 隐藏网络错误
 */
function hideNetworkError() {
    const networkError = document.querySelector('.network-error');
    if (networkError) {
        networkError.remove();
    }
}
