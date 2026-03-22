<script setup lang="ts">
import type { MatchRound } from '@/utils/matchRounds'

defineProps<{
  rounds: MatchRound[]
  selectedRound: number
  selectedRoundMatchCount: number
}>()

const emit = defineEmits<{
  selectRound: [round: number]
}>()
</script>

<template>
  <div class="matches-panel__heading">
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
          @click="emit('selectRound', round.number)"
        >
          Round {{ round.number }}
        </button>
      </div>
    </div>

    <div v-if="rounds.length > 0" class="matches-panel__summary">
      <strong>Round {{ selectedRound }}</strong>
      <p>{{ selectedRoundMatchCount }} matches in this round</p>
    </div>
  </div>
</template>

<style scoped>
.matches-panel__heading {
  display: grid;
  gap: 0.75rem;
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
}

.matches-panel__copy p,
.matches-panel__summary p {
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

@media (max-width: 1100px) {
  .matches-panel__intro {
    grid-template-columns: 1fr;
  }

  .matches-panel__tabs {
    justify-content: flex-start;
  }
}
</style>
