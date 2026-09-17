#!/usr/bin/env python3
"""Generate favicon / apple-touch / OG images into website/public/.

Run from repo root or website/:
  python3 website/scripts/generate-seo-assets.py

Requires: Pillow (pip install pillow)
"""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError as e:
    raise SystemExit("Pillow required: pip install pillow") from e


def brand_icon(size: int) -> Image.Image:
    img = Image.new("RGBA", (size, size), (5, 7, 13, 255))
    d = ImageDraw.Draw(img)
    margin = max(2, size // 8)
    d.rounded_rectangle(
        [margin, margin, size - margin - 1, size - margin - 1],
        radius=max(2, size // 6),
        outline=(25, 230, 210, 255),
        width=max(2, size // 16),
    )
    cx = cy = size // 2
    r = size // 4
    d.polygon(
        [(cx, cy - r), (cx + r, cy), (cx, cy + r), (cx - r, cy)],
        fill=(25, 230, 210, 230),
    )
    return img


def og_image() -> Image.Image:
    og = Image.new("RGB", (1200, 630), (5, 7, 13))
    d = ImageDraw.Draw(og)
    d.rectangle([0, 0, 1200, 8], fill=(25, 230, 210))
    cx, cy, r = 600, 280, 80
    d.polygon(
        [(cx, cy - r), (cx + r, cy), (cx, cy + r), (cx - r, cy)],
        fill=(25, 230, 210),
    )
    try:
        font_large = ImageFont.truetype(
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 72
        )
        font_small = ImageFont.truetype(
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 28
        )
    except OSError:
        font_large = ImageFont.load_default()
        font_small = font_large
    d.text((600, 420), "SPATIAL", fill=(255, 255, 255), font=font_large, anchor="mm")
    d.text(
        (600, 500),
        "Declarative 3D for Android · Jetpack Compose",
        fill=(180, 190, 200),
        font=font_small,
        anchor="mm",
    )
    return og


def main() -> None:
    script_dir = Path(__file__).resolve().parent
    public = script_dir.parent / "public"
    public.mkdir(parents=True, exist_ok=True)

    brand_icon(32).save(public / "favicon-32x32.png", "PNG")
    brand_icon(180).save(public / "apple-touch-icon.png", "PNG")
    brand_icon(192).save(public / "android-chrome-192x192.png", "PNG")
    brand_icon(512).save(public / "android-chrome-512x512.png", "PNG")

    icon32 = brand_icon(32)
    icon16 = icon32.resize((16, 16), Image.Resampling.LANCZOS)
    icon16.save(public / "favicon.ico", format="ICO", sizes=[(16, 16), (32, 32)])

    og_image().save(public / "og-image.png", "PNG", optimize=True)

    for name in (
        "favicon.ico",
        "favicon-32x32.png",
        "apple-touch-icon.png",
        "android-chrome-192x192.png",
        "android-chrome-512x512.png",
        "og-image.png",
    ):
        path = public / name
        print(f"wrote {path} ({path.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
