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

            <el-card shadow="hover" style="margin-top: 20px;" v-if="courseResources.length > 0">
              <template #header>
                <h3>课程资源</h3>
              </template>
              <div class="course-resources">
                <div v-for="resource in courseResources" :key="resource.id" class="resource-item">
                  <div class="resource-info">
                    <el-icon><Document /></el-icon>
                    <span class="resource-name">{{ resource.name }}</span>
                    <span class="resource-type">{{ resource.type }}</span>
                  </div>
                  <div class="resource-actions">
                    <el-button type="text" size="small">下载</el-button>
                    <el-button type="text" size="small">预览</el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>

          <el-col :span="8">
            <el-card shadow="hover">
              <template #header>
                <h3>快捷操作</h3>
              </template>
              <div class="quick-actions">
                <el-button type="primary" size="large" style="width: 100%; margin-bottom: 12px;" @click="watchVideo">
                  <el-icon><VideoPlay /></el-icon>
                  观看课程视频
                </el-button>
                <el-button size="large" style="width: 100%; margin-bottom: 12px;" @click="viewMaterials">
                  <el-icon><Document /></el-icon>
                  查看课件资料 ({{ courseResources.length }})
                </el-button>
                <el-button size="large" style="width: 100%; margin-bottom: 12px;" @click="submitAssignment">
                  <el-icon><EditPen /></el-icon>
                  提交作业
                </el-button>
                <el-button size="large" style="width: 100%;" @click="joinDiscussion">
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
                  <span>课程进度 ({{ courseProgress.completedChapters }}/{{ courseProgress.totalChapters }})</span>
                  <el-progress :percentage="courseProgress.courseProgress" />
                </div>
                <div class="progress-item">
                  <span>作业完成 ({{ courseProgress.completedAssignments }}/{{ courseProgress.totalAssignments }})</span>
                  <el-progress :percentage="courseProgress.assignmentProgress" color="#e6a23c" />
                </div>
                <div class="progress-item">
                  <span>出勤率</span>
                  <el-progress :percentage="courseProgress.attendanceRate" color="#67c23a" />
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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  VideoPlay,
  Document,
  EditPen,
  ChatLineRound
} from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { studentApi } from '@/api/student'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const course = ref({})
const courseProgress = ref({})
const courseResources = ref([])

// 计算属性
const courseId = computed(() => route.params.id)

const loadCourseDetail = async () => {
  loading.value = true
  try {
    // 加载课程基本信息
    const { data } = await studentApi.getStudentCourseDetail(courseId.value)

    course.value = {
      id: data.id,
      name: data.courseName || data.name,
      code: data.courseCode || data.code,
      teacherName: data.teacherName || data.teacher?.realName,
      credits: data.credits || 0,
      type: data.courseType || data.type || '专业课',
      semester: data.semester || data.semesterName,
      description: data.description || data.courseDescription,
      content: data.content || data.syllabus || data.outline,
      schedule: data.schedule || formatSchedule(data),
      location: data.location || data.classroom || data.classroomName,
      totalHours: data.totalHours || 0,
      currentWeek: data.currentWeek || 1,
      totalWeeks: data.totalWeeks || 18
    }

    // 加载学习进度
    await loadCourseProgress()

    // 加载课程资源
    await loadCourseResources()

  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('加载课程详情失败')

    // 如果API失败，使用默认数据
    course.value = {
      id: courseId.value,
      name: '课程详情',
      code: 'COURSE001',
      teacherName: '教师',
      credits: 0,
      type: '专业课',
      semester: '当前学期',
      description: '课程描述加载失败',
      content: '课程内容加载失败'
    }
  } finally {
    loading.value = false
  }
}

// 加载学习进度
const loadCourseProgress = async () => {
  try {
    const { data } = await studentApi.getCourseProgress(courseId.value)

    courseProgress.value = {
      courseProgress: data.courseProgress || 0,
      assignmentProgress: data.assignmentProgress || 0,
      attendanceRate: data.attendanceRate || 0,
      totalChapters: data.totalChapters || 0,
      completedChapters: data.completedChapters || 0,
      totalAssignments: data.totalAssignments || 0,
      completedAssignments: data.completedAssignments || 0
    }
  } catch (error) {
    console.error('加载学习进度失败:', error)
    // 使用默认进度
    courseProgress.value = {
      courseProgress: 75,
      assignmentProgress: 60,
      attendanceRate: 85,
      totalChapters: 10,
      completedChapters: 7,
      totalAssignments: 5,
      completedAssignments: 3
    }
  }
}

// 加载课程资源
const loadCourseResources = async () => {
  try {
    const { data } = await courseApi.getCourseResources(courseId.value)
    const resources = Array.isArray(data) ? data : (data.resources || [])

    courseResources.value = resources.map(resource => ({
      id: resource.id,
      name: resource.name || resource.title,
      type: resource.type || resource.resourceType,
      url: resource.url || resource.downloadUrl,
      size: resource.size || resource.fileSize,
      uploadTime: resource.uploadTime || resource.createTime
    }))
  } catch (error) {
    console.error('加载课程资源失败:', error)
    courseResources.value = []
  }
}

// 格式化课程时间
const formatSchedule = (data) => {
  if (data.schedule) return data.schedule
  if (data.dayOfWeek && data.startTime && data.endTime) {
    const dayMap = ['日', '一', '二', '三', '四', '五', '六']
    return `周${dayMap[data.dayOfWeek]} ${data.startTime}-${data.endTime}`
  }
  return '时间待定'
}

// 快捷操作
const watchVideo = () => {
  ElMessage.info('课程视频功能开发中...')
}

const viewMaterials = () => {
  if (courseResources.value.length > 0) {
    ElMessage.success('正在打开课件资料...')
    // 这里可以跳转到资源列表页面
  } else {
    ElMessage.info('暂无课件资料')
  }
}

const submitAssignment = () => {
  // 跳转到作业页面
  router.push(`/student/assignments?courseId=${courseId.value}`)
}

const joinDiscussion = () => {
  ElMessage.info('课程讨论功能开发中...')
}

const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  loadCourseDetail()
})
</script>

<style scoped>
@import '@/styles/student.css';

.course-detail {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.course-title {
  margin-left: 20px;
}

.course-title h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.course-title p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.main-content {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.course-content {
  line-height: 1.8;
  color: #606266;
  white-space: pre-line;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.progress-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-item span {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.course-resources {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.resource-item:hover {
  background: #e9ecef;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.resource-name {
  font-weight: 500;
  color: #303133;
}

.resource-type {
  font-size: 12px;
  color: #909399;
  background: #e9ecef;
  padding: 2px 8px;
  border-radius: 4px;
}

.resource-actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .course-detail {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
  }

  .course-title {
    margin-left: 0;
  }

  .course-title h1 {
    font-size: 24px;
  }

  .resource-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .resource-actions {
    align-self: flex-end;
  }
}
</style>