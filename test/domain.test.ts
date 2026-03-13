import { describe, expect, it } from 'vitest'
import { createRoundRobinMatches } from '../src/domain/pairing'
import { parseBestOfThree } from '../src/domain/scoring'
import { buildStandings } from '../src/domain/ranking'

const players = [
  { id: 'a', name: 'Alice' },
  { id: 'b', name: 'Bob' },
  { id: 'c', name: 'Chandra' }
]

describe('domain logic', () => {
  it('generates round robin pairings', () => {
    const matches = createRoundRobinMatches(players)
    expect(matches).toHaveLength(3)
  })

  it('accepts only valid bo3 finals', () => {
    expect(parseBestOfThree(2, 0).winnerSide).toBe('A')
    expect(parseBestOfThree(1, 2).winnerSide).toBe('B')
    expect(() => parseBestOfThree(1, 1)).toThrow()
  })

  it('builds standings ordered by wins then game diff', () => {
    const standings = buildStandings(players, [
      {
        id: 'a-b',
        playerAId: 'a',
        playerBId: 'b',
        roundNumber: 1,
        status: 'completed',
        winsA: 2,
        winsB: 1,
        winnerId: 'a'
      },
      {
        id: 'a-c',
        playerAId: 'a',
        playerBId: 'c',
        roundNumber: 2,
        status: 'completed',
        winsA: 0,
        winsB: 2,
        winnerId: 'c'
      },
      {
        id: 'b-c',
        playerAId: 'b',
        playerBId: 'c',
        roundNumber: 3,
        status: 'completed',
        winsA: 1,
        winsB: 2,
        winnerId: 'c'
      }
    ])

    expect(standings[0].playerName).toBe('Chandra')
    expect(standings[2].playerName).toBe('Bob')
  })
})
