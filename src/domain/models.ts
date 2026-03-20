export type MatchStatus = 'pending' | 'completed'

export interface Player {
  id: string
  name: string
  claimedByEmail?: string | null
  claimedByName?: string | null
}

export interface Match {
  id: string
  playerAId: string
  playerBId: string
  status: MatchStatus
  winsA: number
  winsB: number
  winnerId?: string
}

export interface TournamentParticipant {
  email: string
  name: string
  playerId: string
  playerName: string
  joinedAt: string
}

export interface Tournament {
  id: string
  name: string
  createdAt: string
  players: Player[]
  matches: Match[]
  status: 'setup' | 'active' | 'complete'
  joinCode: string
  joinEnabled: boolean
  joinCodeExpiresAt?: string | null
  participants: TournamentParticipant[]
  currentUserJoined: boolean
}

export interface JoinTournamentPreview {
  tournamentId: string
  tournamentName: string
  joinCode: string
  status: Tournament['status']
  joinEnabled: boolean
  joinCodeExpiresAt?: string | null
  availablePlayers: Player[]
}

export interface StandingRow {
  playerId: string
  playerName: string
  matchWins: number
  matchLosses: number
  gameWins: number
  gameLosses: number
  gameDiff: number
  matchWinPct: number
  gameWinPct: number
}
