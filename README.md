# KitKat Antivirus

An offline Android security prototype with a classic Android 4.x Holo-style interface. Built with Java and native Android APIs, minimum Android 4.4 (API 19), and designed to install on modern Android versions.

## Current features

- Native classic Android interface.
- Scans installed packages visible to the app.
- Reviews a small set of sensitive permission combinations and labels them as review indicators, not proof of malware.
- Calculates SHA-256 hashes for readable installed APK files.
- Lets the user select an APK file and calculates its SHA-256 hash offline.
- Compares hashes with a local known-threat signature set.
- No INTERNET permission, account, analytics, or network calls.
- GitHub Actions builds a debug APK only when manually triggered.

## Important detection limitations

This is an early prototype, not a replacement for Google Play Protect or a production antivirus. The bundled known-threat hash list is intentionally empty until verified signatures are added. A hash that does not match a local signature is not proof that a file is safe. Permission heuristics can produce false positives and miss malware. The APK-file flow currently hashes the selected file but does not yet fully parse or statically analyze its contents.

Android limits app visibility and access to other apps' files depending on OS version, target SDK, and device policy. Some APK paths may not be readable. The app cannot silently uninstall other apps or scan every private app data directory.

## Build with GitHub Actions

1. Open the repository's **Actions** tab.
2. Select **Build Android Antivirus APK**.
3. Click **Run workflow** and select `main`.
4. Open the completed workflow run.
5. Under **Artifacts**, download `kitkat-antivirus-debug-apk` and extract `app-debug.apk`.

The workflow uses Java 17, Android SDK Platform 35, and Gradle 8.7. No Gradle wrapper or signing secrets are required. The artifact is a debug APK for testing, not a Play Store release.

## Local build

Install JDK 17, Android SDK Platform 35 / Build Tools 35.0.0, and Gradle 8.7, then run:

```bash
gradle --no-daemon assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.
