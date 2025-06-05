<template>
  <div class="parent-grades">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>成绩查看</h1>
          <p>查看子女的学习成绩和学业进展</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="generateReport">
            <el-icon><Document /></el-icon>
            生成报告
          </el-button>
          <el-button @click="exportGrades">
            <el-icon><Download /></el-icon>
            导出成绩
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 子女选择器 -->
    <div class="child-selector-section" v-if="children.length > 1">
      <el-card shadow="hover">
        <template #header>
          <h3>选择子女</h3>
        </template>
        <div class="child-tabs">
          <div
            v-for="child in children"
            :key="child.id"
            class="child-tab"
            :class="{ active: currentChild?.id === child.id }"
            @click="switchChild(child)"
          >
            <el-avatar :size="40" :src="child.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <div class="child-info">
              <span class="name">{{ child.name }}</span>
              <span class="class">{{ child.className }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 成绩概览 -->
    <div class="grades-overview-section" v-if="currentChild">
      <el-row :gutter="20">
        <el-col :xs="24" :lg="8">
          <el-card class="overview-card gpa-card" shadow="hover">
            <div class="overview-content">
              <div class="overview-icon">
                <el-icon :size="32" color="#409eff"><TrendCharts /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ gradeOverview.gpa }}</div>
                <div class="overview-label">平均GPA</div>
                <div class="overview-trend" v-if="gradeOverview.gpaTrend">
                  <el-icon :color="gradeOverview.gpaTrend > 0 ? '#67c23a' : '#f56c6c'">
                    <component :is="gradeOverview.gpaTrend > 0 ? 'ArrowUp' : 'ArrowDown'" />
                  </el-icon>
                  <span :class="gradeOverview.gpaTrend > 0 ? 'trend-up' : 'trend-down'">
                    {{ Math.abs(gradeOverview.gpaTrend) }}%
                  </span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :lg="8">
          <el-card class="overview-card rank-card" shadow="hover">
            <div class="overview-content">
              <div class="overview-icon">
                <el-icon :size="32" color="#e6a23c"><Trophy /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ gradeOverview.classRank }}</div>
                <div class="overview-label">班级排名</div>
                <div class="overview-description">
                  共 {{ gradeOverview.totalStudents }} 人
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :xs="24" :lg="8">
          <el-card class="overview-card progress-card" shadow="hover">
            <div class="overview-content">
              <div class="overview-icon">
                <el-icon :size="32" color="#67c23a"><CircleCheck /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ gradeOverview.passRate }}%</div>
                <div class="overview-label">通过率</div>
                <div class="overview-description">
                  {{ gradeOverview.passedCourses }}/{{ gradeOverview.totalCourses }} 门通过
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content-section" v-if="currentChild">
      <el-row :gutter="20">
        <!-- 成绩趋势图 -->
        <el-col :xs="24" :lg="16">
          <el-card class="trend-chart-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <div class="header-title">
                  <el-icon color="#409eff"><TrendCharts /></el-icon>
                  <h3>成绩趋势</h3>
                </div>
                <el-select v-model="trendPeriod" size="small" @change="loadGradeTrend">
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
        </el-col>
        
        <!-- 科目成绩分布 -->
        <el-col :xs="24" :lg="8">
          <el-card class="distribution-card" shadow="hover">
            <template #header>
              <div class="header-title">
                <el-icon color="#67c23a"><PieChart /></el-icon>
                <h3>科目分布</h3>
              </div>
            </template>
            
            <div v-loading="loading.distribution" class="chart-container">
              <div ref="distributionChartRef" class="distribution-chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section" v-if="currentChild">
      <el-card shadow="never">
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
            <el-select v-model="filterExamType" placeholder="考试类型" clearable @change="handleFilter">
              <el-option label="期中考试" value="midterm" />
              <el-option label="期末考试" value="final" />
              <el-option label="平时作业" value="homework" />
              <el-option label="实验成绩" value="lab" />
            </el-select>
          </el-col>
          <el-col :xs="12" :sm="6" :md="4">
            <el-select v-model="filterGradeRange" placeholder="成绩范围" clearable @change="handleFilter">
              <el-option label="优秀 (90-100)" value="excellent" />
              <el-option label="良好 (80-89)" value="good" />
              <el-option label="中等 (70-79)" value="fair" />
              <el-option label="及格 (60-69)" value="pass" />
              <el-option label="不及格 (<60)" value="fail" />
            </el-select>
          </el-col>
          <el-col :xs="12" :sm="12" :md="6">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="table">
                <el-icon><List /></el-icon>
                表格
              </el-radio-button>
              <el-radio-button label="chart">
                <el-icon><PieChart /></el-icon>
                图表
              </el-radio-button>
            </el-radio-group>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 成绩详情 -->
    <div class="grades-detail-section" v-if="currentChild">
      <el-card class="grades-content" shadow="hover">
        <template #header>
          <div class="content-header">
            <h3>{{ currentChild.name }} - 成绩详情</h3>
            <div class="header-stats">
              <el-tag type="info">共 {{ filteredGrades.length }} 条记录</el-tag>
            </div>
          </div>
        </template>

        <div v-loading="loading.grades">
          <!-- 表格视图 -->
          <div v-if="viewMode === 'table'" class="grades-table">
            <el-table 
              :data="paginatedGrades" 
              style="width: 100%"
              :row-class-name="getRowClassName"
              @row-click="viewGradeDetail"
            >
              <el-table-column label="课程信息" min-width="150" fixed="left">
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

              <el-table-column prop="examDate" label="考试时间" width="120" align="center">
                <template #default="{ row }">
                  {{ formatDate(row.examDate) }}
                </template>
              </el-table-column>

              <el-table-column prop="teacherName" label="任课教师" width="100" align="center" />

              <el-table-column label="操作" width="100" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" @click.stop="viewGradeDetail(row)">
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 图表视图 -->
          <div v-else class="grades-charts">
            <el-row :gutter="20">
              <!-- 各科成绩雷达图 -->
              <el-col :xs="24" :lg="12">
                <div class="chart-container">
                  <h4>各科成绩对比</h4>
                  <div ref="radarChartRef" class="chart"></div>
                </div>
              </el-col>
              
              <!-- 学期成绩对比柱状图 -->
              <el-col :xs="24" :lg="12">
                <div class="chart-container">
                  <h4>学期成绩对比</h4>
                  <div ref="semesterChartRef" class="chart"></div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 空状态 -->
          <el-empty v-if="!loading.grades && filteredGrades.length === 0" description="暂无成绩记录">
            <el-button type="primary" @click="refreshData">刷新数据</el-button>
          </el-empty>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="filteredGrades.length > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[20, 50, 100]"
            :total="filteredGrades.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

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
            {{ formatDate(selectedGrade.examDate) }}
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
          <el-descriptions-item label="任课教师">
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
              <span class="label">年级平均分：</span>
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
          <el-button type="primary" @click="contactTeacher(selectedGrade)">
            联系老师
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 空状态 -->
    <div v-if="!currentChild && children.length === 0" class="empty-state">
      <el-empty description="您还没有添加子女信息">
        <el-button type="primary" @click="goToChildren">添加子女</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Document,
  Download,
  Refresh,
  User,
  TrendCharts,
  Trophy,
  CircleCheck,
  PieChart,
  Search,
  List,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'
import * as echarts from 'echarts'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref({
  grades: false,
  trend: false,
  distribution: false
})

const children = ref([])
const currentChild = ref(null)
const grades = ref([])
const gradeOverview = ref({})
const semesters = ref([])

const searchQuery = ref('')
const filterSemester = ref('')
const filterExamType = ref('')
const filterGradeRange = ref('')
const viewMode = ref('table')
const trendPeriod = ref('semester')
const currentPage = ref(1)
const pageSize = ref(20)

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

  // 考试类型过滤
  if (filterExamType.value) {
    result = result.filter(grade => grade.examType === filterExamType.value)
  }

  // 成绩范围过滤
  if (filterGradeRange.value) {
    result = result.filter(grade => {
      const score = grade.score
      switch (filterGradeRange.value) {
        case 'excellent': return score >= 90
        case 'good': return score >= 80 && score < 90
        case 'fair': return score >= 70 && score < 80
        case 'pass': return score >= 60 && score < 70
        case 'fail': return score < 60
        default: return true
      }
    })
  }

  return result
})

const paginatedGrades = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredGrades.value.slice(start, end)
})

// 方法
const loadChildren = async () => {
  try {
    const { data } = await parentApi.getChildren()
    children.value = data
    
    // 从路由参数中获取子女ID，或默认选择第一个
    const childId = route.query.childId
    if (childId) {
      currentChild.value = data.find(child => child.id === childId) || data[0]
    } else if (data.length > 0) {
      currentChild.value = data[0]
    }
  } catch (error) {
    console.error('加载子女信息失败:', error)
    children.value = []
  }
}

const loadGrades = async () => {
  if (!currentChild.value) return
  
  loading.value.grades = true
  
  try {
    const { data } = await parentApi.getChildGrades(currentChild.value.id)
    grades.value = data
    
    await loadGradeOverview()
  } catch (error) {
    console.error('加载成绩失败:', error)
    ElMessage.error('加载成绩数据失败')
    grades.value = []
  } finally {
    loading.value.grades = false
  }
}

const loadGradeOverview = async () => {
  if (!currentChild.value) return
  
  try {
    const { data } = await parentApi.getChildGradeStats(currentChild.value.id)
    gradeOverview.value = data
  } catch (error) {
    console.error('加载成绩概览失败:', error)
  }
}

const loadGradeTrend = async () => {
  if (!currentChild.value) return
  
  loading.value.trend = true
  
  try {
    const { data } = await parentApi.getChildGradeTrend(currentChild.value.id, {
      period: trendPeriod.value
    })
    renderTrendChart(data)
  } catch (error) {
    console.error('加载成绩趋势失败:', error)
  } finally {
    loading.value.trend = false
  }
}

const loadSemesters = async () => {
  try {
    const { data } = await parentApi.getSemesters()
    semesters.value = data
  } catch (error) {
    console.error('加载学期数据失败:', error)
    semesters.value = [
      { label: '2024年春季', value: '2024-spring' },
      { label: '2024年秋季', value: '2024-autumn' }
    ]
  }
}

const switchChild = async (child) => {
  currentChild.value = child
  currentPage.value = 1
  await loadGrades()
  await loadGradeTrend()
  renderCharts()
}

const renderTrendChart = (trendData) => {
  if (!trendChartRef.value || !trendData?.length) return
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>平均分: {c}'
    },
    xAxis: {
      type: 'category',
      data: trendData.map(item => item.period),
      axisLabel: {
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        color: '#666'
      }
    },
    series: [{
      data: trendData.map(item => item.average),
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409eff',
        width: 3
      },
      itemStyle: {
        color: '#409eff'
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
    }]
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
    series: [{
      name: '成绩分布',
      type: 'pie',
      radius: '70%',
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
  
  // 取最近的几门课程
  const courseData = grades.value.slice(0, 6)
  
  const option = {
    tooltip: {},
    radar: {
      indicator: courseData.map(grade => ({
        name: grade.courseName.substring(0, 4),
        max: 100
      }))
    },
    series: [{
      name: '各科成绩',
      type: 'radar',
      data: [{
        value: courseData.map(grade => grade.score),
        name: currentChild.value?.name
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

const viewGradeDetail = async (grade) => {
  try {
    const { data } = await parentApi.getGradeDetail(grade.id)
    selectedGrade.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载成绩详情失败:', error)
    ElMessage.error('加载成绩详情失败')
  }
}

const contactTeacher = (grade) => {
  router.push({
    path: '/parent/communication',
    query: { 
      childId: currentChild.value.id,
      teacherId: grade.teacherId,
      subject: `关于${grade.courseName}成绩的咨询`
    }
  })
}

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

const refreshData = async () => {
  await Promise.all([
    loadGrades(),
    loadGradeTrend()
  ])
  ElMessage.success('数据刷新成功')
}

const generateReport = () => {
  ElMessage.info('生成报告功能开发中...')
}

const exportGrades = () => {
  ElMessage.info('导出成绩功能开发中...')
}

const goToChildren = () => {
  router.push('/parent/children')
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
    'midterm': '期中',
    'final': '期末',
    'homework': '作业',
    'lab': '实验'
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

const getRowClassName = ({ row }) => {
  if (row.score >= 90) return 'excellent-row'
  if (row.score < 60) return 'fail-row'
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 监听器
watch(viewMode, (newMode) => {
  if (newMode === 'chart') {
    renderCharts()
  }
})

// 生命周期
onMounted(async () => {
  await nextTick()
  await Promise.all([
    loadChildren(),
    loadSemesters()
  ])
  
  if (currentChild.value) {
    await Promise.all([
      loadGrades(),
      loadGradeTrend()
    ])
  }
  
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
.parent-grades {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  color: white;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 8px;
}

.title-section p {
  font-size: 16px;
  opacity: 0.9;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 子女选择器 */
.child-selector-section {
  max-width: 1200px;
  margin: -20px auto 20px;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.child-tabs {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.child-tab {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
  border: 2px solid transparent;
}

.child-tab:hover {
  background: #e3f2fd;
  border-color: #409eff;
}

.child-tab.active {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-color: #409eff;
}

.child-info .name {
  display: block;
  font-weight: 600;
  margin-bottom: 4px;
}

.child-info .class {
  font-size: 12px;
  opacity: 0.8;
}

/* 成绩概览 */
.grades-overview-section {
  max-width: 1200px;
  margin: 0 auto 30px;
  padding: 0 20px;
}

.overview-card {
  height: 120px;
  border-radius: 12px;
  overflow: hidden;
}

.overview-content {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 16px;
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gpa-card .overview-icon {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
}

.rank-card .overview-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #f56c6c 100%);
}

.progress-card .overview-icon {
  background: linear-gradient(135deg, #67c23a 0%, #409eff 100%);
}

.overview-icon .el-icon {
  color: white;
}

.overview-info {
  flex: 1;
}

.overview-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.overview-label {
  color: #666;
  font-size: 16px;
  margin-bottom: 4px;
}

.overview-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.overview-description {
  color: #999;
  font-size: 12px;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

/* 主要内容区域 */
.main-content-section {
  max-width: 1200px;
  margin: 0 auto 20px;
  padding: 0 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.chart-container {
  padding: 20px 0;
}

.trend-chart {
  height: 300px;
}

.distribution-chart {
  height: 300px;
}

/* 筛选区域 */
.filter-section {
  max-width: 1200px;
  margin: 0 auto 20px;
  padding: 0 20px;
}

/* 成绩详情 */
.grades-detail-section {
  max-width: 1200px;
  margin: 0 auto 30px;
  padding: 0 20px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-header h3 {
  margin: 0;
  color: #333;
}

.header-stats {
  display: flex;
  gap: 12px;
}

/* 表格样式 */
.grades-table {
  margin-top: 20px;
}

.course-info .course-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.course-info .course-code {
  font-size: 12px;
  color: #666;
}

.score-excellent {
  color: #67c23a;
  font-weight: 600;
}

.score-good {
  color: #409eff;
  font-weight: 600;
}

.score-fair {
  color: #e6a23c;
  font-weight: 600;
}

.score-pass {
  color: #909399;
  font-weight: 600;
}

.score-fail {
  color: #f56c6c;
  font-weight: 600;
}

.gpa-value {
  font-weight: 600;
  color: #409eff;
}

.rank-info {
  font-size: 12px;
  color: #666;
}

.text-muted {
  color: #999;
}

/* 表格行样式 */
:deep(.excellent-row) {
  background-color: #f0f9ff;
}

:deep(.fail-row) {
  background-color: #fef0f0;
}

/* 图表视图 */
.grades-charts {
  margin-top: 20px;
}

.chart {
  height: 300px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}

/* 详情对话框 */
.grade-detail {
  max-height: 600px;
  overflow-y: auto;
}

.grade-analysis {
  margin-top: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.grade-analysis h4 {
  margin: 0 0 16px 0;
  color: #333;
  font-weight: 600;
}

.analysis-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.analysis-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background: white;
  border-radius: 4px;
}

.analysis-item .label {
  color: #666;
}

.analysis-item .value {
  color: #333;
  font-weight: 600;
}

.teacher-comment {
  margin-top: 24px;
  padding: 16px;
  background: #f0f9ff;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.teacher-comment h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-weight: 600;
}

.comment-content {
  color: #666;
  line-height: 1.6;
}

.rank-display {
  font-weight: 600;
  color: #e6a23c;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 空状态 */
.empty-state {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 15px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .title-section h1 {
    font-size: 24px;
  }
  
  .child-selector-section,
  .grades-overview-section,
  .main-content-section,
  .filter-section,
  .grades-detail-section {
    margin-left: 15px;
    margin-right: 15px;
  }
  
  .child-tabs {
    flex-direction: column;
  }
  
  .overview-value {
    font-size: 24px;
  }
  
  .chart {
    height: 250px;
  }
  
  .analysis-content {
    grid-template-columns: 1fr;
  }
}
</style>