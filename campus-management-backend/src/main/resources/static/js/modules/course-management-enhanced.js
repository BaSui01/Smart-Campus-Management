/**
 * 课程管理增强模块
 * 基于CourseApiController实现完整的课程CRUD操作
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */

window.CourseManagementEnhanced = (function() {
    'use strict';

    // 模块配置
    const CONFIG = {
        API_BASE: '/api/v1/courses',
        TABLE_ID: '#coursesTable',
        MODAL_ID: '#createCourseModal',
        FORM_ID: '#createCourseForm',
        BATCH_LIMIT: 100
    };

    // 模块状态
    let dataTable = null;
    let selectedCourses = new Set();

    /**
     * 初始化模块
     */
    function init() {
        console.log('初始化课程管理模块...');
        
        // 初始化数据表格
        initDataTable();
        
        // 绑定事件监听器
        bindEventListeners();
        
        // 加载统计数据
        loadStatistics();
        
        // 加载教师列表
        loadTeachers();
        
        console.log('课程管理模块初始化完成');
    }

    /**
     * 初始化DataTables
     */
    function initDataTable() {
        if ($.fn.DataTable.isDataTable(CONFIG.TABLE_ID)) {
            $(CONFIG.TABLE_ID).DataTable().destroy();
        }

        dataTable = $(CONFIG.TABLE_ID).DataTable({
            processing: true,
            serverSide: true,
            responsive: true,
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.13.6/i18n/zh.json'
            },
            ajax: {
                url: CONFIG.API_BASE,
                type: 'GET',
                data: function(d) {
                    // 添加搜索参数
                    d.keyword = $('#searchKeyword').val();
                    d.courseType = $('#courseTypeFilter').val();
                    d.status = $('#statusFilter').val();
                    d.semester = $('#semesterFilter').val();
                    
                    // 转换DataTables参数为后端分页参数
                    d.page = Math.floor(d.start / d.length) + 1;
                    d.size = d.length;
                    
                    return d;
                },
                dataSrc: function(json) {
                    // 转换后端响应为DataTables格式
                    json.recordsTotal = json.data?.total || 0;
                    json.recordsFiltered = json.data?.total || 0;
                    return json.data?.content || [];
                },
                error: function(xhr, error, thrown) {
                    console.error('加载课程数据失败:', error);
                    showAlert('加载课程数据失败: ' + (xhr.responseJSON?.message || error), 'danger');
                }
            },
            columns: [
                {
                    data: null,
                    orderable: false,
                    className: 'text-center',
                    width: '40px',
                    render: function(data, type, row) {
                        return `<input type="checkbox" class="form-check-input course-checkbox" value="${row.id}">`;
                    }
                },
                {
                    data: 'courseCode',
                    title: '课程编号',
                    render: function(data, type, row) {
                        return `<span class="fw-bold text-primary">${data}</span>`;
                    }
                },
                {
                    data: 'courseName',
                    title: '课程名称',
                    render: function(data, type, row) {
                        return `<span class="fw-bold">${data}</span>`;
                    }
                },
                {
                    data: 'courseType',
                    title: '课程类型',
                    render: function(data, type, row) {
                        const typeColors = {
                            '必修': 'bg-danger',
                            '选修': 'bg-primary',
                            '实践': 'bg-success',
                            '通识': 'bg-info'
                        };
                        const colorClass = typeColors[data] || 'bg-secondary';
                        return `<span class="badge ${colorClass} course-type-badge">${data}</span>`;
                    }
                },
                {
                    data: 'credits',
                    title: '学分',
                    className: 'text-center',
                    render: function(data, type, row) {
                        return `<span class="badge credit-badge">${data}</span>`;
                    }
                },
                {
                    data: 'hours',
                    title: '学时',
                    className: 'text-center',
                    render: function(data, type, row) {
                        return `${data}学时`;
                    }
                },
                {
                    data: 'teacherName',
                    title: '授课教师',
                    render: function(data, type, row) {
                        return data || '未分配';
                    }
                },
                {
                    data: 'enrollmentCount',
                    title: '选课人数',
                    className: 'text-center',
                    render: function(data, type, row) {
                        const count = data || 0;
                        const max = row.maxStudents || 0;
                        const percentage = max > 0 ? (count / max * 100).toFixed(1) : 0;
                        
                        let colorClass = 'text-success';
                        if (percentage > 80) colorClass = 'text-danger';
                        else if (percentage > 60) colorClass = 'text-warning';
                        
                        return `<span class="${colorClass}">${count}/${max}</span>`;
                    }
                },
                {
                    data: 'status',
                    title: '状态',
                    className: 'text-center',
                    render: function(data, type, row) {
                        const statusClass = data === 1 ? 'bg-success' : 'bg-danger';
                        const statusText = data === 1 ? '启用' : '禁用';
                        return `<span class="badge ${statusClass} course-status-badge">${statusText}</span>`;
                    }
                },
                {
                    data: 'createdAt',
                    title: '创建时间',
                    render: function(data, type, row) {
                        return data ? new Date(data).toLocaleString('zh-CN') : '-';
                    }
                },
                {
                    data: null,
                    title: '操作',
                    orderable: false,
                    className: 'text-center',
                    width: '200px',
                    render: function(data, type, row) {
                        return `
                            <div class="btn-group btn-group-sm" role="group">
                                <button type="button" class="btn btn-outline-info" onclick="CourseManagementEnhanced.viewCourse(${row.id})" title="查看详情">
                                    <i class="fas fa-eye"></i>
                                </button>
                                <button type="button" class="btn btn-outline-primary" onclick="CourseManagementEnhanced.editCourse(${row.id})" title="编辑">
                                    <i class="fas fa-edit"></i>
                                </button>
                                <button type="button" class="btn btn-outline-success" onclick="CourseManagementEnhanced.manageSchedule(${row.id})" title="课程安排">
                                    <i class="fas fa-calendar-alt"></i>
                                </button>
                                <button type="button" class="btn btn-outline-warning" onclick="CourseManagementEnhanced.toggleStatus(${row.id})" title="${row.status === 1 ? '禁用' : '启用'}">
                                    <i class="fas ${row.status === 1 ? 'fa-ban' : 'fa-check'}"></i>
                                </button>
                                <button type="button" class="btn btn-outline-danger" onclick="CourseManagementEnhanced.deleteCourse(${row.id})" title="删除">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </div>
                        `;
                    }
                }
            ],
            order: [[9, 'desc']], // 按创建时间降序排列
            pageLength: 10,
            lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
            dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rtip',
            drawCallback: function() {
                // 重新绑定复选框事件
                bindCheckboxEvents();
            }
        });
    }

    /**
     * 绑定事件监听器
     */
    function bindEventListeners() {
        // 全选复选框
        $('#selectAll').on('change', function() {
            const isChecked = this.checked;
            $('.course-checkbox').prop('checked', isChecked);
            updateSelectedCourses();
        });

        // 搜索按钮
        $('#searchKeyword, #courseTypeFilter, #statusFilter, #semesterFilter').on('change keyup', function() {
            if (dataTable) {
                dataTable.ajax.reload();
            }
        });

        // 刷新按钮
        $(document).on('click', '[onclick="refreshCourseData()"]', function() {
            refreshData();
        });

        // 导出按钮
        $(document).on('click', '[onclick="exportCourses()"]', function() {
            exportCourses();
        });

        // 创建课程按钮
        $(document).on('click', '[onclick="showCreateCourseModal()"]', function() {
            showCreateCourseModal();
        });

        // 批量操作按钮
        $(document).on('click', '[onclick="batchEnable()"]', function() {
            batchOperation('enable');
        });

        $(document).on('click', '[onclick="batchDisable()"]', function() {
            batchOperation('disable');
        });

        $(document).on('click', '[onclick="batchDelete()"]', function() {
            batchOperation('delete');
        });

        // 表单验证
        $(CONFIG.FORM_ID).on('submit', function(e) {
            e.preventDefault();
            createCourse();
        });
    }

    /**
     * 绑定复选框事件
     */
    function bindCheckboxEvents() {
        $('.course-checkbox').off('change').on('change', function() {
            updateSelectedCourses();
        });
    }

    /**
     * 更新选中的课程
     */
    function updateSelectedCourses() {
        selectedCourses.clear();
        $('.course-checkbox:checked').each(function() {
            selectedCourses.add(parseInt(this.value));
        });

        const count = selectedCourses.size;
        $('#selectedCount').text(count);
        
        if (count > 0) {
            $('#batchActions').show();
        } else {
            $('#batchActions').hide();
        }

        // 更新全选复选框状态
        const totalCheckboxes = $('.course-checkbox').length;
        const checkedCheckboxes = $('.course-checkbox:checked').length;
        
        $('#selectAll').prop('indeterminate', checkedCheckboxes > 0 && checkedCheckboxes < totalCheckboxes);
        $('#selectAll').prop('checked', checkedCheckboxes === totalCheckboxes && totalCheckboxes > 0);
    }

    /**
     * 加载统计数据
     */
    function loadStatistics() {
        ApiClient.get(CONFIG.API_BASE + '/stats')
            .then(response => {
                if (response.success && response.data) {
                    const stats = response.data;
                    $('#totalCourses').text(stats.total || 0);
                    $('#activeCourses').text(stats.active || 0);
                    $('#totalEnrollments').text(stats.totalEnrollments || 0);
                    $('#newCourses').text(stats.newThisSemester || 0);
                }
            })
            .catch(error => {
                console.error('加载统计数据失败:', error);
            });
    }

    /**
     * 加载教师列表
     */
    function loadTeachers() {
        ApiClient.get('/api/v1/teachers')
            .then(response => {
                if (response.success && response.data) {
                    const teacherSelect = $('#teacherId');
                    teacherSelect.empty().append('<option value="">请选择教师</option>');
                    
                    response.data.forEach(teacher => {
                        teacherSelect.append(`<option value="${teacher.id}">${teacher.realName || teacher.username}</option>`);
                    });
                }
            })
            .catch(error => {
                console.error('加载教师列表失败:', error);
            });
    }

    /**
     * 刷新数据
     */
    function refreshData() {
        if (dataTable) {
            dataTable.ajax.reload();
        }
        loadStatistics();
        showAlert('数据已刷新', 'success');
    }

    /**
     * 显示创建课程模态框
     */
    function showCreateCourseModal() {
        $(CONFIG.FORM_ID)[0].reset();
        $(CONFIG.FORM_ID).removeClass('was-validated');
        $(CONFIG.MODAL_ID).modal('show');
    }

    /**
     * 创建课程
     */
    function createCourse() {
        const form = $(CONFIG.FORM_ID)[0];
        
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        const formData = new FormData(form);
        const courseData = Object.fromEntries(formData.entries());

        ApiClient.post(CONFIG.API_BASE, courseData)
            .then(response => {
                if (response.success) {
                    $(CONFIG.MODAL_ID).modal('hide');
                    showAlert('课程创建成功', 'success');
                    refreshData();
                } else {
                    showAlert('创建课程失败: ' + response.message, 'danger');
                }
            })
            .catch(error => {
                console.error('创建课程失败:', error);
                showAlert('创建课程失败: ' + error.message, 'danger');
            });
    }

    /**
     * 查看课程详情
     */
    function viewCourse(courseId) {
        // TODO: 实现课程详情查看
        showAlert('课程详情功能开发中...', 'info');
    }

    /**
     * 编辑课程
     */
    function editCourse(courseId) {
        // TODO: 实现课程编辑功能
        showAlert('课程编辑功能开发中...', 'info');
    }

    /**
     * 管理课程安排
     */
    function manageSchedule(courseId) {
        // TODO: 实现课程安排管理
        showAlert('课程安排管理功能开发中...', 'info');
    }

    /**
     * 切换课程状态
     */
    function toggleStatus(courseId) {
        if (!confirm('确定要切换课程状态吗？')) {
            return;
        }

        ApiClient.post(`${CONFIG.API_BASE}/${courseId}/toggle-status`)
            .then(response => {
                if (response.success) {
                    showAlert('课程状态切换成功', 'success');
                    refreshData();
                } else {
                    showAlert('状态切换失败: ' + response.message, 'danger');
                }
            })
            .catch(error => {
                console.error('切换课程状态失败:', error);
                showAlert('状态切换失败: ' + error.message, 'danger');
            });
    }

    /**
     * 删除课程
     */
    function deleteCourse(courseId) {
        if (!confirm('确定要删除这门课程吗？此操作不可恢复！')) {
            return;
        }

        ApiClient.delete(`${CONFIG.API_BASE}/${courseId}`)
            .then(response => {
                if (response.success) {
                    showAlert('课程删除成功', 'success');
                    refreshData();
                } else {
                    showAlert('删除课程失败: ' + response.message, 'danger');
                }
            })
            .catch(error => {
                console.error('删除课程失败:', error);
                showAlert('删除课程失败: ' + error.message, 'danger');
            });
    }

    /**
     * 批量操作
     */
    function batchOperation(operation) {
        if (selectedCourses.size === 0) {
            showAlert('请先选择要操作的课程', 'warning');
            return;
        }

        if (selectedCourses.size > CONFIG.BATCH_LIMIT) {
            showAlert(`批量操作最多支持${CONFIG.BATCH_LIMIT}门课程`, 'warning');
            return;
        }

        const courseIds = Array.from(selectedCourses);
        let confirmMessage = '';
        let apiUrl = '';

        switch (operation) {
            case 'enable':
                confirmMessage = `确定要启用选中的 ${courseIds.length} 门课程吗？`;
                apiUrl = `${CONFIG.API_BASE}/batch/enable`;
                break;
            case 'disable':
                confirmMessage = `确定要禁用选中的 ${courseIds.length} 门课程吗？`;
                apiUrl = `${CONFIG.API_BASE}/batch/disable`;
                break;
            case 'delete':
                confirmMessage = `确定要删除选中的 ${courseIds.length} 门课程吗？此操作不可恢复！`;
                apiUrl = `${CONFIG.API_BASE}/batch/delete`;
                break;
            default:
                showAlert('未知的批量操作', 'danger');
                return;
        }

        if (!confirm(confirmMessage)) {
            return;
        }

        ApiClient.post(apiUrl, { courseIds })
            .then(response => {
                if (response.success) {
                    showAlert(`批量${operation === 'enable' ? '启用' : operation === 'disable' ? '禁用' : '删除'}成功`, 'success');
                    selectedCourses.clear();
                    $('#batchActions').hide();
                    refreshData();
                } else {
                    showAlert(`批量操作失败: ${response.message}`, 'danger');
                }
            })
            .catch(error => {
                console.error('批量操作失败:', error);
                showAlert(`批量操作失败: ${error.message}`, 'danger');
            });
    }

    /**
     * 导出课程数据
     */
    function exportCourses() {
        const params = new URLSearchParams({
            keyword: $('#searchKeyword').val() || '',
            courseType: $('#courseTypeFilter').val() || '',
            status: $('#statusFilter').val() || '',
            semester: $('#semesterFilter').val() || ''
        });

        window.open(`${CONFIG.API_BASE}/export?${params.toString()}`, '_blank');
        showAlert('正在导出课程数据...', 'info');
    }

    /**
     * 显示提示消息
     */
    function showAlert(message, type = 'info') {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        
        $('#alertContainer').html(alertHtml);
        
        // 自动隐藏成功和信息提示
        if (type === 'success' || type === 'info') {
            setTimeout(() => {
                $('#alertContainer .alert').alert('close');
            }, 3000);
        }
    }

    // 公开API
    return {
        init,
        viewCourse,
        editCourse,
        manageSchedule,
        toggleStatus,
        deleteCourse,
        refreshData,
        showCreateCourseModal,
        createCourse
    };

})();

// 全局函数（用于HTML onclick事件）
function refreshCourseData() {
    CourseManagementEnhanced.refreshData();
}

function exportCourses() {
    CourseManagementEnhanced.exportCourses();
}

function showCreateCourseModal() {
    CourseManagementEnhanced.showCreateCourseModal();
}

function applyFilters() {
    if (window.CourseManagementEnhanced && window.CourseManagementEnhanced.dataTable) {
        window.CourseManagementEnhanced.dataTable.ajax.reload();
    }
}

function resetFilters() {
    $('#searchKeyword').val('');
    $('#courseTypeFilter').val('');
    $('#statusFilter').val('');
    $('#semesterFilter').val('');
    applyFilters();
}

function batchEnable() {
    CourseManagementEnhanced.batchOperation('enable');
}

function batchDisable() {
    CourseManagementEnhanced.batchOperation('disable');
}

function batchDelete() {
    CourseManagementEnhanced.batchOperation('delete');
}

function clearSelection() {
    $('.course-checkbox').prop('checked', false);
    $('#selectAll').prop('checked', false);
    CourseManagementEnhanced.updateSelectedCourses();
}
