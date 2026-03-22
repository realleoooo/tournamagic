<script setup lang="ts">
import type { TournamentSection } from '@/composables/useTournamentShell'

defineProps<{
  activeSection: TournamentSection
  activeSectionLabel: string
  activeSectionDescription: string
  tournamentName?: string
  canStartTournament: boolean
  currentUserJoined: boolean
}>()

const emit = defineEmits<{
  start: []
  reset: []
  leave: []
}>()
</script>

<template>
  <header :class="['feature-panel__header', `feature-panel__header--${activeSection}`]">
    <div class="feature-panel__header-copy">
      <strong>{{ activeSectionLabel }}</strong>
      <p v-if="activeSectionDescription">{{ activeSectionDescription }}</p>
    </div>

    <div v-if="tournamentName" class="feature-panel__header-side">
      <strong class="feature-panel__tournament-name">{{ tournamentName }}</strong>

      <div class="feature-panel__actions">
        <button v-if="canStartTournament" type="button" @click="emit('start')">Start tournament</button>
        <button type="button" class="warn" @click="emit('reset')">Reset tournament</button>
        <button v-if="currentUserJoined" type="button" class="secondary" @click="emit('leave')">
          Leave tournament
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.feature-panel__header {
  padding: 1.6rem 1.55rem 1rem;
  border-bottom: 1px solid rgba(148, 105, 50, 0.35);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  background:
    radial-gradient(circle at 95% 15%, rgba(150, 115, 57, 0.12), transparent 15%),
    linear-gradient(180deg, rgba(255, 245, 213, 0.4), rgba(233, 196, 137, 0.18));
}

.feature-panel__header-copy {
  min-width: 0;
}

.feature-panel__header-side {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.9rem;
  min-width: 0;
}

.feature-panel__header strong {
  color: #2e1705;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2.15rem;
  line-height: 1;
}

.feature-panel__header p {
  margin: 0.35rem 0 0;
  color: #3c2711;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.95rem;
}

.feature-panel__header--opponents strong {
  text-transform: none;
}

.feature-panel__tournament-name {
  color: #3d2206;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.1rem;
  line-height: 1.2;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.feature-panel__actions {
  display: flex;
  flex-wrap: nowrap;
  justify-content: flex-end;
  gap: 0.75rem;
}

.feature-panel__actions button {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

@media (max-width: 959px) {
  .feature-panel__header {
    flex-direction: column;
    align-items: stretch;
  }

  .feature-panel__header-side {
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .feature-panel__actions {
    flex-wrap: wrap;
    justify-content: flex-start;
  }
}
</style>
