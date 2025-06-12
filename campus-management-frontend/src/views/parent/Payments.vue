<template>
  <div class="parent-payments">
    <div class="page-header">
      <h1>缴费记录</h1>
      <p>查看和管理子女的缴费信息</p>
    </div>
    
    <!-- 子女选择和筛选 -->
    <el-card class="filter-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-select v-model="selectedChildId" placeholder="选择子女" @change="handleChildChange">
            <el-option
              v-for="child in children"
              :key="child.id"
              :label="child.name"
              :value="child.id"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterStatus" placeholder="缴费状态" clearable>
            <el-option label="待缴费" value="unpaid" />
            <el-option label="已缴费" value="paid" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterType" placeholder="费用类型" clearable>
            <el-option label="学费" value="tuition" />
            <el-option label="住宿费" value="accommodation" />
            <el-option label="教材费" value="textbook" />
            <el-option label="活动费" value="activity" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="3">
          <el-button @click="exportPayments">
            <el-icon><Download /></el-icon>
            导出记录
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 缴费统计 -->
    <el-row :gutter="20" class="stats-section" v-if="selectedChildId">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number text-danger">¥{{ paymentStats.totalUnpaid }}</div>
            <div class="stat-label">待缴金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number text-success">¥{{ paymentStats.totalPaid }}</div>
            <div class="stat-label">已缴金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ paymentStats.unpaidCount }}</div>
            <div class="stat-label">待缴项目</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ paymentStats.overdueCount }}</div>
            <div class="stat-label">逾期项目</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 待缴费提醒 -->
    <el-card v-if="unpaidItems.length > 0" class="unpaid-alert-card">
      <template #header>
        <div class="card-header">
          <h3 class="text-danger">
            <el-icon><Warning /></el-icon>
            待缴费提醒
          </h3>
        </div>
      </template>
      
      <div class="unpaid-items">
        <div
          v-for="item in unpaidItems"
          :key="item.id"
          class="unpaid-item"
          :class="{ 'urgent': item.isOverdue }"
        >
          <div class="item-info">
            <h4>{{ item.name }}</h4>
            <p>{{ item.description }}</p>
            <div class="item-meta">
              <span class="amount">¥{{ item.amount }}</span>
              <span class="due-date" :class="{ 'overdue': item.isOverdue }">
                截止：{{ item.dueDate }}
              </span>
            </div>
          </div>
          <div class="item-actions">
            <el-button type="primary" @click="payNow(item)">
              立即缴费
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 缴费记录列表 -->
    <el-card class="payments-table-card" v-if="selectedChildId">
      <template #header>
        <div class="card-header">
          <h3>缴费记录</h3>
          <div class="header-actions">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="table">表格视图</el-radio-button>
              <el-radio-button label="timeline">时间线视图</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      
      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'">
        <el-table
          :data="filteredPayments"
          style="width: 100%"
          :row-class-name="getRowClassName"
        >
          <el-table-column prop="name" label="费用名称" min-width="150" />
          
          <el-table-column prop="type" label="费用类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)">
                {{ getTypeName(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="amount" label="金额" width="120" align="right">
            <template #default="{ row }">
              <span class="amount">¥{{ row.amount }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="dueDate" label="截止日期" width="120" align="center" />
          
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="paymentDate" label="缴费日期" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.paymentDate">{{ row.paymentDate }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="paymentMethod" label="缴费方式" width="100" align="center">
            <template #default="{ row }">
              <span v-if="row.paymentMethod">{{ row.paymentMethod }}</span>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="150" align="center">
            <template #default="{ row }">
              <el-button 
                v-if="row.status === 'unpaid'" 
                size="small" 
                type="primary" 
                @click="payNow(row)"
              >
                立即缴费
              </el-button>
              <el-button size="small" @click="viewDetail(row)">
                详情
              </el-button>
              <el-button 
                v-if="row.status === 'paid'" 
                size="small" 
                @click="downloadReceipt(row)"
              >
                票据
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 时间线视图 -->
      <div v-else class="payments-timeline">
        <el-timeline>
          <el-timeline-item
            v-for="payment in filteredPayments"
            :key="payment.id"
            :timestamp="payment.paymentDate || payment.dueDate"
            :type="getTimelineType(payment.status)"
          >
            <div class="timeline-content">
              <div class="payment-info">
                <h4>{{ payment.name }}</h4>
                <p>{{ payment.description }}</p>
                <div class="payment-meta">
                  <span class="amount">¥{{ payment.amount }}</span>
                  <el-tag :type="getStatusType(payment.status)" size="small">
                    {{ getStatusName(payment.status) }}
                  </el-tag>
                  <span v-if="payment.paymentMethod" class="method">
                    {{ payment.paymentMethod }}
                  </span>
                </div>
              </div>
              <div v-if="payment.status === 'unpaid'" class="timeline-actions">
                <el-button size="small" type="primary" @click="payNow(payment)">
                  立即缴费
                </el-button>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 选择子女提示 -->
    <el-empty v-else description="请先选择子女查看缴费记录" />
    
    <!-- 缴费详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="缴费详情"
      width="500px"
    >
      <div v-if="selectedPayment" class="payment-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="费用名称">
            {{ selectedPayment.name }}
          </el-descriptions-item>
          <el-descriptions-item label="费用类型">
            {{ getTypeName(selectedPayment.type) }}
          </el-descriptions-item>
          <el-descriptions-item label="金额">
            ¥{{ selectedPayment.amount }}
          </el-descriptions-item>
          <el-descriptions-item label="截止日期">
            {{ selectedPayment.dueDate }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedPayment.status)">
              {{ getStatusName(selectedPayment.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="缴费日期">
            {{ selectedPayment.paymentDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="缴费方式">
            {{ selectedPayment.paymentMethod || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="交易号">
            {{ selectedPayment.transactionId || '-' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="payment-description">
          <h4>费用说明</h4>
          <p>{{ selectedPayment.description }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="selectedPayment?.status === 'unpaid'" 
          type="primary" 
          @click="payNow(selectedPayment)"
        >
          立即缴费
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 在线支付对话框 -->
    <el-dialog
      v-model="paymentDialogVisible"
      title="在线缴费"
      width="400px"
    >
      <div v-if="currentPayment" class="payment-form">
        <div class="payment-info">
          <h3>{{ currentPayment.name }}</h3>
          <div class="amount-display">
            <span class="label">缴费金额：</span>
            <span class="amount">¥{{ currentPayment.amount }}</span>
          </div>
        </div>
        
        <el-form :model="paymentForm" label-width="100px">
          <el-form-item label="缴费方式">
            <el-radio-group v-model="paymentForm.method">
              <el-radio label="alipay">支付宝</el-radio>
              <el-radio label="wechat">微信支付</el-radio>
              <el-radio label="bank">银行卡</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="备注">
            <el-input
              v-model="paymentForm.remark"
              type="textarea"
              :rows="2"
              placeholder="可选填写备注信息"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPayment">
          确认缴费
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Download, Warning } from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'

const selectedChildId = ref('')
const filterStatus = ref('')
const filterType = ref('')
const dateRange = ref([])
const viewMode = ref('table')
const detailDialogVisible = ref(false)
const paymentDialogVisible = ref(false)
const selectedPayment = ref(null)
const currentPayment = ref(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const children = ref([])

const paymentStats = ref({
  totalUnpaid: '0.00',
  totalPaid: '0.00',
  unpaidCount: 0,
  overdueCount: 0
})

const paymentForm = reactive({
  method: 'alipay',
  remark: ''
})

const payments = ref([])

const unpaidItems = computed(() => {
  return payments.value.filter(p => p.status === 'unpaid' || p.status === 'overdue')
})

const filteredPayments = computed(() => {
  return payments.value.filter(payment => {
    const matchesStatus = !filterStatus.value || payment.status === filterStatus.value
    const matchesType = !filterType.value || payment.type === filterType.value
    
    let matchesDate = true
    if (dateRange.value && dateRange.value.length === 2) {
      const paymentDate = payment.paymentDate || payment.dueDate
      matchesDate = paymentDate >= dateRange.value[0] && paymentDate <= dateRange.value[1]
    }
    
    return matchesStatus && matchesType && matchesDate
  })
})

const getStatusType = (status) => {
  const statusMap = {
    'paid': 'success',
    'unpaid': 'warning',
    'overdue': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusName = (status) => {
  const statusMap = {
    'paid': '已缴费',
    'unpaid': '待缴费',
    'overdue': '已逾期'
  }
  return statusMap[status] || status
}

const getTypeTagType = (type) => {
  const typeMap = {
    'tuition': 'primary',
    'accommodation': 'success',
    'textbook': 'warning',
    'activity': 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeName = (type) => {
  const typeMap = {
    'tuition': '学费',
    'accommodation': '住宿费',
    'textbook': '教材费',
    'activity': '活动费'
  }
  return typeMap[type] || type
}

const getTimelineType = (status) => {
  const typeMap = {
    'paid': 'success',
    'unpaid': 'warning',
    'overdue': 'danger'
  }
  return typeMap[status] || 'info'
}

const getRowClassName = ({ row }) => {
  if (row.status === 'overdue') return 'overdue-row'
  if (row.status === 'unpaid') return 'unpaid-row'
  return ''
}

// 加载子女列表
const loadChildren = async () => {
  try {
    const { data } = await parentApi.getChildren()
    children.value = data

    if (data.length > 0) {
      selectedChildId.value = data[0].id
      await loadPayments()
    }
  } catch (error) {
    console.error('加载子女信息失败:', error)
    ElMessage.error('加载子女信息失败')
  }
}

// 加载缴费记录
const loadPayments = async () => {
  if (!selectedChildId.value) return

  try {
    const { data } = await parentApi.getPaymentRecords({
      childId: selectedChildId.value,
      status: filterStatus.value,
      type: filterType.value,
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1],
      page: currentPage.value,
      size: pageSize.value
    })

    payments.value = data.payments || data.list || []
    total.value = data.total || data.totalElements || payments.value.length

    // 计算统计数据
    calculateStats()
  } catch (error) {
    console.error('加载缴费记录失败:', error)
    ElMessage.error('加载缴费记录失败')
    payments.value = []
  }
}

// 计算统计数据
const calculateStats = () => {
  const stats = payments.value.reduce((acc, payment) => {
    const amount = parseFloat(payment.amount || 0)

    if (payment.status === 'paid') {
      acc.totalPaid += amount
    } else if (payment.status === 'unpaid') {
      acc.totalUnpaid += amount
      acc.unpaidCount++
    } else if (payment.status === 'overdue') {
      acc.totalUnpaid += amount
      acc.overdueCount++
    }

    return acc
  }, {
    totalPaid: 0,
    totalUnpaid: 0,
    unpaidCount: 0,
    overdueCount: 0
  })

  paymentStats.value = {
    totalPaid: stats.totalPaid.toFixed(2),
    totalUnpaid: stats.totalUnpaid.toFixed(2),
    unpaidCount: stats.unpaidCount,
    overdueCount: stats.overdueCount
  }
}

const handleChildChange = async (childId) => {
  if (childId) {
    currentPage.value = 1
    await loadPayments()
    const childName = children.value.find(c => c.id === childId)?.name
    ElMessage.success(`切换到${childName}的缴费记录`)
  }
}

const handleSearch = async () => {
  currentPage.value = 1
  await loadPayments()
}

const exportPayments = () => {
  ElMessage.info('导出功能开发中...')
}

const payNow = (payment) => {
  currentPayment.value = payment
  paymentDialogVisible.value = true
  detailDialogVisible.value = false
}

const viewDetail = (payment) => {
  selectedPayment.value = payment
  detailDialogVisible.value = true
}

const downloadReceipt = (payment) => {
  ElMessage.info(`下载${payment.name}的缴费票据`)
}

const confirmPayment = async () => {
  if (!currentPayment.value) return
  
  try {
    await ElMessageBox.confirm(
      `确定要缴费 ¥${currentPayment.value.amount} 吗？`,
      '确认缴费',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用API进行支付
    await parentApi.makePayment(currentPayment.value.id, {
      method: paymentForm.method,
      remark: paymentForm.remark
    })

    ElMessage.success('缴费成功！')
    paymentDialogVisible.value = false

    // 重新加载缴费记录
    await loadPayments()

    // 重置表单
    paymentForm.method = 'alipay'
    paymentForm.remark = ''
  } catch {
    // 用户取消
  }
}

const getMethodName = (method) => {
  const methodMap = {
    'alipay': '支付宝',
    'wechat': '微信支付',
    'bank': '银行卡'
  }
  return methodMap[method] || method
}

const updateStats = () => {
  const unpaidPayments = payments.value.filter(p => p.status === 'unpaid' || p.status === 'overdue')
  const paidPayments = payments.value.filter(p => p.status === 'paid')
  const overduePayments = payments.value.filter(p => p.status === 'overdue')
  
  const totalUnpaid = unpaidPayments.reduce((sum, p) => sum + parseFloat(p.amount), 0)
  const totalPaid = paidPayments.reduce((sum, p) => sum + parseFloat(p.amount), 0)
  
  paymentStats.value = {
    totalUnpaid: totalUnpaid.toFixed(2),
    totalPaid: totalPaid.toFixed(2),
    unpaidCount: unpaidPayments.length,
    overdueCount: overduePayments.length
  }
}

const handleSizeChange = async (size) => {
  pageSize.value = size
  currentPage.value = 1
  await loadPayments()
}

const handleCurrentChange = async (page) => {
  currentPage.value = page
  await loadPayments()
}

onMounted(async () => {
  await loadChildren()
})
</script>

<style scoped>
@import '@/styles/parent.css';
</style>