<template>
  <div class="user-orders">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>我的订单</h2>
      </div>
    </div>
    
    <div class="order-tabs">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane name="0">
          <template #label>
            <span>待付款 <el-badge v-if="statusCounts[0]" :value="statusCounts[0]" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="1">
          <template #label>
            <span>待发货 <el-badge v-if="statusCounts[1]" :value="statusCounts[1]" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane name="2">
          <template #label>
            <span>待收货 <el-badge v-if="statusCounts[2]" :value="statusCounts[2]" /></span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>
    </div>
    
    <div class="orders-list" v-loading="loading">
      <template v-if="orders.length > 0">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatDate(order.createTime) }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" size="small">
              {{ order.statusText }}
            </el-tag>
          </div>
          
          <div class="order-items">
            <div v-for="item in order.orderItems" :key="item.id" class="order-item">
              <img 
                :src="item.bookCover || defaultCover" 
                :alt="item.bookTitle" 
                class="item-cover"
                @error="handleImageError"
              />
              <div class="item-info">
                <div class="item-title">{{ item.bookTitle }}</div>
                <div class="item-meta">
                  <span class="item-price">¥{{ item.price }}</span>
                  <span class="item-quantity">x{{ item.quantity }}</span>
                </div>
              </div>
              <div class="item-subtotal">¥{{ item.totalAmount }}</div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-total">
              <span>共 {{ order.orderItems.length }} 件商品</span>
              <span class="total-amount">
                实付：<strong>¥{{ order.payAmount }}</strong>
              </span>
            </div>
            <div class="order-actions">
              <template v-if="order.status === 0">
                <el-button type="danger" size="small" @click="handlePay(order)">
                  立即付款
                </el-button>
                <el-button size="small" @click="handleCancel(order)">
                  取消订单
                </el-button>
              </template>
              <template v-if="order.status === 1">
                <el-button size="small" disabled>等待发货</el-button>
              </template>
              <template v-if="order.status === 2">
                <el-button type="primary" size="small" @click="handleReceive(order)">
                  确认收货
                </el-button>
                <el-button size="small" @click="handleTrack(order)">
                  查看物流
                </el-button>
              </template>
              <template v-if="order.status === 3">
                <el-button type="primary" size="small" plain @click="handleReview(order)">
                  评价
                </el-button>
                <el-button size="small" @click="handleRebuy(order)">
                  再次购买
                </el-button>
              </template>
              <el-button size="small" text @click="handleDetail(order)">
                订单详情
              </el-button>
            </div>
          </div>
        </div>
      </template>
      
      <el-empty v-else description="暂无订单">
        <el-button type="primary" @click="$router.push('/books')">去购物</el-button>
      </el-empty>
    </div>
    
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>
    
    <el-dialog v-model="showPayDialog" title="订单支付" width="400px">
      <div class="pay-content">
        <div class="pay-amount">
          <span>支付金额：</span>
          <strong>¥{{ currentOrder?.payAmount }}</strong>
        </div>
        <div class="pay-method">
          <el-radio-group v-model="payMethod">
            <el-radio :label="1">支付宝</el-radio>
            <el-radio :label="2">微信支付</el-radio>
          </el-radio-group>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">
          确认支付
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showReviewDialog" title="订单评价" width="600px">
      <div class="review-content">
        <div v-for="item in reviewItems" :key="item.bookId" class="review-item">
          <div class="book-info">
            <img :src="item.bookCover || defaultCover" class="book-cover" />
            <span class="book-title">{{ item.bookTitle }}</span>
          </div>
          <div class="review-input">
            <el-rate v-model="item.rating" />
            <el-input
              v-model="item.content"
              type="textarea"
              :rows="2"
              placeholder="请输入评价内容"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewing">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'
import { cartApi } from '@/api/cart'
import { reviewApi } from '@/api/review'

const router = useRouter()
const defaultCover = 'https://picsum.photos/seed/order/60/80'

const orders = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeStatus = ref('all')
const statusCounts = ref({})

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const showPayDialog = ref(false)
const currentOrder = ref(null)
const payMethod = ref(1)
const paying = ref(false)

const showReviewDialog = ref(false)
const reviewItems = ref([])
const reviewing = ref(false)

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const status = activeStatus.value !== 'all' ? parseInt(activeStatus.value) : null
    const res = await orderApi.getUserList({ ...params, status })
    
    if (res.code === 200) {
      orders.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('获取订单失败', error)
  } finally {
    loading.value = false
  }
}

const fetchStatusCounts = async () => {
  try {
    const [res0, res1, res2] = await Promise.all([
      orderApi.getUserList({ status: 0, page: 0, size: 1 }),
      orderApi.getUserList({ status: 1, page: 0, size: 1 }),
      orderApi.getUserList({ status: 2, page: 0, size: 1 })
    ])
    
    statusCounts.value = {
      0: res0.code === 200 ? res0.data.totalElements : 0,
      1: res1.code === 200 ? res1.data.totalElements : 0,
      2: res2.code === 200 ? res2.data.totalElements : 0
    }
  } catch (error) {
    console.error('获取订单统计失败', error)
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchOrders()
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }
  return types[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const handleImageError = (e) => {
  e.target.src = defaultCover
}

const handlePay = (order) => {
  currentOrder.value = order
  showPayDialog.value = true
}

const confirmPay = async () => {
  paying.value = true
  try {
    const res = await orderApi.pay(currentOrder.value.orderNo)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      showPayDialog.value = false
      fetchOrders()
      fetchStatusCounts()
    }
  } catch (error) {
    console.error('支付失败', error)
  } finally {
    paying.value = false
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    const res = await orderApi.cancel(order.orderNo)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      fetchOrders()
      fetchStatusCounts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败', error)
    }
  }
}

const handleReceive = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', { type: 'info' })
    const res = await orderApi.receive(order.orderNo)
    if (res.code === 200) {
      ElMessage.success('已确认收货')
      fetchOrders()
      fetchStatusCounts()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败', error)
    }
  }
}

const handleTrack = (order) => {
  ElMessage.info('物流功能开发中')
}

const handleDetail = (order) => {
  ElMessage.info('订单详情功能开发中')
}

const handleReview = (order) => {
  currentOrder.value = order
  reviewItems.value = order.orderItems.map(item => ({
    bookId: item.bookId,
    bookTitle: item.bookTitle,
    bookCover: item.bookCover,
    orderId: order.id,
    rating: 5,
    content: ''
  }))
  showReviewDialog.value = true
}

const submitReview = async () => {
  reviewing.value = true
  try {
    for (const item of reviewItems.value) {
      if (!item.content || !item.content.trim()) {
        ElMessage.warning('请填写评价内容')
        reviewing.value = false
        return
      }
      const res = await reviewApi.add({
        bookId: item.bookId,
        orderId: item.orderId,
        rating: item.rating,
        content: item.content
      })
      if (res.code !== 200) {
        throw new Error(res.message || '评价失败')
      }
    }
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    fetchOrders()
  } catch (error) {
    console.error('评价失败', error)
    ElMessage.error('评价失败：' + (error.message || '请稍后重试'))
  } finally {
    reviewing.value = false
  }
}

const handleRebuy = async (order) => {
  try {
    for (const item of order.orderItems) {
      await cartApi.add(item.bookId, item.quantity)
    }
    ElMessage.success('已添加到购物车')
  } catch (error) {
    console.error('添加失败', error)
  }
}

onMounted(() => {
  fetchOrders()
  fetchStatusCounts()
})
</script>

<style lang="scss" scoped>
.user-orders {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  
  .page-header {
    margin-bottom: 20px;
    
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
        margin: 0;
      }
    }
  }
  
  .order-tabs {
    margin-bottom: 20px;
  }
  
  .orders-list {
    min-height: 300px;
  }
  
  .order-card {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 16px;
    overflow: hidden;
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      background: #f5f7fa;
      border-bottom: 1px solid #ebeef5;
      
      .order-info {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .order-no {
          font-weight: 500;
        }
        
        .order-time {
          color: #909399;
          font-size: 13px;
        }
      }
    }
    
    .order-items {
      padding: 16px;
      
      .order-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .item-cover {
          width: 60px;
          height: 80px;
          object-fit: cover;
          border-radius: 4px;
        }
        
        .item-info {
          flex: 1;
          margin-left: 16px;
          
          .item-title {
            font-size: 14px;
            color: #303133;
            margin-bottom: 8px;
          }
          
          .item-meta {
            display: flex;
            gap: 16px;
            font-size: 13px;
            color: #909399;
          }
        }
        
        .item-subtotal {
          color: #f56c6c;
          font-weight: 500;
        }
      }
    }
    
    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      background: #fafafa;
      border-top: 1px solid #ebeef5;
      
      .order-total {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 14px;
        color: #606266;
        
        .total-amount strong {
          color: #f56c6c;
          font-size: 18px;
        }
      }
      
      .order-actions {
        display: flex;
        gap: 10px;
      }
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }
  
  .pay-content {
    .pay-amount {
      text-align: center;
      margin-bottom: 20px;
      
      strong {
        font-size: 28px;
        color: #f56c6c;
      }
    }
    
    .pay-method {
      display: flex;
      justify-content: center;
    }
  }
  
  .review-content {
    .review-item {
      padding: 16px 0;
      border-bottom: 1px solid #ebeef5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .book-info {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 12px;
        
        .book-cover {
          width: 50px;
          height: 65px;
          object-fit: cover;
          border-radius: 4px;
        }
        
        .book-title {
          font-size: 14px;
        }
      }
      
      .review-input {
        .el-rate {
          margin-bottom: 12px;
        }
      }
    }
  }
}
</style>
