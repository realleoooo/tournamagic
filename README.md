# TournaMagic

Magic: The Gathering draft tournament tracker with Vue 3 + Vite frontend and Spring Boot backend.

## Auth status
- Email/password auth is enabled.
- Google login is enabled.
- Apple login has been removed for now to keep local setup simple.

## Quick start
### 1) Configure environment
Frontend:
```bash
cp .env.example .env
```
Set in `.env`:
- `VITE_API_BASE_URL=/api`
- `VITE_GOOGLE_CLIENT_ID=...apps.googleusercontent.com`

Backend (shell env or file):
- `AUTH_GOOGLE_CLIENT_ID=...apps.googleusercontent.com`
- `AUTH_SESSION_TTL_HOURS=168` (optional)

### 2) Start backend
```bash
cd backend
mvn spring-boot:run
```

### 3) Start frontend
```bash
npm install
npm run dev
```

Open `http://localhost:5174`.

## Full local setup guide
See `docs/local-setup.md` for:
- dependency installation
- Google Cloud Console setup
- env variables
- login testing steps
- troubleshooting

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


## Docker troubleshooting
If backend image build fails with Maven dependency transfer errors (for example `Tag mismatch`), rebuild backend without cache:

```bash
docker compose build --no-cache backend
```

The backend Dockerfile includes retry logic for transient Maven download failures.
