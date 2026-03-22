<script setup lang="ts">
import { getSeatInitial } from '@/components/join/joinViewFormatters'
import type { Player } from '@/domain/models'

defineProps<{
  player: Player
  selected: boolean
}>()

const emit = defineEmits<{
  select: [id: string]
}>()
</script>

<template>
  <button
    type="button"
    :class="['join-seat', { 'join-seat--active': selected }]"
    @click="emit('select', player.id)"
  >
    <span class="join-seat__badge">{{ getSeatInitial(player.name) }}</span>

    <span class="join-seat__copy">
      <strong>{{ player.name }}</strong>
      <span>Unclaimed player slot</span>
    </span>
  </button>
</template>

<style scoped>
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

.join-seat__copy strong,
.join-seat__copy span {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-seat__copy strong {
  color: #f4e3bc;
  font-size: 1rem;
}

.join-seat__copy span {
  color: #dcc59c;
  font-size: 0.9rem;
}
</style>
