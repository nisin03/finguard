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

class ParseRequest(BaseModel):
    email_text: str

class ParsedExpense(BaseModel):
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