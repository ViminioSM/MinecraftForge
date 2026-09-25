---
name: breaking-api-design
description: >
  Replace bad legacy APIs with clean modern ones: define the new API, document
  the break, and ship migration instructions for rebuilt mods. Trigger:
  any intentional API break (registries, networking, config, proxies, lifecycle).
---

# Breaking API Design

## Purpose

Never contort the platform to preserve a bad API. Design the improved API,
document the break, migrate the mods, remove the obsolete code (AGENTS.md §5).

## Workflow

1. Identify all known callers before finalizing the design.
2. Write the migration doc (prefer `docs/migrations/`, else `MIGRATION.md`;
   group related changes — AGENTS.md §6) covering:
   old API, new API, reason, migration path, behavioral differences,
   removed assumptions.
3. Example target: eliminate `@SidedProxy public static CommonProxy proxy`,
   `GameRegistry` hacks, whole networking systems, broken lifecycle —
   with explicit before/after + mod-side diff.
4. Coordinate caller updates in the same or follow-up work
   (`legacy-mod-migration`); delete dead compat aggressively once the
   replacement is established (AGENTS.md §41).
5. Update README / architecture notes / examples in the same work (AGENTS.md §42).

## Rules

- Breaking changes must be intentional and documented — no silent behavior shifts.
- No ABI-compat shims kept "just in case"; keep shims only with a real
  migration reason and a removal plan.

## Validation

At least one migrated consumer compiled and running per changed API,
plus the `migration-regression-testing` layers.
