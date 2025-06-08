<template>
  <div class="online-exam">
    <!-- 考试信息头部 -->
    <div class="exam-header">
      <div class="exam-info">
        <h2>{{ examInfo.examName }}</h2>
        <div class="exam-meta">
          <span>课程：{{ examInfo.courseName }}</span>
          <span>总分：{{ examInfo.totalScore }}分</span>
          <span>时长：{{ examInfo.durationMinutes }}分钟</span>
        </div>
      </div>
      <div class="exam-timer">
        <div class="timer-display">
          <el-icon><Timer /></el-icon>
          <span class="time-text">{{ formatTime(remainingTime) }}</span>
        </div>
        <div class="timer-status" :class="getTimerStatusClass()">
          {{ getTimerStatusText() }}
        </div>
      </div>
    </div>

    <!-- 考试状态栏 -->
    <div class="exam-status-bar">
      <div class="progress-info">
        <span>已答题：{{ answeredCount }} / {{ totalQuestions }}</span>
        <el-progress 
          :percentage="progressPercentage" 
          :stroke-width="8"
          :show-text="false"
        />
      </div>
      <div class="exam-actions">
        <el-button 
          type="info" 
          size="small" 
          @click="showQuestionNav = !showQuestionNav"
        >
          题目导航
        </el-button>
        <el-button 
          type="warning" 
          size="small" 
          @click="saveAnswers"
          :loading="saving"
        >
          保存答案
        </el-button>
        <el-button 
          type="danger" 
          size="small" 
          @click="submitExam"
          :disabled="!canSubmit"
        >
          提交考试
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="exam-content">
      <!-- 题目导航侧边栏 -->
      <div v-if="showQuestionNav" class="question-nav">
        <h4>题目导航</h4>
        <div class="nav-grid">
          <div
            v-for="(question, index) in questions"
            :key="question.id"
            class="nav-item"
            :class="getNavItemClass(index)"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </div>
        </div>
        <div class="nav-legend">
          <div class="legend-item">
            <span class="legend-color answered"></span>
            <span>已答</span>
          </div>
          <div class="legend-item">
            <span class="legend-color current"></span>
            <span>当前</span>
          </div>
          <div class="legend-item">
            <span class="legend-color unanswered"></span>
            <span>未答</span>
          </div>
        </div>
      </div>

      <!-- 题目内容区域 -->
      <div class="question-content" :class="{ 'with-nav': showQuestionNav }">
        <div v-if="currentQuestion" class="question-container">
          <!-- 题目头部 -->
          <div class="question-header">
            <div class="question-number">
              第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ totalQuestions }} 题
            </div>
            <div class="question-type">
              <el-tag :type="getQuestionTypeColor(currentQuestion.questionType)">
                {{ getQuestionTypeText(currentQuestion.questionType) }}
              </el-tag>
            </div>
            <div class="question-score">
              {{ currentQuestion.score }} 分
            </div>
          </div>

          <!-- 题目内容 -->
          <div class="question-body">
            <div class="question-text" v-html="currentQuestion.questionText"></div>
            <div v-if="currentQuestion.questionImage" class="question-image">
              <el-image 
                :src="currentQuestion.questionImage" 
                fit="contain"
                :preview-src-list="[currentQuestion.questionImage]"
              />
            </div>
          </div>

          <!-- 答题区域 -->
          <div class="answer-area">
            <!-- 单选题 -->
            <div v-if="currentQuestion.questionType === 'single_choice'" class="single-choice">
              <el-radio-group 
                v-model="currentAnswer" 
                @change="updateAnswer"
                size="large"
              >
                <div
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="option-item"
                >
                  <el-radio :label="option.key">
                    <span class="option-content">
                      <span class="option-label">{{ option.key }}.</span>
                      <span class="option-text">{{ option.text }}</span>
                    </span>
                  </el-radio>
                </div>
              </el-radio-group>
            </div>

            <!-- 多选题 -->
            <div v-else-if="currentQuestion.questionType === 'multiple_choice'" class="multiple-choice">
              <el-checkbox-group 
                v-model="currentAnswer" 
                @change="updateAnswer"
                size="large"
              >
                <div
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="option-item"
                >
                  <el-checkbox :label="option.key">
                    <span class="option-content">
                      <span class="option-label">{{ option.key }}.</span>
                      <span class="option-text">{{ option.text }}</span>
                    </span>
                  </el-checkbox>
                </div>
              </el-checkbox-group>
            </div>

            <!-- 判断题 -->
            <div v-else-if="currentQuestion.questionType === 'true_false'" class="true-false">
              <el-radio-group 
                v-model="currentAnswer" 
                @change="updateAnswer"
                size="large"
              >
                <div class="option-item">
                  <el-radio label="true">
                    <span class="option-content">
                      <span class="option-label">A.</span>
                      <span class="option-text">正确</span>
                    </span>
                  </el-radio>
                </div>
                <div class="option-item">
                  <el-radio label="false">
                    <span class="option-content">
                      <span class="option-label">B.</span>
                      <span class="option-text">错误</span>
                    </span>
                  </el-radio>
                </div>
              </el-radio-group>
            </div>

            <!-- 填空题 -->
            <div v-else-if="currentQuestion.questionType === 'fill_blank'" class="fill-blank">
              <div class="blank-inputs">
                <div
                  v-for="(blank, index) in currentQuestion.blanks"
                  :key="index"
                  class="blank-item"
                >
                  <label>第{{ index + 1 }}空：</label>
                  <el-input
                    v-model="currentAnswer[index]"
                    @input="updateAnswer"
                    placeholder="请输入答案"
                    clearable
                  />
                </div>
              </div>
            </div>

            <!-- 问答题 -->
            <div v-else-if="currentQuestion.questionType === 'essay'" class="essay">
              <el-input
                v-model="currentAnswer"
                @input="updateAnswer"
                type="textarea"
                :rows="8"
                placeholder="请输入您的答案..."
                show-word-limit
                :maxlength="2000"
              />
            </div>
          </div>

          <!-- 题目导航按钮 -->
          <div class="question-navigation">
            <el-button 
              :disabled="currentQuestionIndex === 0"
              @click="previousQuestion"
            >
              <el-icon><ArrowLeft /></el-icon>
              上一题
            </el-button>
            <el-button 
              v-if="currentQuestionIndex < totalQuestions - 1"
              type="primary"
              @click="nextQuestion"
            >
              下一题
              <el-icon><ArrowRight /></el-icon>
            </el-button>
            <el-button 
              v-else
              type="success"
              @click="submitExam"
            >
              提交考试
            </el-button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-else class="loading-container">
          <el-skeleton :rows="8" animated />
        </div>
      </div>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交考试"
      width="500px"
      :before-close="handleSubmitDialogClose"
    >
      <div class="submit-confirmation">
        <div class="warning-icon">
          <el-icon size="48" color="#f56c6c"><Warning /></el-icon>
        </div>
        <h3>确认提交考试？</h3>
        <div class="submit-stats">
          <p>已答题：{{ answeredCount }} / {{ totalQuestions }}</p>
          <p>未答题：{{ totalQuestions - answeredCount }}</p>
          <p>剩余时间：{{ formatTime(remainingTime) }}</p>
        </div>
        <p class="submit-warning">
          提交后将无法修改答案，请确认所有题目都已完成。
        </p>
      </div>
      <template #footer>
        <el-button @click="submitDialogVisible = false">继续答题</el-button>
        <el-button 
          type="danger" 
          @click="confirmSubmit"
          :loading="submitting"
        >
          确认提交
        </el-button>
      </template>
    </el-dialog>

    <!-- 防作弊监控 -->
    <anti-cheat-monitor
      v-if="examInfo.antiCheatEnabled"
      :exam-record-id="examRecordId"
      @cheat-warning="handleCheatWarning"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, ArrowLeft, ArrowRight, Warning } from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'
import AntiCheatMonitor from '@/components/AntiCheatMonitor.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const examInfo = ref({})
const questions = ref([])
const answers = reactive({})
const currentQuestionIndex = ref(0)
const remainingTime = ref(0)
const examRecordId = ref(null)
const showQuestionNav = ref(false)
const submitDialogVisible = ref(false)
const saving = ref(false)
const submitting = ref(false)

// 定时器
let timer = null
let autoSaveTimer = null

// 计算属性
const currentQuestion = computed(() => {
  return questions.value[currentQuestionIndex.value]
})

const currentAnswer = computed({
  get() {
    const questionId = currentQuestion.value?.id
    return questionId ? answers[questionId] : null
  },
  set(value) {
    const questionId = currentQuestion.value?.id
    if (questionId) {
      answers[questionId] = value
    }
  }
})

const totalQuestions = computed(() => questions.value.length)

const answeredCount = computed(() => {
  return Object.keys(answers).filter(key => {
    const answer = answers[key]
    if (Array.isArray(answer)) {
      return answer.length > 0
    }
    return answer !== null && answer !== undefined && answer !== ''
  }).length
})

const progressPercentage = computed(() => {
  return totalQuestions.value > 0 ? Math.round((answeredCount.value / totalQuestions.value) * 100) : 0
})

const canSubmit = computed(() => {
  return answeredCount.value > 0
})

// 方法
const loadExamInfo = async () => {
  try {
    const examId = route.params.examId
    const response = await examApi.getStudentExamDetail(examId)
    examInfo.value = response.data
    
    // 检查考试状态
    if (examInfo.value.status !== 'ongoing') {
      ElMessage.error('考试未开始或已结束')
      router.push('/student/exams')
      return
    }
    
    await loadQuestions()
    await startExam()
  } catch (error) {
    ElMessage.error('加载考试信息失败')
    router.push('/student/exams')
  }
}

const loadQuestions = async () => {
  try {
    const response = await examApi.getExamQuestions(examInfo.value.id)
    questions.value = response.data.list
    
    // 初始化答案对象
    questions.value.forEach(question => {
      if (question.questionType === 'multiple_choice' || question.questionType === 'fill_blank') {
        answers[question.id] = []
      } else {
        answers[question.id] = null
      }
    })
  } catch (error) {
    ElMessage.error('加载题目失败')
  }
}

const startExam = async () => {
  try {
    const response = await examApi.startStudentExam(examInfo.value.id)
    examRecordId.value = response.data.id
    remainingTime.value = examInfo.value.durationMinutes * 60
    
    // 启动计时器
    startTimer()
    startAutoSave()
    
    // 加载已保存的答案
    await loadSavedAnswers()
  } catch (error) {
    ElMessage.error('开始考试失败')
  }
}

const loadSavedAnswers = async () => {
  try {
    const response = await examApi.getExamRecord(examRecordId.value)
    if (response.data.answers) {
      const savedAnswers = JSON.parse(response.data.answers)
      Object.assign(answers, savedAnswers)
    }
  } catch (error) {
    console.error('加载已保存答案失败:', error)
  }
}

const startTimer = () => {
  timer = setInterval(() => {
    remainingTime.value--
    
    if (remainingTime.value <= 0) {
      autoSubmitExam()
    }
  }, 1000)
}

const startAutoSave = () => {
  // 每30秒自动保存一次
  autoSaveTimer = setInterval(() => {
    saveAnswers(true)
  }, 30000)
}

const saveAnswers = async (silent = false) => {
  try {
    saving.value = true
    await examApi.saveExamAnswers(examRecordId.value, answers)
    if (!silent) {
      ElMessage.success('答案保存成功')
    }
  } catch (error) {
    if (!silent) {
      ElMessage.error('保存答案失败')
    }
  } finally {
    saving.value = false
  }
}

const updateAnswer = () => {
  // 答案更新时的处理逻辑
  // 可以在这里添加实时保存等功能
}

const goToQuestion = (index) => {
  currentQuestionIndex.value = index
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < totalQuestions.value - 1) {
    currentQuestionIndex.value++
  }
}

const submitExam = () => {
  submitDialogVisible.value = true
}

const confirmSubmit = async () => {
  try {
    submitting.value = true
    await examApi.submitStudentExam(examRecordId.value, answers)
    
    ElMessage.success('考试提交成功')
    
    // 清理定时器
    clearTimers()
    
    // 跳转到结果页面
    router.push(`/student/exam-result/${examRecordId.value}`)
  } catch (error) {
    ElMessage.error('提交考试失败')
  } finally {
    submitting.value = false
    submitDialogVisible.value = false
  }
}

const autoSubmitExam = async () => {
  try {
    await examApi.submitStudentExam(examRecordId.value, answers)
    ElMessage.warning('考试时间已到，系统自动提交')
    
    clearTimers()
    router.push(`/student/exam-result/${examRecordId.value}`)
  } catch (error) {
    ElMessage.error('自动提交失败')
  }
}

const handleSubmitDialogClose = () => {
  submitDialogVisible.value = false
}

const handleCheatWarning = (warningData) => {
  ElMessage.warning(`检测到可疑行为：${warningData.type}`)
}

const clearTimers = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
    autoSaveTimer = null
  }
}

// 工具方法
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getTimerStatusClass = () => {
  if (remainingTime.value <= 300) return 'urgent' // 5分钟
  if (remainingTime.value <= 900) return 'warning' // 15分钟
  return 'normal'
}

const getTimerStatusText = () => {
  if (remainingTime.value <= 300) return '时间紧急'
  if (remainingTime.value <= 900) return '注意时间'
  return '时间充足'
}

const getNavItemClass = (index) => {
  const questionId = questions.value[index]?.id
  const hasAnswer = answers[questionId] !== null && answers[questionId] !== undefined && answers[questionId] !== ''
  
  return {
    'current': index === currentQuestionIndex.value,
    'answered': hasAnswer,
    'unanswered': !hasAnswer
  }
}

const getQuestionTypeColor = (type) => {
  const colorMap = {
    'single_choice': 'primary',
    'multiple_choice': 'success',
    'true_false': 'warning',
    'fill_blank': 'info',
    'essay': 'danger'
  }
  return colorMap[type] || 'info'
}

const getQuestionTypeText = (type) => {
  const textMap = {
    'single_choice': '单选题',
    'multiple_choice': '多选题',
    'true_false': '判断题',
    'fill_blank': '填空题',
    'essay': '问答题'
  }
  return textMap[type] || '未知题型'
}

// 生命周期
onMounted(() => {
  loadExamInfo()
  
  // 防止页面刷新
  window.addEventListener('beforeunload', (e) => {
    e.preventDefault()
    e.returnValue = '确定要离开考试页面吗？未保存的答案将丢失。'
  })
})

onUnmounted(() => {
  clearTimers()
  window.removeEventListener('beforeunload', () => {})
})

// 监听路由变化，防止意外离开
watch(() => route.path, (newPath, oldPath) => {
  if (oldPath.includes('/online-exam/') && !newPath.includes('/exam-result/')) {
    // 如果是从考试页面离开且不是去结果页面，则保存答案
    saveAnswers(true)
  }
})
</script>

<style scoped>
.online-exam {
  min-height: 100vh;
  background: #f5f7fa;
}

.exam-header {
  background: white;
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-info h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.exam-meta {
  display: flex;
  gap: 20px;
  color: #606266;
}

.exam-timer {
  text-align: center;
}

.timer-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.timer-display.urgent {
  color: #f56c6c;
}

.timer-display.warning {
  color: #e6a23c;
}

.timer-display.normal {
  color: #67c23a;
}

.exam-status-bar {
  background: white;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.exam-content {
  display: flex;
  height: calc(100vh - 140px);
}

.question-nav {
  width: 250px;
  background: white;
  border-right: 1px solid #e4e7ed;
  padding: 20px;
  overflow-y: auto;
}

.nav-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  margin-bottom: 20px;
}

.nav-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-item.current {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.nav-item.answered {
  background: #67c23a;
  color: white;
  border-color: #67c23a;
}

.nav-item.unanswered {
  background: #f5f7fa;
  color: #909399;
}

.nav-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 2px;
}

.legend-color.answered {
  background: #67c23a;
}

.legend-color.current {
  background: #409eff;
}

.legend-color.unanswered {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
}

.question-content {
  flex: 1;
  background: white;
  overflow-y: auto;
}

.question-content.with-nav {
  margin-left: 0;
}

.question-container {
  padding: 30px;
  max-width: 900px;
  margin: 0 auto;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.question-number {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.question-score {
  font-size: 16px;
  color: #409eff;
  font-weight: bold;
}

.question-body {
  margin-bottom: 30px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 15px;
}

.question-image {
  text-align: center;
  margin: 20px 0;
}

.answer-area {
  margin-bottom: 40px;
}

.option-item {
  margin-bottom: 15px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.option-content {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.option-label {
  font-weight: bold;
  color: #409eff;
  min-width: 20px;
}

.option-text {
  flex: 1;
  line-height: 1.5;
}

.blank-inputs {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.blank-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.blank-item label {
  min-width: 80px;
  font-weight: bold;
}

.question-navigation {
  display: flex;
  justify-content: space-between;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.loading-container {
  padding: 30px;
}

.submit-confirmation {
  text-align: center;
}

.warning-icon {
  margin-bottom: 20px;
}

.submit-stats {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  margin: 20px 0;
}

.submit-warning {
  color: #f56c6c;
  font-weight: bold;
  margin-top: 15px;
}
</style>
