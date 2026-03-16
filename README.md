# TournaMagic

Magic: The Gathering draft tournament tracker with Vue 3 + Vite frontend and Spring Boot backend.

## Implemented phases
- **Phase 1:** Frontend foundation (Vue/Vite/TS, Pinia, Router, MTG-themed UI tokens).
- **Phase 2:** Tournament domain logic (round-robin pairings, Bo3 validation, ranking).
- **Phase 3:** Complete tournament UX (setup, leaderboard, progress, results, filters, undo).
- **Phase 4:** Spring Boot REST backend + persistence (H2 + Flyway), frontend API integration.
- **Phase 5:** Dockerized full stack (`docker-compose`) for one-command startup.
- **Phase 6:** Hardening/QA baseline via backend integration tests, frontend domain tests, error/loading states.
- **Phase 7:** Authentication (email/password + Google + Apple social login with backend token validation).

## Environment variables
### Frontend (`.env`)
Use `.env.example` as a template.

- `VITE_API_BASE_URL` (default `/api`)
- `VITE_GOOGLE_CLIENT_ID`
- `VITE_APPLE_CLIENT_ID`
- `VITE_APPLE_REDIRECT_URI` (e.g. `http://localhost:5174/login`)

### Backend (`backend/.env` or shell env)
Use `backend/.env.example` as a template.

- `AUTH_SESSION_TTL_HOURS` (default `168`)
- `AUTH_GOOGLE_CLIENT_ID`
- `AUTH_APPLE_CLIENT_ID`

## Local development
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

Frontend runs on `http://localhost:5174` and calls backend via `/api` proxy to `http://localhost:8080`.

## OAuth provider setup
### Google Cloud Console
1. Create or select a Google Cloud project.
2. Configure OAuth consent screen.
3. Create an **OAuth Client ID** for a web application.
4. Add authorized JavaScript origins (e.g. `http://localhost:5174`).
5. Add authorized redirect URIs if needed by your Google flow.
6. Copy the client ID into both:
   - `VITE_GOOGLE_CLIENT_ID`
   - `AUTH_GOOGLE_CLIENT_ID`

### Apple Developer
1. Create an App ID / Services ID for Sign in with Apple.
2. Enable **Sign in with Apple** capability.
3. Configure the web domain and return URL (e.g. `http://localhost:5174/login`).
4. Use the Services ID as:
   - `VITE_APPLE_CLIENT_ID`
   - `AUTH_APPLE_CLIENT_ID`
5. Set `VITE_APPLE_REDIRECT_URI` to your login page callback URL.

## Docker (no local npm/java needed)
```bash
docker compose up --build
```

Then open:
- App: `http://localhost:5174`
- API: `http://localhost:8080/api/tournaments`

## Testing
### Frontend
```bash
npm test
npm run build
```

### Backend
```bash
cd backend
mvn test
```
