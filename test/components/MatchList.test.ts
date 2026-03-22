import { mount } from '@vue/test-utils'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import MatchList from '../../src/components/matches/MatchList.vue'
import { useTournamentShell } from '../../src/composables/useTournamentShell'
import { buildMatchRounds } from '../../src/utils/matchRounds'

const push = vi.fn()

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push
  })
}))

const players = [
  { id: 'a', name: 'Alice' },
  { id: 'b', name: 'Bob' },
  { id: 'c', name: 'Chandra' },
  { id: 'd', name: 'Dack' }
]

const matches = [
  { id: 'ab', playerAId: 'a', playerBId: 'b', status: 'pending', winsA: 0, winsB: 0 },
  { id: 'ac', playerAId: 'a', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 },
  { id: 'ad', playerAId: 'a', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 },
  { id: 'bc', playerAId: 'b', playerBId: 'c', status: 'pending', winsA: 0, winsB: 0 },
  { id: 'bd', playerAId: 'b', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 },
  { id: 'cd', playerAId: 'c', playerBId: 'd', status: 'pending', winsA: 0, winsB: 0 }
]

const resolveName = (id: string) => players.find((player) => player.id === id)?.name ?? id

describe('MatchList', () => {
  beforeEach(() => {
    const shell = useTournamentShell()
    shell.setSelectedRound(1)
    push.mockReset()
  })

  it('keeps round selection in sync and falls back to a valid round', async () => {
    const shell = useTournamentShell()
    shell.setSelectedRound(2)

    const wrapper = mount(MatchList, {
      props: {
        matches,
        players,
        resolveName,
        resolveProfileEmail: () => undefined
      },
      global: {
        stubs: {
          MatchTimer: true
        }
      }
    })

    expect(wrapper.text()).toContain('Round 2')

    const roundButtons = wrapper.findAll('button').filter((button) => button.text().startsWith('Round '))
    await roundButtons[2].trigger('click')
    expect(wrapper.text()).toContain('Round 3')

    await wrapper.setProps({
      players: players.slice(0, 2),
      matches: [matches[0]]
    })

    expect(wrapper.text()).toContain('Round 1')
  })

  it('emits submit and clear with the current score inputs', async () => {
    const wrapper = mount(MatchList, {
      props: {
        matches,
        players,
        resolveName,
        resolveProfileEmail: (id: string) => `${id}@example.com`
      },
      global: {
        stubs: {
          MatchTimer: true
        }
      }
    })

    const firstRoundFirstMatch = buildMatchRounds(players, matches)[0].matches[0]
    const inputs = wrapper.findAll('input[type="number"]')

    await inputs[0].setValue('1')
    await inputs[1].setValue('2')

    const saveButton = wrapper.findAll('button').find((button) => button.text() === 'Save Result')
    const undoButton = wrapper.findAll('button').find((button) => button.text() === 'Undo')

    await saveButton!.trigger('click')
    await undoButton!.trigger('click')

    expect(wrapper.emitted('submit')).toEqual([[firstRoundFirstMatch.id, 1, 2]])
    expect(wrapper.emitted('clear')).toEqual([[firstRoundFirstMatch.id]])
  })
})
