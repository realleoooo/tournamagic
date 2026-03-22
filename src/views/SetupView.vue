<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import SetupBoard from '@/components/setup/SetupBoard.vue'
import SetupCreateTournamentModal from '@/components/setup/SetupCreateTournamentModal.vue'
import SetupHero from '@/components/setup/SetupHero.vue'
import SetupStatusMessage from '@/components/setup/SetupStatusMessage.vue'
import SetupTournamentList from '@/components/setup/SetupTournamentList.vue'
import { formatTournamentCount } from '@/components/setup/setupViewFormatters'
import { useTournamentStore } from '@/stores/tournament'

const router = useRouter()
const store = useTournamentStore()
const isCreateModalOpen = ref(false)

const tournamentCountLabel = computed(() => formatTournamentCount(store.tournaments.length))

const closeCreateModal = () => {
  isCreateModalOpen.value = false
}

const openCreateModal = () => {
  isCreateModalOpen.value = true
}

onMounted(async () => {
  await store.refreshTournamentList()
})

const handleCreate = async (name: string, players: string[], creatorPlayerName: string) => {
  const created = await store.createTournament(name, players, creatorPlayerName)
  if (created) {
    closeCreateModal()
    router.push('/tournament')
  }
}

const openTournament = async (id: string) => {
  const loaded = await store.openTournament(id)
  if (loaded) {
    router.push('/tournament')
  }
}

const deleteTournament = async (id: string) => {
  await store.deleteFromList(id)
}
</script>

<template>
  <div class="setup-page">
    <SetupHero
      eyebrow="Overview"
      title="Tournament Hall"
      description="Manage and organize your Magic the Gathering tournaments."
    />

    <SetupBoard
      title="Your Tournaments"
      description="Only tournaments you have already joined are listed here."
      :count-label="tournamentCountLabel"
      create-label="Create New Tournament"
      @create="openCreateModal"
    >
      <SetupStatusMessage v-if="store.error" tone="error"><strong>Error:</strong> {{ store.error }}</SetupStatusMessage>
      <SetupStatusMessage v-if="store.loading">Loading...</SetupStatusMessage>

      <SetupTournamentList
        v-if="!store.loading"
        :tournaments="store.tournaments"
        @open="openTournament"
        @delete="deleteTournament"
      />
    </SetupBoard>

    <SetupCreateTournamentModal v-if="isCreateModalOpen" @close="closeCreateModal" @create="handleCreate" />
  </div>
</template>

<style scoped>
.setup-page {
  display: grid;
  gap: 1.1rem;
  width: min(100%, 940px);
  margin: 0 auto;
  padding: 0.65rem 0 2.1rem;
}

@media (max-width: 720px) {
  .setup-page {
    padding-bottom: 1.25rem;
  }
}
</style>
