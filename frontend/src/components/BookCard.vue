<template>
  <div class="book-card" @click="goToDetail">
    <div class="book-cover">
      <img 
        :src="book.coverImage || defaultCover" 
        :alt="book.title"
        @error="handleImageError"
        loading="lazy"
      />
      <div class="book-overlay">
        <span class="view-btn">查看详情</span>
      </div>
      <div class="book-tags">
        <span v-if="book.isHot" class="tag hot">热销</span>
        <span v-if="book.isNew" class="tag new">新品</span>
      </div>
    </div>
    <div class="book-info">
      <h3 class="book-title">{{ book.title }}</h3>
      <p class="book-author">{{ book.author }}</p>
      <div class="book-meta">
        <div class="book-rating" v-if="book.avgRating">
          <el-rate :model-value="book.avgRating" disabled size="small" />
          <span class="rating-count">{{ book.ratingCount }}</span>
        </div>
        <div class="book-sales">{{ book.sales }}人已购买</div>
      </div>
      <div class="book-price">
        <span class="price-label">¥</span>
        <span class="current-price">{{ formatPrice(book.price) }}</span>
        <span v-if="book.originalPrice && book.originalPrice > book.price" class="original-price">
          ¥{{ formatPrice(book.originalPrice) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  book: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const defaultCover = 'https://picsum.photos/seed/default/200/280'
const imageError = ref(false)

const handleImageError = (e) => {
  if (!imageError.value) {
    imageError.value = true
    e.target.src = defaultCover
  }
}

const goToDetail = () => {
  router.push({ name: 'BookDetail', params: { id: props.book.id } })
}

const formatPrice = (price) => {
  if (!price) return '0.00'
  return Number(price).toFixed(2)
}
</script>

<style lang="scss" scoped>
.book-card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
  
  &:hover {
    transform: translateY(-8px);
    box-shadow: var(--shadow-lg);
    
    .book-cover {
      img {
        transform: scale(1.08);
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
    background: var(--bg-primary);
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform var(--transition-slow);
    }
    
    .book-overlay {
      position: absolute;
      inset: 0;
      background: rgba(26, 26, 46, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity var(--transition-normal);
      
      .view-btn {
        background: #fff;
        color: var(--primary-color);
        padding: 10px 24px;
        border-radius: var(--radius-xl);
        font-size: 13px;
        font-weight: 600;
        letter-spacing: 0.02em;
      }
    }
    
    .book-tags {
      position: absolute;
      top: 12px;
      left: 12px;
      display: flex;
      flex-direction: column;
      gap: 6px;
      
      .tag {
        padding: 4px 10px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 600;
        letter-spacing: 0.02em;
        
        &.hot {
          background: var(--accent-color);
          color: #fff;
        }
        
        &.new {
          background: var(--primary-color);
          color: #fff;
        }
      }
    }
  }
  
  .book-info {
    padding: 16px 18px 20px;
    
    .book-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 6px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      letter-spacing: -0.01em;
    }
    
    .book-author {
      font-size: 13px;
      color: var(--text-muted);
      margin-bottom: 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .book-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      
      .book-rating {
        display: flex;
        align-items: center;
        gap: 6px;
        
        :deep(.el-rate__icon) {
          font-size: 12px;
          margin-right: 0;
        }
        
        .rating-count {
          font-size: 12px;
          color: var(--text-muted);
        }
      }
      
      .book-sales {
        font-size: 12px;
        color: var(--text-muted);
      }
    }
    
    .book-price {
      display: flex;
      align-items: baseline;
      gap: 2px;
      
      .price-label {
        font-size: 14px;
        font-weight: 600;
        color: var(--accent-color);
      }
      
      .current-price {
        font-size: 20px;
        font-weight: 700;
        color: var(--accent-color);
        letter-spacing: -0.02em;
      }
      
      .original-price {
        font-size: 13px;
        color: var(--text-muted);
        text-decoration: line-through;
        margin-left: 8px;
      }
    }
  }
}
</style>
