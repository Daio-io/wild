# THE-217 InteractionStyleNode Prototype Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Prototype and measure a unified `InteractionStyleNode` composite as an internal `candidate_composite` path beside `current_traversal`, with parity tests, documented node/layer counts, benchmark comparison, and a written adopt/retain/defer decision — without changing public API.

**Architecture:** Keep existing traversal `Modifier.interactionStyle` as `current_traversal`. Add internal `InteractionStyleElement` / `InteractionStyleNode` (`DelegatingNode`) that owns interaction collection, `StyleResolver` resolution, change-mask invalidation, and direct layout/draw delegates (no style-parent-to-child traversal). Wire only the TV playbook `candidate_composite` benchmark path to the prototype. Reuse existing `StyleResolver`. Preserve THE-219 reset and THE-225 snapshot observation on the block path.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform Modifier.Node / DelegatingNode, existing style module tests + Android TV macrobenchmark harness (THE-222).

**Global Constraints:**
- No public API change; `style/api/api.txt` must remain unchanged unless a separate API decision is recorded.
- Candidate must perform no style-parent-to-child traversal.
- Equal resolved output must perform no phase invalidation.
- Do not switch default `Modifier.interactionStyle` unless measurements + decision say adopt; this ticket ships the prototype + decision.
- Reuse existing `StyleResolver` (`Value` / `Block`); do not duplicate it.
- Copyright header: `// Copyright 2024, Dai Williams` + `// SPDX-License-Identifier: Apache-2.0`
- Conventional commits; no AI attribution trailers.
- Worktree: `/Users/dai/projects/wild/.worktrees/the-217-interaction-style-node`
- Branch: `feat/the-217-interaction-style-node`
- Ticket: https://linear.app/the-good-egg-studio/issue/THE-217/prototype-and-benchmark-a-unified-interactionstylenode-architecture
- Verification: `./gradlew :style:jvmTest :style:spotlessCheck` (plus container/component tests if wired); document TV benchmark commands/results or device unavailability.

---

### Task 1: InteractionStyleNode prototype core

**Files:**
- Create: `style/src/commonMain/kotlin/io/daio/wild/style/modifiers/InteractionStyleElement.kt`
- Create: `style/src/commonTest/kotlin/io.daio.wild.style/modifiers/InteractionStyleNodeTest.kt` (and/or integration tests)
- Possibly modify: existing scale/border/background/shape node logic only if extracting shared helpers is required — prefer composing via `DelegatingNode.delegate()` with direct updates, not traversal

**Steps:**
1. TDD: write failing tests that apply an internal candidate modifier and assert:
   - Style Value path resolves colors/scale/etc. for focus/press/enabled/selected
   - Equal re-resolution does not invalidate draw/placement (instrument via test hooks if needed)
   - Nested candidate styles do not interfere
   - No `traverseDirectDescendants` / StyleScopeChild dispatch path is used by the candidate (structural: candidate has no StyleScopeParentNode children)
2. Implement `InteractionStyleElement(interactionSource, enabled, selected, resolver)` → `InteractionStyleNode : DelegatingNode` owning:
   - interaction collection (can embed InteractionSourceNode behavior or collect directly)
   - StyleScope fields + StyleResolver Value/Block (Block: THE-219 reset + THE-225 observeReads)
   - layout delegate (scale/z-index/alpha/clip/shape as appropriate)
   - draw delegate (background then content then border; preserve inset/focus-ring)
   - change mask → only affected invalidateDraw / invalidatePlacement
3. Expose an internal factory e.g. `internal fun Modifier.interactionStyleComposite(...)` used only by tests/benchmark wiring — do not change public `interactionStyle` yet.
4. Run `./gradlew :style:jvmTest :style:spotlessApply :style:spotlessCheck`
5. Commit: `feat: add InteractionStyleNode composite prototype`

---

### Task 2: Wire candidate_composite + behavior parity

**Files:**
- Modify: `playbook/androidTv/src/main/java/io/daio/android/tv/TvLayout.kt` (`CandidateCompositeItem`)
- Possibly: `layout/container` only if Container must opt into composite for the benchmark path — prefer applying composite modifier from playbook without changing Container public API
- Modify/extend: `style/src/commonTest/...` attached/traversal integration coverage for both variants where feasible
- Modify: playbook TV unit tests if they assert CandidateComposite implementation details

**Steps:**
1. Point `candidate_composite` item rendering at the composite modifier path (still using Style + clickable as needed).
2. Ensure `current_traversal` still uses existing `interactionStyle` chain.
3. Add/adapt tests covering nested styles, initial attach, source replacement, state transitions, equal-output no-op for the candidate.
4. Run `:style:jvmTest`, playbook TV unit tests if present, `:style:spotlessCheck`
5. Commit: `feat: wire candidate_composite to InteractionStyleNode`

---

### Task 3: Counts, benchmarks, written decision

**Files:**
- Create: `docs/superpowers/specs/2026-07-26-the-217-interaction-style-node-decision.md` (or under `docs/` if preferred)
- Possibly update: benchmark README / playbook notes with reproduction commands
- Do not change public API

**Steps:**
1. Document candidate vs current node count, delegate count, and layer count.
2. Run THE-222 harness comparison (`scrollGridWithCurrentTraversal` vs `scrollGridWithCandidateComposite`) if a device/emulator is available; otherwise record exact commands and that hardware was unavailable, plus any micro/local evidence.
3. Write adopt / retain-with-fallback / defer decision with rationale tied to measurements and parity results.
4. Confirm `git diff -- style/api/api.txt` empty.
5. Commit: `docs: record THE-217 InteractionStyleNode prototype decision`

---

### Task 4: Final verification

**Steps:**
1. `./gradlew :style:jvmTest :style:spotlessCheck`
2. Spot-check api.txt unchanged
3. Self-review against acceptance criteria checklist from THE-217
4. Commit any leftover fixes if needed
