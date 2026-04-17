<script setup lang="ts">
import { useToast } from '@/composables/useToast'
const { toasts, removeToast, confirmMessage, resolveConfirm } = useToast()
</script>

<template>
  <div class="fixed top-4 right-4 flex flex-col gap-2 z-50">
    <div v-for="toast in toasts" :key="toast.id" @click="removeToast(toast.id)"
      :class="['px-4 py-3 rounded-xl shadow-lg text-sm font-medium cursor-pointer',
        toast.type === 'success' ? 'bg-gray-900 text-white' :
        toast.type === 'error'   ? 'bg-red-500 text-white' : 'bg-gray-600 text-white']">
      {{ toast.message }}
    </div>
  </div>

  <div v-if="confirmMessage" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl p-6 w-full max-w-xs shadow-xl flex flex-col gap-4">
      <p class="text-sm font-medium text-gray-900">{{ confirmMessage }}</p>
      <div class="flex gap-2">
        <button @click="resolveConfirm(false)" class="flex-1 border border-gray-200 text-gray-600 text-sm font-medium py-2 rounded-lg hover:bg-gray-50 transition-colors">Cancel</button>
        <button @click="resolveConfirm(true)" class="flex-1 bg-red-500 text-white text-sm font-medium py-2 rounded-lg hover:bg-red-600 transition-colors">Delete</button>
      </div>
    </div>
  </div>
</template>