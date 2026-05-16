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

    Return JSON only.
    No markdown.
    No explanation.
    No code fences.

    Fields:
    - description: merchant or vendor name
    - amount: numbers only, no currency symbols
    - date: YYYY-MM-DD format
    - category: one of Food, Transport, Shopping, Utilities, Entertainment, Other

    If any field can't be determined, use "unknown".

    Email text: {email_text}
    """.strip()

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt,
    )

    response_text = response.text if response.text is not None else "{}"
    data = json.loads(response_text)

    return ParsedExpense(
        description=data.get("description", "unknown"),
        amount=data.get("amount", "unknown"),
        date=data.get("date", "unknown"),
        category=data.get("category", "unknown"),
    )
