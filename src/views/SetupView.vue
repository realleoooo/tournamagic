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

const handleCreate = async (name: string, players: string[], creatorPlayerName: string) => {
  const created = await store.createTournament(name, players, creatorPlayerName)
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
  <div class="setup-page">
    <section class="setup-board">
      <div class="setup-board__header">
        <h2>Your Tournaments</h2>
        <p>Only tournaments you have already joined are listed here.</p>
      </div>

      <section v-if="store.error" class="setup-message setup-message--error">
        <strong>Error:</strong> {{ store.error }}
      </section>

      <section v-if="store.loading" class="setup-message">Loading…</section>

      <div v-if="store.tournaments.length === 0 && !store.loading" class="setup-empty">
        You have not joined any tournaments yet.
      </div>

      <div class="setup-list">
        <article v-for="item in store.tournaments" :key="item.id" class="setup-tournament">
          <div class="setup-tournament__copy">
            <strong>{{ item.name }}</strong>
            <p>{{ item.playerCount }} players · {{ item.completedMatches }} / {{ item.totalMatches }} matches · {{ item.status }}</p>
          </div>

          <div class="setup-tournament__actions">
            <button type="button" class="setup-button setup-button--open" @click="openTournament(item.id)">Open</button>
            <button type="button" class="setup-button setup-button--delete" @click="deleteTournament(item.id)">Delete</button>
          </div>
        </article>
      </div>
    </section>

    <PlayerRosterForm @create="handleCreate" />
  </div>
</template>

<style scoped>
.setup-page {
  display: grid;
  grid-template-columns: minmax(0, 2.2fr) minmax(360px, 1fr);
  gap: 1.35rem;
  align-items: stretch;
}

.setup-board {
  position: relative;
  min-height: 640px;
  background:
    linear-gradient(180deg, rgba(91, 56, 34, 0.24), rgba(34, 20, 14, 0.14)),
    rgba(67, 40, 26, 0.24);
  border: 1px solid #8c5d2b;
  box-shadow:
    inset 0 0 0 1px rgba(236, 190, 105, 0.16),
    0 18px 28px rgba(0, 0, 0, 0.24);
  padding: 1.55rem 1.45rem 1.35rem;
}

.setup-board::before,
.setup-board::after {
  content: '';
  position: absolute;
  left: 50%;
  width: 20px;
  height: 20px;
  margin-left: -10px;
  transform: rotate(45deg);
  border: 1px solid rgba(223, 177, 95, 0.72);
  background: linear-gradient(135deg, rgba(255, 223, 151, 0.88), rgba(143, 87, 30, 0.42));
  box-shadow: 0 0 10px rgba(255, 190, 80, 0.22);
}

.setup-board::before {
  bottom: -10px;
}

.setup-board::after {
  display: none;
}

.setup-board__header h2 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
}

.setup-board__header p {
  margin: 0.7rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.setup-message,
.setup-empty {
  margin-top: 1rem;
  border: 1px solid rgba(158, 113, 56, 0.72);
  background:
    linear-gradient(180deg, rgba(73, 44, 26, 0.95), rgba(32, 20, 13, 0.98)),
    #29180f;
  color: #efd9af;
  padding: 0.9rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.setup-message--error {
  color: #f0b29a;
}

.setup-list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
  align-content: start;
}

.setup-tournament {
  position: relative;
  border: 1px solid #9a6a31;
  background:
    linear-gradient(180deg, rgba(94, 58, 36, 0.22), rgba(27, 17, 12, 0.18)),
    rgba(56, 33, 22, 0.28);
  padding: 1.2rem 1.15rem;
  box-shadow:
    inset 0 0 0 1px rgba(234, 187, 104, 0.13),
    0 10px 18px rgba(0, 0, 0, 0.16);
}

.setup-tournament::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 1px solid rgba(203, 155, 84, 0.18);
  pointer-events: none;
}

.setup-tournament__copy strong {
  display: block;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.1rem;
}

.setup-tournament__copy p {
  margin: 0.35rem 0 0;
  color: #e2c79a;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.97rem;
}

.setup-tournament__actions {
  display: flex;
  gap: 0.7rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}

.setup-button {
  min-width: 92px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.setup-button--open {
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
}

.setup-button--open:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

.setup-button--delete {
  background:
    linear-gradient(180deg, rgba(167, 78, 57, 0.94), rgba(101, 39, 30, 0.98)),
    #7c3429;
  border-color: rgba(211, 154, 101, 0.68);
  color: #f6dfca;
}

.setup-button--delete:hover {
  background:
    linear-gradient(180deg, rgba(186, 87, 63, 0.96), rgba(114, 46, 35, 0.98)),
    #8a3a2e;
}

@media (max-width: 1080px) {
  .setup-page {
    grid-template-columns: 1fr;
  }

  .setup-board {
    min-height: 0;
  }
}
</style>
