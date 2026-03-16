# TournaMagic

Magic: The Gathering draft tournament tracker with Vue 3 + Vite frontend and Spring Boot backend.

## Architecture
- Frontend: Vue 3 + Pinia + Vue Router
- Backend: Spring Boot REST API
- Data + Auth: Supabase (self-hosted in Docker)

## Run locally with Docker
```bash
docker compose up --build
```

Then open:
- App: `http://localhost:5174`
- API: `http://localhost:8080/api`

Additional setup details are in `docs/setup.md`.

## Environment variables (Docker)
- `SUPABASE_DB_PASSWORD` (default: `postgres`)
- `SUPABASE_JWT_SECRET`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`

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
