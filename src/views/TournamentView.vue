<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import InviteShareSection from '@/components/tournaments/InviteShareSection.vue'
import TournamentFeatureHeader from '@/components/tournaments/TournamentFeatureHeader.vue'
import TournamentLeaderboardSection from '@/components/tournaments/TournamentLeaderboardSection.vue'
import TournamentOpponentsSection from '@/components/tournaments/TournamentOpponentsSection.vue'
import TournamentOverviewSection from '@/components/tournaments/TournamentOverviewSection.vue'
import TournamentSidebar from '@/components/tournaments/TournamentSidebar.vue'
import { buildRemainingOpponentGroups } from '@/components/tournaments/remainingOpponents'
import { tournamentSections } from '@/components/tournaments/tournamentSections'
import { useTournamentShell } from '@/composables/useTournamentShell'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()
const shell = useTournamentShell()

const tournament = computed(() => store.tournament)

const activeSectionLabel = computed(
  () => tournamentSections.find((section) => section.id === shell.state.activeSection)?.label ?? 'Tournament'
)

const activeSectionDescription = computed(() => {
  if (!tournament.value) {
    return ''
  }

  const playerCount = tournament.value.players.length
  return `${playerCount} ${playerCount === 1 ? 'player' : 'players'} active in this tournament`
})

const canStartTournament = computed(
  () =>
    tournament.value?.status === 'setup' &&
    tournament.value.players.length >= 2 &&
    tournament.value.players.every((player) => player.claimedByEmail)
)

const remainingOpponentGroups = computed(() =>
  tournament.value ? buildRemainingOpponentGroups(tournament.value, store.remainingOpponents) : []
)

const profileEmailForPlayer = (playerId: string) =>
  tournament.value?.players.find((player) => player.id === playerId)?.claimedByEmail ?? undefined

const openPlayerProfile = (playerId: string) => {
  const email = profileEmailForPlayer(playerId)
  if (!email) {
    return
  }

  router.push({
    name: 'player-profile',
    params: { email }
  })
}

const onReset = async () => {
  await store.resetTournament()
  shell.closeSidebar()
  router.replace('/')
}

const onStart = async () => {
  await store.startTournament()
}

const onLeave = async () => {
  const updated = await store.leaveJoinedTournament()
  if (updated) {
    store.leaveTournament()
    shell.closeSidebar()
    router.replace('/')
  }
}

onMounted(async () => {
  shell.setActiveSection('overview')

  if (!tournament.value) {
    await store.bootstrap()
  }

  if (!store.tournament) {
    router.replace('/')
  }
})
</script>

<template>
  <div :class="['tournament-shell', `tournament-shell--${shell.state.activeSection}`]">
    <TournamentSidebar
      :sections="tournamentSections"
      :active-section="shell.state.activeSection"
      :sidebar-open="shell.state.sidebarOpen"
      @close="shell.closeSidebar()"
      @select-section="shell.setActiveSection($event)"
    />

    <section class="feature-panel">
      <TournamentFeatureHeader
        :active-section="shell.state.activeSection"
        :active-section-label="activeSectionLabel"
        :active-section-description="activeSectionDescription"
        :tournament-name="tournament?.name"
        :can-start-tournament="Boolean(canStartTournament)"
        :current-user-joined="Boolean(tournament?.currentUserJoined)"
        @start="onStart"
        @reset="onReset"
        @leave="onLeave"
      />

      <p v-if="store.error" class="feature-panel__error">API error: {{ store.error }}</p>

      <div v-if="store.loading" class="feature-panel__loading">Loading...</div>

      <div v-else-if="tournament" class="feature-panel__body">
        <TournamentOverviewSection
          v-show="shell.state.activeSection === 'overview'"
          :tournament="tournament"
          :completed-matches="store.completion.completed"
          :total-matches="store.completion.total"
          :resolve-name="store.resolveName"
          :resolve-profile-email="profileEmailForPlayer"
          @submit="store.submitResult"
          @clear="store.clearResult"
        />

        <TournamentLeaderboardSection
          v-show="shell.state.activeSection === 'leaderboard'"
          :standings="store.standings"
          :has-matches="tournament.matches.length > 0"
          :resolve-profile-email="profileEmailForPlayer"
        />

        <TournamentOpponentsSection
          v-show="shell.state.activeSection === 'opponents'"
          :groups="remainingOpponentGroups"
          @open-player-profile="openPlayerProfile"
        />

        <section v-show="shell.state.activeSection === 'invite'" class="feature-section feature-section--invite">
          <InviteShareSection :tournament="tournament" />
        </section>
      </div>
    </section>
  </div>
</template>

<style scoped>
.tournament-shell {
  display: grid;
  grid-template-columns: 292px minmax(0, 1fr);
  gap: 1rem;
  min-height: 0;
  height: 100%;
}

.feature-panel {
  position: relative;
  min-height: 0;
  min-width: 0;
  border: 1px solid #9d6e34;
  background:
    linear-gradient(180deg, rgba(249, 227, 177, 0.96), rgba(222, 189, 132, 0.96)),
    #e6c287;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  box-shadow:
    inset 0 0 0 1px rgba(255, 241, 205, 0.22),
    0 18px 32px rgba(0, 0, 0, 0.24);
}

.feature-panel__error {
  margin: 0;
  padding: 0.85rem 1.55rem 0;
  color: var(--danger);
}

.feature-panel__loading {
  padding: 1.25rem;
}

.feature-panel__body {
  min-height: 0;
  height: 100%;
  min-width: 0;
}

.feature-section {
  padding: 1rem 1.2rem 1.2rem;
  overflow: auto;
  scrollbar-gutter: stable;
  min-height: 0;
  height: 100%;
  min-width: 0;
}

@media (max-width: 959px) {
  .tournament-shell {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
