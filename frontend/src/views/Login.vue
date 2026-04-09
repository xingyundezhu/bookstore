<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h2>用户登录</h2>
        
        <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名/邮箱/手机号" size="large" prefix-icon="User" />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" native-type="submit" class="login-btn">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: calc(100vh - 64px - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .login-container {
    width: 100%;
    max-width: 400px;
    padding: 20px;
  }
  
  .login-card {
    background: #fff;
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    
    h2 {
      text-align: center;
      margin-bottom: 32px;
      color: #303133;
    }
    
    .login-btn {
      width: 100%;
      height: 48px;
      font-size: 16px;
    }
    
    .login-footer {
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
