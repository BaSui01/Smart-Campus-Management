/**
 * 用户管理页面脚本
 */
$(document).ready(function() {
    // 初始化页面
    initUserManagement();
});

/**
 * 初始化用户管理
 */
function initUserManagement() {
    // 绑定事件
    bindEvents();
    
    // 初始化日期选择器
    initDatePicker();
    
    // 初始化表格
    initDataTable();
}

/**
 * 绑定事件
 */
function bindEvents() {
    // 搜索表单提交
    $('#searchForm').on('submit', function(e) {
        e.preventDefault();
        searchUsers();
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
    
    // 编辑按钮
    $(document).on('click', '.edit-btn', function() {
        const userId = $(this).data('id');
        editUser(userId);
    });
    
    // 删除按钮
    $(document).on('click', '.delete-btn', function() {
        const userId = $(this).data('id');
        deleteUser(userId);
    });
    
    // 创建用户表单提交
    $('#createUserForm').on('submit', function(e) {
        e.preventDefault();
        createUser();
    });
    
    // 编辑用户表单提交
    $('#editUserForm').on('submit', function(e) {
        e.preventDefault();
        updateUser();
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
        exportUsers();
    });
}

/**
 * 初始化日期选择器
 */
function initDatePicker() {
    if (typeof flatpickr !== 'undefined') {
        flatpickr("#dateRange", {
            mode: "range",
            dateFormat: "Y-m-d",
            locale: "zh"
        });
    }
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
 * 搜索用户
 */
function searchUsers() {
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
 * 创建用户
 */
function createUser() {
    const formData = new FormData($('#createUserForm')[0]);
    const userData = Object.fromEntries(formData.entries());

    // 显示加载状态
    const submitBtn = $('#createUserForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);

    // 发送请求到正确的API端点
    $.ajax({
        url: '/api/v1/users',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(userData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('用户创建成功');
                $('#createUserModal').modal('hide');
                $('#createUserForm')[0].reset();
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
 * 编辑用户
 */
function editUser(userId) {
    // 获取用户信息
    $.ajax({
        url: `/api/v1/users/${userId}`,
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const user = response.data;

                // 填充表单
                $('#editUserId').val(user.id);
                $('#editUsername').val(user.username);
                $('#editRealName').val(user.realName || '');
                $('#editEmail').val(user.email || '');
                $('#editPhone').val(user.phone || '');
                $('#editRoleId').val(user.roleId || '');
                $('#editStatus').prop('checked', user.status === 1);

                // 显示模态框
                $('#editUserModal').modal('show');
            } else {
                showError(response.message || '获取用户信息失败');
            }
        },
        error: function() {
            showError('获取用户信息失败，请稍后重试');
        }
    });
}

/**
 * 更新用户
 */
function updateUser() {
    const formData = new FormData($('#editUserForm')[0]);
    const userData = Object.fromEntries(formData.entries());
    const userId = userData.id;
    
    // 显示加载状态
    const submitBtn = $('#editUserForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);
    
    // 发送请求
    $.ajax({
        url: `/api/v1/users/${userId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(userData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('用户更新成功');
                $('#editUserModal').modal('hide');
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
 * 删除用户
 */
function deleteUser(userId) {
    if (!confirm('确定要删除这个用户吗？此操作不可恢复。')) {
        return;
    }
    
    $.ajax({
        url: `/api/v1/users/${userId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.code === 200) {
                showSuccess('用户删除成功');
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
 * 批量更新状态
 */
function batchUpdateStatus(status) {
    const selectedIds = $('.row-checkbox:checked').map(function() {
        return $(this).val();
    }).get();
    
    if (selectedIds.length === 0) {
        showWarning('请选择要操作的用户');
        return;
    }
    
    const action = status === 1 ? '启用' : '禁用';
    if (!confirm(`确定要${action}选中的 ${selectedIds.length} 个用户吗？`)) {
        return;
    }
    
    $.ajax({
        url: '/api/v1/users/batch-status',
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            userIds: selectedIds,
            status: status
        }),
        success: function(response) {
            if (response.code === 200) {
                showSuccess(`批量${action}成功`);
                setTimeout(() => location.reload(), 1000);
            } else {
                showError(response.message || `批量${action}失败`);
            }
        },
        error: function(xhr) {
            const response = xhr.responseJSON;
            showError(response?.message || `批量${action}失败，请稍后重试`);
        }
    });
}

/**
 * 导出用户数据
 */
function exportUsers() {
    const searchParams = new URLSearchParams($('#searchForm').serialize());
    const exportUrl = '/api/v1/users/export?' + searchParams.toString();
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `users_${new Date().toISOString().split('T')[0]}.xlsx`;
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

/**
 * 显示成功消息
 */
function showSuccess(message) {
    // 使用Toast或其他通知组件
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'success',
            title: '成功',
            text: message,
            timer: 2000,
            showConfirmButton: false
        });
    } else {
        alert(message);
    }
}

/**
 * 显示错误消息
 */
function showError(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'error',
            title: '错误',
            text: message
        });
    } else {
        alert(message);
    }
}

/**
 * 显示警告消息
 */
function showWarning(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'warning',
            title: '警告',
            text: message
        });
    } else {
        alert(message);
    }
}

/**
 * 显示信息消息
 */
function showInfo(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: 'info',
            title: '提示',
            text: message,
            timer: 3000,
            showConfirmButton: false
        });
    } else {
        alert(message);
    }
}
