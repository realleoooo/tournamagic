import type { JoinTournamentPreview, Tournament } from '@/domain/models'
import { getAuthHeaders } from '@/api/authSession'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api'
const ID_KEY = 'tournamagic.tournamentId'

export type TournamentSummary = {
  id: string
  name: string
  status: string
  createdAt: string
  playerCount: number
  completedMatches: number
  totalMatches: number
}

type CreateTournamentPayload = { name: string; players: string[] }
type MatchPayload = { winsA: number; winsB: number }
type JoinTournamentPayload = { code: string }

const readId = () => window.localStorage.getItem(ID_KEY) ?? undefined
const writeId = (id?: string) => {
  if (!id) {
    window.localStorage.removeItem(ID_KEY)
    return
  }
  window.localStorage.setItem(ID_KEY, id)
}

const parseErrorMessage = (status: number, text: string) => {
  const trimmed = text.trim()

  if (!trimmed) {
    return `Request failed: ${status}`
  }

  if (trimmed.startsWith('<')) {
    if (status >= 500) {
      return 'The backend is currently unavailable. Please try again in a moment.'
    }

    return 'The server returned an unexpected response.'
  }

  return trimmed
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeaders(),
      ...(init?.headers ?? {})
    }
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(parseErrorMessage(response.status, text))
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export const tournamentApi = {
  getStoredTournamentId: readId,
  setStoredTournamentId: writeId,
  async listTournaments(): Promise<TournamentSummary[]> {
    return request<TournamentSummary[]>('/tournaments')
  },
  async createTournament(payload: CreateTournamentPayload): Promise<Tournament> {
    const tournament = await request<Tournament>('/tournaments', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    writeId(tournament.id)
    return tournament
  },
  async fetchTournament(id: string): Promise<Tournament> {
    return request<Tournament>(`/tournaments/${id}`)
  },
  async previewJoin(code: string): Promise<JoinTournamentPreview> {
    return request<JoinTournamentPreview>(`/tournaments/join/${encodeURIComponent(code)}`)
  },
  async joinTournament(payload: JoinTournamentPayload): Promise<Tournament> {
    const tournament = await request<Tournament>('/tournaments/join', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    writeId(tournament.id)
    return tournament
  },
  async submitResult(tournamentId: string, matchId: string, payload: MatchPayload): Promise<Tournament> {
    return request<Tournament>(`/tournaments/${tournamentId}/matches/${matchId}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    })
  },
  async clearResult(tournamentId: string, matchId: string): Promise<Tournament> {
    return request<Tournament>(`/tournaments/${tournamentId}/matches/${matchId}`, {
      method: 'DELETE'
    })
  },
  async deleteTournament(tournamentId: string): Promise<void> {
    await request<void>(`/tournaments/${tournamentId}`, { method: 'DELETE' })
    const current = readId()
    if (current === tournamentId) {
      writeId(undefined)
    }
  }
}
