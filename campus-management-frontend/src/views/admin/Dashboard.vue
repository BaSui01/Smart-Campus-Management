<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h1>管理员仪表盘</h1>
      <p>系统总览和数据统计</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><Odometer /></el-icon>
          <h3>管理员仪表盘功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { Odometer } from '@element-plus/icons-vue'
</script>


<style scoped>
@import '@/styles/admin.css';
</style>