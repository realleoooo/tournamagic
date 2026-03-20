export type AuthSessionUser = {
  name: string
  email: string
}

export const AUTH_SESSION_STORAGE_KEY = 'tournamagic.auth.session'

const safeParse = <T>(raw: string | null, fallback: T): T => {
  if (!raw) {
    return fallback
  }

  try {
    return JSON.parse(raw) as T
  } catch {
    return fallback
  }
}

export const getStoredAuthSession = (): AuthSessionUser | null =>
  safeParse<AuthSessionUser | null>(window.localStorage.getItem(AUTH_SESSION_STORAGE_KEY), null)

export const getAuthHeaders = (): HeadersInit => {
  const user = getStoredAuthSession()

  if (!user) {
    return {}
  }

  return {
    'X-Auth-User-Name': user.name,
    'X-Auth-User-Email': user.email
  }
}
