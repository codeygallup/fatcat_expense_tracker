<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import VueApexCharts from 'vue3-apexcharts'
import api from '@/api/index'

type AccountType = 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD'
type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER'
type TransactionCategory =
  | 'FOOD'
  | 'TRANSPORT'
  | 'ENTERTAINMENT'
  | 'UTILITIES'
  | 'HEALTH'
  | 'SHOPPING'
  | 'RENT'
  | 'OTHER'

interface Account {
  id: string
  name: string
  balance: number
  accountType: AccountType
}

interface Transaction {
  id: string
  merchant: string
  amount: number
  date: string
  transactionType: TransactionType
  category: TransactionCategory
  reimbursable: boolean
}

interface TransactionForm {
  merchant: string
  amount: number
  date: string
  transactionType: TransactionType
  category: TransactionCategory
  reimbursable: boolean
}

const route = useRoute()
const accountId = route.params.id as string

const account = ref<Account | null>(null)
const transactions = ref<Transaction[]>([])
const showModal = ref(false)
const editingTx = ref<Transaction | null>(null)
const form = ref<TransactionForm>({
  merchant: '',
  amount: 0,
  date: new Date().toISOString().split('T')[0],
  transactionType: 'WITHDRAWAL',
  category: 'OTHER',
  reimbursable: false,
})

const CATEGORIES: TransactionCategory[] = [
  'FOOD', 'TRANSPORT', 'ENTERTAINMENT', 'UTILITIES', 'HEALTH', 'SHOPPING', 'RENT', 'OTHER',
]
const TRANSACTION_TYPES: TransactionType[] = ['WITHDRAWAL', 'DEPOSIT', 'TRANSFER']

const categoryLabel = (c: TransactionCategory) =>
  c.charAt(0) + c.slice(1).toLowerCase()

const typeLabel = (t: TransactionType) =>
  t.charAt(0) + t.slice(1).toLowerCase()

const typeIcon = (type: AccountType) =>
  ({ CHECKING: '🏦', SAVINGS: '🐖', CREDIT_CARD: '💳' })[type]

// Donut — spending by category (withdrawals only)
const spendingByCategory = computed(() => {
  const map: Record<string, number> = {}
  transactions.value
    .filter(tx => tx.transactionType !== 'DEPOSIT')
    .forEach(tx => {
      map[tx.category] = (map[tx.category] ?? 0) + tx.amount
    })
  return map
})

const donutSeries = computed(() => Object.values(spendingByCategory.value))
const donutLabels = computed(() =>
  Object.keys(spendingByCategory.value).map(categoryLabel)
)
const donutOptions = computed(() => ({
  chart: { type: 'donut' },
  labels: donutLabels.value,
  legend: { position: 'bottom' },
  dataLabels: { enabled: false },
  plotOptions: { pie: { donut: { size: '65%' } } },
  colors: ['#6366f1','#f59e0b','#10b981','#3b82f6','#ef4444','#8b5cf6','#ec4899','#94a3b8'],
}))

async function fetchAll() {
  const [acct, txs] = await Promise.all([
    api(`/accounts/${accountId}`).then(r => r.json()),
    api(`/transactions?accountId=${accountId}`).then(r => r.json()),
  ])
  account.value = acct
  transactions.value = txs
}

async function deleteTx(id: string) {
  if (!window.confirm('Delete this transaction?')) return
  await api(`/transactions/${id}`, { method: 'DELETE' })
  await fetchAll()
}

function openAdd() {
  editingTx.value = null
  form.value = {
    merchant: '',
    amount: 0,
    date: new Date().toISOString().split('T')[0],
    transactionType: 'WITHDRAWAL',
    category: 'OTHER',
    reimbursable: false,
  }
  showModal.value = true
}

function openEdit(tx: Transaction) {
  editingTx.value = tx
  form.value = {
    merchant: tx.merchant,
    amount: tx.amount,
    date: tx.date,
    transactionType: tx.transactionType,
    category: tx.category,
    reimbursable: tx.reimbursable,
  }
  showModal.value = true
}

async function submitForm() {
  const payload = {
    accountId,
    merchant: form.value.merchant,
    amount: form.value.amount,
    date: form.value.date,
    transactionType: form.value.transactionType,
    category: form.value.category,
    reimbursable: form.value.reimbursable,
  }
  if (editingTx.value) {
    await api(`/transactions/${editingTx.value.id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
  } else {
    await api('/transactions', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  }
  showModal.value = false
  await fetchAll()
}

onMounted(fetchAll)
</script>

<template>
  <div class="p-4 max-w-2xl mx-auto pb-24 sm:pb-4">

    <!-- Account header -->
    <div v-if="account" class="bg-white border border-gray-200 rounded-2xl p-6 mb-4 flex items-center gap-4">
      <span class="text-3xl">{{ typeIcon(account.accountType) }}</span>
      <div class="flex-1 min-w-0">
        <h1 class="text-xl font-bold text-gray-900 truncate">{{ account.name }}</h1>
        <p class="text-xs text-gray-400 capitalize">{{ account.accountType.toLowerCase().replace('_', ' ') }}</p>
      </div>
      <span class="text-2xl font-bold" :class="account.balance < 0 ? 'text-red-600' : 'text-gray-900'">
        {{ new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(account.balance) }}
      </span>
    </div>

    <!-- Donut chart -->
    <div v-if="donutSeries.length" class="bg-white border border-gray-200 rounded-2xl p-6 mb-4">
      <p class="text-xs text-gray-500 uppercase tracking-wide mb-3">Spending by Category</p>
      <VueApexCharts type="donut" height="260" :options="donutOptions" :series="donutSeries" />
    </div>

    <!-- Transactions list -->
    <div class="flex items-center justify-between mb-3">
      <p class="text-xs text-gray-500 uppercase tracking-wide">Transactions</p>
      <button
        @click="openAdd"
        class="hidden sm:flex items-center gap-1 text-sm font-medium bg-gray-900 text-white px-3 py-1.5 rounded-lg hover:bg-gray-700 transition-colors"
      >
        + Add
      </button>
    </div>

    <div class="flex flex-col gap-2">
      <div
        v-for="tx in transactions"
        :key="tx.id"
        class="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-3"
      >
        <div class="flex-1 min-w-0">
          <p class="font-medium text-gray-900 truncate">{{ tx.merchant }}</p>
          <p class="text-xs text-gray-400">
            {{ categoryLabel(tx.category) }} · {{ new Date(tx.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) }}
          </p>
        </div>
        <span class="text-base font-semibold shrink-0" :class="tx.transactionType === 'DEPOSIT' ? 'text-green-600' : 'text-gray-900'">
          {{ tx.transactionType === 'DEPOSIT' ? '+' : '-' }}{{ new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(tx.amount) }}
        </span>
        <div class="flex items-center gap-1 shrink-0">
          <button @click="openEdit(tx)" title="Edit" class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-black/5 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </button>
          <button @click="deleteTx(tx.id)" title="Delete" class="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
              <path d="M10 11v6M14 11v6"/>
              <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
            </svg>
          </button>
        </div>
      </div>
      <div v-if="!transactions.length" class="text-sm text-gray-400 text-center py-12">No transactions yet</div>
    </div>

    <!-- Mobile FAB -->
    <button
      @click="openAdd"
      class="fixed bottom-6 right-6 sm:hidden w-14 h-14 bg-gray-900 text-white rounded-full text-2xl flex items-center justify-center shadow-lg hover:bg-gray-700 transition-colors"
    >
      +
    </button>

    <!-- Add / Edit modal -->
    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 bg-black/40 flex items-end sm:items-center justify-center z-50 p-4"
        @click.self="showModal = false"
      >
        <div class="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
          <h2 class="text-lg font-bold text-gray-900 mb-4">{{ editingTx ? 'Edit Transaction' : 'Add Transaction' }}</h2>
          <div class="flex flex-col gap-3">
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Merchant</label>
              <input v-model="form.merchant" type="text" placeholder="Amazon, Starbucks…"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Amount</label>
              <input v-model.number="form.amount" type="number" step="0.01" min="0" placeholder="0.00"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Date</label>
              <input v-model="form.date" type="date"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Type</label>
              <select v-model="form.transactionType"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 bg-white">
                <option v-for="t in TRANSACTION_TYPES" :key="t" :value="t">{{ typeLabel(t) }}</option>
              </select>
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Category</label>
              <select v-model="form.category"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 bg-white">
                <option v-for="c in CATEGORIES" :key="c" :value="c">{{ categoryLabel(c) }}</option>
              </select>
            </div>
            <label class="flex items-center gap-2 text-sm text-gray-600 cursor-pointer">
              <input v-model="form.reimbursable" type="checkbox" class="rounded" />
              Reimbursable
            </label>
          </div>
          <div class="flex gap-2 mt-5">
            <button @click="showModal = false"
              class="flex-1 border border-gray-200 text-gray-600 text-sm font-medium py-2 rounded-lg hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button @click="submitForm"
              class="flex-1 bg-gray-900 text-white text-sm font-medium py-2 rounded-lg hover:bg-gray-700 transition-colors">
              {{ editingTx ? 'Save' : 'Add' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>