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
    fake_citation = Citation(
        document_name="demo.md",
        chunk_id="chunk-demo-001",
        content="这里是临时引用内容，后续会替换成真实知识库检索结果。"
    )

    return AskResponse(
        answer=f"这是 AskFlow AI 的临时回答。你提出的问题是：{request.question}",
        citations=[fake_citation],
        debug={
            "mode": "mock",
            "retrieval": "not_enabled_yet"
        }
    )