/**
 * 课表管理页面JavaScript
 */

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log('课表管理页面已加载');
    initSchedulePage();
});

/**
 * 初始化课表页面
 */
function initSchedulePage() {
    // 初始化数据表格
    if (typeof $.fn.DataTable !== 'undefined') {
        $('#dataTable').DataTable({
            language: {
                url: '/js/lib/datatables-zh.json'
            },
            pageLength: 10,
            order: [[0, 'asc']], // 按ID升序排列
            columnDefs: [
                { orderable: false, targets: [9] } // 操作列不可排序
            ]
        });
    }

    // 绑定事件
    bindScheduleEvents();
}

/**
 * 绑定课表相关事件
 */
function bindScheduleEvents() {
    // 添加课表按钮
    const addBtn = document.querySelector('a[href="/admin/academic/schedules/add"]');
    if (addBtn) {
        addBtn.addEventListener('click', function(e) {
            console.log('跳转到添加课表页面');
        });
    }

    // 筛选表单提交
    const filterForm = document.querySelector('form[action="/admin/academic/schedules"]');
    if (filterForm) {
        filterForm.addEventListener('submit', function(e) {
            console.log('提交筛选条件');
        });
    }
}

/**
 * 删除课表
 */
function deleteSchedule(id) {
    if (!id) {
        showAlert('课表ID无效', 'error');
        return;
    }

    // 确认删除
    if (!confirm('确定要删除这个课表安排吗？此操作不可恢复！')) {
        return;
    }

    showLoading('正在删除课表...');

    // 模拟删除操作
    setTimeout(() => {
        hideLoading();
        showAlert('课表删除成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 调课操作
 */
function adjustSchedule(id) {
    if (!id) {
        showAlert('课表ID无效', 'error');
        return;
    }

    if (!confirm('确定要调整这个课表安排吗？')) {
        return;
    }

    showLoading('正在调整课表...');

    // 模拟调课操作
    setTimeout(() => {
        hideLoading();
        showAlert('课表调整成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 复制课表
 */
function copySchedule(id) {
    if (!id) {
        showAlert('课表ID无效', 'error');
        return;
    }

    if (!confirm('确定要复制这个课表安排吗？')) {
        return;
    }

    showLoading('正在复制课表...');

    // 模拟复制操作
    setTimeout(() => {
        hideLoading();
        showAlert('课表复制成功', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1000);
}

/**
 * 批量删除课表
 */
function batchDeleteSchedules() {
    const checkboxes = document.querySelectorAll('input[name="scheduleIds"]:checked');
    
    if (checkboxes.length === 0) {
        showAlert('请选择要删除的课表', 'warning');
        return;
    }

    if (!confirm(`确定要删除选中的 ${checkboxes.length} 个课表安排吗？此操作不可恢复！`)) {
        return;
    }

    const ids = Array.from(checkboxes).map(cb => cb.value);
    
    showLoading('正在批量删除课表...');

    // 模拟批量删除操作
    setTimeout(() => {
        hideLoading();
        showAlert(`成功删除 ${ids.length} 个课表安排`, 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 1500);
}

/**
 * 导出课表数据
 */
function exportSchedules() {
    showLoading('正在导出课表数据...');

    // 模拟导出操作
    setTimeout(() => {
        hideLoading();
        
        // 创建模拟的CSV数据
        const csvData = [
            ['ID', '课程名称', '课程代码', '教师', '教室', '星期', '时间段', '学期', '状态'],
            ['1', 'Java程序设计', 'CS101', '张教授', 'A101', '周一', '08:00-09:40', '2024春季学期', '正常'],
            ['2', '数据结构', 'CS102', '李教授', 'A102', '周二', '10:00-11:40', '2024春季学期', '正常'],
            ['3', '数据库原理', 'CS103', '王教授', 'A103', '周三', '14:00-15:40', '2024春季学期', '调课']
        ];

        // 转换为CSV格式
        const csvContent = csvData.map(row => row.join(',')).join('\n');
        
        // 下载文件
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', '课表数据_' + new Date().toISOString().slice(0, 10) + '.csv');
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        
        showAlert('课表数据导出成功', 'success');
    }, 1000);
}

/**
 * 检查课表冲突
 */
function checkScheduleConflicts() {
    showLoading('正在检查课表冲突...');

    // 模拟冲突检查
    setTimeout(() => {
        hideLoading();
        
        const conflicts = [
            {
                course1: 'Java程序设计',
                course2: '数据结构',
                conflict: '教室冲突',
                time: '周一 08:00-09:40',
                classroom: 'A101'
            }
        ];

        if (conflicts.length === 0) {
            showAlert('未发现课表冲突', 'success');
        } else {
            let message = `发现 ${conflicts.length} 个冲突：\n`;
            conflicts.forEach((conflict, index) => {
                message += `${index + 1}. ${conflict.course1} 与 ${conflict.course2} 在 ${conflict.time} ${conflict.conflict}\n`;
            });
            showAlert(message, 'warning');
        }
    }, 1500);
}

/**
 * 自动排课
 */
function autoSchedule() {
    if (!confirm('自动排课将重新安排所有课程时间，确定要继续吗？')) {
        return;
    }

    showLoading('正在自动排课，请稍候...');

    // 模拟自动排课过程
    setTimeout(() => {
        hideLoading();
        showAlert('自动排课完成！已优化课表安排', 'success');
        
        // 刷新页面
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    }, 3000);
}

/**
 * 查看课表详情
 */
function viewScheduleDetail(id) {
    if (!id) {
        showAlert('课表ID无效', 'error');
        return;
    }

    // 跳转到详情页面
    window.location.href = `/admin/academic/schedules/detail?id=${id}`;
}

/**
 * 筛选课表
 */
function filterSchedules() {
    const form = document.querySelector('form[action="/admin/academic/schedules"]');
    if (form) {
        form.submit();
    }
}

/**
 * 重置筛选条件
 */
function resetFilter() {
    const form = document.querySelector('form[action="/admin/academic/schedules"]');
    if (form) {
        const inputs = form.querySelectorAll('input, select');
        inputs.forEach(input => {
            if (input.type === 'text') {
                input.value = '';
            } else if (input.tagName === 'SELECT') {
                input.selectedIndex = 0;
            }
        });
        form.submit();
    }
}

// 工具函数
function showAlert(message, type = 'info') {
    const alertClass = {
        'success': 'alert-success',
        'error': 'alert-danger',
        'warning': 'alert-warning',
        'info': 'alert-info'
    };

    const alertHtml = `
        <div class="alert ${alertClass[type]} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-triangle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    const container = document.querySelector('.container-fluid');
    if (container) {
        container.insertAdjacentHTML('afterbegin', alertHtml);
        
        setTimeout(() => {
            const alert = container.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 3000);
    }
}

function showLoading(message = '加载中...') {
    console.log('Loading:', message);
}

function hideLoading() {
    console.log('Loading hidden');
}
