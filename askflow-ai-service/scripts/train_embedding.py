import json
import random
from pathlib import Path

from sentence_transformers import SentenceTransformer, InputExample, losses
from torch.utils.data import DataLoader


BASE_DIR = Path(__file__).resolve().parent.parent

TRAIN_PAIRS_FILE = BASE_DIR / "data" / "rag_finetune" / "train_pairs.jsonl"
OUTPUT_DIR = BASE_DIR / "models" / "askflow-embedding-ft"

BASE_MODEL_NAME = "paraphrase-multilingual-MiniLM-L12-v2"


def load_pairs():
    pairs = []

    with TRAIN_PAIRS_FILE.open("r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            item = json.loads(line)
            query = item.get("query", "").strip()
            positive = item.get("positive", "").strip()

            if not query or not positive:
                continue

            pairs.append(
                {
                    "query": query,
                    "positive": positive,
                    "case_id": item.get("case_id"),
                    "chunk_id": item.get("chunk_id"),
                }
            )

    return pairs


def main():
    random.seed(42)

    pairs = load_pairs()

    if len(pairs) < 8:
        raise RuntimeError(f"Too few train pairs: {len(pairs)}")

    random.shuffle(pairs)

    split_idx = int(len(pairs) * 0.8)
    train_pairs = pairs[:split_idx]
    dev_pairs = pairs[split_idx:]

    print(f"Total pairs: {len(pairs)}")
    print(f"Train pairs: {len(train_pairs)}")
    print(f"Dev pairs: {len(dev_pairs)}")

    model = SentenceTransformer(BASE_MODEL_NAME)

    train_examples = [
        InputExample(texts=[item["query"], item["positive"]])
        for item in train_pairs
    ]

    train_dataloader = DataLoader(
        train_examples,
        shuffle=True,
        batch_size=8,
        drop_last=True,
    )

    train_loss = losses.MultipleNegativesRankingLoss(model)

    warmup_steps = max(1, int(len(train_dataloader) * 3 * 0.1))

    model.fit(
        train_objectives=[(train_dataloader, train_loss)],
        epochs=3,
        warmup_steps=warmup_steps,
        output_path=str(OUTPUT_DIR),
        show_progress_bar=True,
    )

    dev_file = BASE_DIR / "data" / "rag_finetune" / "dev_pairs.jsonl"
    with dev_file.open("w", encoding="utf-8") as f:
        for item in dev_pairs:
            f.write(json.dumps(item, ensure_ascii=False) + "\n")

    print(f"Fine-tuned model saved to: {OUTPUT_DIR}")
    print(f"Dev pairs saved to: {dev_file}")


if __name__ == "__main__":
    main()