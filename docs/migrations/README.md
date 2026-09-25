# Migration Guides

One guide per breaking-change group. Group related changes logically —
do not create hundreds of trivial documents (AGENTS.md §6).

## Index

| Guide | Status | Scope |
|---|---|---|
| [000-template](000-template.md) | Template — do not treat as a real change | How to write a guide |
| [001-sided-execution](001-sided-execution.md) | Planned | `@SidedProxy` / sided-code replacement |
| [002-registries](002-registries.md) | Planned | `GameRegistry` and registry overhaul |
| [003-networking](003-networking.md) | Planned | Networking system replacement |
| [004-lifecycle](004-lifecycle.md) | Planned | Loading-lifecycle redesign |

`Planned` = breaking direction announced, new API not yet implemented.
A guide becomes `Effective` only when the new API lands and at least one
migrated consumer compiles and runs against it.

## Rules for authors

1. Every guide must cover: old API, new API, reason, migration path,
   behavioral differences, removed assumptions.
2. Show a before/after mod-side diff, not just prose.
3. Record affected callers found in the workspace.
4. Link the implementing change (commit/PR) once it exists.
5. Update this index status (`Planned` → `Effective`) in the same work.
