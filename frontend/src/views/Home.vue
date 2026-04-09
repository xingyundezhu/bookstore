<template>
  <div class="home-page">
    <section class="hero-banner">
      <el-carousel height="480px" :interval="5000" arrow="hover" indicator-position="outside">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item">
            <img :src="item.image" :alt="item.title" class="banner-image" />
            <div class="banner-overlay"></div>
            <div class="banner-content">
              <span class="banner-tag">{{ item.tag }}</span>
              <h1>{{ item.title }}</h1>
              <p>{{ item.desc }}</p>
              <el-button class="banner-btn" size="large" @click="$router.push('/books')">
                立即选购
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <section class="category-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">图书分类</h2>
        </div>
        <div class="category-grid">
          <div 
            v-for="category in categories" 
            :key="category.id" 
            class="category-card"
            @click="$router.push({ name: 'BookList', query: { category: category.id } })"
          >
            <div class="category-icon" :style="{ background: category.color }">
              <el-icon :size="28"><component :is="category.icon" /></el-icon>
            </div>
            <span class="category-name">{{ category.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="books-section">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">热门推荐</h2>
          <router-link to="/books?sort=sales" class="view-more">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="books-grid">
          <book-card v-for="book in hotBooks" :key="book.id" :book="book" />
        </div>
      </div>
    </section>

    <section class="books-section alt-bg">
      <div class="section-container">
        <div class="section-header">
          <h2 class="section-title">新书上架</h2>
          <router-link to="/books?sort=createTime" class="view-more">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="books-grid">
          <book-card v-for="book in newBooks" :key="book.id" :book="book" />
        </div>
      </div>
    </section>

    <section class="books-section" v-if="userStore.isLoggedIn">
      <div class="section-container">
        <div class="section-header">
          <div class="title-wrapper">
            <h2 class="section-title">为你推荐</h2>
            <span class="recommend-badge">
              <el-icon><MagicStick /></el-icon>
              个性化推荐
            </span>
          </div>
        </div>
        <div class="books-grid">
          <book-card v-for="book in recommendBooks" :key="book.id" :book="book" />
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="section-container">
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="28"><Van /></el-icon>
            </div>
            <h3>快速配送</h3>
            <p>全国配送，极速到达</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="28"><CircleCheck /></el-icon>
            </div>
            <h3>品质保证</h3>
            <p>正版图书，品质保障</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="28"><Service /></el-icon>
            </div>
            <h3>贴心服务</h3>
            <p>7天无理由退换货</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <el-icon :size="28"><Lock /></el-icon>
            </div>
            <h3>安全支付</h3>
            <p>多种支付方式，安全可靠</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { bookApi } from '@/api/book'
import { categoryApi } from '@/api/category'
import { recommendationApi } from '@/api/recommendation'
import BookCard from '@/components/BookCard.vue'

const userStore = useUserStore()

const banners = ref([
  { id: 1, title: '阅读改变人生', desc: '海量图书，等你来发现', tag: '精选推荐', image: '/api/uploads/banner1.png' },
  { id: 2, title: '新书上架', desc: '最新畅销书，限时优惠', tag: '新品上市', image: '/api/uploads/banner2.png' },
  { id: 3, title: '精品推荐', desc: '精选好书，品质阅读', tag: '品质之选', image: '/api/uploads/banner3.png' }
])

const categories = ref([
  { id: 1, name: '文学小说', icon: 'Notebook', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { id: 2, name: '科技编程', icon: 'Monitor', color: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' },
  { id: 3, name: '经济管理', icon: 'TrendCharts', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 4, name: '人文历史', icon: 'Clock', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { id: 5, name: '艺术设计', icon: 'Picture', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { id: 6, name: '生活百科', icon: 'Coffee', color: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' },
  { id: 7, name: '教育学习', icon: 'Reading', color: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' },
  { id: 8, name: '童书绘本', icon: 'Present', color: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)' }
])

const hotBooks = ref([])
const newBooks = ref([])
const recommendBooks = ref([])

const fetchData = async () => {
  try {
    const [hotRes, newRes] = await Promise.all([
      bookApi.getHot(8),
      bookApi.getNew(8)
    ])
    
    if (hotRes.code === 200) hotBooks.value = hotRes.data
    if (newRes.code === 200) newBooks.value = newRes.data
    
    if (userStore.isLoggedIn) {
      const recRes = await recommendationApi.getPersonalized(8)
      if (recRes.code === 200) recommendBooks.value = recRes.data
    }
  } catch (error) {
    console.error('获取数据失败', error)
  }
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getActive()
    if (res.code === 200 && res.data.length > 0) {
      const iconMap = {
        '文学小说': 'Notebook',
        '科技编程': 'Monitor',
        '经济管理': 'TrendCharts',
        '人文历史': 'Clock',
        '艺术设计': 'Picture',
        '生活百科': 'Coffee',
        '教育学习': 'Reading',
        '童书绘本': 'Present'
      }
      const colorMap = [
        'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
        'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
        'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
        'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)'
      ]
      
      categories.value = res.data.slice(0, 8).map((cat, index) => ({
        id: cat.id,
        name: cat.name,
        icon: iconMap[cat.name] || 'Document',
        color: colorMap[index % colorMap.length]
      }))
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.home-page {
  .hero-banner {
    :deep(.el-carousel__indicators) {
      .el-carousel__indicator {
        .el-carousel__button {
          width: 32px;
          height: 3px;
          border-radius: 2px;
          background: rgba(255, 255, 255, 0.4);
        }
        
        &.is-active .el-carousel__button {
          background: #fff;
          width: 48px;
        }
      }
    }
    
    :deep(.el-carousel__arrow) {
      background: rgba(255, 255, 255, 0.9);
      color: var(--primary-color);
      width: 48px;
      height: 48px;
      
      &:hover {
        background: #fff;
      }
    }
    
    .banner-item {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      
      .banner-image {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
      
      .banner-overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(135deg, rgba(26, 26, 46, 0.7) 0%, rgba(26, 26, 46, 0.3) 100%);
      }
      
      .banner-content {
        position: relative;
        z-index: 1;
        text-align: center;
        color: #fff;
        
        .banner-tag {
          display: inline-block;
          background: rgba(255, 255, 255, 0.2);
          backdrop-filter: blur(10px);
          padding: 6px 16px;
          border-radius: var(--radius-xl);
          font-size: 12px;
          font-weight: 500;
          letter-spacing: 0.1em;
          margin-bottom: 20px;
        }
        
        h1 {
          font-size: 52px;
          font-weight: 700;
          margin-bottom: 16px;
          letter-spacing: -0.02em;
        }
        
        p {
          font-size: 18px;
          margin-bottom: 32px;
          opacity: 0.9;
          font-weight: 300;
        }
        
        .banner-btn {
          background: #fff;
          color: var(--primary-color);
          border: none;
          padding: 14px 32px;
          font-weight: 600;
          border-radius: var(--radius-xl);
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
          }
        }
      }
    }
  }
  
  .section-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 60px 24px;
  }
  
  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32px;
    
    .title-wrapper {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }
  
  .section-title {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary);
    letter-spacing: -0.02em;
  }
  
  .view-more {
    display: flex;
    align-items: center;
    gap: 6px;
    color: var(--text-muted);
    font-size: 14px;
    font-weight: 500;
    padding: 8px 16px;
    border-radius: var(--radius-xl);
    transition: all var(--transition-fast);
    
    &:hover {
      color: var(--primary-color);
      background: var(--bg-primary);
    }
  }
  
  .recommend-badge {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: var(--accent-color);
    background: rgba(233, 69, 96, 0.1);
    padding: 6px 14px;
    border-radius: var(--radius-xl);
    font-weight: 500;
  }
  
  .category-section {
    background: var(--bg-secondary);
    
    .category-grid {
      display: grid;
      grid-template-columns: repeat(8, 1fr);
      gap: 16px;
      
      .category-card {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 14px;
        padding: 24px 16px;
        background: var(--bg-card);
        border-radius: var(--radius-md);
        cursor: pointer;
        transition: all var(--transition-normal);
        
        &:hover {
          transform: translateY(-6px);
          box-shadow: var(--shadow-lg);
          
          .category-icon {
            transform: scale(1.1);
          }
        }
        
        .category-icon {
          width: 56px;
          height: 56px;
          border-radius: var(--radius-md);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          transition: transform var(--transition-normal);
        }
        
        .category-name {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-secondary);
        }
      }
    }
  }
  
  .books-section {
    background: var(--bg-secondary);
    
    &.alt-bg {
      background: var(--bg-primary);
    }
  }
  
  .books-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }
  
  .features-section {
    background: var(--bg-secondary);
    
    .features-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 32px;
      
      .feature-item {
        text-align: center;
        padding: 32px 24px;
        background: var(--bg-card);
        border-radius: var(--radius-md);
        transition: all var(--transition-normal);
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: var(--shadow-md);
        }
        
        .feature-icon {
          width: 56px;
          height: 56px;
          margin: 0 auto 16px;
          background: var(--bg-primary);
          border-radius: var(--radius-md);
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--primary-color);
        }
        
        h3 {
          margin: 0 0 8px;
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
        }
        
        p {
          margin: 0;
          font-size: 14px;
          color: var(--text-muted);
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .home-page {
    .category-grid {
      grid-template-columns: repeat(4, 1fr) !important;
    }
    .books-grid {
      grid-template-columns: repeat(3, 1fr) !important;
    }
  }
}

@media (max-width: 768px) {
  .home-page {
    .hero-banner {
      .banner-content {
        h1 {
          font-size: 32px;
        }
        
        p {
          font-size: 14px;
        }
      }
    }
    
    .category-grid {
      grid-template-columns: repeat(2, 1fr) !important;
    }
    .books-grid {
      grid-template-columns: repeat(2, 1fr) !important;
    }
    .features-grid {
      grid-template-columns: repeat(2, 1fr) !important;
    }
  }
}
</style>
