/**
 * 学生管理模块
 */
class StudentManagement {
    constructor() {
        this.currentPage = 0;
        this.pageSize = 20;
        this.searchParams = {};
        this.formValidator = null;
        this.selectedStudents = new Set();
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadStudents();
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

        // 添加学生按钮
        const addStudentBtn = document.getElementById('addStudentBtn');
        if (addStudentBtn) {
            addStudentBtn.addEventListener('click', () => this.showAddStudentModal());
        }

        // 保存学生按钮
        const saveStudentBtn = document.getElementById('saveStudentBtn');
        if (saveStudentBtn) {
            saveStudentBtn.addEventListener('click', () => this.saveStudent());
        }

        // 批量删除按钮
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        if (batchDeleteBtn) {
            batchDeleteBtn.addEventListener('click', () => this.batchDeleteStudents());
        }

        // 导出Excel按钮
        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.exportStudents());
        }

        // 全选复选框
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', (e) => this.handleSelectAll(e.target.checked));
        }

        // 分页按钮
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('page-link')) {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                if (!isNaN(page)) {
                    this.loadStudents(page);
                }
            }
        });

        // 操作按钮
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('edit-student-btn')) {
                const studentId = e.target.dataset.studentId;
                this.editStudent(studentId);
            } else if (e.target.classList.contains('delete-student-btn')) {
                const studentId = e.target.dataset.studentId;
                this.deleteStudent(studentId);
            } else if (e.target.classList.contains('view-student-btn')) {
                const studentId = e.target.dataset.studentId;
                this.viewStudent(studentId);
            }
        });

        // 学生复选框
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('student-checkbox')) {
                this.handleStudentSelect(e.target);
            }
        });
    }

    /**
     * 初始化表单验证器
     */
    initFormValidator() {
        const studentForm = document.getElementById('studentForm');
        if (studentForm) {
            this.formValidator = new FormValidator(studentForm);
            this.formValidator
                .addRule('studentNo', { 
                    required: true, 
                    maxLength: 20,
                    pattern: /^[0-9A-Za-z]+$/
                }, '学号不能为空，只能包含数字和字母，不超过20个字符')
                .addRule('grade', { required: true }, '请选择年级')
                .addRule('major', { required: true }, '请选择专业')
                .addRule('classId', { required: true }, '请选择班级');
        }
    }

    /**
     * 加载学生列表
     */
    async loadStudents(page = 0) {
        try {
            LoadingManager.showPageLoading('#studentTableContainer');
            
            const params = {
                page: page,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get('/api/students', params);
            
            if (response.success) {
                this.renderStudentTable(response.data.students);
                this.renderPagination(response.data.students);
                this.updateStatistics(response.data.stats);
                this.currentPage = page;
                this.selectedStudents.clear();
                this.updateBatchActions();
            } else {
                MessageUtils.error('加载学生列表失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('加载学生列表失败：' + error.message);
        } finally {
            LoadingManager.hidePageLoading('#studentTableContainer');
        }
    }

    /**
     * 渲染学生表格
     */
    renderStudentTable(studentPage) {
        const tbody = document.querySelector('#studentTable tbody');
        if (!tbody) return;

        if (!studentPage.content || studentPage.content.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" class="text-center text-muted py-4">
                        <i class="fas fa-inbox fa-2x mb-2"></i><br>
                        暂无学生数据
                    </td>
                </tr>
            `;
            return;
        }

        const rows = studentPage.content.map(student => `
            <tr>
                <td>
                    <div class="form-check">
                        <input class="form-check-input student-checkbox" type="checkbox" value="${student.id}">
                    </div>
                </td>
                <td>
                    <div class="d-flex align-items-center">
                        <div class="avatar-sm me-2">
                            <div class="avatar-title bg-primary rounded-circle">
                                ${(student.realName || student.name || '学').charAt(0)}
                            </div>
                        </div>
                        <div>
                            <div class="fw-bold">${student.realName || student.name || '-'}</div>
                            <small class="text-muted">${student.studentNo}</small>
                        </div>
                    </div>
                </td>
                <td>
                    <span class="badge bg-info">${student.grade || '-'}</span>
                </td>
                <td>
                    <span class="badge bg-secondary">${student.major || '-'}</span>
                </td>
                <td>${student.className || '-'}</td>
                <td>
                    <span class="badge ${this.getStatusBadgeClass(student.academicStatus)}">
                        ${this.getStatusText(student.academicStatus)}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${this.formatDate(student.enrollmentDate)}</small>
                </td>
                <td>
                    <small class="text-muted">${this.formatDate(student.createdAt)}</small>
                </td>
                <td>
                    <div class="btn-group btn-group-sm">
                        <button type="button" class="btn btn-outline-info view-student-btn" 
                                data-student-id="${student.id}" title="查看详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button type="button" class="btn btn-outline-primary edit-student-btn" 
                                data-student-id="${student.id}" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger delete-student-btn" 
                                data-student-id="${student.id}" title="删除">
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
    renderPagination(studentPage) {
        const pagination = document.getElementById('pagination');
        if (!pagination || !studentPage) return;

        const totalPages = studentPage.totalPages;
        const currentPage = studentPage.number;

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
            totalStudents: document.getElementById('totalStudents'),
            activeStudents: document.getElementById('activeStudents'),
            graduatedStudents: document.getElementById('graduatedStudents'),
            newStudents: document.getElementById('newStudents')
        };

        if (statElements.totalStudents) statElements.totalStudents.textContent = stats.totalStudents || 0;
        if (statElements.activeStudents) statElements.activeStudents.textContent = stats.activeStudents || 0;
        if (statElements.graduatedStudents) statElements.graduatedStudents.textContent = stats.graduatedStudents || 0;
        if (statElements.newStudents) statElements.newStudents.textContent = stats.newStudents || 0;
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

        this.loadStudents(0);
    }

    /**
     * 重置搜索
     */
    resetSearch() {
        document.getElementById('searchForm').reset();
        this.searchParams = {};
        this.loadStudents(0);
    }

    /**
     * 显示添加学生模态框
     */
    async showAddStudentModal() {
        try {
            // 获取表单数据
            const response = await apiClient.get('/api/students/form-data');
            if (response.success) {
                this.populateFormData(response.data);
                this.resetStudentForm();
                
                const modal = new bootstrap.Modal(document.getElementById('studentModal'));
                modal.show();
            } else {
                MessageUtils.error('获取表单数据失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('获取表单数据失败：' + error.message);
        }
    }

    /**
     * 编辑学生
     */
    async editStudent(studentId) {
        try {
            // 获取学生详情和表单数据
            const [studentResponse, formDataResponse] = await Promise.all([
                apiClient.get(`/api/students/${studentId}`),
                apiClient.get('/api/students/form-data')
            ]);

            if (studentResponse.success && formDataResponse.success) {
                this.populateFormData(formDataResponse.data);
                this.populateStudentForm(studentResponse.data);
                
                const modal = new bootstrap.Modal(document.getElementById('studentModal'));
                modal.show();
            } else {
                MessageUtils.error('获取学生信息失败');
            }
        } catch (error) {
            MessageUtils.error('获取学生信息失败：' + error.message);
        }
    }

    /**
     * 查看学生详情
     */
    async viewStudent(studentId) {
        try {
            const response = await apiClient.get(`/api/students/${studentId}`);
            if (response.success) {
                this.showStudentDetailModal(response.data);
            } else {
                MessageUtils.error('获取学生详情失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('获取学生详情失败：' + error.message);
        }
    }

    /**
     * 保存学生
     */
    async saveStudent() {
        if (!this.formValidator) return;

        const validation = this.formValidator.validate();
        if (!validation.isValid) {
            MessageUtils.error('请检查表单输入');
            return;
        }

        const saveBtn = document.getElementById('saveStudentBtn');
        try {
            LoadingManager.showButtonLoading(saveBtn, '保存中...');

            const formData = new FormData(document.getElementById('studentForm'));
            const studentData = Object.fromEntries(formData.entries());
            
            // 转换数据类型
            studentData.classId = parseInt(studentData.classId);
            studentData.academicStatus = parseInt(studentData.academicStatus || 1);

            const studentId = document.getElementById('studentId').value;
            let response;

            if (studentId) {
                // 更新学生
                response = await apiClient.put(`/api/students/${studentId}`, studentData);
            } else {
                // 创建学生
                response = await apiClient.post('/api/students', studentData);
            }

            if (response.success) {
                MessageUtils.success(response.message || '保存成功');
                bootstrap.Modal.getInstance(document.getElementById('studentModal')).hide();
                this.loadStudents(this.currentPage);
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
     * 删除学生
     */
    async deleteStudent(studentId) {
        const confirmed = await MessageUtils.confirm('确定要删除这个学生吗？此操作不可撤销。', '删除确认');
        if (!confirmed) return;

        try {
            const response = await apiClient.delete(`/api/students/${studentId}`);
            if (response.success) {
                MessageUtils.success('删除成功');
                this.loadStudents(this.currentPage);
            } else {
                MessageUtils.error('删除失败：' + response.message);
            }
        } catch (error) {
            MessageUtils.error('删除失败：' + error.message);
        }
    }

    /**
     * 批量删除学生
     */
    async batchDeleteStudents() {
        if (this.selectedStudents.size === 0) {
            MessageUtils.warning('请选择要删除的学生');
            return;
        }

        const confirmed = await MessageUtils.confirm(
            `确定要删除选中的 ${this.selectedStudents.size} 个学生吗？此操作不可撤销。`, 
            '批量删除确认'
        );
        if (!confirmed) return;

        try {
            const deletePromises = Array.from(this.selectedStudents).map(studentId => 
                apiClient.delete(`/api/students/${studentId}`)
            );

            await Promise.all(deletePromises);
            MessageUtils.success('批量删除成功');
            this.loadStudents(this.currentPage);
        } catch (error) {
            MessageUtils.error('批量删除失败：' + error.message);
        }
    }

    /**
     * 导出学生数据
     */
    async exportStudents() {
        try {
            const exportBtn = document.getElementById('exportBtn');
            LoadingManager.showButtonLoading(exportBtn, '导出中...');

            // 这里应该调用实际的导出API
            MessageUtils.info('导出功能开发中...');
        } catch (error) {
            MessageUtils.error('导出失败：' + error.message);
        } finally {
            const exportBtn = document.getElementById('exportBtn');
            LoadingManager.hideButtonLoading(exportBtn);
        }
    }

    /**
     * 处理全选
     */
    handleSelectAll(checked) {
        const checkboxes = document.querySelectorAll('.student-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = checked;
            this.handleStudentSelect(checkbox);
        });
    }

    /**
     * 处理学生选择
     */
    handleStudentSelect(checkbox) {
        const studentId = checkbox.value;
        if (checkbox.checked) {
            this.selectedStudents.add(studentId);
        } else {
            this.selectedStudents.delete(studentId);
        }
        this.updateBatchActions();
    }

    /**
     * 更新批量操作按钮状态
     */
    updateBatchActions() {
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        const selectedCount = this.selectedStudents.size;
        
        if (batchDeleteBtn) {
            batchDeleteBtn.disabled = selectedCount === 0;
            batchDeleteBtn.textContent = selectedCount > 0 ? `删除选中 (${selectedCount})` : '批量删除';
        }

        // 更新全选复选框状态
        const selectAllCheckbox = document.getElementById('selectAll');
        const checkboxes = document.querySelectorAll('.student-checkbox');
        if (selectAllCheckbox && checkboxes.length > 0) {
            const checkedCount = document.querySelectorAll('.student-checkbox:checked').length;
            selectAllCheckbox.checked = checkedCount === checkboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
        }
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

        // 填充班级选项
        const classSelect = document.getElementById('classId');
        if (classSelect && data.classes) {
            classSelect.innerHTML = '<option value="">请选择班级</option>';
            data.classes.forEach(schoolClass => {
                classSelect.innerHTML += `<option value="${schoolClass.id}">${schoolClass.className}</option>`;
            });
        }
    }

    /**
     * 填充学生表单
     */
    populateStudentForm(student) {
        document.getElementById('studentId').value = student.id || '';
        document.getElementById('studentNo').value = student.studentNo || '';
        document.getElementById('grade').value = student.grade || '';
        document.getElementById('major').value = student.major || '';
        document.getElementById('classId').value = student.classId || '';
        document.getElementById('academicStatus').value = student.academicStatus || 1;
        
        // 更新模态框标题
        document.getElementById('studentModalLabel').textContent = '编辑学生';
    }

    /**
     * 重置学生表单
     */
    resetStudentForm() {
        document.getElementById('studentForm').reset();
        document.getElementById('studentId').value = '';
        document.getElementById('studentModalLabel').textContent = '添加学生';
        
        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    /**
     * 显示学生详情模态框
     */
    showStudentDetailModal(student) {
        // 这里可以实现学生详情模态框的显示逻辑
        MessageUtils.info('学生详情功能开发中...');
    }

    /**
     * 获取状态徽章类
     */
    getStatusBadgeClass(status) {
        const classes = {
            1: 'bg-success',
            2: 'bg-info',
            3: 'bg-danger',
            4: 'bg-warning',
            5: 'bg-secondary'
        };
        return classes[status] || 'bg-secondary';
    }

    /**
     * 获取状态文本
     */
    getStatusText(status) {
        const texts = {
            1: '在读',
            2: '毕业',
            3: '退学',
            4: '休学',
            5: '转学'
        };
        return texts[status] || '未知';
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
    if (document.getElementById('studentTable')) {
        new StudentManagement();
    }
});
