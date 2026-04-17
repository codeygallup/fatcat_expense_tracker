import api from '@/api'
import type { FilterTab, Bill, BillForm, BillStatus } from '@/types'
import { useToast } from './useToast'
import { computed, onMounted, ref } from 'vue'
import { useAmountInput } from './useAmountInput'

export function useBill() {
  const {
    amountInput,
    amountError,
    parsedAmount,
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
  } = useAmountInput()

  const { addToast, confirm } = useToast()
  const bills = ref<Bill[]>([])
  const activeFilter = ref<FilterTab>('ALL')
  const showModal = ref(false)
  const editingBill = ref<Bill | null>(null)
  const form = ref<BillForm>({ name: '', dueDate: '' })
  const loading = ref(false)
  const TABS: FilterTab[] = ['ALL', 'PAID', 'UNPAID', 'OVERDUE']

  const tabLabel = (t: FilterTab) => (t === 'ALL' ? 'ALL' : t.charAt(0) + t.slice(1).toLowerCase())
  const filteredBills = computed(() => {
    const list =
      activeFilter.value === 'ALL'
        ? bills.value
        : bills.value.filter((b) => b.status === activeFilter.value)
    return [...list].sort((a, b) => new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime())
  })
  const statusBadge = (status: BillStatus) =>
    ({
      PAID: 'bg-green-100 text-green-800',
      UNPAID: 'bg-yellow-100 text-yellow-800',
      OVERDUE: 'bg-red-100 text-red-800',
    })[status]
  const rowBg = (status: BillStatus) =>
    ({
      PAID: 'bg-green-50 border-green-200',
      UNPAID: 'bg-yellow-50 border-yellow-200',
      OVERDUE: 'bg-red-50 border-red-200',
    })[status]

  const fetchBills = async () => {
    loading.value = true
    try {
      bills.value = await api('/bills').then((r) => r.json())
    } catch (e) {
      console.error('Failed to fetch bills', e)
    } finally {
      loading.value = false
    }
  }

  const updateStatus = async (bill: Bill) => {
    const updated: Bill = await api(`/bills/${bill.id}`, { method: 'PATCH' }).then((r) => r.json())
    const idx = bills.value.findIndex((b) => b.id === bill.id)
    if (idx !== -1) bills.value[idx] = updated
  }

  const deleteBill = async (id: string) => {
    const ok = await confirm(
      'Are you sure you want to delete this bill? This action cannot be undone.',
    )
    if (!ok) return
    try {
      await api(`/bills/${id}`, { method: 'DELETE' })
      await fetchBills()
      addToast('Bill deleted successfully')
    } catch (e) {
      console.error('Failed to delete bill', e)
      addToast('Failed to delete bill', 'error')
    }
  }

  const openAdd = () => {
    editingBill.value = null
    form.value = { name: '', dueDate: '' }
    amountInput.value = ''
    showModal.value = true
  }

  const openEdit = (bill: Bill) => {
    editingBill.value = bill
    form.value = {
      name: bill.name,
      dueDate: bill.dueDate.slice(0, 10),
    }
    amountInput.value = bill.amount.toString()
    showModal.value = true
  }

  const submitForm = async () => {
    try {
      if (editingBill.value) {
        await api(`/bills/${editingBill.value.id}`, {
          method: 'PUT',
          body: JSON.stringify({ ...form.value, amount: parsedAmount.value }),
        })
      } else {
        await api('/bills', {
          method: 'POST',
          body: JSON.stringify({ ...form.value, amount: parsedAmount.value }),
        })
      }
    } catch (e) {
      console.error('Failed to submit form', e)
      addToast('Failed to save bill', 'error')
    }
    showModal.value = false
    await fetchBills()
  }

  onMounted(fetchBills)

  return {
    bills,
    activeFilter,
    showModal,
    editingBill,
    form,
    loading,
    TABS,
    tabLabel,
    filteredBills,
    statusBadge,
    rowBg,
    updateStatus,
    deleteBill,
    openAdd,
    openEdit,
    submitForm,
    amountInput,
    amountError,
    parsedAmount,
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
  }
}
