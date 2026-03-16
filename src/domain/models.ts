export type MatchStatus = 'pending' | 'completed'
export type TimerDirection = 'up' | 'down'

export interface Player {
  id: string
  name: string
}

export interface Match {
  id: string
  playerAId: string
  playerBId: string
  status: MatchStatus
  winsA: number
  winsB: number
  winnerId?: string
  timerDirection: TimerDirection
  timerDurationSeconds: number
  timerNotifyIntervalSeconds: number
  timerRunning: boolean
  timerElapsedSeconds: number
  nextNotificationAtSeconds: number
  remainingSeconds: number
}

export interface Tournament {
  id: string
  name: string
  createdAt: string
  players: Player[]
  matches: Match[]
  status: 'setup' | 'active' | 'complete'
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
