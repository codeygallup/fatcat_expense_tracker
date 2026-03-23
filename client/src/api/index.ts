const BASE_URL = 'http://localhost:8080/api'

export const api = (path: string, options?: RequestInit) => {
  const token = localStorage.getItem('token')
  return fetch(`${BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  }).then((res) => {
    if (!res.ok) throw new Error(`API error: ${res.status} ${res.statusText}`)
    return res
  })
}

export default api
