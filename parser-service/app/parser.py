import os
import json

from .models import ParsedExpense
from google import genai

def parse_email(email_text: str) -> ParsedExpense:
    api_key = os.environ.get("GEMINI_API_KEY")

    if not api_key:
        raise ValueError("GEMINI_API_KEY is not set")

    client = genai.Client(api_key=api_key)

    prompt = f"""
    Extract expense data from this email text.

    Rules:
    - description should be the merchant or vendor name
    - amount should be the numeric transaction amount
    - date should be the transaction date
    - category should be the best matching allowed category
    - use null when amount or date cannot be determined

    Email text: {email_text}
    """.strip()

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt,
        config={
            "response_mime_type": "application/json",
            "response_json_schema": ParsedExpense.model_json_schema(),
        },
    )

    return ParsedExpense.model_validate_json(response.text or "{}")
