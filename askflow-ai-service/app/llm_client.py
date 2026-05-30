import os
from typing import Any

import requests


LLM_PROVIDER = os.getenv("LLM_PROVIDER", "mock").lower()
LLM_BASE_URL = os.getenv("LLM_BASE_URL", "https://api.openai.com/v1")
LLM_API_KEY = os.getenv("LLM_API_KEY", "")
LLM_MODEL = os.getenv("LLM_MODEL", "gpt-4o-mini")


def build_rag_prompt(question: str, context_chunks: list[dict[str, Any]]) -> list[dict[str, str]]:
    context_text = "\n\n".join(
        [
            f"[{index + 1}] 文档：{chunk.get('document_name', '')}\n"
            f"片段ID：{chunk.get('chunk_id', '')}\n"
            f"内容：{chunk.get('content', '')}"
            for index, chunk in enumerate(context_chunks)
        ]
    )

    system_prompt = (
        "你是 AskFlow 企业知识库问答助手。"
        "你必须严格依据给定的知识库片段回答问题。"
        "如果知识库片段中没有足够依据，请明确说明“知识库中没有检索到足够依据”，不要编造。"
        "回答要简洁、准确，并尽量给出排查步骤或结论。"
    )

    user_prompt = f"""
用户问题：
{question}

知识库片段：
{context_text}

请基于以上知识库片段回答用户问题。
"""

    return [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_prompt},
    ]


def generate_mock_answer(question: str, context_chunks: list[dict[str, Any]]) -> str:
    if not context_chunks:
        return f"暂时没有从知识库中检索到相关内容。你提出的问题是：{question}"

    first_context = context_chunks[0].get("content", "")

    return (
        f"根据知识库检索到的内容，针对问题“{question}”，可以得到如下结论：\n"
        f"{first_context}\n\n"
        f"以上回答主要依据已检索到的知识库片段生成。"
    )


def generate_llm_answer(question: str, context_chunks: list[dict[str, Any]]) -> tuple[str, dict[str, Any]]:
    if LLM_PROVIDER == "mock":
        answer = generate_mock_answer(question, context_chunks)
        return answer, {
            "mode": "rag_mock",
            "llm_provider": "mock",
            "model": None,
        }

    if not LLM_API_KEY:
        answer = generate_mock_answer(question, context_chunks)
        return answer, {
            "mode": "rag_mock",
            "llm_provider": LLM_PROVIDER,
            "model": LLM_MODEL,
            "fallback_reason": "LLM_API_KEY is empty",
        }

    messages = build_rag_prompt(question, context_chunks)

    url = LLM_BASE_URL.rstrip("/") + "/chat/completions"

    payload = {
        "model": LLM_MODEL,
        "messages": messages,
        "temperature": 0.2,
    }

    headers = {
        "Authorization": f"Bearer {LLM_API_KEY}",
        "Content-Type": "application/json",
    }

    try:
        response = requests.post(url, headers=headers, json=payload, timeout=60)
        response.raise_for_status()
        data = response.json()

        answer = data["choices"][0]["message"]["content"]

        return answer, {
            "mode": "rag_real_llm",
            "llm_provider": LLM_PROVIDER,
            "model": LLM_MODEL,
        }

    except Exception as exc:
        answer = generate_mock_answer(question, context_chunks)
        return answer, {
            "mode": "rag_mock",
            "llm_provider": LLM_PROVIDER,
            "model": LLM_MODEL,
            "fallback_reason": str(exc),
        }