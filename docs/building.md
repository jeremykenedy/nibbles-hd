# Building and CI

Use JDK 21, Android SDK platform 36/build-tools 36.0.0, Python 3.10+, curl, zip and ShellCheck. On Linux set `ANDROID_HOME`; macOS defaults to `~/Library/Android/sdk`.

```sh
bash scripts/test-coverage.sh
bash scripts/test-python-coverage.sh
bash scripts/check-style.sh
bash build.sh --unsigned
python3 scripts/check-artifact.py build/nibbles-hd-unsigned.apk
python3 scripts/check-docs.py
git diff --check
```

CI runs this same sequence on Ubuntu with pinned checkout/Java/Python action commits, plus a local Gitleaks 8.30.1 source-history scan. Java formatter and JaCoCo downloads have pinned SHA-256 values. Python tools have fixed versions. CI never receives the production key and only builds an unsigned review APK. No external rating service is configured or claimed.

`bash build.sh` signs locally. The unique production key and password file are outside Git; preserve them for updates. The build refuses a missing key paired with an existing password file. Do not generate a replacement for a published identity. The release package is `com.jeremykenedy.nibbleshd`, version 1.0.0/code 3, min API 23 and target 36.

## Coverage boundary

JaCoCo includes every executable line/branch in the owned Android-free `NibblesGame`, its Snake class and `NibblesOptions`, with 100% line/branch gates and no partial class exclusions. Tests cover food placement/growth, simultaneous collision semantics, safe departing tails, stage resets, full boards, route safety, invalid input and multi-seed autonomous progression. The deterministic simulation consumes 503 food items and enters seven collision/recovery rounds.

Python coverage includes all of `install.py`, requiring 100%, including line and branch outcomes. Tests cover trusted release paths/redirects, malformed metadata, byte limits, checksum failures, private temporary files, ADB/device selection, cancellation, install/update, removal confirmation and cleanup after failure. ADB/HTTP are mocked in host tests; native install/update evidence is separate.

Native Android Activities, Dialog, ContentProvider, Canvas/Choreographer and DreamService classes are outside host instrumentation. Their D-pad, storage, lifecycle, preview and Dream behavior is recorded in [verification](verification.md). CI scripts and framework-generated R are not presented as game coverage. Artifact checks verify package/version/SDK, no requested permissions, Dream metadata, native preview, bundled notices and absence of network/reporting references and extra executable libraries.
