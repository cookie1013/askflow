from pydantic import BaseModel
from typing import List, Dict, Any, Optional
from pydantic import BaseModel, Field
from typing import Optional
class ContextChunk(BaseModel):
    chunk_id: str
    document_name: str
    content: str
    score: Optional[float] = None
    page_no: Optional[int] = None
    chunk_type: Optional[str] = None
    section_title: Optional[str] = None


class AskRequest(BaseModel):
    question: str
    context_chunks: Optional[List[ContextChunk]] = []


class Citation(BaseModel):
    document_name: str
    chunk_id: str
    content: str
    page_no: Optional[int] = None
    chunk_type: Optional[str] = None
    section_title: Optional[str] = None
    score: Optional[float] = None

class AskResponse(BaseModel):
    answer: str
    citations: List[Citation]
    debug: Dict[str, Any] = {}

from typing import Optional
class VectorChunk(BaseModel):
    chunk_id: int
    space_id: int
    document_id: int
    document_title: str = ""
    chunk_index: int = 0
    content: str
    token_count: int = 0
    page_no: Optional[int] = None
    chunk_type: Optional[str] = None
    section_title: Optional[str] = None


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
    page_no: Optional[int] = None
    chunk_type: Optional[str] = None
    section_title: Optional[str] = None


class VectorSearchResponse(BaseModel):
    hits: list[VectorSearchHit]
    debug: dict

class VectorDeleteByDocumentRequest(BaseModel):
    document_id: int


class VectorDeleteByDocumentResponse(BaseModel):
    document_id: int
    deleted_count: int