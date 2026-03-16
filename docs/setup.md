# Quick setup (Docker + local Supabase)

You **do not** need a Supabase cloud account.
This project runs Supabase locally inside Docker.

## 1) Copy env file

```bash
cp .env.example .env
```

## 2) Start everything

```bash
docker compose up --build
```

## 3) Open app

- Frontend: http://localhost:5174
- Backend API: http://localhost:8080/api

## Notes

- The backend already reads Supabase settings from `.env`.
- Default keys in `.env.example` are for local/dev only.
