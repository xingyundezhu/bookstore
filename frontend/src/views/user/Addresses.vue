<template>
  <div class="user-addresses">
    <div class="page-header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="showAddressDialog = true">
        <el-icon><Plus /></el-icon>
        添加地址
      </el-button>
    </div>
    
    <div class="address-list" v-loading="loading">
      <template v-if="addresses.length > 0">
        <div 
          v-for="addr in addresses" 
          :key="addr.id" 
          class="address-card"
          :class="{ 'is-default': addr.isDefault === 1 }"
        >
          <div class="address-content">
            <div class="address-header">
              <span class="receiver">{{ addr.receiver }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault === 1" type="primary" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
            </div>
          </div>
          <div class="address-actions">
            <el-button 
              v-if="addr.isDefault !== 1" 
              text 
              type="primary" 
              @click="setDefault(addr.id)"
            >
              设为默认
            </el-button>
            <el-button text type="primary" @click="editAddress(addr)">
              编辑
            </el-button>
            <el-button text type="danger" @click="deleteAddress(addr.id)">
              删除
            </el-button>
          </div>
        </div>
      </template>
      
      <el-empty v-else description="暂无收货地址">
        <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
      </el-empty>
    </div>
    
    <el-dialog 
      v-model="showAddressDialog" 
      :title="editingAddress ? '编辑地址' : '添加地址'" 
      width="500px"
    >
      <el-form :model="addressForm" :rules="addressRules" ref="formRef" label-width="80px">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="addressForm.receiver" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input 
            v-model="addressForm.detail" 
            type="textarea" 
            :rows="2"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addressApi } from '@/api/address'

const loading = ref(false)
const addresses = ref([])
const showAddressDialog = ref(false)
const editingAddress = ref(null)
const saving = ref(false)
const formRef = ref()

const addressForm = reactive({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const addressRules = {
  receiver: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await addressApi.getList()
    if (res.code === 200) {
      addresses.value = res.data
    }
  } catch (error) {
    console.error('获取地址失败', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(addressForm, {
    receiver: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detail: '',
    isDefault: false
  })
  editingAddress.value = null
}

const editAddress = (addr) => {
  editingAddress.value = addr
  Object.assign(addressForm, {
    receiver: addr.receiver,
    phone: addr.phone,
    province: addr.province,
    city: addr.city,
    district: addr.district,
    detail: addr.detail,
    isDefault: addr.isDefault === 1
  })
  showAddressDialog.value = true
}

const saveAddress = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  saving.value = true
  try {
    const data = {
      ...addressForm,
      isDefault: addressForm.isDefault ? 1 : 0
    }
    
    let res
    if (editingAddress.value) {
      res = await addressApi.update(editingAddress.value.id, data)
    } else {
      res = await addressApi.create(data)
    }
    
    if (res.code === 200) {
      ElMessage.success('保存成功')
      showAddressDialog.value = false
      resetForm()
      fetchAddresses()
    }
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const setDefault = async (id) => {
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

onMounted(() => {
  fetchAddresses()
})
</script>

<style lang="scss" scoped>
.user-addresses {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h2 {
      font-size: 20px;
      font-weight: 600;
    }
  }
  
  .address-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .address-card {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    padding: 16px;
    transition: all 0.3s;
    
    &:hover {
      border-color: #409eff;
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
    }
    
    &.is-default {
      border-color: #409eff;
      background: #ecf5ff;
    }
    
    .address-content {
      margin-bottom: 12px;
      
      .address-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
        
        .receiver {
          font-size: 16px;
          font-weight: 500;
        }
        
        .phone {
          color: #606266;
        }
      }
      
      .address-detail {
        color: #606266;
        font-size: 14px;
        line-height: 1.5;
      }
    }
    
    .address-actions {
      display: flex;
      gap: 8px;
      padding-top: 12px;
      border-top: 1px solid #ebeef5;
    }
  }
}

@media (max-width: 768px) {
  .user-addresses {
    .address-list {
      grid-template-columns: 1fr;
    }
  }
}
</style>
