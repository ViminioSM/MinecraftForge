---
name: legacy-mod-migration
description: >
  Rebuild a legacy 1.7.10 mod against the new platform API: inventory Forge/FML
  dependencies, coremods, ASM, reflection, proxies, networking, IDs, rendering
  and saves, then produce a migration report. Trigger: API breaking change or
  mod port request.
---

# Legacy Mod Migration

## Purpose

Execute the canonical pipeline (AGENTS.md mission):

```text
Old mod → analysis → migration/refactoring → new API → recompilation → modernized Forge
```

Binary ABI compat with old mods is NOT required; save/world and gameplay
compatibility is prioritized where possible.

## Workflow

1. Inventory before touching code (AGENTS.md §16): `@Mod` entrypoint,
   lifecycle handlers, dependencies (incl. optional), `@SidedProxy`,
   registries, networking, GUI handlers, config, worldgen, events,
   rendering hooks, ASM/coremods, Access Transformers, reflection, direct
   Minecraft/LWJGL/Forge-internal usage, NBT/save formats, cross-mod
   integrations.
2. Map each item to the new API; list removed assumptions
   (e.g. `@SidedProxy public static CommonProxy proxy`, `GameRegistry`
   hacks, Java 6/7/8 assumptions, coremod ordering).
3. Refactor against the new API; delete dead compat only after the
   replacement is established (AGENTS.md §41).
4. Handle persistent data explicitly: detect old formats, migrate or fail
   clearly — never silently reinterpret (AGENTS.md §20).
5. Produce a migration report: old API → new API, reason, behavioral
   differences, files changed, remaining risks.

## Validation

Compile + run on client and dedicated server + load/create worlds +
exercise the mod's systems + shutdown/restart. Test at least one migrated
consumer per changed API (AGENTS.md §32).
