<script setup lang="ts">
import { RouterView } from 'vue-router'
import Navbar from './components/Navbar.vue'
import ToastContainer from './components/ToastContainer.vue';
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col">
    <ToastContainer />
    <Navbar />
    <main class="flex flex-1 flex-col">
      <RouterView />
    </main>
  </div>
</template>
