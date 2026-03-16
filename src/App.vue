<template>
  <div class="app-bg">
    <header class="topbar">
      <div class="topbar__content">
        <div>
          <h1>TournaMagic</h1>
          <p>Draft Tournament Tracker</p>
        </div>
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
      </div>
    </header>
    <main class="layout">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

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

const applyTheme = (theme: ThemeValue) => {
  document.documentElement.dataset.theme = theme
  window.localStorage.setItem(THEME_STORAGE_KEY, theme)
}

onMounted(() => {
  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY) as ThemeValue | null
  const isKnownTheme = themeOptions.some((option) => option.value === storedTheme)
  selectedTheme.value = isKnownTheme && storedTheme ? storedTheme : 'green'
  applyTheme(selectedTheme.value)
})
</script>
