<template>
  <footer class="footer-bar">
    <div class="footer-container">
      <div class="footer-main">
        <div class="footer-brand">
          <div class="brand-logo">
            <div class="logo-icon">
              <el-icon :size="20"><Reading /></el-icon>
            </div>
            <span class="logo-text">BOOKSTORE</span>
          </div>
          <p class="brand-desc">发现阅读的乐趣，探索知识的海洋</p>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4>快速链接</h4>
            <router-link to="/">首页</router-link>
            <router-link to="/books">全部图书</router-link>
            <router-link to="/books?sort=newest">新书上架</router-link>
            <router-link to="/books?sort=sales">热销榜单</router-link>
          </div>
          
          <div class="link-group">
            <h4>用户服务</h4>
            <router-link to="/cart">购物车</router-link>
            <router-link to="/user/orders">我的订单</router-link>
            <router-link to="/user/favorites">我的收藏</router-link>
            <router-link to="/user/security">账户安全</router-link>
          </div>
          
          <div class="link-group">
            <h4>帮助中心</h4>
            <a href="javascript:;">购物指南</a>
            <a href="javascript:;">配送说明</a>
            <a href="javascript:;">退换货政策</a>
            <a href="javascript:;">联系客服</a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} BOOKSTORE. All rights reserved.</p>
        <div class="bottom-links">
          <a href="javascript:;">隐私政策</a>
          <span class="divider">|</span>
          <a href="javascript:;">服务条款</a>
          <span class="divider">|</span>
          <a href="javascript:;">网站地图</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style lang="scss" scoped>
.footer-bar {
  background: var(--primary-color);
  color: rgba(255, 255, 255, 0.85);
  margin-top: auto;
  
  .footer-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px;
  }
  
  .footer-main {
    display: flex;
    justify-content: space-between;
    padding: 48px 0;
    gap: 80px;
  }
  
  .footer-brand {
    max-width: 280px;
    
    .brand-logo {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 16px;
      
      .logo-icon {
        width: 36px;
        height: 36px;
        background: rgba(255, 255, 255, 0.15);
        border-radius: var(--radius-sm);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }
      
      .logo-text {
        font-size: 16px;
        font-weight: 700;
        color: #fff;
        letter-spacing: 0.05em;
      }
    }
    
    .brand-desc {
      font-size: 14px;
      line-height: 1.6;
      color: rgba(255, 255, 255, 0.6);
    }
  }
  
  .footer-links {
    display: flex;
    gap: 80px;
    
    .link-group {
      h4 {
        font-size: 14px;
        font-weight: 600;
        color: #fff;
        margin-bottom: 20px;
        letter-spacing: 0.02em;
      }
      
      a {
        display: block;
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);
        margin-bottom: 12px;
        transition: color var(--transition-fast);
        
        &:hover {
          color: #fff;
        }
      }
    }
  }
  
  .footer-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 0;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    
    p {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.5);
    }
    
    .bottom-links {
      display: flex;
      align-items: center;
      gap: 16px;
      
      a {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.5);
        transition: color var(--transition-fast);
        
        &:hover {
          color: #fff;
        }
      }
      
      .divider {
        color: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

@media (max-width: 768px) {
  .footer-bar {
    .footer-main {
      flex-direction: column;
      gap: 40px;
    }
    
    .footer-links {
      flex-wrap: wrap;
      gap: 40px;
    }
    
    .footer-bottom {
      flex-direction: column;
      gap: 12px;
      text-align: center;
    }
  }
}
</style>
