package com.limbo.emu.main;

import android.Manifest;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.max2idea.android.limbo.log.Logger;
import com.max2idea.android.limbo.main.Config;
import com.max2idea.android.limbo.main.LimboActivity;
import com.max2idea.android.limbo.links.LinksManager;
import com.max2idea.android.limbo.main.LimboApplication;

/**
 * Application entry point customized for the KitKat VM build.
 *
 * The virtual machine engine and most emulator UI are provided by Limbo.
 * This class disables KVM and adds a small menu for downloading the official
 * Android-x86 4.4-r4 guest ISO.
 */
public class LimboEmuActivity extends LimboActivity {
    private static final int MENU_DOWNLOAD_KITKAT_ISO = 0x4B49534F;
    private static final int REQUEST_KITKAT_ISO_STORAGE = 4144;

    private static final String KITKAT_ISO_FILE = "android-x86-4.4-r4.iso";
    private static final String KITKAT_ISO_URL_SOURCEFORGE =
            "https://sourceforge.net/projects/android-x86/files/Release%204.4/android-x86-4.4-r4.iso/download";
    private static final String KITKAT_ISO_URL_FOSSHUB =
            "https://www.fosshub.com/Android-x86.html/android-x86-4.4-r4.iso";
    private static final String KITKAT_RELEASE_NOTES_URL =
            "https://www.android-x86.org/releases/releasenote-4-4-r4.html";
    private static final String KITKAT_ISO_SHA1 =
            "2a33c508a632dd228928816a30fe44eff0da4728";

    private String pendingKitKatIsoUrl = KITKAT_ISO_URL_SOURCEFORGE;
    private String pendingKitKatIsoSourceName = "SourceForge (project download site)";

    @Override
    public void onCreate(Bundle bundle) {
        LimboApplication.arch = Config.Arch.x86_64;
        Config.clientClass = this.getClass();

        // KVM is unavailable on ordinary Android devices and must not be used.
        Config.enableKVM = false;

        // XXX: only for 64-bit hosts; QEMU must support x86_64-softmmu.
        if (LimboApplication.isHost64Bit() && Config.enableMTTCG) {
            Config.enableMTTCG = true;
        } else {
            Config.enableMTTCG = false;
        }

        Config.osImages.put(getString(R.string.SlaxLinux),
                new LinksManager.LinkInfo(getString(R.string.SlaxLinux),
                        getString(R.string.SlaxLinuxDescr),
                        "https://github.com/limboemu/limbo/wiki/Slax",
                        LinksManager.LinkType.ISO));
        Config.osImages.put(getString(R.string.SlitazLinux),
                new LinksManager.LinkInfo(getString(R.string.SlitazLinux),
                        getString(R.string.SlitazLinuxDescr),
                        "https://github.com/limboemu/limbo/wiki/Slitaz",
                        LinksManager.LinkType.ISO));
        Config.osImages.put(getString(R.string.DSLLinux),
                new LinksManager.LinkInfo(getString(R.string.DSLLinux),
                        getString(R.string.DSLLinuxDescr),
                        "https://github.com/limboemu/limbo/wiki/DSL-linux",
                        LinksManager.LinkType.ISO));
        Config.osImages.put(getString(R.string.DebianLinux),
                new LinksManager.LinkInfo(getString(R.string.DebianLinux),
                        getString(R.string.DebianLinuxDescr),
                        "https://github.com/limboemu/limbo/wiki/Debian-Linux",
                        LinksManager.LinkType.ISO));
        Config.osImages.put("Trinux",
                new LinksManager.LinkInfo(getString(R.string.Trinux),
                        getString(R.string.TrinuxDescr),
                        "https://github.com/limboemu/limbo/wiki/Trinux",
                        LinksManager.LinkType.ISO));
        Config.osImages.put(getString(R.string.Freedos),
                new LinksManager.LinkInfo(getString(R.string.Freedos),
                        getString(R.string.FreedosDescr),
                        "https://github.com/limboemu/limbo/wiki/FreeDOS",
                        LinksManager.LinkType.ISO));
        Config.osImages.put(getString(R.string.KolibriOS),
                new LinksManager.LinkInfo(getString(R.string.KolibriOS),
                        getString(R.string.KolibriOSDescr),
                        "https://github.com/limboemu/limbo/wiki/KolibriOS",
                        LinksManager.LinkType.ISO));

        super.onCreate(bundle);

        // Keep logs in app-private storage rather than a legacy root-level path.
        Logger.setupLogFile(getFilesDir().getAbsolutePath() + "/limbo-x86-log.txt");
    }

    @Override
    protected void loadQEMULib() {
        try {
            System.loadLibrary("qemu-system-i386");
        } catch (Error ex) {
            System.loadLibrary("qemu-system-x86_64");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean result = super.onPrepareOptionsMenu(menu);
        // Avoid adding duplicate menu items whenever Android rebuilds the menu.
        menu.removeItem(MENU_DOWNLOAD_KITKAT_ISO);
        menu.add(0, MENU_DOWNLOAD_KITKAT_ISO, 100, "Download KitKat ISO")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_DOWNLOAD_KITKAT_ISO) {
            promptKitKatIsoDownload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void promptKitKatIsoDownload() {
        final String[] sourceNames = {
                "SourceForge (project download site)",
                "FossHub (alternative listed by Android-x86)"
        };
        final String[] sourceUrls = {
                KITKAT_ISO_URL_SOURCEFORGE,
                KITKAT_ISO_URL_FOSSHUB
        };

        new AlertDialog.Builder(this)
                .setTitle("Choose official Android-x86 mirror")
                .setItems(sourceNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmKitKatIsoDownload(sourceNames[which], sourceUrls[which]);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmKitKatIsoDownload(final String sourceName, final String sourceUrl) {
        String message = "Downloads the official Android-x86 4.4-r4 ISO (about 451 MB)."
                + "\n\nSelected source: " + sourceName
                + "\n\nExact download request URL:\n" + sourceUrl
                + "\n\nOfficial release notes:\n" + KITKAT_RELEASE_NOTES_URL
                + "\n\nSHA-1 published by the release notes:\n" + KITKAT_ISO_SHA1
                + "\n\nThe selected host may redirect to a mirror. The ISO will be saved as Downloads/"
                + KITKAT_ISO_FILE + ". Mobile data charges may apply.";

        new AlertDialog.Builder(this)
                .setTitle("Confirm ISO download")
                .setMessage(message)
                .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestKitKatIsoStoragePermission(sourceUrl, sourceName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void requestKitKatIsoStoragePermission(final String sourceUrl,
                                                   final String sourceName) {
        pendingKitKatIsoUrl = sourceUrl;
        pendingKitKatIsoSourceName = sourceName;

        if (Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_KITKAT_ISO_STORAGE);
            return;
        }

        enqueueKitKatIsoDownload(sourceUrl, sourceName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_KITKAT_ISO_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enqueueKitKatIsoDownload(pendingKitKatIsoUrl, pendingKitKatIsoSourceName);
            } else {
                Toast.makeText(this,
                        "Storage permission is required to save the ISO in Downloads.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enqueueKitKatIsoDownload(String sourceUrl, String sourceName) {
        DownloadManager manager =
                (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (manager == null) {
            Toast.makeText(this, "Android Download Manager is unavailable.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            DownloadManager.Request request =
                    new DownloadManager.Request(Uri.parse(sourceUrl));
            request.setTitle(KITKAT_ISO_FILE);
            request.setDescription("Official Android-x86 4.4-r4 ISO via " + sourceName);
            request.setMimeType("application/octet-stream");
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setAllowedOverRoaming(false);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, KITKAT_ISO_FILE);

            long id = manager.enqueue(request);
            Toast.makeText(this,
                    "ISO download queued. Follow progress in Android notifications.\nDownload ID: " + id,
                    Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(this,
                    "Android denied access to Downloads. Grant storage access and try again.",
                    Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Could not start the ISO download: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
