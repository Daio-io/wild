# Screenshot testing

Screenshot coverage spans Android mobile, Android TV, Desktop, the iOS simulator, JS, and Wasm.
The exact host task enrollment lives in
[build.gradle.kts](https://github.com/Daio-io/wild/blob/main/build.gradle.kts), under
`verifyHostScreenshots`. It includes Android, Android TV, Desktop, and the internal
iOS simulator harness. Library iOS test adapters exist, but their verify tasks are
not enrolled in the aggregate while their iOS baselines are absent. When adding
library iOS baselines, enroll the corresponding verify task explicitly in that list.

## Find the suite and baseline

| Scope | Scene/test source | Committed baseline |
| --- | --- | --- |
| Library components, content color, layouts | `<module>/src/commonTest/` scenes; `androidUnitTest`, `jvmTest`, and `iosTest` screenshot adapters | `<module>/screenshots/android/`, `desktop/`; iOS recording writes `iosSimulatorArm64/` |
| Shared host harness | `internal/screenshot-tests/src/{androidMain,jvmMain,iosMain}/` | No committed goldens here; consumers capture in their own module |
| Android playbook | `playbook/android/src/test/java/` | `playbook/android/screenshots/debug/` |
| Android TV playbook | `playbook/androidTv/src/test/java/` | `playbook/androidTv/screenshots/debug/` |
| Desktop playbook | `playbook/desktop/src/jvmTest/` | `playbook/desktop/screenshots/jvm/` |
| Web JS and Wasm | `playbook/web/screenshot-tests/tests/` | `playbook/web/screenshot-tests/screenshots/{js,wasm}/` |

Find existing cases before adding or recording a scene:

```bash
rg --files components/button/src | rg 'Screenshot'
rg --files components/button/screenshots
```

Library JVM adapters normally use `screenshots/desktop`; named playbook captures
use `screenshots/jvm`. The shared harness defines these paths relative to the
consuming module, so a JVM Gradle task does not imply a `jvm` baseline directory.

Host task output defaults and Android screenshot test filtering are configured in
[RoborazziConventionPlugin.kt](https://github.com/Daio-io/wild/blob/main/gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/RoborazziConventionPlugin.kt).
Explicit capture paths are defined in the
[shared harness](https://github.com/Daio-io/wild/tree/main/internal/screenshot-tests/src).
Web snapshot paths and comparison options are configured in
[playwright.config.ts](https://github.com/Daio-io/wild/blob/main/playbook/web/screenshot-tests/playwright.config.ts).
The screenshot job and uploaded failure-report paths are in
[.github/workflows/build.yml](https://github.com/Daio-io/wild/blob/main/.github/workflows/build.yml).

The aggregate host task list and the asset validator have separate scopes. The
validator discovers every directory named `screenshots` in the checkout, excluding
`.git` and build output. It therefore includes library and web assets as well as
internal and playbook assets, regardless of whether a host verify task is enrolled.

## Verify

From a clean checkout, install the web test dependencies and browser, then run every screenshot
verification suite and the asset policy check:

```bash
./gradlew verifyHostScreenshots :playbook:web:prepareScreenshotDistributions
npm ci --prefix playbook/web/screenshot-tests
npx --prefix playbook/web/screenshot-tests playwright install chromium
npm --prefix playbook/web/screenshot-tests test
./scripts/verify-screenshot-assets.sh
```

Verification reads committed goldens. It does not record or update screenshots. Actual and diff
reports are written to ignored build or Playwright report directories when a comparison fails.

## Record

For host screenshots, run the scoped `recordRoborazzi<Target>` task for the target being changed.
Use the same module and target for subsequent verification. For a JVM button change:

```bash
./gradlew :components:button:recordRoborazziJvm
./gradlew :components:button:verifyRoborazziJvm
```

To inspect enrollment without comparing or updating images, use
`./gradlew verifyHostScreenshots --dry-run`.

For example, use `:playbook:desktop:recordRoborazziJvm` for the Desktop playbook,
`:playbook:android:recordRoborazziDebug` for the Android playbook, or
`:components:button:recordRoborazziIosSimulatorArm64` for an iOS library suite.

For web screenshots, update only the focused case:

```bash
npm --prefix playbook/web/screenshot-tests run test:update -- <case filter>
```

## Review

Inspect only the intended reference PNGs and the generated actual/diff report. Keep viewport and
state fixed; do not use network access, randomness, or a live clock. Give each scene one named
case.

Screenshot assets must be PNG or WebP, no larger than 500 KiB each and no larger than 20 MiB in
total. They must remain ordinary Git blobs and must never use Git LFS. Limits and discovery
are implemented in
[scripts/verify-screenshot-assets.sh](https://github.com/Daio-io/wild/blob/main/scripts/verify-screenshot-assets.sh).
The validator accepts explicit directories for a focused check:

```bash
./scripts/verify-screenshot-assets.sh components/button/screenshots
```

The aggregate size in a focused check covers only the supplied roots. Run the
validator without arguments before submitting the PR to check the entire checkout.

When changing the asset policy itself, run its focused regression suite:

```bash
./scripts/verify-screenshot-assets-test.sh
```

That suite checks policy failure paths with temporary fixtures; it is separate from
image comparison and is not a step in the screenshot CI job. Changes to comparison
thresholds, clocks, or viewport/TV qualifiers change the rendering contract. Review
those with their owning suite and its baseline; expanding CI enrollment does not
require changing those settings.

Any user-visible component or style change updates or adds its focused screenshot scene in the
same pull request.
