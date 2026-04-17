import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/login' }, 
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
    { path: '/dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
    { path: '/accounts', name: 'Accounts', component: () => import('@/views/Account.vue') },
    { path: '/accounts/:id', name: 'AccountDetail', component: () => import('@/views/AccountDetail.vue') },
    { path: '/bills', name: 'Bills', component: () => import('@/views/Bill.vue') },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const publicPages = ['Login', 'Register']

  if (!token && !publicPages.includes(to.name as string)) {
    return '/'
  }
})

export default router
