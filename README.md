# Cosmic Explorer AI (backend)

Spring Boot 3 + Java 17 REST API for the Cosmic Explorer AI frontend.

- PostgreSQL database (or in-memory H2 for a quick run)
- Spring Security with JWT login, passwords hashed with BCrypt
- Planets, exoplanets, missions and quiz stored in the database (filled automatically on first start)
- Per-user history, favourites and quiz best score
- AI assistant through the Google Gemini API
- Live NASA data (Astronomy Picture of the Day, Exoplanet Archive)

> **Status:** this code was written and checked for Java syntax, but it was **not compiled or run** where it was written (Maven Central was not reachable there). Run `mvn test` first. If anything fails, send the error and it will be fixed.

## What to install

| Tool | Version | Check |
| --- | --- | --- |
| JDK | 17 or newer | `java -version` |
| Maven | 3.9+ (IntelliJ has it built in) | `mvn -v` |
| PostgreSQL | 14+ (only for the real database) | pgAdmin should open |

## Quick start (no PostgreSQL needed)

```bash
mvn test                                              # runs the API smoke test on H2
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Open http://localhost:8080/api/planets. You should see 10 planets as JSON. Data is in memory and resets when you stop the server.

## Run with PostgreSQL

1. Create the database (pgAdmin, or `psql -U postgres`):
   ```sql
   CREATE DATABASE cosmic_explorer;
   ```
   Or, if you have Docker: `docker compose up -d` starts PostgreSQL with the same settings.
2. If your PostgreSQL password is not `postgres`, set it before starting:
   - Windows (Command Prompt): `set DB_PASSWORD=your_password`
   - Windows (PowerShell): `$env:DB_PASSWORD="your_password"`
   - Linux or Mac: `export DB_PASSWORD=your_password`
3. Start the server:
   ```bash
   mvn spring-boot:run
   ```
   Tables are created automatically and the seed data is inserted on the first start.

## Settings (environment variables)

| Variable | Default | Purpose |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/cosmic_explorer` | database address |
| `DB_USER` / `DB_PASSWORD` | `postgres` / `postgres` | database login |
| `JWT_SECRET` | a development value | long random string (32+ characters). Change it before deploying |
| `CORS_ORIGINS` | `http://localhost:5173` | frontend address(es), comma separated |
| `GEMINI_API_KEY` | empty | turns the AI assistant on |
| `GEMINI_MODEL` | `gemini-2.5-flash` | Gemini model name. Google renames models over time, so check the current name in Google AI Studio if you get a 404 |
| `PORT` | `8080` | server port (hosting platforms set this) |
| `NASA_API_KEY` | `DEMO_KEY` | free key from https://api.nasa.gov, DEMO_KEY has low limits |

**Get a Gemini key:** open https://aistudio.google.com, sign in, choose "Get API key", then set `GEMINI_API_KEY` before starting the server. Never put the key in the frontend or commit it to GitHub.

## API

Swagger UI (try every endpoint, use Authorize with the login token): http://localhost:8080/swagger-ui.html
Health check: `GET /api/health`. `POST /api/assistant/chat` accepts `{"question": "...", "simple": true}` for kid-level answers.

| Method | Path | Login needed | Returns |
| --- | --- | --- | --- |
| GET | `/api/planets`, `/api/planets/{id}` | no | planets |
| GET | `/api/exoplanets` | no | exoplanets |
| GET | `/api/missions` | no | missions |
| GET | `/api/quiz` | no | quiz questions |
| POST | `/api/assistant/chat` | no | `{ question }` returns `{ answer }` |
| GET | `/api/nasa/apod` | no | NASA picture of the day |
| GET | `/api/nasa/exoplanets?limit=30` | no | newest planets from the NASA Exoplanet Archive |
| POST | `/api/auth/register` | no | `{ name, email, password }` returns `{ name, token }` |
| POST | `/api/auth/login` | no | `{ email, password }` returns `{ name, token }` |
| GET | `/api/me` | yes | `{ name, email, bestScore }` |
| GET, POST, DELETE | `/api/me/history` | yes | `[ { kind, text, at } ]` |
| GET | `/api/me/favourites` | yes | `[ "earth", "mars" ]` |
| PUT, DELETE | `/api/me/favourites/{id}` | yes | no content |
| PUT | `/api/me/best` | yes | `{ score }`, kept only if higher |

Logged-in requests send `Authorization: Bearer <token>`. Errors come back as `{ "message": "..." }`.

Try it with curl:

```bash
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"name":"Bunny","email":"bunny@example.com","password":"secret123"}'
```

## Project structure

```
src/main/java/com/cosmicexplorer/
  CosmicExplorerApplication.java
  config/    SecurityConfig, JwtService, JwtFilter
  model/     Planet, Exoplanet, Mission, QuizQuestion, AppUser, HistoryEntry, Favourite  (database tables)
  repo/      Spring Data JPA repositories
  web/       controllers, Dtos, ApiExceptionHandler
  service/   DataSeeder, AssistantService (Gemini), NasaService
src/main/resources/
  application.properties, application-h2.properties
  seed/      planets.json, exoplanets.json, missions.json, quiz.json  (edit these to change the starting data)
```

The seed files only load into **empty** tables. To reload them, drop the tables (or use the H2 profile).

## Not covered yet

- `/api/nasa/*` and the Gemini call are written against the public NASA and Google APIs but have not been run against the live services.
- No admin panel, email verification or password reset.
- Passwords need 6 or more characters. There is no rate limiting on login.

## Docker

```bash
docker build -t cosmic-backend .
docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... -e JWT_SECRET=... cosmic-backend
```
