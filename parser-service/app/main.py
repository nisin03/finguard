from dotenv import load_dotenv
from fastapi import HTTPException

load_dotenv()

from fastapi import FastAPI

from .models import ParseRequest
from .parser import parse_email

app = FastAPI()

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/parse")
def parse_request(request: ParseRequest):
    try:
        return parse_email(request.email_text)
    except ValueError as exc:
        raise HTTPException(status_code=500, detail=str(exc)) from exc
