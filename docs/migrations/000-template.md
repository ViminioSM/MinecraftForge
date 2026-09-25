# 000 — Migration Guide Template

> Copy this file to `NNN-topic.md` when announcing a breaking change.
> Delete this quote block in the copy. Status starts as `Planned`.

- Status: Planned | Effective (since: <commit/PR>)
- Affected subsystem: <lifecycle | registries | events | networking | config | sided execution | ...>
- Old API removed/replaced: <...>
- New API: <...>

## Reason

Why the old API could not be kept (concrete problem: correctness, Java
compatibility, maintainability, performance, extensibility, hack removal).

## Old API

```java
// minimal before-example
```

## New API

```java
// minimal after-example
```

## Migration path

1. Step-by-step instructions for a mod author.
2. Include mechanical transforms where possible (search/replace patterns).
3. Call out what cannot be automated.

## Behavioral differences

- What changes at runtime, even for correctly migrated callers.
- What assumptions were removed.

## Removed assumptions

- Explicit list (e.g. Java 6/7/8 behavior, LaunchWrapper ordering,
  coremod sequencing, client-only availability).

## Affected callers

- Workspace callers found (file paths) and their migration state.
- External mods known to be affected.

## Save/world impact

- None | Migrated automatically | Detected + clear failure | Manual step.
- Never silently reinterpret incompatible persistent data (AGENTS.md §20).

## Validation

- Commands and runtime tests performed (see `migration-regression-testing`).
