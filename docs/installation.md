# Installation

## Guided standalone installer

Install Android Platform Tools, connect and authorize your TV with ADB, then run:

```sh
python3 install.py --serial YOUR_TV_IP:5555
```

The installer asks before changing the TV, downloads the latest release and sibling SHA-256 file, verifies their bytes and uses `adb install -r`. HTTPS downloads are restricted to this repository's release path and GitHub's asset hosts, with byte and redirect limits. Updates retain the app's eight options and require the existing signer. An unauthorized/disconnected device, invalid download or failed ADB operation exits nonzero. Ctrl-C cancels; if ADB installation had begun, inspect the TV before retrying.

For scripts use `--yes --serial SERIAL`. Multiple connected devices require an explicit serial in noninteractive mode. To use a locally built or separately verified APK, pass `--apk build/nibbles-hd.apk`; local bytes are trusted input and are not fetched or checked against the public release checksum. Android still enforces the installed package's signature on updates.

## Activate and restore

Installation does not change selection, timers, network, permissions or update settings. On Android TV/Google TV, use the device's supported screensaver settings. Fire OS may hide third-party dreams; use [Fire TV Toolkit or Fire TV UI](integrations.md). Save the currently selected saver before changing it. No sleep/rollback protection is silently installed. Toolkit provides its own supported, reversible guard and timer controls.

Open Nibbles HD from Apps for settings and Preview. Select Nibbles HD separately for idle activation. A preview alone does not prove automatic startup.

## Update

Run the installer again. It uses the latest signed release and preserves app preferences. Version 1.0.0 is the first public release; same-signer local code 1/2 candidates can update to code 3. A signature mismatch needs the matching original key, not a forced uninstall. Uninstalling would erase preferences.

## Remove

First select your previous screensaver through the same supported picker. Then run:

```sh
python3 install.py --serial YOUR_TV_IP:5555 --uninstall
```

Noninteractive removal requires both `--yes --force`. It removes only `com.jeremykenedy.nibbleshd` and its settings. No unrelated APK or file is removed. Because the standalone installer never changes OS selection or timers, it has no OS snapshot to restore. Restore your prior selection before removal.

## Manual APK

Download [the APK](https://github.com/jeremykenedy/nibbles-hd/releases/latest/download/nibbles-hd.apk) and [its checksum](https://github.com/jeremykenedy/nibbles-hd/releases/latest/download/nibbles-hd.apk.sha256) into the same directory, then verify and install:

```sh
shasum -a 256 -c nibbles-hd.apk.sha256
adb -s YOUR_TV_IP:5555 install -r nibbles-hd.apk
```

The computer downloads files only when requested; the installed screensaver never checks for updates.
