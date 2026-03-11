<script setup>
import { ref } from 'vue'

const isOpen = ref(false)
const toggleMenu = () => {
  isOpen.value = !isOpen.value
}
</script>

<template>
  <nav class="bg-gray-800 text-white p-4">
    <div
      class="container mx-auto flex justify-between items-center"
      @click="$router.push('/dashboard')"
    >
      <div class="text-lg font-bold">
        <img src="../assets/icon.png" alt="Fatcat Icon" class="w-20 h-20 inline-block ml-2" />
        Fatcat
      </div>
      <div class="hidden md:flex space-x-4">
        <a href="/" class="px-3 py-2 hover:bg-gray-700 rounded">Home</a>
        <a href="/login" class="px-3 py-2 hover:bg-gray-700 rounded">Login</a>
        <a href="/register" class="px-3 py-2 hover:bg-gray-700 rounded">Register</a>
        <a href="/dashboard" class="px-3 py-2 hover:bg-gray-700 rounded">Dashboard</a>
      </div>
      <button class="md:hidden px-3 py-2 border rounded" @click="toggleMenu">
        <svg
          class="h-6 w-6"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M4 6h16M4 12h16M4 18h16"
          ></path>
        </svg>
      </button>
    </div>
  </nav>

  <div v-if="isOpen" class="md:hidden bg-gray-800 text-white p-4">
    <!-- <a href="/" class="block px-3 py-2 hover:bg-gray-700 rounded">Home</a> -->
    <a href="/login" class="block px-3 py-2 hover:bg-gray-700 rounded">Login</a>
    <a href="/register" class="block px-3 py-2 hover:bg-gray-700 rounded">Register</a>
    <!-- <a href="/dashboard" class="block px-3 py-2 hover:bg-gray-700 rounded">Dashboard</a> -->
  </div>
</template>
