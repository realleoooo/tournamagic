<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { Match, Player } from '@/domain/models'
import MatchTimer from '@/components/matches/MatchTimer.vue'
import { useTournamentShell } from '@/composables/useTournamentShell'

const props = defineProps<{
  matches: Match[]
  players: Player[]
  resolveName: (id: string) => string
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
}>()

const resultInputs = ref<Record<string, { winsA: number; winsB: number }>>({})
const shell = useTournamentShell()

const selectedRound = computed({
  get: () => shell.state.selectedRound,
  set: (round: number) => shell.setSelectedRound(round)
})

const inputFor = (matchId: string) => {
  if (!resultInputs.value[matchId]) {
    resultInputs.value[matchId] = { winsA: 2, winsB: 0 }
  }
  return resultInputs.value[matchId]
}

const toMatchKey = (a: string, b: string) => [a, b].sort().join('::')

const playerInitials = (playerId: string) =>
  props
    .resolveName(playerId)
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('')

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
      shell.setSelectedRound(1)
      return
    }

    const exists = nextRounds.some((round) => round.number === selectedRound.value)
    if (!exists) {
      shell.setSelectedRound(nextRounds[0].number)
    }
  },
  { immediate: true }
)
</script>

<template>
  <section class="matches-panel">
    <div class="matches-panel__intro">
      <div class="matches-panel__copy">
        <strong>Round overview</strong>
        <p>Pick a round and record match results without leaving the page.</p>
      </div>

      <div class="matches-panel__tabs">
        <button
          v-for="round in rounds"
          :key="round.number"
          type="button"
          :class="['matches-panel__tab', { 'matches-panel__tab--active': selectedRound === round.number }]"
          @click="selectedRound = round.number"
        >
          Round {{ round.number }}
        </button>
      </div>
    </div>

    <p v-if="rounds.length === 0" class="matches-panel__empty">No rounds generated yet.</p>

    <div v-else class="matches-panel__content">
      <div class="matches-panel__summary">
        <strong>Round {{ selectedRound }}</strong>
        <p>{{ selectedRoundMatches.length }} matches in this round</p>
      </div>

      <div class="matches-panel__list">
        <article v-for="match in selectedRoundMatches" :key="match.id" class="match-row">
          <div class="match-row__title">
            <strong>{{ resolveName(match.playerAId).toUpperCase() }} vs {{ resolveName(match.playerBId).toUpperCase() }}</strong>
            <span :class="['match-row__status', { 'match-row__status--done': match.status === 'completed' }]">
              {{ match.status }}
            </span>
          </div>

          <div class="match-row__arena">
            <section class="match-player">
              <div class="match-player__avatar">
                <span>{{ playerInitials(match.playerAId) }}</span>
              </div>
              <div>
                <strong>{{ resolveName(match.playerAId) }}</strong>
              </div>
            </section>

            <div class="match-score">
              <label class="match-score__box">
                <input v-model.number="inputFor(match.id).winsA" type="number" min="0" max="2" />
              </label>
              <span class="match-score__divider">-</span>
              <label class="match-score__box">
                <input v-model.number="inputFor(match.id).winsB" type="number" min="0" max="2" />
              </label>
            </div>

            <section class="match-player match-player--right">
              <div>
                <strong>{{ resolveName(match.playerBId) }}</strong>
              </div>
              <div class="match-player__avatar match-player__avatar--right">
                <span>{{ playerInitials(match.playerBId) }}</span>
              </div>
            </section>
          </div>

          <div class="match-row__actions">
            <button type="button" class="match-action match-action--primary" @click="emit('submit', match.id, inputFor(match.id).winsA, inputFor(match.id).winsB)">
              Save Result
            </button>
            <button type="button" class="match-action match-action--secondary" @click="emit('clear', match.id)">
              Undo
            </button>
          </div>

          <MatchTimer />
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.matches-panel {
  border: 1px solid #875923;
  background:
    linear-gradient(180deg, rgba(71, 43, 24, 0.96), rgba(31, 18, 12, 0.98)),
    #26170f;
  padding: 0.95rem;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 0.95rem;
  min-height: 0;
  box-shadow:
    inset 0 0 0 1px rgba(222, 176, 94, 0.14),
    0 12px 22px rgba(0, 0, 0, 0.18);
}

.matches-panel__intro {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 1rem;
  align-items: center;
}

.matches-panel__copy strong,
.matches-panel__summary strong {
  color: #f5dfb6;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
  text-transform: uppercase;
}

.matches-panel__copy p,
.matches-panel__summary p,
.matches-panel__empty {
  margin: 0.25rem 0 0;
  color: #f0d8aa;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.95rem;
}

.matches-panel__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  justify-content: flex-end;
}

.matches-panel__tab {
  min-width: 106px;
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(248, 227, 175, 0.96), rgba(204, 157, 90, 0.94)),
    #d6ae69;
  border-color: rgba(150, 101, 43, 0.92);
  color: #3c1e05;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  box-shadow:
    inset 0 1px 0 rgba(255, 246, 213, 0.55),
    0 0 0 1px rgba(83, 50, 19, 0.42);
}

.matches-panel__tab:hover {
  background:
    linear-gradient(180deg, rgba(252, 234, 191, 0.98), rgba(219, 171, 100, 0.96)),
    #e0b46e;
}

.matches-panel__tab--active {
  background:
    linear-gradient(180deg, rgba(112, 174, 63, 0.96), rgba(42, 95, 31, 0.98)),
    #417f30;
  border-color: rgba(196, 156, 83, 0.84);
  color: #fef7da;
  box-shadow:
    inset 0 1px 0 rgba(224, 255, 196, 0.34),
    0 0 14px rgba(118, 210, 79, 0.24);
}

.matches-panel__content {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 0.75rem;
  min-height: 0;
}

.matches-panel__summary {
  border: 1px solid rgba(154, 108, 49, 0.62);
  background:
    linear-gradient(180deg, rgba(249, 228, 184, 0.92), rgba(220, 188, 129, 0.94)),
    #e2be83;
  color: #2d1705;
  padding: 0.95rem 1rem;
}

.matches-panel__summary strong {
  color: #2d1705;
  font-size: 1.6rem;
}

.matches-panel__summary p {
  color: #3f2810;
  font-size: 0.95rem;
}

.matches-panel__list {
  display: grid;
  gap: 0.9rem;
  overflow: auto;
  min-height: 0;
  scrollbar-gutter: stable;
  padding-right: 0.2rem;
}

.match-row {
  border: 1px solid #8f5f27;
  background:
    linear-gradient(180deg, rgba(245, 223, 180, 0.96), rgba(222, 189, 131, 0.96)),
    #e2bf84;
  padding: 0.95rem;
  display: grid;
  gap: 0.75rem;
  box-shadow:
    inset 0 0 0 1px rgba(255, 239, 203, 0.18),
    0 12px 20px rgba(0, 0, 0, 0.16);
}

.match-row__title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  color: #2a1505;
}

.match-row__title strong {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.1rem;
}

.match-row__status {
  color: #6e5633;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  text-transform: capitalize;
}

.match-row__status--done {
  color: #336d2b;
}

.match-row__arena {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border: 1px solid #825724;
  background:
    linear-gradient(180deg, rgba(32, 31, 34, 0.95), rgba(18, 18, 20, 0.98)),
    #1c1c1e;
  box-shadow: inset 0 0 0 1px rgba(235, 191, 109, 0.08);
}

.match-player {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  min-width: 0;
}

.match-player--right {
  justify-content: flex-end;
  text-align: right;
}

.match-player strong {
  display: block;
  color: #f2ddb4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
}

.match-player__avatar {
  width: 64px;
  height: 64px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  border: 2px solid #c0924d;
  background:
    radial-gradient(circle at 35% 35%, rgba(113, 174, 63, 0.86), rgba(25, 60, 21, 0.92)),
    #295229;
  color: #fff1cf;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.25rem;
  box-shadow:
    inset 0 1px 0 rgba(223, 255, 195, 0.24),
    0 0 0 2px rgba(72, 44, 20, 0.45);
}

.match-player__avatar--right {
  background:
    radial-gradient(circle at 35% 35%, rgba(78, 147, 224, 0.9), rgba(18, 54, 95, 0.96)),
    #244f78;
}

.match-score {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.match-score__box {
  width: 118px;
  display: block;
}

.match-score__box input {
  text-align: center;
  font-size: 2.05rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  color: #f7e4bd;
  background:
    linear-gradient(180deg, rgba(32, 68, 31, 0.96), rgba(22, 45, 17, 0.98)),
    #244920;
  border-color: rgba(194, 153, 83, 0.86);
  border-radius: 12px;
  padding: 0.55rem 0.6rem;
}

.match-score__divider {
  color: #f4e0b4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
}

.match-row__actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.match-action {
  min-width: 188px;
  border-radius: 12px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.match-action--primary {
  background:
    linear-gradient(180deg, rgba(116, 179, 64, 0.96), rgba(42, 92, 29, 0.98)),
    #417e30;
  border-color: rgba(204, 165, 88, 0.84);
  color: #fff6d9;
}

.match-action--primary:hover {
  background:
    linear-gradient(180deg, rgba(128, 195, 70, 0.98), rgba(52, 108, 36, 0.98)),
    #498737;
}

.match-action--secondary {
  background:
    linear-gradient(180deg, rgba(86, 59, 33, 0.96), rgba(42, 27, 17, 0.98)),
    #41291a;
  border-color: rgba(174, 128, 66, 0.82);
  color: #f1dcaf;
}

.match-action--secondary:hover {
  background:
    linear-gradient(180deg, rgba(100, 68, 38, 0.96), rgba(50, 31, 19, 0.98)),
    #4a2f1d;
}

@media (max-width: 1100px) {
  .matches-panel__intro {
    grid-template-columns: 1fr;
  }

  .matches-panel__tabs {
    justify-content: flex-start;
  }

  .match-row__arena {
    grid-template-columns: 1fr;
    justify-items: center;
  }

  .match-player,
  .match-player--right {
    width: 100%;
    justify-content: center;
    text-align: center;
  }
}

@media (max-width: 720px) {
  .match-score__box {
    width: 92px;
  }

  .match-score__box input {
    font-size: 1.7rem;
  }
}
</style>
