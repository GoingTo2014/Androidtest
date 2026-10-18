# KitKat VM prototype

This folder documents the Android 4.4 VM build. The APK is built from the open-source Limbo/QEMU emulator project; the existing Androidtest app remains untouched.

## What this build is

- A real QEMU PC-emulation app, not a simulated KitKat UI.
- Built for **ARM64 Android tablets** and Android 14+ compatibility.
- QEMU's **x86_64 system emulator** can boot a 32-bit x86 guest such as Android-x86 4.4-r4.
- Users choose the guest ISO and can configure guest RAM, CPU count, storage, and other machine options in the emulator UI.
- QEMU software emulation only: KVM acceleration is explicitly disabled. No root access or `/dev/kvm` is used or required.

## Performance expectations

Android-x86 4.4 is an x86 guest running on an ARM64 tablet. Because CPU architectures differ, QEMU must translate guest instructions in software. This will be slower than native Android and cannot be promised to feel fast on every tablet. The build targets only ARM64 to keep the APK smaller. RAM and CPU settings are configurable, but allocating more guest RAM/cores does not guarantee better speed and can make the host tablet less responsive. Start with 768 MB RAM and 1 virtual CPU; test 2 CPUs only if the tablet remains responsive.

The first build uses upstream Limbo's existing VM configuration UI rather than reimplementing the emulation engine. Native compilation may need maintenance because Limbo's upstream Android Gradle and QEMU build scripts are old. GitHub Actions logs are the source of truth for whether a given build succeeds.

## Build an APK from your tablet

1. Open the repository's **Actions** tab.
2. Select **Build KitKat VM (ARM64)** and choose **Run workflow**.
3. Wait for the workflow to finish.
4. Download the `kitkat-vm-arm64-debug-apk` artifact from the completed run and extract the APK.
5. Install the APK on Android 14 or newer. Android may ask you to allow installs from your browser.

This workflow is manual only; ordinary commits will not trigger the VM build.

## Get the guest OS

Download the official [Android-x86 4.4-r4 release](https://www.android-x86.org/releases/releasenote-4-4-r4.html) on the tablet and keep the ISO in local storage. The ISO is not bundled in the APK because it is a separate, large operating-system image.

In the emulator, create a VM using the x86_64 machine, attach the ISO as its CD-ROM, create a virtual hard disk, then boot and install Android-x86 to that virtual disk. The first boot can take a long time. Some guest hardware, sound, networking, graphics acceleration, suspend/resume, and touch behavior may be limited.

## Engine and licensing

- Emulator engine: [Limbo (QEMU for Android)](https://github.com/limboemu/limbo), built from upstream source.
- Guest OS: [Android-x86 4.4-r4](https://www.android-x86.org/releases/releasenote-4-4-r4.html), downloaded separately by the user.
- Limbo and QEMU have their own license and redistribution requirements. Review the upstream `COPYING` file before redistributing modified builds.
