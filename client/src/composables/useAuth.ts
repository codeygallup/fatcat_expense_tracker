import api from '@/api'
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from '@/composables/useToast'

const isLoggedIn = ref(!!localStorage.getItem('token'))

export function useAuth(props = { isRegister: false }) {
  const router = useRouter()
  const { addToast } = useToast()
  const email = ref('')
  const password = ref('')
  const confirmPassword = ref('')
  const showStrongPassword = ref(false)
  let strongTimer: ReturnType<typeof setTimeout> | null = null

  const hasUpper = computed(() => /[A-Z]/.test(password.value))
  const hasLower = computed(() => /[a-z]/.test(password.value))
  const hasNumber = computed(() => /[0-9]/.test(password.value))
  const hasSymbol = computed(() => /[^A-Za-z0-9]/.test(password.value))
  const hasMinLength = computed(() => password.value.length >= 8)
  const passwordValid = computed(
    () =>
      hasUpper.value && hasLower.value && hasNumber.value && hasSymbol.value && hasMinLength.value,
  )
  const passwordsMatch = computed(() => password.value === confirmPassword.value)
  const passwordRules = computed(() => [
    { label: 'At least 8 characters', valid: hasMinLength.value },
    { label: 'Uppercase letter', valid: hasUpper.value },
    { label: 'Lowercase letter', valid: hasLower.value },
    { label: 'Number', valid: hasNumber.value },
    { label: 'Special character', valid: hasSymbol.value },
  ])
  watch(passwordValid, (val) => {
    if (val) {
      showStrongPassword.value = true
      clearTimeout(strongTimer!)
      strongTimer = setTimeout(() => {
        showStrongPassword.value = false
      }, 2500)
    } else {
      showStrongPassword.value = false
      clearTimeout(strongTimer!)
    }
  })

  const handleSubmit = async () => {
    if (props.isRegister && (!passwordValid.value || !passwordsMatch.value)) {
      addToast('Please fix the password requirements before submitting.', 'error')
      return
    }

    try {
      const res = await api(props.isRegister ? '/users/register' : '/users/login', {
        method: 'POST',
        body: JSON.stringify({ email: email.value, password: password.value }),
        raw: true
      })
      if (!res.ok) {
        if (res.status === 401) addToast('Invalid email or password.', 'error')
        else if (res.status === 409) addToast('Email already in use. Please choose another.', 'error')
        else addToast('Something went wrong. Please try again.', 'error')
        return
      }
      const token = await res.text()
      login(token)
      router.push('/dashboard')
    } catch (err) {
      addToast('Failed to connect to server. Please try again.', 'error')
    }
  }

  const login = (token: string) => {
    localStorage.setItem('token', token)
    isLoggedIn.value = true
  }

  const logout = () => {
    localStorage.removeItem('token')
    isLoggedIn.value = false
    router.push('/login')
  }

  return {
    isLoggedIn,
    logout,
    email,
    password,
    confirmPassword,
    passwordRules,
    passwordValid,
    passwordsMatch,
    handleSubmit,
    showStrongPassword,
  }
}
