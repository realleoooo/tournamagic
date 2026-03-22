import { onMounted, ref, watch, type Ref } from 'vue'
import { tournamentApi } from '@/api/tournamentApi'
import type { PlayerProfile } from '@/domain/models'

export const usePlayerProfile = (email: Readonly<Ref<string>>) => {
  const profile = ref<PlayerProfile | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const loadProfile = async () => {
    if (!email.value) {
      error.value = 'No player email was provided.'
      profile.value = null
      return
    }

    loading.value = true
    error.value = null

    try {
      profile.value = await tournamentApi.fetchPlayerProfile(email.value)
    } catch (err) {
      profile.value = null
      error.value = err instanceof Error ? err.message : 'Unable to load this profile right now.'
    } finally {
      loading.value = false
    }
  }

  watch(email, loadProfile)
  onMounted(loadProfile)

  return {
    error,
    loading,
    loadProfile,
    profile
  }
}
