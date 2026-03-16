export const AUTH_TOKEN_STORAGE_KEY = 'tournamagic.auth.token'

export const getAuthToken = () => window.localStorage.getItem(AUTH_TOKEN_STORAGE_KEY)

export const saveAuthToken = (token?: string) => {
  if (!token) {
    window.localStorage.removeItem(AUTH_TOKEN_STORAGE_KEY)
    return
  }

  window.localStorage.setItem(AUTH_TOKEN_STORAGE_KEY, token)
}
