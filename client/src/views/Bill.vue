<script setup lang="ts">
import { ref } from 'vue'
const bills = ref([
  { id: 1, name: 'Electricity', amount: 100, dueDate: '2024-07-01' },
  { id: 2, name: 'Water', amount: 50, dueDate: '2024-07-05' },
  { id: 3, name: 'Internet', amount: 75, dueDate: '2024-07-10' },
])
</script>

<template>
  <div class="flex flex-col items-center justify-around p-8 flex-1">
    <div class="flex flex-col items-center justify-around p-8 flex-1">
      <h1 class="text-3xl font-bold text-gray-800 text-center">Bill Page</h1>
      <p class="text-lg text-gray-600 text-center">
        This is where you can manage your bills and expenses.
      </p>
      <div class="w-full max-w-md mt-8">
        <div v-for="bill in bills" :key="bill.id" class="bg-white shadow-md rounded-lg p-4 mb-4">
          <h2 class="text-xl font-bold text-gray-800">{{ bill.name }}</h2>
          <p class="text-gray-600">Amount: ${{ bill.amount }}</p>
          <p class="text-gray-600">Due Date: {{ bill.dueDate }}</p>
        </div>
        <button
          class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded w-full"
        >
          Add New Bill
        </button>
        <button
          class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded w-full mt-4"
        >
          Delete All Bills
        </button>
      </div>
    </div>
  </div>
</template>
