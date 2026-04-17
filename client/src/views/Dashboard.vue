<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import api from '@/api/index'
import Skeleton from '@/components/Skeleton.vue'
import type { Transaction } from '@/types'

const accounts = ref<{ id: number; balance: number }[]>([])
const billsDue = ref<{ id: number; name: string; dueDate: string; status: string }[]>([])
const recentTransactions = ref<Transaction[]>([])
const loading = ref(true)

const formattedBalance = computed(() => {
  const total = accounts.value.reduce((sum, a) => sum + a.balance, 0)
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(total)
})

const overdueBills = computed(() => billsDue.value.filter((b) => b.status === 'OVERDUE'))
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
  <div
    class="p-4 max-w-4xl mx-auto md:max-w-none md:mx-20 md:h-[calc(100vh-140px)] md:overflow-hidden"
  >
    <div class="flex gap-3 mb-6 flex-wrap justify-center md:hidden">
      <RouterLink to="/accounts" class="text-sm font-medium underline underline-offset-4"
        >Accounts</RouterLink
      >
      <RouterLink to="/bills" class="text-sm font-medium underline underline-offset-4"
        >Bills</RouterLink
      >
    </div>

    <template v-if="loading">
      <div class="flex flex-col gap-4">
        <Skeleton v-for="n in 3" :key="n" type="card" />
      </div>
    </template>
    <template v-else>
      <div class="flex flex-col gap-4 md:flex-row md:gap-6 md:items-stretch md:h-full">
        <!-- Stat cards: 2-col grid on mobile, stacked column on desktop -->
        <div class="grid grid-cols-2 gap-4 md:flex md:flex-col md:w-72 md:shrink-0 md:self-stretch">
          <RouterLink
            to="/bills"
            class="bg-white border rounded-2xl p-6 flex flex-col gap-1 md:gap-3 md:p-8 md:flex-1 hover:shadow-md transition-shadow justify-between md:justify-around text-center"
            :class="hasOverdue ? 'border-red-300 bg-red-50' : 'border-gray-200'"
          >
            <span class="text-xs text-gray-500 uppercase tracking-wide">Bills Due</span>
            <span
              class="text-2xl md:text-4xl font-bold"
              :class="hasOverdue ? 'text-red-600' : 'text-gray-900'"
              >{{ billsDue.length }}</span
            >
            <span class="text-xs md:text-sm" :class="hasOverdue ? 'text-red-400' : 'text-gray-400'">
              {{ hasOverdue ? `${overdueBills.length} overdue!` : 'next 14 days' }}
            </span>
          </RouterLink>

          <RouterLink
            to="/accounts"
            class="bg-white border border-gray-200 rounded-2xl p-6 flex flex-col gap-1 md:gap-3 md:p-8 md:flex-1 hover:shadow-md transition-shadow justify-between md:justify-around text-center"
          >
            <span class="text-xs text-gray-500 uppercase tracking-wide">Total Balance</span>
            <span class="md:text-4xl font-bold text-gray-900"   :class="Number(formattedBalance.replace(/[^0-9.-]+/g, '')) >= 1000000 ? 'text-lg' : 'text-2xl'">{{ formattedBalance }}</span>
            <span class="text-xs md:text-sm text-gray-400"
              >across {{ accounts.length }} accounts</span
            >
          </RouterLink>
        </div>

        <!-- Recent activity -->
        <RouterLink
          to="/transactions"
          class="bg-white border border-gray-200 rounded-2xl p-6 hover:shadow-md transition-shadow md:flex-1 md:overflow-hidden"
        >
          <span class="text-xs text-gray-500 uppercase tracking-wide block mb-3"
            >Recent Activity</span
          >
          <div class="flex flex-col gap-2 md:h-[calc(100%-2rem)] md:overflow-y-auto">
            <div
              v-for="tx in recentTransactions"
              :key="tx.id"
              class="flex justify-between items-center text-sm p-2 rounded outline-1 outline-gray-200"
            >
              <span class="text-gray-700">{{ tx.merchant }}</span>
              <div class="flex flex-col items-end gap-1">
                <span class="text-gray-500">{{ new Date(tx.date).toLocaleDateString() }}</span>
                <span
                  class="font-medium"
                  :class="tx.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-gray-900'"
                >
                  {{ tx.transactionType === 'DEPOSIT' ? '+' : '-' }}${{ tx.amount.toFixed(2) }}
                </span>
              </div>
            </div>
            <div v-if="!recentTransactions.length" class="text-sm text-gray-400">
              No recent transactions
            </div>
          </div>
        </RouterLink>
      </div>
    </template>
  </div>
</template>
