<script setup lang="ts">
import SkeletonBlock from '@/components/shared/SkeletonBlock.vue'

const rows = [
  { titleWidth: '12.5rem', summaryWidth: '18rem' },
  { titleWidth: '10.75rem', summaryWidth: '15.5rem' },
  { titleWidth: '13.25rem', summaryWidth: '17rem' }
]
</script>

<template>
  <div class="setup-list" aria-busy="true" aria-label="Loading your tournaments">
    <article v-for="(row, index) in rows" :key="index" class="setup-tournament">
      <div class="setup-tournament__copy">
        <SkeletonBlock width="row.titleWidth" height="1.15rem" />
        <div class="setup-tournament__summary">
          <SkeletonBlock width="row.summaryWidth" height="0.95rem" />
          <SkeletonBlock width="11rem" height="0.95rem" />
        </div>
      </div>

      <div class="setup-tournament__actions">
        <SkeletonBlock variant="light" width="92px" height="2.75rem" radius="0.85rem" />
        <SkeletonBlock variant="light" width="92px" height="2.75rem" radius="0.85rem" />
      </div>
    </article>
  </div>
</template>

<style scoped>
.setup-list {
  margin-top: 1rem;
  display: grid;
  gap: 0.95rem;
  align-content: start;
}

.setup-tournament {
  position: relative;
  border: 1px solid rgba(179, 126, 63, 0.88);
  border-radius: 22px;
  background:
    linear-gradient(180deg, rgba(94, 58, 36, 0.22), rgba(27, 17, 12, 0.18)),
    rgba(56, 33, 22, 0.28);
  padding: 1.2rem 1.15rem;
  box-shadow:
    inset 0 0 0 1px rgba(234, 187, 104, 0.13),
    0 10px 18px rgba(0, 0, 0, 0.16);
}

.setup-tournament::before {
  content: '';
  position: absolute;
  inset: 10px;
  border: 1px solid rgba(203, 155, 84, 0.16);
  border-radius: 14px;
  pointer-events: none;
}

.setup-tournament__copy {
  display: grid;
  gap: 0.45rem;
}

.setup-tournament__summary {
  display: grid;
  gap: 0.28rem;
}

.setup-tournament__actions {
  display: flex;
  gap: 0.7rem;
  flex-wrap: wrap;
  margin-top: 1rem;
}
</style>
