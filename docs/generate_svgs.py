#!/usr/bin/env python3
"""Génère des SVG professionnels sans silhouette humaine pour les exercices."""

import os

TEMPLATE = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 320" width="400" height="320">
  <defs>
    <linearGradient id="bgGrad" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#1A1A2E"/>
      <stop offset="100%" stop-color="#16213E"/>
    </linearGradient>
    <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="2.5" result="blur"/>
      <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
    </filter>
    <marker id="arrow" markerWidth="10" markerHeight="10" refX="9" refY="3" orient="auto">
      <path d="M0,0 L0,6 L9,3 z" fill="{accent}"/>
    </marker>
  </defs>

  <!-- Fond -->
  <rect width="400" height="320" fill="url(#bgGrad)"/>

  <!-- Titre -->
  <text x="200" y="28" text-anchor="middle" fill="#E0E0E0" font-size="15" font-weight="bold" font-family="sans-serif">{title}</text>
  <text x="200" y="45" text-anchor="middle" fill="#888" font-size="11" font-family="sans-serif">{subtitle}</text>

  <!-- Zone centrale décorative -->
  <circle cx="200" cy="155" r="75" fill="none" stroke="#2A2A4A" stroke-width="1.5"/>
  <circle cx="200" cy="155" r="60" fill="none" stroke="#2A2A4A" stroke-width="1" stroke-dasharray="4 4"/>

  {center_content}

  <!-- Muscles sollicités -->
  <text x="25" y="275" fill="#FF6B35" font-size="10" font-weight="bold" font-family="sans-serif">▬ MUSCLES</text>
  {muscles_text}

  <!-- Vigilance -->
  <rect x="195" y="258" width="185" height="52" rx="8" fill="#2A1A1A" stroke="#FF6B35" stroke-width="1.2"/>
  <text x="208" y="272" fill="#FF6B35" font-size="10" font-weight="bold" font-family="sans-serif">⚠ VIGILANCE</text>
  {vigilance_text}
</svg>'''


def dumbbell(x, y, w=40, h=10, color="#AAA", handle_color="#888"):
    """Dessine une haltère horizontale centrée en (x,y)."""
    hw = w / 2
    hh = h / 2
    return f'''
  <rect x="{x-hw}" y="{y-hh}" width="{w}" height="{h}" rx="{h/2}" fill="{handle_color}"/>
  <rect x="{x-hw-6}" y="{y-hh-4}" width="{8}" height="{h+8}" rx="2" fill="{color}"/>
  <rect x="{x+hw-2}" y="{y-hh-4}" width="{8}" height="{h+8}" rx="2" fill="{color}"/>'''


def vertical_dumbbell(x, y, h=50, w=10, color="#AAA", handle_color="#888"):
    """Dessine une haltère verticale centrée en (x,y)."""
    hh = h / 2
    hw = w / 2
    return f'''
  <rect x="{x-hw}" y="{y-hh}" width="{w}" height="{h}" rx="{w/2}" fill="{handle_color}"/>
  <rect x="{x-hw-4}" y="{y-hh-6}" width="{w+8}" height="{10}" rx="2" fill="{color}"/>
  <rect x="{x-hw-4}" y="{y+hh-4}" width="{w+8}" height="{10}" rx="2" fill="{color}"/>'''


def arrow_line(x1, y1, x2, y2, color="#FF6B35", width="2.5", opacity="1"):
    return f'  <line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{color}" stroke-width="{width}" marker-end="url(#arrow)" opacity="{opacity}"/>'


def muscle_line(y, color, text):
    return f'  <text x="25" y="{y}" fill="{color}" font-size="9" font-family="sans-serif">▬ {text}</text>'


def vigilance_line(y, text):
    return f'  <text x="208" y="{y}" fill="#CCC" font-size="9" font-family="sans-serif">{text}</text>'


EXERCISES = [
    {
        "file": "bench_press_halteres",
        "title": "BENCH PRESS HALTÈRES",
        "subtitle": "3 × 8–10 reps — Charge Modérée",
        "center": lambda: (
            dumbbell(145, 140, 50, 10, "#FF6B35", "#666") +
            dumbbell(255, 140, 50, 10, "#FF6B35", "#666") +
            vertical_dumbbell(145, 170, 30, 8, "#FF6B35", "#666") +
            vertical_dumbbell(255, 170, 30, 8, "#FF6B35", "#666") +
            arrow_line(145, 160, 145, 130, "#FF6B35") +
            arrow_line(255, 160, 255, 130, "#FF6B35") +
            arrow_line(145, 125, 145, 155, "#FF6B35", opacity="0.4") +
            arrow_line(255, 125, 255, 155, "#FF6B35", opacity="0.4") +
            '  <rect x="100" y="210" width="200" height="8" rx="3" fill="#4A4A6A"/>' +
            '  <rect x="120" y="218" width="8" height="25" fill="#3A3A5A"/>' +
            '  <rect x="272" y="218" width="8" height="25" fill="#3A3A5A"/>'
        ),
        "muscles": [(290, "Pectoraux"), (305, "Deltoïdes ant."), (320, "Triceps")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Ne creuse pas le dos"), (290, "Colonne soutenue sur le banc")]
    },
    {
        "file": "bench_press_prise_serree",
        "title": "BENCH PRESS PRISE SERRÉE",
        "subtitle": "3 × 8–10 reps — Charge Modérée",
        "center": lambda: (
            dumbbell(180, 140, 35, 10, "#FF6B35", "#666") +
            dumbbell(220, 140, 35, 10, "#FF6B35", "#666") +
            vertical_dumbbell(180, 170, 30, 8, "#FF6B35", "#666") +
            vertical_dumbbell(220, 170, 30, 8, "#FF6B35", "#666") +
            arrow_line(180, 160, 180, 130, "#FF6B35") +
            arrow_line(220, 160, 220, 130, "#FF6B35") +
            '  <rect x="100" y="210" width="200" height="8" rx="3" fill="#4A4A6A"/>' +
            '  <rect x="120" y="218" width="8" height="25" fill="#3A3A5A"/>' +
            '  <rect x="272" y="218" width="8" height="25" fill="#3A3A5A"/>'
        ),
        "muscles": [(290, "Pectoraux méd."), (305, "Triceps"), (320, "Deltoïdes ant.")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Ne creuse pas le dos"), (290, "Concentration sur les triceps")]
    },
    {
        "file": "rowing_1_bras",
        "title": "ROWING 1 BRAS",
        "subtitle": "3 × 10 reps — Charge Modérée",
        "center": lambda: (
            '  <rect x="140" y="100" width="120" height="8" rx="3" fill="#4A4A6A"/>' +
            '  <rect x="155" y="108" width="6" height="20" fill="#3A3A5A"/>' +
            '  <rect x="239" y="108" width="6" height="20" fill="#3A3A5A"/>' +
            dumbbell(260, 155, 45, 10, "#42A5F5", "#666") +
            arrow_line(260, 170, 260, 140, "#42A5F5") +
            '  <circle cx="200" cy="155" r="4" fill="#42A5F5" filter="url(#glow)"/>' +
            '  <text x="200" y="175" text-anchor="middle" fill="#42A5F5" font-size="9" font-family="sans-serif">COUDE RENTRÉ</text>'
        ),
        "muscles": [(290, "Dorsaux"), (305, "Rhomboïdes"), (320, "Biceps")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Alterne les bras"), (290, "Garde le dos droit")]
    },
    {
        "file": "rowing_buste_pencho_2_bras",
        "title": "ROWING BUSTE PENCHÉ 2 BRAS",
        "subtitle": "3 × 10 reps — Charge Légère",
        "center": lambda: (
            dumbbell(170, 150, 40, 10, "#42A5F5", "#666") +
            dumbbell(230, 150, 40, 10, "#42A5F5", "#666") +
            arrow_line(170, 165, 170, 135, "#42A5F5") +
            arrow_line(230, 165, 230, 135, "#42A5F5") +
            '  <line x1="140" y1="190" x2="260" y2="190" stroke="#4A4A6A" stroke-width="2" stroke-dasharray="4 2"/>' +
            '  <text x="200" y="205" text-anchor="middle" fill="#888" font-size="9" font-family="sans-serif">BUSTE PENCHÉ 45°</text>'
        ),
        "muscles": [(290, "Dorsaux"), (305, "Rhomboïdes"), (320, "Biceps")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Buste bien penché"), (290, "Dos droit, charge légère")]
    },
    {
        "file": "curl_halteres",
        "title": "CURL HALTÈRES",
        "subtitle": "3 × 12 reps — Charge Légère",
        "center": lambda: (
            dumbbell(170, 175, 40, 10, "#66BB6A", "#666") +
            dumbbell(230, 175, 40, 10, "#66BB6A", "#666") +
            '  <path d="M 170 175 Q 170 140 170 130" stroke="#66BB6A" stroke-width="2" fill="none" marker-end="url(#arrow)"/>' +
            '  <path d="M 230 175 Q 230 140 230 130" stroke="#66BB6A" stroke-width="2" fill="none" marker-end="url(#arrow)"/>' +
            '  <circle cx="170" cy="130" r="4" fill="#66BB6A" filter="url(#glow)"/>' +
            '  <circle cx="230" cy="130" r="4" fill="#66BB6A" filter="url(#glow)"/>'
        ),
        "muscles": [(290, "Biceps brachial"), (305, "Brachial ant.")],
        "muscles_colors": ["#66BB6A", "#42A5F5"],
        "vigilance": [(288, "Ne bascule pas en arrière"), (290, "Contrôle la descente")]
    },
    {
        "file": "curl_marteau",
        "title": "CURL MARTEAU",
        "subtitle": "3 × 10 reps — Charge Légère",
        "center": lambda: (
            dumbbell(170, 175, 40, 10, "#66BB6A", "#666") +
            dumbbell(230, 175, 40, 10, "#66BB6A", "#666") +
            '  <text x="200" y="130" text-anchor="middle" fill="#66BB6A" font-size="10" font-family="sans-serif">POSITION MARTEAU</text>' +
            '  <line x1="170" y1="140" x2="170" y2="155" stroke="#66BB6A" stroke-width="2" marker-end="url(#arrow)"/>' +
            '  <line x1="230" y1="140" x2="230" y2="155" stroke="#66BB6A" stroke-width="2" marker-end="url(#arrow)"/>' +
            '  <rect x="160" y="115" width="80" height="18" rx="4" fill="none" stroke="#66BB6A" stroke-width="1" stroke-dasharray="3 2"/>'
        ),
        "muscles": [(290, "Biceps"), (305, "Brachial ant."), (320, "Brachioradial")],
        "muscles_colors": ["#66BB6A", "#42A5F5", "#FF6B35"],
        "vigilance": [(288, "Coudes collés au corps"), (290, "Contrôle la descente")]
    },
    {
        "file": "extension_triceps",
        "title": "EXTENSION TRICEPS",
        "subtitle": "3 × 12 reps — Charge Légère",
        "center": lambda: (
            dumbbell(200, 140, 50, 10, "#42A5F5", "#666") +
            arrow_line(200, 155, 200, 125, "#42A5F5") +
            arrow_line(200, 120, 200, 150, "#42A5F5", opacity="0.4") +
            '  <circle cx="200" cy="125" r="4" fill="#42A5F5" filter="url(#glow)"/>' +
            '  <text x="200" y="180" text-anchor="middle" fill="#888" font-size="9" font-family="sans-serif">COUCHÉ / DEBOUT</text>'
        ),
        "muscles": [(290, "Triceps brachial")],
        "muscles_colors": ["#42A5F5"],
        "vigilance": [(288, "Ne laisse pas les coudes s'écarter"), (290, "Mouvement strict")]
    },
    {
        "file": "extension_triceps_corde",
        "title": "EXTENSION TRICEPS CORDE",
        "subtitle": "3 × 12 reps — Charge Légère",
        "center": lambda: (
            '  <line x1="200" y1="80" x2="200" y2="140" stroke="#666" stroke-width="3"/>' +
            '  <circle cx="200" cy="75" r="8" fill="#888"/>' +
            '  <line x1="190" y1="140" x2="190" y2="170" stroke="#D4C4B0" stroke-width="3"/>' +
            '  <line x1="200" y1="140" x2="200" y2="170" stroke="#D4C4B0" stroke-width="3"/>' +
            '  <line x1="210" y1="140" x2="210" y2="170" stroke="#D4C4B0" stroke-width="3"/>' +
            '  <circle cx="185" cy="175" r="4" fill="#42A5F5"/>' +
            '  <circle cx="200" cy="175" r="4" fill="#42A5F5"/>' +
            '  <circle cx="215" cy="175" r="4" fill="#42A5F5"/>' +
            arrow_line(200, 185, 200, 155, "#42A5F5") +
            '  <text x="200" y="205" text-anchor="middle" fill="#888" font-size="9" font-family="sans-serif">ÉCARTER À LA FIN</text>'
        ),
        "muscles": [(290, "Triceps (3 chefs)")],
        "muscles_colors": ["#42A5F5"],
        "vigilance": [(288, "Ne laisse pas les coudes s'écarter"), (290, "Mouvement strict et contrôlé")]
    },
    {
        "file": "developpe_militaire_assis",
        "title": "DÉVELOPPÉ MILITAIRE ASSIS",
        "subtitle": "3 × 8–10 reps — Charge Modérée",
        "center": lambda: (
            dumbbell(170, 130, 40, 10, "#FF6B35", "#666") +
            dumbbell(230, 130, 40, 10, "#FF6B35", "#666") +
            arrow_line(170, 145, 170, 115, "#FF6B35") +
            arrow_line(230, 145, 230, 115, "#FF6B35") +
            arrow_line(170, 110, 170, 140, "#FF6B35", opacity="0.4") +
            arrow_line(230, 110, 230, 140, "#FF6B35", opacity="0.4") +
            '  <rect x="160" y="185" width="80" height="8" rx="3" fill="#4A4A6A"/>' +
            '  <rect x="175" y="193" width="6" height="20" fill="#3A3A5A"/>' +
            '  <rect x="219" y="193" width="6" height="20" fill="#3A3A5A"/>'
        ),
        "muscles": [(290, "Deltoïdes ant. &amp; lat."), (305, "Triceps"), (320, "Trapèzes")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Assis obligatoire"), (290, "Pas de charge lourde debout")]
    },
    {
        "file": "fentes_alternees",
        "title": "FENTES ALTERNÉES",
        "subtitle": "3 × 10 reps — Charge Légère",
        "center": lambda: (
            dumbbell(160, 130, 35, 10, "#FF6B35", "#666") +
            dumbbell(240, 130, 35, 10, "#FF6B35", "#666") +
            '  <circle cx="180" cy="170" r="5" fill="#FF6B35" filter="url(#glow)"/>' +
            '  <circle cx="220" cy="170" r="5" fill="#FF6B35" filter="url(#glow)"/>' +
            '  <line x1="180" y1="170" x2="220" y2="170" stroke="#FF6B35" stroke-width="2" stroke-dasharray="3 2"/>' +
            arrow_line(200, 185, 200, 155, "#FF6B35") +
            '  <text x="200" y="205" text-anchor="middle" fill="#888" font-size="9" font-family="sans-serif">GRAND PAS EN AVANT</text>'
        ),
        "muscles": [(290, "Quadriceps"), (305, "Fessiers"), (320, "Stabilisateurs")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Haltères légers"), (290, "Genou avant ne dépasse pas la pointe")]
    },
    {
        "file": "elevations_laterales",
        "title": "ÉLÉVATIONS LATÉRALES",
        "subtitle": "3 × 12 reps — Charge Légère",
        "center": lambda: (
            dumbbell(160, 170, 35, 10, "#42A5F5", "#666") +
            dumbbell(240, 170, 35, 10, "#42A5F5", "#666") +
            '  <line x1="160" y1="170" x2="160" y2="140" stroke="#42A5F5" stroke-width="2" marker-end="url(#arrow)"/>' +
            '  <line x1="240" y1="170" x2="240" y2="140" stroke="#42A5F5" stroke-width="2" marker-end="url(#arrow)"/>' +
            '  <line x1="120" y1="140" x2="280" y2="140" stroke="#42A5F5" stroke-width="1" stroke-dasharray="4 2" opacity="0.6"/>' +
            '  <text x="200" y="130" text-anchor="middle" fill="#42A5F5" font-size="9" font-family="sans-serif">HORIZONTAL</text>'
        ),
        "muscles": [(290, "Deltoïdes latéraux")],
        "muscles_colors": ["#42A5F5"],
        "vigilance": [(288, "Ne balance pas le buste"), (290, "Charge légère, contrôle total")]
    },
    {
        "file": "gainage_ventral",
        "title": "GAINAGE VENTRAL",
        "subtitle": "3 × 30–45 sec — Poids du corps",
        "center": lambda: (
            '  <line x1="80" y1="180" x2="320" y2="180" stroke="#FF6B35" stroke-width="6" stroke-linecap="round"/>' +
            '  <circle cx="80" cy="180" r="8" fill="#FF6B35" filter="url(#glow)"/>' +
            '  <circle cx="320" cy="180" r="8" fill="#FF6B35" filter="url(#glow)"/>' +
            '  <line x1="80" y1="180" x2="110" y2="180" stroke="#42A5F5" stroke-width="4" stroke-linecap="round"/>' +
            '  <line x1="290" y1="180" x2="320" y2="180" stroke="#42A5F5" stroke-width="4" stroke-linecap="round"/>' +
            '  <text x="200" y="165" text-anchor="middle" fill="#FF6B35" font-size="11" font-family="sans-serif">CORE ENGAGÉ</text>' +
            '  <text x="200" y="205" text-anchor="middle" fill="#888" font-size="9" font-family="sans-serif">CORPS EN LIGNE DROITE</text>' +
            '  <line x1="60" y1="180" x2="340" y2="180" stroke="#4A4A6A" stroke-width="1" stroke-dasharray="4 2" opacity="0.5"/>'
        ),
        "muscles": [(290, "Transverse"), (305, "Rectus abdominis"), (320, "Érecteurs")],
        "muscles_colors": ["#FF6B35", "#42A5F5", "#66BB6A"],
        "vigilance": [(288, "Ne creuse pas le dos"), (290, "Arrête en cas de douleur")]
    },
]


def build_svg(ex):
    center = ex["center"]()
    muscles = "\n".join(
        muscle_line(y, color, text)
        for (y, text), color in zip(ex["muscles"], ex["muscles_colors"])
    )
    vigilance = "\n".join(
        vigilance_line(y, text)
        for y, text in ex["vigilance"]
    )
    accent = ex["muscles_colors"][0]
    return TEMPLATE.format(
        title=ex["title"],
        subtitle=ex["subtitle"],
        center_content=center,
        muscles_text=muscles,
        vigilance_text=vigilance,
        accent=accent
    )


def main():
    out_dir = "exercises"
    os.makedirs(out_dir, exist_ok=True)
    for ex in EXERCISES:
        svg = build_svg(ex)
        path = os.path.join(out_dir, f"{ex['file']}.svg")
        with open(path, "w", encoding="utf-8") as f:
            f.write(svg)
        print(f"Généré : {path}")


if __name__ == "__main__":
    main()
