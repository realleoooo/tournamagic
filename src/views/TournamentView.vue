<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import InviteShareSection from '@/components/tournaments/InviteShareSection.vue'
import ScrollArea from '@/components/shared/ScrollArea.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const menuItems = [
  {
    key: 'rounds',
    label: 'Round Overview',
    icon: [
      'M4 6.5h16',
      'M4 12h16',
      'M4 17.5h10'
    ]
  },
  {
    key: 'leaderboard',
    label: 'Leaderboard',
    icon: [
      'M5 18V9',
      'M10 18V6',
      'M15 18v-4',
      'M20 18V3'
    ]
  },
  {
    key: 'opponents',
    label: 'Remaining Opponents',
    icon: [
      'M7 7h10',
      'M7 12h10',
      'M7 17h6',
      'M4 7h.01',
      'M4 12h.01',
      'M4 17h.01'
    ]
  },
  {
    key: 'invites',
    label: 'Invite Players',
    icon: [
      'M4 7.5 12 13l8-5.5',
      'M5.5 6h13A1.5 1.5 0 0 1 20 7.5v9A1.5 1.5 0 0 1 18.5 18h-13A1.5 1.5 0 0 1 4 16.5v-9A1.5 1.5 0 0 1 5.5 6Z'
    ]
  }
] as const

type SectionKey = (typeof menuItems)[number]['key']

const tournament = computed(() => store.tournament)
const activeSection = ref<SectionKey>('rounds')
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

const toggleSidebar = () => {
  sidebarOpen.value = !sidebarOpen.value
}

const closeSidebar = () => {
  sidebarOpen.value = false
}

onMounted(async () => {
  window.addEventListener('tournamagic:toggle-sidebar', toggleSidebar)

  if (!tournament.value) {
    await store.bootstrap()
  }

  if (!store.tournament) {
    router.replace('/')
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('tournamagic:toggle-sidebar', toggleSidebar)
})

const selectSection = (section: SectionKey) => {
  activeSection.value = section
  closeSidebar()
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
      <button v-if="sidebarOpen" type="button" class="sidebar-backdrop" aria-label="Close menu" @click="closeSidebar" />

      <aside class="sidebar" :class="{ 'sidebar--open': sidebarOpen }">
        <div class="sidebar__section">
          <p class="sidebar__label">Tournament menu</p>
          <nav class="sidebar__nav" aria-label="Tournament sections">
            <button
              v-for="item in menuItems"
              :key="item.key"
              type="button"
              class="sidebar__link"
              :class="{ 'sidebar__link--active': activeSection === item.key }"
              @click="selectSection(item.key)"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path
                  v-for="segment in item.icon"
                  :key="segment"
                  :d="segment"
                  fill="none"
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="1.8"
                />
              </svg>
              <span>{{ item.label }}</span>
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
        <section class="header-row" :class="{ 'header-row--with-progress': activeSection === 'rounds' && tournament.matches.length > 0 }">
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

          <ProgressPanel
            v-if="activeSection === 'rounds' && tournament.matches.length > 0"
            class="header-row__progress"
            :completed="store.completion.completed"
            :total="store.completion.total"
          />
        </section>

        <template v-if="activeSection === 'rounds'">
          <MatchList
            class="section-scroll-panel"
            :matches="tournament.matches"
            :players="tournament.players"
            :resolve-name="store.resolveName"
            @submit="store.submitResult"
            @clear="store.clearResult"
          />
        </template>

        <template v-else-if="activeSection === 'leaderboard'">
          <LeaderboardTable class="section-scroll-panel" v-if="tournament.matches.length > 0" :standings="store.standings" />
        </template>

        <template v-else-if="activeSection === 'opponents'">
          <section v-if="tournament.matches.length > 0" class="card opponents-panel section-scroll-panel">
            <div class="section-heading">
              <h2>Remaining Opponents</h2>
              <p>See who still needs to play each player before the schedule is complete.</p>
            </div>

            <ScrollArea class="opponents-panel__scroll">
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
            </ScrollArea>
          </section>
        </template>

        <section v-else class="invite-workspace">
          <InviteShareSection :tournament="tournament" class="invite-workspace__panel" />

          <section class="card claims-panel invite-workspace__panel">
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
        </section>
      </main>
    </div>
  </div>
</template>

<style scoped>
.tournament-shell {
  display: grid;
  gap: 1rem;
}

.tournament-layout {
  position: relative;
  display: grid;
  gap: 1rem;
}

.sidebar-backdrop {
  position: fixed;
  inset: 0;
  z-index: 24;
  background: rgba(0, 0, 0, 0.45);
  border: 0;
  border-radius: 0;
  padding: 0;
}

.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 25;
  width: min(280px, 84vw);
  padding: 1.25rem 1rem;
  background: var(--bg-surface);
  border-right: 1px solid var(--border-subtle);
  display: grid;
  gap: 1rem;
  align-content: start;
  transform: translateX(-100%);
  transition: transform 180ms ease;
}

.sidebar--open {
  transform: translateX(0);
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
  justify-content: space-between;
  align-items: center;
  gap: 0.7rem;
  background: transparent;
  border: 1px solid transparent;
  color: var(--text-main);
  font-weight: 600;
}

.sidebar__link span {
  flex: 1 1 auto;
}

.sidebar__link svg {
  width: 18px;
  height: 18px;
  flex: 0 0 auto;
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

.content-column {
  display: grid;
  gap: 1rem;
  min-height: 0;
}

.header-row {
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

.header-row__progress {
  align-self: stretch;
}

.section-scroll-panel {
  min-height: 0;
  max-height: min(70vh, 900px);
}

.opponents-panel__scroll {
  min-height: 0;
}

.claims-panel {
  display: grid;
  align-content: start;
}

.invite-workspace {
  display: grid;
  gap: 1rem;
}

.invite-workspace__panel {
  height: 100%;
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
  .sidebar-backdrop {
    display: none;
  }

  .tournament-layout {
    grid-template-columns: 250px minmax(0, 1fr);
    align-items: stretch;
  }

  .header-row--with-progress {
    grid-template-columns: minmax(0, 1fr) 320px;
    align-items: stretch;
  }

  .sidebar {
    position: static;
    width: auto;
    padding: 1rem;
    height: 100%;
    transform: none;
    transition: none;
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
  }

  .invite-workspace {
    grid-template-columns: minmax(0, 1fr) minmax(280px, 0.9fr);
    align-items: stretch;
  }
}
</style>
