<script setup lang="ts">
import { computed, ref } from 'vue'

const emit = defineEmits<{
  create: [name: string]
}>()

const tournamentName = ref('Friday Night Draft')
const canCreate = computed(() => tournamentName.value.trim().length > 0)

const create = () => {
  if (!canCreate.value) return
  emit('create', tournamentName.value.trim())
}
</script>

<template>
  <section class="card">
    <h2>Create Tournament</h2>
    <p class="muted">Create the room first, then invite players to join by scanning your QR code.</p>

    <div class="grid">
      <label>
        Tournament name
        <input v-model="tournamentName" type="text" />
      </label>
    </div>

    <button type="button" :disabled="!canCreate" @click="create">Create Tournament</button>
  </section>
</template>

<style scoped>
.muted {
  color: var(--text-soft);
}
</style>
