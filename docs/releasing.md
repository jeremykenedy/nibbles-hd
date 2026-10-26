# Releasing

Use the global release standards and the verified remote primary branch. Never publish from a feature branch or move an existing release tag.

1. Preserve `com.jeremykenedy.nibbleshd` and the existing production signer. Update SemVer/version code and notes for a new release.
2. Run the complete checks in [building](building.md). Inspect permissions, Dream/settings metadata, native preview, offline assets, signer, exact APK bytes and SHA-256.
3. Review the exact native build on an emulator and suitable physical TV. Runtime changes need fresh moving, settings/lifecycle and actual normal Dream evidence; packaging-only changes may use explicit source/class equivalence with a bounded native structural check. Preserve measurements and limitations.
4. Update current screenshots/provenance, docs, About, website and topics. Verify desktop/mobile/theme/reduced-motion banners and galleries in a real browser. Rerun affected checks after edits.
5. Merge the reviewed PR after required functional CI passes. Fetch primary and verify the release checkout matches its remote tip. Tag that exact commit as `vMAJOR.MINOR.PATCH`.
6. Publish the signed `nibbles-hd.apk` and `nibbles-hd.apk.sha256`, using reviewed notes from `docs/releases/`. Download both anonymously and compare bytes/hash, package/version/SDK and signer. Verify the tag and published body.
7. Immediately integrate the verified public artifact into both Toolkit and Fire TV UI using their current add-screensaver workflow. Preserve other entries/defaults and release both hosts with their required artifacts. Add the repository to the completed public URL list only when all three releases are delivered.

Keys/passwords stay outside Git and CI. CI artifacts are unsigned; do not publish them as production APKs. Record native device rollout separately from public catalog/release delivery.
