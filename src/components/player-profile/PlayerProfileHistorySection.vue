<script setup lang="ts">
import type { PlayerProfileTournament } from '@/domain/models'
import PlayerProfileHistoryCard from '@/components/player-profile/PlayerProfileHistoryCard.vue'

defineProps<{
  tournaments: PlayerProfileTournament[]
}>()
</script>

<template>
  <section class="profile-history">
    <div class="profile-history__header">
      <div>
        <h3>Tournament history</h3>
        <p>Every tournament this player has joined through TournaMagic.</p>
      </div>
    </div>

    <div v-if="tournaments.length === 0" class="profile-history__empty">
      <strong>No tournaments yet</strong>
      <p>This profile will start filling up as soon as the player joins an event.</p>
    </div>

    <div v-else class="profile-history__list">
      <PlayerProfileHistoryCard
        v-for="entry in tournaments"
        :key="entry.tournamentId"
        :entry="entry"
      />
    </div>
  </section>
</template>

<style scoped>
.profile-history {
  padding: 1.35rem 1.4rem;
  border: 1px solid rgba(201, 153, 82, 0.52);
  background:
    linear-gradient(180deg, rgba(85, 53, 33, 0.28), rgba(29, 18, 13, 0.24)),
    rgba(57, 34, 22, 0.28);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 34px rgba(0, 0, 0, 0.24);
}

.profile-history__header {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
}

.profile-history h3 {
  margin: 0;
  color: #f6e3b8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-history__header p {
  margin: 0;
  color: #e2c89c;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-history__empty {
  margin-top: 1rem;
  padding: 1rem 1.05rem;
  border: 1px solid rgba(182, 136, 67, 0.46);
  background: rgba(30, 19, 13, 0.34);
}

.profile-history__empty strong {
  color: #f6e2b4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.2rem;
}

.profile-history__empty p {
  margin: 0.45rem 0 0;
  color: #ddc49b;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-history__list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
}

@media (max-width: 720px) {
  .profile-history {
    padding: 1.1rem;
  }
}
</style>
