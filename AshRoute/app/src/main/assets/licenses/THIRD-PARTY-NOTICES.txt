# Third-party notices

AshRoute includes or builds against the following open-source components.
Copyright remains with each component's authors and contributors.

## v2rayNG

- Source: https://github.com/2dust/v2rayNG
- AshRoute base revision: `6fe3893bfd37865dc677111fa48aef14c0a41efd`
- License: GNU General Public License, version 3

AshRoute is a modified version of v2rayNG. It is not affiliated with or
endorsed by the v2rayNG maintainers.

## AndroidLibXrayLite

- Source: https://github.com/2dust/AndroidLibXrayLite
- Revision: `ea96a7f9c33d6e18021386db96bf95680e853c93`
- Release: `v26.9.30`
- License: GNU Lesser General Public License, version 3

## Xray-core

- Source: https://github.com/XTLS/Xray-core
- Revision: `b26a91de4f32`
- License: Mozilla Public License, version 2.0

Xray-core is included through AndroidLibXrayLite.

## hev-socks5-tunnel

- Source: https://github.com/heiher/hev-socks5-tunnel
- Revision: `d9dca26c7ad0e494492244f0309e80ee583e739e`
- Release: `2.18.0`
- License: MIT License

## Android and JVM dependencies

Licenses for Gradle-resolved Android and JVM dependencies are included in
`AshRoute/app/src/main/assets/open_source_licenses.html` and are available
from the Open-source licenses entry in the app.

## Corresponding source

The source for each AshRoute release is available at:

https://github.com/yjd953/AshRoute

Clone a release recursively so the native source revisions are included:

```sh
git clone --recursive --branch <release-tag> https://github.com/yjd953/AshRoute.git
```
