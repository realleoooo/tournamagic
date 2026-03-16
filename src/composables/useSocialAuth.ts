import { ref } from 'vue'

const GOOGLE_SCRIPT = 'https://accounts.google.com/gsi/client'
const APPLE_SCRIPT = 'https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js'

const loadScript = (src: string) =>
  new Promise<void>((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve()
      return
    }

    const script = document.createElement('script')
    script.src = src
    script.async = true
    script.defer = true
    script.onload = () => resolve()
    script.onerror = () => reject(new Error(`Failed to load SDK: ${src}`))
    document.head.appendChild(script)
  })

export const useSocialAuth = () => {
  const loadingProvider = ref<'google' | 'apple' | null>(null)

  const signInWithGoogle = async (): Promise<string> => {
    loadingProvider.value = 'google'
    try {
      await loadScript(GOOGLE_SCRIPT)
      const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
      if (!clientId) {
        throw new Error('Google login is not configured.')
      }

      return await new Promise<string>((resolve, reject) => {
        const googleWindow = (window as Window & {
          google?: {
            accounts: {
              id: {
                initialize: (config: {
                  client_id: string
                  callback: (response: { credential?: string }) => void
                }) => void
                prompt: () => void
              }
            }
          }
        }).google

        if (!googleWindow) {
          reject(new Error('Google SDK unavailable.'))
          return
        }

        googleWindow.accounts.id.initialize({
          client_id: clientId,
          callback: (response) => {
            if (!response.credential) {
              reject(new Error('Google login canceled or failed.'))
              return
            }
            resolve(response.credential)
          }
        })

        googleWindow.accounts.id.prompt()
      })
    } finally {
      loadingProvider.value = null
    }
  }

  const signInWithApple = async (): Promise<string> => {
    loadingProvider.value = 'apple'
    try {
      await loadScript(APPLE_SCRIPT)
      const clientId = import.meta.env.VITE_APPLE_CLIENT_ID
      const redirectUri = import.meta.env.VITE_APPLE_REDIRECT_URI

      if (!clientId || !redirectUri) {
        throw new Error('Apple login is not configured.')
      }

      const appleWindow = (window as Window & {
        AppleID?: {
          auth: {
            init: (config: Record<string, unknown>) => void
            signIn: () => Promise<{ authorization?: { id_token?: string } }>
          }
        }
      }).AppleID

      if (!appleWindow) {
        throw new Error('Apple SDK unavailable.')
      }

      appleWindow.auth.init({
        clientId,
        scope: 'name email',
        redirectURI: redirectUri,
        usePopup: true,
        responseType: 'code id_token'
      })

      const response = await appleWindow.auth.signIn()
      const idToken = response.authorization?.id_token
      if (!idToken) {
        throw new Error('Apple login canceled or failed.')
      }

      return idToken
    } finally {
      loadingProvider.value = null
    }
  }

  return {
    loadingProvider,
    signInWithGoogle,
    signInWithApple
  }
}
