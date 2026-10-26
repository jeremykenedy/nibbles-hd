"""Check the built package and its runtime permission boundary."""

import os
import re
import subprocess
import sys
import zipfile
from pathlib import Path

sdk = Path(os.environ.get("ANDROID_HOME", Path.home() / "Library/Android/sdk"))
aapt = sdk / "build-tools/36.0.0/aapt"
apk = Path(sys.argv[1])


def dump(*arguments):
    return subprocess.check_output([str(aapt), "dump", *arguments, str(apk)], text=True)


badging = dump("badging")
if not re.search(
    r"package: name='com\.jeremykenedy\.nibbleshd' versionCode='3' versionName='1\.0\.0'", badging
):
    raise SystemExit("Unexpected package or version")
permissions = dump("permissions")
if "uses-permission:" in permissions:
    raise SystemExit("Nibbles HD must not request runtime or network permissions")
manifest = subprocess.check_output(
    [str(aapt), "dump", "xmltree", str(apk), "AndroidManifest.xml"], text=True
)
for value in (
    "android.permission.BIND_DREAM_SERVICE",
    "android.service.dreams.DreamService",
    ".NibblesDreamService",
    "com.jeremykenedy.nibbleshd.settings",
):
    if value not in manifest:
        raise SystemExit("Missing required DreamService or provider declaration: " + value)
if "sdkVersion:'23'" not in badging or "targetSdkVersion:'36'" not in badging:
    raise SystemExit("Unexpected SDK requirements")
if "leanback-launchable-activity:" not in badging:
    raise SystemExit("Missing TV launcher")
dream = subprocess.check_output(
    [str(aapt), "dump", "xmltree", str(apk), "res/xml/dream.xml"], text=True
)
for value in ("settingsActivity", "com.jeremykenedy.nibbleshd.SettingsActivity", "previewImage"):
    if value not in dream:
        raise SystemExit("Missing Dream metadata: " + value)
root = Path(__file__).resolve().parent.parent
with zipfile.ZipFile(apk) as archive:
    names = archive.namelist()
    if any(name.startswith("lib/") or name.endswith(".jar") for name in names):
        raise SystemExit("Unexpected bundled native code or library")
    if [name for name in names if name.endswith(".dex")] != ["classes.dex"]:
        raise SystemExit("Unexpected executable payload")
    # Uri describes local ContentProvider addresses; it is not a network client.
    dex = archive.read("classes.dex").replace(b"Landroid/net/Uri;", b"")
    for forbidden in (
        b"Ljava/net/",
        b"Landroid/net/",
        b"Landroid/webkit/",
        b"Lokhttp",
        b"Lretrofit",
        b"Lcom/google/firebase",
        b"Lcom/google/android/gms/ads",
        b"Lsentry/",
        b"http://",
        b"https://",
    ):
        if forbidden in dex:
            raise SystemExit("Unexpected network/reporting reference: " + forbidden.decode())
    for name in ("LICENSE", "NOTICE"):
        if archive.read("assets/" + name) != (root / name).read_bytes():
            raise SystemExit("Bundled license/notice mismatch")
    if (
        archive.read("res/drawable-nodpi-v4/screensaver_preview.jpg")
        != (root / "res/drawable-nodpi/screensaver_preview.jpg").read_bytes()
    ):
        raise SystemExit("Native preview mismatch")
print("Verified identity/SDK, TV/Dream metadata, preview, notices and offline executable boundary.")
