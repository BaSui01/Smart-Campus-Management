/**
 * 用户管理模块
 * 提供用户的增删改查功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */

class UserManagement {
    constructor() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.totalPages = 0;
        this.searchParams = {};
        this.selectedUsers = new Set();
        this.init();
    }

    /**
     * 初始化用户管理
     */
    async init() {
        try {
            await this.loadUsers();
            this.bindEvents();
            this.initializeComponents();
            console.log('用户管理模块初始化完成');
        } catch (error) {
            console.error('用户管理模块初始化失败:', error);
            showAlert('用户管理模块初始化失败: ' + error.message, 'error');
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
        const closeModalBtns = document.querySelectorAll('[data-dismiss="modal"]');
        closeModalBtns.forEach(btn => {
            btn.addEventListener('click', () => this.closeModal());
        });
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
            showLoading('正在加载用户数据...');

            const params = {
                page: this.currentPage - 1,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/users', params);
            
            if (response.success) {
                this.renderUserTable(response.data.content || response.data);
                this.updatePagination(response.data);
                this.updateUserStats(response.data);
            } else {
                throw new Error(response.message || '加载用户数据失败');
            }
        } catch (error) {
            console.error('加载用户数据失败:', error);
            showAlert('加载用户数据失败: ' + error.message, 'error');
            this.renderUserTable([]);
        } finally {
            hideLoading();
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
        this.updateSelectAllState();
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
        if (pageData.totalPages !== undefined) {
            this.totalPages = pageData.totalPages;
            this.currentPage = pageData.number + 1;
        }

        const pagination = document.getElementById('userPagination');
        if (!pagination || this.totalPages <= 1) {
            if (pagination) pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';
        
        // 上一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="window.userManagement.goToPage(${this.currentPage - 1})">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(this.totalPages, this.currentPage + 2);

        if (startPage > 1) {
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="window.userManagement.goToPage(1)">1</a></li>`;
            if (startPage > 2) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="window.userManagement.goToPage(${i})">${i}</a>
                </li>
            `;
        }

        if (endPage < this.totalPages) {
            if (endPage < this.totalPages - 1) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="window.userManagement.goToPage(${this.totalPages})">${this.totalPages}</a></li>`;
        }

        // 下一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === this.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="window.userManagement.goToPage(${this.currentPage + 1})">
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
        const totalElement = document.getElementById('totalUsers');
        const activeElement = document.getElementById('activeUsers');
        
        if (totalElement && data.totalElements !== undefined) {
            totalElement.textContent = data.totalElements.toLocaleString();
        }
        
        if (activeElement && data.activeCount !== undefined) {
            activeElement.textContent = data.activeCount.toLocaleString();
        }
    }

    /**
     * 处理搜索
     */
    async handleSearch() {
        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');
        const roleFilter = document.getElementById('roleFilter');

        this.searchParams = {};

        if (searchInput && searchInput.value.trim()) {
            this.searchParams.keyword = searchInput.value.trim();
        }

        if (statusFilter && statusFilter.value) {
            this.searchParams.status = statusFilter.value;
        }

        if (roleFilter && roleFilter.value) {
            this.searchParams.roleId = roleFilter.value;
        }

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
            showLoading('正在加载用户信息...');

            const response = await apiClient.get(`/api/users/${userId}`);

            if (response.success) {
                this.fillUserForm(response.data);
                document.getElementById('userModalTitle').textContent = '编辑用户';
                document.getElementById('userId').value = userId;
                this.showModal('userModal');
            } else {
                throw new Error(response.message || '获取用户信息失败');
            }
        } catch (error) {
            console.error('获取用户信息失败:', error);
            showAlert('获取用户信息失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 填充用户表单
     */
    fillUserForm(user) {
        document.getElementById('username').value = user.username || '';
        document.getElementById('realName').value = user.realName || '';
        document.getElementById('email').value = user.email || '';
        document.getElementById('phone').value = user.phone || '';
        document.getElementById('gender').value = user.gender || '';
        document.getElementById('idCard').value = user.idCard || '';
        document.getElementById('address').value = user.address || '';
        document.getElementById('userStatus').value = user.status || 1;
        document.getElementById('remarks').value = user.remarks || '';

        // 设置角色
        if (user.roles && user.roles.length > 0) {
            const roleSelect = document.getElementById('userRoles');
            if (roleSelect) {
                Array.from(roleSelect.options).forEach(option => {
                    option.selected = user.roles.some(role => role.id == option.value);
                });
            }
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

            showLoading('正在保存用户...');

            const userId = document.getElementById('userId').value;
            let response;

            if (userId) {
                // 编辑用户
                response = await apiClient.put(`/api/users/${userId}`, formData);
            } else {
                // 添加用户
                response = await apiClient.post('/api/users', formData);
            }

            if (response.success) {
                showAlert(userId ? '用户更新成功' : '用户添加成功', 'success');
                this.closeModal();
                await this.loadUsers();
            } else {
                throw new Error(response.message || '保存用户失败');
            }
        } catch (error) {
            console.error('保存用户失败:', error);
            showAlert('保存用户失败: ' + error.message, 'error');
        } finally {
            hideLoading();
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
        if (!formData.username) {
            showAlert('请输入用户名', 'warning');
            return false;
        }

        if (!formData.realName) {
            showAlert('请输入真实姓名', 'warning');
            return false;
        }

        if (formData.email && !this.isValidEmail(formData.email)) {
            showAlert('请输入有效的邮箱地址', 'warning');
            return false;
        }

        if (formData.phone && !this.isValidPhone(formData.phone)) {
            showAlert('请输入有效的手机号码', 'warning');
            return false;
        }

        return true;
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
     * 删除用户
     */
    async deleteUser(userId) {
        if (!confirm('确定要删除这个用户吗？此操作不可恢复！')) {
            return;
        }

        try {
            showLoading('正在删除用户...');

            const response = await apiClient.delete(`/api/users/${userId}`);

            if (response.success) {
                showAlert('用户删除成功', 'success');
                await this.loadUsers();
            } else {
                throw new Error(response.message || '删除用户失败');
            }
        } catch (error) {
            console.error('删除用户失败:', error);
            showAlert('删除用户失败: ' + error.message, 'error');
        } finally {
            hideLoading();
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
            showLoading('正在重置密码...');

            const response = await apiClient.post(`/api/users/${userId}/reset-password`);

            if (response.success) {
                showAlert('密码重置成功，新密码已发送到用户邮箱', 'success');
            } else {
                throw new Error(response.message || '重置密码失败');
            }
        } catch (error) {
            console.error('重置密码失败:', error);
            showAlert('重置密码失败: ' + error.message, 'error');
        } finally {
            hideLoading();
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
            batchDeleteBtn.textContent = selectedCount > 0 ? `批量删除 (${selectedCount})` : '批量删除';
        }
    }

    /**
     * 批量删除用户
     */
    async handleBatchDelete() {
        if (this.selectedUsers.size === 0) {
            showAlert('请选择要删除的用户', 'warning');
            return;
        }

        if (!confirm(`确定要删除选中的 ${this.selectedUsers.size} 个用户吗？此操作不可恢复！`)) {
            return;
        }

        try {
            showLoading('正在批量删除用户...');

            const userIds = Array.from(this.selectedUsers);
            const response = await apiClient.post('/api/users/batch-delete', { userIds });

            if (response.success) {
                showAlert(`成功删除 ${userIds.length} 个用户`, 'success');
                this.selectedUsers.clear();
                await this.loadUsers();
            } else {
                throw new Error(response.message || '批量删除失败');
            }
        } catch (error) {
            console.error('批量删除失败:', error);
            showAlert('批量删除失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 导出用户数据
     */
    async handleExport() {
        try {
            showLoading('正在导出用户数据...');

            const params = { ...this.searchParams, export: true };
            const response = await apiClient.get('/api/users/export', params);

            if (response.success) {
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

                showAlert('用户数据导出成功', 'success');
            } else {
                throw new Error(response.message || '导出失败');
            }
        } catch (error) {
            console.error('导出失败:', error);
            showAlert('导出失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 显示用户详情
     */
    async showUserDetail(userId) {
        try {
            showLoading('正在加载用户详情...');

            const response = await apiClient.get(`/api/users/${userId}`);

            if (response.success) {
                this.renderUserDetail(response.data);
                this.showModal('userDetailModal');
            } else {
                throw new Error(response.message || '获取用户详情失败');
            }
        } catch (error) {
            console.error('获取用户详情失败:', error);
            showAlert('获取用户详情失败: ' + error.message, 'error');
        } finally {
            hideLoading();
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
            const response = await apiClient.get('/api/roles');

            if (response.success) {
                this.renderRoleOptions(response.data);
            }
        } catch (error) {
            console.error('加载角色列表失败:', error);
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
        const modal = document.getElementById(modalId);
        if (modal) {
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
        }
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
}

// 全局实例
let userManagement;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 确保在用户管理页面才初始化
    if (document.getElementById('userTableBody') || document.getElementById('userTable')) {
        userManagement = new UserManagement();
        // 将实例暴露到全局作用域
        window.userManagement = userManagement;
        console.log('用户管理模块已初始化');
    }
});
