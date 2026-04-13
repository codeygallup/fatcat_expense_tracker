<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/api/index'
import Skeleton from '@/components/Skeleton.vue'

interface Transaction {
  id: number
  description: string
  merchant: string
  amount: number
  date: string
  transactionType: 'DEPOSIT' | 'WITHDRAWAL' 
}

const accounts = ref<{ id: number; balance: number }[]>([])
const billsDue = ref<{ id: number; name: string; dueDate: string; status: string }[]>([])
const recentTransactions = ref<Transaction[]>([])
const loading = ref(true)

const formattedBalance = computed(() => {
  const total = accounts.value.reduce((sum, a) => sum + a.balance, 0)
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(total)
})

const overdueBills = computed(() => billsDue.value.filter(b => b.status === 'OVERDUE'))
const hasOverdue = computed(() => overdueBills.value.length > 0)

onMounted(async () => {
  try {
    const [accountsRes, billsRes, transactionsRes] = await Promise.all([
      api('/accounts'),
      api('/bills/upcoming'),
      api('/transactions/recent'),
    ])
    accounts.value = await accountsRes.json()
    billsDue.value = await billsRes.json()
    recentTransactions.value = await transactionsRes.json()
  } catch (e) {
    console.error('Failed to fetch dashboard data', e)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="p-4 max-w-4xl mx-auto">
    <div class="flex gap-3 mb-6 flex-wrap justify-center">
      <RouterLink to="/accounts" class="text-sm font-medium underline underline-offset-4">Accounts</RouterLink>
      <RouterLink to="/bills" class="text-sm font-medium underline underline-offset-4">Bills</RouterLink>
    </div>
    
    <template v-if="loading" class="mb-6 flex flex-col gap-4">
        <Skeleton v-for="n in 3" :key="n" type="card" />
    </template>
    <template v-else>
    <div class="grid grid-cols-2 gap-4 text-center">

      <RouterLink to="/bills" class="bg-white border rounded-2xl p-6 flex flex-col gap-1 hover:shadow-md transition-shadow min-h-40 justify-between flex-1"
      :class="hasOverdue ? 'border-red-300 bg-red-50' : 'border-gray-200'">
      <span class="text-xs text-gray-500 uppercase tracking-wide">Bills Due</span>
      <span class="text-2xl font-bold" :class="hasOverdue ? 'text-red-600' : 'text-gray-900'">{{ billsDue.length }}</span>
      <span class="text-xs" :class="hasOverdue ? 'text-red-400' : 'text-gray-400'">
          {{ hasOverdue ? `${overdueBills.length} overdue!` : 'next 14 days' }}
        </span>
      </RouterLink>

      <RouterLink to="/accounts" class="bg-white border border-gray-200 rounded-2xl p-6 flex flex-col gap-1 hover:shadow-md transition-shadow min-h-36 justify-between flex-1">
        <span class="text-xs text-gray-500 uppercase tracking-wide">Total Balance</span>
        <span class="text-2xl font-bold text-gray-900">{{ formattedBalance }}</span>
        <span class="text-xs text-gray-400">across {{ accounts.length }} accounts</span>
      </RouterLink>

      <RouterLink to="/transactions" class="col-span-2 bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-md transition-shadow">
        <span class="text-xs text-gray-500 uppercase tracking-wide block mb-3">Recent Activity</span>
        <div class="flex flex-col gap-2 overflow-y-auto max-h-80 sm:max-h-none">
          <div
            v-for="tx in recentTransactions"
            :key="tx.id"
            class="flex justify-between items-center text-sm p-2 outline-1 outline-gray-200 rounded"
          >
            <span class="text-gray-700">{{ tx.merchant }}</span>
            <div class="flex flex-col items-end gap-1">
              <span class="text-gray-500">{{ new Date(tx.date).toLocaleDateString() }}</span>
              <span class="font-medium" :class="tx.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-gray-900'">
                {{ tx.transactionType === 'DEPOSIT' ? '+' : '-' }}${{ tx.amount.toFixed(2) }}
              </span>
            </div>
          </div>
          <div v-if="!recentTransactions.length" class="text-sm text-gray-400">No recent transactions</div>
        </div>
      </RouterLink>
      
    </div>
  </template>
  </div>
</template>