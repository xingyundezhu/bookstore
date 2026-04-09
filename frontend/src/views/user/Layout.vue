<template>
  <div class="user-layout">
    <div class="user-container">
      <aside class="user-sidebar">
        <div class="user-info">
          <el-avatar :size="64" :src="userStore.user?.avatar || defaultAvatar">
            {{ userStore.user?.username?.charAt(0).toUpperCase() }}
          </el-avatar>
          <div class="user-name">{{ userStore.user?.nickname || userStore.user?.username }}</div>
          <div class="user-role">
            <el-tag size="small" :type="userStore.isAdmin ? 'danger' : 'primary'">
              {{ userStore.isAdmin ? '管理员' : '普通用户' }}
            </el-tag>
          </div>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          router
          class="user-menu"
        >
          <el-menu-item index="/user/orders">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/user/cart">
            <el-icon><ShoppingCart /></el-icon>
            <span>我的购物车</span>
          </el-menu-item>
          <el-menu-item index="/user/addresses">
            <el-icon><Location /></el-icon>
            <span>收货地址</span>
          </el-menu-item>
          <el-menu-item index="/user/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="/user/reviews">
            <el-icon><ChatDotRound /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
          <el-divider />
          <el-menu-item index="/user/security">
            <el-icon><Lock /></el-icon>
            <span>账户安全</span>
          </el-menu-item>
        </el-menu>
        
        <div class="sidebar-footer">
          <el-button text type="danger" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </aside>
      
      <main class="user-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeMenu = computed(() => {
  return route.path
})

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style lang="scss" scoped>
.user-layout {
  min-height: calc(100vh - 72px - 200px);
  background: var(--bg-primary);
  padding: 24px 0;
  
  .user-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    gap: 24px;
  }
  
  .user-sidebar {
    width: 240px;
    flex-shrink: 0;
    background: var(--bg-card);
    border-radius: var(--radius-md);
    padding: 24px;
    height: fit-content;
    position: sticky;
    top: 96px;
    
    .user-info {
      text-align: center;
      padding-bottom: 20px;
      border-bottom: 1px solid var(--border-color);
      margin-bottom: 10px;
      
      .user-name {
        margin-top: 12px;
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }
      
      .user-role {
        margin-top: 8px;
      }
    }
    
    .user-menu {
      border: none;
      background: transparent;
      
      .el-menu-item {
        height: 44px;
        line-height: 44px;
        margin: 4px 0;
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
      
      .el-divider {
        margin: 12px 0;
        border-color: var(--border-color);
      }
    }
    
    .sidebar-footer {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid var(--border-color);
    }
  }
  
  .user-content {
    flex: 1;
    min-width: 0;
  }
}

@media (max-width: 768px) {
  .user-layout {
    .user-container {
      flex-direction: column;
    }
    
    .user-sidebar {
      width: 100%;
      position: static;
    }
  }
}
</style>
