<template>
  <div class="teacher-course-detail">
    <div class="page-header">
      <h1>课程详情</h1>
      <p>课程信息和教学管理</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><Document /></el-icon>
          <h3>课程详情功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { Document } from '@element-plus/icons-vue'
</script>

<style scoped>
.teacher-course-detail {
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