<template>
  <div class="teacher-profile">
    <div class="page-header">
      <h1>个人资料</h1>
      <p>教师个人信息管理</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><User /></el-icon>
          <h3>个人资料功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
</script>


<style >
@import '@/styles/teacher.css';
</style>
