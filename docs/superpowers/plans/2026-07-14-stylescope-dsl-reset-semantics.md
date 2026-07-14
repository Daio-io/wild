# StyleScope DSL Reset Semantics Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Give `Modifier.interactionStyle { ... }` deterministic reset-before-resolution semantics so style properties set only under one interaction branch do not leak into later states (Linear THE-219).

**Architecture:** Before each DSL block invocation on `StyleScopeParentNode`, snapshot previous resolved visual properties, reset them to documented defaults (without dispatching children), invoke the block, then dispatch children once only when the final resolved output differs from the snapshot. Interaction/component flags are never reset. THE-225 snapshot observation is out of scope — invoke the block directly until that lands.

**Tech Stack:** Kotlin Multiplatform, Compose Multiplatform Modifier.Node, JUnit/kotlin.test + Compose UI testing (`runComposeUiTest`).

**Linear:** [THE-219](https://linear.app/the-good-egg-studio/issue/THE-219/define-and-enforce-stylescope-dsl-reset-semantics)

**Worktree:** `/Users/dai/projects/wild/.worktrees/the-219-stylescope-dsl-reset` on branch `fix/the-219-stylescope-dsl-reset`

---

## File structure

| File | Responsibility |
|------|----------------|
| `style/.../modifiers/StyleScopeParentElement.kt` | `resetResolvedStyle()`, previous-value snapshot, skip equal dispatch, `onReset()` |
| `style/.../Style.kt` | KDoc on DSL `interactionStyle` overload describing reset semantics |
| `docs/style.md` | Short release note / usage note about reset defaults |
| `style/.../modifiers/StyleTraversalIntegrationTest.kt` | Compose UI tests for sticky-state bugs + equal-output no-dispatch |
| `.gitignore` | Include `.worktrees/` (project convention from this branch) |

Out of scope: THE-225 `observeReads`, THE-217 change-mask/delegate architecture, public API / Metalava signature changes.

---

### Task 1: Failing integration tests for reset semantics

**Files:**
- Modify: `style/src/commonTest/kotlin/io.daio.wild.style/modifiers/StyleTraversalIntegrationTest.kt`
- Modify: `.gitignore` (add `.worktrees/` if missing)

- [ ] **Step 1: Add `.worktrees/` to `.gitignore`** if not already present on this branch.

- [ ] **Step 2: Write failing tests** in `StyleTraversalIntegrationTest` (same `StyleRecorder` helpers):

```kotlin
@Test
fun conditionalFocusedScaleResetsWhenFocusLost() = runComposeUiTest {
    val source = MutableInteractionSource()
    val recorder = StyleRecorder()
    setContent {
        Box(
            Modifier
                .size(1.dp)
                .interactionStyle(source) {
                    if (focused) scale = 1.1f
                }
                .recordStyle(recorder),
        )
    }
    waitForIdle()
    assertEquals(1f, recorder.last.scale)

    lateinit var focus: FocusInteraction.Focus
    runBlocking {
        focus = FocusInteraction.Focus()
        source.emit(focus)
    }
    waitForIdle()
    assertEquals(1.1f, recorder.last.scale)

    runBlocking { source.emit(FocusInteraction.Unfocus(focus)) }
    waitForIdle()
    assertEquals(1f, recorder.last.scale)
}

@Test
fun conditionalPressedColorResetsToUnspecified() = runComposeUiTest { /* color → Color.Unspecified */ }

@Test
fun conditionalAlphaShapeBorderAndAnimationSpecReset() = runComposeUiTest { /* cover all remaining defaults */ }

@Test
fun multiBranchBlockResolvesExpectedFinalBranch() = runComposeUiTest { /* if/else branches */ }

@Test
fun interactionFlagsRemainCurrentWhileOutputsReset() = runComposeUiTest {
    // after focus lost: focused==false, scale==1f, enabled/selected unchanged
}

@Test
fun equalFinalOutputAfterResetDoesNotRedispatch() = runComposeUiTest {
    // block always sets color = Color.Red; emit redundant interaction that leaves output equal
    // assert recorder.snapshots.size does not grow on no-op resolution
}

@Test
fun nestedScopesMaintainIndependentDefaults() = runComposeUiTest {
    // outer sets focused scale; inner omits scale — inner stays 1f independently
}
```

Skip THE-225 snapshot-driven case. For equal-output: trigger a state update that does not change the final style result (e.g. block ignores interaction and always sets the same color; or re-set the same `enabled`/`selected`/block via recomposition without property change) and assert no new snapshot. Prefer interaction toggles that cancel out if possible — if the parent currently re-resolves only on interaction/flag/block change, emit a no-op path carefully. Acceptable approach: after stable styled state, call an update that re-invokes resolution with identical final values (e.g. replace block with an equal-capturing new lambda that sets the same props — may always re-resolve). Better: once focused scale set then unfocused back to defaults, re-apply same unfocused resolution somehow and assert no extra child update — if hard with public APIs, add a test that resolves twice with the same final defaults via focus focus/unfocus where second unfocus is skipped by parent (already). Minimum for equal-dispatch: with a constant block `{ color = Color.Red }`, after idle, emit Hover that parent handles; if hover does not change outputs, ensure either parent skips (if hover updates) or document that skip works when `updateStyle` early-returns after equals. Implement equals check so: focus→unfocus→unfocus-path doesn't apply. Simpler: mutate `selected` from false→false via same values won't call updateStyle. Instead after first attach, force two identical resolutions by focusing then unfocusing where both ends produce default scale and same color — the second focus/unfocus cycle's unfocused end should skip if values equal previous. Count snapshots on second cycle's unfocus.

- [ ] **Step 3: Run tests and confirm failure**

```bash
./gradlew :style:jvmTest --tests "io.daio.wild.style.modifiers.StyleTraversalIntegrationTest.conditionalFocusedScaleResetsWhenFocusLost"
```

Expect failure: scale stays `1.1f` after unfocus.

- [ ] **Step 4: Commit**

```bash
git add .gitignore style/src/commonTest/kotlin/io.daio.wild.style/modifiers/StyleTraversalIntegrationTest.kt
git commit -m "$(cat <<'EOF'
test: add StyleScope DSL reset semantics coverage

EOF
)"
```

---

### Task 2: Implement reset-before-resolution on StyleScopeParentNode

**Files:**
- Modify: `style/src/commonMain/kotlin/io/daio/wild/style/modifiers/StyleScopeParentElement.kt`

- [ ] **Step 1: Implement reset + snapshot + conditional dispatch**

```kotlin
private fun updateStyle() {
    val previousColor = color
    val previousAlpha = alpha
    val previousScale = scale
    val previousShape = shape
    val previousBorder = border
    val previousScaleAnimationSpec = scaleAnimationSpec

    resetResolvedStyle()
    block.invoke(this)

    val changed =
        color != previousColor ||
            alpha != previousAlpha ||
            scale != previousScale ||
            shape != previousShape ||
            border != previousBorder ||
            scaleAnimationSpec != previousScaleAnimationSpec

    if (!changed) return

    traverseDirectDescendants<StyleScopeChildNode>(key = StyleChildTraversalKey) {
        it.updateStyle(this)
    }
}

private fun resetResolvedStyle() {
    color = Color.Unspecified
    alpha = 1f
    scale = 1f
    shape = RectangleShape
    border = BorderDefaults.None
    scaleAnimationSpec = null
}

override fun onReset() {
    resetResolvedStyle()
}
```

Do not reset `_focused` / `_hovered` / `_pressed` / `_selected` / `_enabled`.

**First attach note:** previous values equal defaults after fresh node construction; first `block` that leaves defaults produces `changed == false` and would skip initial child dispatch. Avoid that:

- Option A: track `var hasResolved = false` and always dispatch the first resolution after attach.
- Option B: snapshot only after at least one prior dispatch.
- Prefer Option A:

```kotlin
private var hasDispatchedStyle = false

private fun updateStyle() {
    val previousColor = color
    ...
    resetResolvedStyle()
    block.invoke(this)
    val changed = !hasDispatchedStyle || color != previousColor || ...
    if (!changed) return
    hasDispatchedStyle = true
    traverseDirectDescendants ...
}

override fun onReset() {
    resetResolvedStyle()
    hasDispatchedStyle = false
}
```

- [ ] **Step 2: Run the new tests + full style jvmTest**

```bash
./gradlew :style:jvmTest :style:spotlessApply :style:spotlessCheck
```

- [ ] **Step 3: Commit**

```bash
git commit -m "$(cat <<'EOF'
fix: reset StyleScope DSL properties before each resolution

EOF
)"
```

---

### Task 3: Document semantics (KDoc + docs note)

**Files:**
- Modify: `style/src/commonMain/kotlin/io/daio/wild/style/Style.kt` (DSL `interactionStyle` KDoc; optionally deprecated experimental overload)
- Modify: `docs/style.md`

- [ ] **Step 1: Update KDoc** on `fun Modifier.interactionStyle(..., block: StyleScope.() -> Unit)` stating that each evaluation resets visual properties to defaults (`Color.Unspecified`, `alpha`/`scale` `1f`, `RectangleShape`, no border, null animation spec) before running the block; omit a property → default for that invocation; interaction flags are inputs, not reset outputs.

- [ ] **Step 2: Add a short note under Modifiers in `docs/style.md`** describing the corrected behavior for partial DSL blocks (release-note style).

- [ ] **Step 3: Verify API text unchanged**

```bash
./gradlew :style:metalavaCheck or equivalent if present; otherwise confirm style/api/api.txt untouched
./gradlew :style:jvmTest :style:spotlessCheck
```

- [ ] **Step 4: Commit**

```bash
git commit -m "$(cat <<'EOF'
docs: document StyleScope DSL reset-before-resolution semantics

EOF
)"
```

---

### Task 4: Full verification

- [ ] Run `./gradlew :style:jvmTest :style:spotlessCheck`
- [ ] Confirm `style/api/api.txt` unchanged
- [ ] Self-check acceptance criteria from THE-219
- [ ] No commit required unless fixes needed

---

## Acceptance criteria (from THE-219)

- No resolved visual property persists merely because a later block invocation omitted it.
- Reset is not externally observable as an intermediate draw/layout state.
- Children are updated at most once per resolution.
- KDoc and docs note describe the semantics.
- `Style` object overload remains unchanged visually.
- Public API signature / API snapshot remains unchanged.
