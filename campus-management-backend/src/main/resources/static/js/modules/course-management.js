/**
 * 课程管理模块 v3.0
 * 基于CrudBase的课程增删改查功能
 */
class CourseManagement extends CrudBase {
    constructor() {
        super({
            apiEndpoint: '/api/v1/courses',
            tableName: '课程',
            modalId: 'courseModal',
            formId: 'courseForm',
            tableBodyId: 'courseTable',
            paginationId: 'pagination',
            searchFormId: 'searchForm'
        });
        
        this.formData = {
            courseTypes: ['必修课', '选修课', '实践课', '通识课', '专业课', '基础课'],
            semesters: [],
            teachers: []
        };
    }

    renderTableRow(course) {
        return \
            <tr>
                <td>
                    <div class=\
form-check\>
                        <input class=\form-check-input\ type=\checkbox\ value=\\\>
                    </div>
                </td>
                <td>
                    <div>
                        <div class=\fw-bold\>\</div>
                        <small class=\text-muted\>\</small>
                    </div>
                </td>
                <td>
                    <span class=\badge
bg-info\>\</span>
                </td>
                <td>
                    <span class=\badge
bg-secondary\>\</span>
                </td>
                <td>
                    <span class=\badge
bg-primary\>\</span>
                </td>
                <td>
                    \
                </td>
                <td>
                    <small class=\text-muted\>\</small>
                </td>
                <td>
                    <div class=\action-buttons\>
                        <button type=\button\ class=\btn-action
btn-view\ 
                                data-action=\view\ data-id=\\\ title=\查看详情\>
                            <i class=\fas
fa-eye\></i>
                        </button>
                        <button type=\button\ class=\btn-action
btn-edit\ 
                                data-action=\edit\ data-id=\\\ title=\编辑\>
                            <i class=\fas
fa-edit\></i>
                        </button>
                        <button type=\button\ class=\btn-action
btn-delete\ 
                                data-action=\delete\ data-id=\\\ title=\删除\>
                            <i class=\fas
fa-trash\></i>
                        </button>
                    </div>
                </td>
            </tr>
        \;
    }

    getFormData() {
        const form = document.getElementById(this.config.formId);
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        data.credits = parseFloat(data.credits) || 0;
        data.status = parseInt(data.status) || 1;
        data.teacherId = data.teacherId ? parseInt(data.teacherId) : null;
        
        return data;
    }

    populateForm(data) {
        document.getElementById('courseId').value = data.id || '';
        document.getElementById('courseCode').value = data.courseCode || '';
        document.getElementById('courseName').value = data.courseName || '';
        document.getElementById('courseType').value = data.courseType || '';
        document.getElementById('teacherId').value = data.teacherId || '';
        document.getElementById('credits').value = data.credits || '';
        document.getElementById('semester').value = data.semester || '';
        document.getElementById('status').value = data.status || 1;
        document.getElementById('description').value = data.description || '';
        
        document.getElementById('courseModalLabel').innerHTML = '<i class=\fas
fa-edit
me-2\></i>编辑课程';
    }

    resetForm() {
        const form = document.getElementById(this.config.formId);
        form.reset();
        document.getElementById('courseId').value = '';
        document.getElementById('courseModalLabel').innerHTML = '<i class=\fas
fa-plus
me-2\></i>添加课程';
        
        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    getCurrentId() {
        return document.getElementById('courseId').value || null;
    }

    async prepareFormData() {
        try {
            const response = await apiClient.get('/api/v1/courses/form-data');
            if (this.isResponseSuccess(response)) {
                this.formData = { ...this.formData, ...response.data };
                this.populateFormSelects();
            }
        } catch (error) {
            console.error('获取表单数据失败:', error);
        }
    }

    populateFormSelects() {
        const courseTypeSelect = document.getElementById('courseType');
        if (courseTypeSelect) {
            courseTypeSelect.innerHTML = '<option value=\\>请选择课程类型</option>';
            this.formData.courseTypes.forEach(type => {
                courseTypeSelect.innerHTML += \<option value=\\\>\</option>\;
            });
        }

        const semesterSelect = document.getElementById('semester');
        if (semesterSelect && this.formData.semesters) {
            semesterSelect.innerHTML = '<option value=\\>请选择学期</option>';
            this.formData.semesters.forEach(semester => {
                semesterSelect.innerHTML += \<option value=\\\>\</option>\;
            });
        }

        const teacherSelect = document.getElementById('teacherId');
        if (teacherSelect && this.formData.teachers) {
            teacherSelect.innerHTML = '<option value=\\>请选择教师</option>';
            this.formData.teachers.forEach(teacher => {
                teacherSelect.innerHTML += \<option value=\\\>\</option>\;
            });
        }
    }

    getValidationRules() {
        return [
            {
                field: 'courseCode',
                rules: { required: true, maxLength: 20 },
                message: '课程代码不能为空且不超过20个字符'
            },
            {
                field: 'courseName',
                rules: { required: true, maxLength: 100 },
                message: '课程名称不能为空且不超过100个字符'
            },
            {
                field: 'courseType',
                rules: { required: true },
                message: '请选择课程类型'
            },
            {
                field: 'credits',
                rules: { 
                    required: true,
                    custom: (value) => {
                        const num = parseFloat(value);
                        return (num >= 0.5 && num <= 10) || '学分必须在0.5-10之间';
                    }
                },
                message: '学分格式不正确'
            }
        ];
    }

    updateStatistics(data) {
        const stats = data.stats || {};
        
        const statElements = {
            totalCourses: document.getElementById('totalCourses'),
            activeCourses: document.getElementById('activeCourses'),
            totalCredits: document.getElementById('totalCredits'),
            courseTypes: document.getElementById('courseTypes')
        };

        Object.keys(statElements).forEach(key => {
            if (statElements[key]) {
                statElements[key].textContent = stats[key] || 0;
            }
        });
    }

    getExportHeaders() {
        return ['课程代码', '课程名称', '学分', '课程类型', '学期', '状态', '创建时间'];
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('courseTable')) {
        new CourseManagement();
    }
});
