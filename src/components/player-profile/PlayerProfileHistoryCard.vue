<script setup lang="ts">
import type { PlayerProfileTournament } from '@/domain/models'
import PlayerProfileStatusBadge from '@/components/player-profile/PlayerProfileStatusBadge.vue'
import {
  formatProfileDate,
  getPlacementHeading,
  getPlacementLabel
} from '@/components/player-profile/playerProfileFormatters'

defineProps<{
  entry: PlayerProfileTournament
}>()
</script>

<template>
  <article class="history-card">
    <div class="history-card__top">
      <div>
        <PlayerProfileStatusBadge :status="entry.status" />
        <h4>{{ entry.tournamentName }}</h4>
      </div>
      <div class="history-card__placement">
        <span>{{ getPlacementHeading(entry.status) }}</span>
        <strong>{{ getPlacementLabel(entry.placement) }}</strong>
      </div>
    </div>

    <div class="history-card__meta">
      <span>{{ formatProfileDate(entry.createdAt) }}</span>
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
        <strong>{{ formatProfileDate(entry.joinedAt) }}</strong>
      </div>
    </div>
  </article>
</template>

<style scoped>
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

.history-card h4 {
  margin: 0;
  color: #f6e3b8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.history-card__placement {
  text-align: right;
}

.history-card__placement span,
.history-card__stats span,
.history-card__meta {
  color: #5d3a17;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
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
  .history-card__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .history-card__top {
    flex-direction: column;
  }

  .history-card__placement {
    width: 100%;
    text-align: left;
  }

  .history-card__stats {
    grid-template-columns: 1fr;
  }
}
</style>
