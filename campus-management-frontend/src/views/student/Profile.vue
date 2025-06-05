<template>
  <div class="profile-page">
    <div class="page-header">
      <h1>个人信息</h1>
      <p>查看和编辑个人资料</p>
    </div>
    
    <el-row :gutter="20">
      <!-- 个人基本信息 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <h3>基本信息</h3>
          </template>
          
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="80" :src="studentInfo.avatar">
                <el-icon size="40"><User /></el-icon>
              </el-avatar>
              <el-button type="text" @click="changeAvatar" class="change-avatar">
                更换头像
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
        <el-card class="form-card">
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

const authStore = useAuthStore()
const profileFormRef = ref()
const passwordFormRef = ref()
const isEditing = ref(false)
const passwordDialogVisible = ref(false)

const studentInfo = ref({
  name: '张三',
  studentId: '2021001001',
  major: '计算机科学与技术',
  className: '计科21-1班',
  grade: 2021,
  duration: 4,
  avatar: ''
})

const learningStats = ref({
  totalCredits: 45,
  gpa: 3.75,
  rank: 15,
  courses: 12
})

const editForm = reactive({
  realName: '张三',
  gender: 'male',
  birthDate: '2000-01-01',
  idCard: '110101200001011234',
  phone: '13800138000',
  email: 'zhangsan@email.com',
  address: '北京市海淀区某某街道某某小区',
  emergencyContact: '张父',
  emergencyPhone: '13900139000',
  bio: '我是一名计算机专业的学生，热爱编程和技术。'
})

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
      // 这里调用API保存数据
      // await studentApi.updateStudentInfo(editForm)
      
      ElMessage.success('个人信息更新成功')
      isEditing.value = false
    } catch (error) {
      ElMessage.error('更新失败，请稍后重试')
    }
  })
}

const changeAvatar = () => {
  ElMessage.info('更换头像功能开发中...')
}

const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 这里调用API修改密码
      // await authApi.changePassword(passwordForm)
      
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
      
      // 清空表单
      Object.assign(passwordForm, {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
    } catch (error) {
      ElMessage.error('密码修改失败，请稍后重试')
    }
  })
}

const exportProfile = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(() => {
  // 加载学生信息
})
</script>

<style scoped>
.profile-page {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-content {
  text-align: center;
}

.avatar-section {
  margin-bottom: 20px;
}

.change-avatar {
  display: block;
  margin: 10px auto 0;
  color: #409eff;
}

.basic-info {
  text-align: left;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.info-item label {
  width: 60px;
  color: #666;
  font-size: 14px;
}

.info-item span {
  color: #333;
  font-weight: 500;
}

.stats-card {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
}

.edit-actions {
  display: flex;
  gap: 10px;
}

.profile-form {
  margin-top: 20px;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}
</style>