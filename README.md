# TournaMagic

Magic: The Gathering draft tournament tracker with Vue 3 + Vite frontend and Spring Boot backend.

## Implemented phases
- **Phase 1:** Frontend foundation (Vue/Vite/TS, Pinia, Router, MTG-themed UI tokens).
- **Phase 2:** Tournament domain logic (round-robin pairings, Bo3 validation, ranking).
- **Phase 3:** Complete tournament UX (setup, leaderboard, progress, results, filters, undo).
- **Phase 4:** Spring Boot REST backend + persistence (Flyway + PostgreSQL), frontend API integration.
- **Phase 5:** Dockerized full stack (`docker-compose`) for one-command startup.
- **Phase 6:** Hardening/QA baseline via backend integration tests, frontend domain tests, error/loading states.

## Database
The app now uses **Supabase Postgres (local container)** via Docker Compose:
- service: `supabase-db`
- image: `supabase/postgres:latest`
- db: `tournamagic`
- user/password: `postgres/postgres`

Backend datasource defaults can be overridden with environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Local development
### Start database first
```bash
docker compose up -d supabase-db
```

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
npm install
npm run dev
```

Frontend runs on `http://localhost:5173` and calls backend via `/api` proxy to `http://localhost:8080`.

## Docker (no local npm/java needed)
```bash
docker compose up --build
```

Then open:
- App: `http://localhost:5173`
- API: `http://localhost:8080/api/tournaments`
- Database port: `localhost:5432`

## Testing
### Frontend
```bash
npm test
```

### Backend
```bash
cd backend
mvn test
```

## 431 troubleshooting (Vite dev server)
If you see `431 Request Header Fields Too Large` on localhost:
1. Open in private/incognito mode.
2. Clear localhost cookies/site data.
3. Try another port: `npm run dev -- --port 5174`.
4. Disable browser extensions that inject headers.
