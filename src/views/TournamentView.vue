<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import InviteShareSection from '@/components/tournaments/InviteShareSection.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const tournament = computed(() => store.tournament)
const activeSection = ref<'competition' | 'invites'>('competition')
const sidebarOpen = ref(false)

const canStartTournament = computed(
  () =>
    tournament.value?.status === 'setup' &&
    tournament.value.players.length >= 2 &&
    tournament.value.players.every((player) => player.claimedByEmail)
)

const claimedPlayers = computed(
  () => tournament.value?.players.filter((player) => player.claimedByEmail).length ?? 0
)

const competitionRows = computed(() =>
  tournament.value?.players.map((player) => ({
    id: player.id,
    name: player.name,
    opponents: store.remainingOpponents(player.id)
  })) ?? []
)

onMounted(async () => {
  if (!tournament.value) {
    await store.bootstrap()
  }

  if (!store.tournament) {
    router.replace('/')
  }
})

const selectSection = (section: 'competition' | 'invites') => {
  activeSection.value = section
  sidebarOpen.value = false
}

const goOverview = () => {
  store.leaveTournament()
  router.replace('/')
}

const onReset = async () => {
  await store.resetTournament()
  router.replace('/')
}

const onStart = async () => {
  await store.startTournament()
}

const onLeave = async () => {
  const updated = await store.leaveJoinedTournament()
  if (updated) {
    store.leaveTournament()
    router.replace('/')
  }
}
</script>

<template>
  <div class="tournament-shell">
    <section v-if="store.error" class="card status-card status-card--error">
      <strong>API error:</strong> {{ store.error }}
    </section>

    <section v-if="store.loading" class="card status-card">
      Loading…
    </section>

    <div v-if="tournament" class="tournament-layout">
      <button
        type="button"
        class="sidebar-toggle secondary"
        :aria-expanded="sidebarOpen"
        @click="sidebarOpen = !sidebarOpen"
      >
        {{ sidebarOpen ? 'Hide menu' : 'Show menu' }}
      </button>

      <aside class="sidebar card" :class="{ 'sidebar--open': sidebarOpen }">
        <div class="sidebar__section">
          <p class="sidebar__label">Tournament menu</p>
          <nav class="sidebar__nav" aria-label="Tournament sections">
            <button
              type="button"
              class="sidebar__link"
              :class="{ 'sidebar__link--active': activeSection === 'competition' }"
              @click="selectSection('competition')"
            >
              Competition
            </button>
            <button
              type="button"
              class="sidebar__link"
              :class="{ 'sidebar__link--active': activeSection === 'invites' }"
              @click="selectSection('invites')"
            >
              Invite Players
            </button>
          </nav>
        </div>

        <div class="sidebar__section sidebar__meta">
          <p><strong>Status</strong><span>{{ tournament.status }}</span></p>
          <p><strong>Players</strong><span>{{ tournament.players.length }}</span></p>
          <p><strong>Claims</strong><span>{{ claimedPlayers }}/{{ tournament.players.length }}</span></p>
        </div>
      </aside>

      <main class="content-column">
        <section class="card tournament-header">
          <div>
            <h2>{{ tournament.name }}</h2>
            <p>
              {{ tournament.players.length }} players · {{ store.completion.completed }}/{{ store.completion.total }} matches reported
            </p>
          </div>
          <div class="tournament-header__actions">
            <button type="button" class="secondary" @click="goOverview">Back to overview</button>
            <button class="warn" type="button" @click="onReset">Reset Tournament</button>
            <button v-if="canStartTournament" type="button" @click="onStart">Start Tournament</button>
            <button v-if="tournament.currentUserJoined" type="button" class="secondary" @click="onLeave">
              Leave Tournament
            </button>
          </div>
          <p v-if="tournament.status === 'setup'" class="tournament-header__note">
            Every player slot must be claimed before the tournament can start.
          </p>
        </section>

        <template v-if="activeSection === 'competition'">
          <ProgressPanel
            v-if="tournament.matches.length > 0"
            :completed="store.completion.completed"
            :total="store.completion.total"
          />

          <MatchList
            :matches="tournament.matches"
            :players="tournament.players"
            :resolve-name="store.resolveName"
            @submit="store.submitResult"
            @clear="store.clearResult"
          />

          <LeaderboardTable v-if="tournament.matches.length > 0" :standings="store.standings" />

          <section v-if="tournament.matches.length > 0" class="card opponents-panel">
            <div class="section-heading">
              <h2>Remaining Opponents</h2>
              <p>See who still needs to play each player before the schedule is complete.</p>
            </div>

            <div class="opponents-board">
              <article v-for="row in competitionRows" :key="row.id" class="opponent-row">
                <div>
                  <h3>{{ row.name }}</h3>
                  <p>{{ row.opponents.length }} match{{ row.opponents.length === 1 ? '' : 'es' }} left</p>
                </div>
                <p class="opponent-row__list">
                  {{ row.opponents.join(', ') || 'All pairings complete' }}
                </p>
              </article>
            </div>
          </section>
        </template>

        <InviteShareSection v-else :tournament="tournament" />
      </main>

      <aside class="claims-column">
        <section class="card claims-panel">
          <div class="section-heading">
            <h2>Player Claims</h2>
            <p>Joined users must claim a seat before they can participate in the tournament.</p>
          </div>

          <ul class="participant-list">
            <li v-for="player in tournament.players" :key="player.id" class="participant-list__item">
              <div>
                <strong>{{ player.name }}</strong>
                <p>{{ player.claimedByName ? `${player.claimedByName} · ${player.claimedByEmail}` : 'Unclaimed' }}</p>
              </div>
              <span :class="player.claimedByEmail ? 'claim-state claim-state--joined' : 'claim-state'">
                {{ player.claimedByEmail ? 'Joined' : 'Waiting' }}
              </span>
            </li>
          </ul>
        </section>
      </aside>
    </div>
  </div>
</template>

<style scoped>
.tournament-shell {
  display: grid;
  gap: 1rem;
}

.tournament-layout {
  display: grid;
  gap: 1rem;
}

.sidebar-toggle {
  justify-self: start;
}

.sidebar {
  display: none;
}

.sidebar--open {
  display: grid;
  gap: 1rem;
}

.sidebar__section {
  display: grid;
  gap: 0.75rem;
}

.sidebar__label {
  margin: 0;
  color: var(--text-soft);
  font-size: 0.95rem;
}

.sidebar__nav {
  display: grid;
  gap: 0.35rem;
}

.sidebar__link {
  width: 100%;
  justify-content: flex-start;
  background: transparent;
  border: 1px solid transparent;
  color: var(--text-main);
  font-weight: 600;
}

.sidebar__link:hover,
.sidebar__link:focus-visible {
  border-color: var(--border-strong);
  background: var(--bg-muted);
}

.sidebar__link--active {
  border-color: var(--accent-arcane);
  background: color-mix(in srgb, var(--accent-arcane) 18%, var(--bg-surface));
}

.sidebar__meta p {
  display: flex;
  justify-content: space-between;
  margin: 0;
  color: var(--text-soft);
}

.content-column,
.claims-column {
  display: grid;
  gap: 1rem;
}

.tournament-header {
  display: grid;
  gap: 1rem;
}

.tournament-header h2,
.section-heading h2,
.opponent-row h3 {
  margin: 0;
}

.tournament-header p,
.section-heading p,
.opponent-row p {
  margin: 0.3rem 0 0;
  color: var(--text-soft);
}

.tournament-header__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
}

.tournament-header__note {
  margin: 0;
}

.claims-panel {
  height: fit-content;
}

.participant-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  gap: 0.75rem;
}

.participant-list__item {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.85rem 0;
  border-top: 1px solid var(--border-subtle);
}

.participant-list__item:first-child {
  border-top: 0;
  padding-top: 0;
}

.participant-list__item p {
  margin: 0.2rem 0 0;
  color: var(--text-soft);
}

.claim-state {
  white-space: nowrap;
  color: var(--text-soft);
}

.claim-state--joined {
  color: var(--accent-gold);
}

.opponents-board {
  display: grid;
  gap: 0.75rem;
}

.opponent-row {
  display: grid;
  gap: 0.4rem;
  padding: 0.95rem 0;
  border-top: 1px solid var(--border-subtle);
}

.opponent-row:first-child {
  border-top: 0;
  padding-top: 0;
}

.opponent-row__list {
  color: var(--text-main);
}

.status-card--error {
  border-color: var(--danger);
}

@media (min-width: 980px) {
  .tournament-layout {
    grid-template-columns: 250px minmax(0, 1fr) 320px;
    align-items: start;
  }

  .sidebar-toggle {
    display: none;
  }

  .sidebar {
    display: grid;
    gap: 1rem;
    position: sticky;
    top: 5.5rem;
  }

  .claims-column {
    position: sticky;
    top: 5.5rem;
  }
}
</style>
