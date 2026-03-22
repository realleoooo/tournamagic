<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  description: string
}>()
</script>

<template>
  <section class="setup-hero">
    <div class="setup-hero__eyebrow">{{ eyebrow }}</div>
    <h2>{{ title }}</h2>
    <p>{{ description }}</p>
  </section>
</template>

<style scoped>
.setup-hero {
  display: grid;
  justify-items: center;
  gap: 0.6rem;
  padding: 1.2rem 1rem 0.35rem;
  text-align: center;
}

.setup-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 112px;
  padding: 0.34rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(239, 203, 136, 0.34);
  background: rgba(34, 21, 15, 0.42);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.setup-hero h2 {
  margin: 0;
  color: #f7e5bc;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: clamp(2rem, 3.6vw, 2.7rem);
  line-height: 1;
}

.setup-hero p {
  max-width: 30rem;
  margin: 0;
  color: #e5cfa2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  line-height: 1.4;
}

@media (max-width: 720px) {
  .setup-hero {
    padding: 0.7rem 0.5rem 0.35rem;
  }
}
</style>
