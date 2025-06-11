/**
 * 缴费管理模块
 */

class PaymentsManager {
    constructor() {
        this.apiBase = '/admin/api/v1/payments';
        this.currentPage = 0;
        this.pageSize = 10;
        this.searchQuery = '';
        this.statusFilter = '';
        this.editingPayment = null;
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadPayments();
        this.initializeFormValidation();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 添加缴费记录按钮
        const addBtn = document.querySelector('[data-bs-target="#paymentModal"]');
        if (addBtn) {
            addBtn.addEventListener('click', () => this.showAddModal());
        }

        // 表单提交
        const form = document.getElementById('paymentForm');
        if (form) {
            form.addEventListener('submit', (e) => this.handleFormSubmit(e));
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
    }

    /**
     * 加载缴费管理列表
     */
    async loadPayments() {
        try {
            this.showLoading();
            const params = new URLSearchParams({
                page: this.currentPage,
                size: this.pageSize,
                search: this.searchQuery,
                status: this.statusFilter
            });

            const response = await fetch(`${this.apiBase}?${params}`);
            if (!response.ok) {
                throw new Error('获取缴费信息失败');
            }

            const data = await response.json();
            this.renderPayments(data.content || []);
            this.renderPagination(data);
        } catch (error) {
            console.error('Error loading payments:', error);
            this.showError('加载缴费信息失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染缴费管理列表
     */
    renderPayments(payments) {
        const tbody = document.querySelector('#paymentsTable tbody');
        if (!tbody) return;

        if (payments.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <div class="empty-state">
                            <i class="fas fa-credit-card empty-icon"></i>
                            <h5>暂无缴费记录</h5>
                            <p class="text-muted">还没有任何缴费记录数据</p>
                            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#paymentModal">
                                <i class="fas fa-plus me-2"></i>添加第一条缴费记录
                            </button>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = payments.map(payment => `
            <tr data-id="${payment.id}">
                <td>
                    <div>
                        <div class="fw-bold">${this.escapeHtml(payment.studentName || payment.studentId)}</div>
                        <small class="text-muted">学号：${this.escapeHtml(payment.studentNo || payment.studentId)}</small>
                    </div>
                </td>
                <td>
                    <div>
                        <div>${this.escapeHtml(payment.feeItemName || payment.feeType)}</div>
                        <small class="text-muted">${this.escapeHtml(payment.feeType || '')}</small>
                    </div>
                </td>
                <td class="amount-display amount-positive">
                    ¥${this.formatNumber(payment.amountDue || 0)}
                </td>
                <td class="amount-display ${payment.amountPaid > 0 ? 'amount-positive' : 'amount-zero'}">
                    ¥${this.formatNumber(payment.amountPaid || 0)}
                </td>
                <td>
                    <span class="status-badge ${this.getStatusClass(payment.status)}">
                        ${this.getStatusText(payment.status)}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${payment.paymentTime ? this.formatDateTime(payment.paymentTime) : '未缴费'}</small>
                </td>
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn btn-outline-primary btn-sm" 
                                onclick="paymentsManager.viewPayment(${payment.id})" title="查看详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button type="button" class="btn btn-outline-success btn-sm" 
                                onclick="paymentsManager.editPayment(${payment.id})" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        ${payment.status === 'pending' ? `
                            <button type="button" class="btn btn-outline-info btn-sm" 
                                    onclick="paymentsManager.processPayment(${payment.id})" title="处理缴费">
                                <i class="fas fa-dollar-sign"></i>
                            </button>
                            <button type="button" class="btn btn-outline-warning btn-sm" 
                                    onclick="paymentsManager.sendReminder(${payment.id})" title="发送提醒">
                                <i class="fas fa-bell"></i>
                            </button>
                        ` : ''}
                        ${payment.status === 'overdue' ? `
                            <button type="button" class="btn btn-outline-danger btn-sm" 
                                    onclick="paymentsManager.sendOverdueNotice(${payment.id})" title="逾期通知">
                                <i class="fas fa-exclamation-triangle"></i>
                            </button>
                        ` : ''}
                        ${payment.status === 'paid' ? `
                            <button type="button" class="btn btn-outline-success btn-sm" 
                                    onclick="paymentsManager.printReceipt(${payment.id})" title="打印凭证">
                                <i class="fas fa-print"></i>
                            </button>
                            <button type="button" class="btn btn-outline-warning btn-sm" 
                                    onclick="paymentsManager.refundPayment(${payment.id})" title="退费">
                                <i class="fas fa-undo"></i>
                            </button>
                        ` : ''}
                        ${payment.status === 'refunded' ? `
                            <button type="button" class="btn btn-outline-info btn-sm" 
                                    onclick="paymentsManager.viewRefundDetails(${payment.id})" title="退费详情">
                                <i class="fas fa-info-circle"></i>
                            </button>
                        ` : ''}
                        <button type="button" class="btn btn-outline-danger btn-sm" 
                                onclick="paymentsManager.deletePayment(${payment.id})" title="删除">
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
        this.editingPayment = null;
        const modal = document.getElementById('paymentModal');
        const title = modal.querySelector('.modal-title');
        const form = document.getElementById('paymentForm');
        
        title.textContent = '添加缴费记录';
        form.reset();
        form.classList.remove('was-validated');
    }

    /**
     * 显示编辑模态框
     */
    async showEditModal(payment) {
        this.editingPayment = payment;
        const modal = document.getElementById('paymentModal');
        const title = modal.querySelector('.modal-title');
        const form = document.getElementById('paymentForm');
        
        title.textContent = '编辑缴费记录';
        
        // 填充表单数据
        this.populateForm(form, payment);
        
        // 显示模态框
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    }

    /**
     * 填充表单数据
     */
    populateForm(form, data) {
        Object.keys(data).forEach(key => {
            const input = form.querySelector(`[name="${key}"]`);
            if (input) {
                if (input.type === 'checkbox') {
                    input.checked = data[key];
                } else {
                    input.value = data[key] || '';
                }
            }
        });
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
            
            const url = this.editingPayment ? `${this.apiBase}/${this.editingPayment.id}` : this.apiBase;
            const method = this.editingPayment ? 'PUT' : 'POST';
            
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
            this.showSuccess(this.editingPayment ? '缴费记录更新成功' : '缴费记录添加成功');
            
            // 关闭模态框
            const modal = bootstrap.Modal.getInstance(document.getElementById('paymentModal'));
            modal.hide();
            
            // 重新加载数据
            this.loadPayments();
            
        } catch (error) {
            console.error('Error saving payment:', error);
            this.showError('保存失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 查看缴费详情
     */
    async viewPayment(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            if (!response.ok) {
                throw new Error('获取缴费详情失败');
            }
            
            const payment = await response.json();
            this.showPaymentDetails(payment);
        } catch (error) {
            console.error('Error viewing payment:', error);
            this.showError('获取详情失败：' + error.message);
        }
    }

    /**
     * 显示缴费详情
     */
    showPaymentDetails(payment) {
        const modal = this.createDetailsModal(payment);
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
    createDetailsModal(payment) {
        const modal = document.createElement('div');
        modal.className = 'modal fade modal-finance';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">缴费详情</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>学生信息</label>
                                    <div class="form-control-plaintext">
                                        <div class="fw-bold">${this.escapeHtml(payment.studentName || payment.studentId)}</div>
                                        <small class="text-muted">学号：${this.escapeHtml(payment.studentNo || payment.studentId)}</small>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费项目</label>
                                    <div class="form-control-plaintext">
                                        <div>${this.escapeHtml(payment.feeItemName || payment.feeType)}</div>
                                        <small class="text-muted">${this.escapeHtml(payment.feeType || '')}</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>应缴金额</label>
                                    <div class="form-control-plaintext amount-display amount-positive">¥${this.formatNumber(payment.amountDue || 0)}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>实缴金额</label>
                                    <div class="form-control-plaintext amount-display ${payment.amountPaid > 0 ? 'amount-positive' : 'amount-zero'}">¥${this.formatNumber(payment.amountPaid || 0)}</div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费方式</label>
                                    <div class="form-control-plaintext">${this.escapeHtml(payment.paymentMethod || '未设置')}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费状态</label>
                                    <div class="form-control-plaintext">
                                        <span class="status-badge ${this.getStatusClass(payment.status)}">
                                            ${this.getStatusText(payment.status)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>缴费时间</label>
                                    <div class="form-control-plaintext">${payment.paymentTime ? this.formatDateTime(payment.paymentTime) : '未缴费'}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>创建时间</label>
                                    <div class="form-control-plaintext">${this.formatDateTime(payment.createTime)}</div>
                                </div>
                            </div>
                        </div>
                        ${payment.remarks ? `
                            <div class="form-group-finance">
                                <label>备注</label>
                                <div class="form-control-plaintext">${this.escapeHtml(payment.remarks)}</div>
                            </div>
                        ` : ''}
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" onclick="paymentsManager.editPayment(${payment.id})">编辑</button>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    /**
     * 编辑缴费记录
     */
    async editPayment(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            if (!response.ok) {
                throw new Error('获取缴费信息失败');
            }
            
            const payment = await response.json();
            this.showEditModal(payment);
        } catch (error) {
            console.error('Error editing payment:', error);
            this.showError('获取编辑信息失败：' + error.message);
        }
    }

    /**
     * 处理缴费
     */
    async processPayment(id) {
        if (!confirm('确定要处理这笔缴费吗？')) {
            return;
        }

        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/process`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('处理缴费失败');
            }

            this.showSuccess('缴费处理成功');
            this.loadPayments();
        } catch (error) {
            console.error('Error processing payment:', error);
            this.showError('处理失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 发送缴费提醒
     */
    async sendReminder(id) {
        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/reminder`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('发送提醒失败');
            }

            this.showSuccess('缴费提醒发送成功');
        } catch (error) {
            console.error('Error sending reminder:', error);
            this.showError('发送提醒失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 发送逾期通知
     */
    async sendOverdueNotice(id) {
        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/overdue-notice`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('发送逾期通知失败');
            }

            this.showSuccess('逾期通知发送成功');
        } catch (error) {
            console.error('Error sending overdue notice:', error);
            this.showError('发送逾期通知失败：' + error.message);
        } finally {
            this.hideLoading();
        }
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
     * 退费处理
     */
    async refundPayment(id) {
        if (!confirm('确定要进行退费吗？此操作不可撤销！')) {
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
                throw new Error('退费处理失败');
            }

            this.showSuccess('退费处理成功');
            this.loadPayments();
        } catch (error) {
            console.error('Error refunding payment:', error);
            this.showError('退费失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 查看退费详情
     */
    async viewRefundDetails(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}/refund-details`);
            if (!response.ok) {
                throw new Error('获取退费详情失败');
            }
            
            const details = await response.json();
            this.showRefundDetails(details);
        } catch (error) {
            console.error('Error viewing refund details:', error);
            this.showError('获取退费详情失败：' + error.message);
        }
    }

    /**
     * 显示退费详情
     */
    showRefundDetails(details) {
        const modal = this.createRefundDetailsModal(details);
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        // 模态框关闭后移除
        modal.addEventListener('hidden.bs.modal', () => {
            document.body.removeChild(modal);
        });
    }

    /**
     * 创建退费详情模态框
     */
    createRefundDetailsModal(details) {
        const modal = document.createElement('div');
        modal.className = 'modal fade modal-finance';
        modal.innerHTML = `
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">退费详情</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group-finance">
                            <label>退费金额</label>
                            <div class="form-control-plaintext amount-display amount-negative">¥${this.formatNumber(details.refundAmount)}</div>
                        </div>
                        <div class="form-group-finance">
                            <label>退费时间</label>
                            <div class="form-control-plaintext">${this.formatDateTime(details.refundTime)}</div>
                        </div>
                        <div class="form-group-finance">
                            <label>退费原因</label>
                            <div class="form-control-plaintext">${this.escapeHtml(details.refundReason || '无')}</div>
                        </div>
                        <div class="form-group-finance">
                            <label>处理人员</label>
                            <div class="form-control-plaintext">${this.escapeHtml(details.refundOperator || '未知')}</div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    /**
     * 删除缴费记录
     */
    async deletePayment(id) {
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
            this.loadPayments();
        } catch (error) {
            console.error('Error deleting payment:', error);
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
        this.debounce(() => this.loadPayments(), 300)();
    }

    /**
     * 状态筛选
     */
    handleStatusFilter(status) {
        this.statusFilter = status;
        this.currentPage = 0;
        this.loadPayments();
    }

    /**
     * 跳转到指定页
     */
    goToPage(page) {
        this.currentPage = page;
        this.loadPayments();
    }

    /**
     * 刷新数据
     */
    refreshData() {
        this.loadPayments();
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
                status: this.statusFilter
            });

            const response = await fetch(`${this.apiBase}/export?${params}`);
            if (!response.ok) {
                throw new Error('导出失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `缴费管理_${new Date().toISOString().slice(0, 10)}.xlsx`;
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
                <a class="page-link" href="#" onclick="paymentsManager.goToPage(${currentPage - 1})" ${currentPage === 0 ? 'tabindex="-1"' : ''}>
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        for (let i = 0; i < totalPages; i++) {
            if (i === 0 || i === totalPages - 1 || Math.abs(i - currentPage) <= 2) {
                html += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="paymentsManager.goToPage(${i})">${i + 1}</a>
                    </li>
                `;
            } else if (Math.abs(i - currentPage) === 3) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // 下一页
        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="paymentsManager.goToPage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'tabindex="-1"' : ''}>
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
        const form = document.getElementById('paymentForm');
        if (!form) return;

        // 添加自定义验证
        const amountDueInput = form.querySelector('input[name="amountDue"]');
        if (amountDueInput) {
            amountDueInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (value <= 0) {
                    this.setCustomValidity('应缴金额必须大于0');
                } else if (value > 999999.99) {
                    this.setCustomValidity('金额不能超过999,999.99');
                } else {
                    this.setCustomValidity('');
                }
            });
        }

        const amountPaidInput = form.querySelector('input[name="amountPaid"]');
        if (amountPaidInput) {
            amountPaidInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (value < 0) {
                    this.setCustomValidity('实缴金额不能为负数');
                } else if (value > 999999.99) {
                    this.setCustomValidity('金额不能超过999,999.99');
                } else {
                    this.setCustomValidity('');
                }
            });
        }
    }

    // 工具函数
    getStatusClass(status) {
        const classes = {
            'paid': 'status-paid',
            'pending': 'status-pending',
            'overdue': 'status-overdue',
            'refunded': 'status-refunded'
        };
        return classes[status] || 'status-pending';
    }

    getStatusText(status) {
        const texts = {
            'paid': '已缴费',
            'pending': '待缴费',
            'overdue': '逾期未缴',
            'refunded': '已退费'
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
let paymentsManager;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    paymentsManager = new PaymentsManager();
});

// 全局函数（用于模板中的onclick事件）
window.paymentsManager = {
    viewPayment: (id) => paymentsManager?.viewPayment(id),
    editPayment: (id) => paymentsManager?.editPayment(id),
    processPayment: (id) => paymentsManager?.processPayment(id),
    sendReminder: (id) => paymentsManager?.sendReminder(id),
    sendOverdueNotice: (id) => paymentsManager?.sendOverdueNotice(id),
    printReceipt: (id) => paymentsManager?.printReceipt(id),
    refundPayment: (id) => paymentsManager?.refundPayment(id),
    viewRefundDetails: (id) => paymentsManager?.viewRefundDetails(id),
    deletePayment: (id) => paymentsManager?.deletePayment(id),
    goToPage: (page) => paymentsManager?.goToPage(page)
};