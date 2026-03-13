<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import MatchList from '@/components/matches/MatchList.vue'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const router = useRouter()

const tournament = computed(() => store.tournament)
const selectedRound = ref<number | undefined>(undefined)

const roundNumbers = computed(() => {
  if (!tournament.value) return []
  return [...new Set(tournament.value.matches.map((match) => match.roundNumber))].sort((a, b) => a - b)
})

const selectedRoundMatches = computed(() => {
  if (!tournament.value) return []
  if (!selectedRound.value) return tournament.value.matches
  return tournament.value.matches.filter((match) => match.roundNumber === selectedRound.value)
})

watch(
  roundNumbers,
  (rounds) => {
    if (rounds.length === 0) {
      selectedRound.value = undefined
      return
    }
    if (!selectedRound.value || !rounds.includes(selectedRound.value)) {
      selectedRound.value = rounds[0]
    }
  },
  { immediate: true }
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

        <section class="card">
          <h2>Rounds</h2>
          <p style="color:var(--text-soft)">Select a round to see who plays each other.</p>
          <div style="display:flex; gap:0.5rem; flex-wrap:wrap;">
            <button
              v-for="round in roundNumbers"
              :key="round"
              type="button"
              :class="selectedRound === round ? '' : 'secondary'"
              @click="selectedRound = round"
            >
              Round {{ round }}
            </button>
          </div>
        </section>

        <ProgressPanel :completed="store.completion.completed" :total="store.completion.total" />
        <MatchList
          :matches="selectedRoundMatches"
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
