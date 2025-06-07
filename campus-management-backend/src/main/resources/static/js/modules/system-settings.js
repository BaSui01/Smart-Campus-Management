/**
 * 系统设置管理模块
 * 处理系统设置页面的所有交互功能
 */
class SystemSettingsManager {
    constructor() {
        this.currentSettings = {};
        this.isLoading = false;
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadSystemSettings();
        this.loadSystemStatistics();
        this.startStatisticsRefresh();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 延迟绑定事件，确保DOM完全加载
        setTimeout(() => {
            // 保存所有设置
            const saveAllBtn = document.querySelector('[onclick="saveAllSettings()"]');
            if (saveAllBtn) {
                console.log('Found saveAllBtn, binding event');
                saveAllBtn.removeAttribute('onclick');
                saveAllBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.saveAllSettings();
                });
            }

            // Logo上传
            const uploadLogoBtn = document.querySelector('[onclick="uploadLogo()"]');
            if (uploadLogoBtn) {
                console.log('Found uploadLogoBtn, binding event');
                uploadLogoBtn.removeAttribute('onclick');
                uploadLogoBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.uploadLogo();
                });
            }

            // 数据备份
            const backupBtn = document.querySelector('[onclick="backupData()"]');
            if (backupBtn) {
                console.log('Found backupBtn, binding event');
                backupBtn.removeAttribute('onclick');
                backupBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.backupData();
                });
            }

            // 清理缓存
            const clearCacheBtn = document.querySelector('[onclick="clearCache()"]');
            if (clearCacheBtn) {
                console.log('Found clearCacheBtn, binding event');
                clearCacheBtn.removeAttribute('onclick');
                clearCacheBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.clearCache();
                });
            }

            // 查看日志
            const viewLogsBtn = document.querySelector('[onclick="viewLogs()"]');
            if (viewLogsBtn) {
                console.log('Found viewLogsBtn, binding event');
                viewLogsBtn.removeAttribute('onclick');
                viewLogsBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.viewLogs();
                });
            }

            // 重启系统
            const restartBtn = document.querySelector('[onclick="restartSystem()"]');
            if (restartBtn) {
                console.log('Found restartBtn, binding event');
                restartBtn.removeAttribute('onclick');
                restartBtn.addEventListener('click', (e) => {
                    e.preventDefault();
                    this.restartSystem();
                });
            }

            // 表单变化监听
            this.bindFormChangeEvents();
            
            // 绑定刷新统计按钮
            this.bindRefreshStatsButton();
        }, 100);
    }

    /**
     * 绑定表单变化事件
     */
    bindFormChangeEvents() {
        const forms = ['basicSettingsForm', 'securitySettingsForm', 'notificationSettingsForm'];
        
        forms.forEach(formId => {
            const form = document.getElementById(formId);
            if (form) {
                const inputs = form.querySelectorAll('input, select, textarea');
                inputs.forEach(input => {
                    input.addEventListener('change', () => this.markFormAsChanged(form));
                    input.addEventListener('input', () => this.markFormAsChanged(form));
                });
            }
        });
    }

    /**
     * 绑定刷新统计按钮事件
     */
    bindRefreshStatsButton() {
        const refreshBtn = document.getElementById('refreshStatsBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                LoadingManager.showButtonLoading(refreshBtn, '刷新中...');
                this.loadSystemStatistics().finally(() => {
                    LoadingManager.hideButtonLoading(refreshBtn);
                });
            });
        }
    }

    /**
     * 标记表单已更改
     */
    markFormAsChanged(form) {
        form.classList.add('form-changed');
        
        // 添加保存提示
        let saveHint = form.querySelector('.save-hint');
        if (!saveHint) {
            saveHint = document.createElement('div');
            saveHint.className = 'save-hint alert alert-warning alert-sm mt-2';
            saveHint.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>设置已修改，请记得保存';
            form.appendChild(saveHint);
        }
    }

    /**
     * 清除表单变化标记
     */
    clearFormChangeMarks() {
        const changedForms = document.querySelectorAll('.form-changed');
        changedForms.forEach(form => {
            form.classList.remove('form-changed');
            const saveHint = form.querySelector('.save-hint');
            if (saveHint) {
                saveHint.remove();
            }
        });
    }

    /**
     * 加载系统设置
     */
    async loadSystemSettings() {
        try {
            LoadingManager.showPageLoading('.card-body');

            // 确保API客户端包含认证头
            const response = await apiClient.get('/api/system/settings');

            if (response.success) {
                this.currentSettings = response.data;
                this.populateSettingsForms(response.data);
                this.updateSettingsStatus('success');
                console.log('系统设置加载成功');
            } else {
                this.updateSettingsStatus('error');
                showAlert('加载系统设置失败：' + response.message, 'error');
            }
        } catch (error) {
            console.error('加载系统设置失败:', error);
            this.updateSettingsStatus('error');
            showAlert('加载系统设置失败：' + error.message, 'error');
        } finally {
            LoadingManager.hidePageLoading('.card-body');
        }
    }

    /**
     * 填充设置表单
     */
    populateSettingsForms(settings) {
        // 基本设置
        this.setFieldValue('systemName', settings.systemName);
        this.setFieldValue('systemVersion', settings.systemVersion);
        this.setFieldValue('systemLogo', settings.systemLogo);
        this.setFieldValue('contactEmail', settings.contactEmail);
        this.setFieldValue('contactPhone', settings.contactPhone);

        // 安全设置
        this.setFieldValue('maxLoginAttempts', settings.maxLoginAttempts);
        this.setFieldValue('sessionTimeout', settings.sessionTimeout);
        this.setFieldValue('passwordMinLength', settings.passwordMinLength);
        this.setFieldValue('enableCaptcha', settings.enableCaptcha, 'checkbox');

        // 通知设置
        this.setFieldValue('enableEmailNotification', settings.enableEmailNotification, 'checkbox');
        this.setFieldValue('enableSmsNotification', settings.enableSmsNotification, 'checkbox');
        this.setFieldValue('notificationEmail', settings.notificationEmail);
        this.setFieldValue('notificationPhone', settings.notificationPhone);
    }

    /**
     * 设置字段值
     */
    setFieldValue(fieldId, value, type = 'text') {
        const field = document.getElementById(fieldId);
        if (field) {
            if (type === 'checkbox') {
                field.checked = value === true || value === 'true';
            } else {
                field.value = value || '';
            }
        }
    }

    /**
     * 收集表单数据
     */
    collectFormData() {
        const data = {};

        // 基本设置
        data.systemName = document.getElementById('systemName')?.value?.trim();
        data.systemLogo = document.getElementById('systemLogo')?.value?.trim();
        data.contactEmail = document.getElementById('contactEmail')?.value?.trim();
        data.contactPhone = document.getElementById('contactPhone')?.value?.trim();

        // 安全设置
        data.maxLoginAttempts = parseInt(document.getElementById('maxLoginAttempts')?.value) || 5;
        data.sessionTimeout = parseInt(document.getElementById('sessionTimeout')?.value) || 30;
        data.passwordMinLength = parseInt(document.getElementById('passwordMinLength')?.value) || 8;
        data.enableCaptcha = document.getElementById('enableCaptcha')?.checked || false;

        // 通知设置
        data.enableEmailNotification = document.getElementById('enableEmailNotification')?.checked || false;
        data.enableSmsNotification = document.getElementById('enableSmsNotification')?.checked || false;
        data.notificationEmail = document.getElementById('notificationEmail')?.value?.trim();
        data.notificationPhone = document.getElementById('notificationPhone')?.value?.trim();

        return data;
    }

    /**
     * 验证表单数据
     */
    validateFormData(data) {
        const errors = [];

        if (!data.systemName) {
            errors.push('系统名称不能为空');
        }

        if (data.contactEmail && !this.isValidEmail(data.contactEmail)) {
            errors.push('联系邮箱格式不正确');
        }

        if (data.notificationEmail && !this.isValidEmail(data.notificationEmail)) {
            errors.push('通知邮箱格式不正确');
        }

        if (data.contactPhone && !this.isValidPhone(data.contactPhone)) {
            errors.push('联系电话格式不正确');
        }

        if (data.notificationPhone && !this.isValidPhone(data.notificationPhone)) {
            errors.push('通知手机格式不正确');
        }

        if (data.maxLoginAttempts < 1 || data.maxLoginAttempts > 10) {
            errors.push('最大登录尝试次数必须在1-10之间');
        }

        if (data.sessionTimeout < 5 || data.sessionTimeout > 120) {
            errors.push('会话超时时间必须在5-120分钟之间');
        }

        if (data.passwordMinLength < 4 || data.passwordMinLength > 20) {
            errors.push('密码最小长度必须在4-20之间');
        }

        return errors;
    }

    /**
     * 验证邮箱格式
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * 验证电话格式
     */
    isValidPhone(phone) {
        const phoneRegex = /^[1][3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/;
        return phoneRegex.test(phone);
    }

    /**
     * 保存所有设置
     */
    async saveAllSettings() {
        if (this.isLoading) return;

        try {
            this.isLoading = true;

            const formData = this.collectFormData();
            const errors = this.validateFormData(formData);

            if (errors.length > 0) {
                showAlert('数据验证失败：\n' + errors.join('\n'), 'error');
                return;
            }

            const saveBtn = document.querySelector('[onclick="saveAllSettings()"]') ||
                           document.getElementById('saveSettingsBtn');
            if (saveBtn) {
                LoadingManager.showButtonLoading(saveBtn, '保存中...');
            }

            // 确保API客户端包含认证头
            const response = await apiClient.post('/api/system/settings', formData);

            if (response.success) {
                this.currentSettings = { ...this.currentSettings, ...formData };
                this.clearFormChangeMarks();
                this.updateSettingsStatus('success');
                showAlert('系统设置保存成功', 'success');
            } else {
                showAlert('保存系统设置失败：' + response.message, 'error');
            }
        } catch (error) {
            console.error('保存系统设置失败:', error);
            showAlert('保存系统设置失败：' + error.message, 'error');
        } finally {
            this.isLoading = false;
            const saveBtn = document.querySelector('[onclick="saveAllSettings()"]') ||
                           document.getElementById('saveSettingsBtn');
            if (saveBtn) {
                LoadingManager.hideButtonLoading(saveBtn);
            }
        }
    }

    /**
     * 上传Logo
     */
    async uploadLogo() {
        try {
            // 创建文件选择器
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'image/*';
            input.style.display = 'none';

            input.onchange = async (event) => {
                const file = event.target.files[0];
                if (!file) return;

                // 验证文件大小（2MB）
                if (file.size > 2 * 1024 * 1024) {
                    MessageUtils.error('文件大小不能超过2MB');
                    return;
                }

                // 验证文件类型
                if (!file.type.startsWith('image/')) {
                    MessageUtils.error('请选择图片文件');
                    return;
                }

                try {
                    const uploadBtn = document.querySelector('[onclick="uploadLogo()"]');
                    LoadingManager.showButtonLoading(uploadBtn, '上传中...');

                    const formData = new FormData();
                    formData.append('file', file);

                    const response = await apiClient.upload('/api/system/upload-logo', formData);

                    if (response.success) {
                        // 更新Logo显示
                        document.getElementById('systemLogo').value = response.data.logoUrl;
                        MessageUtils.success('Logo上传成功');
                        
                        // 标记表单已更改
                        const form = document.getElementById('basicSettingsForm');
                        this.markFormAsChanged(form);
                    } else {
                        MessageUtils.error('Logo上传失败：' + response.message);
                    }
                } catch (error) {
                    console.error('Logo上传失败:', error);
                    MessageUtils.error('Logo上传失败：' + error.message);
                } finally {
                    const uploadBtn = document.querySelector('[onclick="uploadLogo()"]');
                    LoadingManager.hideButtonLoading(uploadBtn);
                }
            };

            // 触发文件选择
            document.body.appendChild(input);
            input.click();
            document.body.removeChild(input);
        } catch (error) {
            console.error('创建文件选择器失败:', error);
            MessageUtils.error('创建文件选择器失败');
        }
    }

    /**
     * 数据备份
     */
    async backupData() {
        const confirmed = await MessageUtils.confirm(
            '确定要进行数据备份吗？这可能需要几分钟时间。',
            '确认备份'
        );

        if (!confirmed) return;

        try {
            const backupBtn = document.querySelector('[onclick="backupData()"]');
            LoadingManager.showButtonLoading(backupBtn, '备份中...');

            const response = await apiClient.post('/api/system/backup');

            if (response.success) {
                const backupInfo = response.data;
                MessageUtils.success(
                    `数据备份成功！\n` +
                    `备份ID: ${backupInfo.backupId}\n` +
                    `备份时间: ${backupInfo.backupTime}\n` +
                    `备份大小: ${backupInfo.backupSize}`
                );
            } else {
                MessageUtils.error('数据备份失败：' + response.message);
            }
        } catch (error) {
            console.error('数据备份失败:', error);
            MessageUtils.error('数据备份失败：' + error.message);
        } finally {
            const backupBtn = document.querySelector('[onclick="backupData()"]');
            LoadingManager.hideButtonLoading(backupBtn);
        }
    }

    /**
     * 清理缓存
     */
    async clearCache() {
        const confirmed = await MessageUtils.confirm(
            '确定要清理系统缓存吗？',
            '确认清理'
        );

        if (!confirmed) return;

        try {
            const clearBtn = document.querySelector('[onclick="clearCache()"]');
            LoadingManager.showButtonLoading(clearBtn, '清理中...');

            const response = await apiClient.post('/api/system/clear-cache');

            if (response.success) {
                MessageUtils.success('系统缓存清理成功');
            } else {
                MessageUtils.error('清理系统缓存失败：' + response.message);
            }
        } catch (error) {
            console.error('清理系统缓存失败:', error);
            MessageUtils.error('清理系统缓存失败：' + error.message);
        } finally {
            const clearBtn = document.querySelector('[onclick="clearCache()"]');
            LoadingManager.hideButtonLoading(clearBtn);
        }
    }

    /**
     * 查看日志
     */
    async viewLogs() {
        try {
            // 创建日志查看模态框
            this.createLogViewModal();
            
            // 加载日志数据
            await this.loadSystemLogs(1, 50);
            
            // 显示模态框
            const modal = new bootstrap.Modal(document.getElementById('logViewModal'));
            modal.show();
        } catch (error) {
            console.error('查看日志失败:', error);
            MessageUtils.error('查看日志失败：' + error.message);
        }
    }

    /**
     * 创建日志查看模态框
     */
    createLogViewModal() {
        // 检查是否已存在
        let existingModal = document.getElementById('logViewModal');
        if (existingModal) {
            existingModal.remove();
        }

        const modalHtml = `
            <div class="modal fade" id="logViewModal" tabindex="-1">
                <div class="modal-dialog modal-xl">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">
                                <i class="fas fa-file-alt me-2"></i>系统日志
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <div>
                                    <select class="form-select form-select-sm d-inline-block w-auto me-2" id="logLevelFilter">
                                        <option value="">所有级别</option>
                                        <option value="ERROR">错误</option>
                                        <option value="WARN">警告</option>
                                        <option value="INFO">信息</option>
                                    </select>
                                    <button class="btn btn-outline-primary btn-sm" id="refreshLogsBtn">
                                        <i class="fas fa-refresh"></i> 刷新
                                    </button>
                                </div>
                                <div>
                                    <span class="text-muted">总计: <span id="logTotal">0</span> 条</span>
                                </div>
                            </div>
                            <div class="table-responsive" style="max-height: 400px;">
                                <table class="table table-sm table-striped">
                                    <thead>
                                        <tr>
                                            <th width="150">时间</th>
                                            <th width="80">级别</th>
                                            <th width="120">来源</th>
                                            <th>消息</th>
                                        </tr>
                                    </thead>
                                    <tbody id="logTableBody">
                                        <tr>
                                            <td colspan="4" class="text-center">
                                                <div class="spinner-border spinner-border-sm"></div>
                                                加载中...
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <select class="form-select form-select-sm d-inline-block w-auto" id="logPageSize">
                                        <option value="25">每页25条</option>
                                        <option value="50" selected>每页50条</option>
                                        <option value="100">每页100条</option>
                                    </select>
                                </div>
                                <nav>
                                    <ul class="pagination pagination-sm mb-0" id="logPagination">
                                    </ul>
                                </nav>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);

        // 绑定筛选事件
        document.getElementById('logLevelFilter').addEventListener('change', () => {
            this.refreshLogs();
        });

        document.getElementById('logPageSize').addEventListener('change', () => {
            this.refreshLogs();
        });

        // 绑定刷新按钮事件
        document.getElementById('refreshLogsBtn').addEventListener('click', () => {
            this.refreshLogs();
        });
    }

    /**
     * 加载系统日志
     */
    async loadSystemLogs(page = 1, size = 50) {
        try {
            const response = await apiClient.get('/api/system/logs', { page, size });

            if (response.success) {
                this.renderLogTable(response.data);
                this.renderLogPagination(response.data);
            } else {
                MessageUtils.error('加载系统日志失败：' + response.message);
            }
        } catch (error) {
            console.error('加载系统日志失败:', error);
            MessageUtils.error('加载系统日志失败：' + error.message);
        }
    }

    /**
     * 渲染日志表格
     */
    renderLogTable(data) {
        const tbody = document.getElementById('logTableBody');
        const totalSpan = document.getElementById('logTotal');

        if (totalSpan) {
            totalSpan.textContent = data.total;
        }

        if (!data.logs || data.logs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">暂无日志数据</td></tr>';
            return;
        }

        const rows = data.logs.map(log => {
            const levelClass = this.getLogLevelClass(log.level);
            const timestamp = new Date(log.timestamp).toLocaleString();
            
            return `
                <tr>
                    <td><small>${timestamp}</small></td>
                    <td><span class="badge ${levelClass}">${log.level}</span></td>
                    <td><small>${log.source}</small></td>
                    <td>${log.message}</td>
                </tr>
            `;
        }).join('');

        tbody.innerHTML = rows;
    }

    /**
     * 获取日志级别样式类
     */
    getLogLevelClass(level) {
        switch (level) {
            case 'ERROR': return 'bg-danger';
            case 'WARN': return 'bg-warning';
            case 'INFO': return 'bg-info';
            default: return 'bg-secondary';
        }
    }

    /**
     * 更新设置状态
     */
    updateSettingsStatus(status) {
        const statusElements = document.querySelectorAll('.settings-status');
        statusElements.forEach(element => {
            element.className = 'settings-status';
            if (status === 'success') {
                element.classList.add('text-success');
                element.innerHTML = '<i class="fas fa-check-circle me-1"></i>设置已同步';
            } else if (status === 'error') {
                element.classList.add('text-danger');
                element.innerHTML = '<i class="fas fa-exclamation-circle me-1"></i>设置同步失败';
            } else {
                element.classList.add('text-warning');
                element.innerHTML = '<i class="fas fa-sync-alt me-1"></i>正在同步...';
            }
        });
    }

    /**
     * 加载系统统计信息
     */
    async loadSystemStatistics() {
        try {
            const response = await apiClient.get('/api/system/statistics');

            if (response.success) {
                this.updateStatistics(response.data);
            } else {
                console.error('加载系统统计失败:', response.message);
            }
        } catch (error) {
            console.error('加载系统统计失败:', error);
        }
    }

    /**
     * 更新统计信息显示
     */
    updateStatistics(stats) {
        const statElements = {
            totalUsers: document.getElementById('totalUsers'),
            totalStudents: document.getElementById('totalStudents'),
            totalClasses: document.getElementById('totalClasses'),
            totalCourses: document.getElementById('totalCourses'),
            systemUptime: document.getElementById('systemUptime'),
            memoryUsage: document.getElementById('memoryUsage'),
            diskUsage: document.getElementById('diskUsage'),
            cpuUsage: document.getElementById('cpuUsage')
        };

        Object.keys(statElements).forEach(key => {
            if (statElements[key] && stats[key] !== undefined) {
                if (key.includes('Usage')) {
                    statElements[key].textContent = stats[key] + '%';
                } else {
                    statElements[key].textContent = stats[key];
                }
            }
        });

        // 更新最后更新时间
        const lastUpdateElement = document.getElementById('lastStatsUpdate');
        if (lastUpdateElement) {
            lastUpdateElement.textContent = new Date().toLocaleString();
        }
    }

    /**
     * 开始统计信息自动刷新
     */
    startStatisticsRefresh() {
        // 每30秒刷新一次统计信息
        setInterval(() => {
            this.loadSystemStatistics();
        }, 30000);
    }

    /**
     * 刷新日志
     */
    refreshLogs() {
        const page = 1;
        const size = parseInt(document.getElementById('logPageSize')?.value) || 50;
        this.loadSystemLogs(page, size);
    }

    /**
     * 重启系统
     */
    async restartSystem() {
        const confirmed = await MessageUtils.confirm(
            '确定要重启系统吗？这将中断所有用户的连接。',
            '确认重启',
            'warning'
        );

        if (!confirmed) return;

        try {
            const restartBtn = document.querySelector('[onclick="restartSystem()"]');
            LoadingManager.showButtonLoading(restartBtn, '重启中...');

            const response = await apiClient.post('/api/system/restart');

            if (response.success) {
                MessageUtils.success('系统重启指令已发送，请稍候...');

                // 5秒后刷新页面
                setTimeout(() => {
                    window.location.reload();
                }, 5000);
            } else {
                MessageUtils.error('系统重启失败：' + response.message);
            }
        } catch (error) {
            console.error('系统重启失败:', error);
            MessageUtils.error('系统重启失败：' + error.message);
        } finally {
            const restartBtn = document.querySelector('[onclick="restartSystem()"]');
            LoadingManager.hideButtonLoading(restartBtn);
        }
    }

    /**
     * 渲染日志分页
     */
    renderLogPagination(data) {
        const pagination = document.getElementById('logPagination');
        if (!pagination) return;

        const totalPages = Math.ceil(data.total / data.size);
        const currentPage = data.page;

        let paginationHtml = '';

        // 上一页
        if (currentPage > 1) {
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="systemSettings.loadSystemLogs(${currentPage - 1}, ${data.size})">上一页</a>
                </li>
            `;
        }

        // 页码
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="systemSettings.loadSystemLogs(${i}, ${data.size})">${i}</a>
                </li>
            `;
        }

        // 下一页
        if (currentPage < totalPages) {
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" onclick="systemSettings.loadSystemLogs(${currentPage + 1}, ${data.size})">下一页</a>
                </li>
            `;
        }

        pagination.innerHTML = paginationHtml;
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    window.systemSettings = new SystemSettingsManager();
});

// 兼容原有的全局函数（防止页面中的onclick调用失效）
function saveAllSettings() {
    if (window.systemSettings) {
        window.systemSettings.saveAllSettings();
    }
}

function uploadLogo() {
    if (window.systemSettings) {
        window.systemSettings.uploadLogo();
    }
}

function backupData() {
    if (window.systemSettings) {
        window.systemSettings.backupData();
    }
}

function clearCache() {
    if (window.systemSettings) {
        window.systemSettings.clearCache();
    }
}

function viewLogs() {
    if (window.systemSettings) {
        window.systemSettings.viewLogs();
    }
}

function restartSystem() {
    if (window.systemSettings) {
        window.systemSettings.restartSystem();
    }
}