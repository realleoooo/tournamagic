<script setup lang="ts">
defineProps<{
  title: string
  description: string
  metaItems?: string[]
}>()
</script>

<template>
  <section class="join-board">
    <div class="join-board__header">
      <div>
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
      </div>

      <div v-if="metaItems?.length" class="join-board__meta">
        <span v-for="item in metaItems" :key="item" class="join-chip">{{ item }}</span>
      </div>
    </div>

    <slot />
  </section>
</template>

<style scoped>
.join-board {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(209, 159, 85, 0.5);
  padding: 1.6rem;
  background:
    linear-gradient(180deg, rgba(91, 56, 34, 0.28), rgba(34, 20, 14, 0.2)),
    rgba(67, 40, 26, 0.24);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 40px rgba(0, 0, 0, 0.24);
}

.join-board::before {
  content: '';
  position: absolute;
  inset: 14px;
  border: 1px solid rgba(230, 186, 108, 0.15);
  border-radius: 20px;
  pointer-events: none;
}

.join-board__header {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 1rem;
}

.join-board__header h3 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.85rem;
}

.join-board__header p {
  margin: 0.55rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.join-board__meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.join-chip {
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(218, 174, 96, 0.34);
  background: rgba(29, 18, 12, 0.38);
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

@media (max-width: 720px) {
  .join-board {
    padding: 1.15rem;
  }

  .join-board__header {
    align-items: start;
    flex-direction: column;
  }

  .join-board__meta {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
