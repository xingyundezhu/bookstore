<template>
  <div class="admin-books-page">
    <div class="page-header">
      <el-button type="primary" @click="showDialog = true">
        <el-icon><Plus /></el-icon>
        添加图书
      </el-button>
    </div>
    
    <el-table :data="books" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="{ row }">
          <img :src="row.coverImage || defaultCover" style="width: 60px; height: 80px; object-fit: cover; border-radius: 4px" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="书名" min-width="200" show-overflow-tooltip />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="editBook(row)">编辑</el-button>
          <el-button text :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button text type="danger" @click="deleteBook(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchBooks"
      />
    </div>
    
    <el-dialog v-model="showDialog" :title="editingBook ? '编辑图书' : '添加图书'" width="600px">
      <el-form :model="bookForm" label-width="80px">
        <el-form-item label="书名" required>
          <el-input v-model="bookForm.title" />
        </el-form-item>
        <el-form-item label="作者" required>
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="出版社" required>
          <el-input v-model="bookForm.publisher" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="bookForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="价格" required>
              <el-input-number v-model="bookForm.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价">
              <el-input-number v-model="bookForm.originalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" required>
              <el-input-number v-model="bookForm.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ISBN">
              <el-input v-model="bookForm.isbn" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面">
          <el-input v-model="bookForm.coverImage" placeholder="封面图片URL" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="标签">
          <el-checkbox v-model="bookForm.isHot">热门</el-checkbox>
          <el-checkbox v-model="bookForm.isNew">新品</el-checkbox>
          <el-checkbox v-model="bookForm.isRecommend">推荐</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveBook">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bookApi } from '@/api/book'
import { categoryApi } from '@/api/category'

const defaultCover = 'https://via.placeholder.com/60x80/409eff/ffffff?text=Book'

const books = ref([])
const categories = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(7)
const showDialog = ref(false)
const editingBook = ref(null)

const bookForm = reactive({
  title: '',
  author: '',
  publisher: '',
  categoryId: null,
  price: 0,
  originalPrice: null,
  stock: 0,
  isbn: '',
  coverImage: '',
  description: '',
  isHot: false,
  isNew: false,
  isRecommend: false
})

const fetchBooks = async () => {
  loading.value = true
  try {
    const res = await bookApi.getAdminList({ page: currentPage.value - 1, size: pageSize.value })
    if (res.code === 200) {
      books.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('获取图书失败', error)
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await categoryApi.getAll()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const editBook = (book) => {
  editingBook.value = book
  Object.assign(bookForm, {
    title: book.title,
    author: book.author,
    publisher: book.publisher,
    categoryId: book.categoryId,
    price: book.price,
    originalPrice: book.originalPrice,
    stock: book.stock,
    isbn: book.isbn,
    coverImage: book.coverImage,
    description: book.description,
    isHot: book.isHot === 1,
    isNew: book.isNew === 1,
    isRecommend: book.isRecommend === 1
  })
  showDialog.value = true
}

const saveBook = async () => {
  try {
    const data = {
      ...bookForm,
      isHot: bookForm.isHot ? 1 : 0,
      isNew: bookForm.isNew ? 1 : 0,
      isRecommend: bookForm.isRecommend ? 1 : 0
    }
    
    let res
    if (editingBook.value) {
      res = await bookApi.update(editingBook.value.id, data)
    } else {
      res = await bookApi.create(data)
    }
    
    if (res.code === 200) {
      ElMessage.success('保存成功')
      showDialog.value = false
      editingBook.value = null
      resetForm()
      fetchBooks()
    }
  } catch (error) {
    console.error('保存失败', error)
  }
}

const toggleStatus = async (book) => {
  try {
    const newStatus = book.status === 1 ? 0 : 1
    await bookApi.update(book.id, { status: newStatus })
    ElMessage.success(newStatus === 1 ? '上架成功' : '下架成功')
    fetchBooks()
  } catch (error) {
    console.error('操作失败', error)
  }
}

const deleteBook = async (book) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗？', '提示', { type: 'warning' })
    await bookApi.delete(book.id)
    ElMessage.success('删除成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

const resetForm = () => {
  Object.assign(bookForm, {
    title: '',
    author: '',
    publisher: '',
    categoryId: null,
    price: 0,
    originalPrice: null,
    stock: 0,
    isbn: '',
    coverImage: '',
    description: '',
    isHot: false,
    isNew: false,
    isRecommend: false
  })
}

onMounted(() => {
  fetchBooks()
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.admin-books-page {
  .page-header {
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
