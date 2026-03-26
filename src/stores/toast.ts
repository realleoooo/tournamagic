import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ToastTone = 'success' | 'error'

export type ToastItem = {
  id: number
  message: string
  tone: ToastTone
}

const TOAST_DURATION_MS = 3600

export const useToastStore = defineStore('toast', () => {
  const toasts = ref<ToastItem[]>([])
  let nextToastId = 1

  const removeToast = (id: number) => {
    toasts.value = toasts.value.filter((toast) => toast.id !== id)
  }

  const show = (message: string, tone: ToastTone) => {
    const id = nextToastId++
    toasts.value = [...toasts.value, { id, message, tone }]
    window.setTimeout(() => {
      removeToast(id)
    }, TOAST_DURATION_MS)
  }

  const success = (message: string) => {
    show(message, 'success')
  }

  const error = (message: string) => {
    show(message, 'error')
  }

  return {
    toasts,
    removeToast,
    show,
    success,
    error
  }
})
