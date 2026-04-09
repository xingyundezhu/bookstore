<template>
  <div class="user-reviews">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>我的评价</h2>
      </div>
    </div>
    
    <div class="reviews-list" v-loading="loading">
      <template v-if="reviews.length > 0">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="book-info">
            <img :src="review.bookCover || defaultCover" class="book-cover" @error="handleImageError" />
            <div class="book-detail">
              <div class="book-title" @click="goToBook(review.bookId)">{{ review.bookTitle }}</div>
              <div class="review-time">{{ formatDate(review.createTime) }}</div>
            </div>
          </div>
          <div class="review-content">
            <el-rate :model-value="review.rating" disabled />
            <p class="content-text">{{ review.content || '用户未填写评价内容' }}</p>
          </div>
          <div class="review-actions">
            <el-button size="small" text type="primary" @click="editReview(review)">
              编辑
            </el-button>
            <el-button size="small" text type="danger" @click="deleteReview(review.id)">
              删除
            </el-button>
          </div>
        </div>
      </template>
      <el-empty v-else description="暂无评价记录">
        <template #image>
          <el-icon :size="60"><Document /></el-icon>
        </template>
        <el-button type="primary" @click="$router.push('/books')">去选购图书</el-button>
      </el-empty>
    </div>
    
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
    
    <el-dialog v-model="showEditDialog" title="编辑评价" width="500px">
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="editForm.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入评价内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reviewApi } from '@/api/review'

import { useUserStore } from '@/store/user'

import request from '@/api/request'

const router = useRouter()
const userStore = useUserStore()
const defaultCover = 'https://picsum.photos/seed/review/60/80'
const loading = ref(false)
const reviews = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
const showEditDialog = ref(false)
const saving = ref(false)
const editForm = reactive({
  id: null,
  rating: 5,
  content: ''
})
const handleImageError = (e) => {
  e.target.src = defaultCover
}
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}
const goToBook = (bookId) => {
  router.push(`/books/${bookId}`)
}
const fetchReviews = async () => {
  loading.value = true
  try {
    const res = await reviewApi.getUserReviews({
      page: currentPage.value - 1,
      size: pageSize.value
    })
    if (res.code === 200) {
      if (res.data.content) {
        reviews.value = res.data.content
        total.value = res.data.totalElements
      } else {
        reviews.value = res.data
        total.value = res.data.length || 0
      }
    }
  } catch (error) {
    console.error('获取评价失败', error)
    ElMessage.error('获取评价失败')
  } finally {
    loading.value = false
  }
}
const handlePageChange = (page) => {
  currentPage.value = page
  fetchReviews()
}
const editReview = (review) => {
  editForm.id = review.id
  editForm.rating = review.rating
  editForm.content = review.content || ''
  showEditDialog.value = true
}
const saveEdit = async () => {
  saving.value = true
  try {
    const res = await reviewApi.update(editForm.id, {
      rating: editForm.rating,
      content: editForm.content
    })
    if (res.code === 200) {
      ElMessage.success('修改成功')
      showEditDialog.value = false
      fetchReviews()
    }
  } catch (error) {
    console.error('修改失败', error)
    ElMessage.error('修改失败')
  } finally {
    saving.value = false
  }
}
const deleteReview = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该评价吗？', '提示', { type: 'warning' })
    const res = await reviewApi.delete(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchReviews()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}
onMounted(() => {
  fetchReviews()
})
</script>

<style lang="scss" scoped>
.user-reviews {
  .page-header {
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .back-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 16px;
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
  
  .reviews-list {
    min-height: 300px;
  }
  
  .review-card {
    display: flex;
    gap: 16px;
    padding: 16px;
    border: 1px solid #ebeef5;
    border-radius: 8px;
    margin-bottom: 16px;
    
    .book-info {
      display: flex;
      gap: 12px;
      width: 200px;
      flex-shrink: 0;
      
      .book-cover {
        width: 50px;
        height: 65px;
        object-fit: cover;
        border-radius: 4px;
      }
      
      .book-detail {
        .book-title {
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          
          &:hover {
            color: #409eff;
          }
        }
        
        .review-time {
          font-size: 12px;
          color: #909399;
          margin-top: 4px;
        }
      }
    }
    
    .review-content {
      flex: 1;
      
      .content-text {
        margin-top: 8px;
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
      }
    }
    
    .review-actions {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }
  
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;
  }
}

@media (max-width: 768px) {
  .user-reviews {
    .review-card {
      flex-direction: column;
      
      .book-info {
        width: 100%;
      }
      
      .review-actions {
        flex-direction: row;
      }
    }
  }
}
</style>
