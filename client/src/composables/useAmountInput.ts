import { computed, ref } from 'vue'

export function useAmountInput() {
  const amountInput = ref('')
  const amountError = ref('')

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
    window.clearTimeout((showAmountError as any).timeout)
    ;(showAmountError as any).timeout = window.setTimeout(() => {
      amountError.value = ''
    }, 3000)
  }

  const handleAmountInput = (e: InputEvent) => {
    const target = e.target as HTMLInputElement
    const sanitized = sanitizeAmount(target.value)
    if (target.value !== sanitized) {
      showAmountError(
        'Only numbers and a single decimal point are allowed. Max two decimal places.',
      )
    }
    amountInput.value = sanitized
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
    const target = e.target as HTMLInputElement
    if (allowedKeys.includes(e.key)) return
    if (e.key === '.') {
      if (target.value.includes('.')) {
        e.preventDefault()
        showAmountError('Only one decimal point is allowed.')
      }
      return
    }
    if (!/^\d$/.test(e.key)) {
      e.preventDefault()
      showAmountError('Only numeric input is allowed.')
      return
    }
    const start = target.selectionStart ?? 0
    const end = target.selectionEnd ?? 0
    const proposed = target.value.slice(0, start) + e.key + target.value.slice(end)
    const decimalIndex = proposed.indexOf('.')
    if (decimalIndex >= 0 && proposed.slice(decimalIndex + 1).length > 2) {
      e.preventDefault()
      showAmountError('Maximum two decimal places allowed.')
    }
  }

  const handleAmountPaste = (e: ClipboardEvent) => {
    const pasted = e.clipboardData?.getData('text/plain') ?? ''
    if (/[^0-9.]/.test(pasted) || (pasted.match(/\./g) ?? []).length > 1) {
      e.preventDefault()
      showAmountError('Only digits and a decimal point are allowed.')
      return
    }
    const target = e.target as HTMLInputElement
    const start = target.selectionStart ?? 0
    const end = target.selectionEnd ?? 0
    const proposed = target.value.slice(0, start) + pasted + target.value.slice(end)
    const decimalIndex = proposed.indexOf('.')
    if (decimalIndex >= 0 && proposed.slice(decimalIndex + 1).length > 2) {
      e.preventDefault()
      showAmountError('Only two decimal places are allowed.')
    }
  }

  const parsedAmount = computed(() => parseFloat(amountInput.value) || 0)

  return {
    amountInput,
    amountError,
    parsedAmount,
    handleAmountInput,
    handleAmountKeydown,
    handleAmountPaste,
  }
}
