# Screenshot testing

Screenshot coverage spans Android mobile, Android TV, Desktop, the iOS simulator, JS, and Wasm.
CI host verification compares committed Android, Android TV, and Desktop goldens. Library iOS
simulator suites are enrolled for recording, but `verifyHostScreenshots` only wires module iOS
verify tasks once `screenshots/iosSimulatorArm64` baselines are committed.

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
total. They must remain ordinary Git blobs and must never use Git LFS.

Any user-visible component or style change updates or adds its focused screenshot scene in the
same pull request.
