<template>
  <div class="admin-categories-page">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon>
        添加分类
      </el-button>
    </div>
    
    <el-table :data="categories" v-loading="loading" row-key="id">
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="100" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button text type="primary" @click="editCategory(row)">编辑</el-button>
          <el-button 
            v-if="row.status === 1" 
            text 
            type="warning" 
            @click="handleDisable(row)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            text 
            type="success" 
            @click="handleEnable(row)"
          >
            启用
          </el-button>
          <el-button text type="danger" @click="deleteCategory(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDialog" :title="editingCategory ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="categoryForm" :rules="formRules" ref="formRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" v-if="editingCategory">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCategory" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '@/api/category'

const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingCategory = ref(null)
const formRef = ref()

const categoryForm = reactive({
  name: '',
  sort: 0,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await categoryApi.getAll()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取分类失败', error)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  editingCategory.value = null
  resetForm()
  showDialog.value = true
}

const editCategory = (category) => {
  editingCategory.value = category
  Object.assign(categoryForm, {
    name: category.name,
    sort: category.sort || 0,
    status: category.status
  })
  showDialog.value = true
}

const saveCategory = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    const data = { ...categoryForm }
    
    let res
    if (editingCategory.value) {
      res = await categoryApi.update(editingCategory.value.id, data)
    } else {
      res = await categoryApi.create(data)
    }
    
    if (res.code === 200) {
      ElMessage.success('保存成功')
      showDialog.value = false
      editingCategory.value = null
      resetForm()
      fetchCategories()
    }
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleEnable = async (category) => {
  try {
    const res = await categoryApi.enable(category.id)
    if (res.code === 200) {
      ElMessage.success('启用成功')
      fetchCategories()
    }
  } catch (error) {
    console.error('启用失败', error)
    ElMessage.error('启用失败')
  }
}

const handleDisable = async (category) => {
  try {
    await ElMessageBox.confirm(
      '确定要禁用该分类吗？禁用后该分类下的书籍将不在前台显示。',
      '提示',
      { type: 'warning' }
    )
    
    const res = await categoryApi.disable(category.id)
    if (res.code === 200) {
      ElMessage.success('禁用成功')
      fetchCategories()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用失败', error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('禁用失败')
      }
    }
  }
}

const deleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该分类吗？此操作不可恢复。',
      '提示',
      { type: 'warning' }
    )
    
    const res = await categoryApi.delete(category.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCategories()
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

const resetForm = () => {
  Object.assign(categoryForm, {
    name: '',
    sort: 0,
    status: 1
  })
  formRef.value?.resetFields()
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.admin-categories-page {
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
}
</style>
