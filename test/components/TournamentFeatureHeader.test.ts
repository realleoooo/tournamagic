import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import TournamentFeatureHeader from '../../src/components/tournaments/TournamentFeatureHeader.vue'

describe('TournamentFeatureHeader', () => {
  it('shows and emits the available top-level actions', async () => {
    const wrapper = mount(TournamentFeatureHeader, {
      props: {
        activeSection: 'overview',
        activeSectionLabel: 'Round overview',
        activeSectionDescription: '4 players active in this tournament',
        tournamentName: 'Friday Draft',
        canStartTournament: true,
        currentUserJoined: true
      }
    })

    expect(wrapper.text()).toContain('Round overview')
    expect(wrapper.text()).toContain('Friday Draft')
    expect(wrapper.text()).toContain('Start tournament')
    expect(wrapper.text()).toContain('Leave tournament')

    await wrapper.get('button:not(.warn)').trigger('click')
    await wrapper.get('button.warn').trigger('click')
    await wrapper.getAll('button')[2].trigger('click')

    expect(wrapper.emitted('start')).toHaveLength(1)
    expect(wrapper.emitted('reset')).toHaveLength(1)
    expect(wrapper.emitted('leave')).toHaveLength(1)
  })

  it('hides conditional actions when they are unavailable', () => {
    const wrapper = mount(TournamentFeatureHeader, {
      props: {
        activeSection: 'leaderboard',
        activeSectionLabel: 'Leaderboard',
        activeSectionDescription: '2 players active in this tournament',
        tournamentName: 'Bracket Night',
        canStartTournament: false,
        currentUserJoined: false
      }
    })

    expect(wrapper.text()).not.toContain('Start tournament')
    expect(wrapper.text()).not.toContain('Leave tournament')
    expect(wrapper.text()).toContain('Reset tournament')
  })
})
