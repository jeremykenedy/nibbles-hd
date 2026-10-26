"""Check local links, native capture hashes and responsive banner variants."""

import hashlib
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path
from urllib.parse import unquote, urlsplit

root = Path(__file__).resolve().parent.parent
readme = (root / "README.md").read_text()
index = (root / "docs/index.md").read_text()
for document in [*root.glob("*.md"), *sorted((root / "docs").rglob("*.md"))]:
    text = document.read_text()
    if "\u2014" in text:
        raise SystemExit("Unexpected em dash: " + str(document))
    links = re.findall(r"\]\(([^)]+)\)", text) + re.findall(r'(?:src|srcset|href)="([^"]+)"', text)
    for link in links:
        parsed = urlsplit(link)
        if parsed.scheme or not parsed.path:
            continue
        if not (document.parent / unquote(parsed.path)).is_file():
            raise SystemExit(f"Broken local link in {document.name}: {link}")
for document in (root / "docs").rglob("*.md"):
    relative = document.relative_to(root / "docs").as_posix()
    if relative != "index.md" and f"]({relative})" not in index:
        raise SystemExit("Maintained guide missing from docs index: " + relative)
references = set(re.findall(r'(?:src|srcset)="(art/[^"#]+)', readme))
for banner in (root / "art").glob("banner-*.svg"):
    ET.parse(banner)
    match = re.fullmatch(r"(banner-[a-z-]+)\.([a-f0-9]{12})\.svg", banner.name)
    if match:
        if hashlib.sha256(banner.read_bytes()).hexdigest()[:12] != match[2]:
            raise SystemExit("Incorrect banner content hash: " + banner.name)
        if "art/" + banner.name not in references:
            raise SystemExit("Unreferenced hashed banner: " + banner.name)
        if banner.read_bytes() != (root / "art" / (match[1] + ".svg")).read_bytes():
            raise SystemExit("Hashed banner differs from source: " + banner.name)
if len([name for name in references if name.endswith(".svg")]) != 8:
    raise SystemExit("README must reference all eight banner variants")
provenance = json.loads((root / "art/screenshots/provenance.json").read_text())
for capture in provenance["images"]:
    if hashlib.sha256((root / capture["file"]).read_bytes()).hexdigest() != capture["sha256"]:
        raise SystemExit("Capture hash mismatch: " + capture["file"])
print("Local docs/gallery links, capture hashes and eight responsive banners pass.")
