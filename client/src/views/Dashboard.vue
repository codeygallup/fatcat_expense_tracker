<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/api/index'

const accounts = ref<{ id: number; balance: number }[]>([])
const billsDue = ref<{ id: number; name: string; dueDate: string; status: string }[]>([])
const recentTransactions = ref<{ id: number; description: string; amount: number }[]>([])

const formattedBalance = computed(() => {
  const total = accounts.value.reduce((sum, a) => sum + a.balance, 0)
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(total)
})

const overdueBills = computed(() => billsDue.value.filter(b => b.status === 'OVERDUE'))
const hasOverdue = computed(() => overdueBills.value.length > 0)

onMounted(async () => {
  const token = localStorage.getItem('token')
  const headers = { Authorization: `Bearer ${token}` }

  // accounts.value = await api('/accounts', { headers }).then(r => r.json())
  // billsDue.value = await api('/bills/upcoming', { headers }).then(r => r.json())
  // recentTransactions.value = await api('/transactions/recent', { headers }).then(r => r.json())
})
</script>

<template>
  <div class="p-4 max-w-4xl mx-auto">
    <div class="flex gap-3 mb-6 flex-wrap justify-center">
      <RouterLink to="/accounts" class="text-sm font-medium underline underline-offset-4">Accounts</RouterLink>
      <RouterLink to="/bills" class="text-sm font-medium underline underline-offset-4">Bills</RouterLink>
      <RouterLink to="/transactions" class="text-sm font-medium underline underline-offset-4">Transactions</RouterLink>
    </div>

    <div class="grid grid-cols-2 gap-4 text-center">

      <RouterLink to="/accounts" class="bg-white border border-gray-200 rounded-2xl p-6 flex flex-col gap-1 hover:shadow-md transition-shadow min-h-36 justify-between flex-1">
        <span class="text-xs text-gray-500 uppercase tracking-wide">Total Balance</span>
        <span class="text-2xl font-bold text-gray-900">{{ formattedBalance }}</span>
        <span class="text-xs text-gray-400">across {{ accounts.length }} accounts</span>
      </RouterLink>

      <RouterLink to="/bills" class="bg-white border rounded-2xl p-6 flex flex-col gap-1 hover:shadow-md transition-shadow min-h-40 justify-between flex-1"
        :class="hasOverdue ? 'border-red-300 bg-red-50' : 'border-gray-200'">
        <span class="text-xs text-gray-500 uppercase tracking-wide">Bills Due</span>
        <span class="text-2xl font-bold" :class="hasOverdue ? 'text-red-600' : 'text-gray-900'">{{ billsDue.length }}</span>
        <span class="text-xs" :class="hasOverdue ? 'text-red-400' : 'text-gray-400'">
          {{ hasOverdue ? `${overdueBills.length} overdue!` : 'next 14 days' }}
        </span>
      </RouterLink>

      <RouterLink to="/transactions" class="col-span-2 bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-md transition-shadow">
        <span class="text-xs text-gray-500 uppercase tracking-wide block mb-3">Recent Spending</span>
        <div class="flex flex-col gap-2">
          <div v-for="tx in recentTransactions" :key="tx.id" class="flex justify-between items-center text-sm">
            <span class="text-gray-700">{{ tx.description }}</span>
            <span class="font-medium text-gray-900">-${{ tx.amount.toFixed(2) }}</span>
          </div>
          <div v-if="!recentTransactions.length" class="text-sm text-gray-400">No recent transactions</div>
        </div>
      </RouterLink>

    </div>
  </div>
</template>
