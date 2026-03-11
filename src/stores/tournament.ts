import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import { createRoundRobinMatches } from '@/domain/pairing'
import { buildStandings } from '@/domain/ranking'
import { parseBestOfThree } from '@/domain/scoring'
import type { Match, Player, Tournament } from '@/domain/models'
import { loadTournament, saveTournament } from '@/composables/useLocalStorageSync'

const uid = () => Math.random().toString(36).slice(2, 10)

export const useTournamentStore = defineStore('tournament', () => {
  const tournament = ref<Tournament | undefined>(loadTournament())

  const isSetup = computed(() => !tournament.value || tournament.value.status === 'setup')
  const standings = computed(() =>
    tournament.value ? buildStandings(tournament.value.players, tournament.value.matches) : []
  )
  const pendingMatches = computed(() =>
    tournament.value ? tournament.value.matches.filter((m) => m.status === 'pending') : []
  )
  const completion = computed(() => {
    if (!tournament.value) return { completed: 0, total: 0 }
    const total = tournament.value.matches.length
    const completed = tournament.value.matches.filter((m) => m.status === 'completed').length
    return { completed, total }
  })

  watch(
    tournament,
    (value) => saveTournament(value),
    { deep: true }
  )

  const createTournament = (name: string, playerNames: string[]) => {
    const players: Player[] = playerNames.map((playerName) => ({ id: uid(), name: playerName.trim() }))
    const matches = createRoundRobinMatches(players)

    tournament.value = {
      id: uid(),
      name,
      createdAt: new Date().toISOString(),
      players,
      matches,
      status: 'active'
    }
  }

  const submitResult = (matchId: string, winsA: number, winsB: number) => {
    if (!tournament.value) return

    const parsed = parseBestOfThree(winsA, winsB)
    const match = tournament.value.matches.find((item) => item.id === matchId)
    if (!match) return

    match.winsA = parsed.winsA
    match.winsB = parsed.winsB
    match.status = 'completed'
    match.winnerId = parsed.winnerSide === 'A' ? match.playerAId : match.playerBId

    if (tournament.value.matches.every((item) => item.status === 'completed')) {
      tournament.value.status = 'complete'
    }
  }

  const clearResult = (matchId: string) => {
    if (!tournament.value) return
    const match = tournament.value.matches.find((item) => item.id === matchId)
    if (!match) return

    match.status = 'pending'
    match.winsA = 0
    match.winsB = 0
    match.winnerId = undefined
    tournament.value.status = 'active'
  }

  const resetTournament = () => {
    tournament.value = undefined
  }

  const resolveName = (playerId: string) =>
    tournament.value?.players.find((player) => player.id === playerId)?.name ?? playerId

  const remainingOpponents = (playerId: string): string[] => {
    if (!tournament.value) return []
    const pending = tournament.value.matches.filter(
      (match) =>
        match.status === 'pending' && (match.playerAId === playerId || match.playerBId === playerId)
    )

    return pending.map((match) =>
      resolveName(match.playerAId === playerId ? match.playerBId : match.playerAId)
    )
  }

  return {
    tournament,
    isSetup,
    standings,
    pendingMatches,
    completion,
    createTournament,
    submitResult,
    clearResult,
    resetTournament,
    resolveName,
    remainingOpponents
  }
})
