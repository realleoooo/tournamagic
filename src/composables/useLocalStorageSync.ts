import type { Tournament } from '@/domain/models'

const STORAGE_KEY = 'tournamagic.current'

export const loadTournament = (): Tournament | undefined => {
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as Tournament) : undefined
  } catch {
    return undefined
  }
}

export const saveTournament = (tournament: Tournament | undefined): void => {
  if (!tournament) {
    window.localStorage.removeItem(STORAGE_KEY)
    return
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(tournament))
}
