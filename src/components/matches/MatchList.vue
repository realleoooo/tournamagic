<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Match, Player } from '@/domain/models'
import MatchTimer from '@/components/matches/MatchTimer.vue'

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
  <section class="card match-panel">
    <div class="section-heading">
      <h2>Round Overview</h2>
      <p>Switch rounds to record results and keep pairings visible at a glance.</p>
    </div>

    <div v-if="rounds.length > 0" class="round-tabs">
      <button
        v-for="round in rounds"
        :key="round.number"
        type="button"
        class="round-tabs__button"
        :class="{ 'round-tabs__button--inactive': selectedRound !== round.number }"
        @click="selectedRound = round.number"
      >
        Round {{ round.number }}
      </button>
    </div>

    <p v-if="rounds.length === 0" class="empty-state">No rounds generated yet.</p>

    <div v-else class="round-card">
      <div class="round-card__header">
        <div>
          <h3>Round {{ selectedRound }}</h3>
          <p>{{ selectedRoundMatches.length }} matches in this round</p>
        </div>
      </div>

      <article v-for="match in selectedRoundMatches" :key="match.id" class="match-row">
        <div class="match-row__top">
          <div>
            <h4>{{ resolveName(match.playerAId) }} vs {{ resolveName(match.playerBId) }}</h4>
            <p>{{ match.status === 'completed' ? 'Result recorded' : 'Waiting for result' }}</p>
          </div>
          <span :class="match.status === 'completed' ? 'match-status match-status--done' : 'match-status'">
            {{ match.status }}
          </span>
        </div>

        <div class="match-row__controls">
          <label>
            <span>{{ resolveName(match.playerAId) }}</span>
            <input v-model.number="inputFor(match.id).winsA" type="number" min="0" max="2" />
          </label>
          <label>
            <span>{{ resolveName(match.playerBId) }}</span>
            <input v-model.number="inputFor(match.id).winsB" type="number" min="0" max="2" />
          </label>
          <div class="match-row__actions">
            <button type="button" @click="emit('submit', match.id, inputFor(match.id).winsA, inputFor(match.id).winsB)">
              Save
            </button>
            <button type="button" class="secondary" @click="emit('clear', match.id)">Undo</button>
          </div>
        </div>

        <MatchTimer />
      </article>
    </div>
  </section>
</template>

<style scoped>
.match-panel,
.round-card {
  display: grid;
  gap: 1rem;
}

.section-heading h2,
.section-heading p,
.round-card__header h3,
.round-card__header p,
.match-row h4,
.match-row p {
  margin: 0;
}

.section-heading p,
.round-card__header p,
.match-row p,
.match-row label span {
  color: var(--text-soft);
}

.section-heading p,
.round-card__header p,
.match-row p {
  margin-top: 0.3rem;
}

.round-tabs {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.round-tabs__button {
  background: color-mix(in srgb, var(--accent-arcane) 18%, var(--bg-surface));
}

.round-tabs__button--inactive {
  background: transparent;
  border-color: var(--border-strong);
  color: var(--text-main);
}

.empty-state {
  margin: 0;
  color: var(--text-soft);
}

.round-card {
  padding-top: 0.25rem;
}

.match-row {
  display: grid;
  gap: 0.85rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-subtle);
}

.match-row__top {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  align-items: start;
}

.match-status {
  color: var(--text-soft);
  white-space: nowrap;
}

.match-status--done {
  color: var(--accent-gold);
}

.match-row__controls {
  display: grid;
  gap: 0.75rem;
}

.match-row__controls label {
  display: grid;
  gap: 0.35rem;
}

.match-row__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
  align-items: end;
}

@media (min-width: 720px) {
  .match-row__controls {
    grid-template-columns: repeat(2, minmax(0, 140px)) auto;
    align-items: end;
  }
}
</style>
