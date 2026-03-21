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
  <section class="roster-panel">
    <div class="roster-panel__header">
      <h2>Create Tournament</h2>
      <p>Add the tournament player names, then choose which player you are before creating the invite room.</p>
    </div>

    <div class="roster-form">
      <label class="roster-field">
        <span>Tournament name</span>
        <input v-model="tournamentName" type="text" />
      </label>

      <label class="roster-field">
        <span>Add player</span>
        <div class="roster-add">
          <input v-model="playerName" type="text" placeholder="Leo" @keyup.enter="addPlayer" />
          <button type="button" class="roster-action" @click="addPlayer">Add</button>
        </div>
      </label>

      <label v-if="players.length > 0" class="roster-field">
        <span>You are</span>
        <select v-model="creatorPlayerName">
          <option v-for="player in players" :key="player" :value="player">{{ player }}</option>
        </select>
      </label>

      <ul v-if="players.length > 0" class="roster-list">
        <li v-for="(player, index) in players" :key="player" class="roster-list__item">
          <span>{{ player }}</span>
          <button type="button" class="roster-remove" @click="removePlayer(index)">Remove</button>
        </li>
      </ul>

      <button type="button" class="roster-create" :disabled="!canCreate" @click="create">Create Tournament</button>
    </div>
  </section>
</template>

<style scoped>
.roster-panel {
  position: relative;
  min-height: 640px;
  background:
    linear-gradient(180deg, rgba(91, 56, 34, 0.24), rgba(34, 20, 14, 0.14)),
    rgba(67, 40, 26, 0.24);
  border: 1px solid #8c5d2b;
  box-shadow:
    inset 0 0 0 1px rgba(236, 190, 105, 0.16),
    0 18px 28px rgba(0, 0, 0, 0.24);
  padding: 1.55rem 1.35rem 1.35rem;
}

.roster-panel__header h2 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
}

.roster-panel__header p {
  margin: 0.8rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  line-height: 1.25;
}

.roster-form {
  margin-top: 1.5rem;
  display: grid;
  gap: 1.15rem;
  align-content: start;
}

.roster-field {
  display: grid;
  gap: 0.55rem;
}

.roster-field span {
  color: #f0dcb2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.roster-field input,
.roster-field select {
  background:
    linear-gradient(180deg, rgba(25, 17, 12, 0.96), rgba(18, 13, 10, 0.98)),
    #17120e;
  border-color: rgba(170, 125, 68, 0.84);
  color: #f2ddb6;
  border-radius: 10px;
  padding: 0.78rem 0.95rem;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.34);
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.roster-add {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.7rem;
}

.roster-action,
.roster-create {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.roster-action {
  min-width: 78px;
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
}

.roster-action:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

.roster-list {
  list-style: none;
  margin: 0;
  padding: 0.25rem 0 0;
  display: grid;
  gap: 0.55rem;
}

.roster-list__item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  padding: 0.65rem 0;
  border-bottom: 1px solid rgba(137, 99, 55, 0.38);
  color: #edd7af;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.roster-remove {
  background:
    linear-gradient(180deg, rgba(92, 61, 35, 0.94), rgba(44, 28, 19, 0.98)),
    #432c1d;
  border-color: rgba(171, 127, 67, 0.72);
  color: #efdcb0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.92rem;
  padding: 0.42rem 0.7rem;
}

.roster-remove:hover {
  background:
    linear-gradient(180deg, rgba(105, 71, 40, 0.96), rgba(51, 33, 21, 0.98)),
    #4c3120;
}

.roster-create {
  justify-self: start;
  min-width: 230px;
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
  font-size: 1.05rem;
}

.roster-create:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

@media (max-width: 1080px) {
  .roster-panel {
    min-height: 0;
  }
}

@media (max-width: 720px) {
  .roster-add {
    grid-template-columns: 1fr;
  }

  .roster-create {
    width: 100%;
    justify-self: stretch;
  }
}
</style>
