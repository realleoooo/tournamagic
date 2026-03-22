<script setup lang="ts">
import type { Match } from '@/domain/models'
import MatchParticipant from '@/components/matches/MatchParticipant.vue'
import MatchResultActions from '@/components/matches/MatchResultActions.vue'
import MatchScoreInputs from '@/components/matches/MatchScoreInputs.vue'
import MatchTimer from '@/components/matches/MatchTimer.vue'

defineProps<{
  match: Match
  playerAName: string
  playerBName: string
  playerAHasProfile: boolean
  playerBHasProfile: boolean
  winsA: number
  winsB: number
}>()

const emit = defineEmits<{
  'update:winsA': [value: number]
  'update:winsB': [value: number]
  submit: []
  clear: []
  openPlayer: [playerId: string]
}>()
</script>

<template>
  <article class="match-row">
    <div class="match-row__title">
      <strong>{{ playerAName.toUpperCase() }} vs {{ playerBName.toUpperCase() }}</strong>
      <span :class="['match-row__status', { 'match-row__status--done': match.status === 'completed' }]">
        {{ match.status }}
      </span>
    </div>

    <div class="match-row__arena">
      <MatchParticipant :name="playerAName" :clickable="playerAHasProfile" @open="emit('openPlayer', match.playerAId)" />

      <MatchScoreInputs
        :wins-a="winsA"
        :wins-b="winsB"
        @update:wins-a="emit('update:winsA', $event)"
        @update:wins-b="emit('update:winsB', $event)"
      />

      <MatchParticipant
        :name="playerBName"
        :clickable="playerBHasProfile"
        right-aligned
        @open="emit('openPlayer', match.playerBId)"
      />
    </div>

    <MatchResultActions @submit="emit('submit')" @clear="emit('clear')" />

    <MatchTimer />
  </article>
</template>

<style scoped>
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

@media (max-width: 1100px) {
  .match-row__arena {
    grid-template-columns: 1fr;
    justify-items: center;
  }
}
</style>
