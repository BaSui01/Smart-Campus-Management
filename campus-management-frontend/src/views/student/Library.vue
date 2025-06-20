<template>
  <div class="library-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>图书馆</h1>
          <p>图书查询、借阅管理和学习资源</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ libraryStats.totalBooks }}</span>
            <span class="stat-label">馆藏图书</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ libraryStats.borrowedBooks }}</span>
            <span class="stat-label">已借图书</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ libraryStats.availableBooks }}</span>
            <span class="stat-label">可借图书</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            v-model="searchQuery"
            placeholder="搜索书名、作者、ISBN..."
            clearable
            :prefix-icon="Search"
            @keyup.enter="searchBooks"
            @clear="searchBooks"
            @input="debouncedSearch"
          >
            <template #append>
              <el-button @click="searchBooks" :loading="loading.search">
                搜索
              </el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterCategory" placeholder="图书分类" clearable @change="searchBooks">
            <el-option
              v-for="category in bookCategories"
              :key="category.value"
              :label="category.label"
              :value="category.value"
            />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterStatus" placeholder="借阅状态" clearable @change="searchBooks">
            <el-option label="全部" value="" />
            <el-option label="可借" value="available" />
            <el-option label="已借出" value="borrowed" />
            <el-option label="预约中" value="reserved" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8">
          <div class="view-controls">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
                网格视图
              </el-radio-button>
              <el-radio-button label="list">
                <el-icon><List /></el-icon>
                列表视图
              </el-radio-button>
            </el-radio-group>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 快捷功能 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :xs="12" :sm="6" :md="3">
        <el-card class="action-card" @click="showMyBorrowedBooks">
          <div class="action-content">
            <el-icon size="32" color="#409eff"><Reading /></el-icon>
            <h4>我的借阅</h4>
            <p>{{ myBorrowedBooks.length }} 本</p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="3">
        <el-card class="action-card" @click="showReservations">
          <div class="action-content">
            <el-icon size="32" color="#e6a23c"><Clock /></el-icon>
            <h4>我的预约</h4>
            <p>{{ myReservations.length }} 本</p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="3">
        <el-card class="action-card" @click="showBorrowHistory">
          <div class="action-content">
            <el-icon size="32" color="#67c23a"><Document /></el-icon>
            <h4>借阅历史</h4>
            <p>查看记录</p>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6" :md="3">
        <el-card class="action-card" @click="showRecommendations">
          <div class="action-content">
            <el-icon size="32" color="#f56c6c"><Star /></el-icon>
            <h4>推荐图书</h4>
            <p>个性推荐</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图书列表 -->
    <el-card class="books-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>图书列表</h3>
          <div class="header-controls">
            <el-button size="small" @click="refreshBooks">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading.books">
        <!-- 网格视图 -->
        <div v-if="viewMode === 'grid'" class="books-grid">
          <div
            v-for="book in books"
            :key="book.id"
            class="book-card"
            @click="viewBookDetail(book)"
          >
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
              <h4 class="book-title">{{ book.title }}</h4>
              <p class="book-author">{{ book.author }}</p>
              <p class="book-category">{{ book.category }}</p>
              <div class="book-status">
                <el-tag :type="getStatusType(book.status)" size="small">
                  {{ getStatusText(book.status) }}
                </el-tag>
              </div>
              <div class="book-actions">
                <el-button
                  v-if="book.status === 'available'"
                  type="primary"
                  size="small"
                  @click.stop="borrowBook(book)"
                >
                  借阅
                </el-button>
                <el-button
                  v-else-if="book.status === 'borrowed'"
                  type="warning"
                  size="small"
                  @click.stop="reserveBook(book)"
                >
                  预约
                </el-button>
                <el-button
                  size="small"
                  @click.stop="viewBookDetail(book)"
                >
                  详情
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="books-list">
          <el-table :data="books" style="width: 100%">
            <el-table-column label="封面" width="80">
              <template #default="{ row }">
                <el-image
                  :src="row.coverUrl || '/images/default-book-cover.jpg'"
                  style="width: 50px; height: 70px"
                  fit="cover"
                >
                  <template #error>
                    <div class="image-slot">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </template>
            </el-table-column>

            <el-table-column prop="title" label="书名" min-width="200" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="isbn" label="ISBN" width="120" />
            <el-table-column prop="publisher" label="出版社" width="120" />
            <el-table-column prop="publishDate" label="出版日期" width="100" />

            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'available'"
                  type="primary"
                  size="small"
                  @click="borrowBook(row)"
                >
                  借阅
                </el-button>
                <el-button
                  v-else-if="row.status === 'borrowed'"
                  type="warning"
                  size="small"
                  @click="reserveBook(row)"
                >
                  预约
                </el-button>
                <el-button
                  size="small"
                  @click="viewBookDetail(row)"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading.books && books.length === 0" description="暂无图书数据">
          <el-button type="primary" @click="refreshBooks">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 图书详情弹窗 -->
    <el-dialog
      v-model="bookDetailVisible"
      title="图书详情"
      width="80%"
      :before-close="() => { bookDetailVisible = false; selectedBook = null }"
    >
      <BookDetail
        v-if="selectedBook"
        :book="selectedBook"
        @borrow="borrowBook"
        @reserve="reserveBook"
        @view-book="viewBookDetail"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Reading,
  Search,
  Grid,
  List,
  Clock,
  Document,
  Star,
  Refresh,
  Picture
} from '@element-plus/icons-vue'
import { libraryApi } from '@/api/library'
import BookDetail from './components/BookDetail.vue'
import { debounce } from '@/utils/performance'

// 响应式数据
const loading = reactive({
  search: false,
  books: false
})

const searchQuery = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const viewMode = ref('grid')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const books = ref([])
const myBorrowedBooks = ref([])
const myReservations = ref([])
const libraryStats = reactive({
  totalBooks: 0,
  borrowedBooks: 0,
  availableBooks: 0
})

// 弹窗控制
const bookDetailVisible = ref(false)
const selectedBook = ref(null)

// 图书分类
const bookCategories = ref([])

// 方法
const loadLibraryStats = async () => {
  try {
    const [statsResponse, myStatsResponse] = await Promise.all([
      libraryApi.getLibraryStats(),
      libraryApi.getMyLibraryStats()
    ])

    const stats = statsResponse.data
    const myStats = myStatsResponse.data

    libraryStats.totalBooks = stats.totalBooks || 50000
    libraryStats.borrowedBooks = myStats.borrowedCount || 0
    libraryStats.availableBooks = stats.availableBooks || 49995
  } catch (error) {
    console.error('加载图书馆统计失败:', error)
    // 使用默认值
    libraryStats.totalBooks = 50000
    libraryStats.borrowedBooks = 0
    libraryStats.availableBooks = 49995
  }
}

const searchBooks = async () => {
  loading.books = true

  try {
    const { data } = await libraryApi.searchBooks({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchQuery.value,
      category: filterCategory.value,
      status: filterStatus.value
    })

    const bookList = Array.isArray(data) ? data : (data.books || data.list || data.content || [])

    books.value = bookList.map(book => ({
      id: book.id,
      title: book.title || book.bookTitle,
      author: book.author || book.authorName,
      category: book.category || book.categoryName,
      isbn: book.isbn,
      publisher: book.publisher || book.publisherName,
      publishDate: book.publishDate || book.publishYear,
      status: book.status || (book.availableCopies > 0 ? 'available' : 'borrowed'),
      coverUrl: book.coverUrl || book.imageUrl,
      description: book.description || book.summary,
      location: book.location || book.shelfLocation,
      totalCopies: book.totalCopies || 1,
      availableCopies: book.availableCopies || 0,
      rating: book.rating || 0,
      reviewCount: book.reviewCount || 0
    }))

    total.value = data.total || data.totalElements || bookList.length

  } catch (error) {
    console.error('搜索图书失败:', error)
    ElMessage.error('搜索图书失败')

    // API失败时返回空数据
    books.value = []
    total.value = 0
  } finally {
    loading.books = false
  }
}



const loadMyBorrowedBooks = async () => {
  try {
    const { data } = await libraryApi.getMyBorrows({ status: 'borrowed' })
    const borrowList = Array.isArray(data) ? data : (data.borrows || data.list || [])

    myBorrowedBooks.value = borrowList.map(borrow => ({
      id: borrow.id,
      title: borrow.bookTitle || borrow.book?.title,
      author: borrow.bookAuthor || borrow.book?.author,
      borrowDate: borrow.borrowDate || borrow.createTime,
      dueDate: borrow.dueDate || borrow.returnDate,
      status: borrow.status || 'borrowed',
      renewCount: borrow.renewCount || 0,
      maxRenewCount: borrow.maxRenewCount || 2
    }))
  } catch (error) {
    console.error('加载借阅图书失败:', error)
    myBorrowedBooks.value = []
  }
}

const loadMyReservations = async () => {
  try {
    const { data } = await libraryApi.getMyReservations({ status: 'active' })
    const reservationList = Array.isArray(data) ? data : (data.reservations || data.list || [])

    myReservations.value = reservationList.map(reservation => ({
      id: reservation.id,
      title: reservation.bookTitle || reservation.book?.title,
      author: reservation.bookAuthor || reservation.book?.author,
      reserveDate: reservation.reserveDate || reservation.createTime,
      status: reservation.status || 'reserved',
      queuePosition: reservation.queuePosition || 1,
      estimatedDate: reservation.estimatedDate
    }))
  } catch (error) {
    console.error('加载预约图书失败:', error)
    myReservations.value = []
  }
}

// 加载图书分类
const loadBookCategories = async () => {
  try {
    const { data } = await libraryApi.getBookCategories()
    const categories = Array.isArray(data) ? data : (data.categories || [])

    bookCategories.value = categories.map(category => ({
      label: category.name || category.categoryName,
      value: category.id || category.code || category.value
    }))
  } catch (error) {
    console.error('加载图书分类失败:', error)
    // 使用默认分类
    bookCategories.value = [
      { label: '计算机科学', value: 'computer' },
      { label: '数学', value: 'mathematics' },
      { label: '物理学', value: 'physics' },
      { label: '化学', value: 'chemistry' },
      { label: '生物学', value: 'biology' },
      { label: '文学', value: 'literature' },
      { label: '历史', value: 'history' },
      { label: '哲学', value: 'philosophy' },
      { label: '经济学', value: 'economics' },
      { label: '管理学', value: 'management' }
    ]
  }
}

const refreshBooks = () => {
  currentPage.value = 1
  searchBooks()
}

const borrowBook = async (book) => {
  try {
    await ElMessageBox.confirm(
      `确定要借阅《${book.title}》吗？借期为30天。`,
      '确认借阅',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const { data } = await libraryApi.borrowBook({
      bookId: book.id,
      borrowDays: 30
    })

    // 更新图书状态
    book.status = 'borrowed'
    book.availableCopies = Math.max(0, book.availableCopies - 1)

    // 添加到我的借阅列表
    myBorrowedBooks.value.push({
      id: data.id,
      title: book.title,
      author: book.author,
      borrowDate: data.borrowDate || new Date().toISOString().split('T')[0],
      dueDate: data.dueDate || new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      status: 'borrowed'
    })

    ElMessage.success('借阅成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('借阅失败:', error)
      ElMessage.error('借阅失败，请稍后重试')
    }
  }
}

const reserveBook = async (book) => {
  try {
    await ElMessageBox.confirm(
      `确定要预约《${book.title}》吗？图书可借时会通知您。`,
      '确认预约',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const { data } = await libraryApi.reserveBook(book.id)

    // 更新图书状态
    book.status = 'reserved'

    // 添加到我的预约列表
    myReservations.value.push({
      id: data.id,
      title: book.title,
      author: book.author,
      reserveDate: data.reserveDate || new Date().toISOString().split('T')[0],
      status: 'reserved',
      queuePosition: data.queuePosition || 1
    })

    ElMessage.success(`预约成功，您在队列中的位置：第${data.queuePosition || 1}位`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预约失败:', error)
      ElMessage.error('预约失败，请稍后重试')
    }
  }
}

const viewBookDetail = async (book) => {
  try {
    const { data } = await libraryApi.getBookDetail(book.id)

    selectedBook.value = {
      ...book,
      ...data
    }
    bookDetailVisible.value = true
  } catch (error) {
    console.error('加载图书详情失败:', error)
    ElMessage.error('加载图书详情失败')

    // 如果API失败，使用基本信息显示详情
    selectedBook.value = book
    bookDetailVisible.value = true
  }
}

const showMyBorrowedBooks = () => {
  ElMessage.info('显示我的借阅图书')
}

const showReservations = () => {
  ElMessage.info('显示我的预约图书')
}

const showBorrowHistory = () => {
  ElMessage.info('显示借阅历史')
}

const showRecommendations = () => {
  ElMessage.info('显示推荐图书')
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

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  searchBooks()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  searchBooks()
}

// 防抖搜索
const debouncedSearch = debounce(() => {
  currentPage.value = 1
  searchBooks()
}, 500)

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadLibraryStats(),
    loadMyBorrowedBooks(),
    loadMyReservations(),
    loadBookCategories(),
    searchBooks()
  ])
})
</script>

<style scoped>
@import '@/styles/student.css';

.library-page {
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

.search-card {
  margin-bottom: 20px;
}

.view-controls {
  display: flex;
  justify-content: flex-end;
}

.quick-actions {
  margin-bottom: 20px;
}

.action-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 120px;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-content {
  text-align: center;
  padding: 20px;
}

.action-content h4 {
  margin: 12px 0 8px 0;
  color: #303133;
}

.action-content p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.books-card {
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

.books-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.book-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.book-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.book-cover {
  height: 200px;
  overflow: hidden;
}

.book-cover .el-image {
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

.book-info {
  padding: 16px;
}

.book-title {
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

.book-author {
  margin: 0 0 4px 0;
  color: #606266;
  font-size: 14px;
}

.book-category {
  margin: 0 0 8px 0;
  color: #909399;
  font-size: 12px;
}

.book-status {
  margin-bottom: 12px;
}

.book-actions {
  display: flex;
  gap: 8px;
}

.books-list {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .library-page {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 20px;
  }

  .header-stats {
    align-self: stretch;
    justify-content: space-around;
  }

  .books-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }

  .view-controls {
    justify-content: center;
    margin-top: 16px;
  }
}
</style>