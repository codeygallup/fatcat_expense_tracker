<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api/index'
import { useAuth } from '@/composables/useAuth'

const props = defineProps<{ isRegister?: boolean }>()

const name = ref('')
const email = ref('')
const password = ref('')

const { login } = useAuth()

const router = useRouter()

const handleSubmit = async () => {
  const res = await api(props.isRegister ? '/users/register' : '/users/login', {
    method: 'POST',
    body: JSON.stringify({ email: email.value, password: password.value }),
  })
  const token = await res.text()
  login(token)
  router.push('/dashboard')
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
      </div>
      <div class="flex items-center justify-between">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
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
