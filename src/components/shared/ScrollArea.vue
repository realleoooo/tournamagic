<script setup lang="ts">
withDefaults(
  defineProps<{
    maxHeight?: string
  }>(),
  {
    maxHeight: 'min(68vh, 720px)'
  }
)
</script>

<template>
  <div class="scroll-area" :style="{ '--scroll-area-max-height': maxHeight }">
    <div class="scroll-area__viewport">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.scroll-area {
  min-height: 0;
}

.scroll-area__viewport {
  min-height: 0;
  max-height: var(--scroll-area-max-height);
  overflow: auto;
  scrollbar-gutter: stable;
  scrollbar-width: thin;
  scrollbar-color: color-mix(in srgb, var(--accent-arcane) 70%, transparent) transparent;
}

.scroll-area__viewport::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

.scroll-area__viewport::-webkit-scrollbar-track {
  background: transparent;
}

.scroll-area__viewport::-webkit-scrollbar-thumb {
  background: color-mix(in srgb, var(--accent-arcane) 65%, transparent);
  border-radius: 999px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.scroll-area__viewport::-webkit-scrollbar-thumb:hover {
  background: color-mix(in srgb, var(--accent-arcane) 82%, transparent);
}
</style>
