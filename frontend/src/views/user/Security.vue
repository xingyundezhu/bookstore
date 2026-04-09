<template>
  <div class="user-security">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>账户安全</h2>
      </div>
    </div>
    
    <div class="security-content">
      <div class="security-item">
        <div class="item-icon">
          <el-icon :size="32"><Lock /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-title">登录密码</div>
          <div class="item-desc">定期更换密码可以提高账户安全性</div>
        </div>
        <div class="item-action">
          <el-button type="primary" @click="openPasswordDialog">
            修改密码
          </el-button>
        </div>
      </div>
      
      <div class="security-item">
        <div class="item-icon" :class="{ 'is-bound': userStore.user?.email }">
          <el-icon :size="32"><Message /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-title">邮箱绑定</div>
          <div class="item-desc">
            <template v-if="userStore.user?.email">
              已绑定：{{ maskEmail(userStore.user.email) }}
            </template>
            <template v-else>
              绑定邮箱可用于找回密码和接收通知
            </template>
          </div>
        </div>
        <div class="item-action">
          <el-button 
            :type="userStore.user?.email ? 'default' : 'primary'" 
            @click="openEmailDialog"
          >
            {{ userStore.user?.email ? '修改邮箱' : '绑定邮箱' }}
          </el-button>
        </div>
      </div>
      
      <div class="security-item">
        <div class="item-icon" :class="{ 'is-bound': userStore.user?.phone }">
          <el-icon :size="32"><Iphone /></el-icon>
        </div>
        <div class="item-info">
          <div class="item-title">手机绑定</div>
          <div class="item-desc">
            <template v-if="userStore.user?.phone">
              已绑定：{{ maskPhone(userStore.user.phone) }}
            </template>
            <template v-else>
              绑定手机可用于找回密码和账户安全验证
            </template>
          </div>
        </div>
        <div class="item-action">
          <el-button 
            :type="userStore.user?.phone ? 'default' : 'primary'" 
            @click="openPhoneDialog"
          >
            {{ userStore.user?.phone ? '修改手机' : '绑定手机' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules" 
        ref="passwordFormRef"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password
            placeholder="请输入原密码"
            @keyup.enter="changePassword"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码（6-20位）"
            @keyup.enter="changePassword"
          />
          <div class="password-strength" v-if="passwordForm.newPassword">
            <span>密码强度：</span>
            <div class="strength-bar">
              <div 
                class="strength-fill" 
                :style="{ width: passwordStrength.percent + '%', background: passwordStrength.color }"
              ></div>
            </div>
            <span :style="{ color: passwordStrength.color }">{{ passwordStrength.text }}</span>
          </div>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
            @keyup.enter="changePassword"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changing">
          确认修改
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showEmailDialog" :title="userStore.user?.email ? '修改邮箱' : '绑定邮箱'" width="420px">
      <el-form 
        :model="emailForm" 
        :rules="emailRules" 
        ref="emailFormRef"
        label-width="100px"
      >
        <el-form-item label="邮箱地址" prop="email">
          <el-input 
            v-model="emailForm.email" 
            placeholder="请输入邮箱地址"
          />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input 
              v-model="emailForm.code" 
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button 
              :disabled="emailCountdown > 0" 
              @click="sendEmailCode"
              :loading="sendingCode"
            >
              {{ emailCountdown > 0 ? `${emailCountdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEmailDialog = false">取消</el-button>
        <el-button type="primary" @click="bindEmail" :loading="binding">
          确认绑定
        </el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="showPhoneDialog" :title="userStore.user?.phone ? '修改手机' : '绑定手机'" width="420px">
      <el-form 
        :model="phoneForm" 
        :rules="phoneRules" 
        ref="phoneFormRef"
        label-width="100px"
      >
        <el-form-item label="手机号码" prop="phone">
          <el-input 
            v-model="phoneForm.phone" 
            placeholder="请输入手机号码"
            maxlength="11"
          />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input 
              v-model="phoneForm.code" 
              placeholder="请输入验证码"
              maxlength="6"
            />
            <el-button 
              :disabled="phoneCountdown > 0" 
              @click="sendPhoneCode"
              :loading="sendingCode"
            >
              {{ phoneCountdown > 0 ? `${phoneCountdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPhoneDialog = false">取消</el-button>
        <el-button type="primary" @click="bindPhone" :loading="binding">
          确认绑定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const showPasswordDialog = ref(false)
const showEmailDialog = ref(false)
const showPhoneDialog = ref(false)
const passwordFormRef = ref()
const emailFormRef = ref()
const phoneFormRef = ref()
const changing = ref(false)
const binding = ref(false)
const sendingCode = ref(false)
const emailCountdown = ref(0)
const phoneCountdown = ref(0)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const emailForm = reactive({
  email: '',
  code: ''
})

const phoneForm = reactive({
  phone: '',
  code: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateNewPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (value.length > 20) {
    callback(new Error('密码长度不能超过20位'))
  } else if (value === passwordForm.oldPassword) {
    callback(new Error('新密码不能与原密码相同'))
  } else {
    callback()
  }
}

const passwordStrength = computed(() => {
  const pwd = passwordForm.newPassword
  if (!pwd) return { percent: 0, color: '#e6e8eb', text: '' }
  
  let strength = 0
  if (pwd.length >= 6) strength += 20
  if (pwd.length >= 10) strength += 20
  if (/[a-z]/.test(pwd)) strength += 20
  if (/[A-Z]/.test(pwd)) strength += 20
  if (/[0-9]/.test(pwd)) strength += 10
  if (/[^a-zA-Z0-9]/.test(pwd)) strength += 10
  
  if (strength <= 30) {
    return { percent: 30, color: '#f56c6c', text: '弱' }
  } else if (strength <= 60) {
    return { percent: 60, color: '#e6a23c', text: '中' }
  } else {
    return { percent: 100, color: '#67c23a', text: '强' }
  }
})

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const emailRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}

const maskEmail = (email) => {
  if (!email) return ''
  const [name, domain] = email.split('@')
  const maskedName = name.length > 2 
    ? name[0] + '***' + name[name.length - 1] 
    : name[0] + '***'
  return `${maskedName}@${domain}`
}

const maskPhone = (phone) => {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const openPasswordDialog = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPasswordDialog.value = true
}

const openEmailDialog = () => {
  emailForm.email = ''
  emailForm.code = ''
  showEmailDialog.value = true
}

const openPhoneDialog = () => {
  phoneForm.phone = ''
  phoneForm.code = ''
  showPhoneDialog.value = true
}

const startCountdown = (type) => {
  const countdown = type === 'email' ? emailCountdown : phoneCountdown
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const sendEmailCode = async () => {
  if (!emailForm.email) {
    ElMessage.warning('请先输入邮箱地址')
    return
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailForm.email)) {
    ElMessage.warning('请输入正确的邮箱格式')
    return
  }
  
  sendingCode.value = true
  try {
    const res = await userApi.sendEmailCode(emailForm.email)
    if (res.code === 200) {
      ElMessage.success('验证码已发送到您的邮箱')
      startCountdown('email')
    }
  } catch (error) {
    console.error('发送验证码失败', error)
    ElMessage.error('发送验证码失败')
  } finally {
    sendingCode.value = false
  }
}

const sendPhoneCode = async () => {
  if (!phoneForm.phone) {
    ElMessage.warning('请先输入手机号码')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phoneForm.phone)) {
    ElMessage.warning('请输入正确的手机号码')
    return
  }
  
  sendingCode.value = true
  try {
    const res = await userApi.sendPhoneCode(phoneForm.phone)
    if (res.code === 200) {
      ElMessage.success('验证码已发送到您的手机')
      startCountdown('phone')
    }
  } catch (error) {
    console.error('发送验证码失败', error)
    ElMessage.error('发送验证码失败')
  } finally {
    sendingCode.value = false
  }
}

const changePassword = async () => {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  changing.value = true
  try {
    const res = await userApi.updatePassword(
      passwordForm.oldPassword,
      passwordForm.newPassword
    )
    
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      showPasswordDialog.value = false
      
      await ElMessageBox.confirm(
        '密码已修改，需要重新登录',
        '提示',
        { confirmButtonText: '重新登录', showCancelButton: false, type: 'success' }
      )
      
      userStore.logout()
    }
  } catch (error) {
    console.error('修改失败', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('密码修改失败，请检查原密码是否正确')
    }
  } finally {
    changing.value = false
  }
}

const bindEmail = async () => {
  const valid = await emailFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  binding.value = true
  try {
    const res = await userApi.bindEmail(emailForm.email, emailForm.code)
    if (res.code === 200) {
      ElMessage.success('邮箱绑定成功')
      userStore.updateUserInfo({ email: emailForm.email })
      showEmailDialog.value = false
    }
  } catch (error) {
    console.error('绑定失败', error)
    ElMessage.error('绑定失败，请检查验证码是否正确')
  } finally {
    binding.value = false
  }
}

const bindPhone = async () => {
  const valid = await phoneFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  binding.value = true
  try {
    const res = await userApi.bindPhone(phoneForm.phone, phoneForm.code)
    if (res.code === 200) {
      ElMessage.success('手机绑定成功')
      userStore.updateUserInfo({ phone: phoneForm.phone })
      showPhoneDialog.value = false
    }
  } catch (error) {
    console.error('绑定失败', error)
    ElMessage.error('绑定失败，请检查验证码是否正确')
  } finally {
    binding.value = false
  }
}
</script>

<style lang="scss" scoped>
.user-security {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 24px;
  
  .page-header {
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        background: var(--bg-primary);
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
  
  .security-content {
    .security-item {
      display: flex;
      align-items: center;
      padding: 20px;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      margin-bottom: 16px;
      
      .item-icon {
        width: 60px;
        height: 60px;
        border-radius: var(--radius-md);
        background: rgba(26, 26, 46, 0.08);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--primary-color);
        margin-right: 20px;
        
        &.is-bound {
          background: rgba(103, 194, 58, 0.1);
          color: #67c23a;
        }
      }
      
      .item-info {
        flex: 1;
        
        .item-title {
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 4px;
          color: var(--text-primary);
        }
        
        .item-desc {
          font-size: 13px;
          color: var(--text-muted);
        }
      }
    }
  }
  
  .code-input {
    display: flex;
    gap: 10px;
    
    .el-input {
      flex: 1;
    }
    
    .el-button {
      width: 120px;
    }
  }
  
  .password-strength {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 8px;
    font-size: 12px;
    
    .strength-bar {
      width: 100px;
      height: 4px;
      background: #e6e8eb;
      border-radius: 2px;
      overflow: hidden;
      
      .strength-fill {
        height: 100%;
        transition: all var(--transition-normal);
      }
    }
  }
}
</style>
