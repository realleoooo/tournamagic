import type { Match, Player } from './models'

export const createRoundRobinMatches = (players: Player[]): Match[] => {
  const matches: Match[] = []

  for (let i = 0; i < players.length; i += 1) {
    for (let j = i + 1; j < players.length; j += 1) {
      const a = players[i]
      const b = players[j]
      matches.push({
        id: `${a.id}-${b.id}`,
        playerAId: a.id,
        playerBId: b.id,
        roundNumber: 1,
        status: 'pending',
        winsA: 0,
        winsB: 0
      })
    }
  }

  return matches
}
