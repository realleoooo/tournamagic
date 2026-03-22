<script setup lang="ts">
import JoinCardShell from '@/components/join/JoinCardShell.vue'
import JoinSeatOption from '@/components/join/JoinSeatOption.vue'
import { formatSeatAvailabilityLabel } from '@/components/join/joinViewFormatters'
import type { Player } from '@/domain/models'

defineProps<{
  availablePlayers: Player[]
  selectedPlayerId: string
  loading: boolean
  canJoin: boolean
}>()

const emit = defineEmits<{
  select: [id: string]
  join: []
  back: []
}>()
</script>

<template>
  <JoinCardShell title="Choose Your Seat" :meta="formatSeatAvailabilityLabel(availablePlayers.length)">
    <div v-if="availablePlayers.length > 0" class="join-seat-list">
      <JoinSeatOption
        v-for="player in availablePlayers"
        :key="player.id"
        :player="player"
        :selected="selectedPlayerId === player.id"
        @select="emit('select', $event)"
      />
    </div>

    <div v-else class="join-empty">
      <strong>No open seats</strong>
      <p>All players in this tournament have already been claimed.</p>
    </div>

    <div class="join-actions">
      <button type="button" class="join-actions__primary" :disabled="loading || !canJoin" @click="emit('join')">
        Join Tournament
      </button>
      <button type="button" class="secondary join-actions__secondary" @click="emit('back')">Back to Overview</button>
    </div>
  </JoinCardShell>
</template>

<style scoped>
.join-seat-list {
  display: grid;
  gap: 0.75rem;
}

.join-empty {
  display: grid;
  gap: 0.4rem;
  min-height: 180px;
  place-items: center;
  text-align: center;
}

.join-empty strong,
.join-empty p {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-empty strong {
  color: #f4dfb3;
  font-size: 1.3rem;
}

.join-empty p {
  margin: 0;
  max-width: 20rem;
  color: #e2c79a;
  font-size: 0.95rem;
}

.join-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}

.join-actions__primary,
.join-actions__secondary {
  min-width: 180px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.join-actions__primary {
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
}

.join-actions__primary:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

@media (max-width: 720px) {
  .join-actions__primary,
  .join-actions__secondary {
    width: 100%;
  }
}
</style>
