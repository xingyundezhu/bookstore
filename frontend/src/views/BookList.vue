<template>
  <div class="book-list-page" ref="pageRef">
    <div class="page-container">
      <aside class="sidebar">
        <div class="category-filter">
          <h3>图书分类</h3>
          <el-menu :default-active="selectedCategory" @select="handleCategorySelect">
            <el-menu-item index="">
              <span>全部分类</span>
            </el-menu-item>
            <el-menu-item v-for="cat in categories" :key="cat.id" :index="String(cat.id)">
              <span>{{ cat.name }}</span>
            </el-menu-item>
          </el-menu>
        </div>
      </aside>
      
      <main class="main-content">
        <div class="filter-bar">
          <div class="sort-options">
            <span>排序：</span>
            <el-radio-group v-model="sortBy" @change="handleSortChange">
              <el-radio-button value="sales">销量优先</el-radio-button>
              <el-radio-button value="priceAsc">价格从低到高</el-radio-button>
              <el-radio-button value="priceDesc">价格从高到低</el-radio-button>
              <el-radio-button value="createTime">最新上架</el-radio-button>
            </el-radio-group>
          </div>
          <div class="view-count">
            <span>共 {{ total }} 本图书</span>
          </div>
        </div>
        
        <div class="books-grid" v-loading="loading">
          <div 
            v-for="book in books" 
            :key="book.id" 
            class="book-item"
            @click="handleBookClick(book)"
          >
            <book-card :book="book" />
          </div>
        </div>
        
        <div class="pagination-wrapper" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bookApi } from '@/api/book'
import { categoryApi } from '@/api/category'
import BookCard from '@/components/BookCard.vue'

defineOptions({
  name: 'BookList'
})

const route = useRoute()
const router = useRouter()

const books = ref([])
const categories = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const sortBy = ref('sales')
const selectedCategory = ref('')

const STORAGE_KEY = 'booklist_state'

const handleBookClick = (book) => {
  const state = {
    scrollY: window.scrollY,
    page: currentPage.value,
    sort: sortBy.value,
    category: selectedCategory.value,
    bookId: book.id
  }
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(state))
  router.push({ name: 'BookDetail', params: { id: book.id } })
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchBooks = async () => {
  loading.value = true
  try {
    let sortField = 'sales'
    let sortDir = 'desc'
    
    switch (sortBy.value) {
      case 'sales':
        sortField = 'sales'
        sortDir = 'desc'
        break
      case 'priceAsc':
        sortField = 'price'
        sortDir = 'asc'
        break
      case 'priceDesc':
        sortField = 'price'
        sortDir = 'desc'
        break
      case 'createTime':
        sortField = 'createTime'
        sortDir = 'desc'
        break
    }
    
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: sortField,
      sortDir: sortDir
    }
    
    let res
    if (selectedCategory.value) {
      res = await bookApi.getByCategory(selectedCategory.value, params)
    } else {
      res = await bookApi.getList(params)
    }
    
    if (res.code === 200) {
      books.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('获取图书失败', error)
  } finally {
    loading.value = false
  }
}

const handleCategorySelect = (index) => {
  selectedCategory.value = index
  currentPage.value = 1
  sessionStorage.removeItem(STORAGE_KEY)
  
  if (index) {
    router.push({ name: 'BookList', query: { category: index } })
  } else {
    router.push({ name: 'BookList' })
  }
  
  fetchBooks()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSortChange = () => {
  currentPage.value = 1
  sessionStorage.removeItem(STORAGE_KEY)
  fetchBooks()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handlePageChange = () => {
  sessionStorage.removeItem(STORAGE_KEY)
  fetchBooks()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(() => route.query.category, (newCategory) => {
  if (newCategory) {
    selectedCategory.value = String(newCategory)
  } else {
    selectedCategory.value = ''
  }
  currentPage.value = 1
  fetchBooks()
}, { immediate: false })

watch(() => route.query.sort, (newSort) => {
  if (newSort === 'sales') {
    sortBy.value = 'sales'
  } else if (newSort === 'createTime') {
    sortBy.value = 'createTime'
  }
  currentPage.value = 1
  fetchBooks()
}, { immediate: false })

onMounted(async () => {
  const savedState = sessionStorage.getItem(STORAGE_KEY)
  
  if (savedState) {
    const state = JSON.parse(savedState)
    currentPage.value = state.page || 1
    sortBy.value = state.sort || 'sales'
    selectedCategory.value = state.category || ''
  }
  
  if (route.query.category) {
    selectedCategory.value = String(route.query.category)
  }
  if (route.query.sort) {
    if (route.query.sort === 'sales') {
      sortBy.value = 'sales'
    } else if (route.query.sort === 'createTime') {
      sortBy.value = 'createTime'
    }
  }
  
  await fetchCategories()
  await fetchBooks()
  
  if (savedState) {
    const state = JSON.parse(savedState)
    await nextTick()
    
    setTimeout(() => {
      window.scrollTo(0, state.scrollY || 0)
    }, 200)
  }
})
</script>

<style lang="scss" scoped>
.book-list-page {
  min-height: calc(100vh - 72px - 200px);
  background: var(--bg-primary);
  
  .page-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 24px;
    display: flex;
    gap: 24px;
  }
  
  .sidebar {
    width: 220px;
    flex-shrink: 0;
    
    .category-filter {
      background: var(--bg-card);
      border-radius: var(--radius-md);
      padding: 20px;
      position: sticky;
      top: 96px;
      
      h3 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--border-color);
        color: var(--text-primary);
      }
      
      .el-menu {
        border: none;
        background: transparent;
        
        .el-menu-item {
          height: 40px;
          line-height: 40px;
          border-radius: var(--radius-sm);
          color: var(--text-secondary);
          
          &.is-active {
            background: rgba(26, 26, 46, 0.08);
            color: var(--primary-color);
          }
          
          &:hover {
            background: var(--bg-primary);
          }
        }
      }
    }
  }
  
  .main-content {
    flex: 1;
    
    .filter-bar {
      background: var(--bg-card);
      border-radius: var(--radius-md);
      padding: 16px 20px;
      margin-bottom: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .sort-options {
        display: flex;
        align-items: center;
        gap: 12px;
        
        span {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .view-count {
        color: var(--text-muted);
        font-size: 14px;
      }
    }
    
    .books-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 24px;
      min-height: 400px;
      
      .book-item {
        cursor: pointer;
      }
    }
    
    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 32px;
    }
  }
}

@media (max-width: 1200px) {
  .book-list-page {
    .books-grid {
      grid-template-columns: repeat(3, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .book-list-page {
    .page-container {
      flex-direction: column;
    }
    
    .sidebar {
      width: 100%;
      
      .category-filter {
        position: static;
      }
    }
    
    .books-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>
