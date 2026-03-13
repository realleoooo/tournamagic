<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Match, Player } from '@/domain/models'

const props = defineProps<{
  matches: Match[]
  players: Player[]
  resolveName: (id: string) => string
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
}>()

const selectedRound = ref(1)
const resultInputs = ref<Record<string, { winsA: number; winsB: number }>>({})

const inputFor = (matchId: string) => {
  if (!resultInputs.value[matchId]) {
    resultInputs.value[matchId] = { winsA: 2, winsB: 0 }
  }
  return resultInputs.value[matchId]
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
      </div>
    </div>
  </section>
</template>
