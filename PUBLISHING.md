# Publishing

Spatial publishes 15 modules to Maven Central under `io.github.danielitocode`.
Two GitHub Actions workflows share the responsibility:

## `.github/workflows/ci.yml` — don't break the project

Runs on every push to `master`/`claude` and every PR into `master`.
Executes `./gradlew verifyRepository`, which builds every module, runs
tests, publishes to the *local* Maven repository, and checks that a
`.jar`/`.aar` was actually produced for each publishable module. It never
touches Maven Central and never requires signing credentials, because
`signAllPublications()` in the root `build.gradle.kts` only runs when
`-PspatialRelease=true` is passed — which CI never does.

## `.github/workflows/publish.yml` — cut a new release

Runs only on a `v*.*.*` tag push or a manual dispatch. It imports a GPG key
into a real keyring (required because every module signs via
`signing { useGpgCmd() }`, not in-memory signing), then runs
`./gradlew publishAllModules -PVERSION_NAME=<version> -PspatialRelease=true`.

Required repository secrets (not yet configured as of this writing):

- `SPATIAL_GPG_PRIVATE_KEY`, `SPATIAL_GPG_PASSPHRASE`
- `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_PASSWORD`

## Version

The published version lives in a single place: `VERSION_NAME` in
`gradle.properties` (currently `0.1.0-alpha01`). The publish workflow
overrides it per-release from the git tag; every module reads it via
`project.version`, so no module file hardcodes a version anymore.

## Verification status

This setup was built and statically reviewed in a sandbox with no network
access to Maven Central or Google Maven, so `./gradlew` could not actually
be run here. Brace balance and structural correctness were checked by hand;
first real verification will happen when `ci.yml` runs on GitHub Actions.
Treat the first CI run on this change as the actual test, not this commit
message.
