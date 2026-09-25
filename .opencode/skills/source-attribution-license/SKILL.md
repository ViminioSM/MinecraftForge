---
name: source-attribution-license
description: >
  Maintain mandatory traceability for reused upstream work: project, URL,
  commit/tag, files used, authors, license, local changes and credits.
  Keeps UPSTREAMS.md and THIRD_PARTY_LICENSES/ compliant. Trigger: any
  copy, port, adaptation or substantial idea reuse.
---

# Source Attribution & License

## Purpose

Make every incorporation traceable and license-compliant (AGENTS.md §4).
A README mention alone is never sufficient.

## When to use

Whenever code or a substantial implementation idea is reused from any
upstream repository — copied, ported, adapted, or used as architecture.

## Workflow

1. Identify the upstream repository and verify its license FIRST
   (LICENSE path, branch, SPDX, per-module exceptions).
2. Record the exact commit/tag inspected.
3. Preserve required copyright headers and notice files (`CREDITS`,
   `README.original.md`, per-module `LICENSE`).
4. Classify the reuse honestly: copied / ported / adapted / inspired by /
   reference only.
5. Update `UPSTREAMS.md` (project, URL, revision, usage, subsystem,
   local changes, license, credits) using the template there.
6. Update `THIRD_PARTY_LICENSES/`: replace the matching placeholder with
   the full license text + notices when code is vendored.
7. Document local modifications precisely.

## Special cases (see UPSTREAMS.md)

- Angelica: SPDX NOASSERTION — manual attribution required.
- UniMixins: Unlicense base + 3 LGPL modules — audit per `module-*`.
- ArchaicFix / FalseTweaks `occlusion`: excluded provenance — isolate,
  do not vendor without due diligence.
- FalseTweaks `mcpatche` assets: CC-BY-SA-4.0, not LGPL.

## Pitfalls

- Copying before checking the license.
- Recording only "Unlicense" for UniMixins or only "LGPL" for ArchaicFix.
- Claiming "copied" when only the architecture was used, or vice versa.
