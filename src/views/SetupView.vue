<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import PlayerRosterForm from '@/components/players/PlayerRosterForm.vue'
import { useTournamentStore } from '@/stores/tournament'

const router = useRouter()
const store = useTournamentStore()
const isCreateModalOpen = ref(false)

const tournamentCountLabel = computed(() => {
  const count = store.tournaments.length
  return `${count} tournament${count === 1 ? '' : 's'}`
})

const closeCreateModal = () => {
  isCreateModalOpen.value = false
}

const openCreateModal = () => {
  isCreateModalOpen.value = true
}

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && isCreateModalOpen.value) {
    closeCreateModal()
  }
}

onMounted(async () => {
  await store.refreshTournamentList()
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
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
    <section class="setup-hero">
      <div class="setup-hero__eyebrow">Overview</div>
      <h2>Tournament Hall</h2>
      <p>Manage and organize your Magic the Gathering tournaments.</p>
    </section>

    <section class="setup-board">
      <div class="setup-board__header">
        <div>
          <h3>Your Tournaments</h3>
          <p>Only tournaments you have already joined are listed here.</p>
        </div>

        <div class="setup-board__actions">
          <span class="setup-board__count">{{ tournamentCountLabel }}</span>
          <button type="button" class="setup-board__create" @click="openCreateModal">Create New Tournament</button>
        </div>
      </div>

      <section v-if="store.error" class="setup-message setup-message--error">
        <strong>Error:</strong> {{ store.error }}
      </section>

      <section v-if="store.loading" class="setup-message">Loading...</section>

      <template v-if="!store.loading">
        <div v-if="store.tournaments.length === 0" class="setup-empty">
          <strong>No tournaments yet</strong>
          <p>Begin your journey by creating your first tournament.</p>
        </div>

        <div v-else class="setup-list">
          <article v-for="item in store.tournaments" :key="item.id" class="setup-tournament">
            <div class="setup-tournament__copy">
              <strong>{{ item.name }}</strong>
              <p>{{ item.playerCount }} players - {{ item.completedMatches }} / {{ item.totalMatches }} matches - {{ item.status }}</p>
            </div>

            <div class="setup-tournament__actions">
              <button type="button" class="setup-button setup-button--open" @click="openTournament(item.id)">Open</button>
              <button type="button" class="setup-button setup-button--delete" @click="deleteTournament(item.id)">Delete</button>
            </div>
          </article>
        </div>
      </template>
    </section>

    <div v-if="isCreateModalOpen" class="setup-modal" @click.self="closeCreateModal">
      <div class="setup-modal__dialog">
        <div class="setup-modal__header">
          <div>
            <h3>Create Tournament</h3>
            <p>Build your next event and add the player roster without leaving the overview.</p>
          </div>

          <button type="button" class="setup-modal__close" aria-label="Close create tournament modal" @click="closeCreateModal">
            Close
          </button>
        </div>

        <div class="setup-modal__body">
          <PlayerRosterForm @create="handleCreate" />
        </div>
      </div>
    </div>
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

.setup-board {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(209, 159, 85, 0.5);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 40px rgba(0, 0, 0, 0.24);
}

.setup-hero {
  display: grid;
  justify-items: center;
  gap: 0.6rem;
  padding: 1.2rem 1rem 0.35rem;
  text-align: center;
}

.setup-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 112px;
  padding: 0.34rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(239, 203, 136, 0.34);
  background: rgba(34, 21, 15, 0.42);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.setup-hero h2 {
  margin: 0;
  color: #f7e5bc;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: clamp(2rem, 3.6vw, 2.7rem);
  line-height: 1;
}

.setup-hero p {
  max-width: 30rem;
  margin: 0;
  color: #e5cfa2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  line-height: 1.4;
}

.setup-board::before {
  content: '';
  position: absolute;
  inset: 14px;
  border: 1px solid rgba(230, 186, 108, 0.15);
  border-radius: 20px;
  pointer-events: none;
}

.setup-board {
  padding: 1.6rem;
  background:
    linear-gradient(180deg, rgba(91, 56, 34, 0.28), rgba(34, 20, 14, 0.2)),
    rgba(67, 40, 26, 0.24);
}

.setup-board__header {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 1rem;
  margin-bottom: 0.35rem;
}

.setup-board__header h3 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.85rem;
}

.setup-board__header p {
  margin: 0.55rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.setup-board__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.setup-board__count {
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(218, 174, 96, 0.34);
  background: rgba(29, 18, 12, 0.38);
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

.setup-board__create {
  min-width: 220px;
  padding: 0.8rem 1.2rem;
  border-radius: 14px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  background:
    linear-gradient(180deg, rgba(134, 181, 74, 0.98), rgba(66, 109, 40, 0.98)),
    #699c49;
  border-color: rgba(225, 190, 118, 0.82);
  color: #f8f2d8;
}

.setup-board__create:hover {
  background:
    linear-gradient(180deg, rgba(149, 195, 88, 0.98), rgba(74, 121, 46, 0.98)),
    #75a552;
}

.setup-message,
.setup-empty {
  margin-top: 1rem;
  border: 1px solid rgba(158, 113, 56, 0.72);
  background:
    linear-gradient(180deg, rgba(73, 44, 26, 0.95), rgba(32, 20, 13, 0.98)),
    #29180f;
  color: #efd9af;
  padding: 0.95rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.setup-message--error {
  color: #f0b29a;
}

.setup-empty {
  display: grid;
  gap: 0.45rem;
  min-height: 180px;
  place-items: center;
  text-align: center;
}

.setup-empty strong {
  font-size: 1.35rem;
  font-weight: 700;
}

.setup-empty p {
  margin: 0;
  max-width: 26rem;
}

.setup-list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
  align-content: start;
}

.setup-tournament {
  position: relative;
  border: 1px solid rgba(179, 126, 63, 0.88);
  border-radius: 22px;
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
  border: 1px solid rgba(203, 155, 84, 0.16);
  border-radius: 14px;
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

.setup-modal {
  position: fixed;
  inset: 0;
  z-index: 30;
  display: grid;
  place-items: center;
  padding: 1.2rem;
  background:
    radial-gradient(circle at top, rgba(231, 191, 113, 0.14), transparent 30%),
    rgba(10, 7, 6, 0.76);
  backdrop-filter: blur(6px);
}

.setup-modal__dialog {
  width: min(100%, 760px);
  max-height: min(88vh, 980px);
  overflow: auto;
  border-radius: 28px;
  border: 1px solid rgba(209, 159, 85, 0.56);
  background:
    linear-gradient(180deg, rgba(92, 57, 36, 0.94), rgba(34, 21, 15, 0.98)),
    #20130d;
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 28px 60px rgba(0, 0, 0, 0.42);
}

.setup-modal__header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 1rem;
  padding: 1.35rem 1.4rem 0;
}

.setup-modal__header h3 {
  margin: 0;
  color: #f6e2b5;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.55rem;
}

.setup-modal__header p {
  margin: 0.45rem 0 0;
  color: #e3c99a;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.94rem;
  line-height: 1.4;
}

.setup-modal__close {
  min-width: 92px;
  padding: 0.72rem 0.95rem;
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(92, 61, 35, 0.94), rgba(44, 28, 19, 0.98)),
    #432c1d;
  border-color: rgba(171, 127, 67, 0.72);
  color: #efdcb0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.94rem;
}

.setup-modal__close:hover {
  background:
    linear-gradient(180deg, rgba(105, 71, 40, 0.96), rgba(51, 33, 21, 0.98)),
    #4c3120;
}

.setup-modal__body {
  padding: 0.6rem 1rem 1rem;
}

:deep(.roster-panel) {
  min-height: 0;
  border: 0;
  border-radius: 0;
  box-shadow: none;
  padding: 1rem 0.8rem 0.85rem;
  background: transparent;
}

@media (max-width: 720px) {
  .setup-page {
    padding-bottom: 1.25rem;
  }

  .setup-hero {
    padding: 0.7rem 0.5rem 0.35rem;
  }

  .setup-board {
    padding: 1.15rem;
  }

  .setup-board__header {
    align-items: start;
    flex-direction: column;
  }

  .setup-board__actions {
    width: 100%;
    justify-content: flex-start;
  }

  .setup-board__create {
    width: 100%;
  }

  .setup-modal {
    padding: 0.7rem;
  }

  .setup-modal__header {
    flex-direction: column;
  }

  .setup-modal__close {
    width: 100%;
  }

  :deep(.roster-panel) {
    padding: 0.85rem 0.25rem 0.4rem;
  }
}
</style>
