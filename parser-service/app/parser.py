import os

from .models import ParsedExpense, FieldConfidence, Decision, ExtractionResult, Confidence
from google import genai
from decimal import Decimal

HIGH_CONFIDENCE_THRESHOLD = Decimal("0.90")
MEDIUM_CONFIDENCE_THRESHOLD = Decimal("0.70")

def calculate_overall_confidence(fields: FieldConfidence) -> Decimal:
    return min(
        fields.description,
        fields.amount,
        fields.date,
        fields.category
    )

def decide(overall: Decimal) -> Decision:
    if overall >= HIGH_CONFIDENCE_THRESHOLD:
        return "AUTO_SAVE"
    if overall >= MEDIUM_CONFIDENCE_THRESHOLD:
        return "SUGGEST"
    return "REVIEW"

def build_warnings(result: ExtractionResult) -> list[str]:
    warnings: list[str] = []

    if result.transaction.amount is None:
        warnings.append("Amount could not be determined")
    if result.transaction.date is None:
        warnings.append("Date could not be determined")
    if result.confidence.category < MEDIUM_CONFIDENCE_THRESHOLD:
        warnings.append("Category confidence is low")

    return warnings

def parse_email(email_text: str) -> ParsedExpense:
    api_key = os.environ.get("GEMINI_API_KEY")

    if not api_key:
        raise ValueError("GEMINI_API_KEY is not set")

    client = genai.Client(api_key=api_key)

    prompt = f"""
    Extract expense data from the email text and estimate confidence for each field.

    Transaction rules:
    - description: merchant or vendor name
    - amount: numeric transaction amount only, or null if unknown
    - date: transaction date in YYYY-MM-DD format, or null if unknown
    - category: one of Food, Transport, Shopping, Utilities, Entertainment, Other

    Confidence rules:
    - return a confidence score from 0.0 to 1.0 for each field
    - 1.0 means the value is explicitly stated and unambiguous
    - 0.7 to 0.9 means strongly inferred from clear context
    - below 0.7 means weakly inferred or uncertain
    - use 0.0 when a field is unknown or missing
    - confidence should reflect the evidence in the email, not how common the merchant is

    Scoring guidance:
    - amount should be high only when a payment total or charged amount is explicit
    - date should be high only when a clear transaction or order date is present
    - description should be high only when the merchant or vendor is directly named
    - category may be inferred from merchant/context, but lower the score if uncertain

    Return the result using the provided schema.

    Email text: {email_text}
    """.strip()

    response = client.models.generate_content(
        model="gemini-2.5-flash",
        contents=prompt,
        config={
            "response_mime_type": "application/json",
            "response_json_schema": ExtractionResult.model_json_schema(),
        },
    )

    result = ExtractionResult.model_validate_json(response.text or "{}")
    overall = calculate_overall_confidence(result.confidence)

    return ParsedExpense(
        transaction=result.transaction,
        confidence=Confidence(
            overall=overall,
            fields=result.confidence,
        ),
        decision=decide(overall),
        warnings=build_warnings(result),
    )
