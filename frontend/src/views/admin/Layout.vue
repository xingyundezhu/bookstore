<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px">
        <div class="logo">
          <el-icon :size="24"><Setting /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
        >
          <el-menu-item index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          <el-menu-item index="/admin/books">
            <el-icon><Notebook /></el-icon>
            <span>图书管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/sales">
            <el-icon><TrendCharts /></el-icon>
            <span>销量管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reviews">
            <el-icon><ChatDotRound /></el-icon>
            <span>评价管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
            <div class="header-right">
              <router-link to="/" class="back-link">
                <el-icon><Back /></el-icon>
                返回前台
              </router-link>
              <el-dropdown @command="handleCommand">
                <span class="user-info">
                  <el-avatar :size="32">{{ userStore.user?.username?.charAt(0) }}</el-avatar>
                  <span>{{ userStore.user?.username }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title || '仪表盘')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
  
  .el-aside {
    background: #304156;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      border-bottom: 1px solid #3a4a5e;
    }
    
    .el-menu {
      border: none;
    }
  }
  
  .el-header {
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    padding: 0 20px;
    
    .header-content {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    
    .header-right {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .back-link {
        display: flex;
        align-items: center;
        gap: 4px;
        color: #606266;
        text-decoration: none;
        
        &:hover {
          color: #409eff;
        }
      }
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
      }
    }
  }
  
  .el-main {
    background: #f0f2f5;
    padding: 20px;
  }
}
</style>
