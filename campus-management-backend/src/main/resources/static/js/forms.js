/**
 * 表单通用功能脚本
 */

// 全局表单配置
const FormConfig = {
    // 验证规则
    validation: {
        studentId: /^\d{10}$/,
        idCard: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/,
        phone: /^1[3-9]\d{9}$/,
        email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    },
    
    // 错误消息
    messages: {
        required: '此字段为必填项',
        studentId: '学号格式不正确，应为10位数字',
        idCard: '身份证号格式不正确',
        phone: '手机号格式不正确',
        email: '邮箱格式不正确',
        dateRange: '结束日期必须晚于开始日期',
        numberRange: '数值超出允许范围'
    }
};

// 表单工具类
class FormUtils {
    
    /**
     * 显示提示消息
     */
    static showAlert(message, type = 'info', duration = 3000) {
        // 移除现有的提示
        const existingAlert = document.querySelector('.form-alert');
        if (existingAlert) {
            existingAlert.remove();
        }
        
        // 创建新的提示
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show form-alert`;
        alert.style.position = 'fixed';
        alert.style.top = '20px';
        alert.style.right = '20px';
        alert.style.zIndex = '9999';
        alert.style.minWidth = '300px';
        
        alert.innerHTML = `
            <i class="fas fa-${this.getAlertIcon(type)}"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alert);
        
        // 自动移除
        if (duration > 0) {
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.remove();
                }
            }, duration);
        }
    }
    
    /**
     * 获取提示图标
     */
    static getAlertIcon(type) {
        const icons = {
            success: 'check-circle',
            danger: 'exclamation-triangle',
            warning: 'exclamation-circle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    }
    
    /**
     * 验证字段
     */
    static validateField(field, value) {
        const rules = FormConfig.validation;
        const messages = FormConfig.messages;
        
        // 必填验证
        if (field.hasAttribute('required') && !value.trim()) {
            return { valid: false, message: messages.required };
        }
        
        // 特定格式验证
        const fieldType = field.getAttribute('data-validate') || field.type;
        
        if (value.trim() && rules[fieldType]) {
            if (!rules[fieldType].test(value)) {
                return { valid: false, message: messages[fieldType] };
            }
        }
        
        // 数值范围验证
        if (field.type === 'number' && value) {
            const min = field.getAttribute('min');
            const max = field.getAttribute('max');
            const numValue = parseFloat(value);
            
            if (min && numValue < parseFloat(min)) {
                return { valid: false, message: `最小值为 ${min}` };
            }
            
            if (max && numValue > parseFloat(max)) {
                return { valid: false, message: `最大值为 ${max}` };
            }
        }
        
        return { valid: true };
    }
    
    /**
     * 设置字段验证状态
     */
    static setFieldValidation(field, isValid, message = '') {
        const feedback = field.parentNode.querySelector('.invalid-feedback') || 
                        field.parentNode.querySelector('.valid-feedback');
        
        // 移除现有状态
        field.classList.remove('is-valid', 'is-invalid');
        if (feedback) {
            feedback.remove();
        }
        
        // 设置新状态
        if (isValid) {
            field.classList.add('is-valid');
        } else {
            field.classList.add('is-invalid');
            
            // 添加错误消息
            const errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            errorDiv.textContent = message;
            field.parentNode.appendChild(errorDiv);
        }
    }
    
    /**
     * 验证整个表单
     */
    static validateForm(form) {
        let isValid = true;
        const fields = form.querySelectorAll('input, select, textarea');
        
        fields.forEach(field => {
            const value = field.value;
            const validation = this.validateField(field, value);
            
            this.setFieldValidation(field, validation.valid, validation.message);
            
            if (!validation.valid) {
                isValid = false;
            }
        });
        
        return isValid;
    }
    
    /**
     * 重置表单验证状态
     */
    static resetFormValidation(form) {
        const fields = form.querySelectorAll('input, select, textarea');
        
        fields.forEach(field => {
            field.classList.remove('is-valid', 'is-invalid');
            const feedback = field.parentNode.querySelector('.invalid-feedback, .valid-feedback');
            if (feedback) {
                feedback.remove();
            }
        });
    }
    
    /**
     * 设置表单加载状态
     */
    static setFormLoading(form, loading = true) {
        if (loading) {
            form.classList.add('form-loading');
            form.querySelectorAll('button[type="submit"]').forEach(btn => {
                btn.disabled = true;
                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 保存中...';
            });
        } else {
            form.classList.remove('form-loading');
            form.querySelectorAll('button[type="submit"]').forEach(btn => {
                btn.disabled = false;
                btn.innerHTML = btn.getAttribute('data-original-text') || '保存';
            });
        }
    }
    
    /**
     * 自动保存表单数据到本地存储
     */
    static autoSaveForm(form, key) {
        const data = new FormData(form);
        const formData = {};
        
        for (let [key, value] of data.entries()) {
            formData[key] = value;
        }
        
        localStorage.setItem(`form_${key}`, JSON.stringify(formData));
    }
    
    /**
     * 从本地存储恢复表单数据
     */
    static restoreForm(form, key) {
        const savedData = localStorage.getItem(`form_${key}`);
        
        if (savedData) {
            try {
                const formData = JSON.parse(savedData);
                
                Object.keys(formData).forEach(fieldName => {
                    const field = form.querySelector(`[name="${fieldName}"]`);
                    if (field) {
                        field.value = formData[fieldName];
                    }
                });
                
                this.showAlert('已恢复上次填写的内容', 'info');
            } catch (e) {
                console.error('恢复表单数据失败:', e);
            }
        }
    }
    
    /**
     * 清除保存的表单数据
     */
    static clearSavedForm(key) {
        localStorage.removeItem(`form_${key}`);
    }
    
    /**
     * 格式化日期
     */
    static formatDate(date, format = 'YYYY-MM-DD') {
        if (!date) return '';
        
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        
        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day);
    }
    
    /**
     * 计算年龄
     */
    static calculateAge(birthDate) {
        if (!birthDate) return '';
        
        const today = new Date();
        const birth = new Date(birthDate);
        let age = today.getFullYear() - birth.getFullYear();
        const monthDiff = today.getMonth() - birth.getMonth();
        
        if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
            age--;
        }
        
        return age;
    }
    
    /**
     * 生成随机ID
     */
    static generateId(prefix = '', length = 8) {
        const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        let result = prefix;
        
        for (let i = 0; i < length; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        
        return result;
    }
}

// 表单初始化
document.addEventListener('DOMContentLoaded', function() {
    
    // 为所有表单添加实时验证
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        // 保存原始按钮文本
        form.querySelectorAll('button[type="submit"]').forEach(btn => {
            btn.setAttribute('data-original-text', btn.innerHTML);
        });
        
        // 实时验证
        form.addEventListener('input', function(e) {
            const field = e.target;
            if (field.matches('input, select, textarea')) {
                const validation = FormUtils.validateField(field, field.value);
                FormUtils.setFieldValidation(field, validation.valid, validation.message);
            }
        });
        
        // 表单提交验证
        form.addEventListener('submit', function(e) {
            if (!FormUtils.validateForm(form)) {
                e.preventDefault();
                FormUtils.showAlert('请检查表单中的错误信息', 'danger');
                
                // 滚动到第一个错误字段
                const firstError = form.querySelector('.is-invalid');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            }
        });
        
        // 重置按钮功能
        form.addEventListener('reset', function() {
            setTimeout(() => {
                FormUtils.resetFormValidation(form);
            }, 10);
        });
    });
    
    // 自动保存功能（可选）
    const autoSaveForms = document.querySelectorAll('[data-auto-save]');
    autoSaveForms.forEach(form => {
        const key = form.getAttribute('data-auto-save');
        
        // 恢复数据
        FormUtils.restoreForm(form, key);
        
        // 定时保存
        setInterval(() => {
            FormUtils.autoSaveForm(form, key);
        }, 30000); // 每30秒保存一次
        
        // 表单提交成功后清除保存的数据
        form.addEventListener('submit', function() {
            setTimeout(() => {
                FormUtils.clearSavedForm(key);
            }, 1000);
        });
    });
    
    // 日期字段自动计算年龄
    const birthDateFields = document.querySelectorAll('input[name="birthDate"]');
    birthDateFields.forEach(field => {
        field.addEventListener('change', function() {
            const age = FormUtils.calculateAge(this.value);
            const ageField = document.querySelector('input[name="age"]');
            if (ageField && age) {
                ageField.value = age;
            }
        });
    });
    
    // 文件上传预览
    const fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const preview = this.parentNode.querySelector('.file-preview');
                if (preview) {
                    if (file.type.startsWith('image/')) {
                        const reader = new FileReader();
                        reader.onload = function(e) {
                            preview.innerHTML = `<img src="${e.target.result}" style="max-width: 200px; max-height: 200px;">`;
                        };
                        reader.readAsDataURL(file);
                    } else {
                        preview.innerHTML = `<i class="fas fa-file"></i> ${file.name}`;
                    }
                }
            }
        });
    });
});

// 导出工具类供全局使用
window.FormUtils = FormUtils;
window.showAlert = FormUtils.showAlert;
