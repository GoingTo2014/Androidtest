# KitKat VM for Android

This repository now focuses on the **rootless Android 4.4 KitKat virtual machine app** for ARM64 Android tablets. The old antivirus demo has been removed from the current `main` branch.

## Where is the source code?

The app is built from the open-source [Limbo/QEMU Android app](https://github.com/limboemu/limbo/tree/887c6a68cd6b414377d7f8071bc12bbc16e59809/limbo-android-x86), pinned to a specific upstream commit. The build workflow downloads that source, applies the KitKat VM app changes, compiles QEMU, and packages the APK.

- **Build script and custom app changes:** [`.github/workflows/build-kitkat-vm.yml`](.github/workflows/build-kitkat-vm.yml)
- **VM setup, performance notes, ISO download and build instructions:** [`kitkat-vm/README.md`](kitkat-vm/README.md)
- **Official Android-x86 KitKat 4.4-r4 release notes:** https://www.android-x86.org/releases/releasenote-4-4-r4.html

The complete upstream emulator source is linked above; the customized source is currently prepared by the workflow at build time rather than stored as a complete standalone Limbo source tree in this repository. The VM app's activity source is [here](https://github.com/limboemu/limbo/blob/887c6a68cd6b414377d7f8071bc12bbc16e59809/limbo-android-x86/src/main/java/com/limbo/emu/main/LimboEmuActivity.java); this repository's custom downloader and build modifications are in the workflow.

## Build the APK

1. Open **Actions**.
2. Select **Build KitKat VM (ARM64)**.
3. Select **Run workflow**.
4. Download and extract the `kitkat-vm-arm64-debug-apk` artifact after the run finishes.

The workflow is manual only. The APK has not yet been confirmed to build successfully or tested on a physical tablet.

## Important limitations

- No root is needed. KVM is disabled.
- This is real x86 PC emulation on an ARM64 device, so it may be slow because guest CPU instructions are translated in software.
- Download Android-x86 through the in-app ISO download option. Choose SourceForge or FossHub; both are listed by the [official Android-x86 4.4-r4 release notes](https://www.android-x86.org/releases/releasenote-4-4-r4.html). The app displays the selected URL before starting.
- The VM uses upstream Limbo/QEMU; review upstream licensing before redistributing builds.
