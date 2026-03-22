import { reactive, readonly } from 'vue'

export type TournamentSection = 'overview' | 'leaderboard' | 'opponents' | 'invite'

const state = reactive({
  activeSection: 'overview' as TournamentSection,
  sidebarOpen: false,
  selectedRound: 1
})

export const useTournamentShell = () => {
  const setActiveSection = (section: TournamentSection) => {
    state.activeSection = section
    state.sidebarOpen = false
  }

  const openSidebar = () => {
    state.sidebarOpen = true
  }

  const closeSidebar = () => {
    state.sidebarOpen = false
  }

  const toggleSidebar = () => {
    state.sidebarOpen = !state.sidebarOpen
  }

  const setSelectedRound = (round: number) => {
    state.selectedRound = round
  }

  return {
    state: readonly(state),
    setActiveSection,
    openSidebar,
    closeSidebar,
    toggleSidebar,
    setSelectedRound
  }
}
