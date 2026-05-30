from pathlib import Path
from typing import Any

import chromadb
from sentence_transformers import SentenceTransformer


BASE_DIR = Path(__file__).resolve().parent.parent
CHROMA_DIR = BASE_DIR / "data" / "chroma"
COLLECTION_NAME = "askflow_chunks"

# 支持中文的轻量多语言 embedding 模型
MODEL_NAME = "paraphrase-multilingual-MiniLM-L12-v2"

_model = SentenceTransformer(MODEL_NAME)
_client = chromadb.PersistentClient(path=str(CHROMA_DIR))
_collection = _client.get_or_create_collection(
    name=COLLECTION_NAME,
    metadata={"hnsw:space": "cosine"},
)


def _embed_texts(texts: list[str]) -> list[list[float]]:
    embeddings = _model.encode(texts, normalize_embeddings=True)
    return embeddings.tolist()


def upsert_chunks(chunks: list[dict[str, Any]]) -> int:
    if not chunks:
        return 0

    ids: list[str] = []
    documents: list[str] = []
    metadatas: list[dict[str, Any]] = []

    for chunk in chunks:
        chunk_id = int(chunk["chunk_id"])

        ids.append(str(chunk_id))
        documents.append(chunk["content"])
        metadatas.append(
            {
                "chunk_id": chunk_id,
                "space_id": int(chunk["space_id"]),
                "document_id": int(chunk["document_id"]),
                "document_title": chunk.get("document_title") or "",
                "chunk_index": int(chunk.get("chunk_index") or 0),
                "token_count": int(chunk.get("token_count") or 0),
            }
        )

    embeddings = _embed_texts(documents)

    _collection.upsert(
        ids=ids,
        embeddings=embeddings,
        documents=documents,
        metadatas=metadatas,
    )

    return len(chunks)


def search_chunks(space_id: int, question: str, top_k: int = 5, min_score: float = 0.0) -> list[dict[str, Any]]:
    query_embedding = _embed_texts([question])[0]

    result = _collection.query(
        query_embeddings=[query_embedding],
        n_results=top_k,
        where={"space_id": int(space_id)},
        include=["documents", "metadatas", "distances"],
    )

    hits: list[dict[str, Any]] = []

    documents = result.get("documents", [[]])[0]
    metadatas = result.get("metadatas", [[]])[0]
    distances = result.get("distances", [[]])[0]

    for document, metadata, distance in zip(documents, metadatas, distances):
        # Chroma cosine distance 越小越相似，这里转成 score，越大越相似
        score = 1.0 - float(distance)

        if score < min_score:
            continue

        hits.append(
            {
                "chunk_id": metadata["chunk_id"],
                "space_id": metadata["space_id"],
                "document_id": metadata["document_id"],
                "document_title": metadata["document_title"],
                "chunk_index": metadata["chunk_index"],
                "token_count": metadata["token_count"],
                "content": document,
                "score": round(score, 4),
            }
        )

    return hits