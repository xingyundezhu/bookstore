<template>
  <div class="admin-users-page">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名/邮箱/手机号"
          clearable
          style="width: 250px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>
    
    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :size="40" :src="row.avatar || defaultAvatar">
            {{ row.username?.charAt(0).toUpperCase() }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
            {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small" @click="viewUser(row)">
            <el-icon><View /></el-icon>
            查看
          </el-button>
          <el-button 
            text 
            :type="row.status === 1 ? 'danger' : 'success'" 
            size="small" 
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button 
            v-if="row.role !== 'ADMIN'" 
            text 
            type="warning" 
            size="small" 
            @click="setAdmin(row)"
          >
            设为管理员
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchUsers"
        @size-change="fetchUsers"
      />
    </div>
    
    <el-dialog v-model="showUserDialog" title="用户详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ currentUser.gender === 1 ? '男' : currentUser.gender === 2 ? '女' : '保密' }}
        </el-descriptions-item>
        <el-descriptions-item label="生日">{{ currentUser.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'primary'" size="small">
            {{ currentUser.role === 'ADMIN' ? '管理员' : '用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDate(currentUser.updateTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const users = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const showUserDialog = ref(false)
const currentUser = ref({})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/users', {
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })
    if (res.code === 200) {
      users.value = res.data.content
      total.value = res.data.totalElements
    }
  } catch (error) {
    console.error('获取用户失败', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    users.value = users.value.filter(u => 
      u.username?.includes(searchKeyword.value) ||
      u.email?.includes(searchKeyword.value) ||
      u.phone?.includes(searchKeyword.value)
    )
  } else {
    fetchUsers()
  }
}

const resetSearch = () => {
  searchKeyword.value = ''
  currentPage.value = 1
  fetchUsers()
}

const viewUser = (user) => {
  currentUser.value = user
  showUserDialog.value = true
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定要${action}用户 "${user.username}" 吗？`, '提示', {
      type: 'warning'
    })
    
    await request.put(`/users/${user.id}/status`, null, {
      params: { status: newStatus }
    })
    
    ElMessage.success(`用户已${action}`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败', error)
      ElMessage.error('操作失败')
    }
  }
}

const setAdmin = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要将用户 "${user.username}" 设置为管理员吗？`, '提示', {
      type: 'warning'
    })
    
    await request.put(`/admin/users/${user.id}/role`, null, {
      params: { role: 'ADMIN' }
    })
    
    ElMessage.success('已设置为管理员')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败', error)
      ElMessage.error('操作失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.admin-users-page {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      font-size: 20px;
      font-weight: 600;
      margin: 0;
    }
    
    .header-actions {
      display: flex;
      gap: 12px;
    }
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
