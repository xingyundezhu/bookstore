<template>
  <div class="sales-page">
    <div class="page-header">
      <h2>销量统计</h2>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#409eff"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalSales || 0 }}</div>
              <div class="stat-label">总销量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#67c23a"><Notebook /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.activeBooks || 0 }}</div>
              <div class="stat-label">上架图书</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" color="#e6a23c"><Menu /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ categories.length || 0 }}</div>
              <div class="stat-label">分类数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>分类销量统计</span>
            </div>
          </template>
          <el-table :data="categorySales" style="width: 100%">
            <el-table-column prop="categoryName" label="分类名称" />
            <el-table-column prop="sales" label="销量" width="120">
              <template #default="{ row }">
                <el-tag type="success">{{ row.sales }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销量排行榜</span>
              <el-select v-model="selectedCategory" placeholder="选择分类" clearable style="width: 150px" @change="handleCategoryChange">
                <el-option label="全部分类" :value="null" />
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </div>
          </template>
          <el-table :data="ranking" style="width: 100%" v-loading="loading">
            <el-table-column label="排名" width="60">
              <template #default="{ $index }">
                <el-tag :type="getRankType($index)">{{ getRankNumber($index) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="书名" show-overflow-tooltip />
            <el-table-column prop="sales" label="销量" width="100">
              <template #default="{ row }">
                <span class="sales-num">{{ row.sales }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button text type="primary" @click="showEditDialog(row)">修改</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="handlePageChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog v-model="editDialogVisible" title="修改销量" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="图书">
          <el-input :value="editForm.title" disabled />
        </el-form-item>
        <el-form-item label="销量">
          <el-input-number v-model="editForm.sales" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateSales">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { bookApi } from '@/api/book'
import { categoryApi } from '@/api/category'

const statistics = ref({})
const categorySales = ref([])
const ranking = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const editDialogVisible = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 7
const total = ref(0)

const editForm = reactive({
  id: null,
  title: '',
  sales: 0
})

const fetchStatistics = async () => {
  try {
    const res = await bookApi.getSalesStatistics()
    if (res.code === 200) {
      statistics.value = res.data || {}
      categorySales.value = res.data?.categorySales || []
    }
  } catch (error) {
    console.error('获取销量统计失败', error)
  }
}

const fetchRanking = async () => {
  loading.value = true
  try {
    const res = await bookApi.getSalesRanking(currentPage.value - 1, pageSize, selectedCategory.value)
    if (res.code === 200) {
      if (res.data.content) {
        ranking.value = res.data.content
        total.value = res.data.totalElements
      } else {
        ranking.value = res.data
        total.value = res.data.length
      }
    }
  } catch (error) {
    console.error('获取销量排行失败', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchRanking()
}

const handleCategoryChange = () => {
  currentPage.value = 1
  fetchRanking()
}

const getRankNumber = (index) => {
  return (currentPage.value - 1) * pageSize + index + 1
}

const getRankType = (index) => {
  const rank = getRankNumber(index)
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return 'info'
}

const showEditDialog = (book) => {
  editForm.id = book.id
  editForm.title = book.title
  editForm.sales = book.sales
  editDialogVisible.value = true
}

const updateSales = async () => {
  try {
    const res = await bookApi.updateSales(editForm.id, editForm.sales)
    if (res.code === 200) {
      ElMessage.success('销量更新成功')
      editDialogVisible.value = false
      fetchRanking()
      fetchStatistics()
    }
  } catch (error) {
    console.error('更新销量失败', error)
    ElMessage.error('更新销量失败')
  }
}

onMounted(() => {
  fetchStatistics()
  fetchRanking()
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.sales-page {
  .page-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 20px;
    }
  }
  
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    display: flex;
    align-items: center;
    gap: 20px;
    
    .stat-info {
      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: #303133;
      }
      
      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .sales-num {
    font-weight: 600;
    color: #f56c6c;
  }
  
  .pagination-container {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
