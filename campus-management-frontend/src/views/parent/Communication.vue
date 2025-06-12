<template>
  <div class="parent-communication">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>家校沟通</h1>
          <p>与老师和学校保持密切联系</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="startNewConversation">
            <el-icon><ChatLineRound /></el-icon>
            发起对话
          </el-button>
          <el-button @click="refreshMessages">
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

    <!-- 主要内容区域 -->
    <div class="main-content-section" v-if="currentChild">
      <el-row :gutter="20">
        <!-- 左侧：对话列表 -->
        <el-col :xs="24" :md="8">
          <el-card class="conversation-list-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <h3>对话列表</h3>
                <el-badge :value="unreadCount" :hidden="unreadCount === 0" type="danger">
                  <el-icon size="20"><Message /></el-icon>
                </el-badge>
              </div>
            </template>

            <div v-loading="loading.conversations" class="conversation-list">
              <div
                v-for="conversation in conversations"
                :key="conversation.id"
                class="conversation-item"
                :class="{ active: currentConversation?.id === conversation.id }"
                @click="selectConversation(conversation)"
              >
                <div class="conversation-avatar">
                  <el-avatar :size="40" :src="conversation.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div v-if="conversation.unreadCount > 0" class="unread-badge">
                    {{ conversation.unreadCount }}
                  </div>
                </div>

                <div class="conversation-content">
                  <div class="conversation-header">
                    <span class="conversation-name">{{ conversation.teacherName }}</span>
                    <span class="conversation-time">{{ formatTime(conversation.lastMessageTime) }}</span>
                  </div>
                  <div class="conversation-preview">
                    <span class="subject">{{ conversation.subject }}</span>
                    <p class="last-message">{{ conversation.lastMessage }}</p>
                  </div>
                </div>
              </div>

              <el-empty v-if="!loading.conversations && conversations.length === 0" description="暂无对话记录">
                <el-button type="primary" @click="startNewConversation">发起对话</el-button>
              </el-empty>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：聊天区域 -->
        <el-col :xs="24" :md="16">
          <el-card class="chat-area-card" shadow="hover">
            <template #header v-if="currentConversation">
              <div class="chat-header">
                <div class="chat-info">
                  <el-avatar :size="32" :src="currentConversation.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="chat-details">
                    <span class="teacher-name">{{ currentConversation.teacherName }}</span>
                    <span class="teacher-role">{{ currentConversation.teacherRole }}</span>
                  </div>
                </div>
                <div class="chat-actions">
                  <el-button size="small" @click="viewTeacherInfo(currentConversation)">
                    <el-icon><InfoFilled /></el-icon>
                    教师信息
                  </el-button>
                </div>
              </div>
            </template>

            <div class="chat-content">
              <!-- 消息列表 -->
              <div v-if="currentConversation" v-loading="loading.messages" class="message-list" ref="messageListRef">
                <div
                  v-for="message in messages"
                  :key="message.id"
                  class="message-item"
                  :class="{ 'own-message': message.senderId === currentUserId }"
                >
                  <div class="message-avatar">
                    <el-avatar :size="32" :src="message.senderAvatar">
                      <el-icon><User /></el-icon>
                    </el-avatar>
                  </div>

                  <div class="message-content">
                    <div class="message-header">
                      <span class="sender-name">{{ message.senderName }}</span>
                      <span class="message-time">{{ formatDateTime(message.sendTime) }}</span>
                    </div>

                    <div class="message-body">
                      <div v-if="message.type === 'text'" class="text-message">
                        {{ message.content }}
                      </div>

                      <div v-else-if="message.type === 'image'" class="image-message">
                        <el-image
                          :src="message.imageUrl"
                          :preview-src-list="[message.imageUrl]"
                          fit="cover"
                          style="max-width: 200px; max-height: 200px;"
                        />
                      </div>

                      <div v-else-if="message.type === 'file'" class="file-message">
                        <div class="file-info" @click="downloadFile(message)">
                          <el-icon><Document /></el-icon>
                          <span>{{ message.fileName }}</span>
                          <span class="file-size">({{ formatFileSize(message.fileSize) }})</span>
                        </div>
                      </div>
                    </div>

                    <div v-if="message.status === 'failed'" class="message-status error">
                      <el-icon><WarningFilled /></el-icon>
                      发送失败
                      <el-button type="text" size="small" @click="resendMessage(message)">重发</el-button>
                    </div>
                  </div>
                </div>

                <div v-if="messages.length === 0 && !loading.messages" class="empty-messages">
                  <p>暂无消息，开始对话吧！</p>
                </div>
              </div>

              <!-- 空状态 -->
              <div v-else class="empty-chat">
                <el-icon :size="60" color="#c0c4cc"><ChatLineRound /></el-icon>
                <h3>选择一个对话开始聊天</h3>
                <p>或者发起新的对话</p>
                <el-button type="primary" @click="startNewConversation">发起对话</el-button>
              </div>
            </div>

            <!-- 消息输入区域 -->
            <div v-if="currentConversation" class="message-input-area">
              <div class="input-toolbar">
                <el-button size="small" @click="selectImage">
                  <el-icon><Picture /></el-icon>
                  图片
                </el-button>
                <el-button size="small" @click="selectFile">
                  <el-icon><Document /></el-icon>
                  文件
                </el-button>
              </div>

              <div class="input-container">
                <el-input
                  v-model="messageInput"
                  type="textarea"
                  :rows="3"
                  placeholder="输入消息..."
                  @keydown.ctrl.enter="sendMessage"
                  @keydown.meta.enter="sendMessage"
                />
                <div class="input-actions">
                  <span class="input-tip">Ctrl+Enter 发送</span>
                  <el-button
                    type="primary"
                    :disabled="!messageInput.trim()"
                    :loading="sending"
                    @click="sendMessage"
                  >
                    发送
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatLineRound,
  Refresh,
  User,
  Message,
  InfoFilled,
  Document,
  Picture,
  WarningFilled
} from '@element-plus/icons-vue'
import { parentApi } from '@/api/parent'

// 响应式数据
const loading = reactive({
  conversations: false,
  messages: false
})

const children = ref([])
const currentChild = ref(null)
const conversations = ref([])
const currentConversation = ref(null)
const messages = ref([])
const messageInput = ref('')
const sending = ref(false)
const messageListRef = ref()

// 当前用户ID（从认证状态获取）
const currentUserId = ref(localStorage.getItem('userId') || '')

// 计算属性
const unreadCount = computed(() => {
  return conversations.value.reduce((total, conv) => total + (conv.unreadCount || 0), 0)
})

// 方法
const loadChildren = async () => {
  try {
    const { data } = await parentApi.getChildren()
    children.value = data

    if (data.length > 0) {
      currentChild.value = data[0]
      await loadConversations()
    }
  } catch (error) {
    console.error('加载子女信息失败:', error)
    ElMessage.error('加载子女信息失败')
  }
}

const loadConversations = async () => {
  if (!currentChild.value) return

  loading.conversations = true

  try {
    const { data } = await parentApi.getMessages({
      childId: currentChild.value.id,
      type: 'conversation'
    })

    conversations.value = data.conversations || []
  } catch (error) {
    console.error('加载对话列表失败:', error)
    ElMessage.error('加载对话列表失败')
    conversations.value = []
  } finally {
    loading.conversations = false
  }
}

const loadMessages = async (conversationId) => {
  loading.messages = true

  try {
    const { data } = await parentApi.getMessages({
      conversationId,
      page: 1,
      size: 50
    })

    messages.value = data.messages || []

    // 标记消息为已读
    await markConversationRead(conversationId)

    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载消息失败:', error)
    ElMessage.error('加载消息失败')
    messages.value = []
  } finally {
    loading.messages = false
  }
}

const switchChild = async (child) => {
  currentChild.value = child
  currentConversation.value = null
  messages.value = []
  await loadConversations()
}

const selectConversation = async (conversation) => {
  currentConversation.value = conversation
  await loadMessages(conversation.id)
}

const startNewConversation = async () => {
  try {
    // 获取教师列表
    const { data } = await parentApi.getTeachers(currentChild.value.id)
    const teachers = data.teachers || []

    if (teachers.length === 0) {
      ElMessage.warning('暂无可联系的教师')
      return
    }

    // 显示教师选择对话框
    const { value: teacherId } = await ElMessageBox.prompt(
      '请选择要联系的教师',
      '发起对话',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'select',
        inputOptions: teachers.reduce((options, teacher) => {
          options[teacher.id] = `${teacher.name} (${teacher.subject})`
          return options
        }, {})
      }
    )

    if (teacherId) {
      // 创建新对话
      const teacher = teachers.find(t => t.id === teacherId)
      const newConversation = {
        id: `new_${Date.now()}`,
        teacherId: teacher.id,
        teacherName: teacher.name,
        teacherRole: teacher.subject,
        avatar: teacher.avatar,
        lastMessage: '',
        lastMessageTime: new Date().toISOString(),
        unreadCount: 0
      }

      conversations.value.unshift(newConversation)
      currentConversation.value = newConversation
      messages.value = []
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发起对话失败:', error)
      ElMessage.error('发起对话失败')
    }
  }
}

const sendMessage = async () => {
  if (!messageInput.value.trim() || !currentConversation.value) return

  const messageContent = messageInput.value.trim()
  messageInput.value = ''
  sending.value = true

  // 添加临时消息到列表
  const tempMessage = {
    id: `temp_${Date.now()}`,
    content: messageContent,
    type: 'text',
    senderId: currentUserId.value,
    senderName: '我',
    senderAvatar: '',
    sendTime: new Date().toISOString(),
    status: 'sending'
  }

  messages.value.push(tempMessage)
  scrollToBottom()

  try {
    const { data } = await parentApi.sendMessage({
      conversationId: currentConversation.value.id,
      receiverId: currentConversation.value.teacherId,
      content: messageContent,
      type: 'text',
      childId: currentChild.value.id
    })

    // 更新临时消息
    const index = messages.value.findIndex(m => m.id === tempMessage.id)
    if (index > -1) {
      messages.value[index] = {
        ...data.message,
        status: 'sent'
      }
    }

    // 更新对话列表
    const convIndex = conversations.value.findIndex(c => c.id === currentConversation.value.id)
    if (convIndex > -1) {
      conversations.value[convIndex].lastMessage = messageContent
      conversations.value[convIndex].lastMessageTime = new Date().toISOString()
    }

  } catch (error) {
    console.error('发送消息失败:', error)

    // 标记消息发送失败
    const index = messages.value.findIndex(m => m.id === tempMessage.id)
    if (index > -1) {
      messages.value[index].status = 'failed'
    }

    ElMessage.error('消息发送失败')
  } finally {
    sending.value = false
  }
}

const markConversationRead = async (conversationId) => {
  try {
    await parentApi.markMessageRead(conversationId)

    // 更新本地未读计数
    const conversation = conversations.value.find(c => c.id === conversationId)
    if (conversation) {
      conversation.unreadCount = 0
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const refreshMessages = async () => {
  await loadConversations()
  if (currentConversation.value) {
    await loadMessages(currentConversation.value.id)
  }
  ElMessage.success('消息刷新成功')
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`

  return date.toLocaleDateString('zh-CN')
}

const formatDateTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const formatFileSize = (size) => {
  if (!size) return '0B'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  while (size >= 1024 && index < units.length - 1) {
    size /= 1024
    index++
  }
  return `${size.toFixed(1)}${units[index]}`
}

// 生命周期
onMounted(async () => {
  await loadChildren()
})
</script>

<style scoped>
@import '@/styles/parent.css';

.parent-communication {
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

.child-selector-section {
  margin-bottom: 20px;
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
  padding: 12px 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.child-tab:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.child-tab.active {
  border-color: #409eff;
  background: #ecf5ff;
}

.child-info {
  display: flex;
  flex-direction: column;
}

.child-info .name {
  font-weight: 600;
  color: #303133;
}

.child-info .class {
  font-size: 12px;
  color: #909399;
}

.main-content-section {
  margin-bottom: 20px;
}

.conversation-list-card,
.chat-area-card {
  height: 600px;
  display: flex;
  flex-direction: column;
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

.conversation-list {
  flex: 1;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.conversation-item:hover {
  background-color: #f8f9fa;
}

.conversation-item.active {
  background-color: #ecf5ff;
}

.conversation-avatar {
  position: relative;
}

.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conversation-name {
  font-weight: 600;
  color: #303133;
}

.conversation-time {
  font-size: 12px;
  color: #909399;
}

.conversation-preview .subject {
  font-size: 12px;
  color: #409eff;
  background: #ecf5ff;
  padding: 2px 6px;
  border-radius: 4px;
  margin-right: 8px;
}

.conversation-preview .last-message {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-details {
  display: flex;
  flex-direction: column;
}

.teacher-name {
  font-weight: 600;
  color: #303133;
}

.teacher-role {
  font-size: 12px;
  color: #909399;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.message-item.own-message {
  flex-direction: row-reverse;
}

.message-item.own-message .message-content {
  align-items: flex-end;
}

.message-item.own-message .message-body {
  background: #409eff;
  color: white;
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message-header {
  display: flex;
  gap: 8px;
  margin-bottom: 4px;
  font-size: 12px;
  color: #909399;
}

.message-body {
  background: white;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.text-message {
  line-height: 1.6;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #409eff;
}

.file-info:hover {
  text-decoration: underline;
}

.file-size {
  color: #909399;
  font-size: 12px;
}

.message-status.error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.empty-messages,
.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-chat h3 {
  margin: 16px 0 8px 0;
  color: #606266;
}

.message-input-area {
  border-top: 1px solid #e4e7ed;
  padding: 16px;
  background: white;
}

.input-toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}

.input-container {
  position: relative;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.input-tip {
  font-size: 12px;
  color: #909399;
}

@media (max-width: 768px) {
  .parent-communication {
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

  .conversation-list-card,
  .chat-area-card {
    height: 500px;
  }

  .message-item {
    margin-bottom: 12px;
  }

  .message-content {
    max-width: 85%;
  }
}
</style>