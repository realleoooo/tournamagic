import { ref } from 'vue'

const GOOGLE_SCRIPT = 'https://accounts.google.com/gsi/client'

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
  const loadingProvider = ref<'google' | null>(null)

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

  return {
    loadingProvider,
    signInWithGoogle
  }
}
