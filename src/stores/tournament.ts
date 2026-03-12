import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { buildStandings } from '@/domain/ranking'
import type { Tournament } from '@/domain/models'
import { tournamentApi } from '@/api/tournamentApi'

export const useTournamentStore = defineStore('tournament', () => {
  const tournament = ref<Tournament | undefined>(undefined)
  const loading = ref(false)
  const error = ref<string | undefined>(undefined)

  const standings = computed(() =>
    tournament.value ? buildStandings(tournament.value.players, tournament.value.matches) : []
  )

  const completion = computed(() => {
    if (!tournament.value) return { completed: 0, total: 0 }
    const total = tournament.value.matches.length
    const completed = tournament.value.matches.filter((m) => m.status === 'completed').length
    return { completed, total }
  })

  const withLoading = async <T>(fn: () => Promise<T>): Promise<T | undefined> => {
    loading.value = true
    error.value = undefined
    try {
      return await fn()
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Unknown error'
      return undefined
    } finally {
      loading.value = false
    }
  }

  const bootstrap = async () => {
    const savedId = tournamentApi.getStoredTournamentId()
    if (!savedId) return

    const fetched = await withLoading(() => tournamentApi.fetchTournament(savedId))
    if (fetched) {
      tournament.value = fetched
    }
  }

  const createTournament = async (name: string, playerNames: string[]) => {
    const created = await withLoading(() => tournamentApi.createTournament({ name, players: playerNames }))
    if (created) {
      tournament.value = created
    }
    return created
  }

  const submitResult = async (matchId: string, winsA: number, winsB: number) => {
    if (!tournament.value) return
    const updated = await withLoading(() =>
      tournamentApi.submitResult(tournament.value!.id, matchId, { winsA, winsB })
    )
    if (updated) {
      tournament.value = updated
    }
  }

  const clearResult = async (matchId: string) => {
    if (!tournament.value) return
    const updated = await withLoading(() => tournamentApi.clearResult(tournament.value!.id, matchId))
    if (updated) {
      tournament.value = updated
    }
  }

  const resetTournament = async () => {
    if (!tournament.value) return
    await withLoading(() => tournamentApi.deleteTournament(tournament.value!.id))
    tournament.value = undefined
  }

  const resolveName = (playerId: string) =>
    tournament.value?.players.find((player) => player.id === playerId)?.name ?? playerId

  const remainingOpponents = (playerId: string): string[] => {
    if (!tournament.value) return []
    return tournament.value.matches
      .filter((match) =>
        match.status === 'pending' && (match.playerAId === playerId || match.playerBId === playerId)
      )
      .map((match) => resolveName(match.playerAId === playerId ? match.playerBId : match.playerAId))
  }

  return {
    tournament,
    loading,
    error,
    standings,
    completion,
    bootstrap,
    createTournament,
    submitResult,
    clearResult,
    resetTournament,
    resolveName,
    remainingOpponents
  }
})
