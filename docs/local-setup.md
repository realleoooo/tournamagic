# Local setup (Google login only)

This project now supports:
- Email/password login
- Google login

Apple login has been removed to keep local development simple and reliable.

## 1) Prerequisites
- Node.js 20+
- npm 10+
- Java 21
- Maven 3.9+

## 2) Install dependencies
Frontend:
```bash
npm install
```

Backend (optional prefetch):
```bash
cd backend
mvn -q -DskipTests dependency:go-offline
cd ..
```

## 3) Environment variables

### Frontend `.env`
Copy `.env.example` to `.env` in repo root:
```bash
cp .env.example .env
```

Required:
- `VITE_API_BASE_URL=/api`
- `VITE_GOOGLE_CLIENT_ID=<your-google-web-client-id>`

### Backend environment
Copy backend template if you want to source from file:
```bash
cp backend/.env.example backend/.env
```

Required:
- `AUTH_GOOGLE_CLIENT_ID=<same-google-web-client-id>`
- `AUTH_SESSION_TTL_HOURS=168` (optional, default exists)

If you use `backend/.env`, export it before starting backend (example):
```bash
set -a
source backend/.env
set +a
```

## 4) Google Cloud Console setup
1. Open Google Cloud Console.
2. Create/select a project.
3. Configure OAuth consent screen.
4. Create OAuth client ID of type **Web application**.
5. Add authorized JavaScript origin:
   - `http://localhost:5174`
6. Copy the Client ID (format like `xxx.apps.googleusercontent.com`).
7. Put this value in:
   - `.env` -> `VITE_GOOGLE_CLIENT_ID`
   - backend env -> `AUTH_GOOGLE_CLIENT_ID`

## 5) Start locally
Terminal 1 (backend):
```bash
cd backend
mvn spring-boot:run
```

Terminal 2 (frontend):
```bash
npm run dev
```

Open app at:
- `http://localhost:5174`

## 6) Test login flow
1. Open `http://localhost:5174/login`.
2. Click **Continue with Google**.
3. Complete Google consent.
4. You should be redirected into the app.
5. Refresh browser: you should remain logged in (session token persists).

## 7) Common issues

### Backend fails to download dependencies
- Run again from a network that can access `https://repo.maven.apache.org`.
- If corporate proxy is required, configure Maven proxy in `~/.m2/settings.xml`.

### Google button returns "Google login is not configured"
- `VITE_GOOGLE_CLIENT_ID` is missing in `.env`.
- Restart Vite after updating `.env`.

### CORS / unauthorized API calls
- Confirm frontend is on `http://localhost:5174`.
- Confirm backend is running and token exists after login.
- If token is stale, log out and log in again.


### Docker build fails with Maven `Tag mismatch`
This is typically a transient network/download corruption issue while fetching Maven artifacts.

Try:
```bash
docker compose build --no-cache backend
```

The backend Dockerfile now retries Maven dependency resolution/package automatically and clears the known problematic byte-buddy cache between attempts.
