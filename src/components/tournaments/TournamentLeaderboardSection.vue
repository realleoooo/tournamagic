<script setup lang="ts">
import type { StandingRow } from '@/domain/models'
import LeaderboardTable from '@/components/leaderboard/LeaderboardTable.vue'

defineProps<{
  standings: StandingRow[]
  hasMatches: boolean
  resolveProfileEmail: (playerId: string) => string | null | undefined
}>()
</script>

<template>
  <section class="feature-section">
    <LeaderboardTable v-if="hasMatches" :standings="standings" :resolve-profile-email="resolveProfileEmail" />

    <section v-else class="panel panel--empty">
      <strong>Leaderboard unavailable</strong>
      <p>Start the tournament to generate standings.</p>
    </section>
  </section>
</template>

<style scoped>
.feature-section {
  min-height: 0;
  height: 100%;
  min-width: 0;
  padding: 1rem 1.2rem 1.2rem;
  overflow: auto;
  scrollbar-gutter: stable;
}

.panel {
  border: 1px solid #7e5524;
  background:
    linear-gradient(180deg, rgba(47, 29, 18, 0.95), rgba(28, 17, 11, 0.98)),
    #24170f;
  padding: 1rem;
  box-shadow:
    inset 0 0 0 1px rgba(214, 171, 92, 0.14),
    0 10px 18px rgba(0, 0, 0, 0.18);
}

.panel--empty {
  display: grid;
  gap: 0.35rem;
}

.panel--empty p {
  margin: 0;
  color: #e2ca9d;
}
</style>
