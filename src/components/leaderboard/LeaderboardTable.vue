<script setup lang="ts">
import type { StandingRow } from '@/domain/models'

defineProps<{
  standings: StandingRow[]
}>()
</script>

<template>
  <section class="card">
    <h2>Leaderboard</h2>
    <table style="width:100%; border-collapse:collapse;">
      <thead>
        <tr>
          <th>#</th>
          <th style="text-align:left">Player</th>
          <th>W-L</th>
          <th>GW-GL</th>
          <th>MW%</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, index) in standings" :key="row.playerId">
          <td>{{ index + 1 }}</td>
          <td>{{ row.playerName }}</td>
          <td>{{ row.matchWins }}-{{ row.matchLosses }}</td>
          <td>{{ row.gameWins }}-{{ row.gameLosses }}</td>
          <td>{{ (row.matchWinPct * 100).toFixed(0) }}%</td>
        </tr>
      </tbody>
    </table>
  </section>
</template>
