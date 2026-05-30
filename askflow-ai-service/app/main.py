from fastapi import FastAPI
from app.schemas import AskRequest, AskResponse, Citation

from app.schemas import (
    VectorSearchRequest,
    VectorSearchResponse,
    VectorUpsertRequest,
    VectorUpsertResponse,
)
from app.vector_store import search_chunks, upsert_chunks

app = FastAPI(
    title="AskFlow AI Service",
    description="RAG and Agent service for AskFlow AI platform",
    version="0.1.0"
)


@app.get("/health")
def health():
    return {
        "status": "ok",
        "service": "askflow-ai-service"
    }


@app.post("/rag/ask", response_model=AskResponse)
def ask(request: AskRequest):
    context_chunks = request.context_chunks or []

    if not context_chunks:
        return AskResponse(
            answer=f"暂时没有从知识库中检索到相关内容。你提出的问题是：{request.question}",
            citations=[],
            debug={
                "mode": "rag_mock",
                "retrieval": "empty",
                "context_count": 0
            }
        )

    citations = [
        Citation(
            document_name=chunk.document_name,
            chunk_id=chunk.chunk_id,
            content=chunk.content
        )
        for chunk in context_chunks
    ]

    context_text = "\n".join(
        [f"[{i + 1}] {chunk.content}" for i, chunk in enumerate(context_chunks)]
    )

    answer = (
        f"根据知识库检索到的内容，针对问题“{request.question}”，可以得到如下结论：\n"
        f"{context_chunks[0].content}\n\n"
        f"以上回答主要依据已检索到的知识库片段生成。"
    )

    return AskResponse(
        answer=answer,
        citations=citations,
        debug={
            "mode": "rag_mock",
            "retrieval": "enabled",
            "context_count": len(context_chunks),
            "context_preview": context_text[:300]
        }
    )
@app.post("/vector/upsert", response_model=VectorUpsertResponse)
def vector_upsert(request: VectorUpsertRequest):
    chunks = [chunk.model_dump() for chunk in request.chunks]
    indexed_count = upsert_chunks(chunks)
    return VectorUpsertResponse(indexed_count=indexed_count)


@app.post("/vector/search", response_model=VectorSearchResponse)
def vector_search(request: VectorSearchRequest):
    hits = search_chunks(
        space_id=request.space_id,
        question=request.question,
        top_k=request.top_k,
        min_score=request.min_score,
    )

    return VectorSearchResponse(
        hits=hits,
        debug={
            "retrieval": "vector",
            "space_id": request.space_id,
            "top_k": request.top_k,
            "min_score": request.min_score,
            "matched_count": len(hits),
        },
    )