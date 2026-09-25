---
name: gtnh-fix-harvester
description: >
  Harvest Hodgepodge, ArchaicFix, CoreTweaks, GTNHLib, Angelica, FalseTweaks
  and related fix mods for behavior that belongs natively in the platform.
  Trigger: any old Minecraft/Forge bug fix or optimization proposal.
---

# GTNH Fix Harvester

## Purpose

Turn years of community 1.7.10 fixes into native platform behavior instead of
perpetual corrective mods — but only when the fix belongs in the platform
(AGENTS.md §7, §19). Hodgepodge is still actively fixed in 2026; ArchaicFix
covers lighting, leaks, worldgen and performance.

## Sources

Hodgepodge, lwjgl3ify, Angelica, GTNHLib, RetroFuturaBootstrap,
RetroFuturaGradle, GTNH forks, ArchaicFix, CoreTweaks, FalseTweaks,
other maintained 1.7.10 fix/optimization projects.

## Workflow

1. Identify the original bug precisely (repro first where possible).
2. Locate the upstream implementation (exact commit/tag) and understand:
   bug, implementation, side effects, config/compat assumptions, license,
   whether it still applies after our architectural changes.
3. Decide: integrate natively vs leave as a mod. Platform-level root causes
   go central; mod-specific bugs stay out of Forge.
4. Do not merge optimizations indiscriminately — require evidence
   (baseline → change → measurement → comparison, AGENTS.md §22).
5. Attribute via `source-attribution-license` (`UPSTREAMS.md` +
   `THIRD_PARTY_LICENSES/`); respect the occlusion/CC-BY-SA exclusions.

## Pitfalls

- Vendoring the ArchaicFix/FalseTweaks `occlusion` modules as if LGPL.
- Carrying over config assumptions that no longer hold natively.
- Claiming speedups without profiler evidence.
