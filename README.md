# FinGuard PH

FinGuard PH is a small finance-tracking system made of:

- `expense-service`: a Spring Boot API for JWT login and expense CRUD
- `parser-service`: a FastAPI API that extracts expense-like data from receipt text with Gemini
- `postgres`: the local database used by `expense-service`

## Local setup

1. Copy `.env.example` to `.env`.
2. Fill in the environment variables listed below.
3. Start the full stack:

```bash
docker compose up --build
```

Local endpoints:

- Expense API: `http://localhost:8080`
- Parser API: `http://localhost:8000`
- PostgreSQL: `localhost:5432`

## Environment variables

| Variable | Used by | Purpose |
| --- | --- | --- |
| `DB_NAME` | PostgreSQL, expense-service | Database name used by Docker Compose |
| `DB_URL` | expense-service | JDBC connection URL when running Spring Boot outside Docker |
| `DB_USER` | PostgreSQL, expense-service | Database username |
| `DB_PASSWORD` | PostgreSQL, expense-service | Database password |
| `JWT_SECRET` | expense-service | HMAC signing secret for JWTs |
| `JWT_EXPIRATION` | expense-service | JWT lifetime in milliseconds; defaults to `3600000` |
| `GEMINI_API_KEY` | parser-service | Gemini API key used for receipt parsing |

The checked-in `.env.example` is safe to share. Keep real secrets in your local `.env` or deployment environment only.

## Quick verification

```bash
curl http://localhost:8000/health

curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Use the token from the login response in the examples below:

```bash
TOKEN="<paste-token-here>"
```

## API examples

### Expense service

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

curl http://localhost:8080/api/expenses \
  -H "Authorization: Bearer $TOKEN"

curl -X POST http://localhost:8080/api/expenses/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "Lunch at Jollibee",
    "amount": 150.50,
    "date": "2026-05-01",
    "category": "Food"
  }'
```

### Parser service

```bash
curl http://localhost:8000/health

curl -X POST http://localhost:8000/parse \
  -H "Content-Type: application/json" \
  -d '{
    "email_text": "Thank you for your order at Grab Food! Your total of PHP 350.50 has been charged to your card ending in 1234. Order placed on May 9, 2026."
  }'
```

## Tests

```bash
cd expense-service
./mvnw test

cd ../parser-service
python -m pytest
```

## Notes

- The parser currently depends on Gemini responses, so keep contract changes covered by tests.
- `expense-service` uses soft deletes for expenses; normal reads return only non-deleted rows.
