<template>
  <div class="cart-page">
    <div class="page-container">
      <h2>我的购物车</h2>
      
      <div class="cart-content" v-loading="loading">
        <div class="cart-list" v-if="cartItems.length > 0">
          <div class="cart-header">
            <el-checkbox v-model="allSelected" @change="handleSelectAll">全选</el-checkbox>
            <span class="col-product">商品信息</span>
            <span class="col-price">单价</span>
            <span class="col-quantity">数量</span>
            <span class="col-subtotal">小计</span>
            <span class="col-action">操作</span>
          </div>
          
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <el-checkbox v-model="item.selected" @change="handleSelectItem(item)" />
            <div class="col-product">
              <img 
                :src="item.bookCover || defaultCover" 
                :alt="item.bookTitle" 
                class="book-cover"
                @error="handleImageError"
              />
              <div class="book-info">
                <router-link :to="`/books/${item.bookId}`" class="book-title">{{ item.bookTitle }}</router-link>
                <div class="book-stock" :class="{ 'low-stock': item.stock < 10 }">
                  库存: {{ item.stock }}件
                </div>
              </div>
            </div>
            <div class="col-price">
              <span class="price">¥{{ item.price }}</span>
            </div>
            <div class="col-quantity">
              <el-input-number 
                v-model="item.quantity" 
                :min="1" 
                :max="item.stock"
                size="small"
                @change="(val) => updateQuantity(item.bookId, val)"
              />
            </div>
            <div class="col-subtotal">
              <span class="subtotal">¥{{ calculateSubtotal(item) }}</span>
            </div>
            <div class="col-action">
              <el-button type="danger" text @click="removeItem(item.bookId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
        
        <el-empty v-else description="购物车是空的">
          <el-button type="primary" @click="$router.push('/books')">
            <el-icon><ShoppingCart /></el-icon>
            去选购
          </el-button>
        </el-empty>
      </div>
      
      <div class="cart-footer" v-if="cartItems.length > 0">
        <div class="footer-left">
          <el-button text type="danger" @click="clearCart">
            <el-icon><Delete /></el-icon>
            清空购物车
          </el-button>
        </div>
        <div class="footer-right">
          <div class="summary">
            <div class="summary-item">
              <span>已选择 <strong class="highlight">{{ selectedCount }}</strong> 件商品</span>
            </div>
            <div class="summary-item">
              <span>商品总额：</span>
              <span class="price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <div class="summary-item total">
              <span>应付金额：</span>
              <span class="final-price">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
          </div>
          <el-button 
            type="danger" 
            size="large" 
            @click="checkout" 
            :disabled="selectedCount === 0"
            class="checkout-btn"
          >
            去结算 ({{ selectedCount }})
          </el-button>
        </div>
      </div>
      
      <div class="recommend-section" v-if="recommendBooks.length > 0">
        <h3>猜你喜欢</h3>
        <div class="recommend-list">
          <div v-for="book in recommendBooks" :key="book.id" class="recommend-item" @click="goToBook(book.id)">
            <img :src="book.coverImage || defaultCover" :alt="book.title" @error="handleImageError" />
            <div class="recommend-info">
              <div class="recommend-title">{{ book.title }}</div>
              <div class="recommend-price">¥{{ book.price }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useCartStore } from '@/store/cart'
import { cartApi } from '@/api/cart'
import { bookApi } from '@/api/book'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const defaultCover = 'https://picsum.photos/seed/cart/80/100'

const cartItems = ref([])
const loading = ref(false)
const recommendBooks = ref([])

const allSelected = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: () => {}
})

const selectedCount = computed(() => cartItems.value.filter(item => item.selected).length)

const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + calculateSubtotal(item), 0)
})

const calculateSubtotal = (item) => {
  return (item.price || 0) * (item.quantity || 1)
}

const handleImageError = (e) => {
  e.target.src = defaultCover
}

const fetchCart = async () => {
  if (!userStore.checkToken()) {
    router.push('/login')
    return
  }
  
  loading.value = true
  try {
    const res = await cartApi.getList()
    if (res.code === 200) {
      cartItems.value = res.data.map(item => ({
        ...item,
        selected: item.selected === 1
      }))
    }
  } catch (error) {
    console.error('获取购物车失败', error)
    if (error.response?.status === 401) {
      userStore.clearAuth()
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const fetchRecommendBooks = async () => {
  try {
    const res = await bookApi.getHot(6)
    if (res.code === 200) {
      recommendBooks.value = res.data
    }
  } catch (error) {
    console.error('获取推荐失败', error)
  }
}

const handleSelectAll = async (val) => {
  try {
    await cartApi.updateAllSelected(val ? 1 : 0)
    cartItems.value.forEach(item => item.selected = val)
  } catch (error) {
    console.error('更新失败', error)
    ElMessage.error('操作失败，请重试')
  }
}

const handleSelectItem = async (item) => {
  try {
    await cartApi.updateSelected(item.bookId, item.selected ? 1 : 0)
  } catch (error) {
    console.error('更新失败', error)
    ElMessage.error('操作失败，请重试')
  }
}

const updateQuantity = async (bookId, quantity) => {
  if (quantity < 1) return
  
  try {
    await cartApi.updateQuantity(bookId, quantity)
    const item = cartItems.value.find(i => i.bookId === bookId)
    if (item) {
      ElMessage.success('数量已更新')
    }
  } catch (error) {
    console.error('更新数量失败', error)
    ElMessage.error('库存不足或操作失败')
    fetchCart()
  }
}

const removeItem = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { 
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await cartApi.remove(bookId)
    cartItems.value = cartItems.value.filter(item => item.bookId !== bookId)
    cartStore.fetchCartCount()
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', { 
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await cartApi.clear()
    cartItems.value = []
    cartStore.clearCart()
    ElMessage.success('购物车已清空')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空失败', error)
      ElMessage.error('操作失败，请重试')
    }
  }
}

const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请选择要购买的商品')
    return
  }
  
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  router.push('/checkout')
}

const goToBook = (bookId) => {
  router.push(`/books/${bookId}`)
}

onMounted(() => {
  fetchCart()
  fetchRecommendBooks()
})
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: calc(100vh - 64px - 200px);
  background: #f5f5f5;
  padding: 40px 0;
  
  .page-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    
    h2 {
      margin-bottom: 20px;
    }
  }
  
  .cart-content {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    min-height: 300px;
  }
  
  .cart-header {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 2px solid #ebeef5;
    font-weight: 500;
    color: #606266;
    
    .col-product { flex: 1; margin-left: 20px; }
    .col-price { width: 120px; text-align: center; }
    .col-quantity { width: 150px; text-align: center; }
    .col-subtotal { width: 120px; text-align: center; }
    .col-action { width: 100px; text-align: center; }
  }
  
  .cart-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #ebeef5;
    
    .col-product {
      flex: 1;
      display: flex;
      align-items: center;
      margin-left: 20px;
      
      .book-cover {
        width: 80px;
        height: 100px;
        object-fit: cover;
        border-radius: 4px;
      }
      
      .book-info {
        margin-left: 16px;
        
        .book-title {
          display: block;
          font-size: 14px;
          color: #303133;
          text-decoration: none;
          margin-bottom: 8px;
          
          &:hover {
            color: #409eff;
          }
        }
        
        .book-stock {
          font-size: 12px;
          color: #909399;
          
          &.low-stock {
            color: #f56c6c;
          }
        }
      }
    }
    
    .col-price {
      width: 120px;
      text-align: center;
      
      .price {
        font-size: 16px;
        color: #f56c6c;
      }
    }
    
    .col-quantity {
      width: 150px;
      display: flex;
      justify-content: center;
    }
    
    .col-subtotal {
      width: 120px;
      text-align: center;
      
      .subtotal {
        font-size: 16px;
        font-weight: 500;
        color: #f56c6c;
      }
    }
    
    .col-action {
      width: 100px;
      text-align: center;
    }
  }
  
  .cart-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    background: #fff;
    border-radius: 8px;
    margin-top: 20px;
    
    .footer-right {
      display: flex;
      align-items: center;
      gap: 24px;
      
      .summary {
        text-align: right;
        
        .summary-item {
          margin-bottom: 8px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .highlight {
            color: #f56c6c;
          }
          
          .price {
            font-size: 16px;
            color: #f56c6c;
          }
          
          &.total {
            .final-price {
              font-size: 24px;
              font-weight: 600;
              color: #f56c6c;
            }
          }
        }
      }
      
      .checkout-btn {
        width: 140px;
        height: 50px;
        font-size: 16px;
      }
    }
  }
  
  .recommend-section {
    margin-top: 40px;
    
    h3 {
      margin-bottom: 20px;
    }
    
    .recommend-list {
      display: grid;
      grid-template-columns: repeat(6, 1fr);
      gap: 16px;
      
      .recommend-item {
        background: #fff;
        border-radius: 8px;
        overflow: hidden;
        cursor: pointer;
        transition: transform 0.3s;
        
        &:hover {
          transform: translateY(-4px);
        }
        
        img {
          width: 100%;
          height: 150px;
          object-fit: cover;
        }
        
        .recommend-info {
          padding: 12px;
          
          .recommend-title {
            font-size: 14px;
            color: #303133;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          
          .recommend-price {
            font-size: 14px;
            color: #f56c6c;
            margin-top: 8px;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .cart-page {
    .cart-header {
      display: none;
    }
    
    .cart-item {
      flex-wrap: wrap;
      
      .col-product {
        width: 100%;
        margin-left: 0;
        margin-bottom: 12px;
      }
      
      .col-price, .col-quantity, .col-subtotal, .col-action {
        width: 25%;
      }
    }
    
    .recommend-list {
      grid-template-columns: repeat(3, 1fr) !important;
    }
  }
}
</style>
