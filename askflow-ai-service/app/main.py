from fastapi import FastAPI
from app.schemas import AskRequest, AskResponse, Citation
from app.llm_client import generate_llm_answer

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
def rag_ask(request: AskRequest):
    context_chunks = request.context_chunks or []

    context_dicts = []
    for chunk in context_chunks:
        context_dicts.append(
            {
                "chunk_id": chunk.chunk_id,
                "document_name": chunk.document_name,
                "content": chunk.content,
                "score": chunk.score,
            }
        )

    answer, llm_debug = generate_llm_answer(
        question=request.question,
        context_chunks=context_dicts,
    )

    citations = [
        Citation(
            content=chunk.content,
            document_name=chunk.document_name,
            chunk_id=chunk.chunk_id,
        )
        for chunk in context_chunks
    ]

    context_preview = "\n".join(
        [
            f"[{index + 1}] score={chunk.score} | {chunk.content}"
            for index, chunk in enumerate(context_chunks)
        ]
    )
    retrieval_scores = [
        {
            "chunk_id": chunk.chunk_id,
            "document_name": chunk.document_name,
            "score": chunk.score,
        }
        for chunk in context_chunks
    ]

    debug = {
        **llm_debug,
        "retrieval": "enabled" if len(context_chunks) > 0 else "empty",
        "context_count": len(context_chunks),
        "retrieval_scores": retrieval_scores,
        "context_preview": context_preview,
    }

    return AskResponse(
        answer=answer,
        citations=citations,
        debug=debug,
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