/**
 * 班级管理模块 v3.0
 * 基于CrudBase的班级增删改查功能
 */
class ClassManagement extends CrudBase {
    constructor() {
        super({
            apiEndpoint: '/api/classes',
            tableName: '班级',
            modalId: 'classModal',
            formId: 'classForm',
            tableBodyId: 'classTable',
            paginationId: 'pagination',
            searchFormId: 'searchForm'
        });

        this.formData = {
            grades: [],
            majors: [],
            teachers: []
        };
    }

    /**
     * 渲染表格行
     */
    renderTableRow(schoolClass) {
        return `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${schoolClass.id}">
                    </div>
                </td>
                <td>
                    <div class="fw-bold">${schoolClass.className}</div>
                    <small class="text-muted">${schoolClass.classCode || ''}</small>
                    <div><span class="badge bg-info">${schoolClass.grade}</span></div>
                </td>
                <td>
                    <span class="badge bg-secondary">${schoolClass.major || '-'}</span>
                </td>
                <td>
                    <span class="badge bg-primary">${schoolClass.currentStudents || 0}/${schoolClass.maxStudents || 0}</span>
                </td>
                <td>${schoolClass.headTeacherName || '-'}</td>
                <td>
                    ${DataFormatter.formatStatus(schoolClass.status)}
                </td>
                <td>
                    <small class="text-muted">${DataFormatter.formatDate(schoolClass.createdAt)}</small>
                </td>
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn-action btn-view"
                                data-action="view" data-id="${schoolClass.id}" title="查看详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button type="button" class="btn-action btn-edit"
                                data-action="edit" data-id="${schoolClass.id}" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn-action btn-delete"
                                data-action="delete" data-id="${schoolClass.id}" title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }





    /**
     * 加载班级列表
     */
    async loadClasses(page = 0) {
        try {
            LoadingManager.showPageLoading('#classTableContainer');
            
            const params = {
                page: page,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/classes', params);
            
            if (response.success) {
                this.renderClassTable(response.data.classes);
                this.renderPagination(response.data.classes);
                this.updateStatistics(response.data.stats);
                this.currentPage = page;
            } else {
                MessageUtils.error('加载班级列表失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('加载班级列表失败：' + error.message);
        } finally {
            LoadingManager.hidePageLoading('#classTableContainer');
        }
    }

    /**
     * 渲染班级表格
     */
    renderClassTable(classPage) {
        const tbody = document.querySelector('#classTable tbody');
        if (!tbody) return;

        if (!classPage.content || classPage.content.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i><br>
                        暂无班级数据
                    </td>
                </tr>
            `;
            return;
        }

        const rows = classPage.content.map(schoolClass => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" value="${schoolClass.id}">
                    </div>
                </td>
                <td>
                    <div class="fw-bold">${schoolClass.className}</div>
                    <small class="text-muted">${schoolClass.grade}</small>
                </td>
                <td>
                    <span class="badge bg-info">${schoolClass.major || '-'}</span>
                </td>
                <td>
                    <span class="badge bg-primary">${schoolClass.currentStudents || 0}/${schoolClass.maxStudents || 0}</span>
                </td>
                <td>${schoolClass.teacherName || '-'}</td>
                <td>
                    <span class="badge ${schoolClass.status === 1 ? 'bg-success' : 'bg-secondary'}">
                        ${schoolClass.status === 1 ? '正常' : '停用'}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${this.formatDate(schoolClass.createdAt)}</small>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button type="button" class="btn btn-outline-info view-students-btn" 
                                data-class-id="${schoolClass.id}" title="查看学生">
                            <i class="fas fa-users"></i>
                        </button>
                        <button type="button" class="btn btn-outline-primary edit-class-btn" 
                                data-class-id="${schoolClass.id}" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger delete-class-btn" 
                                data-class-id="${schoolClass.id}" title="删除">
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
    renderPagination(classPage) {
        const pagination = document.getElementById('pagination');
        if (!pagination || !classPage) return;

        const totalPages = classPage.totalPages;
        const currentPage = classPage.number;

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
            totalClasses: document.getElementById('totalClasses'),
            activeClasses: document.getElementById('activeClasses'),
            totalStudents: document.getElementById('totalStudents'),
            avgStudentsPerClass: document.getElementById('avgStudentsPerClass')
        };

        if (statElements.totalClasses) statElements.totalClasses.textContent = stats.totalClasses || 0;
        if (statElements.activeClasses) statElements.activeClasses.textContent = stats.activeClasses || 0;
        if (statElements.totalStudents) statElements.totalStudents.textContent = stats.totalStudents || 0;
        if (statElements.avgStudentsPerClass) statElements.avgStudentsPerClass.textContent = stats.avgStudentsPerClass || 0;
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

        this.loadClasses(0);
    }

    /**
     * 重置搜索
     */
    resetSearch() {
        document.getElementById('searchForm').reset();
        this.searchParams = {};
        this.loadClasses(0);
    }

    /**
     * 显示添加班级模态框
     */
    async showAddClassModal() {
        try {
            // 获取表单数据
            const response = await apiClient.get('/api/classes/form-data');
            if (response.success) {
                this.populateFormData(response.data);
                this.resetClassForm();
                
                const modal = new bootstrap.Modal(document.getElementById('classModal'));
                modal.show();
            } else {
                MessageUtils.error('获取表单数据失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('获取表单数据失败：' + error.message);
        }
    }

    /**
     * 编辑班级
     */
    async editClass(classId) {
        try {
            // 获取班级详情和表单数据
            const [classResponse, formDataResponse] = await Promise.all([
                apiClient.get(`/api/classes/${classId}`),
                apiClient.get('/api/classes/form-data')
            ]);

            if (classResponse.success && formDataResponse.success) {
                this.populateFormData(formDataResponse.data);
                this.populateClassForm(classResponse.data);
                
                const modal = new bootstrap.Modal(document.getElementById('classModal'));
                modal.show();
            } else {
                MessageUtils.error('获取班级信息失败');
            }
        } catch (error) {
            MessageUtils.error('获取班级信息失败：' + error.message);
        }
    }

    /**
     * 保存班级
     */
    async saveClass() {
        if (!this.formValidator) return;

        const validation = this.formValidator.validate();
        if (!validation.isValid) {
            MessageUtils.error('请检查表单输入');
            return;
        }

        const saveBtn = document.getElementById('saveClassBtn');
        try {
            LoadingManager.showButtonLoading(saveBtn, '保存中...');

            const formData = new FormData(document.getElementById('classForm'));
            const classData = Object.fromEntries(formData.entries());
            
            // 转换数据类型
            classData.maxStudents = parseInt(classData.maxStudents);
            classData.status = parseInt(classData.status || 1);

            const classId = document.getElementById('classId').value;
            let response;

            if (classId) {
                // 更新班级
                response = await apiClient.put(`/api/classes/${classId}`, classData);
            } else {
                // 创建班级
                response = await apiClient.post('/api/classes', classData);
            }

            if (response.success) {
                MessageUtils.success(response.message || '保存成功');
                bootstrap.Modal.getInstance(document.getElementById('classModal')).hide();
                this.loadClasses(this.currentPage);
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
     * 删除班级
     */
    async deleteClass(classId) {
        const confirmed = await MessageUtils.confirm('确定要删除这个班级吗？此操作不可撤销。', '删除确认');
        if (!confirmed) return;

        try {
            const response = await apiClient.delete(`/api/classes/${classId}`);
            if (response.success) {
                MessageUtils.success('删除成功');
                this.loadClasses(this.currentPage);
            } else {
                MessageUtils.error('删除失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('删除失败：' + error.message);
        }
    }

    /**
     * 查看班级学生
     */
    viewClassStudents(classId) {
        // 跳转到学生管理页面，并筛选该班级的学生
        window.location.href = `/admin/students?classId=${classId}`;
    }

    /**
     * 填充表单数据
     */
    populateFormData(data) {
        // 填充年级选项
        const gradeSelect = document.getElementById('grade');
        if (gradeSelect && data.grades) {
            gradeSelect.innerHTML = '<option value="">请选择年级</option>';
            data.grades.forEach(grade => {
                gradeSelect.innerHTML += `<option value="${grade}">${grade}</option>`;
            });
        }

        // 填充专业选项
        const majorSelect = document.getElementById('major');
        if (majorSelect && data.majors) {
            majorSelect.innerHTML = '<option value="">请选择专业</option>';
            data.majors.forEach(major => {
                majorSelect.innerHTML += `<option value="${major}">${major}</option>`;
            });
        }

        // 填充班主任选项
        const teacherSelect = document.getElementById('teacherId');
        if (teacherSelect && data.teachers) {
            teacherSelect.innerHTML = '<option value="">请选择班主任</option>';
            data.teachers.forEach(teacher => {
                teacherSelect.innerHTML += `<option value="${teacher.id}">${teacher.realName}</option>`;
            });
        }
    }

    /**
     * 填充班级表单
     */
    populateClassForm(schoolClass) {
        document.getElementById('classId').value = schoolClass.id || '';
        document.getElementById('className').value = schoolClass.className || '';
        document.getElementById('grade').value = schoolClass.grade || '';
        document.getElementById('major').value = schoolClass.major || '';
        document.getElementById('maxStudents').value = schoolClass.maxStudents || '';
        document.getElementById('teacherId').value = schoolClass.teacherId || '';
        document.getElementById('description').value = schoolClass.description || '';
        document.getElementById('status').value = schoolClass.status || 1;
        
        // 更新模态框标题
        document.getElementById('classModalLabel').textContent = '编辑班级';
    }

    /**
     * 重置班级表单
     */
    resetClassForm() {
        document.getElementById('classForm').reset();
        document.getElementById('classId').value = '';
        document.getElementById('classModalLabel').textContent = '添加班级';
        
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

    /**
     * 获取表单数据
     */
    getFormData() {
        const form = document.getElementById(this.config.formId);
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        // 转换数据类型
        data.maxStudents = parseInt(data.maxStudents) || 0;
        data.status = parseInt(data.status) || 1;
        data.headTeacherId = data.headTeacherId ? parseInt(data.headTeacherId) : null;

        return data;
    }

    /**
     * 填充表单
     */
    populateForm(data) {
        document.getElementById('classId').value = data.id || '';
        document.getElementById('className').value = data.className || '';
        document.getElementById('classCode').value = data.classCode || '';
        document.getElementById('grade').value = data.grade || '';
        document.getElementById('major').value = data.major || '';
        document.getElementById('maxStudents').value = data.maxStudents || '';
        document.getElementById('headTeacherId').value = data.headTeacherId || '';
        document.getElementById('description').value = data.description || '';
        document.getElementById('status').value = data.status || 1;

        // 更新模态框标题
        document.getElementById('classModalLabel').innerHTML = '<i class="fas fa-edit me-2"></i>编辑班级';
    }

    /**
     * 重置表单
     */
    resetForm() {
        const form = document.getElementById(this.config.formId);
        form.reset();
        document.getElementById('classId').value = '';
        document.getElementById('classModalLabel').innerHTML = '<i class="fas fa-plus me-2"></i>添加班级';

        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    /**
     * 获取当前编辑的ID
     */
    getCurrentId() {
        return document.getElementById('classId').value || null;
    }

    /**
     * 准备表单数据
     */
    async prepareFormData() {
        try {
            const response = await apiClient.get('/api/classes/form-data');
            if (this.isResponseSuccess(response)) {
                this.formData = response.data;
                this.populateFormSelects();
            }
        } catch (error) {
            console.error('获取表单数据失败:', error);
        }
    }

    /**
     * 填充表单选择框
     */
    populateFormSelects() {
        // 填充年级选项
        const gradeSelects = document.querySelectorAll('select[name="grade"]');
        gradeSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择年级</option>';
            this.formData.grades.forEach(grade => {
                select.innerHTML += `<option value="${grade}">${grade}</option>`;
            });
        });

        // 填充专业选项
        const majorSelects = document.querySelectorAll('select[name="major"]');
        majorSelects.forEach(select => {
            select.innerHTML = '<option value="">请选择专业</option>';
            this.formData.majors.forEach(major => {
                select.innerHTML += `<option value="${major}">${major}</option>`;
            });
        });

        // 填充班主任选项
        const teacherSelect = document.getElementById('headTeacherId');
        if (teacherSelect) {
            teacherSelect.innerHTML = '<option value="">请选择班主任</option>';
            this.formData.teachers.forEach(teacher => {
                teacherSelect.innerHTML += `<option value="${teacher.id}">${teacher.realName}</option>`;
            });
        }
    }

    /**
     * 获取验证规则
     */
    getValidationRules() {
        return [
            {
                field: 'className',
                rules: { required: true, maxLength: 50 },
                message: '班级名称不能为空且不超过50个字符'
            },
            {
                field: 'classCode',
                rules: { required: true, maxLength: 20 },
                message: '班级代码不能为空且不超过20个字符'
            },
            {
                field: 'grade',
                rules: { required: true },
                message: '请选择年级'
            },
            {
                field: 'major',
                rules: { required: true },
                message: '请选择专业'
            },
            {
                field: 'maxStudents',
                rules: {
                    required: true,
                    custom: (value) => {
                        const num = parseInt(value);
                        return (num > 0 && num <= 100) || '班级人数必须在1-100之间';
                    }
                },
                message: '班级人数格式不正确'
            }
        ];
    }

    /**
     * 更新统计信息
     */
    updateStatistics(data) {
        const stats = data.stats || {};

        const statElements = {
            totalClasses: document.getElementById('totalClasses'),
            activeClasses: document.getElementById('activeClasses'),
            totalStudents: document.getElementById('totalStudents'),
            avgStudentsPerClass: document.getElementById('avgStudentsPerClass')
        };

        Object.keys(statElements).forEach(key => {
            if (statElements[key]) {
                statElements[key].textContent = stats[key] || 0;
            }
        });
    }

    /**
     * 获取导出表头
     */
    getExportHeaders() {
        return ['班级名称', '班级代码', '年级', '专业', '班级人数', '班主任', '状态', '创建时间'];
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('classTable')) {
        new ClassManagement();
    }
});
