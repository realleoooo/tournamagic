<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const tournament = computed(() => store.tournament)

if (!tournament.value) {
  router.push('/')
}

const onReset = () => {
  store.resetTournament()
  router.push('/')
}
</script>

<template>
  <div v-if="tournament" class="grid two">
    <div class="grid">
      <section class="card">
        <h2>{{ tournament.name }}</h2>
        <p>{{ tournament.players.length }} players</p>
        <button class="warn" type="button" @click="onReset">Reset Tournament</button>
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
</template>
