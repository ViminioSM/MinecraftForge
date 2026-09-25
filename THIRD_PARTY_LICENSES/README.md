# THIRD_PARTY_LICENSES

Intended location for full license texts of upstream components actually
incorporated (copied, ported, or adapted) into this branch.
Full license texts are vendored below — no upstream code is vendored yet.

**Current status: license texts present, no upstream code vendored.**
Each file is the byte-exact upstream license file fetched from the source
URL listed in `../UPSTREAMS.md` (fetched 2026-09-25; re-fetch and diff if
you suspect upstream changed it).

When you incorporate upstream code:

1. Verify the license (see `../UPSTREAMS.md`).
2. Confirm the vendored text above still matches upstream (re-fetch and
   diff if stale); add any missing per-module notice files.
3. Preserve all copyright headers and notice files (`CREDITS`,
   `README.original.md`, per-module `LICENSE` files where required).
4. Record project, URL, commit/tag, files used, authors, changes and
   attribution in `../UPSTREAMS.md`.

## Files

- `RetroFuturaGradle-LGPL-2.1.txt` — GTNewHorizons/RetroFuturaGradle, `LICENSE` on `master` (full LGPL-2.1)
- `GTNHGradle-LGPL-2.1.txt` — GTNewHorizons/GTNHGradle, `LICENSE` on `master` (full LGPL-2.1)
- `RetroFuturaBootstrap-LGPL-3.0.txt` — GTNewHorizons/RetroFuturaBootstrap, `LICENSE` on `master` (full LGPL-3.0)
- `lwjgl3ify-LGPL-3.0.txt` — GTNewHorizons/lwjgl3ify, `LICENSE` on `master` (full LGPL-3.0)
- `Hodgepodge-LGPL-3.0.txt` — GTNewHorizons/Hodgepodge, `LICENSE.txt` on `master` (full LGPL-3.0)
- `GTNHLib-LGPL-3.0.txt` — GTNewHorizons/GTNHLib, `LICENSE.txt` on `master` (full LGPL-3.0)
- `Angelica-LGPL-3.0.txt` — GTNewHorizons/Angelica, `LICENSE` on `master` (custom header + full LGPL-3.0)
- `ArchaicFix-LGPL-3.0.txt` — embeddedt/ArchaicFix, `LICENSE.md` on `main` (LGPLv3 pointer + occlusion exclusion; the LGPL-3.0 terms it points to are vendored above)
- `CoreTweaks-MIT.txt` — makamys/CoreTweaks, `LICENSE` on `master` (MIT, Copyright (c) 2021 makamys)
- `UniMixins-Unlicense.txt` — LegacyModdingMC/UniMixins, `LICENSE.UNLICENSE` on `master` (Unlicense; module LGPL exceptions documented in `../UPSTREAMS.md`)
- `FalseTweaks-LICENSE.txt` — FalsePattern/FalseTweaks, `LICENSE` on `master` (LGPLv3-only + all third-party blocks: CC-BY-SA-4.0, MIT, LGPL-2.1+)
- `CC-BY-SA-4.0.txt` — Creative Commons Attribution-ShareAlike 4.0 legal code (covers `mcpatche` assets referenced by FalseTweaks)

Per-module exclusions (occlusion culling, LoliASM, mcpatche assets) must be
tracked in `../UPSTREAMS.md`; do not vendor those directories without their
own license files and due diligence.
