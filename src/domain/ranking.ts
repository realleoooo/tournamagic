import type { Match, Player, StandingRow } from './models'

const findDirectWinner = (matches: Match[], a: string, b: string): string | undefined => {
  const match = matches.find(
    (item) =>
      item.status === 'completed' &&
      ((item.playerAId === a && item.playerBId === b) || (item.playerAId === b && item.playerBId === a))
  )

  return match?.winnerId
}

export const buildStandings = (players: Player[], matches: Match[]): StandingRow[] => {
  const rows = new Map<string, StandingRow>()

  players.forEach((player) => {
    rows.set(player.id, {
      playerId: player.id,
      playerName: player.name,
      matchWins: 0,
      matchLosses: 0,
      gameWins: 0,
      gameLosses: 0,
      gameDiff: 0,
      matchWinPct: 0,
      gameWinPct: 0
    })
  })

  const completed = matches.filter((match) => match.status === 'completed' && match.winnerId)

  completed.forEach((match) => {
    const rowA = rows.get(match.playerAId)
    const rowB = rows.get(match.playerBId)
    if (!rowA || !rowB) {
      return
    }

    rowA.gameWins += match.winsA
    rowA.gameLosses += match.winsB
    rowB.gameWins += match.winsB
    rowB.gameLosses += match.winsA

    if (match.winnerId === match.playerAId) {
      rowA.matchWins += 1
      rowB.matchLosses += 1
    } else {
      rowB.matchWins += 1
      rowA.matchLosses += 1
    }
  })

  const standings = [...rows.values()].map((row) => {
    const matchTotal = row.matchWins + row.matchLosses
    const gameTotal = row.gameWins + row.gameLosses
    return {
      ...row,
      gameDiff: row.gameWins - row.gameLosses,
      matchWinPct: matchTotal === 0 ? 0 : row.matchWins / matchTotal,
      gameWinPct: gameTotal === 0 ? 0 : row.gameWins / gameTotal
    }
  })

  standings.sort((a, b) => {
    if (b.matchWins !== a.matchWins) return b.matchWins - a.matchWins

    const sameTier = standings.filter((entry) => entry.matchWins === a.matchWins)
    if (sameTier.length === 2) {
      const directWinner = findDirectWinner(matches, a.playerId, b.playerId)
      if (directWinner === a.playerId) return -1
      if (directWinner === b.playerId) return 1
    }

    if (b.gameDiff !== a.gameDiff) return b.gameDiff - a.gameDiff
    if (b.gameWins !== a.gameWins) return b.gameWins - a.gameWins
    return a.playerName.localeCompare(b.playerName)
  })

  return standings
}
