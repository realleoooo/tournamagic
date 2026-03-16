import { defineStore } from 'pinia'
import { authApi } from '@/api/authApi'
import { saveAuthToken, getAuthToken } from '@/utils/authSession'

export type AuthUser = {
  name: string
  email: string
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
    async initialize() {
      if (this.initialized) {
        return
      }

      const token = getAuthToken()
      if (!token) {
        this.initialized = true
        return
      }

      try {
        this.user = await authApi.getSession()
      } catch {
        saveAuthToken(undefined)
        this.user = null
      } finally {
        this.initialized = true
      }
    },
    async register(values: { name: string; email: string; password: string }) {
      try {
        const result = await authApi.register(values)
        this.user = result.user
        saveAuthToken(result.token)
        return { ok: true as const }
      } catch (error) {
        return {
          ok: false as const,
          error: error instanceof Error ? error.message : 'Could not register.'
        }
      }
    },
    async login(values: { email: string; password: string }) {
      try {
        const result = await authApi.login(values)
        this.user = result.user
        saveAuthToken(result.token)
        return { ok: true as const }
      } catch (error) {
        return {
          ok: false as const,
          error: error instanceof Error ? error.message : 'Could not log in.'
        }
      }
    },
    async socialLogin(values: { provider: 'google' | 'apple'; idToken: string }) {
      try {
        const result = await authApi.socialLogin(values)
        this.user = result.user
        saveAuthToken(result.token)
        return { ok: true as const }
      } catch (error) {
        return {
          ok: false as const,
          error: error instanceof Error ? error.message : 'Social login failed.'
        }
      }
    },
    async logout() {
      try {
        await authApi.logout()
      } catch {
        // no-op: clear local state regardless
      }
      this.user = null
      saveAuthToken(undefined)
    }
  }
})
