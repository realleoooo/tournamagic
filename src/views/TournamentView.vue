<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const tournament = computed(() => store.tournament)

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
          <p>{{ tournament.players.length }} players</p>
          <div style="display:flex; gap:0.5rem; flex-wrap:wrap;">
            <button type="button" class="secondary" @click="goOverview">Back to overview</button>
            <button class="warn" type="button" @click="onReset">Reset Tournament</button>
          </div>
        </section>
        <ProgressPanel :completed="store.completion.completed" :total="store.completion.total" />
        <MatchList
          :matches="tournament.matches"
          :resolve-name="store.resolveName"
          @submit="store.submitResult"
          @clear="store.clearResult"
        />
      </div>
      <div class="grid">
        <LeaderboardTable :standings="store.standings" />
        <section class="card">
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
