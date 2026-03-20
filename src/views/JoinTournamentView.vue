<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const route = useRoute()
const router = useRouter()
const successMessage = ref('')
const attemptedJoin = ref(false)

const joinCode = computed(() => String(route.params.code ?? ''))
const preview = computed(() => store.joinPreview)

const join = async () => {
  attemptedJoin.value = true
  successMessage.value = ''
  const joined = await store.joinTournament(joinCode.value)

  if (joined) {
    successMessage.value = `You joined ${joined.name}. Redirecting to the tournament view…`
    window.setTimeout(() => {
      router.replace('/tournament')
    }, 900)
  }
}

onMounted(async () => {
  await store.previewJoin(joinCode.value)

  if (!store.error) {
    await join()
  }
})
</script>

<template>
  <div class="grid">
    <section class="card grid">
      <div>
        <h2>Join Tournament</h2>
        <p class="muted">Use this invite to join a tournament with your account.</p>
      </div>

      <div v-if="preview" class="join-preview">
        <strong>{{ preview.tournamentName }}</strong>
        <p class="muted">Code: {{ preview.joinCode }} · Status: {{ preview.status }}</p>
      </div>

      <p v-if="successMessage" class="success">{{ successMessage }}</p>
      <p v-else-if="store.loading" class="muted">Joining tournament…</p>
      <p v-if="store.error" class="error">{{ store.error }}</p>

      <div class="join-actions">
        <button type="button" :disabled="store.loading" @click="join">
          {{ attemptedJoin ? 'Try again' : 'Join now' }}
        </button>
        <button type="button" class="secondary" @click="router.replace('/')">Back to overview</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.muted {
  margin: 0.35rem 0 0;
  color: var(--text-soft);
}

.join-preview {
  padding: 0.85rem;
  border-radius: 12px;
  background: color-mix(in srgb, var(--bg-main) 50%, transparent);
}

.join-actions {
  display: flex;
  flex-wrap: wrap;
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
