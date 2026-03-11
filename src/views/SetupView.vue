<script setup lang="ts">
import { useRouter } from 'vue-router'
import PlayerRosterForm from '@/components/players/PlayerRosterForm.vue'
import { useTournamentStore } from '@/stores/tournament'

const router = useRouter()
const store = useTournamentStore()

const handleCreate = (name: string, players: string[]) => {
  store.createTournament(name, players)
  router.push('/tournament')
}
</script>

<template>
  <PlayerRosterForm @create="handleCreate" />
</template>
