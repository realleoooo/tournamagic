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
    <div class="timer-strip">
      <div class="timer-strip__meta">
        <p class="timer-label">Match Timer</p>
        <p class="timer-mode">{{ timer.config.value.mode === 'count_up' ? 'Count up' : 'Count down' }}</p>
        <div class="timer-controls">
          <button type="button" class="timer-control" :disabled="timer.running.value || timer.isExpired.value" @click="timer.start">Start</button>
          <button type="button" class="timer-control" :disabled="!timer.running.value" @click="timer.pause">Pause</button>
          <button type="button" class="timer-control" @click="timer.reset">Reset</button>
        </div>
      </div>

      <div class="timer-strip__clock">
        <p class="timer-display" :class="{ expired: timer.isExpired.value }">{{ displayValue }}</p>
        <p v-if="timer.isExpired.value" class="timer-expired">Time is up!</p>
        <p v-else-if="reminderMessage" class="timer-reminder">{{ reminderMessage }}</p>
      </div>

      <div class="timer-strip__actions">
        <button type="button" class="secondary timer-settings" @click="openSettings">Timer Settings</button>
      </div>
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
  border: 1px solid #7e5524;
  background:
    linear-gradient(180deg, rgba(52, 31, 19, 0.95), rgba(26, 15, 10, 0.98)),
    #24170f;
  box-shadow: inset 0 0 0 1px rgba(214, 171, 92, 0.12);
}

.timer-strip {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 1rem;
  padding: 0.85rem 1rem;
}

.timer-strip__meta {
  display: grid;
  gap: 0.2rem;
  min-width: 180px;
}

.timer-label,
.timer-mode,
.timer-expired,
.timer-reminder,
.settings-error {
  margin: 0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.timer-label {
  color: #f3deb4;
  font-size: 1rem;
}

.timer-mode {
  color: #dcbf91;
  font-size: 0.92rem;
}

.timer-controls,
.modal-actions {
  display: flex;
  gap: 0.45rem;
  flex-wrap: wrap;
}

.timer-control {
  background:
    linear-gradient(180deg, rgba(86, 59, 33, 0.96), rgba(42, 27, 17, 0.98)),
    #41291a;
  border-color: rgba(174, 128, 66, 0.82);
  color: #f1dcaf;
  padding: 0.4rem 0.7rem;
  font-size: 0.86rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.timer-control:hover {
  background:
    linear-gradient(180deg, rgba(100, 68, 38, 0.96), rgba(50, 31, 19, 0.98)),
    #4a2f1d;
}

.timer-strip__clock {
  text-align: center;
}

.timer-display {
  margin: 0;
  color: #ffe1a2;
  font-size: 3.1rem;
  line-height: 1;
  font-weight: 700;
  letter-spacing: 0.08rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  text-shadow: 0 0 18px rgba(255, 168, 57, 0.24);
}

.timer-display.expired,
.timer-expired,
.settings-error {
  color: #ef9a73;
}

.timer-reminder {
  color: #f5d189;
}

.timer-settings {
  min-width: 162px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background:
    radial-gradient(circle at center, rgba(120, 74, 33, 0.18), rgba(0, 0, 0, 0.72)),
    rgba(0, 0, 0, 0.68);
  display: grid;
  place-items: center;
  z-index: 25;
  padding: 1rem;
}

.modal-card {
  position: relative;
  width: min(460px, 100%);
  border: 1px solid #8d6030;
  background:
    linear-gradient(180deg, rgba(92, 55, 33, 0.24), rgba(33, 20, 14, 0.18)),
    rgba(66, 40, 26, 0.96);
  box-shadow:
    inset 0 0 0 1px rgba(237, 190, 105, 0.16),
    0 20px 34px rgba(0, 0, 0, 0.34);
  padding: 2rem 1.5rem 1.4rem;
}

.modal-card::before,
.modal-card::after {
  content: '';
  position: absolute;
  left: 50%;
  width: 18px;
  height: 18px;
  margin-left: -9px;
  transform: rotate(45deg);
  border: 1px solid rgba(223, 177, 95, 0.78);
  background: linear-gradient(135deg, rgba(255, 229, 163, 0.9), rgba(153, 91, 30, 0.45));
  box-shadow: 0 0 12px rgba(255, 187, 72, 0.26);
}

.modal-card::before {
  top: -9px;
}

.modal-card::after {
  bottom: -9px;
}

.modal-card h3 {
  margin: 0 0 1.1rem;
  color: #f5dfb3;
  text-align: center;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.7rem;
}

.settings-group {
  display: grid;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.settings-group label {
  display: grid;
  gap: 0.45rem;
  color: #efdcb0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.settings-group input,
.settings-group select {
  background:
    linear-gradient(180deg, rgba(23, 16, 12, 0.96), rgba(19, 14, 11, 0.98)),
    #18120e;
  border-color: rgba(174, 128, 69, 0.84);
  color: #f2ddb6;
  border-radius: 12px;
  padding: 0.82rem 0.95rem;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.36);
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 0.6rem;
  color: #f2ddb6 !important;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  accent-color: #7aa54f;
  padding: 0;
  box-shadow: none;
}

.modal-actions {
  justify-content: center;
  gap: 0.75rem;
  margin-top: 0.35rem;
}

.modal-actions button {
  min-width: 144px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.modal-actions .secondary {
  background:
    linear-gradient(180deg, rgba(92, 61, 35, 0.94), rgba(44, 28, 19, 0.98)),
    #432c1d;
  border-color: rgba(171, 127, 67, 0.72);
  color: #efdcb0;
}

.modal-actions .secondary:hover {
  background:
    linear-gradient(180deg, rgba(105, 71, 40, 0.96), rgba(51, 33, 21, 0.98)),
    #4c3120;
}

.modal-actions button:not(.secondary) {
  background:
    linear-gradient(180deg, rgba(130, 170, 78, 0.96), rgba(59, 98, 36, 0.98)),
    #618f43;
  border-color: rgba(211, 173, 103, 0.8);
  color: #faf2d6;
}

.modal-actions button:not(.secondary):hover {
  background:
    linear-gradient(180deg, rgba(143, 187, 87, 0.96), rgba(69, 110, 44, 0.98)),
    #6e9d4d;
}

@media (max-width: 980px) {
  .timer-strip {
    grid-template-columns: 1fr;
    justify-items: center;
    text-align: center;
  }

  .timer-strip__meta,
  .timer-strip__actions {
    justify-items: center;
  }

  .timer-controls {
    justify-content: center;
  }
}
</style>
