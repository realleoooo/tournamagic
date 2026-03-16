import { computed, onBeforeUnmount, ref } from 'vue'

export type TimerMode = 'count_up' | 'count_down'

export interface MatchTimerConfig {
  mode: TimerMode
  startDurationMs: number
}

const TICK_RATE_MS = 250

export const formatMmSs = (valueMs: number) => {
  const totalSeconds = Math.max(0, Math.floor(valueMs / 1000))
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

export const useMatchTimer = (initialConfig: MatchTimerConfig) => {
  const config = ref<MatchTimerConfig>({ ...initialConfig })
  const running = ref(false)
  const nowMs = ref(Date.now())
  const elapsedBeforeRunMs = ref(0)
  const runStartedAtMs = ref<number | null>(null)
  let tickerId: number | undefined

  const stopTicker = () => {
    if (tickerId !== undefined) {
      window.clearInterval(tickerId)
      tickerId = undefined
    }
  }

  const startTicker = () => {
    if (tickerId !== undefined) return
    tickerId = window.setInterval(() => {
      nowMs.value = Date.now()

      if (config.value.mode === 'count_down' && remainingMs.value <= 0) {
        elapsedBeforeRunMs.value = config.value.startDurationMs
        running.value = false
        runStartedAtMs.value = null
        stopTicker()
      }
    }, TICK_RATE_MS)
  }

  const elapsedMs = computed(() => {
    if (!running.value || runStartedAtMs.value === null) return elapsedBeforeRunMs.value
    return elapsedBeforeRunMs.value + (nowMs.value - runStartedAtMs.value)
  })

  const remainingMs = computed(() => Math.max(0, config.value.startDurationMs - elapsedMs.value))

  const displayMs = computed(() =>
    config.value.mode === 'count_down' ? remainingMs.value : Math.max(0, elapsedMs.value)
  )

  const isExpired = computed(() => config.value.mode === 'count_down' && remainingMs.value <= 0)

  const start = () => {
    if (running.value || isExpired.value) return
    nowMs.value = Date.now()
    runStartedAtMs.value = nowMs.value
    running.value = true
    startTicker()
  }

  const pause = () => {
    if (!running.value || runStartedAtMs.value === null) return
    nowMs.value = Date.now()
    elapsedBeforeRunMs.value = elapsedMs.value
    running.value = false
    runStartedAtMs.value = null
    stopTicker()
  }

  const reset = () => {
    running.value = false
    runStartedAtMs.value = null
    elapsedBeforeRunMs.value = 0
    nowMs.value = Date.now()
    stopTicker()
  }

  const configure = (nextConfig: MatchTimerConfig) => {
    config.value = { ...nextConfig }
    reset()
  }

  onBeforeUnmount(() => {
    stopTicker()
  })

  return {
    config,
    running,
    displayMs,
    isExpired,
    start,
    pause,
    reset,
    configure
  }
}
