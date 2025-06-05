<template>
  <div class="teacher-schedule">
    <div class="page-header">
      <h1>课程表</h1>
      <p>查看教学时间安排</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><Calendar /></el-icon>
          <h3>课程表功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { Calendar } from '@element-plus/icons-vue'
</script>


<style >
@import '@/styles/teacher.css';
</style>
