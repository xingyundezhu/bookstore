<template>
  <div class="back-button">
    <el-button @click="goBack" :type="type" :size="size">
      <el-icon><ArrowLeft /></el-icon>
      {{ text }}
    </el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  text: {
    type: String,
    default: '返回'
  },
  type: {
    type: String,
    default: 'default'
  },
  size: {
    type: String,
    default: 'default'
  }
})

const router = useRouter()

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
.back-button {
  margin-bottom: 20px;
  
  .el-button {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }
}
</style>
