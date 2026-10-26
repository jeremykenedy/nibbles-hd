# Native verification

## Physical reviewed runtime

The exact matte code 2 APK was installed on one physical Amazon AFTDEC012E, Android 11/API 30, with an actual 1920x1080 native view/surface. SHA-256: `7a91934b61956165384e56ff0637ce4d720e032604d9e2e12a00745988c03f4b`. The game, options, scene renderer, provider, Dream and Preview source and class bytecode are identical in production code 3. Final code 3 metadata/settings/credits checks are recorded separately below.

Default and high-load native settings/Preview, D-pad value changes, Back, Home, process recreation, all eight persisted values and callback stop were checked. A same-PID Settings pause held the rendered-frame counter at 245 over four seconds. Android reclaimed an earlier background process for LOW_MEMORY at importance 400; this was not an app crash. Options survived recreation. External component Back behavior was distinguished from a fresh Settings-owned Preview/Back.

For ordinary idle, only the supported picker selection was changed temporarily; the existing 300000 ms delay stayed unchanged. Main was left with no input. The last not-bound observation was at 291.431 seconds and the first bound normal, non-test Dream at 301.883 seconds. This bounds observation time rather than asserting an exact five-minute callback. No forced Dream starter or timer change was used. The same PID remained normally bound after a separate clean measurement window and moving capture.

Actual native movies were independently played at 1x to the end, with sampled raw frames: default 24.900756 seconds, high-load 19.869567 seconds and normal Dream 44.539 seconds. No moving-quality rejection was found. This supports production packaging, not owner acceptance or every-frame certification. Source movie hashes are in [provenance](../art/screenshots/provenance.json).

## Short pacing and memory samples

Measurements were separated from movie recording and input. Keep both compositor samples and raw gfxinfo rather than treating capture frame rate as pacing proof.

| Case | Clean window | SurfaceFlinger sampled presents/s | gfx median / p99 | Reported jank | Deadline misses | PSS |
| --- | --- | --- | --- | --- | --- | --- |
| Default | 13.768 s | Later rings 59.9994, 59.9999 | 18 / 38 ms | 819/823, 99.51% | 15 | 98,043 KB |
| Trio/Brisk/Radiant | 13.893 s | 60.0005, 59.9999, 59.9989 | 19 / 31 ms | 815/840, 97.02% | 0 | 87,611 KB |
| Normal Dream | 14.426 s | 60.0002, 53.6208, 59.9992 | 19 / 44 ms | 824/847, 97.28% | 25 | 76,778 KB |

The first default SF ring, 46.0982 presents/s, includes recording-transition history and is excluded from steady interpretation. The later short samples do not establish universal 60 FPS; raw gfxinfo reports substantial jank and deadline misses. Thermal endurance, long-term idle power, reboot persistence, native 4K and other physical devices remain unverified. D-pad input was sent through ADB, not a physical hardware remote test.

After testing, prior Aerial selection, full tracked system/UI settings, 58 baseline APK identities, package/UID grants/appops, provider values, external backup-file bytes/existence, accessibility bindings and guard health were restored. Final provider reads preceded stopped-flag restoration. The new code 2 package was retained stopped with no PID; final Main was awake and Dream null. Strict cleanup reported no errors.

## Emulator

A Google TV API 34 emulator at 1920x1080 demonstrated Preview, three native looks, bottom-row D-pad On/Off and Back. Broader code 1 checks covered invalid provider writes, process recreation and lifecycle; the relevant source remained unchanged in code 2. The 57.453-second matte movie was independently played to the end. Emulator performance is not physical Fire TV pacing, and no vendor Dream activation is inferred from Preview.

## Production code 3

The exact 1.0.0/code 3 signed APK, SHA-256 `c49bc22e5cbf5067da4bbcb1cd63140d31b0a49a83f6826e111986ee4b7d7971`, passed the final bounded native check on Google sdk_google_atv64_amati_arm64, Android 14/API 34 at 1920x1080. The same-signer code 2 replacement retained all eight values and identical provider schema. Native Preview settled for 40 seconds and returned to Settings with Back. D-pad Right changed speed to Brisk, which persisted through force-stop/recreation while the other seven values stayed intact. The final Credits row kept 72 pixels of bottom space; its offline NOTICE/Apache dialog scrolled with the D-pad and closed with Back. The existing UI 68 picker displayed the actual app-provided native thumbnail without Select or an OS selection write.

Full 33-package APK/UID/grant/package-and-UID-appop snapshots, 14 UI values, 19 OS values, network/audio settings, readable providers, all eight options and external backup/import/preview-file bytes and existence were preserved. Final provider reads preceded restored stopped flags. Code 3 was retained stopped with no PID, Google Home resumed, guard stopped, Dream null and accessibility binding/crashed sets empty. No native pacing sample or new automatic-Dream claim is made for code 3; the unchanged physical code 2 evidence remains separate.

Harness corrections are retained separately: Android 14 reports appId rather than the assumed userId label, immediate Back queries can precede transition completion, the theme uppercases CLOSE, and an initial picker transition produced no XML until a fresh settled read. A tuple-versus-JSON-list grant comparison was corrected offline; both saved complete control snapshots were equal, followed by a fresh read-only final-state check. These were automation assumptions, not native app faults. No source or artifact was edited during the gate.

## Other devices

No other physical Fire TV, Android TV or Google TV model was available. Volunteers are needed: [report model, OS/API, resolution, settings, Preview/idle behavior and results](https://github.com/jeremykenedy/nibbles-hd/issues/new?template=bug.yml). Minimum API 23 is a build/runtime floor, not a tested-device claim.
