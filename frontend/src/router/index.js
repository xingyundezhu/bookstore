/**
 * 路由配置模块
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

/**
 * 路由配置数组
 * @type {Array<Object>}
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/books',
    name: 'BookList',
    component: () => import('@/views/BookList.vue'),
    meta: { title: '图书列表' }
  },
  {
    path: '/books/:id',
    name: 'BookDetail',
    component: () => import('@/views/BookDetail.vue'),
    meta: { title: '图书详情' }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { title: '搜索结果' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/Checkout.vue'),
    meta: { title: '结算', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('@/views/user/Layout.vue'),
    meta: { title: '用户中心', requiresAuth: true },
    children: [
      {
        path: '',
        name: 'UserHome',
        redirect: '/user/orders'
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'cart',
        name: 'UserCart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '我的购物车' }
      },
      {
        path: 'addresses',
        name: 'UserAddresses',
        component: () => import('@/views/user/Addresses.vue'),
        meta: { title: '收货地址' }
      },
      {
        path: 'favorites',
        name: 'UserFavorites',
        component: () => import('@/views/user/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'reviews',
        name: 'UserReviews',
        component: () => import('@/views/user/Reviews.vue'),
        meta: { title: '我的评价' }
      },
      {
        path: 'security',
        name: 'UserSecurity',
        component: () => import('@/views/user/Security.vue'),
        meta: { title: '账户安全' }
      }
    ]
  },
  {
    path: '/orders',
    redirect: '/user/orders'
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'books',
        name: 'AdminBooks',
        component: () => import('@/views/admin/Books.vue'),
        meta: { title: '图书管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/Categories.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'sales',
        name: 'AdminSales',
        component: () => import('@/views/admin/Sales.vue'),
        meta: { title: '销量管理' }
      },
      {
        path: 'reviews',
        name: 'AdminReviews',
        component: () => import('@/views/admin/Reviews.vue'),
        meta: { title: '评价管理' }
      }
    ]
  }
]

/**
 * Vue Router实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes,
  /**
   * 滚动行为配置
   * @param {Object} to - 目标路由
   * @param {Object} from - 来源路由
   * @param {Object} savedPosition - 保存的滚动位置
   * @returns {Object} 滚动位置
   */
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

/**
 * 全局前置守卫
 * 在路由跳转前执行权限检查：
 * - 设置页面标题
 * - 检查是否需要登录（requiresAuth）
 * - 检查是否是游客页面（guest）
 * - 检查是否需要管理员权限（requiresAdmin）
 */
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 网上图书系统` : '网上图书系统'
  
  const userStore = useUserStore()
  const isValid = userStore.checkToken()
  
  // 需要登录但未登录，跳转到登录页
  if (to.meta.requiresAuth && !isValid) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  // 游客页面但已登录，跳转到首页
  } else if (to.meta.guest && isValid) {
    next({ name: 'Home' })
  // 需要管理员权限但不是管理员，跳转到首页
  } else if (to.meta.requiresAdmin && userStore.user?.role !== 'ADMIN') {
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
