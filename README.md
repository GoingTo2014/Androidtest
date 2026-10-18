# KitKat Antivirus

An offline Android utility with a classic Android 4.x Holo-style interface. Built with Java and native Android APIs, minimum Android 4.4 (API 19), and designed to install on modern Android versions.

## Current features

- Native classic Android interface.
- Lets the user choose one APK file through Android's file picker.
- Calculates the selected file's SHA-256 hash locally.
- No installed-app enumeration or inspection of other apps' permissions.
- No INTERNET permission, account, analytics, or network calls.
- GitHub Actions builds a debug APK only when manually triggered.

## Important limitations

This test build is an offline APK hash utility, not a malware scanner and not a replacement for Google Play Protect. It does not compare hashes against a malware database or determine whether an APK is safe. A hash is only an identifier for file contents.

The app does not request broad package visibility and does not scan installed apps. Android may still show warnings based on the APK's signing identity, reputation, target SDK, or other signals. Removing package scanning cannot guarantee that Play Protect will stop warning about the app.

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
