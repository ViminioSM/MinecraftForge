---
name: mixin-bytecode-modernization
description: >
  Migrate legacy ASM transformers/coremods to Mixin/MixinExtras via UniMixins
  infrastructure where it pays off. Covers target stability, mappings, frames
  and ordering. Trigger: coremod, transformer or injection work.
---

# Mixin & Bytecode Modernization

## Purpose

Prefer maintainable transformations: evaluate UniMixins + modern Mixin before
creating new legacy ASM transformers (AGENTS.md §17). This branch ships
SpongePowered Mixin 0.8.7 in FML (`mcmod.info` `"mixins"`,
`IFMLEarlyMixinLoader`); GTNH `gtnhmixins`/`spongemixins` loader code was
deliberately NOT taken (LGPLv3).

## Mixin rules

- Verify targets, mappings, and injection stability.
- Avoid overly broad injections and unnecessary `@Overwrite`.
- Document fragile targets; verify Mixin↔Mixin interactions.
- Refresh/prepare configs around mod construction (existing FML behavior
  adapted from the `mixinbooterlegacy` idiom — attributed, see README.md).

## ASM rules (when Mixin is insufficient)

- Understand the bytecode; verify stack/frames and classfile compatibility.
- Verify runtime ordering; test transformed output.
- Never transform on guessed instruction sequences.
- Honor mixin-safety markers (FML `IMixinSafeTransformer` + Makamys'
  equivalent by name) and `-Dfml.mixin.excludedTransformers`; rewrite
  foreign shaded ASM refs to shipped ASM 9.6 (see README.md).

## Validation

Transformed-output inspection + client/server boot + affected mods +
packaged/reobfuscated runtime (mappings break late).
