<script setup lang="ts">
import { computed } from 'vue'
import type { RemainingOpponentGroup } from '@/components/tournaments/remainingOpponents'

const props = defineProps<{
  player: RemainingOpponentGroup
  alternateCountTone: boolean
}>()

const emit = defineEmits<{
  openPlayerProfile: [playerId: string]
}>()

const countClass = computed(() => `opponent-card__count--${Math.min(props.player.remaining.length, 3)}`)
</script>

<template>
  <article class="opponent-card">
    <div class="opponent-card__header">
      <div>
        <button
          v-if="player.claimedByEmail"
          type="button"
          class="player-link"
          @click="emit('openPlayerProfile', player.id)"
        >
          {{ player.name }}
        </button>
        <strong v-else>{{ player.name }}</strong>
        <p>
          <button
            v-if="player.claimedByEmail"
            type="button"
            class="player-link player-link--secondary"
            @click="emit('openPlayerProfile', player.id)"
          >
            {{ player.claimedByLabel }}
          </button>
          <template v-else>{{ player.claimedByLabel }}</template>
        </p>
      </div>

      <span
        :class="['opponent-card__count', countClass, { 'opponent-card__count--alt': alternateCountTone }]"
      >
        {{ player.remaining.length }} left
      </span>
    </div>

    <ul v-if="player.remaining.length > 0" class="opponent-card__list">
      <li v-for="opponent in player.remaining" :key="`${player.id}-${opponent}`">{{ opponent }}</li>
    </ul>
    <p v-else class="opponent-card__empty">All matches completed.</p>
  </article>
</template>

<style scoped>
.opponent-card {
  position: relative;
  border: 1px solid #94652b;
  background:
    linear-gradient(180deg, rgba(58, 35, 22, 0.94), rgba(25, 17, 12, 0.98)),
    #24170f;
  padding: 1rem 1.05rem;
  display: grid;
  gap: 0.85rem;
  box-shadow:
    inset 0 0 0 1px rgba(214, 171, 92, 0.12),
    0 10px 18px rgba(0, 0, 0, 0.18);
}

.opponent-card::before,
.opponent-card::after {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  border: 1px solid rgba(220, 174, 97, 0.7);
  transform: rotate(45deg);
  background: linear-gradient(135deg, rgba(255, 222, 151, 0.8), rgba(138, 87, 34, 0.45));
}

.opponent-card::before {
  left: -6px;
  bottom: 34px;
}

.opponent-card::after {
  right: -6px;
  bottom: 34px;
}

.opponent-card__header {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  align-items: flex-start;
  padding-bottom: 0.85rem;
  border-bottom: 1px solid rgba(150, 108, 58, 0.52);
}

.opponent-card__header strong {
  color: #f3deb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
}

.player-link {
  padding: 0;
  border: 0;
  background: transparent;
  color: #f3deb3;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
  font-weight: 700;
  box-shadow: none;
}

.player-link:hover {
  background: transparent;
  color: #fff4d5;
  text-decoration: underline;
  text-underline-offset: 0.18em;
}

.player-link--secondary {
  color: #e1c89d;
  font-size: 0.92rem;
  font-weight: 400;
}

.opponent-card__header p {
  margin: 0.4rem 0 0;
  color: #e1c89d;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.92rem;
}

.opponent-card__count {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.02rem;
  font-weight: 700;
  white-space: nowrap;
}

.opponent-card__count--3 {
  color: #9edb78;
}

.opponent-card__count--3.opponent-card__count--alt {
  color: #b9edf1;
}

.opponent-card__count--2 {
  color: #f0a06f;
}

.opponent-card__count--1 {
  color: #efd7a7;
}

.opponent-card__count--0 {
  color: #bda98b;
}

.opponent-card__list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  gap: 0;
}

.opponent-card__list li {
  padding: 0.5rem 0;
  border-top: 1px solid rgba(128, 92, 50, 0.38);
  color: #f0ddb8;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 0.96rem;
}

.opponent-card__list li:first-child {
  border-top: 0;
  padding-top: 0;
}

.opponent-card__empty {
  margin: 0.35rem 0 0;
  color: #d9c29d;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}
</style>
