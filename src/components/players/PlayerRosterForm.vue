<script setup lang="ts">
import { computed, ref } from 'vue'

const emit = defineEmits<{
  create: [name: string, players: string[]]
}>()

const tournamentName = ref('Friday Night Draft')
const playerName = ref('')
const players = ref<string[]>([])

const normalized = computed(() => players.value.map((player) => player.toLowerCase()))

const addPlayer = () => {
  const name = playerName.value.trim()
  if (!name) return
  if (normalized.value.includes(name.toLowerCase())) return
  players.value.push(name)
  playerName.value = ''
}

const removePlayer = (index: number) => {
  players.value.splice(index, 1)
}

const canStart = computed(() => players.value.length >= 2 && tournamentName.value.trim().length > 0)

const start = () => {
  if (!canStart.value) return
  emit('create', tournamentName.value.trim(), players.value)
}
</script>

<template>
  <section class="card">
    <h2>Setup Tournament</h2>
    <div class="grid">
      <label>
        Tournament name
        <input v-model="tournamentName" type="text" />
      </label>
      <label>
        Add player
        <div style="display:flex; gap:0.5rem; margin-top:0.3rem;">
          <input v-model="playerName" type="text" placeholder="Jace" @keyup.enter="addPlayer" />
          <button type="button" @click="addPlayer">Add</button>
        </div>
      </label>
    </div>

    <ul>
      <li v-for="(player, index) in players" :key="player" style="display:flex; justify-content:space-between; padding:0.35rem 0;">
        <span>{{ player }}</span>
        <button type="button" class="secondary" @click="removePlayer(index)">Remove</button>
      </li>
    </ul>

    <button type="button" :disabled="!canStart" @click="start">Start Tournament</button>
  </section>
</template>
