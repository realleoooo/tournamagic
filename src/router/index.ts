import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import SetupView from '@/views/SetupView.vue'
import TournamentView from '@/views/TournamentView.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import JoinTournamentView from '@/views/JoinTournamentView.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'setup', component: SetupView, meta: { requiresAuth: true } },
    { path: '/tournament', name: 'tournament', component: TournamentView, meta: { requiresAuth: true } },
    { path: '/join/:code', name: 'join-tournament', component: JoinTournamentView, meta: { requiresAuth: true } },
    { path: '/login', name: 'login', component: LoginView, meta: { guestOnly: true } },
    { path: '/register', name: 'register', component: RegisterView, meta: { guestOnly: true } }
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  authStore.initialize()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    const redirect = typeof to.query.redirect === 'string' ? to.query.redirect : undefined
    return redirect ?? { name: 'setup' }
  }

  return true
})
