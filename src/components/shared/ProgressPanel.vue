<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  completed: number
  total: number
}>()

const progressValue = computed(() => (props.total > 0 ? (props.completed / props.total) * 100 : 0))
</script>

<template>
  <section class="progress-panel">
    <div class="progress-panel__header">
      <div>
        <strong>Progress</strong>
        <p>{{ completed }} of {{ total }} matches completed</p>
      </div>
      <span>{{ progressValue.toFixed(0) }}%</span>
    </div>

    <div class="progress-panel__track" aria-hidden="true">
      <div class="progress-panel__fill" :style="{ width: `${progressValue}%` }"></div>
    </div>
  </section>
</template>

<style scoped>
.progress-panel {
  border: 1px solid #7e5524;
  background:
    linear-gradient(180deg, rgba(58, 35, 22, 0.95), rgba(28, 17, 11, 0.98)),
    #24170f;
  padding: 1rem 1.1rem;
  display: grid;
  gap: 0.8rem;
  box-shadow:
    inset 0 0 0 1px rgba(214, 171, 92, 0.14),
    0 10px 18px rgba(0, 0, 0, 0.18);
}

.progress-panel__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: center;
}

.progress-panel__header strong {
  color: #f2ddb4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
  text-transform: uppercase;
}

.progress-panel__header p {
  margin: 0.25rem 0 0;
  color: #ecd6ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.95rem;
}

.progress-panel__header span {
  font-size: 1.25rem;
  font-weight: 700;
  color: #83d749;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.progress-panel__track {
  position: relative;
  height: 18px;
  border-radius: 999px;
  background: linear-gradient(180deg, #25140d, #3f2614);
  border: 1px solid rgba(174, 126, 65, 0.8);
  overflow: hidden;
  box-shadow:
    inset 0 1px 3px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(83, 52, 22, 0.5);
}

.progress-panel__track::after {
  content: '';
  position: absolute;
  inset: 2px;
  border: 1px solid rgba(232, 192, 118, 0.15);
  border-radius: inherit;
}

.progress-panel__fill {
  height: 100%;
  border-radius: inherit;
  background:
    linear-gradient(180deg, #89df53, #34742a 70%, #1f4e1d),
    #4f8d38;
  box-shadow:
    inset 0 1px 0 rgba(220, 255, 196, 0.38),
    0 0 14px rgba(125, 216, 91, 0.28);
  transition: width 160ms ease;
}
</style>
