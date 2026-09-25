# 001 — Sided Execution (Planned)

- Status: Planned (new API not yet implemented)
- Affected subsystem: sided execution
- Old API removed/replaced: `@SidedProxy` string-referenced proxy pattern
- New API: TBD — to be designed under `forge-fml-redesign` + `breaking-api-design`

## Reason

String-based `@SidedProxy` proxies (`@SidedProxy(clientSide = "...",
serverSide = "...") public static CommonProxy proxy;`) defer wiring errors
to runtime, resist refactoring tools, and encode 2014 sided-code assumptions
the modernized platform should replace with an explicit, type-safe mechanism.

## Old API

```java
@SidedProxy(clientSide = "com.example.mod.ClientProxy", serverSide = "com.example.mod.ServerProxy")
public static CommonProxy proxy;
```

## New API

TBD. The replacement must provide explicit client/server/common separation
without string class names, with failures surfaced at build time where
possible. Design it before migrating callers.

## Migration path

TBD once the new API is defined. Expected shape: replace the annotated
static field with the new sided-binding declaration + adjust proxy classes.

## Behavioral differences

TBD.

## Removed assumptions

TBD (at minimum: string-based lazy class loading of proxies).

## Affected callers

TBD — inventory with `legacy-mod-migration` before finalizing the design.

## Save/world impact

Expected: none (code-wiring change, not persistent data).

## Validation

TBD — client + dedicated server boot with at least one migrated mod
exercising both sides.
