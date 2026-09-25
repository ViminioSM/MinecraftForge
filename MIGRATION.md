# Migration Guide

Binary compatibility with old Forge 1.7.10 mods is **not** required.
Mods are migrated, refactored and recompiled against the new API:

```text
Old mod
    ↓
automated analysis
    ↓
migration / refactoring
    ↓
new API
    ↓
recompilation
    ↓
modernized Forge
```

Compatibility priority is **worlds/saves and gameplay behavior**,
not legacy binary ABI (AGENTS.md mission, §5, §20).

## Guides

Grouped by subsystem, under `docs/migrations/`:

- [Template — how to write a guide](docs/migrations/000-template.md)
- [001 — Sided execution](docs/migrations/001-sided-execution.md) (`@SidedProxy` replacement, planned)
- [002 — Registries](docs/migrations/002-registries.md) (`GameRegistry` overhaul, planned)
- [003 — Networking](docs/migrations/003-networking.md) (networking replacement, planned)
- [004 — Loading lifecycle](docs/migrations/004-lifecycle.md) (lifecycle redesign, planned)

No guide is `Effective` yet — no breaking API has landed. A guide becomes
effective only when the new API is implemented and at least one migrated
consumer compiles and runs against it.

## For mod authors

1. Start with the guide for the subsystem that broke your build.
2. Each guide gives old API → new API, reason, migration path, behavioral
   differences and removed assumptions.
3. The `legacy-mod-migration` skill describes the full per-mod workflow
   (inventory, refactor, save-format handling, report).
4. The `breaking-api-design` skill describes the platform-side contract
   every guide must satisfy.

## For contributors

- Write the guide in the same work as the breaking change (AGENTS.md §42).
- Group related changes; do not create hundreds of trivial documents.
- Update the guide status (`Planned` → `Effective`) with the implementing commit/PR.
