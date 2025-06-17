/**
 * 表单构建器组件
 * Form Builder Component
 */

class FormBuilderComponent {
    constructor(containerId, options = {}) {
        this.containerId = containerId;
        this.container = document.getElementById(containerId);
        
        if (!this.container) {
            throw new Error(`Container with id "${containerId}" not found`);
        }

        // 默认配置
        this.config = {
            // 表单配置
            method: 'POST',
            action: '',
            enctype: 'application/x-www-form-urlencoded',
            
            // 布局配置
            layout: 'vertical', // vertical, horizontal, inline
            labelWidth: 'col-md-3',
            inputWidth: 'col-md-9',
            
            // 验证配置
            validation: true,
            validateOnSubmit: true,
            validateOnBlur: true,
            validateOnInput: false,
            
            // 提交配置
            submitButton: true,
            submitText: '提交',
            submitClass: 'btn-primary',
            resetButton: false,
            resetText: '重置',
            resetClass: 'btn-secondary',
            
            // 样式配置
            formClass: '',
            fieldClass: 'mb-3',
            
            // 字段配置
            fields: [],
            
            // 事件回调
            onSubmit: null,
            onReset: null,
            onFieldChange: null,
            onValidate: null,
            
            ...options
        };

        this.formData = {};
        this.validators = {};
        this.errors = {};
        this.isSubmitting = false;
        this.isInitialized = false;
    }

    /**
     * 初始化表单
     */
    init() {
        try {
            console.log(`初始化表单构建器: ${this.containerId}`);
            
            // 创建表单结构
            this.createFormStructure();
            
            // 绑定事件
            this.bindEvents();
            
            // 初始化验证器
            this.initValidators();
            
            this.isInitialized = true;
            console.log(`表单构建器初始化完成: ${this.containerId}`);
            
        } catch (error) {
            console.error('表单构建器初始化失败:', error);
        }
    }

    /**
     * 创建表单结构
     */
    createFormStructure() {
        const formHtml = `
            <form class="form-builder ${this.config.formClass}" 
                  method="${this.config.method}" 
                  action="${this.config.action}"
                  ${this.config.enctype !== 'application/x-www-form-urlencoded' ? `enctype="${this.config.enctype}"` : ''}
                  novalidate>
                
                <!-- 表单字段 -->
                <div class="form-fields">
                    ${this.createFormFields()}
                </div>
                
                <!-- 表单按钮 -->
                <div class="form-actions">
                    ${this.createFormButtons()}
                </div>
            </form>
        `;
        
        this.container.innerHTML = formHtml;
        this.form = this.container.querySelector('form');
    }

    /**
     * 创建表单字段
     */
    createFormFields() {
        return this.config.fields.map(field => {
            return this.createFormField(field);
        }).join('');
    }

    /**
     * 创建单个表单字段
     */
    createFormField(field) {
        const fieldId = field.name || field.id || Utils.generateUUID();
        const fieldClass = `${this.config.fieldClass} field-${field.type}`;
        const isRequired = field.required || field.rules?.includes('required');
        const requiredMark = isRequired ? '<span class="text-danger">*</span>' : '';
        
        let fieldHtml = '';
        
        // 根据布局类型创建字段
        switch (this.config.layout) {
            case 'horizontal':
                fieldHtml = `
                    <div class="${fieldClass}">
                        <div class="row">
                            <label class="${this.config.labelWidth} col-form-label" for="${fieldId}">
                                ${field.label}${requiredMark}
                            </label>
                            <div class="${this.config.inputWidth}">
                                ${this.createFieldInput(field, fieldId)}
                                ${this.createFieldHelp(field)}
                                ${this.createFieldError(fieldId)}
                            </div>
                        </div>
                    </div>
                `;
                break;
                
            case 'inline':
                fieldHtml = `
                    <div class="${fieldClass} d-inline-block me-3">
                        <label class="form-label" for="${fieldId}">
                            ${field.label}${requiredMark}
                        </label>
                        ${this.createFieldInput(field, fieldId)}
                        ${this.createFieldError(fieldId)}
                    </div>
                `;
                break;
                
            default: // vertical
                fieldHtml = `
                    <div class="${fieldClass}">
                        <label class="form-label" for="${fieldId}">
                            ${field.label}${requiredMark}
                        </label>
                        ${this.createFieldInput(field, fieldId)}
                        ${this.createFieldHelp(field)}
                        ${this.createFieldError(fieldId)}
                    </div>
                `;
                break;
        }
        
        return fieldHtml;
    }

    /**
     * 创建字段输入控件
     */
    createFieldInput(field, fieldId) {
        const commonAttrs = this.getCommonAttributes(field, fieldId);
        const inputClass = this.getInputClass(field);
        
        switch (field.type) {
            case 'text':
            case 'email':
            case 'password':
            case 'url':
            case 'tel':
            case 'number':
                return `<input type="${field.type}" class="${inputClass}" ${commonAttrs}>`;
                
            case 'textarea':
                const rows = field.rows || 3;
                return `<textarea class="${inputClass}" rows="${rows}" ${commonAttrs}></textarea>`;
                
            case 'select':
                return this.createSelectField(field, fieldId, inputClass);
                
            case 'radio':
                return this.createRadioField(field, fieldId);
                
            case 'checkbox':
                return this.createCheckboxField(field, fieldId);
                
            case 'file':
                return this.createFileField(field, fieldId, inputClass);
                
            case 'date':
            case 'datetime-local':
            case 'time':
                return `<input type="${field.type}" class="${inputClass}" ${commonAttrs}>`;
                
            case 'hidden':
                return `<input type="hidden" ${commonAttrs}>`;
                
            case 'custom':
                return field.html || '';
                
            default:
                return `<input type="text" class="${inputClass}" ${commonAttrs}>`;
        }
    }

    /**
     * 创建下拉选择字段
     */
    createSelectField(field, fieldId, inputClass) {
        const multiple = field.multiple ? 'multiple' : '';
        const size = field.size ? `size="${field.size}"` : '';
        
        let optionsHtml = '';
        
        if (field.placeholder) {
            optionsHtml += `<option value="">${field.placeholder}</option>`;
        }
        
        if (field.options) {
            optionsHtml += field.options.map(option => {
                const value = typeof option === 'object' ? option.value : option;
                const text = typeof option === 'object' ? option.text : option;
                const selected = field.value === value ? 'selected' : '';
                const disabled = option.disabled ? 'disabled' : '';
                
                return `<option value="${value}" ${selected} ${disabled}>${text}</option>`;
            }).join('');
        }
        
        return `
            <select class="${inputClass}" id="${fieldId}" name="${field.name}" ${multiple} ${size}>
                ${optionsHtml}
            </select>
        `;
    }

    /**
     * 创建单选按钮字段
     */
    createRadioField(field, fieldId) {
        if (!field.options) return '';
        
        return field.options.map((option, index) => {
            const value = typeof option === 'object' ? option.value : option;
            const text = typeof option === 'object' ? option.text : option;
            const checked = field.value === value ? 'checked' : '';
            const disabled = option.disabled ? 'disabled' : '';
            const radioId = `${fieldId}_${index}`;
            
            return `
                <div class="form-check ${field.inline ? 'form-check-inline' : ''}">
                    <input class="form-check-input" type="radio" 
                           id="${radioId}" name="${field.name}" 
                           value="${value}" ${checked} ${disabled}>
                    <label class="form-check-label" for="${radioId}">
                        ${text}
                    </label>
                </div>
            `;
        }).join('');
    }

    /**
     * 创建复选框字段
     */
    createCheckboxField(field, fieldId) {
        if (field.options) {
            // 多选复选框
            return field.options.map((option, index) => {
                const value = typeof option === 'object' ? option.value : option;
                const text = typeof option === 'object' ? option.text : option;
                const checked = Array.isArray(field.value) && field.value.includes(value) ? 'checked' : '';
                const disabled = option.disabled ? 'disabled' : '';
                const checkboxId = `${fieldId}_${index}`;
                
                return `
                    <div class="form-check ${field.inline ? 'form-check-inline' : ''}">
                        <input class="form-check-input" type="checkbox" 
                               id="${checkboxId}" name="${field.name}[]" 
                               value="${value}" ${checked} ${disabled}>
                        <label class="form-check-label" for="${checkboxId}">
                            ${text}
                        </label>
                    </div>
                `;
            }).join('');
        } else {
            // 单个复选框
            const checked = field.value ? 'checked' : '';
            return `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" 
                           id="${fieldId}" name="${field.name}" 
                           value="1" ${checked}>
                    <label class="form-check-label" for="${fieldId}">
                        ${field.checkboxLabel || field.label}
                    </label>
                </div>
            `;
        }
    }

    /**
     * 创建文件上传字段
     */
    createFileField(field, fieldId, inputClass) {
        const multiple = field.multiple ? 'multiple' : '';
        const accept = field.accept ? `accept="${field.accept}"` : '';
        
        return `
            <input type="file" class="${inputClass}" 
                   id="${fieldId}" name="${field.name}" 
                   ${multiple} ${accept}>
        `;
    }

    /**
     * 获取通用属性
     */
    getCommonAttributes(field, fieldId) {
        const attrs = [];
        
        attrs.push(`id="${fieldId}"`);
        attrs.push(`name="${field.name}"`);
        
        if (field.value !== undefined) {
            attrs.push(`value="${Utils.escapeHtml(String(field.value))}"`);
        }
        
        if (field.placeholder) {
            attrs.push(`placeholder="${field.placeholder}"`);
        }
        
        if (field.readonly) {
            attrs.push('readonly');
        }
        
        if (field.disabled) {
            attrs.push('disabled');
        }
        
        if (field.required) {
            attrs.push('required');
        }
        
        if (field.min !== undefined) {
            attrs.push(`min="${field.min}"`);
        }
        
        if (field.max !== undefined) {
            attrs.push(`max="${field.max}"`);
        }
        
        if (field.step !== undefined) {
            attrs.push(`step="${field.step}"`);
        }
        
        if (field.pattern) {
            attrs.push(`pattern="${field.pattern}"`);
        }
        
        if (field.maxlength) {
            attrs.push(`maxlength="${field.maxlength}"`);
        }
        
        if (field.autocomplete) {
            attrs.push(`autocomplete="${field.autocomplete}"`);
        }
        
        if (field.attributes) {
            Object.entries(field.attributes).forEach(([key, value]) => {
                attrs.push(`${key}="${value}"`);
            });
        }
        
        return attrs.join(' ');
    }

    /**
     * 获取输入控件CSS类
     */
    getInputClass(field) {
        const baseClass = field.type === 'file' ? 'form-control' : 'form-control';
        const sizeClass = field.size ? `form-control-${field.size}` : '';
        const customClass = field.class || '';
        
        return [baseClass, sizeClass, customClass].filter(Boolean).join(' ');
    }

    /**
     * 创建字段帮助文本
     */
    createFieldHelp(field) {
        if (!field.help) return '';
        
        return `<div class="form-text">${field.help}</div>`;
    }

    /**
     * 创建字段错误提示
     */
    createFieldError(fieldId) {
        return `<div class="invalid-feedback" id="${fieldId}_error"></div>`;
    }

    /**
     * 创建表单按钮
     */
    createFormButtons() {
        let buttonsHtml = '';
        
        if (this.config.submitButton) {
            buttonsHtml += `
                <button type="submit" class="btn ${this.config.submitClass} submit-btn">
                    <span class="btn-text">${this.config.submitText}</span>
                    <span class="btn-spinner spinner-border spinner-border-sm d-none" role="status">
                        <span class="visually-hidden">提交中...</span>
                    </span>
                </button>
            `;
        }
        
        if (this.config.resetButton) {
            buttonsHtml += `
                <button type="reset" class="btn ${this.config.resetClass} reset-btn ms-2">
                    ${this.config.resetText}
                </button>
            `;
        }
        
        return buttonsHtml;
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        if (!this.form) return;
        
        // 表单提交事件
        this.form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleSubmit();
        });
        
        // 表单重置事件
        this.form.addEventListener('reset', (e) => {
            this.handleReset();
        });
        
        // 字段变化事件
        this.form.addEventListener('input', (e) => {
            this.handleFieldChange(e.target);
        });
        
        this.form.addEventListener('change', (e) => {
            this.handleFieldChange(e.target);
        });
        
        // 字段失焦验证
        if (this.config.validateOnBlur) {
            this.form.addEventListener('blur', (e) => {
                if (e.target.matches('input, select, textarea')) {
                    this.validateField(e.target);
                }
            }, true);
        }
    }

    /**
     * 初始化验证器
     */
    initValidators() {
        this.config.fields.forEach(field => {
            if (field.rules) {
                this.validators[field.name] = this.parseValidationRules(field.rules);
            }
        });
    }

    /**
     * 解析验证规则
     */
    parseValidationRules(rules) {
        const validators = [];
        
        if (typeof rules === 'string') {
            rules = rules.split('|');
        }
        
        if (Array.isArray(rules)) {
            rules.forEach(rule => {
                if (typeof rule === 'string') {
                    const [name, ...params] = rule.split(':');
                    validators.push({ name, params: params.join(':').split(',') });
                } else if (typeof rule === 'object') {
                    validators.push(rule);
                }
            });
        }
        
        return validators;
    }

    // 其他方法将在后续文件中继续...
}

// 导出组件
window.FormBuilder = FormBuilderComponent;
