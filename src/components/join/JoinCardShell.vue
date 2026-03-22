<script setup lang="ts">
defineProps<{
  title: string
  meta: string
}>()
</script>

<template>
  <article class="join-card">
    <div class="join-card__header">
      <strong>{{ title }}</strong>
      <span>{{ meta }}</span>
    </div>

    <slot />
  </article>
</template>

<style scoped>
.join-card {
  position: relative;
  min-height: 0;
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

.join-card::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 1px solid rgba(203, 155, 84, 0.16);
  border-radius: 14px;
  pointer-events: none;
}

.join-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.9rem;
  padding-bottom: 0.65rem;
  border-bottom: 1px solid rgba(176, 131, 70, 0.38);
}

.join-card__header strong,
.join-card__header span {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-card__header strong {
  color: #f4dfb3;
  font-size: 1.12rem;
}

.join-card__header span {
  color: #e4cda3;
  font-size: 0.94rem;
}
</style>
