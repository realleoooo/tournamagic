<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import InviteShareSection from '@/components/tournaments/InviteShareSection.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const tournament = computed(() => store.tournament)
const canStartTournament = computed(
  () => tournament.value?.status === 'setup' && tournament.value.players.length >= 2
)

onMounted(async () => {
  if (!tournament.value) {
    await store.bootstrap()
  }

  if (!store.tournament) {
    router.replace('/')
  }
})

const goOverview = () => {
  store.leaveTournament()
  router.replace('/')
}

const onReset = async () => {
  await store.resetTournament()
  router.replace('/')
}

const onStart = async () => {
  const started = await store.startTournament()
  if (started) {
    return
  }
}

const onLeave = async () => {
  const updated = await store.leaveJoinedTournament()
  if (updated) {
    router.replace('/')
  }
}
</script>

<template>
  <div class="grid">
    <section v-if="store.error" class="card" style="border-color: var(--danger)">
      <strong>API error:</strong> {{ store.error }}
    </section>

    <section v-if="store.loading" class="card">Loading…</section>

    <div v-if="tournament" class="grid two">
      <div class="grid">
        <section class="card">
          <h2>{{ tournament.name }}</h2>
          <p>{{ tournament.players.length }} joined players · status: {{ tournament.status }}</p>
          <div style="display:flex; gap:0.5rem; flex-wrap:wrap; margin-bottom: 0.75rem;">
            <button type="button" class="secondary" @click="goOverview">Back to overview</button>
            <button class="warn" type="button" @click="onReset">Reset Tournament</button>
            <button v-if="canStartTournament" type="button" @click="onStart">Start Tournament</button>
            <button
              v-if="tournament.currentUserJoined && tournament.status === 'setup'"
              type="button"
              class="secondary"
              @click="onLeave"
            >
              Leave Tournament
            </button>
          </div>
          <p v-if="tournament.status === 'setup'" class="muted">
            Invite players with the QR code. Once at least 2 players have joined, start the tournament to generate matches.
          </p>
          <p v-else-if="!tournament.currentUserJoined" class="muted">
            You are viewing this tournament, but you are not currently part of its player roster.
          </p>
        </section>

        <InviteShareSection v-if="tournament.status === 'setup'" :tournament="tournament" />
        <ProgressPanel v-if="tournament.matches.length > 0" :completed="store.completion.completed" :total="store.completion.total" />
        <MatchList
          :matches="tournament.matches"
          :players="tournament.players"
          :resolve-name="store.resolveName"
          @submit="store.submitResult"
          @clear="store.clearResult"
        />
      </div>
      <div class="grid">
        <section class="card">
          <h2>Joined Players</h2>
          <p class="muted">Everyone who joined through the invite is part of the tournament roster.</p>
          <ul v-if="tournament.participants.length > 0" class="participant-list">
            <li v-for="participant in tournament.participants" :key="participant.email" class="participant-list__item">
              <div>
                <strong>{{ participant.name }}</strong>
                <p>{{ participant.email }}</p>
              </div>
              <span>{{ new Date(participant.joinedAt).toLocaleDateString() }}</span>
            </li>
          </ul>
          <p v-else class="muted">No players have joined yet.</p>
        </section>

        <LeaderboardTable v-if="tournament.matches.length > 0" :standings="store.standings" />
        <section v-if="tournament.matches.length > 0" class="card">
          <h2>Remaining Opponents</h2>
          <ul>
            <li v-for="player in tournament.players" :key="player.id">
              <strong>{{ player.name }}:</strong>
              {{ store.remainingOpponents(player.id).join(', ') || 'None' }}
            </li>
          </ul>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.muted {
  color: var(--text-soft);
}

.participant-list {
  list-style: none;
  padding: 0;
  margin: 1rem 0 0;
  display: grid;
  gap: 0.75rem;
}

.participant-list__item {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  padding: 0.85rem;
  border-radius: 12px;
  background: color-mix(in srgb, var(--bg-main) 45%, transparent);
}

.participant-list__item p {
  margin: 0.25rem 0 0;
  color: var(--text-soft);
}
</style>
