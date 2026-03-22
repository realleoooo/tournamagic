<script setup lang="ts">
defineProps<{
  title: string
  description: string
  countLabel: string
  createLabel: string
}>()

const emit = defineEmits<{
  create: []
}>()
</script>

<template>
  <section class="setup-board">
    <div class="setup-board__header">
      <div>
        <h3>{{ title }}</h3>
        <p>{{ description }}</p>
      </div>

      <div class="setup-board__actions">
        <span class="setup-board__count">{{ countLabel }}</span>
        <button type="button" class="setup-board__create" @click="emit('create')">{{ createLabel }}</button>
      </div>
    </div>

    <slot />
  </section>
</template>

<style scoped>
.setup-board {
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

.setup-board::before {
  content: '';
  position: absolute;
  inset: 14px;
  border: 1px solid rgba(230, 186, 108, 0.15);
  border-radius: 20px;
  pointer-events: none;
}

.setup-board__header {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 1rem;
  margin-bottom: 0.35rem;
}

.setup-board__header h3 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.85rem;
}

.setup-board__header p {
  margin: 0.55rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.setup-board__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.setup-board__count {
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(218, 174, 96, 0.34);
  background: rgba(29, 18, 12, 0.38);
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

.setup-board__create {
  min-width: 220px;
  padding: 0.8rem 1.2rem;
  border-radius: 14px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  background:
    linear-gradient(180deg, rgba(134, 181, 74, 0.98), rgba(66, 109, 40, 0.98)),
    #699c49;
  border-color: rgba(225, 190, 118, 0.82);
  color: #f8f2d8;
}

.setup-board__create:hover {
  background:
    linear-gradient(180deg, rgba(149, 195, 88, 0.98), rgba(74, 121, 46, 0.98)),
    #75a552;
}

@media (max-width: 720px) {
  .setup-board {
    padding: 1.15rem;
  }

  .setup-board__header {
    align-items: start;
    flex-direction: column;
  }

  .setup-board__actions {
    width: 100%;
    justify-content: flex-start;
  }

  .setup-board__create {
    width: 100%;
  }
}
</style>
