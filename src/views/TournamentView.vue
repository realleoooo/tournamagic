<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import InviteShareSection from '@/components/tournaments/InviteShareSection.vue'
import InviteIcon from '@/components/icons/InviteIcon.vue'
import LeaderboardIcon from '@/components/icons/LeaderboardIcon.vue'
import OpponentsIcon from '@/components/icons/OpponentsIcon.vue'
import OverviewIcon from '@/components/icons/OverviewIcon.vue'
import { useTournamentShell, type TournamentSection } from '@/composables/useTournamentShell'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()
const shell = useTournamentShell()

const tournament = computed(() => store.tournament)

const sections: Array<{ id: TournamentSection; label: string; icon: unknown }> = [
  { id: 'overview', label: 'Round overview', icon: OverviewIcon },
  { id: 'leaderboard', label: 'Leaderboard', icon: LeaderboardIcon },
  { id: 'opponents', label: 'Remaining opponents', icon: OpponentsIcon },
  { id: 'invite', label: 'Invite players', icon: InviteIcon }
]

const activeSectionLabel = computed(
  () => sections.find((section) => section.id === shell.state.activeSection)?.label ?? 'Tournament'
)

const activeSectionDescription = computed(() => {
  if (!tournament.value) return ''
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
  tournament.value
    ? tournament.value.players.map((player) => ({
        id: player.id,
        name: player.name,
        remaining: store.remainingOpponents(player.id),
        claimedBy: player.claimedByName ? `${player.claimedByName} - ${player.claimedByEmail}` : 'Unclaimed'
      }))
    : []
)

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
    <div
      v-if="shell.state.sidebarOpen"
      class="sidebar-backdrop"
      aria-hidden="true"
      @click="shell.closeSidebar()"
    ></div>

    <aside :class="['sidebar', { 'sidebar--open': shell.state.sidebarOpen }]">
      <div class="sidebar__header">
        <strong>Navigation</strong>
        <button type="button" class="secondary sidebar__close" @click="shell.closeSidebar()">Close</button>
      </div>

      <nav class="sidebar__nav" aria-label="Tournament sections">
        <button
          v-for="section in sections"
          :key="section.id"
          type="button"
          :class="['sidebar__link', { 'sidebar__link--active': shell.state.activeSection === section.id }]"
          @click="shell.setActiveSection(section.id)"
        >
          <component :is="section.icon" class="sidebar__icon" />
          <span>{{ section.label }}</span>
        </button>
      </nav>
    </aside>

    <section class="feature-panel">
      <header :class="['feature-panel__header', `feature-panel__header--${shell.state.activeSection}`]">
        <div class="feature-panel__header-copy">
          <strong>{{ activeSectionLabel }}</strong>
          <p v-if="tournament">{{ activeSectionDescription }}</p>
        </div>

        <div v-if="tournament" class="feature-panel__header-side">
          <strong class="feature-panel__tournament-name">{{ tournament.name }}</strong>

          <div class="feature-panel__actions">
            <button v-if="canStartTournament" type="button" @click="onStart">Start tournament</button>
            <button type="button" class="warn" @click="onReset">Reset tournament</button>
            <button v-if="tournament.currentUserJoined" type="button" class="secondary" @click="onLeave">
              Leave tournament
            </button>
          </div>
        </div>
      </header>

      <p v-if="store.error" class="feature-panel__error">API error: {{ store.error }}</p>

      <div v-if="store.loading" class="feature-panel__loading">Loading…</div>

      <div v-else-if="tournament" class="feature-panel__body">
          <section v-show="shell.state.activeSection === 'overview'" class="feature-section feature-section--overview">
          <div class="overview-top">
            <ProgressPanel
              v-if="tournament.matches.length > 0"
              :completed="store.completion.completed"
              :total="store.completion.total"
            />

            <section class="panel">
              <div class="overview-summary">
                <div>
                  <strong>{{ tournament.name }}</strong>
                  <p>{{ tournament.status }} tournament with {{ tournament.players.length }} registered players</p>
                </div>
                <span class="overview-summary__code">Join code: {{ tournament.joinCode }}</span>
              </div>
              <p v-if="tournament.status === 'setup'" class="overview-note">
                Every player slot must be claimed before the tournament can start.
              </p>
            </section>
          </div>

          <MatchList
            :matches="tournament.matches"
            :players="tournament.players"
            :resolve-name="store.resolveName"
            @submit="store.submitResult"
            @clear="store.clearResult"
          />
        </section>

        <section v-show="shell.state.activeSection === 'leaderboard'" class="feature-section">
          <LeaderboardTable v-if="tournament.matches.length > 0" :standings="store.standings" />
          <section v-else class="panel panel--empty">
            <strong>Leaderboard unavailable</strong>
            <p>Start the tournament to generate standings.</p>
          </section>
        </section>

        <section v-show="shell.state.activeSection === 'opponents'" class="feature-section">
          <section class="opponents-panel">
            <div class="opponents-list">
              <article
                v-for="(player, index) in remainingOpponentGroups"
                :key="player.id"
                class="opponent-card"
              >
                <div class="opponent-card__header">
                  <div>
                    <strong>{{ player.name }}</strong>
                    <p>{{ player.claimedBy }}</p>
                  </div>
                  <span :class="['opponent-card__count', `opponent-card__count--${Math.min(player.remaining.length, 3)}`, { 'opponent-card__count--alt': index % 2 === 1 }]">
                    {{ player.remaining.length }} left
                  </span>
                </div>

                <ul v-if="player.remaining.length > 0" class="opponent-card__list">
                  <li v-for="opponent in player.remaining" :key="`${player.id}-${opponent}`">{{ opponent }}</li>
                </ul>
                <p v-else class="opponent-card__empty">All matches completed.</p>
              </article>
            </div>
          </section>
        </section>

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

.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 17, 0.48);
  opacity: 1;
  transition: opacity 160ms ease;
  z-index: 20;
}

.sidebar {
  position: relative;
  background:
    radial-gradient(circle at 50% 0, rgba(46, 122, 206, 0.08), transparent 18%),
    linear-gradient(180deg, #20140f, #120b08 58%, #0d0908);
  border: 1px solid #76542e;
  min-height: 0;
  padding: 1.05rem 0.95rem 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  box-shadow:
    inset 0 0 0 1px rgba(220, 172, 91, 0.15),
    0 18px 34px rgba(0, 0, 0, 0.3);
}

.sidebar__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 0.6rem 0.55rem;
  border-bottom: 1px solid rgba(191, 143, 74, 0.26);
  margin-bottom: 0.4rem;
}

.sidebar::before,
.sidebar::after {
  content: '';
  position: absolute;
  left: 50%;
  width: 28px;
  height: 28px;
  margin-left: -14px;
  transform: rotate(45deg);
  border: 1px solid rgba(216, 170, 90, 0.7);
  box-shadow:
    inset 0 0 0 1px rgba(255, 225, 159, 0.18),
    0 0 12px rgba(255, 192, 95, 0.22);
}

.sidebar::before {
  top: -14px;
  background: linear-gradient(135deg, #4f87ac, #1c3240 72%, #140f0d);
}

.sidebar::after {
  bottom: -14px;
  background: linear-gradient(135deg, #ffcf80, #9f5b18 72%, #140f0d);
}

.sidebar__close {
  display: none;
}

.sidebar__nav {
  display: grid;
  gap: 0.5rem;
}

.sidebar__link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  justify-content: flex-start;
  background:
    linear-gradient(180deg, rgba(85, 55, 29, 0.18), rgba(26, 17, 12, 0.88)),
    #1a120d;
  color: #e7d1a7;
  border: 1px solid rgba(161, 118, 62, 0.5);
  border-radius: 12px;
  padding: 0.92rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.03rem;
  box-shadow:
    inset 0 1px 0 rgba(255, 225, 164, 0.1),
    0 0 0 1px rgba(42, 25, 17, 0.45);
}

.sidebar__link:hover,
.sidebar__link--active {
  color: #fff0c7;
  border-color: #ba8d4c;
  background:
    linear-gradient(180deg, rgba(161, 125, 54, 0.28), rgba(59, 39, 22, 0.94)),
    #1f1711;
  box-shadow:
    inset 0 1px 0 rgba(255, 225, 164, 0.16),
    0 0 16px rgba(209, 167, 71, 0.16);
}

.sidebar__header strong {
  color: #f1dcaf;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.12rem;
  letter-spacing: 0.02em;
}

.sidebar__icon {
  width: 20px;
  height: 20px;
  flex: none;
  padding: 3px;
  border-radius: 999px;
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

.feature-panel__header {
  padding: 1.6rem 1.55rem 1rem;
  border-bottom: 1px solid rgba(148, 105, 50, 0.35);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  background:
    radial-gradient(circle at 95% 15%, rgba(150, 115, 57, 0.12), transparent 15%),
    linear-gradient(180deg, rgba(255, 245, 213, 0.4), rgba(233, 196, 137, 0.18));
}

.feature-panel__header-copy {
  min-width: 0;
}

.feature-panel__header-side {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.9rem;
  min-width: 0;
}

.feature-panel__header p,
.feature-panel__loading,
.overview-summary p,
.overview-note,
.section-copy p,
.opponent-card__header p,
.opponent-card__empty {
  margin: 0.35rem 0 0;
  color: var(--text-soft);
}

.feature-panel__error {
  margin: 0;
  padding: 0.85rem 1.55rem 0;
  color: var(--danger);
}

.feature-panel__tournament-name {
  color: #3d2206;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.1rem;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.feature-panel__actions {
  display: flex;
  flex-wrap: nowrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.feature-panel__actions button {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

.feature-panel__loading {
  padding: 1.25rem;
}

.feature-panel__header strong {
  color: #2e1705;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2.15rem;
  line-height: 1;
}

.feature-panel__header p {
  color: #3c2711;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.95rem;
}

.feature-panel__header--opponents strong {
  text-transform: none;
}

.feature-panel__body,
.feature-section {
  min-height: 0;
  height: 100%;
  min-width: 0;
}

.feature-section {
  padding: 1rem 1.2rem 1.2rem;
  overflow: auto;
  scrollbar-gutter: stable;
}

.feature-section--overview {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 1rem;
}

.overview-top {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(280px, 0.8fr);
  gap: 1rem;
}

.panel {
  border: 1px solid #7e5524;
  background:
    linear-gradient(180deg, rgba(47, 29, 18, 0.95), rgba(28, 17, 11, 0.98)),
    #24170f;
  padding: 1rem;
  box-shadow:
    inset 0 0 0 1px rgba(214, 171, 92, 0.14),
    0 10px 18px rgba(0, 0, 0, 0.18);
}

.panel--empty {
  display: grid;
  gap: 0.35rem;
}

.panel--empty p {
  margin: 0;
  color: #e2ca9d;
}

.overview-summary {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.overview-summary__code {
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.overview-summary strong {
  color: #d7f18e;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.3rem;
}

.overview-summary p,
.overview-note {
  color: #f0dfbe;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
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

.opponent-card {
  position: relative;
  border: 1px solid #94652b;
  background:
    linear-gradient(180deg, rgba(58, 35, 22, 0.94), rgba(25, 17, 12, 0.98)),
    #24170f;
  padding: 1rem 1.05rem;
  display: grid;
  gap: 0.85rem;
  box-shadow:
    inset 0 0 0 1px rgba(214, 171, 92, 0.12),
    0 10px 18px rgba(0, 0, 0, 0.18);
}

.opponent-card::before,
.opponent-card::after {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  border: 1px solid rgba(220, 174, 97, 0.7);
  transform: rotate(45deg);
  background: linear-gradient(135deg, rgba(255, 222, 151, 0.8), rgba(138, 87, 34, 0.45));
}

.opponent-card::before {
  left: -6px;
  bottom: 34px;
}

.opponent-card::after {
  right: -6px;
  bottom: 34px;
}

.opponent-card__header {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  align-items: flex-start;
  padding-bottom: 0.85rem;
  border-bottom: 1px solid rgba(150, 108, 58, 0.52);
}

.opponent-card__header strong {
  color: #f3deb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
}

.opponent-card__header p {
  margin-top: 0.4rem;
  color: #e1c89d;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.92rem;
}

.opponent-card__count {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.02rem;
  font-weight: 700;
  white-space: nowrap;
}

.opponent-card__count--3 {
  color: #9edb78;
}

.opponent-card__count--3.opponent-card__count--alt {
  color: #b9edf1;
}

.opponent-card__count--2 {
  color: #f0a06f;
}

.opponent-card__count--1 {
  color: #efd7a7;
}

.opponent-card__count--0 {
  color: #bda98b;
}

.opponent-card__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0;
}

.opponent-card__list li {
  padding: 0.5rem 0;
  border-top: 1px solid rgba(128, 92, 50, 0.38);
  color: #f0ddb8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.96rem;
}

.opponent-card__list li:first-child {
  border-top: 0;
  padding-top: 0;
}

.opponent-card__empty {
  color: #d9c29d;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

@media (max-width: 959px) {
  .tournament-shell {
    grid-template-columns: minmax(0, 1fr);
  }

  .sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    width: min(280px, calc(100vw - 2rem));
    transform: translateX(-100%);
    transition: transform 180ms ease;
    z-index: 30;
  }

  .sidebar--open {
    transform: translateX(0);
  }

  .sidebar__close {
    display: inline-flex;
  }

  .feature-panel__header {
    flex-direction: column;
    align-items: stretch;
  }

  .feature-panel__header-side {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .feature-panel__actions {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .overview-top {
    grid-template-columns: 1fr;
  }

  .opponents-list {
    grid-template-columns: 1fr;
  }
}
</style>
