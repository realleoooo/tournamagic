import type { Tournament } from '@/domain/models'

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? '/api'
const ID_KEY = 'tournamagic.tournamentId'

type CreateTournamentPayload = { name: string; players: string[] }
type MatchPayload = { winsA: number; winsB: number }

const readId = () => window.localStorage.getItem(ID_KEY) ?? undefined
const writeId = (id?: string) => {
  if (!id) {
    window.localStorage.removeItem(ID_KEY)
    return
  }
  window.localStorage.setItem(ID_KEY, id)
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {})
    }
  })

  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export const tournamentApi = {
  getStoredTournamentId: readId,
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
    writeId(undefined)
  }
}
