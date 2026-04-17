<script setup lang="ts">
import { RouterLink } from 'vue-router'
import Skeleton from '@/components/Skeleton.vue'
import { useAccount } from '@/composables/useAccount'

const { accounts, loading, editingAccount, form, showModal, ACCOUNT_TYPES, typeLabel, typeIcon, truncateText, deleteAccount, openAdd, openEdit, submitForm } = useAccount()

</script>

<template>
  <div class="p-4 max-w-sm md:max-w-full mx-auto md:mx-20 pb-24 sm:pb-4">
    <!-- Header -->
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-bold text-gray-900">Accounts</h1>
      <button @click="openAdd"
        class="hidden sm:flex items-center gap-1 text-sm font-medium bg-gray-900 text-white px-3 py-1.5 rounded-lg hover:bg-gray-700 transition-colors">
        + Add Account
      </button>
    </div>

    <!-- Account cards -->
    <div class="flex flex-col gap-3">
      <template v-if="loading" class="flex flex-col gap-3">
        <Skeleton v-for="n in 3" :key="n" type="card" />
      </template>
      <template v-else>
        <div v-for="account in accounts" :key="account.id"
          class="bg-white border border-gray-200 rounded-xl p-4 flex items-center gap-3 hover:shadow-md transition-shadow">
          <!-- Icon -->
          <span class="text-2xl shrink-0">{{ typeIcon(account.accountType) }}</span>

          <!-- Info → navigates to detail -->
          <RouterLink :to="`/accounts/${account.id}`" class="flex-1 min-w-0">
            <p class="font-medium text-gray-900 truncate">{{ truncateText(account.name, 24) }}</p>
            <p class="text-xs text-gray-400">{{ typeLabel(account.accountType) }}</p>
          </RouterLink>

          <!-- Balance -->
        <span class="text-base font-semibold shrink-0 max-w-30 truncate"
          :class="account.balance < 0 ? 'text-red-600' : 'text-gray-900'">
          {{
            new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(
              account.balance,
            )
          }}
        </span>

          <!-- Actions -->
          <div class="flex items-center gap-1 shrink-0">
            <button @click.prevent="openEdit(account)" title="Edit"
              class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-black/5 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
              </svg>
            </button>
            <button @click.prevent="deleteAccount(account.id)" title="Delete"
              class="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors">
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" viewBox="0 0 24 24" fill="none"
                stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polyline points="3 6 5 6 21 6" />
                <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
                <path d="M10 11v6M14 11v6" />
                <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
              </svg>
            </button>
          </div>
        </div>
      </template>

      <div v-if="!accounts.length" class="text-sm text-gray-400 text-center py-12">
        No accounts yet
      </div>
    </div>

    <!-- Mobile FAB -->
    <button @click="openAdd"
      class="fixed bottom-6 right-6 sm:hidden w-14 h-14 bg-gray-900 text-white rounded-full text-2xl flex items-center justify-center shadow-lg hover:bg-gray-700 transition-colors">
      +
    </button>

    <!-- Add / Edit modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 bg-black/40 flex items-end sm:items-center justify-center z-50 p-4"
        @click.self="showModal = false">
        <div class="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
          <h2 class="text-lg font-bold text-gray-900 mb-4">
            {{ editingAccount ? 'Edit Account' : 'Add Account' }}
          </h2>
          <div class="flex flex-col gap-3">
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Name</label>
              <input v-model="form.name" type="text" placeholder="My Checking…"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Type</label>
              <select v-model="form.accountType"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900 bg-white">
                <option v-for="type in ACCOUNT_TYPES" :key="type" :value="type">
                  {{ typeIcon(type) }} {{ typeLabel(type) }}
                </option>
              </select>
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Balance</label>
              <input v-model.number="form.balance" type="number" step="0.01" placeholder="0.00"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900" />
            </div>
          </div>
          <div class="flex gap-2 mt-5">
            <button @click="showModal = false"
              class="flex-1 border border-gray-200 text-gray-600 text-sm font-medium py-2 rounded-lg hover:bg-gray-50 transition-colors">
              Cancel
            </button>
            <button @click="submitForm"
              class="flex-1 bg-gray-900 text-white text-sm font-medium py-2 rounded-lg hover:bg-gray-700 transition-colors">
              {{ editingAccount ? 'Save' : 'Add' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
