/**
 * 角色管理模块
 * 提供角色的增删改查功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */

class RoleManagement {
    constructor() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.totalPages = 0;
        this.searchParams = {};
        this.selectedRoles = new Set();
        this.permissions = [];
        this.init();
    }

    /**
     * 初始化角色管理
     */
    async init() {
        try {
            await this.loadPermissions();
            await this.loadRoles();
            this.bindEvents();
            this.initializeComponents();
            console.log('角色管理模块初始化完成');
        } catch (error) {
            console.error('角色管理模块初始化失败:', error);
            showAlert('角色管理模块初始化失败: ' + error.message, 'error');
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

        // 添加角色
        const addRoleBtn = document.getElementById('addRoleBtn');
        if (addRoleBtn) {
            addRoleBtn.addEventListener('click', () => this.showAddRoleModal());
        }

        // 批量删除
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        if (batchDeleteBtn) {
            batchDeleteBtn.addEventListener('click', () => this.handleBatchDelete());
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
        // 保存角色
        const saveRoleBtn = document.getElementById('saveRoleBtn');
        if (saveRoleBtn) {
            saveRoleBtn.addEventListener('click', () => this.handleSaveRole());
        }

        // 关闭模态框
        const closeModalBtns = document.querySelectorAll('[data-dismiss="modal"]');
        closeModalBtns.forEach(btn => {
            btn.addEventListener('click', () => this.closeModal());
        });

        // 权限全选
        const selectAllPermissions = document.getElementById('selectAllPermissions');
        if (selectAllPermissions) {
            selectAllPermissions.addEventListener('change', (e) => this.handleSelectAllPermissions(e.target.checked));
        }
    }

    /**
     * 初始化组件
     */
    initializeComponents() {
        // 更新批量操作按钮状态
        this.updateBatchButtons();
    }

    /**
     * 加载权限列表
     */
    async loadPermissions() {
        try {
            const response = await apiClient.get('/api/permissions');
            
            if (response.success) {
                this.permissions = response.data;
                this.renderPermissionTree();
            } else {
                throw new Error(response.message || '加载权限列表失败');
            }
        } catch (error) {
            console.error('加载权限列表失败:', error);
            this.permissions = [];
        }
    }

    /**
     * 渲染权限树
     */
    renderPermissionTree() {
        const container = document.getElementById('permissionTree');
        if (!container || !this.permissions.length) return;

        // 按模块分组权限
        const groupedPermissions = this.groupPermissionsByModule();
        
        container.innerHTML = Object.keys(groupedPermissions).map(module => `
            <div class="permission-module mb-3">
                <div class="form-check">
                    <input class="form-check-input module-checkbox" type="checkbox" 
                           id="module_${module}" onchange="roleManagement.handleModuleSelect('${module}', this.checked)">
                    <label class="form-check-label fw-bold" for="module_${module}">
                        ${this.getModuleName(module)}
                    </label>
                </div>
                <div class="ms-4 mt-2">
                    ${groupedPermissions[module].map(permission => `
                        <div class="form-check">
                            <input class="form-check-input permission-checkbox" type="checkbox" 
                                   value="${permission.id}" id="permission_${permission.id}"
                                   data-module="${module}">
                            <label class="form-check-label" for="permission_${permission.id}">
                                ${permission.permissionName}
                                <small class="text-muted">(${permission.permissionKey})</small>
                            </label>
                        </div>
                    `).join('')}
                </div>
            </div>
        `).join('');
    }

    /**
     * 按模块分组权限
     */
    groupPermissionsByModule() {
        const grouped = {};
        this.permissions.forEach(permission => {
            const module = permission.permissionKey.split(':')[0] || 'other';
            if (!grouped[module]) {
                grouped[module] = [];
            }
            grouped[module].push(permission);
        });
        return grouped;
    }

    /**
     * 获取模块名称
     */
    getModuleName(module) {
        const moduleNames = {
            'user': '用户管理',
            'role': '角色管理',
            'permission': '权限管理',
            'student': '学生管理',
            'course': '课程管理',
            'class': '班级管理',
            'payment': '缴费管理',
            'system': '系统管理',
            'dashboard': '仪表盘',
            'other': '其他'
        };
        return moduleNames[module] || module;
    }

    /**
     * 处理模块选择
     */
    handleModuleSelect(module, checked) {
        const checkboxes = document.querySelectorAll(`input[data-module="${module}"]`);
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
        this.updateSelectAllPermissionsState();
    }

    /**
     * 处理权限全选
     */
    handleSelectAllPermissions(checked) {
        const checkboxes = document.querySelectorAll('.permission-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
        
        const moduleCheckboxes = document.querySelectorAll('.module-checkbox');
        moduleCheckboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
    }

    /**
     * 更新权限全选状态
     */
    updateSelectAllPermissionsState() {
        const selectAllCheckbox = document.getElementById('selectAllPermissions');
        const checkboxes = document.querySelectorAll('.permission-checkbox');
        
        if (selectAllCheckbox && checkboxes.length > 0) {
            const checkedCount = document.querySelectorAll('.permission-checkbox:checked').length;
            selectAllCheckbox.checked = checkedCount === checkboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
        }

        // 更新模块复选框状态
        const modules = [...new Set(Array.from(checkboxes).map(cb => cb.dataset.module))];
        modules.forEach(module => {
            const moduleCheckbox = document.getElementById(`module_${module}`);
            const modulePermissions = document.querySelectorAll(`input[data-module="${module}"]`);
            const checkedModulePermissions = document.querySelectorAll(`input[data-module="${module}"]:checked`);
            
            if (moduleCheckbox) {
                moduleCheckbox.checked = checkedModulePermissions.length === modulePermissions.length;
                moduleCheckbox.indeterminate = checkedModulePermissions.length > 0 && 
                                               checkedModulePermissions.length < modulePermissions.length;
            }
        });
    }

    /**
     * 加载角色列表
     */
    async loadRoles() {
        try {
            showLoading('正在加载角色数据...');

            const params = {
                page: this.currentPage - 1,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/roles', params);
            
            if (response.success) {
                this.renderRoleTable(response.data.content || response.data);
                this.updatePagination(response.data);
                this.updateRoleStats(response.data);
            } else {
                throw new Error(response.message || '加载角色数据失败');
            }
        } catch (error) {
            console.error('加载角色数据失败:', error);
            showAlert('加载角色数据失败: ' + error.message, 'error');
            this.renderRoleTable([]);
        } finally {
            hideLoading();
        }
    }

    /**
     * 渲染角色表格
     */
    renderRoleTable(roles) {
        const tbody = document.getElementById('roleTableBody');
        if (!tbody) return;

        if (!roles || roles.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i>
                        <div>暂无角色数据</div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = roles.map(role => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input role-checkbox" type="checkbox" 
                               value="${role.id}" onchange="roleManagement.handleRoleSelect(${role.id}, this.checked)">
                    </div>
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-user-tag text-primary me-2"></i>
                        <div>
                            <div class="fw-bold">${role.roleName}</div>
                            <small class="text-muted">${role.roleKey}</small>
                        </div>
                    </div>
                </td>
                <td>${role.description || '-'}</td>
                <td>
                    <span class="badge ${this.getStatusBadgeClass(role.status)}">
                        ${this.getStatusText(role.status)}
                    </span>
                </td>
                <td>
                    <small class="text-muted">
                        ${role.createdTime ? new Date(role.createdTime).toLocaleString() : '-'}
                    </small>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" onclick="roleManagement.showEditRoleModal(${role.id})" 
                                title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-outline-info" onclick="roleManagement.showRoleDetail(${role.id})" 
                                title="详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button class="btn btn-outline-success" onclick="roleManagement.showPermissionModal(${role.id})" 
                                title="权限设置">
                            <i class="fas fa-key"></i>
                        </button>
                        <button class="btn btn-outline-danger" onclick="roleManagement.deleteRole(${role.id})" 
                                title="删除" ${role.roleKey === 'ADMIN' ? 'disabled' : ''}>
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
            case 1: return '启用';
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

        const pagination = document.getElementById('rolePagination');
        if (!pagination || this.totalPages <= 1) {
            if (pagination) pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';

        // 上一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="roleManagement.goToPage(${this.currentPage - 1})">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(this.totalPages, this.currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="roleManagement.goToPage(${i})">${i}</a>
                </li>
            `;
        }

        // 下一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === this.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="roleManagement.goToPage(${this.currentPage + 1})">
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
        await this.loadRoles();
    }

    /**
     * 更新角色统计
     */
    updateRoleStats(data) {
        const totalElement = document.getElementById('totalRoles');
        const activeElement = document.getElementById('activeRoles');

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

        this.searchParams = {};

        if (searchInput && searchInput.value.trim()) {
            this.searchParams.keyword = searchInput.value.trim();
        }

        if (statusFilter && statusFilter.value) {
            this.searchParams.status = statusFilter.value;
        }

        this.currentPage = 1;
        await this.loadRoles();
    }

    /**
     * 重置搜索
     */
    async handleReset() {
        const searchInput = document.getElementById('searchInput');
        const statusFilter = document.getElementById('statusFilter');

        if (searchInput) searchInput.value = '';
        if (statusFilter) statusFilter.value = '';

        this.searchParams = {};
        this.currentPage = 1;
        await this.loadRoles();
    }

    /**
     * 显示添加角色模态框
     */
    showAddRoleModal() {
        this.clearRoleForm();
        document.getElementById('roleModalTitle').textContent = '添加角色';
        document.getElementById('roleId').value = '';
        this.showModal('roleModal');
    }

    /**
     * 显示编辑角色模态框
     */
    async showEditRoleModal(roleId) {
        try {
            showLoading('正在加载角色信息...');

            const response = await apiClient.get(`/api/roles/${roleId}`);

            if (response.success) {
                this.fillRoleForm(response.data);
                document.getElementById('roleModalTitle').textContent = '编辑角色';
                document.getElementById('roleId').value = roleId;
                this.showModal('roleModal');
            } else {
                throw new Error(response.message || '获取角色信息失败');
            }
        } catch (error) {
            console.error('获取角色信息失败:', error);
            showAlert('获取角色信息失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 填充角色表单
     */
    fillRoleForm(role) {
        document.getElementById('roleName').value = role.roleName || '';
        document.getElementById('roleKey').value = role.roleKey || '';
        document.getElementById('description').value = role.description || '';
        document.getElementById('roleStatus').value = role.status || 1;
        document.getElementById('sortOrder').value = role.sortOrder || 0;
    }

    /**
     * 清空角色表单
     */
    clearRoleForm() {
        const form = document.getElementById('roleForm');
        if (form) {
            form.reset();
            document.getElementById('roleStatus').value = '1';
            document.getElementById('sortOrder').value = '0';
        }
    }

    /**
     * 处理保存角色
     */
    async handleSaveRole() {
        try {
            const formData = this.getRoleFormData();

            // 验证表单
            if (!this.validateRoleForm(formData)) {
                return;
            }

            showLoading('正在保存角色...');

            const roleId = document.getElementById('roleId').value;
            let response;

            if (roleId) {
                // 编辑角色
                response = await apiClient.put(`/api/roles/${roleId}`, formData);
            } else {
                // 添加角色
                response = await apiClient.post('/api/roles', formData);
            }

            if (response.success) {
                showAlert(roleId ? '角色更新成功' : '角色添加成功', 'success');
                this.closeModal();
                await this.loadRoles();
            } else {
                throw new Error(response.message || '保存角色失败');
            }
        } catch (error) {
            console.error('保存角色失败:', error);
            showAlert('保存角色失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 获取角色表单数据
     */
    getRoleFormData() {
        return {
            roleName: document.getElementById('roleName').value.trim(),
            roleKey: document.getElementById('roleKey').value.trim(),
            description: document.getElementById('description').value.trim(),
            status: parseInt(document.getElementById('roleStatus').value),
            sortOrder: parseInt(document.getElementById('sortOrder').value) || 0
        };
    }

    /**
     * 验证角色表单
     */
    validateRoleForm(formData) {
        if (!formData.roleName) {
            showAlert('请输入角色名称', 'warning');
            return false;
        }

        if (!formData.roleKey) {
            showAlert('请输入角色标识', 'warning');
            return false;
        }

        // 验证角色标识格式
        if (!/^[A-Z_]+$/.test(formData.roleKey)) {
            showAlert('角色标识只能包含大写字母和下划线', 'warning');
            return false;
        }

        return true;
    }

    /**
     * 删除角色
     */
    async deleteRole(roleId) {
        if (!confirm('确定要删除这个角色吗？此操作不可恢复！')) {
            return;
        }

        try {
            showLoading('正在删除角色...');

            const response = await apiClient.delete(`/api/roles/${roleId}`);

            if (response.success) {
                showAlert('角色删除成功', 'success');
                await this.loadRoles();
            } else {
                throw new Error(response.message || '删除角色失败');
            }
        } catch (error) {
            console.error('删除角色失败:', error);
            showAlert('删除角色失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 显示权限设置模态框
     */
    async showPermissionModal(roleId) {
        try {
            showLoading('正在加载角色权限...');

            const response = await apiClient.get(`/api/roles/${roleId}/permissions`);

            if (response.success) {
                this.setRolePermissions(response.data);
                document.getElementById('permissionRoleId').value = roleId;
                this.showModal('permissionModal');
            } else {
                throw new Error(response.message || '获取角色权限失败');
            }
        } catch (error) {
            console.error('获取角色权限失败:', error);
            showAlert('获取角色权限失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 设置角色权限
     */
    setRolePermissions(permissions) {
        // 清空所有选择
        const checkboxes = document.querySelectorAll('.permission-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = false;
        });

        // 设置已有权限
        permissions.forEach(permission => {
            const checkbox = document.getElementById(`permission_${permission.id}`);
            if (checkbox) {
                checkbox.checked = true;
            }
        });

        // 更新状态
        this.updateSelectAllPermissionsState();
    }

    /**
     * 保存角色权限
     */
    async saveRolePermissions() {
        try {
            const roleId = document.getElementById('permissionRoleId').value;
            const selectedPermissions = Array.from(document.querySelectorAll('.permission-checkbox:checked'))
                .map(checkbox => ({ id: parseInt(checkbox.value) }));

            showLoading('正在保存权限...');

            const response = await apiClient.post(`/api/roles/${roleId}/permissions`, {
                permissions: selectedPermissions
            });

            if (response.success) {
                showAlert('权限设置成功', 'success');
                this.closeModal();
            } else {
                throw new Error(response.message || '保存权限失败');
            }
        } catch (error) {
            console.error('保存权限失败:', error);
            showAlert('保存权限失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 处理角色选择
     */
    handleRoleSelect(roleId, checked) {
        if (checked) {
            this.selectedRoles.add(roleId);
        } else {
            this.selectedRoles.delete(roleId);
        }
        this.updateBatchButtons();
        this.updateSelectAllState();
    }

    /**
     * 处理全选
     */
    handleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.role-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
            const roleId = parseInt(checkbox.value);
            if (checked) {
                this.selectedRoles.add(roleId);
            } else {
                this.selectedRoles.delete(roleId);
            }
        });
        this.updateBatchButtons();
    }

    /**
     * 更新全选状态
     */
    updateSelectAllState() {
        const selectAllCheckbox = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.role-checkbox');

        if (selectAllCheckbox && checkboxes.length > 0) {
            const checkedCount = document.querySelectorAll('.role-checkbox:checked').length;
            selectAllCheckbox.checked = checkedCount === checkboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
        }
    }

    /**
     * 更新批量操作按钮状态
     */
    updateBatchButtons() {
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        const selectedCount = this.selectedRoles.size;

        if (batchDeleteBtn) {
            batchDeleteBtn.disabled = selectedCount === 0;
            batchDeleteBtn.textContent = selectedCount > 0 ? `批量删除 (${selectedCount})` : '批量删除';
        }
    }

    /**
     * 批量删除角色
     */
    async handleBatchDelete() {
        if (this.selectedRoles.size === 0) {
            showAlert('请选择要删除的角色', 'warning');
            return;
        }

        if (!confirm(`确定要删除选中的 ${this.selectedRoles.size} 个角色吗？此操作不可恢复！`)) {
            return;
        }

        try {
            showLoading('正在批量删除角色...');

            const roleIds = Array.from(this.selectedRoles);
            const response = await apiClient.post('/api/roles/batch-delete', { roleIds });

            if (response.success) {
                showAlert(`成功删除 ${roleIds.length} 个角色`, 'success');
                this.selectedRoles.clear();
                await this.loadRoles();
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
     * 显示角色详情
     */
    async showRoleDetail(roleId) {
        try {
            showLoading('正在加载角色详情...');

            const response = await apiClient.get(`/api/roles/${roleId}`);

            if (response.success) {
                this.renderRoleDetail(response.data);
                this.showModal('roleDetailModal');
            } else {
                throw new Error(response.message || '获取角色详情失败');
            }
        } catch (error) {
            console.error('获取角色详情失败:', error);
            showAlert('获取角色详情失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 渲染角色详情
     */
    renderRoleDetail(role) {
        const detailContainer = document.getElementById('roleDetailContent');
        if (!detailContainer) return;

        detailContainer.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <table class="table table-borderless">
                        <tr>
                            <td class="fw-bold">角色名称：</td>
                            <td>${role.roleName}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">角色标识：</td>
                            <td><code>${role.roleKey}</code></td>
                        </tr>
                        <tr>
                            <td class="fw-bold">状态：</td>
                            <td>
                                <span class="badge ${this.getStatusBadgeClass(role.status)}">
                                    ${this.getStatusText(role.status)}
                                </span>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-bold">排序：</td>
                            <td>${role.sortOrder || 0}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">创建时间：</td>
                            <td>${role.createdTime ? new Date(role.createdTime).toLocaleString() : '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">描述：</td>
                            <td>${role.description || '-'}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-md-6">
                    <h6>角色权限</h6>
                    <div id="rolePermissionList" class="mt-2">
                        <div class="text-muted">正在加载权限信息...</div>
                    </div>
                </div>
            </div>
        `;

        // 加载角色权限
        this.loadRolePermissions(role.id);
    }

    /**
     * 加载角色权限
     */
    async loadRolePermissions(roleId) {
        try {
            const response = await apiClient.get(`/api/roles/${roleId}/permissions`);

            if (response.success) {
                const container = document.getElementById('rolePermissionList');
                if (container) {
                    if (response.data.length === 0) {
                        container.innerHTML = '<div class="text-muted">暂无权限</div>';
                    } else {
                        container.innerHTML = response.data.map(permission =>
                            `<span class="badge bg-info me-1 mb-1">${permission.permissionName}</span>`
                        ).join('');
                    }
                }
            }
        } catch (error) {
            console.error('加载角色权限失败:', error);
            const container = document.getElementById('rolePermissionList');
            if (container) {
                container.innerHTML = '<div class="text-danger">加载权限失败</div>';
            }
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
let roleManagement;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    roleManagement = new RoleManagement();
});
