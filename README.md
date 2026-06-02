#Testing

# Hands Of Retail — Run & Environment

Quick instructions to run the application locally (H2) and in production (MySQL / DigitalOcean).

Prerequisites

- Java 17+ / JDK 21
- Gradle wrapper (included)
- `.env` for local; `.env.prod` (or environment variables) for production

Files

- `.env` — local environment variables (H2). Do NOT commit.
- `.env.prod` — production environment example. Do NOT commit; use host secrets.
- `src/main/resources/application-local.properties` — local profile (H2).
- `src/main/resources/application-prod.properties` — production profile (MySQL).

Run locally (recommended)

1. Load `.env` into your shell (PowerShell example) then start with Gradle:

```powershell
Get-Content .env | ForEach-Object { if ($_ -match '^\s*#') { continue } ; $p = $_ -split '=',2 ; if ($p.Length -eq 2) { Set-Item -Path Env:$($p[0].Trim()) -Value $p[1].Trim() } }
./gradlew.bat bootRun
```

Or run without loading `.env` by passing the profile explicitly:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Build jar (for manual production testing)

```bash
./gradlew bootJar
```

Run production (example)

1. On the host, set environment variables (preferred) or load `.env.prod` into the shell.
2. Start the app with the `prod` profile:

```bash
# load .env.prod into the shell, then
export SPRING_PROFILES_ACTIVE=prod
java -jar build/libs/your-app-name.jar
```

Notes & best practices

- Do not commit `.env` or `.env.prod` — add them to `.gitignore`.
- Use `JPA_HIBERNATE_DDL_AUTO=update` only for local development; use `validate` in production and manage schema changes with Flyway or Liquibase.
- For production secrets, prefer your cloud provider's secret store over files on disk.

# Gradle bootRun

./gradlew.bat bootRun --args='--spring.profiles.active=prod'

# Or run packaged jar

java -jar build/libs/your-app.jar --spring.profiles.active=prod
