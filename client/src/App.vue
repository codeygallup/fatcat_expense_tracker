<script setup lang="ts">
import icon from './assets/icon.png'
</script>

<template>
  <div class="bg-gray-100 min-h-screen flex flex-col items-center justify-around p-8 w-2/3 mx-auto">
    <div>
      <h1 class="text-3xl font-bold text-gray-800">Fatcat Expense Tracker</h1>
      <img :src="icon" alt="Fatcat Icon" class="w-32 h-32 mt-4" />
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
      <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
        Get Started
      </button>
    </div>
  </div>
</template>

<style scoped></style>
