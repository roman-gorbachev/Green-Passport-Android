import math
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFilter

ROOT = Path(__file__).resolve().parent.parent
GAMES = ROOT / 'games'
MASCOT = GAMES / 'common' / 'mascot.png'
SIZE = 2048
OUTPUT = 512
MAX_BYTES = 150_000
SHADOW_OFFSET = (0, 36)
SHADOW_BLUR = 34
SHADOW_ALPHA = 70

WHITE = (255, 255, 255, 255)
CREAM = (255, 248, 231, 255)
FOREST = (31, 107, 71, 255)
DARK_FOREST = (20, 74, 49, 255)
LIME = (195, 238, 90, 255)
INK = (33, 40, 46, 255)


def rgba(hex_color, alpha=255):
    value = int(hex_color.lstrip('#'), 16)
    return ((value >> 16) & 255, (value >> 8) & 255, value & 255, alpha)


def mix(first, second, amount):
    return tuple(round(a + (b - a) * amount) for a, b in zip(first, second))


def gradient(top_left, bottom_right):
    corners = Image.new('RGBA', (2, 2))
    corners.putpixel((0, 0), top_left)
    corners.putpixel((1, 0), mix(top_left, bottom_right, 0.5))
    corners.putpixel((0, 1), mix(top_left, bottom_right, 0.5))
    corners.putpixel((1, 1), bottom_right)
    return corners.resize((SIZE, SIZE), Image.BICUBIC)


def layer():
    image = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    return image, ImageDraw.Draw(image)


def with_shadow(base, shape, offset=SHADOW_OFFSET, blur=SHADOW_BLUR, alpha=SHADOW_ALPHA):
    mask = shape.getchannel('A').filter(ImageFilter.GaussianBlur(blur))
    shadow = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    shadow.putalpha(mask.point(lambda value: value * alpha // 255))
    base.alpha_composite(shadow, offset)
    base.alpha_composite(shape)


def rotated(shape, angle, center):
    return shape.rotate(angle, resample=Image.BICUBIC, center=center)


def lens(center, length, width, angle, steps=48):
    cx, cy = center
    points = []
    for index in range(steps + 1):
        t = math.pi * index / steps
        points.append((math.cos(t) * length / 2, math.sin(t) * width / 2))
    for index in range(steps + 1):
        t = math.pi + math.pi * index / steps
        points.append((math.cos(t) * length / 2, math.sin(t) * width / 2))
    radians = math.radians(angle)
    return [
        (cx + x * math.cos(radians) - y * math.sin(radians), cy + x * math.sin(radians) + y * math.cos(radians))
        for x, y in points
    ]


def leaf(draw, center, length, angle, color=LIME):
    draw.polygon(lens(center, length, length * 0.48, angle), fill=color)
    radians = math.radians(angle)
    dx, dy = math.cos(radians) * length * 0.42, math.sin(radians) * length * 0.42
    draw.line([(center[0] - dx, center[1] - dy), (center[0] + dx, center[1] + dy)], fill=mix(color, DARK_FOREST, 0.45), width=max(6, int(length * 0.04)))


def glow(base, center, radius, color):
    halo, draw = layer()
    draw.ellipse([center[0] - radius, center[1] - radius, center[0] + radius, center[1] + radius], fill=color)
    base.alpha_composite(halo.filter(ImageFilter.GaussianBlur(radius * 0.45)))


def decorate(base, light):
    dots, draw = layer()
    for x, y, radius in [(260, 300, 150), (1820, 420, 90), (1700, 1750, 210), (360, 1720, 70)]:
        draw.ellipse([x - radius, y - radius, x + radius, y + radius], fill=(*light[:3], 40))
    base.alpha_composite(dots)


def mascot(size, angle=0):
    image = Image.open(MASCOT).convert('RGBA')
    width = size
    height = round(image.height * size / image.width)
    image = image.resize((width, height), Image.LANCZOS)
    return image.rotate(angle, resample=Image.BICUBIC, expand=True) if angle else image


def paste_with_shadow(base, image, position):
    shape, _ = layer()
    shape.alpha_composite(image, position)
    with_shadow(base, shape)


def eco_runner(base):
    hills, draw = layer()
    draw.ellipse([-500, 1350, 1300, 2600], fill=(126, 217, 87, 255))
    draw.ellipse([800, 1450, 2600, 2700], fill=(91, 190, 96, 255))
    base.alpha_composite(hills)
    ground, draw = layer()
    draw.rectangle([0, 1720, SIZE, SIZE], fill=DARK_FOREST)
    for x in range(-60, SIZE, 260):
        draw.rounded_rectangle([x, 1830, x + 150, 1858], radius=14, fill=(255, 255, 255, 80))
    base.alpha_composite(ground)
    bag, draw = layer()
    draw.ellipse([1180, 1300, 1700, 1760], fill=(70, 78, 86, 255))
    draw.polygon([(1380, 1330), (1440, 1180), (1500, 1330)], fill=(70, 78, 86, 255))
    draw.rounded_rectangle([1395, 1270, 1485, 1310], radius=18, fill=(255, 196, 0, 255))
    draw.arc([1260, 1420, 1460, 1620], start=200, end=320, fill=(110, 120, 130, 255), width=26)
    with_shadow(base, bag)
    motion, draw = layer()
    for y, length in [(760, 360), (900, 260), (1040, 320)]:
        draw.rounded_rectangle([170, y, 170 + length, y + 44], radius=22, fill=(255, 255, 255, 120))
    base.alpha_composite(motion)
    paste_with_shadow(base, mascot(980, angle=10), (430, 260))
    leaves, draw = layer()
    leaf(draw, (1560, 520), 300, -30)
    leaf(draw, (1780, 760), 230, 20)
    leaf(draw, (1640, 980), 190, -60)
    with_shadow(base, leaves, offset=(0, 20), blur=18)


def sort_conveyor(base):
    belt, draw = layer()
    draw.rounded_rectangle([140, 560, 1908, 820], radius=130, fill=(54, 64, 74, 255))
    draw.rounded_rectangle([200, 600, 1848, 780], radius=90, fill=(84, 96, 108, 255))
    for x in range(260, 1800, 170):
        draw.polygon([(x, 780), (x + 70, 780), (x + 130, 600), (x + 60, 600)], fill=(104, 118, 130, 255))
    for cx in (270, 1778):
        draw.ellipse([cx - 92, 598, cx + 92, 782], fill=(36, 42, 48, 255))
        draw.ellipse([cx - 34, 656, cx + 34, 724], fill=(150, 160, 170, 255))
    with_shadow(base, belt)
    bottle, draw = layer()
    draw.rounded_rectangle([880, 230, 1170, 610], radius=80, fill=(141, 226, 176, 255))
    draw.rounded_rectangle([965, 120, 1085, 260], radius=30, fill=(141, 226, 176, 255))
    draw.rounded_rectangle([950, 70, 1100, 140], radius=24, fill=(52, 199, 123, 255))
    draw.rounded_rectangle([910, 360, 1140, 470], radius=20, fill=WHITE)
    draw.rounded_rectangle([945, 260, 985, 560], radius=20, fill=(255, 255, 255, 120))
    with_shadow(base, rotated(bottle, -8, (1025, 400)))
    colors = ['#34C77B', '#8E7CF0', '#4DA3FF', '#FF9F43']
    bins, draw = layer()
    width, gap, top = 380, 56, 1150
    left = (SIZE - (width * 4 + gap * 3)) // 2
    for index, color in enumerate(colors):
        x = left + index * (width + gap)
        body = rgba(color)
        draw.rounded_rectangle([x + 20, top + 120, x + width - 20, 1880], radius=70, fill=body)
        draw.rounded_rectangle([x, top, x + width, top + 130], radius=60, fill=mix(body, INK, 0.25))
        draw.rounded_rectangle([x + width // 2 - 70, top - 50, x + width // 2 + 70, top + 10], radius=30, fill=mix(body, INK, 0.25))
        for stripe in (0.33, 0.66):
            sx = x + int(width * stripe)
            draw.rounded_rectangle([sx - 16, top + 260, sx + 16, 1760], radius=16, fill=(255, 255, 255, 70))
    with_shadow(base, bins)


def wave_polygon(y, amplitude, length, phase, bottom=SIZE):
    points = [(0, bottom)]
    for x in range(0, SIZE + 32, 32):
        points.append((x, y + math.sin(x / length + phase) * amplitude))
    points.append((SIZE, bottom))
    return points


def ocean_cleanup(base):
    sun, draw = layer()
    draw.ellipse([1460, 180, 1820, 540], fill=(255, 224, 120, 255))
    base.alpha_composite(sun)
    glow(base, (1640, 360), 260, (255, 224, 120, 120))
    boat, draw = layer()
    draw.polygon([(520, 760), (1360, 760), (1240, 930), (640, 930)], fill=FOREST)
    draw.rounded_rectangle([760, 560, 1060, 770], radius=40, fill=WHITE)
    draw.rounded_rectangle([810, 610, 900, 690], radius=18, fill=(43, 179, 232, 255))
    draw.rectangle([520, 740, 1360, 780], fill=LIME)
    with_shadow(base, boat)
    for index, (y, alpha) in enumerate([(900, 255), (980, 255), (1080, 255)]):
        water, draw = layer()
        draw.polygon(wave_polygon(y, 34, 120, index * 1.7), fill=mix(rgba('#2BB3E8'), rgba('#0B3A5C'), 0.25 + index * 0.25))
        base.alpha_composite(water)
    rope, draw = layer()
    draw.line([(1260, 820), (1380, 1050), (1420, 1180)], fill=CREAM, width=22, joint='curve')
    base.alpha_composite(rope)
    net, draw = layer()
    cx, cy, radius = 1300, 1520, 360
    draw.ellipse([cx - radius, cy - radius, cx + radius, cy + radius], fill=(255, 255, 255, 60))
    mesh, mesh_draw = layer()
    for offset in range(-radius * 2, radius * 2, 90):
        mesh_draw.line([(cx + offset - radius, cy - radius), (cx + offset + radius, cy + radius)], fill=(255, 255, 255, 150), width=14)
        mesh_draw.line([(cx + offset + radius, cy - radius), (cx + offset - radius, cy + radius)], fill=(255, 255, 255, 150), width=14)
    circle = Image.new('L', (SIZE, SIZE), 0)
    ImageDraw.Draw(circle).ellipse([cx - radius, cy - radius, cx + radius, cy + radius], fill=255)
    mesh.putalpha(ImageChops.multiply(mesh.getchannel('A'), circle))
    net.alpha_composite(mesh)
    draw.ellipse([cx - radius, cy - radius, cx + radius, cy + radius], outline=LIME, width=44)
    base.alpha_composite(net)
    bottle, draw = layer()
    draw.rounded_rectangle([1200, 1420, 1420, 1690], radius=60, fill=(255, 255, 255, 230))
    draw.rounded_rectangle([1270, 1340, 1350, 1440], radius=24, fill=(255, 255, 255, 230))
    draw.rounded_rectangle([1255, 1300, 1365, 1350], radius=20, fill=(255, 159, 67, 255))
    with_shadow(base, rotated(bottle, 25, (1310, 1500)), offset=(0, 20), blur=20)
    bubbles, draw = layer()
    for x, y, radius in [(420, 1400, 60), (520, 1640, 36), (330, 1780, 46), (700, 1860, 28)]:
        draw.ellipse([x - radius, y - radius, x + radius, y + radius], outline=(255, 255, 255, 170), width=14)
    base.alpha_composite(bubbles)


def fir(draw, x, bottom, height, color):
    draw.rounded_rectangle([x - height * 0.06, bottom - height * 0.16, x + height * 0.06, bottom], radius=12, fill=(122, 82, 52, 255))
    for level in range(3):
        tier_bottom = bottom - height * 0.14 - level * height * 0.24
        half = height * (0.36 - level * 0.08)
        draw.polygon([(x - half, tier_bottom), (x + half, tier_bottom), (x, tier_bottom - height * 0.42)], fill=mix(color, DARK_FOREST, level * 0.12))


def flame(draw, center, size):
    cx, cy = center
    draw.polygon(lens((cx, cy), size, size * 0.62, -90), fill=(255, 112, 67, 255))
    draw.polygon(lens((cx, cy + size * 0.12), size * 0.6, size * 0.38, -90), fill=(255, 213, 79, 255))


def drop(draw, center, size, color):
    cx, cy = center
    draw.ellipse([cx - size * 0.5, cy - size * 0.1, cx + size * 0.5, cy + size * 0.9], fill=color)
    draw.polygon([(cx - size * 0.44, cy + size * 0.2), (cx, cy - size * 0.75), (cx + size * 0.44, cy + size * 0.2)], fill=color)
    draw.ellipse([cx - size * 0.24, cy + size * 0.2, cx - size * 0.04, cy + size * 0.5], fill=(255, 255, 255, 170))


def forest_guard(base):
    ground, draw = layer()
    draw.ellipse([-300, 1560, 2350, 2600], fill=(46, 140, 94, 255))
    base.alpha_composite(ground)
    trees, draw = layer()
    fir(draw, 520, 1780, 1000, (52, 168, 83, 255))
    fir(draw, 1530, 1800, 1050, (52, 168, 83, 255))
    fir(draw, 1020, 1880, 1300, (36, 140, 70, 255))
    with_shadow(base, trees)
    fire, draw = layer()
    flame(draw, (1560, 1260), 330)
    with_shadow(base, fire, offset=(0, 18), blur=18)
    glow(base, (1560, 1260), 240, (255, 160, 60, 110))
    water, draw = layer()
    drop(draw, (1620, 520), 300, (77, 163, 255, 255))
    drop(draw, (1800, 820), 150, (159, 211, 255, 255))
    with_shadow(base, water)


def sprout(draw, center, size):
    cx, cy = center
    draw.line([(cx, cy + size * 0.4), (cx, cy - size * 0.1)], fill=DARK_FOREST, width=int(size * 0.07))
    leaf(draw, (cx - size * 0.18, cy - size * 0.12), size * 0.42, -30, (126, 217, 87, 255))
    leaf(draw, (cx + size * 0.2, cy - size * 0.2), size * 0.5, 30, (126, 217, 87, 255))


def eco_merge(base):
    tiles, draw = layer()
    size, gap = 760, 80
    left = (SIZE - size * 2 - gap) // 2
    top = (SIZE - size * 2 - gap) // 2
    shades = [(255, 255, 255, 235), (240, 252, 230, 235), (220, 245, 205, 235), (195, 235, 175, 235)]
    centers = []
    for index in range(4):
        x = left + (index % 2) * (size + gap)
        y = top + (index // 2) * (size + gap)
        draw.rounded_rectangle([x, y, x + size, y + size], radius=150, fill=shades[index])
        centers.append((x + size // 2, y + size // 2))
    with_shadow(base, tiles)
    art, draw = layer()
    (sx, sy), sprout_center, (bx, by), (tx, ty) = centers
    draw.ellipse([sx - 150, sy - 110, sx + 150, sy + 130], fill=(150, 96, 56, 255))
    draw.ellipse([sx - 80, sy - 70, sx + 10, sy - 10], fill=(196, 140, 90, 255))
    sprout(draw, sprout_center, 520)
    for dx, dy, radius in [(-130, 60, 170), (130, 60, 170), (0, -80, 200)]:
        draw.ellipse([bx + dx - radius, by + dy - radius, bx + dx + radius, by + dy + radius], fill=(52, 168, 83, 255))
    draw.rounded_rectangle([tx - 45, ty + 40, tx + 45, ty + 300], radius=20, fill=(122, 82, 52, 255))
    draw.ellipse([tx - 250, ty - 300, tx + 250, ty + 160], fill=FOREST)
    draw.ellipse([tx - 150, ty - 240, tx + 30, ty - 80], fill=(52, 168, 83, 255))
    base.alpha_composite(art)


def question_mark(draw, center, size, color):
    cx, cy = center
    width = int(size * 0.16)
    radius = size * 0.3
    top = cy - size * 0.32
    draw.arc([cx - radius, top - radius, cx + radius, top + radius], start=180, end=405, fill=color, width=width)
    hook_end = (cx + radius * math.cos(math.radians(45)) - width * 0.5, top + radius * math.sin(math.radians(45)) - width * 0.5)
    draw.line([hook_end, (cx, cy + size * 0.06), (cx, cy + size * 0.18)], fill=color, width=width, joint='curve')
    draw.ellipse([cx - width * 0.62, cy + size * 0.3, cx + width * 0.62, cy + size * 0.3 + width * 1.24], fill=color)


def quiz_rush(base):
    ring, draw = layer()
    cx, cy, radius = 1024, 780, 560
    draw.ellipse([cx - radius, cy - radius, cx + radius, cy + radius], outline=(255, 255, 255, 90), width=90)
    draw.arc([cx - radius, cy - radius, cx + radius, cy + radius], start=-90, end=170, fill=LIME, width=90)
    base.alpha_composite(ring)
    bubble, draw = layer()
    draw.rounded_rectangle([cx - 380, cy - 360, cx + 380, cy + 300], radius=200, fill=WHITE)
    draw.polygon([(cx - 120, cy + 260), (cx - 260, cy + 460), (cx + 40, cy + 280)], fill=WHITE)
    question_mark(draw, (cx, cy + 10), 540, (232, 89, 12, 255))
    with_shadow(base, bubble)
    figure = mascot(1000)
    paste_with_shadow(base, figure, (1100, 1300))


def light_switch(base):
    for x, y in [(240, 260), (420, 520), (1880, 300)]:
        stars, draw = layer()
        draw.ellipse([x - 14, y - 14, x + 14, y + 14], fill=(255, 255, 255, 200))
        base.alpha_composite(stars)
    house, draw = layer()
    draw.polygon([(260, 900), (1024, 300), (1788, 900)], fill=(181, 82, 59, 255))
    draw.rounded_rectangle([380, 860, 1668, 1860], radius=60, fill=CREAM)
    draw.rectangle([300, 1820, 1748, 1900], fill=(110, 120, 110, 255))
    windows = [(500, 980), (1080, 980), (500, 1400), (1080, 1400)]
    for index, (x, y) in enumerate(windows):
        lit = index == 1
        draw.rounded_rectangle([x, y, x + 470, y + 340], radius=40, fill=(255, 226, 120, 255) if lit else (44, 56, 84, 255))
        draw.rectangle([x + 225, y, x + 245, y + 340], fill=CREAM)
        draw.rectangle([x, y + 160, x + 470, y + 180], fill=CREAM)
    with_shadow(base, house)
    glow(base, (1315, 1150), 330, (255, 226, 120, 140))
    bulb, draw = layer()
    cx, cy = 1620, 420
    draw.ellipse([cx - 190, cy - 230, cx + 190, cy + 150], fill=(255, 241, 170, 255))
    draw.rounded_rectangle([cx - 100, cy + 110, cx + 100, cy + 270], radius=30, fill=(140, 150, 160, 255))
    draw.line([(cx - 60, cy - 10), (cx - 20, cy + 90), (cx + 20, cy - 10), (cx + 60, cy + 90)], fill=(240, 143, 51, 255), width=24, joint='curve')
    for angle in range(-160, 1, 40):
        radians = math.radians(angle)
        start = (cx + math.cos(radians) * 270, cy - 40 + math.sin(radians) * 270)
        end = (cx + math.cos(radians) * 380, cy - 40 + math.sin(radians) * 380)
        draw.line([start, end], fill=WHITE, width=36)
    with_shadow(base, bulb)


def flower(draw, center, petal, petals, petal_color, core_color):
    cx, cy = center
    for index in range(petals):
        radians = 2 * math.pi * index / petals
        px, py = cx + math.cos(radians) * petal * 0.9, cy + math.sin(radians) * petal * 0.9
        draw.ellipse([px - petal * 0.62, py - petal * 0.62, px + petal * 0.62, py + petal * 0.62], fill=petal_color)
    draw.ellipse([cx - petal * 0.6, cy - petal * 0.6, cx + petal * 0.6, cy + petal * 0.6], fill=core_color)


def bee(draw, center, size):
    cx, cy = center
    draw.ellipse([cx - size * 0.15, cy - size * 0.62, cx + size * 0.42, cy - size * 0.05], fill=(255, 255, 255, 210))
    draw.ellipse([cx - size * 0.5, cy - size * 0.55, cx + size * 0.02, cy - size * 0.02], fill=(255, 255, 255, 170))
    body, mask = layer()
    mask.ellipse([cx - size * 0.62, cy - size * 0.36, cx + size * 0.62, cy + size * 0.36], fill=(255, 200, 40, 255))
    for offset in (-0.18, 0.12, 0.42):
        mask.rectangle([cx + size * offset - size * 0.07, cy - size * 0.4, cx + size * offset + size * 0.07, cy + size * 0.4], fill=INK)
    clip = Image.new('L', (SIZE, SIZE), 0)
    ImageDraw.Draw(clip).ellipse([cx - size * 0.62, cy - size * 0.36, cx + size * 0.62, cy + size * 0.36], fill=255)
    body.putalpha(ImageChops.multiply(body.getchannel('A'), clip))
    return body


def bee_garden(base):
    meadow, draw = layer()
    draw.ellipse([-400, 1500, 2450, 2700], fill=(255, 214, 102, 255))
    base.alpha_composite(meadow)
    flowers, draw = layer()
    for x, bottom, top in [(560, 2048, 1220), (1500, 2048, 1420)]:
        draw.line([(x, bottom), (x, top)], fill=FOREST, width=48)
        leaf(draw, (x + 110, top + 300), 260, -30, (126, 217, 87, 255))
    flower(draw, (560, 1160), 210, 6, (255, 143, 177, 255), (255, 209, 64, 255))
    flower(draw, (1500, 1360), 170, 8, WHITE, (255, 183, 3, 255))
    with_shadow(base, flowers)
    path, draw = layer()
    points = [(700 + t * 9, 950 - math.sin(t / 22) * 260 - t * 2.4) for t in range(0, 110)]
    for index in range(0, len(points) - 3, 6):
        draw.line([points[index], points[index + 3]], fill=(255, 255, 255, 200), width=26)
    base.alpha_composite(path)
    insect, draw = layer()
    cx, cy, size = 1520, 560, 520
    draw.line([(cx - size * 0.62, cy - size * 0.12), (cx - size * 0.78, cy - size * 0.42)], fill=INK, width=18)
    draw.line([(cx - size * 0.56, cy - size * 0.14), (cx - size * 0.6, cy - size * 0.46)], fill=INK, width=18)
    draw.ellipse([cx - size * 0.86, cy - size * 0.22, cx - size * 0.46, cy + size * 0.18], fill=INK)
    insect.alpha_composite(bee(draw, (cx, cy), size))
    draw.ellipse([cx - size * 0.76, cy - size * 0.1, cx - size * 0.62, cy + size * 0.04], fill=WHITE)
    draw.ellipse([cx - size * 0.73, cy - size * 0.07, cx - size * 0.66, cy], fill=INK)
    with_shadow(base, rotated(insect, -12, (1520, 560)))


def bicycle(draw, center, size, color):
    cx, cy = center
    wheel = size * 0.36
    back, front = (cx - size * 0.52, cy + size * 0.2), (cx + size * 0.52, cy + size * 0.2)
    for wx, wy in (back, front):
        draw.ellipse([wx - wheel, wy - wheel, wx + wheel, wy + wheel], outline=INK, width=int(size * 0.07))
        draw.ellipse([wx - size * 0.05, wy - size * 0.05, wx + size * 0.05, wy + size * 0.05], fill=INK)
    pedal = (cx - size * 0.05, cy + size * 0.2)
    seat = (cx - size * 0.2, cy - size * 0.3)
    bar = (cx + size * 0.36, cy - size * 0.38)
    width = int(size * 0.07)
    draw.line([back, pedal, (cx + size * 0.3, cy - size * 0.2), back], fill=color, width=width, joint='curve')
    draw.line([pedal, seat], fill=color, width=width)
    draw.line([(cx + size * 0.3, cy - size * 0.2), front], fill=color, width=width)
    draw.line([(cx + size * 0.3, cy - size * 0.2), bar], fill=color, width=width)
    draw.rounded_rectangle([seat[0] - size * 0.13, seat[1] - size * 0.06, seat[0] + size * 0.1, seat[1] + size * 0.03], radius=20, fill=INK)
    draw.line([(bar[0] - size * 0.08, bar[1]), (bar[0] + size * 0.1, bar[1] - size * 0.04)], fill=INK, width=width)


def bike_lane(base):
    road, draw = layer()
    draw.polygon([(820, 0), (1228, 0), (1900, SIZE), (148, SIZE)], fill=(60, 64, 72, 255))
    for index in range(6):
        top = index * 380 - 60
        width_top = 18 + top * 0.025
        draw.polygon([(1024 - width_top, top), (1024 + width_top, top), (1024 + width_top * 1.4, top + 200), (1024 - width_top * 1.4, top + 200)], fill=(255, 255, 255, 210))
    draw.polygon([(800, 0), (830, 0), (170, SIZE), (100, SIZE)], fill=LIME)
    draw.polygon([(1218, 0), (1248, 0), (1948, SIZE), (1878, SIZE)], fill=LIME)
    base.alpha_composite(road)
    bike, draw = layer()
    bicycle(draw, (1024, 1380), 900, LIME)
    with_shadow(base, bike)
    leaves, draw = layer()
    leaf(draw, (420, 520), 240, -20, (126, 217, 87, 255))
    leaf(draw, (1650, 700), 200, 40, (126, 217, 87, 255))
    leaf(draw, (1500, 330), 160, -60, (126, 217, 87, 255))
    with_shadow(base, leaves, offset=(0, 18), blur=16)


def card(face, size, back):
    width, height = size
    image, draw = layer()
    left, top = (SIZE - width) // 2, (SIZE - height) // 2
    if back:
        draw.rounded_rectangle([left, top, left + width, top + height], radius=110, fill=FOREST)
        draw.rounded_rectangle([left + 50, top + 50, left + width - 50, top + height - 50], radius=80, outline=(255, 255, 255, 90), width=20)
        leaf(draw, (SIZE // 2, SIZE // 2), width * 0.55, -40, LIME)
    else:
        draw.rounded_rectangle([left, top, left + width, top + height], radius=110, fill=WHITE)
        face(draw, (SIZE // 2, SIZE // 2), width)
    return image


def sun_face(draw, center, width):
    cx, cy = center
    radius = width * 0.2
    for angle in range(0, 360, 45):
        radians = math.radians(angle)
        draw.line([(cx + math.cos(radians) * radius * 1.4, cy + math.sin(radians) * radius * 1.4), (cx + math.cos(radians) * radius * 2, cy + math.sin(radians) * radius * 2)], fill=(255, 183, 3, 255), width=int(width * 0.05))
    draw.ellipse([cx - radius, cy - radius, cx + radius, cy + radius], fill=(255, 196, 0, 255))


def drop_face(draw, center, width):
    drop(draw, (center[0], center[1] - width * 0.12), width * 0.38, (77, 163, 255, 255))


def eco_memory(base):
    size = (760, 1040)
    for face, back, angle, offset in [(None, True, 16, (-420, 80)), (drop_face, False, 0, (0, -40)), (sun_face, False, -16, (420, 80))]:
        image = card(face, size, back)
        image = rotated(image, angle, (SIZE // 2, SIZE // 2))
        shifted = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
        shifted.alpha_composite(image, (0, 0))
        shifted = ImageChops.offset(shifted, offset[0], offset[1])
        with_shadow(base, shifted)


SCENES = {
    'eco_runner': (eco_runner, '#34C77B', '#1F6B47'),
    'sort_conveyor': (sort_conveyor, '#4DA3FF', '#2E6FD1'),
    'ocean_cleanup': (ocean_cleanup, '#2BB3E8', '#145F8F'),
    'forest_guard': (forest_guard, '#7ED957', '#2E8C5E'),
    'eco_merge': (eco_merge, '#C3EE5A', '#4E9F3D'),
    'quiz_rush': (quiz_rush, '#FF9F43', '#E8590C'),
    'light_switch': (light_switch, '#FFD54F', '#F08F33'),
    'bee_garden': (bee_garden, '#FFB703', '#FB8500'),
    'bike_lane': (bike_lane, '#8E7CF0', '#5B4BC4'),
    'eco_memory': (eco_memory, '#FF6B8A', '#C9184A'),
}


def save(image, game_id):
    target = GAMES / game_id / 'icon.png'
    small = image.convert('RGB').resize((OUTPUT, OUTPUT), Image.LANCZOS)
    small.save(target, optimize=True)
    if target.stat().st_size > MAX_BYTES:
        small.quantize(colors=256, method=Image.Quantize.MEDIANCUT).save(target, optimize=True)
    print(f'{game_id}: {target.stat().st_size // 1024} KB')


def main():
    for game_id, (scene, first, second) in SCENES.items():
        base = gradient(rgba(first), rgba(second))
        decorate(base, rgba('#FFFFFF'))
        scene(base)
        save(base, game_id)


if __name__ == '__main__':
    main()
