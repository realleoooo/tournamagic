<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTournamentStore } from '@/stores/tournament'

const store = useTournamentStore()
const route = useRoute()
const router = useRouter()
const successMessage = ref('')
const selectedPlayerId = ref('')

const joinCode = computed(() => String(route.params.code ?? ''))
const preview = computed(() => store.joinPreview)
const availablePlayers = computed(() => preview.value?.availablePlayers ?? [])
const availableSeatLabel = computed(() => {
  const count = availablePlayers.value.length
  return `${count} open ${count === 1 ? 'seat' : 'seats'}`
})
const joinStateLabel = computed(() => (preview.value?.joinEnabled ? 'Invite open' : 'Invite closed'))
const selectedPlayerName = computed(
  () => availablePlayers.value.find((player) => player.id === selectedPlayerId.value)?.name ?? ''
)
const canJoin = computed(
  () => Boolean(preview.value?.joinEnabled && availablePlayers.value.length > 0 && selectedPlayerId.value)
)

const join = async () => {
  if (!selectedPlayerId.value) {
    store.error = 'Choose which player you are before joining.'
    return
  }

  successMessage.value = ''
  const joined = await store.joinTournament(joinCode.value, selectedPlayerId.value)

  if (joined) {
    successMessage.value = `You joined ${joined.name} as ${selectedPlayerName.value || 'your seat'}. Redirecting to the tournament view…`
    window.setTimeout(() => {
      router.replace('/tournament')
    }, 900)
  }
}

onMounted(async () => {
  const previewResult = await store.previewJoin(joinCode.value)
  if (previewResult?.availablePlayers.length) {
    selectedPlayerId.value = previewResult.availablePlayers[0].id
  }
})
</script>

<template>
  <div class="join-page">
    <section class="join-hero">
      <div class="join-hero__eyebrow">Invitation</div>
      <h2>{{ preview?.tournamentName ?? 'Join Tournament' }}</h2>
      <p>Claim your seat from the invite link and step straight into the tournament.</p>
    </section>

    <section class="join-board">
      <div class="join-board__header">
        <div>
          <h3>Join Tournament</h3>
          <p v-if="preview">Choose the player slot that belongs to you in this tournament.</p>
          <p v-else>Loading invite details for this tournament.</p>
        </div>

        <div v-if="preview" class="join-board__meta">
          <span class="join-chip">{{ joinStateLabel }}</span>
          <span class="join-chip">{{ availableSeatLabel }}</span>
        </div>
      </div>

      <section v-if="successMessage" class="join-message join-message--success">
        {{ successMessage }}
      </section>

      <section v-if="store.error" class="join-message join-message--error">
        {{ store.error }}
      </section>

      <section v-if="store.loading && !preview" class="join-message">
        Preparing your invitation…
      </section>

      <div v-if="preview" class="join-grid">
        <article class="join-card">
          <div class="join-card__header">
            <strong>Tournament Details</strong>
            <span>{{ preview.status }}</span>
          </div>

          <dl class="join-details">
            <div>
              <dt>Tournament</dt>
              <dd>{{ preview.tournamentName }}</dd>
            </div>

            <div>
              <dt>Join code</dt>
              <dd>{{ preview.joinCode }}</dd>
            </div>

            <div>
              <dt>Invitation</dt>
              <dd>{{ preview.joinEnabled ? 'Open for joining' : 'Currently closed' }}</dd>
            </div>
          </dl>

          <p v-if="!preview.joinEnabled" class="join-note join-note--warn">
            Joining is currently unavailable because this tournament is closed.
          </p>
          <p v-else class="join-note">
            Share this page with the right player, then claim the matching seat below.
          </p>
        </article>

        <article class="join-card">
          <div class="join-card__header">
            <strong>Choose Your Seat</strong>
            <span>{{ availablePlayers.length }} available</span>
          </div>

          <div v-if="availablePlayers.length > 0" class="join-seat-list">
            <button
              v-for="player in availablePlayers"
              :key="player.id"
              type="button"
              :class="['join-seat', { 'join-seat--active': selectedPlayerId === player.id }]"
              @click="selectedPlayerId = player.id"
            >
              <span class="join-seat__badge">{{ player.name.slice(0, 1).toUpperCase() }}</span>

              <span class="join-seat__copy">
                <strong>{{ player.name }}</strong>
                <span>Unclaimed player slot</span>
              </span>
            </button>
          </div>

          <div v-else class="join-empty">
            <strong>No open seats</strong>
            <p>All players in this tournament have already been claimed.</p>
          </div>

          <div class="join-actions">
            <button type="button" class="join-actions__primary" :disabled="store.loading || !canJoin" @click="join">
              Join Tournament
            </button>
            <button type="button" class="secondary join-actions__secondary" @click="router.replace('/')">
              Back to Overview
            </button>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<style scoped>
.join-page {
  display: grid;
  gap: 1.1rem;
  width: min(100%, 980px);
  margin: 0 auto;
  padding: 0.65rem 0 2rem;
}

.join-hero {
  display: grid;
  justify-items: center;
  gap: 0.6rem;
  padding: 1.2rem 1rem 0.35rem;
  text-align: center;
}

.join-hero__eyebrow {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 128px;
  padding: 0.34rem 0.9rem;
  border-radius: 999px;
  border: 1px solid rgba(239, 203, 136, 0.34);
  background: rgba(34, 21, 15, 0.42);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.72rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.join-hero h2 {
  margin: 0;
  color: #f7e5bc;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: clamp(2rem, 3.8vw, 2.8rem);
  line-height: 1;
}

.join-hero p {
  max-width: 34rem;
  margin: 0;
  color: #e5cfa2;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
  line-height: 1.45;
}

.join-board {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(209, 159, 85, 0.5);
  padding: 1.6rem;
  background:
    linear-gradient(180deg, rgba(91, 56, 34, 0.28), rgba(34, 20, 14, 0.2)),
    rgba(67, 40, 26, 0.24);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 40px rgba(0, 0, 0, 0.24);
}

.join-board::before {
  content: '';
  position: absolute;
  inset: 14px;
  border: 1px solid rgba(230, 186, 108, 0.15);
  border-radius: 20px;
  pointer-events: none;
}

.join-board__header {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 1rem;
}

.join-board__header h3 {
  margin: 0;
  color: #f4dfb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.85rem;
}

.join-board__header p {
  margin: 0.55rem 0 0;
  color: #e4cda3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.join-board__meta {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.join-chip {
  padding: 0.45rem 0.85rem;
  border-radius: 999px;
  border: 1px solid rgba(218, 174, 96, 0.34);
  background: rgba(29, 18, 12, 0.38);
  color: #efd9ac;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  white-space: nowrap;
}

.join-message {
  position: relative;
  z-index: 1;
  margin-top: 1rem;
  border: 1px solid rgba(158, 113, 56, 0.72);
  background:
    linear-gradient(180deg, rgba(73, 44, 26, 0.95), rgba(32, 20, 13, 0.98)),
    #29180f;
  color: #efd9af;
  padding: 0.95rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-message--success {
  color: #d6ebaf;
}

.join-message--error {
  color: #f0b29a;
}

.join-grid {
  position: relative;
  z-index: 1;
  margin-top: 1rem;
  display: grid;
  grid-template-columns: minmax(280px, 0.9fr) minmax(320px, 1.1fr);
  gap: 1rem;
}

.join-card {
  position: relative;
  min-height: 0;
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

.join-card::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 1px solid rgba(203, 155, 84, 0.16);
  border-radius: 14px;
  pointer-events: none;
}

.join-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.9rem;
  padding-bottom: 0.65rem;
  border-bottom: 1px solid rgba(176, 131, 70, 0.38);
}

.join-card__header strong,
.join-card__header span,
.join-details dt,
.join-details dd,
.join-note,
.join-empty strong,
.join-empty p,
.join-seat__copy strong,
.join-seat__copy span {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-card__header strong {
  color: #f4dfb3;
  font-size: 1.12rem;
}

.join-card__header span {
  color: #e4cda3;
  font-size: 0.94rem;
}

.join-details {
  margin: 0;
  display: grid;
  gap: 0.8rem;
}

.join-details div {
  display: grid;
  gap: 0.2rem;
}

.join-details dt {
  color: #cfae7d;
  font-size: 0.82rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.join-details dd {
  margin: 0;
  color: #f4e3bc;
  font-size: 1.02rem;
}

.join-note {
  margin: 1rem 0 0;
  color: #e3c99a;
  font-size: 0.95rem;
  line-height: 1.45;
}

.join-note--warn {
  color: #efb299;
}

.join-seat-list {
  display: grid;
  gap: 0.75rem;
}

.join-seat {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  gap: 0.85rem;
  width: 100%;
  padding: 0.9rem;
  border-radius: 18px;
  border: 1px solid rgba(176, 131, 70, 0.46);
  background:
    linear-gradient(180deg, rgba(75, 47, 28, 0.42), rgba(27, 17, 12, 0.48)),
    rgba(33, 20, 14, 0.42);
  color: #f2dfb6;
  text-align: left;
  box-shadow:
    inset 0 0 0 1px rgba(230, 188, 109, 0.08),
    0 8px 16px rgba(0, 0, 0, 0.14);
}

.join-seat:hover {
  background:
    linear-gradient(180deg, rgba(92, 58, 34, 0.52), rgba(31, 19, 13, 0.56)),
    rgba(40, 24, 17, 0.48);
}

.join-seat--active {
  border-color: rgba(222, 183, 103, 0.8);
  background:
    linear-gradient(180deg, rgba(159, 124, 56, 0.34), rgba(73, 47, 24, 0.78)),
    rgba(55, 34, 20, 0.72);
  box-shadow:
    inset 0 0 0 1px rgba(250, 219, 151, 0.14),
    0 0 18px rgba(215, 173, 86, 0.16);
}

.join-seat__badge {
  width: 46px;
  height: 46px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  border: 2px solid rgba(214, 168, 90, 0.84);
  background:
    radial-gradient(circle at 35% 35%, rgba(117, 176, 65, 0.88), rgba(32, 68, 25, 0.96)),
    #2f5a28;
  color: #fff0ca;
  font-size: 1.05rem;
  box-shadow:
    inset 0 1px 0 rgba(223, 255, 195, 0.24),
    0 0 0 2px rgba(72, 44, 20, 0.32);
}

.join-seat__copy {
  display: grid;
  gap: 0.18rem;
  min-width: 0;
}

.join-seat__copy strong {
  color: #f4e3bc;
  font-size: 1rem;
}

.join-seat__copy span {
  color: #dcc59c;
  font-size: 0.9rem;
}

.join-empty {
  display: grid;
  gap: 0.4rem;
  min-height: 180px;
  place-items: center;
  text-align: center;
}

.join-empty strong {
  color: #f4dfb3;
  font-size: 1.3rem;
}

.join-empty p {
  margin: 0;
  max-width: 20rem;
  color: #e2c79a;
  font-size: 0.95rem;
}

.join-actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}

.join-actions__primary,
.join-actions__secondary {
  min-width: 180px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.98rem;
}

.join-actions__primary {
  background:
    linear-gradient(180deg, rgba(126, 170, 74, 0.96), rgba(57, 96, 35, 0.98)),
    #5d8f40;
  border-color: rgba(207, 170, 103, 0.78);
  color: #f6f1d2;
}

.join-actions__primary:hover {
  background:
    linear-gradient(180deg, rgba(138, 184, 82, 0.96), rgba(67, 108, 43, 0.98)),
    #699c49;
}

@media (max-width: 900px) {
  .join-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .join-page {
    padding-bottom: 1.25rem;
  }

  .join-hero {
    padding: 0.7rem 0.5rem 0.35rem;
  }

  .join-board {
    padding: 1.15rem;
  }

  .join-board__header {
    align-items: start;
    flex-direction: column;
  }

  .join-board__meta {
    width: 100%;
    justify-content: flex-start;
  }

  .join-actions__primary,
  .join-actions__secondary {
    width: 100%;
  }
}
</style>
