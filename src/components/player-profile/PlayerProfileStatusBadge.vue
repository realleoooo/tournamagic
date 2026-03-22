<script setup lang="ts">
import type { Tournament } from '@/domain/models'

defineProps<{
  status: Tournament['status']
}>()
</script>

<template>
  <span :class="['history-card__status', `history-card__status--${status}`]">
    {{ status }}
  </span>
</template>

<style scoped>
.history-card__status {
  display: inline-flex;
  padding: 0.24rem 0.55rem;
  border-radius: 999px;
  color: #5d3a17;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-size: 0.73rem;
  border: 1px solid rgba(122, 82, 37, 0.25);
  background: rgba(255, 248, 225, 0.48);
}

.history-card__status--complete {
  color: #2f651f;
}

.history-card__status--active {
  color: #7d4b0d;
}

.history-card__status--setup {
  color: #5f3e19;
}
</style>
