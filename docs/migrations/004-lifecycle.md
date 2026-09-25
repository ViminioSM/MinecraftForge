# 004 — Loading Lifecycle (Planned)

- Status: Planned (new API not yet implemented)
- Affected subsystem: loading lifecycle (discovery → construction → pre/init/post)
- Old API removed/replaced: legacy lifecycle event/handler ordering assumptions (exact surface TBD)
- New API: TBD — to be designed under `forge-fml-redesign` + `breaking-api-design`

## Reason

Legacy lifecycle behavior preserves historical accidents alongside real
ordering guarantees. The redesign must state explicit phases, guarantees
and failure semantics without keeping broken ordering "for historical
reasons" (AGENTS.md §5).

## Old API

TBD — inventory lifecycle handlers and ordering dependencies first.

## New API

TBD.

## Migration path

TBD once the new API is defined.

## Behavioral differences

TBD.

## Removed assumptions

TBD.

## Affected callers

TBD — inventory with `legacy-mod-migration` before finalizing the design.

## Save/world impact

Expected: none directly, unless lifecycle interacts with registry snapshots
or world loading — then AGENTS.md §20 applies.

## Validation

TBD — full client + dedicated-server startup with mod loading,
world creation/loading and shutdown/restart.
