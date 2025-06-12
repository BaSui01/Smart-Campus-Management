<template>
  <div class="grade-detail-card">
    <!-- 成绩概览 -->
    <div class="grade-overview">
      <div class="grade-header">
        <div class="grade-score">
          <span class="score-value" :class="getScoreClass(grade.score, grade.totalScore)">
            {{ grade.score }}
          </span>
          <span class="score-total">/ {{ grade.totalScore }}</span>
        </div>
        <div class="grade-percentage">
          <el-progress
            :percentage="getPercentage(grade.score, grade.totalScore)"
            :color="getProgressColor(grade.score, grade.totalScore)"
            :stroke-width="8"
          />
        </div>
      </div>
      
      <div class="grade-meta">
        <el-tag :type="getGradeType(grade.score, grade.totalScore)" size="large">
          {{ getGradeLevel(grade.score, grade.totalScore) }}
        </el-tag>
        <div class="grade-rank" v-if="grade.classRank">
          班级排名：{{ grade.classRank }}/{{ grade.totalStudents }}
        </div>
      </div>
    </div>

    <!-- 详细信息 -->
    <el-divider />
    
    <el-descriptions :column="2" border>
      <el-descriptions-item label="课程名称">
        <div class="course-info">
          <span class="course-name">{{ grade.courseName }}</span>
          <el-tag v-if="grade.courseCode" size="small" type="info">
            {{ grade.courseCode }}
          </el-tag>
        </div>
      </el-descriptions-item>
      
      <el-descriptions-item label="考试类型">
        <el-tag :type="getExamTypeTag(grade.examType)" size="small">
          {{ getExamTypeText(grade.examType) }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="考试名称">
        {{ grade.examName || grade.assignmentName || '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="考试时间">
        {{ formatDate(grade.examDate) }}
      </el-descriptions-item>
      
      <el-descriptions-item label="任课教师">
        {{ grade.teacherName || '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="学分">
        {{ grade.credits || '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="班级平均分">
        <span class="average-score">{{ grade.classAverage || '-' }}</span>
      </el-descriptions-item>
      
      <el-descriptions-item label="最高分">
        <span class="highest-score">{{ grade.highestScore || '-' }}</span>
      </el-descriptions-item>
      
      <el-descriptions-item label="及格率">
        {{ grade.passRate ? `${grade.passRate}%` : '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="成绩状态">
        <el-tag :type="getStatusTag(grade.status)" size="small">
          {{ getStatusText(grade.status) }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="录入时间">
        {{ formatDateTime(grade.createTime) }}
      </el-descriptions-item>
      
      <el-descriptions-item label="更新时间">
        {{ formatDateTime(grade.updateTime) }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 成绩分析 -->
    <div class="grade-analysis" v-if="showAnalysis">
      <el-divider>
        <span class="analysis-title">成绩分析</span>
      </el-divider>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="analysis-item">
            <div class="analysis-label">与班级平均分对比</div>
            <div class="analysis-value">
              <span :class="getComparisonClass(grade.score, grade.classAverage)">
                {{ getComparisonText(grade.score, grade.classAverage) }}
              </span>
            </div>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="analysis-item">
            <div class="analysis-label">超越百分比</div>
            <div class="analysis-value">
              <span class="exceed-percentage">
                {{ getExceedPercentage(grade.classRank, grade.totalStudents) }}%
              </span>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <div class="grade-distribution" v-if="grade.distribution">
        <div class="distribution-title">成绩分布</div>
        <div class="distribution-chart" ref="chartRef"></div>
      </div>
    </div>

    <!-- 评语和建议 -->
    <div class="grade-comments" v-if="grade.comments || grade.suggestions">
      <el-divider>
        <span class="comments-title">教师评语</span>
      </el-divider>
      
      <div v-if="grade.comments" class="teacher-comments">
        <el-alert
          :title="grade.comments"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      
      <div v-if="grade.suggestions" class="teacher-suggestions">
        <el-alert
          title="学习建议"
          :description="grade.suggestions"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="grade-actions" v-if="showActions">
      <el-divider />
      <div class="actions-container">
        <el-button v-if="allowPrint" @click="printGrade">
          <el-icon><Printer /></el-icon>
          打印成绩单
        </el-button>
        
        <el-button v-if="allowExport" @click="exportGrade">
          <el-icon><Download /></el-icon>
          导出详情
        </el-button>
        
        <el-button v-if="allowAppeal" type="warning" @click="appealGrade">
          <el-icon><Warning /></el-icon>
          成绩申诉
        </el-button>
        
        <el-button v-if="allowViewPaper" type="primary" @click="viewExamPaper">
          <el-icon><Document /></el-icon>
          查看试卷
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Printer, 
  Download, 
  Warning, 
  Document 
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

// Props
const props = defineProps({
  grade: {
    type: Object,
    required: true
  },
  showAnalysis: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  allowPrint: {
    type: Boolean,
    default: true
  },
  allowExport: {
    type: Boolean,
    default: true
  },
  allowAppeal: {
    type: Boolean,
    default: false
  },
  allowViewPaper: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['print', 'export', 'appeal', 'viewPaper'])

// 响应式数据
const chartRef = ref()
let chart = null

// 方法
const getScoreClass = (score, total) => {
  const percentage = (score / total) * 100
  if (percentage >= 90) return 'excellent'
  if (percentage >= 80) return 'good'
  if (percentage >= 70) return 'average'
  if (percentage >= 60) return 'pass'
  return 'fail'
}

const getPercentage = (score, total) => {
  return Math.round((score / total) * 100)
}

const getProgressColor = (score, total) => {
  const percentage = (score / total) * 100
  if (percentage >= 90) return '#67c23a'
  if (percentage >= 80) return '#409eff'
  if (percentage >= 70) return '#e6a23c'
  if (percentage >= 60) return '#f56c6c'
  return '#909399'
}

const getGradeType = (score, total) => {
  const percentage = (score / total) * 100
  if (percentage >= 90) return 'success'
  if (percentage >= 80) return 'primary'
  if (percentage >= 70) return 'warning'
  if (percentage >= 60) return 'danger'
  return 'info'
}

const getGradeLevel = (score, total) => {
  const percentage = (score / total) * 100
  if (percentage >= 90) return '优秀'
  if (percentage >= 80) return '良好'
  if (percentage >= 70) return '中等'
  if (percentage >= 60) return '及格'
  return '不及格'
}

const getExamTypeTag = (type) => {
  const typeMap = {
    'midterm': 'primary',
    'final': 'danger',
    'quiz': 'success',
    'assignment': 'warning',
    'project': 'info'
  }
  return typeMap[type] || 'info'
}

const getExamTypeText = (type) => {
  const textMap = {
    'midterm': '期中考试',
    'final': '期末考试',
    'quiz': '随堂测验',
    'assignment': '作业',
    'project': '项目作业'
  }
  return textMap[type] || type
}

const getStatusTag = (status) => {
  const statusMap = {
    'published': 'success',
    'draft': 'warning',
    'reviewing': 'primary'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'published': '已发布',
    'draft': '草稿',
    'reviewing': '审核中'
  }
  return textMap[status] || status
}

const getComparisonClass = (score, average) => {
  if (!average) return ''
  const diff = score - average
  if (diff > 10) return 'much-higher'
  if (diff > 0) return 'higher'
  if (diff > -10) return 'similar'
  return 'lower'
}

const getComparisonText = (score, average) => {
  if (!average) return '-'
  const diff = score - average
  if (diff > 10) return `高出${diff.toFixed(1)}分 (优秀)`
  if (diff > 0) return `高出${diff.toFixed(1)}分`
  if (diff > -10) return `低${Math.abs(diff).toFixed(1)}分`
  return `低${Math.abs(diff).toFixed(1)}分 (需努力)`
}

const getExceedPercentage = (rank, total) => {
  if (!rank || !total) return 0
  return Math.round(((total - rank) / total) * 100)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

// 操作方法
const printGrade = () => {
  emit('print', props.grade)
  ElMessage.success('正在准备打印...')
}

const exportGrade = () => {
  emit('export', props.grade)
  ElMessage.success('正在导出成绩详情...')
}

const appealGrade = () => {
  emit('appeal', props.grade)
  ElMessage.info('成绩申诉功能开发中...')
}

const viewExamPaper = () => {
  emit('viewPaper', props.grade)
  ElMessage.info('查看试卷功能开发中...')
}

// 初始化图表
const initChart = async () => {
  if (!chartRef.value || !props.grade.distribution) return
  
  await nextTick()
  
  if (chart) {
    chart.dispose()
  }
  
  chart = echarts.init(chartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '成绩分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: props.grade.distribution.excellent || 0, name: '优秀(90-100)' },
          { value: props.grade.distribution.good || 0, name: '良好(80-89)' },
          { value: props.grade.distribution.average || 0, name: '中等(70-79)' },
          { value: props.grade.distribution.pass || 0, name: '及格(60-69)' },
          { value: props.grade.distribution.fail || 0, name: '不及格(<60)' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 生命周期
onMounted(() => {
  if (props.showAnalysis && props.grade.distribution) {
    initChart()
  }
})
</script>

<style scoped>
.grade-detail-card {
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.grade-overview {
  text-align: center;
  margin-bottom: 20px;
}

.grade-header {
  margin-bottom: 16px;
}

.grade-score {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 12px;
}

.score-value {
  font-size: 48px;
  font-weight: bold;
  margin-right: 8px;
}

.score-value.excellent {
  color: #67c23a;
}

.score-value.good {
  color: #409eff;
}

.score-value.average {
  color: #e6a23c;
}

.score-value.pass {
  color: #f56c6c;
}

.score-value.fail {
  color: #909399;
}

.score-total {
  font-size: 24px;
  color: #909399;
}

.grade-percentage {
  margin: 0 auto;
  max-width: 300px;
}

.grade-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.grade-rank {
  font-size: 14px;
  color: #606266;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-name {
  font-weight: 600;
}

.average-score,
.highest-score {
  font-weight: 600;
  color: #409eff;
}

.grade-analysis {
  margin-top: 20px;
}

.analysis-title,
.comments-title {
  font-weight: 600;
  color: #303133;
}

.analysis-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
}

.analysis-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.analysis-value {
  font-size: 18px;
  font-weight: 600;
}

.much-higher {
  color: #67c23a;
}

.higher {
  color: #409eff;
}

.similar {
  color: #e6a23c;
}

.lower {
  color: #f56c6c;
}

.exceed-percentage {
  color: #409eff;
}

.grade-distribution {
  margin-top: 20px;
}

.distribution-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.distribution-chart {
  height: 300px;
}

.grade-comments {
  margin-top: 20px;
}

.teacher-comments,
.teacher-suggestions {
  margin-top: 12px;
}

.grade-actions {
  margin-top: 20px;
}

.actions-container {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .grade-detail-card {
    padding: 16px;
  }
  
  .score-value {
    font-size: 36px;
  }
  
  .score-total {
    font-size: 18px;
  }
  
  .grade-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .actions-container {
    flex-direction: column;
  }
  
  .actions-container .el-button {
    width: 100%;
  }
}
</style>
