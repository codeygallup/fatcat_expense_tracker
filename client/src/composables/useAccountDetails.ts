import api from '@/api'
import type {
  TransactionForm,
  Account,
  AccountType,
  Transaction,
  TransactionCategory,
  TransactionType,
} from '@/types'
import { computed, onMounted, ref } from 'vue'
import { useToast } from './useToast'
import { useAmountInput } from './useAmountInput'

export function useAccountDetails(accountId: string) {
  const {
    amountInput,
    amountError,
    parsedAmount,
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
  } = useAmountInput()

  const { addToast, confirm } = useToast()
  const account = ref<Account | null>(null)
  const transactions = ref<Transaction[]>([])
  const showModal = ref(false)
  const showChart = ref(true)
  const chartType = ref<'time' | 'category'>('time')
  const timeRange = ref<'weekly' | 'monthly'>('weekly')
  const editingTx = ref<Transaction | null>(null)
  const form = ref<TransactionForm>({
    merchant: '',
    amount: 0,
    date: new Date().toISOString().split('T')[0] ?? '',
    transactionType: 'WITHDRAWAL',
    category: 'MISCELLANEOUS',
    reimbursable: false,
  })
  const loading = ref(false)
  const CATEGORIES: TransactionCategory[] = [
    'GROCERIES',
    'DINING_OUT',
    'HOUSING',
    'TRANSPORTATION',
    'BILLS_AND_SUBSCRIPTIONS',
    'ENTERTAINMENT',
    'MISCELLANEOUS',
  ]
  const TRANSACTION_TYPES: TransactionType[] = ['WITHDRAWAL', 'DEPOSIT']

  const categoryLabel = (c: TransactionCategory) =>
    c
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/^\w/, (ch) => ch.toUpperCase())
  const typeLabel = (t: TransactionType) =>
    ({
      WITHDRAWAL: 'Expense',
      DEPOSIT: 'Income',
      TRANSFER: 'Transfer',
    })[t]
  const typeIcon = (t: AccountType) => ({ CHECKING: '🏦', SAVINGS: '🐖', CREDIT_CARD: '💳' })[t]

  // Line chart — spending by date (withdrawals only)
  const spendingByDate = computed(() => {
    const map: Record<string, number> = {}
    const dates: string[] = []
    const txs = transactions.value
      .filter((t) => t.transactionType !== 'DEPOSIT')
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())

    if (timeRange.value === 'weekly') {
      txs.forEach((t) => {
        const date = new Date(t.date + 'T00:00:00')
        const weekStart = new Date(date)
        weekStart.setDate(date.getDate() - date.getDay())
        const weekKey = weekStart.toISOString().split('T')[0] || ''
        if (weekKey) {
          map[weekKey] = (map[weekKey] || 0) + t.amount
          if (!dates.includes(weekKey)) dates.push(weekKey)
        }
      })
    } else {
      txs.forEach((t) => {
        const date = new Date(t.date + 'T00:00:00')
        const monthKey = date.toISOString().slice(0, 7)
        if (monthKey) {
          map[monthKey] = (map[monthKey] || 0) + t.amount
          if (!dates.includes(monthKey)) dates.push(monthKey)
        }
      })
    }

    dates.sort()
    return { dates, amounts: dates.map((d) => Math.round((map[d] ?? 0) * 100) / 100) }
  })

  const lineOptions = computed(() => {
    const categoryLabels = spendingByDate.value.dates.map((d) => {
      if (timeRange.value === 'weekly') {
        const date = new Date(d + 'T00:00:00')
        const weekEnd = new Date(date)
        weekEnd.setDate(date.getDate() + 6)
        return `${date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} - ${weekEnd.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`
      } else {
        return new Date(d + '-01T00:00:00').toLocaleDateString('en-US', {
          month: 'short',
          year: '2-digit',
        })
      }
    })
    return {
      chart: { type: 'line' as const, zoom: { enabled: true } },
      xaxis: { categories: categoryLabels },
      yaxis: { title: { text: 'Amount ($)' } },
      stroke: { curve: 'smooth' as const },
      fill: { type: 'gradient' as const },
    }
  })
  const lineSeries = computed(() => [{ name: 'Spending', data: spendingByDate.value.amounts }])
  // Donut — spending by category (withdrawals only)
  const spendingByCategory = computed(() => {
    const map: Record<string, number> = {}
    transactions.value
      .filter((t) => t.transactionType !== 'DEPOSIT')
      .forEach((t) => {
        map[t.category] = (map[t.category] || 0) + t.amount
      })
    return map
  })
  const donutSeries = computed(() => Object.values(spendingByCategory.value))
  const donutLabels = computed(() =>
    Object.keys(spendingByCategory.value).map((c) => categoryLabel(c as TransactionCategory)),
  )
  const donutOptions = computed(() => ({
    chart: { type: 'donut' as const },
    labels: donutLabels.value,
    legend: { position: 'bottom' as const },
    dataLabels: { enabled: false },
    plotOptions: { pie: { donut: { size: '70%' } } },
    colors: [
      '#6366f1',
      '#f59e0b',
      '#10b981',
      '#3b82f6',
      '#ef4444',
      '#8b5cf6',
      '#ec4899',
      '#94a3b8',
    ],
  }))

  const fetchAll = async () => {
    loading.value = true
    try {
      const [accountData, transactionsData] = await Promise.all([
        api(`/accounts/${accountId}`).then((r) => r.json()),
        api(`/transactions?accountId=${accountId}`).then((r) => r.json()),
      ])
      account.value = accountData
      transactions.value = transactionsData
    } catch (e) {
      console.error('Failed to fetch account details', e)
    } finally {
      loading.value = false
    }
  }
  const deleteTransaction = async (id: string) => {
    const ok = await confirm(
      'Are you sure you want to delete this transaction? This action cannot be undone.',
    )
    if (!ok) return
    try {
      await api(`/transactions/${id}`, { method: 'DELETE' })
      await fetchAll()
      addToast('Transaction deleted successfully')
    } catch (e) {
      console.error('Failed to delete transaction', e)
      addToast('Failed to delete transaction', 'error')
    }
  }
  const openAdd = () => {
    editingTx.value = null
    form.value = {
      merchant: '',
      amount: 0,
      date: new Date().toISOString().split('T')[0] ?? '',
      transactionType: 'WITHDRAWAL',
      category: 'MISCELLANEOUS',
      reimbursable: false,
    }
    amountInput.value = ''
    showModal.value = true
  }
  const openEdit = (tx: Transaction) => {
    editingTx.value = tx
    form.value = { ...tx }
    amountInput.value = tx.amount.toString()
    showModal.value = true
  }
  const submitForm = async () => {
    try {
      if (editingTx.value) {
        await api(`/transactions/${editingTx.value.id}`, {
          method: 'PUT',
          body: JSON.stringify({ ...form.value, amount: parsedAmount.value }),
        })
      } else {
        await api('/transactions', {
          method: 'POST',
          body: JSON.stringify({ ...form.value, amount: parsedAmount.value, accountId }),
        })
      }
      showModal.value = false
      await fetchAll()
      addToast(`Transaction ${editingTx.value ? 'updated' : 'added'} successfully`)
    } catch (e) {
      console.error('Failed to submit transaction', e)
      addToast(`Failed to ${editingTx.value ? 'update' : 'add'} transaction`, 'error')
    }
  }
  onMounted(fetchAll)

  return {
    account,
    transactions,
    showModal,
    showChart,
    chartType,
    timeRange,
    editingTx,
    amountInput,
    amountError,
    form,
    loading,
    CATEGORIES,
    TRANSACTION_TYPES,
    categoryLabel,
    typeLabel,
    typeIcon,
    lineOptions,
    lineSeries,
    donutOptions,
    donutSeries,
    donutLabels,
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
    fetchAll,
    deleteTx: deleteTransaction,
    openAdd,
    openEdit,
    submitForm,
  }
}
