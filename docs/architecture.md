# Architecture

`NibblesGame` owns an Android-free grid, food placement, simultaneous snake movement, growth, collisions and round reset. Food adds three cells of growth. Route choice combines food distance, reachable space, other snakes' threats and randomized ties, rather than replaying one fixed route. A snake may enter its own departing tail only when it is not growing or eating. Wall/body/head collisions resolve together. Collisions, a full board or the round limit lead to a fresh stage and round.

`NibblesOptions` validates the public schema and resolves supported random choices. `SettingsProvider` exposes the validated persistent interface; `SettingsActivity` is a D-pad screen. `NibblesSceneView` uses Canvas and Choreographer at its actual view dimensions, interpolating previous/current positions into rounded matte ribbons. Ambient motion and subtle board drift reduce fixed imagery; they are not a burn-in guarantee.

`PreviewActivity` starts/stops callbacks on resume/pause and releases its view on destroy. `NibblesDreamService` starts/stops on Dream callbacks and releases on detach. Neither runs a background update or work service. There is no network client, media decoder, sound, native library or third-party runtime dependency.

Production code 3 adds an offline credits dialog and a native-derived preview. The game, options, scene, provider, Dream and Preview source and compiled class bytes remain identical to physical code 2. Android framework rendering, lifecycle and persistence are checked natively rather than represented by host coverage percentages.
