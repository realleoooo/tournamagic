<script setup lang="ts">
import AppToastCard from '@/components/shared/AppToastCard.vue'
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()
</script>

<template>
  <Teleport to="body">
    <div v-if="toastStore.toasts.length" class="toast-viewport" aria-live="polite" aria-atomic="true">
      <TransitionGroup name="toast">
        <AppToastCard
          v-for="toast in toastStore.toasts"
          :key="toast.id"
          :toast="toast"
          @dismiss="toastStore.removeToast"
        />
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style scoped>
.toast-viewport {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 50;
  display: grid;
  gap: 0.75rem;
  width: min(calc(100vw - 2rem), 360px);
  pointer-events: none;
}

.toast-enter-active,
.toast-leave-active {
  transition: transform 180ms ease, opacity 180ms ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.98);
}

@media (max-width: 720px) {
  .toast-viewport {
    top: auto;
    right: 0.75rem;
    bottom: 0.75rem;
    left: 0.75rem;
    width: auto;
  }
}
</style>
