#!/usr/bin/env bash
# Applies the small app-specific edits to the pinned upstream Limbo checkout.
# The customized activity itself lives in kitkat-vm/app-src/ so it is easy to browse.
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 /path/to/limbo-checkout" >&2
  exit 2
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
LIMBO_DIR="$1"

ACTIVITY_REL="limbo-android-x86/src/main/java/com/limbo/emu/main/LimboEmuActivity.java"
CONFIG_REL="limbo-android-lib/src/main/java/com/max2idea/android/limbo/main/Config.java"
MANIFEST_REL="limbo-android-x86/src/main/AndroidManifest.xml"
GRADLE_REL="limbo-android-x86/build.gradle"

for file in "$ACTIVITY_REL" "$CONFIG_REL" "$MANIFEST_REL" "$GRADLE_REL"; do
  if [[ ! -f "$LIMBO_DIR/$file" ]]; then
    echo "Expected upstream source file not found: $LIMBO_DIR/$file" >&2
    exit 1
  fi
done

# Replace the upstream activity with the complete, readable customized source.
cp "$ROOT_DIR/kitkat-vm/app-src/com/limbo/emu/main/LimboEmuActivity.java" \
   "$LIMBO_DIR/$ACTIVITY_REL"

# Keep conservative CPU defaults and make the app identity distinct.
sed -i 's/public static final String APP_NAME = "Limbo Emulator";/public static final String APP_NAME = "KitKat VM (Preview)";/' \
  "$LIMBO_DIR/$CONFIG_REL"
sed -i 's/public static final int MAX_CPU_NUM = 8;/public static final int MAX_CPU_NUM = 4;/' \
  "$LIMBO_DIR/$CONFIG_REL"

# Use a unique package name and retain compatibility with the older storage API.
sed -i 's/android:label="Limbo x86 PC Emulator"/android:label="KitKat VM (Preview)" android:requestLegacyExternalStorage="true"/' \
  "$LIMBO_DIR/$MANIFEST_REL"

# Build only an ARM64 Android host APK.
sed -i 's/abiFilters "armeabi-v7a","arm64-v8a", "x86", "x86_64"/abiFilters "arm64-v8a"/' \
  "$LIMBO_DIR/$GRADLE_REL"
sed -i 's/applicationId "com.limbo.emu.main"/applicationId "com.goingto2014.androidtest.kitkatvm"/' \
  "$LIMBO_DIR/$GRADLE_REL"

# Fail early if upstream source drifted and a change did not apply.
grep -q 'Config.enableKVM = false;' "$LIMBO_DIR/$ACTIVITY_REL"
grep -q 'getFilesDir().getAbsolutePath() + "/limbo-x86-log.txt"' "$LIMBO_DIR/$ACTIVITY_REL"
grep -q 'MENU_DOWNLOAD_KITKAT_ISO' "$LIMBO_DIR/$ACTIVITY_REL"
grep -q 'MAX_CPU_NUM = 4' "$LIMBO_DIR/$CONFIG_REL"
grep -q 'android:requestLegacyExternalStorage="true"' "$LIMBO_DIR/$MANIFEST_REL"
grep -q 'abiFilters "arm64-v8a"' "$LIMBO_DIR/$GRADLE_REL"
grep -q 'applicationId "com.goingto2014.androidtest.kitkatvm"' "$LIMBO_DIR/$GRADLE_REL"

echo "Applied KitKat VM app source and packaging changes."
