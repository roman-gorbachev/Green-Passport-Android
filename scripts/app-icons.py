import math
import random
from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
RES = ROOT / 'app' / 'src' / 'main' / 'res'
STANDARD_FOREGROUND = Image.open(RES / 'mipmap-xxxhdpi' / 'ic_launcher_foreground.webp').convert('RGBA')
MASCOT = Image.open(ROOT / 'core' / 'src' / 'main' / 'res' / 'drawable-nodpi' / 'mascot.webp').convert('RGBA')
MASCOT_HEIGHT_FRACTION = 0.48
MASCOT_CENTER_FRACTION = 0.52
SHADOW_OFFSET_FRACTION = 0.012
SHADOW_BLUR_FRACTION = 0.02
SHADOW_ALPHA = 80
BASE = 432
DENSITIES = {'mdpi': 108, 'hdpi': 162, 'xhdpi': 216, 'xxhdpi': 324, 'xxxhdpi': 432}
VISIBLE_FRACTION = 72 / 108
PREVIEW_SIZE = 180
STAR_COUNT = 40
STAR_SEED = 7


def rgba(hex_color, alpha=255):
    value = int(hex_color.lstrip('#'), 16)
    return ((value >> 16) & 255, (value >> 8) & 255, value & 255, alpha)


def scaled(fraction):
    return round(BASE * fraction)


def vertical_gradient(top, bottom):
    strip = Image.new('RGBA', (1, 2))
    strip.putpixel((0, 0), rgba(top))
    strip.putpixel((0, 1), rgba(bottom))
    return strip.resize((BASE, BASE), Image.BICUBIC)


def glow(image, box, color, blur):
    layer = Image.new('RGBA', image.size, (0, 0, 0, 0))
    ImageDraw.Draw(layer).ellipse([scaled(value) for value in box], fill=color)
    image.alpha_composite(layer.filter(ImageFilter.GaussianBlur(blur)))


def solid(color):
    return Image.new('RGBA', (BASE, BASE), rgba(color))


def sunset():
    image = vertical_gradient('#FFC27A', '#E85A7A')
    glow(image, (0.22, 0.18, 0.78, 0.74), rgba('#FFF1C2', 170), 14)
    glow(image, (0.3, 0.26, 0.7, 0.66), rgba('#FFE7A0', 230), 2)
    return image


def night():
    image = vertical_gradient('#0D1A36', '#24426E')
    draw = ImageDraw.Draw(image)
    rng = random.Random(STAR_SEED)
    for _ in range(STAR_COUNT):
        x, y = rng.randint(0, BASE), rng.randint(0, BASE)
        radius = rng.choice((1, 1, 2))
        draw.ellipse((x - radius, y - radius, x + radius, y + radius), fill=rgba('#FFFFFF', rng.randint(140, 255)))
    moon = Image.new('L', (BASE, BASE), 0)
    moon_draw = ImageDraw.Draw(moon)
    moon_draw.ellipse([scaled(value) for value in (0.6, 0.2, 0.74, 0.34)], fill=255)
    moon_draw.ellipse([scaled(value) for value in (0.64, 0.18, 0.78, 0.32)], fill=0)
    image.paste(rgba('#F6EDC4'), (0, 0), moon)
    return image


def ocean():
    image = vertical_gradient('#4FD2C6', '#0B4F82')
    for offset, color in ((0.68, rgba('#7FE3D8', 90)), (0.77, rgba('#0A3F6B', 140))):
        layer = Image.new('RGBA', image.size, (0, 0, 0, 0))
        points = [
            (x, scaled(offset) + scaled(0.03) * math.sin(x / BASE * math.tau * 1.5 + offset * 10))
            for x in range(0, BASE + 4, 4)
        ]
        ImageDraw.Draw(layer).polygon(points + [(BASE, BASE), (0, BASE)], fill=color)
        image.alpha_composite(layer)
    return image


def lime():
    inner, outer = rgba('#F3FCC0'), rgba('#9DCC32')
    image = Image.new('RGBA', (BASE, BASE))
    pixels = image.load()
    half = BASE / 2
    max_distance = math.hypot(half, half)
    for y in range(BASE):
        for x in range(BASE):
            t = min(math.hypot(x - half, y - half) / max_distance, 1) ** 1.4
            pixels[x, y] = tuple(round(a + (b - a) * t) for a, b in zip(inner, outer))
    return image


SCENES = {
    'standard': lambda: solid('#D0F1E0'),
    'dark': lambda: solid('#1F6B47'),
    'sunset': sunset,
    'night': night,
    'ocean': ocean,
    'lime': lime,
}
RASTER_BACKGROUNDS = ('sunset', 'night', 'ocean', 'lime')


def mascot_foreground():
    layer = Image.new('RGBA', (BASE, BASE), (0, 0, 0, 0))
    height = scaled(MASCOT_HEIGHT_FRACTION)
    mascot = MASCOT.resize((round(MASCOT.width * height / MASCOT.height), height), Image.LANCZOS)
    left = (BASE - mascot.width) // 2
    top = scaled(MASCOT_CENTER_FRACTION) - height // 2
    shadow = Image.new('RGBA', (BASE, BASE), (0, 0, 0, 0))
    shadow.paste((0, 0, 0, SHADOW_ALPHA), (left, top + scaled(SHADOW_OFFSET_FRACTION)), mascot.split()[3])
    layer.alpha_composite(shadow.filter(ImageFilter.GaussianBlur(scaled(SHADOW_BLUR_FRACTION))))
    layer.alpha_composite(mascot, (left, top))
    return layer


def write_foreground(foreground):
    for density, size in DENSITIES.items():
        foreground.resize((size, size), Image.LANCZOS).save(
            RES / f'mipmap-{density}' / 'ic_launcher_foreground_mascot.webp', quality=92
        )


def write_backgrounds(name, background):
    for density, size in DENSITIES.items():
        folder = RES / f'mipmap-{density}'
        background.resize((size, size), Image.LANCZOS).convert('RGB').save(
            folder / f'ic_launcher_background_{name}.webp', quality=92
        )


def write_preview(name, background, foreground):
    composed = background.copy()
    composed.alpha_composite(foreground)
    margin = round(BASE * (1 - VISIBLE_FRACTION) / 2)
    visible = composed.crop((margin, margin, BASE - margin, BASE - margin))
    folder = ROOT / 'feature' / 'profile' / 'src' / 'main' / 'res' / 'drawable-nodpi'
    folder.mkdir(exist_ok=True)
    visible.resize((PREVIEW_SIZE, PREVIEW_SIZE), Image.LANCZOS).convert('RGB').save(
        folder / f'app_icon_preview_{name}.webp', quality=92
    )


def main():
    foreground = mascot_foreground()
    write_foreground(foreground)
    for name, scene in SCENES.items():
        background = scene()
        if name in RASTER_BACKGROUNDS:
            write_backgrounds(name, background)
        layer = STANDARD_FOREGROUND.resize((BASE, BASE), Image.LANCZOS) if name == 'standard' else foreground
        write_preview(name, background, layer)


if __name__ == '__main__':
    main()
