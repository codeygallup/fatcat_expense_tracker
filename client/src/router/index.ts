import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'Home', component: () => import('@/views/Home.vue') },
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/Register.vue') },
    { path: '/dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue') },
  ],
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const publicPages = ['Home', 'Login', 'Register']

  if (!token && !publicPages.includes(to.name as string)) {
    return '/'
  }
})

export default router
