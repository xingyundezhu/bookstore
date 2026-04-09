<template>
  <div class="app-container">
    <header-nav />
    <main class="main-content">
      <router-view />
    </main>
    <footer-bar />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import HeaderNav from '@/components/HeaderNav.vue'
import FooterBar from '@/components/FooterBar.vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onMounted(() => {
  userStore.checkToken()
})
</script>

<style lang="scss">
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  
  .main-content {
    flex: 1;
    background: var(--bg-primary);
  }
}
</style>
