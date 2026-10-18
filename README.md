# KitKat VM for Android

This repository builds a rootless Android 4.4 KitKat virtual machine app for ARM64 Android devices. The old antivirus demo has been removed from `main`.

## Browse the source

The custom app code is stored in the repository instead of being embedded inside the GitHub Actions workflow:

- **Customized Android activity:** [`LimboEmuActivity.java`](kitkat-vm/app-src/com/limbo/emu/main/LimboEmuActivity.java)
- **Source preparation / packaging adjustments:** [`prepare-limbo-source.sh`](kitkat-vm/scripts/prepare-limbo-source.sh)
- **Build workflow:** [`build-kitkat-vm.yml`](.github/workflows/build-kitkat-vm.yml)
- **Build and usage notes:** [`kitkat-vm/README.md`](kitkat-vm/README.md)

The emulator engine and standard Limbo UI are based on the pinned [upstream Limbo Android source](https://github.com/limboemu/limbo/tree/887c6a68cd6b414377d7f8071bc12bbc16e59809/limbo-android-x86). The workflow checks out that exact upstream commit, copies in this repository's customized activity, applies the small packaging changes from the shell script, then builds the APK. This keeps the workflow focused on build steps while making our own app changes easy to browse and edit.

## Build the APK

1. Open **Actions**.
2. Select **Build KitKat VM (ARM64)**.
3. Select **Run workflow**.
4. Download the `kitkat-vm-arm64-debug-apk` artifact if the run succeeds.

The workflow is manual only, so regular commits do not start a build. The APK has not yet been confirmed to build successfully or tested on a physical tablet.

## Notes

- No root is needed; KVM is disabled.
- Android-x86 4.4 runs as an x86 guest on an ARM64 device, so software CPU translation can be slow.
- The in-app ISO downloader offers SourceForge and FossHub, both listed by the [official Android-x86 4.4-r4 release notes](https://www.android-x86.org/releases/releasenote-4-4-r4.html).
- Review Limbo and QEMU licensing requirements before redistributing builds.
