<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/index'
import { useAuth } from '@/composables/useAuth'

const props = defineProps<{ isRegister?: boolean }>()

const email = ref('')
const password = ref('')
const confirmPassword = ref('')

const hasUpper = computed(() => /[A-Z]/.test(password.value))
const hasLower = computed(() => /[a-z]/.test(password.value))
const hasNumber = computed(() => /[0-9]/.test(password.value))
const hasSymbol = computed(() => /[^A-Za-z0-9]/.test(password.value))
const hasMinLength = computed(() => password.value.length >= 8)
const passwordValid = computed(
  () => hasUpper.value && hasLower.value && hasNumber.value && hasSymbol.value && hasMinLength.value
)
const passwordsMatch = computed(
  () => confirmPassword.value.length > 0 && confirmPassword.value === password.value
)

const passwordRules = computed(() => [
  { label: 'At least 8 characters', valid: hasMinLength.value },
  { label: 'Uppercase letter', valid: hasUpper.value },
  { label: 'Lowercase letter', valid: hasLower.value },
  { label: 'Number', valid: hasNumber.value },
  { label: 'Symbol', valid: hasSymbol.value },
])

const { login } = useAuth()

const router = useRouter()

const error = ref<string | null>(null)

const handleSubmit = async () => {
  error.value = null
  if (props.isRegister && (!passwordValid.value || !passwordsMatch.value)) {
    error.value = !passwordValid.value
      ? 'Please complete the password requirements.'
      : 'Passwords must match.'
    return
  }

  try {
    const res = await api(props.isRegister ? '/users/register' : '/users/login', {
      method: 'POST',
      body: JSON.stringify({ email: email.value, password: password.value }),
    })
    const token = await res.text()
    login(token)
    router.push('/dashboard')
  } catch (err) {
    error.value = (err as Error).message
  }
}
</script>

<template>
  <div class="flex flex-col items-center justify-center p-8 flex-1">
    <h2 class="text-2xl font-bold mb-6">{{ isRegister ? 'Register' : 'Login' }}</h2>
    <form @submit.prevent="handleSubmit" class="w-full max-w-sm bg-white rounded-lg shadow-md p-6">
      <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="email">Email</label>
        <input
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          v-model="email"
          id="email"
          type="email"
          placeholder="Enter your email"
        />
      </div>
      <div class="mb-6">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="password">Password</label>
        <input
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
          v-model="password"
          id="password"
          type="password"
          placeholder="Enter your password"
        />

        <div v-if="props.isRegister" class="space-y-2 text-sm">
          <div v-if="!passwordValid">
            <p class="text-gray-500">Password must include:</p>
            <div v-for="rule in passwordRules" :key="rule.label" class="flex items-center gap-2">
              <span
                :class="rule.valid ? 'text-green-600' : 'text-gray-400'"
                class="inline-flex h-5 w-5 items-center justify-center rounded-full border"
              >
                <svg v-if="rule.valid" xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="3">
                  <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                </svg>
                <span v-else class="h-1.5 w-1.5 rounded-full bg-current block"></span>
              </span>
              <span :class="rule.valid ? 'text-gray-900' : 'text-gray-500'">{{ rule.label }}</span>
            </div>
          </div>

          <div v-else>
            <p class="text-green-700 font-medium">Nice! Your password is strong.</p>
            <div class="space-y-2">
              <label class="block text-gray-700 text-sm font-bold" for="confirmPassword">Confirm Password</label>
              <input
                v-model="confirmPassword"
                id="confirmPassword"
                type="password"
                placeholder="Re-enter your password"
                :class="[
                  'shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline',
                  confirmPassword && !passwordsMatch ? 'border-red-500' : ''
                ]"
              />
              <p v-if="confirmPassword && !passwordsMatch" class="text-sm text-red-500">Passwords do not match.</p>
            </div>
          </div>
        </div>
      </div>
      <div class="flex items-center justify-between">
        <p v-if="error" class="text-sm text-red-500 mb-3">{{ error }}</p>
        <button
          :disabled="props.isRegister && (!passwordValid || !passwordsMatch)"
          :class="[
            'font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline',
            props.isRegister && (!passwordValid || !passwordsMatch)
              ? 'bg-blue-300 text-white cursor-not-allowed'
              : 'bg-blue-500 hover:bg-blue-700 text-white'
          ]"
          type="submit"
        >
          {{ isRegister ? 'Register' : 'Login' }}
        </button>
      </div>
    </form>
    <template v-if="isRegister">
      <p class="mt-4 text-sm text-gray-600">
        Already have an account?
        <RouterLink to="/login" class="text-blue-500 hover:text-blue-700">Login here</RouterLink>
      </p>
    </template>

    <template v-else>
      <p class="mt-4 text-sm text-gray-600">
        Don't have an account?
        <RouterLink to="/register" class="text-blue-500 hover:text-blue-700"
          >Register here</RouterLink
        >
      </p>
    </template>
  </div>
</template>
