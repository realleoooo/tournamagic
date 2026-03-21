<template>
  <div :class="['app-bg', { 'app-bg--invite': isFantasyRoute }]">
    <header :class="['topbar', { 'topbar--invite': isFantasyRoute }]">
      <div :class="['topbar__content', { 'topbar__content--auth': isAuthRoute }]">
        <div :class="['topbar__row', { 'topbar__row--auth': isAuthRoute }]">
          <div class="brand-block">
            <button
              v-if="isTournamentRoute"
              type="button"
              class="menu-toggle secondary"
              aria-label="Open navigation menu"
              @click="shell.toggleSidebar()"
            >
              <span></span>
              <span></span>
              <span></span>
            </button>

            <div>
              <h1>TournaMagic</h1>
              <p>Draft tournament tracker</p>
            </div>
          </div>

          <div class="topbar__actions">
            <div class="theme-picker">
              <span>Theme</span>
              <div class="theme-swatches" role="group" aria-label="Theme picker">
                <button
                  v-for="theme in themeOptions"
                  :key="theme.value"
                  type="button"
                  :class="['theme-swatch', `theme-swatch--${theme.value}`, { 'theme-swatch--active': selectedTheme === theme.value }]"
                  :aria-label="`Use ${theme.label} theme`"
                  @click="selectedTheme = theme.value; applyTheme(theme.value)"
                ></button>
              </div>
            </div>

            <div v-if="authStore.isAuthenticated" class="auth-status">
              <div class="auth-status__user">
                <strong>{{ authStore.user?.name }}</strong>
                <span>{{ authStore.user?.email }}</span>
              </div>
              <button type="button" class="secondary" @click="logout">Log out</button>
            </div>
          </div>
        </div>

        <div v-if="showTournamentHeader" class="topbar__row topbar__row--tournament">
          <div class="tournament-summary">
            <strong>{{ tournament?.name }}</strong>
            <span>{{ tournament?.players.length }} players</span>
            <span>{{ tournament?.status }}</span>
          </div>

          <div class="tournament-actions">
            <button type="button" class="secondary" @click="goOverview">Back to overview</button>
            <button type="button" class="warn" @click="onReset">Reset tournament</button>
            <button v-if="canStartTournament" type="button" @click="onStart">Start tournament</button>
            <button v-if="tournament?.currentUserJoined" type="button" class="secondary" @click="onLeave">
              Leave tournament
            </button>
          </div>
        </div>
      </div>
    </header>

    <main
      :class="[
        'layout',
        {
          'layout--tournament': isTournamentRoute,
          'layout--invite': isFantasyRoute,
          'layout--setup': isSetupRoute
        }
      ]"
    >
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useTournamentStore } from '@/stores/tournament'
import { useTournamentShell } from '@/composables/useTournamentShell'

const THEME_STORAGE_KEY = 'tournamagic.theme'

const themeOptions = [
  { value: 'green', label: 'Green' },
  { value: 'red', label: 'Red' },
  { value: 'blue', label: 'Blue' },
  { value: 'white', label: 'White' },
  { value: 'black', label: 'Black' }
] as const

type ThemeValue = (typeof themeOptions)[number]['value']

const selectedTheme = ref<ThemeValue>('green')
const authStore = useAuthStore()
const tournamentStore = useTournamentStore()
const router = useRouter()
const route = useRoute()
const shell = useTournamentShell()

const tournament = computed(() => tournamentStore.tournament)
const isTournamentRoute = computed(() => route.name === 'tournament')
const isSetupRoute = computed(() => route.name === 'setup')
const isAuthRoute = computed(() => route.name === 'login' || route.name === 'register')
const isFantasyRoute = computed(() => isTournamentRoute.value || isSetupRoute.value || isAuthRoute.value)
const showTournamentHeader = computed(() => isTournamentRoute.value && Boolean(tournament.value))
const canStartTournament = computed(
  () =>
    tournament.value?.status === 'setup' &&
    tournament.value.players.length >= 2 &&
    tournament.value.players.every((player) => player.claimedByEmail)
)

const applyTheme = (theme: ThemeValue) => {
  document.documentElement.dataset.theme = theme
  window.localStorage.setItem(THEME_STORAGE_KEY, theme)
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

const goOverview = () => {
  shell.closeSidebar()
  tournamentStore.leaveTournament()
  router.replace('/')
}

const onReset = async () => {
  await tournamentStore.resetTournament()
  shell.closeSidebar()
  router.replace('/')
}

const onStart = async () => {
  await tournamentStore.startTournament()
}

const onLeave = async () => {
  const updated = await tournamentStore.leaveJoinedTournament()
  if (updated) {
    tournamentStore.leaveTournament()
    shell.closeSidebar()
    router.replace('/')
  }
}

onMounted(() => {
  authStore.initialize()

  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY) as ThemeValue | null
  const isKnownTheme = themeOptions.some((option) => option.value === storedTheme)
  selectedTheme.value = isKnownTheme && storedTheme ? storedTheme : 'green'
  applyTheme(selectedTheme.value)
})
</script>
