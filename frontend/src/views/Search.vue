<template>
  <div class="search-page">
    <div class="page-container">
      <div class="search-header">
        <h2>搜索结果：{{ keyword }}</h2>
        <span>共找到 {{ total }} 本图书</span>
      </div>
      
      <div class="books-grid" v-loading="loading">
        <book-card v-for="book in books" :key="book.id" :book="book" />
      </div>
      
      <el-empty v-if="!loading && books.length === 0" description="没有找到相关图书" />
      
      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="search"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { bookApi } from '@/api/book'
import BookCard from '@/components/BookCard.vue'

const route = useRoute()

const books = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(12)
const keyword = ref('')

const search = async () => {
  if (!keyword.value) return
  
  loading.value = true
  try {
    const res = await bookApi.search(keyword.value, {
      page: currentPage.value - 1,
      size: pageSize.value
    })
    
    if (res.code === 200) {
      books.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('搜索失败', error)
  } finally {
    loading.value = false
  }
}

watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    currentPage.value = 1
    search()
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.search-page {
  min-height: calc(100vh - 64px - 200px);
  background: #fff;
  
  .page-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 40px 20px;
  }
  
  .search-header {
    margin-bottom: 30px;
    
    h2 {
      font-size: 24px;
      margin-bottom: 8px;
    }
    
    span {
      color: #909399;
    }
  }
  
  .books-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
    min-height: 300px;
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }
}
</style>
