<template>
  <div class="parent-attendance">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>出勤记录</h1>
          <p>查看子女的出勤情况和考勤统计</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="applyLeave">
            <el-icon><Document /></el-icon>
            申请请假
          </el-button>
          <el-button @click="exportAttendance">
            <el-icon><Download /></el-icon>
            导出记录
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

    <!-- 出勤统计概览 -->
    <div class="attendance-overview-section" v-if="currentChild">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6">
          <div class="stat-card present-card">
            <div class="stat-icon">
              <el-icon :size="28" color="#67c23a"><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.presentDays }}</div>
              <div class="stat-label">出勤天数</div>
              <div class="stat-rate">{{ attendanceStats.presentRate }}%</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="6">
          <div class="stat-card late-card">
            <div class="stat-icon">
              <el-icon :size="28" color="#e6a23c"><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.lateDays }}</div>
              <div class="stat-label">迟到次数</div>
              <div class="stat-rate">{{ attendanceStats.lateRate }}%</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="6">
          <div class="stat-card absent-card">
            <div class="stat-icon">
              <el-icon :size="28" color="#f56c6c"><CircleClose /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.absentDays }}</div>
              <div class="stat-label">缺勤天数</div>
              <div class="stat-rate">{{ attendanceStats.absentRate }}%</div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="12" :sm="6">
          <div class="stat-card leave-card">
            <div class="stat-icon">
              <el-icon :size="28" color="#409eff"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ attendanceStats.leaveDays }}</div>
              <div class="stat-label">请假天数</div>
              <div class="stat-rate">{{ attendanceStats.leaveRate }}%</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和操作区域 -->
    <div class="filter-section" v-if="currentChild">
      <el-card shadow="never">
        <el-row :gutter="20" align="middle">
          <el-col :xs="24" :sm="8" :md="6">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              @change="handleDateRangeChange"
            />
          </el-col>
          <el-col :xs="12" :sm="6" :md="4">
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="handleFilter">
              <el-option label="全部" value="" />
              <el-option label="正常出勤" value="present" />
              <el-option label="迟到" value="late" />
              <el-option label="早退" value="early" />
              <el-option label="缺勤" value="absent" />
              <el-option label="请假" value="leave" />
            </el-select>
          </el-col>
          <el-col :xs="12" :sm="6" :md="4">
            <el-select v-model="viewMode" @change="switchViewMode">
              <el-option label="日历视图" value="calendar" />
              <el-option label="列表视图" value="list" />
              <el-option label="统计视图" value="chart" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="4" :md="3">
            <el-button-group>
              <el-button @click="goToToday" :type="isToday ? 'primary' : 'default'">今天</el-button>
              <el-button @click="goToPrevious">
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
              <el-button @click="goToNext">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </el-button-group>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content-section" v-if="currentChild">
      <!-- 日历视图 -->
      <div v-if="viewMode === 'calendar'" class="calendar-view">
        <el-card shadow="hover">
          <template #header>
            <div class="calendar-header">
              <h3>{{ currentMonth }}月出勤日历</h3>
              <div class="legend">
                <div class="legend-item">
                  <div class="legend-color present"></div>
                  <span>正常</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color late"></div>
                  <span>迟到</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color absent"></div>
                  <span>缺勤</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color leave"></div>
                  <span>请假</span>
                </div>
              </div>
            </div>
          </template>
          
          <div v-loading="loading.calendar" class="calendar-content">
            <el-calendar v-model="calendarDate" @select="handleDateSelect">
              <template #date-cell="{ data }">
                <div class="calendar-day" :class="getCalendarDayClass(data.day)">
                  <div class="day-number">{{ data.day.split('-').pop() }}</div>
                  <div class="day-status" v-if="getAttendanceStatus(data.day)">
                    <el-icon :size="16" :color="getStatusColor(getAttendanceStatus(data.day))">
                      <component :is="getStatusIcon(getAttendanceStatus(data.day))" />
                    </el-icon>
                  </div>
                </div>
              </template>
            </el-calendar>
          </div>
        </el-card>
      </div>

      <!-- 列表视图 -->
      <div v-else-if="viewMode === 'list'" class="list-view">
        <el-card shadow="hover">
          <template #header>
            <div class="list-header">
              <h3>出勤记录列表</h3>
              <el-tag type="info">共 {{ filteredAttendance.length }} 条记录</el-tag>
            </div>
          </template>
          
          <div v-loading="loading.list" class="attendance-table">
            <el-table 
              :data="paginatedAttendance" 
              style="width: 100%"
              :row-class-name="getRowClassName"
            >
              <el-table-column label="日期" width="120" align="center">
                <template #default="{ row }">
                  <div class="date-cell">
                    <div class="date-main">{{ formatDate(row.date) }}</div>
                    <div class="date-weekday">{{ getWeekday(row.date) }}</div>
                  </div>
                </template>
              </el-table-column>

              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.status)" size="small">
                    <el-icon style="margin-right: 4px;">
                      <component :is="getStatusIcon(row.status)" />
                    </el-icon>
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>

              <el-table-column label="到校时间" width="120" align="center">
                <template #default="{ row }">
                  <span v-if="row.arrivalTime" :class="getLateClass(row.arrivalTime, row.status)">
                    {{ row.arrivalTime }}
                  </span>
                  <span v-else class="text-muted">-</span>
                </template>
              </el-table-column>

              <el-table-column label="离校时间" width="120" align="center">
                <template #default="{ row }">
                  <span v-if="row.departureTime" :class="getEarlyClass(row.departureTime, row.status)">
                    {{ row.departureTime }}
                  </span>
                  <span v-else class="text-muted">-</span>
                </template>
              </el-table-column>

              <el-table-column label="课程" min-width="150">
                <template #default="{ row }">
                  <div v-if="row.courses?.length" class="courses-list">
                    <el-tag 
                      v-for="course in row.courses" 
                      :key="course.id"
                      size="small" 
                      class="course-tag"
                    >
                      {{ course.name }}
                    </el-tag>
                  </div>
                  <span v-else class="text-muted">无课程</span>
                </template>
              </el-table-column>

              <el-table-column label="备注" min-width="200">
                <template #default="{ row }">
                  <div v-if="row.remark" class="remark-cell">
                    {{ row.remark }}
                  </div>
                  <div v-if="row.leaveReason" class="leave-reason">
                    <el-icon color="#409eff"><Document /></el-icon>
                    {{ row.leaveReason }}
                  </div>
                  <span v-if="!row.remark && !row.leaveReason" class="text-muted">-</span>
                </template>
              </el-table-column>

              <el-table-column label="记录时间" width="140" align="center">
                <template #default="{ row }">
                  {{ formatDateTime(row.recordTime) }}
                </template>
              </el-table-column>

              <el-table-column label="操作" width="100" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button 
                    size="small" 
                    @click="viewDetail(row)"
                  >
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 空状态 -->
            <el-empty v-if="!loading.list && filteredAttendance.length === 0" description="暂无出勤记录">
              <el-button type="primary" @click="refreshData">刷新数据</el-button>
            </el-empty>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="filteredAttendance.length > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[20, 50, 100]"
              :total="filteredAttendance.length"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </div>

      <!-- 统计视图 -->
      <div v-else-if="viewMode === 'chart'" class="chart-view">
        <el-row :gutter="20">
          <!-- 月度出勤趋势 -->
          <el-col :xs="24" :lg="12">
            <el-card shadow="hover">
              <template #header>
                <h3>月度出勤趋势</h3>
              </template>
              <div v-loading="loading.chart" class="chart-container">
                <div ref="trendChartRef" class="trend-chart"></div>
              </div>
            </el-card>
          </el-col>
          
          <!-- 出勤状态分布 -->
          <el-col :xs="24" :lg="12">
            <el-card shadow="hover">
              <template #header>
                <h3>出勤状态分布</h3>
              </template>
              <div v-loading="loading.chart" class="chart-container">
                <div ref="pieChartRef" class="pie-chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 周出勤对比 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <el-card shadow="hover">
              <template #header>
                <h3>周出勤对比</h3>
              </template>
              <div v-loading="loading.chart" class="chart-container">
                <div ref="weekChartRef" class="week-chart"></div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 出勤详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${formatDate(selectedRecord?.date)} - 出勤详情`"
      width="600px"
    >
      <div v-if="selectedRecord" class="attendance-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日期">
            {{ formatDate(selectedRecord.date) }} {{ getWeekday(selectedRecord.date) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(selectedRecord.status)" size="large">
              <el-icon style="margin-right: 4px;">
                <component :is="getStatusIcon(selectedRecord.status)" />
              </el-icon>
              {{ getStatusText(selectedRecord.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="到校时间">
            <span v-if="selectedRecord.arrivalTime" :class="getLateClass(selectedRecord.arrivalTime, selectedRecord.status)">
              {{ selectedRecord.arrivalTime }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="离校时间">
            <span v-if="selectedRecord.departureTime" :class="getEarlyClass(selectedRecord.departureTime, selectedRecord.status)">
              {{ selectedRecord.departureTime }}
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="当日课程" :span="2">
            <div v-if="selectedRecord.courses?.length" class="courses-detail">
              <el-tag 
                v-for="course in selectedRecord.courses" 
                :key="course.id"
                class="course-tag"
                style="margin-right: 8px; margin-bottom: 4px;"
              >
                {{ course.name }} ({{ course.time }})
              </el-tag>
            </div>
            <span v-else>无课程安排</span>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ selectedRecord.remark || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="请假原因" :span="2" v-if="selectedRecord.leaveReason">
            <div class="leave-reason-detail">
              <el-icon color="#409eff"><Document /></el-icon>
              {{ selectedRecord.leaveReason }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="记录时间">
            {{ formatDateTime(selectedRecord.recordTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="记录人">
            {{ selectedRecord.recorder || '系统自动' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="selectedRecord?.status === 'absent'" 
            type="warning" 
            @click="applyMakeupLeave(selectedRecord)"
          >
            补申请假条
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 申请请假对话框 -->
    <el-dialog
      v-model="leaveDialogVisible"
      title="申请请假"
      width="500px"
    >
      <el-form
        ref="leaveFormRef"
        :model="leaveForm"
        :rules="leaveRules"
        label-width="80px"
      >
        <el-form-item label="请假日期" prop="dateRange">
          <el-date-picker
            v-model="leaveForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="请假类型" prop="type">
          <el-select v-model="leaveForm.type" placeholder="请选择请假类型">
            <el-option label="病假" value="sick" />
            <el-option label="事假" value="personal" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="请假原因" prop="reason">
          <el-input
            v-model="leaveForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明请假原因"
          />
        </el-form-item>
        
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="leaveForm.phone" placeholder="紧急联系电话" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="leaveDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="submitLoading"
            @click="submitLeaveApplication"
          >
            提交申请
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Download,
  Refresh,
  User,
  CircleCheck,
  Clock,
  CircleClose,
  ArrowLeft,
  ArrowRight
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'
import * as echarts from 'echarts'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref({
  calendar: false,
  list: false,
  chart: false
})

const children = ref([])
const currentChild = ref(null)
const attendanceRecords = ref([])
const attendanceStats = ref({
  presentDays: 0,
  lateDays: 0,
  absentDays: 0,
  leaveDays: 0,
  presentRate: 0,
  lateRate: 0,
  absentRate: 0,
  leaveRate: 0
})

const dateRange = ref([])
const filterStatus = ref('')
const viewMode = ref('calendar')
const calendarDate = ref(new Date())
const currentMonth = ref(new Date().getMonth() + 1)
const isToday = ref(true)
const currentPage = ref(1)
const pageSize = ref(20)
const submitLoading = ref(false)

const detailDialogVisible = ref(false)
const leaveDialogVisible = ref(false)
const selectedRecord = ref(null)

// 表单数据
const leaveFormRef = ref()
const leaveForm = ref({
  dateRange: [],
  type: '',
  reason: '',
  phone: ''
})

// 图表引用
const trendChartRef = ref()
const pieChartRef = ref()
const weekChartRef = ref()

// 图表实例
let trendChart = null
let pieChart = null
let weekChart = null

// 验证规则
const leaveRules = {
  dateRange: [
    { required: true, message: '请选择请假日期', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择请假类型', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入请假原因', trigger: 'blur' },
    { min: 10, message: '请假原因至少10个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 计算属性
const filteredAttendance = computed(() => {
  let result = attendanceRecords.value

  // 日期范围过滤
  if (dateRange.value?.length === 2) {
    const [startDate, endDate] = dateRange.value
    result = result.filter(record => {
      const recordDate = new Date(record.date)
      return recordDate >= startDate && recordDate <= endDate
    })
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter(record => record.status === filterStatus.value)
  }

  return result
})

const paginatedAttendance = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredAttendance.value.slice(start, end)
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

const loadAttendanceData = async () => {
  if (!currentChild.value) return
  
  const loadingKey = viewMode.value === 'calendar' ? 'calendar' : 
                     viewMode.value === 'list' ? 'list' : 'chart'
  loading.value[loadingKey] = true
  
  try {
    const { data } = await parentApi.getChildAttendance(currentChild.value.id, {
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    
    attendanceRecords.value = data.records
    attendanceStats.value = data.stats
    
    if (viewMode.value === 'chart') {
      await nextTick()
      renderCharts()
    }
  } catch (error) {
    console.error('加载出勤数据失败:', error)
    ElMessage.error('加载出勤数据失败')
    attendanceRecords.value = []
  } finally {
    loading.value[loadingKey] = false
  }
}

const switchChild = async (child) => {
  currentChild.value = child
  currentPage.value = 1
  await loadAttendanceData()
}

const switchViewMode = async (mode) => {
  viewMode.value = mode
  await loadAttendanceData()
}

const handleDateRangeChange = () => {
  currentPage.value = 1
  loadAttendanceData()
}

const handleFilter = () => {
  currentPage.value = 1
}

const handleDateSelect = (date) => {
  const selectedDate = new Date(date).toISOString().split('T')[0]
  const record = attendanceRecords.value.find(r => r.date === selectedDate)
  if (record) {
    viewDetail(record)
  }
}

const viewDetail = (record) => {
  selectedRecord.value = record
  detailDialogVisible.value = true
}

const applyLeave = () => {
  leaveDialogVisible.value = true
}

const applyMakeupLeave = (record) => {
  leaveForm.value = {
    dateRange: [new Date(record.date), new Date(record.date)],
    type: '',
    reason: '',
    phone: ''
  }
  detailDialogVisible.value = false
  leaveDialogVisible.value = true
}

const submitLeaveApplication = async () => {
  try {
    await leaveFormRef.value.validate()
    submitLoading.value = true
    
    await parentApi.applyLeave(currentChild.value.id, {
      ...leaveForm.value,
      startDate: leaveForm.value.dateRange[0],
      endDate: leaveForm.value.dateRange[1]
    })
    
    ElMessage.success('请假申请提交成功')
    leaveDialogVisible.value = false
    resetLeaveForm()
    await loadAttendanceData()
  } catch (error) {
    console.error('提交请假申请失败:', error)
    ElMessage.error(error.message || '提交请假申请失败')
  } finally {
    submitLoading.value = false
  }
}

const resetLeaveForm = () => {
  Object.assign(leaveForm.value, {
    dateRange: [],
    type: '',
    reason: '',
    phone: ''
  })
  leaveFormRef.value?.clearValidate()
}

const renderCharts = () => {
  renderTrendChart()
  renderPieChart()
  renderWeekChart()
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  // 模拟月度趋势数据
  const monthlyData = [
    { month: '1月', present: 22, absent: 1, late: 2 },
    { month: '2月', present: 18, absent: 0, late: 1 },
    { month: '3月', present: 23, absent: 2, late: 1 },
    { month: '4月', present: 20, absent: 1, late: 3 },
    { month: '5月', present: 21, absent: 0, late: 2 },
    { month: '6月', present: 19, absent: 1, late: 1 }
  ]
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['出勤', '缺勤', '迟到']
    },
    xAxis: {
      type: 'category',
      data: monthlyData.map(item => item.month)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '出勤',
        type: 'line',
        data: monthlyData.map(item => item.present),
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '缺勤',
        type: 'line',
        data: monthlyData.map(item => item.absent),
        itemStyle: { color: '#f56c6c' }
      },
      {
        name: '迟到',
        type: 'line',
        data: monthlyData.map(item => item.late),
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }
  
  trendChart.setOption(option)
}

const renderPieChart = () => {
  if (!pieChartRef.value) return
  
  if (pieChart) {
    pieChart.dispose()
  }
  
  pieChart = echarts.init(pieChartRef.value)
  
  const data = [
    { name: '正常出勤', value: attendanceStats.value.presentDays, itemStyle: { color: '#67c23a' } },
    { name: '迟到', value: attendanceStats.value.lateDays, itemStyle: { color: '#e6a23c' } },
    { name: '缺勤', value: attendanceStats.value.absentDays, itemStyle: { color: '#f56c6c' } },
    { name: '请假', value: attendanceStats.value.leaveDays, itemStyle: { color: '#409eff' } }
  ]
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '出勤状态',
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
  
  pieChart.setOption(option)
}

const renderWeekChart = () => {
  if (!weekChartRef.value) return
  
  if (weekChart) {
    weekChart.dispose()
  }
  
  weekChart = echarts.init(weekChartRef.value)
  
  // 模拟周出勤数据
  const weekData = [
    { day: '周一', present: 4, absent: 0, late: 1 },
    { day: '周二', present: 5, absent: 0, late: 0 },
    { day: '周三', present: 4, absent: 1, late: 0 },
    { day: '周四', present: 5, absent: 0, late: 0 },
    { day: '周五', present: 4, absent: 0, late: 1 }
  ]
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['出勤', '缺勤', '迟到']
    },
    xAxis: {
      type: 'category',
      data: weekData.map(item => item.day)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '出勤',
        type: 'bar',
        data: weekData.map(item => item.present),
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '缺勤',
        type: 'bar',
        data: weekData.map(item => item.absent),
        itemStyle: { color: '#f56c6c' }
      },
      {
        name: '迟到',
        type: 'bar',
        data: weekData.map(item => item.late),
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }
  
  weekChart.setOption(option)
}

// 导航方法
const goToToday = () => {
  calendarDate.value = new Date()
  currentMonth.value = new Date().getMonth() + 1
  isToday.value = true
}

const goToPrevious = () => {
  const current = new Date(calendarDate.value)
  current.setMonth(current.getMonth() - 1)
  calendarDate.value = current
  currentMonth.value = current.getMonth() + 1
  isToday.value = false
}

const goToNext = () => {
  const current = new Date(calendarDate.value)
  current.setMonth(current.getMonth() + 1)
  calendarDate.value = current
  currentMonth.value = current.getMonth() + 1
  isToday.value = false
}

const refreshData = async () => {
  await loadAttendanceData()
  ElMessage.success('数据刷新成功')
}

const exportAttendance = () => {
  ElMessage.info('导出功能开发中...')
}

const goToChildren = () => {
  router.push('/parent/children')
}

// 分页事件处理
const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 辅助方法
const getAttendanceStatus = (date) => {
  const record = attendanceRecords.value.find(r => r.date === date)
  return record?.status
}

const getCalendarDayClass = (date) => {
  const status = getAttendanceStatus(date)
  if (!status) return ''
  return `calendar-day-${status}`
}

const getStatusIcon = (status) => {
  const iconMap = {
    'present': 'CircleCheck',
    'late': 'Clock',
    'early': 'Clock',
    'absent': 'CircleClose',
    'leave': 'Document'
  }
  return iconMap[status] || 'Minus'
}

const getStatusColor = (status) => {
  const colorMap = {
    'present': '#67c23a',
    'late': '#e6a23c',
    'early': '#e6a23c',
    'absent': '#f56c6c',
    'leave': '#409eff'
  }
  return colorMap[status] || '#909399'
}

const getStatusTagType = (status) => {
  const typeMap = {
    'present': 'success',
    'late': 'warning',
    'early': 'warning',
    'absent': 'danger',
    'leave': 'primary'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'present': '正常',
    'late': '迟到',
    'early': '早退',
    'absent': '缺勤',
    'leave': '请假'
  }
  return textMap[status] || status
}

const getRowClassName = ({ row }) => {
  return `attendance-row-${row.status}`
}

const getLateClass = (time, status) => {
  if (status === 'late') return 'late-time'
  return ''
}

const getEarlyClass = (time, status) => {
  if (status === 'early') return 'early-time'
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return new Date(dateTimeStr).toLocaleString('zh-CN')
}

const getWeekday = (dateStr) => {
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  return weekdays[new Date(dateStr).getDay()]
}

// 监听器
watch(calendarDate, (newDate) => {
  currentMonth.value = newDate.getMonth() + 1
  const today = new Date()
  isToday.value = newDate.getMonth() === today.getMonth() && 
                  newDate.getFullYear() === today.getFullYear()
})

// 生命周期
onMounted(async () => {
  await nextTick()
  await loadChildren()
  
  if (currentChild.value) {
    await loadAttendanceData()
  }
  
  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (trendChart) trendChart.resize()
    if (pieChart) pieChart.resize()
    if (weekChart) weekChart.resize()
  })
})
</script>

<style scoped>
.parent-attendance {
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

/* 出勤统计概览 */
.attendance-overview-section {
  max-width: 1200px;
  margin: 0 auto 20px;
  padding: 0 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 16px;
  height: 120px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.present-card .stat-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.late-card .stat-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.absent-card .stat-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.leave-card .stat-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.stat-icon .el-icon {
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  color: #666;
  font-size: 16px;
  margin-bottom: 4px;
}

.stat-rate {
  color: #999;
  font-size: 12px;
}

/* 筛选区域 */
.filter-section {
  max-width: 1200px;
  margin: 0 auto 20px;
  padding: 0 20px;
}

/* 主要内容区域 */
.main-content-section {
  max-width: 1200px;
  margin: 0 auto 30px;
  padding: 0 20px;
}

/* 日历视图 */
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar-header h3 {
  margin: 0;
  color: #333;
}

.legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.present { background: #67c23a; }
.legend-color.late { background: #e6a23c; }
.legend-color.absent { background: #f56c6c; }
.legend-color.leave { background: #409eff; }

.calendar-day {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 8px 4px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.calendar-day:hover {
  background: #f0f0f0;
}

.calendar-day-present {
  background: #f0f9ff;
  border: 1px solid #67c23a;
}

.calendar-day-late {
  background: #fef7e8;
  border: 1px solid #e6a23c;
}

.calendar-day-absent {
  background: #fef0f0;
  border: 1px solid #f56c6c;
}

.calendar-day-leave {
  background: #f0f9ff;
  border: 1px solid #409eff;
}

.day-number {
  font-weight: 600;
  margin-bottom: 2px;
}

.day-status {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 列表视图 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-header h3 {
  margin: 0;
  color: #333;
}

.date-cell {
  text-align: center;
}

.date-main {
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.date-weekday {
  font-size: 12px;
  color: #666;
}

.courses-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.course-tag {
  margin: 0;
}

.remark-cell {
  color: #666;
  font-size: 13px;
}

.leave-reason {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #409eff;
  font-size: 13px;
}

.text-muted {
  color: #999;
}

.late-time {
  color: #e6a23c;
  font-weight: 600;
}

.early-time {
  color: #e6a23c;
  font-weight: 600;
}

/* 表格行样式 */
:deep(.attendance-row-present) {
  background-color: #f0f9ff;
}

:deep(.attendance-row-late) {
  background-color: #fef7e8;
}

:deep(.attendance-row-absent) {
  background-color: #fef0f0;
}

:deep(.attendance-row-leave) {
  background-color: #f0f9ff;
}

/* 图表视图 */
.chart-container {
  padding: 20px 0;
}

.trend-chart,
.pie-chart,
.week-chart {
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
.attendance-detail {
  max-height: 500px;
  overflow-y: auto;
}

.courses-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.leave-reason-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #409eff;
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
  .attendance-overview-section,
  .filter-section,
  .main-content-section {
    margin-left: 15px;
    margin-right: 15px;
  }
  
  .child-tabs {
    flex-direction: column;
  }
  
  .legend {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
    height: auto;
    gap: 12px;
  }
  
  .chart-container {
    padding: 10px 0;
  }
  
  .trend-chart,
  .pie-chart,
  .week-chart {
    height: 250px;
  }
}
</style>