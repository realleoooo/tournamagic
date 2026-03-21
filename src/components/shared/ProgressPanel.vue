<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  completed: number
  total: number
}>()

const completionText = computed(() => {
  if (props.total === 0) {
    return 'Matches will appear here once the tournament starts.'
  }

  return `${props.completed} of ${props.total} matches complete`
})
</script>

<template>
  <section class="card progress-panel">
    <div>
      <h2>Progress</h2>
      <p>{{ completionText }}</p>
    </div>

    <div class="progress-track" aria-hidden="true">
      <div
        class="progress-track__fill"
        :style="{ width: `${total === 0 ? 0 : (completed / total) * 100}%` }"
      />
    </div>
  </section>
</template>

<style scoped>
.progress-panel {
  display: grid;
  gap: 0.75rem;
}

.progress-panel h2,
.progress-panel p {
  margin: 0;
}

.progress-panel p {
  margin-top: 0.3rem;
  color: var(--text-soft);
}

.progress-track {
  width: 100%;
  height: 10px;
  border-radius: 999px;
  background: var(--bg-muted);
  overflow: hidden;
}

.progress-track__fill {
  height: 100%;
  background: var(--accent-arcane);
  transition: width 160ms ease;
}
</style>
