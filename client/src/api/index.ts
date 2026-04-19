const BASE_URL = import.meta.env.VITE_API_URL

export const api = async (path: string, options?: RequestInit & { raw?: boolean }) => {
  const token = localStorage.getItem('token')
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  })

  if (options?.raw) return res

  if (!res.ok) {
    if (res.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    const body = await res.json().catch(() => null)
    throw new Error(body?.message ?? `API error: ${res.status} ${res.statusText}`)
  }
  return res
}

export default api
