import type { TournamentSummary } from '@/api/tournamentApi'

export const formatTournamentCount = (count: number) => `${count} tournament${count === 1 ? '' : 's'}`

export const formatTournamentSummary = (tournament: TournamentSummary) =>
  `${tournament.playerCount} players - ${tournament.completedMatches} / ${tournament.totalMatches} matches - ${tournament.status}`
