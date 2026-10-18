package com.example.androidtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import java.security.MessageDigest;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int REQUEST_PICK_APK = 41;
    private static final int BG = Color.rgb(245, 245, 245);
    private static final int INK = Color.rgb(33, 33, 33);
    private static final int TEAL = Color.rgb(0, 150, 136);
    private static final int ORANGE = Color.rgb(239, 108, 0);

    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private LinearLayout root;
    private TextView statusText;
    private TextView detailText;
    private ProgressBar progress;
    private Button apkButton;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.rgb(0, 121, 107));
            getWindow().setNavigationBarColor(Color.rgb(0, 77, 64));
        }
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
        TextView subtitle = text("Offline APK hash utility • No account • No internet access", 13, Color.DKGRAY, false);
        subtitle.setPadding(0, dp(3), 0, dp(16));
        root.addView(subtitle, match());

        LinearLayout panel = new LinearLayout(this);
        panel.setGravity(Gravity.CENTER);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(14), dp(22), dp(14), dp(22));
        panel.setBackground(panel(Color.WHITE, TEAL));
        TextView symbol = text("✓", 44, TEAL, true);
        symbol.setGravity(Gravity.CENTER);
        panel.addView(symbol, match());
        statusText = text("Ready to check an APK file", 18, INK, true);
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, dp(6), 0, dp(4));
        panel.addView(statusText, match());
        detailText = text("Select a file to calculate its SHA-256 hash.", 13, Color.DKGRAY, false);
        detailText.setGravity(Gravity.CENTER);
        panel.addView(detailText, match());
        root.addView(panel, match());

        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setIndeterminate(false);
        progress.setMax(100);
        progress.setProgress(0);
        LinearLayout.LayoutParams progressParams = match();
        progressParams.topMargin = dp(16);
        progressParams.bottomMargin = dp(8);
        root.addView(progress, progressParams);

        apkButton = button("SELECT APK FILE");
        apkButton.setOnClickListener(v -> pickApk());
        root.addView(apkButton, match());

        addSection("ABOUT THIS TOOL");
        TextView about = text(
            "This build only calculates the SHA-256 hash of an APK file you choose. It does not scan installed apps, inspect other apps' permissions, or determine whether a file is malware. No hash database is included, so a hash alone cannot establish that an APK is safe.",
            14, INK, false);
        about.setPadding(0, dp(8), 0, dp(12));
        root.addView(about, match());

        addButton("About KitKat Antivirus", v -> new AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("This is a small offline APK hash utility with a KitKat-style interface. It does not replace Google Play Protect or a real antivirus scanner.")
            .setPositiveButton("OK", null).show());

        TextView footer = text("Offline utility • A hash is an identifier, not a malware verdict", 12, Color.DKGRAY, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(18), 0, 0);
        root.addView(footer, match());
    }

    private void pickApk() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select an APK file"), REQUEST_PICK_APK);
        } catch (Exception e) {
            Toast.makeText(this, "No file picker is available", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_PICK_APK || resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;

        apkButton.setEnabled(false);
        statusText.setText("Calculating hash…");
        detailText.setText("Reading only the file you selected.");
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
                apkButton.setEnabled(true);
                progress.setIndeterminate(false);
                progress.setProgress(finalError == null ? 100 : 0);
                if (finalError != null) {
                    statusText.setText("File could not be read");
                    statusText.setTextColor(ORANGE);
                    detailText.setText(finalError);
                } else {
                    statusText.setText("Hash calculated");
                    statusText.setTextColor(TEAL);
                    detailText.setText("SHA-256:\n" + finalHash + "\n\nThis hash does not tell you whether the APK is safe.");
                    new AlertDialog.Builder(this).setTitle("SHA-256 hash")
                        .setMessage(finalHash + "\n\nThis app does not compare the hash against a malware database.")
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

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams match() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    @Override protected void onDestroy() {
        worker.shutdownNow();
        super.onDestroy();
    }
}