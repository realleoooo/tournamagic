<script setup lang="ts">
import { computed } from 'vue'
import type { ToastItem } from '@/stores/toast'

const props = defineProps<{
  toast: ToastItem
}>()

const emit = defineEmits<{
  dismiss: [id: number]
}>()

const eyebrowLabel = computed(() => (props.toast.tone === 'error' ? 'Error' : 'Success'))
</script>

<template>
  <section :class="['toast-card', `toast-card--${toast.tone}`]" role="status">
    <div class="toast-card__eyebrow">{{ eyebrowLabel }}</div>
    <div class="toast-card__message">{{ toast.message }}</div>
    <button
      type="button"
      class="toast-card__close"
      aria-label="Dismiss notification"
      @click="emit('dismiss', toast.id)"
    >
      Close
    </button>
  </section>
</template>

<style scoped>
.toast-card {
  pointer-events: auto;
  position: relative;
  overflow: hidden;
  display: grid;
  gap: 0.35rem;
  padding: 0.95rem 1rem 0.95rem 1.1rem;
  border-radius: 18px;
  border: 1px solid transparent;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.08),
    0 18px 40px rgba(0, 0, 0, 0.28);
}

.toast-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 4px;
  border-radius: 18px 0 0 18px;
}

.toast-card--success {
  border-color: color-mix(in srgb, var(--ok) 45%, var(--border-strong));
  background:
    radial-gradient(circle at top right, color-mix(in srgb, var(--ok) 30%, transparent), transparent 42%),
    linear-gradient(180deg, color-mix(in srgb, var(--bg-elevated) 82%, var(--ok)), var(--bg-surface));
}

.toast-card--success::before {
  background: linear-gradient(180deg, color-mix(in srgb, var(--ok) 84%, white), var(--ok));
}

.toast-card--error {
  border-color: color-mix(in srgb, var(--danger) 52%, var(--border-strong));
  background:
    radial-gradient(circle at top right, color-mix(in srgb, var(--danger) 26%, transparent), transparent 42%),
    linear-gradient(180deg, color-mix(in srgb, var(--bg-elevated) 84%, var(--danger)), var(--bg-surface));
}

.toast-card--error::before {
  background: linear-gradient(180deg, color-mix(in srgb, var(--danger) 82%, white), var(--danger));
}

.toast-card__eyebrow {
  font-size: 0.72rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.toast-card--success .toast-card__eyebrow {
  color: color-mix(in srgb, var(--ok) 78%, white);
}

.toast-card--error .toast-card__eyebrow {
  color: color-mix(in srgb, var(--danger) 74%, white);
}

.toast-card__message {
  max-width: 26ch;
  padding-right: 5rem;
  color: var(--text-main);
  font-size: 0.96rem;
  line-height: 1.35;
}

.toast-card__close {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  padding: 0.34rem 0.55rem;
  border-radius: 999px;
  background: color-mix(in srgb, var(--bg-muted) 82%, black);
  color: var(--text-soft);
  font-size: 0.78rem;
}

.toast-card--success .toast-card__close {
  border-color: color-mix(in srgb, var(--border-strong) 85%, var(--ok));
}

.toast-card--error .toast-card__close {
  border-color: color-mix(in srgb, var(--border-strong) 82%, var(--danger));
}

.toast-card__close:hover {
  background: color-mix(in srgb, var(--bg-elevated) 86%, black);
  color: var(--text-main);
}

@media (max-width: 720px) {
  .toast-card__message {
    max-width: none;
    padding-right: 4.5rem;
  }
}
</style>
