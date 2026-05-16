import json

import pytest

from app.parser import parse_email


class FakeResponse:
    def __init__(self, text: str):
        self.text = text


class FakeModels:
    def __init__(self, response_text: str):
        self.response_text = response_text

    def generate_content(self, model: str, contents: str):
        return FakeResponse(self.response_text)


class FakeClient:
    def __init__(self, api_key: str, response_text: str):
        self.api_key = api_key
        self.models = FakeModels(response_text)


def test_parse_email_requires_api_key(monkeypatch):
    monkeypatch.delenv("GEMINI_API_KEY", raising=False)

    with pytest.raises(ValueError, match="GEMINI_API_KEY is not set"):
        parse_email("receipt text")


def test_parse_email_maps_model_response(monkeypatch):
    monkeypatch.setenv("GEMINI_API_KEY", "test-key")
    monkeypatch.setattr(
        "app.parser.genai.Client",
        lambda api_key: FakeClient(
            api_key,
            json.dumps(
                {
                    "description": "Grab Food",
                    "amount": 350.50,
                    "date": "2026-05-09",
                    "category": "Food",
                }
            ),
        ),
    )

    result = parse_email("receipt text")

    assert result.description == "Grab Food"
    assert result.amount == 350.50
    assert result.date == "2026-05-09"
    assert result.category == "Food"
