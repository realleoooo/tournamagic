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
  <section class="invite-page">
    <section class="invite-card invite-card--qr">
      <div class="invite-card__frame">
        <div class="invite-card__halo"></div>
        <img :src="qrCodeUrl" :alt="`QR code for ${tournament.name}`" class="invite-qr" />
      </div>

      <div class="invite-details">
        <div class="invite-actions">
          <button type="button" class="invite-button" @click="copyText(joinLink, 'Invite link')">
            Copy invite link
          </button>
        </div>

        <p v-if="copyStatus" class="invite-feedback">{{ copyStatus }}</p>

        <label class="invite-field">
          <span>Invite link</span>
          <input :value="joinLink" readonly />
        </label>

        <p v-if="!tournament.joinEnabled" class="invite-warning">
          Joining is currently unavailable because this tournament is closed.
        </p>
      </div>
    </section>

    <section class="invite-card invite-card--claims">
      <div class="claims-card__header">
        <strong>Player claims</strong>
        <span>{{ tournament.players.filter((player) => player.claimedByEmail).length }}/{{ tournament.players.length }}</span>
      </div>

      <ul class="claims-list">
        <li v-for="player in tournament.players" :key="player.id" class="claims-list__item">
          <div class="claims-list__identity">
            <strong>{{ player.name }}</strong>
            <p>{{ player.claimedByName ? `${player.claimedByName} - ${player.claimedByEmail}` : 'Unclaimed' }}</p>
          </div>

          <span :class="['claim-status', { 'claim-status--waiting': !player.claimedByEmail }]">
            {{ player.claimedByEmail ? 'Joined' : 'Waiting' }}
          </span>
        </li>
      </ul>
    </section>
  </section>
</template>

<style scoped>
.invite-page {
  display: grid;
  grid-template-columns: minmax(360px, 0.94fr) minmax(320px, 1.06fr);
  gap: 1.25rem;
  min-height: 100%;
}

.invite-card {
  position: relative;
  min-height: 0;
  background:
    linear-gradient(180deg, rgba(85, 57, 36, 0.2), rgba(25, 17, 12, 0.2)),
    rgba(28, 18, 13, 0.86);
  border: 1px solid rgba(173, 128, 68, 0.62);
  box-shadow:
    inset 0 0 0 1px rgba(224, 185, 108, 0.12),
    inset 0 18px 24px rgba(255, 223, 151, 0.03),
    0 14px 28px rgba(0, 0, 0, 0.26);
}

.invite-card::before,
.invite-card::after {
  content: '';
  position: absolute;
  width: 18px;
  height: 18px;
  border: 1px solid rgba(217, 173, 96, 0.7);
  background: linear-gradient(135deg, rgba(255, 223, 152, 0.9), rgba(129, 82, 31, 0.45));
  transform: rotate(45deg);
}

.invite-card::before {
  top: -9px;
  left: 50%;
  margin-left: -9px;
}

.invite-card::after {
  bottom: -9px;
  left: 50%;
  margin-left: -9px;
}

.invite-card--qr {
  padding: 1.6rem 1.35rem 1.35rem;
  display: grid;
  justify-items: center;
  align-content: start;
  gap: 1.15rem;
}

.invite-card__frame {
  position: relative;
  width: min(100%, 430px);
  aspect-ratio: 0.95;
  padding: 2.2rem;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at center, rgba(255, 195, 95, 0.22), transparent 43%),
    linear-gradient(180deg, rgba(96, 61, 34, 0.42), rgba(21, 13, 10, 0.56));
  border: 1px solid rgba(177, 129, 67, 0.8);
  box-shadow:
    inset 0 0 0 1px rgba(233, 194, 123, 0.16),
    0 0 0 8px rgba(18, 11, 9, 0.22);
}

.invite-card__frame::before,
.invite-card__frame::after {
  content: '';
  position: absolute;
  inset: 14px;
  border: 1px solid rgba(214, 167, 92, 0.2);
  pointer-events: none;
}

.invite-card__halo {
  position: absolute;
  inset: 18%;
  border-radius: 999px;
  border: 1px solid rgba(241, 189, 88, 0.24);
  box-shadow:
    0 0 28px rgba(244, 186, 75, 0.18),
    inset 0 0 18px rgba(240, 188, 86, 0.1);
}

.invite-qr {
  position: relative;
  z-index: 1;
  width: min(100%, 255px);
  aspect-ratio: 1;
  border: 6px solid #eadfc6;
  background: #f6f0de;
  padding: 0.7rem;
  box-shadow:
    0 0 0 1px rgba(91, 58, 27, 0.78),
    0 10px 24px rgba(0, 0, 0, 0.18);
}

.invite-details {
  width: min(100%, 360px);
  display: grid;
  gap: 0.75rem;
}

.invite-actions {
  display: flex;
  justify-content: center;
}

.invite-button {
  min-width: 228px;
  background:
    linear-gradient(180deg, rgba(146, 111, 48, 0.78), rgba(67, 44, 22, 0.98)),
    #5e3e22;
  border-color: rgba(207, 170, 103, 0.75);
  color: #f8e8c1;
  box-shadow:
    inset 0 1px 0 rgba(255, 236, 181, 0.25),
    0 0 0 1px rgba(78, 53, 28, 0.5);
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.invite-button:hover {
  background:
    linear-gradient(180deg, rgba(166, 127, 58, 0.92), rgba(77, 50, 25, 0.98)),
    #6b4726;
}

.invite-feedback,
.invite-warning {
  margin: 0;
  text-align: center;
  font-size: 0.92rem;
}

.invite-feedback {
  color: #d6c28b;
}

.invite-warning {
  color: #e39a84;
}

.invite-field {
  display: grid;
  gap: 0.4rem;
}

.invite-field span {
  color: #e6d2ab;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.invite-field input {
  background:
    linear-gradient(180deg, rgba(22, 15, 12, 0.96), rgba(17, 12, 10, 0.98)),
    #16110d;
  border-color: rgba(169, 125, 68, 0.82);
  color: #f2ddbc;
  border-radius: 12px;
  padding: 0.85rem 1rem;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.38);
}

.invite-card--claims {
  padding: 1rem 1rem 0.85rem;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 0.85rem;
}

.claims-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding: 0.45rem 0.55rem 0.85rem;
  border-bottom: 1px solid rgba(176, 131, 70, 0.5);
}

.claims-card__header strong {
  color: #f2dfb6;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.12rem;
}

.claims-card__header span {
  color: #ecd6a5;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
}

.claims-list {
  list-style: none;
  margin: 0;
  padding: 0 0.15rem 0;
  display: grid;
  gap: 0;
  overflow: auto;
  min-height: 0;
}

.claims-list__item {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 1rem;
  padding: 0.95rem 0.4rem;
  border-bottom: 1px solid rgba(143, 105, 58, 0.42);
}

.claims-list__identity strong {
  display: block;
  color: #f4e4c0;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.claims-list__identity p {
  margin: 0.28rem 0 0;
  color: #d5bf97;
  font-size: 0.95rem;
}

.claim-status {
  position: relative;
  padding: 0.35rem 0.8rem 0.35rem 1.75rem;
  border: 1px solid rgba(144, 168, 112, 0.4);
  border-radius: 12px;
  background:
    linear-gradient(180deg, rgba(110, 134, 74, 0.35), rgba(44, 61, 31, 0.8)),
    #2d3a21;
  color: #eef0d2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.claim-status::before {
  content: '';
  position: absolute;
  left: 0.55rem;
  top: 50%;
  width: 10px;
  height: 10px;
  margin-top: -5px;
  border-radius: 999px;
  background: radial-gradient(circle, #d8ff9d, #4e7f2a);
  box-shadow: 0 0 8px rgba(170, 215, 101, 0.45);
}

.claim-status--waiting {
  border-color: rgba(176, 120, 76, 0.42);
  background:
    linear-gradient(180deg, rgba(111, 72, 51, 0.38), rgba(62, 36, 22, 0.78)),
    #442617;
  color: #f0d8c0;
}

.claim-status--waiting::before {
  background: radial-gradient(circle, #ffcf9b, #9f4f2d);
  box-shadow: 0 0 8px rgba(221, 131, 83, 0.4);
}

@media (max-width: 1100px) {
  .invite-page {
    grid-template-columns: 1fr;
  }

  .invite-card--qr {
    order: 1;
  }

  .invite-card--claims {
    order: 2;
  }
}

@media (max-width: 720px) {
  .invite-card--qr {
    padding: 1.2rem 1rem 1rem;
  }

  .invite-card__frame {
    padding: 1.5rem;
  }

  .claims-list__item {
    grid-template-columns: 1fr;
    align-items: flex-start;
  }
}
</style>
