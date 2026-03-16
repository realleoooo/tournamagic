import { getAuthToken } from '@/utils/authSession'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api'

export type AuthResponse = {
  token: string
  user: {
    name: string
    email: string
  }
  created: boolean
}

const request = async <T>(path: string, init?: RequestInit): Promise<T> => {
  const token = getAuthToken()
  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(init?.headers ?? {})
    }
  })

  if (!response.ok) {
    const payload = await response.json().catch(() => ({ message: '' }))
    throw new Error(payload.message || `Request failed with status ${response.status}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export const authApi = {
  register(payload: { name: string; email: string; password: string }) {
    return request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
  },
  login(payload: { email: string; password: string }) {
    return request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
  },
  socialLogin(payload: { provider: 'google'; idToken: string }) {
    return request<AuthResponse>('/auth/social', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
  },
  getSession() {
    return request<{ name: string; email: string }>('/auth/session')
  },
  logout() {
    return request<void>('/auth/logout', {
      method: 'POST'
    })
  }
}
