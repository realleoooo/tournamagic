<script setup lang="ts">
import type { TournamentSection } from '@/composables/useTournamentShell'
import type { TournamentSectionItem } from '@/components/tournaments/tournamentSections'

defineProps<{
  sections: TournamentSectionItem[]
  activeSection: TournamentSection
  sidebarOpen: boolean
}>()

const emit = defineEmits<{
  close: []
  selectSection: [section: TournamentSection]
}>()
</script>

<template>
  <div v-if="sidebarOpen" class="sidebar-backdrop" aria-hidden="true" @click="emit('close')"></div>

  <aside :class="['sidebar', { 'sidebar--open': sidebarOpen }]">
    <div class="sidebar__header">
      <strong>Navigation</strong>
      <button type="button" class="secondary sidebar__close" @click="emit('close')">Close</button>
    </div>

    <nav class="sidebar__nav" aria-label="Tournament sections">
      <button
        v-for="section in sections"
        :key="section.id"
        type="button"
        :class="['sidebar__link', { 'sidebar__link--active': activeSection === section.id }]"
        @click="emit('selectSection', section.id)"
      >
        <component :is="section.icon" class="sidebar__icon" />
        <span>{{ section.label }}</span>
      </button>
    </nav>
  </aside>
</template>

<style scoped>
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 17, 0.48);
  opacity: 1;
  transition: opacity 160ms ease;
  z-index: 20;
}

.sidebar {
  position: relative;
  background:
    radial-gradient(circle at 50% 0, rgba(46, 122, 206, 0.08), transparent 18%),
    linear-gradient(180deg, #20140f, #120b08 58%, #0d0908);
  border: 1px solid #76542e;
  min-height: 0;
  padding: 1.05rem 0.95rem 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  box-shadow:
    inset 0 0 0 1px rgba(220, 172, 91, 0.15),
    0 18px 34px rgba(0, 0, 0, 0.3);
}

.sidebar__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem 0.6rem 0.55rem;
  border-bottom: 1px solid rgba(191, 143, 74, 0.26);
  margin-bottom: 0.4rem;
}

.sidebar::before,
.sidebar::after {
  content: '';
  position: absolute;
  left: 50%;
  width: 28px;
  height: 28px;
  margin-left: -14px;
  transform: rotate(45deg);
  border: 1px solid rgba(216, 170, 90, 0.7);
  box-shadow:
    inset 0 0 0 1px rgba(255, 225, 159, 0.18),
    0 0 12px rgba(255, 192, 95, 0.22);
}

.sidebar::before {
  top: -14px;
  background: linear-gradient(135deg, #4f87ac, #1c3240 72%, #140f0d);
}

.sidebar::after {
  bottom: -14px;
  background: linear-gradient(135deg, #ffcf80, #9f5b18 72%, #140f0d);
}

.sidebar__close {
  display: none;
}

.sidebar__nav {
  display: grid;
  gap: 0.5rem;
}

.sidebar__link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  justify-content: flex-start;
  background:
    linear-gradient(180deg, rgba(85, 55, 29, 0.18), rgba(26, 17, 12, 0.88)),
    #1a120d;
  color: #e7d1a7;
  border: 1px solid rgba(161, 118, 62, 0.5);
  border-radius: 12px;
  padding: 0.92rem 1rem;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.03rem;
  box-shadow:
    inset 0 1px 0 rgba(255, 225, 164, 0.1),
    0 0 0 1px rgba(42, 25, 17, 0.45);
}

.sidebar__link:hover,
.sidebar__link--active {
  color: #fff0c7;
  border-color: #ba8d4c;
  background:
    linear-gradient(180deg, rgba(161, 125, 54, 0.28), rgba(59, 39, 22, 0.94)),
    #1f1711;
  box-shadow:
    inset 0 1px 0 rgba(255, 225, 164, 0.16),
    0 0 16px rgba(209, 167, 71, 0.16);
}

.sidebar__header strong {
  color: #f1dcaf;
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
  font-size: 1.12rem;
  letter-spacing: 0.02em;
}

.sidebar__icon {
  width: 20px;
  height: 20px;
  flex: none;
  padding: 3px;
  border-radius: 999px;
}

@media (max-width: 959px) {
  .sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    width: min(280px, calc(100vw - 2rem));
    transform: translateX(-100%);
    transition: transform 180ms ease;
    z-index: 30;
  }

  .sidebar--open {
    transform: translateX(0);
  }

  .sidebar__close {
    display: inline-flex;
  }
}
</style>
