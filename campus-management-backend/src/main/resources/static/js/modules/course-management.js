/**
 * 课程管理模块
 */
class CourseManagement {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 20;
        this.searchParams = {};
        this.formValidator = null;
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadCourses();
        this.initFormValidator();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 搜索表单提交
        const searchForm = document.getElementById('searchForm');
        if (searchForm) {
            searchForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleSearch();
            });
        }

        // 重置搜索
        const resetBtn = document.getElementById('resetBtn');
        if (resetBtn) {
            resetBtn.addEventListener('click', () => this.resetSearch());
        }

        // 添加课程按钮
        const addCourseBtn = document.getElementById('addCourseBtn');
        if (addCourseBtn) {
            addCourseBtn.addEventListener('click', () => this.showAddCourseModal());
        }

        // 保存课程按钮
        const saveCourseBtn = document.getElementById('saveCourseBtn');
        if (saveCourseBtn) {
            saveCourseBtn.addEventListener('click', () => this.saveCourse());
        }

        // 分页按钮
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('page-link')) {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                if (!isNaN(page)) {
                    this.loadCourses(page);
                }
            }
        });

        // 操作按钮
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('edit-course-btn')) {
                const courseId = e.target.dataset.courseId;
                this.editCourse(courseId);
            } else if (e.target.classList.contains('delete-course-btn')) {
                const courseId = e.target.dataset.courseId;
                this.deleteCourse(courseId);
            } else if (e.target.classList.contains('toggle-status-btn')) {
                const courseId = e.target.dataset.courseId;
                this.toggleCourseStatus(courseId);
            }
        });
    }

    /**
     * 初始化表单验证器
     */
    initFormValidator() {
        const courseForm = document.getElementById('courseForm');
        if (courseForm) {
            this.formValidator = new FormValidator(courseForm);
            this.formValidator
                .addRule('courseName', { required: true, maxLength: 100 }, '课程名称不能为空且不超过100个字符')
                .addRule('courseCode', { required: true, maxLength: 20, pattern: /^[A-Z0-9]+$/ }, '课程代码不能为空，只能包含大写字母和数字')
                .addRule('credits', { required: true, custom: (value) => {
                    const credits = parseFloat(value);
                    return (credits > 0 && credits <= 10) || '学分必须在0-10之间';
                }}, '学分格式不正确')
                .addRule('courseType', { required: true }, '请选择课程类型')
                .addRule('semester', { required: true }, '请选择学期');
        }
    }

    /**
     * 加载课程列表
     */
    async loadCourses(page = 0) {
        try {
            LoadingManager.showPageLoading('#courseTableContainer');
            
            const params = {
                page: page,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/courses', params);
            
            if (response.success) {
                this.renderCourseTable(response.data.courses);
                this.renderPagination(response.data.courses);
                this.updateStatistics(response.data.stats);
                this.currentPage = page;
            } else {
                MessageUtils.error('加载课程列表失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('加载课程列表失败：' + error.message);
        } finally {
            LoadingManager.hidePageLoading('#courseTableContainer');
        }
    }

    /**
     * 渲染课程表格
     */
    renderCourseTable(coursePage) {
        const tbody = document.querySelector('#courseTable tbody');
        if (!tbody) return;

        if (!coursePage.content || coursePage.content.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i><br>
                        暂无课程数据
                    </td>
                </tr>
            `;
            return;
        }

        const rows = coursePage.content.map(course => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${course.id}">
                    </div>
                </td>
                <td>
                    <div class="fw-bold">${course.courseName}</div>
                    <small class="text-muted">${course.courseCode}</small>
                </td>
                <td>
                    <span class="badge bg-primary">${course.credits}学分</span>
                </td>
                <td>
                    <span class="badge bg-info">${course.courseType}</span>
                </td>
                <td>${course.semester || '-'}</td>
                <td>
                    <span class="badge ${course.status === 1 ? 'bg-success' : 'bg-secondary'}">
                        ${course.status === 1 ? '启用' : '禁用'}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${this.formatDate(course.createdAt)}</small>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button type="button" class="btn btn-outline-primary edit-course-btn" 
                                data-course-id="${course.id}" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn btn-outline-warning toggle-status-btn" 
                                data-course-id="${course.id}" title="${course.status === 1 ? '禁用' : '启用'}">
                            <i class="fas ${course.status === 1 ? 'fa-eye-slash' : 'fa-eye'}"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger delete-course-btn" 
                                data-course-id="${course.id}" title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');

        tbody.innerHTML = rows;
    }

    /**
     * 渲染分页
     */
    renderPagination(coursePage) {
        const pagination = document.getElementById('pagination');
        if (!pagination || !coursePage) return;

        const totalPages = coursePage.totalPages;
        const currentPage = coursePage.number;

        if (totalPages <= 1) {
            pagination.innerHTML = '';
            return;
        }

        let paginationHtml = `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">上一页</a>
            </li>
        `;

        // 页码按钮
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }

        paginationHtml += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">下一页</a>
            </li>
        `;

        pagination.innerHTML = paginationHtml;
    }

    /**
     * 更新统计信息
     */
    updateStatistics(stats) {
        if (!stats) return;

        const statElements = {
            totalCourses: document.getElementById('totalCourses'),
            activeCourses: document.getElementById('activeCourses'),
            totalCredits: document.getElementById('totalCredits'),
            courseTypes: document.getElementById('courseTypes')
        };

        if (statElements.totalCourses) statElements.totalCourses.textContent = stats.totalCourses || 0;
        if (statElements.activeCourses) statElements.activeCourses.textContent = stats.activeCourses || 0;
        if (statElements.totalCredits) statElements.totalCredits.textContent = stats.totalCredits || 0;
        if (statElements.courseTypes) statElements.courseTypes.textContent = stats.courseTypes || 0;
    }

    /**
     * 处理搜索
     */
    handleSearch() {
        const formData = new FormData(document.getElementById('searchForm'));
        this.searchParams = {};
        
        for (const [key, value] of formData.entries()) {
            if (value.trim()) {
                this.searchParams[key] = value.trim();
            }
        }

        this.loadCourses(0);
    }

    /**
     * 重置搜索
     */
    resetSearch() {
        document.getElementById('searchForm').reset();
        this.searchParams = {};
        this.loadCourses(0);
    }

    /**
     * 显示添加课程模态框
     */
    async showAddCourseModal() {
        try {
            // 获取表单数据
            const response = await apiClient.get('/api/courses/form-data');
            if (response.success) {
                this.populateFormData(response.data);
                this.resetCourseForm();
                
                const modal = new bootstrap.Modal(document.getElementById('courseModal'));
                modal.show();
            } else {
                MessageUtils.error('获取表单数据失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('获取表单数据失败：' + error.message);
        }
    }

    /**
     * 编辑课程
     */
    async editCourse(courseId) {
        try {
            // 获取课程详情和表单数据
            const [courseResponse, formDataResponse] = await Promise.all([
                apiClient.get(`/api/courses/${courseId}`),
                apiClient.get('/api/courses/form-data')
            ]);

            if (courseResponse.success && formDataResponse.success) {
                this.populateFormData(formDataResponse.data);
                this.populateCourseForm(courseResponse.data);
                
                const modal = new bootstrap.Modal(document.getElementById('courseModal'));
                modal.show();
            } else {
                MessageUtils.error('获取课程信息失败');
            }
        } catch (error) {
            MessageUtils.error('获取课程信息失败：' + error.message);
        }
    }

    /**
     * 保存课程
     */
    async saveCourse() {
        if (!this.formValidator) return;

        const validation = this.formValidator.validate();
        if (!validation.isValid) {
            MessageUtils.error('请检查表单输入');
            return;
        }

        const saveBtn = document.getElementById('saveCourseBtn');
        try {
            LoadingManager.showButtonLoading(saveBtn, '保存中...');

            const formData = new FormData(document.getElementById('courseForm'));
            const courseData = Object.fromEntries(formData.entries());
            
            // 转换数据类型
            courseData.credits = parseFloat(courseData.credits);
            courseData.status = parseInt(courseData.status || 1);

            const courseId = document.getElementById('courseId').value;
            let response;

            if (courseId) {
                // 更新课程
                response = await apiClient.put(`/api/courses/${courseId}`, courseData);
            } else {
                // 创建课程
                response = await apiClient.post('/api/courses', courseData);
            }

            if (response.success) {
                MessageUtils.success(response.message || '保存成功');
                bootstrap.Modal.getInstance(document.getElementById('courseModal')).hide();
                this.loadCourses(this.currentPage);
            } else {
                MessageUtils.error(response.message || '保存失败');
            }
        } catch (error) {
            MessageUtils.error('保存失败：' + error.message);
        } finally {
            LoadingManager.hideButtonLoading(saveBtn);
        }
    }

    /**
     * 删除课程
     */
    async deleteCourse(courseId) {
        const confirmed = await MessageUtils.confirm('确定要删除这门课程吗？此操作不可撤销。', '删除确认');
        if (!confirmed) return;

        try {
            const response = await apiClient.delete(`/api/courses/${courseId}`);
            if (response.success) {
                MessageUtils.success('删除成功');
                this.loadCourses(this.currentPage);
            } else {
                MessageUtils.error('删除失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('删除失败：' + error.message);
        }
    }

    /**
     * 切换课程状态
     */
    async toggleCourseStatus(courseId) {
        try {
            const response = await apiClient.post(`/api/courses/${courseId}/toggle-status`);
            if (response.success) {
                MessageUtils.success('状态更新成功');
                this.loadCourses(this.currentPage);
            } else {
                MessageUtils.error('状态更新失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('状态更新失败：' + error.message);
        }
    }

    /**
     * 填充表单数据
     */
    populateFormData(data) {
        // 填充教师选项
        const teacherSelect = document.getElementById('teacherId');
        if (teacherSelect && data.teachers) {
            teacherSelect.innerHTML = '<option value="">请选择教师</option>';
            data.teachers.forEach(teacher => {
                teacherSelect.innerHTML += `<option value="${teacher.id}">${teacher.realName}</option>`;
            });
        }

        // 填充课程类型选项
        const typeSelect = document.getElementById('courseType');
        if (typeSelect && data.courseTypes) {
            typeSelect.innerHTML = '<option value="">请选择课程类型</option>';
            data.courseTypes.forEach(type => {
                typeSelect.innerHTML += `<option value="${type}">${type}</option>`;
            });
        }

        // 填充学期选项
        const semesterSelect = document.getElementById('semester');
        if (semesterSelect && data.semesters) {
            semesterSelect.innerHTML = '<option value="">请选择学期</option>';
            data.semesters.forEach(semester => {
                semesterSelect.innerHTML += `<option value="${semester}">${semester}</option>`;
            });
        }
    }

    /**
     * 填充课程表单
     */
    populateCourseForm(course) {
        document.getElementById('courseId').value = course.id || '';
        document.getElementById('courseName').value = course.courseName || '';
        document.getElementById('courseCode').value = course.courseCode || '';
        document.getElementById('credits').value = course.credits || '';
        document.getElementById('courseType').value = course.courseType || '';
        document.getElementById('semester').value = course.semester || '';
        document.getElementById('description').value = course.description || '';
        document.getElementById('status').value = course.status || 1;
        
        // 更新模态框标题
        document.getElementById('courseModalLabel').textContent = '编辑课程';
    }

    /**
     * 重置课程表单
     */
    resetCourseForm() {
        document.getElementById('courseForm').reset();
        document.getElementById('courseId').value = '';
        document.getElementById('courseModalLabel').textContent = '添加课程';
        
        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    /**
     * 格式化日期
     */
    formatDate(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('courseTable')) {
        new CourseManagement();
    }
});
