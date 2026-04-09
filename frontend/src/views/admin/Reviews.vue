<template>
  <div class="reviews-page">
    <div class="page-header">
      <h2>评价管理</h2>
    </div>
    
    <el-card>
      <el-table :data="reviews" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookTitle" label="图书" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.bookTitle || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="userNickname" label="用户" width="120">
          <template #default="{ row }">
            {{ row.isAnonymous ? '匿名用户' : (row.userNickname || '用户') }}
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="150">
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-popconfirm title="确定删除该评价？" @confirm="deleteReview(row.id)">
              <template #reference>
                <el-button type="danger" text>删除</el-button>
              </template>
            </el-popconfirm>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reviewApi } from '@/api/review'

const reviews = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 7
const total = ref(0)

const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await reviewApi.getAllReviews({
      page: currentPage.value - 1,
      size: pageSize
    })
    if (res.code === 200) {
      reviews.value = res.data.content || res.data
      total.value = res.data.totalElements || res.data.length || 0
    }
  } catch (error) {
    console.error('获取评价列表失败', error)
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchReviews()
}

const deleteReview = async (id) => {
  try {
    const res = await reviewApi.adminDeleteReview(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchReviews()
    }
  } catch (error) {
    console.error('删除评价失败', error)
    ElMessage.error('删除失败')
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchReviews()
})
</script>

<style lang="scss" scoped>
.reviews-page {
  .page-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0;
      font-size: 20px;
    }
  }
  
  .pagination-container {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
