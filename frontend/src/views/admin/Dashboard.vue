<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="32"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.todayOrders || 0 }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="32"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ statistics.todaySales || 0 }}</div>
            <div class="stat-label">今日销售额</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.monthOrders || 0 }}</div>
            <div class="stat-label">本月订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ statistics.monthSales || 0 }}</div>
            <div class="stat-label">本月销售额</div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <div class="chart-card">
          <h3>最新订单</h3>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNo" label="订单号" width="200" />
            <el-table-column prop="receiver" label="收货人" width="120" />
            <el-table-column prop="payAmount" label="金额" width="120">
              <template #default="{ row }">
                ¥{{ row.payAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间">
              <template #default="{ row }">
                {{ formatDate(row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-card">
          <h3>热门图书</h3>
          <div class="hot-books">
            <div v-for="(book, index) in hotBooks" :key="book.id" class="hot-book-item">
              <span class="rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
              <span class="title">{{ book.title }}</span>
              <span class="sales">{{ book.sales }} 销量</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '@/api/order'
import { bookApi } from '@/api/book'

const statistics = ref({})
const hotBooks = ref([])
const recentOrders = ref([])

const fetchStatistics = async () => {
  try {
    const res = await orderApi.getStatistics()
    if (res.code === 200) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('获取统计失败', error)
  }
}

const fetchHotBooks = async () => {
  try {
    const res = await bookApi.getHot(10)
    if (res.code === 200) {
      hotBooks.value = res.data
    }
  } catch (error) {
    console.error('获取热门图书失败', error)
  }
}

const fetchRecentOrders = async () => {
  try {
    const res = await orderApi.getAdminList({ page: 0, size: 10 })
    if (res.code === 200) {
      recentOrders.value = res.data.content
    }
  } catch (error) {
    console.error('获取订单失败', error)
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

onMounted(() => {
  fetchStatistics()
  fetchHotBooks()
  fetchRecentOrders()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  .stat-card {
    background: var(--bg-card);
    border-radius: var(--radius-md);
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 20px;
    
    .stat-icon {
      width: 64px;
      height: 64px;
      border-radius: var(--radius-md);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }
    
    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: var(--text-primary);
      }
      
      .stat-label {
        font-size: 14px;
        color: var(--text-muted);
        margin-top: 4px;
      }
    }
  }
  
  .chart-card {
    background: var(--bg-card);
    border-radius: var(--radius-md);
    padding: 24px;
    
    h3 {
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
    }
    
    .hot-books {
      .hot-book-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-color);
        
        &:last-child {
          border-bottom: none;
        }
        
        .rank {
          width: 24px;
          height: 24px;
          border-radius: 4px;
          background: var(--bg-primary);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: var(--text-muted);
          margin-right: 12px;
          
          &.top {
            background: var(--accent-color);
            color: #fff;
          }
        }
        
        .title {
          flex: 1;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          color: var(--text-primary);
        }
        
        .sales {
          color: var(--text-muted);
          font-size: 13px;
        }
      }
    }
  }
}
</style>
