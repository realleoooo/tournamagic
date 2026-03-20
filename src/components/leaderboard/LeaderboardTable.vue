<script setup lang="ts">
import type { StandingRow } from '@/domain/models'

defineProps<{
  standings: StandingRow[]
}>()
</script>

<template>
  <section class="card leaderboard-panel">
    <div class="section-heading">
      <h2>Leaderboard</h2>
      <p>Standings update as soon as match results are saved.</p>
    </div>

    <div class="leaderboard-table-wrap">
      <table class="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Player</th>
            <th>Match</th>
            <th>Games</th>
            <th>MW%</th>
            <th>GW%</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in standings" :key="row.playerId">
            <td class="leaderboard-table__rank">{{ index + 1 }}</td>
            <td>
              <div class="leaderboard-table__player">
                <strong>{{ row.playerName }}</strong>
                <span>{{ row.gameDiff >= 0 ? '+' : '' }}{{ row.gameDiff }} game diff</span>
              </div>
            </td>
            <td>{{ row.matchWins }}-{{ row.matchLosses }}</td>
            <td>{{ row.gameWins }}-{{ row.gameLosses }}</td>
            <td>{{ (row.matchWinPct * 100).toFixed(0) }}%</td>
            <td>{{ (row.gameWinPct * 100).toFixed(0) }}%</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.leaderboard-panel {
  display: grid;
  gap: 1rem;
}

.leaderboard-panel.section-scroll-panel {
  overflow: hidden;
  grid-template-rows: auto minmax(0, 1fr);
}

.section-heading h2,
.section-heading p {
  margin: 0;
}

.section-heading p {
  margin-top: 0.3rem;
  color: var(--text-soft);
}

.leaderboard-table-wrap {
  min-height: 0;
  overflow: auto;
}

.leaderboard-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 640px;
}

.leaderboard-table th,
.leaderboard-table td {
  padding: 0.85rem 0.75rem;
  border-top: 1px solid var(--border-subtle);
  text-align: left;
  vertical-align: middle;
}

.leaderboard-table thead th {
  border-top: 0;
  color: var(--text-soft);
  font-size: 0.92rem;
  font-weight: 600;
}

.leaderboard-table tbody tr:hover {
  background: color-mix(in srgb, var(--bg-muted) 72%, transparent);
}

.leaderboard-table__rank {
  width: 64px;
  font-weight: 700;
  color: var(--accent-gold);
}

.leaderboard-table__player {
  display: grid;
  gap: 0.2rem;
}

.leaderboard-table__player span {
  color: var(--text-soft);
  font-size: 0.92rem;
}
</style>
