/**
 * 角色管理页面脚本
 */
$(document).ready(function() {
    // 初始化页面
    initRoleManagement();
});

/**
 * 初始化角色管理
 */
function initRoleManagement() {
    // 绑定事件
    bindEvents();
    
    // 初始化表格
    initDataTable();
    
    // 加载统计数据
    loadRoleStatistics();
}

/**
 * 绑定事件
 */
function bindEvents() {
    // 搜索表单提交
    $('#searchForm').on('submit', function(e) {
        e.preventDefault();
        searchRoles();
    });
    
    // 重置按钮
    $('#resetBtn').on('click', function() {
        $('#searchForm')[0].reset();
        searchRoles();
    });
    
    // 刷新按钮
    $('#refreshBtn').on('click', function() {
        location.reload();
    });
    
    // 全选/取消全选
    $('#selectAll').on('change', function() {
        $('.row-checkbox').prop('checked', this.checked);
        updateBatchButtons();
    });
    
    // 行选择
    $(document).on('change', '.row-checkbox', function() {
        updateSelectAll();
        updateBatchButtons();
    });
    
    // 权限配置按钮
    $(document).on('click', '.permission-btn', function() {
        const roleId = $(this).data('id');
        const roleName = $(this).closest('tr').find('td:nth-child(2)').text().trim();
        showPermissionModal(roleId, roleName);
    });
    
    // 编辑按钮
    $(document).on('click', '.edit-btn', function() {
        const roleId = $(this).data('id');
        editRole(roleId);
    });
    
    // 删除按钮
    $(document).on('click', '.delete-btn', function() {
        const roleId = $(this).data('id');
        deleteRole(roleId);
    });
    
    // 创建角色表单提交
    $('#createRoleForm').on('submit', function(e) {
        e.preventDefault();
        createRole();
    });
    
    // 编辑角色表单提交
    $('#editRoleForm').on('submit', function(e) {
        e.preventDefault();
        updateRole();
    });
    
    // 保存权限按钮
    $('#savePermissionsBtn').on('click', function() {
        savePermissions();
    });
    
    // 批量操作
    $('#batchEnableBtn').on('click', function() {
        batchUpdateStatus(1);
    });
    
    $('#batchDisableBtn').on('click', function() {
        batchUpdateStatus(0);
    });
    
    // 导出数据
    $('#exportBtn').on('click', function() {
        exportRoles();
    });
}

/**
 * 初始化数据表格
 */
function initDataTable() {
    // 如果使用DataTables插件
    if (typeof $.fn.DataTable !== 'undefined') {
        $('#dataTable').DataTable({
            "language": {
                "url": "/js/plugins/datatables/Chinese.json"
            },
            "pageLength": 20,
            "ordering": true,
            "searching": false,
            "columnDefs": [
                { "orderable": false, "targets": [0, 8] }
            ]
        });
    }
}

/**
 * 加载角色统计数据
 */
function loadRoleStatistics() {
    $.ajax({
        url: '/api/v1/roles/statistics',
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                updateStatistics(response.data);
            }
        },
        error: function() {
            console.warn('加载角色统计数据失败');
        }
    });
}

/**
 * 更新统计信息
 */
function updateStatistics(stats) {
    $('#totalRoles').text(stats.totalRoles || 0);
    $('#systemRoles').text(stats.systemRoles || 0);
    $('#customRoles').text(stats.customRoles || 0);
    $('#activeRoles').text(stats.activeRoles || 0);
}

/**
 * 搜索角色
 */
function searchRoles() {
    const formData = $('#searchForm').serialize();
    const currentUrl = new URL(window.location);
    
    // 更新URL参数
    const params = new URLSearchParams(formData);
    params.forEach((value, key) => {
        if (value) {
            currentUrl.searchParams.set(key, value);
        } else {
            currentUrl.searchParams.delete(key);
        }
    });
    
    // 重置页码
    currentUrl.searchParams.set('page', '0');
    
    // 跳转到新URL
    window.location.href = currentUrl.toString();
}

/**
 * 创建角色
 */
function createRole() {
    const formData = new FormData($('#createRoleForm')[0]);
    const roleData = Object.fromEntries(formData.entries());
    
    // 转换状态值
    roleData.status = roleData.status ? 1 : 0;
    
    // 显示加载状态
    const submitBtn = $('#createRoleForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);
    
    // 发送请求
    $.ajax({
        url: '/api/v1/roles',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(roleData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('角色创建成功');
                $('#createRoleModal').modal('hide');
                $('#createRoleForm')[0].reset();
                setTimeout(() => location.reload(), 1000);
            } else {
                showError(response.message || '创建失败');
            }
        },
        error: function(xhr) {
            const response = xhr.responseJSON;
            showError(response?.message || '创建失败，请稍后重试');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

/**
 * 编辑角色
 */
function editRole(roleId) {
    // 获取角色信息
    $.ajax({
        url: `/api/v1/roles/${roleId}`,
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const role = response.data;
                
                // 填充表单
                $('#editRoleId').val(role.id);
                $('#editRoleName').val(role.name);
                $('#editRoleCode').val(role.code);
                $('#editRoleDescription').val(role.description || '');
                $('#editRoleType').val(role.type || 'CUSTOM');
                $('#editRoleStatus').prop('checked', role.status === 1);
                
                // 显示模态框
                $('#editRoleModal').modal('show');
            } else {
                showError(response.message || '获取角色信息失败');
            }
        },
        error: function() {
            showError('获取角色信息失败，请稍后重试');
        }
    });
}

/**
 * 更新角色
 */
function updateRole() {
    const formData = new FormData($('#editRoleForm')[0]);
    const roleData = Object.fromEntries(formData.entries());
    const roleId = roleData.id;
    
    // 转换状态值
    roleData.status = roleData.status ? 1 : 0;
    
    // 显示加载状态
    const submitBtn = $('#editRoleForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);
    
    // 发送请求
    $.ajax({
        url: `/api/v1/roles/${roleId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(roleData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('角色更新成功');
                $('#editRoleModal').modal('hide');
                setTimeout(() => location.reload(), 1000);
            } else {
                showError(response.message || '更新失败');
            }
        },
        error: function(xhr) {
            const response = xhr.responseJSON;
            showError(response?.message || '更新失败，请稍后重试');
        },
        complete: function() {
            submitBtn.html(originalText).prop('disabled', false);
        }
    });
}

/**
 * 删除角色
 */
function deleteRole(roleId) {
    if (!confirm('确定要删除这个角色吗？此操作不可恢复。')) {
        return;
    }
    
    $.ajax({
        url: `/api/v1/roles/${roleId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.code === 200) {
                showSuccess('角色删除成功');
                setTimeout(() => location.reload(), 1000);
            } else {
                showError(response.message || '删除失败');
            }
        },
        error: function(xhr) {
            const response = xhr.responseJSON;
            showError(response?.message || '删除失败，请稍后重试');
        }
    });
}

/**
 * 显示权限管理模态框
 */
function showPermissionModal(roleId, roleName) {
    $('#permissionRoleId').val(roleId);
    $('#permissionModalTitle').text(`权限配置 - ${roleName}`);
    
    // 加载权限数据
    loadPermissions(roleId);
    
    $('#permissionModal').modal('show');
}

/**
 * 加载权限数据
 */
function loadPermissions(roleId) {
    $('#permissionTree').html('<div class="text-center py-4"><i class="fas fa-spinner fa-spin fa-2x"></i><p class="mt-2">加载权限数据中...</p></div>');
    
    // 这里需要根据实际的权限API来实现
    // 暂时使用模拟数据
    setTimeout(() => {
        renderPermissionTree([]);
    }, 1000);
}

/**
 * 渲染权限树
 */
function renderPermissionTree(permissions) {
    let html = '<div class="alert alert-info">权限管理功能开发中...</div>';
    $('#permissionTree').html(html);
}

/**
 * 保存权限
 */
function savePermissions() {
    const roleId = $('#permissionRoleId').val();
    // 权限保存逻辑
    showInfo('权限保存功能开发中...');
}

/**
 * 批量更新状态
 */
function batchUpdateStatus(status) {
    const selectedIds = $('.row-checkbox:checked').map(function() {
        return $(this).val();
    }).get();
    
    if (selectedIds.length === 0) {
        showWarning('请选择要操作的角色');
        return;
    }
    
    const action = status === 1 ? '启用' : '禁用';
    if (!confirm(`确定要${action}选中的 ${selectedIds.length} 个角色吗？`)) {
        return;
    }
    
    // 批量状态更新逻辑
    showInfo(`批量${action}功能开发中...`);
}

/**
 * 导出角色数据
 */
function exportRoles() {
    const searchParams = new URLSearchParams($('#searchForm').serialize());
    const exportUrl = '/api/v1/roles/export?' + searchParams.toString();
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `roles_${new Date().toISOString().split('T')[0]}.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showInfo('导出任务已开始，请稍候...');
}

/**
 * 更新全选状态
 */
function updateSelectAll() {
    const totalCheckboxes = $('.row-checkbox').length;
    const checkedCheckboxes = $('.row-checkbox:checked').length;
    
    $('#selectAll').prop('checked', totalCheckboxes > 0 && checkedCheckboxes === totalCheckboxes);
}

/**
 * 更新批量操作按钮状态
 */
function updateBatchButtons() {
    const hasSelected = $('.row-checkbox:checked').length > 0;
    $('#batchEnableBtn, #batchDisableBtn').toggleClass('disabled', !hasSelected);
}

// 消息提示函数
function showSuccess(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'success', title: '成功', text: message, timer: 2000, showConfirmButton: false });
    } else { alert(message); }
}

function showError(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'error', title: '错误', text: message });
    } else { alert(message); }
}

function showWarning(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'warning', title: '警告', text: message });
    } else { alert(message); }
}

function showInfo(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'info', title: '提示', text: message, timer: 3000, showConfirmButton: false });
    } else { alert(message); }
}
