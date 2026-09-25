---
name: forge-fml-redesign
description: >
  Redesign Forge/FML internals without preserving bad APIs for compatibility:
  lifecycle, registries, events, networking, config, sided execution, loader.
  Requires a mod migration plan. Trigger: obsolete or unmaintainable internals.
---

# Forge/FML Redesign

## Purpose

Replace obsolete Forge/FML architecture with clean, safe, fast, maintainable
implementations (AGENTS.md §1, §5, §14–§15). Breaking changes are allowed
when intentional, documented, and migrated.

## Scope

Mod discovery, dependency resolution, loading lifecycle, event dispatch,
registries, sided execution, networking, configuration, metadata, bootstrap,
classloading, transforms, error reporting, logging, resource handling.

## Workflow

1. Run `upstream-project-research` first (modern Forge, NeoForge, Fabric,
   Sponge, GTNH forks).
2. Identify the owning layer (AGENTS.md §7) and all known callers.
3. Design the improved API; justify with a concrete problem (correctness,
   Java compat, maintainability, performance, extensibility, hack removal).
4. Define the breaking change + migration path (hand off to
   `breaking-api-design` for the migration doc).
5. Coordinate migrated mod updates (`legacy-mod-migration`); never silently
   expand scope (AGENTS.md §39).
6. Remove the obsolete implementation once replacements are established
   (AGENTS.md §41).

## Rules

- Do not keep obsolete APIs, reflection hacks, or LaunchWrapper assumptions
  solely because 2014 mods used them.
- Do not rewrite for aesthetics alone.
- Update docs, migration guides and attribution in the same work (AGENTS.md §42).

## Validation

Build + unit/integration tests + client and dedicated-server startup +
mod loading + world create/load + shutdown/restart (AGENTS.md §32, §34).
