---
name: migration-regression-testing
description: >
  Validate the rebuilt Forge plus rebuilt mods (not old binaries): compile,
  unit/integration, client + dedicated server, mod loading, world create/load,
  shutdown/restart. Prioritizes save/gameplay compat over legacy ABI.
  Trigger: any platform change, API break, or mod migration.
---

# Migration & Regression Testing

## Purpose

A change is not complete because it compiles (AGENTS.md §32–§33). Test the
rebuilt stack as a whole: modernized Forge + recompiled mods. Compatibility
priority is worlds/saves and gameplay behavior, not old binary ABI.

## Layers (apply relevant ones every time)

Compile → unit tests → integration tests → client startup →
dedicated-server startup → dev runtime → packaged runtime → mod loading →
world creation → world loading → shutdown/restart. Plus packaged/reobfuscated
checks for transform/mapping changes, and ≥1 migrated consumer per API break.

## Regression discipline

1. Reproduce the original failure if possible.
2. Implement the fix.
3. Add a regression test or deterministic validation when practical.
4. Verify the original failure is gone.
5. Never delete failing tests to make the build green.

## Side awareness

Client success never implies server success: no client-only refs in
common/server code; dedicated-server validation is mandatory for lifecycle,
registries, networking, classloading, world logic, bootstrap and dependency
resolution (AGENTS.md §34).

## Reporting

Close with the AGENTS.md §44 report: Changed, Architecture, Upstream Work
(copied/adapted/ported/inspired/reference), Breaking Changes, Migration
Impact, Validation (commands + runtime tests), Remaining Risks.
