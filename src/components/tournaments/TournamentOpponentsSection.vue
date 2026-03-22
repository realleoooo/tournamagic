<script setup lang="ts">
import type { RemainingOpponentGroup } from '@/components/tournaments/remainingOpponents'
import TournamentOpponentCard from '@/components/tournaments/TournamentOpponentCard.vue'

defineProps<{
  groups: RemainingOpponentGroup[]
}>()

const emit = defineEmits<{
  openPlayerProfile: [playerId: string]
}>()
</script>

<template>
  <section class="feature-section">
    <div class="opponents-list">
      <TournamentOpponentCard
        v-for="(player, index) in groups"
        :key="player.id"
        :player="player"
        :alternate-count-tone="index % 2 === 1"
        @open-player-profile="emit('openPlayerProfile', $event)"
      />
    </div>
  </section>
</template>

<style scoped>
.feature-section {
  min-height: 0;
  height: 100%;
  min-width: 0;
  padding: 1rem 1.2rem 1.2rem;
  overflow: auto;
  scrollbar-gutter: stable;
}

.opponents-list {
  border: 1px solid #875923;
  background:
    linear-gradient(180deg, rgba(71, 43, 24, 0.96), rgba(31, 18, 12, 0.98)),
    #26170f;
  padding: 1rem;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1rem;
  align-content: start;
  min-height: 100%;
  box-shadow:
    inset 0 0 0 1px rgba(222, 176, 94, 0.14),
    0 12px 22px rgba(0, 0, 0, 0.18);
}

@media (max-width: 959px) {
  .opponents-list {
    grid-template-columns: 1fr;
  }
}
</style>
