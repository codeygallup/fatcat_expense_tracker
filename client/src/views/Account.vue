<script setup lang="ts">
import { ref } from 'vue'
const accounts = ref([
  { id: 1, name: 'Checking Account', balance: 1500 },
  { id: 2, name: 'Savings Account', balance: 5000 },
])
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-around p-8 flex-1">
    <div class="p-2 w-full max-w-4xl">
      <h2 class="text-2xl font-bold mb-4">Your Accounts</h2>
      <div
        v-for="account in accounts"
        :key="account.id"
        class="bg-white rounded-lg shadow-md p-4 mb-4 w-full max-w-md hover:shadow-lg transition-shadow cursor-pointer"
      >
        <RouterLink :to="`/accounts/${account.id}`" class="block">
          <h3 class="text-xl font-semibold text-center mb-4">{{ account.name }}</h3>
          <p class="text-gray-600">Balance: ${{ account.balance }}</p>
        </RouterLink>
      </div>
    </div>
    <RouterLink
      to="/dashboard"
      class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded flex items-center justify-center w-full max-w-md"
    >
      Add New Account
    </RouterLink>
  </div>
</template>

<!-- Accounts Page → dashboard feel, cards for each account with balances, click into detail view. Chart here makes sense — maybe a donut/pie showing breakdown across accounts or a line chart for balance over time.
For charts use ApexCharts (vue3-apexcharts) -->
