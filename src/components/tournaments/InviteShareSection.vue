<script setup lang="ts">
import { computed, ref } from 'vue'
import type { Tournament } from '@/domain/models'

const props = defineProps<{
  tournament: Tournament
}>()

const copyStatus = ref('')

const joinLink = computed(() => `${window.location.origin}/join/${props.tournament.joinCode}`)
const qrCodeUrl = computed(
  () =>
    `https://api.qrserver.com/v1/create-qr-code/?size=220x220&data=${encodeURIComponent(joinLink.value)}`
)

const copyText = async (value: string, label: string) => {
  try {
    await navigator.clipboard.writeText(value)
    copyStatus.value = `${label} copied.`
  } catch {
    copyStatus.value = `Unable to copy ${label.toLowerCase()}.`
  }
}
</script>

<template>
  <section class="card invite-panel">
    <div class="section-heading">
      <h2>Invite Players</h2>
      <p>Share the QR code or direct link so each player can claim their own tournament seat.</p>
    </div>

    <div class="invite-layout">
      <img :src="qrCodeUrl" :alt="`QR code for ${tournament.name}`" class="invite-qr" />

      <div class="invite-details">
        <div class="invite-actions">
          <button type="button" @click="copyText(joinLink, 'Invite link')">Copy invite link</button>
        </div>

        <label class="invite-field">
          <span>Invite link</span>
          <input :value="joinLink" readonly />
        </label>

        <p v-if="copyStatus" class="invite-feedback">{{ copyStatus }}</p>
        <p v-if="!tournament.joinEnabled" class="invite-warning">
          Joining is currently unavailable because this tournament is closed.
        </p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.invite-panel {
  display: grid;
  gap: 1rem;
  height: 100%;
  align-content: start;
}

.section-heading h2,
.section-heading p {
  margin: 0;
}

.section-heading p {
  margin-top: 0.3rem;
  color: var(--text-soft);
}

.invite-layout {
  display: grid;
  gap: 1rem;
}

.invite-qr {
  width: min(100%, 220px);
  aspect-ratio: 1;
  border-radius: 10px;
  border: 1px solid var(--border-strong);
  background: white;
  padding: 0.75rem;
}

.invite-details {
  display: grid;
  gap: 0.85rem;
  align-content: start;
}

.invite-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
}

.invite-field {
  display: grid;
  gap: 0.35rem;
  color: var(--text-soft);
}

.invite-feedback,
.invite-warning {
  margin: 0;
}

.invite-feedback {
  color: var(--success);
}

.invite-warning {
  color: var(--danger);
}

@media (min-width: 720px) {
  .invite-layout {
    grid-template-columns: auto minmax(0, 1fr);
    align-items: center;
  }
}
</style>
