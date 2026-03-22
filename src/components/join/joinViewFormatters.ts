export const formatAvailableSeatLabel = (count: number) => `${count} open ${count === 1 ? 'seat' : 'seats'}`

export const formatJoinStateLabel = (joinEnabled: boolean) => (joinEnabled ? 'Invite open' : 'Invite closed')

export const formatInvitationLabel = (joinEnabled: boolean) =>
  joinEnabled ? 'Open for joining' : 'Currently closed'

export const formatSeatAvailabilityLabel = (count: number) => `${count} available`

export const getSeatInitial = (name: string) => name.slice(0, 1).toUpperCase()
