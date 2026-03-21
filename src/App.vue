<template>
  <div class="app-bg">
    <header class="topbar">
      <div class="topbar__content">
        <div>
          <h1>TournaMagic</h1>
          <p>Draft Tournament Tracker</p>
        </div>
        <div class="topbar__actions">
          <button
            v-if="showTournamentMenuButton"
            type="button"
            class="secondary sidebar-trigger"
            aria-label="Open tournament menu"
            @click="toggleTournamentMenu"
          >
            <span class="sidebar-trigger__icon" aria-hidden="true">
              <span></span>
              <span></span>
              <span></span>
            </span>
            <span>Menu</span>
          </button>
          <label class="theme-picker" for="theme-select">
            <span>Mana Theme</span>
            <select id="theme-select" v-model="selectedTheme" @change="applyTheme(selectedTheme)">
              <option
                v-for="theme in themeOptions"
                :key="theme.value"
                :value="theme.value"
              >
                {{ theme.label }}
              </option>
            </select>
          </label>
          <div v-if="authStore.isAuthenticated" class="auth-status">
            <span>{{ authStore.user?.name }}</span>
            <button type="button" class="secondary" @click="logout">Log out</button>
          </div>
        </div>
      </div>
    </header>
    <main class="layout">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const THEME_STORAGE_KEY = 'tournamagic.theme'

const themeOptions = [
  { value: 'green', label: 'Green · Growth' },
  { value: 'red', label: 'Red · Chaos' },
  { value: 'blue', label: 'Blue · Insight' },
  { value: 'white', label: 'White · Order' },
  { value: 'black', label: 'Black · Ambition' }
] as const

type ThemeValue = (typeof themeOptions)[number]['value']

const selectedTheme = ref<ThemeValue>('green')
const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()
const showTournamentMenuButton = computed(() => route.name === 'tournament')

const applyTheme = (theme: ThemeValue) => {
  document.documentElement.dataset.theme = theme
  window.localStorage.setItem(THEME_STORAGE_KEY, theme)
}

const toggleTournamentMenu = () => {
  window.dispatchEvent(new CustomEvent('tournamagic:toggle-sidebar'))
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  authStore.initialize()

  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY) as ThemeValue | null
  const isKnownTheme = themeOptions.some((option) => option.value === storedTheme)
  selectedTheme.value = isKnownTheme && storedTheme ? storedTheme : 'green'
  applyTheme(selectedTheme.value)
})
</script>
