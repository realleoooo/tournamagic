import { mount } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'
import TournamentOpponentsSection from '../../src/components/tournaments/TournamentOpponentsSection.vue'

describe('TournamentOpponentsSection', () => {
  it('renders claimed and unclaimed players and forwards profile clicks', async () => {
    const wrapper = mount(TournamentOpponentsSection, {
      props: {
        groups: [
          {
            id: 'p1',
            name: 'Alice',
            remaining: ['Bob', 'Chandra'],
            claimedByEmail: 'alice@example.com',
            claimedByLabel: 'Alice Smith - alice@example.com'
          },
          {
            id: 'p2',
            name: 'Bob',
            remaining: [],
            claimedByEmail: null,
            claimedByLabel: 'Unclaimed'
          }
        ]
      }
    })

    expect(wrapper.text()).toContain('Alice Smith - alice@example.com')
    expect(wrapper.text()).toContain('Unclaimed')
    expect(wrapper.text()).toContain('All matches completed.')

    await wrapper.get('button.player-link').trigger('click')

    expect(wrapper.emitted('openPlayerProfile')).toEqual([['p1']])
  })
})
