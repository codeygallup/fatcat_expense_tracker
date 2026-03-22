<script setup>
import { ref } from 'vue'
import { useAuth } from '@/composables/useAuth'

const { isLoggedIn, logout } = useAuth()

const isOpen = ref(false)
const toggleMenu = () => {
  isOpen.value = !isOpen.value
}
</script>

<template>
  <nav class="bg-gray-800 text-white p-4 sticky top-0 z-50">
    <div class="container mx-auto flex justify-between items-center">
      <RouterLink :to="isLoggedIn ? '/dashboard' : '/'" class="flex items-center space-x-2">
        <img src="../assets/icon.png" alt="Fatcat Icon" class="w-20 h-20 inline-block ml-2" />
        <span>Fatcat</span>
      </RouterLink>
      <div class="hidden md:flex space-x-4">
        <template v-if="isLoggedIn">
          <RouterLink to="/accounts">Accounts</RouterLink>
          <RouterLink to="/bills">Bills</RouterLink>
          <RouterLink to="/transactions">Spending</RouterLink>
          <button @click="logout" class="px-3 py-2 hover:bg-gray-700 rounded">Logout</button>
        </template>

        <template v-else>
          <RouterLink to="/login" class="px-3 py-2 hover:bg-gray-700 rounded">Login</RouterLink>
          <RouterLink to="/register" class="px-3 py-2 hover:bg-gray-700 rounded"
            >Register</RouterLink
          >
        </template>
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
    <template v-if="isLoggedIn">
      <RouterLink to="/accounts" class="block px-3 py-2 hover:bg-gray-700 rounded"
        >Accounts</RouterLink
      >
      <RouterLink to="/bills" class="block px-3 py-2 hover:bg-gray-700 rounded">Bills</RouterLink>
      <RouterLink to="/transactions" class="block px-3 py-2 hover:bg-gray-700 rounded"
        >Spending</RouterLink
      >
      <button @click="logout" class="block w-full text-left px-3 py-2 hover:bg-gray-700 rounded">
        Logout
      </button>

      >
    </template>

    <template v-else>
      <RouterLink to="/login" class="block px-3 py-2 hover:bg-gray-700 rounded">Login</RouterLink>
      <RouterLink to="/register" class="block px-3 py-2 hover:bg-gray-700 rounded"
        >Register</RouterLink
      >
    </template>
  </div>
</template>
