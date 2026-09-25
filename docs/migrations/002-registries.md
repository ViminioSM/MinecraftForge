# 002 — Registries (Planned)

- Status: Planned (new API not yet implemented)
- Affected subsystem: registries
- Old API removed/replaced: `GameRegistry` hacks and ID-based registration paths
- New API: TBD — to be designed under `forge-fml-redesign` + `breaking-api-design`

## Reason

Legacy ID-based registration and `GameRegistry` workarounds encode 2014
constraints the modernized platform should replace with a clean,
type-safe registry API.

## Old API

TBD — inventory current `GameRegistry` call sites first.

## New API

TBD.

## Migration path

TBD once the new API is defined.

## Behavioral differences

TBD — registry changes interact with persistent data; treat as high-risk
for worlds/saves (AGENTS.md §20).

## Removed assumptions

TBD (at minimum: numeric-ID stability assumptions).

## Affected callers

TBD — inventory with `legacy-mod-migration` before finalizing the design.

## Save/world impact

TBD — must explicitly address numeric IDs, NBT, chunks, TileEntities,
inventories and dimensions. Detect old formats; migrate or fail clearly.

## Validation

TBD — world create/load round-trips with migrated content mods.
