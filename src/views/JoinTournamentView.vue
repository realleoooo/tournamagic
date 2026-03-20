<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const route = useRoute()
const router = useRouter()
const successMessage = ref('')
const selectedPlayerId = ref('')

const joinCode = computed(() => String(route.params.code ?? ''))
const preview = computed(() => store.joinPreview)

const join = async () => {
  if (!selectedPlayerId.value) {
    store.error = 'Choose which player you are before joining.'
    return
  }

  successMessage.value = ''
  const joined = await store.joinTournament(joinCode.value, selectedPlayerId.value)

  if (joined) {
    successMessage.value = `You joined ${joined.name}. Redirecting to the tournament view…`
    window.setTimeout(() => {
      router.replace('/tournament')
    }, 900)
  }
}

onMounted(async () => {
  const previewResult = await store.previewJoin(joinCode.value)
  if (previewResult?.availablePlayers.length) {
    selectedPlayerId.value = previewResult.availablePlayers[0].id
  }
})
</script>

<template>
  <div class="grid">
    <section class="card grid">
      <div>
        <h2>Join Tournament</h2>
        <p class="muted">Scan the invite, then choose which player name in this tournament belongs to you.</p>
      </div>

      <div v-if="preview" class="join-preview">
        <strong>{{ preview.tournamentName }}</strong>
        <p class="muted">Status: {{ preview.status }}</p>
      </div>

      <label v-if="preview && preview.availablePlayers.length > 0" class="join-field">
        <span>Choose your player</span>
        <select v-model="selectedPlayerId">
          <option v-for="player in preview.availablePlayers" :key="player.id" :value="player.id">{{ player.name }}</option>
        </select>
      </label>

      <p v-else-if="preview" class="muted">No unclaimed players are currently available for this tournament.</p>

      <p v-if="successMessage" class="success">{{ successMessage }}</p>
      <p v-if="store.error" class="error">{{ store.error }}</p>

      <div class="join-actions">
        <button type="button" :disabled="store.loading || !preview?.availablePlayers.length" @click="join">Join now</button>
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

.join-field {
  display: grid;
  gap: 0.35rem;
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
