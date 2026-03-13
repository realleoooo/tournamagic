<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Match } from '@/domain/models'

const props = defineProps<{
  matches: Match[]
  resolveName: (id: string) => string
}>()

const emit = defineEmits<{
  submit: [matchId: string, winsA: number, winsB: number]
  clear: [matchId: string]
}>()

const playerFilter = ref('all')
const statusFilter = ref<'all' | 'pending' | 'completed'>('all')

const resultInputs = ref<Record<string, { winsA: number; winsB: number }>>({})

const inputFor = (matchId: string) => {
  if (!resultInputs.value[matchId]) {
    resultInputs.value[matchId] = { winsA: 2, winsB: 0 }
  }
  return resultInputs.value[matchId]
}

const playerOptions = computed(() => {
  const ids = new Set<string>()
  props.matches.forEach((match) => {
    ids.add(match.playerAId)
    ids.add(match.playerBId)
  })
  return [...ids]
})

const filteredMatches = computed(() =>
  props.matches.filter((match) => {
    const statusOk = statusFilter.value === 'all' || match.status === statusFilter.value
    const playerOk =
      playerFilter.value === 'all' ||
      match.playerAId === playerFilter.value ||
      match.playerBId === playerFilter.value
    return statusOk && playerOk
  })
)
</script>

<template>
  <section class="card">
    <h2>Matches</h2>
    <div style="display:flex; gap:0.5rem; margin-bottom:0.8rem;">
      <select v-model="playerFilter">
        <option value="all">All Players</option>
        <option v-for="playerId in playerOptions" :key="playerId" :value="playerId">{{ resolveName(playerId) }}</option>
      </select>
      <select v-model="statusFilter">
        <option value="all">All Status</option>
        <option value="pending">Pending</option>
        <option value="completed">Completed</option>
      </select>
    </div>

    <div v-for="match in filteredMatches" :key="match.id" class="card" style="margin:0.5rem 0;">
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <strong>{{ resolveName(match.playerAId) }} vs {{ resolveName(match.playerBId) }}</strong>
        <span :style="{ color: match.status === 'completed' ? 'var(--ok)' : 'var(--text-soft)' }">{{ match.status }}</span>
      </div>

      <div style="display:flex; gap:0.5rem; margin-top:0.6rem; align-items:center;">
        <input v-model.number="inputFor(match.id).winsA" type="number" min="0" max="2" style="width:70px" />
        <span>-</span>
        <input v-model.number="inputFor(match.id).winsB" type="number" min="0" max="2" style="width:70px" />
        <button type="button" @click="emit('submit', match.id, inputFor(match.id).winsA, inputFor(match.id).winsB)">Save</button>
        <button type="button" class="secondary" @click="emit('clear', match.id)">Undo</button>
      </div>
    </div>
  </section>
</template>
