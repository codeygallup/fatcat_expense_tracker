<script setup lang="ts">
import { ref } from 'vue'
const accounts = ref([
  { id: 1, name: 'Checking Account', balance: 1500 },
  { id: 2, name: 'Savings Account', balance: 5000 },
])
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-around p-8 flex-1">
    <div class="p-4">
      <h2 class="text-2xl font-bold mb-4">Your Accounts</h2>
      <div
        v-for="account in accounts"
        :key="account.id"
        class="bg-white rounded-lg shadow-md p-4 mb-4"
      >
        <h3 class="text-xl font-semibold">{{ account.name }}</h3>
        <p class="text-gray-600">Balance: ${{ account.balance }}</p>
      </div>
    </div>
    <button
      class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded"
      @click="$router.push('/dashboard')"
    >
      Add New Account
    </button>
    <button
      class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
      @click="$router.push('/dashboard')"
    >
      Back to Dashboard
    </button>
  </div>
</template>
