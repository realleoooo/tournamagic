# Tournamagic Frontend Product & UX Plan (Draft-Only Tournaments)

## 1) Product Goal (Phase 1: Frontend-Only)
Build a **single-page tournament manager UI** for local Magic: The Gathering draft groups (example: 6 friends) where:
- Players can be added before tournament start.
- Every player plays every other player (**round robin**).
- Each matchup is **best-of-three games** and always has one winner.
- Standings update live with a clear leaderboard.
- Match schedule/overview is always visible so everyone knows who plays whom next.

This phase is strictly **frontend planning + frontend implementation later** (no backend yet).

---

## 2) Scope and Assumptions

### In Scope (Phase 1)
- Frontend UX, visual design language, page flow, component architecture.
- Local state data model (in-memory + optional localStorage persistence).
- Tournament generation logic on client side.
- Result-entry workflow and live leaderboard calculations.

### Out of Scope (Phase 1)
- Multi-user sync.
- Login/auth.
- Server-side persistence/database.
- Online matchmaking.

### Assumptions
- 4–10 players supported in first iteration (6 is primary target).
- Single device is used by playgroup during one event.
- Each match ends with a winner (2-0 or 2-1).

---

## 3) Core User Roles
- **Organizer**: creates tournament, adds players, can edit/delete results.
- **Player/Viewer**: checks pairings, standings, and personal record.

(For frontend phase, both roles can use same interface, but organizer actions can be visually marked.)

---

## 4) Functional Requirements (Core)

### FR-1 Tournament Setup
1. User can create a new tournament (name + optional date).
2. User can add, edit, remove players before start.
3. UI enforces minimum 4 players.
4. “Start Tournament” locks player list and generates full round-robin pairings.

### FR-2 Pairing & Match Overview
1. Show all rounds and pairings in a clear structure.
2. Show per-match state: `Not started`, `In progress`, `Completed`.
3. Show who still needs to play and what is currently pending.
4. Optional filter: by player (see only your matches).

### FR-3 Match Result Entry (Best of Three)
1. Each match allows score entry only in valid states:
   - 2-0
   - 2-1
   - 1-2
   - 0-2
2. Invalid outcomes (e.g., 2-2, 1-0 final) are blocked by UI validation.
3. When result saved, winner/loser are determined automatically.
4. Organizer can edit previously entered result with confirmation.

### FR-4 Leaderboard / Standings
1. Live standings update after each saved match.
2. Standings columns:
   - Rank
   - Player
   - Match Wins
   - Match Losses
   - Match Win %
   - Game Wins
   - Game Losses
   - Game Win %
3. Default tie-break order:
   1) Match Wins (descending)
   2) Game Win % (descending)
   3) Head-to-head (if tie between 2 players)
   4) Stable fallback (alphabetic)
4. Highlight top player(s) and bottom position clearly.

### FR-5 Tournament Progress Tracking
1. Progress indicator (e.g., `9/15 matches completed`).
2. “Remaining matches” list for quick next-game decisions.
3. “Tournament complete” state when all matches are finished.
4. Final standings snapshot view.

### FR-6 Local Persistence (Frontend-only)
1. Save tournament state to localStorage automatically.
2. On reload, recover active tournament.
3. Offer reset/clear tournament action with confirmation modal.

### FR-7 UX Utilities
1. Quick actions: “Enter next result”, “Jump to player”, “Show unfinished only”.
2. Toast notifications for save/update/reset actions.
3. Empty states with helpful guidance.

---

## 5) Non-Functional Requirements (Core)

### NFR-1 Usability & UX
- First-time user can create and start tournament in <2 minutes.
- Result entry in <= 3 taps/clicks from match card.
- Readability in low-light room environments.

### NFR-2 Performance
- Main interactions should feel instant (<100ms perceived latency for local operations).
- Support up to 10 players without UI lag.

### NFR-3 Reliability
- Prevent invalid tournament states through strong client validation.
- Protect destructive actions via confirmation dialogs.

### NFR-4 Accessibility
- Keyboard reachable core flows (setup, result entry, leaderboard view).
- Minimum AA contrast.
- Color + icon/text redundancy (not color-only status communication).

### NFR-5 Responsiveness
- Mobile-first layout (phone on table) and tablet/desktop enhancement.
- No horizontal scrolling for critical screens.

### NFR-6 Maintainability
- Component-driven architecture.
- Clear separation: presentation components vs tournament logic utilities.
- Typed models and deterministic pure functions for standings and pairings.

---

## 6) Gamer-Focused UI/Visual Direction (Not “generic AI look”)

### Theme Direction: “Arcane Arena HUD”
- Visual mood: dark fantasy + neon accent (Magic-inspired, not IP-infringing assets).
- Palette suggestion:
  - Base: deep charcoal / obsidian
  - Accent 1: mana-blue glow
  - Accent 2: ember-orange for warnings/action
  - Accent 3: arcane-violet for highlights
- Typography:
  - Display font for headings (fantasy-tech flavor)
  - Highly readable sans-serif for data tables
- UI motifs:
  - Subtle card-frame borders
  - Gradient glows on active cards
  - Rank badges (gold/silver/bronze + neutral)

### Component Styling Principles
- Match cards resemble compact game cards with status ribbons.
- Leaderboard rows animate subtly when ranking changes.
- Important actions use distinct iconography + text labels.
- Avoid visual clutter: strong spacing + clear hierarchy.

---

## 7) Frontend Information Architecture

### Proposed Screens / Views
1. **Setup View**
   - Tournament name/date
   - Player management list
   - Start button + validation hints

2. **Tournament Dashboard (Main)**
   - Top strip: progress + quick actions
   - Left/top: current/remaining matches
   - Right/bottom: leaderboard

3. **Pairings View**
   - Round-based list or matrix grid (toggle option)
   - Filter by player

4. **Final Results View**
   - Final ranking card
   - Export-ready summary block (future backend/export feature)

### Navigation Pattern
- Tab-like top navigation: `Dashboard | Pairings | Leaderboard | Setup`.
- Floating quick-add result button on mobile.

---

## 8) Frontend Data Model (Client State)

```ts
type Player = {
  id: string;
  name: string;
};

type MatchResult = {
  winsA: 0 | 1 | 2;
  winsB: 0 | 1 | 2;
  winnerId: string;
};

type Match = {
  id: string;
  playerAId: string;
  playerBId: string;
  round: number;
  status: 'not_started' | 'in_progress' | 'completed';
  result?: MatchResult;
};

type Tournament = {
  id: string;
  name: string;
  createdAt: string;
  status: 'setup' | 'active' | 'completed';
  players: Player[];
  matches: Match[];
};
```

All ranking and progress values should be derived selectors (no duplicated source-of-truth fields).

---

## 9) Core Algorithms (Frontend)
1. **Round-robin pairing generation**
   - Deterministic output for reproducibility.
   - If odd player count (future): one BYE each round.
2. **Standings computation**
   - Aggregate match and game records.
   - Compute percentages safely (divide-by-zero guards).
3. **Tie-break resolver**
   - Apply ordered rules consistently.

---

## 10) Architecture Plan (Agent-Oriented)

### Architect Agent Deliverables
- Finalize component boundaries and folder structure.
- Define pure utility modules for scheduling and standings.
- Define state management strategy (e.g., React context + reducer or Zustand).

### Frontend Agent Deliverables
- Implement UI screens/components with responsive behavior.
- Implement forms, validation, and interaction flows.
- Implement theme tokens + reusable design system primitives.

### Backend Agent (Deferred)
- Prepare API contract draft later for persistence and multi-device sync.

### DevOps Agent (Deferred)
- Prepare CI + preview deployment after frontend MVP exists.

---

## 11) Suggested Frontend Folder Structure (for implementation phase)

```text
src/
  app/
    routes/
  features/
    tournament/
      components/
      hooks/
      state/
      utils/
      types/
    leaderboard/
      components/
      utils/
  shared/
    ui/
    theme/
    lib/
```

---

## 12) MVP Acceptance Criteria (Frontend-only)
1. User can create tournament with 6 players and start it.
2. App auto-generates all round-robin matches.
3. User can enter only valid best-of-three results.
4. Leaderboard updates correctly after each result.
5. App persists state in localStorage across refresh.
6. UI is usable on mobile and desktop.
7. Visual style reflects gamer theme (dark, polished, non-generic).

---

## 13) Implementation Plan (Next Step Preview)

### Milestone A — Foundation
- Set up app shell, theme tokens, base layout.
- Build setup view + player list management.

### Milestone B — Tournament Engine
- Pairing generator + match models.
- Dashboard with match cards and result modal.

### Milestone C — Standings & Polish
- Leaderboard computation + tie-break rules.
- Animations, transitions, micro-interactions.
- Local persistence + recovery.

### Milestone D — QA & UX hardening
- Validation edge cases.
- Accessibility pass.
- Responsive polish for small screens.

