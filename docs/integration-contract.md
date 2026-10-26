# Integration contract

| Field | Exact production value |
| --- | --- |
| Title / saver ID | Nibbles HD / `nibbles-hd` |
| Repository | `jeremykenedy/nibbles-hd` |
| Package | `com.jeremykenedy.nibbleshd` |
| Dream | `com.jeremykenedy.nibbleshd/.NibblesDreamService` |
| Full Dream | `com.jeremykenedy.nibbleshd/com.jeremykenedy.nibbleshd.NibblesDreamService` |
| Settings / Preview | `.SettingsActivity` / `.PreviewActivity` in the same package |
| Version | 1.0.0, code 3 |
| Minimum / target SDK | 23 / 36 |
| Release | `v1.0.0` |
| APK | `https://github.com/jeremykenedy/nibbles-hd/releases/download/v1.0.0/nibbles-hd.apk` |
| Checksum | Same URL plus `.sha256` |
| APK bytes / SHA-256 | 41727 / `c49bc22e5cbf5067da4bbcb1cd63140d31b0a49a83f6826e111986ee4b7d7971` |
| Signer SHA-256 | `cdf8f4f4fd70dbb133fe8b05b6ab165eb3e98b033ddcb7d305cf0d84d874bc75` |
| Native preview | `res/drawable-nodpi/screensaver_preview.jpg`, 384x216 |
| Preview SHA-256 | `99c65890a75fe6404d95f198be309811be60d995d3249ad735ea3035356fef59` |
| License | Apache-2.0 with bundled offline NOTICE |
| Schema | `content://com.jeremykenedy.nibbleshd.settings/schema` |
| Values | `content://com.jeremykenedy.nibbleshd.settings/settings` |

The [configuration guide](configuration.md) defines all eight keys/defaults, validation and local provider access. The [device matrix and limits](verification.md) distinguish the physical native HD/normal idle result from emulator Preview and untested models. [Preview provenance](../art/screenshots/provenance.json) records the actual capture and resize, with source/runtime equivalence.

## Toolkit registration

Add one entry to the current `src/screensaver-registry.js`, preserving all other entries and the default:

```js
{
  id: 'nibbles-hd',
  name: 'Nibbles HD',
  pkg: 'com.jeremykenedy.nibbleshd',
  dreamComponent: 'com.jeremykenedy.nibbleshd/.NibblesDreamService',
  repo: 'jeremykenedy/nibbles-hd',
  asset: 'nibbles-hd.apk',
  blurb: 'Autonomous matte snakes find food, grow and begin fresh rounds.',
}
```

Update the available list, exact artifact contract and native gallery. Run the registry/install and project-required checks. The normal installer verifies the public checksum; do not register an unpublished candidate as installable.

## Fire TV UI registration

Installed service discovery uses the real exported BIND_DREAM_SERVICE service and `res/xml/dream.xml`. Add one pinned entry to `app/screensavers.json` using the release URL, tag, exact hash and byte count above. Add the 384x216 native JPEG to `app/previews/` under the SHA-256 of the normalized full Dream component plus `.jpg`, using the project's existing helper. Record provenance in its preview index/docs. Preserve all other catalog entries, selection and layout. Run its tests/build/artifact checks and native catalog/settings/preview checks under an authorized lease.

The hosts open native SettingsActivity; neither claims a generic editor for the provider. Saver release, host release and physical rollout are separate evidence gates.
