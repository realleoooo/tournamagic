import type { Component } from 'vue'
import InviteIcon from '@/components/icons/InviteIcon.vue'
import LeaderboardIcon from '@/components/icons/LeaderboardIcon.vue'
import OpponentsIcon from '@/components/icons/OpponentsIcon.vue'
import OverviewIcon from '@/components/icons/OverviewIcon.vue'
import type { TournamentSection } from '@/composables/useTournamentShell'

export interface TournamentSectionItem {
  id: TournamentSection
  label: string
  icon: Component
}

export const tournamentSections: TournamentSectionItem[] = [
  { id: 'overview', label: 'Round overview', icon: OverviewIcon },
  { id: 'leaderboard', label: 'Leaderboard', icon: LeaderboardIcon },
  { id: 'opponents', label: 'Remaining opponents', icon: OpponentsIcon },
  { id: 'invite', label: 'Invite players', icon: InviteIcon }
]
