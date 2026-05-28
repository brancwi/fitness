#!/usr/bin/env python3
"""Generate launcher icons for the Muscu fitness app."""

from PIL import Image, ImageDraw, ImageFilter
import os

# Config
OUTPUT_DIR = "app/src/main/res"
BG_COLOR = "#16213e"
ACCENT_COLOR = "#4CAF50"
ICON_COLOR = "#ffffff"

SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def draw_rounded_rect(draw, xy, radius, fill):
    """Draw a rectangle with rounded corners."""
    x1, y1, x2, y2 = xy
    if x2 <= x1 or y2 <= y1:
        return
    radius = min(radius, (x2 - x1) // 2, (y2 - y1) // 2)
    if radius <= 0:
        draw.rectangle(xy, fill=fill)
        return
    draw.rectangle([x1 + radius, y1, x2 - radius, y2], fill=fill)
    draw.rectangle([x1, y1 + radius, x2, y2 - radius], fill=fill)
    draw.ellipse([x1, y1, x1 + radius * 2, y1 + radius * 2], fill=fill)
    draw.ellipse([x2 - radius * 2, y1, x2, y1 + radius * 2], fill=fill)
    draw.ellipse([x1, y2 - radius * 2, x1 + radius * 2, y2], fill=fill)
    draw.ellipse([x2 - radius * 2, y2 - radius * 2, x2, y2], fill=fill)

def draw_dumbbell(draw, cx, cy, size, color):
    """Draw a modern stylized dumbbell."""
    bar_w = int(size * 0.50)
    bar_h = max(2, int(size * 0.07))
    weight_w = max(3, int(size * 0.10))
    weight_h = int(size * 0.32)
    gap = max(2, int(size * 0.05))
    corner = max(2, weight_w // 2)

    # Bar
    bx = cx - bar_w // 2
    by = cy - bar_h // 2
    draw_rounded_rect(draw, [bx, by, bx + bar_w, by + bar_h], bar_h // 2, color)

    # Left outer weight
    lx = bx - gap - weight_w
    ly = cy - weight_h // 2
    draw_rounded_rect(draw, [lx, ly, lx + weight_w, ly + weight_h], corner, color)

    # Left inner weight (slightly smaller)
    li_w = int(weight_w * 0.7)
    lix = bx + gap
    draw_rounded_rect(draw, [lix, ly + weight_h // 5, lix + li_w, ly + weight_h - weight_h // 5], li_w // 2, color)

    # Right inner weight
    rix = bx + bar_w - gap - li_w
    draw_rounded_rect(draw, [rix, ly + weight_h // 5, rix + li_w, ly + weight_h - weight_h // 5], li_w // 2, color)

    # Right outer weight
    rx = bx + bar_w + gap
    draw_rounded_rect(draw, [rx, ly, rx + weight_w, ly + weight_h], corner, color)

def generate_icon(px_size, output_path, round_mask=True, add_shadow=False):
    """Generate a single launcher icon PNG."""
    img = Image.new("RGBA", (px_size, px_size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Background circle with margin
    margin = px_size // 7
    cx = px_size // 2
    cy = px_size // 2
    r = (px_size - margin * 2) // 2

    if add_shadow and px_size >= 96:
        shadow = Image.new("RGBA", (px_size, px_size), (0, 0, 0, 0))
        sdraw = ImageDraw.Draw(shadow)
        sdraw.ellipse([margin + 4, margin + 4, px_size - margin + 4, px_size - margin + 4], fill=(0, 0, 0, 60))
        shadow = shadow.filter(ImageFilter.GaussianBlur(radius=max(2, px_size // 32)))
        img = Image.alpha_composite(img, shadow)
        draw = ImageDraw.Draw(img)

    # Main background
    draw.ellipse([margin, margin, px_size - margin, px_size - margin], fill=BG_COLOR)

    # Subtle inner gradient ring (accent)
    ring_w = max(1, px_size // 28)
    draw.ellipse([margin, margin, px_size - margin, px_size - margin], outline=ACCENT_COLOR, width=ring_w)

    # Dumbbell icon
    draw_dumbbell(draw, cx, cy, px_size * 0.45, ICON_COLOR)

    # Circular mask for round icons
    if round_mask:
        mask = Image.new("L", (px_size, px_size), 0)
        mask_draw = ImageDraw.Draw(mask)
        mask_draw.ellipse([0, 0, px_size, px_size], fill=255)
        img.putalpha(mask)

    img.save(output_path, "PNG")

def main():
    for folder, size in SIZES.items():
        path = os.path.join(OUTPUT_DIR, folder, "ic_launcher.png")
        generate_icon(size, path, round_mask=False, add_shadow=True)
        print(f"Created {path}")

    for folder, size in SIZES.items():
        path = os.path.join(OUTPUT_DIR, folder, "ic_launcher_round.png")
        generate_icon(size, path, round_mask=True, add_shadow=True)
        print(f"Created {path}")

    store_path = "app/src/main/ic_launcher-playstore.png"
    generate_icon(512, store_path, round_mask=False, add_shadow=True)
    print(f"Created {store_path}")

    print("Done.")

if __name__ == "__main__":
    main()
