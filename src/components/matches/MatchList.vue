<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import type { Match, Player } from '@/domain/models'
import MatchRow from '@/components/matches/MatchRow.vue'
import RoundOverviewSkeleton from '@/components/matches/RoundOverviewSkeleton.vue'
import RoundSelector from '@/components/matches/RoundSelector.vue'
import { useTournamentShell } from '@/composables/useTournamentShell'
import { buildMatchRounds } from '@/utils/matchRounds'

const props = defineProps<{
  matches: Match[]
  players: Player[]
  resolveName: (id: string) => string
  resolveProfileEmail: (id: string) => string | null | undefined
  loading?: boolean
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
}>()

const resultInputs = ref<Record<string, { winsA: number; winsB: number }>>({})
const shell = useTournamentShell()
const router = useRouter()

const selectedRound = computed({
  get: () => shell.state.selectedRound,
  set: (round: number) => shell.setSelectedRound(round)
})

const rounds = computed(() => buildMatchRounds(props.players, props.matches))

const selectedRoundMatches = computed(
  () => rounds.value.find((round) => round.number === selectedRound.value)?.matches ?? []
)

const inputFor = (matchId: string) => {
  if (!resultInputs.value[matchId]) {
    resultInputs.value[matchId] = { winsA: 2, winsB: 0 }
  }

  return resultInputs.value[matchId]
}

const profileEmailFor = (playerId: string) => props.resolveProfileEmail(playerId)

const goToProfile = (playerId: string) => {
  const email = profileEmailFor(playerId)
  if (!email) {
    return
  }

  router.push({
    name: 'player-profile',
    params: { email }
  })
}

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
  <RoundOverviewSkeleton v-if="loading" />

  <section v-else class="matches-panel">
    <RoundSelector
      :rounds="rounds"
      :selected-round="selectedRound"
      :selected-round-match-count="selectedRoundMatches.length"
      @select-round="selectedRound = $event"
    />

    <p v-if="rounds.length === 0" class="matches-panel__empty">No rounds generated yet.</p>

    <div v-else class="matches-panel__content">
      <div class="matches-panel__list">
        <MatchRow
          v-for="match in selectedRoundMatches"
          :key="match.id"
          :match="match"
          :player-a-name="resolveName(match.playerAId)"
          :player-b-name="resolveName(match.playerBId)"
          :player-a-has-profile="Boolean(profileEmailFor(match.playerAId))"
          :player-b-has-profile="Boolean(profileEmailFor(match.playerBId))"
          :wins-a="inputFor(match.id).winsA"
          :wins-b="inputFor(match.id).winsB"
          @update:wins-a="inputFor(match.id).winsA = $event"
          @update:wins-b="inputFor(match.id).winsB = $event"
          @open-player="goToProfile"
          @submit="emit('submit', match.id, inputFor(match.id).winsA, inputFor(match.id).winsB)"
          @clear="emit('clear', match.id)"
        />
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

.matches-panel__empty {
  margin: 0;
  color: #f0d8aa;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.95rem;
}

.matches-panel__content {
  display: grid;
  grid-template-rows: minmax(0, 1fr);
  min-height: 0;
}

.matches-panel__list {
  display: grid;
  gap: 0.9rem;
  overflow: auto;
  min-height: 0;
  scrollbar-gutter: stable;
  padding-right: 0.2rem;
}

@media (max-width: 1100px) {
  .matches-panel {
    padding: 0.8rem;
  }
}
</style>
