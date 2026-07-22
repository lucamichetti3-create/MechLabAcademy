#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import re
import sys
import tomllib

ROOT = Path(__file__).resolve().parents[1]
errors: list[str] = []

required = [
    "README.md",
    "CONTENT_GUIDE.md",
    "PROGRAMMA_FONTI.md",
    "BUILD_VERIFICATION.md",
    "app/build.gradle.kts",
    "app/src/main/AndroidManifest.xml",
    ".github/workflows/android.yml",
    "tools/validate_dataset.py",
    "tools/build_portal_catalog.py",
    "tools/mechlab_studio/generate_pilot.py",
    "portal/index.html",
    "portal/app.js",
    "portal/sw.js",
    "ECOSYSTEM_2.0.md",
    "VIDEO_STUDIO.md",
]
for relative in required:
    if not (ROOT / relative).is_file():
        errors.append(f"missing required file: {relative}")

root_gradle = (ROOT / "build.gradle.kts").read_text(encoding="utf-8")
app_gradle = (ROOT / "app/build.gradle.kts").read_text(encoding="utf-8")
versions = tomllib.loads((ROOT / "gradle/libs.versions.toml").read_text(encoding="utf-8"))["versions"]

if "org.jetbrains.kotlin.android" in root_gradle + app_gradle:
    errors.append("AGP 9 built-in Kotlin: org.jetbrains.kotlin.android must not be applied")
if 'id("com.android.application") version "9.3.0"' not in root_gradle:
    errors.append("AGP 9.3.0 not configured")
for key, expected in {"compileSdk": "37", "targetSdk": "36", "minSdk": "23"}.items():
    if versions.get(key) != expected:
        errors.append(f"{key} expected {expected}, found {versions.get(key)}")
if versions.get("room") != "2.8.4":
    errors.append("Room version mismatch")
if versions.get("navigation") != "2.9.8":
    errors.append("Navigation version mismatch")
if versions.get("work") != "2.11.2":
    errors.append("WorkManager version mismatch")
if versions.get("media3") != "1.10.1":
    errors.append("Media3 version mismatch")
if 'versionName = "2.0.0"' not in app_gradle:
    errors.append("App versionName 2.0.0 not configured")

all_text = "\n".join(
    path.read_text(encoding="utf-8", errors="ignore")
    for path in ROOT.rglob("*")
    if path.is_file() and path != Path(__file__) and path.suffix.lower() in {".kt", ".kts", ".xml", ".md", ".json", ".py", ".toml", ".yml"}
)
if re.search(r"lorem\s+ipsum", all_text, flags=re.IGNORECASE):
    errors.append("Lorem ipsum placeholder found")

package = "it.lucamichetti.mechlabacademy"
for path in (ROOT / "app/src/main/java").rglob("*.kt"):
    text = path.read_text(encoding="utf-8")
    if f"package {package}" not in text and f"package {package}." not in text:
        errors.append(f"unexpected package in {path.relative_to(ROOT)}")
    if text.count("{") != text.count("}"):
        errors.append(f"unbalanced braces in {path.relative_to(ROOT)}")

routes = (ROOT / "app/src/main/java/it/lucamichetti/mechlabacademy/ui/Routes.kt").read_text(encoding="utf-8")
for route in ["HOME", "SUBJECTS", "PLAN", "EXERCISES", "PROFILE", "SEARCH", "VIDEOS", "MAPS", "FLASHCARDS", "LABS", "GLOSSARY", "NOTES", "TOOLS", "QUIZ", "TODAY", "SIMULATORS"]:
    if f"const val {route}" not in routes:
        errors.append(f"missing route {route}")

raw_dir = ROOT / "app/src/main/res/raw"
local_videos = sorted(raw_dir.glob("mechlab_*.mp4"))
if len(local_videos) < 10:
    errors.append(f"expected at least 10 local MechLab videos, found {len(local_videos)}")
for video in local_videos:
    if video.stat().st_size < 100_000:
        errors.append(f"local video too small or corrupt: {video.name}")
    portal_copy = ROOT / "portal/media" / video.name
    if not portal_copy.is_file():
        errors.append(f"missing portal copy for {video.name}")

if errors:
    print("PROJECT VALIDATION ERRORS:", file=sys.stderr)
    print("\n".join(errors), file=sys.stderr)
    sys.exit(1)
print("Project structure, versions, packages, routes, ecosystem, video assets and portal copies: OK")
