import { ref } from 'vue'
import { useRouter } from 'vue-router'

const isLoggedIn = ref(!!localStorage.getItem('token'))

export function useAuth() {
  const router = useRouter()

  const login = (token: string) => {
    localStorage.setItem('token', token)
    isLoggedIn.value = true
  }

  const logout = () => {
    localStorage.removeItem('token')
    isLoggedIn.value = false
    router.push('/login')
  }

  return { isLoggedIn, login, logout }
}
