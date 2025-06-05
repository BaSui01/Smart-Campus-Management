<template>
  <div class="contact-page">
    <div class="container">
      <div class="page-header">
        <h1>联系我们</h1>
        <p>我们随时准备为您提供专业的智慧校园解决方案咨询服务</p>
      </div>
      
      <div class="contact-content">
        <div class="contact-info">
          <div class="info-card">
            <div class="info-icon">
              <el-icon :size="32" color="#667eea"><Location /></el-icon>
            </div>
            <h3>公司地址</h3>
            <p>北京市海淀区中关村软件园<br>智慧教育大厦 15层</p>
          </div>
          
          <div class="info-card">
            <div class="info-icon">
              <el-icon :size="32" color="#67c23a"><Phone /></el-icon>
            </div>
            <h3>联系电话</h3>
            <p>400-123-4567<br>010-8888-9999</p>
          </div>
          
          <div class="info-card">
            <div class="info-icon">
              <el-icon :size="32" color="#e6a23c"><Message /></el-icon>
            </div>
            <h3>邮箱地址</h3>
            <p>info@smartcampus.com<br>support@smartcampus.com</p>
          </div>
          
          <div class="info-card">
            <div class="info-icon">
              <el-icon :size="32" color="#f56c6c"><Clock /></el-icon>
            </div>
            <h3>服务时间</h3>
            <p>周一至周五 9:00-18:00<br>7×24小时在线技术支持</p>
          </div>
        </div>
        
        <div class="contact-form">
          <h2>在线咨询</h2>
          <el-form :model="form" label-width="100px" size="large">
            <el-form-item label="姓名">
              <el-input v-model="form.name" placeholder="请输入您的姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入您的手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入您的邮箱" />
            </el-form-item>
            <el-form-item label="学校类型">
              <el-select v-model="form.schoolType" placeholder="请选择学校类型" style="width: 100%">
                <el-option label="小学" value="primary" />
                <el-option label="初中" value="middle" />
                <el-option label="高中" value="high" />
                <el-option label="职业学校" value="vocational" />
                <el-option label="大学" value="university" />
              </el-select>
            </el-form-item>
            <el-form-item label="咨询内容">
              <el-input
                v-model="form.message"
                type="textarea"
                :rows="4"
                placeholder="请描述您的需求或问题"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="submitForm" style="width: 100%">
                提交咨询
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Location, Phone, Message, Clock } from '@element-plus/icons-vue'

const form = reactive({
  name: '',
  phone: '',
  email: '',
  schoolType: '',
  message: ''
})

const submitForm = () => {
  // 简单的表单验证
  if (!form.name || !form.phone || !form.message) {
    ElMessage.warning('请填写必要信息')
    return
  }
  
  // 模拟提交
  ElMessage.success('咨询信息已提交，我们会尽快与您联系！')
  
  // 重置表单
  Object.keys(form).forEach(key => {
    form[key] = ''
  })
}
</script>

<style scoped>
.contact-page {
  min-height: 100vh;
  padding: 120px 0 60px;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 60px;
}

.page-header h1 {
  font-size: 42px;
  font-weight: 700;
  color: #333;
  margin-bottom: 20px;
}

.page-header p {
  font-size: 18px;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

.contact-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: start;
}

.contact-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.info-card {
  background: white;
  padding: 30px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15);
}

.info-icon {
  margin-bottom: 20px;
}

.info-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.info-card p {
  color: #666;
  line-height: 1.6;
}

.contact-form {
  background: white;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.contact-form h2 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

@media (max-width: 768px) {
  .contact-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .contact-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .page-header h1 {
    font-size: 32px;
  }
  
  .contact-form {
    padding: 30px 20px;
  }
}
</style>