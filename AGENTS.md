# Forge 1.7.10 Modernization Project

## Project Mission

This repository is a modernization and reconstruction of the Minecraft Forge 1.7.10 ecosystem.

The goal is **NOT** to preserve the original Forge 1.7.10 implementation exactly.

The goal is to preserve the Minecraft 1.7.10 gameplay/content ecosystem while replacing obsolete infrastructure with cleaner, safer, faster, and more maintainable implementations.

Breaking changes are explicitly allowed.

Mods will also be rebuilt, migrated, or refactored to target the new platform.

Do not preserve a bad API, implementation, bootstrap mechanism, transformer, or compatibility hack solely because Forge 1.7.10 originally used it.

Prefer a well-designed modern replacement when the migration cost is reasonable.

The canonical migration pipeline is:

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

There is explicitly NO requirement that the new Forge must run old mods
without recompilation. Prioritize save/world and gameplay-behavior
compatibility where possible, not legacy binary ABI compatibility.

---

# Core Principles

## 1. Modernize, Do Not Merely Patch

Treat the original Forge 1.7.10 codebase as:

- a behavioral reference;
- a compatibility reference where useful;
- a source of required game/modding functionality;
- historical implementation context.

Do NOT treat its architecture as immutable.

It is acceptable to redesign or replace:

- FML internals;
- Forge internals;
- bootstrap logic;
- class loading;
- registries;
- networking;
- event infrastructure;
- sided execution;
- configuration systems;
- rendering infrastructure;
- transformation systems;
- build tooling;
- dependency handling;
- lifecycle handling;
- mod discovery;
- metadata handling;
- Java compatibility layers;
- deprecated APIs.

Breaking changes must be intentional and documented.

---

# 2. Existing Open-Source Work Comes First

Before implementing a substantial fix, compatibility layer, Java modernization feature, rendering change, bootstrap system, classloading fix, transformer fix, or build-system feature, search existing maintained Minecraft 1.7.10 projects.

Do not reinvent a solved problem without first evaluating existing implementations.

Important upstream projects include, but are not limited to:

- GTNewHorizons/RetroFuturaGradle
- GTNewHorizons/RetroFuturaBootstrap
- GTNewHorizons/lwjgl3ify
- LegacyModdingMC/UniMixins
- GTNewHorizons/Hodgepodge
- GTNewHorizons/GTNHLib
- GTNewHorizons/GTNHGradle
- GTNewHorizons/Angelica
- embeddedt/ArchaicFix
- makamys/CoreTweaks
- FalsePattern/FalseTweaks
- MinecraftForge/Forge
- ForgeGradle
- relevant LegacyModdingMC projects
- relevant maintained GTNewHorizons forks

Other projects may be researched when relevant.

Do not restrict research only to this list.

For architectural ideas, modern Forge, NeoForge, Fabric, Sponge, Mixin, modern Gradle plugins, or other JVM projects may also be consulted.

However, never copy modern behavior blindly into 1.7.10 without understanding the surrounding assumptions.

---

# 3. Upstream Research Workflow

Before implementing substantial infrastructure, determine:

1. Has this problem already been solved?
2. Is there an actively maintained implementation?
3. What repository contains it?
4. What exact version/commit was inspected?
5. What license applies?
6. Can code legally and technically be reused?
7. Should we:
   - use the dependency directly;
   - port the implementation;
   - cherry-pick/adapt the implementation;
   - reproduce only the architecture;
   - or implement our own solution?

Prefer proven implementations over speculative rewrites.

When multiple implementations exist, compare them before choosing.

Evaluate:

- maintainability;
- correctness;
- Java compatibility;
- integration complexity;
- performance;
- licensing;
- upstream activity;
- known bugs;
- interaction with the rest of this project.

---

# 4. Attribution and Licensing Are Mandatory

Never copy source code from another repository without checking its license first.

Every incorporated or substantially adapted upstream implementation must be traceable.

Maintain:

- `README.md`
- `UPSTREAMS.md`
- `THIRD_PARTY_LICENSES/`

when present or create them when the project reaches the stage where external code is incorporated.

For every substantial upstream incorporation, record:

- project name;
- repository URL;
- author or organization;
- license;
- exact commit, tag, or version inspected;
- files/components incorporated;
- whether the implementation was copied, ported, adapted, or merely used as architectural reference;
- local changes;
- required attribution;
- relevant copyright notices.

Preserve required license headers and copyright notices.

Do not assume that mentioning a project in README is sufficient license compliance.

Do not copy code when licensing is unclear or incompatible.

When only an idea or architecture is used, document the relationship accurately rather than claiming code was copied.

Example entry:

```text
Project: RetroFuturaBootstrap
Repository: https://github.com/GTNewHorizons/RetroFuturaBootstrap
Revision: <commit/tag>
Usage: Architecture reference / adapted implementation
Affected subsystem: bootstrap and classloading
Local changes: <description>
License: <verified license>
```

---

# 5. Breaking Changes Are Allowed

Binary compatibility with existing Forge 1.7.10 mods is NOT a primary requirement.

Old mod binaries do not need to continue working unchanged.

Mods are expected to be migrated and rebuilt.

Therefore:

DO NOT:

- keep obsolete APIs only because old mods use them;
- maintain unnecessary reflection hacks;
- duplicate legacy behavior purely for binary compatibility;
- keep broken lifecycle behavior for historical reasons;
- preserve LaunchWrapper assumptions unnecessarily;
- preserve unsafe APIs solely because a mod compiled in 2014 expects them.

Instead:

1. design the improved API;
2. document the breaking change;
3. identify affected mods;
4. migrate those mods;
5. remove the obsolete implementation when no longer needed.

---

# 6. Breaking Change Documentation

Every meaningful breaking API change must include enough information to migrate callers.

Document:

- old API;
- new API;
- reason for the change;
- migration path;
- behavioral differences;
- removed assumptions.

Use an appropriate migration document when one exists.

Prefer:

```text
docs/migrations/
```

or:

```text
MIGRATION.md
```

Do not create hundreds of trivial migration documents.

Group related changes logically.

---

# 7. Minecraft Behavior vs Forge Architecture

Always identify which layer owns a behavior before changing it.

Possible layers include:

```text
Minecraft vanilla
↓
Minecraft patches
↓
Forge
↓
FML
↓
Bootstrap / classloader
↓
Transformer / Mixin
↓
Mod API
↓
Individual mods
```

Before implementing a fix, determine where the problem actually belongs.

Do not fix a mod bug inside Forge unless there is a strong platform-level reason.

Do not fix a Forge architectural problem separately in every mod.

When a fix belongs in the platform, implement it centrally.

---

# 8. Prefer Structural Fixes Over Compatibility Hacks

When possible, prefer:

1. correct source-level implementation;
2. clean platform API;
3. Forge/Minecraft patch;
4. Mixin;
5. bytecode transformer;
6. reflection hack.

This is a preference, not an absolute law.

Use lower-level mechanisms when technically justified.

Avoid accumulating new coremods and transformers when the underlying platform can simply be fixed.

---

# 9. Java Modernization

The project is intended to run on modern Java.

Do not assume Java 8 behavior is desirable.

When modernizing Java support, explicitly investigate:

- removed JDK APIs;
- module encapsulation;
- reflection restrictions;
- `Unsafe`;
- classloader changes;
- `URLClassLoader` assumptions;
- `SecurityManager`;
- old TLS behavior;
- old ASM versions;
- classfile versions;
- final/static mutation;
- native libraries;
- old Apache libraries;
- Guava compatibility;
- logging libraries;
- JVM arguments;
- `--add-opens`;
- serialization assumptions.

Use projects such as:

- RetroFuturaBootstrap;
- lwjgl3ify;
- Hodgepodge;
- RetroFuturaGradle;

as important references before inventing new Java compatibility mechanisms.

Do not solve modern-Java problems by adding permanent JVM hacks if the underlying implementation can reasonably be corrected.

---

# 10. Bootstrap and Classloading

Classloading is a critical subsystem.

Treat changes involving:

- LaunchWrapper;
- LaunchClassLoader;
- tweakers;
- coremods;
- `IFMLLoadingPlugin`;
- transformers;
- early loading;
- late loading;
- classpath discovery;
- transformer exclusions;
- classloader exclusions;

as high-risk changes.

RetroFuturaBootstrap must be evaluated before implementing a new bootstrap or LaunchWrapper replacement.

Do not build a second competing bootstrap system without a clear technical reason.

Changes to bootstrap/classloading must be tested with:

- client startup;
- dedicated server startup;
- transformed classes;
- normal mods;
- Mixins;
- early-loading components;
- development environment;
- packaged runtime.

---

# 11. Build Toolchain

Prefer maintained tooling over the original abandoned ForgeGradle stack where practical.

RetroFuturaGradle is a primary reference for the build system.

Before creating custom Gradle logic, determine whether RFG or related tooling already solves the problem.

The build must remain reproducible.

Do not rely on:

- developer-specific absolute paths;
- manually modified caches;
- undocumented local Maven repositories;
- hidden IDE state;
- files outside the repository without documentation.

Generated artifacts must not become undocumented sources of truth.

---

# 12. MCP, SRG, and Mappings

Always understand which namespace code is currently using.

Be aware of:

```text
Notch / obfuscated names
SRG names
MCP names
development mappings
runtime mappings
reobfuscation
deobfuscation
```

Do not blindly rename mapped symbols.

When changing mapping-related infrastructure, verify:

- development compilation;
- runtime transformation;
- packaged jars;
- reobfuscation;
- reflection targets;
- Mixins;
- Access Transformers;
- transformers.

Mapping problems can appear only after packaging.

Development success alone is not sufficient validation.

---

# 13. Source and Generated Code

Before editing a file, determine whether it is:

- hand-written source;
- generated source;
- decompiled Minecraft source;
- generated patched source;
- upstream source;
- build output.

Do not directly modify generated output when the change belongs in:

- a patch;
- generator;
- mapping;
- Gradle task;
- source template;
- upstream integration.

If the repository uses patches as the canonical representation, modify the patch workflow rather than treating generated sources as permanent source files.

---

# 14. Forge/FML Refactoring

Forge and FML internals may be redesigned.

Potential modernization targets include:

- mod discovery;
- dependency resolution;
- loading lifecycle;
- event dispatch;
- registries;
- sided execution;
- networking;
- configuration;
- metadata;
- bootstrap;
- classloading;
- transforms;
- error reporting;
- logging;
- resource handling.

Do not perform a huge rewrite solely for aesthetic reasons.

Every major redesign must solve a concrete problem:

- obsolete implementation;
- modern Java incompatibility;
- maintainability;
- correctness;
- performance;
- extensibility;
- removal of legacy hacks.

---

# 15. Mod Migration Is Part of the Project

When Forge APIs change, mods may be updated.

The correct solution can therefore include coordinated changes across:

```text
Forge
+
FML
+
shared libraries
+
mods
```

Do not artificially constrain the platform because a mod can be migrated.

When modifying an API, search for known callers before finalizing the design.

When migrated mods are present in the workspace, update affected callers together when practical.

---

# 16. Mod Migration Analysis

Before refactoring a legacy mod, identify:

- `@Mod` entrypoint;
- lifecycle handlers;
- dependencies;
- optional dependencies;
- `@SidedProxy`;
- registries;
- networking;
- GUI handlers;
- configuration;
- world generation;
- events;
- rendering hooks;
- ASM transformers;
- coremods;
- Access Transformers;
- reflection;
- direct Minecraft internals;
- direct LWJGL calls;
- direct Forge internals;
- save/NBT formats;
- cross-mod integrations.

Do not begin a large mod migration until these dependencies are understood.

---

# 17. Mixins and Transformers

Prefer maintainable transformations.

UniMixins and modern Mixin infrastructure should be evaluated before creating additional legacy ASM transformers.

When working with Mixins:

- verify targets;
- verify mappings;
- verify injection stability;
- avoid overly broad injections;
- avoid unnecessary `@Overwrite`;
- document fragile targets;
- verify interactions with other Mixins.

When ASM is necessary:

- understand the bytecode;
- verify stack/frames;
- verify classfile compatibility;
- verify runtime ordering;
- test transformed output.

Never perform bytecode transformations based solely on guessed instruction sequences.

---

# 18. Rendering and LWJGL

Do not assume LWJGL2 is permanently required.

Use the work from lwjgl3ify and Angelica as major references.

Rendering modernization may include:

- LWJGL3;
- GLFW;
- modern OpenGL context management;
- fixed-function emulation;
- display list replacement/emulation;
- input modernization;
- window lifecycle;
- monitor handling;
- OpenAL modernization.

Rendering changes must be benchmarked and visually validated.

Do not replace rendering architecture solely because newer APIs exist.

Preserve Minecraft 1.7.10's intended visual behavior unless a deliberate change is documented.

---

# 19. GTNH and Community Fix Integration

The GTNH ecosystem contains years of 1.7.10 bug fixes.

When fixing an old Minecraft/Forge issue, investigate relevant code in:

- Hodgepodge;
- lwjgl3ify;
- Angelica;
- GTNHLib;
- RetroFuturaBootstrap;
- RetroFuturaGradle;
- relevant GTNH mod forks.

Also investigate:

- ArchaicFix;
- CoreTweaks;
- FalseTweaks;
- other maintained 1.7.10 optimization/fix projects.

When a fix is generally useful and belongs in the platform, consider integrating it directly rather than requiring a separate fix mod.

Do not indiscriminately merge every optimization or fix.

Understand:

- original bug;
- upstream implementation;
- side effects;
- configuration assumptions;
- compatibility assumptions;
- licensing;
- whether the fix still applies after our architectural changes.

---

# 20. Save Data

Breaking changes are allowed, but silent world corruption is not.

Changes involving:

- NBT;
- chunks;
- entities;
- TileEntities;
- player data;
- inventories;
- dimensions;
- registries;
- IDs;
- world metadata;

must be treated carefully.

If an old format is intentionally no longer supported:

- detect it where practical;
- fail clearly or migrate it;
- document the incompatibility.

Never silently interpret incompatible persistent data as if it were valid.

---

# 21. Networking

Networking APIs may be redesigned.

When changing networking, consider:

- packet registration;
- packet IDs;
- serialization;
- Netty usage;
- main-thread dispatch;
- client/server side separation;
- malformed packets;
- size limits;
- authentication/state assumptions.

Old client/new server compatibility is not mandatory unless specifically requested.

Correctness and maintainability take priority over legacy protocol compatibility.

---

# 22. Performance

Do not claim an optimization without evidence.

Performance changes require:

```text
baseline
→ change
→ measurement
→ comparison
```

Relevant areas include:

- startup;
- classloading;
- chunk loading;
- chunk generation;
- lighting;
- entity ticking;
- TileEntity ticking;
- rendering;
- texture handling;
- networking;
- allocation rate;
- garbage collection;
- memory leaks.

Prefer profiler evidence over intuition.

Do not trade correctness for tiny speculative improvements.

---

# 23. Concurrency

Legacy Minecraft code frequently assumes single-threaded execution.

Do not make systems asynchronous or multithreaded merely because modern hardware has more cores.

Before introducing concurrency, identify:

- ownership;
- mutation points;
- thread-affinity assumptions;
- game-thread requirements;
- rendering-thread requirements;
- lifecycle constraints;
- race conditions;
- shutdown behavior.

Concurrency changes require targeted tests.

---

# 24. Error Handling

Modernization should improve diagnostics.

Prefer errors that identify:

- failing subsystem;
- failing mod;
- failing transformer;
- relevant class;
- dependency;
- expected state;
- actual state.

Do not swallow exceptions.

Do not replace meaningful errors with generic catch-all messages.

Do not use empty `catch` blocks.

---

# 25. Dependency Policy

Before adding a dependency:

1. determine whether it is already available;
2. determine whether an existing platform dependency solves the problem;
3. check maintenance status;
4. check license;
5. check Java compatibility;
6. check size/runtime impact.

Avoid dependency duplication.

Avoid shading libraries unnecessarily.

Do not add a library merely to avoid implementing a trivial utility.

---

# 26. Refactoring Rules

Large refactors are allowed when technically justified.

However:

- understand behavior first;
- isolate changes;
- keep commits/tasks conceptually coherent;
- avoid unrelated formatting changes;
- avoid mass renames during functional changes unless necessary;
- preserve tests or improve them;
- document architectural decisions.

Do not rewrite a subsystem simply because the existing style is old.

Rewrite it when the architecture itself is a problem.

---

# 27. Investigation Before Modification

For non-trivial changes:

1. locate relevant source;
2. identify ownership layer;
3. inspect callers;
4. inspect tests;
5. inspect patches;
6. inspect upstream implementations;
7. understand runtime behavior;
8. then design the change.

Never make a deep Forge change based on one isolated file.

---

# 28. Agent Delegation

For complex tasks, the primary agent should act as an orchestrator.

Use available subagents when appropriate.

Typical responsibilities:

```text
explorer
    local codebase investigation

researcher
    upstream projects, documentation and GitHub research

architect
    subsystem redesign and decomposition

implementer
    scoped implementation

tester
    build/runtime/regression validation

reviewer
    independent code review
```

Parallelize independent research and implementation work.

Do not serialize independent tasks unnecessarily.

---

# 29. Parallel Implementation Safety

Before launching multiple implementers, assign explicit ownership.

Example:

```text
Implementer A
owns:
src/main/java/.../loader/**

Implementer B
owns:
src/main/java/.../network/**

Implementer C
owns:
buildSrc/**
```

Two parallel agents must not edit the same file unless coordination is explicitly planned.

Shared integration files should generally be handled by the orchestrator after parallel work completes.

---

# 30. Delegation Contract

Every delegated implementation task should contain:

```text
OBJECTIVE
CONTEXT
OWNED FILES
DO NOT MODIFY
EXPECTED RESULT
VALIDATION
DEPENDENCIES
```

Do not send vague tasks such as:

```text
modernize Forge
```

Prefer:

```text
Replace the legacy classpath discovery component with the selected
RetroFuturaBootstrap-compatible implementation.

Own:
src/.../bootstrap/**

Do not modify:
networking or registry packages.

Validate:
client startup
server startup
transformer discovery
```

---

# 31. Upstream Research Through Subagents

For substantial changes, the researcher should search relevant upstream implementations in parallel when useful.

Example:

```text
Researcher A
RetroFuturaBootstrap implementation

Researcher B
lwjgl3ify handling of the same Java issue

Researcher C
Hodgepodge/ArchaicFix handling of affected mods
```

The orchestrator must compare results.

Do not automatically choose the first implementation found.

---

# 32. Testing Strategy

A change is not complete because it compiles.

Use the relevant validation layers.

At minimum, consider:

```text
compile
unit tests
integration tests
client startup
dedicated server startup
development runtime
packaged runtime
mod loading
world creation
world loading
shutdown/restart
```

For transformation/mapping changes, also test packaged/reobfuscated behavior.

For mod API changes, test at least one migrated consumer where practical.

---

# 33. Regression Testing

When a bug is fixed:

1. reproduce the original failure if possible;
2. implement the fix;
3. add a regression test or deterministic validation when practical;
4. verify the original failure no longer occurs.

Do not delete failing tests merely to make the build green.

---

# 34. Client and Dedicated Server

Do not assume client success means server success.

Forge changes must account for both sides.

Avoid accidental client-only references in common/server code.

Dedicated server validation is required for changes involving:

- lifecycle;
- registries;
- networking;
- classloading;
- world logic;
- bootstrap;
- dependency resolution.

---

# 35. Do Not Guess Build Commands

Use the build files in the repository as the source of truth.

Before running commands, inspect:

- Gradle wrapper;
- build scripts;
- settings;
- buildSrc;
- CI configuration;
- documented developer instructions.

Do not assume legacy ForgeGradle commands when the repository has already migrated to newer tooling.

---

# 36. Generated and Downloaded Dependencies

Do not commit build caches or downloaded artifacts unless intentionally vendored.

Do not edit artifacts inside Gradle caches.

Do not solve dependency issues by manually changing files in:

```text
.gradle/
~/.gradle/
IDE caches
temporary extraction directories
```

Fix the build configuration instead.

---

# 37. Security

Do not expose:

- tokens;
- credentials;
- signing keys;
- private repository credentials;
- local machine secrets.

Do not commit `.env` secrets.

Do not print secrets in logs.

Treat dependency downloads and executable build tooling carefully.

---

# 38. Git Safety

Do not automatically run:

```text
git push
git reset --hard
git clean -fd
git clean -fdx
git rebase
git filter-branch
git filter-repo
```

unless explicitly requested and understood.

Do not overwrite unrelated user changes.

Do not automatically commit unless requested.

Before destructive filesystem operations, verify exactly what will be affected.

---

# 39. Scope Discipline

Do not mix unrelated modernization work into the current task.

If another problem is discovered:

- record it;
- explain it;
- optionally create a follow-up task;
- do not silently expand the implementation.

Exception:

A blocking architectural problem may be addressed when necessary to complete the requested work.

---

# 40. Code Quality

New code should favor:

- explicit ownership;
- clear APIs;
- understandable names;
- testability;
- predictable lifecycle;
- minimal hidden global state;
- maintainable abstractions.

Avoid architecture astronautics.

Do not create abstractions until there is a real reason for them.

Prefer deleting obsolete compatibility layers over wrapping them indefinitely.

---

# 41. Legacy Code Removal

Before deleting legacy code, determine:

- what used it;
- whether migrated replacements exist;
- whether runtime behavior changes;
- whether configuration/data migration is needed.

Delete dead compatibility code aggressively once its replacement is established.

Do not maintain both old and new systems indefinitely without a migration reason.

---

# 42. Documentation Must Follow Architecture

When architecture changes, update relevant documentation in the same work.

Documentation may include:

- README;
- migration guides;
- upstream credits;
- architecture notes;
- build instructions;
- API usage;
- mod migration examples.

Do not leave documentation describing removed systems.

---

# 43. Completion Criteria

A substantial task is not complete until:

- the requested behavior exists;
- architecture is internally coherent;
- relevant callers are updated;
- affected migrated mods are considered;
- upstream attribution is recorded when needed;
- license obligations are respected;
- the project builds;
- relevant tests pass;
- relevant runtime validation succeeds;
- no known blocking regression remains;
- documentation is updated when behavior/API changed.

---

# 44. Final Agent Report

For substantial completed work, report:

## Changed

What was implemented.

## Architecture

Important design decisions.

## Upstream Work

Repositories and revisions used or studied.

Clearly distinguish:

- copied;
- adapted;
- ported;
- inspired by;
- used only as reference.

## Breaking Changes

APIs or behavior intentionally changed.

## Migration Impact

Mods/components that must be updated.

## Validation

Commands and runtime tests performed.

## Remaining Risks

Anything not fully validated.

---

# Project Philosophy

This project is not an attempt to freeze Minecraft modding in 2014.

It is an attempt to build a modern, maintainable foundation around the Minecraft 1.7.10 ecosystem.

Preserve what makes 1.7.10 valuable.

Replace what makes it unnecessarily obsolete.