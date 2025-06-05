<template>
  <div class="exams-page">
    <div class="page-header">
      <h1>考试安排</h1>
      <p>查看考试时间安排和成绩</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><Calendar /></el-icon>
          <h3>考试安排功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { Calendar } from '@element-plus/icons-vue'
</script>

<style scoped>
@import '@/styles/student.css';
</style>