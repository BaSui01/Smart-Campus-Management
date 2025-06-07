/**
 * 费用项目管理模块
 */

class FeeItemsManager {
    constructor() {
        this.apiBase = '/admin/api/fee-items';
        this.currentPage = 0;
        this.pageSize = 10;
        this.searchQuery = '';
        this.statusFilter = '';
        this.typeFilter = '';
        this.editingItem = null;
        this.init();
    }

    /**
     * 初始化
     */
    init() {
        this.bindEvents();
        this.loadFeeItems();
        this.initializeFormValidation();
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 添加费用项目按钮
        const addBtn = document.querySelector('[data-bs-target="#addFeeItemModal"]');
        if (addBtn) {
            addBtn.addEventListener('click', () => this.showAddModal());
        }

        // 表单提交
        const addForm = document.getElementById('addFeeItemForm');
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

        const typeFilter = document.getElementById('typeFilter');
        if (typeFilter) {
            typeFilter.addEventListener('change', (e) => this.handleTypeFilter(e.target.value));
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
     * 加载费用项目列表
     */
    async loadFeeItems() {
        try {
            this.showLoading();
            const params = new URLSearchParams({
                page: this.currentPage,
                size: this.pageSize,
                search: this.searchQuery,
                status: this.statusFilter,
                type: this.typeFilter
            });

            const response = await fetch(`${this.apiBase}?${params}`);
            if (!response.ok) {
                throw new Error('获取费用项目失败');
            }

            const data = await response.json();
            this.renderFeeItems(data.content || []);
            this.renderPagination(data);
        } catch (error) {
            console.error('Error loading fee items:', error);
            this.showError('加载费用项目失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 渲染费用项目列表
     */
    renderFeeItems(items) {
        const tbody = document.querySelector('#feeItemsTable tbody');
        if (!tbody) return;

        if (items.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="empty-state">
                            <i class="fas fa-inbox empty-icon"></i>
                            <h5>暂无费用项目</h5>
                            <p class="text-muted">还没有添加任何费用项目</p>
                            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addFeeItemModal">
                                <i class="fas fa-plus me-2"></i>添加第一个费用项目
                            </button>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tbody.innerHTML = items.map(item => `
            <tr data-id="${item.id}">
                <td>
                    <div class="d-flex align-items-center">
                        <div>
                            <div class="fw-bold">${this.escapeHtml(item.itemName)}</div>
                            <small class="text-muted">${this.escapeHtml(item.itemCode)}</small>
                        </div>
                    </div>
                </td>
                <td>
                    <span class="badge bg-secondary">${this.escapeHtml(item.feeType)}</span>
                </td>
                <td class="amount-display amount-positive">
                    ¥${this.formatNumber(item.amount)}
                </td>
                <td>
                    <span class="status-badge ${item.status === 1 ? 'status-paid' : 'status-pending'}">
                        ${item.status === 1 ? '启用' : '禁用'}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${this.formatDateTime(item.createTime)}</small>
                </td>
                <td>
                    <div class="action-buttons">
                        <button type="button" class="btn btn-outline-primary btn-sm" 
                                onclick="feeItemsManager.viewItem(${item.id})" title="查看详情">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button type="button" class="btn btn-outline-success btn-sm" 
                                onclick="feeItemsManager.editItem(${item.id})" title="编辑">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger btn-sm" 
                                onclick="feeItemsManager.deleteItem(${item.id})" title="删除">
                            <i class="fas fa-trash"></i>
                        </button>
                        <button type="button" class="btn btn-outline-info btn-sm" 
                                onclick="feeItemsManager.toggleStatus(${item.id}, ${item.status})" 
                                title="${item.status === 1 ? '禁用' : '启用'}">
                            <i class="fas fa-${item.status === 1 ? 'times' : 'check'}"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
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
                <a class="page-link" href="#" onclick="feeItemsManager.goToPage(${currentPage - 1})" ${currentPage === 0 ? 'tabindex="-1"' : ''}>
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `;

        // 页码
        for (let i = 0; i < totalPages; i++) {
            if (i === 0 || i === totalPages - 1 || Math.abs(i - currentPage) <= 2) {
                html += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="feeItemsManager.goToPage(${i})">${i + 1}</a>
                    </li>
                `;
            } else if (Math.abs(i - currentPage) === 3) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        // 下一页
        html += `
            <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="feeItemsManager.goToPage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'tabindex="-1"' : ''}>
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `;

        pagination.innerHTML = html;
    }

    /**
     * 显示添加模态框
     */
    showAddModal() {
        this.editingItem = null;
        const modal = document.getElementById('addFeeItemModal');
        const title = modal.querySelector('.modal-title');
        const form = document.getElementById('addFeeItemForm');
        
        title.textContent = '添加费用项目';
        form.reset();
        form.classList.remove('was-validated');
        
        // 生成项目编码
        this.generateItemCode();
    }

    /**
     * 显示编辑模态框
     */
    async showEditModal(item) {
        this.editingItem = item;
        const modal = document.getElementById('addFeeItemModal');
        const title = modal.querySelector('.modal-title');
        const form = document.getElementById('addFeeItemForm');
        
        title.textContent = '编辑费用项目';
        
        // 填充表单数据
        this.populateForm(form, item);
        
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
     * 生成项目编码
     */
    generateItemCode() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');
        
        const code = `FEE${year}${month}${day}${random}`;
        const codeInput = document.querySelector('input[name="itemCode"]');
        if (codeInput) {
            codeInput.value = code;
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
            
            const url = this.editingItem ? `${this.apiBase}/${this.editingItem.id}` : this.apiBase;
            const method = this.editingItem ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                throw new Error('保存费用项目失败');
            }

            const result = await response.json();
            this.showSuccess(this.editingItem ? '费用项目更新成功' : '费用项目添加成功');
            
            // 关闭模态框
            const modal = bootstrap.Modal.getInstance(document.getElementById('addFeeItemModal'));
            modal.hide();
            
            // 重新加载数据
            this.loadFeeItems();
            
        } catch (error) {
            console.error('Error saving fee item:', error);
            this.showError('保存失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 查看费用项目详情
     */
    async viewItem(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            if (!response.ok) {
                throw new Error('获取费用项目详情失败');
            }
            
            const item = await response.json();
            this.showItemDetails(item);
        } catch (error) {
            console.error('Error viewing item:', error);
            this.showError('获取详情失败：' + error.message);
        }
    }

    /**
     * 显示项目详情
     */
    showItemDetails(item) {
        const modal = this.createDetailsModal(item);
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
    createDetailsModal(item) {
        const modal = document.createElement('div');
        modal.className = 'modal fade modal-finance';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">费用项目详情</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>项目编码</label>
                                    <div class="form-control-plaintext">${this.escapeHtml(item.itemCode)}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>项目名称</label>
                                    <div class="form-control-plaintext">${this.escapeHtml(item.itemName)}</div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>费用类型</label>
                                    <div class="form-control-plaintext">${this.escapeHtml(item.feeType)}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>金额</label>
                                    <div class="form-control-plaintext amount-display amount-positive">¥${this.formatNumber(item.amount)}</div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>适用年级</label>
                                    <div class="form-control-plaintext">${this.escapeHtml(item.applicableGrade || '全年级')}</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>截止日期</label>
                                    <div class="form-control-plaintext">${item.dueDate ? this.formatDate(item.dueDate) : '无限制'}</div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group-finance">
                            <label>项目描述</label>
                            <div class="form-control-plaintext">${this.escapeHtml(item.description || '无')}</div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>状态</label>
                                    <div class="form-control-plaintext">
                                        <span class="status-badge ${item.status === 1 ? 'status-paid' : 'status-pending'}">
                                            ${item.status === 1 ? '启用' : '禁用'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group-finance">
                                    <label>创建时间</label>
                                    <div class="form-control-plaintext">${this.formatDateTime(item.createTime)}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-primary" onclick="feeItemsManager.editItem(${item.id})">编辑</button>
                    </div>
                </div>
            </div>
        `;
        return modal;
    }

    /**
     * 编辑费用项目
     */
    async editItem(id) {
        try {
            const response = await fetch(`${this.apiBase}/${id}`);
            if (!response.ok) {
                throw new Error('获取费用项目信息失败');
            }
            
            const item = await response.json();
            this.showEditModal(item);
        } catch (error) {
            console.error('Error editing item:', error);
            this.showError('获取编辑信息失败：' + error.message);
        }
    }

    /**
     * 删除费用项目
     */
    async deleteItem(id) {
        if (!confirm('确定要删除这个费用项目吗？此操作不可撤销！')) {
            return;
        }

        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('删除费用项目失败');
            }

            this.showSuccess('费用项目删除成功');
            this.loadFeeItems();
        } catch (error) {
            console.error('Error deleting item:', error);
            this.showError('删除失败：' + error.message);
        } finally {
            this.hideLoading();
        }
    }

    /**
     * 切换状态
     */
    async toggleStatus(id, currentStatus) {
        const newStatus = currentStatus === 1 ? 0 : 1;
        const action = newStatus === 1 ? '启用' : '禁用';
        
        if (!confirm(`确定要${action}这个费用项目吗？`)) {
            return;
        }

        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/${id}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) {
                throw new Error(`${action}费用项目失败`);
            }

            this.showSuccess(`费用项目${action}成功`);
            this.loadFeeItems();
        } catch (error) {
            console.error('Error toggling status:', error);
            this.showError(`${action}失败：${error.message}`);
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
        this.debounce(() => this.loadFeeItems(), 300)();
    }

    /**
     * 状态筛选
     */
    handleStatusFilter(status) {
        this.statusFilter = status;
        this.currentPage = 0;
        this.loadFeeItems();
    }

    /**
     * 类型筛选
     */
    handleTypeFilter(type) {
        this.typeFilter = type;
        this.currentPage = 0;
        this.loadFeeItems();
    }

    /**
     * 跳转到指定页
     */
    goToPage(page) {
        this.currentPage = page;
        this.loadFeeItems();
    }

    /**
     * 刷新数据
     */
    refreshData() {
        this.loadFeeItems();
        this.showSuccess('数据已刷新');
    }

    /**
     * 导出数据
     */
    async exportData() {
        try {
            this.showLoading();
            const response = await fetch(`${this.apiBase}/export`, {
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error('导出失败');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `费用项目_${new Date().toISOString().slice(0, 10)}.xlsx`;
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
     * 初始化表单验证
     */
    initializeFormValidation() {
        const form = document.getElementById('addFeeItemForm');
        if (!form) return;

        // 添加自定义验证
        const amountInput = form.querySelector('input[name="amount"]');
        if (amountInput) {
            amountInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (value < 0) {
                    this.setCustomValidity('金额不能为负数');
                } else if (value > 999999.99) {
                    this.setCustomValidity('金额不能超过999,999.99');
                } else {
                    this.setCustomValidity('');
                }
            });
        }
    }

    // 工具函数
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
        // 显示加载状态
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
let feeItemsManager;

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    feeItemsManager = new FeeItemsManager();
});

// 全局函数（用于模板中的onclick事件）
window.feeItemsManager = {
    viewItem: (id) => feeItemsManager?.viewItem(id),
    editItem: (id) => feeItemsManager?.editItem(id),
    deleteItem: (id) => feeItemsManager?.deleteItem(id),
    toggleStatus: (id, status) => feeItemsManager?.toggleStatus(id, status),
    goToPage: (page) => feeItemsManager?.goToPage(page)
};