<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h2>用户注册</h2>
        
        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱" size="large" prefix-icon="Message" />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" native-type="submit" class="register-btn">
              注册
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: calc(100vh - 64px - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  
  .register-container {
    width: 100%;
    max-width: 400px;
    padding: 20px;
  }
  
  .register-card {
    background: #fff;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    
    h2 {
      text-align: center;
      margin-bottom: 32px;
      color: #303133;
    }
    
    .register-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
    }
    
    .register-footer {
      text-align: center;
      margin-top: 20px;
      color: #909399;
      
      a {
        color: #409eff;
        text-decoration: none;
        margin-left: 4px;
        
        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>
