<template>
  <div class="book-detail-page" v-loading="loading">
    <div class="back-nav">
      <el-button @click="goBack" text>
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    <div class="page-container" v-if="book">
      <div class="book-main">
        <div class="book-gallery">
          <div class="main-image">
            <img :src="currentImage" :alt="book.title" />
          </div>
          <div class="thumbnail-list" v-if="book.images && book.images.length > 1">
            <div 
              v-for="(img, index) in book.images" 
              :key="index"
              class="thumbnail"
              :class="{ active: currentImage === img }"
              @click="currentImage = img"
            >
              <img :src="img" :alt="`${book.title} - ${index + 1}`" />
            </div>
          </div>
        </div>
        
        <div class="book-info">
          <h1 class="book-title">{{ book.title }}</h1>
          <p class="book-author">作者：{{ book.author }}</p>
          
          <div class="book-rating" v-if="book.avgRating">
            <el-rate :model-value="book.avgRating" disabled show-score text-color="#ff9900" />
            <span class="rating-count">{{ book.ratingCount }} 条评价</span>
          </div>
          
          <div class="book-meta">
            <div class="meta-item">
              <span class="label">出版社：</span>
              <span>{{ book.publisher }}</span>
            </div>
            <div class="meta-item">
              <span class="label">出版日期：</span>
              <span>{{ book.publishDate || '未知' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">ISBN：</span>
              <span>{{ book.isbn || '未知' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">页数：</span>
              <span>{{ book.pages || '未知' }} 页</span>
            </div>
          </div>
          
          <div class="book-price-section">
            <div class="price-row">
              <span class="label">价格：</span>
              <span class="current-price">¥{{ book.price }}</span>
              <span v-if="book.originalPrice && book.originalPrice > book.price" class="original-price">
                ¥{{ book.originalPrice }}
              </span>
              <el-tag v-if="book.discount && book.discount < 1" type="danger" size="small">
                {{ (book.discount * 10).toFixed(1) }}折
              </el-tag>
            </div>
          </div>
          
          <div class="book-stats">
            <span>库存：{{ book.stock }} 本</span>
            <span>销量：{{ book.sales }} 本</span>
          </div>
          
          <div class="quantity-selector">
            <span class="label">数量：</span>
            <el-input-number v-model="quantity" :min="1" :max="book.stock" />
          </div>
          
          <div class="action-buttons">
            <el-button 
              :type="isFavorited ? 'warning' : 'default'" 
              size="large" 
              @click="toggleFavorite"
              :loading="togglingFavorite"
            >
              <el-icon><Star /></el-icon>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button type="primary" size="large" @click="addToCart" :loading="addingToCart">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button type="danger" size="large" @click="buyNow">
              <el-icon><CreditCard /></el-icon>
              立即购买
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="book-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="图书简介" name="description">
            <div class="description-content">
              {{ book.description || '暂无简介' }}
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="用户评价" name="reviews">
            <div class="reviews-section">
              <div class="reviews-header">
                <span>用户评价 ({{ totalReviews }})</span>
                <el-button type="primary" size="small" @click="showReviewDialog" v-if="canReview">
                  写评价
                </el-button>
              </div>
              
              <div class="reviews-list" v-if="reviews.length > 0">
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <el-avatar :size="40" :src="review.userAvatar">
                      {{ review.isAnonymous ? '匿' : (review.userNickname?.charAt(0) || '用') }}
                    </el-avatar>
                    <div class="review-info">
                      <span class="username">{{ review.isAnonymous ? '匿名用户' : (review.userNickname || '用户') }}</span>
                      <el-rate :model-value="review.rating" disabled size="small" />
                    </div>
                    <span class="review-time">{{ formatDate(review.createTime) }}</span>
                  </div>
                  <div class="review-content">{{ review.content }}</div>
                </div>
              </div>
              
              <el-empty v-else description="暂无评价" />
              
              <div class="pagination-wrapper" v-if="totalReviews > reviewPageSize">
                <el-pagination
                  v-model:current-page="reviewPage"
                  :page-size="reviewPageSize"
                  :total="totalReviews"
                  layout="prev, pager, next"
                  @current-change="fetchReviews"
                />
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="相关推荐" name="recommend">
            <div class="recommend-grid">
              <book-card v-for="item in similarBooks" :key="item.id" :book="item" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    
    <el-dialog v-model="reviewDialogVisible" title="写评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="匿名评价">
          <el-switch v-model="reviewForm.isAnonymous" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="submittingReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { bookApi } from '@/api/book'
import { cartApi } from '@/api/cart'
import { reviewApi } from '@/api/review'
import { favoriteApi } from '@/api/favorite'
import { recommendationApi } from '@/api/recommendation'
import BookCard from '@/components/BookCard.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const book = ref(null)
const loading = ref(true)
const currentImage = ref('')
const quantity = ref(1)
const addingToCart = ref(false)
const activeTab = ref('description')

const isFavorited = ref(false)
const togglingFavorite = ref(false)

const reviews = ref([])
const totalReviews = ref(0)
const reviewPage = ref(1)
const reviewPageSize = ref(5)

const similarBooks = ref([])

const reviewDialogVisible = ref(false)
const submittingReview = ref(false)
const reviewForm = reactive({
  rating: 5,
  content: '',
  isAnonymous: false
})

const canReview = computed(() => userStore.isLoggedIn)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/books')
  }
}

const fetchBook = async () => {
  loading.value = true
  try {
    const res = await bookApi.getById(route.params.id)
    if (res.code === 200) {
      book.value = res.data
      currentImage.value = res.data.coverImage || res.data.images?.[0] || ''
      
      if (userStore.isLoggedIn) {
        recommendationApi.viewBook(book.value.id)
        checkFavorite()
      }
      
      fetchSimilarBooks()
    }
  } catch (error) {
    console.error('获取图书详情失败', error)
  } finally {
    loading.value = false
  }
}

const checkFavorite = async () => {
  if (!userStore.isLoggedIn || !book.value) return
  try {
    const res = await favoriteApi.check(book.value.id)
    if (res.code === 200) {
      isFavorited.value = res.data
    }
  } catch (error) {
    console.error('检查收藏状态失败', error)
  }
}

const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  togglingFavorite.value = true
  try {
    if (isFavorited.value) {
      const res = await favoriteApi.remove(book.value.id)
      if (res.code === 200) {
        isFavorited.value = false
        ElMessage.success('已取消收藏')
      }
    } else {
      const res = await favoriteApi.add(book.value.id)
      if (res.code === 200) {
        isFavorited.value = true
        ElMessage.success('收藏成功')
      }
    }
  } catch (error) {
    console.error('收藏操作失败', error)
    ElMessage.error('操作失败')
  } finally {
    togglingFavorite.value = false
  }
}

const fetchReviews = async () => {
  try {
    const res = await reviewApi.getBookReviews(route.params.id, {
      page: reviewPage.value - 1,
      size: reviewPageSize.value
    })
    if (res.code === 200) {
      if (res.data.content) {
        reviews.value = res.data.content
        totalReviews.value = res.data.totalElements
      } else {
        reviews.value = res.data
        totalReviews.value = res.data.length || 0
      }
    }
  } catch (error) {
    console.error('获取评价失败', error)
    reviews.value = []
    totalReviews.value = 0
  }
}

const fetchSimilarBooks = async () => {
  try {
    const res = await recommendationApi.getSimilar(book.value.id, 4)
    if (res.code === 200) {
      similarBooks.value = res.data
    }
  } catch (error) {
    console.error('获取推荐失败', error)
  }
}

const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  addingToCart.value = true
  try {
    const res = await cartApi.add(book.value.id, quantity.value)
    if (res.code === 200) {
      cartStore.fetchCartCount()
      ElMessage.success('已添加到购物车')
    }
  } catch (error) {
    console.error('添加购物车失败', error)
  } finally {
    addingToCart.value = false
  }
}

const buyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  
  try {
    const res = await cartApi.add(book.value.id, quantity.value)
    if (res.code === 200) {
      cartStore.fetchCartCount()
      router.push({ name: 'Checkout' })
    }
  } catch (error) {
    console.error('添加购物车失败', error)
  }
}

const showReviewDialog = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  reviewForm.rating = 5
  reviewForm.content = ''
  reviewForm.isAnonymous = false
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  submittingReview.value = true
  try {
    const res = await reviewApi.create({
      bookId: book.value.id,
      rating: reviewForm.rating,
      content: reviewForm.content,
      isAnonymous: reviewForm.isAnonymous
    })
    if (res.code === 200) {
      ElMessage.success('评价提交成功')
      reviewDialogVisible.value = false
      fetchReviews()
      fetchBook()
    }
  } catch (error) {
    console.error('提交评价失败', error)
    ElMessage.error('提交评价失败')
  } finally {
    submittingReview.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

watch(() => route.params.id, () => {
  if (route.params.id) {
    fetchBook()
    fetchReviews()
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.book-detail-page {
  min-height: calc(100vh - 64px - 200px);
  background: var(--bg-primary);
  
  .back-nav {
    max-width: 1400px;
    margin: 0 auto;
    padding: 16px 24px 0;
    
    .el-button {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      font-weight: 500;
      color: var(--text-secondary);
      
      &:hover {
        color: var(--primary-color);
      }
    }
  }
  
  .page-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 20px 24px 40px;
    background: var(--bg-card);
    border-radius: var(--radius-lg);
  }
  
  .book-main {
    display: flex;
    gap: 60px;
    margin-bottom: 40px;
  }
  
  .book-gallery {
    width: 400px;
    flex-shrink: 0;
    
    .main-image {
      width: 100%;
      aspect-ratio: 3/4;
      border-radius: 8px;
      overflow: hidden;
      background: #f5f7fa;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .thumbnail-list {
      display: flex;
      gap: 10px;
      margin-top: 16px;
      
      .thumbnail {
        width: 60px;
        height: 80px;
        border-radius: 4px;
        overflow: hidden;
        cursor: pointer;
        border: 2px solid transparent;
        
        &.active {
          border-color: #409eff;
        }
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
    }
  }
  
  .book-info {
    flex: 1;
    
    .book-title {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 12px;
    }
    
    .book-author {
      font-size: 16px;
      color: #606266;
      margin-bottom: 16px;
    }
    
    .book-rating {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      
      .rating-count {
        font-size: 14px;
        color: #909399;
      }
    }
    
    .book-meta {
      background: #f5f7fa;
      padding: 16px;
      border-radius: 8px;
      margin-bottom: 20px;
      
      .meta-item {
        display: flex;
        margin-bottom: 8px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .label {
          color: #909399;
          width: 80px;
        }
      }
    }
    
    .book-price-section {
      margin-bottom: 20px;
      
      .price-row {
        display: flex;
        align-items: baseline;
        gap: 12px;
        
        .label {
          color: #606266;
        }
        
        .current-price {
          font-size: 28px;
          font-weight: 600;
          color: #f56c6c;
        }
        
        .original-price {
          font-size: 16px;
          color: #c0c4cc;
          text-decoration: line-through;
        }
      }
    }
    
    .book-stats {
      display: flex;
      gap: 24px;
      margin-bottom: 20px;
      font-size: 14px;
      color: #909399;
    }
    
    .quantity-selector {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;
      
      .label {
        color: #606266;
      }
    }
    
    .action-buttons {
      display: flex;
      gap: 16px;
      
      .el-button {
        flex: 1;
        height: 48px;
        font-size: 16px;
      }
    }
  }
  
  .book-tabs {
    margin-top: 40px;
    
    .description-content {
      line-height: 1.8;
      color: #606266;
      white-space: pre-wrap;
    }
    
    .reviews-section {
      .reviews-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        font-size: 16px;
        font-weight: 500;
      }
      
      .review-item {
        padding: 20px 0;
        border-bottom: 1px solid #ebeef5;
        
        .review-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;
          
          .review-info {
            display: flex;
            flex-direction: column;
            gap: 4px;
            
            .username {
              font-size: 14px;
              color: #606266;
            }
          }
          
          .review-time {
            margin-left: auto;
            font-size: 13px;
            color: #909399;
          }
        }
        
        .review-content {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
        }
      }
    }
    
    .recommend-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }
}
</style>
