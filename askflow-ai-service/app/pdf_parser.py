import io
from typing import Dict, List, Optional

import fitz  # PyMuPDF
import pdfplumber


def parse_pdf_text(file_bytes: bytes) -> List[Dict]:
    """
    Parse PDF into structured chunks.

    Output example:
    [
        {
            "page_no": 1,
            "chunk_type": "TEXT",
            "section_title": None,
            "content": "..."
        },
        {
            "page_no": 5,
            "chunk_type": "TABLE",
            "section_title": "故障排查矩阵",
            "content": "| 故障现象 | 可能原因 | 排查动作 | 期望处理 | ..."
        }
    ]
    """
    text_chunks = _parse_text_chunks(file_bytes)
    table_chunks = _parse_table_chunks(file_bytes)

    chunks = text_chunks + table_chunks
    chunks.sort(key=lambda item: (item.get("page_no") or 0, _type_order(item.get("chunk_type"))))

    return chunks


def _parse_text_chunks(file_bytes: bytes) -> List[Dict]:
    doc = fitz.open(stream=file_bytes, filetype="pdf")
    results = []

    for page_index, page in enumerate(doc):
        text = page.get_text("text").strip()

        if not text:
            continue

        results.append({
            "page_no": page_index + 1,
            "chunk_type": "TEXT",
            "section_title": _guess_section_title(text),
            "content": text
        })

    doc.close()
    return results


def _parse_table_chunks(file_bytes: bytes) -> List[Dict]:
    results = []

    with pdfplumber.open(io.BytesIO(file_bytes)) as pdf:
        for page_index, page in enumerate(pdf.pages):
            tables = page.extract_tables()

            if not tables:
                continue

            for table_index, table in enumerate(tables):
                markdown = _table_to_markdown(table)

                if not markdown:
                    continue

                results.append({
                    "page_no": page_index + 1,
                    "chunk_type": "TABLE",
                    "section_title": f"page_{page_index + 1}_table_{table_index + 1}",
                    "content": markdown
                })

    return results


def _table_to_markdown(table: List[List[Optional[str]]]) -> str:
    if not table:
        return ""

    cleaned_rows = []
    for row in table:
        if not row:
            continue

        cleaned = [_clean_cell(cell) for cell in row]

        if any(cell for cell in cleaned):
            cleaned_rows.append(cleaned)

    if len(cleaned_rows) < 2:
        return ""

    header = cleaned_rows[0]
    col_count = len(header)

    normalized_rows = []
    for row in cleaned_rows:
        normalized = row[:col_count] + [""] * max(0, col_count - len(row))
        normalized_rows.append(normalized)

    lines = []
    lines.append("| " + " | ".join(normalized_rows[0]) + " |")
    lines.append("| " + " | ".join(["---"] * col_count) + " |")

    for row in normalized_rows[1:]:
        lines.append("| " + " | ".join(row) + " |")

    return "\n".join(lines)


def _clean_cell(cell: Optional[str]) -> str:
    if cell is None:
        return ""

    return " ".join(str(cell).replace("\n", " ").split()).strip()


def _guess_section_title(text: str) -> Optional[str]:
    if not text:
        return None

    lines = [line.strip() for line in text.splitlines() if line.strip()]
    if not lines:
        return None

    first = lines[0]

    if len(first) <= 40:
        return first

    return None


def _type_order(chunk_type: Optional[str]) -> int:
    if chunk_type == "TEXT":
        return 0
    if chunk_type == "TABLE":
        return 1
    return 9