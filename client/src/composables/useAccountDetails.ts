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

export function useAccountDetails(accountId: string) {
  const { addToast, confirm } = useToast()
  const account = ref<Account | null>(null)
  const transactions = ref<Transaction[]>([])
  const showModal = ref(false)
  const showChart = ref(true)
  const chartType = ref<'time' | 'category'>('time')
  const timeRange = ref<'weekly' | 'monthly'>('weekly')
  const editingTx = ref<Transaction | null>(null)
  const amountInput = ref('')
  const amountError = ref('')
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
  const sanitizeAmount = (value: string) => {
    const cleaned = value
      .replace(/,/g, '')
      .replace(/[^0-9.]/g, '')
      .replace(/(\..*)\./g, '$1')

    const [integer = '', decimal] = cleaned.split('.')
    const trimmedInt = integer.replace(/^0+(?=\d)/, '') || '0'
    const trimmedDec = decimal !== undefined ? decimal.slice(0, 2) : undefined

    return trimmedDec !== undefined ? `${trimmedInt}.${trimmedDec}` : trimmedInt
  }
  const showAmountError = (message: string) => {
    amountError.value = message
    window.clearTimeout((showAmountError as any).timeout);(showAmountError as any).timeout =
      window.setTimeout(() => {
        amountError.value = ''
      }, 3000)
  }
  const handleAmountInput = (e: InputEvent) => {
    const target = e.target as HTMLInputElement
    const raw = target.value
    const sanitized = sanitizeAmount(raw)

    if (raw !== sanitized) {
      showAmountError(
        'Only numbers and a single decimal point are allowed. Max two decimal places.',
      )
    }

    amountInput.value = sanitized
    amountError.value = ''
  }

  const handleAmountKeydown = (e: KeyboardEvent) => {
    const allowedKeys = [
      'Backspace',
      'Tab',
      'ArrowLeft',
      'ArrowRight',
      'ArrowUp',
      'ArrowDown',
      'Delete',
      'Home',
      'End',
    ]
    const key = e.key
    const target = e.target as HTMLInputElement

    if (allowedKeys.includes(key)) return

    if (key === '.') {
      if (target.value.includes('.')) {
        e.preventDefault()
        showAmountError('Only one decimal point is allowed.')
      }
      return
    }

    if (!/^\d$/.test(key)) {
      e.preventDefault()
      showAmountError('Only numeric input is allowed.')
      return
    }

    const selectionStart = target.selectionStart ?? 0
    const selectionEnd = target.selectionEnd ?? 0
    const proposed = target.value.slice(0, selectionStart) + key + target.value.slice(selectionEnd)
    const decimalIndex = proposed.indexOf('.')

    if (decimalIndex >= 0) {
      const decimalPart = proposed.slice(decimalIndex + 1)
      if (decimalPart.length > 2) {
        e.preventDefault()
        showAmountError('Maximum two decimal places allowed.')
      }
    }
  }
  const handleAmountPaste = (event: ClipboardEvent) => {
    const pasted = event.clipboardData?.getData('text/plain') ?? ''
    if (/[^0-9.]/.test(pasted) || (pasted.match(/\./g) ?? []).length > 1) {
      event.preventDefault()
      showAmountError('Only digits and a decimal point are allowed.')
      return
    }

    const target = event.target as HTMLInputElement
    const selectionStart = target.selectionStart ?? 0
    const selectionEnd = target.selectionEnd ?? 0
    const proposed =
      target.value.slice(0, selectionStart) + pasted + target.value.slice(selectionEnd)
    const decimalIndex = proposed.indexOf('.')
    if (decimalIndex >= 0 && proposed.slice(decimalIndex + 1).length > 2) {
      event.preventDefault()
      showAmountError('Only two decimal places are allowed.')
    }
  }
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
          body: JSON.stringify({ ...form.value, amount: parseFloat(amountInput.value) }),
        })
      } else {
        await api('/transactions', {
          method: 'POST',
          body: JSON.stringify({ ...form.value, amount: parseFloat(amountInput.value), accountId }),
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
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
    donutOptions,
    donutSeries,
    donutLabels,
    fetchAll,
    deleteTx: deleteTransaction,
    openAdd,
    openEdit,
    submitForm,
  }
}
