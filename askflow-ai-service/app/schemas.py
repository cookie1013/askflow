from pydantic import BaseModel
from typing import List, Dict, Any, Optional


class ContextChunk(BaseModel):
    chunk_id: str
    document_name: str
    content: str


class AskRequest(BaseModel):
    question: str
    context_chunks: Optional[List[ContextChunk]] = []


class Citation(BaseModel):
    document_name: str
    chunk_id: str
    content: str


class AskResponse(BaseModel):
    answer: str
    citations: List[Citation]
    debug: Dict[str, Any] = {}