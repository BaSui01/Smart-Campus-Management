/**
 * 缴费管理模块 v3.0
 * 基于CrudBase的缴费增删改查功能
 */
class PaymentManagement extends CrudBase {
    constructor() {
        super({
            apiEndpoint: '/api/payments',
            tableName: '缴费记录',
            modalId: 'paymentModal',
            formId: 'paymentForm',
            tableBodyId: 'paymentTable',
            paginationId: 'pagination',
            searchFormId: 'searchForm'
        });
        
        this.formData = {
            students: [],
            feeTypes: ['tuition', 'accommodation', 'textbook', 'insurance', 'other'],
            paymentMethods: ['cash', 'bank_transfer', 'alipay', 'wechat', 'credit_card'],
            statuses: ['pending', 'paid', 'overdue', 'refunded']
        };
    }

    renderTableRow(payment) {
        const statusMap = {
            'pending': { text: '待缴费', class: 'status-pending' },
            'paid': { text: '已缴费', class: 'status-active' },
            'overdue': { text: '逾期未缴', class: 'status-inactive' },
            'refunded': { text: '已退费', class: 'status-inactive' }
        };

        const feeTypeMap = {
            'tuition': '学费',
            'accommodation': '住宿费',
            'textbook': '教材费',
            'insurance': '保险费',
            'other': '其他费用'
        };

        const paymentMethodMap = {
            'cash': '现金',
            'bank_transfer': '银行转账',
            'alipay': '支付宝',
            'wechat': '微信支付',
            'credit_card': '信用卡'
        };

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
                    <span class=\text-primary
fw-bold\>\</span>
                </td>
                <td>
                    <span class=\text-success
fw-bold\>\</span>
                </td>
                <td>
                    \
                </td>
                <td>
                    <div>
                        <small class=\text-muted\>\</small>
                        \
                    </div>
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
        
        data.studentId = data.studentId ? parseInt(data.studentId) : null;
        data.amountDue = parseFloat(data.amountDue) || 0;
        data.amountPaid = parseFloat(data.amountPaid) || 0;
        
        return data;
    }

    populateForm(data) {
        document.getElementById('paymentId').value = data.id || '';
        document.getElementById('studentId').value = data.studentId || '';
        document.getElementById('feeType').value = data.feeType || '';
        document.getElementById('amountDue').value = data.amountDue || '';
        document.getElementById('amountPaid').value = data.amountPaid || '';
        document.getElementById('paymentMethod').value = data.paymentMethod || '';
        document.getElementById('status').value = data.status || 'pending';
        document.getElementById('remarks').value = data.remarks || '';
        
        document.getElementById('paymentModalLabel').innerHTML = '<i class=\fas
fa-edit
me-2\></i>编辑缴费记录';
    }

    resetForm() {
        const form = document.getElementById(this.config.formId);
        form.reset();
        document.getElementById('paymentId').value = '';
        document.getElementById('paymentModalLabel').innerHTML = '<i class=\fas
fa-plus
me-2\></i>添加缴费记录';
        
        if (this.formValidator) {
            this.formValidator.clearAllErrors();
        }
    }

    getCurrentId() {
        return document.getElementById('paymentId').value || null;
    }

    async prepareFormData() {
        try {
            const response = await apiClient.get('/api/payments/form-data');
            if (this.isResponseSuccess(response)) {
                this.formData = { ...this.formData, ...response.data };
                this.populateFormSelects();
            }
        } catch (error) {
            console.error('获取表单数据失败:', error);
        }
    }

    populateFormSelects() {
        const studentSelect = document.getElementById('studentId');
        if (studentSelect && this.formData.students) {
            studentSelect.innerHTML = '<option value=\\>请选择学生</option>';
            this.formData.students.forEach(student => {
                studentSelect.innerHTML += \<option value=\\\>\ - \</option>\;
            });
        }
    }

    getValidationRules() {
        return [
            {
                field: 'studentId',
                rules: { required: true },
                message: '请选择学生'
            },
            {
                field: 'feeType',
                rules: { required: true },
                message: '请选择缴费项目'
            },
            {
                field: 'amountDue',
                rules: { 
                    required: true,
                    custom: (value) => {
                        const num = parseFloat(value);
                        return (num > 0) || '应缴金额必须大于0';
                    }
                },
                message: '应缴金额格式不正确'
            }
        ];
    }

    updateStatistics(data) {
        const stats = data.stats || {};
        
        const statElements = {
            totalPayments: document.getElementById('totalPayments'),
            paidPayments: document.getElementById('paidPayments'),
            pendingPayments: document.getElementById('pendingPayments'),
            overduePayments: document.getElementById('overduePayments')
        };

        Object.keys(statElements).forEach(key => {
            if (statElements[key]) {
                statElements[key].textContent = stats[key] || 0;
            }
        });
    }

    getExportHeaders() {
        return ['学生姓名', '学号', '缴费项目', '应缴金额', '实缴金额', '缴费状态', '缴费方式', '缴费时间', '备注'];
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('paymentTable')) {
        new PaymentManagement();
    }
});
