#!/usr/bin/env python3
"""MechLab Studio: genera videolezioni MP4 da uno storyboard JSON.

Requisiti: Python 3, Pillow, espeak, ffmpeg, ffprobe.
Esempio:
  python tools/mechlab_studio/generate_pilot.py \
    --storyboard studio/pilot_forze_momenti/storyboard.json
"""
from __future__ import annotations

import argparse
import json
import math
import re
import subprocess
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[2]
WIDTH, HEIGHT = 960, 540
FPS = 25
FONT_REG = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
FONT_BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
SCENES: list[dict] = []
WORK = ROOT / "studio" / "build"
OUT = ROOT / "app" / "src" / "main" / "res" / "raw" / "mechlab_video.mp4"
MODULE = "Meccanica"

def font(size: int, bold: bool = False):
    return ImageFont.truetype(FONT_BOLD if bold else FONT_REG, size)


def wrap(draw: ImageDraw.ImageDraw, text: str, fnt, width: int) -> list[str]:
    lines: list[str] = []
    for paragraph in text.splitlines():
        if not paragraph:
            lines.append("")
            continue
        words = paragraph.split()
        current = ""
        for word in words:
            test = f"{current} {word}".strip()
            if draw.textbbox((0, 0), test, font=fnt)[2] <= width:
                current = test
            else:
                if current:
                    lines.append(current)
                current = word
        if current:
            lines.append(current)
    return lines



def caption_chunks(text: str, max_chars: int = 92) -> list[str]:
    """Divide il parlato in didascalie brevi senza spezzare le parole."""
    sentences = [part.strip() for part in re.split(r"(?<=[.!?;:])\s+", text.strip()) if part.strip()]
    chunks: list[str] = []
    for sentence in sentences:
        words = sentence.split()
        current = ""
        for word in words:
            candidate = f"{current} {word}".strip()
            if current and len(candidate) > max_chars:
                chunks.append(current)
                current = word
            else:
                current = candidate
        if current:
            chunks.append(current)
    return chunks or [text.strip()]

def draw_arrow(draw, start, end, fill, width=8):
    draw.line([start, end], fill=fill, width=width)
    angle = math.atan2(end[1]-start[1], end[0]-start[0])
    length = 22
    for delta in (2.6, -2.6):
        p = (end[0] + length*math.cos(angle+delta), end[1] + length*math.sin(angle+delta))
        draw.line([end, p], fill=fill, width=width)


def make_slide(scene: dict, index: int) -> Path:
    img = Image.new("RGB", (WIDTH, HEIGHT), "#101820")
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, WIDTH, 12), fill="#f9a825")
    draw.rectangle((0, HEIGHT-62, WIDTH, HEIGHT), fill="#162733")
    draw.text((36, 28), "MECHLAB ACADEMY", font=font(22, True), fill="#f9a825")
    draw.text((36, 72), scene["title"], font=font(42, True), fill="white")

    body_font = font(28)
    body_lines = wrap(draw, scene["body"], body_font, 560)
    y = 150
    for line in body_lines:
        draw.text((44, y), line, font=body_font, fill="#e7eef3")
        y += 40

    kind = scene["kind"]
    steel = "#90a4ae"
    blue = "#42a5f5"
    orange = "#ffb300"
    if kind in {"intro", "lever", "example"}:
        # Leva / chiave stilizzata
        pivot = (760, 370)
        draw.ellipse((pivot[0]-18, pivot[1]-18, pivot[0]+18, pivot[1]+18), fill=orange)
        draw.line((610, 330, 850, 415), fill=steel, width=22)
        draw_arrow(draw, (620, 180), (620, 320), blue)
        draw.text((585, 145), "F", font=font(30, True), fill=blue)
        draw.line((620, 345, pivot[0], pivot[1]), fill="#ef5350", width=4)
        draw.text((675, 350), "b", font=font(28, True), fill="#ef5350")
        draw.arc((710, 310, 850, 445), 210, 340, fill=orange, width=6)
    elif kind == "force":
        draw.rectangle((690, 300, 850, 410), fill="#455a64", outline="#cfd8dc", width=3)
        draw_arrow(draw, (770, 290), (770, 155), blue)
        draw.text((790, 180), "forza", font=font(25, True), fill=blue)
        draw.ellipse((758, 278, 782, 302), fill=orange)
        draw.text((650, 430), "punto di applicazione", font=font(20), fill="#e7eef3")
    elif kind == "formula":
        draw.rounded_rectangle((650, 180, 900, 390), radius=22, fill="#1e3442", outline=orange, width=4)
        draw.text((700, 230), "M", font=font(62, True), fill=orange)
        draw.text((775, 230), "=", font=font(54, True), fill="white")
        draw.text((835, 230), "F·b", font=font(46, True), fill=blue)
        draw.text((690, 320), "N · m", font=font(34, True), fill="#e7eef3")
    elif kind == "balance":
        draw.line((660, 360, 900, 360), fill=steel, width=14)
        draw.polygon([(780, 360), (745, 445), (815, 445)], fill=orange)
        draw_arrow(draw, (690, 180), (690, 345), blue)
        draw_arrow(draw, (870, 210), (870, 345), "#ef5350")
        draw.text((650, 150), "F₁", font=font(28, True), fill=blue)
        draw.text((885, 200), "F₂", font=font(28, True), fill="#ef5350")
    elif kind == "recap":
        draw.rounded_rectangle((665, 165, 895, 420), radius=20, fill="#1e3442", outline=orange, width=4)
        draw.text((710, 205), "M = F·b", font=font(40, True), fill=orange)
        draw.text((710, 280), "ΣM = 0", font=font(40, True), fill=blue)
        draw.text((705, 355), "N·m", font=font(36, True), fill="white")

    draw.text((36, HEIGHT-43), f"Videolezione originale offline • modulo: {MODULE}", font=font(18), fill="#cfd8dc")
    draw.text((WIDTH-95, HEIGHT-43), f"{index+1}/{len(SCENES)}", font=font(18, True), fill="#f9a825")
    path = WORK / f"slide_{index:02d}.png"
    img.save(path, quality=95)
    return path


def run(cmd: list[str]) -> None:
    subprocess.run(cmd, check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Genera una videolezione MechLab da storyboard JSON")
    parser.add_argument("--storyboard", default="studio/pilot_forze_momenti/storyboard.json")
    parser.add_argument("--output", default="", help="Percorso MP4; per default usa res/raw e outputName")
    parser.add_argument("--keep-build", action="store_true", help="Mantiene slide, audio e segmenti intermedi")
    return parser.parse_args()

def main() -> None:
    global SCENES, WORK, OUT, MODULE
    args = parse_args()
    storyboard_path = (ROOT / args.storyboard).resolve() if not Path(args.storyboard).is_absolute() else Path(args.storyboard)
    config = json.loads(storyboard_path.read_text(encoding="utf-8"))
    SCENES = config["scenes"]
    MODULE = config.get("module", "Meccanica")
    slug = config.get("slug", storyboard_path.stem)
    WORK = storyboard_path.parent / "build"
    default_out = ROOT / "app" / "src" / "main" / "res" / "raw" / config.get("outputName", f"mechlab_{slug}.mp4")
    OUT = Path(args.output).resolve() if args.output else default_out
    language = config.get("language", "it")
    voice_speed = str(config.get("voiceSpeed", 148))
    voice_pitch = str(config.get("voicePitch", 45))

    WORK.mkdir(parents=True, exist_ok=True)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    segments: list[Path] = []
    srt_lines: list[str] = []
    timeline = 0.0

    for i, scene in enumerate(SCENES):
        slide = make_slide(scene, i)
        wav = WORK / f"voice_{i:02d}.wav"
        run(["espeak", "-v", language, "-s", voice_speed, "-p", voice_pitch, "-w", str(wav), scene["voice"]])
        duration = float(subprocess.check_output([
            "ffprobe", "-v", "error", "-show_entries", "format=duration",
            "-of", "default=noprint_wrappers=1:nokey=1", str(wav)
        ]).decode().strip()) + 0.45
        segment = WORK / f"segment_{i:02d}.mp4"
        run([
            "ffmpeg", "-y", "-loop", "1", "-i", str(slide), "-i", str(wav),
            "-vf", f"scale={WIDTH}:{HEIGHT},format=yuv420p",
            "-c:v", "libx264", "-preset", "veryfast", "-crf", "28",
            "-c:a", "aac", "-b:a", "96k", "-r", str(FPS), "-shortest", "-t", f"{duration:.2f}", str(segment)
        ])
        segments.append(segment)

        def stamp(value: float) -> str:
            hours = int(value // 3600)
            minutes = int((value % 3600) // 60)
            seconds = value % 60
            return f"{hours:02d}:{minutes:02d}:{seconds:06.3f}".replace('.', ',')

        chunks = caption_chunks(scene["voice"])
        total_weight = sum(max(len(chunk), 1) for chunk in chunks)
        caption_start = timeline
        for chunk in chunks:
            chunk_duration = duration * max(len(chunk), 1) / total_weight
            caption_end = caption_start + chunk_duration
            srt_lines += [
                str(len([line for line in srt_lines if line.isdigit()]) + 1),
                f"{stamp(caption_start)} --> {stamp(caption_end)}",
                chunk,
                "",
            ]
            caption_start = caption_end
        timeline += duration

    concat = WORK / "concat.txt"
    concat.write_text("\n".join(f"file '{p.as_posix()}'" for p in segments), encoding="utf-8")
    subtitle_out = storyboard_path.parent / "subtitles.srt"
    subtitle_out.write_text("\n".join(srt_lines), encoding="utf-8")

    base_video = WORK / "base.mp4"
    run([
        "ffmpeg", "-y", "-f", "concat", "-safe", "0", "-i", str(concat),
        "-c", "copy", "-movflags", "+faststart", str(base_video)
    ])

    if config.get("burnSubtitles", True):
        # Didascalie compatte, leggibili e tenute sopra il footer. Lo sfondo
        # semitrasparente evita che il testo si confonda con schemi e formule.
        escaped_srt = subtitle_out.as_posix().replace("\\", "\\\\").replace(":", "\\:").replace("'", "\\'")
        style = (
            "FontName=DejaVu Sans,FontSize=16,PrimaryColour=&H00FFFFFF,"
            "OutlineColour=&HAA000000,BackColour=&H80000000,"
            "BorderStyle=3,Outline=1,Shadow=0,Alignment=2,MarginV=66"
        )
        run([
            "ffmpeg", "-y", "-i", str(base_video),
            "-vf", f"subtitles='{escaped_srt}':force_style='{style}'",
            "-c:v", "libx264", "-preset", "veryfast", "-crf", "25",
            "-c:a", "copy", "-movflags", "+faststart", str(OUT),
        ])
    else:
        base_video.replace(OUT)
    if not args.keep_build:
        import shutil
        shutil.rmtree(WORK, ignore_errors=True)
    print(f"Creato: {OUT} ({OUT.stat().st_size / 1024 / 1024:.2f} MiB)")
    print(f"Sottotitoli: {subtitle_out}")

if __name__ == "__main__":
    main()
