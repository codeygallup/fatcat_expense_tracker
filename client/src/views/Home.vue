<script setup lang="ts">
import icon from '../assets/icon.png'
</script>

<template>
  <div class="flex flex-col items-center justify-around p-8 flex-1">
    <div class="flex flex-col items-center space-y-8">
      <h1 class="text-3xl font-bold text-gray-800 text-center">Fatcat Expense Tracker</h1>
    </div>

    <div>
      <p class="text-lg text-gray-600 text-center">
        Welcome to the Fatcat Expense Tracker! This application helps you manage your expenses
        efficiently and effectively. With our user-friendly interface, you can easily track your
        spending, set budgets, and gain insights into your financial habits. Start taking control of
        your finances today with the Fatcat Expense Tracker!
      </p>
    </div>

    <div>
      <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded w-60" @click="$router.push('/login')">
        Get Started
      </button>
    </div>
  </div>
</template>

<style scoped></style>
