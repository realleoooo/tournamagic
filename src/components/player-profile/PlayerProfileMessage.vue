<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string
    message: string
    tone?: 'default' | 'error'
  }>(),
  {
    tone: 'default'
  }
)
</script>

<template>
  <section :class="['profile-message', { 'profile-message--error': tone === 'error' }]">
    <strong>{{ title }}</strong>
    <p>{{ message }}</p>
  </section>
</template>

<style scoped>
.profile-message {
  display: grid;
  gap: 0.45rem;
  padding: 1.35rem 1.4rem;
  border: 1px solid rgba(201, 153, 82, 0.52);
  background:
    linear-gradient(180deg, rgba(85, 53, 33, 0.28), rgba(29, 18, 13, 0.24)),
    rgba(57, 34, 22, 0.28);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 34px rgba(0, 0, 0, 0.24);
}

.profile-message strong {
  color: #f6e2b4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.2rem;
}

.profile-message p {
  margin: 0;
  color: #e2c89c;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-message--error strong {
  color: #ffcfbf;
}

@media (max-width: 720px) {
  .profile-message {
    padding: 1.1rem;
  }
}
</style>
