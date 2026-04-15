import { onMounted, ref } from 'vue'
import type { AccountForm, Account, AccountType } from '@/types'
import { useToast } from './useToast'
import api from '@/api'

export function useAccount() {
  const { addToast, confirm } = useToast()
  const loading = ref(false)
  const accounts = ref<Account[]>([])
  const editingAccount = ref<Account | null>(null)
  const form = ref<AccountForm>({
    name: '',
    accountType: 'CHECKING',
    balance: 0,
  })
  const showModal = ref(false)
  const ACCOUNT_TYPES: AccountType[] = ['CHECKING', 'SAVINGS', 'CREDIT_CARD']
  const typeLabel = (t: AccountType) =>
    ({ CHECKING: 'Checking', SAVINGS: 'Savings', CREDIT_CARD: 'Credit Card' })[t]
  const typeIcon = (type: AccountType) =>
    ({ CHECKING: '🏦', SAVINGS: '🐖', CREDIT_CARD: '💳' })[type]
  const truncateText = (text: string, maxLength = 24) =>
    text.length > maxLength ? `${text.slice(0, maxLength - 3)}...` : text

  const fetchAccounts = async () => {
    loading.value = true
    try {
      accounts.value = await api('/accounts').then((r) => r.json())
    } catch (e) {
      console.error('Failed to fetch accounts', e)
    } finally {
      loading.value = false
    }
  }

  const deleteAccount = async (id: string) => {
    const ok = await confirm(
      'Are you sure you want to delete this account? This action cannot be undone.',
    )
    if (!ok) return
    try {
      await api(`/accounts/${id}`, { method: 'DELETE' })
      await fetchAccounts()
      addToast('Account deleted successfully')
    } catch (e) {
      console.error('Failed to delete account', e)
      addToast('Failed to delete account', 'error')
    }
  }

  const openAdd = () => {
    editingAccount.value = null
    form.value = {
      name: '',
      accountType: 'CHECKING',
      balance: 0,
    }
    showModal.value = true
  }

  const openEdit = (account: Account) => {
    editingAccount.value = account
    form.value = { name: account.name, accountType: account.accountType, balance: account.balance }
    showModal.value = true
  }

  const submitForm = async () => {
    try {
      if (editingAccount.value) {
        await api(`/accounts/${editingAccount.value.id}`, {
          method: 'PUT',
          body: JSON.stringify(form.value),
        })
      } else {
        await api('/accounts', {
          method: 'POST',
          body: JSON.stringify(form.value),
        })
      }
    } catch (e) {
      console.error('Failed to submit form', e)
    }
    showModal.value = false
    await fetchAccounts()
  }

  onMounted(fetchAccounts)

  return {
    loading,
    accounts,
    editingAccount,
    form,
    showModal,
    ACCOUNT_TYPES,
    typeLabel,
    typeIcon,
    truncateText,
    deleteAccount,
    openAdd,
    openEdit,
    submitForm,
  }
}
