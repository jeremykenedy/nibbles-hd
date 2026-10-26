# Native review checkpoint

Nibbles HD is a local candidate. It has not passed a native visual gate and is
not published, released or registered in the shared hosts.

## Scene

An original autonomous snake game on a dark, softly lit table. Smooth colored
ribbons seek small warm food targets, grow, navigate obstacles and recover from
real collisions. Every segment is interpolated between game steps. Faint moving
light in the board, small drifting particles and subtle whole-table travel keep
the composition alive without a fixed screen border. There are no words,
scores, logos, watermarks or clock overlays in the running scene.

The AI uses breadth-first food distances, reachable space, immediate occupancy
and nearby opponents when choosing a move. It uses seeded randomness for varied
choices and new food positions. It does not follow a fixed loop or a single
Hamiltonian demonstration.

## Completed local checks

- Behavioral tests cover growth, food placement, own-tail occupancy, wall/body/
  obstacle/head collisions, simultaneous moves, reset, full-board recovery,
  route legality and strict option validation.
- Twelve seeded 300-tick simulations consumed 503 food items and completed seven
  collision/recovery rounds. Every accepted body and food position was checked
  for overlap. These are host algorithm tests, not TV performance results.
- Host gameplay and option logic, including the nested snake state, has 100%
  line and branch coverage under JaCoCo. The gate is enforced by
  `scripts/test-coverage.sh`.
- Android packaging, Java formatting, shell checks and package/manifest checks
  run locally. The signed artifact has no requested permissions.

## Coverage boundary

`NibblesGame` and `NibblesOptions` are the Android-free domain and validation
boundary. No gameplay or validation classes are excluded. Android View drawing,
Choreographer, ContentProvider persistence, Activity navigation and DreamService
lifecycle require native instrumentation and manual review. Host coverage does
not establish correctness of that boundary.

## Pending gates

- Real preview and DreamService rendering at the measured Android surface size.
- Visual quality after 40 to 60 seconds, representative captures and a small
  actual-app preview replacing the clearly illustrative vector artwork.
- D-pad focus, scrolling, Left/Right/Select changes, Back, option persistence,
  provider schema reads and strict update rejection.
- Pause/resume and leaving the dream without leaked frame callbacks.
- Sustained frame pacing, CPU/memory/thermal behavior and runtime network audit.
- Physical idle activation, installed byte parity and restoration of any
  temporarily changed device settings by the owner of the native-review lease.
- Final README, animated light/dark desktop/mobile banners, native screenshot
  gallery, installer, CI, repository metadata, publication and shared-host work.

No worker device, ADB or browser calls are authorized by this checkpoint. The
root coordinator assigns a fresh device lease before any native review.
