# Integrations

[Fire TV Toolkit](https://github.com/jeremykenedy/fire-tv-toolkit) installs and updates signed release APKs with published SHA-256 verification. It also offers supported screensaver selection/timer controls, launcher choices and protection for supported settings. Install and selection are separate; app settings remain app-owned.

```sh
git clone https://github.com/jeremykenedy/fire-tv-toolkit.git
cd fire-tv-toolkit
node setup.js
firetv-screensavers --install=nibbles-hd
screensaver --set=nibbles-hd
```

Run the install command again to update. To remove, first select a different saver, then use `firetv-screensavers --uninstall=nibbles-hd --yes --force`. The guarded Toolkit flow explains changes and asks for confirmation interactively. Retain your prior selection and timer values for restoration.

[Fire TV UI](https://github.com/jeremykenedy/fire-tv-ui) discovers installed DreamServices, displays native/bundled previews and opens the app's own settings. Its reviewed download catalog uses pinned signed release metadata and Android installation confirmation. It does not offer arbitrary-URL installs or a generic editor for this app's scene settings. Nibbles HD itself stays offline; companion downloads are separate.

The [developer contract](integration-contract.md) records exact identity, release assets, preview hashes and local provider semantics. Repository and host release delivery are tracked separately from physical rollout; no other TV deployment is implied by catalog presence.

## Source and artwork

Gameplay and procedural scene art are original. No QBasic game source, branding, fonts, audio or game asset is included. Native settings, build, DreamService and installer scaffolding were adapted from Jeremy Kenedy's Apache-2.0 Prism Clock and Cloud Drift Clock. Required notices are preserved in [NOTICE](../NOTICE), bundled offline alongside [LICENSE](../LICENSE).
