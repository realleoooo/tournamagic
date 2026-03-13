# TournaMagic Architecture (Simple Overview)

This file explains the current structure in easy words.

## Big picture
The project has three main parts:

1. **Frontend** (`src/`)  
   The screen users interact with (Vue + Vite).
2. **Backend** (`backend/`)  
   The API and business logic (Spring Boot).
3. **Docker setup** (`docker-compose.yml`, Dockerfiles, `docker/nginx/`)  
   Runs frontend + backend + database together.

---

## How requests flow
1. User opens the frontend in browser.
2. Frontend sends API requests to `/api/...`.
3. Nginx/Vite proxy forwards these requests to backend.
4. Backend reads/writes tournament data in PostgreSQL.
5. Backend returns JSON to frontend.
6. Frontend updates UI (overview, matches, leaderboard).

---

## Directory structure and responsibilities

### `src/` (frontend)
- `views/` → page-level screens (`SetupView`, `TournamentView`).
- `components/` → reusable UI blocks (matches table, leaderboard, progress panel, player form).
- `stores/` → app state (Pinia store for tournament data and actions).
- `api/` → HTTP calls to backend endpoints.
- `domain/` → pure frontend logic (pairings, scoring, ranking models/rules).
- `router/` → page routing.
- `styles/` → theme tokens and global styles.

Frontend depends on backend API for saved tournament data.

### `backend/` (Spring Boot)
- `controller/` → REST endpoints (`/api/tournaments/...`).
- `service/` → core backend logic (create tournament, validate/update match, standings).
- `domain/` → JPA entities mapped to DB tables.
- `repo/` → database access repositories.
- `dto/` → request/response objects sent to frontend.
- `resources/db/migration/` → Flyway SQL migrations.
- `resources/application.yml` → datasource and app config.

Backend depends on PostgreSQL and Flyway for persistence/schema migration.

### Docker and runtime files
- `docker-compose.yml` → starts DB + backend + frontend stack.
- `backend/Dockerfile` → builds/runs Spring Boot app.
- `Dockerfile.frontend` → builds frontend static assets and serves via Nginx.
- `docker/nginx/default.conf` → routes `/api` to backend and serves SPA.

---

## How parts build on each other
1. **Domain logic** defines tournament behavior.
2. **Backend service + repositories** apply that logic with persistence.
3. **API controllers** expose backend capabilities to clients.
4. **Frontend API/store** consume controller endpoints.
5. **Frontend views/components** render current state for users.
6. **Docker compose** ties all services together for local execution.

So each higher layer depends on the layer below it:
- UI depends on store/API,
- store/API depends on backend endpoints,
- backend endpoints depend on service/domain/repository,
- repository depends on database.
