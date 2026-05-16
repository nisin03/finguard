from fastapi.testclient import TestClient

from app.main import app
from app.models import ParsedExpense


client = TestClient(app)


def test_health_returns_ok():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_parse_returns_expense_payload(monkeypatch):
    expected = ParsedExpense(
        description="Grab Food",
        amount=350.50,
        date="2026-05-09",
        category="Food",
    )

    monkeypatch.setattr("app.main.parse_email", lambda email_text: expected)

    response = client.post(
        "/parse",
        json={"email_text": "receipt text"},
    )

    assert response.status_code == 200
    assert response.json() == {
        "description": "Grab Food",
        "amount": 350.5,
        "date": "2026-05-09",
        "category": "Food",
    }


def test_parse_returns_500_when_parser_raises_value_error(monkeypatch):
    def raise_value_error(email_text: str):
        raise ValueError("GEMINI_API_KEY is not set")

    monkeypatch.setattr("app.main.parse_email", raise_value_error)

    response = client.post(
        "/parse",
        json={"email_text": "receipt text"},
    )

    assert response.status_code == 500
    assert response.json() == {"detail": "GEMINI_API_KEY is not set"}
