/**
 * 统一认证管理器
 * 负责登录、登出、Token管理等认证相关功能
 * 智慧校园管理系统
 */

// 避免重复声明
if (typeof window.AuthManager === 'undefined') {

class AuthManager {
    constructor() {
        this.token = null;
        this.user = null;
        this.refreshTimer = null;
        this.tokenKey = 'campus_auth_token';
        this.userKey = 'campus_auth_user';
        this.init();
    }

    /**
     * 初始化认证管理器
     */
    init() {
        console.log('🔐 初始化认证管理器...');
        
        // 从本地存储恢复认证信息
        this.loadFromStorage();
        
        // 设置Token自动刷新
        this.setupTokenRefresh();
        
        // 监听页面卸载事件
        this.setupBeforeUnloadHandler();
        
        console.log('✅ 认证管理器初始化完成');
    }

    /**
     * 用户登录
     */
    async login(credentials) {
        console.log('🚀 开始用户登录...');
        
        try {
            const response = await fetch('/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(credentials),
                credentials: 'include'
            });

            if (response.ok) {
                // 检查是否重定向到仪表盘
                if (response.url.includes('/admin/dashboard')) {
                    console.log('✅ 登录成功，正在获取认证信息...');
                    
                    // 获取认证信息
                    await this.fetchAuthInfo();
                    
                    return {
                        success: true,
                        message: '登录成功',
                        redirectUrl: '/admin/dashboard'
                    };
                } else {
                    // 登录失败，解析错误信息
                    const text = await response.text();
                    const errorMatch = text.match(/class="alert alert-danger"[^>]*>[\s\S]*?<span[^>]*>(.*?)<\/span>/);
                    const errorMessage = errorMatch ? errorMatch[1].trim() : '登录失败';
                    
                    return {
                        success: false,
                        message: errorMessage
                    };
                }
            } else {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
        } catch (error) {
            console.error('❌ 登录失败:', error);
            return {
                success: false,
                message: '网络错误，请稍后重试'
            };
        }
    }

    /**
     * 获取认证信息
     */
    async fetchAuthInfo() {
        try {
            console.log('📡 获取认证信息...');
            
            // 获取Token状态
            const tokenResponse = await fetch('/admin/token-status', {
                method: 'GET',
                credentials: 'include'
            });

            if (tokenResponse.ok) {
                const tokenData = await tokenResponse.json();
                console.log('🎫 Token信息:', tokenData);
                
                if (tokenData.valid && tokenData.token) {
                    this.token = tokenData.token;
                    this.saveToStorage();
                }
            }

            // 获取用户信息
            const userResponse = await fetch('/admin/current-user', {
                method: 'GET',
                credentials: 'include'
            });

            if (userResponse.ok) {
                const userData = await userResponse.json();
                console.log('👤 用户信息:', userData);
                
                this.user = userData;
                this.saveToStorage();
            }

        } catch (error) {
            console.error('❌ 获取认证信息失败:', error);
        }
    }

    /**
     * 用户登出
     */
    async logout() {
        console.log('🚪 用户登出...');
        
        try {
            await fetch('/admin/logout', {
                method: 'POST',
                credentials: 'include'
            });
        } catch (error) {
            console.error('登出请求失败:', error);
        } finally {
            // 清除本地认证信息
            this.clearAuth();
            
            // 重定向到登录页面
            window.location.href = '/admin/login';
        }
    }

    /**
     * 获取当前Token
     */
    getToken() {
        return this.token;
    }

    /**
     * 获取当前用户
     */
    getUser() {
        return this.user;
    }

    /**
     * 检查是否已认证
     */
    isAuthenticated() {
        return !!(this.token && this.user);
    }

    /**
     * 刷新Token
     */
    async refreshToken() {
        console.log('🔄 刷新Token...');
        
        try {
            const response = await fetch('/admin/refresh-token', {
                method: 'POST',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success && data.token) {
                    this.token = data.token;
                    this.saveToStorage();
                    console.log('✅ Token刷新成功');
                    return true;
                }
            }
            
            console.warn('⚠️ Token刷新失败');
            return false;
        } catch (error) {
            console.error('❌ Token刷新错误:', error);
            return false;
        }
    }

    /**
     * 保存到本地存储
     */
    saveToStorage() {
        if (this.token) {
            localStorage.setItem(this.tokenKey, this.token);
        }
        if (this.user) {
            localStorage.setItem(this.userKey, JSON.stringify(this.user));
        }
    }

    /**
     * 从本地存储加载
     */
    loadFromStorage() {
        this.token = localStorage.getItem(this.tokenKey);
        const userStr = localStorage.getItem(this.userKey);
        if (userStr) {
            try {
                this.user = JSON.parse(userStr);
            } catch (error) {
                console.error('解析用户信息失败:', error);
            }
        }
    }

    /**
     * 清除认证信息
     */
    clearAuth() {
        this.token = null;
        this.user = null;
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        
        // 清除定时器
        if (this.refreshTimer) {
            clearInterval(this.refreshTimer);
            this.refreshTimer = null;
        }
    }

    /**
     * 设置Token自动刷新
     */
    setupTokenRefresh() {
        // 每5分钟检查一次Token状态
        this.refreshTimer = setInterval(async () => {
            if (this.isAuthenticated()) {
                await this.checkTokenStatus();
            }
        }, 5 * 60 * 1000);
    }

    /**
     * 检查Token状态
     */
    async checkTokenStatus() {
        try {
            const response = await fetch('/admin/token-status', {
                method: 'GET',
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                if (!data.valid) {
                    console.warn('⚠️ Token已失效，需要重新登录');
                    this.clearAuth();
                    window.location.href = '/admin/login?error=token_expired';
                }
            }
        } catch (error) {
            console.error('检查Token状态失败:', error);
        }
    }

    /**
     * 设置页面卸载前的处理
     */
    setupBeforeUnloadHandler() {
        window.addEventListener('beforeunload', () => {
            if (this.refreshTimer) {
                clearInterval(this.refreshTimer);
            }
        });
    }

    /**
     * 获取认证头
     */
    getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        return headers;
    }
}

// 创建全局认证管理器实例
const authManager = new AuthManager();

// 将认证管理器暴露到全局作用域
window.authManager = authManager;
window.AuthManager = AuthManager;

// 导出给其他模块使用
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AuthManager;
}

console.log('🔐 认证管理器已加载');

} // 结束条件检查
