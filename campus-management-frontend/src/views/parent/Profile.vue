<template>
  <div class="parent-profile">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>个人资料</h1>
          <p>管理您的个人信息和账户设置</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="editProfile">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
          <el-button @click="changePassword">
            <el-icon><Lock /></el-icon>
            修改密码
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <el-row :gutter="20">
      <!-- 左侧：个人信息 -->
      <el-col :xs="24" :lg="16">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <h3>基本信息</h3>
              <el-button type="text" @click="editProfile">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
            </div>
          </template>

          <div class="profile-content">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <div class="avatar-container">
                <el-avatar :size="120" :src="userInfo.avatar" class="profile-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="avatar-overlay" @click="changeAvatar">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
              <div class="user-basic">
                <h2>{{ userInfo.realName || userInfo.name }}</h2>
                <p class="user-role">家长</p>
                <el-tag v-if="userInfo.isVerified" type="success" size="small">
                  <el-icon><CircleCheck /></el-icon>
                  已认证
                </el-tag>
                <el-tag v-else type="warning" size="small">
                  <el-icon><Warning /></el-icon>
                  待认证
                </el-tag>
              </div>
            </div>

            <!-- 详细信息 -->
            <div class="info-section">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="真实姓名">
                  {{ userInfo.realName || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="用户名">
                  {{ userInfo.username || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="手机号码">
                  {{ userInfo.phone || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="邮箱地址">
                  {{ userInfo.email || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="身份证号">
                  {{ maskIdCard(userInfo.idCard) }}
                </el-descriptions-item>
                <el-descriptions-item label="性别">
                  {{ getGenderText(userInfo.gender) }}
                </el-descriptions-item>
                <el-descriptions-item label="出生日期">
                  {{ formatDate(userInfo.birthDate) }}
                </el-descriptions-item>
                <el-descriptions-item label="注册时间">
                  {{ formatDate(userInfo.createTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="联系地址" :span="2">
                  {{ userInfo.address || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="个人简介" :span="2">
                  {{ userInfo.bio || '暂无个人简介' }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-card>

        <!-- 子女信息 -->
        <el-card class="children-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <h3>子女信息</h3>
              <el-button type="text" @click="manageChildren">
                <el-icon><Setting /></el-icon>
                管理
              </el-button>
            </div>
          </template>

          <div class="children-list">
            <div
              v-for="child in children"
              :key="child.id"
              class="child-item"
            >
              <el-avatar :size="60" :src="child.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="child-info">
                <h4>{{ child.name }}</h4>
                <p>{{ child.grade }} {{ child.className }}</p>
                <p class="relationship">{{ getRelationshipText(child.relationship) }}</p>
              </div>
              <div class="child-status">
                <el-tag :type="getStatusType(child.status)" size="small">
                  {{ getStatusText(child.status) }}
                </el-tag>
              </div>
            </div>

            <el-empty v-if="children.length === 0" description="暂无子女信息">
              <el-button type="primary" @click="addChild">添加子女</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：账户设置 -->
      <el-col :xs="24" :lg="8">
        <!-- 账户安全 -->
        <el-card class="security-card" shadow="hover">
          <template #header>
            <h3>账户安全</h3>
          </template>

          <div class="security-items">
            <div class="security-item">
              <div class="security-info">
                <div class="security-title">
                  <el-icon><Lock /></el-icon>
                  登录密码
                </div>
                <div class="security-desc">
                  {{ userInfo.lastPasswordChange ? `上次修改：${formatDate(userInfo.lastPasswordChange)}` : '建议定期修改密码' }}
                </div>
              </div>
              <el-button size="small" @click="changePassword">修改</el-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <div class="security-title">
                  <el-icon><Phone /></el-icon>
                  手机绑定
                </div>
                <div class="security-desc">
                  {{ userInfo.phone ? `已绑定：${maskPhone(userInfo.phone)}` : '未绑定手机号' }}
                </div>
              </div>
              <el-button size="small" @click="bindPhone">
                {{ userInfo.phone ? '更换' : '绑定' }}
              </el-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <div class="security-title">
                  <el-icon><Message /></el-icon>
                  邮箱绑定
                </div>
                <div class="security-desc">
                  {{ userInfo.email ? `已绑定：${maskEmail(userInfo.email)}` : '未绑定邮箱' }}
                </div>
              </div>
              <el-button size="small" @click="bindEmail">
                {{ userInfo.email ? '更换' : '绑定' }}
              </el-button>
            </div>

            <div class="security-item">
              <div class="security-info">
                <div class="security-title">
                  <el-icon><Shield /></el-icon>
                  实名认证
                </div>
                <div class="security-desc">
                  {{ userInfo.isVerified ? '已完成实名认证' : '提高账户安全性' }}
                </div>
              </div>
              <el-button
                size="small"
                :disabled="userInfo.isVerified"
                @click="verifyIdentity"
              >
                {{ userInfo.isVerified ? '已认证' : '认证' }}
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 通知设置 -->
        <el-card class="notification-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <h3>通知设置</h3>
          </template>

          <div class="notification-settings">
            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">成绩通知</div>
                <div class="setting-desc">子女成绩发布时通知</div>
              </div>
              <el-switch v-model="notificationSettings.gradeNotification" />
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">考勤通知</div>
                <div class="setting-desc">子女考勤异常时通知</div>
              </div>
              <el-switch v-model="notificationSettings.attendanceNotification" />
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">作业通知</div>
                <div class="setting-desc">作业提醒和批改通知</div>
              </div>
              <el-switch v-model="notificationSettings.assignmentNotification" />
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">活动通知</div>
                <div class="setting-desc">校园活动和重要通知</div>
              </div>
              <el-switch v-model="notificationSettings.activityNotification" />
            </div>

            <div class="setting-item">
              <div class="setting-info">
                <div class="setting-title">缴费通知</div>
                <div class="setting-desc">缴费提醒和到期通知</div>
              </div>
              <el-switch v-model="notificationSettings.paymentNotification" />
            </div>
          </div>

          <div class="notification-actions">
            <el-button type="primary" @click="saveNotificationSettings">
              保存设置
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Edit,
  Lock,
  Camera,
  CircleCheck,
  Warning,
  Setting,
  Phone,
  Message,
  Shield
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const userInfo = ref({
  id: '',
  username: '',
  realName: '',
  phone: '',
  email: '',
  idCard: '',
  gender: '',
  birthDate: '',
  address: '',
  bio: '',
  avatar: '',
  isVerified: false,
  createTime: '',
  lastPasswordChange: ''
})

const children = ref([])

const notificationSettings = reactive({
  gradeNotification: true,
  attendanceNotification: true,
  assignmentNotification: true,
  activityNotification: true,
  paymentNotification: true
})

// 方法
const loadUserProfile = async () => {
  try {
    const { data } = await parentApi.getProfile()
    userInfo.value = {
      ...userInfo.value,
      ...data
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  }
}

const loadChildren = async () => {
  try {
    const { data } = await parentApi.getChildren()
    children.value = data
  } catch (error) {
    console.error('加载子女信息失败:', error)
    ElMessage.error('加载子女信息失败')
  }
}

const editProfile = () => {
  ElMessage.info('编辑资料功能开发中...')
}

const changePassword = () => {
  ElMessage.info('修改密码功能开发中...')
}

const changeAvatar = async () => {
  try {
    // 创建文件输入元素
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*'

    input.onchange = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      // 检查文件大小（限制为2MB）
      if (file.size > 2 * 1024 * 1024) {
        ElMessage.error('图片大小不能超过2MB')
        return
      }

      // 检查文件类型
      if (!file.type.startsWith('image/')) {
        ElMessage.error('请选择图片文件')
        return
      }

      const formData = new FormData()
      formData.append('avatar', file)

      try {
        const { data } = await parentApi.uploadAvatar(formData)
        userInfo.value.avatar = data.avatarUrl
        ElMessage.success('头像更新成功')
      } catch (error) {
        console.error('上传头像失败:', error)
        ElMessage.error('上传头像失败')
      }
    }

    input.click()
  } catch (error) {
    console.error('更换头像失败:', error)
    ElMessage.error('更换头像失败')
  }
}

const bindPhone = () => {
  ElMessage.info('绑定手机功能开发中...')
}

const bindEmail = () => {
  ElMessage.info('绑定邮箱功能开发中...')
}

const verifyIdentity = () => {
  ElMessage.info('实名认证功能开发中...')
}

const saveNotificationSettings = async () => {
  try {
    await parentApi.updateNotificationSettings(notificationSettings)
    ElMessage.success('通知设置保存成功')
  } catch (error) {
    console.error('保存通知设置失败:', error)
    ElMessage.error('保存通知设置失败')
  }
}

const manageChildren = () => {
  router.push('/parent/children')
}

const addChild = () => {
  router.push('/parent/children')
}

// 辅助方法
const maskIdCard = (idCard) => {
  if (!idCard) return '-'
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

const maskPhone = (phone) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const maskEmail = (email) => {
  if (!email) return '-'
  const [username, domain] = email.split('@')
  if (username.length <= 2) return email
  return `${username.substring(0, 2)}***@${domain}`
}

const getGenderText = (gender) => {
  const genderMap = {
    'male': '男',
    'female': '女'
  }
  return genderMap[gender] || '-'
}

const getRelationshipText = (relationship) => {
  const relationshipMap = {
    'father': '父亲',
    'mother': '母亲',
    'guardian': '监护人',
    'other': '其他'
  }
  return relationshipMap[relationship] || relationship
}

const getStatusType = (status) => {
  const statusMap = {
    'active': 'success',
    'inactive': 'danger',
    'graduated': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'active': '在校',
    'inactive': '离校',
    'graduated': '毕业'
  }
  return statusMap[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadUserProfile(),
    loadChildren()
  ])
})
</script>

<style scoped>
@import '@/styles/parent.css';

.parent-profile {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.profile-card,
.children-card,
.security-card,
.notification-card {
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

.profile-content {
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e4e7ed;
}

.avatar-container {
  position: relative;
  cursor: pointer;
}

.profile-avatar {
  border: 4px solid #f0f0f0;
  transition: all 0.3s ease;
}

.avatar-container:hover .profile-avatar {
  border-color: #409eff;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 12px;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.user-basic h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
}

.user-role {
  margin: 0 0 12px 0;
  color: #909399;
  font-size: 14px;
}

.info-section {
  margin-top: 24px;
}

.children-list {
  padding: 16px 0;
}

.child-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.child-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.child-info {
  flex: 1;
}

.child-info h4 {
  margin: 0 0 4px 0;
  color: #303133;
  font-size: 16px;
}

.child-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.relationship {
  color: #909399 !important;
  font-size: 12px !important;
}

.security-items,
.notification-settings {
  padding: 16px 0;
}

.security-item,
.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.security-item:last-child,
.setting-item:last-child {
  border-bottom: none;
}

.security-info,
.setting-info {
  flex: 1;
}

.security-title,
.setting-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.security-desc,
.setting-desc {
  color: #909399;
  font-size: 12px;
}

.notification-actions {
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  text-align: center;
}

@media (max-width: 768px) {
  .parent-profile {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }

  .header-actions {
    align-self: stretch;
    justify-content: center;
  }

  .avatar-section {
    flex-direction: column;
    text-align: center;
  }

  .child-item {
    flex-direction: column;
    text-align: center;
  }

  .security-item,
  .setting-item {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>