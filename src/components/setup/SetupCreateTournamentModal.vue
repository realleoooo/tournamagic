<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'
import PlayerRosterForm from '@/components/players/PlayerRosterForm.vue'

const emit = defineEmits<{
  close: []
  create: [name: string, players: string[], creatorPlayerName: string]
}>()

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape') {
    emit('close')
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown)
})

const handleCreate = (name: string, players: string[], creatorPlayerName: string) => {
  emit('create', name, players, creatorPlayerName)
}
</script>

<template>
  <div class="setup-modal" @click.self="emit('close')">
    <div class="setup-modal__dialog">
      <div class="setup-modal__header">
        <div>
          <h3>Create Tournament</h3>
          <p>Build your next event and add the player roster without leaving the overview.</p>
        </div>

        <button type="button" class="setup-modal__close" aria-label="Close create tournament modal" @click="emit('close')">
          Close
        </button>
      </div>

      <div class="setup-modal__body">
        <PlayerRosterForm @create="handleCreate" />
      </div>
    </div>
  </div>
</template>

<style scoped>
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
