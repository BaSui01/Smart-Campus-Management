const fs = require('fs');
const path = require('path');

// 通用组件模板
const createComponentTemplate = (title, description, icon = 'Document') => `
<template>
  <div class="page">
    <div class="page-header">
      <h1>${title}</h1>
      <p>${description}</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><${icon} /></el-icon>
          <h3>${title}功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ${icon} } from '@element-plus/icons-vue'
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  color: white;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.placeholder-content {
  text-align: center;
  padding: 60px 20px;
}

.placeholder-content h3 {
  margin: 20px 0 10px 0;
  color: #333;
}

.placeholder-content p {
  color: #666;
}
</style>
`;

// 需要创建的组件列表
const components = [
  // Student components
  { path: 'src/views/student/Exams.vue', title: '考试安排', description: '查看考试时间安排和成绩', icon: 'Calendar' },
  { path: 'src/views/student/Library.vue', title: '图书馆', description: '图书查询和借阅管理', icon: 'Reading' },
  
  // Teacher components  
  { path: 'src/views/teacher/CourseDetail.vue', title: '课程详情', description: '课程信息和教学管理', icon: 'Document' },
  { path: 'src/views/teacher/Students.vue', title: '学生管理', description: '查看和管理班级学生', icon: 'User' },
  { path: 'src/views/teacher/Assignments.vue', title: '作业管理', description: '发布和批改作业', icon: 'EditPen' },
  { path: 'src/views/teacher/Exams.vue', title: '考试管理', description: '考试安排和成绩录入', icon: 'Calendar' },
  { path: 'src/views/teacher/Schedule.vue', title: '课程表', description: '查看教学时间安排', icon: 'Calendar' },
  { path: 'src/views/teacher/Profile.vue', title: '个人资料', description: '教师个人信息管理', icon: 'User' },
  
  // Parent components
  { path: 'src/views/parent/Communication.vue', title: '家校沟通', description: '与老师交流沟通', icon: 'ChatLineRound' },
  { path: 'src/views/parent/Activities.vue', title: '校园活动', description: '查看校园活动信息', icon: 'Trophy' },
  { path: 'src/views/parent/Profile.vue', title: '个人资料', description: '家长个人信息管理', icon: 'User' },
  
  // Admin components
  { path: 'src/views/admin/Dashboard.vue', title: '管理员仪表盘', description: '系统总览和数据统计', icon: 'Odometer' },
  { path: 'src/views/admin/Users.vue', title: '用户管理', description: '管理系统用户账户', icon: 'User' },
  { path: 'src/views/admin/Courses.vue', title: '课程管理', description: '管理课程信息', icon: 'Document' },
  { path: 'src/views/admin/System.vue', title: '系统设置', description: '系统配置和参数设置', icon: 'Setting' }
];

// 创建目录（如果不存在）
const ensureDirectoryExists = (filePath) => {
  const dirname = path.dirname(filePath);
  if (!fs.existsSync(dirname)) {
    fs.mkdirSync(dirname, { recursive: true });
  }
};

// 创建组件文件
components.forEach(component => {
  const filePath = component.path;
  const fullPath = path.resolve(__dirname, filePath);
  
  // 确保目录存在
  ensureDirectoryExists(fullPath);
  
  // 如果文件不存在，则创建
  if (!fs.existsSync(fullPath)) {
    const content = createComponentTemplate(component.title, component.description, component.icon);
    fs.writeFileSync(fullPath, content.trim());
    console.log(\`Created: \${filePath}\`);
  } else {
    console.log(\`Exists: \${filePath}\`);
  }
});

console.log('All missing components created successfully!');