# Integration contract

| Field | Candidate value |
| --- | --- |
| Title | Nibbles HD |
| Saver ID | `nibbles-hd` |
| Intended repository | `jeremykenedy/nibbles-hd`, not created |
| Application | `com.jeremykenedy.nibbleshd` |
| Dream | `com.jeremykenedy.nibbleshd/.NibblesDreamService` |
| Settings | `com.jeremykenedy.nibbleshd/.SettingsActivity` |
| Preview | `com.jeremykenedy.nibbleshd/.PreviewActivity` |
| Minimum / target API | 23 / 36 |
| Candidate version | 1.0.0, version code 1 |
| Intended APK/checksum | `nibbles-hd.apk`, `nibbles-hd.apk.sha256` |
| License | Apache-2.0 |
| Schema | `content://com.jeremykenedy.nibbleshd.settings/schema` |
| Values | `content://com.jeremykenedy.nibbleshd.settings/settings` |

The provider returns schema columns `key`, `title`, `type`, `default`, `choices`
and `randomAllowed`. `choices` contains pipe-separated stored values. A settings
query returns `key`, `value`. Updates require exactly two ContentValues entries,
`key` and `value`, with one supported value. Unsupported keys, values, paths and
insert/delete operations are rejected. Writes persist synchronously. Changes
apply when the next preview or dream is constructed.

| Key | Default | Values |
| --- | --- | --- |
| `speed` | `steady` | `relaxed`, `steady`, `brisk`, `random` |
| `snakes` | `duet` | `solo`, `duet`, `trio`, `random` |
| `palette` | `mineral` | `mineral`, `citrus`, `tide`, `orchid`, `random` |
| `stage` | `random` | `open`, `islands`, `channels`, `random` |
| `board` | `standard` | `wide`, `standard`, `close`, `random` |
| `effects` | `soft` | `clean`, `soft`, `radiant`, `random` |
| `brightness` | `balanced` | `dim`, `balanced`, `bright`, `random` |
| `randomize_all` | `false` | `false`, `true` |

Speed is 5, 8 or 12 game steps per second. Snakes are 1, 2 or 3. Board dimensions
are 56x30, 48x26 or 38x22 cells. All are drawn at the actual Android View size.
Random values are chosen at scene start; random stage changes again per round.
Randomize-all overrides the other choices at scene start. Effects control
particles, ribbon bloom and food rings. Lighting changes the entire scene.

The app contains no network SDK, runtime network request, requested permission,
ads, tracking, diagnostics or background update behavior. Build-time test-tool
downloads are separate from app runtime. Inspect the final APK again before
release and record native runtime evidence.

The current vector launcher/dream artwork is original illustrative artwork. It
is not a screenshot and must not be passed off as one. Real native captures and
the host thumbnail are pending the native review.

Publication and both shared-host integrations are owned by the root coordinator.
Do not add this candidate to an installable registry before a verified public
signed release and checksum exist.
