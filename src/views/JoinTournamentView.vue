<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import JoinInfoCard from '@/components/join/JoinInfoCard.vue'
import JoinSeatCard from '@/components/join/JoinSeatCard.vue'
import JoinStatusMessage from '@/components/join/JoinStatusMessage.vue'
import JoinTournamentBoard from '@/components/join/JoinTournamentBoard.vue'
import JoinTournamentHero from '@/components/join/JoinTournamentHero.vue'
import { formatAvailableSeatLabel, formatJoinStateLabel } from '@/components/join/joinViewFormatters'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const route = useRoute()
const router = useRouter()
const successMessage = ref('')
const selectedPlayerId = ref('')

const joinCode = computed(() => String(route.params.code ?? ''))
const preview = computed(() => store.joinPreview)
const availablePlayers = computed(() => preview.value?.availablePlayers ?? [])
const availableSeatLabel = computed(() => formatAvailableSeatLabel(availablePlayers.value.length))
const joinStateLabel = computed(() => formatJoinStateLabel(Boolean(preview.value?.joinEnabled)))
const selectedPlayerName = computed(
  () => availablePlayers.value.find((player) => player.id === selectedPlayerId.value)?.name ?? ''
)
const canJoin = computed(
  () => Boolean(preview.value?.joinEnabled && availablePlayers.value.length > 0 && selectedPlayerId.value)
)
const boardDescription = computed(() =>
  preview.value
    ? 'Choose the player slot that belongs to you in this tournament.'
    : 'Loading invite details for this tournament.'
)
const boardMetaItems = computed(() => (preview.value ? [joinStateLabel.value, availableSeatLabel.value] : []))

const join = async () => {
  if (!selectedPlayerId.value) {
    store.error = 'Choose which player you are before joining.'
    return
  }

  successMessage.value = ''
  const joined = await store.joinTournament(joinCode.value, selectedPlayerId.value)

  if (joined) {
    successMessage.value = `You joined ${joined.name} as ${selectedPlayerName.value || 'your seat'}. Redirecting to the tournament view\u2026`
    window.setTimeout(() => {
      router.replace('/tournament')
    }, 900)
  }
}

const goBack = () => {
  router.replace('/')
}

onMounted(async () => {
  const previewResult = await store.previewJoin(joinCode.value)
  if (previewResult?.availablePlayers.length) {
    selectedPlayerId.value = previewResult.availablePlayers[0].id
  }
})
</script>

<template>
  <div class="join-page">
    <JoinTournamentHero :title="preview?.tournamentName ?? 'Join Tournament'" />

    <JoinTournamentBoard title="Join Tournament" :description="boardDescription" :meta-items="boardMetaItems">
      <JoinStatusMessage v-if="successMessage" tone="success">
        {{ successMessage }}
      </JoinStatusMessage>

      <JoinStatusMessage v-if="store.error" tone="error">
        {{ store.error }}
      </JoinStatusMessage>

      <JoinStatusMessage v-if="store.loading && !preview">Preparing your invitation&hellip;</JoinStatusMessage>

      <div v-if="preview" class="join-grid">
        <JoinInfoCard :preview="preview" />

        <JoinSeatCard
          :available-players="availablePlayers"
          :selected-player-id="selectedPlayerId"
          :loading="store.loading"
          :can-join="canJoin"
          @select="selectedPlayerId = $event"
          @join="join"
          @back="goBack"
        />
      </div>
    </JoinTournamentBoard>
  </div>
</template>

<style scoped>
.join-page {
  display: grid;
  gap: 1.1rem;
  width: min(100%, 980px);
  margin: 0 auto;
  padding: 0.65rem 0 2rem;
}

.join-grid {
  position: relative;
  z-index: 1;
  margin-top: 1rem;
  display: grid;
  grid-template-columns: minmax(280px, 0.9fr) minmax(320px, 1.1fr);
  gap: 1rem;
}

@media (max-width: 900px) {
  .join-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .join-page {
    padding-bottom: 1.25rem;
  }
}
</style>
