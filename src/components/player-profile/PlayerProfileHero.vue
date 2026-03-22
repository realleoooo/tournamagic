<script setup lang="ts">
defineProps<{
  name: string
  email: string
  currentUser?: boolean
  hasTournamentContext: boolean
}>()

defineEmits<{
  openTournament: []
  openOverview: []
}>()
</script>

<template>
  <header class="profile-hero">
    <div class="profile-hero__copy">
      <span class="profile-hero__eyebrow">{{ currentUser ? 'Your profile' : 'Player profile' }}</span>
      <h2>{{ name }}</h2>
      <p>{{ email }}</p>
    </div>

    <div class="profile-hero__actions">
      <button v-if="hasTournamentContext" type="button" class="secondary" @click="$emit('openTournament')">
        Back to tournament
      </button>
      <button type="button" @click="$emit('openOverview')">Tournament hall</button>
    </div>
  </header>
</template>

<style scoped>
.profile-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  padding: 1.4rem 1.5rem;
  border: 1px solid rgba(201, 153, 82, 0.52);
  background:
    linear-gradient(180deg, rgba(85, 53, 33, 0.28), rgba(29, 18, 13, 0.24)),
    rgba(57, 34, 22, 0.28);
  box-shadow:
    inset 0 0 0 1px rgba(247, 213, 153, 0.08),
    0 18px 34px rgba(0, 0, 0, 0.24);
}

.profile-hero__copy {
  display: grid;
  gap: 0.45rem;
}

.profile-hero__eyebrow {
  display: inline-flex;
  width: fit-content;
  padding: 0.34rem 0.75rem;
  border-radius: 999px;
  border: 1px solid rgba(238, 202, 137, 0.28);
  background: rgba(34, 21, 15, 0.38);
  color: #f0d8a8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.75rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.profile-hero h2 {
  margin: 0;
  color: #f6e3b8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: clamp(2rem, 4vw, 2.8rem);
  line-height: 1;
}

.profile-hero p {
  margin: 0;
  color: #e2c89c;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.profile-hero__actions {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
  justify-content: flex-end;
}

@media (max-width: 720px) {
  .profile-hero {
    padding: 1.1rem;
    flex-direction: column;
  }

  .profile-hero__actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
