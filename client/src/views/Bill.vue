<script setup lang="ts">
import { ref, computed } from 'vue'
const bills = ref([
  { id: 1, name: 'Electricity', amount: 100, dueDate: '2026-07-01' },
  { id: 2, name: 'Water', amount: 50, dueDate: '2026-03-25' },
  { id: 3, name: 'Internet', amount: 75, dueDate: '2024-07-10' },
])
const isPast = (date) => {
  return new Date(date) < new Date()
}
const nearlyDue = (date) => {
  const today = new Date()
  const dueDate = new Date(date)
  const diffTime = dueDate - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 && diffDays <= 3
}

const sortedBills = computed(() =>
  [...bills.value].sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
)
</script>

<template>
  <div class="flex flex-col items-center justify-around p-8 flex-1">
    <div class="flex flex-col items-center justify-around p-8 flex-1 w-full max-w-4xl">
      <h1 class="text-3xl font-bold text-gray-800 text-center">Bill Page</h1>
      <div class="w-full max-w-md mt-8">
        <div
          v-for="bill in sortedBills"
          :key="bill.id"
          class="shadow-md rounded-lg p-4 mb-4 border"
          :class="
            isPast(bill.dueDate)
              ? 'bg-red-100 border-red-300'
              : nearlyDue(bill.dueDate)
                ? 'bg-yellow-100 border-yellow-300'
                : 'bg-gray-50 border-gray-200'
          "
        >
          <h2 class="text-xl font-bold text-gray-800">{{ bill.name }}</h2>
          <div class="h-px bg-gray-200 my-2"></div>
          <p class="text-gray-600">Amount: ${{ bill.amount }}</p>
          <p class="text-gray-600">
            Due Date: {{ bill.dueDate }} <span v-if="isPast(bill.dueDate)">(Overdue)</span>
            <span v-else-if="nearlyDue(bill.dueDate)">(Due Soon)</span>
          </p>
        </div>
        <div class="flex flex-col mt-6">
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
  </div>
</template>
<!-- Bills Page → this one basically wants a sorted table or card list. A simple sort by due date ascending is probably the most useful default. -->
