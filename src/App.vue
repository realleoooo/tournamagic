<template>
  <div class="app-bg">
    <header class="topbar">
      <div class="topbar__content">
        <div>
          <h1>TournaMagic</h1>
          <p>Draft Tournament Tracker</p>
        </div>
        <div class="topbar__actions">
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
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

const applyTheme = (theme: ThemeValue) => {
  document.documentElement.dataset.theme = theme
  window.localStorage.setItem(THEME_STORAGE_KEY, theme)
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
