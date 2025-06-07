/**
 * API客户端测试文件
 * 用于验证API客户端是否能正常工作
 */

console.log('=== API客户端测试开始 ===');

// 检查API客户端是否已加载
if (typeof ApiClient !== 'undefined') {
    console.log('✅ ApiClient类已定义');
} else {
    console.error('❌ ApiClient类未定义');
}

if (typeof apiClient !== 'undefined') {
    console.log('✅ apiClient实例已创建');
} else {
    console.error('❌ apiClient实例未创建');
}

// 页面加载完成后的测试
document.addEventListener('DOMContentLoaded', function() {
    console.log('📄 页面加载完成，开始API客户端功能测试');
    
    setTimeout(() => {
        // 测试基本功能
        testApiClientBasics();
    }, 1000);
});

/**
 * 测试API客户端基本功能
 */
function testApiClientBasics() {
    console.log('🧪 测试API客户端基本功能...');
    
    try {
        // 测试Token获取
        const token = apiClient.getToken();
        console.log('Token状态:', token ? '已获取' : '未获取');
        
        // 测试headers获取
        apiClient.getHeaders().then(headers => {
            console.log('✅ Headers获取成功:', headers);
        }).catch(error => {
            console.error('❌ Headers获取失败:', error);
        });
        
        // 测试简单的GET请求（到一个通常存在的端点）
        testSimpleRequest();
        
    } catch (error) {
        console.error('❌ API客户端基本功能测试失败:', error);
    }
}

/**
 * 测试简单请求
 */
async function testSimpleRequest() {
    console.log('🌐 测试简单API请求...');
    
    try {
        // 测试健康检查端点
        const response = await apiClient.get('/api/health');
        console.log('✅ 健康检查请求成功:', response);
    } catch (error) {
        console.log('ℹ️ 健康检查请求失败（这是正常的，如果后端未运行）:', error.message);
        
        // 测试另一个端点
        try {
            const response = await apiClient.get('/api/test/hello');
            console.log('✅ 测试请求成功:', response);
        } catch (error2) {
            console.log('ℹ️ 测试请求也失败（这是正常的，如果后端未运行）:', error2.message);
        }
    }
}

// 全局测试函数
window.testApiClient = testApiClientBasics;

console.log('=== API客户端测试文件已加载 ===');