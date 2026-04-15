
export type BillStatus = 'PAID' | 'UNPAID' | 'OVERDUE'
export type FilterTab = 'ALL' | BillStatus
export type AccountType = 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD'
export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER'
export type TransactionCategory =
  | 'GROCERIES'
  | 'DINING_OUT'
  | 'HOUSING'
  | 'TRANSPORTATION'
  | 'BILLS_AND_SUBSCRIPTIONS'
  | 'ENTERTAINMENT'
  | 'MISCELLANEOUS'

export interface Toast {
  id: number
  message: string
  type: 'success' | 'error' | 'info'
}

export interface SkeletonProps {
  type?: 'card' | 'list' | 'text' | 'default'
  count?: number
}

export interface Account {
  id: string
  name: string
  balance: number
  accountType: AccountType
}

export interface AccountForm {
  name: string
  accountType: AccountType
  balance: number
}

export interface Bill {
  id: string
  name: string
  amount: number
  dueDate: string
  status: BillStatus
}

export interface BillForm {
  name: string
  amount: string
  dueDate: string
}

export interface Transaction {
  id: string
  merchant: string
  amount: number
  date: string
  transactionType: TransactionType
  category: TransactionCategory
  reimbursable: boolean
}

export interface TransactionForm {
  merchant: string
  amount: number
  date: string
  transactionType: TransactionType
  category: TransactionCategory
  reimbursable: boolean
}