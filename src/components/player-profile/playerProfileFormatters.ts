import type { PlayerProfileStats, Tournament } from '@/domain/models'

const dateFormatter = new Intl.DateTimeFormat('en', {
  dateStyle: 'medium'
})

export const formatProfileDate = (value: string) => dateFormatter.format(new Date(value))

export const getMatchWinRate = (stats: PlayerProfileStats) => {
  const total = stats.totalMatchWins + stats.totalMatchLosses
  return total === 0 ? 0 : Math.round((stats.totalMatchWins / total) * 100)
}

export const getPodiumCount = (stats: PlayerProfileStats) =>
  stats.firstPlaces + stats.secondPlaces + stats.thirdPlaces

export const getPlacementLabel = (placement?: number | null) => {
  if (!placement) {
    return 'Still in progress'
  }

  if (placement === 1) return '1st place'
  if (placement === 2) return '2nd place'
  if (placement === 3) return '3rd place'

  const remainder = placement % 10
  const suffix =
    remainder === 1 && placement !== 11
      ? 'st'
      : remainder === 2 && placement !== 12
        ? 'nd'
        : remainder === 3 && placement !== 13
          ? 'rd'
          : 'th'

  return `${placement}${suffix} place`
}

export const getPlacementHeading = (status: Tournament['status']) =>
  status === 'complete' ? 'Final placing' : 'Current standing'
