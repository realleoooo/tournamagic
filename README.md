# tournamagic

Frontend planning and React prototype for a Magic: The Gathering draft tournament tracker.

## Documents
- Product and UX planning: `docs/frontend-planning.md`
- Frontend implementation phases: `docs/frontend-implementation-phases.md`

## Frontend app (React)
The frontend now runs as a React app in the browser and includes interactive tournament features with mock data:
- tournament setup and player management
- round-robin match generation
- best-of-three result entry + undo
- live leaderboard updates
- match filtering by player name
- round-grouped matches with status pills
- remaining matches panel and win percentages
- toast feedback and improved keyboard focus states

Run locally:
```bash
python3 -m http.server 4173 --bind 0.0.0.0
```
Then open `http://localhost:4173`.
