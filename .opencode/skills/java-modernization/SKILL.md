---
name: java-modernization
description: >
  Move 1.7.10 off Java 8 assumptions toward modern Java deliberately, using
  lwjgl3ify and RetroFuturaBootstrap as references. Covers modules, reflection,
  Unsafe, classloaders, ASM, natives and logging. Trigger: Java compat work.
---

# Java Modernization

## Purpose

Run the modernized stack on modern Java (currently validated to JDK 21 for
dedicated server + client boot; ASM 9.6, Guava 21.0, `-Djava.security.manager=allow`).
Do not assume Java 8 behavior is desirable (AGENTS.md §9).

## Checklist

Removed JDK APIs, module encapsulation, reflection restrictions, `Unsafe`,
classloader changes, `URLClassLoader` assumptions, `SecurityManager`,
old TLS, old ASM / classfile versions, `final`/`static` mutation, natives,
old Apache/Guava/logging libs, JVM args, `--add-opens`, serialization.

## References (read before inventing)

- RetroFuturaBootstrap — bootstrap/classloading on modern Java.
- lwjgl3ify — ASM 9.x upgrade, `sun.reflect` hack replacement.
- Hodgepodge, RetroFuturaGradle — remaining compat patterns.
- Never copy without license check (`source-attribution-license`).

## Rules

- Fix the underlying implementation instead of adding permanent JVM hacks
  when reasonably possible.
- Do not run `Launch` (casts the system classloader to `URLClassLoader`);
  use `FMLLaunchWrapper` or the RFB-based path (`bootstrap-classloading`).
- Validate both dev and packaged runtimes; mapping/transform issues often
  appear only after packaging (AGENTS.md §12, §32).

## Validation

Client + dedicated-server startup on the target JDK, transformed classes,
normal mods, Mixins, early-loading components, dev + packaged runtime.
