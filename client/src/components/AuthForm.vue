<script setup lang="ts">
defineProps<{
  isRegister?: boolean
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center p-8 flex-1">
    <h2 class="text-2xl font-bold mb-6">{{ isRegister ? 'Register' : 'Login' }}</h2>
    <form class="w-full max-w-sm bg-white rounded-lg shadow-md p-6">
      <div v-if="isRegister" class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="username">Username</label>
        <input
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="username"
          type="text"
          placeholder="Enter your username"
        />
      </div>
      <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="email">Email</label>
        <input
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          id="email"
          type="email"
          placeholder="Enter your email"
        />
      </div>
      <div class="mb-6">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="password">Password</label>
        <input
          class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
          id="password"
          type="password"
          placeholder="Enter your password"
        />
      </div>
      <div class="flex items-center justify-between">
        <button
          class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
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
