/**
 * 用户管理模块
 * 提供用户的增删改查功能
 *
 * @author Campus Management Team
 * @version 2.3.0 - 修复重复声明和500错误问题
 * @since 2025-06-06
 */

// 防止重复声明
if (typeof window.UserManagement !== 'undefined') {
    console.warn('⚠️ UserManagement 已经定义，跳过重复声明');
} else {

class UserManagement {
    constructor() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.totalPages = 0;
        this.searchParams = {};
        this.selectedUsers = new Set();
        this.loadingCount = 0; // 加载状态计数器

        // 启动定期清理机制，防止加载状态卡住
        this.startLoadingCleanup();
    }

    /**
     * 启动加载状态清理机制
     */
    startLoadingCleanup() {
        // 每5秒检查一次是否有卡住的加载状态
        setInterval(() => {
            const loadingElement = document.getElementById('globalLoading');
            if (loadingElement) {
                console.warn('⚠️ 检测到可能卡住的加载状态，准备清理');
                // 如果加载状态存在超过10秒，强制清理
                if (!loadingElement.dataset.timestamp) {
                    loadingElement.dataset.timestamp = Date.now();
                } else {
                    const elapsed = Date.now() - parseInt(loadingElement.dataset.timestamp);
                    if (elapsed > 10000) { // 10秒
                        console.warn('🧹 强制清理卡住的加载状态');
                        this.forceHideLoading();
                    }
                }
            }
        }, 5000);
    }

    /**
     * 检查API响应是否成功
     * 支持两种响应格式：{success: true} 和 {code: 200}
     */
    isResponseSuccess(response) {
        return response.success === true || response.code === 200;
    }

    /**
     * 初始化用户管理
     */
    async init() {
        try {
            await this.loadUsers();
            await this.loadDisabledUsers();
            this.bindEvents();
            this.initializeComponents();
            console.log('用户管理模块初始化完成');
        } catch (error) {
            console.error('用户管理模块初始化失败:', error);
            this.showAlert('用户管理模块初始化失败: ' + error.message, 'error');
        }
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 搜索功能
        const searchBtn = document.getElementById('searchBtn');
        const resetBtn = document.getElementById('resetBtn');
        const searchInput = document.getElementById('searchInput');

        if (searchBtn) {
            searchBtn.addEventListener('click', () => this.handleSearch());
        }

        if (resetBtn) {
            resetBtn.addEventListener('click', () => this.handleReset());
        }

        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.handleSearch();
                }
            });
        }

        // 添加用户
        const addUserBtn = document.getElementById('addUserBtn');
        if (addUserBtn) {
            addUserBtn.addEventListener('click', () => this.showAddUserModal());
        }

        // 批量删除
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        if (batchDeleteBtn) {
            batchDeleteBtn.addEventListener('click', () => this.handleBatchDelete());
        }

        // 导出功能
        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.handleExport());
        }

        // 全选功能
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', (e) => this.handleSelectAll(e.target.checked));
        }

        // 模态框事件
        this.bindModalEvents();
        
        // 表单验证事件
        this.bindFormValidationEvents();
    }

    /**
     * 绑定模态框事件
     */
    bindModalEvents() {
        // 保存用户
        const saveUserBtn = document.getElementById('saveUserBtn');
        if (saveUserBtn) {
            saveUserBtn.addEventListener('click', () => this.handleSaveUser());
        }

        // 关闭模态框
        const closeModalBtns = document.querySelectorAll('[data-bs-dismiss="modal"]');
        closeModalBtns.forEach(btn => {
            btn.addEventListener('click', () => this.closeModal());
        });
    }

    /**
     * 绑定表单验证事件
     */
    bindFormValidationEvents() {
        // 用户名验证
        const usernameField = document.getElementById('username');
        if (usernameField) {
            usernameField.addEventListener('blur', () => this.validateField('username'));
            usernameField.addEventListener('input', () => this.clearFieldError('username'));
        }

        // 真实姓名验证
        const realNameField = document.getElementById('realName');
        if (realNameField) {
            realNameField.addEventListener('blur', () => this.validateField('realName'));
            realNameField.addEventListener('input', () => this.clearFieldError('realName'));
        }

        // 邮箱验证
        const emailField = document.getElementById('email');
        if (emailField) {
            emailField.addEventListener('blur', () => this.validateField('email'));
            emailField.addEventListener('input', () => this.clearFieldError('email'));
        }

        // 手机号验证
        const phoneField = document.getElementById('phone');
        if (phoneField) {
            phoneField.addEventListener('blur', () => this.validateField('phone'));
            phoneField.addEventListener('input', () => this.clearFieldError('phone'));
        }

        // 身份证号验证
        const idCardField = document.getElementById('idCard');
        if (idCardField) {
            idCardField.addEventListener('blur', () => this.validateField('idCard'));
            idCardField.addEventListener('input', () => this.clearFieldError('idCard'));
        }

        // 角色选择验证
        const rolesField = document.getElementById('userRoles');
        if (rolesField) {
            rolesField.addEventListener('change', () => this.validateField('userRoles'));
        }
    }

    /**
     * 验证单个字段
     */
    validateField(fieldId) {
        const field = document.getElementById(fieldId);
        if (!field) return true;

        const value = field.value.trim();
        let isValid = true;
        let message = '';

        switch (fieldId) {
            case 'username':
                if (!value || value.length < 3) {
                    isValid = false;
                    message = '用户名不能为空且至少3个字符';
                } else if (!/^[a-zA-Z0-9_]{3,20}$/.test(value)) {
                    isValid = false;
                    message = '用户名只能包含字母、数字和下划线，长度3-20位';
                }
                break;

            case 'realName':
                if (!value || value.length < 2) {
                    isValid = false;
                    message = '真实姓名不能为空且至少2个字符';
                }
                break;

            case 'email':
                if (value && !this.isValidEmail(value)) {
                    isValid = false;
                    message = '请输入有效的邮箱地址';
                }
                break;

            case 'phone':
                if (value && !this.isValidPhone(value)) {
                    isValid = false;
                    message = '请输入有效的手机号码';
                }
                break;

            case 'idCard':
                if (value && !this.isValidIdCard(value)) {
                    isValid = false;
                    message = '请输入有效的身份证号码';
                }
                break;

            case 'userRoles':
                const selectedOptions = Array.from(field.selectedOptions);
                if (selectedOptions.length === 0) {
                    isValid = false;
                    message = '请至少选择一个角色';
                }
                break;
        }

        if (isValid) {
            this.setFieldSuccess(fieldId);
        } else {
            this.setFieldError(fieldId, message);
        }

        return isValid;
    }

    /**
     * 清除字段错误
     */
    clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.remove('is-invalid', 'is-valid');
        }
    }

    /**
     * 设置字段成功状态
     */
    setFieldSuccess(fieldId) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
    }

    /**
     * 初始化组件
     */
    initializeComponents() {
        // 初始化日期选择器
        this.initializeDatePickers();
        
        // 初始化下拉选择
        this.initializeSelects();
        
        // 更新批量操作按钮状态
        this.updateBatchButtons();
    }

    /**
     * 初始化日期选择器
     */
    initializeDatePickers() {
        const dateInputs = document.querySelectorAll('input[type="date"]');
        dateInputs.forEach(input => {
            if (!input.value) {
                input.value = new Date().toISOString().split('T')[0];
            }
        });
    }

    /**
     * 初始化下拉选择
     */
    async initializeSelects() {
        try {
            // 加载角色列表
            await this.loadRoles();
        } catch (error) {
            console.error('初始化下拉选择失败:', error);
        }
    }

    /**
     * 加载用户列表
     */
    async loadUsers() {
        try {
            // this.showLoading('正在加载用户数据...');

            const params = {
                page: this.currentPage,
                size: this.pageSize,
                ...this.searchParams
            };

            console.log('📡 发送API请求参数:', params);
            const response = await apiClient.get('/api/v1/users', params);
            console.log('📥 API响应:', response);

            // 检查响应格式：支持 {success: true} 和 {code: 200} 两种格式
            const isSuccess = this.isResponseSuccess(response);

            if (isSuccess) {
                // 处理分页数据
                const pageData = response.data;
                console.log('📊 分页数据:', pageData);

                const users = pageData.records || pageData.content || pageData;
                console.log('👥 用户数据:', users);

                try {
                    this.renderUserTable(Array.isArray(users) ? users : []);
                    console.log('✅ 用户表格渲染完成');

                    this.updatePagination(pageData);
                    console.log('✅ 分页更新完成');

                    this.updateUserStats(pageData);
                    console.log('✅ 统计更新完成');
                } catch (renderError) {
                    console.error('❌ 渲染数据时出错:', renderError);
                    throw new Error('渲染用户数据失败: ' + renderError.message);
                }
            } else {
                console.error('❌ API响应失败:', response);
                throw new Error(response.message || '加载用户数据失败');
            }
        } catch (error) {
            console.error('加载用户数据失败:', error);
            this.showAlert('加载用户数据失败: ' + error.message, 'error');
            this.renderUserTable([]);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染用户表格
     */
    renderUserTable(users) {
        const tbody = document.getElementById('userTableBody');
        if (!tbody) return;

        if (!users || users.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i>
                        <div>暂无用户数据</div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = users.map(user => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input user-checkbox" type="checkbox"
                               value="${user.id}" onchange="userManagement.handleUserSelect(${user.id}, this.checked)">
                    </div>
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <img src="${user.avatarUrl || '/images/default-avatar.svg'}"
                             alt="头像" class="rounded-circle me-2" width="32" height="32">
                        <div>
                            <div class="fw-bold">${user.realName || user.username}</div>
                            <small class="text-muted">${user.username}</small>
                        </div>
                    </div>
                </td>
                <td>${user.email || '-'}</td>
                <td>${user.phone || '-'}</td>
                <td>
                    <span class="badge ${this.getStatusBadgeClass(user.status)}">
                        ${this.getStatusText(user.status)}
                    </span>
                </td>
                <td>
                    ${user.roles ? user.roles.map(role => 
                        `<span class="badge bg-info me-1">${role.roleName}</span>`
                    ).join('') : '-'}
                </td>
                <td>
                    <small class="text-muted">
                        ${user.lastLoginTime ? new Date(user.lastLoginTime).toLocaleString() : '从未登录'}
                    </small>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="viewUser(${user.id})"
                                title="详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-outline-warning" onclick="editUser(${user.id})"
                                title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn ${user.status === 1 ? 'btn-outline-secondary' : 'btn-outline-success'}"
                                onclick="toggleUserStatus(${user.id})"
                                title="${user.status === 1 ? '禁用用户' : '启用用户'}"
                                ${user.username === 'admin' ? 'disabled' : ''}>
                            <i class="fas ${user.status === 1 ? 'fa-user-slash' : 'fa-user-check'}"></i>
                        </button>
                        <button class="btn btn-outline-info" onclick="resetPassword(${user.id})"
                                title="重置密码">
                            <i class="fas fa-key"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="deleteUser(${user.id})"
                                title="删除" ${user.username === 'admin' ? 'disabled' : ''}>
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');

        // 更新选中状态
        try {
            this.updateSelectAllState();
        } catch (selectError) {
            console.warn('更新选中状态失败:', selectError);
        }
    }

    /**
     * 获取状态徽章样式
     */
    getStatusBadgeClass(status) {
        switch (status) {
            case 1: return 'bg-success';
            case 0: return 'bg-danger';
            default: return 'bg-secondary';
        }
    }

    /**
     * 获取状态文本
     */
    getStatusText(status) {
        switch (status) {
            case 1: return '正常';
            case 0: return '禁用';
            default: return '未知';
        }
    }

    /**
     * 更新分页
     */
    updatePagination(pageData) {
        console.log('📄 更新分页信息:', pageData);

        if (pageData && pageData.pages !== undefined) {
            this.totalPages = pageData.pages;
            this.currentPage = pageData.current || 1;
        }

        console.log('📄 分页状态:', { totalPages: this.totalPages, currentPage: this.currentPage });

        // 更新页面信息
        const currentPageInfo = document.getElementById('currentPageInfo');
        const totalPagesInfo = document.getElementById('totalPagesInfo');
        
        if (currentPageInfo) currentPageInfo.textContent = this.currentPage;
        if (totalPagesInfo) totalPagesInfo.textContent = this.totalPages;

        const pagination = document.getElementById('userPagination');
        if (!pagination || this.totalPages <= 1) {
            if (pagination) pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';
        
        // 上一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="event.preventDefault(); window.userManagement.goToPage(${this.currentPage - 1})">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(this.totalPages, this.currentPage + 2);

        if (startPage > 1) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="event.preventDefault(); window.userManagement.goToPage(1)">1</a></li>`;
            if (startPage > 2) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="event.preventDefault(); window.userManagement.goToPage(${i})">${i}</a>
                </li>
            `;
        }

        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="event.preventDefault(); window.userManagement.goToPage(${this.totalPages})">${this.totalPages}</a></li>`;
        }

        // 下一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === this.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="event.preventDefault(); window.userManagement.goToPage(${this.currentPage + 1})">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `;

        pagination.innerHTML = paginationHtml;
    }

    /**
     * 跳转到指定页面
     */
    async goToPage(page) {
        if (page < 1 || page > this.totalPages || page === this.currentPage) {
            return;
        }
        this.currentPage = page;
        await this.loadUsers();
    }

    /**
     * 更新用户统计
     */
    updateUserStats(data) {
        console.log('📊 更新用户统计:', data);

        const totalElement = document.getElementById('totalUsers');
        const activeElement = document.getElementById('activeUsers');

        if (totalElement && data.total !== undefined) {
            totalElement.textContent = data.total.toLocaleString();
        }

        // 暂时使用总数作为活跃用户数，后续可以从API获取具体统计
        if (activeElement && data.total !== undefined) {
            activeElement.textContent = data.total.toLocaleString();
        }
    }

    /**
     * 处理搜索
     */
    async handleSearch() {
        console.log('🔍 开始搜索');

        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');
        const roleFilter = document.getElementById('roleFilter');

        this.searchParams = {};

        if (searchInput && searchInput.value.trim()) {
            this.searchParams.search = searchInput.value.trim(); // 修改为search
            console.log('🔍 搜索关键词:', this.searchParams.search);
        }

        if (statusFilter && statusFilter.value) {
            this.searchParams.status = statusFilter.value;
            console.log('🔍 状态筛选:', this.searchParams.status);
        }

        if (roleFilter && roleFilter.value) {
            this.searchParams.role = roleFilter.value; // 修改为role
            console.log('🔍 角色筛选:', this.searchParams.role);
        }

        console.log('🔍 搜索参数:', this.searchParams);

        this.currentPage = 1;
        await this.loadUsers();
    }

    /**
     * 重置搜索
     */
    async handleReset() {
        // 清空搜索条件
        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');
        const roleFilter = document.getElementById('roleFilter');

        if (searchInput) searchInput.value = '';
        if (statusFilter) statusFilter.value = '';
        if (roleFilter) roleFilter.value = '';

        this.searchParams = {};
        this.currentPage = 1;
        await this.loadUsers();
    }

    /**
     * 显示添加用户模态框
     */
    showAddUserModal() {
        this.clearUserForm();
        document.getElementById('userModalTitle').textContent = '添加用户';
        document.getElementById('userId').value = '';
        this.showModal('userModal');
    }

    /**
     * 显示编辑用户模态框
     */
    async showEditUserModal(userId) {
        try {
            console.log('🔄 开始显示编辑用户模态框，用户ID:', userId);
            this.showLoading('正在加载用户信息...');

            console.log('📡 获取用户信息...');
            const response = await apiClient.get(`/api/v1/users/${userId}`);
            console.log('📥 用户信息响应:', response);

            if (this.isResponseSuccess(response)) {
                console.log('✅ 用户信息获取成功，开始显示模态框');

                // 先显示模态框
                this.showModal('userModal');

                // 监听模态框显示完成事件
                const modal = document.getElementById('userModal');
                if (modal) {
                    const handleModalShown = () => {
                        console.log('✅ 模态框已完全显示，开始填充表单');

                        // 设置模态框标题
                        const titleElement = document.getElementById('userModalTitle');
                        if (titleElement) {
                            titleElement.textContent = '编辑用户';
                        }

                        // 填充表单
                        this.fillUserForm(response.data);
                        this.setFieldValue('userId', userId);

                        console.log('✅ 编辑用户模态框显示完成');

                        // 移除事件监听器
                        modal.removeEventListener('shown.bs.modal', handleModalShown);
                    };

                    modal.addEventListener('shown.bs.modal', handleModalShown);
                } else {
                    console.error('❌ 找不到用户模态框');
                }
            } else {
                console.log('❌ 获取用户信息失败:', response);
                throw new Error(response.message || '获取用户信息失败');
            }
        } catch (error) {
            console.error('❌ 显示编辑用户模态框失败:', error);
            this.showAlert('获取用户信息失败: ' + error.message, 'error');
        } finally {
            console.log('🔚 编辑用户模态框操作完成，隐藏加载状态');
            this.hideLoading();
        }
    }

    /**
     * 填充用户表单
     */
    fillUserForm(user) {
        console.log('🔄 开始填充用户表单，用户数据:', user);

        // 安全设置表单字段值
        this.setFieldValue('username', user.username || '');
        this.setFieldValue('realName', user.realName || '');
        this.setFieldValue('email', user.email || '');
        this.setFieldValue('phone', user.phone || '');
        this.setFieldValue('gender', user.gender || '');
        this.setFieldValue('idCard', user.idCard || '');
        this.setFieldValue('address', user.address || '');
        this.setFieldValue('userStatus', user.status || 1);
        this.setFieldValue('remarks', user.remarks || '');

        // 设置角色
        if (user.roles && user.roles.length > 0) {
            const roleSelect = document.getElementById('userRoles');
            if (roleSelect) {
                Array.from(roleSelect.options).forEach(option => {
                    option.selected = user.roles.some(role => role.id == option.value);
                });
                console.log('✅ 角色设置完成');
            } else {
                console.warn('⚠️ 角色选择框不存在');
            }
        }

        console.log('✅ 用户表单填充完成');
    }

    /**
     * 安全设置字段值
     */
    setFieldValue(fieldId, value) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.value = value;
            console.log(`✅ 设置字段 ${fieldId} = ${value}`);
        } else {
            console.error(`❌ 字段不存在: ${fieldId}`);
        }
    }

    /**
     * 清空用户表单
     */
    clearUserForm() {
        const form = document.getElementById('userForm');
        if (form) {
            form.reset();
            document.getElementById('userStatus').value = '1';
        }
    }

    /**
     * 处理保存用户
     */
    async handleSaveUser() {
        try {
            const formData = this.getUserFormData();

            // 验证表单
            if (!this.validateUserForm(formData)) {
                return;
            }

            this.showLoading('正在保存用户...');

            const userId = document.getElementById('userId').value;
            let response;

            if (userId) {
                // 编辑用户
                response = await apiClient.put(`/api/v1/users/${userId}`, formData);
            } else {
                // 添加用户
                response = await apiClient.post('/api/v1/users', formData);
            }

            if (this.isResponseSuccess(response)) {
                this.showAlert(userId ? '用户更新成功' : '用户添加成功', 'success');
                this.closeModal();
                await this.loadUsers(); // loadUsers会处理自己的加载状态
            } else {
                throw new Error(response.message || '保存用户失败');
            }
        } catch (error) {
            console.error('保存用户失败:', error);
            this.showAlert('保存用户失败: ' + error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 获取用户表单数据
     */
    getUserFormData() {
        const roleSelect = document.getElementById('userRoles');
        const selectedRoles = Array.from(roleSelect.selectedOptions).map(option => ({
            id: parseInt(option.value)
        }));

        return {
            username: document.getElementById('username').value.trim(),
            realName: document.getElementById('realName').value.trim(),
            email: document.getElementById('email').value.trim(),
            phone: document.getElementById('phone').value.trim(),
            gender: document.getElementById('gender').value,
            idCard: document.getElementById('idCard').value.trim(),
            address: document.getElementById('address').value.trim(),
            status: parseInt(document.getElementById('userStatus').value),
            remarks: document.getElementById('remarks').value.trim(),
            roles: selectedRoles
        };
    }

    /**
     * 验证用户表单
     */
    validateUserForm(formData) {
        const form = document.getElementById('userForm');
        if (form) {
            form.classList.add('was-validated');
        }

        // 清除之前的错误状态
        this.clearFormErrors();

        let isValid = true;

        // 验证用户名
        if (!formData.username || formData.username.length < 3) {
            this.setFieldError('username', '用户名不能为空且至少3个字符');
            isValid = false;
        } else if (!/^[a-zA-Z0-9_]{3,20}$/.test(formData.username)) {
            this.setFieldError('username', '用户名只能包含字母、数字和下划线，长度3-20位');
            isValid = false;
        }

        // 验证真实姓名
        if (!formData.realName || formData.realName.length < 2) {
            this.setFieldError('realName', '真实姓名不能为空且至少2个字符');
            isValid = false;
        }

        // 验证邮箱
        if (formData.email && !this.isValidEmail(formData.email)) {
            this.setFieldError('email', '请输入有效的邮箱地址');
            isValid = false;
        }

        // 验证手机号
        if (formData.phone && !this.isValidPhone(formData.phone)) {
            this.setFieldError('phone', '请输入有效的手机号码');
            isValid = false;
        }

        // 验证身份证号
        if (formData.idCard && !this.isValidIdCard(formData.idCard)) {
            this.setFieldError('idCard', '请输入有效的身份证号码');
            isValid = false;
        }

        // 验证角色
        if (!formData.roles || formData.roles.length === 0) {
            this.setFieldError('userRoles', '请至少选择一个角色');
            isValid = false;
        }

        if (!isValid) {
            this.showAlert('请检查表单输入', 'warning');
        }

        return isValid;
    }

    /**
     * 设置字段错误
     */
    setFieldError(fieldId, message) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.add('is-invalid');
            
            let feedback = field.parentNode.querySelector('.invalid-feedback');
            if (feedback) {
                feedback.textContent = message;
            }
        }
    }

    /**
     * 清除表单错误
     */
    clearFormErrors() {
        const form = document.getElementById('userForm');
        if (form) {
            const errorFields = form.querySelectorAll('.is-invalid');
            errorFields.forEach(field => {
                field.classList.remove('is-invalid');
            });
            
            const successFields = form.querySelectorAll('.is-valid');
            successFields.forEach(field => {
                field.classList.remove('is-valid');
            });
        }
    }

    /**
     * 验证邮箱格式
     */
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * 验证手机号格式
     */
    isValidPhone(phone) {
        const phoneRegex = /^1[3-9]\d{9}$/;
        return phoneRegex.test(phone);
    }

    /**
     * 验证身份证号格式
     */
    isValidIdCard(idCard) {
        const idCardRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
        return idCardRegex.test(idCard);
    }

    /**
     * 删除用户
     */
    async deleteUser(userId) {
        if (!confirm('确定要删除这个用户吗？此操作不可恢复！')) {
            return;
        }

        try {
            const response = await apiClient.delete(`/api/v1/users/${userId}`);

            if (this.isResponseSuccess(response)) {
                this.showAlert('用户删除成功', 'success');
                await this.loadUsers(); // loadUsers会处理自己的加载状态
            } else {
                throw new Error(response.message || '删除用户失败');
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            this.showAlert('删除用户失败: ' + error.message, 'error');
        }
    }

    /**
     * 重置密码
     */
    async resetPassword(userId) {
        if (!confirm('确定要重置该用户的密码吗？新密码将发送到用户邮箱。')) {
            return;
        }

        try {
            console.log('🔄 开始重置密码，用户ID:', userId);
            this.showLoading('正在重置密码...');

            console.log('📡 发送重置密码请求...');
            const response = await apiClient.post(`/api/v1/users/${userId}/reset-password`, null);
            console.log('📥 重置密码响应:', response);

            if (this.isResponseSuccess(response)) {
                console.log('✅ 密码重置成功');
                this.showAlert('密码重置成功，新密码已发送到用户邮箱', 'success');
            } else {
                console.log('❌ 密码重置失败:', response);
                throw new Error(response.message || '重置密码失败');
            }
        } catch (error) {
            console.error('❌ 重置密码异常:', error);
            this.showAlert('重置密码失败: ' + error.message, 'error');
        } finally {
            console.log('🔚 重置密码操作完成，隐藏加载状态');
            this.hideLoading();
        }
    }

    /**
     * 处理用户选择
     */
    handleUserSelect(userId, checked) {
        if (checked) {
            this.selectedUsers.add(userId);
        } else {
            this.selectedUsers.delete(userId);
        }
        this.updateBatchButtons();
        this.updateSelectAllState();
    }

    /**
     * 处理全选
     */
    handleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.user-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
            const userId = parseInt(checkbox.value);
            if (checked) {
                this.selectedUsers.add(userId);
            } else {
                this.selectedUsers.delete(userId);
            }
        });
        this.updateBatchButtons();
    }

    /**
     * 更新全选状态
     */
    updateSelectAllState() {
        const selectAllCheckbox = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.user-checkbox');

        if (selectAllCheckbox && checkboxes.length > 0) {
            const checkedCount = document.querySelectorAll('.user-checkbox:checked').length;
            selectAllCheckbox.checked = checkedCount === checkboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
        }
    }

    /**
     * 更新批量操作按钮状态
     */
    updateBatchButtons() {
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        const selectedCount = this.selectedUsers.size;

        if (batchDeleteBtn) {
            batchDeleteBtn.disabled = selectedCount === 0;
            
            // 更新按钮文本和图标
            if (selectedCount > 0) {
                batchDeleteBtn.innerHTML = `<i class="fas fa-trash me-1"></i>批量删除 (${selectedCount})`;
                batchDeleteBtn.classList.remove('btn-outline-danger');
                batchDeleteBtn.classList.add('btn-danger');
            } else {
                batchDeleteBtn.innerHTML = `<i class="fas fa-trash me-1"></i>批量删除`;
                batchDeleteBtn.classList.remove('btn-danger');
                batchDeleteBtn.classList.add('btn-outline-danger');
            }
        }
    }

    /**
     * 批量删除用户
     */
    async handleBatchDelete() {
        if (this.selectedUsers.size === 0) {
            this.showAlert('请选择要删除的用户', 'warning');
            return;
        }

        if (!confirm(`确定要删除选中的 ${this.selectedUsers.size} 个用户吗？此操作不可恢复！`)) {
            return;
        }

        try {
            const userIds = Array.from(this.selectedUsers);
            const response = await apiClient.delete('/api/v1/users/batch', userIds);

            if (this.isResponseSuccess(response)) {
                this.showAlert(`成功删除 ${userIds.length} 个用户`, 'success');
                this.selectedUsers.clear();
                await this.loadUsers(); // loadUsers会处理自己的加载状态
            } else {
                throw new Error(response.message || '批量删除失败');
            }
        } catch (error) {
            console.error('批量删除失败:', error);
            this.showAlert('批量删除失败: ' + error.message, 'error');
        }
    }

    /**
     * 导出用户数据
     */
    async handleExport() {
        try {
            this.showLoading('正在导出用户数据...');

            const params = { ...this.searchParams, export: true };
            const response = await apiClient.get('/api/v1/users/export', params);

            if (this.isResponseSuccess(response)) {
                // 创建下载链接
                const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `用户数据_${new Date().toISOString().split('T')[0]}.xlsx`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);

                this.showAlert('用户数据导出成功', 'success');
            } else {
                throw new Error(response.message || '导出失败');
            }
        } catch (error) {
            console.error('导出失败:', error);
            this.showAlert('导出失败: ' + error.message, 'error');
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 显示用户详情
     */
    async showUserDetail(userId) {
        try {
            console.log('🔄 开始显示用户详情，用户ID:', userId);
            this.showLoading('正在加载用户详情...');

            console.log('📡 获取用户详情...');
            const response = await apiClient.get(`/api/v1/users/${userId}`);
            console.log('📥 用户详情响应:', response);

            if (this.isResponseSuccess(response)) {
                console.log('✅ 用户详情获取成功');
                this.renderUserDetail(response.data);
                this.showModal('userDetailModal');
                console.log('✅ 用户详情模态框显示完成');
            } else {
                console.log('❌ 获取用户详情失败:', response);
                throw new Error(response.message || '获取用户详情失败');
            }
        } catch (error) {
            console.error('❌ 显示用户详情失败:', error);
            this.showAlert('获取用户详情失败: ' + error.message, 'error');
        } finally {
            console.log('🔚 用户详情操作完成，隐藏加载状态');
            this.hideLoading();
        }
    }

    /**
     * 渲染用户详情
     */
    renderUserDetail(user) {
        const detailContainer = document.getElementById('userDetailContent');
        if (!detailContainer) return;

        detailContainer.innerHTML = `
            <div class="row">
                <div class="col-md-4 text-center">
                    <img src="${user.avatarUrl || '/images/default-avatar.svg'}"
                         alt="头像" class="rounded-circle mb-3" width="120" height="120">
                    <h5>${user.realName || user.username}</h5>
                    <p class="text-muted">${user.username}</p>
                </div>
                <div class="col-md-8">
                    <table class="table table-borderless">
                        <tr>
                            <td class="fw-bold">邮箱：</td>
                            <td>${user.email || '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">手机：</td>
                            <td>${user.phone || '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">性别：</td>
                            <td>${user.gender === 'M' ? '男' : user.gender === 'F' ? '女' : '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">身份证：</td>
                            <td>${user.idCard || '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">地址：</td>
                            <td>${user.address || '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">状态：</td>
                            <td>
                                <span class="badge ${this.getStatusBadgeClass(user.status)}">
                                    ${this.getStatusText(user.status)}
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-bold">角色：</td>
                            <td>
                                ${user.roles ? user.roles.map(role =>
                                    `<span class="badge bg-info me-1">${role.roleName}</span>`
                                ).join('') : '-'}
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-bold">最后登录：</td>
                            <td>${user.lastLoginTime ? new Date(user.lastLoginTime).toLocaleString() : '从未登录'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">登录次数：</td>
                            <td>${user.loginCount || 0} 次</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">创建时间：</td>
                            <td>${user.createdAt ? new Date(user.createdAt).toLocaleString() : '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">备注：</td>
                            <td>${user.remarks || '-'}</td>
                        </tr>
                    </table>
                </div>
            </div>
        `;
    }

    /**
     * 加载角色列表
     */
    async loadRoles() {
        try {
            console.log('🔄 加载角色列表 - 使用硬编码数据');
            
            // 使用硬编码的角色数据（与后端AdminSystemController保持一致）
            const roles = [
                { id: 1, roleName: 'ADMIN', roleDescription: '系统管理员', status: 1 },
                { id: 2, roleName: 'TEACHER', roleDescription: '教师', status: 1 },
                { id: 3, roleName: 'STUDENT', roleDescription: '学生', status: 1 },
                { id: 4, roleName: 'FINANCE', roleDescription: '财务人员', status: 1 },
                { id: 5, roleName: 'ACADEMIC_ADMIN', roleDescription: '教务管理员', status: 1 }
            ];

            console.log('✅ 角色数据加载成功:', roles);
            this.renderRoleOptions(roles);
        } catch (error) {
            console.error('❌ 加载角色列表失败:', error);
        }
    }

    /**
     * 渲染角色选项
     */
    renderRoleOptions(roles) {
        const roleSelect = document.getElementById('userRoles');
        const roleFilter = document.getElementById('roleFilter');

        if (roleSelect) {
            roleSelect.innerHTML = roles.map(role =>
                `<option value="${role.id}">${role.roleName}</option>`
            ).join('');
        }

        if (roleFilter) {
            roleFilter.innerHTML = `
                <option value="">全部角色</option>
                ${roles.map(role =>
                    `<option value="${role.id}">${role.roleName}</option>`
                ).join('')}
            `;
        }
    }

    /**
     * 显示模态框
     */
    showModal(modalId) {
        console.log('🔄 尝试显示模态框:', modalId);

        // 等待DOM完全加载
        setTimeout(() => {
            const modal = document.getElementById(modalId);
            if (!modal) {
                console.error('❌ 找不到模态框元素:', modalId);
                console.log('🔍 当前页面所有模态框:', document.querySelectorAll('.modal'));
                this.showAlert('模态框元素不存在: ' + modalId, 'error');
                return;
            }

            console.log('✅ 找到模态框元素:', modal);

            // 检查Bootstrap是否可用
            if (typeof bootstrap === 'undefined') {
                console.error('❌ Bootstrap未加载');
                this.showAlert('Bootstrap未加载，无法显示模态框', 'error');
                return;
            }

            try {
                // 先检查是否已有实例
                let bsModal = bootstrap.Modal.getInstance(modal);
                if (!bsModal) {
                    console.log('🔄 创建新的Bootstrap模态框实例');
                    bsModal = new bootstrap.Modal(modal);
                } else {
                    console.log('✅ 使用现有的Bootstrap模态框实例');
                }

                console.log('🔄 显示模态框...');
                bsModal.show();
                console.log('✅ 模态框显示命令已执行');
            } catch (error) {
                console.error('❌ 显示模态框失败:', error);
                this.showAlert('显示模态框失败: ' + error.message, 'error');
            }
        }, 100);
    }

    /**
     * 关闭模态框
     */
    closeModal() {
        const modals = document.querySelectorAll('.modal.show');
        modals.forEach(modal => {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) {
                bsModal.hide();
            }
        });
    }

    /**
     * 显示加载状态
     */
    showLoading(message = '加载中...') {
        console.log('🔄 显示加载状态:', message);

        // 移除现有的加载指示器
        this.hideLoading();

        // 创建新的加载指示器
        const loadingElement = document.createElement('div');
        loadingElement.id = 'globalLoading';
        loadingElement.className = 'position-fixed top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center';
        loadingElement.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        loadingElement.style.zIndex = '9999';
        loadingElement.dataset.timestamp = Date.now(); // 添加时间戳
        loadingElement.innerHTML = `
            <div class="bg-white p-4 rounded shadow">
                <div class="d-flex align-items-center">
                    <div class="spinner-border text-primary me-3" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <span>${message}</span>
                </div>
            </div>
        `;
        document.body.appendChild(loadingElement);

        console.log('✅ 加载状态已显示');
    }

    /**
     * 隐藏加载状态
     */
    hideLoading() {
        console.log('🔚 隐藏加载状态');

        const loadingElement = document.getElementById('globalLoading');
        if (loadingElement) {
            loadingElement.remove();
            console.log('✅ 加载状态已隐藏');
        } else {
            console.log('ℹ️ 没有找到加载状态元素');
        }
    }

    /**
     * 强制清理所有加载状态
     */
    forceHideLoading() {
        console.log('🧹 强制清理所有加载状态');

        // 清理所有可能的加载元素
        const loadingElements = document.querySelectorAll('#globalLoading, .loading-overlay, [id*="loading"]');
        loadingElements.forEach(element => {
            element.remove();
        });

        // 重置计数器
        this.loadingCount = 0;

        console.log('✅ 强制清理完成');
    }

    /**
     * 显示提示信息
     */
    showAlert(message, type = 'info', duration = 3000) {
        // 创建提示元素
        const alertElement = document.createElement('div');
        alertElement.className = `alert alert-${this.getAlertClass(type)} alert-dismissible fade show position-fixed`;
        alertElement.style.top = '20px';
        alertElement.style.right = '20px';
        alertElement.style.zIndex = '10000';
        alertElement.style.minWidth = '300px';
        
        alertElement.innerHTML = `
            <i class="fas ${this.getAlertIcon(type)} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        document.body.appendChild(alertElement);

        // 自动移除
        setTimeout(() => {
            if (alertElement && alertElement.parentNode) {
                alertElement.remove();
            }
        }, duration);
    }

    /**
     * 获取提示样式类
     */
    getAlertClass(type) {
        switch (type) {
            case 'success': return 'success';
            case 'error': return 'danger';
            case 'warning': return 'warning';
            case 'info': return 'info';
            default: return 'primary';
        }
    }

    /**
     * 获取提示图标
     */
    getAlertIcon(type) {
        switch (type) {
            case 'success': return 'fa-check-circle';
            case 'error': return 'fa-exclamation-circle';
            case 'warning': return 'fa-exclamation-triangle';
            case 'info': return 'fa-info-circle';
            default: return 'fa-bell';
        }
    }

    /**
     * 切换用户状态
     */
    async toggleUserStatus(userId) {
        try {
            const response = await apiClient.post(`/api/v1/users/${userId}/toggle-status`);

            if (this.isResponseSuccess(response)) {
                this.showAlert('用户状态切换成功', 'success');
                await this.loadUsers(); // loadUsers会处理自己的加载状态
                await this.loadDisabledUsers(); // 同时刷新禁用用户列表
            } else {
                throw new Error(response.message || '切换用户状态失败');
            }
        } catch (error) {
            console.error('切换用户状态失败:', error);
            this.showAlert('切换用户状态失败: ' + error.message, 'error');
        }
    }

    /**
     * 启用用户
     */
    async enableUser(userId) {
        if (!confirm('确定要启用该用户吗？')) {
            return;
        }

        try {
            const response = await apiClient.post(`/api/v1/users/${userId}/toggle-status`);

            if (this.isResponseSuccess(response)) {
                this.showAlert('用户启用成功', 'success');
                await this.loadUsers(); // 刷新主用户列表
                await this.loadDisabledUsers(); // 刷新禁用用户列表
            } else {
                throw new Error(response.message || '启用用户失败');
            }
        } catch (error) {
            console.error('启用用户失败:', error);
            this.showAlert('启用用户失败: ' + error.message, 'error');
        }
    }

    /**
     * 格式化日期
     */
    formatDate(dateString) {
        if (!dateString) return '-';
        
        try {
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (error) {
            return dateString;
        }
    }

    /**
     * 刷新用户列表
     */
    async refreshUsers() {
        await this.loadUsers();
        await this.loadDisabledUsers();
    }

    /**
     * 加载禁用用户列表
     */
    async loadDisabledUsers() {
        try {
            console.log('🔄 开始加载禁用用户列表');

            const response = await apiClient.get('/api/v1/users/disabled');
            console.log('📥 禁用用户列表响应:', response);

            if (this.isResponseSuccess(response)) {
                const disabledUsers = response.data || [];
                this.renderDisabledUsers(disabledUsers);
                console.log('✅ 禁用用户列表加载完成，共', disabledUsers.length, '个用户');
            } else {
                throw new Error(response.message || '获取禁用用户列表失败');
            }
        } catch (error) {
            console.error('❌ 加载禁用用户列表失败:', error);
            this.showAlert('加载禁用用户列表失败: ' + error.message, 'error');
        }
    }

    /**
     * 渲染禁用用户列表
     */
    renderDisabledUsers(disabledUsers) {
        const container = document.getElementById('disabledUsersList');
        const countElement = document.getElementById('disabledUserCount');

        if (!container) {
            console.warn('⚠️ 禁用用户列表容器不存在');
            return;
        }

        // 更新计数
        if (countElement) {
            countElement.textContent = disabledUsers.length;
        }

        if (disabledUsers.length === 0) {
            container.innerHTML = `
                <div class="text-center p-3 text-muted">
                    <i class="fas fa-user-slash fa-2x mb-2"></i>
                    <p class="mb-0">暂无禁用用户</p>
                </div>
            `;
            return;
        }

        const html = disabledUsers.map(user => `
            <div class="border-bottom p-3">
                <div class="d-flex align-items-center">
                    <img src="${user.avatarUrl || '/images/default-avatar.svg'}"
                         alt="头像" class="rounded-circle me-2" width="32" height="32">
                    <div class="flex-grow-1">
                        <div class="fw-bold small">${user.realName || user.username}</div>
                        <small class="text-muted">${user.username}</small>
                    </div>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-success btn-sm"
                                onclick="enableUser(${user.id})"
                                title="启用用户">
                            <i class="fas fa-user-check"></i>
                        </button>
                        <button class="btn btn-outline-danger btn-sm"
                                onclick="deleteUser(${user.id})"
                                title="删除用户">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');

        container.innerHTML = html;
    }

    /**
     * 获取选中的用户ID列表
     */
    getSelectedUserIds() {
        return Array.from(this.selectedUsers);
    }

    /**
     * 清空选中状态
     */
    clearSelection() {
        this.selectedUsers.clear();
        const checkboxes = document.querySelectorAll('.user-checkbox');
        checkboxes.forEach(checkbox => checkbox.checked = false);
        
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.checked = false;
            selectAllCheckbox.indeterminate = false;
        }
        
        this.updateBatchButtons();
    }
}

// 移除重复的初始化代码，统一在文件末尾处理

// 全局函数桥接 - 用于HTML按钮调用
window.viewUser = function(userId) {
    if (window.userManagement) {
        window.userManagement.showUserDetail(userId);
    }
};

window.editUser = function(userId) {
    if (window.userManagement) {
        window.userManagement.showEditUserModal(userId);
    }
};

window.deleteUser = function(userId) {
    if (window.userManagement) {
        window.userManagement.deleteUser(userId);
    }
};

window.resetPassword = function(userId) {
    if (window.userManagement) {
        window.userManagement.resetPassword(userId);
    }
};

window.toggleUserStatus = function(userId) {
    if (window.userManagement) {
        window.userManagement.toggleUserStatus(userId);
    }
};

window.enableUser = function(userId) {
    if (window.userManagement) {
        window.userManagement.enableUser(userId);
    }
};

// 防止重复声明和初始化
(function() {
    'use strict';

    // 检查是否在用户管理页面
    function isUserManagementPage() {
        return document.getElementById('userTableBody') ||
               document.getElementById('userTable') ||
               document.querySelector('.user-management-page') ||
               document.querySelector('[data-current-page="users"]') ||
               window.location.pathname.includes('/users');
    }

    // 初始化函数
    function initializeUserManagement() {
        // 防止重复初始化
        if (window.userManagement) {
            console.log('⚠️ 用户管理实例已存在，跳过重复初始化');
            return;
        }

        if (!isUserManagementPage()) {
            console.log('📄 非用户管理页面，跳过初始化');
            return;
        }

        try {
            console.log('🚀 初始化用户管理模块 v2.2');

            // 确保UserManagement类可用
            if (typeof UserManagement === 'undefined') {
                console.error('❌ UserManagement类未定义');
                return;
            }

            window.userManagement = new UserManagement();
            window.userManagement.init();
            console.log('✅ 用户管理模块初始化完成');
        } catch (error) {
            console.error('❌ 用户管理模块初始化失败:', error);
        }
    }

    // 延迟初始化，确保DOM完全加载
    function delayedInit() {
        setTimeout(initializeUserManagement, 100);
    }

    // 页面加载完成后初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', delayedInit);
    } else {
        // DOM已经加载完成
        delayedInit();
    }
})();

} // 结束防重复声明的条件块
