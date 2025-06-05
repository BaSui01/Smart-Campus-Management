<template>
  <div class="cases-page">
    <div class="container">
      <div class="page-header">
        <h1>案例展示</h1>
        <p>查看我们为各类学校提供的智慧校园解决方案实际应用案例</p>
      </div>
      
      <div class="cases-grid">
        <div class="case-card">
          <div class="case-image case-image-1">
            <el-icon :size="48" style="color: white;"><School /></el-icon>
          </div>
          <div class="case-content">
            <h3>某重点中学智慧校园建设</h3>
            <p>覆盖3000名学生，实现了全方位的数字化管理</p>
            <div class="case-tags">
              <span class="tag">中学</span>
              <span class="tag">3000人</span>
              <span class="tag">全模块</span>
            </div>
          </div>
        </div>
        
        <div class="case-card">
          <div class="case-image case-image-2">
            <el-icon :size="48" style="color: white;"><Reading /></el-icon>
          </div>
          <div class="case-content">
            <h3>某职业学院教学管理系统</h3>
            <p>专业的职业教育管理，提升教学质量</p>
            <div class="case-tags">
              <span class="tag">职业学院</span>
              <span class="tag">5000人</span>
              <span class="tag">教学管理</span>
            </div>
          </div>
        </div>
        
        <div class="case-card">
          <div class="case-image case-image-3">
            <el-icon :size="48" style="color: white;"><UserFilled /></el-icon>
          </div>
          <div class="case-content">
            <h3>某示范小学家校互通平台</h3>
            <p>加强家校沟通，关注学生全面发展</p>
            <div class="case-tags">
              <span class="tag">小学</span>
              <span class="tag">1200人</span>
              <span class="tag">家校互通</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 案例展示页面
</script>

<style scoped>
.cases-page {
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

.cases-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 40px;
}

.case-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.case-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15);
}

.case-image {
  width: 100%;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.case-image-1 {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.case-image-2 {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.case-image-3 {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.case-content {
  padding: 30px;
}

.case-content h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
}

.case-content p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.case-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.tag {
  display: inline-block;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
</style>