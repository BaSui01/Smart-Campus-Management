<template>
  <div class="student-grades">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>成绩查询</h1>
          <p>查看您的学习成绩和学业进展</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="exportGrades">
            <el-icon><Download /></el-icon>
            导出成绩单
          </el-button>
          <el-button @click="generateReport">
            <el-icon><Document /></el-icon>
            生成报告
          </el-button>
        </div>
      </div>
    </div>

    <!-- 成绩概览统计 -->
    <div class="stats-overview">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="(stat, index) in gradeStats" :key="index">
          <div class="stat-card" :class="`stat-${stat.type}`">
            <div class="stat-icon">
              <el-icon :size="24" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-description">{{ stat.description }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- GPA趋势图表 -->
    <el-card class="trend-chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon color="#409eff"><TrendCharts /></el-icon>
            <h3>GPA变化趋势</h3>
          </div>
          <el-select v-model="trendPeriod" size="small" @change="loadTrendData">
            <el-option label="近一年" value="year" />
            <el-option label="近半年" value="halfYear" />
            <el-option label="本学期" value="semester" />
          </el-select>
        </div>
      </template>
      
      <div v-loading="loading.trend" class="chart-container">
        <div ref="trendChartRef" class="trend-chart"></div>
      </div>
    </el-card>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程名称"
            clearable
            :prefix-icon="Search"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterSemester" placeholder="学期" clearable @change="handleFilter">
            <el-option 
              v-for="semester in semesters" 
              :key="semester.value" 
              :label="semester.label" 
              :value="semester.value" 
            />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterType" placeholder="考试类型" clearable @change="handleFilter">
            <el-option label="期中考试" value="midterm" />
            <el-option label="期末考试" value="final" />
            <el-option label="平时作业" value="homework" />
            <el-option label="实验成绩" value="lab" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterStatus" placeholder="成绩状态" clearable @change="handleFilter">
            <el-option label="已发布" value="published" />
            <el-option label="待发布" value="pending" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="table">
              <el-icon><List /></el-icon>
              表格视图
            </el-radio-button>
            <el-radio-button label="chart">
              <el-icon><PieChart /></el-icon>
              图表视图
            </el-radio-button>
          </el-radio-group>
        </el-col>
      </el-row>
    </el-card>

    <!-- 成绩内容 -->
    <el-card class="grades-content" shadow="hover">
      <template #header>
        <div class="content-header">
          <h3>成绩详情</h3>
          <div class="header-controls">
            <el-button size="small" @click="refreshGrades">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading.grades">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'" class="grades-table">
          <el-table 
            :data="filteredGrades" 
            style="width: 100%"
            :row-class-name="getRowClassName"
            @row-click="viewGradeDetail"
          >
            <el-table-column label="课程信息" min-width="200" fixed="left">
              <template #default="{ row }">
                <div class="course-info">
                  <div class="course-name">{{ row.courseName }}</div>
                  <div class="course-code">{{ row.courseCode }}</div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="examType" label="考试类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getExamTypeTag(row.examType)" size="small">
                  {{ getExamTypeName(row.examType) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="score" label="分数" width="80" align="center">
              <template #default="{ row }">
                <span :class="getScoreClass(row.score)">{{ row.score }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="grade" label="等级" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="getGradeType(row.grade)" size="small">
                  {{ row.grade }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="gpa" label="绩点" width="80" align="center">
              <template #default="{ row }">
                <span class="gpa-value">{{ row.gpa }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="credits" label="学分" width="80" align="center" />

            <el-table-column prop="rank" label="排名" width="100" align="center">
              <template #default="{ row }">
                <span v-if="row.rank" class="rank-info">
                  {{ row.rank }}/{{ row.totalStudents }}
                </span>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>

            <el-table-column prop="semester" label="学期" width="100" align="center" />

            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="publishTime" label="发布时间" width="120" align="center">
              <template #default="{ row }">
                <span v-if="row.publishTime">{{ formatDate(row.publishTime) }}</span>
                <span v-else class="text-muted">未发布</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="120" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click.stop="viewGradeDetail(row)">
                  详情
                </el-button>
                <el-button 
                  v-if="row.status === 'published'" 
                  size="small" 
                  type="primary" 
                  @click.stop="viewAnalysis(row)"
                >
                  分析
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 图表视图 -->
        <div v-else class="grades-charts">
          <el-row :gutter="20">
            <!-- 成绩分布饼图 -->
            <el-col :xs="24" :lg="12">
              <div class="chart-container">
                <h4>成绩分布</h4>
                <div ref="distributionChartRef" class="chart"></div>
              </div>
            </el-col>
            
            <!-- 各科成绩雷达图 -->
            <el-col :xs="24" :lg="12">
              <div class="chart-container">
                <h4>各科成绩对比</h4>
                <div ref="radarChartRef" class="chart"></div>
              </div>
            </el-col>
          </el-row>

          <!-- 学期成绩对比柱状图 -->
          <div class="chart-container full-width">
            <h4>学期成绩对比</h4>
            <div ref="semesterChartRef" class="chart large"></div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading.grades && filteredGrades.length === 0" description="暂无成绩记录">
          <el-button type="primary" @click="refreshGrades">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="filteredGrades.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 成绩详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedGrade?.courseName} - 成绩详情`"
      width="600px"
    >
      <div v-if="selectedGrade" class="grade-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedGrade.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedGrade.courseCode }}
          </el-descriptions-item>
          <el-descriptions-item label="考试类型">
            <el-tag :type="getExamTypeTag(selectedGrade.examType)">
              {{ getExamTypeName(selectedGrade.examType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="考试时间">
            {{ formatDate(selectedGrade.examTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="分数">
            <span :class="getScoreClass(selectedGrade.score)" style="font-size: 18px; font-weight: bold;">
              {{ selectedGrade.score }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="等级">
            <el-tag :type="getGradeType(selectedGrade.grade)" size="large">
              {{ selectedGrade.grade }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="绩点">
            <span class="gpa-value" style="font-size: 16px; font-weight: bold;">
              {{ selectedGrade.gpa }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedGrade.credits }}
          </el-descriptions-item>
          <el-descriptions-item label="班级排名">
            <span v-if="selectedGrade.rank" class="rank-display">
              第 {{ selectedGrade.rank }} 名 / 共 {{ selectedGrade.totalStudents }} 人
            </span>
            <span v-else>暂无排名</span>
          </el-descriptions-item>
          <el-descriptions-item label="学期">
            {{ selectedGrade.semester }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ selectedGrade.teacherName }}
          </el-descriptions-item>
          <el-descriptions-item label="发布时间">
            {{ formatDate(selectedGrade.publishTime) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 成绩分析 -->
        <div class="grade-analysis" v-if="selectedGrade.analysis">
          <h4>成绩分析</h4>
          <div class="analysis-content">
            <div class="analysis-item">
              <span class="label">班级平均分：</span>
              <span class="value">{{ selectedGrade.analysis.classAverage }}</span>
            </div>
            <div class="analysis-item">
              <span class="label">全年级平均分：</span>
              <span class="value">{{ selectedGrade.analysis.gradeAverage }}</span>
            </div>
            <div class="analysis-item">
              <span class="label">最高分：</span>
              <span class="value">{{ selectedGrade.analysis.maxScore }}</span>
            </div>
            <div class="analysis-item">
              <span class="label">最低分：</span>
              <span class="value">{{ selectedGrade.analysis.minScore }}</span>
            </div>
          </div>
        </div>

        <!-- 教师评语 -->
        <div class="teacher-comment" v-if="selectedGrade.comment">
          <h4>教师评语</h4>
          <div class="comment-content">
            {{ selectedGrade.comment }}
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="downloadCertificate(selectedGrade)">
            下载成绩单
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Download, 
  Document, 
  TrendCharts, 
  Search, 
  List, 
  PieChart, 
  Refresh 
} from '@element-plus/icons-vue'
import { gradeApi } from '@/api/grade'
import { studentApi } from '@/api/student'
import * as echarts from 'echarts'

// 响应式数据
const loading = ref({
  grades: false,
  trend: false,
  charts: false
})

const searchQuery = ref('')
const filterSemester = ref('')
const filterType = ref('')
const filterStatus = ref('')
const viewMode = ref('table')
const trendPeriod = ref('semester')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const grades = ref([])
const semesters = ref([])
const gradeStats = ref([])
const trendData = ref([])

const detailDialogVisible = ref(false)
const selectedGrade = ref(null)

// 图表引用
const trendChartRef = ref()
const distributionChartRef = ref()
const radarChartRef = ref()
const semesterChartRef = ref()

// 图表实例
let trendChart = null
let distributionChart = null
let radarChart = null
let semesterChart = null

// 计算属性
const filteredGrades = computed(() => {
  let result = grades.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(grade => 
      grade.courseName.toLowerCase().includes(query) ||
      grade.courseCode.toLowerCase().includes(query)
    )
  }

  // 学期过滤
  if (filterSemester.value) {
    result = result.filter(grade => grade.semester === filterSemester.value)
  }

  // 类型过滤
  if (filterType.value) {
    result = result.filter(grade => grade.examType === filterType.value)
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter(grade => grade.status === filterStatus.value)
  }

  return result
})

// 方法
const loadGrades = async () => {
  loading.value.grades = true
  
  try {
    const { data } = await gradeApi.getStudentGrades({
      page: currentPage.value,
      size: pageSize.value,
      semester: filterSemester.value,
      examType: filterType.value,
      status: filterStatus.value,
      search: searchQuery.value
    })
    
    grades.value = data.grades
    total.value = data.total
    
    await loadGradeStats()
  } catch (error) {
    console.error('加载成绩失败:', error)
    ElMessage.error('加载成绩数据失败')
    grades.value = []
  } finally {
    loading.value.grades = false
  }
}

const loadGradeStats = async () => {
  try {
    const { data } = await gradeApi.getGradeStats()
    gradeStats.value = [
      {
        type: 'gpa',
        icon: 'TrendCharts',
        color: '#409eff',
        label: '平均GPA',
        value: data.averageGPA,
        description: `较上学期${data.gpaChange >= 0 ? '上升' : '下降'} ${Math.abs(data.gpaChange)}`
      },
      {
        type: 'credits',
        icon: 'Star',
        color: '#67c23a',
        label: '已获学分',
        value: data.totalCredits,
        description: `共计 ${data.totalCourses} 门课程`
      },
      {
        type: 'rank',
        icon: 'Trophy',
        color: '#e6a23c',
        label: '年级排名',
        value: data.gradeRank,
        description: `共 ${data.totalStudents} 人`
      },
      {
        type: 'pass',
        icon: 'CircleCheck',
        color: '#f56c6c',
        label: '通过率',
        value: `${data.passRate}%`,
        description: `${data.passedCourses}/${data.totalCourses} 门通过`
      }
    ]
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 设置默认统计数据
    gradeStats.value = [
      { type: 'gpa', icon: 'TrendCharts', color: '#409eff', label: '平均GPA', value: '0.0', description: '暂无数据' },
      { type: 'credits', icon: 'Star', color: '#67c23a', label: '已获学分', value: '0', description: '暂无数据' },
      { type: 'rank', icon: 'Trophy', color: '#e6a23c', label: '年级排名', value: '-', description: '暂无数据' },
      { type: 'pass', icon: 'CircleCheck', color: '#f56c6c', label: '通过率', value: '0%', description: '暂无数据' }
    ]
  }
}

const loadSemesters = async () => {
  try {
    const { data } = await studentApi.getSemesters()
    semesters.value = data
  } catch (error) {
    console.error('加载学期数据失败:', error)
    semesters.value = [
      { label: '2024年春季', value: '2024-spring' },
      { label: '2024年秋季', value: '2024-autumn' }
    ]
  }
}

const loadTrendData = async () => {
  loading.value.trend = true
  
  try {
    const { data } = await gradeApi.getGradeTrend({
      period: trendPeriod.value
    })
    trendData.value = data
    renderTrendChart()
  } catch (error) {
    console.error('加载趋势数据失败:', error)
    trendData.value = []
  } finally {
    loading.value.trend = false
  }
}

const renderTrendChart = () => {
  if (!trendChartRef.value || !trendData.value.length) return
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  const option = {
    title: {
      text: 'GPA变化趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>GPA: {c}'
    },
    xAxis: {
      type: 'category',
      data: trendData.value.map(item => item.period),
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 4,
      axisLine: {
        lineStyle: {
          color: '#e4e7ed'
        }
      },
      axisLabel: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#f5f5f5'
        }
      }
    },
    series: [{
      data: trendData.value.map(item => item.gpa),
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409eff',
        width: 3
      },
      itemStyle: {
        color: '#409eff',
        borderWidth: 2
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }
  
  trendChart.setOption(option)
}

const renderCharts = () => {
  if (viewMode.value !== 'chart') return
  
  nextTick(() => {
    renderDistributionChart()
    renderRadarChart()
    renderSemesterChart()
  })
}

const renderDistributionChart = () => {
  if (!distributionChartRef.value) return
  
  if (distributionChart) {
    distributionChart.dispose()
  }
  
  distributionChart = echarts.init(distributionChartRef.value)
  
  const gradeDistribution = grades.value.reduce((acc, grade) => {
    acc[grade.grade] = (acc[grade.grade] || 0) + 1
    return acc
  }, {})
  
  const data = Object.entries(gradeDistribution).map(([grade, count]) => ({
    name: grade,
    value: count
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '成绩分布',
      type: 'pie',
      radius: '50%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  distributionChart.setOption(option)
}

const renderRadarChart = () => {
  if (!radarChartRef.value) return
  
  if (radarChart) {
    radarChart.dispose()
  }
  
  radarChart = echarts.init(radarChartRef.value)
  
  // 这里需要根据实际的课程数据来构建雷达图
  const courseData = grades.value.slice(0, 6) // 取前6门课程
  
  const option = {
    tooltip: {},
    radar: {
      indicator: courseData.map(grade => ({
        name: grade.courseName,
        max: 100
      }))
    },
    series: [{
      name: '各科成绩',
      type: 'radar',
      data: [{
        value: courseData.map(grade => grade.score),
        name: '我的成绩'
      }]
    }]
  }
  
  radarChart.setOption(option)
}

const renderSemesterChart = () => {
  if (!semesterChartRef.value) return
  
  if (semesterChart) {
    semesterChart.dispose()
  }
  
  semesterChart = echarts.init(semesterChartRef.value)
  
  // 按学期分组统计平均分
  const semesterStats = grades.value.reduce((acc, grade) => {
    if (!acc[grade.semester]) {
      acc[grade.semester] = { total: 0, count: 0 }
    }
    acc[grade.semester].total += grade.score
    acc[grade.semester].count += 1
    return acc
  }, {})
  
  const semesterData = Object.entries(semesterStats).map(([semester, stats]) => ({
    semester,
    average: (stats.total / stats.count).toFixed(1)
  }))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>平均分: {c}'
    },
    xAxis: {
      type: 'category',
      data: semesterData.map(item => item.semester)
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [{
      data: semesterData.map(item => item.average),
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
          offset: 0,
          color: '#409eff'
        }, {
          offset: 1,
          color: '#67c23a'
        }])
      }
    }]
  }
  
  semesterChart.setOption(option)
}

// 辅助方法
const getExamTypeTag = (type) => {
  const typeMap = {
    'midterm': 'warning',
    'final': 'primary',
    'homework': 'success',
    'lab': 'info'
  }
  return typeMap[type] || 'info'
}

const getExamTypeName = (type) => {
  const typeMap = {
    'midterm': '期中考试',
    'final': '期末考试',
    'homework': '平时作业',
    'lab': '实验成绩'
  }
  return typeMap[type] || type
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-fair'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const getGradeType = (grade) => {
  const gradeMap = {
    'A+': 'success',
    'A': 'success',
    'A-': 'success',
    'B+': 'primary',
    'B': 'primary',
    'B-': 'primary',
    'C+': 'warning',
    'C': 'warning',
    'C-': 'warning',
    'D': 'danger',
    'F': 'danger'
  }
  return gradeMap[grade] || 'info'
}

const getStatusType = (status) => {
  const statusMap = {
    'published': 'success',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusName = (status) => {
  const statusMap = {
    'published': '已发布',
    'pending': '待发布'
  }
  return statusMap[status] || status
}

const getRowClassName = ({ row }) => {
  if (row.status === 'pending') return 'pending-row'
  if (row.score >= 90) return 'excellent-row'
  if (row.score < 60) return 'fail-row'
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
  loadGrades()
}

const refreshGrades = async () => {
  await loadGrades()
  ElMessage.success('数据刷新成功')
}

const exportGrades = () => {
  ElMessage.info('导出功能开发中...')
}

const generateReport = () => {
  ElMessage.info('报告生成功能开发中...')
}

const viewGradeDetail = async (grade) => {
  try {
    const { data } = await gradeApi.getGradeDetail(grade.id)
    selectedGrade.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载成绩详情失败:', error)
    ElMessage.error('加载成绩详情失败')
  }
}

const viewAnalysis = (grade) => {
  ElMessage.info('成绩分析功能开发中...')
}

const downloadCertificate = (grade) => {
  ElMessage.info('成绩单下载功能开发中...')
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadGrades()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadGrades()
}

// 监听器
watch(viewMode, (newMode) => {
  if (newMode === 'chart') {
    renderCharts()
  }
})

watch(trendPeriod, () => {
  loadTrendData()
})

// 生命周期
onMounted(async () => {
  await nextTick()
  await Promise.all([
    loadGrades(),
    loadSemesters(),
    loadTrendData()
  ])
  
  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (trendChart) trendChart.resize()
    if (distributionChart) distributionChart.resize()
    if (radarChart) radarChart.resize()
    if (semesterChart) semesterChart.resize()
  })
})
</script>

<style scoped>
@import '@/styles/student.css';
</style>