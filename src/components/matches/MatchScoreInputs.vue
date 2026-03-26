<script setup lang="ts">
defineProps<{
  winsA: number
  winsB: number
}>()

const emit = defineEmits<{
  'update:winsA': [value: number]
  'update:winsB': [value: number]
}>()
</script>

<template>
  <div class="match-score">
    <label class="match-score__box">
      <input
        :value="winsA"
        type="number"
        min="0"
        max="2"
        step="1"
        inputmode="numeric"
        @input="emit('update:winsA', Number(($event.target as HTMLInputElement).value))"
      />
    </label>
    <span class="match-score__divider">-</span>
    <label class="match-score__box">
      <input
        :value="winsB"
        type="number"
        min="0"
        max="2"
        step="1"
        inputmode="numeric"
        @input="emit('update:winsB', Number(($event.target as HTMLInputElement).value))"
      />
    </label>
  </div>
</template>

<style scoped>
.match-score {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.match-score__box {
  width: 118px;
  display: block;
}

.match-score__box input {
  width: 100%;
  text-align: center;
  font-size: 2.05rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  color: #f7e4bd;
  background:
    linear-gradient(180deg, rgba(32, 68, 31, 0.96), rgba(22, 45, 17, 0.98)),
    #244920;
  border-color: rgba(194, 153, 83, 0.86);
  border-radius: 12px;
  padding: 0.55rem 0.6rem;
}

.match-score__divider {
  color: #f4e0b4;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 2rem;
}

@media (max-width: 720px) {
  .match-score__box {
    width: 92px;
  }

  .match-score__box input {
    font-size: 1.7rem;
  }
}
</style>
