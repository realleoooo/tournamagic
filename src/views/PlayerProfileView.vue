<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { tournamentApi } from '@/api/tournamentApi'
import type { PlayerProfile } from '@/domain/models'
import { useTournamentStore } from '@/stores/tournament'

const route = useRoute()
const router = useRouter()
const tournamentStore = useTournamentStore()

const profile = ref<PlayerProfile | null>(null)
const loading = ref(false)
const error = ref<string | null>(null)

const routeEmail = computed(() => (typeof route.params.email === 'string' ? route.params.email : ''))
const hasTournamentContext = computed(() => Boolean(tournamentStore.tournament))

const dateFormatter = new Intl.DateTimeFormat('en', {
  dateStyle: 'medium'
})

const loadProfile = async () => {
  if (!routeEmail.value) {
    error.value = 'No player email was provided.'
    profile.value = null
    return
  }

  loading.value = true
  error.value = null

  try {
    profile.value = await tournamentApi.fetchPlayerProfile(routeEmail.value)
  } catch (err) {
    profile.value = null
    error.value = err instanceof Error ? err.message : 'Unable to load this profile right now.'
  } finally {
    loading.value = false
  }
}

const matchWinRate = computed(() => {
  if (!profile.value) {
    return 0
  }

  const total = profile.value.stats.totalMatchWins + profile.value.stats.totalMatchLosses
  return total === 0 ? 0 : Math.round((profile.value.stats.totalMatchWins / total) * 100)
})

const podiumCount = computed(() => {
  if (!profile.value) {
    return 0
  }

  return (
    profile.value.stats.firstPlaces +
    profile.value.stats.secondPlaces +
    profile.value.stats.thirdPlaces
  )
})

const formatDate = (value: string) => dateFormatter.format(new Date(value))

const placementLabel = (placement?: number | null) => {
  if (!placement) {
    return 'Still in progress'
  }

  if (placement === 1) return '1st place'
  if (placement === 2) return '2nd place'
  if (placement === 3) return '3rd place'

  const remainder = placement % 10
  const suffix =
    remainder === 1 && placement !== 11
      ? 'st'
      : remainder === 2 && placement !== 12
        ? 'nd'
        : remainder === 3 && placement !== 13
          ? 'rd'
          : 'th'

  return `${placement}${suffix} place`
}

const openOverview = () => {
  router.push({ name: 'setup' })
}

const openTournament = () => {
  router.push({ name: 'tournament' })
}

watch(routeEmail, loadProfile)
onMounted(loadProfile)
</script>

<template>
  <div class="profile-page">
    <header class="profile-hero">
      <div class="profile-hero__copy">
        <span class="profile-hero__eyebrow">{{ profile?.currentUser ? 'Your profile' : 'Player profile' }}</span>
        <h2>{{ profile?.name ?? routeEmail }}</h2>
        <p>{{ profile?.email ?? routeEmail }}</p>
      </div>

      <div class="profile-hero__actions">
        <button v-if="hasTournamentContext" type="button" class="secondary" @click="openTournament">
          Back to tournament
        </button>
        <button type="button" @click="openOverview">Tournament hall</button>
      </div>
    </header>

    <section v-if="error" class="profile-message profile-message--error">
      <strong>Profile unavailable</strong>
      <p>{{ error }}</p>
    </section>

    <section v-else-if="loading" class="profile-message">
      <strong>Loading profile</strong>
      <p>Gathering tournament history and player stats.</p>
    </section>

    <template v-else-if="profile">
      <section class="profile-stats">
        <article class="profile-stat">
          <span class="profile-stat__label">Tournaments</span>
          <strong>{{ profile.stats.tournamentsPlayed }}</strong>
          <p>{{ profile.stats.completedTournaments }} completed, {{ profile.stats.inProgressTournaments }} active</p>
        </article>

        <article class="profile-stat">
          <span class="profile-stat__label">Match record</span>
          <strong>{{ profile.stats.totalMatchWins }} - {{ profile.stats.totalMatchLosses }}</strong>
          <p>{{ matchWinRate }}% win rate across reported matches</p>
        </article>

        <article class="profile-stat">
          <span class="profile-stat__label">Podium finishes</span>
          <strong>{{ podiumCount }}</strong>
          <p>
            {{ profile.stats.firstPlaces }} first, {{ profile.stats.secondPlaces }} second, {{ profile.stats.thirdPlaces }} third
          </p>
        </article>

        <article class="profile-stat">
          <span class="profile-stat__label">Game record</span>
          <strong>{{ profile.stats.totalGameWins }} - {{ profile.stats.totalGameLosses }}</strong>
          <p>Best-of-three game wins and losses from completed results</p>
        </article>
      </section>

      <section class="profile-history">
        <div class="profile-history__header">
          <div>
            <h3>Tournament history</h3>
            <p>Every tournament this player has joined through TournaMagic.</p>
          </div>
        </div>

        <div v-if="profile.tournaments.length === 0" class="profile-history__empty">
          <strong>No tournaments yet</strong>
          <p>This profile will start filling up as soon as the player joins an event.</p>
        </div>

        <div v-else class="profile-history__list">
          <article v-for="entry in profile.tournaments" :key="entry.tournamentId" class="history-card">
            <div class="history-card__top">
              <div>
                <span :class="['history-card__status', `history-card__status--${entry.status}`]">
                  {{ entry.status }}
                </span>
                <h4>{{ entry.tournamentName }}</h4>
              </div>
              <div class="history-card__placement">
                <span>{{ entry.status === 'complete' ? 'Final placing' : 'Current standing' }}</span>
                <strong>{{ placementLabel(entry.placement) }}</strong>
              </div>
            </div>

            <div class="history-card__meta">
              <span>{{ formatDate(entry.createdAt) }}</span>
              <span>{{ entry.playerCount }} players</span>
              <span>{{ entry.completedMatches }} / {{ entry.totalMatches }} matches reported</span>
            </div>

            <div class="history-card__stats">
              <div>
                <span>Player slot</span>
                <strong>{{ entry.playerName }}</strong>
              </div>
              <div>
                <span>Match record</span>
                <strong>{{ entry.matchWins }} - {{ entry.matchLosses }}</strong>
              </div>
              <div>
                <span>Game record</span>
                <strong>{{ entry.gameWins }} - {{ entry.gameLosses }}</strong>
              </div>
              <div>
                <span>Joined</span>
                <strong>{{ formatDate(entry.joinedAt) }}</strong>
              </div>
            </div>
          </article>
        </div>
      </section>
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

.profile-hero,
.profile-stats,
.profile-history,
.profile-message {
  border: 1px solid rgba(201, 153, 82, 0.52);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 34px rgba(0, 0, 0, 0.24);
}

.profile-hero,
.profile-history,
.profile-message {
  background:
    linear-gradient(180deg, rgba(85, 53, 33, 0.28), rgba(29, 18, 13, 0.24)),
    rgba(57, 34, 22, 0.28);
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  padding: 1.4rem 1.5rem;
}

.profile-hero__copy {
  display: grid;
  gap: 0.45rem;
}

.profile-hero__eyebrow {
  display: inline-flex;
  width: fit-content;
  padding: 0.34rem 0.75rem;
  border-radius: 999px;
  border: 1px solid rgba(238, 202, 137, 0.28);
  background: rgba(34, 21, 15, 0.38);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.75rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-hero h2,
.profile-history h3,
.history-card h4 {
  margin: 0;
  color: #f6e3b8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-hero h2 {
  font-size: clamp(2rem, 4vw, 2.8rem);
  line-height: 1;
}

.profile-hero p,
.profile-history__header p,
.profile-message p {
  margin: 0;
  color: #e2c89c;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-hero__actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 1px;
  background: rgba(191, 143, 74, 0.26);
}

.profile-stat {
  padding: 1.15rem 1.1rem;
  background:
    linear-gradient(180deg, rgba(246, 224, 181, 0.95), rgba(221, 189, 132, 0.95)),
    #e2bf84;
}

.profile-stat__label,
.history-card__placement span,
.history-card__stats span,
.history-card__meta,
.history-card__status {
  color: #5d3a17;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-stat strong {
  display: block;
  margin-top: 0.35rem;
  color: #2c1606;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
  line-height: 1;
}

.profile-stat p {
  margin: 0.45rem 0 0;
  color: #4a3016;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-history,
.profile-message {
  padding: 1.35rem 1.4rem;
}

.profile-message {
  display: grid;
  gap: 0.45rem;
}

.profile-message strong,
.profile-history__empty strong {
  color: #f6e2b4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.2rem;
}

.profile-message--error strong {
  color: #ffcfbf;
}

.profile-history__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
}

.profile-history__empty {
  margin-top: 1rem;
  padding: 1rem 1.05rem;
  border: 1px solid rgba(182, 136, 67, 0.46);
  background: rgba(30, 19, 13, 0.34);
}

.profile-history__empty p {
  margin: 0.45rem 0 0;
  color: #ddc49b;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-history__list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
}

.history-card {
  border: 1px solid rgba(188, 136, 67, 0.54);
  background:
    linear-gradient(180deg, rgba(245, 223, 179, 0.96), rgba(222, 188, 130, 0.96)),
    #e2bf84;
  padding: 1rem 1.05rem;
  box-shadow:
    inset 0 0 0 1px rgba(255, 240, 206, 0.18),
    0 12px 20px rgba(0, 0, 0, 0.15);
}

.history-card__top {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
}

.history-card__status {
  display: inline-flex;
  padding: 0.24rem 0.55rem;
  border-radius: 999px;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-size: 0.73rem;
  border: 1px solid rgba(122, 82, 37, 0.25);
  background: rgba(255, 248, 225, 0.48);
}

.history-card__status--complete {
  color: #2f651f;
}

.history-card__status--active {
  color: #7d4b0d;
}

.history-card__status--setup {
  color: #5f3e19;
}

.history-card__placement {
  text-align: right;
}

.history-card__placement strong {
  display: block;
  margin-top: 0.2rem;
  color: #2d1705;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.15rem;
}

.history-card__meta {
  display: flex;
  gap: 0.85rem;
  flex-wrap: wrap;
  margin-top: 0.75rem;
  font-size: 0.92rem;
}

.history-card__stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 0.9rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(136, 93, 42, 0.34);
}

.history-card__stats div {
  display: grid;
  gap: 0.2rem;
}

.history-card__stats strong {
  color: #2d1705;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

@media (max-width: 980px) {
  .profile-stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .history-card__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .profile-page {
    padding-bottom: 1rem;
  }

  .profile-hero,
  .profile-history,
  .profile-message {
    padding: 1.1rem;
  }

  .profile-hero,
  .history-card__top {
    flex-direction: column;
  }

  .profile-hero__actions,
  .history-card__placement {
    width: 100%;
    justify-content: flex-start;
    text-align: left;
  }

  .profile-stats,
  .history-card__stats {
    grid-template-columns: 1fr;
  }
}
</style>
