/**
 * 权限管理模块
 * 提供权限的增删改查功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */

class PermissionManagement {
    constructor() {
        this.currentPage = 1;
        this.pageSize = 15;
        this.totalPages = 0;
        this.searchParams = {};
        this.selectedPermissions = new Set();
        this.init();
    }

    /**
     * 初始化权限管理
     */
    async init() {
        try {
            await this.loadPermissions();
            this.bindEvents();
            this.initializeComponents();
            console.log('权限管理模块初始化完成');
        } catch (error) {
            console.error('权限管理模块初始化失败:', error);
            showAlert('权限管理模块初始化失败: ' + error.message, 'error');
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

        // 添加权限
        const addPermissionBtn = document.getElementById('addPermissionBtn');
        if (addPermissionBtn) {
            addPermissionBtn.addEventListener('click', () => this.showAddPermissionModal());
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
        // 保存权限
        const savePermissionBtn = document.getElementById('savePermissionBtn');
        if (savePermissionBtn) {
            savePermissionBtn.addEventListener('click', () => this.handleSavePermission());
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
        // 更新批量操作按钮状态
        this.updateBatchButtons();
    }

    /**
     * 加载权限列表
     */
    async loadPermissions() {
        try {
            showLoading('正在加载权限数据...');

            const params = {
                page: this.currentPage - 1,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/permissions', params);
            
            if (response.success) {
                this.renderPermissionTable(response.data.content || response.data);
                this.updatePagination(response.data);
                this.updatePermissionStats(response.data);
            } else {
                throw new Error(response.message || '加载权限数据失败');
            }
        } catch (error) {
            console.error('加载权限数据失败:', error);
            showAlert('加载权限数据失败: ' + error.message, 'error');
            this.renderPermissionTable([]);
        } finally {
            hideLoading();
        }
    }

    /**
     * 渲染权限表格
     */
    renderPermissionTable(permissions) {
        const tbody = document.getElementById('permissionTableBody');
        if (!tbody) return;

        if (!permissions || permissions.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i>
                        <div>暂无权限数据</div>
                    </td>
                </tr>
            `;
            return;
        }

        // 按模块分组显示
        const groupedPermissions = this.groupPermissionsByModule(permissions);
        let tableHtml = '';

        Object.keys(groupedPermissions).forEach(module => {
            // 模块标题行
            tableHtml += `
                <tr class="table-secondary">
                    <td colspan="6" class="fw-bold">
                        <i class="fas fa-folder-open me-2"></i>
                        ${this.getModuleName(module)}
                    </td>
                </tr>
            `;

            // 模块下的权限
            groupedPermissions[module].forEach(permission => {
                tableHtml += `
                    <tr>
                        <td>
                            <div class="form-check">
                                <input class="form-check-input permission-checkbox" type="checkbox" 
                                       value="${permission.id}" onchange="permissionManagement.handlePermissionSelect(${permission.id}, this.checked)">
                            </div>
                        </td>
                        <td>
                            <div class="d-flex align-items-center">
                                <i class="fas fa-key text-warning me-2"></i>
                                <div>
                                    <div class="fw-bold">${permission.permissionName}</div>
                                    <small class="text-muted">${permission.permissionKey}</small>
                                </div>
                            </div>
                        </td>
                        <td>${permission.description || '-'}</td>
                        <td>
                            <span class="badge bg-info">${this.getModuleName(module)}</span>
                        </td>
                        <td>
                            <small class="text-muted">
                                ${permission.createdTime ? new Date(permission.createdTime).toLocaleString() : '-'}
                            </small>
                        </td>
                        <td>
                            <div class="btn-group btn-group-sm">
                                <button class="btn btn-outline-primary" onclick="permissionManagement.showEditPermissionModal(${permission.id})" 
                                        title="编辑">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button class="btn btn-outline-info" onclick="permissionManagement.showPermissionDetail(${permission.id})" 
                                        title="详情">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button class="btn btn-outline-danger" onclick="permissionManagement.deletePermission(${permission.id})" 
                                        title="删除">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                `;
            });
        });

        tbody.innerHTML = tableHtml;

        // 更新选中状态
        this.updateSelectAllState();
    }

    /**
     * 按模块分组权限
     */
    groupPermissionsByModule(permissions) {
        const grouped = {};
        permissions.forEach(permission => {
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
     * 更新分页
     */
    updatePagination(pageData) {
        if (pageData.totalPages !== undefined) {
            this.totalPages = pageData.totalPages;
            this.currentPage = pageData.number + 1;
        }

        const pagination = document.getElementById('permissionPagination');
        if (!pagination || this.totalPages <= 1) {
            if (pagination) pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';
        
        // 上一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="permissionManagement.goToPage(${this.currentPage - 1})">
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
                    <a class="page-link" href="#" onclick="permissionManagement.goToPage(${i})">${i}</a>
                </li>
            `;
        }

        // 下一页
        paginationHtml += `
            <li class="page-item ${this.currentPage === this.totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="permissionManagement.goToPage(${this.currentPage + 1})">
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
        await this.loadPermissions();
    }

    /**
     * 更新权限统计
     */
    updatePermissionStats(data) {
        const totalElement = document.getElementById('totalPermissions');
        
        if (totalElement && data.totalElements !== undefined) {
            totalElement.textContent = data.totalElements.toLocaleString();
        }
    }

    /**
     * 处理搜索
     */
    async handleSearch() {
        const searchInput = document.getElementById('searchInput');
        const moduleFilter = document.getElementById('moduleFilter');

        this.searchParams = {};

        if (searchInput && searchInput.value.trim()) {
            this.searchParams.keyword = searchInput.value.trim();
        }

        if (moduleFilter && moduleFilter.value) {
            this.searchParams.module = moduleFilter.value;
        }

        this.currentPage = 1;
        await this.loadPermissions();
    }

    /**
     * 重置搜索
     */
    async handleReset() {
        const searchInput = document.getElementById('searchInput');
        const moduleFilter = document.getElementById('moduleFilter');

        if (searchInput) searchInput.value = '';
        if (moduleFilter) moduleFilter.value = '';

        this.searchParams = {};
        this.currentPage = 1;
        await this.loadPermissions();
    }

    /**
     * 显示添加权限模态框
     */
    showAddPermissionModal() {
        this.clearPermissionForm();
        document.getElementById('permissionModalTitle').textContent = '添加权限';
        document.getElementById('permissionId').value = '';
        this.showModal('permissionModal');
    }

    /**
     * 显示编辑权限模态框
     */
    async showEditPermissionModal(permissionId) {
        try {
            showLoading('正在加载权限信息...');

            const response = await apiClient.get(`/api/permissions/${permissionId}`);

            if (response.success) {
                this.fillPermissionForm(response.data);
                document.getElementById('permissionModalTitle').textContent = '编辑权限';
                document.getElementById('permissionId').value = permissionId;
                this.showModal('permissionModal');
            } else {
                throw new Error(response.message || '获取权限信息失败');
            }
        } catch (error) {
            console.error('获取权限信息失败:', error);
            showAlert('获取权限信息失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 填充权限表单
     */
    fillPermissionForm(permission) {
        document.getElementById('permissionName').value = permission.permissionName || '';
        document.getElementById('permissionKey').value = permission.permissionKey || '';
        document.getElementById('permissionDescription').value = permission.description || '';

        // 从权限键中提取模块
        const module = permission.permissionKey.split(':')[0] || '';
        document.getElementById('permissionModule').value = module;
    }

    /**
     * 清空权限表单
     */
    clearPermissionForm() {
        const form = document.getElementById('permissionForm');
        if (form) {
            form.reset();
        }
    }

    /**
     * 处理保存权限
     */
    async handleSavePermission() {
        try {
            const formData = this.getPermissionFormData();

            // 验证表单
            if (!this.validatePermissionForm(formData)) {
                return;
            }

            showLoading('正在保存权限...');

            const permissionId = document.getElementById('permissionId').value;
            let response;

            if (permissionId) {
                // 编辑权限
                response = await apiClient.put(`/api/permissions/${permissionId}`, formData);
            } else {
                // 添加权限
                response = await apiClient.post('/api/permissions', formData);
            }

            if (response.success) {
                showAlert(permissionId ? '权限更新成功' : '权限添加成功', 'success');
                this.closeModal();
                await this.loadPermissions();
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
     * 获取权限表单数据
     */
    getPermissionFormData() {
        const module = document.getElementById('permissionModule').value;
        const action = document.getElementById('permissionAction').value;

        // 自动生成权限键
        let permissionKey = document.getElementById('permissionKey').value.trim();
        if (!permissionKey && module && action) {
            permissionKey = `${module}:${action}`;
        }

        return {
            permissionName: document.getElementById('permissionName').value.trim(),
            permissionKey: permissionKey,
            description: document.getElementById('permissionDescription').value.trim()
        };
    }

    /**
     * 验证权限表单
     */
    validatePermissionForm(formData) {
        if (!formData.permissionName) {
            showAlert('请输入权限名称', 'warning');
            return false;
        }

        if (!formData.permissionKey) {
            showAlert('请输入权限标识', 'warning');
            return false;
        }

        // 验证权限标识格式
        if (!/^[a-z_]+:[a-z_]+$/.test(formData.permissionKey)) {
            showAlert('权限标识格式应为：模块:操作（如：user:create）', 'warning');
            return false;
        }

        return true;
    }

    /**
     * 删除权限
     */
    async deletePermission(permissionId) {
        if (!confirm('确定要删除这个权限吗？此操作不可恢复！')) {
            return;
        }

        try {
            showLoading('正在删除权限...');

            const response = await apiClient.delete(`/api/permissions/${permissionId}`);

            if (response.success) {
                showAlert('权限删除成功', 'success');
                await this.loadPermissions();
            } else {
                throw new Error(response.message || '删除权限失败');
            }
        } catch (error) {
            console.error('删除权限失败:', error);
            showAlert('删除权限失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 处理权限选择
     */
    handlePermissionSelect(permissionId, checked) {
        if (checked) {
            this.selectedPermissions.add(permissionId);
        } else {
            this.selectedPermissions.delete(permissionId);
        }
        this.updateBatchButtons();
        this.updateSelectAllState();
    }

    /**
     * 处理全选
     */
    handleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.permission-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
            const permissionId = parseInt(checkbox.value);
            if (checked) {
                this.selectedPermissions.add(permissionId);
            } else {
                this.selectedPermissions.delete(permissionId);
            }
        });
        this.updateBatchButtons();
    }

    /**
     * 更新全选状态
     */
    updateSelectAllState() {
        const selectAllCheckbox = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.permission-checkbox');

        if (selectAllCheckbox && checkboxes.length > 0) {
            const checkedCount = document.querySelectorAll('.permission-checkbox:checked').length;
            selectAllCheckbox.checked = checkedCount === checkboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
        }
    }

    /**
     * 更新批量操作按钮状态
     */
    updateBatchButtons() {
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        const selectedCount = this.selectedPermissions.size;

        if (batchDeleteBtn) {
            batchDeleteBtn.disabled = selectedCount === 0;
            batchDeleteBtn.textContent = selectedCount > 0 ? `批量删除 (${selectedCount})` : '批量删除';
        }
    }

    /**
     * 批量删除权限
     */
    async handleBatchDelete() {
        if (this.selectedPermissions.size === 0) {
            showAlert('请选择要删除的权限', 'warning');
            return;
        }

        if (!confirm(`确定要删除选中的 ${this.selectedPermissions.size} 个权限吗？此操作不可恢复！`)) {
            return;
        }

        try {
            showLoading('正在批量删除权限...');

            const permissionIds = Array.from(this.selectedPermissions);
            const response = await apiClient.post('/api/permissions/batch-delete', { permissionIds });

            if (response.success) {
                showAlert(`成功删除 ${permissionIds.length} 个权限`, 'success');
                this.selectedPermissions.clear();
                await this.loadPermissions();
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
     * 显示权限详情
     */
    async showPermissionDetail(permissionId) {
        try {
            showLoading('正在加载权限详情...');

            const response = await apiClient.get(`/api/permissions/${permissionId}`);

            if (response.success) {
                this.renderPermissionDetail(response.data);
                this.showModal('permissionDetailModal');
            } else {
                throw new Error(response.message || '获取权限详情失败');
            }
        } catch (error) {
            console.error('获取权限详情失败:', error);
            showAlert('获取权限详情失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 渲染权限详情
     */
    renderPermissionDetail(permission) {
        const detailContainer = document.getElementById('permissionDetailContent');
        if (!detailContainer) return;

        const module = permission.permissionKey.split(':')[0] || 'other';

        detailContainer.innerHTML = `
            <div class="row">
                <div class="col-md-8">
                    <table class="table table-borderless">
                        <tr>
                            <td class="fw-bold">权限名称：</td>
                            <td>${permission.permissionName}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">权限标识：</td>
                            <td><code>${permission.permissionKey}</code></td>
                        </tr>
                        <tr>
                            <td class="fw-bold">所属模块：</td>
                            <td>
                                <span class="badge bg-info">${this.getModuleName(module)}</span>
                            </td>
                        </tr>
                        <tr>
                            <td class="fw-bold">创建时间：</td>
                            <td>${permission.createdTime ? new Date(permission.createdTime).toLocaleString() : '-'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">描述：</td>
                            <td>${permission.description || '-'}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-md-4">
                    <h6>关联角色</h6>
                    <div id="permissionRoleList" class="mt-2">
                        <div class="text-muted">正在加载关联角色...</div>
                    </div>
                </div>
            </div>
        `;

        // 加载关联角色
        this.loadPermissionRoles(permission.id);
    }

    /**
     * 加载权限关联的角色
     */
    async loadPermissionRoles(permissionId) {
        try {
            const response = await apiClient.get(`/api/permissions/${permissionId}/roles`);

            if (response.success) {
                const container = document.getElementById('permissionRoleList');
                if (container) {
                    if (response.data.length === 0) {
                        container.innerHTML = '<div class="text-muted">暂无关联角色</div>';
                    } else {
                        container.innerHTML = response.data.map(role =>
                            `<span class="badge bg-primary me-1 mb-1">${role.roleName}</span>`
                        ).join('');
                    }
                }
            }
        } catch (error) {
            console.error('加载权限关联角色失败:', error);
            const container = document.getElementById('permissionRoleList');
            if (container) {
                container.innerHTML = '<div class="text-danger">加载关联角色失败</div>';
            }
        }
    }

    /**
     * 批量生成权限
     */
    showBatchGenerateModal() {
        this.showModal('batchGenerateModal');
    }

    /**
     * 处理批量生成权限
     */
    async handleBatchGenerate() {
        try {
            const modules = document.getElementById('generateModules').value.split(',').map(m => m.trim()).filter(m => m);
            const actions = document.getElementById('generateActions').value.split(',').map(a => a.trim()).filter(a => a);

            if (modules.length === 0 || actions.length === 0) {
                showAlert('请输入模块和操作', 'warning');
                return;
            }

            showLoading('正在批量生成权限...');

            const permissions = [];
            modules.forEach(module => {
                actions.forEach(action => {
                    permissions.push({
                        permissionName: `${this.getModuleName(module)}-${this.getActionName(action)}`,
                        permissionKey: `${module}:${action}`,
                        description: `${this.getModuleName(module)}的${this.getActionName(action)}权限`
                    });
                });
            });

            const response = await apiClient.post('/api/permissions/batch-create', { permissions });

            if (response.success) {
                showAlert(`成功生成 ${permissions.length} 个权限`, 'success');
                this.closeModal();
                await this.loadPermissions();
            } else {
                throw new Error(response.message || '批量生成权限失败');
            }
        } catch (error) {
            console.error('批量生成权限失败:', error);
            showAlert('批量生成权限失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 获取操作名称
     */
    getActionName(action) {
        const actionNames = {
            'create': '创建',
            'read': '查看',
            'update': '更新',
            'delete': '删除',
            'list': '列表',
            'export': '导出',
            'import': '导入',
            'manage': '管理'
        };
        return actionNames[action] || action;
    }

    /**
     * 权限同步
     */
    async syncPermissions() {
        if (!confirm('确定要同步权限吗？这将从代码中扫描并更新权限列表。')) {
            return;
        }

        try {
            showLoading('正在同步权限...');

            const response = await apiClient.post('/api/permissions/sync');

            if (response.success) {
                showAlert('权限同步成功', 'success');
                await this.loadPermissions();
            } else {
                throw new Error(response.message || '权限同步失败');
            }
        } catch (error) {
            console.error('权限同步失败:', error);
            showAlert('权限同步失败: ' + error.message, 'error');
        } finally {
            hideLoading();
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
let permissionManagement;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    permissionManagement = new PermissionManagement();
});
