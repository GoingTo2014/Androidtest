# Android 4.4 UI Showcase

A native Android widget playground inspired by the Holo Light theme from Android 4.4 KitKat.

## Compatibility

- Minimum Android version: Android 4.4 (API 19)
- Compile SDK: Android 15 / API 35
- Java source level: 8
- Uses native Android widgets and no third-party runtime libraries.
- Installs on modern Android versions too. System-owned surfaces (such as permission prompts) use the OS version's design.

## Build with GitHub Actions

1. Open the repository's **Actions** tab.
2. Select **Build Android APK**.
3. Click **Run workflow** and select the `main` branch, or wait for a push that changes app/build files.
4. Open the completed workflow run.
5. Under **Artifacts**, download `androidtest-debug-apk` and extract `app-debug.apk`.

The workflow installs Java 17 and the Android SDK, then runs `gradle assembleDebug`. It does not require a checked-in Gradle wrapper or signing secrets. The generated debug APK is for testing and can be installed directly after enabling installation from the chosen source on your device.

## Build locally

Install JDK 17 and Android SDK Platform 35 / Build Tools 35.0.0, then run:

```bash
gradle --version
gradle assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Included demos

Text and password inputs, buttons, toggles, checkboxes, radio buttons, switches, spinners, autocomplete, seek/progress/rating bars, dialogs, date/time pickers, popup/context menus, list/grid views, expandable sections, horizontal scrolling, built-in icons, clipboard, Toasts, local HTML in a WebView, media-control mockups, and device information.

Media controls are UI demonstrations, not a real media player. The app does not request internet permission and does not send data over the network.
