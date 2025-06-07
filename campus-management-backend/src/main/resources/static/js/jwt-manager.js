/**
 * JWT Token管理器
 * 负责Token的自动刷新和状态监控
 */

// 避免重复声明
if (typeof window.JwtManager === 'undefined') {

class JwtManager {
    constructor() {
        this.refreshInterval = null;
        this.checkInterval = 5 * 60 * 1000; // 5分钟检查一次
        this.refreshThreshold = 30 * 60; // 30分钟内自动刷新
        this.init();
    }

    /**
     * 初始化JWT管理器
     */
    init() {
        this.startTokenCheck();
        this.setupBeforeUnloadHandler();
    }

    /**
     * 开始Token状态检查
     */
    startTokenCheck() {
        // 立即检查一次
        this.checkTokenStatus();
        
        // 设置定期检查
        this.refreshInterval = setInterval(() => {
            this.checkTokenStatus();
        }, this.checkInterval);
    }

    /**
     * 停止Token检查
     */
    stopTokenCheck() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
    }

    /**
     * 检查Token状态
     */
    async checkTokenStatus() {
        try {
            const response = await fetch('/admin/token-status', {
                method: 'GET',
                credentials: 'same-origin'
            });

            if (response.ok) {
                const data = await response.json();
                
                if (data.valid) {
                    console.log('Token状态:', data);
                    
                    // 检查是否需要刷新
                    if (data.expiresIn <= this.refreshThreshold) {
                        console.log('Token即将过期，尝试刷新...');
                        await this.refreshToken();
                    }
                } else {
                    console.warn('Token无效:', data.message);
                    this.handleTokenExpired();
                }
            } else {
                console.error('检查Token状态失败:', response.status);
            }
        } catch (error) {
            console.error('检查Token状态时发生错误:', error);
        }
    }

    /**
     * 刷新Token
     */
    async refreshToken() {
        try {
            const response = await fetch('/admin/refresh-token', {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                
                if (data.success) {
                    console.log('Token刷新成功，新的过期时间:', data.expiresIn, '秒');
                    
                    // 显示刷新成功提示（可选）
                    this.showNotification('Token已自动刷新', 'success');
                } else {
                    console.error('Token刷新失败:', data.message);
                    this.handleTokenExpired();
                }
            } else {
                console.error('Token刷新请求失败:', response.status);
                this.handleTokenExpired();
            }
        } catch (error) {
            console.error('刷新Token时发生错误:', error);
            this.handleTokenExpired();
        }
    }

    /**
     * 处理Token过期
     */
    handleTokenExpired() {
        this.stopTokenCheck();
        
        // 显示过期提示
        this.showNotification('登录已过期，请重新登录', 'error');
        
        // 延迟跳转到登录页面
        setTimeout(() => {
            window.location.href = '/admin/login?error=token_expired';
        }, 2000);
    }

    /**
     * 显示通知消息
     */
    showNotification(message, type = 'info') {
        // 创建通知元素
        const notification = document.createElement('div');
        notification.className = `jwt-notification jwt-notification-${type}`;
        notification.innerHTML = `
            <div class="jwt-notification-content">
                <span class="jwt-notification-message">${message}</span>
                <button class="jwt-notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
        `;

        // 添加样式
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 10000;
            padding: 12px 16px;
            border-radius: 4px;
            color: white;
            font-size: 14px;
            max-width: 300px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            background-color: ${type === 'success' ? '#52c41a' : type === 'error' ? '#ff4d4f' : '#1890ff'};
        `;

        // 添加到页面
        document.body.appendChild(notification);

        // 自动移除
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }

    /**
     * 设置页面卸载前的处理
     */
    setupBeforeUnloadHandler() {
        window.addEventListener('beforeunload', () => {
            this.stopTokenCheck();
        });
    }

    /**
     * 手动刷新Token
     */
    async manualRefresh() {
        console.log('手动刷新Token...');
        await this.refreshToken();
    }

    /**
     * 获取Token状态信息
     */
    async getTokenInfo() {
        try {
            const response = await fetch('/admin/token-status', {
                method: 'GET',
                credentials: 'same-origin'
            });

            if (response.ok) {
                return await response.json();
            } else {
                throw new Error('获取Token信息失败');
            }
        } catch (error) {
            console.error('获取Token信息时发生错误:', error);
            return null;
        }
    }
}

// 全局JWT管理器实例
let jwtManager = null;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 只在管理后台页面初始化
    if (window.location.pathname.startsWith('/admin/') && 
        !window.location.pathname.includes('/login')) {
        jwtManager = new JwtManager();
        
        // 将管理器实例暴露到全局，方便调试
        window.jwtManager = jwtManager;
        window.JwtManager = JwtManager;

        console.log('JWT管理器已初始化');
    }
});

// 导出给其他脚本使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = JwtManager;
}

} // 结束条件检查
