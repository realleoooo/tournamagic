<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PlayerProfileHero from '@/components/player-profile/PlayerProfileHero.vue'
import PlayerProfileHistorySection from '@/components/player-profile/PlayerProfileHistorySection.vue'
import PlayerProfileMessage from '@/components/player-profile/PlayerProfileMessage.vue'
import PlayerProfileStatsSection from '@/components/player-profile/PlayerProfileStatsSection.vue'
import { usePlayerProfile } from '@/composables/usePlayerProfile'
import { useTournamentStore } from '@/stores/tournament'

const route = useRoute()
const router = useRouter()
const tournamentStore = useTournamentStore()

const routeEmail = computed(() => (typeof route.params.email === 'string' ? route.params.email : ''))
const hasTournamentContext = computed(() => Boolean(tournamentStore.tournament))
const { error, loading, profile } = usePlayerProfile(routeEmail)

const openOverview = () => {
  router.push({ name: 'setup' })
}

const openTournament = () => {
  router.push({ name: 'tournament' })
}
</script>

<template>
  <div class="profile-page">
    <PlayerProfileHero
      :name="profile?.name ?? routeEmail"
      :email="profile?.email ?? routeEmail"
      :current-user="profile?.currentUser"
      :has-tournament-context="hasTournamentContext"
      @open-tournament="openTournament"
      @open-overview="openOverview"
    />

    <PlayerProfileMessage
      v-if="error"
      title="Profile unavailable"
      :message="error"
      tone="error"
    />

    <PlayerProfileMessage
      v-else-if="loading"
      title="Loading profile"
      message="Gathering tournament history and player stats."
    />

    <template v-else-if="profile">
      <PlayerProfileStatsSection :stats="profile.stats" />
      <PlayerProfileHistorySection :tournaments="profile.tournaments" />
    </template>
  </div>
</template>

<style scoped>
.profile-page {
  display: grid;
  gap: 1.2rem;
  width: min(100%, 1040px);
  margin: 0 auto;
  padding-bottom: 1.8rem;
}

@media (max-width: 720px) {
  .profile-page {
    padding-bottom: 1rem;
  }
}
</style>
