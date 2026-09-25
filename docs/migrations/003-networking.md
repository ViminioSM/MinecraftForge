# 003 — Networking (Planned)

- Status: Planned (new API not yet implemented)
- Affected subsystem: networking
- Old API removed/replaced: entire legacy packet/channel system (exact surface TBD)
- New API: TBD — to be designed under `forge-fml-redesign` + `breaking-api-design`

## Reason

The inherited networking stack carries 2014 protocol, threading and
side-separation assumptions. A clean replacement must address packet
registration, serialization, Netty usage, main-thread dispatch, side
separation, malformed-packet handling, size limits and auth/state
assumptions (AGENTS.md §21).

## Old API

TBD — inventory current packet/channel call sites first.

## New API

TBD.

## Migration path

TBD once the new API is defined.

## Behavioral differences

TBD. Old client/new server compatibility is not mandatory unless
specifically requested — correctness and maintainability come first.

## Removed assumptions

TBD.

## Affected callers

TBD — inventory with `legacy-mod-migration` before finalizing the design.

## Save/world impact

Expected: none directly (protocol change), unless IDs or persistent
payloads are involved — then AGENTS.md §20 applies.

## Validation

TBD — client + dedicated-server packet round-trips with migrated mods,
including malformed-packet and size-limit cases.
