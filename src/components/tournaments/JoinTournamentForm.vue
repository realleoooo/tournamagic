<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  join: [code: string]
}>()

const props = defineProps<{
  loading?: boolean
  error?: string
  success?: string
}>()

const joinCode = ref('')

const submit = () => {
  if (!joinCode.value.trim()) {
    return
  }

  emit('join', joinCode.value.trim())
}
</script>

<template>
  <section class="card grid">
    <div>
      <h2>Join a Tournament</h2>
      <p class="muted">Paste a join code from a friend or scan their QR code and finish joining here.</p>
    </div>

    <label class="join-field">
      <span>Join code</span>
      <input v-model="joinCode" type="text" autocomplete="off" placeholder="ABCD2345" @keyup.enter="submit" />
    </label>

    <div class="join-actions">
      <button type="button" :disabled="loading || !joinCode.trim()" @click="submit">
        {{ loading ? 'Joining…' : 'Join tournament' }}
      </button>
    </div>

    <p v-if="success" class="success">{{ success }}</p>
    <p v-if="error" class="error">{{ error }}</p>
  </section>
</template>

<style scoped>
.muted {
  margin: 0.35rem 0 0;
  color: var(--text-soft);
}

.join-field {
  display: grid;
  gap: 0.35rem;
  color: var(--text-soft);
}

.join-actions {
  display: flex;
  gap: 0.65rem;
}

.success {
  margin: 0;
  color: var(--success);
}

.error {
  margin: 0;
  color: var(--danger);
}
</style>
