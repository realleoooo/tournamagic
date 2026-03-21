<script setup lang="ts">
import type { StandingRow } from '@/domain/models'

defineProps<{
  standings: StandingRow[]
}>()

const iconClasses = ['leader-icon--bone', 'leader-icon--leaf', 'leader-icon--sun', 'leader-icon--ember', 'leader-icon--water']
</script>

<template>
  <section class="card">
    <h2>Leaderboard</h2>
    <div class="leaderboard-table">
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th class="leaderboard-table__player">Player</th>
            <th>W-L</th>
            <th>GW-GL</th>
            <th>MW%</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in standings" :key="row.playerId">
            <td>{{ index + 1 }}</td>
            <td class="leaderboard-table__player">{{ row.playerName }}</td>
            <td>{{ row.matchWins }}-{{ row.matchLosses }}</td>
            <td>{{ row.gameWins }}-{{ row.gameLosses }}</td>
            <td>{{ (row.matchWinPct * 100).toFixed(0) }}%</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.leaderboard-table {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 420px;
  border-collapse: collapse;
}

th,
td {
  padding: 0.55rem 0.35rem;
  text-align: center;
}

.leaderboard-table__player {
  text-align: left;
}
</style>
