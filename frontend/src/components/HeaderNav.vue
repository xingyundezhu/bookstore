<template>
  <header class="header-nav">
    <div class="header-container">
      <div class="logo" @click="$router.push('/')">
        <div class="logo-icon">
          <el-icon :size="24"><Reading /></el-icon>
        </div>
        <span class="logo-text">BOOKSTORE</span>
      </div>
      
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索图书、作者..."
          @keyup.enter="handleSearch"
          clearable
          size="large"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <nav class="nav-menu">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/books" class="nav-item">全部图书</router-link>
        
        <template v-if="userStore.isLoggedIn">
          <router-link to="/cart" class="nav-item cart-link">
            <el-badge :value="cartStore.cartCount" :hidden="cartStore.cartCount === 0" :max="99">
              <el-icon :size="20"><ShoppingCart /></el-icon>
            </el-badge>
          </router-link>
          
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar">
              <el-avatar :size="36" :src="userStore.user?.avatar || defaultAvatar">
                {{ userStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <div class="dropdown-header">
                  <span class="dropdown-username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
                  <span class="dropdown-email">{{ userStore.user?.email || '未绑定邮箱' }}</span>
                </div>
                <el-dropdown-item command="userCenter">
                  <el-icon><User /></el-icon>
                  <span>用户中心</span>
                </el-dropdown-item>
                <el-dropdown-item command="orders">
                  <el-icon><Document /></el-icon>
                  <span>我的订单</span>
                </el-dropdown-item>
                <el-dropdown-item command="cart">
                  <el-icon><ShoppingCart /></el-icon>
                  <span>购物车</span>
                </el-dropdown-item>
                <el-dropdown-item command="favorites">
                  <el-icon><Star /></el-icon>
                  <span>我的收藏</span>
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>
                  <el-icon><Setting /></el-icon>
                  <span>管理后台</span>
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        
        <template v-else>
          <router-link to="/login" class="nav-item">登录</router-link>
          <router-link to="/register" class="register-btn">
            <span>立即注册</span>
          </router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const searchKeyword = ref('')
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value } })
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'userCenter':
      router.push('/user/orders')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'cart':
      router.push('/cart')
      break
    case 'favorites':
      router.push('/user/favorites')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      cartStore.clearCart()
      break
  }
}

watch(() => userStore.isLoggedIn, (isLoggedIn) => {
  if (isLoggedIn) {
    cartStore.fetchCartCount()
  } else {
    cartStore.clearCart()
  }
})

onMounted(() => {
  if (userStore.checkToken()) {
    cartStore.fetchCartCount()
  }
})
</script>

<style lang="scss" scoped>
.header-nav {
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  
  .header-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 24px;
    height: 72px;
    display: flex;
    align-items: center;
    gap: 48px;
  }
  
  .logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    
    .logo-icon {
      width: 40px;
      height: 40px;
      background: var(--primary-color);
      border-radius: var(--radius-sm);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    .logo-text {
      font-size: 18px;
      font-weight: 700;
      color: var(--primary-color);
      letter-spacing: 0.05em;
    }
  }
  
  .search-box {
    flex: 1;
    max-width: 480px;
    
    .el-input {
      :deep(.el-input__wrapper) {
        border-radius: var(--radius-xl);
        background: var(--bg-primary);
        border: none;
        padding: 4px 16px;
        box-shadow: none;
        
        &:hover, &.is-focus {
          background: #fff;
          box-shadow: var(--shadow-md);
        }
      }
      
      :deep(.el-input__inner) {
        font-size: 14px;
        
        &::placeholder {
          color: var(--text-muted);
        }
      }
    }
  }
  
  .nav-menu {
    display: flex;
    align-items: center;
    gap: 32px;
    
    .nav-item {
      color: var(--text-secondary);
      font-size: 14px;
      font-weight: 500;
      transition: color var(--transition-fast);
      position: relative;
      
      &:hover, &.router-link-active {
        color: var(--primary-color);
        
        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 0;
          right: 0;
          height: 2px;
          background: var(--primary-color);
          border-radius: 1px;
        }
      }
      
      &.cart-link {
        display: flex;
        align-items: center;
        
        &::after {
          display: none;
        }
        
        &:hover {
          color: var(--accent-color);
        }
      }
    }
    
    .register-btn {
      background: var(--primary-color);
      color: #fff;
      padding: 10px 24px;
      border-radius: var(--radius-xl);
      font-size: 14px;
      font-weight: 500;
      transition: all var(--transition-fast);
      
      &:hover {
        background: var(--primary-light);
        transform: translateY(-1px);
        box-shadow: var(--shadow-md);
        
        &::after {
          display: none;
        }
      }
    }
    
    .user-avatar {
      cursor: pointer;
      padding: 4px;
      border-radius: 50%;
      transition: all var(--transition-fast);
      
      &:hover {
        background: var(--bg-primary);
      }
    }
  }
}

.dropdown-header {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  
  .dropdown-username {
    display: block;
    font-weight: 600;
    color: var(--text-primary);
    font-size: 14px;
  }
  
  .dropdown-email {
    display: block;
    font-size: 12px;
    color: var(--text-muted);
    margin-top: 2px;
  }
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  font-size: 14px;
  
  .el-icon {
    font-size: 16px;
    color: var(--text-muted);
  }
  
  &:hover {
    background: var(--bg-primary);
    color: var(--primary-color);
    
    .el-icon {
      color: var(--primary-color);
    }
  }
}
</style>
