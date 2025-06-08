<template>
  <div class="learning-monitor">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>学习监控</h2>
        <p>实时了解孩子的学习情况和表现</p>
      </div>
      <div class="header-right">
        <el-select v-model="selectedChildId" placeholder="选择孩子" @change="handleChildChange">
          <el-option
            v-for="child in children"
            :key="child.id"
            :label="child.name"
            :value="child.id"
          >
            <div class="child-option">
              <span>{{ child.name }}</span>
              <span class="child-class">{{ child.className }}</span>
            </div>
          </el-option>
        </el-select>
      </div>
    </div>

    <div v-if="selectedChildId" class="monitor-content">
      <!-- 学习概览 -->
      <el-row :gutter="20" class="overview-section">
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-icon">
                <el-icon size="32" color="#409eff"><TrendCharts /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-number">{{ learningOverview.averageScore }}</div>
                <div class="overview-label">平均成绩</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-icon">
                <el-icon size="32" color="#67c23a"><Calendar /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-number">{{ learningOverview.attendanceRate }}%</div>
                <div class="overview-label">出勤率</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-icon">
                <el-icon size="32" color="#e6a23c"><Document /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-number">{{ learningOverview.assignmentCompletion }}%</div>
                <div class="overview-label">作业完成率</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-icon">
                <el-icon size="32" color="#f56c6c"><Trophy /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-number">{{ learningOverview.classRank }}</div>
                <div class="overview-label">班级排名</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 学习趋势图表 -->
      <el-row :gutter="20" class="charts-section">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>成绩趋势</span>
                <el-select v-model="gradeChartPeriod" size="small" style="width: 120px">
                  <el-option label="近一月" value="month" />
                  <el-option label="近三月" value="quarter" />
                  <el-option label="本学期" value="semester" />
                </el-select>
              </div>
            </template>
            <div ref="gradeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>学习时长统计</span>
                <el-select v-model="studyTimeChartPeriod" size="small" style="width: 120px">
                  <el-option label="近一周" value="week" />
                  <el-option label="近一月" value="month" />
                </el-select>
              </div>
            </template>
            <div ref="studyTimeChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 详细信息标签页 -->
      <el-card class="details-card">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <!-- 最近成绩 -->
          <el-tab-pane label="最近成绩" name="grades">
            <el-table :data="recentGrades" style="width: 100%">
              <el-table-column prop="courseName" label="课程" width="120" />
              <el-table-column prop="examName" label="考试/作业" min-width="150" />
              <el-table-column prop="score" label="得分" width="80" align="center">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.score, row.totalScore)">
                    {{ row.score }}/{{ row.totalScore }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="classAverage" label="班级平均分" width="100" align="center" />
              <el-table-column prop="classRank" label="班级排名" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getRankType(row.classRank, row.totalStudents)" size="small">
                    {{ row.classRank }}/{{ row.totalStudents }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="examDate" label="考试时间" width="120">
                <template #default="{ row }">
                  {{ formatDate(row.examDate) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button size="small" @click="viewGradeDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 考勤记录 -->
          <el-tab-pane label="考勤记录" name="attendance">
            <div class="attendance-filter">
              <el-date-picker
                v-model="attendanceDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="loadAttendanceRecords"
              />
            </div>
            <el-table :data="attendanceRecords" style="width: 100%">
              <el-table-column prop="courseName" label="课程" width="120" />
              <el-table-column prop="attendanceDate" label="日期" width="120">
                <template #default="{ row }">
                  {{ formatDate(row.attendanceDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="attendanceTime" label="时间" width="100" />
              <el-table-column prop="attendanceStatus" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getAttendanceStatusType(row.attendanceStatus)">
                    {{ getAttendanceStatusText(row.attendanceStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="lateMinutes" label="迟到时长" width="100" align="center">
                <template #default="{ row }">
                  <span v-if="row.lateMinutes > 0">{{ row.lateMinutes }}分钟</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="remarks" label="备注" min-width="150" />
            </el-table>
          </el-tab-pane>

          <!-- 作业情况 -->
          <el-tab-pane label="作业情况" name="assignments">
            <el-table :data="assignmentRecords" style="width: 100%">
              <el-table-column prop="courseName" label="课程" width="120" />
              <el-table-column prop="assignmentTitle" label="作业标题" min-width="150" />
              <el-table-column prop="dueDate" label="截止时间" width="150">
                <template #default="{ row }">
                  {{ formatDateTime(row.dueDate) }}
                </template>
              </el-table-column>
              <el-table-column prop="submissionTime" label="提交时间" width="150">
                <template #default="{ row }">
                  <span v-if="row.submissionTime">
                    {{ formatDateTime(row.submissionTime) }}
                  </span>
                  <span v-else class="not-submitted">未提交</span>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getSubmissionStatusType(row.status)">
                    {{ getSubmissionStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="得分" width="100" align="center">
                <template #default="{ row }">
                  <span v-if="row.score !== null">{{ row.score }}/{{ row.maxScore }}</span>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button size="small" @click="viewAssignmentDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 课程表 -->
          <el-tab-pane label="课程表" name="schedule">
            <course-schedule-table :student-id="selectedChildId" />
          </el-tab-pane>

          <!-- 学习建议 -->
          <el-tab-pane label="学习建议" name="suggestions">
            <div class="suggestions-content">
              <el-alert
                v-for="suggestion in learningSuggestions"
                :key="suggestion.id"
                :title="suggestion.title"
                :type="suggestion.type"
                :description="suggestion.content"
                show-icon
                :closable="false"
                class="suggestion-item"
              />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-empty description="请选择要监控的孩子" />
    </div>

    <!-- 成绩详情对话框 -->
    <el-dialog
      v-model="gradeDetailVisible"
      title="成绩详情"
      width="600px"
    >
      <grade-detail-card
        v-if="selectedGrade"
        :grade="selectedGrade"
      />
    </el-dialog>

    <!-- 作业详情对话框 -->
    <el-dialog
      v-model="assignmentDetailVisible"
      title="作业详情"
      width="700px"
    >
      <assignment-detail-card
        v-if="selectedAssignment"
        :assignment="selectedAssignment"
        :readonly="true"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  TrendCharts, Calendar, Document, Trophy
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { parentApi } from '@/api/parent'
import { gradeApi } from '@/api/grade'
import { attendanceApi } from '@/api/attendance'
import { assignmentApi } from '@/api/assignment'
import CourseScheduleTable from '@/components/CourseScheduleTable.vue'
import GradeDetailCard from '@/components/GradeDetailCard.vue'
import AssignmentDetailCard from '@/components/AssignmentDetailCard.vue'
import { formatDate, formatDateTime } from '@/utils/date'

// 响应式数据
const children = ref([])
const selectedChildId = ref('')
const activeTab = ref('grades')
const gradeChartPeriod = ref('month')
const studyTimeChartPeriod = ref('week')
const attendanceDate = ref([])
const gradeDetailVisible = ref(false)
const assignmentDetailVisible = ref(false)
const selectedGrade = ref(null)
const selectedAssignment = ref(null)

// 图表引用
const gradeChartRef = ref(null)
const studyTimeChartRef = ref(null)
let gradeChart = null
let studyTimeChart = null

// 数据
const learningOverview = ref({
  averageScore: 0,
  attendanceRate: 0,
  assignmentCompletion: 0,
  classRank: 0
})

const recentGrades = ref([])
const attendanceRecords = ref([])
const assignmentRecords = ref([])
const learningSuggestions = ref([])

// 方法
const loadChildren = async () => {
  try {
    const response = await parentApi.getChildren()
    children.value = response.data
    if (children.value.length > 0) {
      selectedChildId.value = children.value[0].id
    }
  } catch (error) {
    ElMessage.error('加载孩子信息失败')
  }
}

const handleChildChange = () => {
  loadAllData()
}

const loadAllData = async () => {
  if (!selectedChildId.value) return
  
  await Promise.all([
    loadLearningOverview(),
    loadRecentGrades(),
    loadAttendanceRecords(),
    loadAssignmentRecords(),
    loadLearningSuggestions()
  ])
  
  await nextTick()
  initCharts()
}

const loadLearningOverview = async () => {
  try {
    const response = await parentApi.getLearningOverview(selectedChildId.value)
    learningOverview.value = response.data
  } catch (error) {
    ElMessage.error('加载学习概览失败')
  }
}

const loadRecentGrades = async () => {
  try {
    const response = await gradeApi.getStudentGrades(selectedChildId.value, {
      page: 1,
      size: 10,
      sortBy: 'examDate',
      sortDir: 'desc'
    })
    recentGrades.value = response.data.list
  } catch (error) {
    ElMessage.error('加载成绩记录失败')
  }
}

const loadAttendanceRecords = async () => {
  try {
    const params = {
      studentId: selectedChildId.value,
      page: 1,
      size: 20
    }
    
    if (attendanceDate.value && attendanceDate.value.length === 2) {
      params.startDate = formatDate(attendanceDate.value[0])
      params.endDate = formatDate(attendanceDate.value[1])
    }
    
    const response = await attendanceApi.getStudentAttendance(selectedChildId.value, params)
    attendanceRecords.value = response.data.list
  } catch (error) {
    ElMessage.error('加载考勤记录失败')
  }
}

const loadAssignmentRecords = async () => {
  try {
    const response = await assignmentApi.getStudentAssignments({
      studentId: selectedChildId.value,
      page: 1,
      size: 20
    })
    assignmentRecords.value = response.data.list
  } catch (error) {
    ElMessage.error('加载作业记录失败')
  }
}

const loadLearningSuggestions = async () => {
  try {
    const response = await parentApi.getLearningSuggestions(selectedChildId.value)
    learningSuggestions.value = response.data
  } catch (error) {
    console.error('加载学习建议失败:', error)
  }
}

const initCharts = () => {
  initGradeChart()
  initStudyTimeChart()
}

const initGradeChart = async () => {
  if (!gradeChartRef.value) return
  
  try {
    const response = await parentApi.getGradeTrend(selectedChildId.value, gradeChartPeriod.value)
    const data = response.data
    
    if (gradeChart) {
      gradeChart.dispose()
    }
    
    gradeChart = echarts.init(gradeChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['成绩', '班级平均分']
      },
      xAxis: {
        type: 'category',
        data: data.dates
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 100
      },
      series: [
        {
          name: '成绩',
          type: 'line',
          data: data.scores,
          itemStyle: { color: '#409eff' }
        },
        {
          name: '班级平均分',
          type: 'line',
          data: data.classAverages,
          itemStyle: { color: '#67c23a' }
        }
      ]
    }
    
    gradeChart.setOption(option)
  } catch (error) {
    console.error('初始化成绩图表失败:', error)
  }
}

const initStudyTimeChart = async () => {
  if (!studyTimeChartRef.value) return
  
  try {
    const response = await parentApi.getStudyTimeStats(selectedChildId.value, studyTimeChartPeriod.value)
    const data = response.data
    
    if (studyTimeChart) {
      studyTimeChart.dispose()
    }
    
    studyTimeChart = echarts.init(studyTimeChartRef.value)
    
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: data.dates
      },
      yAxis: {
        type: 'value',
        name: '小时'
      },
      series: [
        {
          name: '学习时长',
          type: 'bar',
          data: data.hours,
          itemStyle: { color: '#e6a23c' }
        }
      ]
    }
    
    studyTimeChart.setOption(option)
  } catch (error) {
    console.error('初始化学习时长图表失败:', error)
  }
}

const handleTabClick = (tab) => {
  if (tab.name === 'attendance') {
    loadAttendanceRecords()
  } else if (tab.name === 'assignments') {
    loadAssignmentRecords()
  }
}

const viewGradeDetail = async (grade) => {
  try {
    const response = await gradeApi.getGradeDetail(grade.id)
    selectedGrade.value = response.data
    gradeDetailVisible.value = true
  } catch (error) {
    ElMessage.error('加载成绩详情失败')
  }
}

const viewAssignmentDetail = async (assignment) => {
  try {
    const response = await assignmentApi.getAssignmentById(assignment.assignmentId)
    selectedAssignment.value = response.data
    assignmentDetailVisible.value = true
  } catch (error) {
    ElMessage.error('加载作业详情失败')
  }
}

// 工具方法
const getScoreClass = (score, totalScore) => {
  const percentage = (score / totalScore) * 100
  if (percentage >= 90) return 'excellent'
  if (percentage >= 80) return 'good'
  if (percentage >= 70) return 'average'
  return 'poor'
}

const getRankType = (rank, total) => {
  const percentage = (rank / total) * 100
  if (percentage <= 10) return 'success'
  if (percentage <= 30) return 'primary'
  if (percentage <= 60) return 'warning'
  return 'danger'
}

const getAttendanceStatusType = (status) => {
  const statusMap = {
    present: 'success',
    absent: 'danger',
    late: 'warning',
    early_leave: 'warning',
    sick_leave: 'info',
    personal_leave: 'info'
  }
  return statusMap[status] || 'info'
}

const getAttendanceStatusText = (status) => {
  const statusMap = {
    present: '出勤',
    absent: '缺勤',
    late: '迟到',
    early_leave: '早退',
    sick_leave: '病假',
    personal_leave: '事假'
  }
  return statusMap[status] || status
}

const getSubmissionStatusType = (status) => {
  const statusMap = {
    submitted: 'success',
    graded: 'primary',
    late: 'warning',
    not_submitted: 'danger'
  }
  return statusMap[status] || 'info'
}

const getSubmissionStatusText = (status) => {
  const statusMap = {
    submitted: '已提交',
    graded: '已批改',
    late: '迟交',
    not_submitted: '未提交'
  }
  return statusMap[status] || status
}

// 监听器
watch(gradeChartPeriod, () => {
  initGradeChart()
})

watch(studyTimeChartPeriod, () => {
  initStudyTimeChart()
})

// 生命周期
onMounted(() => {
  loadChildren()
})

watch(selectedChildId, (newVal) => {
  if (newVal) {
    loadAllData()
  }
})
</script>

<style scoped>
.learning-monitor {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 5px 0;
  color: #303133;
}

.header-left p {
  margin: 0;
  color: #606266;
}

.child-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.child-class {
  color: #909399;
  font-size: 12px;
}

.overview-section {
  margin-bottom: 20px;
}

.overview-card {
  cursor: pointer;
  transition: all 0.3s;
}

.overview-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.overview-info {
  flex: 1;
}

.overview-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.overview-label {
  color: #606266;
  font-size: 14px;
}

.charts-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.details-card {
  margin-bottom: 20px;
}

.attendance-filter {
  margin-bottom: 15px;
}

.excellent {
  color: #67c23a;
  font-weight: bold;
}

.good {
  color: #409eff;
  font-weight: bold;
}

.average {
  color: #e6a23c;
}

.poor {
  color: #f56c6c;
  font-weight: bold;
}

.not-submitted {
  color: #f56c6c;
}

.suggestions-content {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.suggestion-item {
  margin-bottom: 0;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
</style>
