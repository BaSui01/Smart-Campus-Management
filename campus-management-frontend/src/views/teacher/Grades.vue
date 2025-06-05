<template>
  <div class="teacher-grades">
    <div class="page-header">
      <h1>成绩录入</h1>
      <p>录入和管理学生成绩</p>
    </div>
    
    <!-- 操作工具栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="5">
          <el-select v-model="selectedCourse" placeholder="选择课程" @change="handleCourseChange">
            <el-option
              v-for="course in teacherCourses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedExamType" placeholder="考试类型">
            <el-option label="期中考试" value="midterm" />
            <el-option label="期末考试" value="final" />
            <el-option label="平时作业" value="homework" />
            <el-option label="实验成绩" value="lab" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input
            v-model="searchQuery"
            placeholder="搜索学生姓名或学号"
            clearable
            :prefix-icon="Search"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="batchInputGrades">
            <el-icon><Edit /></el-icon>
            批量录入
          </el-button>
          <el-button type="success" @click="showImportDialog">
            <el-icon><Upload /></el-icon>
            导入成绩
          </el-button>
        </el-col>
        <el-col :span="5">
          <el-button @click="exportGrades">
            <el-icon><Download /></el-icon>
            导出成绩
          </el-button>
          <el-button type="warning" @click="publishGrades">
            <el-icon><Bell /></el-icon>
            发布成绩
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 成绩统计 -->
    <el-row :gutter="20" class="stats-section" v-if="selectedCourse">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ gradeStats.totalStudents }}</div>
            <div class="stat-label">总学生数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ gradeStats.gradedCount }}</div>
            <div class="stat-label">已录成绩</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ gradeStats.avgScore }}</div>
            <div class="stat-label">平均分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ gradeStats.passRate }}%</div>
            <div class="stat-label">及格率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 学生成绩表格 -->
    <el-card class="grades-table-card" v-if="selectedCourse">
      <template #header>
        <div class="card-header">
          <h3>学生成绩列表</h3>
          <div class="header-actions">
            <el-button 
              size="small" 
              type="success" 
              :disabled="!hasUnsavedChanges"
              @click="saveAllGrades"
            >
              保存所有更改
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table
        :data="filteredStudentGrades"
        style="width: 100%"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        
        <el-table-column prop="studentId" label="学号" width="120" />
        
        <el-table-column prop="className" label="班级" width="100" />
        
        <el-table-column label="成绩录入" width="150" align="center">
          <template #default="{ row }">
            <el-input-number
              v-model="row.score"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              @change="handleScoreChange(row)"
            />
          </template>
        </el-table-column>
        
        <el-table-column prop="grade" label="等级" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.score !== null" :type="getGradeType(row.grade)">
              {{ row.grade }}
            </el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="gpa" label="绩点" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.gpa !== null">{{ row.gpa }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="submitTime" label="提交时间" width="150" align="center">
          <template #default="{ row }">
            <span v-if="row.submitTime" class="text-success">
              {{ row.submitTime }}
            </span>
            <span v-else class="text-warning">未提交</span>
          </template>
        </el-table-column>
        
        <el-table-column label="备注" min-width="120">
          <template #default="{ row }">
            <el-input
              v-model="row.remark"
              placeholder="添加备注"
              size="small"
              @change="handleRemarkChange(row)"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              :disabled="!row.changed"
              @click="saveGrade(row)"
            >
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
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
    
    <!-- 选择课程提示 -->
    <el-empty v-else description="请先选择要录入成绩的课程" />
    
    <!-- 批量录入对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量录入成绩"
      width="600px"
    >
      <div class="batch-input">
        <el-form :model="batchForm" label-width="100px">
          <el-form-item label="统一成绩">
            <el-input-number
              v-model="batchForm.score"
              :min="0"
              :max="100"
              :precision="1"
              placeholder="输入统一成绩"
              style="width: 200px"
            />
            <el-button 
              type="primary" 
              @click="applyBatchScore"
              style="margin-left: 10px"
            >
              应用到所有未录入学生
            </el-button>
          </el-form-item>
          
          <el-form-item label="成绩分布">
            <div class="score-distribution">
              <el-button @click="setScoreRange(90, 100)">优秀 (90-100)</el-button>
              <el-button @click="setScoreRange(80, 89)">良好 (80-89)</el-button>
              <el-button @click="setScoreRange(70, 79)">中等 (70-79)</el-button>
              <el-button @click="setScoreRange(60, 69)">及格 (60-69)</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchInput">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 导入成绩对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入成绩"
      width="500px"
    >
      <div class="import-content">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          accept=".xlsx,.xls,.csv"
          drag
        >
          <el-icon class="el-icon--upload"><Upload /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传 Excel 或 CSV 文件，且不超过 10MB
            </div>
          </template>
        </el-upload>
        
        <div class="template-download">
          <el-button type="text" @click="downloadTemplate">
            下载导入模板
          </el-button>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmImport">确定导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Edit, 
  Upload, 
  Download, 
  Bell 
} from '@element-plus/icons-vue'

const selectedCourse = ref('')
const selectedExamType = ref('final')
const searchQuery = ref('')
const batchDialogVisible = ref(false)
const importDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const uploadRef = ref()

const teacherCourses = ref([
  { id: 1, name: '数据结构', code: 'CS301' },
  { id: 2, name: '算法设计', code: 'CS401' },
  { id: 3, name: '数据库原理', code: 'CS501' }
])

const gradeStats = ref({
  totalStudents: 0,
  gradedCount: 0,
  avgScore: 0,
  passRate: 0
})

const studentGrades = ref([])

const batchForm = reactive({
  score: null
})

// 模拟数据
const mockStudentGrades = [
  {
    id: 1,
    studentId: '2021001001',
    studentName: '张三',
    className: '计科21-1',
    score: 92,
    grade: 'A',
    gpa: 4.0,
    submitTime: '2024-06-01 10:30',
    remark: '优秀',
    status: '已录入',
    changed: false
  },
  {
    id: 2,
    studentId: '2021001002',
    studentName: '李四',
    className: '计科21-1',
    score: null,
    grade: null,
    gpa: null,
    submitTime: null,
    remark: '',
    status: '未录入',
    changed: false
  },
  {
    id: 3,
    studentId: '2021001003',
    studentName: '王五',
    className: '计科21-1',
    score: 85,
    grade: 'B',
    gpa: 3.0,
    submitTime: '2024-06-01 11:15',
    remark: '良好',
    status: '已录入',
    changed: false
  }
]

const filteredStudentGrades = computed(() => {
  return studentGrades.value.filter(student => {
    const matchesSearch = !searchQuery.value || 
      student.studentName.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      student.studentId.includes(searchQuery.value)
    
    return matchesSearch
  })
})

const hasUnsavedChanges = computed(() => {
  return studentGrades.value.some(student => student.changed)
})

const calculateGrade = (score) => {
  if (score >= 90) return 'A'
  if (score >= 80) return 'B'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

const calculateGPA = (score) => {
  if (score >= 90) return 4.0
  if (score >= 80) return 3.0
  if (score >= 70) return 2.0
  if (score >= 60) return 1.0
  return 0.0
}

const getGradeType = (grade) => {
  const gradeMap = {
    'A': 'success',
    'B': 'primary',
    'C': 'warning',
    'D': 'warning',
    'F': 'danger'
  }
  return gradeMap[grade] || 'info'
}

const getStatusType = (status) => {
  const statusMap = {
    '已录入': 'success',
    '未录入': 'warning',
    '已发布': 'info'
  }
  return statusMap[status] || 'info'
}

const getRowClassName = ({ row }) => {
  if (row.changed) return 'changed-row'
  if (row.status === '未录入') return 'ungraded-row'
  return ''
}

const handleCourseChange = (courseId) => {
  if (courseId) {
    // 加载该课程的学生成绩数据
    studentGrades.value = [...mockStudentGrades]
    calculateStats()
    total.value = studentGrades.value.length
  } else {
    studentGrades.value = []
  }
}

const handleScoreChange = (row) => {
  if (row.score !== null) {
    row.grade = calculateGrade(row.score)
    row.gpa = calculateGPA(row.score)
    row.status = '已录入'
  } else {
    row.grade = null
    row.gpa = null
    row.status = '未录入'
  }
  row.changed = true
  calculateStats()
}

const handleRemarkChange = (row) => {
  row.changed = true
}

const calculateStats = () => {
  const total = studentGrades.value.length
  const graded = studentGrades.value.filter(s => s.score !== null)
  const totalScore = graded.reduce((sum, s) => sum + s.score, 0)
  const avgScore = graded.length > 0 ? (totalScore / graded.length).toFixed(1) : 0
  const passCount = graded.filter(s => s.score >= 60).length
  const passRate = graded.length > 0 ? ((passCount / graded.length) * 100).toFixed(1) : 0
  
  gradeStats.value = {
    totalStudents: total,
    gradedCount: graded.length,
    avgScore: avgScore,
    passRate: passRate
  }
}

const saveGrade = async (row) => {
  try {
    // 调用API保存单个成绩
    row.submitTime = new Date().toLocaleString()
    row.changed = false
    ElMessage.success(`${row.studentName} 的成绩保存成功`)
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

const saveAllGrades = async () => {
  try {
    const changedRows = studentGrades.value.filter(row => row.changed)
    
    // 批量保存
    for (const row of changedRows) {
      row.submitTime = new Date().toLocaleString()
      row.changed = false
    }
    
    ElMessage.success(`已保存 ${changedRows.length} 条成绩记录`)
  } catch (error) {
    ElMessage.error('批量保存失败')
  }
}

const batchInputGrades = () => {
  batchDialogVisible.value = true
}

const applyBatchScore = () => {
  if (batchForm.score === null) {
    ElMessage.warning('请输入成绩')
    return
  }
  
  studentGrades.value.forEach(row => {
    if (row.status === '未录入') {
      row.score = batchForm.score
      handleScoreChange(row)
    }
  })
  
  ElMessage.success('批量成绩应用成功')
}

const setScoreRange = (min, max) => {
  const ungraded = studentGrades.value.filter(row => row.status === '未录入')
  
  ungraded.forEach(row => {
    row.score = Math.floor(Math.random() * (max - min + 1)) + min
    handleScoreChange(row)
  })
  
  ElMessage.success(`已为未录入学生设置 ${min}-${max} 分数范围`)
}

const confirmBatchInput = () => {
  batchDialogVisible.value = false
  ElMessage.success('批量录入完成')
}

const showImportDialog = () => {
  importDialogVisible.value = true
}

const handleFileChange = (file) => {
  console.log('选择文件:', file)
}

const downloadTemplate = () => {
  ElMessage.info('模板下载功能开发中...')
}

const confirmImport = () => {
  ElMessage.info('导入功能开发中...')
  importDialogVisible.value = false
}

const exportGrades = () => {
  ElMessage.info('导出功能开发中...')
}

const publishGrades = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要发布当前课程的所有成绩吗？发布后学生将能看到成绩。',
      '确认发布',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 更新状态为已发布
    studentGrades.value.forEach(row => {
      if (row.status === '已录入') {
        row.status = '已发布'
      }
    })
    
    ElMessage.success('成绩发布成功')
  } catch {
    // 用户取消
  }
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
@import '@/styles/global.css';

.teacher-grades {
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

.toolbar-card {
  margin-bottom: 20px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  height: 100px;
}

.stat-content {
  padding: 20px 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.grades-table-card {
  margin-bottom: 30px;
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

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格行样式 */
:deep(.changed-row) {
  background-color: #fff9e6;
}

:deep(.ungraded-row) {
  background-color: #fef2f2;
}

.batch-input {
  padding: 20px 0;
}

.score-distribution {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.import-content {
  padding: 20px 0;
}

.template-download {
  text-align: center;
  margin-top: 20px;
}

.text-success {
  color: #67c23a;
}

.text-warning {
  color: #e6a23c;
}

.text-muted {
  color: #999;
}
</style>