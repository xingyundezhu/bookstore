<template>
  <div class="admin-orders-page">
    <div class="page-header">
      <h2>订单管理</h2>
      <el-select v-model="filterStatus" placeholder="订单状态" clearable @change="fetchOrders" style="width: 150px">
        <el-option label="全部" value="" />
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
    </div>
    
    <el-table :data="orders" v-loading="loading" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="receiver" label="收货人" width="100" />
      <el-table-column prop="receiverPhone" label="电话" width="120" />
      <el-table-column prop="payAmount" label="金额" width="100">
        <template #default="{ row }">
          ¥{{ row.payAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" text type="primary" @click="deliverOrder(row.orderNo)">
            发货
          </el-button>
          <el-button text type="primary" @click="viewDetail(row)">
            详情
          </el-button>
          <el-button v-if="row.status === 3" text type="danger" @click="deleteOrder(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>
    
    <el-dialog v-model="showDetail" title="订单详情" width="600px">
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <h4>订单信息</h4>
          <p><span>订单号：</span>{{ currentOrder.orderNo }}</p>
          <p><span>下单时间：</span>{{ formatDate(currentOrder.createTime) }}</p>
          <p><span>订单状态：</span>
            <el-tag :type="getStatusType(currentOrder.status)">{{ currentOrder.statusText }}</el-tag>
          </p>
        </div>
        
        <div class="detail-section">
          <h4>收货信息</h4>
          <p><span>收货人：</span>{{ currentOrder.receiver }}</p>
          <p><span>电话：</span>{{ currentOrder.receiverPhone }}</p>
          <p><span>地址：</span>{{ currentOrder.receiverAddress }}</p>
        </div>
        
        <div class="detail-section">
          <h4>商品信息</h4>
          <div v-for="item in currentOrder.orderItems" :key="item.id" class="order-item">
            <span>{{ item.bookTitle }}</span>
            <span>¥{{ item.price }} x {{ item.quantity }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>金额信息</h4>
          <p><span>商品总额：</span>¥{{ currentOrder.totalAmount }}</p>
          <p><span>实付金额：</span><strong style="color: #f56c6c">¥{{ currentOrder.payAmount }}</strong></p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const orders = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const filterStatus = ref('')
const showDetail = ref(false)
const currentOrder = ref(null)

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value - 1, size: pageSize.value }
    const res = await orderApi.getAdminList(params)
    
    if (res.code === 200) {
      let orderList = res.data.content
      
      if (filterStatus.value !== '') {
        orderList = orderList.filter(o => o.status === filterStatus.value)
      }
      
      orders.value = orderList
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('获取订单失败', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success', 4: 'info', 5: 'danger' }
  return types[status] || 'info'
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const deliverOrder = async (orderNo) => {
  try {
    const res = await orderApi.deliver(orderNo)
    if (res.code === 200) {
      ElMessage.success('发货成功')
      fetchOrders()
    }
  } catch (error) {
    console.error('发货失败', error)
    ElMessage.error('发货失败')
  }
}

const deleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除订单 ${order.orderNo} 吗？此操作不可恢复。`,
      '删除确认',
      { type: 'warning' }
    )
    
    const res = await orderApi.delete(order.orderNo)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchOrders()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('删除失败')
      }
    }
  }
}

const viewDetail = (order) => {
  currentOrder.value = order
  showDetail.value = true
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.admin-orders-page {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 24px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }
  
  .pagination-wrapper {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
  }
  
  .order-detail {
    .detail-section {
      margin-bottom: 20px;
      
      h4 {
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 1px solid var(--border-color);
        color: var(--text-primary);
      }
      
      p {
        margin-bottom: 8px;
        
        span:first-child {
          color: var(--text-muted);
          display: inline-block;
          width: 80px;
        }
      }
      
      .order-item {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px dashed var(--border-color);
      }
    }
  }
}
</style>
