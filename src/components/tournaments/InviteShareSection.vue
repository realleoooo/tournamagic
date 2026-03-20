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
  <section class="card grid">
    <div>
      <h2>Invite Players</h2>
      <p class="muted">Share this QR code so players can join the tournament with their own account.</p>
    </div>

    <div class="invite-layout">
      <img :src="qrCodeUrl" :alt="`QR code for ${tournament.name}`" class="invite-qr" />

      <div class="grid invite-details">
        <div class="invite-meta">
          <span class="invite-label">Invite link</span>
          <strong class="invite-code">Ready to scan</strong>
        </div>

        <div class="invite-actions">
          <button type="button" @click="copyText(joinLink, 'Invite link')">Copy invite link</button>
        </div>

        <label class="invite-field">
          <span>Invite link</span>
          <input :value="joinLink" readonly />
        </label>

        <p v-if="copyStatus" class="invite-feedback">{{ copyStatus }}</p>
        <p v-if="!tournament.joinEnabled || tournament.status !== 'setup'" class="invite-warning">
          Joining is currently unavailable because this tournament has already started.
        </p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.muted {
  margin: 0.35rem 0 0;
  color: var(--text-soft);
}

.invite-layout {
  display: grid;
  gap: 1rem;
}

.invite-qr {
  width: min(100%, 220px);
  aspect-ratio: 1;
  border-radius: 14px;
  border: 1px solid color-mix(in srgb, var(--accent-arcane) 35%, transparent);
  background: white;
  padding: 0.75rem;
}

.invite-details {
  align-content: start;
}

.invite-meta {
  display: grid;
  gap: 0.35rem;
}

.invite-label {
  color: var(--text-soft);
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.invite-code {
  font-size: 1.25rem;
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

.invite-feedback {
  margin: 0;
  color: var(--success);
}

.invite-warning {
  margin: 0;
  color: var(--danger);
}

@media (min-width: 720px) {
  .invite-layout {
    grid-template-columns: auto 1fr;
    align-items: center;
  }
}
</style>
