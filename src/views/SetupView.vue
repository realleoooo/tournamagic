<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PlayerRosterForm from '@/components/players/PlayerRosterForm.vue'
import { useTournamentStore } from '@/stores/tournament'

const router = useRouter()
const store = useTournamentStore()

onMounted(async () => {
  await store.refreshTournamentList()
})

const handleCreate = async (name: string, players: string[]) => {
  const created = await store.createTournament(name, players)
  if (created) {
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
  <div class="grid two">
    <section class="card">
      <h2>Tournaments Overview</h2>
      <p style="color: var(--text-soft)">Open an existing tournament or delete old ones.</p>
      <section v-if="store.error" class="card" style="border-color: var(--danger); margin-bottom: 0.75rem;">
        <strong>Error:</strong> {{ store.error }}
      </section>
      <section v-if="store.loading" class="card" style="margin-bottom: 0.75rem;">Loading…</section>

      <div v-if="store.tournaments.length === 0" class="card">No tournaments yet. Create your first one.</div>

      <div v-for="item in store.tournaments" :key="item.id" class="card" style="margin-bottom:0.65rem;">
        <strong>{{ item.name }}</strong>
        <p style="margin:0.35rem 0; color:var(--text-soft)">
          {{ item.playerCount }} players · {{ item.completedMatches }}/{{ item.totalMatches }} matches · {{ item.status }}
        </p>
        <div style="display:flex; gap:0.5rem;">
          <button type="button" @click="openTournament(item.id)">Open</button>
          <button type="button" class="warn" @click="deleteTournament(item.id)">Delete</button>
        </div>
      </div>
    </section>

    <PlayerRosterForm @create="handleCreate" />
  </div>
</template>
