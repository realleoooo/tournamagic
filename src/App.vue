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

            <button
              type="button"
              class="brand-link"
              :aria-label="isTournamentRoute ? 'Back to overview' : 'Go to overview'"
              @click="goOverview"
            >
              <h1>TournaMagic</h1>
              <p>Draft tournament tracker</p>
            </button>
          </div>

          <div class="topbar__actions">
            <div v-if="authStore.isAuthenticated" class="auth-status">
              <button type="button" class="auth-status__user auth-status__user--link" @click="goToOwnProfile">
                <strong>{{ authStore.user?.name }}</strong>
                <span>{{ authStore.user?.email }}</span>
              </button>
              <button type="button" class="secondary" @click="logout">Log out</button>
            </div>
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

    <AppToastViewport />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppToastViewport from '@/components/shared/AppToastViewport.vue'
import { useAuthStore } from '@/stores/auth'
import { useTournamentStore } from '@/stores/tournament'
import { useTournamentShell } from '@/composables/useTournamentShell'
const authStore = useAuthStore()
const tournamentStore = useTournamentStore()
const router = useRouter()
const route = useRoute()
const shell = useTournamentShell()

const isTournamentRoute = computed(() => route.name === 'tournament')
const isSetupRoute = computed(() => route.name === 'setup')
const isJoinRoute = computed(() => route.name === 'join-tournament')
const isProfileRoute = computed(() => route.name === 'player-profile')
const isAuthRoute = computed(() => route.name === 'login' || route.name === 'register')
const isFantasyRoute = computed(
  () =>
    isTournamentRoute.value ||
    isSetupRoute.value ||
    isJoinRoute.value ||
    isProfileRoute.value ||
    isAuthRoute.value
)

const logout = () => {
  authStore.logout()
  router.push('/login')
}

const goOverview = () => {
  shell.closeSidebar()
  tournamentStore.leaveTournament()
  router.replace('/')
}

const goToOwnProfile = () => {
  if (!authStore.user?.email) {
    return
  }

  router.push({
    name: 'player-profile',
    params: { email: authStore.user.email }
  })
}

onMounted(() => {
  authStore.initialize()
})
</script>
