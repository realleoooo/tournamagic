import { describe, expect, it } from 'vitest'
import { buildMatchRounds } from '../src/utils/matchRounds'

const players = [
  { id: 'a', name: 'Alice' },
  { id: 'b', name: 'Bob' },
  { id: 'c', name: 'Chandra' },
  { id: 'd', name: 'Dack' }
]

describe('buildMatchRounds', () => {
  it('groups an even player count into full rounds', () => {
    const rounds = buildMatchRounds(players, [
      { id: 'ab', playerAId: 'a', playerBId: 'b', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'ac', playerAId: 'a', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'ad', playerAId: 'a', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'bc', playerAId: 'b', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'bd', playerAId: 'b', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'cd', playerAId: 'c', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 }
    ])

    expect(rounds).toHaveLength(3)
    expect(rounds.every((round) => round.matches.length === 2)).toBe(true)
  })

  it('keeps bye rounds for odd player counts', () => {
    const rounds = buildMatchRounds(players.slice(0, 3), [
      { id: 'ab', playerAId: 'a', playerBId: 'b', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'ac', playerAId: 'a', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 },
      { id: 'bc', playerAId: 'b', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 }
    ])

    expect(rounds).toHaveLength(3)
    expect(rounds.map((round) => round.matches[0]?.id).sort()).toEqual(['ab', 'ac', 'bc'])
  })

  it('matches pairings regardless of player order in the source match', () => {
    const rounds = buildMatchRounds(players.slice(0, 2), [
      { id: 'ba', playerAId: 'b', playerBId: 'a', status: 'pending', winsA: 0, winsB: 0 }
    ])

    expect(rounds).toEqual([{ number: 1, matches: [{ id: 'ba', playerAId: 'b', playerBId: 'a', status: 'pending', winsA: 0, winsB: 0 }] }])
  })
})
