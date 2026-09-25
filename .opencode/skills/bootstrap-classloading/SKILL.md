---
name: bootstrap-classloading
description: >
  Replace LaunchWrapper/FML bootstrap with a RetroFuturaBootstrap-inspired
  modern bootstrap: tweakers, coremods, transformers, early/late loading and
  classpath discovery. Trigger: bootstrap, classloader, transformer discovery
  or launch-pipeline changes. High-risk subsystem.
---

# Bootstrap & Classloading

## Purpose

Provide a clean modern bootstrap on Java 17+ without building a second
competing system for no reason: evaluate RetroFuturaBootstrap first
(AGENTS.md §10). RFB also supplies early-loading plugins and transformers
that repair FML and coremods.

## Scope (high-risk)

LaunchWrapper, LaunchClassLoader, tweakers, coremods, `IFMLLoadingPlugin`,
transformers, early/late loading, classpath discovery, transformer and
classloader exclusions.

## Workflow

1. `upstream-project-research`: RFB implementation + lwjgl3ify handling of
   the same Java issue + Hodgepodge/ArchaicFix impact on affected mods.
2. Design the replacement (or RFB-compatible integration); document ordering,
   exclusions, and early vs late responsibilities.
3. Implement centrally — do not fix platform classloading per-mod (AGENTS.md §7).
4. Preserve structural-fix preference: source fix > platform API > patch >
   Mixin > transformer > reflection hack (AGENTS.md §8).

## Validation (all required)

Client startup, dedicated-server startup, transformed classes, normal mods,
Mixins, early-loading components, dev environment, packaged runtime.
Client success never implies server success (AGENTS.md §34).

## Pitfalls

- Competing bootstrap systems without a clear technical reason.
- Classpath ordering bugs (modern libs must precede stale Mojang-bundled
  copies, e.g. guava/gson/netty in `minecraft_server.jar`).
- Testing only the dev environment and missing packaged/reobfuscated breaks.
