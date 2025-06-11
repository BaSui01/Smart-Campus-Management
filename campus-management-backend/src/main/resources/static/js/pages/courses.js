/**
 * 课程管理页面脚本
 */
$(document).ready(function() {
    // 初始化页面
    initCourseManagement();
});

/**
 * 初始化课程管理
 */
function initCourseManagement() {
    // 绑定事件
    bindEvents();
    
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
        searchCourses();
    });
    
    // 重置按钮
    $('#resetBtn').on('click', function() {
        $('#searchForm')[0].reset();
        searchCourses();
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
        const courseId = $(this).data('id');
        showCourseDetail(courseId);
    });
    
    // 编辑按钮
    $(document).on('click', '.edit-btn', function() {
        const courseId = $(this).data('id');
        editCourse(courseId);
    });
    
    // 删除按钮
    $(document).on('click', '.delete-btn', function() {
        const courseId = $(this).data('id');
        deleteCourse(courseId);
    });
    
    // 创建课程表单提交
    $('#createCourseForm').on('submit', function(e) {
        e.preventDefault();
        createCourse();
    });
    
    // 编辑课程表单提交
    $('#editCourseForm').on('submit', function(e) {
        e.preventDefault();
        updateCourse();
    });
    
    // 批量操作
    $('#batchActiveBtn').on('click', function() {
        batchUpdateStatus('ACTIVE');
    });
    
    $('#batchInactiveBtn').on('click', function() {
        batchUpdateStatus('INACTIVE');
    });
    
    // 导出数据
    $('#exportBtn').on('click', function() {
        exportCourses();
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
 * 搜索课程
 */
function searchCourses() {
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
 * 创建课程
 */
function createCourse() {
    const formData = new FormData($('#createCourseForm')[0]);
    const courseData = Object.fromEntries(formData.entries());

    // 转换数据类型
    if (courseData.credits) {
        courseData.credits = parseInt(courseData.credits);
    }
    if (courseData.maxStudents) {
        courseData.maxStudents = parseInt(courseData.maxStudents);
    }
    courseData.status = courseData.isActive ? 1 : 0;
    delete courseData.isActive;

    // 显示加载状态
    const submitBtn = $('#createCourseForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);

    // 发送请求
    $.ajax({
        url: '/api/v1/courses',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(courseData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('课程创建成功');
                $('#createCourseModal').modal('hide');
                $('#createCourseForm')[0].reset();
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
 * 编辑课程
 */
function editCourse(courseId) {
    // 获取课程信息
    $.ajax({
        url: `/api/v1/courses/${courseId}`,
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const course = response.data;
                
                // 填充表单
                $('#editCourseId').val(course.id);
                $('#editCourseCode').val(course.courseCode);
                $('#editCourseName').val(course.courseName);
                // 填充其他字段...
                
                // 显示模态框
                $('#editCourseModal').modal('show');
            } else {
                showError(response.message || '获取课程信息失败');
            }
        },
        error: function() {
            showError('获取课程信息失败，请稍后重试');
        }
    });
}

/**
 * 更新课程
 */
function updateCourse() {
    const formData = new FormData($('#editCourseForm')[0]);
    const courseData = Object.fromEntries(formData.entries());
    const courseId = courseData.id;
    
    // 显示加载状态
    const submitBtn = $('#editCourseForm button[type="submit"]');
    const originalText = submitBtn.html();
    submitBtn.html('<i class="fas fa-spinner fa-spin me-1"></i>保存中...').prop('disabled', true);
    
    // 发送请求
    $.ajax({
        url: `/api/v1/courses/${courseId}`,
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(courseData),
        success: function(response) {
            if (response.code === 200) {
                showSuccess('课程更新成功');
                $('#editCourseModal').modal('hide');
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
 * 删除课程
 */
function deleteCourse(courseId) {
    if (!confirm('确定要删除这个课程吗？此操作不可恢复。')) {
        return;
    }
    
    $.ajax({
        url: `/api/v1/courses/${courseId}`,
        method: 'DELETE',
        success: function(response) {
            if (response.code === 200) {
                showSuccess('课程删除成功');
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
 * 显示课程详情
 */
function showCourseDetail(courseId) {
    $('#courseDetailContent').html('<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 加载中...</div>');
    $('#courseDetailModal').modal('show');
    
    $.ajax({
        url: `/api/v1/courses/${courseId}`,
        method: 'GET',
        success: function(response) {
            if (response.code === 200) {
                const course = response.data;
                renderCourseDetail(course);
            } else {
                $('#courseDetailContent').html('<div class="text-center text-danger">加载失败</div>');
            }
        },
        error: function() {
            $('#courseDetailContent').html('<div class="text-center text-danger">加载失败</div>');
        }
    });
}

/**
 * 渲染课程详情
 */
function renderCourseDetail(course) {
    const html = `
        <div class="row">
            <div class="col-md-6">
                <table class="table table-borderless">
                    <tr>
                        <td><strong>课程代码:</strong></td>
                        <td>${course.courseCode}</td>
                    </tr>
                    <tr>
                        <td><strong>课程名称:</strong></td>
                        <td>${course.courseName}</td>
                    </tr>
                    <tr>
                        <td><strong>所属院系:</strong></td>
                        <td>${course.departmentName || '-'}</td>
                    </tr>
                    <tr>
                        <td><strong>学分:</strong></td>
                        <td>${course.credits}</td>
                    </tr>
                    <tr>
                        <td><strong>课程类型:</strong></td>
                        <td>${getCourseTypeText(course.courseType)}</td>
                    </tr>
                </table>
            </div>
            <div class="col-md-6">
                <table class="table table-borderless">
                    <tr>
                        <td><strong>授课教师:</strong></td>
                        <td>${course.teacherName || '未分配'}</td>
                    </tr>
                    <tr>
                        <td><strong>最大学生数:</strong></td>
                        <td>${course.maxStudents || '-'}</td>
                    </tr>
                    <tr>
                        <td><strong>当前学生数:</strong></td>
                        <td>${course.studentCount || 0}</td>
                    </tr>
                    <tr>
                        <td><strong>开设学期:</strong></td>
                        <td>${getSemesterText(course.semester)}</td>
                    </tr>
                    <tr>
                        <td><strong>状态:</strong></td>
                        <td>${getStatusText(course.status)}</td>
                    </tr>
                </table>
            </div>
        </div>
        ${course.description ? `
        <div class="row">
            <div class="col-12">
                <h6>课程描述:</h6>
                <p class="text-muted">${course.description}</p>
            </div>
        </div>
        ` : ''}
    `;
    
    $('#courseDetailContent').html(html);
}

/**
 * 批量更新状态
 */
function batchUpdateStatus(status) {
    const selectedIds = $('.row-checkbox:checked').map(function() {
        return $(this).val();
    }).get();
    
    if (selectedIds.length === 0) {
        showWarning('请选择要操作的课程');
        return;
    }
    
    const action = status === 'ACTIVE' ? '开设' : '暂停';
    if (!confirm(`确定要${action}选中的 ${selectedIds.length} 个课程吗？`)) {
        return;
    }
    
    $.ajax({
        url: '/api/v1/courses/batch-status',
        method: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            courseIds: selectedIds,
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
 * 导出课程数据
 */
function exportCourses() {
    const searchParams = new URLSearchParams($('#searchForm').serialize());
    const exportUrl = '/api/v1/courses/export?' + searchParams.toString();
    
    // 创建下载链接
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `courses_${new Date().toISOString().split('T')[0]}.xlsx`;
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
    $('#batchActiveBtn, #batchInactiveBtn').toggleClass('disabled', !hasSelected);
}

/**
 * 获取课程类型文本
 */
function getCourseTypeText(type) {
    const types = {
        'REQUIRED': '必修课',
        'ELECTIVE': '选修课',
        'PUBLIC': '公共课'
    };
    return types[type] || type;
}

/**
 * 获取学期文本
 */
function getSemesterText(semester) {
    const semesters = {
        'SPRING': '春季学期',
        'SUMMER': '夏季学期',
        'FALL': '秋季学期',
        'WINTER': '冬季学期'
    };
    return semesters[semester] || semester;
}

/**
 * 获取状态文本
 */
function getStatusText(status) {
    const statuses = {
        'ACTIVE': '<span class="badge bg-success">开设中</span>',
        'INACTIVE': '<span class="badge bg-secondary">未开设</span>',
        'SUSPENDED': '<span class="badge bg-warning">暂停</span>'
    };
    return statuses[status] || status;
}

/**
 * 显示成功消息
 */
function showSuccess(message) {
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
