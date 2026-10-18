package com.example.androidtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(242, 244, 246);
    private static final int INK = Color.rgb(35, 42, 48);
    private static final int TEAL = Color.rgb(0, 137, 123);
    private static final int MUTED = Color.rgb(100, 110, 118);
    private static final int CARD = Color.WHITE;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private LinearLayout root;
    private String screen = "Home";
    private boolean scanning = false;
    private int scanStep = 0;
    private boolean demoRealtime = true;
    private boolean demoNotifications = true;
    private boolean demoAutoScan = false;

    private final String[] scanSteps = {
        "Preparing simulated scan…",
        "Checking sample app list…",
        "Reviewing demo protection status…",
        "Checking sample files…",
        "Finishing demo scan…"
    };
    private final int[] scanProgress = {8, 28, 51, 78, 94};

    private final Runnable scanTick = new Runnable() {
        @Override public void run() {
            if (!scanning) return;
            scanStep++;
            if (scanStep >= scanSteps.length) {
                scanning = false;
                screen = "Scan results";
                buildScreen();
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Demo scan complete")
                    .setMessage("This was a simulation. No apps, files, settings, or device security state were examined. Sample result: 0 simulated threats.")
                    .setPositiveButton("OK", null)
                    .show();
                return;
            }
            buildScreen();
            handler.postDelayed(this, 850);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.rgb(0, 105, 92));
            getWindow().setNavigationBarColor(Color.rgb(0, 77, 64));
        }
        setTitle("KitKat Antivirus Demo");
        buildScreen();
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(22));
        scroll.addView(root);
        setContentView(scroll);

        TextView brand = text("KitKat Antivirus", 24, INK, true);
        root.addView(brand, match());
        TextView subtitle = text("Classic Android security UI concept", 13, MUTED, false);
        subtitle.setPadding(0, dp(2), 0, dp(10));
        root.addView(subtitle, match());

        TextView demoBanner = text("DEMO MODE  •  NO REAL PROTECTION", 12, Color.WHITE, true);
        demoBanner.setGravity(Gravity.CENTER);
        demoBanner.setPadding(dp(8), dp(10), dp(8), dp(10));
        demoBanner.setBackground(solid(Color.rgb(183, 91, 0), 3));
        root.addView(demoBanner, match());

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(0, dp(10), 0, dp(8));
        addNav(nav, "Home");
        addNav(nav, "Apps");
        addNav(nav, "Quarantine");
        addNav(nav, "History");
        root.addView(nav, match());

        if ("Home".equals(screen)) showHome();
        else if ("Apps".equals(screen)) showApps();
        else if ("Quarantine".equals(screen)) showQuarantine();
        else if ("History".equals(screen)) showHistory();
        else if ("Settings".equals(screen)) showSettings();
        else if ("Scan results".equals(screen)) showResults();
        else if ("Scanning".equals(screen)) showScanning();
        else showHome();

        TextView footer = text("SIMULATED DATA ONLY · This app does not scan or protect your device.", 11, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(20), 0, dp(2));
        root.addView(footer, match());
    }

    private void showHome() {
        addHeading("Security dashboard");
        LinearLayout status = card();
        TextView icon = text("✓", 42, TEAL, true);
        icon.setGravity(Gravity.CENTER);
        status.addView(icon, match());
        TextView title = text("Demo status: looking good", 19, INK, true);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(4), 0, dp(4));
        status.addView(title, match());
        TextView desc = text("This status is made up for the demo. Nothing on your device has been checked.", 13, MUTED, false);
        desc.setGravity(Gravity.CENTER);
        status.addView(desc, match());
        root.addView(status, match());

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setPadding(0, dp(10), 0, dp(2));
        addStat(stats, "12", "Sample apps");
        addStat(stats, "0", "Demo threats");
        addStat(stats, "100%", "Demo shield");
        root.addView(stats, match());

        addHeading("Quick actions");
        addAction("▶  Scan device (simulation)", "Runs an animated demo scan using sample data only.", () -> startDemoScan());
        addAction("▣  Sample app list", "Browse fictional app entries.", () -> navigate("Apps"));
        addAction("⌑  Quarantine", "View fictional detection examples.", () -> navigate("Quarantine"));
        addAction("◷  Scan history", "View sample scan reports.", () -> navigate("History"));
        addAction("⚙  Settings", "Toggle demo-only switches.", () -> navigate("Settings"));
        Button about = button("About this demo");
        about.setOnClickListener(v -> showAbout());
        root.addView(about, match());
    }

    private void showApps() {
        addHeading("App scanner");
        addNote("Sample data only. These names and statuses are examples; the app does not read your installed apps.");
        addAction("✓  Example Calendar", "Status: Demo OK · Last checked: Just now", () -> showSampleDetail("Example Calendar"));
        addAction("✓  Sample Camera", "Status: Demo OK · Last checked: Yesterday", () -> showSampleDetail("Sample Camera"));
        addAction("✓  Notes Example", "Status: Demo OK · Last checked: 3 days ago", () -> showSampleDetail("Notes Example"));
        addAction("!  Suspicious Demo App", "Status: Example warning · Not a real detection", () -> showSampleDetail("Suspicious Demo App"));
        addAction("↻  Refresh sample list", "Rebuild the fictional list.", () -> {
            Toast.makeText(this, "Sample list refreshed — no device scan performed", Toast.LENGTH_SHORT).show();
            buildScreen();
        });
    }

    private void showQuarantine() {
        addHeading("Quarantine");
        addNote("Nothing has been quarantined. The entries below are fictional examples and cannot affect files on your device.");
        addAction("Example_Adware.apk", "DEMO ITEM · Added 2 days ago", () -> new AlertDialog.Builder(this)
            .setTitle("Demo quarantine item")
            .setMessage("This is a fictional item. No file exists in quarantine.")
            .setPositiveButton("Close", null).show());
        addAction("Unknown_Sample.apk", "DEMO ITEM · Added last week", () -> new AlertDialog.Builder(this)
            .setTitle("Demo quarantine item")
            .setMessage("This is a fictional item. No file exists in quarantine.")
            .setPositiveButton("Close", null).show());
        Button clear = button("Clear demo list");
        clear.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Clear sample entries?")
            .setMessage("This only hides the fictional examples until you reopen the screen. No files will be changed.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear", (d, w) -> {
                addToastAndRefresh("Demo list cleared");
                screen = "Home";
                buildScreen();
            }).show());
        root.addView(clear, match());
    }

    private void showHistory() {
        addHeading("Scan history");
        addNote("Example reports only. These entries were not created by real device scans.");
        addAction("Today · Quick scan", "Completed · 12 sample apps · 0 demo threats", () -> showHistoryDetail("Quick scan", "12 sample apps checked · 0 demo threats"));
        addAction("Yesterday · Full scan", "Completed · 24 sample items · 1 demo warning", () -> showHistoryDetail("Full scan", "24 sample items · 1 fictional warning"));
        addAction("Oct 12 · Quick scan", "Completed · 10 sample apps · 0 demo threats", () -> showHistoryDetail("Quick scan", "10 sample apps · 0 demo threats"));
        Button clear = button("Clear demo history");
        clear.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Clear demo history?")
            .setMessage("This removes no real records; the history is sample data.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear", (d, w) -> {
                screen = "Home";
                buildScreen();
                Toast.makeText(this, "Demo history cleared", Toast.LENGTH_SHORT).show();
            }).show());
        root.addView(clear, match());
    }

    private void showSettings() {
        addHeading("Demo settings");
        addNote("These switches only change their appearance while this app remains open. They do not enable security features.");
        addSwitch("Real-time protection (demo)", "Changes the sample shield indicator only.", demoRealtime, value -> {
            demoRealtime = value;
            Toast.makeText(this, "Demo setting changed; no protection is active", Toast.LENGTH_SHORT).show();
        });
        addSwitch("Scan notifications (demo)", "No actual notifications are sent.", demoNotifications, value -> demoNotifications = value);
        addSwitch("Automatic scans (demo)", "No background work or scans will run.", demoAutoScan, value -> demoAutoScan = value);
        addAction("Privacy & permissions", "This demo requests no special Android permissions and makes no network requests.", () ->
            new AlertDialog.Builder(this).setTitle("Privacy")
                .setMessage("The app uses fictional data and performs no real scan, file inspection, app enumeration, network request, or background protection.")
                .setPositiveButton("OK", null).show());
        Button reset = button("Reset demo settings");
        reset.setOnClickListener(v -> {
            demoRealtime = true;
            demoNotifications = true;
            demoAutoScan = false;
            buildScreen();
        });
        root.addView(reset, match());
    }

    private void showResults() {
        addHeading("Scan results");
        addNote("The most recent scan was simulated. No device content was examined.");
        addAction("✓  Sample apps", "12 fictional entries", () -> navigate("Apps"));
        addAction("✓  Sample files", "8 fictional entries", () -> showSampleDetail("Sample files"));
        addAction("✓  Demo threats found", "0 fictional detections", () -> showSampleDetail("Demo threat results"));
        Button rescan = button("Run demo scan again");
        rescan.setOnClickListener(v -> startDemoScan());
        root.addView(rescan, match());
        Button home = button("Return to dashboard");
        home.setOnClickListener(v -> navigate("Home"));
        root.addView(home, match());
    }

    private void startDemoScan() {
        if (scanning) {
            scanning = false;
            handler.removeCallbacks(scanTick);
            Toast.makeText(this, "Demo scan stopped", Toast.LENGTH_SHORT).show();
            buildScreen();
            return;
        }
        scanning = true;
        scanStep = -1;
        screen = "Scanning";
        addToastAndRefresh("Starting simulated scan");
        scanStep = 0;
        buildScreen();
        handler.postDelayed(scanTick, 850);
    }

    private void showScanning() {
        addHeading("Scanning (simulation)");
        TextView warning = text("No apps or files are being scanned. This is an animation only.", 13, MUTED, false);
        warning.setPadding(0, 0, 0, dp(14));
        root.addView(warning, match());
        TextView step = text(scanSteps[Math.min(scanStep, scanSteps.length - 1)], 16, INK, true);
        step.setPadding(0, dp(10), 0, dp(10));
        root.addView(step, match());
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setProgress(scanProgress[Math.min(scanStep, scanProgress.length - 1)]);
        root.addView(bar, match());
        TextView pct = text(scanProgress[Math.min(scanStep, scanProgress.length - 1)] + "%", 13, TEAL, true);
        pct.setPadding(0, dp(8), 0, dp(12));
        root.addView(pct, match());
        Button cancel = button("Stop demo scan");
        cancel.setOnClickListener(v -> {
            scanning = false;
            handler.removeCallbacks(scanTick);
            screen = "Home";
            buildScreen();
            Toast.makeText(this, "Demo scan stopped; nothing was scanned", Toast.LENGTH_SHORT).show();
        });
        root.addView(cancel, match());
    }

    private void addNav(LinearLayout nav, String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(11);
        b.setAllCaps(false);
        b.setTextColor(label.equals(screen) ? TEAL : INK);
        b.setPadding(dp(2), 0, dp(2), 0);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(46), 1);
        nav.addView(b, p);
        b.setOnClickListener(v -> navigate(label));
    }

    private void navigate(String destination) {
        if (scanning) {
            scanning = false;
            handler.removeCallbacks(scanTick);
        }
        screen = destination;
        buildScreen();
    }

    private void addHeading(String title) {
        TextView t = text(title, 20, INK, true);
        t.setPadding(0, dp(16), 0, dp(10));
        root.addView(t, match());
    }

    private void addNote(String message) {
        TextView t = text(message, 13, MUTED, false);
        t.setPadding(dp(2), 0, dp(2), dp(12));
        root.addView(t, match());
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(14), dp(16), dp(14), dp(16));
        box.setBackground(panel(CARD, Color.rgb(222, 226, 229)));
        return box;
    }

    private void addStat(LinearLayout parent, String value, String label) {
        LinearLayout box = card();
        TextView v = text(value, 22, TEAL, true);
        v.setGravity(Gravity.CENTER);
        box.addView(v, match());
        TextView l = text(label, 10, MUTED, false);
        l.setGravity(Gravity.CENTER);
        l.setPadding(0, dp(3), 0, 0);
        box.addView(l, match());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(90), 1);
        p.setMargins(dp(2), 0, dp(2), 0);
        parent.addView(box, p);
    }

    private void addAction(String title, String description, Runnable action) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dp(12), dp(11), dp(12), dp(11));
        item.setBackground(panel(CARD, Color.rgb(222, 226, 229)));
        TextView t = text(title, 15, INK, true);
        item.addView(t, match());
        TextView d = text(description, 12, MUTED, false);
        d.setPadding(0, dp(4), 0, 0);
        item.addView(d, match());
        LinearLayout.LayoutParams p = match();
        p.bottomMargin = dp(8);
        root.addView(item, p);
        item.setOnClickListener(v -> action.run());
        item.setClickable(true);
        item.setFocusable(true);
    }

    private interface ToggleChanged { void onChanged(boolean value); }

    private void addSwitch(String title, String description, boolean checked, ToggleChanged listener) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(dp(12), dp(10), dp(12), dp(10));
        item.setBackground(panel(CARD, Color.rgb(222, 226, 229)));
        TextView t = text(title, 15, INK, true);
        item.addView(t, match());
        TextView d = text(description, 12, MUTED, false);
        d.setPadding(0, dp(3), 0, dp(6));
        item.addView(d, match());
        Switch sw = new Switch(this);
        sw.setChecked(checked);
        item.addView(sw, match());
        sw.setOnCheckedChangeListener((button, value) -> listener.onChanged(value));
        LinearLayout.LayoutParams p = match();
        p.bottomMargin = dp(8);
        root.addView(item, p);
    }

    private void showSampleDetail(String name) {
        new AlertDialog.Builder(this).setTitle(name)
            .setMessage("Sample information for demonstration purposes only. No real app, file, or security state was accessed.")
            .setPositiveButton("OK", null).show();
    }

    private void showHistoryDetail(String title, String details) {
        new AlertDialog.Builder(this).setTitle(title + " · Demo report")
            .setMessage(details + "\n\nThis report uses fictional data. No real scan occurred.")
            .setPositiveButton("OK", null).show();
    }

    private void showAbout() {
        new AlertDialog.Builder(this).setTitle("About KitKat Antivirus Demo")
            .setMessage("A pretend antivirus interface inspired by classic Android 4.x apps. Every scan, status, app entry, threat, history item, and protection setting is simulated. This app does not provide antivirus protection.")
            .setPositiveButton("OK", null).show();
    }

    private void addToastAndRefresh(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        d.setCornerRadius(dp(3));
        d.setStroke(dp(1), stroke);
        return d;
    }

    private GradientDrawable solid(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private LinearLayout.LayoutParams match() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    @Override public void onBackPressed() {
        if (!"Home".equals(screen)) {
            navigate("Home");
        } else {
            super.onBackPressed();
        }
    }

    @Override protected void onDestroy() {
        scanning = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
