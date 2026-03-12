export interface ParsedResult {
  winsA: number
  winsB: number
  winnerSide: 'A' | 'B'
}

export const isValidBestOfThree = (winsA: number, winsB: number): boolean => {
  const total = winsA + winsB
  const oneReachedTwo = winsA === 2 || winsB === 2
  return oneReachedTwo && total >= 2 && total <= 3
}

export const parseBestOfThree = (winsA: number, winsB: number): ParsedResult => {
  if (!isValidBestOfThree(winsA, winsB)) {
    throw new Error('Invalid best-of-three result. Valid finals: 2-0 or 2-1.')
  }

  return {
    winsA,
    winsB,
    winnerSide: winsA > winsB ? 'A' : 'B'
  }
}
