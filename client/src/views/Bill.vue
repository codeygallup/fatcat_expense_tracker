<script setup lang="ts">
import Skeleton from '@/components/Skeleton.vue'
import { useBill } from '@/composables/useBill'

const { activeFilter, showModal, editingBill, form, loading, TABS, tabLabel, filteredBills, statusBadge, rowBg, updateStatus, deleteBill, openAdd, openEdit, submitForm } = useBill()
</script>

<template>
  <div class="p-4 mx-auto md:mx-20 pb-24 sm:pb-4">
    <div class="flex items-center justify-between mb-4">
      <h1 class="text-xl font-bold text-gray-900">Bills</h1>
      <button
        @click="openAdd"
        class="hidden sm:flex items-center gap-1 text-sm font-medium bg-gray-900 text-white px-3 py-1.5 rounded-lg hover:bg-gray-700 transition-colors"
      >
        + Add Bill
      </button>
    </div>

    <div class="flex gap-1 mb-4 bg-gray-100 rounded-xl p-1">
      <button
        v-for="tab in TABS"
        :key="tab"
        @click="activeFilter = tab"
        class="flex-1 text-xs font-medium py-1.5 rounded-lg transition-colors"
        :class="
          activeFilter === tab
            ? 'bg-white shadow text-gray-900'
            : 'text-gray-500 hover:text-gray-700'
        "
      >
        {{ tabLabel(tab) }}
      </button>
    </div>
    <p class="text-xs text-gray-400 text-center mb-4">
      Tap a bill's status badge to toggle its status
    </p>

    <div class="flex flex-col gap-2">
      <template v-if="loading" class="flex flex-col gap-2">
        <Skeleton v-for="n in 5" :key="n" type="card" />
      </template>
      <template v-else class="flex flex-col gap-2">
        <div
          v-for="bill in filteredBills"
          :key="bill.id"
          class="border rounded-xl p-4 flex items-center gap-3 transition-colors"
          :class="rowBg(bill.status)"
        >
        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2 flex-wrap">
            <span class="font-medium text-gray-900 truncate">{{ bill.name }}</span>
            <span
              @click="updateStatus(bill)"
              class="text-xs px-2 py-0.5 rounded-full font-medium shrink-0 cursor-pointer select-none"
              :class="statusBadge(bill.status)"
              :title="bill.status === 'PAID' ? 'Click to mark unpaid' : 'Click to mark paid'"
            >
              {{ tabLabel(bill.status) }}
            </span>
          </div>
          <span class="text-xs text-gray-400 mt-0.5 block">
            Due
            {{
              new Date(bill.dueDate + 'T00:00:00').toLocaleDateString('en-US', {
                month: 'short',
                day: 'numeric',
                year: 'numeric',
              })
            }}
          </span>
        </div>

        <span class="text-base font-semibold text-gray-900 shrink-0">
          ${{ bill.amount.toFixed(2) }}
        </span>

        <div class="flex items-center gap-1 shrink-0">
          <button
            @click.stop="openEdit(bill)"
            title="Edit"
            class="p-2 rounded-lg text-gray-400 hover:text-gray-600 hover:bg-black/5 transition-colors"
          >
            <!-- pencil -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
            </svg>
          </button>
          <button
            @click.stop="deleteBill(bill.id)"
            title="Delete"
            class="p-2 rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 transition-colors"
          >
            <!-- trash -->
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="w-4 h-4"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <polyline points="3 6 5 6 21 6" />
              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
              <path d="M10 11v6M14 11v6" />
              <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
            </svg>
          </button>
        </div>
      </div>

      <div v-if="!filteredBills.length" class="text-sm text-gray-400 text-center py-12">
        No {{ activeFilter === 'ALL' ? '' : tabLabel(activeFilter).toLowerCase() + ' ' }}bills
      </div>
      </template>
    </div>

    <button
      @click="openAdd"
      class="fixed bottom-6 right-6 sm:hidden w-14 h-14 bg-gray-900 text-white rounded-full text-2xl flex items-center justify-center shadow-lg hover:bg-gray-700 transition-colors"
    >
      +
    </button>

    <Teleport to="body">
      <div
        v-if="showModal"
        class="fixed inset-0 bg-black/40 flex items-end sm:items-center justify-center z-50 p-4"
        @click.self="showModal = false"
      >
        <div class="bg-white rounded-2xl p-6 w-full max-w-sm shadow-xl">
          <h2 class="text-lg font-bold text-gray-900 mb-4">
            {{ editingBill ? 'Edit Bill' : 'Add Bill' }}
          </h2>
          <div class="flex flex-col gap-3">
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Name</label>
              <input
                v-model="form.name"
                type="text"
                placeholder="Netflix, Rent…"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900"
              />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block">Amount</label>
              <input
                v-model="form.amount"
                type="number"
                step="0.01"
                min="0"
                placeholder="0.00"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900"
              />
            </div>
            <div>
              <label class="text-xs text-gray-500 uppercase tracking-wide mb-1 block"
                >Due Date</label
              >
              <input
                v-model="form.dueDate"
                type="date"
                class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-gray-900"
              />
            </div>
          </div>
          <div class="flex gap-2 mt-5">
            <button
              @click="showModal = false"
              class="flex-1 border border-gray-200 text-gray-600 text-sm font-medium py-2 rounded-lg hover:bg-gray-50 transition-colors"
            >
              Cancel
            </button>
            <button
              @click="submitForm"
              class="flex-1 bg-gray-900 text-white text-sm font-medium py-2 rounded-lg hover:bg-gray-700 transition-colors"
            >
              {{ editingBill ? 'Save' : 'Add' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
