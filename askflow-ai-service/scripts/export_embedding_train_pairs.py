import json
import os
from pathlib import Path
from typing import Any

import pymysql


BASE_DIR = Path(__file__).resolve().parent.parent
OUTPUT_DIR = BASE_DIR / "data" / "rag_finetune"
OUTPUT_FILE = OUTPUT_DIR / "train_pairs.jsonl"


DB_HOST = os.getenv("ASKFLOW_DB_HOST", "127.0.0.1")
DB_PORT = int(os.getenv("ASKFLOW_DB_PORT", "3306"))
DB_NAME = os.getenv("ASKFLOW_DB_NAME", "askflow")
DB_USER = os.getenv("ASKFLOW_DB_USERNAME", "askflow_user")
DB_PASSWORD = os.getenv("ASKFLOW_DB_PASSWORD", "Askflow@123456")


def connect():
    return pymysql.connect(
        host=DB_HOST,
        port=DB_PORT,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
    )


def get_table_columns(conn, table_name: str) -> set[str]:
    with conn.cursor() as cursor:
        cursor.execute(f"SHOW COLUMNS FROM {table_name}")
        rows = cursor.fetchall()
    return {row["Field"] for row in rows}


def pick_column(columns: set[str], candidates: list[str]) -> str:
    for name in candidates:
        if name in columns:
            return name
    raise RuntimeError(f"Cannot find any column from candidates: {candidates}")


def parse_expected_chunk_ids(value: Any) -> list[int]:
    if value is None:
        return []

    if isinstance(value, int):
        return [value]

    text = str(value).strip()
    if not text:
        return []

    # JSON array: [16, 32] or ["16", "32"]
    try:
        data = json.loads(text)
        if isinstance(data, list):
            return [int(x) for x in data]
        if isinstance(data, int):
            return [data]
        if isinstance(data, str) and data.isdigit():
            return [int(data)]
    except Exception:
        pass

    # fallback: comma-separated: 16,32
    ids = []
    for part in text.replace("[", "").replace("]", "").replace('"', "").split(","):
        part = part.strip()
        if part.isdigit():
            ids.append(int(part))

    return ids


def fetch_eval_cases(conn):
    columns = get_table_columns(conn, "rag_eval_case")

    question_col = pick_column(columns, ["question"])
    expected_col = pick_column(
        columns,
        [
            "expected_chunk_ids",
            "expected_chunk_ids_json",
            "expectedChunkIds",
            "expected_chunks",
            "expected_chunk_id",
        ],
    )

    where_parts = []
    if "status" in columns:
        where_parts.append("status = 1")

    where_sql = ""
    if where_parts:
        where_sql = "WHERE " + " AND ".join(where_parts)

    sql = f"""
        SELECT id, {question_col} AS question, {expected_col} AS expected_chunk_ids
        FROM rag_eval_case
        {where_sql}
        ORDER BY id ASC
    """

    with conn.cursor() as cursor:
        cursor.execute(sql)
        return cursor.fetchall()


def fetch_chunk_content(conn, chunk_id: int) -> str | None:
    with conn.cursor() as cursor:
        cursor.execute(
            """
            SELECT content
            FROM kb_document_chunk
            WHERE id = %s
            """,
            (chunk_id,),
        )
        row = cursor.fetchone()

    if not row:
        return None

    content = row.get("content")
    if content is None or not str(content).strip():
        return None

    return str(content).strip()


def main():
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    train_pairs = []
    skipped_cases = []

    conn = connect()
    try:
        cases = fetch_eval_cases(conn)

        for case in cases:
            case_id = case["id"]
            question = str(case["question"]).strip()
            expected_chunk_ids = parse_expected_chunk_ids(case["expected_chunk_ids"])

            if not question or not expected_chunk_ids:
                skipped_cases.append(
                    {
                        "case_id": case_id,
                        "reason": "empty question or expected_chunk_ids",
                    }
                )
                continue

            for chunk_id in expected_chunk_ids:
                content = fetch_chunk_content(conn, chunk_id)

                if not content:
                    skipped_cases.append(
                        {
                            "case_id": case_id,
                            "chunk_id": chunk_id,
                            "reason": "chunk content not found",
                        }
                    )
                    continue

                train_pairs.append(
                    {
                        "query": question,
                        "positive": content,
                        "case_id": case_id,
                        "chunk_id": chunk_id,
                    }
                )

    finally:
        conn.close()

    with OUTPUT_FILE.open("w", encoding="utf-8") as f:
        for item in train_pairs:
            f.write(json.dumps(item, ensure_ascii=False) + "\n")

    print(f"Exported train pairs: {len(train_pairs)}")
    print(f"Output file: {OUTPUT_FILE}")

    if skipped_cases:
        print(f"Skipped items: {len(skipped_cases)}")
        print("First 10 skipped:")
        for item in skipped_cases[:10]:
            print(item)


if __name__ == "__main__":
    main()