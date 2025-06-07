/**
 * API测试页面JavaScript
 * 智慧校园管理系统
 */

// 全局变量
let currentToken = null;
let testResults = {};
let allApiData = [];
let apiEndpoints = [];

// 立即执行函数，确保所有函数都绑定到全局作用域
(function() {
    'use strict';

    console.log('=== test-api.js 开始加载 ===');

    // 页面加载完成后初始化
    document.addEventListener('DOMContentLoaded', function() {
        console.log('页面加载完成，开始初始化...');
        initializePage();
        loadTokenFromAuth();
        setupEventHandlers();

        // 直接加载预定义API列表，确保页面能正常显示
        setTimeout(() => {
            console.log('开始加载API列表...');
            console.log('直接使用预定义API列表进行测试');

            // 立即测试DOM操作
            const tableBody = document.getElementById('api-table-body');
            console.log('找到api-table-body元素:', tableBody);

            if (tableBody) {
                console.log('开始替换表格内容...');
                tableBody.innerHTML = '<tr><td colspan="5" class="text-center">JavaScript正在工作！</td></tr>';
                console.log('表格内容已替换');
            } else {
                console.error('未找到api-table-body元素');
            }

            // 先尝试从Swagger加载，失败则使用预定义列表
            setTimeout(() => {
                console.log('开始从Swagger加载API列表...');
                loadApiEndpoints();
            }, 1000);
        }, 500);
    });

    /**
     * 初始化页面
     */
    function initializePage() {
        console.log('API测试页面初始化...');
        console.log('当前页面URL:', window.location.href);
        console.log('当前域名:', window.location.origin);

        // 简单的提示，不依赖复杂的Toast系统
        console.log('API测试页面已准备就绪');
    }



    /**
     * 从认证系统加载Token
     */
    async function loadTokenFromAuth() {
        try {
            console.log('🔐 从认证系统加载Token...');

            // 优先使用认证管理器
            if (window.authManager) {
                console.log('使用认证管理器获取Token');
                currentToken = window.authManager.getToken();

                // 如果没有token，尝试获取认证信息
                if (!currentToken) {
                    console.log('本地没有Token，尝试从服务器获取...');
                    await window.authManager.fetchAuthInfo();
                    currentToken = window.authManager.getToken();
                }
            } else if (window.apiClient) {
                console.log('使用API客户端获取Token');
                currentToken = window.apiClient.getToken();

                // 如果本地没有token，尝试从服务器获取
                if (!currentToken) {
                    currentToken = await window.apiClient.fetchTokenFromServer();
                }
            }

            console.log('当前Token:', currentToken ? '已获取' : '未获取');

            // 更新UI显示
            updateTokenDisplay();
            checkAuthStatus();

        } catch (error) {
            console.error('❌ 加载Token失败:', error);
        }
    }

    /**
     * 更新Token显示
     */
    function updateTokenDisplay() {
        const tokenElement = document.getElementById('currentToken');
        if (tokenElement) {
            if (currentToken) {
                tokenElement.textContent = currentToken.substring(0, 50) + '...';
                tokenElement.className = 'text-success';
            } else {
                tokenElement.textContent = '未设置Token';
                tokenElement.className = 'text-muted';
            }
        }
    }

    /**
     * 检查认证状态
     */
    function checkAuthStatus() {
        const authStatus = document.getElementById('auth-status');
        const authMessage = document.getElementById('auth-message');

        if (currentToken) {
            authStatus.className = 'alert alert-success';
            authMessage.textContent = '已认证 - 可以测试所有API接口';
        } else {
            authStatus.className = 'alert alert-warning';
            authMessage.textContent = '未认证 - 只能测试公开接口，请先登录获取Token';
        }
    }

    /**
     * 设置事件处理器
     */
    function setupEventHandlers() {
        console.log('设置事件处理器...');

        // 添加分类筛选事件监听器
        document.addEventListener('click', function(event) {
            if (event.target.classList.contains('filter-link')) {
                event.preventDefault();
                const category = event.target.getAttribute('data-category');
                console.log('点击分类筛选:', category);
                filterByCategory(category);
            }
        });

        // 刷新Token按钮
        const refreshTokenBtn = document.getElementById('refreshToken');
        if (refreshTokenBtn) {
            refreshTokenBtn.addEventListener('click', async function() {
                console.log('🔄 手动刷新Token...');

                // 显示加载状态
                this.disabled = true;
                this.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>刷新中...';

                try {
                    // 优先使用认证管理器刷新
                    if (window.authManager) {
                        const success = await window.authManager.refreshToken();
                        if (success) {
                            currentToken = window.authManager.getToken();
                            showToast('Token刷新成功', 'success');
                        } else {
                            // 如果刷新失败，重新获取认证信息
                            await window.authManager.fetchAuthInfo();
                            currentToken = window.authManager.getToken();
                        }
                    } else {
                        // 备用方案：重新加载Token
                        await loadTokenFromAuth();
                    }

                    // 更新UI显示
                    updateTokenDisplay();
                    checkAuthStatus();

                } catch (error) {
                    console.error('刷新Token失败:', error);
                    showToast('Token刷新失败: ' + error.message, 'error');
                } finally {
                    // 恢复按钮状态
                    this.disabled = false;
                    this.innerHTML = '<i class="fas fa-sync-alt me-1"></i>刷新Token';
                }
            });
        }

        // 清除结果按钮
        const clearResultsBtn = document.getElementById('clearResults');
        if (clearResultsBtn) {
            clearResultsBtn.addEventListener('click', function() {
                clearAllResults();
            });
        }

        // 全部测试按钮
        const testAllApisBtn = document.getElementById('testAllApis');
        if (testAllApisBtn) {
            testAllApisBtn.addEventListener('click', function() {
                testAllApis();
            });
        }

        // 测试所有API按钮（包括危险操作）
        const testAllDangerousBtn = document.getElementById('testAllDangerous');
        if (testAllDangerousBtn) {
            testAllDangerousBtn.addEventListener('click', function() {
                if (confirm('⚠️ 警告：这将测试所有API端点，包括可能修改数据的POST/PUT/DELETE请求！\n\n这可能会：\n- 创建测试数据\n- 修改现有数据\n- 删除数据\n\n确定要继续吗？')) {
                    testAllApisIncludingDangerous();
                }
            });
        }
    }

    /**
     * 从后端加载API端点列表
     */
    async function loadApiEndpoints() {
        console.log('🚀 开始从OpenAPI文档加载API端点...');

        // 先显示加载状态
        const tableBody = document.getElementById('api-table-body');
        if (tableBody) {
            tableBody.innerHTML = '<tr><td colspan="5" class="text-center"><i class="fas fa-spinner fa-spin text-primary"></i> 正在从Swagger加载API列表...</td></tr>';
        }

        try {
            const apiDocsUrl = '/api/v3/api-docs';
            console.log(`📡 请求API文档: ${window.location.origin}${apiDocsUrl}`);

            const response = await fetch(apiDocsUrl, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'include' // 包含认证信息
            });

            console.log(`📋 API文档响应状态: ${response.status} ${response.statusText}`);
            console.log('📋 响应头:', Object.fromEntries(response.headers.entries()));

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const swaggerDoc = await response.json();
            console.log('✅ 成功获取OpenAPI文档');
            console.log('📄 文档版本:', swaggerDoc.openapi);
            console.log('📄 文档标题:', swaggerDoc.info?.title);
            console.log('📄 API路径数量:', Object.keys(swaggerDoc.paths || {}).length);

            if (!swaggerDoc.paths || Object.keys(swaggerDoc.paths).length === 0) {
                throw new Error('OpenAPI文档中没有找到有效的paths字段');
            }

            // 解析文档
            parseSwaggerDoc(swaggerDoc);

            if (apiEndpoints.length === 0) {
                throw new Error('没有解析到任何API端点');
            }

            // 渲染表格
            renderApiTable();
            console.log(`🎉 API列表加载成功，共 ${apiEndpoints.length} 个端点`);

            // 显示成功提示
            showToast(`成功加载 ${apiEndpoints.length} 个API端点`, 'success');

        } catch (error) {
            console.error('❌ 加载API文档失败:', error);
            console.log('🔄 使用预定义API列表作为备选方案');

            // 显示错误提示
            showToast(`加载Swagger文档失败: ${error.message}，使用预定义列表`, 'warning');

            // 使用预定义列表
            loadPredefinedApis();
        }
    }

    /**
     * 解析Swagger文档 (OpenAPI 3.0格式)
     */
    function parseSwaggerDoc(swaggerDoc) {
        console.log('开始解析OpenAPI 3.0文档...');
        console.log('文档信息:', swaggerDoc.info);
        apiEndpoints = [];

        if (!swaggerDoc || !swaggerDoc.paths) {
            console.error('无效的OpenAPI文档结构');
            return;
        }

        const paths = swaggerDoc.paths;
        console.log('找到API路径数量:', Object.keys(paths).length);

        // 解析每个路径
        for (const [path, methods] of Object.entries(paths)) {
            if (!methods || typeof methods !== 'object') {
                console.warn(`跳过无效路径: ${path}`);
                continue;
            }

            console.log(`解析路径: ${path}`);

            // 解析每个HTTP方法
            for (const [method, details] of Object.entries(methods)) {
                const httpMethod = method.toLowerCase();

                // 只处理标准HTTP方法
                if (['get', 'post', 'put', 'delete', 'patch'].includes(httpMethod)) {
                    const endpoint = {
                        method: httpMethod.toUpperCase(),
                        path: path,
                        summary: details.summary || details.description || '无描述',
                        tags: details.tags && details.tags.length > 0 ? details.tags : ['未分类'],
                        operationId: details.operationId || '',
                        parameters: details.parameters || [],
                        requestBody: details.requestBody || null,
                        description: details.description || '',
                        security: details.security || []
                    };

                    apiEndpoints.push(endpoint);
                    console.log(`✓ 解析API: ${endpoint.method} ${endpoint.path} - ${endpoint.summary} [${endpoint.tags.join(', ')}]`);
                } else {
                    console.log(`跳过非标准HTTP方法: ${method}`);
                }
            }
        }

        console.log(`✅ 解析完成，共获得 ${apiEndpoints.length} 个API端点`);

        if (apiEndpoints.length === 0) {
            console.warn('⚠️ 没有解析到任何有效的API端点');
        } else {
            // 按标签分组统计
            const tagStats = {};
            apiEndpoints.forEach(api => {
                api.tags.forEach(tag => {
                    tagStats[tag] = (tagStats[tag] || 0) + 1;
                });
            });
            console.log('📊 API标签统计:', tagStats);
        }
    }

    /**
     * 加载预定义的API列表
     */
    function loadPredefinedApis() {
        console.log('=== 开始加载预定义API列表 ===');

        // 检查DOM元素是否存在
        const tableBody = document.getElementById('api-table-body');
        console.log('api-table-body元素:', tableBody);

        apiEndpoints = [
            // 测试接口
            { method: 'GET', path: '/api/test/hello', summary: '测试接口', tags: ['测试接口'] },

            // 认证API
            { method: 'POST', path: '/api/auth/login', summary: '用户登录', tags: ['认证API'] },
            { method: 'POST', path: '/api/auth/register', summary: '用户注册', tags: ['认证API'] },
            { method: 'GET', path: '/api/auth/me', summary: '获取当前用户信息', tags: ['认证API'] },
            { method: 'POST', path: '/api/auth/logout', summary: '用户登出', tags: ['认证API'] },

            // 仪表盘API
            { method: 'GET', path: '/api/dashboard/stats', summary: '获取仪表盘统计', tags: ['仪表盘API'] },
            { method: 'POST', path: '/api/dashboard/refresh', summary: '刷新仪表盘数据', tags: ['仪表盘API'] },

            // 用户管理API
            { method: 'GET', path: '/api/users', summary: '获取用户列表', tags: ['用户管理API'] },
            { method: 'POST', path: '/api/users', summary: '创建用户', tags: ['用户管理API'] },
            { method: 'GET', path: '/api/users/{id}', summary: '获取用户详情', tags: ['用户管理API'] },

            // 学生管理API
            { method: 'GET', path: '/api/students', summary: '获取学生列表', tags: ['学生管理API'] },
            { method: 'POST', path: '/api/students', summary: '创建学生', tags: ['学生管理API'] },

            // 班级管理API
            { method: 'GET', path: '/api/classes', summary: '获取班级列表', tags: ['班级管理API'] },
            { method: 'POST', path: '/api/classes', summary: '创建班级', tags: ['班级管理API'] },
            { method: 'GET', path: '/api/classes/stats/grade', summary: '统计班级数量按年级', tags: ['班级管理API'] },

            // 课程管理API
            { method: 'GET', path: '/api/courses', summary: '获取课程列表', tags: ['课程管理API'] },
            { method: 'POST', path: '/api/courses', summary: '创建课程', tags: ['课程管理API'] },

            // 课程安排管理
            { method: 'GET', path: '/api/schedules', summary: '获取课程安排列表', tags: ['课程安排管理'] },
            { method: 'POST', path: '/api/schedules', summary: '创建课程安排', tags: ['课程安排管理'] },

            // 成绩管理API
            { method: 'GET', path: '/api/grades', summary: '获取成绩列表', tags: ['成绩管理API'] },
            { method: 'POST', path: '/api/grades', summary: '创建成绩记录', tags: ['成绩管理API'] },

            // 缴费管理API
            { method: 'GET', path: '/api/payments/records', summary: '获取缴费记录列表', tags: ['缴费管理API'] },
            { method: 'GET', path: '/api/fee-items', summary: '分页查询缴费项目', tags: ['缴费管理API'] },

            // 系统管理API
            { method: 'GET', path: '/api/system/settings', summary: '获取系统设置', tags: ['系统管理API'] },

            // 缓存管理
            { method: 'GET', path: '/api/cache/info', summary: '获取缓存信息', tags: ['缓存管理'] },
            { method: 'GET', path: '/api/cache/stats', summary: '获取缓存统计', tags: ['缓存管理'] }
        ];

        console.log('加载预定义API列表，共', apiEndpoints.length, '个端点');
        console.log('准备调用renderApiTable函数...');

        try {
            renderApiTable();
            console.log('renderApiTable函数调用完成');
        } catch (error) {
            console.error('renderApiTable函数执行失败:', error);
        }
    }

    /**
     * 渲染API表格
     */
    function renderApiTable() {
        const tableBody = document.getElementById('api-table-body');
        if (!tableBody) {
            console.error('找不到api-table-body元素');
            return;
        }

        console.log(`开始渲染API表格，共 ${apiEndpoints.length} 个端点`);

        // 如果没有API端点，显示空状态或使用预定义列表
        if (apiEndpoints.length === 0) {
            console.log('没有API端点，使用预定义列表');
            loadPredefinedApis();
            return;
        }

        // 获取所有标签用于分类
        const allTags = [...new Set(apiEndpoints.flatMap(api => api.tags))];
        console.log('所有标签:', allTags);

        // 更新导航栏
        updateNavigation(allTags);

        // 渲染表格行
        const rows = apiEndpoints.map((api, index) => {
            const tag = api.tags[0] || '未分类';
            const testId = `api-${index}`;

            return `
                <tr data-category="${tag}">
                    <td><span class="method-badge method-${api.method.toLowerCase()}">${api.method}</span></td>
                    <td><code class="api-url">${api.path}</code></td>
                    <td>${api.summary}</td>
                    <td><span class="badge bg-${getTagColor(tag)}">${tag}</span></td>
                    <td>
                        <button class="btn btn-sm btn-test me-1" onclick="testApi('${api.method}', '${api.path}', getTestDataForApi('${api.method}', '${api.path}'), '${testId}')">
                            <i class="fas fa-play"></i> 测试
                        </button>
                        <button class="btn btn-sm btn-copy" onclick="copyApiUrl('${api.path}')">
                            <i class="fas fa-copy"></i>
                        </button>
                    </td>
                </tr>
            `;
        }).join('');

        tableBody.innerHTML = rows;
        console.log('API表格渲染完成');
    }

    /**
     * 更新导航栏
     */
    function updateNavigation(tags) {
        const navContainer = document.querySelector('.nav-pills');
        if (!navContainer) return;

        const navItems = [
            '<a class="nav-link active filter-link" href="#" data-category="all">全部接口</a>'
        ].concat(
            tags.map(tag =>
                `<a class="nav-link filter-link" href="#" data-category="${tag}">${tag}</a>`
            )
        );

        navContainer.innerHTML = navItems.join('');
    }

    /**
     * 获取标签颜色
     */
    function getTagColor(tag) {
        const colors = {
            '测试接口': 'secondary',
            '认证API': 'warning',
            '仪表盘API': 'info',
            '用户管理API': 'primary',
            '学生管理API': 'success',
            '班级管理API': 'dark',
            '课程管理API': 'warning',
            '课程安排管理': 'secondary',
            '成绩管理API': 'danger',
            '缴费管理API': 'light',
            '系统管理API': 'dark',
            '缓存管理': 'info'
        };
        return colors[tag] || 'secondary';
    }

    /**
     * 测试API接口
     */
    async function testApi(method, url, data, testId) {
        if (!currentToken && !url.includes('/api/test/')) {
            console.warn('请先获取Token');
            alert('请先获取Token');
            return;
        }

        const startTime = new Date().getTime();

        // 处理URL和查询参数
        let finalUrl = url;
        let requestData = data;

        // 对于GET请求，将data作为查询参数添加到URL
        if (method === 'GET' && data && typeof data === 'object') {
            const queryParams = new URLSearchParams();
            Object.keys(data).forEach(key => {
                if (data[key] !== null && data[key] !== undefined) {
                    queryParams.append(key, data[key]);
                }
            });

            if (queryParams.toString()) {
                finalUrl = url + (url.includes('?') ? '&' : '?') + queryParams.toString();
            }

            requestData = null; // GET请求不需要请求体
        }

        // 显示加载状态
        showApiResult(testId, {
            method: method,
            url: finalUrl,
            status: 'loading',
            message: '请求中...'
        });

        try {
            const requestConfig = {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                }
            };

            // 添加认证头
            if (currentToken) {
                requestConfig.headers['Authorization'] = 'Bearer ' + currentToken;
            }

            // 添加请求数据（仅对POST、PUT、PATCH）
            if (requestData && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
                requestConfig.body = JSON.stringify(requestData);
            }

            // 发送请求
            const response = await fetch(finalUrl, requestConfig);
            const endTime = new Date().getTime();
            const duration = endTime - startTime;

            let responseData;
            try {
                responseData = await response.json();
            } catch (e) {
                responseData = await response.text();
            }

            showApiResult(testId, {
                method: method,
                url: finalUrl,
                status: response.ok ? 'success' : 'error',
                statusCode: response.status,
                response: responseData,
                duration: duration,
                requestData: requestData,
                queryParams: method === 'GET' && data ? data : null
            });

        } catch (error) {
            const endTime = new Date().getTime();
            const duration = endTime - startTime;

            showApiResult(testId, {
                method: method,
                url: finalUrl,
                status: 'error',
                statusCode: 0,
                response: error.message,
                duration: duration,
                requestData: requestData,
                queryParams: method === 'GET' && data ? data : null,
                error: error.message
            });
        }
    }

    /**
     * 显示API测试结果
     */
    function showApiResult(testId, result) {
        const resultContainer = document.getElementById('apiResults');
        if (!resultContainer) return;

        let resultElement = document.getElementById(`result-${testId}`);

        if (!resultElement) {
            resultElement = document.createElement('div');
            resultElement.id = `result-${testId}`;
            resultElement.className = 'card mb-3';
            resultElement.innerHTML = `
                <div class="card-header">
                    <h6 class="mb-0">
                        <span class="badge bg-${getMethodColor(result.method)}">${result.method}</span>
                        <span class="ms-2">${result.url}</span>
                        <span class="float-end" id="status-${testId}"></span>
                    </h6>
                </div>
                <div class="card-body">
                    <div id="content-${testId}"></div>
                </div>
            `;
            resultContainer.appendChild(resultElement);
        }

        const statusElement = document.getElementById(`status-${testId}`);
        const contentElement = document.getElementById(`content-${testId}`);

        if (result.status === 'loading') {
            statusElement.innerHTML = '<span class="badge bg-warning">请求中...</span>';
            contentElement.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></i> 请求中...</div>';
        } else if (result.status === 'success') {
            statusElement.innerHTML = `
                <span class="badge bg-success">${result.statusCode}</span>
                <small class="text-muted ms-2">${result.duration}ms</small>
            `;

            contentElement.innerHTML = `
                ${result.queryParams ? `
                    <div class="mb-3">
                        <strong>查询参数:</strong>
                        <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.queryParams, null, 2)}</code></pre>
                    </div>
                ` : ''}
                ${result.requestData ? `
                    <div class="mb-3">
                        <strong>请求数据:</strong>
                        <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.requestData, null, 2)}</code></pre>
                    </div>
                ` : ''}
                <div>
                    <strong>响应数据:</strong>
                    <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.response, null, 2)}</code></pre>
                </div>
            `;
        } else {
            statusElement.innerHTML = `
                <span class="badge bg-danger">${result.statusCode || 'ERROR'}</span>
                <small class="text-muted ms-2">${result.duration}ms</small>
            `;

            contentElement.innerHTML = `
                ${result.queryParams ? `
                    <div class="mb-3">
                        <strong>查询参数:</strong>
                        <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.queryParams, null, 2)}</code></pre>
                    </div>
                ` : ''}
                ${result.requestData ? `
                    <div class="mb-3">
                        <strong>请求数据:</strong>
                        <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.requestData, null, 2)}</code></pre>
                    </div>
                ` : ''}
                <div class="mb-3">
                    <strong>错误信息:</strong>
                    <div class="alert alert-danger">${result.error || '请求失败'}</div>
                </div>
                <div>
                    <strong>响应数据:</strong>
                    <pre class="bg-light p-2 mt-1"><code>${JSON.stringify(result.response, null, 2)}</code></pre>
                </div>
            `;
        }
    }

    /**
     * 获取HTTP方法对应的颜色
     */
    function getMethodColor(method) {
        switch (method.toUpperCase()) {
            case 'GET': return 'success';
            case 'POST': return 'primary';
            case 'PUT': return 'warning';
            case 'DELETE': return 'danger';
            case 'PATCH': return 'info';
            default: return 'secondary';
        }
    }

    /**
     * 获取测试数据
     */
    function getTestDataForApi(method, url) {
        // 对于GET请求，检查是否需要查询参数
        if (method === 'GET') {
            return getQueryParamsForApi(url);
        }

        // 只为POST、PUT、PATCH请求提供请求体数据
        if (!['POST', 'PUT', 'PATCH'].includes(method)) {
            return null;
        }

        const testData = {
            '/api/auth/login': {
                username: 'admin',
                password: 'admin123'
            },
            '/api/auth/register': {
                username: 'testuser' + Date.now(),
                password: 'test123',
                email: 'test@example.com',
                realName: '测试用户',
                phone: '13800138000',
                gender: '男'
            },
            '/api/students': {
                userId: 1,
                studentNo: 'STU' + Date.now(),
                grade: '2024级',
                major: '计算机科学与技术',
                classId: 1,
                enrollmentYear: 2024,
                enrollmentDate: '2024-09-01',
                academicStatus: 1,
                studentType: '本科生',
                trainingMode: '全日制',
                academicSystem: 4,
                currentSemester: '2024-1',
                parentName: '测试家长',
                parentPhone: '13800138001',
                emergencyContact: '紧急联系人',
                emergencyPhone: '13800138002',
                dormitory: '1号楼101',
                status: 1
            },
            '/api/classes': {
                className: '计算机科学与技术2024级1班',
                classCode: 'CS2024' + Date.now(),
                grade: '2024级',
                major: '计算机科学与技术',
                departmentId: 1,
                headTeacherId: 1,
                enrollmentYear: 2024,
                enrollmentDate: '2024-09-01',
                expectedGraduationDate: '2028-06-30',
                classType: '本科班',
                academicSystem: 4,
                description: '计算机科学与技术专业2024级1班',
                motto: '勤奋学习，追求卓越',
                goals: '培养高素质计算机专业人才',
                maxStudents: 30,
                classroom: '教学楼A101',
                classStatus: 1,
                status: 1
            },
            '/api/courses': {
                courseName: '数据结构与算法',
                courseCode: 'CS' + Date.now(),
                courseNameEn: 'Data Structures and Algorithms',
                credits: 4,
                hours: 64,
                theoryHours: 48,
                labHours: 16,
                practiceHours: 0,
                departmentId: 1,
                description: '数据结构与算法基础课程',
                objectives: '掌握基本数据结构和算法设计',
                teacherId: 1,
                courseType: '专业必修',
                courseNature: '理论+实践',
                applicableMajors: '计算机科学与技术',
                prerequisites: '程序设计基础',
                semester: '2024-1',
                academicYear: 2024,
                classTime: '周一1-2节，周三3-4节',
                classroom: '教学楼B201',
                maxStudents: 60,
                selectionStartTime: '2024-08-20T00:00:00',
                selectionEndTime: '2024-08-30T23:59:59',
                assessmentMethod: '考试',
                regularScoreRatio: 0.3,
                midtermScoreRatio: 0.3,
                finalScoreRatio: 0.4,
                textbook: '数据结构（C语言版）',
                references: '算法导论',
                status: 1
            },
            '/api/users': {
                username: 'testuser' + Date.now(),
                realName: '测试用户',
                email: 'test@example.com',
                phone: '13800138000',
                gender: '男',
                birthday: '1990-01-01',
                address: '测试地址',
                roleKey: 'STUDENT',
                status: 1
            }
        };

        return testData[url] || {
            name: '测试数据',
            description: 'API测试用数据',
            status: 1,
            createTime: new Date().toISOString()
        };
    }

    /**
     * 获取GET请求的查询参数
     */
    function getQueryParamsForApi(path) {
        switch (path) {
            // 成绩搜索需要关键词
            case '/api/grades/search':
                return { keyword: '张三' };

            // 课程搜索需要关键词
            case '/api/courses/search':
                return { keyword: '数学' };

            // 用户搜索需要关键词
            case '/api/users/search':
                return { keyword: 'admin' };

            // 缴费项目生成编码需要费用类型
            case '/api/fee-items/generate-code':
                return { feeType: '学费' };

            // 缴费项目检查编码需要项目编码
            case '/api/fee-items/check-code':
                return { itemCode: 'FEE001' };

            // 按截止日期查询缴费项目需要日期范围
            case '/api/fee-items/by-due-date':
                return {
                    startDate: '2024-01-01',
                    endDate: '2025-06-07-31'
                };

            // 按金额查询缴费项目需要金额范围
            case '/api/fee-items/by-amount':
                return {
                    minAmount: 0,
                    maxAmount: 10000
                };

            // 缴费项目搜索
            case '/api/fee-items/search':
                return {
                    keyword: '学费',
                    feeType: '学费',
                    status: 1
                };

            // 学生搜索
            case '/api/students/search':
                return { keyword: '张三' };

            // 班级搜索
            case '/api/classes/search':
                return { keyword: '计算机' };

            // 课程安排搜索
            case '/api/schedules/search':
                return { keyword: '数学' };

            // 其他需要分页参数的API
            case '/api/users':
            case '/api/students':
            case '/api/courses':
            case '/api/classes':
            case '/api/grades':
            case '/api/fee-items':
            case '/api/schedules':
            case '/api/payments/records':
                return {
                    page: 1,
                    size: 10
                };

            // 默认返回null（不需要查询参数）
            default:
                return null;
        }
    }

    /**
     * 清除所有结果
     */
    function clearAllResults() {
        const resultsContainer = document.getElementById('apiResults');
        if (resultsContainer) {
            resultsContainer.innerHTML = `
                <div class="text-center text-muted py-4">
                    <i class="fas fa-code fa-3x mb-3"></i>
                    <p>点击左侧API列表中的"测试"按钮查看响应结果</p>
                </div>
            `;
        }

        testResults = {};
        showToast('已清除测试结果', 'info');
    }

    /**
     * 测试所有API
     */
    async function testAllApis() {
        console.log('🚀 开始批量测试所有API...');

        if (!apiEndpoints || apiEndpoints.length === 0) {
            showToast('没有可测试的API端点，请先加载API列表', 'warning');
            return;
        }

        // 过滤出适合批量测试的API（主要是GET请求和一些安全的POST请求）
        const safeApis = apiEndpoints.filter(api => {
            // GET请求通常是安全的
            if (api.method === 'GET') {
                return true;
            }

            // 一些安全的POST请求
            const safePostPaths = [
                '/api/auth/logout',
                '/api/dashboard/refresh',
                '/api/cache/clear-all',
                '/api/system/clear-cache'
            ];

            if (api.method === 'POST' && safePostPaths.includes(api.path)) {
                return true;
            }

            return false;
        });

        if (safeApis.length === 0) {
            showToast('没有找到适合批量测试的安全API端点', 'warning');
            return;
        }

        showToast(`开始批量测试 ${safeApis.length} 个API端点...`, 'info');
        console.log(`📋 将测试以下API:`, safeApis.map(api => `${api.method} ${api.path}`));

        // 清除之前的结果
        clearAllResults();

        let successCount = 0;
        let errorCount = 0;

        // 按分类分组测试
        const apisByCategory = {};
        safeApis.forEach(api => {
            const category = api.tags[0] || '未分类';
            if (!apisByCategory[category]) {
                apisByCategory[category] = [];
            }
            apisByCategory[category].push(api);
        });

        console.log('📊 API分类统计:', Object.keys(apisByCategory).map(cat => `${cat}: ${apisByCategory[cat].length}个`));

        // 逐个分类测试
        for (const [category, apis] of Object.entries(apisByCategory)) {
            console.log(`🔍 测试分类: ${category} (${apis.length}个API)`);
            showToast(`正在测试 ${category} (${apis.length}个API)...`, 'info');

            for (let i = 0; i < apis.length; i++) {
                const api = apis[i];
                const testId = `batch-${category}-${i}`;

                try {
                    console.log(`🧪 测试: ${api.method} ${api.path}`);

                    await testApi(
                        api.method,
                        api.path,
                        getTestDataForApi(api.method, api.path),
                        testId
                    );

                    successCount++;

                    // 添加延迟避免请求过快
                    await new Promise(resolve => setTimeout(resolve, 300));

                } catch (error) {
                    console.error(`❌ 测试 ${api.method} ${api.path} 失败:`, error);
                    errorCount++;
                }
            }

            // 分类间稍长延迟
            await new Promise(resolve => setTimeout(resolve, 500));
        }

        // 显示测试结果统计
        const totalTested = successCount + errorCount;
        const resultMessage = `批量测试完成！共测试 ${totalTested} 个API，成功 ${successCount} 个，失败 ${errorCount} 个`;

        console.log(`✅ ${resultMessage}`);
        showToast(resultMessage, errorCount === 0 ? 'success' : 'warning');

        // 显示详细统计
        setTimeout(() => {
            showToast(`测试覆盖率: ${Math.round((totalTested / apiEndpoints.length) * 100)}% (${totalTested}/${apiEndpoints.length})`, 'info');
        }, 2000);
    }

    /**
     * 测试所有API（包括危险操作）
     */
    async function testAllApisIncludingDangerous() {
        console.log('⚠️ 开始测试所有API（包括危险操作）...');

        if (!apiEndpoints || apiEndpoints.length === 0) {
            showToast('没有可测试的API端点，请先加载API列表', 'warning');
            return;
        }

        showToast(`⚠️ 开始测试所有 ${apiEndpoints.length} 个API端点（包括危险操作）...`, 'warning');
        console.log(`📋 将测试所有API:`, apiEndpoints.map(api => `${api.method} ${api.path}`));

        // 清除之前的结果
        clearAllResults();

        let successCount = 0;
        let errorCount = 0;
        let skippedCount = 0;

        // 需要跳过的危险API（避免造成系统问题）
        const skipApis = [
            '/api/users/{id}', // DELETE 删除用户
            '/api/system/shutdown', // 系统关闭
            '/api/system/restart', // 系统重启
            '/api/database/reset', // 数据库重置
            '/api/cache/clear-all' // 清除所有缓存
        ];

        // 按分类分组测试
        const apisByCategory = {};
        apiEndpoints.forEach(api => {
            const category = api.tags[0] || '未分类';
            if (!apisByCategory[category]) {
                apisByCategory[category] = [];
            }
            apisByCategory[category].push(api);
        });

        console.log('📊 API分类统计:', Object.keys(apisByCategory).map(cat => `${cat}: ${apisByCategory[cat].length}个`));

        // 逐个分类测试
        for (const [category, apis] of Object.entries(apisByCategory)) {
            console.log(`🔍 测试分类: ${category} (${apis.length}个API)`);
            showToast(`正在测试 ${category} (${apis.length}个API)...`, 'info');

            for (let i = 0; i < apis.length; i++) {
                const api = apis[i];
                const testId = `dangerous-${category}-${i}`;

                // 检查是否需要跳过
                if (skipApis.includes(api.path)) {
                    console.log(`⏭️ 跳过危险API: ${api.method} ${api.path}`);
                    skippedCount++;
                    continue;
                }

                try {
                    console.log(`🧪 测试: ${api.method} ${api.path}`);

                    // 对于修改操作，使用测试数据
                    let testData = getTestDataForApi(api.method, api.path);

                    // 对于DELETE操作，使用一个不存在的ID
                    if (api.method === 'DELETE' && api.path.includes('{id}')) {
                        testData = null; // 不传递数据，让URL处理
                    }

                    await testApi(api.method, api.path, testData, testId);
                    successCount++;

                    // 对于修改操作，添加更长的延迟
                    const delay = ['POST', 'PUT', 'DELETE'].includes(api.method) ? 1000 : 300;
                    await new Promise(resolve => setTimeout(resolve, delay));

                } catch (error) {
                    console.error(`❌ 测试 ${api.method} ${api.path} 失败:`, error);
                    errorCount++;
                }
            }

            // 分类间稍长延迟
            await new Promise(resolve => setTimeout(resolve, 1000));
        }

        // 显示测试结果统计
        const totalTested = successCount + errorCount;
        const resultMessage = `完整测试完成！共测试 ${totalTested} 个API，成功 ${successCount} 个，失败 ${errorCount} 个，跳过 ${skippedCount} 个`;

        console.log(`✅ ${resultMessage}`);
        showToast(resultMessage, errorCount === 0 ? 'success' : 'warning');

        // 显示详细统计
        setTimeout(() => {
            showToast(`测试覆盖率: ${Math.round((totalTested / apiEndpoints.length) * 100)}% (${totalTested}/${apiEndpoints.length})`, 'info');
        }, 2000);
    }

    /**
     * 按分类过滤API
     */
    function filterByCategory(category) {
        const rows = document.querySelectorAll('#api-table-body tr');
        const navLinks = document.querySelectorAll('.nav-pills .nav-link');

        // 更新导航状态
        navLinks.forEach(link => {
            link.classList.remove('active');
            if ((category === 'all' && link.textContent === '全部接口') ||
                (category !== 'all' && link.textContent === category)) {
                link.classList.add('active');
            }
        });

        // 过滤表格行
        rows.forEach(row => {
            if (category === 'all' || row.dataset.category === category) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });

        showToast(`已过滤到 ${category === 'all' ? '全部' : category} 接口`, 'info');
    }

    /**
     * 复制API URL
     */
    function copyApiUrl(url) {
        const fullUrl = window.location.origin + url;

        if (navigator.clipboard) {
            navigator.clipboard.writeText(fullUrl).then(() => {
                showToast('API URL已复制到剪贴板', 'success');
            }).catch(err => {
                console.error('复制失败:', err);
                fallbackCopyTextToClipboard(fullUrl);
            });
        } else {
            fallbackCopyTextToClipboard(fullUrl);
        }
    }

    /**
     * 备用复制方法
     */
    function fallbackCopyTextToClipboard(text) {
        const textArea = document.createElement("textarea");
        textArea.value = text;
        textArea.style.top = "0";
        textArea.style.left = "0";
        textArea.style.position = "fixed";

        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
            const successful = document.execCommand('copy');
            if (successful) {
                showToast('API URL已复制到剪贴板', 'success');
            } else {
                showToast('复制失败，请手动复制', 'error');
            }
        } catch (err) {
            console.error('复制失败:', err);
            showToast('复制失败，请手动复制', 'error');
        }

        document.body.removeChild(textArea);
    }

    /**
     * 显示Toast消息
     */
    function showToast(message, type = 'info') {
        console.log(`[${type.toUpperCase()}] ${message}`);

        // 简单的alert替代，避免复杂的Bootstrap依赖
        if (type === 'error') {
            console.error(message);
        } else if (type === 'warning') {
            console.warn(message);
        } else {
            console.info(message);
        }

        // 可选：在页面上显示简单提示
        try {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type === 'warning' ? 'warning' : 'info'} alert-dismissible fade show position-fixed`;
            alertDiv.style.top = '20px';
            alertDiv.style.right = '20px';
            alertDiv.style.zIndex = '9999';
            alertDiv.style.maxWidth = '300px';
            alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            document.body.appendChild(alertDiv);

            // 3秒后自动移除
            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.parentNode.removeChild(alertDiv);
                }
            }, 3000);
        } catch (e) {
            console.error('显示提示失败:', e);
        }
    }



    /**
     * 显示加载状态
     */
    function showLoading(message = '加载中...') {
        console.log('显示加载状态:', message);

        const loadingElement = document.getElementById('loading-indicator');
        if (loadingElement) {
            loadingElement.innerHTML = `
                <div class="text-center py-3">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <div class="mt-2">${message}</div>
                </div>
            `;
            loadingElement.style.display = 'block';
            console.log('加载指示器已显示');
        } else {
            console.error('找不到loading-indicator元素');

            // 如果找不到loading-indicator，尝试在表格中显示
            const tableBody = document.getElementById('api-table-body');
            if (tableBody) {
                tableBody.innerHTML = `
                    <tr>
                        <td colspan="5" class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <div class="mt-2">${message}</div>
                        </td>
                    </tr>
                `;
                console.log('在表格中显示加载状态');
            }
        }
    }

    /**
     * 隐藏加载状态
     */
    function hideLoading() {
        console.log('隐藏加载状态');

        // 查找包含loading-indicator的表格行
        const tableBody = document.getElementById('api-table-body');
        if (tableBody) {
            const loadingRow = tableBody.querySelector('tr');
            if (loadingRow && loadingRow.querySelector('#loading-indicator')) {
                console.log('移除加载行');
                loadingRow.remove();
            }
        }

        // 也尝试直接隐藏loading-indicator元素
        const loadingElement = document.getElementById('loading-indicator');
        if (loadingElement) {
            loadingElement.style.display = 'none';
            console.log('隐藏加载指示器');
        }
    }

    // 将函数绑定到全局作用域，确保HTML中的onclick可以访问
    window.testApi = testApi;
    window.clearAllResults = clearAllResults;
    window.testAllApis = testAllApis;
    window.testAllApisIncludingDangerous = testAllApisIncludingDangerous;
    window.filterByCategory = filterByCategory;
    window.showToast = showToast;
    window.getTestDataForApi = getTestDataForApi;
    window.copyApiUrl = copyApiUrl;
    window.showApiResult = showApiResult;
    window.checkAuthStatus = checkAuthStatus;
    window.loadTokenFromAuth = loadTokenFromAuth;

    console.log('=== test-api.js 加载完成 ===');

})(); // 立即执行函数结束