<script setup lang="ts">
import type { StandingRow } from '@/domain/models'

defineProps<{
  standings: StandingRow[]
}>()

const iconClasses = ['leader-icon--bone', 'leader-icon--leaf', 'leader-icon--sun', 'leader-icon--ember', 'leader-icon--water']
</script>

<template>
  <section class="leaderboard-panel">
    <div class="leaderboard-table-wrap">
      <table class="leaderboard-table">
        <thead>
          <tr>
            <th>#</th>
            <th>Player</th>
            <th>Record</th>
            <th>Games</th>
            <th>MW%</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in standings" :key="row.playerId" :class="{ 'leaderboard-table__row--lead': index === 0 }">
            <td class="leaderboard-rank">{{ index + 1 }}</td>
            <td>
              <div class="leaderboard-player">
                <div :class="['leader-icon', iconClasses[index % iconClasses.length]]">
                  <span>{{ row.playerName.slice(0, 1).toUpperCase() }}</span>
                </div>
                <div class="leaderboard-player__copy">
                  <strong>{{ row.playerName }}</strong>
                  <span>{{ row.matchWins + row.matchLosses }} matches played</span>
                </div>
              </div>
            </td>
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
.leaderboard-panel {
  position: relative;
  border: 1px solid #875923;
  background:
    linear-gradient(180deg, rgba(71, 43, 24, 0.96), rgba(31, 18, 12, 0.98)),
    #26170f;
  padding: 0.95rem;
  min-height: 100%;
  box-shadow:
    inset 0 0 0 1px rgba(222, 176, 94, 0.14),
    0 12px 22px rgba(0, 0, 0, 0.18);
}

.leaderboard-table-wrap {
  min-height: 0;
  overflow: auto;
  scrollbar-gutter: stable;
  border: 1px solid #855821;
  background:
    linear-gradient(180deg, rgba(44, 28, 18, 0.92), rgba(27, 18, 12, 0.96)),
    #24170f;
  box-shadow: inset 0 0 0 1px rgba(214, 171, 92, 0.12);
}

.leaderboard-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.leaderboard-table th,
.leaderboard-table td {
  padding: 1rem 0.95rem;
  border-bottom: 1px solid rgba(145, 102, 52, 0.42);
  text-align: left;
  vertical-align: middle;
  color: #f0dab0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.leaderboard-table thead th {
  color: #ead3a6;
  font-size: 0.95rem;
  font-weight: 700;
  background:
    linear-gradient(180deg, rgba(79, 51, 29, 0.96), rgba(43, 28, 18, 0.98)),
    #352115;
}

.leaderboard-table__row--lead td {
  background:
    linear-gradient(180deg, rgba(121, 78, 37, 0.86), rgba(84, 52, 28, 0.9)),
    #6d4726;
  color: #fff0c4;
}

.leaderboard-rank,
.leaderboard-table td:nth-child(3),
.leaderboard-table td:nth-child(4),
.leaderboard-table td:nth-child(5) {
  font-size: 1.1rem;
  font-weight: 700;
}

.leaderboard-table th:first-child,
.leaderboard-table td:first-child {
  width: 86px;
}

.leaderboard-table th:nth-child(3),
.leaderboard-table td:nth-child(3),
.leaderboard-table th:nth-child(4),
.leaderboard-table td:nth-child(4),
.leaderboard-table th:nth-child(5),
.leaderboard-table td:nth-child(5) {
  width: 15%;
}

.leaderboard-player {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  min-width: 0;
}

.leaderboard-player__copy {
  display: grid;
  gap: 0.15rem;
}

.leaderboard-player strong {
  color: inherit;
  font-size: 1.05rem;
}

.leaderboard-player span {
  color: #dcc297;
  font-size: 0.9rem;
}

.leaderboard-table__row--lead .leaderboard-player span {
  color: #f1dbb3;
}

.leader-icon {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(225, 183, 102, 0.82);
  color: #fff5d6;
  font-size: 0.95rem;
  font-weight: 700;
  box-shadow:
    inset 0 1px 0 rgba(255, 231, 180, 0.18),
    0 0 0 1px rgba(57, 34, 18, 0.46);
}

.leader-icon--bone {
  background: radial-gradient(circle at 35% 35%, #d9c3a1, #6e543c 70%, #281b12);
}

.leader-icon--leaf {
  background: radial-gradient(circle at 35% 35%, #9bd85f, #4a7527 70%, #18240f);
}

.leader-icon--sun {
  background: radial-gradient(circle at 35% 35%, #f3d488, #9a6a20 70%, #29180a);
}

.leader-icon--ember {
  background: radial-gradient(circle at 35% 35%, #ffb36a, #9a3c1d 70%, #2a120d);
}

.leader-icon--water {
  background: radial-gradient(circle at 35% 35%, #76c2ff, #255ca4 70%, #111b29);
}

@media (max-width: 920px) {
  .leaderboard-table {
    min-width: 760px;
  }
}
</style>
