import type { Tournament } from '@/domain/models'

export interface RemainingOpponentGroup {
  id: string
  name: string
  remaining: string[]
  claimedByEmail?: string | null
  claimedByLabel: string
}

export const buildRemainingOpponentGroups = (
  tournament: Tournament,
  remainingOpponents: (playerId: string) => string[]
): RemainingOpponentGroup[] =>
  tournament.players.map((player) => ({
    id: player.id,
    name: player.name,
    remaining: remainingOpponents(player.id),
    claimedByEmail: player.claimedByEmail,
    claimedByLabel: player.claimedByEmail
      ? `${player.claimedByName ?? player.name} - ${player.claimedByEmail}`
      : 'Unclaimed'
  }))
