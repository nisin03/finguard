from dotenv import load_dotenv

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
    return parse_email(request.email_text)
