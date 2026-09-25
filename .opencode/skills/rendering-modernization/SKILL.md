---
name: rendering-modernization
description: >
  Modernize 1.7.10 rendering toward LWJGL3/modern OpenGL using lwjgl3ify and
  Angelica as references: GLFW, contexts, fixed-function emulation, display
  lists, input, window, OpenAL. Trigger: rendering, input, window or audio work.
---

# Rendering Modernization

## Purpose

Evaluate LWJGL3 migration without assuming LWJGL2 must stay (AGENTS.md §18).
Angelica implements an OpenGL 3.3+ core profile with fixed-function
emulation, display lists and deep adaptations; lwjgl3ify is the proven
Java 17+ path. Preserve intended 1.7.10 visual behavior unless a change is
deliberate and documented.

## Scope

LWJGL3, GLFW, context management, fixed-function emulation, display-list
replacement/emulation, input modernization, window lifecycle, monitor
handling, OpenAL modernization.

## Workflow

1. `upstream-project-research`: lwjgl3ify vs Angelica approaches for the
   specific subsystem (compare before choosing).
2. Benchmark and visually validate — no architecture replacement on novelty
   alone.
3. Keep changes measurable (AGENTS.md §22) and side-aware (client-only refs
   must never leak into common/server code, AGENTS.md §34).

## Validation

Visual comparison + benchmarks (startup, frame time, allocation/GC) +
client boot on target JDK + affected mods' rendering paths.
