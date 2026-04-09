<template>
  <div class="user-favorites">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>我的收藏</h2>
      </div>
      <span class="count-badge" v-if="total > 0">{{ total }} 本</span>
    </div>
    
    <div class="favorites-content" v-loading="loading">
      <template v-if="favorites.length > 0">
        <div class="books-grid">
          <div v-for="book in favorites" :key="book.id" class="book-card">
            <div class="book-cover" @click="goToBook(book.id)">
              <img :src="book.coverImage || defaultCover" :alt="book.title" @error="handleImageError" />
              <div class="book-overlay">
                <span class="view-btn">查看详情</span>
              </div>
            </div>
            <div class="book-info">
              <h3 class="book-title" @click="goToBook(book.id)">{{ book.title }}</h3>
              <p class="book-author">{{ book.author }}</p>
              <div class="book-meta">
                <span class="book-price">¥{{ formatPrice(book.price) }}</span>
                <span v-if="book.originalPrice > book.price" class="original-price">
                  ¥{{ formatPrice(book.originalPrice) }}
                </span>
              </div>
              <div class="book-actions">
                <el-button type="primary" size="small" @click="addToCart(book)">
                  <el-icon><ShoppingCart /></el-icon>
                  加入购物车
                </el-button>
                <el-button size="small" @click="removeFavorite(book.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
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
      </template>
      
      <el-empty v-else description="暂无收藏">
        <el-button type="primary" @click="$router.push('/books')">去逛逛</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { favoriteApi } from '@/api/favorite'
import { cartApi } from '@/api/cart'

const router = useRouter()
const defaultCover = 'https://picsum.photos/seed/fav/200/280'
const loading = ref(false)
const favorites = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const handleImageError = (e) => {
  e.target.src = defaultCover
}

const formatPrice = (price) => {
  if (!price) return '0.00'
  return Number(price).toFixed(2)
}

const goToBook = (bookId) => {
  router.push(`/books/${bookId}`)
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await favoriteApi.getList({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    if (res.code === 200) {
      favorites.value = res.data.content || res.data
      total.value = res.data.totalElements || res.data.length
    }
  } catch (error) {
    console.error('获取收藏列表失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchFavorites()
}

const addToCart = async (book) => {
  try {
    const res = await cartApi.add(book.id, 1)
    if (res.code === 200) {
      ElMessage.success('已添加到购物车')
    }
  } catch (error) {
    console.error('添加失败', error)
  }
}

const removeFavorite = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await favoriteApi.remove(bookId)
    if (res.code === 200) {
      ElMessage.success('已取消收藏')
      fetchFavorites()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败', error)
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.user-favorites {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 24px;
  
  .page-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        background: var(--bg-primary);
        border: 1px solid var(--border-color);
        border-radius: var(--radius-sm);
        color: var(--text-secondary);
        font-weight: 500;
        transition: all var(--transition-fast);
        
        &:hover {
          color: var(--primary-color);
          border-color: var(--primary-color);
        }
      }
      
      h2 {
        font-size: 20px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
      }
    }
    
    .count-badge {
      background: var(--bg-primary);
      color: var(--text-secondary);
      padding: 4px 12px;
      border-radius: var(--radius-xl);
      font-size: 13px;
      font-weight: 500;
    }
  }
  
  .books-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }
  
  .book-card {
    background: var(--bg-secondary);
    border-radius: var(--radius-md);
    overflow: hidden;
    transition: all var(--transition-normal);
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-lg);
      
      .book-cover {
        img {
          transform: scale(1.05);
        }
        
        .book-overlay {
          opacity: 1;
        }
      }
    }
    
    .book-cover {
      position: relative;
      aspect-ratio: 3/4;
      overflow: hidden;
      cursor: pointer;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform var(--transition-slow);
      }
      
      .book-overlay {
        position: absolute;
        inset: 0;
        background: rgba(26, 26, 46, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity var(--transition-normal);
        
        .view-btn {
          background: #fff;
          color: var(--primary-color);
          padding: 8px 20px;
          border-radius: var(--radius-xl);
          font-size: 13px;
          font-weight: 600;
        }
      }
    }
    
    .book-info {
      padding: 16px;
      
      .book-title {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 6px;
        cursor: pointer;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        
        &:hover {
          color: var(--accent-color);
        }
      }
      
      .book-author {
        font-size: 13px;
        color: var(--text-muted);
        margin-bottom: 10px;
      }
      
      .book-meta {
        display: flex;
        align-items: baseline;
        gap: 8px;
        margin-bottom: 12px;
        
        .book-price {
          font-size: 18px;
          font-weight: 700;
          color: var(--accent-color);
        }
        
        .original-price {
          font-size: 13px;
          color: var(--text-muted);
          text-decoration: line-through;
        }
      }
      
      .book-actions {
        display: flex;
        gap: 8px;
        
        .el-button {
          flex: 1;
        }
      }
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }
}

@media (max-width: 992px) {
  .user-favorites {
    .books-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 576px) {
  .user-favorites {
    .books-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
