package com.example.androidtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int REQUEST_PICK_APK = 41;
    private static final int BG = Color.rgb(245, 245, 245);
    private static final int INK = Color.rgb(33, 33, 33);
    private static final int TEAL = Color.rgb(0, 150, 136);
    private static final int RED = Color.rgb(198, 40, 40);
    private static final int ORANGE = Color.rgb(239, 108, 0);

    // Add verified SHA-256 hashes of confirmed malware here as the local signature
    // database is maintained. Never label an app malware from a guess alone.
    private static final Set<String> KNOWN_BAD_SHA256;
    static {
        Set<String> hashes = new HashSet<String>();
        KNOWN_BAD_SHA256 = Collections.unmodifiableSet(hashes);
    }

    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private LinearLayout root;
    private TextView overallStatus;
    private TextView countText;
    private TextView detailText;
    private ProgressBar progress;
    private Button scanButton;
    private Button apkButton;
    private final List<ScanResult> lastResults = new ArrayList<ScanResult>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(0, 121, 107));
        getWindow().setNavigationBarColor(Color.rgb(0, 77, 64));
        getWindow().setTitle("KitKat Antivirus");
        buildInterface();
    }

    private void buildInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(14), dp(16), dp(24));
        scroll.addView(root);
        setContentView(scroll);

        TextView title = text("KitKat Antivirus", 25, INK, true);
        root.addView(title, match());
        TextView subtitle = text("Offline protection • No account • No internet access", 13, Color.DKGRAY, false);
        subtitle.setPadding(0, dp(3), 0, dp(16));
        root.addView(subtitle, match());

        LinearLayout shield = new LinearLayout(this);
        shield.setGravity(Gravity.CENTER);
        shield.setOrientation(LinearLayout.VERTICAL);
        shield.setPadding(dp(14), dp(22), dp(14), dp(22));
        shield.setBackground(panel(Color.WHITE, TEAL));
        TextView symbol = text("✓", 44, TEAL, true);
        symbol.setGravity(Gravity.CENTER);
        shield.addView(symbol, match());
        overallStatus = text("Your device is ready to scan", 18, INK, true);
        overallStatus.setGravity(Gravity.CENTER);
        overallStatus.setPadding(0, dp(6), 0, dp(4));
        shield.addView(overallStatus, match());
        detailText = text("No scan has been run yet.", 13, Color.DKGRAY, false);
        detailText.setGravity(Gravity.CENTER);
        shield.addView(detailText, match());
        root.addView(shield, match());

        countText = text("Installed apps: not scanned", 14, INK, false);
        countText.setPadding(0, dp(14), 0, dp(8));
        root.addView(countText, match());

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setIndeterminate(false);
        progress.setMax(100);
        progress.setProgress(0);
        root.addView(progress, match());

        scanButton = button("SCAN INSTALLED APPS");
        scanButton.setOnClickListener(v -> scanInstalledApps());
        root.addView(scanButton, match());

        apkButton = button("CHECK AN APK FILE");
        apkButton.setOnClickListener(v -> pickApk());
        root.addView(apkButton, match());

        addSection("SCAN RESULTS");
        addButton("View latest results", v -> showResults());
        addButton("What does this scanner check?", v -> showAboutDetection());
        addButton("About KitKat Antivirus", v -> new AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("KitKat Antivirus is an offline security prototype. It checks accessible installed-app metadata, selected permission combinations, and SHA-256 hashes against a local signature list. It does not upload data or replace Google Play Protect.")
            .setPositiveButton("OK", null).show());

        TextView footer = text("Offline prototype • Detection is heuristic, not a guarantee", 12, Color.DKGRAY, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(18), 0, 0);
        root.addView(footer, match());
    }

    private void scanInstalledApps() {
        setBusy(true);
        overallStatus.setText("Scanning apps…");
        detailText.setText("Checking package metadata and accessible APK hashes.");
        progress.setIndeterminate(true);
        worker.execute(() -> {
            List<ScanResult> results = new ArrayList<ScanResult>();
            int unreadable = 0;
            try {
                PackageManager pm = getPackageManager();
                List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
                for (PackageInfo info : packages) {
                    ApplicationInfo app = info.applicationInfo;
                    if (app == null) continue;
                    String label;
                    try { label = pm.getApplicationLabel(app).toString(); }
                    catch (Exception e) { label = info.packageName; }
                    String hash = "";
                    try {
                        if (app.sourceDir != null) hash = sha256(new FileInputStream(app.sourceDir));
                    } catch (Exception ignored) { unreadable++; }
                    ScanResult result = evaluate(info, label, hash);
                    results.add(result);
                }
            } catch (Exception e) {
                final String message = e.getMessage() == null ? "Unknown scanning error" : e.getMessage();
                runOnUiThread(() -> {
                    setBusy(false);
                    progress.setIndeterminate(false);
                    overallStatus.setText("Scan could not finish");
                    detailText.setText(message);
                    Toast.makeText(this, "Unable to scan installed apps", Toast.LENGTH_LONG).show();
                });
                return;
            }
            Collections.sort(results, (a, b) -> Integer.compare(b.risk, a.risk));
            final int unreadableCount = unreadable;
            runOnUiThread(() -> {
                lastResults.clear();
                lastResults.addAll(results);
                int known = countRisk(results, 3);
                int suspicious = countRisk(results, 1);
                progress.setIndeterminate(false);
                progress.setProgress(100);
                setBusy(false);
                countText.setText("Apps checked: " + results.size() + " • APK hashes unavailable: " + unreadableCount);
                if (known > 0) {
                    overallStatus.setText("Known threat signature matched");
                    overallStatus.setTextColor(RED);
                } else if (suspicious > 0) {
                    overallStatus.setText("Review recommended");
                    overallStatus.setTextColor(ORANGE);
                } else {
                    overallStatus.setText("Scan complete");
                    overallStatus.setTextColor(TEAL);
                }
                detailText.setText(known + " known signature matches • " + suspicious
                    + " apps with suspicious indicators. This is not a guarantee of safety.");
                showResults();
            });
        });
    }

    private ScanResult evaluate(PackageInfo info, String label, String hash) {
        ScanResult result = new ScanResult();
        result.label = label;
        result.packageName = info.packageName;
        result.hash = hash;
        result.risk = 0;
        if (!hash.isEmpty() && KNOWN_BAD_SHA256.contains(hash.toLowerCase(Locale.US))) {
            result.risk = 3;
            result.reasons.add("SHA-256 matches a locally stored known-threat signature");
            return result;
        }

        Set<String> perms = new HashSet<String>();
        if (info.requestedPermissions != null) {
            Collections.addAll(perms, info.requestedPermissions);
        }
        boolean sms = perms.contains("android.permission.SEND_SMS")
            || perms.contains("android.permission.RECEIVE_SMS")
            || perms.contains("android.permission.READ_SMS");
        boolean contacts = perms.contains("android.permission.READ_CONTACTS");
        boolean calls = perms.contains("android.permission.READ_CALL_LOG");
        boolean install = perms.contains("android.permission.REQUEST_INSTALL_PACKAGES");
        boolean record = perms.contains("android.permission.RECORD_AUDIO");
        boolean internet = perms.contains("android.permission.INTERNET");

        if (sms && contacts) {
            result.risk++;
            result.reasons.add("Requests both SMS and contacts access");
        }
        if (sms && install) {
            result.risk++;
            result.reasons.add("Requests SMS access and permission to request app installs");
        }
        if (calls && contacts && internet) {
            result.risk++;
            result.reasons.add("Requests call-log, contacts, and internet access");
        }
        if (install) {
            result.risk++;
            result.reasons.add("Can request installation of other apps");
        }
        if (record && internet && contacts && result.risk == 0) {
            result.risk++;
            result.reasons.add("Requests microphone, contacts, and internet access");
        }
        if (result.risk >= 2) result.risk = 2;
        return result;
    }

    private int countRisk(List<ScanResult> results, int threshold) {
        int count = 0;
        for (ScanResult r : results) if (r.risk >= threshold) count++;
        return count;
    }

    private void pickApk() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try { startActivityForResult(Intent.createChooser(intent, "Select an APK file"), REQUEST_PICK_APK); }
        catch (Exception e) { Toast.makeText(this, "No file picker is available", Toast.LENGTH_LONG).show(); }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_APK || resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;
        setBusy(true);
        overallStatus.setText("Checking selected file…");
        progress.setIndeterminate(true);
        worker.execute(() -> {
            String hash = "";
            String error = null;
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                if (in == null) throw new Exception("Could not open selected file.");
                hash = sha256(in);
            } catch (Exception e) {
                error = e.getMessage() == null ? "Unable to read file" : e.getMessage();
            }
            final String finalHash = hash;
            final String finalError = error;
            runOnUiThread(() -> {
                setBusy(false);
                progress.setIndeterminate(false);
                if (finalError != null) {
                    overallStatus.setText("File could not be checked");
                    detailText.setText(finalError);
                } else if (KNOWN_BAD_SHA256.contains(finalHash.toLowerCase(Locale.US))) {
                    overallStatus.setText("Known threat signature matched");
                    overallStatus.setTextColor(RED);
                    detailText.setText("SHA-256: " + finalHash);
                    new AlertDialog.Builder(this).setTitle("Known threat signature matched")
                        .setMessage("The selected file's SHA-256 hash matches a local signature. Do not install it.")
                        .setPositiveButton("OK", null).show();
                } else {
                    overallStatus.setText("No local signature match");
                    overallStatus.setTextColor(ORANGE);
                    detailText.setText("SHA-256: " + finalHash + "\nThe local signature list currently contains no bundled threat samples. A hash miss does not mean the APK is safe.");
                    new AlertDialog.Builder(this).setTitle("No signature match")
                        .setMessage("SHA-256:\n" + finalHash + "\n\nNo matching signature was found in the local database. This first version hashes the file but does not yet parse or fully analyze APK contents.")
                        .setPositiveButton("OK", null).show();
                }
            });
        });
    }

    private String sha256(InputStream input) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) digest.update(buffer, 0, read);
            StringBuilder out = new StringBuilder();
            for (byte b : digest.digest()) out.append(String.format(Locale.US, "%02x", b & 0xff));
            return out.toString();
        } finally {
            input.close();
        }
    }

    private void showResults() {
        if (lastResults.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Scan results")
                .setMessage("No installed-app scan results yet. Tap SCAN INSTALLED APPS to begin.")
                .setPositiveButton("OK", null).show();
            return;
        }
        StringBuilder summary = new StringBuilder();
        for (ScanResult r : lastResults) {
            summary.append(r.risk >= 3 ? "[KNOWN THREAT]" : r.risk >= 1 ? "[REVIEW]" : "[NO INDICATORS]")
                .append(" ").append(r.label).append("\n")
                .append(r.packageName).append("\n");
            if (r.reasons.isEmpty()) summary.append("No configured heuristic matched; this does not prove the app is safe.\n");
            else for (String reason : r.reasons) summary.append(" • ").append(reason).append("\n");
            if (!r.hash.isEmpty()) summary.append("SHA-256: ").append(r.hash).append("\n");
            summary.append("\n");
        }
        ScrollView scroller = new ScrollView(this);
        TextView body = text(summary.toString(), 13, INK, false);
        body.setPadding(dp(12), dp(8), dp(12), dp(8));
        scroller.addView(body);
        new AlertDialog.Builder(this).setTitle("Latest scan results (" + lastResults.size() + ")")
            .setView(scroller).setPositiveButton("Close", null).show();
    }

    private void showAboutDetection() {
        new AlertDialog.Builder(this).setTitle("What is checked?")
            .setMessage("• Installed app package names and requested permissions\n• SHA-256 hashes of readable installed APK files\n• Hashes compared with a local known-threat list\n• A few combinations of sensitive permissions\n\nThe bundled threat-signature list starts empty. Permission combinations are only clues, not proof of malware. This app does not upload any information.")
            .setPositiveButton("OK", null).show();
    }

    private void setBusy(boolean busy) {
        scanButton.setEnabled(!busy);
        apkButton.setEnabled(!busy);
        scanButton.setText(busy ? "SCANNING…" : "SCAN INSTALLED APPS");
    }

    private void addSection(String name) {
        TextView heading = text(name, 14, TEAL, true);
        heading.setPadding(0, dp(22), 0, dp(8));
        root.addView(heading, match());
        View line = new View(this);
        line.setBackgroundColor(Color.rgb(190, 190, 190));
        root.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
    }

    private void addButton(String label, View.OnClickListener listener) {
        Button b = button(label);
        b.setOnClickListener(listener);
        root.addView(b, match());
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(14);
        b.setTextColor(TEAL);
        b.setAllCaps(false);
        return b;
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable panel(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(2));
        d.setStroke(dp(1), stroke);
        return d;
    }

    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
    private LinearLayout.LayoutParams match() { return new LinearLayout.LayoutParams(-1, -2); }

    private static class ScanResult {
        String label;
        String packageName;
        String hash;
        int risk;
        final List<String> reasons = new ArrayList<String>();
    }

    @Override protected void onDestroy() {
        worker.shutdownNow();
        super.onDestroy();
    }
}
