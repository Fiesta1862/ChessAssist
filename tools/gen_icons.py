#!/usr/bin/env python3
# one-time: generates mipmap PNG icons from the inline SVG.
# run from REPO ROOT:  python tools/gen_icons.py
# needs: pip install pillow cairosvg
import cairosvg, os

svg = '''<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" viewBox="0 0 108 108">
<rect width="108" height="108" fill="#0A0A0A"/>
<rect x="22" y="66" width="14" height="14" fill="#00FF88"/>
<rect x="36" y="52" width="14" height="14" fill="#FF2E88"/>
<line x1="30" y1="84" x2="78" y2="32" stroke="#00FF88" stroke-width="6" stroke-linecap="round"/>
<polygon points="86,24 80,44 66,30" fill="#00FF88"/>
</svg>'''

sizes = {"mdpi":48,"hdpi":72,"xhdpi":96,"xxhdpi":144,"xxxhdpi":192}
for dens, px in sizes.items():
    path = f"app/src/main/res/mipmap-{dens}"
    os.makedirs(path, exist_ok=True)
    cairosvg.svg2png(bytestring=svg.encode(), write_to=f"{path}/ic_launcher.png",
                     output_width=px, output_height=px)
    cairosvg.svg2png(bytestring=svg.encode(), write_to=f"{path}/ic_launcher_round.png",
                     output_width=px, output_height=px)
print("done")