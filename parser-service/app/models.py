from datetime import date as Date
from decimal import Decimal
from typing import Literal

from pydantic import BaseModel, Field

ExpenseCategory = Literal["Food",
                          "Transport",
                          "Shopping",
                          "Utilities",
                          "Entertainment",
                          "Other",
                          ]

Decision = Literal["AUTO_SAVE",
                   "SUGGEST",
                   "REVIEW"]

class ParseRequest(BaseModel):
    email_text: str

class Transaction(BaseModel):
    description: str = Field(description="Merchant or vendor name")
    amount: Decimal | None = Field(
        description="Expense amount as a positive number, or null if unknown"
    )
    date: Date | None = Field(
        description="Transaction date in YYYY-MM-DD format, or null if unknown"
    )
    category: ExpenseCategory = Field(
        description="Best matching expense category"
    )

class FieldConfidence(BaseModel):
    description: Decimal = Field(ge=0, le=1)
    amount: Decimal = Field(ge=0, le=1)
    date: Decimal = Field(ge=0, le=1)
    category: Decimal = Field(ge=0, le=1)

class ExtractionResult(BaseModel):
    transaction: Transaction
    confidence: FieldConfidence

class Confidence(BaseModel):
    overall: Decimal = Field(ge=0, le=1)
    fields: FieldConfidence

class ParsedExpense(BaseModel):
    transaction: Transaction = Field(description="Business Data")
    confidence: Confidence = Field(description="Parser Judgement")
    decision: Decision = Field(description="Workflow Recommendation")
    warnings: list[str] = Field(default_factory=list)
