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
  </div>
</template>