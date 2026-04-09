<template>
  <div class="profile-page">
    <div class="page-container">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <div class="profile-section">
            <div class="avatar-section">
              <el-avatar :size="100" :src="userStore.user?.avatar || defaultAvatar">
                {{ userStore.user?.username?.charAt(0).toUpperCase() }}
              </el-avatar>
              <el-upload :show-file-list="false" :before-upload="uploadAvatar" accept="image/*">
                <el-button type="primary" size="small">更换头像</el-button>
              </el-upload>
            </div>
            
            <el-form :model="userInfo" label-width="80px" class="info-form">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" disabled />
              </el-form-item>
              <el-form-item label="昵称">
                <el-input v-model="userInfo.nickname" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio :label="0">保密</el-radio>
                  <el-radio :label="1">男</el-radio>
                  <el-radio :label="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="修改密码" name="password">
          <div class="profile-section">
            <el-form :model="passwordForm" label-width="100px" class="password-form">
              <el-form-item label="原密码">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认新密码">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updatePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="收货地址" name="address">
          <div class="address-section">
            <el-button type="primary" @click="showAddressDialog = true" class="add-btn">
              添加地址
            </el-button>
            
            <div class="address-list">
              <div v-for="addr in addresses" :key="addr.id" class="address-card">
                <div class="address-info">
                  <span class="receiver">{{ addr.receiver }}</span>
                  <span class="phone">{{ addr.phone }}</span>
                  <el-tag v-if="addr.isDefault === 1" type="primary" size="small">默认</el-tag>
                </div>
                <div class="address-detail">
                  {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
                </div>
                <div class="address-actions">
                  <el-button text type="primary" @click="setDefaultAddress(addr.id)" v-if="addr.isDefault !== 1">
                    设为默认
                  </el-button>
                  <el-button text type="primary" @click="editAddress(addr)">编辑</el-button>
                  <el-button text type="danger" @click="deleteAddress(addr.id)">删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <el-dialog v-model="showAddressDialog" :title="editingAddress ? '编辑地址' : '添加地址'" width="500px">
      <el-form :model="addressForm" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="addressForm.receiver" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="addressForm.province" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="addressForm.city" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="addressForm.district" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.detail" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'
import { addressApi } from '@/api/address'

const route = useRoute()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const activeTab = ref('info')
const addresses = ref([])
const showAddressDialog = ref(false)
const editingAddress = ref(null)

const userInfo = reactive({
  username: '',
  email: '',
  nickname: '',
  phone: '',
  gender: 0
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const addressForm = reactive({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})

const initUserInfo = () => {
  if (userStore.user) {
    userInfo.username = userStore.user.username
    userInfo.email = userStore.user.email
    userInfo.nickname = userStore.user.nickname || ''
    userInfo.phone = userStore.user.phone || ''
    userInfo.gender = userStore.user.gender || 0
  }
}

const fetchAddresses = async () => {
  try {
    const res = await addressApi.getList()
    if (res.code === 200) {
      addresses.value = res.data
    }
  } catch (error) {
    console.error('获取地址失败', error)
  }
}

const uploadAvatar = async (file) => {
  try {
    const res = await userApi.uploadAvatar(file)
    if (res.code === 200) {
      userStore.updateUserInfo({ avatar: res.data })
      ElMessage.success('头像更新成功')
    }
  } catch (error) {
    console.error('上传失败', error)
  }
  return false
}

const updateInfo = async () => {
  try {
    const res = await userApi.updateInfo({
      nickname: userInfo.nickname,
      phone: userInfo.phone,
      gender: userInfo.gender
    })
    if (res.code === 200) {
      userStore.updateUserInfo(res.data)
      ElMessage.success('保存成功')
    }
  } catch (error) {
    console.error('保存失败', error)
  }
}

const updatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  try {
    const res = await userApi.updatePassword(passwordForm.oldPassword, passwordForm.newPassword)
    if (res.code === 200) {
      ElMessage.success('密码修改成功')
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    }
  } catch (error) {
    console.error('修改失败', error)
  }
}

const editAddress = (addr) => {
  editingAddress.value = addr
  Object.assign(addressForm, addr)
  showAddressDialog.value = true
}

const saveAddress = async () => {
  try {
    let res
    if (editingAddress.value) {
      res = await addressApi.update(editingAddress.value.id, addressForm)
    } else {
      res = await addressApi.create(addressForm)
    }
    
    if (res.code === 200) {
      ElMessage.success('保存成功')
      showAddressDialog.value = false
      editingAddress.value = null
      fetchAddresses()
      Object.assign(addressForm, {
        receiver: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detail: ''
      })
    }
  } catch (error) {
    console.error('保存失败', error)
  }
}

const setDefaultAddress = async (id) => {
  try {
    const res = await addressApi.setDefault(id)
    if (res.code === 200) {
      ElMessage.success('设置成功')
      fetchAddresses()
    }
  } catch (error) {
    console.error('设置失败', error)
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
    const res = await addressApi.delete(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchAddresses()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

watch(() => route.query.tab, (tab) => {
  if (tab) activeTab.value = tab
}, { immediate: true })

onMounted(() => {
  initUserInfo()
  fetchAddresses()
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: calc(100vh - 64px - 200px);
  background: #f5f5f5;
  padding: 40px 0;
  
  .page-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
    background: #fff;
    border-radius: 8px;
  }
  
  .profile-section {
    padding: 24px;
    
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
      margin-bottom: 32px;
    }
    
    .info-form, .password-form {
      max-width: 400px;
    }
  }
  
  .address-section {
    padding: 24px;
    
    .add-btn {
      margin-bottom: 20px;
    }
    
    .address-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    
    .address-card {
      padding: 16px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      
      .address-info {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        .receiver {
          font-weight: 500;
        }
        
        .phone {
          color: #909399;
        }
      }
      
      .address-detail {
        color: #606266;
        margin-bottom: 12px;
      }
      
      .address-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}
</style>
