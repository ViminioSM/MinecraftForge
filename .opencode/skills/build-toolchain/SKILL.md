---
name: build-toolchain
description: >
  Build on maintained tooling (RetroFuturaGradle, GTNHGradle concepts) instead
  of the abandoned ForgeGradle stack. Keeps builds reproducible. Trigger:
  Gradle, mappings, reobfuscation, workspace or publishing work.
---

# Build Toolchain

## Purpose

Prefer maintained tooling over legacy ForgeGradle where practical; RFG is the
primary reference (AGENTS.md §11). GTNHGradle centralizes 1.7.10 mod build
logic (RFG application, publishing, modern-Java utilities) — mine it for
concepts before writing custom Gradle logic.

## Rules

- Source of truth is the repo's build files (wrapper, scripts, settings,
  buildSrc, CI, developer docs) — never guess commands (AGENTS.md §35).
- Keep builds reproducible: no absolute dev paths, hand-edited caches,
  undocumented local Mavens, hidden IDE state, or out-of-repo files.
- Generated artifacts are never undocumented sources of truth.
- Never edit Gradle caches to fix dependency issues — fix the build config.
- Do not commit caches or downloaded artifacts unless intentionally vendored.

## Workflow

1. Inspect current toolchain before proposing changes.
2. `upstream-project-research`: RFG vs GTNHGradle vs forks for the problem
   (workspace gen, mappings, reobfuscation, publishing).
3. Change build logic minimally and document developer impact.

## Validation

Clean setup from scratch (documented steps only) + dev compile + packaged /
reobfuscated output + mapping-sensitive paths (reflection, Mixins, ATs).
