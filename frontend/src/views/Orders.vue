<template>
  <div class="orders-page">
    <div class="page-container">
      <h2>我的订单</h2>
      
      <div class="order-tabs">
        <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
          <el-tab-pane label="全部订单" name="" />
          <el-tab-pane label="待付款" name="0" />
          <el-tab-pane label="待发货" name="1" />
          <el-tab-pane label="待收货" name="2" />
          <el-tab-pane label="已完成" name="3" />
        </el-tabs>
      </div>
      
      <div class="orders-list" v-loading="loading">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatDate(order.createTime) }}</span>
            <el-tag :type="getStatusType(order.status)">{{ order.statusText }}</el-tag>
          </div>
          
          <div class="order-items">
            <div v-for="item in order.orderItems" :key="item.id" class="order-item">
              <img :src="item.bookCover || defaultCover" :alt="item.bookTitle" class="item-cover" />
              <div class="item-info">
                <span class="item-title">{{ item.bookTitle }}</span>
                <span class="item-price">¥{{ item.price }} x {{ item.quantity }}</span>
              </div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-total">
              共 {{ order.orderItems.length }} 件商品，实付：<strong>¥{{ order.payAmount }}</strong>
            </div>
            <div class="order-actions">
              <el-button v-if="order.status === 0" type="danger" @click="payOrder(order.orderNo)">
                立即付款
              </el-button>
              <el-button v-if="order.status === 0" @click="cancelOrder(order.orderNo)">
                取消订单
              </el-button>
              <el-button v-if="order.status === 2" type="primary" @click="receiveOrder(order.orderNo)">
                确认收货
              </el-button>
              <el-button v-if="order.status === 3" type="primary" @click="goToReview(order)">
                评价
              </el-button>
              <el-button @click="viewDetail(order)">查看详情</el-button>
            </div>
          </div>
        </div>
        
        <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const router = useRouter()
const defaultCover = 'https://via.placeholder.com/60x80/409eff/ffffff?text=Book'

const orders = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeStatus = ref('')

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    const status = activeStatus.value !== '' ? parseInt(activeStatus.value) : null
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

const payOrder = async (orderNo) => {
  try {
    await ElMessageBox.confirm('确认支付该订单吗？', '支付确认', { type: 'info' })
    const res = await orderApi.pay(orderNo)
    if (res.code === 200) {
      ElMessage.success('支付成功')
      fetchOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败', error)
    }
  }
}

const cancelOrder = async (orderNo) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消订单', { type: 'warning' })
    const res = await orderApi.cancel(orderNo)
    if (res.code === 200) {
      ElMessage.success('订单已取消')
      fetchOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败', error)
    }
  }
}

const receiveOrder = async (orderNo) => {
  try {
    await ElMessageBox.confirm('确认已收到商品吗？', '确认收货', { type: 'info' })
    const res = await orderApi.receive(orderNo)
    if (res.code === 200) {
      ElMessage.success('已确认收货')
      fetchOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败', error)
    }
  }
}

const goToReview = (order) => {
  router.push({ name: 'Profile', query: { tab: 'reviews', orderId: order.id } })
}

const viewDetail = (order) => {
  ElMessage.info('订单详情功能开发中')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.orders-page {
  min-height: calc(100vh - 64px - 200px);
  background: #f5f5f5;
  padding: 40px 0;
  
  .page-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 20px;
    
    h2 {
      margin-bottom: 20px;
    }
  }
  
  .order-tabs {
    background: #fff;
    border-radius: 8px;
    padding: 0 20px;
    margin-bottom: 20px;
  }
  
  .orders-list {
    .order-card {
      background: #fff;
      border-radius: 8px;
      margin-bottom: 16px;
      overflow: hidden;
      
      .order-header {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px 20px;
        background: #f5f7fa;
        border-bottom: 1px solid #ebeef5;
        
        .order-no {
          font-weight: 500;
        }
        
        .order-time {
          color: #909399;
          font-size: 13px;
        }
      }
      
      .order-items {
        padding: 16px 20px;
        
        .order-item {
          display: flex;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #ebeef5;
          
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
              display: block;
              margin-bottom: 8px;
            }
            
            .item-price {
              color: #909399;
            }
          }
        }
      }
      
      .order-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-top: 1px solid #ebeef5;
        
        .order-total {
          strong {
            font-size: 18px;
            color: #f56c6c;
          }
        }
        
        .order-actions {
          display: flex;
          gap: 10px;
        }
      }
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }
}
</style>
