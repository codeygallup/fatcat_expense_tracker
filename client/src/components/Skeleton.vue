<script setup lang="ts">
import type { SkeletonProps } from '@/types'

withDefaults(defineProps<SkeletonProps>(), {
  type: 'default',
  count: 3,
})
</script>

<template>
  <div class="animate-pulse">
    <div v-if="type === 'card'" class="bg-gray-200 rounded-lg p-4">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 bg-gray-300 rounded-full shrink-0"></div>
        <div class="flex-1">
          <div class="h-4 bg-gray-300 rounded w-3/4 mb-2"></div>
          <div class="h-3 bg-gray-300 rounded w-1/2 mb-1"></div>
          <div class="h-3 bg-gray-300 rounded w-1/3"></div>
        </div>
        <div class="flex items-center gap-2 shrink-0">
          <div class="h-6 bg-gray-300 rounded w-12"></div>
          <div class="w-6 h-6 bg-gray-300 rounded"></div>
          <div class="w-6 h-6 bg-gray-300 rounded"></div>
        </div>
      </div>
    </div>
    <div v-else-if="type === 'list'" class="space-y-2">
      <div v-for="n in count" :key="n" class="h-4 bg-gray-300 rounded"></div>
    </div>
    <div v-else-if="type === 'text'" class="h-4 bg-gray-300 rounded w-full"></div>
    <div v-else class="h-4 bg-gray-300 rounded w-3/4"></div>
  </div>
</template>
