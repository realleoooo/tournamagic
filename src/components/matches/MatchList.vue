<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import type { Match, Player, TimerDirection } from '@/domain/models'

const props = defineProps<{
  matches: Match[]
  players: Player[]
  resolveName: (id: string) => string
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
  timerUpdate: [
    matchId: string,
    payload: {
      running?: boolean
      reset?: boolean
      direction?: TimerDirection
      durationSeconds?: number
      notifyIntervalSeconds?: number
    }
  ]
}>()

const selectedRound = ref(1)
const resultInputs = ref<Record<string, { winsA: number; winsB: number }>>({})
const timerInputs = ref<Record<string, { direction: TimerDirection; durationMinutes: number; notifyMinutes: number }>>({})
const displayTimes = ref<Record<string, number>>({})
const notifiedCheckpoint = ref<Record<string, number>>({})

let ticker: ReturnType<typeof setInterval> | undefined

const inputFor = (matchId: string) => {
  if (!resultInputs.value[matchId]) {
    resultInputs.value[matchId] = { winsA: 2, winsB: 0 }
  }
  return resultInputs.value[matchId]
}

const timerInputFor = (match: Match) => {
  if (!timerInputs.value[match.id]) {
    timerInputs.value[match.id] = {
      direction: match.timerDirection,
      durationMinutes: Math.floor(match.timerDurationSeconds / 60),
      notifyMinutes: Math.max(1, Math.floor(match.timerNotifyIntervalSeconds / 60))
    }
  }
  return timerInputs.value[match.id]
}

const toMatchKey = (a: string, b: string) => [a, b].sort().join('::')

const rounds = computed(() => {
  const playerIds = props.players.map((player) => player.id)
  if (playerIds.length < 2) return []

  const BYE = '__bye__'
  const rotation = [...playerIds]
  if (rotation.length % 2 === 1) {
    rotation.push(BYE)
  }

  const roundsCount = rotation.length - 1
  const matchesByKey = new Map(props.matches.map((match) => [toMatchKey(match.playerAId, match.playerBId), match]))

  const output: Array<{ number: number; matches: Match[] }> = []

  for (let round = 0; round < roundsCount; round += 1) {
    const roundMatches: Match[] = []

    for (let i = 0; i < rotation.length / 2; i += 1) {
      const a = rotation[i]
      const b = rotation[rotation.length - 1 - i]
      if (a === BYE || b === BYE) continue

      const match = matchesByKey.get(toMatchKey(a, b))
      if (match) {
        roundMatches.push(match)
      }
    }

    output.push({ number: round + 1, matches: roundMatches })

    const fixed = rotation[0]
    const rotating = rotation.slice(1)
    rotating.unshift(rotating.pop()!)
    rotation.splice(0, rotation.length, fixed, ...rotating)
  }

  return output
})

const selectedRoundMatches = computed(
  () => rounds.value.find((round) => round.number === selectedRound.value)?.matches ?? []
)

const formatSeconds = (seconds: number) => {
  const safe = Math.max(0, Math.floor(seconds))
  const hours = Math.floor(safe / 3600)
  const minutes = Math.floor((safe % 3600) / 60)
  const remainingSeconds = safe % 60
  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, '0')}:${String(remainingSeconds).padStart(2, '0')}`
  }
  return `${minutes}:${String(remainingSeconds).padStart(2, '0')}`
}

const currentDisplaySeconds = (match: Match) => {
  const elapsed = displayTimes.value[match.id] ?? match.timerElapsedSeconds
  if (match.timerDirection === 'down') {
    return Math.max(0, match.timerDurationSeconds - elapsed)
  }
  return elapsed
}

const announce = (text: string) => {
  if ('Notification' in window && Notification.permission === 'granted') {
    new Notification(text)
    return
  }
  window.alert(text)
}

const requestNotificationPermission = async () => {
  if (!('Notification' in window) || Notification.permission !== 'default') return
  await Notification.requestPermission()
}

const applyTimerSettings = (match: Match) => {
  const config = timerInputFor(match)
  emit('timerUpdate', match.id, {
    direction: config.direction,
    durationSeconds: Math.max(0, config.durationMinutes) * 60,
    notifyIntervalSeconds: Math.max(1, config.notifyMinutes) * 60
  })
}

const startTimer = async (match: Match) => {
  await requestNotificationPermission()
  emit('timerUpdate', match.id, { running: true })
}

const pauseTimer = (match: Match) => {
  emit('timerUpdate', match.id, { running: false })
}

const resetTimer = (match: Match) => {
  notifiedCheckpoint.value[match.id] = 0
  emit('timerUpdate', match.id, { reset: true })
}

const tickTimers = () => {
  for (const match of props.matches) {
    const current = displayTimes.value[match.id] ?? match.timerElapsedSeconds
    const next = match.timerRunning ? current + 1 : match.timerElapsedSeconds
    displayTimes.value[match.id] = next

    if (!match.timerRunning) continue

    const interval = Math.max(1, match.timerNotifyIntervalSeconds)
    const checkpoint = Math.floor(next / interval)
    if (checkpoint > (notifiedCheckpoint.value[match.id] ?? 0)) {
      notifiedCheckpoint.value[match.id] = checkpoint
      const ongoing = formatSeconds(next)
      announce(`Match ${props.resolveName(match.playerAId)} vs ${props.resolveName(match.playerBId)} has been running for ${ongoing}.`)
    }
  }
}

watch(
  rounds,
  (nextRounds) => {
    if (nextRounds.length === 0) {
      selectedRound.value = 1
      return
    }

    const exists = nextRounds.some((round) => round.number === selectedRound.value)
    if (!exists) {
      selectedRound.value = nextRounds[0].number
    }
  },
  { immediate: true }
)

watch(
  () => props.matches,
  (matches) => {
    for (const match of matches) {
      displayTimes.value[match.id] = match.timerElapsedSeconds
      timerInputs.value[match.id] = {
        direction: match.timerDirection,
        durationMinutes: Math.floor(match.timerDurationSeconds / 60),
        notifyMinutes: Math.max(1, Math.floor(match.timerNotifyIntervalSeconds / 60))
      }
      notifiedCheckpoint.value[match.id] = Math.floor(match.timerElapsedSeconds / Math.max(1, match.timerNotifyIntervalSeconds))
    }
  },
  { immediate: true, deep: true }
)

watch(
  () => props.matches.some((match) => match.timerRunning),
  (hasRunning) => {
    if (hasRunning && !ticker) {
      ticker = setInterval(tickTimers, 1000)
    }
    if (!hasRunning && ticker) {
      clearInterval(ticker)
      ticker = undefined
    }
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  if (ticker) {
    clearInterval(ticker)
  }
})
</script>

<template>
  <section class="card">
    <h2>Round Overview</h2>

    <div style="display:flex; gap:0.5rem; margin-bottom:0.8rem; flex-wrap:wrap;">
      <button
        v-for="round in rounds"
        :key="round.number"
        type="button"
        :class="{ secondary: selectedRound !== round.number }"
        @click="selectedRound = round.number"
      >
        Round {{ round.number }}
      </button>
    </div>

    <p v-if="rounds.length === 0">No rounds generated yet.</p>

    <div v-else class="card" style="margin:0.5rem 0;">
      <strong>Round {{ selectedRound }}</strong>
      <p style="margin-top:0.4rem; color: var(--text-soft);">
        {{ selectedRoundMatches.length }} matches in this round
      </p>

      <div v-for="match in selectedRoundMatches" :key="match.id" class="card" style="margin:0.5rem 0;">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <strong>{{ resolveName(match.playerAId) }} vs {{ resolveName(match.playerBId) }}</strong>
          <span :style="{ color: match.status === 'completed' ? 'var(--ok)' : 'var(--text-soft)' }">{{ match.status }}</span>
        </div>

        <div style="display:flex; gap:0.5rem; margin-top:0.6rem; align-items:center;">
          <input v-model.number="inputFor(match.id).winsA" type="number" min="0" max="2" style="width:70px" />
          <span>-</span>
          <input v-model.number="inputFor(match.id).winsB" type="number" min="0" max="2" style="width:70px" />
          <button type="button" @click="emit('submit', match.id, inputFor(match.id).winsA, inputFor(match.id).winsB)">
            Save
          </button>
          <button type="button" class="secondary" @click="emit('clear', match.id)">Undo</button>
        </div>

        <div style="display:grid; gap:0.4rem; margin-top:0.8rem;">
          <strong>
            Timer: {{ formatSeconds(currentDisplaySeconds(match)) }}
            <span v-if="match.timerDirection === 'down'">remaining</span>
            <span v-else>elapsed</span>
          </strong>
          <div style="display:flex; gap:0.4rem; flex-wrap:wrap; align-items:center;">
            <label>
              Direction
              <select v-model="timerInputFor(match).direction">
                <option value="up">Count up</option>
                <option value="down">Count down</option>
              </select>
            </label>
            <label>
              Duration (min)
              <input v-model.number="timerInputFor(match).durationMinutes" type="number" min="0" style="width:90px" />
            </label>
            <label>
              Notify every (min)
              <input v-model.number="timerInputFor(match).notifyMinutes" type="number" min="1" style="width:90px" />
            </label>
            <button type="button" class="secondary" @click="applyTimerSettings(match)">Apply</button>
          </div>

          <div style="display:flex; gap:0.4rem; flex-wrap:wrap;">
            <button type="button" @click="startTimer(match)">{{ match.timerRunning ? 'Restart' : 'Start' }}</button>
            <button type="button" class="secondary" @click="pauseTimer(match)">Pause</button>
            <button type="button" class="secondary" @click="resetTimer(match)">Reset to 0</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>
