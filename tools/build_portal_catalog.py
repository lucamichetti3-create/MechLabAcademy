#!/usr/bin/env python3
"""Genera il catalogo compatto del portale dai seed Android."""
from __future__ import annotations

from datetime import date
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SEED = ROOT / "app" / "src" / "main" / "assets" / "seed"
OUT = ROOT / "portal" / "data" / "catalog.json"


def read(name: str):
    return json.loads((SEED / f"{name}.json").read_text(encoding="utf-8"))


def main() -> None:
    subjects = read("subjects")
    lessons = read("lessons")
    videos = read("videos")
    compact_lessons = [
        {
            key: lesson.get(key)
            for key in (
                "id", "subjectId", "year", "macroarea", "module", "title",
                "durationMinutes", "difficulty", "objectives", "introduction",
                "summary", "status",
            )
        }
        for lesson in lessons
    ]
    portal_videos = []
    for video in videos:
        item = dict(video)
        if item.get("platform") == "MECHLAB_LOCAL" and str(item.get("url", "")).startswith("raw:"):
            item["url"] = "media/" + item["url"].removeprefix("raw:") + ".mp4"
        portal_videos.append(item)
    payload = {
        "version": 2,
        "generated": date.today().isoformat(),
        "subjects": subjects,
        "lessons": compact_lessons,
        "videos": portal_videos,
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(payload, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    print(f"Catalogo portale: {len(subjects)} materie, {len(compact_lessons)} lezioni, {len(portal_videos)} video")


if __name__ == "__main__":
    main()
