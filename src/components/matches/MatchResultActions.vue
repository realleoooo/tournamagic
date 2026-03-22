<script setup lang="ts">
const emit = defineEmits<{
  submit: []
  clear: []
}>()
</script>

<template>
  <div class="match-row__actions">
    <button type="button" class="match-action match-action--primary" @click="emit('submit')">Save Result</button>
    <button type="button" class="match-action match-action--secondary" @click="emit('clear')">Undo</button>
  </div>
</template>

<style scoped>
.match-row__actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.match-action {
  min-width: 188px;
  border-radius: 12px;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1rem;
}

.match-action--primary {
  background:
    linear-gradient(180deg, rgba(116, 179, 64, 0.96), rgba(42, 92, 29, 0.98)),
    #417e30;
  border-color: rgba(204, 165, 88, 0.84);
  color: #fff6d9;
}

.match-action--primary:hover {
  background:
    linear-gradient(180deg, rgba(128, 195, 70, 0.98), rgba(52, 108, 36, 0.98)),
    #498737;
}

.match-action--secondary {
  background:
    linear-gradient(180deg, rgba(86, 59, 33, 0.96), rgba(42, 27, 17, 0.98)),
    #41291a;
  border-color: rgba(174, 128, 66, 0.82);
  color: #f1dcaf;
}

.match-action--secondary:hover {
  background:
    linear-gradient(180deg, rgba(100, 68, 38, 0.96), rgba(50, 31, 19, 0.98)),
    #4a2f1d;
}
</style>
