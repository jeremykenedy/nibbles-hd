# Troubleshooting

- **No connected TV:** enable/authorize ADB using your device's own developer settings, connect it separately and pass its exact `--serial`. The installer does not alter security settings or restart the ADB server.
- **Signature mismatch:** use the release signed with the existing production key. Do not uninstall automatically; removal erases local options.
- **Not listed in Fire OS Settings:** stock Fire OS may hide third-party dreams. Use the supported Toolkit/Fire TV UI picker, then verify ordinary idle behavior. A successful APK install alone does not select it.
- **Changed options seem unchanged:** close Preview and reopen it. Options resolve when a new scene instance is created; randomize-all can override stored choices for that instance.
- **A snake disappears or the board fades:** collisions and round limits intentionally recover into a new round. This is autonomous gameplay, not a static route demonstration.
- **Home or Back takes time:** allow the native launcher/task transition to settle. An external Preview launch may return to a different task than a Preview opened from settings.
- **Slow or uneven motion:** try Solo, Clean effects or Relaxed speed. Report your model, OS/API and resolution. Short measurements on one TV do not promise performance on every model.
- **Removal:** select your previous screensaver first, then uninstall Nibbles HD. The standalone installer never changes or restores system timers.
