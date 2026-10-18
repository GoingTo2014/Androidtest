# KitKat VM prototype

This folder contains this repository's visible app customization and build-preparation code for an Android 4.4 virtual machine app.

## Source layout

- `app-src/com/limbo/emu/main/LimboEmuActivity.java` is the customized activity source. It disables KVM, stores logs in app-private storage, and implements the in-app Android-x86 ISO download menu.
- `scripts/prepare-limbo-source.sh` copies that activity into the pinned Limbo checkout and applies the small app-name, CPU-limit, manifest, package-ID, and ABI-filter changes.
- `../.github/workflows/build-kitkat-vm.yml` sets up dependencies, checks out Limbo, invokes the preparation script, builds QEMU and Gradle, and uploads the APK.

The underlying emulator engine and much of the generic emulator UI remain from [Limbo](https://github.com/limboemu/limbo), pinned to commit [887c6a68cd6b414377d7f8071bc12bbc16e59809](https://github.com/limboemu/limbo/tree/887c6a68cd6b414377d7f8071bc12bbc16e59809). The customized activity and this project's own build changes are directly viewable here; they are no longer embedded as a long code block in the workflow.

## Target and safety

- Targets modern **ARM64 Android devices**. The build packages only the ARM64 host library and uses a unique application ID.
- **No root is needed or used.** KVM is disabled both in the activity and in the native QEMU build.
- This is real x86 PC emulation, not a simulated KitKat screen. The Android-x86 guest ISO is downloaded separately; it is not bundled in the APK.
- The virtual CPU count is capped at four. RAM, virtual disk, display, networking, and other machine options are configured in Limbo's VM interface.

## Performance expectations

Android-x86 4.4 runs as an x86 guest on an ARM64 device. QEMU translates guest CPU instructions in software, so performance depends heavily on the device. For an initial setup, try **512–768 MB RAM and 1 virtual CPU**. More assigned RAM or CPUs do not automatically make a VM faster. Lower guest resolution and disabling guest sound may also reduce overhead.

The legacy native build may require maintenance. GitHub Actions logs are the source of truth for whether a build succeeds; an APK has not yet been validated on a physical Android device.

## Build an APK

1. Open the repository's **Actions** tab.
2. Select **Build KitKat VM (ARM64)**, then choose **Run workflow**.
3. If the run succeeds, download the `kitkat-vm-arm64-debug-apk` artifact.
4. Extract the APK and install it on your ARM64 Android device. Android may ask you to allow installs from your browser.

The workflow is manual only; normal commits do not trigger a build.

## Download the guest OS

In the app's overflow menu, select **Download KitKat ISO** and choose a source. SourceForge and FossHub are both listed by the official Android-x86 4.4-r4 release notes.

- Official release notes: https://www.android-x86.org/releases/releasenote-4-4-r4.html
- SourceForge: https://sourceforge.net/projects/android-x86/files/Release%204.4/android-x86-4.4-r4.iso/download
- FossHub: https://www.fosshub.com/Android-x86.html/android-x86-4.4-r4.iso
- Published SHA-1: `2a33c508a632dd228928816a30fe44eff0da4728`

The app shows the selected request URL and published checksum before downloading. The selected host may redirect to a delivery mirror, and the app does not automatically verify the downloaded file's checksum. Compare the downloaded ISO's SHA-1 with the published value for an integrity check.

Create an x86_64 VM, attach the ISO as its CD-ROM, create a virtual hard disk, then boot and install Android-x86 onto that disk. First boot may take a while. Some guest hardware, audio, networking, graphics acceleration, suspend/resume, and touch behavior may be limited.

## Engine and licensing

- Emulator engine: [Limbo (QEMU for Android)](https://github.com/limboemu/limbo).
- Guest OS: [Android-x86 4.4-r4](https://www.android-x86.org/releases/releasenote-4-4-r4.html).
- Review the upstream Limbo and QEMU license files and redistribution requirements before sharing modified builds.
