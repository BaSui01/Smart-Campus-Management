<template>
  <div class="parent-children">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>子女管理</h1>
          <p>管理和查看您的子女信息</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="addChild">
            <el-icon><Plus /></el-icon>
            添加子女
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 子女列表 -->
    <div class="children-content">
      <div v-loading="loading" class="children-grid">
        <div
          v-for="child in children"
          :key="child.id"
          class="child-card"
          @click="viewChildDetail(child)"
        >
          <!-- 子女头像和基本信息 -->
          <div class="child-header">
            <div class="child-avatar">
              <el-avatar :size="80" :src="child.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="status-indicator" :class="child.status">
                <el-icon>
                  <component :is="getStatusIcon(child.status)" />
                </el-icon>
              </div>
            </div>
            <div class="child-basic-info">
              <h3>{{ child.name }}</h3>
              <p class="student-id">学号: {{ child.studentId }}</p>
              <p class="class-info">{{ child.grade }} {{ child.className }}</p>
              <el-tag :type="getStatusType(child.status)" size="small">
                {{ getStatusText(child.status) }}
              </el-tag>
            </div>
          </div>

          <!-- 子女详细信息 -->
          <div class="child-details">
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">出生日期</span>
                <span class="value">{{ formatDate(child.birthDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">性别</span>
                <span class="value">{{ child.gender === 'male' ? '男' : '女' }}</span>
              </div>
            </div>
            
            <div class="detail-row">
              <div class="detail-item">
                <span class="label">班主任</span>
                <span class="value">{{ child.classTeacher }}</span>
              </div>
              <div class="detail-item">
                <span class="label">联系电话</span>
                <span class="value">{{ child.teacherPhone }}</span>
              </div>
            </div>

            <div class="detail-row">
              <div class="detail-item">
                <span class="label">入学时间</span>
                <span class="value">{{ formatDate(child.enrollmentDate) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">关系</span>
                <span class="value">{{ getRelationshipText(child.relationship) }}</span>
              </div>
            </div>
          </div>

          <!-- 最近表现统计 -->
          <div class="child-stats">
            <div class="stat-item">
              <div class="stat-value">{{ child.stats.averageGrade }}</div>
              <div class="stat-label">平均成绩</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ child.stats.attendanceRate }}%</div>
              <div class="stat-label">出勤率</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ child.stats.homeworkRate }}%</div>
              <div class="stat-label">作业完成率</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ child.stats.classRank }}</div>
              <div class="stat-label">班级排名</div>
            </div>
          </div>

          <!-- 快捷操作 -->
          <div class="child-actions">
            <el-button size="small" type="primary" @click.stop="viewGrades(child)">
              <el-icon><TrendCharts /></el-icon>
              查看成绩
            </el-button>
            <el-button size="small" @click.stop="viewAttendance(child)">
              <el-icon><Calendar /></el-icon>
              出勤记录
            </el-button>
            <el-button size="small" @click.stop="contactTeacher(child)">
              <el-icon><ChatLineRound /></el-icon>
              联系老师
            </el-button>
            <el-dropdown @click.stop="" trigger="click">
              <el-button size="small">
                更多
                <el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="editChild(child)">
                    <el-icon><Edit /></el-icon>
                    编辑信息
                  </el-dropdown-item>
                  <el-dropdown-item @click="viewSchedule(child)">
                    <el-icon><Clock /></el-icon>
                    课程表
                  </el-dropdown-item>
                  <el-dropdown-item @click="applyLeave(child)">
                    <el-icon><Document /></el-icon>
                    申请请假
                  </el-dropdown-item>
                  <el-dropdown-item @click="viewHealthRecords(child)">
                    <el-icon><FirstAidKit /></el-icon>
                    健康记录
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="unbindChild(child)">
                    <el-icon><Delete /></el-icon>
                    解除绑定
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 添加子女卡片 -->
        <div class="add-child-card" @click="addChild">
          <div class="add-child-content">
            <el-icon :size="48" color="#c0c4cc">
              <Plus />
            </el-icon>
            <p>添加子女</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && children.length === 0" description="暂无子女信息">
        <el-button type="primary" @click="addChild">添加子女</el-button>
      </el-empty>
    </div>

    <!-- 添加/编辑子女对话框 -->
    <el-dialog
      v-model="childDialogVisible"
      :title="isEditMode ? '编辑子女信息' : '添加子女'"
      width="600px"
      :before-close="handleCloseDialog"
    >
      <el-form
        ref="childFormRef"
        :model="childForm"
        :rules="childRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="childForm.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentId">
              <el-input v-model="childForm.studentId" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="childForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关系" prop="relationship">
              <el-select v-model="childForm.relationship" placeholder="请选择关系">
                <el-option label="父亲" value="father" />
                <el-option label="母亲" value="mother" />
                <el-option label="监护人" value="guardian" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="childForm.gender">
                <el-radio label="male">男</el-radio>
                <el-radio label="female">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="childForm.birthDate"
                type="date"
                placeholder="选择出生日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="联系地址" prop="address">
          <el-input
            v-model="childForm.address"
            type="textarea"
            :rows="3"
            placeholder="请输入联系地址"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="childForm.remark"
            type="textarea"
            :rows="2"
            placeholder="其他备注信息"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="childDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="submitLoading"
            @click="handleSubmit"
          >
            {{ isEditMode ? '保存' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 子女详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedChild?.name} - 详细信息`"
      width="800px"
    >
      <div v-if="selectedChild" class="child-detail-content">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">
              {{ selectedChild.name }}
            </el-descriptions-item>
            <el-descriptions-item label="学号">
              {{ selectedChild.studentId }}
            </el-descriptions-item>
            <el-descriptions-item label="性别">
              {{ selectedChild.gender === 'male' ? '男' : '女' }}
            </el-descriptions-item>
            <el-descriptions-item label="出生日期">
              {{ formatDate(selectedChild.birthDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="年级班级">
              {{ selectedChild.grade }} {{ selectedChild.className }}
            </el-descriptions-item>
            <el-descriptions-item label="入学时间">
              {{ formatDate(selectedChild.enrollmentDate) }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(selectedChild.status)">
                {{ getStatusText(selectedChild.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="亲属关系">
              {{ getRelationshipText(selectedChild.relationship) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 学习情况 -->
        <div class="detail-section">
          <h4>学习情况</h4>
          <div class="performance-grid">
            <div class="performance-item">
              <div class="performance-value">{{ selectedChild.stats.averageGrade }}</div>
              <div class="performance-label">平均成绩</div>
            </div>
            <div class="performance-item">
              <div class="performance-value">{{ selectedChild.stats.classRank }}</div>
              <div class="performance-label">班级排名</div>
            </div>
            <div class="performance-item">
              <div class="performance-value">{{ selectedChild.stats.attendanceRate }}%</div>
              <div class="performance-label">出勤率</div>
            </div>
            <div class="performance-item">
              <div class="performance-value">{{ selectedChild.stats.homeworkRate }}%</div>
              <div class="performance-label">作业完成率</div>
            </div>
          </div>
        </div>

        <!-- 联系信息 -->
        <div class="detail-section">
          <h4>班级联系信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="班主任">
              {{ selectedChild.classTeacher }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ selectedChild.teacherPhone }}
            </el-descriptions-item>
            <el-descriptions-item label="办公室">
              {{ selectedChild.teacherOffice }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ selectedChild.teacherEmail }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 最近活动 -->
        <div class="detail-section" v-if="selectedChild.recentActivities?.length">
          <h4>最近活动</h4>
          <el-timeline>
            <el-timeline-item
              v-for="activity in selectedChild.recentActivities"
              :key="activity.id"
              :timestamp="activity.time"
              :type="getActivityType(activity.type)"
            >
              {{ activity.description }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="editChild(selectedChild)">
            编辑信息
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  User,
  TrendCharts,
  Calendar,
  ChatLineRound,
  ArrowDown,
  Edit,
  Clock,
  Document,
  FirstAidKit,
  Delete
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const children = ref([])

const childDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEditMode = ref(false)
const selectedChild = ref(null)

const childFormRef = ref()
const childForm = reactive({
  id: null,
  name: '',
  studentId: '',
  idCard: '',
  relationship: '',
  gender: '',
  birthDate: '',
  address: '',
  remark: ''
})

// 验证规则
const childRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { 
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, 
      message: '请输入正确的身份证号', 
      trigger: 'blur' 
    }
  ],
  relationship: [
    { required: true, message: '请选择关系', trigger: 'change' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ]
}

// 方法
const loadChildren = async () => {
  loading.value = true
  
  try {
    const { data } = await parentApi.getChildren()
    children.value = data
  } catch (error) {
    console.error('加载子女信息失败:', error)
    ElMessage.error('加载子女信息失败')
    children.value = []
  } finally {
    loading.value = false
  }
}

const addChild = () => {
  isEditMode.value = false
  resetForm()
  childDialogVisible.value = true
}

const editChild = (child) => {
  isEditMode.value = true
  Object.assign(childForm, {
    id: child.id,
    name: child.name,
    studentId: child.studentId,
    idCard: child.idCard,
    relationship: child.relationship,
    gender: child.gender,
    birthDate: child.birthDate,
    address: child.address,
    remark: child.remark
  })
  childDialogVisible.value = true
  detailDialogVisible.value = false
}

const handleSubmit = async () => {
  try {
    await childFormRef.value.validate()
    submitLoading.value = true
    
    if (isEditMode.value) {
      await parentApi.updateChildInfo(childForm.id, childForm)
      ElMessage.success('更新子女信息成功')
    } else {
      await parentApi.bindChild(childForm)
      ElMessage.success('添加子女成功')
    }
    
    childDialogVisible.value = false
    await loadChildren()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const viewChildDetail = async (child) => {
  try {
    const { data } = await parentApi.getChildDetail(child.id)
    selectedChild.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载子女详情失败:', error)
    ElMessage.error('加载子女详情失败')
  }
}

const unbindChild = async (child) => {
  try {
    await ElMessageBox.confirm(
      `确定要解除与 ${child.name} 的绑定关系吗？此操作不可恢复。`,
      '确认解绑',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await parentApi.unbindChild(child.id)
    ElMessage.success('解绑成功')
    await loadChildren()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解绑失败:', error)
      ElMessage.error(error.message || '解绑失败')
    }
  }
}

const viewGrades = (child) => {
  router.push({
    path: '/parent/grades',
    query: { childId: child.id }
  })
}

const viewAttendance = (child) => {
  router.push({
    path: '/parent/attendance',
    query: { childId: child.id }
  })
}

const contactTeacher = (child) => {
  router.push({
    path: '/parent/communication',
    query: { childId: child.id, teacherId: child.teacherId }
  })
}

const viewSchedule = (child) => {
  ElMessage.info('课程表功能开发中...')
}

const applyLeave = (child) => {
  ElMessage.info('请假申请功能开发中...')
}

const viewHealthRecords = (child) => {
  ElMessage.info('健康记录功能开发中...')
}

const refreshData = () => {
  loadChildren()
  ElMessage.success('数据刷新成功')
}

const handleCloseDialog = () => {
  childDialogVisible.value = false
  resetForm()
}

const resetForm = () => {
  Object.assign(childForm, {
    id: null,
    name: '',
    studentId: '',
    idCard: '',
    relationship: '',
    gender: '',
    birthDate: '',
    address: '',
    remark: ''
  })
  childFormRef.value?.clearValidate()
}

// 辅助方法
const getStatusIcon = (status) => {
  const iconMap = {
    'active': 'CircleCheck',
    'inactive': 'CircleClose',
    'graduated': 'Trophy'
  }
  return iconMap[status] || 'User'
}

const getStatusType = (status) => {
  const typeMap = {
    'active': 'success',
    'inactive': 'danger',
    'graduated': 'warning'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'active': '在校',
    'inactive': '离校',
    'graduated': '毕业'
  }
  return textMap[status] || status
}

const getRelationshipText = (relationship) => {
  const textMap = {
    'father': '父亲',
    'mother': '母亲',
    'guardian': '监护人',
    'other': '其他'
  }
  return textMap[relationship] || relationship
}

const getActivityType = (type) => {
  const typeMap = {
    'grade': 'success',
    'attendance': 'primary',
    'homework': 'warning',
    'exam': 'danger'
  }
  return typeMap[type] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadChildren()
})
</script>

<style scoped>
@import '@/styles/parent.css';
</style>