#!/usr/bin/env bash
set -euo pipefail

APK="${1:?Usage: verify-play-apk.sh <universal-apk>}"
ANDROID_HOME="${ANDROID_HOME:?ANDROID_HOME must point to the Android SDK}"
BUILD_TOOLS_VERSION="${BUILD_TOOLS_VERSION:-37.0.0}"
AAPT="$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION/aapt"
ZIPALIGN="$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION/zipalign"

[[ -f "$APK" ]] || {
  echo "APK not found: $APK" >&2
  exit 1
}

BADGING="$("$AAPT" dump badging "$APK")"
grep -Fq "package: name='io.github.yjd953.ashroute'" <<<"$BADGING"

TARGET_SDK="$(sed -n "s/^targetSdkVersion:'\\([0-9][0-9]*\\)'.*/\\1/p" <<<"$BADGING")"
if [[ -z "$TARGET_SDK" || "$TARGET_SDK" -lt 36 ]]; then
  echo "Play APK must target API 36 or later; found ${TARGET_SDK:-unknown}" >&2
  exit 1
fi

PACKAGED_FILES="$(unzip -Z1 "$APK")"
REQUIRED_ASSETS=(
  "assets/legal_notices.html"
  "assets/open_source_licenses.html"
  "assets/licenses/AshRoute-GPL-3.0.txt"
  "assets/licenses/AshRoute-NOTICE.txt"
  "assets/licenses/THIRD-PARTY-NOTICES.txt"
  "assets/licenses/AndroidLibXrayLite-LGPL-3.0.txt"
  "assets/licenses/Xray-core-MPL-2.0.txt"
  "assets/licenses/hev-socks5-tunnel-MIT.txt"
)
for asset in "${REQUIRED_ASSETS[@]}"; do
  grep -Fxq "$asset" <<<"$PACKAGED_FILES" || {
    echo "Required legal notice is missing from APK: $asset" >&2
    exit 1
  }
done

REQUIRED_LIBRARIES=(
  "libgojni.so"
  "libhev-socks5-tunnel.so"
  "libhevsockstun.so"
)
for abi in arm64-v8a armeabi-v7a x86_64 x86; do
  for library in "${REQUIRED_LIBRARIES[@]}"; do
    grep -Fxq "lib/$abi/$library" <<<"$PACKAGED_FILES" || {
      echo "Required native library is missing from APK: lib/$abi/$library" >&2
      exit 1
    }
  done
done

"$ZIPALIGN" -c -P 16 -v 4 "$APK" >/dev/null

if [[ "${CHECK_ELF_ALIGNMENT:-1}" == "1" ]]; then
  LLVM_OBJDUMP="${LLVM_OBJDUMP:-}"
  if [[ -z "$LLVM_OBJDUMP" ]]; then
    LLVM_OBJDUMP="$(find "${NDK_HOME:?NDK_HOME must point to the Android NDK}"/toolchains/llvm/prebuilt \
      \( -type f -o -type l \) -name llvm-objdump -print -quit)"
  fi
  [[ -x "$LLVM_OBJDUMP" ]] || {
    echo "llvm-objdump not found: $LLVM_OBJDUMP" >&2
    exit 1
  }

  TMP_DIR="$(mktemp -d)"
  trap 'rm -rf "$TMP_DIR"' EXIT
  unzip -q "$APK" 'lib/arm64-v8a/*.so' 'lib/x86_64/*.so' -d "$TMP_DIR"

  while IFS= read -r library; do
    LOAD_SEGMENTS="$("$LLVM_OBJDUMP" -p "$library" | grep 'LOAD off')"
    if grep -Eq 'align 2\*\*([0-9]|1[0-3])([^0-9]|$)' <<<"$LOAD_SEGMENTS"; then
      echo "64-bit library is not 16 KB ELF-aligned: $library" >&2
      exit 1
    fi
  done < <(find "$TMP_DIR/lib/arm64-v8a" "$TMP_DIR/lib/x86_64" -type f -name '*.so' | sort)
  echo "Verified Play APK: package, target API, notices, ABIs, and 16 KB alignment"
else
  echo "Verified Play APK: package, target API, notices, and ABIs (ELF alignment skipped)"
fi
