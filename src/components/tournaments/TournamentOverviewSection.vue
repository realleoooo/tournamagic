<script setup lang="ts">
import type { Tournament } from '@/domain/models'
import MatchList from '@/components/matches/MatchList.vue'
import ProgressPanel from '@/components/shared/ProgressPanel.vue'

defineProps<{
  tournament: Tournament
  completedMatches: number
  totalMatches: number
  resolveName: (id: string) => string
  resolveProfileEmail: (id: string) => string | null | undefined
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
}>()

const forwardSubmit = (matchId: string, winsA: number, winsB: number) => {
  emit('submit', matchId, winsA, winsB)
}
</script>

<template>
  <section class="feature-section feature-section--overview">
    <div class="overview-top">
      <ProgressPanel v-if="tournament.matches.length > 0" :completed="completedMatches" :total="totalMatches" />

      <section class="panel">
        <div class="overview-summary">
          <div>
            <strong>{{ tournament.name }}</strong>
            <p>{{ tournament.status }} tournament with {{ tournament.players.length }} registered players</p>
          </div>
          <span class="overview-summary__code">Join code: {{ tournament.joinCode }}</span>
        </div>
        <p v-if="tournament.status === 'setup'" class="overview-note">
          Every player slot must be claimed before the tournament can start.
        </p>
      </section>
    </div>

    <MatchList
      :matches="tournament.matches"
      :players="tournament.players"
      :resolve-name="resolveName"
      :resolve-profile-email="resolveProfileEmail"
      @submit="forwardSubmit"
      @clear="emit('clear', $event)"
    />
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

.feature-section--overview {
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 1rem;
}

.overview-top {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(280px, 0.8fr);
  gap: 1rem;
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

.overview-summary {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
}

.overview-summary__code {
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.overview-summary strong {
  color: #d7f18e;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.3rem;
}

.overview-summary p,
.overview-note {
  margin: 0.35rem 0 0;
  color: #f0dfbe;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

@media (max-width: 959px) {
  .overview-top {
    grid-template-columns: 1fr;
  }
}
</style>
