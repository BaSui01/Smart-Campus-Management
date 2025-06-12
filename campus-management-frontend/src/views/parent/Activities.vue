<template>
  <div class="parent-activities">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>校园活动</h1>
          <p>参与丰富多彩的校园活动</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ activityStats.total }}</span>
            <span class="stat-label">总活动</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ activityStats.registered }}</span>
            <span class="stat-label">已报名</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ activityStats.upcoming }}</span>
            <span class="stat-label">即将开始</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索活动名称..."
            clearable
            :prefix-icon="Search"
            @keyup.enter="searchActivities"
            @clear="searchActivities"
          />
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterType" placeholder="活动类型" clearable @change="searchActivities">
            <el-option label="全部" value="" />
            <el-option label="学术活动" value="academic" />
            <el-option label="文体活动" value="sports" />
            <el-option label="社会实践" value="practice" />
            <el-option label="志愿服务" value="volunteer" />
            <el-option label="竞赛活动" value="competition" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterStatus" placeholder="活动状态" clearable @change="searchActivities">
            <el-option label="全部" value="" />
            <el-option label="报名中" value="registering" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="ended" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="24" :md="10">
          <div class="filter-actions">
            <el-button @click="searchActivities" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilters">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 活动列表 -->
    <el-card class="activities-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>活动列表</h3>
          <div class="header-controls">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="list">
                <el-icon><List /></el-icon>
                列表
              </el-radio-button>
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
                网格
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div v-loading="loading">
        <!-- 列表视图 -->
        <div v-if="viewMode === 'list'" class="activities-list">
          <div
            v-for="activity in activities"
            :key="activity.id"
            class="activity-item"
            @click="viewActivityDetail(activity)"
          >
            <div class="activity-image">
              <el-image
                :src="activity.imageUrl || '/images/default-activity.jpg'"
                fit="cover"
                :alt="activity.title"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <div class="activity-content">
              <div class="activity-header">
                <h4 class="activity-title">{{ activity.title }}</h4>
                <div class="activity-tags">
                  <el-tag :type="getActivityTypeTag(activity.type)" size="small">
                    {{ getActivityTypeText(activity.type) }}
                  </el-tag>
                  <el-tag :type="getStatusTagType(activity.status)" size="small">
                    {{ getStatusText(activity.status) }}
                  </el-tag>
                </div>
              </div>

              <p class="activity-description">{{ activity.description }}</p>

              <div class="activity-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.registeredCount }}/{{ activity.maxParticipants }} 人</span>
                </div>
              </div>
            </div>

            <div class="activity-actions">
              <el-button
                v-if="activity.status === 'registering' && !activity.isRegistered"
                type="primary"
                @click.stop="registerActivity(activity)"
                :disabled="activity.registeredCount >= activity.maxParticipants"
              >
                {{ activity.registeredCount >= activity.maxParticipants ? '已满员' : '立即报名' }}
              </el-button>
              <el-button
                v-else-if="activity.isRegistered"
                type="warning"
                @click.stop="cancelRegistration(activity)"
              >
                取消报名
              </el-button>
              <el-button
                v-else
                disabled
              >
                {{ getActionText(activity.status) }}
              </el-button>
              <el-button size="small" @click.stop="viewActivityDetail(activity)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>

        <!-- 网格视图 -->
        <div v-else class="activities-grid">
          <div
            v-for="activity in activities"
            :key="activity.id"
            class="activity-card"
            @click="viewActivityDetail(activity)"
          >
            <div class="card-image">
              <el-image
                :src="activity.imageUrl || '/images/default-activity.jpg'"
                fit="cover"
                :alt="activity.title"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="card-overlay">
                <el-tag :type="getActivityTypeTag(activity.type)" size="small">
                  {{ getActivityTypeText(activity.type) }}
                </el-tag>
              </div>
            </div>

            <div class="card-content">
              <h4 class="card-title">{{ activity.title }}</h4>
              <p class="card-description">{{ activity.description }}</p>

              <div class="card-meta">
                <div class="meta-row">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(activity.startTime) }}</span>
                </div>
                <div class="meta-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location }}</span>
                </div>
                <div class="meta-row">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.registeredCount }}/{{ activity.maxParticipants }}</span>
                </div>
              </div>

              <div class="card-status">
                <el-tag :type="getStatusTagType(activity.status)" size="small">
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>
            </div>

            <div class="card-actions">
              <el-button
                v-if="activity.status === 'registering' && !activity.isRegistered"
                type="primary"
                size="small"
                @click.stop="registerActivity(activity)"
                :disabled="activity.registeredCount >= activity.maxParticipants"
              >
                {{ activity.registeredCount >= activity.maxParticipants ? '已满员' : '报名' }}
              </el-button>
              <el-button
                v-else-if="activity.isRegistered"
                type="warning"
                size="small"
                @click.stop="cancelRegistration(activity)"
              >
                取消
              </el-button>
              <el-button
                v-else
                size="small"
                disabled
              >
                {{ getActionText(activity.status) }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && activities.length === 0" description="暂无活动数据">
          <el-button type="primary" @click="searchActivities">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  List,
  Grid,
  Picture,
  Calendar,
  Location,
  User
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const filterType = ref('')
const filterStatus = ref('')
const viewMode = ref('list')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const activities = ref([])
const activityStats = reactive({
  total: 0,
  registered: 0,
  upcoming: 0
})

// 方法
const loadActivityStats = async () => {
  try {
    const { data } = await parentApi.getDashboardStats()
    activityStats.total = data.activities?.total || 0
    activityStats.registered = data.activities?.registered || 0
    activityStats.upcoming = data.activities?.upcoming || 0
  } catch (error) {
    console.error('加载活动统计失败:', error)
  }
}

const searchActivities = async () => {
  loading.value = true

  try {
    const { data } = await parentApi.getActivities({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchQuery.value,
      type: filterType.value,
      status: filterStatus.value
    })

    const activityList = Array.isArray(data) ? data : (data.activities || data.list || [])

    activities.value = activityList.map(activity => ({
      id: activity.id,
      title: activity.title || activity.activityName,
      description: activity.description || activity.content,
      type: activity.type || activity.activityType,
      status: activity.status || getActivityStatus(activity),
      imageUrl: activity.imageUrl || activity.coverImage,
      startTime: activity.startTime || activity.beginTime,
      endTime: activity.endTime || activity.finishTime,
      location: activity.location || activity.venue,
      maxParticipants: activity.maxParticipants || activity.capacity || 100,
      registeredCount: activity.registeredCount || activity.participantCount || 0,
      isRegistered: activity.isRegistered || false,
      organizer: activity.organizer || activity.organizerName,
      requirements: activity.requirements || activity.requirement
    }))

    total.value = data.total || data.totalElements || activityList.length

  } catch (error) {
    console.error('加载活动列表失败:', error)
    ElMessage.error('加载活动列表失败')
    activities.value = []
  } finally {
    loading.value = false
  }
}

const getActivityStatus = (activity) => {
  const now = new Date()
  const startTime = new Date(activity.startTime || activity.beginTime)
  const endTime = new Date(activity.endTime || activity.finishTime)
  const registrationEnd = new Date(activity.registrationEndTime || activity.registrationDeadline)

  if (now < registrationEnd) return 'registering'
  if (now >= startTime && now <= endTime) return 'ongoing'
  if (now > endTime) return 'ended'
  return 'pending'
}

const registerActivity = async (activity) => {
  try {
    await ElMessageBox.confirm(
      `确定要报名参加《${activity.title}》吗？`,
      '确认报名',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await parentApi.registerActivity(activity.id, {
      childId: '', // 这里需要选择子女
      remarks: ''
    })

    // 更新活动状态
    activity.isRegistered = true
    activity.registeredCount++
    activityStats.registered++

    ElMessage.success('报名成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('报名失败:', error)
      ElMessage.error('报名失败，请稍后重试')
    }
  }
}

const cancelRegistration = async (activity) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消报名《${activity.title}》吗？`,
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await parentApi.cancelActivityRegistration(activity.id)

    // 更新活动状态
    activity.isRegistered = false
    activity.registeredCount--
    activityStats.registered--

    ElMessage.success('取消报名成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消报名失败:', error)
      ElMessage.error('取消报名失败，请稍后重试')
    }
  }
}

const viewActivityDetail = (activity) => {
  // 这里可以打开详情弹窗或跳转到详情页
  ElMessage.info(`查看《${activity.title}》详情`)
}

const resetFilters = () => {
  searchQuery.value = ''
  filterType.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  searchActivities()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  searchActivities()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  searchActivities()
}

const getActivityTypeTag = (type) => {
  const typeMap = {
    'academic': 'primary',
    'sports': 'success',
    'practice': 'warning',
    'volunteer': 'info',
    'competition': 'danger'
  }
  return typeMap[type] || 'info'
}

const getActivityTypeText = (type) => {
  const textMap = {
    'academic': '学术活动',
    'sports': '文体活动',
    'practice': '社会实践',
    'volunteer': '志愿服务',
    'competition': '竞赛活动'
  }
  return textMap[type] || '其他活动'
}

const getStatusTagType = (status) => {
  const typeMap = {
    'registering': 'success',
    'ongoing': 'warning',
    'ended': 'info',
    'pending': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'registering': '报名中',
    'ongoing': '进行中',
    'ended': '已结束',
    'pending': '待开始'
  }
  return textMap[status] || '未知'
}

const getActionText = (status) => {
  const textMap = {
    'ongoing': '进行中',
    'ended': '已结束',
    'pending': '未开始'
  }
  return textMap[status] || '不可报名'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadActivityStats(),
    searchActivities()
  ])
})
</script>

<style scoped>
@import '@/styles/parent.css';

.parent-activities {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.title-section h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.title-section p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.header-stats {
  display: flex;
  gap: 30px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.activities-card {
  background: white;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.activities-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.activity-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.activity-image {
  width: 120px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
}

.activity-image .el-image {
  width: 100%;
  height: 100%;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.activity-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.activity-tags {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.activity-description {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.activity-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
  justify-content: center;
}

.activities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.activity-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.activity-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.card-image {
  position: relative;
  height: 160px;
  overflow: hidden;
}

.card-image .el-image {
  width: 100%;
  height: 100%;
}

.card-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
}

.card-content {
  padding: 16px;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-description {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  margin-bottom: 12px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
  font-size: 12px;
  color: #909399;
}

.card-status {
  margin-bottom: 12px;
}

.card-actions {
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .parent-activities {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .header-stats {
    align-self: stretch;
    justify-content: space-around;
  }

  .filter-actions {
    justify-content: center;
    margin-top: 16px;
  }

  .activity-item {
    flex-direction: column;
  }

  .activity-image {
    width: 100%;
    height: 120px;
  }

  .activity-header {
    flex-direction: column;
    gap: 8px;
  }

  .activity-actions {
    flex-direction: row;
    align-items: center;
    justify-content: center;
  }

  .activities-grid {
    grid-template-columns: 1fr;
  }
}
</style>