<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人信息</h1>
      <p>查看和编辑个人资料</p>
    </div>
    
    <el-row :gutter="20">
      <!-- 个人基本信息 -->
      <el-col :span="8">
        <el-card class="profile-card" v-loading="loading">
          <template #header>
            <h3>基本信息</h3>
          </template>
          
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="80" :src="studentInfo.avatar">
                <el-icon size="40"><User /></el-icon>
              </el-avatar>
              <el-button type="text" @click="changeAvatar" class="change-avatar" :loading="avatarUploading">
                {{ avatarUploading ? '上传中...' : '更换头像' }}
              </el-button>
            </div>
            
            <div class="basic-info">
              <div class="info-item">
                <label>姓名：</label>
                <span>{{ studentInfo.name }}</span>
              </div>
              <div class="info-item">
                <label>学号：</label>
                <span>{{ studentInfo.studentId }}</span>
              </div>
              <div class="info-item">
                <label>专业：</label>
                <span>{{ studentInfo.major }}</span>
              </div>
              <div class="info-item">
                <label>班级：</label>
                <span>{{ studentInfo.className }}</span>
              </div>
              <div class="info-item">
                <label>年级：</label>
                <span>{{ studentInfo.grade }}级</span>
              </div>
              <div class="info-item">
                <label>学制：</label>
                <span>{{ studentInfo.duration }}年</span>
              </div>
            </div>
          </div>
        </el-card>
        
        <!-- 学习统计 -->
        <el-card class="stats-card">
          <template #header>
            <h3>学习统计</h3>
          </template>
          
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ learningStats.totalCredits }}</div>
              <div class="stat-label">已获学分</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ learningStats.gpa }}</div>
              <div class="stat-label">平均绩点</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ learningStats.rank }}</div>
              <div class="stat-label">班级排名</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ learningStats.courses }}</div>
              <div class="stat-label">修读课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 详细信息表单 -->
      <el-col :span="16">
        <el-card class="form-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <h3>详细信息</h3>
              <el-button 
                v-if="!isEditing"
                type="primary"
                @click="startEdit"
              >
                编辑信息
              </el-button>
              <div v-else class="edit-actions">
                <el-button @click="cancelEdit">取消</el-button>
                <el-button type="primary" @click="saveChanges">保存</el-button>
              </div>
            </div>
          </template>
          
          <el-form
            ref="profileFormRef"
            :model="editForm"
            :rules="formRules"
            label-width="120px"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input
                    v-model="editForm.realName"
                    :disabled="!isEditing"
                    placeholder="请输入真实姓名"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select
                    v-model="editForm.gender"
                    :disabled="!isEditing"
                    placeholder="请选择性别"
                    style="width: 100%"
                  >
                    <el-option label="男" value="male" />
                    <el-option label="女" value="female" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期" prop="birthDate">
                  <el-date-picker
                    v-model="editForm.birthDate"
                    :disabled="!isEditing"
                    type="date"
                    placeholder="选择出生日期"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="身份证号" prop="idCard">
                  <el-input
                    v-model="editForm.idCard"
                    :disabled="!isEditing"
                    placeholder="请输入身份证号"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input
                    v-model="editForm.phone"
                    :disabled="!isEditing"
                    placeholder="请输入手机号码"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱地址" prop="email">
                  <el-input
                    v-model="editForm.email"
                    :disabled="!isEditing"
                    placeholder="请输入邮箱地址"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="家庭地址" prop="address">
              <el-input
                v-model="editForm.address"
                :disabled="!isEditing"
                type="textarea"
                :rows="2"
                placeholder="请输入家庭地址"
              />
            </el-form-item>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="紧急联系人" prop="emergencyContact">
                  <el-input
                    v-model="editForm.emergencyContact"
                    :disabled="!isEditing"
                    placeholder="请输入紧急联系人"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系人电话" prop="emergencyPhone">
                  <el-input
                    v-model="editForm.emergencyPhone"
                    :disabled="!isEditing"
                    placeholder="请输入联系人电话"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="editForm.bio"
                :disabled="!isEditing"
                type="textarea"
                :rows="3"
                placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="warning" @click="passwordDialogVisible = true">
        <el-icon><Lock /></el-icon>
        修改密码
      </el-button>
      <el-button type="info" @click="exportProfile">
        <el-icon><Download /></el-icon>
        导出信息
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Lock, Download } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { studentApi } from '@/api/student'
import { authApi } from '@/api/auth'

const authStore = useAuthStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const isEditing = ref(false)
const passwordDialogVisible = ref(false)
const loading = ref(false)
const avatarUploading = ref(false)

const studentInfo = ref({
  name: '',
  studentId: '',
  major: '',
  className: '',
  grade: '',
  duration: '',
  avatar: ''
})

const learningStats = ref({
  totalCredits: 0,
  gpa: 0,
  rank: 0,
  courses: 0
})

const editForm = reactive({
  realName: '',
  gender: '',
  birthDate: '',
  idCard: '',
  phone: '',
  email: '',
  address: '',
  emergencyContact: '',
  emergencyPhone: '',
  bio: ''
})

// 加载学生基本信息
const loadStudentInfo = async () => {
  loading.value = true

  try {
    const { data } = await studentApi.getProfile()

    // 更新基本信息
    studentInfo.value = {
      name: data.realName || data.name || '',
      studentId: data.studentNo || data.studentId || '',
      major: data.majorName || data.major || '',
      className: data.className || data.classInfo?.className || '',
      grade: data.grade || data.enrollmentYear || '',
      duration: data.duration || data.studyYears || 4,
      avatar: data.avatarUrl || data.avatar || ''
    }

    // 更新详细信息表单
    Object.assign(editForm, {
      realName: data.realName || '',
      gender: data.gender || '',
      birthDate: data.birthDate || '',
      idCard: data.idCard || data.identityCard || '',
      phone: data.phone || data.mobile || '',
      email: data.email || '',
      address: data.address || data.homeAddress || '',
      emergencyContact: data.emergencyContact || data.emergencyContactName || '',
      emergencyPhone: data.emergencyPhone || data.emergencyContactPhone || '',
      bio: data.bio || data.personalDescription || ''
    })

  } catch (error) {
    console.error('加载学生信息失败:', error)
    ElMessage.error('加载个人信息失败')
  } finally {
    loading.value = false
  }
}

// 加载学习统计
const loadLearningStats = async () => {
  try {
    const { data } = await studentApi.getLearningStats()

    learningStats.value = {
      totalCredits: data.totalCredits || data.earnedCredits || 0,
      gpa: data.gpa || data.averageGPA || 0,
      rank: data.classRank || data.rank || 0,
      courses: data.totalCourses || data.courseCount || 0
    }
  } catch (error) {
    console.error('加载学习统计失败:', error)
    // 使用默认值，不显示错误信息
    learningStats.value = {
      totalCredits: 0,
      gpa: 0,
      rank: 0,
      courses: 0
    }
  }
}

const originalForm = reactive({})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const formRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dX]$/, 
      message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const startEdit = () => {
  isEditing.value = true
  // 备份原始数据
  Object.assign(originalForm, editForm)
}

const cancelEdit = () => {
  isEditing.value = false
  // 恢复原始数据
  Object.assign(editForm, originalForm)
}

const saveChanges = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      loading.value = true

      // 调用API保存数据
      await studentApi.updateProfile(editForm)

      // 重新加载学生信息
      await loadStudentInfo()

      ElMessage.success('个人信息更新成功')
      isEditing.value = false
    } catch (error) {
      console.error('更新个人信息失败:', error)
      ElMessage.error('更新失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}

const changeAvatar = () => {
  // 创建文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'

  input.onchange = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    // 验证文件大小（限制为2MB）
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.error('头像文件大小不能超过2MB')
      return
    }

    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      ElMessage.error('请选择图片文件')
      return
    }

    try {
      avatarUploading.value = true

      // 创建FormData
      const formData = new FormData()
      formData.append('avatar', file)

      // 上传头像
      const { data } = await studentApi.uploadAvatar(formData)

      // 更新头像URL
      studentInfo.value.avatar = data.avatarUrl || data.url

      ElMessage.success('头像更新成功')
    } catch (error) {
      console.error('头像上传失败:', error)
      ElMessage.error('头像上传失败，请稍后重试')
    } finally {
      avatarUploading.value = false
    }
  }

  input.click()
}

const changePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      loading.value = true

      // 调用API修改密码
      await authApi.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })

      ElMessage.success('密码修改成功，请重新登录')
      passwordDialogVisible.value = false

      // 清空表单
      Object.assign(passwordForm, {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      })

      // 延迟后跳转到登录页
      setTimeout(() => {
        authStore.logout()
      }, 1500)

    } catch (error) {
      console.error('密码修改失败:', error)
      ElMessage.error('密码修改失败，请检查当前密码是否正确')
    } finally {
      loading.value = false
    }
  })
}

const exportProfile = async () => {
  try {
    loading.value = true

    const { data } = await studentApi.exportProfile({
      format: 'pdf',
      includeStats: true
    })

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([data]))
    const link = document.createElement('a')
    link.href = url
    link.download = `个人信息_${studentInfo.value.studentId}_${new Date().toISOString().split('T')[0]}.pdf`
    link.click()
    window.URL.revokeObjectURL(url)

    ElMessage.success('个人信息导出成功')
  } catch (error) {
    console.error('导出个人信息失败:', error)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadStudentInfo(),
    loadLearningStats()
  ])
})
</script>

<style scoped>
@import '@/styles/student.css';

.profile-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.profile-card, .stats-card, .form-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.profile-content {
  text-align: center;
}

.avatar-section {
  margin-bottom: 24px;
}

.change-avatar {
  margin-top: 12px;
  color: #409eff;
}

.basic-info {
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  font-weight: 600;
  color: #606266;
  min-width: 60px;
}

.info-item span {
  color: #303133;
  flex: 1;
  text-align: right;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
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

.edit-actions {
  display: flex;
  gap: 8px;
}

.profile-form {
  padding: 20px 0;
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
  display: flex;
  justify-content: center;
  gap: 16px;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 10px;
  }

  .page-header {
    padding: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
    align-items: center;
  }

  .action-buttons .el-button {
    width: 200px;
  }
}

@media (max-width: 992px) {
  .el-col {
    margin-bottom: 20px;
  }
}
</style>