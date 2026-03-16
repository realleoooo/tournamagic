const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api'

export type AuthSession = {
  accessToken: string
  refreshToken: string
  userId: string
  email: string
  name: string
}

type AuthPayload = {
  email: string
  password: string
  name?: string
}

async function request(path: string, payload: AuthPayload): Promise<AuthSession> {
  const response = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(payload)
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `Request failed: ${response.status}`)
  }

  return (await response.json()) as AuthSession
}

export const authApi = {
  login(payload: AuthPayload) {
    return request('/auth/login', payload)
  },
  register(payload: AuthPayload) {
    return request('/auth/register', payload)
  }
}
