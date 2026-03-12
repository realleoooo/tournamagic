<script setup lang="ts">
import { useRouter } from 'vue-router'
import PlayerRosterForm from '@/components/players/PlayerRosterForm.vue'
import { useTournamentStore } from '@/stores/tournament'

const router = useRouter()
const store = useTournamentStore()

const handleCreate = async (name: string, players: string[]) => {
  const created = await store.createTournament(name, players)
  if (created) {
    router.push('/tournament')
  }
}
</script>

<template>
  <div class="grid">
    <section v-if="store.error" class="card" style="border-color: var(--danger)">
      <strong>Could not create tournament:</strong> {{ store.error }}
    </section>
    <PlayerRosterForm @create="handleCreate" />
  </div>
</template>
