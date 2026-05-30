from pydantic import BaseModel
from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field

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

class VectorChunk(BaseModel):
    chunk_id: int
    space_id: int
    document_id: int
    document_title: str = ""
    chunk_index: int = 0
    content: str
    token_count: int = 0


class VectorUpsertRequest(BaseModel):
    chunks: list[VectorChunk]


class VectorUpsertResponse(BaseModel):
    indexed_count: int


class VectorSearchRequest(BaseModel):
    space_id: int
    question: str
    top_k: int = Field(default=5, ge=1, le=20)
    min_score: float = Field(default=0.0, ge=0.0, le=1.0)


class VectorSearchHit(BaseModel):
    chunk_id: int
    space_id: int
    document_id: int
    document_title: str
    chunk_index: int
    token_count: int
    content: str
    score: float


class VectorSearchResponse(BaseModel):
    hits: list[VectorSearchHit]
    debug: dict