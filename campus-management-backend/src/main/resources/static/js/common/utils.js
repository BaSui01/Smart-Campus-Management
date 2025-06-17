/**
 * 通用工具函数库
 * Common Utilities Library
 */

// 全局工具对象
window.Utils = {
    
    /**
     * 格式化日期
     * @param {Date|string} date 日期对象或字符串
     * @param {string} format 格式化模式，默认 'YYYY-MM-DD HH:mm:ss'
     * @returns {string} 格式化后的日期字符串
     */
    formatDate: function(date, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!date) return '';
        
        const d = new Date(date);
        if (isNaN(d.getTime())) return '';
        
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },
    
    /**
     * 格式化文件大小
     * @param {number} bytes 字节数
     * @param {number} decimals 小数位数，默认2
     * @returns {string} 格式化后的文件大小
     */
    formatFileSize: function(bytes, decimals = 2) {
        if (bytes === 0) return '0 Bytes';
        
        const k = 1024;
        const dm = decimals < 0 ? 0 : decimals;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
        
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        
        return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
    },
    
    /**
     * 格式化数字
     * @param {number} num 数字
     * @param {number} decimals 小数位数，默认0
     * @returns {string} 格式化后的数字
     */
    formatNumber: function(num, decimals = 0) {
        if (isNaN(num)) return '0';
        return Number(num).toLocaleString('zh-CN', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        });
    },
    
    /**
     * 防抖函数
     * @param {Function} func 要防抖的函数
     * @param {number} wait 等待时间（毫秒）
     * @param {boolean} immediate 是否立即执行
     * @returns {Function} 防抖后的函数
     */
    debounce: function(func, wait, immediate) {
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
    },
    
    /**
     * 节流函数
     * @param {Function} func 要节流的函数
     * @param {number} limit 时间间隔（毫秒）
     * @returns {Function} 节流后的函数
     */
    throttle: function(func, limit) {
        let inThrottle;
        return function(...args) {
            if (!inThrottle) {
                func.apply(this, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        };
    },
    
    /**
     * 深拷贝对象
     * @param {any} obj 要拷贝的对象
     * @returns {any} 拷贝后的对象
     */
    deepClone: function(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => this.deepClone(item));
        if (typeof obj === 'object') {
            const clonedObj = {};
            for (const key in obj) {
                if (obj.hasOwnProperty(key)) {
                    clonedObj[key] = this.deepClone(obj[key]);
                }
            }
            return clonedObj;
        }
    },
    
    /**
     * 生成UUID
     * @returns {string} UUID字符串
     */
    generateUUID: function() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            const r = Math.random() * 16 | 0;
            const v = c === 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    },
    
    /**
     * 获取URL参数
     * @param {string} name 参数名
     * @param {string} url URL字符串，默认为当前页面URL
     * @returns {string|null} 参数值
     */
    getUrlParameter: function(name, url = window.location.href) {
        name = name.replace(/[\[\]]/g, '\\$&');
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)');
        const results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    },
    
    /**
     * 设置URL参数
     * @param {string} name 参数名
     * @param {string} value 参数值
     * @param {string} url URL字符串，默认为当前页面URL
     * @returns {string} 新的URL
     */
    setUrlParameter: function(name, value, url = window.location.href) {
        const urlObj = new URL(url);
        urlObj.searchParams.set(name, value);
        return urlObj.toString();
    },
    
    /**
     * 移除URL参数
     * @param {string} name 参数名
     * @param {string} url URL字符串，默认为当前页面URL
     * @returns {string} 新的URL
     */
    removeUrlParameter: function(name, url = window.location.href) {
        const urlObj = new URL(url);
        urlObj.searchParams.delete(name);
        return urlObj.toString();
    },
    
    /**
     * 验证邮箱格式
     * @param {string} email 邮箱地址
     * @returns {boolean} 是否有效
     */
    isValidEmail: function(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },
    
    /**
     * 验证手机号格式
     * @param {string} phone 手机号
     * @returns {boolean} 是否有效
     */
    isValidPhone: function(phone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        return phoneRegex.test(phone);
    },
    
    /**
     * 验证身份证号格式
     * @param {string} idCard 身份证号
     * @returns {boolean} 是否有效
     */
    isValidIdCard: function(idCard) {
        const idCardRegex = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
        return idCardRegex.test(idCard);
    },
    
    /**
     * 转义HTML字符
     * @param {string} text 要转义的文本
     * @returns {string} 转义后的文本
     */
    escapeHtml: function(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    },
    
    /**
     * 反转义HTML字符
     * @param {string} html 要反转义的HTML
     * @returns {string} 反转义后的文本
     */
    unescapeHtml: function(html) {
        const map = {
            '&amp;': '&',
            '&lt;': '<',
            '&gt;': '>',
            '&quot;': '"',
            '&#039;': "'"
        };
        return html.replace(/&amp;|&lt;|&gt;|&quot;|&#039;/g, function(m) { return map[m]; });
    },
    
    /**
     * 获取随机颜色
     * @returns {string} 十六进制颜色值
     */
    getRandomColor: function() {
        return '#' + Math.floor(Math.random() * 16777215).toString(16);
    },
    
    /**
     * 计算两个日期之间的天数差
     * @param {Date|string} date1 日期1
     * @param {Date|string} date2 日期2
     * @returns {number} 天数差
     */
    daysBetween: function(date1, date2) {
        const oneDay = 24 * 60 * 60 * 1000;
        const firstDate = new Date(date1);
        const secondDate = new Date(date2);
        return Math.round(Math.abs((firstDate - secondDate) / oneDay));
    },
    
    /**
     * 检查是否为移动设备
     * @returns {boolean} 是否为移动设备
     */
    isMobile: function() {
        return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    },
    
    /**
     * 获取浏览器信息
     * @returns {object} 浏览器信息对象
     */
    getBrowserInfo: function() {
        const ua = navigator.userAgent;
        let browser = 'Unknown';
        let version = 'Unknown';
        
        if (ua.indexOf('Chrome') > -1) {
            browser = 'Chrome';
            version = ua.match(/Chrome\/(\d+)/)[1];
        } else if (ua.indexOf('Firefox') > -1) {
            browser = 'Firefox';
            version = ua.match(/Firefox\/(\d+)/)[1];
        } else if (ua.indexOf('Safari') > -1) {
            browser = 'Safari';
            version = ua.match(/Version\/(\d+)/)[1];
        } else if (ua.indexOf('Edge') > -1) {
            browser = 'Edge';
            version = ua.match(/Edge\/(\d+)/)[1];
        }
        
        return { browser, version };
    },
    
    /**
     * 复制文本到剪贴板
     * @param {string} text 要复制的文本
     * @returns {Promise<boolean>} 是否成功
     */
    copyToClipboard: async function(text) {
        try {
            if (navigator.clipboard) {
                await navigator.clipboard.writeText(text);
                return true;
            } else {
                // 降级方案
                const textArea = document.createElement('textarea');
                textArea.value = text;
                document.body.appendChild(textArea);
                textArea.select();
                document.execCommand('copy');
                document.body.removeChild(textArea);
                return true;
            }
        } catch (err) {
            console.error('复制失败:', err);
            return false;
        }
    },
    
    /**
     * 下载文件
     * @param {string} url 文件URL
     * @param {string} filename 文件名
     */
    downloadFile: function(url, filename) {
        const link = document.createElement('a');
        link.href = url;
        link.download = filename || 'download';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    },
    
    /**
     * 获取文件扩展名
     * @param {string} filename 文件名
     * @returns {string} 扩展名
     */
    getFileExtension: function(filename) {
        return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2);
    },
    
    /**
     * 检查对象是否为空
     * @param {any} obj 要检查的对象
     * @returns {boolean} 是否为空
     */
    isEmpty: function(obj) {
        if (obj == null) return true;
        if (Array.isArray(obj) || typeof obj === 'string') return obj.length === 0;
        if (typeof obj === 'object') return Object.keys(obj).length === 0;
        return false;
    }
};

// 导出到全局
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Utils;
}
