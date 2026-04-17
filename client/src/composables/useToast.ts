import { ref } from 'vue'
import type { Toast } from '@/types'

const toasts = ref<Toast[]>([])
let nextId = 0
const confirmResolver = ref<((val: boolean) => void) | null>(null)
const confirmMessage = ref<string | null>(null)

export function useToast() {
  const addToast = (message: string, type: Toast['type'] = 'success', duration = 3000) => {
    const id = nextId++
    toasts.value.push({ id, message, type })
    setTimeout(() => removeToast(id), duration)
  }

  const removeToast = (id: number) => {
    toasts.value = toasts.value.filter((toast) => toast.id !== id)
  }

  const confirm = (message: string): Promise<boolean> => {
    confirmMessage.value = message
    return new Promise((resolve) => {
      confirmResolver.value = resolve
    })
  }

  const resolveConfirm = (val: boolean) => {
    confirmResolver.value?.(val)
    confirmMessage.value = null
    confirmResolver.value = null
  }

  return { toasts, addToast, removeToast, confirmMessage, confirm, resolveConfirm }
}
