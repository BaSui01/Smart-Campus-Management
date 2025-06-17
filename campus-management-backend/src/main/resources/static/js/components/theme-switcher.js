/**
 * 主题切换组件
 * Theme Switcher Component
 */

class ThemeSwitcherComponent {
    constructor(options = {}) {
        // 默认配置
        this.config = {
            // 存储配置
            storageKey: 'campus-theme',
            
            // 主题配置
            themes: {
                light: {
                    name: 'light',
                    label: '浅色主题',
                    icon: 'fas fa-sun',
                    description: '明亮清爽的浅色界面'
                },
                dark: {
                    name: 'dark',
                    label: '深色主题',
                    icon: 'fas fa-moon',
                    description: '护眼舒适的深色界面'
                },
                auto: {
                    name: 'auto',
                    label: '跟随系统',
                    icon: 'fas fa-desktop',
                    description: '根据系统设置自动切换'
                }
            },
            
            // 默认主题
            defaultTheme: 'light',
            
            // 切换动画
            enableTransition: true,
            transitionDuration: 300,
            
            // 自动检测系统主题
            autoDetect: true,
            
            // 事件回调
            onThemeChange: null,
            
            ...options
        };

        this.currentTheme = null;
        this.systemTheme = null;
        this.isInitialized = false;
        this.mediaQuery = null;
        
        this.init();
    }

    /**
     * 初始化主题切换器
     */
    init() {
        try {
            console.log('初始化主题切换器...');
            
            // 检测系统主题
            this.detectSystemTheme();
            
            // 加载保存的主题
            this.loadSavedTheme();
            
            // 应用主题
            this.applyTheme(this.currentTheme);
            
            // 绑定事件
            this.bindEvents();
            
            // 更新切换按钮
            this.updateToggleButtons();
            
            this.isInitialized = true;
            console.log(`主题切换器初始化完成，当前主题: ${this.currentTheme}`);
            
        } catch (error) {
            console.error('主题切换器初始化失败:', error);
        }
    }

    /**
     * 检测系统主题
     */
    detectSystemTheme() {
        if (!this.config.autoDetect) return;
        
        try {
            // 检查浏览器是否支持媒体查询
            if (window.matchMedia) {
                this.mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
                this.systemTheme = this.mediaQuery.matches ? 'dark' : 'light';
                
                // 监听系统主题变化
                this.mediaQuery.addEventListener('change', (e) => {
                    this.systemTheme = e.matches ? 'dark' : 'light';
                    console.log(`系统主题变化: ${this.systemTheme}`);
                    
                    // 如果当前是自动模式，则应用新的系统主题
                    if (this.currentTheme === 'auto') {
                        this.applyTheme('auto');
                    }
                });
                
                console.log(`检测到系统主题: ${this.systemTheme}`);
            } else {
                console.warn('浏览器不支持媒体查询，无法检测系统主题');
                this.systemTheme = 'light';
            }
        } catch (error) {
            console.error('检测系统主题失败:', error);
            this.systemTheme = 'light';
        }
    }

    /**
     * 加载保存的主题
     */
    loadSavedTheme() {
        try {
            const savedTheme = localStorage.getItem(this.config.storageKey);
            
            if (savedTheme && this.config.themes[savedTheme]) {
                this.currentTheme = savedTheme;
                console.log(`加载保存的主题: ${savedTheme}`);
            } else {
                this.currentTheme = this.config.defaultTheme;
                console.log(`使用默认主题: ${this.currentTheme}`);
            }
        } catch (error) {
            console.error('加载保存的主题失败:', error);
            this.currentTheme = this.config.defaultTheme;
        }
    }

    /**
     * 保存主题设置
     */
    saveTheme(theme) {
        try {
            localStorage.setItem(this.config.storageKey, theme);
            console.log(`主题设置已保存: ${theme}`);
        } catch (error) {
            console.error('保存主题设置失败:', error);
        }
    }

    /**
     * 应用主题
     */
    applyTheme(theme) {
        if (!theme || !this.config.themes[theme]) {
            console.warn(`无效的主题: ${theme}`);
            return;
        }

        try {
            // 确定实际要应用的主题
            let actualTheme = theme;
            if (theme === 'auto') {
                actualTheme = this.systemTheme || 'light';
            }
            
            // 添加过渡动画
            if (this.config.enableTransition) {
                this.addTransition();
            }
            
            // 移除所有主题类
            Object.keys(this.config.themes).forEach(themeName => {
                if (themeName !== 'auto') {
                    document.documentElement.removeAttribute(`data-theme`);
                    document.body.classList.remove(`theme-${themeName}`);
                }
            });
            
            // 应用新主题
            if (actualTheme !== 'light') {
                document.documentElement.setAttribute('data-theme', actualTheme);
                document.body.classList.add(`theme-${actualTheme}`);
            }
            
            // 更新当前主题
            this.currentTheme = theme;
            
            // 移除过渡动画
            if (this.config.enableTransition) {
                setTimeout(() => {
                    this.removeTransition();
                }, this.config.transitionDuration);
            }
            
            // 保存主题设置
            this.saveTheme(theme);
            
            // 更新切换按钮
            this.updateToggleButtons();
            
            // 触发回调
            if (this.config.onThemeChange) {
                this.config.onThemeChange(theme, actualTheme);
            }
            
            // 触发自定义事件
            this.dispatchThemeChangeEvent(theme, actualTheme);
            
            console.log(`主题已应用: ${theme} (实际: ${actualTheme})`);
            
        } catch (error) {
            console.error('应用主题失败:', error);
        }
    }

    /**
     * 切换主题
     */
    toggleTheme() {
        const themes = Object.keys(this.config.themes);
        const currentIndex = themes.indexOf(this.currentTheme);
        const nextIndex = (currentIndex + 1) % themes.length;
        const nextTheme = themes[nextIndex];
        
        this.setTheme(nextTheme);
    }

    /**
     * 设置主题
     */
    setTheme(theme) {
        if (theme === this.currentTheme) {
            console.log(`主题未变化: ${theme}`);
            return;
        }
        
        this.applyTheme(theme);
    }

    /**
     * 获取当前主题
     */
    getCurrentTheme() {
        return this.currentTheme;
    }

    /**
     * 获取实际应用的主题
     */
    getActualTheme() {
        if (this.currentTheme === 'auto') {
            return this.systemTheme || 'light';
        }
        return this.currentTheme;
    }

    /**
     * 获取系统主题
     */
    getSystemTheme() {
        return this.systemTheme;
    }

    /**
     * 获取可用主题列表
     */
    getAvailableThemes() {
        return Object.values(this.config.themes);
    }

    /**
     * 添加过渡动画
     */
    addTransition() {
        const style = document.createElement('style');
        style.id = 'theme-transition';
        style.textContent = `
            *, *::before, *::after {
                transition: background-color ${this.config.transitionDuration}ms ease,
                           color ${this.config.transitionDuration}ms ease,
                           border-color ${this.config.transitionDuration}ms ease,
                           box-shadow ${this.config.transitionDuration}ms ease !important;
            }
        `;
        document.head.appendChild(style);
    }

    /**
     * 移除过渡动画
     */
    removeTransition() {
        const style = document.getElementById('theme-transition');
        if (style) {
            style.remove();
        }
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 绑定主题切换按钮事件
        document.addEventListener('click', (e) => {
            // 主题切换按钮
            if (e.target.matches('[data-action="toggle-theme"]') || 
                e.target.closest('[data-action="toggle-theme"]')) {
                e.preventDefault();
                this.toggleTheme();
            }
            
            // 特定主题按钮
            const themeBtn = e.target.closest('[data-theme]');
            if (themeBtn) {
                e.preventDefault();
                const theme = themeBtn.getAttribute('data-theme');
                this.setTheme(theme);
            }
        });
        
        // 键盘快捷键 (Ctrl/Cmd + Shift + T)
        document.addEventListener('keydown', (e) => {
            if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'T') {
                e.preventDefault();
                this.toggleTheme();
            }
        });
    }

    /**
     * 更新切换按钮
     */
    updateToggleButtons() {
        // 更新主题切换按钮
        const toggleButtons = document.querySelectorAll('[data-action="toggle-theme"]');
        toggleButtons.forEach(button => {
            this.updateToggleButton(button);
        });
        
        // 更新主题选择按钮
        const themeButtons = document.querySelectorAll('[data-theme]');
        themeButtons.forEach(button => {
            this.updateThemeButton(button);
        });
        
        // 更新主题选择器
        const themeSelectors = document.querySelectorAll('.theme-selector');
        themeSelectors.forEach(selector => {
            this.updateThemeSelector(selector);
        });
    }

    /**
     * 更新切换按钮
     */
    updateToggleButton(button) {
        const theme = this.config.themes[this.currentTheme];
        if (!theme) return;
        
        // 更新图标
        const icon = button.querySelector('i');
        if (icon) {
            icon.className = theme.icon;
        }
        
        // 更新文本
        const text = button.querySelector('.theme-text');
        if (text) {
            text.textContent = theme.label;
        }
        
        // 更新工具提示
        button.setAttribute('title', `当前: ${theme.label}`);
        button.setAttribute('data-bs-original-title', `当前: ${theme.label}`);
    }

    /**
     * 更新主题按钮
     */
    updateThemeButton(button) {
        const buttonTheme = button.getAttribute('data-theme');
        const isActive = buttonTheme === this.currentTheme;
        
        button.classList.toggle('active', isActive);
        button.setAttribute('aria-pressed', isActive);
    }

    /**
     * 更新主题选择器
     */
    updateThemeSelector(selector) {
        // 清空现有选项
        selector.innerHTML = '';
        
        // 添加主题选项
        Object.values(this.config.themes).forEach(theme => {
            const option = document.createElement('div');
            option.className = `theme-option ${theme.name === this.currentTheme ? 'active' : ''}`;
            option.setAttribute('data-theme', theme.name);
            option.innerHTML = `
                <div class="theme-option-icon">
                    <i class="${theme.icon}"></i>
                </div>
                <div class="theme-option-content">
                    <div class="theme-option-name">${theme.label}</div>
                    <div class="theme-option-description">${theme.description}</div>
                </div>
                <div class="theme-option-check">
                    <i class="fas fa-check"></i>
                </div>
            `;
            selector.appendChild(option);
        });
    }

    /**
     * 触发主题变化事件
     */
    dispatchThemeChangeEvent(theme, actualTheme) {
        const event = new CustomEvent('themechange', {
            detail: {
                theme: theme,
                actualTheme: actualTheme,
                systemTheme: this.systemTheme
            }
        });
        document.dispatchEvent(event);
    }

    /**
     * 创建主题选择器HTML
     */
    createThemeSelector() {
        const selectorHtml = `
            <div class="theme-selector-container">
                <div class="theme-selector-header">
                    <h6 class="theme-selector-title">选择主题</h6>
                </div>
                <div class="theme-selector">
                    <!-- 主题选项将动态生成 -->
                </div>
            </div>
        `;
        
        return selectorHtml;
    }

    /**
     * 获取主题信息
     */
    getThemeInfo() {
        return {
            current: this.currentTheme,
            actual: this.getActualTheme(),
            system: this.systemTheme,
            available: this.getAvailableThemes()
        };
    }

    /**
     * 重置主题
     */
    reset() {
        this.setTheme(this.config.defaultTheme);
    }

    /**
     * 销毁组件
     */
    destroy() {
        // 移除媒体查询监听器
        if (this.mediaQuery) {
            this.mediaQuery.removeEventListener('change', this.handleSystemThemeChange);
        }
        
        // 移除过渡样式
        this.removeTransition();
        
        this.isInitialized = false;
        console.log('主题切换器已销毁');
    }
}

// 创建全局实例
window.ThemeSwitcher = new ThemeSwitcherComponent();

// 兼容性方法
window.toggleTheme = () => {
    window.ThemeSwitcher.toggleTheme();
};

window.setTheme = (theme) => {
    window.ThemeSwitcher.setTheme(theme);
};

window.getCurrentTheme = () => {
    return window.ThemeSwitcher.getCurrentTheme();
};

// 监听主题变化事件
document.addEventListener('themechange', (e) => {
    console.log('主题已变化:', e.detail);
});
