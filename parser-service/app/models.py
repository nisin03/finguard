from pydantic import BaseModel

class ParseRequest(BaseModel):
    email_text: str


class ParsedExpense(BaseModel):
    description: str
    amount: str
    date: str
    category: str