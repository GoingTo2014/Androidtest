# KitKat Antivirus Demo

A simulated antivirus interface inspired by Android 4.x Holo-style apps. Built with Java and native Android APIs. Minimum Android version: 4.4 (API 19). Designed to install on modern Android versions too.

## Demo features

- Classic Android Holo-style interface.
- Dashboard with sample security status and demo statistics.
- Animated simulated scan with progress and a fictional result.
- Sample app list, example warning, quarantine screen, and scan history.
- Demo-only settings switches and information dialogs.
- All sample entries and results are fictional. The UI does not inspect the device.

## Important: this is not an antivirus

This app is a UI demo only. It does **not** scan installed apps or files, calculate hashes, inspect permissions, quarantine files, provide real-time protection, send notifications, or run background scans. Buttons and settings only simulate an antivirus experience.

The Android manifest declares **no permissions**. The app has no account, analytics, internet access, or network calls. Demo settings are temporary and are not saved. Do not rely on this app to protect a device; keep Google Play Protect or another trusted security product enabled.

## Build with GitHub Actions

1. Open the repository's **Actions** tab.
2. Select **Build Android Antivirus APK**.
3. Click **Run workflow** and select `main`.
4. Open the completed workflow run.
5. Under **Artifacts**, download `kitkat-antivirus-debug-apk` and extract `app-debug.apk`.

The workflow uses Java 17, Android SDK Platform 35, and Gradle 8.7. No Gradle wrapper or signing secrets are required. The artifact is a debug APK for testing, not a Play Store release. The workflow is manually triggered; commits do not automatically build an APK.

## Local build

Install JDK 17, Android SDK Platform 35 / Build Tools 35.0.0, and Gradle 8.7, then run:

```bash
gradle --no-daemon assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.
