# AshRoute

AshRoute is an independent, open-source Android proxy client powered by
[Xray-core](https://github.com/XTLS/Xray-core). It lets users connect to
servers they configure themselves. AshRoute does not provide, sell, or
operate VPN servers or subscriptions.

> AshRoute is a modified version of
> [v2rayNG](https://github.com/2dust/v2rayNG). It is not affiliated with or
> endorsed by the v2rayNG maintainers.

## Changes from v2rayNG

The AshRoute derivative was established by
[yjd953](https://github.com/yjd953) on October 10, 2026. Initial changes
include:

- independent AshRoute branding, application ID, deep link, and release channel;
- independent privacy disclosures, source links, and update metadata;
- a restrained black, graphite, and vermilion visual system;
- Google Play policy preparation for `VpnService` and installed-app visibility.

Later changes are documented in this repository's releases and commits.

## Privacy and security

AshRoute has no analytics, advertising SDK, account system, or
developer-operated backend. Configuration and per-app routing data remain on
the device unless the user explicitly exports or shares them.

Network traffic is sent to the proxy or VPN endpoint selected by the user.
That endpoint is outside AshRoute's control. Review the
[privacy policy](PRIVACY.md) before using or distributing the app.

## Build

The Android project is in `AshRoute/`. Native dependencies are pinned as Git
submodules and are built or downloaded by the workflow in
`.github/workflows/build.yml`.

```sh
git clone --recursive https://github.com/yjd953/AshRoute.git
cd AshRoute
./AshRoute/gradlew -p AshRoute :app:assemblePlaystoreDebug
```

## Open-source origin and license

AshRoute remains licensed as a whole under the
[GNU General Public License v3.0](LICENSE). Copyright in the upstream code
remains with the v2rayNG authors and contributors. Copyright in later
AshRoute-specific changes remains with their respective contributors.

See [NOTICE](NOTICE) and [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for
attribution, pinned component revisions, and license details. Distributing an
APK or Android App Bundle requires making the corresponding source available
under GPL-3.0.
