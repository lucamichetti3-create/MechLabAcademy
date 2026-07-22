#!/usr/bin/env python3
from __future__ import annotations

from collections import Counter
from pathlib import Path
import json
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
SEED = ROOT / "app/src/main/assets/seed"
FILES = [
    "subjects", "lessons", "quiz", "exercises", "flashcards", "glossary",
    "maps", "labs", "videos", "tools", "notes", "study_plan",
]
DATA = {
    name: json.loads((SEED / f"{name}.json").read_text(encoding="utf-8"))
    for name in FILES
}
ERRORS: list[str] = []


def unique_ids(name: str) -> set[str]:
    values = [item["id"] for item in DATA[name]]
    duplicates = [value for value, count in Counter(values).items() if count > 1]
    if duplicates:
        ERRORS.append(f"{name}: duplicate ids {duplicates[:5]}")
    return set(values)


def require_unique_content(name: str, field: str) -> None:
    values = [str(item.get(field, "")).strip() for item in DATA[name]]
    duplicates = [value for value, count in Counter(values).items() if value and count > 1]
    if duplicates:
        ERRORS.append(f"{name}: duplicate {field} values, examples: {duplicates[:3]}")


ids = {name: unique_ids(name) for name in FILES}
subject_ids = ids["subjects"]
lesson_ids = ids["lessons"]

for name in ["lessons", "quiz", "exercises", "flashcards", "glossary", "maps", "labs", "videos"]:
    for item in DATA[name]:
        if item.get("subjectId") not in subject_ids:
            ERRORS.append(f"{name}/{item['id']}: orphan subject {item.get('subjectId')}")

for name in ["quiz", "exercises", "flashcards", "maps", "labs", "videos", "tools", "study_plan"]:
    for item in DATA[name]:
        if item.get("lessonId") not in lesson_ids:
            ERRORS.append(f"{name}/{item['id']}: orphan lesson {item.get('lessonId')}")

for video in DATA["videos"]:
    status = video.get("linkStatus")
    url = video.get("url", "")
    valid_remote = status == "VERIFIED" and re.fullmatch(r"https://[^\s]+", url)
    valid_local = status == "OFFLINE_INCLUDED" and re.fullmatch(r"raw:[a-z0-9_]+", url)
    if not (valid_remote or valid_local):
        ERRORS.append(f"videos/{video['id']}: invalid video source")

minimums = {
    "subjects": 12,
    "lessons": 800,
    "quiz": 2_000,
    "exercises": 1_000,
    "flashcards": 2_000,
    "glossary": 2_500,
    "maps": 200,
    "labs": 200,
    "tools": 150,
}
for name, minimum in minimums.items():
    if len(DATA[name]) < minimum:
        ERRORS.append(f"{name}: {len(DATA[name])} < {minimum}")

for name, field in [
    ("lessons", "title"),
    ("quiz", "prompt"),
    ("exercises", "prompt"),
    ("flashcards", "front"),
    ("glossary", "italianTerm"),
    ("maps", "title"),
    ("labs", "title"),
]:
    require_unique_content(name, field)

for lesson in DATA["lessons"]:
    required = ["title", "objectives", "introduction", "explanation", "summary", "status"]
    missing = [key for key in required if not lesson.get(key)]
    if missing:
        ERRORS.append(f"lessons/{lesson['id']}: empty required fields {missing}")

for map_item in DATA["maps"]:
    node_ids = {node.get("id") for node in map_item.get("nodes", [])}
    for edge in map_item.get("edges", []):
        if edge.get("from") not in node_ids or edge.get("to") not in node_ids:
            ERRORS.append(f"maps/{map_item['id']}: orphan edge {edge}")

for path in ROOT.rglob("*.xml"):
    try:
        ET.parse(path)
    except Exception as exc:
        ERRORS.append(f"XML {path.relative_to(ROOT)}: {exc}")

print(json.dumps({name: len(DATA[name]) for name in FILES}, indent=2, ensure_ascii=False))
if ERRORS:
    print("\nVALIDATION ERRORS:", file=sys.stderr)
    print("\n".join(ERRORS[:100]), file=sys.stderr)
    sys.exit(1)
print("Dataset, IDs, unique content fields, foreign references, map edges, video sources and XML: OK")
