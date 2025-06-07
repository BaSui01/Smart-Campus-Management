/**
 * 角色管理模块 v3.0
 * 基于CrudBase的角色增删改查功能
 */
class RoleManagement extends CrudBase {
    constructor() {
        super({
            apiEndpoint: '/api/roles',
            tableName: '角色',
            modalId: 'addRoleModal',
            formId: 'addRoleForm',
            tableBodyId: 'roleTable',
            paginationId: 'pagination',
            searchFormId: 'searchForm'
        });

        this.permissionData = [];
    }

    /**
     * 渲染表格行
     */
    renderTableRow(role) {
        return `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${role.id}">
                    </div>
                </td>
                <td>
                    <code>${role.roleName}</code>
                </td>
                <td>${role.roleDescription || '-'}</td>
                <td>${role.description || '无描述'}</td>
                <td>
                    <span class="badge bg-primary">系统角色</span>
                </td>
                <td>${role.userCount || 0}</td>
                <td>
                    <small class="text-muted">${DataFormatter.formatDate(role.createdAt)}</small>
                </td>
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn-action btn-view"
                                data-action="permissions"
                                data-id="${role.id}"
                                data-name="${role.roleDescription}"
                                title="管理权限">
                            <i class="fas fa-key"></i>
                        </button>
                        <button type="button" class="btn-action btn-edit"
                                data-action="edit"
                                data-id="${role.id}"
                                title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn-action btn-delete"
                                data-action="delete"
                                data-id="${role.id}"
                                title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        super.bindEvents();

        // 权限管理按钮
        document.addEventListener('click', (e) => {
            const target = e.target.closest('[data-action="permissions"]');
            if (target) {
                const id = target.dataset.id;
                const name = target.dataset.name;
                this.managePermissions(id, name);
            }
        });

        // 编辑角色保存按钮
        const saveEditRoleBtn = document.getElementById('saveEditRoleBtn');
        if (saveEditRoleBtn) {
            saveEditRoleBtn.addEventListener('click', () => this.saveEditRole());
        }
    }

    /**
     * 获取表单数据
     */
    getFormData() {
        const form = document.getElementById(this.config.formId);
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        return data;
    }

    /**
     * 填充表单
     */
    populateForm(data) {
        // 检查是否有编辑表单元素
        const editRoleId = document.getElementById('editRoleId');
        const editRoleName = document.getElementById('editRoleName');
        const editRoleDisplayName = document.getElementById('editRoleDisplayName');
        const editRoleDescription = document.getElementById('editRoleDescription');

        if (editRoleId) editRoleId.value = data.id || '';
        if (editRoleName) editRoleName.value = data.roleName || '';
        if (editRoleDisplayName) editRoleDisplayName.value = data.roleDescription || '';
        if (editRoleDescription) editRoleDescription.value = data.description || '';

        // 显示编辑模态框
        const editModal = document.getElementById('editRoleModal');
        if (editModal) {
            const bsModal = new bootstrap.Modal(editModal);
            bsModal.show();
        }
    }

    /**
     * 重置表单
     */
    resetForm() {
        const form = document.getElementById(this.config.formId);
        if (form) {
            form.reset();
        }

        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    /**
     * 获取当前编辑的ID
     */
    getCurrentId() {
        const editRoleId = document.getElementById('editRoleId');
        return editRoleId ? editRoleId.value || null : null;
    }

    /**
     * 保存编辑的角色
     */
    async saveEditRole() {
        const editRoleId = document.getElementById('editRoleId');
        const editRoleName = document.getElementById('editRoleName');
        const editRoleDisplayName = document.getElementById('editRoleDisplayName');
        const editRoleDescription = document.getElementById('editRoleDescription');

        if (!editRoleId || !editRoleName || !editRoleDisplayName) {
            MessageUtils.error('编辑表单元素未找到');
            return;
        }

        const data = {
            roleName: editRoleName.value.trim(),
            roleDescription: editRoleDisplayName.value.trim(),
            description: editRoleDescription.value.trim()
        };

        if (!data.roleName || !data.roleDescription) {
            MessageUtils.error('请填写角色名称和显示名称');
            return;
        }

        try {
            const response = await apiClient.put(`${this.config.apiEndpoint}/${editRoleId.value}`, data);
            if (this.isResponseSuccess(response)) {
                MessageUtils.success('角色更新成功');
                const editModal = bootstrap.Modal.getInstance(document.getElementById('editRoleModal'));
                if (editModal) {
                    editModal.hide();
                }
                this.loadData(this.currentPage);
            } else {
                MessageUtils.error('角色更新失败: ' + (response.message || '未知错误'));
            }
        } catch (error) {
            MessageUtils.error('角色更新失败: ' + error.message);
        }
    }

    /**
     * 管理权限
     */
    async managePermissions(roleId, roleName) {
        try {
            // 获取权限列表和角色权限
            const [permissionsResponse, rolePermissionsResponse] = await Promise.all([
                apiClient.get('/api/permissions'),
                apiClient.get(`/api/roles/${roleId}/permissions`)
            ]);

            if (this.isResponseSuccess(permissionsResponse) && this.isResponseSuccess(rolePermissionsResponse)) {
                this.permissionData = permissionsResponse.data;
                const rolePermissions = rolePermissionsResponse.data;

                this.showPermissionModal(roleId, roleName, rolePermissions);
            } else {
                MessageUtils.error('获取权限数据失败');
            }
        } catch (error) {
            MessageUtils.error('获取权限数据失败: ' + error.message);
        }
    }

    /**
     * 显示权限管理模态框
     */
    showPermissionModal(roleId, roleName, rolePermissions) {
        const permissionRoleId = document.getElementById('permissionRoleId');
        const permissionModalTitle = document.getElementById('permissionModalTitle');

        if (permissionRoleId) permissionRoleId.value = roleId;
        if (permissionModalTitle) permissionModalTitle.textContent = `管理权限 - ${roleName}`;

        // 渲染权限列表
        this.renderPermissionList(rolePermissions);

        // 显示模态框
        const permissionModal = document.getElementById('permissionModal');
        if (permissionModal) {
            const bsModal = new bootstrap.Modal(permissionModal);
            bsModal.show();
        }
    }

    /**
     * 渲染权限列表
     */
    renderPermissionList(rolePermissions) {
        const permissionList = document.getElementById('permissionList');
        if (!permissionList) return;

        const rolePermissionIds = rolePermissions.map(p => p.id);

        // 按模块分组权限
        const groupedPermissions = this.groupPermissionsByModule(this.permissionData);

        let html = '';
        Object.keys(groupedPermissions).forEach(module => {
            html += `
                <div class="permission-module mb-3">
                    <h6>${module}</h6>
                    <div class="permission-items">
            `;

            groupedPermissions[module].forEach(permission => {
                const isChecked = rolePermissionIds.includes(permission.id);
                html += `
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox"
                               id="perm_${permission.id}" value="${permission.id}"
                               ${isChecked ? 'checked' : ''}>
                        <label class="form-check-label" for="perm_${permission.id}">
                            ${permission.permissionName} - ${permission.description || ''}
                        </label>
                    </div>
                `;
            });

            html += `
                    </div>
                </div>
            `;
        });

        permissionList.innerHTML = html;
    }

    /**
     * 按模块分组权限
     */
    groupPermissionsByModule(permissions) {
        const grouped = {};
        permissions.forEach(permission => {
            const module = permission.module || '其他';
            if (!grouped[module]) {
                grouped[module] = [];
            }
            grouped[module].push(permission);
        });
        return grouped;
    }

    /**
     * 保存权限
     */
    async savePermissions() {
        const permissionRoleId = document.getElementById('permissionRoleId');
        if (!permissionRoleId) {
            MessageUtils.error('权限角色ID未找到');
            return;
        }

        const roleId = permissionRoleId.value;
        const checkboxes = document.querySelectorAll('#permissionList input[type="checkbox"]:checked');
        const permissionIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

        try {
            const response = await apiClient.put(`/api/roles/${roleId}/permissions`, permissionIds);
            if (this.isResponseSuccess(response)) {
                MessageUtils.success('权限保存成功');
                const permissionModal = bootstrap.Modal.getInstance(document.getElementById('permissionModal'));
                if (permissionModal) {
                    permissionModal.hide();
                }
            } else {
                MessageUtils.error('权限保存失败: ' + response.message);
            }
        } catch (error) {
            MessageUtils.error('权限保存失败: ' + error.message);
        }
    }

    /**
     * 切换所有权限
     */
    toggleAllPermissions(checked) {
        const checkboxes = document.querySelectorAll('#permissionList input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = checked);
    }

    /**
     * 获取验证规则
     */
    getValidationRules() {
        return [
            {
                field: 'roleName',
                rules: { required: true, maxLength: 50 },
                message: '角色名称不能为空且不超过50个字符'
            },
            {
                field: 'roleDisplayName',
                rules: { required: true, maxLength: 100 },
                message: '显示名称不能为空且不超过100个字符'
            }
        ];
    }

    /**
     * 更新统计信息
     */
    updateStatistics(data) {
        // 角色管理的统计信息更新
        const stats = data.stats || {};
        // 可以根据需要更新统计卡片
    }

    /**
     * 获取导出表头
     */
    getExportHeaders() {
        return ['角色名称', '显示名称', '描述', '用户数量', '创建时间'];
    }
}

// 全局函数，供模态框按钮调用
window.savePermissions = function() {
    if (window.roleManagement) {
        window.roleManagement.savePermissions();
    }
};

window.toggleAllPermissions = function(checked) {
    if (window.roleManagement) {
        window.roleManagement.toggleAllPermissions(checked);
    }
};

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('roleTable')) {
        window.roleManagement = new RoleManagement();
    }
});