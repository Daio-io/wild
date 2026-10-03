# Contributing

## Find the owning module

Use [settings.gradle.kts](https://github.com/Daio-io/wild/blob/main/settings.gradle.kts)
for the complete module list and Gradle paths. Components live in `components/`,
layouts in `layout/`, and A2UI has separate protocol (`a2ui`), rendering
(`a2ui-compose`), and Wild catalog (`a2ui-catalog`) modules. Each module's
`build.gradle.kts` defines its dependencies and publishing configuration.

Dependency and plugin versions come from
[gradle/libs.versions.toml](https://github.com/Daio-io/wild/blob/main/gradle/libs.versions.toml).
Build conventions live in
[gradle/build-logic/convention/src/main/kotlin/io/daio/gradle/](https://github.com/Daio-io/wild/tree/main/gradle/build-logic/convention/src/main/kotlin/io/daio/gradle).

From the checkout root, search the owning module before widening the search:

```bash
rg --files a2ui-catalog/src
rg -n 'A2uiGalleryFixtures|wildA2uiBasicCatalogV1' a2ui-catalog/src
```

Historical implementation plans are under `docs/plans/archive/` and
`docs/superpowers/`. Compare them with the current source and the task's acceptance
criteria before using their file lists or platform assumptions.

## Change a playbook gallery page

The shared site UI lives in
[playbook/web/src/commonMain/kotlin/io/daio/wild/site/](https://github.com/Daio-io/wild/tree/main/playbook/web/src/commonMain/kotlin/io/daio/wild/site):

| Concern | File relative to the site directory |
| --- | --- |
| Page data and demo pattern | `pages/components/ProgressPage.kt`, `pages/components/A2uiPage.kt` |
| Page wrapper and data model | `components/ComponentPage.kt`, `components/ComponentPageData.kt` |
| Route definitions | `navigation/Routes.kt` |
| Sidebar entries | `navigation/Sidebar.kt` |
| Route-to-page dispatch | `SiteApp.kt` |

Follow the existing `<Page>Defaults.data` pattern and delegate the page wrapper to
`ComponentPage(modifier = modifier, data = data)`. Demo composition state belongs
inside the demo lambda. Set platform metadata from the page's actual support and
acceptance criteria; sibling metadata alone does not establish support.

Add demo dependencies in
[playbook/shared/build.gradle.kts](https://github.com/Daio-io/wild/blob/main/playbook/shared/build.gradle.kts).
For A2UI, the gallery and catalog JVM tests consume the same
[A2uiGalleryFixtures](https://github.com/Daio-io/wild/blob/main/a2ui-catalog/src/commonMain/kotlin/io/daio/wild/a2ui/catalog/A2uiGalleryFixtures.kt).
This is public API because the playbook consumes it across the module boundary.
Use that shared source when changing payloads. See [A2UI](a2ui.md) for adoption.

Check gallery wiring with:

```bash
./gradlew :a2ui-catalog:jvmTest --tests "io.daio.wild.a2ui.catalog.A2uiFixturesTest"
./gradlew :playbook:shared:compileKotlinJvm :playbook:web:compileKotlinJvm
```

The compile checks cover wiring; use the appropriate platform build and run the
gallery to verify platform-specific behavior. Visual changes also need the focused
coverage described in [Screenshot testing](screenshot-testing.md).

## Change a public API

A module that applies `libs.plugins.metalava` configures the signature filename in
its own `build.gradle.kts`. The current signature is `api/api.txt`; versioned files
are release snapshots maintained by `scripts/prepare-release.sh`.

For example, after changing a public catalog declaration:

```bash
./gradlew :a2ui-catalog:metalavaGenerateSignature
git diff -- a2ui-catalog/api/api.txt
```

Review the generated diff alongside the source change. If it rewrites unrelated
signatures, compare the Kotlin/Metalava versions and declarations before accepting
those changes. Add KDoc, an applicable `@since`, and a consumer example with the API.

## Locate PR review feedback

From the PR branch, get its number with `gh pr view --json number,url,baseRefName`.
Fetch inline review threads through GraphQL; `reviewThreads` is not a field of
`gh pr view --json`:

```bash
gh api graphql \
  -f query='query($number:Int!){repository(owner:"Daio-io",name:"wild"){pullRequest(number:$number){reviewThreads(first:100){nodes{id isResolved comments(first:100){nodes{databaseId body path line url author{login}} pageInfo{hasNextPage endCursor}}} pageInfo{hasNextPage endCursor}}}}}' \
  -F number=391
```

Replace the example PR number. Follow `pageInfo.endCursor` with `after:` when a
connection has more pages. Filter threads by `isResolved == false`. Conversation
comments are a separate source: `gh api repos/Daio-io/wild/issues/391/comments --paginate`.
Check both when reviewing. Inline authors use `author`, rather than `user`, in GraphQL.

When the task authorizes replies, the inline reply endpoint is
`repos/Daio-io/wild/pulls/<pr>/comments/<comment-id>/replies`; resolving the thread is
a separate GraphQL mutation. Compare feedback with the task and the current diff
before changing code, especially when historical plans or sibling examples differ.
