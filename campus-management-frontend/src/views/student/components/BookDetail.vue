<template>
  <div class="book-detail">
    <div class="book-header">
      <div class="book-cover">
        <el-image 
          :src="book.coverUrl || '/images/default-book-cover.jpg'" 
          fit="cover"
          :alt="book.title"
        >
          <template #error>
            <div class="image-slot">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
      </div>
      
      <div class="book-info">
        <h2>{{ book.title }}</h2>
        <div class="book-meta">
          <div class="meta-item">
            <label>作者：</label>
            <span>{{ book.author }}</span>
          </div>
          <div class="meta-item">
            <label>ISBN：</label>
            <span>{{ book.isbn }}</span>
          </div>
          <div class="meta-item">
            <label>出版社：</label>
            <span>{{ book.publisher }}</span>
          </div>
          <div class="meta-item">
            <label>出版日期：</label>
            <span>{{ book.publishDate }}</span>
          </div>
          <div class="meta-item">
            <label>分类：</label>
            <span>{{ book.category }}</span>
          </div>
          <div class="meta-item">
            <label>位置：</label>
            <span>{{ book.location }}</span>
          </div>
        </div>
        
        <div class="book-status">
          <div class="status-info">
            <el-tag :type="getStatusType(book.status)" size="large">
              {{ getStatusText(book.status) }}
            </el-tag>
            <span class="copies-info">
              可借：{{ book.availableCopies }}/{{ book.totalCopies }}
            </span>
          </div>
          
          <div class="rating-info" v-if="book.rating > 0">
            <el-rate 
              v-model="book.rating" 
              disabled 
              show-score 
              text-color="#ff9900"
              score-template="{value} 分"
            />
            <span class="review-count">({{ book.reviewCount }} 人评价)</span>
          </div>
        </div>
        
        <div class="book-actions">
          <el-button 
            v-if="book.status === 'available'" 
            type="primary" 
            size="large"
            @click="$emit('borrow', book)"
          >
            借阅图书
          </el-button>
          <el-button 
            v-else-if="book.status === 'borrowed'" 
            type="warning" 
            size="large"
            @click="$emit('reserve', book)"
          >
            预约图书
          </el-button>
          <el-button 
            size="large"
            @click="toggleFavorite"
            :loading="favoriteLoading"
          >
            <el-icon><Star /></el-icon>
            {{ isFavorite ? '取消收藏' : '收藏图书' }}
          </el-button>
        </div>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="book-tabs">
      <!-- 图书简介 -->
      <el-tab-pane label="图书简介" name="description">
        <div class="book-description">
          <p v-if="book.description">{{ book.description }}</p>
          <p v-else class="no-description">暂无图书简介</p>
        </div>
      </el-tab-pane>

      <!-- 读者评价 -->
      <el-tab-pane label="读者评价" name="reviews">
        <div class="reviews-section">
          <!-- 评价统计 -->
          <div class="review-stats" v-if="reviewStats">
            <div class="stats-overview">
              <div class="average-rating">
                <span class="rating-number">{{ reviewStats.averageRating }}</span>
                <el-rate 
                  v-model="reviewStats.averageRating" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                />
                <span class="total-reviews">共 {{ reviewStats.totalReviews }} 条评价</span>
              </div>
              
              <div class="rating-distribution">
                <div 
                  v-for="(count, rating) in reviewStats.ratingDistribution" 
                  :key="rating"
                  class="rating-bar"
                >
                  <span class="rating-label">{{ rating }}星</span>
                  <el-progress 
                    :percentage="(count / reviewStats.totalReviews) * 100"
                    :show-text="false"
                    :stroke-width="8"
                  />
                  <span class="rating-count">{{ count }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的评价 -->
          <div class="my-review" v-if="!myReview">
            <h4>写评价</h4>
            <el-form :model="reviewForm" class="review-form">
              <el-form-item label="评分">
                <el-rate v-model="reviewForm.rating" show-text />
              </el-form-item>
              <el-form-item label="评价">
                <el-input
                  v-model="reviewForm.comment"
                  type="textarea"
                  :rows="4"
                  placeholder="分享您的阅读感受..."
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item>
                <el-button 
                  type="primary" 
                  @click="submitReview"
                  :loading="reviewSubmitting"
                >
                  提交评价
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 评价列表 -->
          <div class="reviews-list">
            <div 
              v-for="review in reviews" 
              :key="review.id"
              class="review-item"
            >
              <div class="review-header">
                <div class="reviewer-info">
                  <span class="reviewer-name">{{ review.reviewerName || '匿名用户' }}</span>
                  <el-rate 
                    v-model="review.rating" 
                    disabled 
                    size="small"
                  />
                </div>
                <span class="review-date">{{ formatDate(review.createTime) }}</span>
              </div>
              <div class="review-content">
                <p>{{ review.comment }}</p>
              </div>
            </div>
            
            <el-empty v-if="reviews.length === 0" description="暂无评价" />
          </div>
        </div>
      </el-tab-pane>

      <!-- 相关推荐 -->
      <el-tab-pane label="相关推荐" name="recommendations">
        <div class="recommendations">
          <div class="book-grid">
            <div 
              v-for="recommendedBook in recommendations" 
              :key="recommendedBook.id"
              class="recommended-book"
              @click="$emit('view-book', recommendedBook)"
            >
              <div class="book-cover-small">
                <el-image 
                  :src="recommendedBook.coverUrl || '/images/default-book-cover.jpg'" 
                  fit="cover"
                >
                  <template #error>
                    <div class="image-slot-small">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="book-info-small">
                <h5>{{ recommendedBook.title }}</h5>
                <p>{{ recommendedBook.author }}</p>
                <el-tag :type="getStatusType(recommendedBook.status)" size="small">
                  {{ getStatusText(recommendedBook.status) }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <el-empty v-if="recommendations.length === 0" description="暂无相关推荐" />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, Star } from '@element-plus/icons-vue'
import { libraryApi } from '@/api/library'

const props = defineProps({
  book: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['borrow', 'reserve', 'view-book'])

// 响应式数据
const activeTab = ref('description')
const favoriteLoading = ref(false)
const isFavorite = ref(false)
const reviewSubmitting = ref(false)
const reviews = ref([])
const recommendations = ref([])
const reviewStats = ref(null)
const myReview = ref(null)

// 评价表单
const reviewForm = reactive({
  rating: 5,
  comment: ''
})

// 方法
const loadBookReviews = async () => {
  try {
    const { data } = await libraryApi.getBookReviews(props.book.id)
    reviews.value = data.reviews || []
    reviewStats.value = data.stats || null
    myReview.value = data.myReview || null
  } catch (error) {
    console.error('加载图书评价失败:', error)
  }
}

const loadRecommendations = async () => {
  try {
    const { data } = await libraryApi.getRecommendedBooks({
      category: props.book.category,
      excludeId: props.book.id,
      limit: 6
    })
    recommendations.value = data.books || []
  } catch (error) {
    console.error('加载推荐图书失败:', error)
  }
}

const toggleFavorite = async () => {
  try {
    favoriteLoading.value = true
    
    if (isFavorite.value) {
      await libraryApi.removeFromFavorites(props.book.id)
      isFavorite.value = false
      ElMessage.success('已取消收藏')
    } else {
      await libraryApi.addToFavorites(props.book.id)
      isFavorite.value = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

const submitReview = async () => {
  if (!reviewForm.comment.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    reviewSubmitting.value = true
    
    await libraryApi.reviewBook(props.book.id, {
      rating: reviewForm.rating,
      comment: reviewForm.comment
    })
    
    ElMessage.success('评价提交成功')
    
    // 重新加载评价
    await loadBookReviews()
    
    // 清空表单
    reviewForm.rating = 5
    reviewForm.comment = ''
    
  } catch (error) {
    console.error('提交评价失败:', error)
    ElMessage.error('提交评价失败')
  } finally {
    reviewSubmitting.value = false
  }
}

const getStatusType = (status) => {
  const typeMap = {
    'available': 'success',
    'borrowed': 'warning',
    'reserved': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'available': '可借',
    'borrowed': '已借出',
    'reserved': '预约中'
  }
  return textMap[status] || '未知'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadBookReviews(),
    loadRecommendations()
  ])
})
</script>

<style scoped>
.book-detail {
  padding: 20px;
}

.book-header {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
}

.book-cover {
  width: 200px;
  height: 280px;
  flex-shrink: 0;
}

.book-cover .el-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.book-info {
  flex: 1;
}

.book-info h2 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.book-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item label {
  min-width: 80px;
  color: #909399;
  font-weight: 500;
}

.meta-item span {
  color: #606266;
}

.book-status {
  margin-bottom: 20px;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.copies-info {
  color: #606266;
  font-size: 14px;
}

.rating-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-count {
  color: #909399;
  font-size: 14px;
}

.book-actions {
  display: flex;
  gap: 12px;
}

.book-tabs {
  margin-top: 20px;
}

.book-description p {
  line-height: 1.8;
  color: #606266;
}

.no-description {
  color: #909399;
  font-style: italic;
}

.reviews-section {
  padding: 20px 0;
}

.review-stats {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stats-overview {
  display: flex;
  gap: 40px;
}

.average-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.rating-number {
  font-size: 36px;
  font-weight: bold;
  color: #ff9900;
}

.total-reviews {
  color: #909399;
  font-size: 14px;
}

.rating-distribution {
  flex: 1;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.rating-label {
  min-width: 40px;
  color: #606266;
  font-size: 14px;
}

.rating-count {
  min-width: 30px;
  color: #909399;
  font-size: 14px;
}

.my-review {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.my-review h4 {
  margin: 0 0 16px 0;
  color: #303133;
}

.review-form {
  max-width: 500px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-name {
  font-weight: 500;
  color: #303133;
}

.review-date {
  color: #909399;
  font-size: 14px;
}

.review-content p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.recommendations {
  padding: 20px 0;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.recommended-book {
  cursor: pointer;
  transition: transform 0.2s;
}

.recommended-book:hover {
  transform: translateY(-2px);
}

.book-cover-small {
  width: 100%;
  height: 140px;
  margin-bottom: 12px;
}

.book-cover-small .el-image {
  width: 100%;
  height: 100%;
  border-radius: 6px;
}

.image-slot-small {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 20px;
}

.book-info-small h5 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.book-info-small p {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 12px;
}

@media (max-width: 768px) {
  .book-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .book-cover {
    width: 150px;
    height: 210px;
    align-self: center;
  }
  
  .book-meta {
    grid-template-columns: 1fr;
  }
  
  .stats-overview {
    flex-direction: column;
    gap: 20px;
  }
  
  .book-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 16px;
  }
}
</style>
