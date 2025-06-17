/**
 * Toast 通知组件
 * Toast Notification Component
 */

class ToastComponent {
    constructor(options = {}) {
        // 默认配置
        this.config = {
            // 容器配置
            container: 'toast-container',
            position: 'top-end', // top-start, top-center, top-end, bottom-start, bottom-center, bottom-end
            
            // 样式配置
            theme: 'light', // light, dark
            animation: 'fade', // fade, slide
            
            // 行为配置
            autoHide: true,
            delay: 5000,
            showProgress: true,
            pauseOnHover: true,
            closeButton: true,
            
            // 声音配置
            sound: false,
            soundSuccess: '/sounds/success.mp3',
            soundError: '/sounds/error.mp3',
            soundWarning: '/sounds/warning.mp3',
            soundInfo: '/sounds/info.mp3',
            
            // 图标配置
            icons: {
                success: 'fas fa-check-circle',
                error: 'fas fa-exclamation-circle',
                warning: 'fas fa-exclamation-triangle',
                info: 'fas fa-info-circle'
            },
            
            // 颜色配置
            colors: {
                success: '#28a745',
                error: '#dc3545',
                warning: '#ffc107',
                info: '#17a2b8'
            },
            
            ...options
        };

        this.toasts = new Map();
        this.container = null;
        this.isInitialized = false;
        
        this.init();
    }

    /**
     * 初始化Toast组件
     */
    init() {
        try {
            // 创建或获取容器
            this.createContainer();
            
            // 绑定全局事件
            this.bindGlobalEvents();
            
            this.isInitialized = true;
            console.log('Toast组件初始化完成');
            
        } catch (error) {
            console.error('Toast组件初始化失败:', error);
        }
    }

    /**
     * 创建Toast容器
     */
    createContainer() {
        // 查找现有容器
        this.container = document.getElementById(this.config.container);
        
        if (!this.container) {
            // 创建新容器
            this.container = document.createElement('div');
            this.container.id = this.config.container;
            this.container.className = this.getContainerClasses();
            document.body.appendChild(this.container);
        } else {
            // 更新现有容器的类
            this.container.className = this.getContainerClasses();
        }
    }

    /**
     * 获取容器CSS类
     */
    getContainerClasses() {
        const baseClass = 'toast-container';
        const positionClass = `position-fixed ${this.getPositionClasses()}`;
        const themeClass = `toast-${this.config.theme}`;
        
        return `${baseClass} ${positionClass} ${themeClass}`;
    }

    /**
     * 获取位置CSS类
     */
    getPositionClasses() {
        const positions = {
            'top-start': 'top-0 start-0',
            'top-center': 'top-0 start-50 translate-middle-x',
            'top-end': 'top-0 end-0',
            'bottom-start': 'bottom-0 start-0',
            'bottom-center': 'bottom-0 start-50 translate-middle-x',
            'bottom-end': 'bottom-0 end-0'
        };
        
        return positions[this.config.position] || positions['top-end'];
    }

    /**
     * 绑定全局事件
     */
    bindGlobalEvents() {
        // 监听键盘事件（ESC关闭所有Toast）
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.hideAll();
            }
        });
        
        // 监听页面可见性变化
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.pauseAll();
            } else {
                this.resumeAll();
            }
        });
    }

    /**
     * 显示成功Toast
     */
    success(message, options = {}) {
        return this.show(message, 'success', options);
    }

    /**
     * 显示错误Toast
     */
    error(message, options = {}) {
        return this.show(message, 'error', options);
    }

    /**
     * 显示警告Toast
     */
    warning(message, options = {}) {
        return this.show(message, 'warning', options);
    }

    /**
     * 显示信息Toast
     */
    info(message, options = {}) {
        return this.show(message, 'info', options);
    }

    /**
     * 显示Toast
     */
    show(message, type = 'info', options = {}) {
        if (!this.isInitialized) {
            console.warn('Toast组件未初始化');
            return null;
        }

        // 合并配置
        const config = { ...this.config, ...options };
        
        // 生成唯一ID
        const toastId = Utils.generateUUID();
        
        // 创建Toast元素
        const toastElement = this.createToastElement(toastId, message, type, config);
        
        // 添加到容器
        this.container.appendChild(toastElement);
        
        // 创建Toast实例
        const toast = new ToastInstance(toastId, toastElement, type, config, this);
        
        // 保存实例
        this.toasts.set(toastId, toast);
        
        // 显示Toast
        toast.show();
        
        // 播放声音
        if (config.sound) {
            this.playSound(type);
        }
        
        return toast;
    }

    /**
     * 创建Toast元素
     */
    createToastElement(id, message, type, config) {
        const toast = document.createElement('div');
        toast.id = id;
        toast.className = this.getToastClasses(type, config);
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        
        const icon = config.icons[type] || config.icons.info;
        const color = config.colors[type] || config.colors.info;
        
        toast.innerHTML = `
            <div class="toast-header">
                <i class="${icon} toast-icon me-2" style="color: ${color}"></i>
                <strong class="toast-title me-auto">${this.getTypeTitle(type)}</strong>
                <small class="toast-time text-muted">刚刚</small>
                ${config.closeButton ? '<button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="关闭"></button>' : ''}
            </div>
            <div class="toast-body">
                ${message}
            </div>
            ${config.showProgress ? '<div class="toast-progress"><div class="toast-progress-bar"></div></div>' : ''}
        `;
        
        return toast;
    }

    /**
     * 获取Toast CSS类
     */
    getToastClasses(type, config) {
        const baseClass = 'toast';
        const typeClass = `toast-${type}`;
        const animationClass = `toast-${config.animation}`;
        const themeClass = `toast-${config.theme}`;
        
        return `${baseClass} ${typeClass} ${animationClass} ${themeClass}`;
    }

    /**
     * 获取类型标题
     */
    getTypeTitle(type) {
        const titles = {
            success: '成功',
            error: '错误',
            warning: '警告',
            info: '信息'
        };
        
        return titles[type] || titles.info;
    }

    /**
     * 播放声音
     */
    playSound(type) {
        try {
            const soundMap = {
                success: this.config.soundSuccess,
                error: this.config.soundError,
                warning: this.config.soundWarning,
                info: this.config.soundInfo
            };
            
            const soundUrl = soundMap[type];
            if (soundUrl) {
                const audio = new Audio(soundUrl);
                audio.volume = 0.5;
                audio.play().catch(e => {
                    console.warn('播放Toast声音失败:', e);
                });
            }
        } catch (error) {
            console.warn('播放Toast声音失败:', error);
        }
    }

    /**
     * 隐藏指定Toast
     */
    hide(toastId) {
        const toast = this.toasts.get(toastId);
        if (toast) {
            toast.hide();
        }
    }

    /**
     * 隐藏所有Toast
     */
    hideAll() {
        this.toasts.forEach(toast => {
            toast.hide();
        });
    }

    /**
     * 暂停所有Toast
     */
    pauseAll() {
        this.toasts.forEach(toast => {
            toast.pause();
        });
    }

    /**
     * 恢复所有Toast
     */
    resumeAll() {
        this.toasts.forEach(toast => {
            toast.resume();
        });
    }

    /**
     * 移除Toast实例
     */
    removeToast(toastId) {
        this.toasts.delete(toastId);
    }

    /**
     * 获取Toast数量
     */
    getCount() {
        return this.toasts.size;
    }

    /**
     * 清理所有Toast
     */
    clear() {
        this.hideAll();
        this.toasts.clear();
        if (this.container) {
            this.container.innerHTML = '';
        }
    }

    /**
     * 销毁组件
     */
    destroy() {
        this.clear();
        
        if (this.container && this.container.parentNode) {
            this.container.parentNode.removeChild(this.container);
        }
        
        this.isInitialized = false;
        console.log('Toast组件已销毁');
    }
}

/**
 * Toast实例类
 */
class ToastInstance {
    constructor(id, element, type, config, manager) {
        this.id = id;
        this.element = element;
        this.type = type;
        this.config = config;
        this.manager = manager;
        
        this.isVisible = false;
        this.isPaused = false;
        this.timer = null;
        this.progressTimer = null;
        this.startTime = null;
        this.remainingTime = config.delay;
        
        this.bindEvents();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 关闭按钮事件
        const closeBtn = this.element.querySelector('.btn-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                this.hide();
            });
        }
        
        // 鼠标悬停事件
        if (this.config.pauseOnHover) {
            this.element.addEventListener('mouseenter', () => {
                this.pause();
            });
            
            this.element.addEventListener('mouseleave', () => {
                this.resume();
            });
        }
        
        // 点击事件
        this.element.addEventListener('click', (e) => {
            if (e.target !== closeBtn) {
                this.handleClick();
            }
        });
    }

    /**
     * 显示Toast
     */
    show() {
        // 添加显示动画
        this.element.classList.add('showing');
        
        // 强制重排以触发动画
        this.element.offsetHeight;
        
        // 显示Toast
        this.element.classList.add('show');
        this.element.classList.remove('showing');
        
        this.isVisible = true;
        
        // 更新时间显示
        this.updateTime();
        
        // 设置自动隐藏
        if (this.config.autoHide) {
            this.startAutoHide();
        }
        
        // 启动进度条
        if (this.config.showProgress) {
            this.startProgress();
        }
    }

    /**
     * 隐藏Toast
     */
    hide() {
        if (!this.isVisible) return;
        
        // 清理定时器
        this.clearTimers();
        
        // 添加隐藏动画
        this.element.classList.add('hiding');
        
        // 动画结束后移除元素
        setTimeout(() => {
            this.remove();
        }, 300);
        
        this.isVisible = false;
    }

    /**
     * 移除Toast元素
     */
    remove() {
        if (this.element && this.element.parentNode) {
            this.element.parentNode.removeChild(this.element);
        }
        
        // 从管理器中移除
        this.manager.removeToast(this.id);
    }

    /**
     * 暂停自动隐藏
     */
    pause() {
        if (this.isPaused || !this.config.autoHide) return;
        
        this.isPaused = true;
        
        // 清理定时器
        this.clearTimers();
        
        // 计算剩余时间
        if (this.startTime) {
            const elapsed = Date.now() - this.startTime;
            this.remainingTime = Math.max(0, this.config.delay - elapsed);
        }
        
        // 暂停进度条
        if (this.config.showProgress) {
            this.pauseProgress();
        }
    }

    /**
     * 恢复自动隐藏
     */
    resume() {
        if (!this.isPaused || !this.config.autoHide) return;
        
        this.isPaused = false;
        
        // 重新开始自动隐藏
        this.startAutoHide();
        
        // 恢复进度条
        if (this.config.showProgress) {
            this.resumeProgress();
        }
    }

    /**
     * 开始自动隐藏
     */
    startAutoHide() {
        this.startTime = Date.now();
        
        this.timer = setTimeout(() => {
            this.hide();
        }, this.remainingTime);
    }

    /**
     * 开始进度条动画
     */
    startProgress() {
        const progressBar = this.element.querySelector('.toast-progress-bar');
        if (!progressBar) return;
        
        progressBar.style.animationDuration = `${this.remainingTime}ms`;
        progressBar.classList.add('toast-progress-running');
    }

    /**
     * 暂停进度条
     */
    pauseProgress() {
        const progressBar = this.element.querySelector('.toast-progress-bar');
        if (!progressBar) return;
        
        progressBar.style.animationPlayState = 'paused';
    }

    /**
     * 恢复进度条
     */
    resumeProgress() {
        const progressBar = this.element.querySelector('.toast-progress-bar');
        if (!progressBar) return;
        
        progressBar.style.animationPlayState = 'running';
    }

    /**
     * 更新时间显示
     */
    updateTime() {
        const timeElement = this.element.querySelector('.toast-time');
        if (timeElement) {
            timeElement.textContent = new Date().toLocaleTimeString('zh-CN', {
                hour: '2-digit',
                minute: '2-digit'
            });
        }
    }

    /**
     * 处理点击事件
     */
    handleClick() {
        // 可以在这里添加点击处理逻辑
        console.log(`Toast clicked: ${this.id}`);
    }

    /**
     * 清理定时器
     */
    clearTimers() {
        if (this.timer) {
            clearTimeout(this.timer);
            this.timer = null;
        }
        
        if (this.progressTimer) {
            clearTimeout(this.progressTimer);
            this.progressTimer = null;
        }
    }
}

// 创建全局实例
window.Toast = new ToastComponent();

// 兼容性方法
window.showToast = (message, type = 'info', options = {}) => {
    return window.Toast.show(message, type, options);
};

window.showSuccess = (message, options = {}) => {
    return window.Toast.success(message, options);
};

window.showError = (message, options = {}) => {
    return window.Toast.error(message, options);
};

window.showWarning = (message, options = {}) => {
    return window.Toast.warning(message, options);
};

window.showInfo = (message, options = {}) => {
    return window.Toast.info(message, options);
};
