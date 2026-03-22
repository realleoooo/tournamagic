<script setup lang="ts">
import type { TournamentSummary } from '@/api/tournamentApi'
import SetupTournamentCard from '@/components/setup/SetupTournamentCard.vue'

defineProps<{
  tournaments: TournamentSummary[]
}>()

const emit = defineEmits<{
  open: [id: string]
  delete: [id: string]
}>()
</script>

<template>
  <div v-if="tournaments.length === 0" class="setup-empty">
    <strong>No tournaments yet</strong>
    <p>Begin your journey by creating your first tournament.</p>
  </div>

  <div v-else class="setup-list">
    <SetupTournamentCard
      v-for="tournament in tournaments"
      :key="tournament.id"
      :tournament="tournament"
      @open="emit('open', $event)"
      @delete="emit('delete', $event)"
    />
  </div>
</template>

<style scoped>
.setup-empty {
  margin-top: 1rem;
  display: grid;
  gap: 0.45rem;
  min-height: 180px;
  place-items: center;
  text-align: center;
  border: 1px solid rgba(158, 113, 56, 0.72);
  background:
    linear-gradient(180deg, rgba(73, 44, 26, 0.95), rgba(32, 20, 13, 0.98)),
    #29180f;
  color: #efd9af;
  padding: 0.95rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.setup-empty strong {
  font-size: 1.35rem;
  font-weight: 700;
}

.setup-empty p {
  margin: 0;
  max-width: 26rem;
}

.setup-list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
  align-content: start;
}
</style>
