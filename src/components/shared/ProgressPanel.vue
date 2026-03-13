<script setup lang="ts">
defineProps<{
  completed: number
  total: number
}>()
</script>

<template>
  <section class="card">
    <h2>Progress</h2>
    <p>{{ completed }}/{{ total }} matches completed</p>
    <progress :value="completed" :max="Math.max(total, 1)" style="width:100%"></progress>
  </section>
</template>
