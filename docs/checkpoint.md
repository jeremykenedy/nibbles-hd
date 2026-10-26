# Matte ribbon candidate checkpoint

Candidate 2 is a local appearance revision. Candidate 1 and its native captures
remain intact in the original worktree. Neither candidate is published or
registered in a shared host.

## Changed appearance

The ribbon uses a restrained matte color gradient and a single precise upper
edge highlight. The dark outer sleeve, center shine and small eyes are removed.
The board and background are brighter at the unchanged default lighting value.
Food has a larger warm disk, stronger contrast and a softer halo. The settings
viewport reserves 36dp below the scrollable area, with 48dp content padding, so
the final focused row can remain inside the screen's safe area.

No gameplay, option values, native resolution, interpolation, round timing or
lifecycle behavior changed. Game rules, settings provider, PreviewActivity and
DreamService are byte-identical to candidate 1 source. Version code is 2; version
name remains 1.0.0 because no public release exists. The original signer is used.

## Verification boundary

Candidate 1's host gameplay/options report passed 100% line and branch coverage.
Those source files and tests are unchanged, so that full suite is not repeated
for this appearance-only revision. Candidate 1 also ran on the native Android TV
emulator at a measured 1920x1080 buffer. Native D-pad, invalid settings,
persistence, recreation, Back/Home/resume and paused-frame checks passed there.
These facts do not establish candidate 2's visual or native functional result.

Candidate 2 requires formatting, shell checks, Android compilation, exact APK
identity/signature/permission inspection and a focused comparison proving the
unchanged domain, option schema, lifecycle and interpolation boundaries. The
signed candidate is frozen with those results before any native review.

## Pending native and delivery gates

- Fresh native moving review of the revised material, contrast, food and turns.
- Last-row D-pad focus and safe bottom spacing at actual TV density.
- New native captures from candidate 2, including useful alternate looks.
- Physical DreamService/natural idle behavior and sustained physical pacing.
- Physical lifecycle, settings restoration, display contrast and couch visibility.
- Final docs, animated banners, verified captures, installer, CI, public
  repository/release and both host integrations after quality acceptance.

A build is not a visual pass. No device, ADB, Chrome or physical-TV access is
allowed without the root coordinator's fresh lease. No hidden buffer reduction,
fisheye transform, scene label or watermark is introduced.
