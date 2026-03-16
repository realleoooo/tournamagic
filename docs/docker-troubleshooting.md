# Docker startup troubleshooting (Supabase DB/Auth)

If `docker compose up --build` fails with errors like:
- `failed to copy` 
- `tls: bad record MAC`

this is usually a **local Docker image/cache/network issue**, not a Supabase account problem.

## You do NOT need a Supabase cloud account

This repo uses self-hosted Supabase containers (`supabase-db`, `supabase-auth`, `supabase-rest`).
No online Supabase project is required.

## Quick fix steps

Run these commands in order:

```bash
docker compose down -v
```

```bash
docker system prune -af
```

```bash
docker pull supabase/postgres:15.8.1.060
docker pull supabase/gotrue:v2.170.0
docker pull postgrest/postgrest:v12.2.8
```

```bash
docker compose up --build
```

## If it still fails

1. Restart Docker Desktop / Docker daemon.
2. Check free disk space (`docker system df`).
3. Disable VPN/proxy temporarily.
4. Try a different network and re-run pulls.
5. Update Docker to the latest stable version.

## Optional: use your own Supabase cloud project later

Only if you want cloud Supabase, replace these env vars in `.env`:
- `SUPABASE_REST_URL`
- `SUPABASE_AUTH_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`

But this is optional; local Docker setup should work without it.
