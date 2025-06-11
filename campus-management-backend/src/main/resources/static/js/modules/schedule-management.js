/**
 * 课程安排管理模块
 * 提供课程安排的增删改查功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */

class ScheduleManagement {
    constructor() {
        this.currentPage = 1;
        this.pageSize = 10;
        this.formData = {};
        this.isEditing = false;
        this.currentScheduleId = null;
        
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadFormData();
        this.loadSchedules();
        this.loadStatistics();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 添加课程安排按钮
        document.getElementById('addScheduleBtn').addEventListener('click', () => {
            this.showAddModal();
        });

        // 保存课程安排按钮
        document.getElementById('saveScheduleBtn').addEventListener('click', () => {
            this.saveSchedule();
        });

        // 搜索表单
        document.getElementById('searchForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.currentPage = 1;
            this.loadSchedules();
        });

        // 重置按钮
        document.getElementById('resetBtn').addEventListener('click', () => {
            this.resetSearch();
        });

        // 自动排课按钮
        document.getElementById('autoScheduleBtn').addEventListener('click', () => {
            this.showAutoScheduleModal();
        });

        // 导出按钮
        document.getElementById('exportBtn').addEventListener('click', () => {
            this.exportSchedules();
        });

        // 全选复选框
        document.getElementById('selectAll').addEventListener('change', (e) => {
            this.toggleSelectAll(e.target.checked);
        });

        // 模态框重置
        document.getElementById('scheduleModal').addEventListener('hidden.bs.modal', () => {
            this.resetForm();
        });
    }

    /**
     * 加载表单数据
     */
    async loadFormData() {
        try {
            const response = await apiClient.get('/api/v1/schedules/form-data');
            if (response.success) {
                this.formData = response.data;
                this.populateFormOptions();
            }
        } catch (error) {
            console.error('加载表单数据失败:', error);
            showAlert('加载表单数据失败', 'error');
        }
    }

    /**
     * 填充表单选项
     */
    populateFormOptions() {
        // 填充课程选项
        const courseSelects = document.querySelectorAll('select[name="courseId"]');
        courseSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择课程</option>';
            if (this.formData.courses) {
                this.formData.courses.forEach(course => {
                    select.innerHTML += `<option value="${course.id}">${course.courseName} (${course.courseCode})</option>`;
                });
            }
        });

        // 填充班级选项
        const classSelects = document.querySelectorAll('select[name="classId"]');
        classSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择班级</option>';
            if (this.formData.classes) {
                this.formData.classes.forEach(schoolClass => {
                    select.innerHTML += `<option value="${schoolClass.id}">${schoolClass.className}</option>`;
                });
            }
        });

        // 填充星期选项
        const daySelects = document.querySelectorAll('select[name="dayOfWeek"]');
        daySelects.forEach(select => {
            select.innerHTML = '<option value="">请选择星期</option>';
            if (this.formData.dayOfWeekOptions) {
                Object.entries(this.formData.dayOfWeekOptions).forEach(([value, text]) => {
                    select.innerHTML += `<option value="${value}">${text}</option>`;
                });
            }
        });

        // 填充教室选项
        const classroomSelects = document.querySelectorAll('select[name="classroom"]');
        classroomSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择教室</option>';
            if (this.formData.classrooms) {
                this.formData.classrooms.forEach(classroom => {
                    select.innerHTML += `<option value="${classroom}">${classroom}</option>`;
                });
            }
        });

        // 填充学期选项
        const semesterSelects = document.querySelectorAll('select[name="semester"]');
        semesterSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择学期</option>';
            if (this.formData.semesters) {
                this.formData.semesters.forEach(semester => {
                    select.innerHTML += `<option value="${semester}">${semester}</option>`;
                });
            }
        });
    }

    /**
     * 加载课程安排列表
     */
    async loadSchedules() {
        try {
            showLoading('正在加载课程安排列表...');
            
            const formData = new FormData(document.getElementById('searchForm'));
            const params = {
                page: this.currentPage,
                size: this.pageSize
            };

            // 添加搜索参数
            for (let [key, value] of formData.entries()) {
                if (value.trim()) {
                    params[key] = value.trim();
                }
            }

            const response = await apiClient.get('/api/v1/schedules', params);
            
            if (response.success) {
                this.renderScheduleTable(response.data.content);
                this.renderPagination(response.data);
            } else {
                throw new Error(response.message || '加载课程安排列表失败');
            }
        } catch (error) {
            console.error('加载课程安排列表失败:', error);
            showAlert('加载课程安排列表失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 渲染课程安排表格
     */
    renderScheduleTable(schedules) {
        const tbody = document.querySelector('#scheduleTable tbody');
        
        if (!schedules || schedules.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" class="text-center py-4">
                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                        <p class="text-muted">暂无课程安排数据</p>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = schedules.map(schedule => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input row-checkbox" type="checkbox" value="${schedule.id}">
                    </div>
                </td>
                <td>
                    <div>
                        <strong>${schedule.courseName || '未知课程'}</strong>
                        <br>
                        <small class="text-muted">${schedule.courseCode || '无编码'}</small>
                    </div>
                </td>
                <td>${schedule.className || '未分配'}</td>
                <td>${schedule.teacherName || '未分配'}</td>
                <td>${this.getDayOfWeekText(schedule.dayOfWeek)}</td>
                <td>${this.formatTime(schedule.startTime)} - ${this.formatTime(schedule.endTime)}</td>
                <td>
                    <span class="badge bg-info">${schedule.classroom || '未分配'}</span>
                </td>
                <td>${schedule.semester || '未设置'}</td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button type="button" class="btn btn-outline-primary" onclick="scheduleManagement.editSchedule(${schedule.id})" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger" onclick="scheduleManagement.deleteSchedule(${schedule.id})" title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    /**
     * 渲染分页
     */
    renderPagination(pageData) {
        const pagination = document.getElementById('pagination');
        const { currentPage, totalPages, hasPrevious, hasNext } = pageData;

        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHtml = '';

        // 上一页
        paginationHtml += `
            <li class="page-item ${!hasPrevious ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="scheduleManagement.changePage(${currentPage - 1})">上一页</a>
            </li>
        `;

        // 页码
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="scheduleManagement.changePage(${i})">${i}</a>
                </li>
            `;
        }

        // 下一页
        paginationHtml += `
            <li class="page-item ${!hasNext ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="scheduleManagement.changePage(${currentPage + 1})">下一页</a>
            </li>
        `;

        pagination.innerHTML = paginationHtml;
    }

    /**
     * 切换页面
     */
    changePage(page) {
        if (page < 1) return;
        this.currentPage = page;
        this.loadSchedules();
    }

    /**
     * 加载统计数据
     */
    async loadStatistics() {
        try {
            const response = await apiClient.get('/api/v1/schedules/stats');
            if (response.success) {
                const stats = response.data;
                document.getElementById('totalSchedules').textContent = stats.totalSchedules || 0;
                document.getElementById('todaySchedules').textContent = stats.todaySchedules || 0;
                document.getElementById('weekSchedules').textContent = stats.weekSchedules || 0;
                document.getElementById('conflictSchedules').textContent = stats.conflictSchedules || 0;
            }
        } catch (error) {
            console.error('加载统计数据失败:', error);
        }
    }

    /**
     * 显示添加模态框
     */
    showAddModal() {
        this.isEditing = false;
        this.currentScheduleId = null;
        document.getElementById('scheduleModalLabel').innerHTML = '<i class="fas fa-plus me-2"></i>添加课程安排';
        this.resetForm();
        new bootstrap.Modal(document.getElementById('scheduleModal')).show();
    }

    /**
     * 编辑课程安排
     */
    async editSchedule(scheduleId) {
        try {
            showLoading('正在加载课程安排信息...');
            
            const response = await apiClient.get(`/api/v1/schedules/${scheduleId}`);
            if (response.success) {
                this.isEditing = true;
                this.currentScheduleId = scheduleId;
                document.getElementById('scheduleModalLabel').innerHTML = '<i class="fas fa-edit me-2"></i>编辑课程安排';
                
                // 填充表单数据
                const scheduleData = response.data;
                document.getElementById('scheduleId').value = scheduleData.id || '';
                document.getElementById('courseId').value = scheduleData.courseId || '';
                document.getElementById('classId').value = scheduleData.classId || '';
                document.getElementById('dayOfWeek').value = scheduleData.dayOfWeek || '';
                document.getElementById('startTime').value = scheduleData.startTime || '';
                document.getElementById('endTime').value = scheduleData.endTime || '';
                document.getElementById('classroom').value = scheduleData.classroom || '';
                document.getElementById('semester').value = scheduleData.semester || '';
                
                new bootstrap.Modal(document.getElementById('scheduleModal')).show();
            } else {
                throw new Error(response.message || '加载课程安排信息失败');
            }
        } catch (error) {
            console.error('加载课程安排信息失败:', error);
            showAlert('加载课程安排信息失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 保存课程安排
     */
    async saveSchedule() {
        const form = document.getElementById('scheduleForm');
        
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        try {
            showLoading(this.isEditing ? '正在更新课程安排...' : '正在创建课程安排...');
            
            const formData = new FormData(form);
            const scheduleData = {
                courseId: parseInt(formData.get('courseId')),
                classId: formData.get('classId') ? parseInt(formData.get('classId')) : null,
                dayOfWeek: parseInt(formData.get('dayOfWeek')),
                startTime: formData.get('startTime'),
                endTime: formData.get('endTime'),
                classroom: formData.get('classroom'),
                semester: formData.get('semester')
            };

            let response;
            if (this.isEditing) {
                response = await apiClient.put(`/api/v1/schedules/${this.currentScheduleId}`, scheduleData);
            } else {
                response = await apiClient.post('/api/v1/schedules', scheduleData);
            }

            if (response.success) {
                showAlert(this.isEditing ? '课程安排更新成功' : '课程安排创建成功', 'success');
                bootstrap.Modal.getInstance(document.getElementById('scheduleModal')).hide();
                this.loadSchedules();
                this.loadStatistics();
            } else {
                throw new Error(response.message || '保存课程安排失败');
            }
        } catch (error) {
            console.error('保存课程安排失败:', error);
            showAlert('保存课程安排失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 删除课程安排
     */
    async deleteSchedule(scheduleId) {
        if (!confirm('确定要删除这个课程安排吗？此操作不可恢复。')) {
            return;
        }

        try {
            showLoading('正在删除课程安排...');
            
            const response = await apiClient.delete(`/api/v1/schedules/${scheduleId}`);
            if (response.success) {
                showAlert('课程安排删除成功', 'success');
                this.loadSchedules();
                this.loadStatistics();
            } else {
                throw new Error(response.message || '删除课程安排失败');
            }
        } catch (error) {
            console.error('删除课程安排失败:', error);
            showAlert('删除课程安排失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 显示自动排课模态框
     */
    showAutoScheduleModal() {
        // 这里可以实现自动排课的界面
        showAlert('自动排课功能开发中...', 'info');
    }

    /**
     * 重置搜索
     */
    resetSearch() {
        document.getElementById('searchForm').reset();
        this.currentPage = 1;
        this.loadSchedules();
    }

    /**
     * 重置表单
     */
    resetForm() {
        const form = document.getElementById('scheduleForm');
        form.reset();
        form.classList.remove('was-validated');
        document.getElementById('scheduleId').value = '';
    }

    /**
     * 全选/取消全选
     */
    toggleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.row-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
        });
    }

    /**
     * 导出课程安排数据
     */
    async exportSchedules() {
        try {
            showLoading('正在导出数据...');
            showAlert('导出功能开发中...', 'info');
        } catch (error) {
            console.error('导出失败:', error);
            showAlert('导出失败: ' + error.message, 'error');
        } finally {
            hideLoading();
        }
    }

    /**
     * 获取星期几文本
     */
    getDayOfWeekText(dayOfWeek) {
        const dayTexts = {
            1: '周一', 2: '周二', 3: '周三', 4: '周四', 
            5: '周五', 6: '周六', 7: '周日'
        };
        return dayTexts[dayOfWeek] || '未知';
    }

    /**
     * 格式化时间
     */
    formatTime(timeString) {
        if (!timeString) return '未设置';
        return timeString.substring(0, 5); // 只显示 HH:mm
    }

    /**
     * 格式化日期
     */
    formatDate(dateString) {
        if (!dateString) return '未知';
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    window.scheduleManagement = new ScheduleManagement();
});
