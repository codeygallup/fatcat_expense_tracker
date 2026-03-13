<script setup lang="ts"></script>

<template>
  <div class="flex flex-col items-center justify-around p-8 flex-1">
    <h1 class="text-3xl font-bold text-gray-800 text-center">Account Page</h1>
    <p class="text-lg text-gray-600 text-center">
      This is where you can view and manage your account details, including your expenses, budgets,
      and settings.
    </p>
    <div class="w-full max-w-md mt-8">
      <h2 class="text-2xl font-semibold text-gray-800 mb-4">Date Range</h2>
      <div class="flex space-x-4">
        <input
          type="date"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <input
          type="date"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>CHART GOES HERE</div>
    </div>
    <div class="w-full max-w-md mt-8">
      <p class="text-lg font-semibold text-gray-800 mb-4">Spending by Category</p>
      <ul>
        <li>Category 1: $100</li>
        <li>Category 2: $50</li>
        <li>Category 3: $25</li>
      </ul>
    </div>
  </div>
</template>
