---
name: upstream-project-research
description: >
  Research existing maintained implementations before building any Forge/FML
  subsystem. Compares GTNH, LegacyModdingMC, modern Forge/NeoForge/Fabric,
  Mixin and JVM projects by code, license, architecture and maturity.
  Trigger: new subsystem, bug fix, Java/bootstrap/rendering/build work.
---

# Upstream Project Research

## Purpose

Prevent reinventing solved problems. Every substantial infrastructure or fix
must first survey existing maintained Minecraft 1.7.10 projects (AGENTS.md §2–§3).

## When to use

- Bootstrap, classloading, transformers, Mixins, Java compat, rendering,
  networking, registries, lifecycle, config, build toolchain, bug fixes.

## Primary references

RetroFuturaGradle, RetroFuturaBootstrap, lwjgl3ify, UniMixins, Hodgepodge,
GTNHLib, GTNHGradle, Angelica, ArchaicFix, CoreTweaks, FalseTweaks,
modern Forge / NeoForge / Fabric / Sponge / Mixin. See `UPSTREAMS.md`.
Do not restrict research to this list when relevant alternatives exist.

## Workflow

1. Define the problem and owning layer (vanilla → patch → Forge → FML →
   bootstrap → transformer → API → mod, AGENTS.md §7).
2. Search each candidate repo (code, issues, commits, docs).
3. For each candidate record: repo URL, commit/tag inspected, license,
   architecture, maturity, activity, known bugs, Java compatibility,
   integration cost.
4. Compare candidates in a table; recommend one of: use directly, port,
   cherry-pick/adapt, architecture-only, or build our own — with reasons.
5. Flag license risks (NOASSERTION, module exceptions, occlusion exclusions;
   see `UPSTREAMS.md` compatibility notes).

## Output

A comparison table + recommendation + exact revisions inspected. Never copy
code in this skill — hand off to `source-attribution-license` and the
implementing skill once a direction is chosen.

## Pitfalls

- Copying modern behavior blindly into 1.7.10 without understanding
  surrounding assumptions.
- Treating a README mention as license compliance.
- Choosing the first implementation found instead of comparing.
