/**
 * 数据表格组件
 * Data Table Component
 */

class DataTableComponent {
    constructor(containerId, options = {}) {
        this.containerId = containerId;
        this.container = document.getElementById(containerId);
        
        if (!this.container) {
            throw new Error(`Container with id "${containerId}" not found`);
        }

        // 默认配置
        this.config = {
            // API配置
            apiUrl: '',
            method: 'GET',
            
            // 分页配置
            pagination: true,
            pageSize: 20,
            pageSizes: [10, 20, 50, 100],
            
            // 搜索配置
            searchable: true,
            searchPlaceholder: '搜索...',
            
            // 排序配置
            sortable: true,
            
            // 选择配置
            selectable: false,
            multiSelect: false,
            
            // 操作配置
            actions: [],
            
            // 样式配置
            striped: true,
            bordered: true,
            hover: true,
            responsive: true,
            
            // 加载配置
            loading: true,
            loadingText: '加载中...',
            emptyText: '暂无数据',
            errorText: '加载失败',
            
            // 列配置
            columns: [],
            
            // 事件回调
            onRowClick: null,
            onRowSelect: null,
            onDataLoad: null,
            onError: null,
            
            ...options
        };

        this.data = [];
        this.filteredData = [];
        this.currentPage = 1;
        this.totalPages = 1;
        this.totalRecords = 0;
        this.selectedRows = new Set();
        this.sortColumn = null;
        this.sortDirection = 'asc';
        this.searchQuery = '';
        
        this.isInitialized = false;
        this.isLoading = false;
    }

    /**
     * 初始化表格
     */
    async init() {
        try {
            console.log(`初始化数据表格: ${this.containerId}`);
            
            // 创建表格结构
            this.createTableStructure();
            
            // 绑定事件
            this.bindEvents();
            
            // 加载数据
            if (this.config.apiUrl) {
                await this.loadData();
            }
            
            this.isInitialized = true;
            console.log(`数据表格初始化完成: ${this.containerId}`);
            
        } catch (error) {
            console.error('数据表格初始化失败:', error);
            this.showError(this.config.errorText);
        }
    }

    /**
     * 创建表格结构
     */
    createTableStructure() {
        const tableHtml = `
            <div class="data-table-container">
                <!-- 表格工具栏 -->
                <div class="table-toolbar">
                    <div class="row align-items-center">
                        <div class="col-md-6">
                            <div class="table-info">
                                <span class="total-records">共 <strong>0</strong> 条记录</span>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="table-controls">
                                ${this.config.searchable ? this.createSearchBox() : ''}
                                ${this.config.actions.length > 0 ? this.createActionButtons() : ''}
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 表格容器 -->
                <div class="table-wrapper ${this.config.responsive ? 'table-responsive' : ''}">
                    <table class="table data-table ${this.getTableClasses()}">
                        <thead class="table-header">
                            ${this.createTableHeader()}
                        </thead>
                        <tbody class="table-body">
                            <!-- 数据行将在这里动态生成 -->
                        </tbody>
                    </table>
                </div>
                
                <!-- 加载状态 -->
                <div class="table-loading" style="display: none;">
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">${this.config.loadingText}</span>
                        </div>
                        <div class="mt-2">${this.config.loadingText}</div>
                    </div>
                </div>
                
                <!-- 空状态 -->
                <div class="table-empty" style="display: none;">
                    <div class="text-center py-4">
                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                        <div class="text-muted">${this.config.emptyText}</div>
                    </div>
                </div>
                
                <!-- 错误状态 -->
                <div class="table-error" style="display: none;">
                    <div class="text-center py-4">
                        <i class="fas fa-exclamation-triangle fa-3x text-danger mb-3"></i>
                        <div class="text-danger">${this.config.errorText}</div>
                        <button class="btn btn-outline-primary mt-2" onclick="this.retry()">
                            <i class="fas fa-redo me-1"></i>重试
                        </button>
                    </div>
                </div>
                
                <!-- 分页 -->
                ${this.config.pagination ? this.createPagination() : ''}
            </div>
        `;
        
        this.container.innerHTML = tableHtml;
    }

    /**
     * 创建搜索框
     */
    createSearchBox() {
        return `
            <div class="search-box">
                <div class="input-group">
                    <input type="text" class="form-control search-input" 
                           placeholder="${this.config.searchPlaceholder}">
                    <button class="btn btn-outline-secondary search-btn" type="button">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
        `;
    }

    /**
     * 创建操作按钮
     */
    createActionButtons() {
        const buttons = this.config.actions.map(action => {
            const btnClass = action.class || 'btn-primary';
            const icon = action.icon ? `<i class="${action.icon} me-1"></i>` : '';
            return `
                <button class="btn ${btnClass} action-btn" 
                        data-action="${action.name}"
                        ${action.disabled ? 'disabled' : ''}>
                    ${icon}${action.label}
                </button>
            `;
        }).join('');
        
        return `<div class="action-buttons">${buttons}</div>`;
    }

    /**
     * 创建表格头部
     */
    createTableHeader() {
        let headerHtml = '';
        
        // 选择列
        if (this.config.selectable) {
            headerHtml += `
                <th class="select-column">
                    ${this.config.multiSelect ? 
                        '<input type="checkbox" class="form-check-input select-all">' : 
                        ''}
                </th>
            `;
        }
        
        // 数据列
        this.config.columns.forEach(column => {
            const sortable = column.sortable !== false && this.config.sortable;
            const sortClass = sortable ? 'sortable' : '';
            const sortIcon = sortable ? '<i class="fas fa-sort sort-icon"></i>' : '';
            
            headerHtml += `
                <th class="data-column ${sortClass}" 
                    data-column="${column.field}"
                    ${column.width ? `style="width: ${column.width}"` : ''}>
                    <div class="column-header">
                        <span class="column-title">${column.title}</span>
                        ${sortIcon}
                    </div>
                </th>
            `;
        });
        
        return `<tr>${headerHtml}</tr>`;
    }

    /**
     * 创建分页组件
     */
    createPagination() {
        return `
            <div class="table-pagination">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <div class="page-size-selector">
                            <label class="form-label">每页显示：</label>
                            <select class="form-select form-select-sm page-size-select">
                                ${this.config.pageSizes.map(size => 
                                    `<option value="${size}" ${size === this.config.pageSize ? 'selected' : ''}>${size}</option>`
                                ).join('')}
                            </select>
                            <span class="form-text">条记录</span>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <nav aria-label="表格分页">
                            <ul class="pagination pagination-sm justify-content-end mb-0">
                                <!-- 分页按钮将动态生成 -->
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * 获取表格CSS类
     */
    getTableClasses() {
        const classes = [];
        
        if (this.config.striped) classes.push('table-striped');
        if (this.config.bordered) classes.push('table-bordered');
        if (this.config.hover) classes.push('table-hover');
        
        return classes.join(' ');
    }

    /**
     * 绑定事件
     */
    bindEvents() {
        const container = this.container;
        
        // 搜索事件
        if (this.config.searchable) {
            const searchInput = container.querySelector('.search-input');
            const searchBtn = container.querySelector('.search-btn');
            
            if (searchInput) {
                searchInput.addEventListener('input', Utils.debounce((e) => {
                    this.search(e.target.value);
                }, 300));
                
                searchInput.addEventListener('keypress', (e) => {
                    if (e.key === 'Enter') {
                        this.search(e.target.value);
                    }
                });
            }
            
            if (searchBtn) {
                searchBtn.addEventListener('click', () => {
                    const query = searchInput ? searchInput.value : '';
                    this.search(query);
                });
            }
        }
        
        // 排序事件
        if (this.config.sortable) {
            container.addEventListener('click', (e) => {
                const sortableHeader = e.target.closest('.sortable');
                if (sortableHeader) {
                    const column = sortableHeader.getAttribute('data-column');
                    this.sort(column);
                }
            });
        }
        
        // 选择事件
        if (this.config.selectable) {
            // 全选
            const selectAllCheckbox = container.querySelector('.select-all');
            if (selectAllCheckbox) {
                selectAllCheckbox.addEventListener('change', (e) => {
                    this.selectAll(e.target.checked);
                });
            }
            
            // 行选择
            container.addEventListener('change', (e) => {
                if (e.target.classList.contains('row-select')) {
                    const rowId = e.target.getAttribute('data-row-id');
                    this.selectRow(rowId, e.target.checked);
                }
            });
        }
        
        // 行点击事件
        container.addEventListener('click', (e) => {
            const row = e.target.closest('tbody tr');
            if (row && this.config.onRowClick) {
                const rowId = row.getAttribute('data-row-id');
                const rowData = this.getRowData(rowId);
                this.config.onRowClick(rowData, row);
            }
        });
        
        // 操作按钮事件
        container.addEventListener('click', (e) => {
            if (e.target.classList.contains('action-btn')) {
                const action = e.target.getAttribute('data-action');
                this.handleAction(action);
            }
        });
        
        // 分页事件
        if (this.config.pagination) {
            // 页面大小改变
            const pageSizeSelect = container.querySelector('.page-size-select');
            if (pageSizeSelect) {
                pageSizeSelect.addEventListener('change', (e) => {
                    this.changePageSize(parseInt(e.target.value));
                });
            }
            
            // 分页按钮点击
            container.addEventListener('click', (e) => {
                if (e.target.classList.contains('page-link')) {
                    e.preventDefault();
                    const page = parseInt(e.target.getAttribute('data-page'));
                    if (!isNaN(page)) {
                        this.goToPage(page);
                    }
                }
            });
        }
    }

    /**
     * 加载数据
     */
    async loadData(params = {}) {
        if (!this.config.apiUrl) {
            console.warn('未配置API URL');
            return;
        }

        try {
            this.showLoading();
            
            // 构建请求参数
            const requestParams = {
                page: this.currentPage - 1, // 后端通常从0开始
                size: this.config.pageSize,
                search: this.searchQuery,
                sort: this.sortColumn,
                direction: this.sortDirection,
                ...params
            };
            
            // 发送请求
            const response = await fetch(`${this.config.apiUrl}?${new URLSearchParams(requestParams)}`, {
                method: this.config.method,
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const result = await response.json();
            
            // 处理响应数据
            if (result.success) {
                this.handleDataLoaded(result.data);
            } else {
                throw new Error(result.message || '数据加载失败');
            }
            
        } catch (error) {
            console.error('加载数据失败:', error);
            this.showError(error.message);
            
            if (this.config.onError) {
                this.config.onError(error);
            }
        }
    }

    /**
     * 处理数据加载完成
     */
    handleDataLoaded(data) {
        // 支持分页和非分页数据格式
        if (data.content && Array.isArray(data.content)) {
            // 分页数据格式
            this.data = data.content;
            this.totalRecords = data.totalElements || data.total || 0;
            this.totalPages = data.totalPages || Math.ceil(this.totalRecords / this.config.pageSize);
            this.currentPage = (data.number || data.page || 0) + 1; // 转换为1开始
        } else if (Array.isArray(data)) {
            // 简单数组格式
            this.data = data;
            this.totalRecords = data.length;
            this.totalPages = 1;
            this.currentPage = 1;
        } else {
            throw new Error('不支持的数据格式');
        }
        
        this.filteredData = [...this.data];
        
        // 渲染表格
        this.renderTable();
        
        // 更新分页
        if (this.config.pagination) {
            this.updatePagination();
        }
        
        // 更新统计信息
        this.updateTableInfo();
        
        // 隐藏加载状态
        this.hideLoading();
        
        // 触发回调
        if (this.config.onDataLoad) {
            this.config.onDataLoad(this.data);
        }
        
        console.log(`数据加载完成: ${this.data.length} 条记录`);
    }

    /**
     * 渲染表格
     */
    renderTable() {
        const tbody = this.container.querySelector('.table-body');
        if (!tbody) return;
        
        if (this.filteredData.length === 0) {
            this.showEmpty();
            return;
        }
        
        const rows = this.filteredData.map((row, index) => {
            return this.createTableRow(row, index);
        }).join('');
        
        tbody.innerHTML = rows;
        
        // 显示表格
        this.showTable();
    }

    /**
     * 创建表格行
     */
    createTableRow(rowData, index) {
        const rowId = rowData.id || index;
        let rowHtml = '';
        
        // 选择列
        if (this.config.selectable) {
            const checked = this.selectedRows.has(rowId) ? 'checked' : '';
            const inputType = this.config.multiSelect ? 'checkbox' : 'radio';
            rowHtml += `
                <td class="select-column">
                    <input type="${inputType}" class="form-check-input row-select" 
                           data-row-id="${rowId}" ${checked}>
                </td>
            `;
        }
        
        // 数据列
        this.config.columns.forEach(column => {
            const value = this.getCellValue(rowData, column);
            const formattedValue = this.formatCellValue(value, column, rowData);
            
            rowHtml += `
                <td class="data-cell" data-column="${column.field}">
                    ${formattedValue}
                </td>
            `;
        });
        
        return `<tr data-row-id="${rowId}" class="table-row">${rowHtml}</tr>`;
    }

    /**
     * 获取单元格值
     */
    getCellValue(rowData, column) {
        if (column.field.includes('.')) {
            // 支持嵌套属性，如 'user.name'
            return column.field.split('.').reduce((obj, key) => obj?.[key], rowData);
        }
        return rowData[column.field];
    }

    /**
     * 格式化单元格值
     */
    formatCellValue(value, column, rowData) {
        // 自定义渲染函数
        if (column.render && typeof column.render === 'function') {
            return column.render(value, rowData);
        }
        
        // 预定义格式化类型
        switch (column.type) {
            case 'date':
                return value ? Utils.formatDate(value, column.format || 'YYYY-MM-DD') : '-';
            case 'datetime':
                return value ? Utils.formatDate(value, column.format || 'YYYY-MM-DD HH:mm:ss') : '-';
            case 'number':
                return value !== null && value !== undefined ? Utils.formatNumber(value, column.decimals || 0) : '-';
            case 'currency':
                return value !== null && value !== undefined ? `¥${Utils.formatNumber(value, 2)}` : '-';
            case 'boolean':
                return value ? '<span class="badge bg-success">是</span>' : '<span class="badge bg-secondary">否</span>';
            case 'status':
                return this.renderStatusBadge(value, column.statusMap);
            case 'actions':
                return this.renderActionButtons(column.actions, rowData);
            default:
                return value !== null && value !== undefined ? Utils.escapeHtml(String(value)) : '-';
        }
    }

    /**
     * 渲染状态徽章
     */
    renderStatusBadge(value, statusMap = {}) {
        const status = statusMap[value] || { text: value, class: 'bg-secondary' };
        return `<span class="badge ${status.class}">${status.text}</span>`;
    }

    /**
     * 渲染操作按钮
     */
    renderActionButtons(actions = [], rowData) {
        return actions.map(action => {
            const btnClass = action.class || 'btn-sm btn-outline-primary';
            const icon = action.icon ? `<i class="${action.icon}"></i>` : '';
            const disabled = action.disabled && action.disabled(rowData) ? 'disabled' : '';
            
            return `
                <button class="btn ${btnClass} row-action" 
                        data-action="${action.name}" 
                        data-row-id="${rowData.id}"
                        ${disabled}
                        title="${action.title || action.label}">
                    ${icon}
                    ${action.showText !== false ? action.label : ''}
                </button>
            `;
        }).join(' ');
    }

    // 其他方法将在下一个文件中继续...
}

// 导出组件
window.DataTable = DataTableComponent;
