<script setup lang="ts">
import { computed, ref } from 'vue'
import { formatMmSs, useMatchTimer, type TimerMode } from '@/composables/useMatchTimer'

const defaultMinutes = 50

const configured = ref(false)
const modeInput = ref<TimerMode>('count_up')
const countdownMinutes = ref(defaultMinutes)

const timer = useMatchTimer({
  mode: 'count_up',
  startDurationMs: defaultMinutes * 60 * 1000
})

const displayValue = computed(() => formatMmSs(timer.displayMs.value))

const applyConfig = () => {
  const safeMinutes = Math.max(1, Number.isFinite(countdownMinutes.value) ? countdownMinutes.value : defaultMinutes)
  countdownMinutes.value = safeMinutes

  timer.configure({
    mode: modeInput.value,
    startDurationMs: safeMinutes * 60 * 1000
  })

  configured.value = true
}
</script>

<template>
  <div class="match-timer">
    <div v-if="!configured" class="timer-config">
      <label>
        Mode
        <select v-model="modeInput">
          <option value="count_up">Count up</option>
          <option value="count_down">Count down</option>
        </select>
      </label>

      <label v-if="modeInput === 'count_down'">
        Start (minutes)
        <input v-model.number="countdownMinutes" type="number" min="1" step="1" />
      </label>

      <button type="button" @click="applyConfig">Create Timer</button>
    </div>

    <div v-else>
      <p class="timer-label">Match Timer ({{ timer.config.value.mode === 'count_up' ? 'Count up' : 'Count down' }})</p>
      <p class="timer-display" :class="{ expired: timer.isExpired.value }">{{ displayValue }}</p>
      <p v-if="timer.isExpired.value" class="timer-expired">Time is up!</p>

      <div class="timer-controls">
        <button type="button" :disabled="timer.running.value || timer.isExpired.value" @click="timer.start">Start</button>
        <button type="button" class="secondary" :disabled="!timer.running.value" @click="timer.pause">Pause</button>
        <button type="button" class="secondary" @click="timer.reset">Reset</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.match-timer {
  margin-top: 0.85rem;
  padding-top: 0.8rem;
  border-top: 1px solid color-mix(in srgb, var(--accent-arcane) 35%, transparent);
}

.timer-config,
.timer-controls {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: end;
}

.timer-config label {
  display: grid;
  gap: 0.3rem;
  font-size: 0.85rem;
  color: var(--text-soft);
}

.timer-label {
  margin: 0;
  color: var(--text-soft);
  font-size: 0.85rem;
}

.timer-display {
  margin: 0.2rem 0 0.5rem;
  font-size: 2rem;
  line-height: 1;
  font-weight: 700;
  letter-spacing: 0.08rem;
}

.timer-display.expired {
  color: var(--danger);
}

.timer-expired {
  margin: 0 0 0.6rem;
  color: var(--danger);
  font-weight: 700;
}
</style>
