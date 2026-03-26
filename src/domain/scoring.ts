export interface ParsedResult {
  winsA: number
  winsB: number
  winnerSide: 'A' | 'B'
}

export interface ResultValidation {
  ok: boolean
  message?: string
}

type ValidationRule = {
  test: (winsA: number, winsB: number) => boolean
  message: string
}

const BEST_OF_THREE_RULES: ValidationRule[] = [
  {
    test: (winsA, winsB) => !Number.isFinite(winsA) || !Number.isFinite(winsB),
    message: 'Enter a numeric match result for both players.'
  },
  {
    test: (winsA, winsB) => !Number.isInteger(winsA) || !Number.isInteger(winsB),
    message: 'Game wins must be whole numbers.'
  },
  {
    test: (winsA, winsB) => winsA < 0 || winsB < 0,
    message: 'Game wins cannot be negative.'
  },
  {
    test: (winsA, winsB) => winsA > 2 || winsB > 2,
    message: 'No player can win more than 2 games in a best-of-three match.'
  },
  {
    test: (winsA, winsB) => winsA === 2 && winsB === 2,
    message: 'A best-of-three match cannot end 2-2. Stop after one player reaches 2 wins.'
  },
  {
    test: (winsA, winsB) => winsA === 0 && winsB === 0,
    message: 'Enter the finished result. A saved match cannot be 0-0.'
  },
  {
    test: (winsA, winsB) => winsA === 1 && winsB === 1,
    message: 'A best-of-three match cannot be saved at 1-1. One player still needs the deciding game.'
  },
  {
    test: (winsA, winsB) => winsA === winsB,
    message: 'A match result cannot end in a tie. Use 2-0, 2-1, 0-2, or 1-2.'
  },
  {
    test: (winsA, winsB) => winsA < 2 && winsB < 2,
    message: 'The winner must reach 2 game wins in a best-of-three match.'
  }
]

export const validateBestOfThree = (winsA: number, winsB: number): ResultValidation => {
  const failedRule = BEST_OF_THREE_RULES.find((rule) => rule.test(winsA, winsB))
  return failedRule ? { ok: false, message: failedRule.message } : { ok: true }
}

export const isValidBestOfThree = (winsA: number, winsB: number): boolean => {
  return validateBestOfThree(winsA, winsB).ok
}

export const parseBestOfThree = (winsA: number, winsB: number): ParsedResult => {
  const validation = validateBestOfThree(winsA, winsB)
  if (!validation.ok) {
    throw new Error(validation.message ?? 'Invalid best-of-three result. Use 2-0, 2-1, 0-2, or 1-2.')
  }

  return {
    winsA,
    winsB,
    winnerSide: winsA > winsB ? 'A' : 'B'
  }
}
