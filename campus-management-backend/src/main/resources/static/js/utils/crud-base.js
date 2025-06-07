/**
 * CRUD操作基类
 * 提供通用的增删改查功能
 * @version 1.0.0
 */
class CrudBase {
    constructor(config) {
        this.config = {
            apiEndpoint: '',
            tableName: '',
            modalId: '',
            formId: '',
            tableBodyId: '',
            paginationId: '',
            searchFormId: '',
            ...config
        };
        
        this.currentPage = 1;
        this.pageSize = 10;
        this.searchParams = {};
        this.formValidator = null;
        this.currentData = null;
    }

    /**
     * 初始化
     */
    async init() {
        try {
            this.bindEvents();
            await this.loadData();
            this.initFormValidator();
            console.log(`${this.config.tableName}管理模块初始化完成`);
        } catch (error) {
            console.error(`${this.config.tableName}管理模块初始化失败:`, error);
            MessageUtils.error(`${this.config.tableName}管理模块初始化失败: ${error.message}`);
        }
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        // 搜索表单
        const searchForm = document.getElementById(this.config.searchFormId);
        if (searchForm) {
            searchForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleSearch();
            });
        }

        // 重置按钮
        const resetBtn = document.getElementById('resetBtn');
        if (resetBtn) {
            resetBtn.addEventListener('click', () => this.resetSearch());
        }

        // 添加按钮
        const addBtn = document.getElementById('addBtn');
        if (addBtn) {
            addBtn.addEventListener('click', () => this.showAddModal());
        }

        // 保存按钮
        const saveBtn = document.getElementById('saveBtn');
        if (saveBtn) {
            saveBtn.addEventListener('click', () => this.save());
        }

        // 导出按钮
        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.exportData());
        }

        // 批量删除按钮
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        if (batchDeleteBtn) {
            batchDeleteBtn.addEventListener('click', () => this.batchDelete());
        }

        // 全选复选框
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', (e) => this.handleSelectAll(e.target.checked));
        }

        // 表格行操作
        document.addEventListener('click', (e) => {
            const target = e.target.closest('[data-action]');
            if (!target) return;

            const action = target.dataset.action;
            const id = target.dataset.id;

            switch (action) {
                case 'view':
                    this.view(id);
                    break;
                case 'edit':
                    this.edit(id);
                    break;
                case 'delete':
                    this.delete(id);
                    break;
            }
        });
    }

    /**
     * 初始化表单验证器
     */
    initFormValidator() {
        const form = document.getElementById(this.config.formId);
        if (form && this.getValidationRules) {
            this.formValidator = new FormValidator(form);
            const rules = this.getValidationRules();
            rules.forEach(rule => {
                this.formValidator.addRule(rule.field, rule.rules, rule.message);
            });
        }
    }

    /**
     * 加载数据
     */
    async loadData(page = 1) {
        try {
            LoadingManager.showPageLoading(`#${this.config.tableBodyId}`);

            const params = {
                page: page,
                size: this.pageSize,
                ...this.searchParams
            };

            const response = await apiClient.get(this.config.apiEndpoint, params);

            if (this.isResponseSuccess(response)) {
                this.renderTable(response.data);
                this.renderPagination(response.data);
                this.updateStatistics(response.data);
                this.currentPage = page;
            } else {
                throw new Error(response.message || '加载数据失败');
            }
        } catch (error) {
            console.error('加载数据失败:', error);
            MessageUtils.error('加载数据失败: ' + error.message);
        } finally {
            LoadingManager.hidePageLoading(`#${this.config.tableBodyId}`);
        }
    }

    /**
     * 渲染表格
     */
    renderTable(data) {
        const tbody = document.querySelector(`#${this.config.tableBodyId}`);
        if (!tbody) return;

        const items = data.content || data.list || data;
        if (!items || items.length === 0) {
            tbody.innerHTML = this.getEmptyTableHtml();
            return;
        }

        const rows = items.map(item => this.renderTableRow(item)).join('');
        tbody.innerHTML = rows;
    }

    /**
     * 渲染分页
     */
    renderPagination(data) {
        const pagination = document.getElementById(this.config.paginationId);
        if (!pagination) return;

        PaginationUtils.render(pagination, data, (page) => {
            this.loadData(page + 1);
        });
    }

    /**
     * 更新统计信息
     */
    updateStatistics(data) {
        // 子类可以重写此方法
    }

    /**
     * 处理搜索
     */
    handleSearch() {
        const searchForm = document.getElementById(this.config.searchFormId);
        if (!searchForm) return;

        const formData = new FormData(searchForm);
        this.searchParams = {};

        for (const [key, value] of formData.entries()) {
            if (value.trim()) {
                this.searchParams[key] = value.trim();
            }
        }

        this.loadData(1);
    }

    /**
     * 重置搜索
     */
    resetSearch() {
        const searchForm = document.getElementById(this.config.searchFormId);
        if (searchForm) {
            searchForm.reset();
        }
        this.searchParams = {};
        this.loadData(1);
    }

    /**
     * 显示添加模态框
     */
    async showAddModal() {
        try {
            await this.prepareFormData();
            this.resetForm();
            this.showModal();
        } catch (error) {
            MessageUtils.error('准备表单数据失败: ' + error.message);
        }
    }

    /**
     * 查看详情
     */
    async view(id) {
        try {
            const response = await apiClient.get(`${this.config.apiEndpoint}/${id}`);
            if (this.isResponseSuccess(response)) {
                this.showViewModal(response.data);
            } else {
                throw new Error(response.message || '获取详情失败');
            }
        } catch (error) {
            MessageUtils.error('获取详情失败: ' + error.message);
        }
    }

    /**
     * 编辑
     */
    async edit(id) {
        try {
            const [dataResponse] = await Promise.all([
                apiClient.get(`${this.config.apiEndpoint}/${id}`),
                this.prepareFormData()
            ]);

            if (this.isResponseSuccess(dataResponse)) {
                this.populateForm(dataResponse.data);
                this.showModal();
            } else {
                throw new Error(dataResponse.message || '获取数据失败');
            }
        } catch (error) {
            MessageUtils.error('获取数据失败: ' + error.message);
        }
    }

    /**
     * 保存
     */
    async save() {
        if (this.formValidator) {
            const validation = this.formValidator.validate();
            if (!validation.isValid) {
                MessageUtils.error('请检查表单输入');
                return;
            }
        }

        const saveBtn = document.getElementById('saveBtn');
        try {
            LoadingManager.showButtonLoading(saveBtn, '保存中...');

            const formData = this.getFormData();
            const id = this.getCurrentId();
            let response;

            if (id) {
                response = await apiClient.put(`${this.config.apiEndpoint}/${id}`, formData);
            } else {
                response = await apiClient.post(this.config.apiEndpoint, formData);
            }

            if (this.isResponseSuccess(response)) {
                MessageUtils.success(response.message || '保存成功');
                this.hideModal();
                this.loadData(this.currentPage);
            } else {
                throw new Error(response.message || '保存失败');
            }
        } catch (error) {
            MessageUtils.error('保存失败: ' + error.message);
        } finally {
            LoadingManager.hideButtonLoading(saveBtn);
        }
    }

    /**
     * 删除
     */
    async delete(id) {
        const confirmed = await MessageUtils.confirm('确定要删除这条记录吗？此操作不可撤销。', '删除确认');
        if (!confirmed) return;

        try {
            const response = await apiClient.delete(`${this.config.apiEndpoint}/${id}`);
            if (this.isResponseSuccess(response)) {
                MessageUtils.success('删除成功');
                this.loadData(this.currentPage);
            } else {
                throw new Error(response.message || '删除失败');
            }
        } catch (error) {
            MessageUtils.error('删除失败: ' + error.message);
        }
    }

    /**
     * 批量删除
     */
    async batchDelete() {
        const selectedIds = this.getSelectedIds();
        if (selectedIds.length === 0) {
            MessageUtils.warning('请选择要删除的记录');
            return;
        }

        const confirmed = await MessageUtils.confirm(`确定要删除选中的 ${selectedIds.length} 条记录吗？此操作不可撤销。`, '批量删除确认');
        if (!confirmed) return;

        try {
            const response = await apiClient.delete(`${this.config.apiEndpoint}/batch`, selectedIds);
            if (this.isResponseSuccess(response)) {
                MessageUtils.success('批量删除成功');
                this.loadData(this.currentPage);
            } else {
                throw new Error(response.message || '批量删除失败');
            }
        } catch (error) {
            MessageUtils.error('批量删除失败: ' + error.message);
        }
    }

    /**
     * 导出数据
     */
    async exportData() {
        try {
            const params = { ...this.searchParams, export: true };
            const response = await apiClient.get(`${this.config.apiEndpoint}/export`, params);
            
            if (this.isResponseSuccess(response)) {
                const filename = `${this.config.tableName}_${DataFormatter.formatDate(new Date(), 'YYYY-MM-DD')}.csv`;
                ExportUtils.exportToCSV(response.data, filename, this.getExportHeaders());
                MessageUtils.success('导出成功');
            } else {
                throw new Error(response.message || '导出失败');
            }
        } catch (error) {
            MessageUtils.error('导出失败: ' + error.message);
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 检查响应是否成功
     */
    isResponseSuccess(response) {
        return response && (response.success === true || response.code === 200);
    }

    /**
     * 显示模态框
     */
    showModal() {
        const modal = document.getElementById(this.config.modalId);
        if (modal) {
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
        }
    }

    /**
     * 隐藏模态框
     */
    hideModal() {
        const modal = document.getElementById(this.config.modalId);
        if (modal) {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) {
                bsModal.hide();
            }
        }
    }

    /**
     * 获取选中的ID列表
     */
    getSelectedIds() {
        const checkboxes = document.querySelectorAll(`#${this.config.tableBodyId} input[type="checkbox"]:checked`);
        return Array.from(checkboxes).map(cb => cb.value).filter(id => id);
    }

    /**
     * 处理全选
     */
    handleSelectAll(checked) {
        const checkboxes = document.querySelectorAll(`#${this.config.tableBodyId} input[type="checkbox"]`);
        checkboxes.forEach(cb => cb.checked = checked);
        this.updateBatchButtons();
    }

    /**
     * 更新批量操作按钮状态
     */
    updateBatchButtons() {
        const selectedCount = this.getSelectedIds().length;
        const batchDeleteBtn = document.getElementById('batchDeleteBtn');
        if (batchDeleteBtn) {
            batchDeleteBtn.disabled = selectedCount === 0;
        }
    }

    // ==================== 需要子类实现的方法 ====================

    /**
     * 渲染表格行 - 子类必须实现
     */
    renderTableRow(item) {
        throw new Error('子类必须实现 renderTableRow 方法');
    }

    /**
     * 获取空表格HTML - 子类可以重写
     */
    getEmptyTableHtml() {
        return `
            <tr>
                <td colspan="100%" class="text-center text-muted py-4">
                    <i class="fas fa-inbox fa-2x mb-2"></i><br>
                    暂无数据
                </td>
            </tr>
        `;
    }

    /**
     * 获取表单数据 - 子类必须实现
     */
    getFormData() {
        throw new Error('子类必须实现 getFormData 方法');
    }

    /**
     * 填充表单 - 子类必须实现
     */
    populateForm(data) {
        throw new Error('子类必须实现 populateForm 方法');
    }

    /**
     * 重置表单 - 子类必须实现
     */
    resetForm() {
        throw new Error('子类必须实现 resetForm 方法');
    }

    /**
     * 获取当前编辑的ID - 子类必须实现
     */
    getCurrentId() {
        throw new Error('子类必须实现 getCurrentId 方法');
    }

    /**
     * 准备表单数据 - 子类可以重写
     */
    async prepareFormData() {
        // 默认实现为空，子类可以重写
    }

    /**
     * 获取验证规则 - 子类可以重写
     */
    getValidationRules() {
        return [];
    }

    /**
     * 获取导出表头 - 子类可以重写
     */
    getExportHeaders() {
        return [];
    }

    /**
     * 显示查看模态框 - 子类可以重写
     */
    showViewModal(data) {
        // 默认实现为空，子类可以重写
    }
}

// 暴露到全局
window.CrudBase = CrudBase;
