<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  name: string
  rightAligned?: boolean
  clickable?: boolean
}>()

const emit = defineEmits<{
  open: []
}>()

const initials = computed(() =>
  props.name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() ?? '')
    .join('')
)
</script>

<template>
  <button
    v-if="clickable"
    type="button"
    :class="['match-player', 'match-player--button', { 'match-player--right': rightAligned }]"
    @click="emit('open')"
  >
    <template v-if="rightAligned">
      <div>
        <strong>{{ name }}</strong>
      </div>
      <div class="match-player__avatar match-player__avatar--right">
        <span>{{ initials }}</span>
      </div>
    </template>

    <template v-else>
      <div class="match-player__avatar">
        <span>{{ initials }}</span>
      </div>
      <div>
        <strong>{{ name }}</strong>
      </div>
    </template>
  </button>

  <section v-else :class="['match-player', { 'match-player--right': rightAligned }]">
    <template v-if="rightAligned">
      <div>
        <strong>{{ name }}</strong>
      </div>
      <div class="match-player__avatar match-player__avatar--right">
        <span>{{ initials }}</span>
      </div>
    </template>

    <template v-else>
      <div class="match-player__avatar">
        <span>{{ initials }}</span>
      </div>
      <div>
        <strong>{{ name }}</strong>
      </div>
    </template>
  </section>
</template>

<style scoped>
.match-player {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  min-width: 0;
}

.match-player--button {
  width: 100%;
  padding: 0;
  border: 0;
  background: transparent;
  color: inherit;
  text-align: left;
  box-shadow: none;
}

.match-player--button:hover {
  background: transparent;
}

.match-player--right {
  justify-content: flex-end;
  text-align: right;
}

.match-player strong {
  display: block;
  color: #f2ddb4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.05rem;
}

.match-player--button:hover strong,
.match-player--button:focus-visible strong {
  color: #fff5d7;
  text-decoration: underline;
  text-underline-offset: 0.2em;
}

.match-player__avatar {
  width: 64px;
  height: 64px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  border: 2px solid #c0924d;
  background:
    radial-gradient(circle at 35% 35%, rgba(113, 174, 63, 0.86), rgba(25, 60, 21, 0.92)),
    #295229;
  color: #fff1cf;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.25rem;
  box-shadow:
    inset 0 1px 0 rgba(223, 255, 195, 0.24),
    0 0 0 2px rgba(72, 44, 20, 0.45);
}

.match-player__avatar--right {
  background:
    radial-gradient(circle at 35% 35%, rgba(78, 147, 224, 0.9), rgba(18, 54, 95, 0.96)),
    #244f78;
}

@media (max-width: 1100px) {
  .match-player,
  .match-player--right {
    width: 100%;
    justify-content: center;
    text-align: center;
  }
}
</style>
