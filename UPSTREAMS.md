# Upstream Projects

This file tracks every external project studied, referenced, adapted, ported,
or copied by the Forge 1.7.10 modernization effort.

**Current status (2026-09-25): no upstream code is vendored in this branch.**
Full license texts are vendored byte-exact from upstream in
`THIRD_PARTY_LICENSES/` (see its `README.md` for file provenance).
All entries below are `Reference` or `Architecture reference` unless stated
otherwise.
All entries below are `Reference` or `Architecture reference` unless stated
otherwise. The UniMixins idioms already reimplemented in FML (mixin late-config
orchestration, crash-report mixin listing, transformer safety markers, ASM
remapping) are attributed in `README.md`; no LGPLv3 loader code was taken.

Rule: never copy code before checking its license. When code or a substantial
implementation idea is reused, update this file plus `THIRD_PARTY_LICENSES/`
and preserve required copyright/license notices. See `AGENTS.md` §2–§4.

License data below was verified on 2026-09-25 via the GitHub API
(`GET /repos/{owner}/{repo}/license` + repo metadata). `pushed_at` marks
upstream activity at verification time.

Revision pinning: no entry pins an exact commit/tag yet because no upstream
code is vendored. Each entry records `Revision: not pinned (reference
verified 2026-09-25)`. Pin the exact commit/tag in the entry the moment any
code, port, or adaptation from that project lands in this branch.

## Reference organizations

- GTNewHorizons — https://github.com/GTNewHorizons
- LegacyModdingMC — https://github.com/LegacyModdingMC

## Projects

### RetroFuturaGradle

- Repository: https://github.com/GTNewHorizons/RetroFuturaGradle
- License: LGPL-2.1 (`spdx_id: LGPL-2.1`, file `LICENSE`, branch `master`)
- Upstream activity at verification: active (`pushed_at: 2026-09-06`)
- Used for: modern Gradle toolchain, MCP/Forge workspace generation,
  reobfuscation architecture (evaluation candidate)
- Integration: Reference / evaluated, not adopted
- Changes: none (no code copied)
- Credits: GTNewHorizons contributors

### RetroFuturaBootstrap

- Repository: https://github.com/GTNewHorizons/RetroFuturaBootstrap
- License: LGPL-3.0 (`spdx_id: LGPL-3.0`, file `LICENSE`, branch `master`)
- Upstream activity at verification: active (`pushed_at: 2026-08-18`)
- Used for: LaunchWrapper replacement, modern-Java bootstrap and classloading,
  early-loading plugins and FML/coremod repair transformers
- Integration: Architecture reference
- Changes: none (no code copied)
- Credits: GTNewHorizons contributors

### lwjgl3ify

- Repository: https://github.com/GTNewHorizons/lwjgl3ify
- License: LGPL-3.0 (`spdx_id: LGPL-3.0`, file `LICENSE`, branch `master`)
- Upstream activity at verification: active (`pushed_at: 2026-09-17`)
- Used for: LWJGL3 / Java 17+ recipe for 1.7.10 (ASM 9.x upgrade,
  `sun.reflect` field/enum hack replacement); design guidance only,
  reimplemented from scratch in this branch
- Integration: Design guidance (techniques reimplemented, no code copied)
- Changes: original implementations in `fml/` + updated deps in `fml/jsons/`
- Credits: eigenraven and contributors

### Hodgepodge

- Repository: https://github.com/GTNewHorizons/Hodgepodge
- License: LGPL-3.0 (`spdx_id: LGPL-3.0`, file `LICENSE.txt`, branch `master`)
- Upstream activity at verification: very active (`pushed_at: 2026-09-24`)
- Used for: general 1.7.10 bug-fix harvest candidate (platform-level fixes)
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: GTNewHorizons contributors

### GTNHLib

- Repository: https://github.com/GTNewHorizons/GTNHLib
- License: LGPL-3.0 (`spdx_id: LGPL-3.0`, file `LICENSE.txt`, branch `master`)
- Upstream activity at verification: very active (`pushed_at: 2026-09-25`)
- Used for: shared-library patterns, fix harvest candidate
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: GTNewHorizons contributors

### GTNHGradle

- Repository: https://github.com/GTNewHorizons/GTNHGradle
- License: LGPL-2.1 (`spdx_id: LGPL-2.1`, file `LICENSE`, branch `master`)
- Upstream activity at verification: active (`pushed_at: 2026-09-22`)
- Used for: build-logic centralization concepts for 1.7.10 mods
  (distinct repo from RetroFuturaGradle, same license family)
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: GTNewHorizons contributors

### Angelica

- Repository: https://github.com/GTNewHorizons/Angelica
- License: LGPL-3.0 intended, but GitHub reports `key: other`
  (`spdx_id: NOASSERTION`, file `LICENSE`, branch `master`).
  File content: custom header over `Shaders.java` provenance plus full
  LGPL-3.0 text. Treat as LGPL-3.0 with manual attribution; SPDX/SBOM
  scanners will flag it as unknown.
- Upstream activity at verification: active (`pushed_at: 2026-09-24`)
- Used for: LWJGL3/OpenGL 3.3+ core profile, fixed-function emulation,
  display-list adaptation (rendering-modernization reference)
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: GTNewHorizons contributors
- Caution: verify the `Shaders.java` relicensing chain before porting
  any shader code.

### UniMixins

- Repository: https://github.com/LegacyModdingMC/UniMixins
- License: **Unlicense (base) + 3 LGPL module exceptions.**
  GitHub reports `key: other`; `LICENSE` (549 bytes) defers to
  `LICENSE.Unlicense` except:
  - `module-spongeMixins`: LGPLv3
  - `module-mixinBooterLegacy`: LGPLv2.1
  - `module-gtnhMixins`: LGPLv3
  Module dirs require preserving `CREDITS`, `LICENSE`,
  `README.original.md`.
- Upstream activity at verification: active (`pushed_at: 2026-09-20`)
- Used for: late-config orchestration idiom (`mixinbooterlegacy`,
  LGPLv2.1, by tox1cozz — reimplemented natively in FML with attribution),
  crash-report mixin listing idea (`compat` module, Unlicense —
  reimplemented inside `FMLCommonHandler`), ASM remapping idea (Unlicense)
- Integration: Reference / reimplemented natively (no module code copied;
  no LGPLv3 loader code taken). If the reimplemented idiom is later judged
  a derivation of the LGPLv2.1 module, vendor the module's full LGPL-2.1
  text + notices into `THIRD_PARTY_LICENSES/` at that time.
- Changes: reimplemented natively in FML; see `README.md` for details
- Credits: LegacyModdingMC, tox1cozz
- Revision: not pinned (reference verified 2026-09-25)

### ArchaicFix

- Repository: https://github.com/embeddedt/ArchaicFix
- License: LGPLv3 base **with module exclusions.**
  GitHub reports `key: other`, file `LICENSE.md`, branch `main`
  (not `master`). The occlusion-culling module is derived from CoFHTweaks
  (itself derived from Minecraft 1.8) and is **not** under LGPLv3 — see
  its own `.../occlusion/LICENSE`. Bundled `zone.rong.loliASM` is
  LGPL-2.1; `ca.spottedleaf` code is documented as LGPL-2.1.
- Upstream activity at verification: maintained
  (`pushed: 2026-05-21`, `updated: 2026-09-25`)
- Used for: lighting, leak, worldgen and performance fix harvest candidate
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: embeddedt and contributors
- Caution: isolate the `occlusion` directory from any automated port.

### CoreTweaks

- Repository: https://github.com/makamys/CoreTweaks
- License: MIT (`key: mit`, `spdx_id: MIT`, file `LICENSE`,
  `Copyright (c) 2021 makamys`). Only pure-permissive license in this list.
- Upstream activity at verification: stale (`pushed_at: 2024-02-10`,
  no push for ~2.5 years); license itself is stable
- Used for: `Mixingasm` preprocessing-exclusion and mixin-safety marker
  concept (public-domain snippet published by Makamys — distinct from the
  MIT-licensed CoreTweaks repo as a whole), pending-block-update fix source
- Integration: Idea reference (foreign safety markers honoured by name,
  exclusions opt-in via `-Dfml.mixin.excludedTransformers`; no code copied)
- Changes: honoured by name only, no code copied
- Credits: makamys
- Revision: not pinned (reference verified 2026-09-25)

### FalseTweaks

- Repository: https://github.com/FalsePattern/FalseTweaks
- License: **LGPLv3-only base + multiple third-party exceptions**
  (`key: other`, file `LICENSE`, ~5.6 KB). Header claims
  `Copyright (C) 2022-2025 FalsePattern / All Rights Reserved` followed by
  `LGPLv3 only` — treat as LGPL-3.0-only, not or-later. Exceptions:
  `assets/minecraft/mcpatche/*` = CC-BY-SA-4.0; chunk occlusion tags /
  threaded updates from ArchaicFix (LGPLv3); occlusion impl under its own
  `.../modules/occlusion/LICENSE` (CoFH); `packing2d` by papuja (MIT);
  `triangulator.sorting` from Minetest/celeron55 (LGPL-2.1+); `threading`
  from Angelica (LGPLv3); `mipmappatch` from Hodgepodge (LGPLv3);
  pending-block-updates from CoreTweaks (MIT).
- Upstream activity at verification: active (`pushed_at: 2026-08-09`)
- Used for: optimization/fix harvest candidate (triage per-module only)
- Integration: Reference (no code copied yet)
- Changes: none yet
- Credits: FalsePattern and all third parties listed in its `LICENSE`

## Compatibility notes

- LGPL-2.1 (RetroFuturaGradle, GTNHGradle) vs LGPL-3.0-only material
  (parts of FalseTweaks) needs per-case compatibility analysis before
  combining; prefer architectural reference over copying when in doubt.
- CC-BY-SA-4.0 applies only to `assets/minecraft/mcpatche` inside
  FalseTweaks — attribution + share-alike for those assets, not LGPL.
- Do not copy the `occlusion` modules (ArchaicFix/FalseTweaks) as if LGPL;
  CoFH/Minecraft-1.8 provenance requires its own due diligence.

## Template for new entries

```text
Project: <name>
Repository: <url>
Revision: <commit/tag inspected>
Usage: Reference / Architecture reference / Adapted / Ported / Copied
Affected subsystem: <bootstrap | classloading | rendering | ...>
Local changes: <description>
License: <verified SPDX + file path + branch>
Credits: <authors/organization>
```
