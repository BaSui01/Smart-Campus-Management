/**
 * 学生管理页面脚本
 */
$(document).ready(function() {
    // 初始化页面
    initStudentManagement();
});

/**
 * 初始化学生管理
 */
function initStudentManagement() {
    // 绑定事件
    bindEvents();
    
    // 初始化表格
    initDataTable();
    
    // 初始化级联选择
    initCascadeSelects();
}

/**
 * 绑定事件
 */
function bindEvents() {
    // 搜索表单提交
    $('#searchForm').on('submit', function(e) {
        e.preventDefault();
        searchStudents();
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
    
    // 详情按钮
    $(document).on('click', '.detail-btn', function() {
        const studentId = $(this).data('id');
        showStudentDetail(studentId);
    });
    
    // 编辑按钮
    $(document).on('click', '.edit-btn', function() {
        const studentId = $(this).data('id');
        editStudent(studentId);
    });
    
    // 选课按钮
    $(document).on('click', '.course-btn', function() {
        const studentId = $(this).data('id');
        manageCourses(studentId);
    });
    
    // 删除按钮
    $(document).on('click', '.delete-btn', function() {
        const studentId = $(this).data('id');
        deleteStudent(studentId);
    });
    
    // 创建学生表单提交
    $('#createStudentForm').on('submit', function(e) {
        e.preventDefault();
        createStudent();
    });
    
    // 批量操作
    $('#batchActiveBtn').on('click', function() {
        batchUpdateStatus('ACTIVE');
    });
    
    $('#batchSuspendBtn').on('click', function() {
        batchUpdateStatus('SUSPENDED');
    });
    
    // 导入按钮
    $('#importBtn').on('click', function() {
        importStudents();
    });
    
    // 导出数据
    $('#exportBtn').on('click', function() {
        exportStudents();
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
                { "orderable": false, "targets": [0, 9] }
            ]
        });
    }
}

/**
 * 初始化级联选择
 */
function initCascadeSelects() {
    // 院系变化时更新专业列表
    $('#departmentId').on('change', function() {
        const departmentId = $(this).val();
        loadMajors(departmentId);
    });
    
    // 专业变化时更新班级列表
    $('#majorId').on('change', function() {
        const majorId = $(this).val();
        loadClasses(majorId);
    });
}

/**
 * 加载专业列表
 */
function loadMajors(departmentId) {
    const majorSelect = $('#majorId');
    majorSelect.html('<option value="">选择专业</option>');

    if (!departmentId) return;

    // 根据实际API调整，这里使用学生表单数据接口
    $.ajax({
        url: '/api/v1/students/form-data',
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const majors = response.data.majors || [];
                majors.forEach(major => {
                    majorSelect.append(`<option value="${major}">${major}</option>`);
                });
            }
        },
        error: function() {
            showError('加载专业列表失败');
        }
    });
}

/**
 * 加载班级列表
 */
function loadClasses(majorId) {
    const classSelect = $('#classId');
    classSelect.html('<option value="">选择班级</option>');

    if (!majorId) return;

    // 根据实际API调整，这里使用学生表单数据接口
    $.ajax({
        url: '/api/v1/students/form-data',
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const classes = response.data.classes || [];
                classes.forEach(clazz => {
                    classSelect.append(`<option value="${clazz.id}">${clazz.className}</option>`);
                });
            }
        },
        error: function() {
            showError('加载班级列表失败');
        }
    });
}

/**
 * 搜索学生
 */
function searchStudents() {
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
 * 创建学生
 */
function createStudent() {
    const formData = new FormData($('#createStudentForm')[0]);
    const studentData = Object.fromEntries(formData.entries());
    
    // 显示加载状态
    const submitBtn = $('#createStudentForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);
    
    // 发送请求
    $.ajax({
        url: '/api/v1/students',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(studentData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('学生创建成功');
                $('#createStudentModal').modal('hide');
                $('#createStudentForm')[0].reset();
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
 * 编辑学生
 */
function editStudent(studentId) {
    // 获取学生信息并显示编辑模态框
    $.ajax({
        url: `/api/v1/students/${studentId}`,
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const student = response.data;
                // 填充编辑表单
                showEditModal(student);
            } else {
                showError(response.message || '获取学生信息失败');
            }
        },
        error: function() {
            showError('获取学生信息失败，请稍后重试');
        }
    });
}

/**
 * 删除学生
 */
function deleteStudent(studentId) {
    if (!confirm('确定要删除这个学生吗？此操作不可恢复。')) {
        return;
    }
    
    $.ajax({
        url: `/api/v1/students/${studentId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.code === 200) {
                showSuccess('学生删除成功');
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
 * 显示学生详情
 */
function showStudentDetail(studentId) {
    // 实现学生详情显示逻辑
    window.open(`/admin/students/${studentId}`, '_blank');
}

/**
 * 管理学生选课
 */
function manageCourses(studentId) {
    // 实现选课管理逻辑
    window.open(`/admin/students/${studentId}/courses`, '_blank');
}

/**
 * 批量更新状态
 */
function batchUpdateStatus(status) {
    const selectedIds = $('.row-checkbox:checked').map(function() {
        return $(this).val();
    }).get();
    
    if (selectedIds.length === 0) {
        showWarning('请选择要操作的学生');
        return;
    }
    
    const action = status === 'ACTIVE' ? '激活' : '休学';
    if (!confirm(`确定要${action}选中的 ${selectedIds.length} 个学生吗？`)) {
        return;
    }
    
    $.ajax({
        url: '/api/v1/students/batch-status',
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            studentIds: selectedIds,
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
 * 导入学生
 */
function importStudents() {
    const fileInput = $('#importFile')[0];
    if (!fileInput.files.length) {
        showWarning('请选择要导入的文件');
        return;
    }
    
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);
    
    const importBtn = $('#importBtn');
    const originalText = importBtn.html();
    importBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>导入中...').prop('disabled', true);
    
    $.ajax({
        url: '/api/v1/students/import',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.code === 200) {
                showSuccess(`导入成功，共导入 ${response.data.successCount} 条记录`);
                $('#importStudentModal').modal('hide');
                setTimeout(() => location.reload(), 1000);
            } else {
                showError(response.message || '导入失败');
            }
        },
        error: function(xhr) {
            const response = xhr.responseJSON;
            showError(response?.message || '导入失败，请稍后重试');
        },
        complete: function() {
            importBtn.html(originalText).prop('disabled', false);
        }
    });
}

/**
 * 导出学生数据
 */
function exportStudents() {
    const searchParams = new URLSearchParams($('#searchForm').serialize());
    const exportUrl = '/api/v1/students/export?' + searchParams.toString();
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `students_${new Date().toISOString().split('T')[0]}.xlsx`;
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
    $('#batchActiveBtn, #batchSuspendBtn').toggleClass('disabled', !hasSelected);
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
