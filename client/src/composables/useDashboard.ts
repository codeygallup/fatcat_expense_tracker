import api from "@/api";
import type { Transaction } from "@/types";
import { computed, onMounted, ref } from "vue";

export function useDashboard() {
    const accounts = ref<{ id: number; balance: number }[]>([]);
    const billsDue = ref<{ id: number; name: string; dueDate: string; status: string }[]>([]);
    const recentTransactions = ref<Transaction[]>([]);
    const loading = ref(false);

    const formattedBalance = computed(() => {
        const total = accounts.value.reduce((sum, acc) => sum + acc.balance, 0);
        return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(total);
    });

    const overdueBills = computed(() => billsDue.value.filter(bill => bill.status === 'OVERDUE'));
    const hasOverdue = computed(() => overdueBills.value.length > 0);

    onMounted(async () => {
        loading.value = true;
        try {
            const [accountsData, billsData, transactionsData] = await Promise.all([
                api('/accounts'),
                api('/bills/upcoming'),
                api('/transactions/recent')
            ])
            accounts.value = await accountsData.json();
            billsDue.value = await billsData.json();
            recentTransactions.value = await transactionsData.json();
        } catch (e) {
            console.error('Failed to fetch dashboard data', e);
            } finally {
            loading.value = false;
            }
    })

    return {
        accounts,
        billsDue,
        recentTransactions,
        formattedBalance,
        overdueBills,
        hasOverdue,
        loading
    }
}