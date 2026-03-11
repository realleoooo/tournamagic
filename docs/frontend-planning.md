# Frontend Planning: Magic: The Gathering Draft Tournament App

## 1) Scope of this phase
This document covers **planning for the frontend only** (Vue + Vite) and Docker setup.  
Backend (Spring Boot) and persistence are noted as future implementation phases.

---

## 2) Product vision
Create a stylish “gamer” web app for friend-group MTG draft tournaments where:
- Players are added at tournament start.
- Every player plays every other player (round-robin pairing).
- Each matchup is best-of-three (Bo3), always producing one winner.
- Users can quickly record match results.
- A live leaderboard ranks players from best to worst.
- A clear schedule view shows who still has to play whom.

Tone and UX should feel themed for MTG players: dark fantasy look, glowing accents, “card/tabletop” visual language.

---

## 3) Core assumptions and rules
1. Tournament mode for v1: **single round-robin** (each pair meets once; each match is Bo3).
2. Match winner is determined by game wins in a Bo3:
   - 2-0, or
   - 2-1 (after 1-1 split).
3. No draws allowed in matches.
4. Frontend-first phase stores state locally (in-memory + optional LocalStorage).
5. Initial optimization target: smooth experience for 4–12 players.

---

## 4) Functional requirements (frontend)

### 4.1 Tournament setup
- Create a tournament session.
- Add, edit, remove players before start.
- Validate player list:
  - minimum 2 players,
  - no empty names,
  - no duplicate names (case-insensitive).
- Start tournament and lock roster (or require reset to change roster).

### 4.2 Pairings / schedule generation
- Generate all unique player-vs-player pairings for round-robin.
- Present schedule in a clear format:
  - “Pending”, “In Progress” (optional), “Completed”.
- Allow filtering by player and status.

### 4.3 Match result entry (Bo3)
- Open a match details panel/modal.
- Record result as game wins:
  - valid scores: 2-0 or 2-1 (and mirrored for opponent).
- Prevent invalid states (e.g., 1-0 final, 2-2, draws).
- Support edit/undo for previously entered results.

### 4.4 Leaderboard
- Live ranking updates after each result.
- Display at least:
  - rank,
  - player name,
  - match record (W-L),
  - game record (GW-GL),
  - match win percentage,
  - game win percentage (optional but recommended).
- Tie-break ordering for v1:
  1. Match wins,
  2. Head-to-head (if exactly two tied and result exists),
  3. Game win differential,
  4. Total game wins,
  5. Alphabetical.

### 4.5 Progress and overview
- Show tournament completion progress (e.g., “9/15 matches completed”).
- Show each player’s remaining opponents.
- Highlight “next suggested matches” (any pending match).

### 4.6 Local persistence (frontend only)
- Persist current tournament state in LocalStorage.
- On reload, restore latest tournament.
- Provide “Reset tournament” with confirmation.

### 4.7 UX polish and accessibility
- Responsive layout (desktop-first, usable on tablets/phones).
- Keyboard-friendly controls for forms and result entry.
- Minimum color-contrast and clear focus states.

---

## 5) Non-functional requirements (frontend)

### 5.1 Usability
- New user can create tournament and enter first match result in under 2 minutes.
- Leaderboard and schedule understandable without docs.

### 5.2 Performance
- Initial load target under 2s on modern desktop (local/dev build).
- UI interactions should feel instant (<100ms perceived) for 12-player tournaments.

### 5.3 Reliability
- Deterministic ranking calculations.
- No data loss on accidental browser refresh (via LocalStorage).

### 5.4 Maintainability
- Typed domain models (TypeScript interfaces/types).
- Separation of concerns:
  - domain logic (pairings/ranking rules),
  - state management,
  - presentational components.
- Unit-testable pure functions for scoring/ranking.

### 5.5 Theming / visual quality
- Distinct MTG-inspired visual identity:
  - dark background,
  - arcane/gold/mana-like accent colors,
  - card-style panels and hover glow,
  - iconography for wins/losses/progress.

---

## 6) Proposed frontend architecture (implementation-ready)

### 6.1 Stack
- Vue 3 + Vite
- TypeScript
- Pinia (state management)
- Vue Router (if multi-view navigation is desired)
- Vitest + Vue Test Utils (unit/component tests)
- Optional UI utility: Tailwind CSS or custom SCSS tokens

### 6.2 Suggested folder structure
```text
src/
  assets/
  components/
    leaderboard/
    matches/
    players/
    shared/
  views/
    SetupView.vue
    TournamentView.vue
  stores/
    tournament.ts
  domain/
    models.ts
    pairing.ts
    scoring.ts
    ranking.ts
  composables/
    useLocalStorageSync.ts
  styles/
    tokens.css
    theme.css
  router/
    index.ts
```

### 6.3 Domain model (frontend)
- `Player`: id, name
- `Match`: id, playerAId, playerBId, status, winsA, winsB, winnerId
- `Tournament`: id, name, createdAt, players[], matches[], status

---

## 7) UX/UI concept (MTG gamer style)

### Visual direction
- “Spellbook dashboard” feel with layered card panels.
- Color palette example:
  - Base: `#0f1117`, `#171a24`
  - Accent: `#7c3aed` (arcane purple), `#d4af37` (mythic gold), `#ef4444` (loss/error)
- Typography:
  - Display font for headers (fantasy style, readable),
  - Clean sans-serif for body.

### Main screens
1. **Setup Screen**
   - Tournament title input
   - Player roster builder
   - “Start Tournament” CTA
2. **Tournament Dashboard**
   - Top: progress + quick actions
   - Left: match schedule list/grid
   - Right: live leaderboard card
   - Modal/drawer: enter/edit Bo3 result

### Micro-interactions
- Subtle glow on actionable cards/buttons.
- Animated rank movement indicator after result submission.
- Clear success feedback (“Result saved”).

---

## 8) Edge cases to account for in frontend logic
- Odd/even number of players (round-robin generator still valid).
- Duplicate names differing only by case.
- Editing a result must correctly recompute standings.
- Head-to-head tie-break should only apply when deterministically valid.
- Browser storage unavailable/full (graceful warning + in-memory fallback).

---

## 9) Acceptance criteria for frontend planning completion
This planning phase is complete when:
1. Functional and non-functional requirements are documented.
2. UI/UX direction is defined and aligned with MTG gamer theme.
3. Data model and ranking/pairing rules are clear.
4. Frontend architecture and implementation roadmap are ready.

---

## 10) Next implementation milestones (frontend only)
1. Bootstrap Vue + Vite + TypeScript project.
2. Implement domain logic (pairings, Bo3 validation, ranking).
3. Build setup screen + tournament dashboard shell.
4. Implement result-entry flow + live leaderboard.
5. Add local persistence.
6. Add unit tests for pairing/ranking.
7. Apply final visual polish/theme pass.

---

## 11) Future phases (out of current scope)
- Spring Boot backend (REST API, persistent storage, multi-device sync).
- Dockerized full stack (frontend + backend + optional DB).
- Authentication and shared tournaments.
