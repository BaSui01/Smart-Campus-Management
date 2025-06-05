<template>
  <div class="course-detail">
    <div class="page-header">
      <div class="header-content">
        <el-button @click="goBack" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="course-title">
          <h1>{{ course.name || '课程详情' }}</h1>
          <p>{{ course.description || '加载中...' }}</p>
        </div>
      </div>
    </div>

    <div class="main-content" v-loading="loading">
      <div v-if="course.id" class="course-info">
        <el-row :gutter="20">
          <el-col :span="16">
            <el-card shadow="hover">
              <template #header>
                <h3>课程信息</h3>
              </template>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="课程名称">
                  {{ course.name }}
                </el-descriptions-item>
                <el-descriptions-item label="课程代码">
                  {{ course.code }}
                </el-descriptions-item>
                <el-descriptions-item label="任课教师">
                  {{ course.teacherName }}
                </el-descriptions-item>
                <el-descriptions-item label="学分">
                  {{ course.credits }}
                </el-descriptions-item>
                <el-descriptions-item label="课程类型">
                  <el-tag>{{ course.type }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="开课学期">
                  {{ course.semester }}
                </el-descriptions-item>
                <el-descriptions-item label="课程描述" :span="2">
                  {{ course.description }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>

            <el-card shadow="hover" style="margin-top: 20px;">
              <template #header>
                <h3>课程内容</h3>
              </template>
              <div class="course-content">
                <p>{{ course.content || '暂无课程内容' }}</p>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <h3>快捷操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" style="width: 100%; margin-bottom: 12px;">
                  <el-icon><VideoPlay /></el-icon>
                  观看课程视频
                </el-button>
                <el-button size="large" style="width: 100%; margin-bottom: 12px;">
                  <el-icon><Document /></el-icon>
                  查看课件资料
                </el-button>
                <el-button size="large" style="width: 100%; margin-bottom: 12px;">
                  <el-icon><EditPen /></el-icon>
                  提交作业
                </el-button>
                <el-button size="large" style="width: 100%;">
                  <el-icon><ChatLineRound /></el-icon>
                  课程讨论
                </el-button>
              </div>
            </el-card>

            <el-card shadow="hover" style="margin-top: 20px;">
              <template #header>
                <h3>学习进度</h3>
              </template>
              <div class="progress-info">
                <div class="progress-item">
                  <span>课程进度</span>
                  <el-progress :percentage="75" />
                </div>
                <div class="progress-item">
                  <span>作业完成</span>
                  <el-progress :percentage="60" color="#e6a23c" />
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <el-empty v-else description="课程信息加载失败">
        <el-button type="primary" @click="loadCourseDetail">重新加载</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, 
  VideoPlay, 
  Document, 
  EditPen, 
  ChatLineRound 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const course = ref({})

const loadCourseDetail = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    course.value = {
      id: route.params.id,
      name: '高等数学',
      code: 'MATH001',
      teacherName: '张教授',
      credits: 4,
      type: '必修课',
      semester: '2024春季',
      description: '本课程主要讲授微积分、线性代数等数学基础知识',
      content: '第一章：函数与极限\n第二章：导数与微分\n第三章：积分\n第四章：级数\n第五章：多元函数微积分'
    }
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('加载课程详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  loadCourseDetail()
})
</script>

<style scoped>
.course-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  color: white;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.course-title h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
}

.course-title p {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
}

.progress-info {
  padding: 10px 0;
}

.progress-item {
  margin-bottom: 20px;
}

.progress-item span {
  display: block;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.course-content {
  line-height: 1.8;
  color: #666;
  white-space: pre-line;
}

@media (max-width: 768px) {
  .main-content {
    padding: 15px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>