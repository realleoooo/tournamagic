<script setup lang="ts">
import type { TournamentSummary } from '@/api/tournamentApi'
import { formatTournamentSummary } from '@/components/setup/setupViewFormatters'

defineProps<{
  tournament: TournamentSummary
}>()

const emit = defineEmits<{
  open: [id: string]
  delete: [id: string]
}>()
</script>

<template>
  <article class="setup-tournament">
    <div class="setup-tournament__copy">
      <strong>{{ tournament.name }}</strong>
      <p>{{ formatTournamentSummary(tournament) }}</p>
    </div>

    <div class="setup-tournament__actions">
      <button type="button" class="setup-button setup-button--open" @click="emit('open', tournament.id)">Open</button>
      <button type="button" class="setup-button setup-button--delete" @click="emit('delete', tournament.id)">
        Delete
      </button>
    </div>
  </article>
</template>

<style scoped>
.setup-tournament {
  position: relative;
  border: 1px solid rgba(179, 126, 63, 0.88);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(94, 58, 36, 0.22), rgba(27, 17, 12, 0.18)),
    rgba(56, 33, 22, 0.28);
  padding: 1.2rem 1.15rem;
  box-shadow:
    inset 0 0 0 1px rgba(234, 187, 104, 0.13),
    0 10px 18px rgba(0, 0, 0, 0.16);
}

.setup-tournament::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 1px solid rgba(203, 155, 84, 0.16);
  border-radius: 14px;
  pointer-events: none;
}

.setup-tournament__copy strong {
  display: block;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.1rem;
}

.setup-tournament__copy p {
  margin: 0.35rem 0 0;
  color: #e2c79a;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.97rem;
}

.setup-tournament__actions {
  display: flex;
  gap: 0.7rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}

.setup-button {
  min-width: 92px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.setup-button--open {
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
}

.setup-button--open:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

.setup-button--delete {
  background:
    linear-gradient(180deg, rgba(167, 78, 57, 0.94), rgba(101, 39, 30, 0.98)),
    #7c3429;
  border-color: rgba(211, 154, 101, 0.68);
  color: #f6dfca;
}

.setup-button--delete:hover {
  background:
    linear-gradient(180deg, rgba(186, 87, 63, 0.96), rgba(114, 46, 35, 0.98)),
    #8a3a2e;
}
</style>
