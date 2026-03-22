import type { Match, Player } from '@/domain/models'

export interface MatchRound {
  number: number
  matches: Match[]
}

const BYE = '__bye__'

const toMatchKey = (a: string, b: string) => [a, b].sort().join('::')

export const buildMatchRounds = (players: Player[], matches: Match[]): MatchRound[] => {
  const playerIds = players.map((player) => player.id)
  if (playerIds.length < 2) {
    return []
  }

  const rotation = [...playerIds]
  if (rotation.length % 2 === 1) {
    rotation.push(BYE)
  }

  const roundsCount = rotation.length - 1
  const matchesByKey = new Map(matches.map((match) => [toMatchKey(match.playerAId, match.playerBId), match]))
  const rounds: MatchRound[] = []

  for (let roundIndex = 0; roundIndex < roundsCount; roundIndex += 1) {
    const roundMatches: Match[] = []

    for (let playerIndex = 0; playerIndex < rotation.length / 2; playerIndex += 1) {
      const playerAId = rotation[playerIndex]
      const playerBId = rotation[rotation.length - 1 - playerIndex]

      if (playerAId === BYE || playerBId === BYE) {
        continue
      }

      const match = matchesByKey.get(toMatchKey(playerAId, playerBId))
      if (match) {
        roundMatches.push(match)
      }
    }

    rounds.push({ number: roundIndex + 1, matches: roundMatches })

    const fixedPlayer = rotation[0]
    const rotatingPlayers = rotation.slice(1)
    rotatingPlayers.unshift(rotatingPlayers.pop()!)
    rotation.splice(0, rotation.length, fixedPlayer, ...rotatingPlayers)
  }

  return rounds
}
