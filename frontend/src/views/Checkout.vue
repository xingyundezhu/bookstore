<template>
  <div class="checkout-page">
    <div class="page-container">
      <h2>确认订单</h2>
      
      <div class="checkout-content">
        <div class="address-section">
          <h3>收货地址</h3>
          <div class="address-list" v-if="addresses.length > 0">
            <div 
              v-for="addr in addresses" 
              :key="addr.id"
              class="address-card"
              :class="{ selected: selectedAddress?.id === addr.id }"
              @click="selectedAddress = addr"
            >
              <div class="address-info">
                <span class="receiver">{{ addr.receiver }}</span>
                <span class="phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault === 1 || addr.isDefault === true" type="primary" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
              </div>
            </div>
          </div>
          <el-empty v-else description="请先添加收货地址">
            <el-button type="primary" @click="showAddressDialog = true">添加地址</el-button>
          </el-empty>
        </div>
        
        <div class="items-section">
          <h3>商品清单</h3>
          <div class="items-list">
            <div v-for="item in cartItems" :key="item.id" class="item-row">
              <img :src="item.bookCover || defaultCover" :alt="item.bookTitle" class="item-cover" />
              <div class="item-info">
                <span class="item-title">{{ item.bookTitle }}</span>
                <span class="item-price">¥{{ item.price }}</span>
              </div>
              <div class="item-quantity">x{{ item.quantity }}</div>
              <div class="item-subtotal">¥{{ calculateSubtotal(item) }}</div>
            </div>
          </div>
        </div>
        
        <div class="payment-section">
          <h3>支付方式</h3>
          <el-radio-group v-model="paymentType">
            <el-radio :label="1">支付宝</el-radio>
            <el-radio :label="2">微信支付</el-radio>
            <el-radio :label="3">银行卡</el-radio>
          </el-radio-group>
        </div>
        
        <div class="remark-section">
          <h3>订单备注</h3>
          <el-input v-model="remark" type="textarea" :rows="2" placeholder="选填，请输入订单备注" />
        </div>
      </div>
      
      <div class="checkout-footer">
        <div class="summary">
          <span>共 {{ totalCount }} 件商品</span>
          <span class="total">
            应付金额：<strong>¥{{ totalPrice.toFixed(2) }}</strong>
          </span>
        </div>
        <el-button type="danger" size="large" @click="submitOrder" :loading="submitting">
          提交订单
        </el-button>
      </div>
    </div>
    
    <el-dialog v-model="showAddressDialog" title="添加收货地址" width="500px">
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { cartApi } from '@/api/cart'
import { addressApi } from '@/api/address'
import { orderApi } from '@/api/order'
import { useCartStore } from '@/store/cart'

const router = useRouter()
const cartStore = useCartStore()
const defaultCover = 'https://via.placeholder.com/60x80/409eff/ffffff?text=Book'

const addresses = ref([])
const selectedAddress = ref(null)
const cartItems = ref([])
const paymentType = ref(1)
const remark = ref('')
const submitting = ref(false)
const showAddressDialog = ref(false)

const addressForm = ref({
  receiver: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})

const calculateSubtotal = (item) => {
  return (item.price || 0) * (item.quantity || 1)
}

const totalCount = computed(() => cartItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalPrice = computed(() => cartItems.value.reduce((sum, item) => sum + calculateSubtotal(item), 0))

const fetchAddresses = async () => {
  try {
    const res = await addressApi.getList()
    if (res.code === 200) {
      addresses.value = res.data
      const defaultAddr = res.data.find(a => a.isDefault === 1 || a.isDefault === true)
      if (defaultAddr) {
        selectedAddress.value = defaultAddr
      } else if (res.data.length > 0) {
        selectedAddress.value = res.data[0]
      }
    }
  } catch (error) {
    console.error('获取地址失败', error)
  }
}

const fetchCartItems = async () => {
  try {
    const res = await cartApi.getList()
    if (res.code === 200) {
      cartItems.value = res.data.filter(item => item.selected)
      if (cartItems.value.length === 0) {
        ElMessage.warning('请先选择要购买的商品')
        router.push('/cart')
      }
    }
  } catch (error) {
    console.error('获取购物车失败', error)
  }
}

const saveAddress = async () => {
  try {
    const res = await addressApi.create(addressForm.value)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showAddressDialog.value = false
      await fetchAddresses()
      addressForm.value = {
        receiver: '',
        phone: '',
        province: '',
        city: '',
        district: '',
        detail: ''
      }
    }
  } catch (error) {
    console.error('添加地址失败', error)
  }
}

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  submitting.value = true
  try {
    const res = await orderApi.create({
      addressId: selectedAddress.value.id,
      paymentType: paymentType.value,
      remark: remark.value
    })
    
    if (res.code === 200) {
      ElMessage.success('下单成功')
      await cartStore.fetchCartCount()
      router.push('/user/orders')
    }
  } catch (error) {
    console.error('下单失败', error)
    ElMessage.error('下单失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchAddresses()
  fetchCartItems()
})
</script>

<style lang="scss" scoped>
.checkout-page {
  min-height: calc(100vh - 64px - 200px);
  background: #f5f5f5;
  padding: 40px 0;
  
  .page-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 20px;
    
    h2 {
      margin-bottom: 20px;
    }
  }
  
  .checkout-content {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    
    h3 {
      font-size: 16px;
      margin-bottom: 16px;
      padding-bottom: 12px;
      border-bottom: 1px solid #ebeef5;
    }
    
    .address-section {
      margin-bottom: 24px;
      
      .address-list {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
      }
      
      .address-card {
        width: 280px;
        padding: 16px;
        border: 2px solid #ebeef5;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          border-color: #409eff;
        }
        
        &.selected {
          border-color: #409eff;
          background: #ecf5ff;
        }
        
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
          font-size: 13px;
          color: #606266;
        }
      }
    }
    
    .items-section {
      margin-bottom: 24px;
      
      .items-list {
        .item-row {
          display: flex;
          align-items: center;
          padding: 16px 0;
          border-bottom: 1px solid #ebeef5;
          
          .item-cover {
            width: 60px;
            height: 80px;
            object-fit: cover;
            border-radius: 4px;
          }
          
          .item-info {
            flex: 1;
            margin-left: 16px;
            
            .item-title {
              display: block;
              margin-bottom: 8px;
            }
            
            .item-price {
              color: #909399;
            }
          }
          
          .item-quantity {
            width: 60px;
            text-align: center;
            color: #909399;
          }
          
          .item-subtotal {
            width: 100px;
            text-align: right;
            color: #f56c6c;
            font-weight: 500;
          }
        }
      }
    }
    
    .payment-section, .remark-section {
      margin-bottom: 24px;
    }
  }
  
  .checkout-footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 24px;
    padding: 20px 24px;
    background: #fff;
    border-radius: 8px;
    margin-top: 20px;
    
    .summary {
      text-align: right;
      
      span {
        display: block;
        margin-bottom: 8px;
      }
      
      .total strong {
        font-size: 24px;
        color: #f56c6c;
      }
    }
    
    .el-button {
      width: 160px;
      height: 48px;
      font-size: 16px;
    }
  }
}
</style>
