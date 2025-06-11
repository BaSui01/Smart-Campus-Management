/**
 * 缴费记录管理模块
 */

class PaymentRecordsManager {
    constructor() {
        this.apiBase = '/admin/api/v1/payment-records';
        this.currentPage = 0;
        this.pageSize = 10;
        this.searchQuery = '';
        this.statusFilter = '';
        this.methodFilter = '';
        this.editingRecord = null;
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadPaymentRecords();
        this.initializeFormValidation();
        this.generateTransactionNo();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 录入缴费按钮
        const addBtn = document.querySelector('[data-bs-target="#addPaymentModal"]');
        if (addBtn) {
            addBtn.addEventListener('click', () => this.showAddModal());
        }

        // 表单提交
        const addForm = document.getElementById('addPaymentForm');
        if (addForm) {
            addForm.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // 搜索框
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => this.handleSearch(e.target.value));
        }

        // 筛选器
        const statusFilter = document.getElementById('statusFilter');
        if (statusFilter) {
            statusFilter.addEventListener('change', (e) => this.handleStatusFilter(e.target.value));
        }

        const methodFilter = document.getElementById('methodFilter');
        if (methodFilter) {
            methodFilter.addEventListener('change', (e) => this.handleMethodFilter(e.target.value));
        }

        // 刷新按钮
        const refreshBtn = document.getElementById('refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => this.refreshData());
        }

        // 导出按钮
        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.exportData());
        }

        // 缴费项目选择变化时更新金额
        const feeItemSelect = document.querySelector('select[name="feeItemId"]');
        if (feeItemSelect) {
            feeItemSelect.addEventListener('change', (e) => this.handleFeeItemChange(e.target.value));
        }
    }

    /**
     * 加载缴费记录列表
     */
    async loadPaymentRecords() {
        try {
            this.showLoading();
            const params = new URLSearchParams({
                page: this.currentPage,
                size: this.pageSize,
                search: this.searchQuery,
                status: this.statusFilter,
                paymentMethod: this.methodFilter
            });

            const response = await fetch(`${this.apiBase}?${params}`);
            if (!response.ok) {
                throw new Error('获取缴费记录失败');
            }

            const data = await response.json();
            this.renderPaymentRecords(data.content || []);
            this.renderPagination(data);
        } catch (error) {
            console.error('Error loading payment records:', error);
            this.showError('加载缴费记录失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染缴费记录列表
     */
    renderPaymentRecords(records) {
        const tbody = document.querySelector('#paymentRecordsTable tbody');
        if (!tbody) return;

        if (records.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center py-4">
                        <div class="empty-state">
                            <i class="fas fa-receipt empty-icon"></i>
                            <h5>暂无缴费记录</h5>
                            <p class="text-muted">还没有任何缴费记录</p>
                            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addPaymentModal">
                                <i class="fas fa-plus me-2"></i>录入第一条缴费记录
                            </button>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = records.map(record => `
            <tr data-id="${record.id}">
                <td>
                    <small class="text-muted">${this.formatDateTime(record.paymentTime)}</small>
                </td>
                <td>
                    <div>
                        <div class="fw-bold">${this.escapeHtml(record.studentName || record.studentId)}</div>
                        <small class="text-muted">学号：${this.escapeHtml(record.studentNo || record.studentId)}</small>
                    </div>
                </td>
                <td>
                    <div>
                        <div>${this.escapeHtml(record.feeItemName || record.feeItemId)}</div>
                        <small class="text-muted">${this.escapeHtml(record.feeType || '')}</small>
                    </div>
                </td>
                <td class="amount-display amount-positive">
                    ¥${this.formatNumber(record.amount)}
                </td>
                <td>
                    <span class="badge ${this.getPaymentMethodClass(record.paymentMethod)}">
                        ${this.getPaymentMethodText(record.paymentMethod)}
                    </span>
                </td>
                <td>
                    <small class="text-muted font-monospace">${this.escapeHtml(record.transactionNo)}</small>
                </td>
                <td>
                    <span class="status-badge ${this.getStatusClass(record.status)}">
                        ${this.getStatusText(record.status)}
                    </span>
                </td>
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn btn-outline-primary btn-sm" 
                                onclick="paymentRecordsManager.viewRecord(${record.id})" title="查看详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button type="button" class="btn btn-outline-success btn-sm" 
                                onclick="paymentRecordsManager.printReceipt(${record.id})" title="打印凭证">
                            <i class="fas fa-print"></i>
                        </button>
                        ${record.status === 1 ? `
                            <button type="button" class="btn btn-outline-warning btn-sm" 
                                    onclick="paymentRecordsManager.refundPayment(${record.id})" title="退款">
                                <i class="fas fa-undo"></i>
                            </button>
                        ` : ''}
                        <button type="button" class="btn btn-outline-danger btn-sm" 
                                onclick="paymentRecordsManager.deleteRecord(${record.id})" title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    /**
     * 显示添加模态框
     */
    showAddModal() {
        this.editingRecord = null;
        const modal = document.getElementById('addPaymentModal');
        const title = modal.querySelector('.modal-title');
        const form = document.getElementById('addPaymentForm');
        
        title.textContent = '录入缴费记录';
        form.reset();
        form.classList.remove('was-validated');
        
        // 重新生成交易流水号
        this.generateTransactionNo();
        
        // 设置默认缴费时间为当前时间
        const now = new Date();
        const paymentTimeInput = form.querySelector('input[name="paymentTime"]');
        if (paymentTimeInput) {
            paymentTimeInput.value = now.toISOString().slice(0, 16);
        }
    }

    /**
     * 生成交易流水号
     */
    generateTransactionNo() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hour = String(now.getHours()).padStart(2, '0');
        const minute = String(now.getMinutes()).padStart(2, '0');
        const second = String(now.getSeconds()).padStart(2, '0');
        const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');

        const transactionNo = `PAY${year}${month}${day}${hour}${minute}${second}${random}`;
        const transactionInput = document.querySelector('input[name="transactionNo"]');
        if (transactionInput) {
            transactionInput.value = transactionNo;
        }
    }

    /**
     * 处理缴费项目变化
     */
    async handleFeeItemChange(feeItemId) {
        if (!feeItemId) return;

        try {
            const response = await fetch(`/admin/api/v1/fee-items/${feeItemId}`);
            if (response.ok) {
                const feeItem = await response.json();
                const amountInput = document.querySelector('input[name="amount"]');
                if (amountInput && feeItem.amount) {
                    amountInput.value = feeItem.amount;
                }
            }
        } catch (error) {
            console.error('Error fetching fee item:', error);
        }
    }

    /**
     * 处理表单提交
     */
    async handleFormSubmit(e) {
        e.preventDefault();
        
        const form = e.target;
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        try {
            this.showLoading();
            const formData = new FormData(form);
            const data = Object.fromEntries(formData.entries());
            
            // 设置状态为已缴费
            data.status = 1;
            
            const url = this.editingRecord ? `${this.apiBase}/${this.editingRecord.id}` : this.apiBase;
            const method = this.editingRecord ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('保存缴费记录失败');
            }

            const result = await response.json();
            this.showSuccess(this.editingRecord ? '缴费记录更新成功' : '缴费记录录入成功');
            
            // 关闭模态框
            const modal = bootstrap.Modal.getInstance(document.getElementById('addPaymentModal'));
            modal.hide();
            
            // 重新加载数据
            this.loadPaymentRecords();
            
        } catch (error) {
            console.error('Error saving payment record:', error);
            this.showError('保存失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 查看缴费记录详情
     */
    async viewRecord(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            if (!response.ok) {
                throw new Error('获取缴费记录详情失败');
            }
            
            const record = await response.json();
            this.showRecordDetails(record);
        } catch (error) {
            console.error('Error viewing record:', error);
            this.showError('获取详情失败：' + error.message);
        }
    }

    /**
     * 显示记录详情
     */
    showRecordDetails(record) {
        const modal = this.createDetailsModal(record);
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        // 模态框关闭后移除
        modal.addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modal);
        });
    }

    /**
     * 创建详情模态框
     */
    createDetailsModal(record) {
        const modal = document.createElement('div');
        modal.className = 'modal fade modal-finance';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">缴费记录详情</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>学生信息</label>
                                    <div class="form-control-plaintext">
                                        <div class="fw-bold">${this.escapeHtml(record.studentName || record.studentId)}</div>
                                        <small class="text-muted">学号：${this.escapeHtml(record.studentNo || record.studentId)}</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费项目</label>
                                    <div class="form-control-plaintext">
                                        <div>${this.escapeHtml(record.feeItemName || record.feeItemId)}</div>
                                        <small class="text-muted">${this.escapeHtml(record.feeType || '')}</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费金额</label>
                                    <div class="form-control-plaintext amount-display amount-positive">¥${this.formatNumber(record.amount)}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费方式</label>
                                    <div class="form-control-plaintext">
                                        <span class="badge ${this.getPaymentMethodClass(record.paymentMethod)}">
                                            ${this.getPaymentMethodText(record.paymentMethod)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>交易流水号</label>
                                    <div class="form-control-plaintext font-monospace">${this.escapeHtml(record.transactionNo)}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费时间</label>
                                    <div class="form-control-plaintext">${this.formatDateTime(record.paymentTime)}</div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>状态</label>
                                    <div class="form-control-plaintext">
                                        <span class="status-badge ${this.getStatusClass(record.status)}">
                                            ${this.getStatusText(record.status)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>创建时间</label>
                                    <div class="form-control-plaintext">${this.formatDateTime(record.createTime)}</div>
                                </div>
                            </div>
                        </div>
                        ${record.remarks ? `
                            <div class="form-group-finance">
                                <label>备注</label>
                                <div class="form-control-plaintext">${this.escapeHtml(record.remarks)}</div>
                            </div>
                        ` : ''}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-success" onclick="paymentRecordsManager.printReceipt(${record.id})">打印凭证</button>
                        ${record.status === 1 ? `
                            <button type="button" class="btn btn-warning" onclick="paymentRecordsManager.refundPayment(${record.id})">退款</button>
                        ` : ''}
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    /**
     * 打印缴费凭证
     */
    async printReceipt(id) {
        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/receipt`);
            if (!response.ok) {
                throw new Error('生成凭证失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            
            // 创建新窗口打印
            const printWindow = window.open(url, '_blank', 'width=800,height=600');
            printWindow.onload = function() {
                printWindow.print();
            };
            
            this.showSuccess('凭证已生成');
        } catch (error) {
            console.error('Error printing receipt:', error);
            this.showError('打印凭证失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 退款处理
     */
    async refundPayment(id) {
        if (!confirm('确定要进行退款吗？此操作不可撤销！')) {
            return;
        }

        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/refund`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('退款处理失败');
            }

            this.showSuccess('退款处理成功');
            this.loadPaymentRecords();
        } catch (error) {
            console.error('Error refunding payment:', error);
            this.showError('退款失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 删除缴费记录
     */
    async deleteRecord(id) {
        if (!confirm('确定要删除这条缴费记录吗？此操作不可撤销！')) {
            return;
        }

        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('删除缴费记录失败');
            }

            this.showSuccess('缴费记录删除成功');
            this.loadPaymentRecords();
        } catch (error) {
            console.error('Error deleting record:', error);
            this.showError('删除失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 搜索处理
     */
    handleSearch(query) {
        this.searchQuery = query;
        this.currentPage = 0;
        this.debounce(() => this.loadPaymentRecords(), 300)();
    }

    /**
     * 状态筛选
     */
    handleStatusFilter(status) {
        this.statusFilter = status;
        this.currentPage = 0;
        this.loadPaymentRecords();
    }

    /**
     * 支付方式筛选
     */
    handleMethodFilter(method) {
        this.methodFilter = method;
        this.currentPage = 0;
        this.loadPaymentRecords();
    }

    /**
     * 跳转到指定页
     */
    goToPage(page) {
        this.currentPage = page;
        this.loadPaymentRecords();
    }

    /**
     * 刷新数据
     */
    refreshData() {
        this.loadPaymentRecords();
        this.showSuccess('数据已刷新');
    }

    /**
     * 导出数据
     */
    async exportData() {
        try {
            this.showLoading();
            const params = new URLSearchParams({
                search: this.searchQuery,
                status: this.statusFilter,
                paymentMethod: this.methodFilter
            });

            const response = await fetch(`${this.apiBase}/export?${params}`);
            if (!response.ok) {
                throw new Error('导出失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `缴费记录_${new Date().toISOString().slice(0, 10)}.xlsx`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            
            this.showSuccess('导出成功');
        } catch (error) {
            console.error('Error exporting data:', error);
            this.showError('导出失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染分页
     */
    renderPagination(data) {
        const pagination = document.querySelector('.pagination-finance');
        if (!pagination || !data.totalPages || data.totalPages <= 1) {
            if (pagination) pagination.style.display = 'none';
            return;
        }

        pagination.style.display = 'flex';
        const totalPages = data.totalPages;
        const currentPage = data.number;

        let html = '';
        
        // 上一页
        html += `
            <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="paymentRecordsManager.goToPage(${currentPage - 1})" ${currentPage === 0 ? 'tabindex="-1"' : ''}>
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        for (let i = 0; i < totalPages; i++) {
            if (i === 0 || i === totalPages - 1 || Math.abs(i - currentPage) <= 2) {
                html += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="paymentRecordsManager.goToPage(${i})">${i + 1}</a>
                    </li>
                `;
            } else if (Math.abs(i - currentPage) === 3) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // 下一页
        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="paymentRecordsManager.goToPage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'tabindex="-1"' : ''}>
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `;

        pagination.innerHTML = html;
    }

    /**
     * 初始化表单验证
     */
    initializeFormValidation() {
        const form = document.getElementById('addPaymentForm');
        if (!form) return;

        // 添加自定义验证
        const amountInput = form.querySelector('input[name="amount"]');
        if (amountInput) {
            amountInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (value <= 0) {
                    this.setCustomValidity('金额必须大于0');
                } else if (value > 999999.99) {
                    this.setCustomValidity('金额不能超过999,999.99');
                } else {
                    this.setCustomValidity('');
                }
            });
        }
    }

    // 工具函数
    getPaymentMethodClass(method) {
        const classes = {
            '现金': 'bg-success',
            '银行卡': 'bg-primary',
            '支付宝': 'bg-info',
            '微信': 'bg-warning',
            '银行转账': 'bg-secondary'
        };
        return classes[method] || 'bg-secondary';
    }

    getPaymentMethodText(method) {
        return method || '未知';
    }

    getStatusClass(status) {
        const classes = {
            1: 'status-paid',
            0: 'status-pending',
            '-1': 'status-refunded'
        };
        return classes[status] || 'status-pending';
    }

    getStatusText(status) {
        const texts = {
            1: '已缴费',
            0: '待缴费',
            '-1': '已退费'
        };
        return texts[status] || '未知';
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    formatNumber(num) {
        return parseFloat(num).toLocaleString('zh-CN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('zh-CN');
    }

    formatDateTime(dateString) {
        return new Date(dateString).toLocaleString('zh-CN');
    }

    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    showLoading() {
        const loading = document.createElement('div');
        loading.className = 'loading-overlay';
        loading.innerHTML = '<div class="loading-spinner"></div>';
        loading.id = 'loadingOverlay';
        document.body.appendChild(loading);
    }

    hideLoading() {
        const loading = document.getElementById('loadingOverlay');
        if (loading) {
            document.body.removeChild(loading);
        }
    }

    showSuccess(message) {
        this.showAlert(message, 'success');
    }

    showError(message) {
        this.showAlert(message, 'danger');
    }

    showAlert(message, type) {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;

        const container = document.querySelector('.container-fluid') || document.body;
        container.insertAdjacentHTML('afterbegin', alertHtml);

        // 自动移除
        setTimeout(() => {
            const alert = container.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 5000);
    }
}

// 全局实例
let paymentRecordsManager;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    paymentRecordsManager = new PaymentRecordsManager();
});

// 全局函数（用于模板中的onclick事件）
window.paymentRecordsManager = {
    viewRecord: (id) => paymentRecordsManager?.viewRecord(id),
    printReceipt: (id) => paymentRecordsManager?.printReceipt(id),
    refundPayment: (id) => paymentRecordsManager?.refundPayment(id),
    deleteRecord: (id) => paymentRecordsManager?.deleteRecord(id),
    goToPage: (page) => paymentRecordsManager?.goToPage(page)
};