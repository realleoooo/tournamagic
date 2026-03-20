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
      <h2>Invite &amp; Share</h2>
      <p class="muted">
        Share the join code, invite link, or QR code so authenticated friends can join this tournament.
      </p>
    </div>

    <div class="invite-layout">
      <img :src="qrCodeUrl" :alt="`QR code for ${tournament.name}`" class="invite-qr" />

      <div class="grid invite-details">
        <div class="invite-meta">
          <span class="invite-label">Join code</span>
          <strong class="invite-code">{{ tournament.joinCode }}</strong>
        </div>

        <div class="invite-actions">
          <button type="button" @click="copyText(tournament.joinCode, 'Join code')">Copy code</button>
          <button type="button" class="secondary" @click="copyText(joinLink, 'Join link')">Copy link</button>
        </div>

        <label class="invite-field">
          <span>Join link</span>
          <input :value="joinLink" readonly />
        </label>

        <p v-if="copyStatus" class="invite-feedback">{{ copyStatus }}</p>
        <p v-if="!tournament.joinEnabled || tournament.status === 'complete'" class="invite-warning">
          Joining is currently unavailable for this tournament.
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
  font-size: 1.6rem;
  letter-spacing: 0.16em;
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
