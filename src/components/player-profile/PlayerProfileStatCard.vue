<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  description: string
}>()
</script>

<template>
  <article class="profile-stat">
    <span class="profile-stat__label">{{ label }}</span>
    <strong>{{ value }}</strong>
    <p>{{ description }}</p>
  </article>
</template>

<style scoped>
.profile-stat {
  padding: 1.15rem 1.1rem;
  background:
    linear-gradient(180deg, rgba(246, 224, 181, 0.95), rgba(221, 189, 132, 0.95)),
    #e2bf84;
}

.profile-stat__label {
  color: #5d3a17;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-stat strong {
  display: block;
  margin-top: 0.35rem;
  color: #2c1606;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
  line-height: 1;
}

.profile-stat p {
  margin: 0.45rem 0 0;
  color: #4a3016;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}
</style>
