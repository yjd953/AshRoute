# Google Play submission checklist

This checklist is for AshRoute package `io.github.yjd953.ashroute`. It records
the intended answers for the current source tree. Re-check the final signed
bundle before submitting because Play Console wording and policy can change.

## Current readiness

| Item | Status | Evidence or next action |
| --- | --- | --- |
| Organization developer account | Blocked outside the repository | Register or verify an Organization account with matching D-U-N-S information. Google requires this for apps approved to use `VpnService`. |
| Target API | Ready | `targetSdk = 37`; Google Play requires API 36 or later for new phone apps from August 31, 2026. |
| VPN disclosure and consent | Ready in code; device video required | The first VPN start displays a separate in-app disclosure before Android's VPN permission prompt. |
| Encrypted tunnel endpoint | Ready in Play build; device test required | The Play flavor rejects profiles whose endpoint encryption cannot be established. |
| Privacy policy | Ready | Public and anonymously readable at `https://sites.google.com/view/ashroute`. |
| Data safety | Answers prepared below | Re-check the uploaded bundle before submitting the form. |
| Store icon | Ready | `fastlane/metadata/android/en-US/images/icon.png` |
| Feature graphic | Ready | `fastlane/metadata/android/en-US/images/featureGraphic.png` |
| Phone screenshots | Missing | Capture at least two real screenshots; four portrait screenshots at 1080 x 1920 or higher are recommended. |
| Android TV listing | Excluded | The manifest does not advertise a Leanback launcher, so the first release targets phones and tablets only. |
| Release signing | Configuration ready | Add the documented GitHub Actions secrets and enroll in Play App Signing. |
| Native notices | Ready | GPL, LGPL, MPL, MIT, component revisions, and source locations are bundled in the APK. |

## Account prerequisites

- Use a verified Google Play organization developer account. Google Play
  requires organization registration for developers providing apps approved
  to use `VpnService`.
- Make sure the legal name and address exactly match the Dun & Bradstreet
  record for the D-U-N-S number used during verification.
- Enroll in Play App Signing and keep the upload keystore outside the
  repository.
- Configure GitHub Actions secrets `APP_KEYSTORE_BASE64`,
  `APP_KEYSTORE_PASSWORD`, `APP_KEYSTORE_ALIAS`, `APP_KEY_PASSWORD`, and
  `GPG_PRIVATE_KEY` before running a tagged release workflow.
- Use `https://sites.google.com/view/ashroute` as the privacy-policy URL.
  Re-check anonymous access before every release.

## Store listing

- App name: `AshRoute`
- Default language: English (United States)
- App category: Tools
- Ads: No
- App access: All functionality is available without an account.
- Target audience: not directed to children
- Website/source: `https://github.com/yjd953/AshRoute`
- Privacy policy: `https://sites.google.com/view/ashroute`

Use the text and icon under `fastlane/metadata/android/en-US/`. Do not claim
that AshRoute provides VPN servers, anonymity, unrestricted access, or an
affiliation with v2rayNG.

Upload these preview assets:

- app icon: `fastlane/metadata/android/en-US/images/icon.png`;
- feature graphic:
  `fastlane/metadata/android/en-US/images/featureGraphic.png`;
- at least two real phone screenshots under
  `fastlane/metadata/android/en-US/images/phoneScreenshots/`.

Do not upload mockups as screenshots. The screenshots must show the current
app UI and must not expose real server addresses, subscription URLs,
credentials, QR codes, or private keys.

## VpnService declaration

Proposed answers for the current implementation:

1. Is VPN the app's core functionality? **Yes**
2. Purpose: create a device-level tunnel and route traffic to a proxy or VPN
   endpoint configured by the user.
3. Monetization-based traffic redirection or manipulation? **No**
4. Does the AshRoute maintainer collect or share data through VpnService?
   **No**, provided the published build contains no added analytics,
   advertising, telemetry, or maintainer-operated endpoint.
5. Encryption: the Play build rejects profiles that cannot be established as
   encrypted to the configured endpoint.

The declaration video must be 90 seconds or shorter and show:

1. a clean installation opening AshRoute;
2. adding a test profile that uses an encrypted protocol;
3. tapping Connect and showing the complete in-app VPN disclosure;
4. tapping Cancel and confirming that no VPN starts;
5. tapping Connect again, accepting the disclosure, and accepting Android's
   system VPN permission;
6. the connected state and then a user-initiated disconnect.

Do not include real credentials, subscription URLs, IP addresses, or private
keys in the recording.

## Permissions

The Play build uses `BIND_VPN_SERVICE` through its VPN service and declares
foreground-service, notification, camera, network-state, boot, and local
network permissions for their visible features.

AshRoute does **not** request `QUERY_ALL_PACKAGES`. Per-app routing lists only
launchable apps Android makes visible through the standard launcher query, so
the broad package visibility declaration is not required.

## Foreground service declaration

The Play bundle declares the `specialUse` foreground service type. Complete
the Foreground service permissions declaration with these user-visible use
cases:

- maintain the user-started VPN tunnel while the status notification is
  visible;
- maintain user-started local proxy or root proxy mode while connected;
- run a user-started connection test with visible progress and cancellation;
- update user-configured subscriptions with visible progress.

Explain that deferring or interrupting the connection services disconnects
active traffic, while interrupting test or update work leaves the requested
operation incomplete. The declaration video may reuse the VPN policy video
when it clearly shows the foreground notification and start/stop flow.

## Data safety

For the unmodified official AshRoute build, the intended declaration is that
the developer does not collect or share user data. Configuration, app
selection, QR scanning, and logs are processed locally. Users explicitly
choose external proxy endpoints, subscription providers, and import/export
destinations.

Proposed form answers:

1. Does the app collect or share any required user data types? **No**
2. Is all user data encrypted in transit? **Yes for the Play build's
   developer-controlled network paths and enforced VPN endpoint.**
3. Does the app provide an account deletion mechanism? **Not applicable; the
   app has no account system.**
4. VpnService data collection or sharing? **No**

The "No" answer relies on these current facts:

- profiles, installed-app selections, QR frames, and logs remain on device;
- the maintainer operates no server and receives none of that data;
- connections to user-selected endpoints and explicit exports are
  user-initiated transfers;
- no advertising, analytics, telemetry, or crash-reporting SDK is present.

Do not reuse these answers after adding a backend, analytics SDK, crash
reporter, advertising SDK, managed VPN endpoint, or remote diagnostics.

Before submission, verify that:

- no analytics, crash reporting, advertising, account, or telemetry SDK has
  been added;
- no maintainer-operated server receives configuration, traffic, app lists,
  logs, device identifiers, or usage events;
- the final privacy policy lists every network service still used by the app;
- the Data safety answers match the exact behavior of the uploaded bundle.

If any of these facts changes, update both the disclosure and Play Console
answers before release.

## Release gates

- Build a signed `playstoreRelease` Android App Bundle.
- Run unit tests, Playstore lint, and release bundle assembly.
- Run `scripts/verify-play-apk.sh` against the universal Play APK. This checks
  the package ID, target API, bundled legal notices, native ABIs, and 16 KB
  alignment for 64-bit libraries.
- Install a matching APK on a physical device and verify connect, disconnect,
  VPN disclosure accept/cancel, per-app routing, restart, and update checks.
- Inspect the bundle for all declared ABIs and native libraries.
- Upload first to an internal testing track.
- Complete content rating, app content, privacy, and VpnService forms.
- Resolve every Play pre-launch report issue before production rollout.

## Play Console submission order

1. Verify the Organization account and D-U-N-S details.
2. Create the app with package name `io.github.yjd953.ashroute`, category
   Tools, and default language English (United States).
3. Enroll in Play App Signing and upload the signed AAB to Internal testing.
4. Complete Store listing, App access, Ads, Target audience, Content rating,
   Data safety, Privacy policy, VpnService, and Foreground service forms.
5. Upload the VPN declaration video and any requested review instructions or
   test profile. Never provide a production credential.
6. Run the pre-launch report and test the Play-delivered build on the Xiaomi
   14 and at least one Android emulator or device outside the Xiaomi/HyperOS
   family.
7. Resolve policy and technical findings, then promote the same approved
   artifact to Production using a staged rollout.

Official references:

- Play Console account requirements:
  https://support.google.com/googleplay/android-developer/answer/10788890
- VpnService policy:
  https://support.google.com/googleplay/android-developer/answer/12564964
- User Data policy:
  https://support.google.com/googleplay/android-developer/answer/10144311
- Data safety:
  https://support.google.com/googleplay/android-developer/answer/10787469
- Target API requirements:
  https://support.google.com/googleplay/android-developer/answer/11926878
- Store preview assets:
  https://support.google.com/googleplay/android-developer/answer/9866151

Google Play approval does not establish that distributing or operating a VPN
is lawful in every country. Obtain jurisdiction-specific legal advice before
targeting or operating in regulated markets.
