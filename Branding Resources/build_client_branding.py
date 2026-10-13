"""Builds the RuneRealm login-screen assets packed into the client cache.

The client draws the login background from `title.jpg` (archive 10) as the left half of the
765x503 screen and a mirrored copy of it as the right half, then draws the `logo` sprite
(archive 8) centred at x=382, y=18. To show a full-width, non-mirrored image:
- `title.jpg` holds the left half of the image.
- `logo` holds the right half (x 382..764, y 18..502) and is offset so it lands at x=382.

Usage: python3 "Branding Resources/build_client_branding.py"
"""

import io
import struct
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "Branding Resources" / "RuneRealm Background.png"
OUT = ROOT / "server/app/src/main/resources/org/runerealm/server/app/branding"

WIDTH, HEIGHT = 765, 503
HALF = 383
LOGO_Y = 18

# The client draws its login-screen fire over the original title's braziers, whose stems sit at
# x=44 and x=720 with bowl rims at y=263. In the source image fitted to 765 wide, the braziers sit
# at x=64 and x=700 with rims at y=248, so scale it up and shift it left to line them up.
FIRE_SCALE = (720 - 44) / (700 - 64)
FIRE_OFFSET_X = round(64 * FIRE_SCALE) - 44
FIRE_OFFSET_Y = round(248 * FIRE_SCALE) - 263


def main() -> None:
    image = Image.open(SOURCE).convert("RGB")
    scaled_width = round(WIDTH * FIRE_SCALE)
    scaled_height = round(image.height * scaled_width / image.width)
    image = image.resize((scaled_width, scaled_height), Image.LANCZOS)
    image = image.crop(
        (FIRE_OFFSET_X, FIRE_OFFSET_Y, FIRE_OFFSET_X + WIDTH, FIRE_OFFSET_Y + HEIGHT)
    )

    left = image.crop((0, 0, HALF, HEIGHT))
    jpg = io.BytesIO()
    left.save(jpg, "JPEG", quality=92, optimize=True, progressive=False)
    (OUT / "title.jpg").write_bytes(jpg.getvalue())

    right = image.crop((WIDTH - HALF, LOGO_Y, WIDTH, HEIGHT))
    (OUT / "logo.dat").write_bytes(encode_sprite(right))


def encode_sprite(image: Image.Image) -> bytes:
    """Encodes [image] as a single-frame, palette-indexed cache sprite."""
    quantized = image.quantize(colors=255, method=Image.Quantize.MEDIANCUT,
                               dither=Image.Dither.FLOYDSTEINBERG)
    raw_palette = quantized.getpalette()[: 255 * 3]
    colours = len(raw_palette) // 3
    width, height = quantized.size
    # Palette index 0 is transparent in the client, so shift every pixel up by one.
    pixels = bytes(p + 1 for p in quantized.tobytes())

    out = bytearray()
    out.append(0)  # Row-major pixels, no alpha.
    out += pixels
    for i in range(colours):
        r, g, b = raw_palette[i * 3: i * 3 + 3]
        out += bytes((r, g, b))
    # Offsets centre the frame on x=382 whether the client centres on the frame or max width.
    out += struct.pack(">HHB", width, height, colours)
    out += struct.pack(">H", width // 2)  # x offset
    out += struct.pack(">H", 0)  # y offset
    out += struct.pack(">H", width)
    out += struct.pack(">H", height)
    out += struct.pack(">H", 1)  # Frame count.
    return bytes(out)


if __name__ == "__main__":
    main()
