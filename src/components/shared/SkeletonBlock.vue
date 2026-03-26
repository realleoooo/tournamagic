<script setup lang="ts">
import { computed } from 'vue'

type SkeletonVariant = 'default' | 'light'

const props = withDefaults(
  defineProps<{
    width?: string
    height?: string
    radius?: string
    variant?: SkeletonVariant
    circle?: boolean
  }>(),
  {
    width: '100%',
    height: '1rem',
    radius: '0.45rem',
    variant: 'default',
    circle: false
  }
)

const blockStyle = computed(() => ({
  width: props.width,
  height: props.height,
  borderRadius: props.circle ? '999px' : props.radius
}))
</script>

<template>
  <span
    aria-hidden="true"
    :class="['skeleton-block', `skeleton-block--${variant}`, { 'skeleton-block--circle': circle }]"
    :style="blockStyle"
  />
</template>

<style scoped>
.skeleton-block {
  position: relative;
  display: block;
  max-width: 100%;
  overflow: hidden;
}

.skeleton-block::before,
.skeleton-block::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
}

.skeleton-block--default::before {
  background:
    linear-gradient(180deg, rgba(244, 224, 182, 0.18), rgba(120, 78, 34, 0.22)),
    linear-gradient(135deg, rgba(132, 86, 36, 0.55), rgba(74, 45, 20, 0.78));
  box-shadow:
    inset 0 0 0 1px rgba(255, 236, 201, 0.08),
    inset 0 1px 1px rgba(255, 239, 213, 0.05);
}

.skeleton-block--light::before {
  background:
    linear-gradient(180deg, rgba(255, 248, 233, 0.92), rgba(241, 215, 163, 0.96)),
    linear-gradient(135deg, rgba(239, 210, 154, 0.94), rgba(213, 170, 96, 0.96));
  box-shadow:
    inset 0 0 0 1px rgba(119, 74, 27, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
}

.skeleton-block::after {
  background: linear-gradient(
    110deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 248, 229, 0.14) 35%,
    rgba(255, 255, 255, 0.4) 50%,
    rgba(255, 248, 229, 0.16) 65%,
    rgba(255, 255, 255, 0) 100%
  );
  transform: translateX(-140%);
  animation: skeleton-shimmer 1.8s ease-in-out infinite;
}

.skeleton-block--light::after {
  background: linear-gradient(
    110deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(255, 255, 255, 0.2) 35%,
    rgba(255, 255, 255, 0.55) 50%,
    rgba(255, 255, 255, 0.2) 65%,
    rgba(255, 255, 255, 0) 100%
  );
}

@keyframes skeleton-shimmer {
  from {
    transform: translateX(-140%);
  }

  to {
    transform: translateX(140%);
  }
}

@media (prefers-reduced-motion: reduce) {
  .skeleton-block::after {
    animation: none;
    transform: none;
    opacity: 0.35;
  }
}
</style>
