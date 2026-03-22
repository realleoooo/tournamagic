<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <section class="join-hero">
    <div class="join-hero__eyebrow">Invitation</div>
    <h2>{{ title }}</h2>
    <p>Claim your seat from the invite link and step straight into the tournament.</p>
  </section>
</template>

<style scoped>
.join-hero {
  display: grid;
  justify-items: center;
  gap: 0.6rem;
  padding: 1.2rem 1rem 0.35rem;
  text-align: center;
}

.join-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 128px;
  padding: 0.34rem 0.9rem;
  border-radius: 999px;
  border: 1px solid rgba(239, 203, 136, 0.34);
  background: rgba(34, 21, 15, 0.42);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.join-hero h2 {
  margin: 0;
  color: #f7e5bc;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: clamp(2rem, 3.8vw, 2.8rem);
  line-height: 1;
}

.join-hero p {
  max-width: 34rem;
  margin: 0;
  color: #e5cfa2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  line-height: 1.45;
}

@media (max-width: 720px) {
  .join-hero {
    padding: 0.7rem 0.5rem 0.35rem;
  }
}
</style>
