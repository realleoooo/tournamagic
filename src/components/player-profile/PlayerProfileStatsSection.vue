<script setup lang="ts">
import { computed } from 'vue'
import type { PlayerProfileStats } from '@/domain/models'
import PlayerProfileStatCard from '@/components/player-profile/PlayerProfileStatCard.vue'
import { getMatchWinRate, getPodiumCount } from '@/components/player-profile/playerProfileFormatters'

const props = defineProps<{
  stats: PlayerProfileStats
}>()

const statCards = computed(() => [
  {
    label: 'Tournaments',
    value: props.stats.tournamentsPlayed,
    description: `${props.stats.completedTournaments} completed, ${props.stats.inProgressTournaments} active`
  },
  {
    label: 'Match record',
    value: `${props.stats.totalMatchWins} - ${props.stats.totalMatchLosses}`,
    description: `${getMatchWinRate(props.stats)}% win rate across reported matches`
  },
  {
    label: 'Podium finishes',
    value: getPodiumCount(props.stats),
    description: `${props.stats.firstPlaces} first, ${props.stats.secondPlaces} second, ${props.stats.thirdPlaces} third`
  },
  {
    label: 'Game record',
    value: `${props.stats.totalGameWins} - ${props.stats.totalGameLosses}`,
    description: 'Best-of-three game wins and losses from completed results'
  }
])
</script>

<template>
  <section class="profile-stats">
    <PlayerProfileStatCard
      v-for="card in statCards"
      :key="card.label"
      :label="card.label"
      :value="card.value"
      :description="card.description"
    />
  </section>
</template>

<style scoped>
.profile-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1px;
  border: 1px solid rgba(201, 153, 82, 0.52);
  background: rgba(191, 143, 74, 0.26);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 34px rgba(0, 0, 0, 0.24);
}

@media (max-width: 980px) {
  .profile-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .profile-stats {
    grid-template-columns: 1fr;
  }
}
</style>
