from fastapi import FastAPI
from app.schemas import AskRequest, AskResponse, Citation

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