# Project standards

- Build an original continuously animated autonomous snake game with no scene text.
- Preserve `com.jeremykenedy.nibbleshd` and its unique production signer for upgrades.
- Keep runtime offline, with no requested permissions, network libraries or reporting.
- Keep gameplay and option validation Android-free and covered by behavioral tests.
- Render at the actual Android view size. Never force a display buffer resolution.
- Stop frame callbacks when paused or detached. Keep settings readable with a TV remote.
- Keep signing keys and build products outside Git. Use Apache-2.0 and preserve notices.
- Require native visual, lifecycle, settings and performance review before release.
