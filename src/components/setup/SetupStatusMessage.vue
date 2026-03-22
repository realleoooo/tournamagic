<script setup lang="ts">
withDefaults(
  defineProps<{
    tone?: 'default' | 'error'
  }>(),
  {
    tone: 'default'
  }
)
</script>

<template>
  <section :class="['setup-message', `setup-message--${tone}`]">
    <slot />
  </section>
</template>

<style scoped>
.setup-message {
  margin-top: 1rem;
  border: 1px solid rgba(158, 113, 56, 0.72);
  background:
    linear-gradient(180deg, rgba(73, 44, 26, 0.95), rgba(32, 20, 13, 0.98)),
    #29180f;
  color: #efd9af;
  padding: 0.95rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.setup-message--error {
  color: #f0b29a;
}
</style>
