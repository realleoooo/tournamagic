<script setup lang="ts">
import { computed, ref } from 'vue'

const emit = defineEmits<{
  create: [name: string, players: string[], creatorPlayerName: string]
}>()

const tournamentName = ref('Friday Night Draft')
const playerName = ref('')
const creatorPlayerName = ref('')
const players = ref<string[]>([])

const normalized = computed(() => players.value.map((player) => player.toLowerCase()))
const canCreate = computed(
  () => tournamentName.value.trim().length > 0 && players.value.length >= 2 && creatorPlayerName.value.trim().length > 0
)

const addPlayer = () => {
  const name = playerName.value.trim()
  if (!name) return
  if (normalized.value.includes(name.toLowerCase())) return
  players.value.push(name)
  if (!creatorPlayerName.value) {
    creatorPlayerName.value = name
  }
  playerName.value = ''
}

const removePlayer = (index: number) => {
  const removed = players.value[index]
  players.value.splice(index, 1)
  if (creatorPlayerName.value === removed) {
    creatorPlayerName.value = players.value[0] ?? ''
  }
}

const create = () => {
  if (!canCreate.value) return
  emit('create', tournamentName.value.trim(), players.value, creatorPlayerName.value)
}
</script>

<template>
  <section class="card">
    <h2>Create Tournament</h2>
    <p class="muted">Add the tournament player names, then choose which player you are before creating the invite room.</p>

    <div class="grid">
      <label>
        Tournament name
        <input v-model="tournamentName" type="text" />
      </label>

      <label>
        Add player
        <div style="display:flex; gap:0.5rem; margin-top:0.3rem;">
          <input v-model="playerName" type="text" placeholder="Leo" @keyup.enter="addPlayer" />
          <button type="button" @click="addPlayer">Add</button>
        </div>
      </label>

      <label v-if="players.length > 0">
        You are
        <select v-model="creatorPlayerName">
          <option v-for="player in players" :key="player" :value="player">{{ player }}</option>
        </select>
      </label>
    </div>

    <ul>
      <li v-for="(player, index) in players" :key="player" style="display:flex; justify-content:space-between; padding:0.35rem 0;">
        <span>{{ player }}</span>
        <button type="button" class="secondary" @click="removePlayer(index)">Remove</button>
      </li>
    </ul>

    <button type="button" :disabled="!canCreate" @click="create">Create Tournament</button>
  </section>
</template>

<style scoped>
.muted {
  color: var(--text-soft);
}
</style>
