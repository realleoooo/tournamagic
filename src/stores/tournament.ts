import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { buildStandings } from '@/domain/ranking'
import type { Tournament } from '@/domain/models'
import { tournamentApi, type TournamentSummary } from '@/api/tournamentApi'

export const useTournamentStore = defineStore('tournament', () => {
  const tournament = ref<Tournament | undefined>(undefined)
  const tournaments = ref<TournamentSummary[]>([])
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

  const withLoading = async <T>(fn: () => Promise<T>, options?: { suppressError?: boolean }): Promise<T | undefined> => {
    loading.value = true
    if (!options?.suppressError) {
      error.value = undefined
    }
    try {
      return await fn()
    } catch (err) {
      if (!options?.suppressError) {
        error.value = err instanceof Error ? err.message : 'Unknown error'
      }
      return undefined
    } finally {
      loading.value = false
    }
  }

  const refreshTournamentList = async (options?: { suppressError?: boolean }) => {
    const list = await withLoading(() => tournamentApi.listTournaments(), options)
    if (list) {
      tournaments.value = list
    }
  }

  const bootstrap = async () => {
    await refreshTournamentList({ suppressError: true })

    const savedId = tournamentApi.getStoredTournamentId()
    if (!savedId) {
      error.value = undefined
      return
    }

    const fetched = await withLoading(() => tournamentApi.fetchTournament(savedId), { suppressError: true })
    if (fetched) {
      tournament.value = fetched
      error.value = undefined
      return
    }

    tournamentApi.setStoredTournamentId(undefined)
    tournament.value = undefined
    error.value = undefined
  }

  const openTournament = async (id: string) => {
    const fetched = await withLoading(() => tournamentApi.fetchTournament(id))
    if (fetched) {
      tournament.value = fetched
      tournamentApi.setStoredTournamentId(id)
    }
    return fetched
  }

  const createTournament = async (name: string, playerNames: string[]) => {
    const created = await withLoading(() => tournamentApi.createTournament({ name, players: playerNames }))
    if (created) {
      tournament.value = created
      await refreshTournamentList()
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
      await refreshTournamentList()
    }
  }

  const clearResult = async (matchId: string) => {
    if (!tournament.value) return
    const updated = await withLoading(() => tournamentApi.clearResult(tournament.value!.id, matchId))
    if (updated) {
      tournament.value = updated
      await refreshTournamentList()
    }
  }

  const updateMatchTimer = async (
    matchId: string,
    payload: {
      running?: boolean
      reset?: boolean
      direction?: 'up' | 'down'
      durationSeconds?: number
      notifyIntervalSeconds?: number
    }
  ) => {
    if (!tournament.value) return
    const updated = await withLoading(() =>
      tournamentApi.updateMatchTimer(tournament.value!.id, matchId, payload)
    )
    if (updated) {
      tournament.value = updated
    }
  }

  const leaveTournament = () => {
    tournament.value = undefined
  }

  const resetTournament = async () => {
    if (!tournament.value) return
    const id = tournament.value.id
    await withLoading(() => tournamentApi.deleteTournament(id))
    tournament.value = undefined
    await refreshTournamentList()
  }

  const deleteFromList = async (id: string) => {
    await withLoading(() => tournamentApi.deleteTournament(id))
    if (tournament.value?.id === id) {
      tournament.value = undefined
    }
    await refreshTournamentList()
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
    tournaments,
    loading,
    error,
    standings,
    completion,
    bootstrap,
    refreshTournamentList,
    openTournament,
    createTournament,
    submitResult,
    clearResult,
    updateMatchTimer,
    leaveTournament,
    resetTournament,
    deleteFromList,
    resolveName,
    remainingOpponents
  }
})
