# Docker setup with self-hosted Supabase

This project now runs with **Supabase only** for authentication and persistence.

## 1) Start everything

```bash
docker compose up --build
```

Services started:
- `supabase-db` (PostgreSQL used internally by Supabase services)
- `supabase-auth` (Supabase Auth / GoTrue)
- `supabase-rest` (PostgREST API for the backend)
- `backend` (Spring Boot API)
- `frontend` (Vue app)

## 2) Open the app

- Frontend: `http://localhost:5174`
- Backend API: `http://localhost:8080/api`

## 3) Environment variables

You can override defaults in `.env` for Docker Compose:

- `SUPABASE_DB_PASSWORD`
- `SUPABASE_JWT_SECRET`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`

If you change the JWT secret, regenerate both API keys so they are signed with the same secret.
