<template>
  <div class="admin-users">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理系统用户账户</p>
    </div>
    
    <div class="main-content">
      <el-card>
        <div class="placeholder-content">
          <el-icon :size="60" color="#409eff"><User /></el-icon>
          <h3>用户管理功能</h3>
          <p>此页面正在开发中...</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
</script>


<style scoped>
@import '@/styles/admin.css';
</style>