import { defineStore } from 'pinia'

export type AuthUser = {
  name: string
  email: string
}

type StoredUser = AuthUser & {
  password: string
}

const AUTH_USERS_STORAGE_KEY = 'tournamagic.auth.users'
const AUTH_SESSION_STORAGE_KEY = 'tournamagic.auth.session'

const normalizeEmail = (email: string) => email.trim().toLowerCase()

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

const loadUsers = (): StoredUser[] => safeParse<StoredUser[]>(window.localStorage.getItem(AUTH_USERS_STORAGE_KEY), [])

const saveUsers = (users: StoredUser[]) => {
  window.localStorage.setItem(AUTH_USERS_STORAGE_KEY, JSON.stringify(users))
}

const loadSession = (): AuthUser | null => safeParse<AuthUser | null>(window.localStorage.getItem(AUTH_SESSION_STORAGE_KEY), null)

const saveSession = (user: AuthUser | null) => {
  if (!user) {
    window.localStorage.removeItem(AUTH_SESSION_STORAGE_KEY)
    return
  }

  window.localStorage.setItem(AUTH_SESSION_STORAGE_KEY, JSON.stringify(user))
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as AuthUser | null,
    initialized: false
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.user)
  },
  actions: {
    initialize() {
      if (this.initialized) {
        return
      }

      this.user = loadSession()
      this.initialized = true
    },
    register(values: { name: string; email: string; password: string }) {
      const users = loadUsers()
      const normalizedEmail = normalizeEmail(values.email)
      const existing = users.find((candidate) => normalizeEmail(candidate.email) === normalizedEmail)

      if (existing) {
        return {
          ok: false as const,
          error: 'An account with this email already exists.'
        }
      }

      const createdUser: StoredUser = {
        name: values.name.trim(),
        email: normalizedEmail,
        password: values.password
      }

      users.push(createdUser)
      saveUsers(users)

      const sessionUser: AuthUser = {
        name: createdUser.name,
        email: createdUser.email
      }

      this.user = sessionUser
      saveSession(sessionUser)

      return { ok: true as const }
    },
    login(values: { email: string; password: string }) {
      const users = loadUsers()
      const normalizedEmail = normalizeEmail(values.email)
      const user = users.find((candidate) => normalizeEmail(candidate.email) === normalizedEmail)

      if (!user || user.password !== values.password) {
        return {
          ok: false as const,
          error: 'Incorrect email or password.'
        }
      }

      const sessionUser: AuthUser = { name: user.name, email: user.email }
      this.user = sessionUser
      saveSession(sessionUser)
      return { ok: true as const }
    },
    logout() {
      this.user = null
      saveSession(null)
    }
  }
})
