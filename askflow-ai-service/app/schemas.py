from pydantic import BaseModel
from typing import List, Dict, Any


class AskRequest(BaseModel):
    question: str


class Citation(BaseModel):
    document_name: str
    chunk_id: str
    content: str


class AskResponse(BaseModel):
    answer: str
    citations: List[Citation]
    debug: Dict[str, Any] = {}