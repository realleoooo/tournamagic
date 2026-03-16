<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { formatMmSs, useMatchTimer, type MatchTimerConfig, type TimerMode } from '@/composables/useMatchTimer'

const defaultSettings: MatchTimerConfig = {
  mode: 'count_up',
  startDurationMs: 50 * 60 * 1000,
  remindersEnabled: false,
  reminderIntervalMs: 5 * 60 * 1000
}

const timer = useMatchTimer(defaultSettings)

const displayValue = computed(() => formatMmSs(timer.displayMs.value))

const isSettingsOpen = ref(false)
const settingsError = ref<string | null>(null)

const modeInput = ref<TimerMode>(defaultSettings.mode)
const countdownMinutesInput = ref(defaultSettings.startDurationMs / 60000)
const remindersEnabledInput = ref(defaultSettings.remindersEnabled)
const reminderIntervalMinutesInput = ref(defaultSettings.reminderIntervalMs / 60000)

const reminderMessage = ref<string | null>(null)
let reminderMessageTimeout: number | undefined

const openSettings = () => {
  const current = timer.config.value
  modeInput.value = current.mode
  countdownMinutesInput.value = Math.max(1, Math.floor(current.startDurationMs / 60000))
  remindersEnabledInput.value = current.remindersEnabled
  reminderIntervalMinutesInput.value = Math.max(1, Math.floor(current.reminderIntervalMs / 60000))
  settingsError.value = null
  isSettingsOpen.value = true
}

const closeSettings = () => {
  isSettingsOpen.value = false
  settingsError.value = null
}

const saveSettings = () => {
  const safeCountdownMinutes = Math.max(
    1,
    Number.isFinite(countdownMinutesInput.value) ? Math.floor(countdownMinutesInput.value) : 1
  )
  const safeReminderMinutes = Math.max(
    1,
    Number.isFinite(reminderIntervalMinutesInput.value) ? Math.floor(reminderIntervalMinutesInput.value) : 1
  )

  if (remindersEnabledInput.value && safeReminderMinutes <= 0) {
    settingsError.value = 'Reminder interval must be a positive number.'
    return
  }

  countdownMinutesInput.value = safeCountdownMinutes
  reminderIntervalMinutesInput.value = safeReminderMinutes

  timer.configure({
    mode: modeInput.value,
    startDurationMs: safeCountdownMinutes * 60 * 1000,
    remindersEnabled: remindersEnabledInput.value,
    reminderIntervalMs: safeReminderMinutes * 60 * 1000
  })

  closeSettings()
}

const playReminderBeep = () => {
  const AudioContextClass = window.AudioContext || (window as Window & { webkitAudioContext?: typeof AudioContext }).webkitAudioContext
  if (!AudioContextClass) return

  const context = new AudioContextClass()
  const oscillator = context.createOscillator()
  const gain = context.createGain()

  oscillator.type = 'sine'
  oscillator.frequency.value = 880
  gain.gain.value = 0.04

  oscillator.connect(gain)
  gain.connect(context.destination)

  oscillator.start()
  oscillator.stop(context.currentTime + 0.2)

  window.setTimeout(() => {
    void context.close()
  }, 300)
}

watch(
  () => timer.latestReminderMinutes.value,
  (minutes) => {
    if (minutes === null) return

    reminderMessage.value = `${minutes} minute${minutes === 1 ? '' : 's'} have passed.`
    playReminderBeep()

    if (reminderMessageTimeout !== undefined) {
      window.clearTimeout(reminderMessageTimeout)
    }

    reminderMessageTimeout = window.setTimeout(() => {
      reminderMessage.value = null
      timer.clearReminderNotice()
    }, 4000)
  }
)
</script>

<template>
  <div class="match-timer">
    <div class="timer-header">
      <p class="timer-label">Match Timer ({{ timer.config.value.mode === 'count_up' ? 'Count up' : 'Count down' }})</p>
      <button type="button" class="secondary" @click="openSettings">Timer Settings</button>
    </div>

    <p class="timer-display" :class="{ expired: timer.isExpired.value }">{{ displayValue }}</p>

    <p v-if="timer.isExpired.value" class="timer-expired">Time is up!</p>
    <p v-if="reminderMessage" class="timer-reminder">{{ reminderMessage }}</p>

    <div class="timer-controls">
      <button type="button" :disabled="timer.running.value || timer.isExpired.value" @click="timer.start">Start</button>
      <button type="button" class="secondary" :disabled="!timer.running.value" @click="timer.pause">Pause</button>
      <button type="button" class="secondary" @click="timer.reset">Reset</button>
    </div>

    <div v-if="isSettingsOpen" class="modal-backdrop" @click.self="closeSettings">
      <section class="modal-card card">
        <h3>Timer Settings</h3>

        <div class="settings-group">
          <label>
            Timer mode
            <select v-model="modeInput">
              <option value="count_up">Count up</option>
              <option value="count_down">Count down</option>
            </select>
          </label>

          <label v-if="modeInput === 'count_down'">
            Countdown start (minutes)
            <input v-model.number="countdownMinutesInput" type="number" min="1" step="1" />
          </label>
        </div>

        <div class="settings-group">
          <label class="checkbox-label">
            <input v-model="remindersEnabledInput" type="checkbox" />
            Enable reminders every X minutes
          </label>

          <label v-if="remindersEnabledInput">
            Reminder interval (minutes)
            <input v-model.number="reminderIntervalMinutesInput" type="number" min="1" step="1" />
          </label>
        </div>

        <p v-if="settingsError" class="settings-error">{{ settingsError }}</p>

        <div class="modal-actions">
          <button type="button" class="secondary" @click="closeSettings">Cancel</button>
          <button type="button" @click="saveSettings">Save Settings</button>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.match-timer {
  margin-top: 0.85rem;
  padding-top: 0.8rem;
  border-top: 1px solid color-mix(in srgb, var(--accent-arcane) 35%, transparent);
}

.timer-header,
.timer-controls,
.modal-actions {
  display: flex;
  gap: 0.5rem;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
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

.timer-display.expired,
.timer-expired,
.settings-error {
  color: var(--danger);
}

.timer-expired,
.timer-reminder,
.settings-error {
  margin: 0 0 0.6rem;
  font-weight: 700;
}

.timer-reminder {
  color: var(--accent-gold);
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: grid;
  place-items: center;
  z-index: 25;
  padding: 1rem;
}

.modal-card {
  width: min(460px, 100%);
}

.settings-group {
  display: grid;
  gap: 0.6rem;
  margin-bottom: 0.8rem;
}

.settings-group label {
  display: grid;
  gap: 0.3rem;
  color: var(--text-soft);
  font-size: 0.9rem;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 0.45rem;
  color: var(--text-main) !important;
}
</style>
