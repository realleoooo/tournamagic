import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'

export type AuthUser = {
  id: string
  name: string
  email: string
}

type AuthSession = {
  accessToken: string
  refreshToken: string
  user: AuthUser
}

const AUTH_SESSION_STORAGE_KEY = 'tournamagic.auth.session'

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

const loadSession = (): AuthSession | null => safeParse<AuthSession | null>(window.localStorage.getItem(AUTH_SESSION_STORAGE_KEY), null)

const saveSession = (session: AuthSession | null) => {
  if (!session) {
    window.localStorage.removeItem(AUTH_SESSION_STORAGE_KEY)
    return
  }

  window.localStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(session))
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as AuthUser | null,
    accessToken: '',
    refreshToken: '',
    initialized: false
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.user && state.accessToken)
  },
  actions: {
    initialize() {
      if (this.initialized) {
        return
      }

      const session = loadSession()
      this.user = session?.user ?? null
      this.accessToken = session?.accessToken ?? ''
      this.refreshToken = session?.refreshToken ?? ''
      this.initialized = true
    },
    async register(values: { name: string; email: string; password: string }) {
      try {
        const session = await authApi.register(values)
        this.setSession(session)
        return { ok: true as const }
      } catch (error) {
        return {
          ok: false as const,
          error: error instanceof Error ? error.message : 'Unable to register.'
        }
      }
    },
    async login(values: { email: string; password: string }) {
      try {
        const session = await authApi.login(values)
        this.setSession(session)
        return { ok: true as const }
      } catch (error) {
        return {
          ok: false as const,
          error: error instanceof Error ? error.message : 'Unable to login.'
        }
      }
    },
    setSession(session: { accessToken: string; refreshToken: string; userId: string; email: string; name: string }) {
      this.accessToken = session.accessToken
      this.refreshToken = session.refreshToken
      this.user = {
        id: session.userId,
        email: session.email,
        name: session.name
      }
      saveSession({
        accessToken: this.accessToken,
        refreshToken: this.refreshToken,
        user: this.user
      })
    },
    logout() {
      this.user = null
      this.accessToken = ''
      this.refreshToken = ''
      saveSession(null)
    }
  }
})
