# KitKat VM prototype

This folder documents the Android 4.4 VM build. The APK is built from the open-source Limbo/QEMU emulator project as a separate app; the existing Androidtest antivirus demo remains untouched.

## Target and safety

- Targets modern **ARM64 Android tablets**, including Android 14 and newer. The upstream app's minimum SDK is 21; the build keeps its legacy target SDK to avoid introducing unrelated Android 14 behavior changes into this older codebase.
- **No root is needed or used.** KVM is disabled both when QEMU is compiled and in the Android activity, so the app does not try to use `/dev/kvm`.
- The VM is real PC emulation, not a simulated KitKat screen. Users must download an Android-x86 4.4-r4 ISO themselves; it is not bundled in this APK.
- CPU count is configurable in the VM interface, capped at 4 virtual CPUs to help avoid accidental over-allocation. RAM, virtual disk, display, networking, and other machine options are configurable there too.
- The workflow builds only the ARM64 host APK to reduce package size. It is intended for ARM64 tablets, not Android-x86 phones/tablets.

## Performance expectations

Android-x86 4.4 runs as an x86 guest on an ARM64 tablet. QEMU has to translate guest CPU instructions in software, so performance depends heavily on the tablet and cannot be guaranteed to feel fast. The build uses optimized native libraries, enables QEMU's multi-threaded TCG option for 64-bit hosts, and limits the native build to two parallel build jobs to avoid excessive CI memory use. Runtime translation can still use substantial CPU while the VM is running.

For an initial setup, try **512–768 MB RAM and 1 virtual CPU**. Test 2 virtual CPUs only if the tablet remains responsive. More RAM/CPUs do not automatically make a VM faster; assigning too much can slow down Android itself. Lower guest resolution and disabling guest sound may also reduce overhead.

The first build uses upstream Limbo's existing VM configuration interface rather than reimplementing the emulation engine. The legacy native build may require maintenance. GitHub Actions logs are the source of truth for whether the build succeeds; an APK has not been validated on a physical Android 14 tablet yet.

## Build an APK from your tablet

1. Open the repository's **Actions** tab.
2. Select **Build KitKat VM (ARM64)** and choose **Run workflow**.
3. Wait for the workflow to finish.
4. Download the `kitkat-vm-arm64-debug-apk` artifact from the completed run and extract the APK.
5. Install it on Android 14 or newer. Android may ask you to allow installs from your browser.

This workflow is manual only; ordinary commits will not trigger the VM build.

## Get the guest OS

Download the official [Android-x86 4.4-r4 release](https://www.android-x86.org/releases/releasenote-4-4-r4.html) on the tablet and keep the ISO in local storage.

In the emulator, create a VM using the x86_64 machine, attach the ISO as its CD-ROM, create a virtual hard disk, then boot and install Android-x86 to that virtual disk. The first boot can take a long time. Some guest hardware, sound, networking, graphics acceleration, suspend/resume, and touch behavior may be limited.

## Engine and licensing

- Emulator engine: [Limbo (QEMU for Android)](https://github.com/limboemu/limbo), built from pinned upstream source.
- Guest OS: [Android-x86 4.4-r4](https://www.android-x86.org/releases/releasenote-4-4-r4.html), downloaded separately by the user.
- Limbo and QEMU have their own license and redistribution requirements. Review the upstream `COPYING` file before redistributing modified builds.
