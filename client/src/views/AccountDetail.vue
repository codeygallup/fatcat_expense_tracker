<script setup lang="ts">
import { useRoute } from 'vue-router'
import VueApexCharts from 'vue3-apexcharts'
import Skeleton from '@/components/Skeleton.vue'
import { useAccountDetails } from '@/composables/useAccountDetails'

const route = useRoute()
const accountId = route.params.id as string

const { account, transactions, showModal, showChart, chartType, timeRange, editingTx, amountInput, amountError, CATEGORIES, TRANSACTION_TYPES, categoryLabel, typeLabel, typeIcon, lineOptions, lineSeries, donutSeries, donutOptions, handleAmountInput, handleAmountKeydown, handleAmountPaste, deleteTx, openAdd, openEdit, submitForm, loading, form } = useAccountDetails(accountId)
</script>

<template>
  <div class="p-4 max-w-6xl mx-auto pb-24 md:pb-4">

    <RouterLink
      to="/accounts"
      class="inline-flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4 transition-colors md:hidden"
    >
      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
      </svg>
      Back to Accounts
    </RouterLink>

    <div class="md:grid md:grid-cols-[1fr_1.5fr] md:gap-6 md:items-start">

      <!-- Left col: account header + chart -->
      <div class="flex flex-col gap-4 mb-4 md:mb-0 md:rounded-2xl md:p-6 md:h-full md:justify-between">

        <div v-if="account" class="bg-white border border-gray-200 rounded-2xl p-6 flex items-center gap-4">
          <span class="text-3xl">{{ typeIcon(account.accountType) }}</span>
          <div class="flex-1 min-w-0">
            <h1 class="text-xl font-bold text-gray-900 truncate">{{ account.name }}</h1>
            <p class="text-xs text-gray-400 capitalize">{{ account.accountType.toLowerCase().replace('_', ' ') }}</p>
          </div>
          <span class="text-2xl font-bold" :class="account.balance < 0 ? 'text-red-600' : 'text-gray-900'">
            {{ new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(account.balance) }}
          </span>
        </div>
        <div v-if="donutSeries.length" class="bg-white border border-gray-200 rounded-2xl p-6 md:h-full flex flex-col justify-around">
          <div class="flex items-center justify-between mb-4">
            <div class="flex gap-2">
              <button @click="chartType = 'time'" :class="['text-xs font-medium uppercase tracking-wide px-3 py-1.5 rounded transition-colors', chartType === 'time' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:text-gray-700']">Over Time</button>
              <button @click="chartType = 'category'" :class="['text-xs font-medium uppercase tracking-wide px-3 py-1.5 rounded transition-colors', chartType === 'category' ? 'bg-gray-900 text-white' : 'text-gray-500 hover:text-gray-700']">By Category</button>
            </div>
            <div class="flex items-center gap-3">
              <div v-if="chartType === 'time'" class="flex gap-2 border-l pl-3">
                <button @click="timeRange = 'weekly'" :class="['text-xs font-medium px-2 py-1 rounded transition-colors', timeRange === 'weekly' ? 'bg-gray-200 text-gray-900' : 'text-gray-500 hover:text-gray-700']">Weekly</button>
                <button @click="timeRange = 'monthly'" :class="['text-xs font-medium px-2 py-1 rounded transition-colors', timeRange === 'monthly' ? 'bg-gray-200 text-gray-900' : 'text-gray-500 hover:text-gray-700']">Monthly</button>
              </div>
              <button @click="showChart = !showChart" class="text-gray-400 hover:text-gray-600 transition-colors" :title="showChart ? 'Collapse' : 'Expand'">
                <svg v-if="showChart" xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" /></svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2"><path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" /></svg>
              </button>
            </div>
          </div>
          <VueApexCharts v-if="showChart && chartType === 'time'" type="line" height="260" :options="lineOptions" :series="lineSeries" />
          <VueApexCharts v-if="showChart && chartType === 'category'" type="donut" height="260" :options="donutOptions" :series="donutSeries" />
        </div>

      </div>

      <!-- Right col: transactions -->
      <div class="flex flex-col gap-2 md:overflow-y-auto md:max-h-[calc(100vh-10rem)]">

        <div class="flex items-center justify-between mb-3 sticky top-0 pt-4 bg-gray-100">
          <p class="text-xs text-gray-500 uppercase tracking-wide">Transactions</p>
          <button @click="openAdd" class="hidden md:flex items-center gap-1 text-sm font-medium bg-gray-900 text-white px-3 py-1.5 rounded-lg hover:bg-gray-700 transition-colors md:mr-2">+ Add</button>
        </div>

        <div class="flex flex-col gap-2">
          <template v-if="loading">
            <Skeleton v-for="n in 4" :key="n" type="card" />
          </template>
          <template v-else>
            <div v-for="tx in transactions" :key="tx.id" class="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-3">
              <div class="flex-1 min-w-0">
                <p class="font-medium text-gray-900 truncate">{{ tx.merchant }}</p>
                <p class="text-xs text-gray-400">
                  {{ categoryLabel(tx.category) }} · {{ new Date(tx.date + 'T00:00:00').toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) }}
                </p>
              </div>
              <span class="text-base font-semibold shrink-0" :class="tx.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-gray-900'">
                {{ tx.transactionType === 'DEPOSIT' ? '+' : '-' }}{{ new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(tx.amount) }}
              </span>
              <div class="flex items-center gap-1 shrink-0">
                <button @click="openEdit(tx)" title="Edit" class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-black/5 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                </button>
                <button @click="deleteTx(tx.id)" title="Delete" class="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6"/><path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>
                </button>
              </div>
            </div>
            <div v-if="!transactions.length" class="text-sm text-gray-400 text-center py-12">No transactions yet</div>
          </template>
        </div>
      </div>

    </div>

    <!-- Mobile FAB -->
    <button @click="openAdd" class="fixed bottom-6 right-6 md:hidden w-14 h-14 bg-gray-900 text-white rounded-full text-2xl flex items-center justify-center shadow-lg hover:bg-gray-700 transition-colors">+</button>

    <!-- Modal unchanged -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 bg-black/40 flex items-end md:items-center justify-center z-50 p-4" @click.self="showModal = false">
        <div class="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
          <h2 class="text-lg font-bold text-gray-900 mb-4">{{ editingTx ? 'Edit Transaction' : 'Add Transaction' }}</h2>
          <div class="flex flex-col gap-3">
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Merchant</label>
              <input v-model="form.merchant" type="text" placeholder="Amazon, Starbucks…" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Amount</label>
              <input :value="amountInput" @input="handleAmountInput" @keydown="handleAmountKeydown" @paste="handleAmountPaste" inputmode="decimal" placeholder="0.00" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
              <p v-if="amountError" class="mt-1 text-xs text-red-500">{{ amountError }}</p>
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Date</label>
              <input v-model="form.date" type="date" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Type</label>
              <select v-model="form.transactionType" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 bg-white">
                <option v-for="t in TRANSACTION_TYPES" :key="t" :value="t">{{ typeLabel(t) }}</option>
              </select>
            </div>
            <div v-if="form.transactionType !== 'DEPOSIT'">
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Category</label>
              <select v-model="form.category" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 bg-white">
                <option v-for="c in CATEGORIES" :key="c" :value="c">{{ categoryLabel(c) }}</option>
              </select>
            </div>
            <label class="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
              <input v-model="form.reimbursable" type="checkbox" class="rounded" />
              Reimbursable
            </label>
          </div>
          <div class="flex gap-2 mt-5">
            <button @click="showModal = false" class="flex-1 border border-gray-200 text-gray-600 text-sm font-medium py-2 rounded-lg hover:bg-gray-50 transition-colors">Cancel</button>
            <button @click="submitForm" class="flex-1 bg-gray-900 text-white text-sm font-medium py-2 rounded-lg hover:bg-gray-700 transition-colors">{{ editingTx ? 'Save' : 'Add' }}</button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>
