# Configuration

## Remote controls

Open **Nibbles HD** from Apps. Up/Down moves focus; Left/Right or Select cycles each option. Preview animation opens the fullscreen scene. Back returns to settings when Preview was opened there; an externally launched Preview follows its Android task's Back behavior. Home leaves the app. Credits and licenses opens an offline, scrollable dialog; Close or Back returns to settings.

Changes persist locally and apply to the next Preview or Dream instance. A running instance does not read every settings change. Rendering stops when Preview pauses, the view detaches or Dream stops. There are no gameplay controls, scores, labels, sound or clock.

| Key | Default | Stored values |
| --- | --- | --- |
| `speed` | `steady` | `relaxed`, `steady`, `brisk`, `random` |
| `snakes` | `duet` | `solo`, `duet`, `trio`, `random` |
| `palette` | `mineral` | `mineral`, `citrus`, `tide`, `orchid`, `random` |
| `stage` | `random` | `open`, `islands`, `channels`, `random` |
| `board` | `standard` | `wide`, `standard`, `close`, `random` |
| `effects` | `soft` | `clean`, `soft`, `radiant`, `random` |
| `brightness` | `balanced` | `dim`, `balanced`, `bright`, `random` |
| `randomize_all` | `false` | `false`, `true` |

Relaxed, Steady and Brisk advance gameplay at 5, 8 and 12 steps/s; rendering interpolates between steps. Solo, Duet and Trio create one, two or three snakes. Wide, Standard and Close use 56x30, 48x26 and 38x22 cells, scaled to the actual native view. Stages change obstacles. Effects adjust ambient particles and food/collision effects. Lighting scales scene brightness to 0.58, 0.84 or 1.0.

A per-option Random choice is resolved at scene start; Random stage is chosen again each round. Randomize all on start overrides all seven choices for that instance without replacing their stored values. The continuous AI seeks food and space but is allowed to collide and recover; it is not an unbeatable solver.

## Local provider

Schema: `content://com.jeremykenedy.nibbleshd.settings/schema`. Values: `content://com.jeremykenedy.nibbleshd.settings/settings`.

```sh
adb -s YOUR_TV_IP:5555 shell content query --uri content://com.jeremykenedy.nibbleshd.settings/schema
adb -s YOUR_TV_IP:5555 shell content update --uri content://com.jeremykenedy.nibbleshd.settings/settings --bind key:s:speed --bind value:s:brisk
```

Schema columns are `key`, `title`, `type`, `default`, `choices`, `randomAllowed`; choices are pipe-separated. Values columns are `key`, `value`. Exactly two ContentValues entries, key and value, are required for one update. Unknown keys, invalid values, unsupported paths, insert and delete are rejected. Writes commit synchronously. The exported provider is intentionally accessible to local companion apps and ADB; it exposes only these scene settings, no files or system settings. Host apps currently open this native screen instead of providing a generic property editor.
