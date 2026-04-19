<script setup lang="ts">
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
import ToastContainer from './components/ToastContainer.vue'
import { ref, onMounted } from 'vue'

const waking = ref(true)

onMounted(async () => {
  try {
    await fetch(`${import.meta.env.VITE_API_URL}/actuator/health`)
  } catch {}
  waking.value = false
})
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col">
    <div v-if="waking" class="fixed inset-0 bg-white/80 flex items-center justify-center z-50">
      <div class="text-center">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-500 mx-auto mb-3"></div>
        <p class="text-gray-600 text-sm">Waking up the server, please wait...</p>
      </div>
    </div>
    <Navbar />
    <main class="flex flex-1 flex-col">
      <RouterView />
    </main>
    <ToastContainer />
  </div>
</template>