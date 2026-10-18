package com.example.androidtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.webkit.WebView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView status;
    private int sectionCount = 0;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setTitle("Android 4.4 UI Showcase");
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(28));
        root.setBackgroundColor(Color.rgb(250, 250, 250));
        scroll.addView(root);
        setContentView(scroll);

        TextView title = new TextView(this);
        title.setText("Android 4.4 UI Showcase");
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setTextColor(Color.rgb(40, 40, 40));
        root.addView(title, matchWrap());
        TextView subtitle = text("Interactive examples of classic Android widgets. Tap and experiment with everything.");
        subtitle.setTextSize(14);
        subtitle.setPadding(0, dp(5), 0, dp(12));
        root.addView(subtitle);

        status = text("Status: Ready");
        status.setTextColor(Color.rgb(0, 110, 150));
        status.setPadding(dp(10), dp(10), dp(10), dp(10));
        status.setBackground(panel());
        root.addView(status, matchWrap());

        basicInputs();
        selectionControls();
        progressAndRating();
        dialogsAndPickers();
        listsAndLayouts();
        displayAndUtilities();
        mediaAndWeb();
        systemInfo();
    }

    private void basicInputs() {
        section("Text and buttons");
        final EditText name = new EditText(this);
        name.setSingleLine(true);
        name.setHint("Text field — enter your name");
        root.addView(name, matchWrap());

        final EditText email = new EditText(this);
        email.setSingleLine(true);
        email.setHint("Email address");
        email.setInputType( android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        root.addView(email, matchWrap());

        final EditText password = new EditText(this);
        password.setSingleLine(true);
        password.setHint("Password field");
        password.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(password, matchWrap());

        final EditText multiline = new EditText(this);
        multiline.setHint("Multiline text box");
        multiline.setGravity(Gravity.TOP | Gravity.LEFT);
        multiline.setMinLines(3);
        multiline.setMaxLines(5);
        root.addView(multiline, matchWrap());

        Button normal = new Button(this);
        normal.setText("Classic button");
        normal.setOnClickListener(v -> toast("Button tapped"));
        root.addView(normal, matchWrap());

        Button submit = new Button(this);
        submit.setText("Read text fields");
        submit.setOnClickListener(v -> {
            String value = name.getText().toString().trim();
            status.setText("Name: " + (value.length() == 0 ? "(empty)" : value)
                + "\nEmail: " + email.getText().toString()
                + "\nPassword characters: " + password.length()
                + "\nNotes characters: " + multiline.length());
            ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(submit.getWindowToken(), 0);
        });
        root.addView(submit, matchWrap());

        ToggleButton toggle = new ToggleButton(this);
        toggle.setTextOn("Toggle: ON");
        toggle.setTextOff("Toggle: OFF");
        toggle.setChecked(false);
        toggle.setOnCheckedChangeListener((b, checked) -> toast("Toggle is " + (checked ? "ON" : "OFF")));
        root.addView(toggle, wrapWrap());
    }

    private void selectionControls() {
        section("Checkboxes, radio buttons and dropdowns");
        CheckBox check = new CheckBox(this);
        check.setText("Checkbox example");
        check.setOnCheckedChangeListener((b, checked) -> status.setText("Checkbox: " + checked));
        root.addView(check, wrapWrap());

        CheckBox check2 = new CheckBox(this);
        check2.setText("Another independent checkbox");
        root.addView(check2, wrapWrap());

        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.VERTICAL);
        RadioButton rb1 = new RadioButton(this); rb1.setText("Radio option A"); rb1.setId(101);
        RadioButton rb2 = new RadioButton(this); rb2.setText("Radio option B"); rb2.setId(102);
        RadioButton rb3 = new RadioButton(this); rb3.setText("Radio option C"); rb3.setId(103);
        group.addView(rb1); group.addView(rb2); group.addView(rb3);
        group.check(101);
        group.setOnCheckedChangeListener((g, id) -> {
            RadioButton selected = findViewById(id);
            if (selected != null) status.setText("Selected: " + selected.getText());
        });
        root.addView(group, wrapWrap());

        Switch switcher = new Switch(this);
        switcher.setText("Switch widget");
        switcher.setChecked(true);
        switcher.setOnCheckedChangeListener((b, checked) -> status.setText("Switch: " + checked));
        root.addView(switcher, matchWrap());

        Spinner spinner = new Spinner(this);
        String[] choices = {"Choose a value…", "Android 4.4", "Android 5.0", "Android 10", "Android 14+"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choices) {{
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }});
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> p) {}
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (pos > 0) status.setText("Spinner selection: " + choices[pos]);
            }
        });
        root.addView(spinner, matchWrap());

        AutoCompleteTextView auto = new AutoCompleteTextView(this);
        auto.setHint("Autocomplete (type 'And' or 'Kit')");
        auto.setSingleLine(true);
        auto.setThreshold(1);
        auto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
            new String[]{"Android", "Android 4.4 KitKat", "Android 5.0 Lollipop", "Android 14", "Checkbox", "EditText"}));
        root.addView(auto, matchWrap());

        MultiAutoCompleteTextView multi = new MultiAutoCompleteTextView(this);
        multi.setHint("Multi-autocomplete (comma separated)");
        multi.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multi.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
            new String[]{"Java", "Kotlin", "Android", "Widgets", "Holo"}));
        root.addView(multi, matchWrap());
    }

    private void progressAndRating() {
        section("Progress, sliders and ratings");
        final TextView seekValue = text("Seek bar value: 35");
        root.addView(seekValue, matchWrap());
        SeekBar seek = new SeekBar(this);
        seek.setMax(100); seek.setProgress(35);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int value, boolean user) { seekValue.setText("Seek bar value: " + value); }
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
        });
        root.addView(seek, matchWrap());

        ProgressBar horizontal = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        horizontal.setMax(100); horizontal.setProgress(62);
        root.addView(horizontal, matchWrap());
        Button progress = new Button(this);
        progress.setText("Advance progress");
        progress.setOnClickListener(v -> {
            int next = (horizontal.getProgress() + 15) % 101;
            horizontal.setProgress(next);
            status.setText("Progress: " + next + "%");
        });
        root.addView(progress, matchWrap());

        RatingBar rating = new RatingBar(this);
        rating.setNumStars(5); rating.setStepSize(0.5f); rating.setRating(3.5f);
        rating.setOnRatingBarChangeListener((b, value, user) -> status.setText("Rating: " + value + " / 5"));
        root.addView(rating, wrapWrap());

        TextView indeterminateLabel = text("Indeterminate progress indicator");
        root.addView(indeterminateLabel, matchWrap());
        ProgressBar spinner = new ProgressBar(this);
        root.addView(spinner, wrapWrap());
    }

    private void dialogsAndPickers() {
        section("Dialogs, date and time pickers");
        addButton("Alert dialog", v -> new AlertDialog.Builder(this)
            .setTitle("Classic alert").setMessage("This is a native Holo-style alert dialog.")
            .setPositiveButton("OK", (d, w) -> toast("OK pressed"))
            .setNegativeButton("Cancel", null).show());

        addButton("Confirmation dialog", v -> new AlertDialog.Builder(this)
            .setTitle("Confirm action").setMessage("Would you like to continue?")
            .setPositiveButton("Yes", (d, w) -> status.setText("Confirmation: Yes"))
            .setNegativeButton("No", (d, w) -> status.setText("Confirmation: No")).show());

        addButton("Single-choice dialog", v -> new AlertDialog.Builder(this)
            .setTitle("Choose one").setSingleChoiceItems(new String[]{"First", "Second", "Third"}, 0,
                (d, which) -> { status.setText("Dialog choice: " + (which + 1)); d.dismiss(); }).setNegativeButton("Close", null).show());

        addButton("Multiple-choice dialog", v -> {
            String[] items = {"Notifications", "Sound", "Vibration"};
            boolean[] checked = {true, false, true};
            new AlertDialog.Builder(this).setTitle("Choose options").setMultiChoiceItems(items, checked,
                (d, which, isChecked) -> status.setText(items[which] + ": " + isChecked))
                .setPositiveButton("Done", null).show();
        });

        addButton("Date picker", v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) ->
                status.setText(String.format(Locale.getDefault(), "Date: %02d/%02d/%04d", day, month + 1, year)),
                c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH), c.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });
        addButton("Time picker", v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) ->
                status.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", hour, minute)),
                c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE), true).show();
        });
        addButton("Popup menu", v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add("First item");
            menu.getMenu().add("Second item");
            menu.getMenu().add("Third item");
            menu.setOnMenuItemClickListener(item -> { status.setText("Popup: " + item.getTitle()); return true; });
            menu.show();
        });
        addButton("Context menu (long-press)", v -> toast("Long-press this button to see a context menu"));
        Button context = new Button(this); context.setText("Long-press me");
        registerForContextMenu(context); root.addView(context, matchWrap());
    }

    @Override public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        menu.setHeaderTitle("Context menu");
        menu.add("Action one"); menu.add("Action two"); menu.add("Action three");
    }
    @Override public boolean onContextItemSelected(android.view.MenuItem item) {
        toast("Context: " + item.getTitle()); return true;
    }

    private void listsAndLayouts() {
        section("Lists, grids and expandable content");
        ListView list = new ListView(this);
        String[] data = {"List row 1", "List row 2", "List row 3", "List row 4"};
        list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));
        list.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(180)));
        list.setOnItemClickListener((p, v, pos, id) -> status.setText("List selected: " + data[pos]));
        root.addView(list);

        GridView grid = new GridView(this);
        grid.setNumColumns(2);
        grid.setVerticalSpacing(dp(4));
        grid.setHorizontalSpacing(dp(4));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        String[] cells = {"Grid 1", "Grid 2", "Grid 3", "Grid 4", "Grid 5", "Grid 6"};
        grid.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cells));
        grid.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(180)));
        grid.setOnItemClickListener((p, v, pos, id) -> status.setText("Grid selected: " + cells[pos]));
        root.addView(grid);

        final TextView expandable = text("Tap to expand this section  ▾");
        expandable.setTextSize(16);
        expandable.setPadding(dp(12), dp(14), dp(12), dp(14));
        expandable.setBackground(panel());
        final TextView details = text("Expanded content: a simple collapsible section, implemented with native views.");
        details.setPadding(dp(12), dp(12), dp(12), dp(12));
        details.setVisibility(View.GONE);
        root.addView(expandable, matchWrap()); root.addView(details, matchWrap());
        expandable.setOnClickListener(v -> {
            boolean show = details.getVisibility() != View.VISIBLE;
            details.setVisibility(show ? View.VISIBLE : View.GONE);
            expandable.setText(show ? "Tap to collapse this section  ▴" : "Tap to expand this section  ▾");
        });

        HorizontalScrollView hsv = new HorizontalScrollView(this);
        LinearLayout row = new LinearLayout(this); row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 1; i <= 5; i++) {
            TextView chip = text(" Panel " + i + " ");
            chip.setTextSize(18); chip.setPadding(dp(22), dp(24), dp(22), dp(24));
            chip.setBackground(panel());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
            lp.setMargins(0, 0, dp(8), 0); row.addView(chip, lp);
            final int n = i;
            chip.setOnClickListener(v -> status.setText("Horizontal panel " + n));
        }
        hsv.addView(row); root.addView(hsv, matchWrap());
    }

    private void displayAndUtilities() {
        section("Images, text and clipboard");
        ImageView image = new ImageView(this);
        image.setImageResource(android.R.drawable.ic_menu_gallery);
        image.setContentDescription("Built-in Android gallery icon");
        image.setBackground(panel());
        image.setPadding(dp(18), dp(18), dp(18), dp(18));
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        root.addView(image, new LinearLayout.LayoutParams(-1, dp(110)));

        TextView styled = text("Large text  •  Bold  •  Classic Android icon");
        styled.setTextSize(18); styled.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        styled.setPadding(0, dp(10), 0, dp(10)); root.addView(styled, matchWrap());

        addButton("Show Toast", v -> Toast.makeText(this, "Hello from Android 4.4 style!", Toast.LENGTH_LONG).show());
        addButton("Copy sample text", v -> {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                android.content.ClipboardManager cb = (android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                cb.setPrimaryClip(android.content.ClipData.newPlainText("Demo text", "Copied from Android UI Showcase"));
                toast("Text copied to clipboard");
            } else {
                android.text.ClipboardManager cb = (android.text.ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                cb.setText("Copied from Android UI Showcase"); toast("Text copied to clipboard");
            }
        });
        addButton("Show current date and time", v ->
            status.setText("Device time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date())));
    }

    private void mediaAndWeb() {
        section("WebView and media controls");
        TextView webNote = text("WebView can display local or web content. This demo stays offline and does not load a website.");
        root.addView(webNote, matchWrap());
        WebView web = new WebView(this);
        web.loadDataWithBaseURL(null, "<html><body style='font-family:sans-serif;background:#fafafa;color:#333'><h3>Embedded WebView</h3><p>This HTML is rendered locally inside the app.</p><button onclick=\"document.getElementById('msg').innerHTML='HTML button clicked!\">Tap HTML button</button><p id='msg'></p></body></html>", "text/html", "UTF-8", null);
        root.addView(web, new LinearLayout.LayoutParams(-1, dp(170)));

        final TextView mediaStatus = text("Media demo: stopped");
        root.addView(mediaStatus, matchWrap());
        SeekBar fakeTrack = new SeekBar(this); fakeTrack.setMax(100); fakeTrack.setProgress(0);
        root.addView(fakeTrack, matchWrap());
        LinearLayout controls = new LinearLayout(this); controls.setOrientation(LinearLayout.HORIZONTAL);
        Button play = new Button(this); play.setText("Play");
        Button pause = new Button(this); pause.setText("Pause");
        Button stop = new Button(this); stop.setText("Stop");
        controls.addView(play, new LinearLayout.LayoutParams(0, -2, 1));
        controls.addView(pause, new LinearLayout.LayoutParams(0, -2, 1));
        controls.addView(stop, new LinearLayout.LayoutParams(0, -2, 1));
        play.setOnClickListener(v -> mediaStatus.setText("Media demo: playing (visual demo only)"));
        pause.setOnClickListener(v -> mediaStatus.setText("Media demo: paused"));
        stop.setOnClickListener(v -> { mediaStatus.setText("Media demo: stopped"); fakeTrack.setProgress(0); });
        fakeTrack.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int p, boolean u) { if (u) mediaStatus.setText("Demo position: " + p + "%"); }
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
        });
        root.addView(controls, matchWrap());
    }

    private void systemInfo() {
        section("Device and app information");
        root.addView(text("Android release: " + android.os.Build.VERSION.RELEASE), matchWrap());
        root.addView(text("API level: " + android.os.Build.VERSION.SDK_INT), matchWrap());
        root.addView(text("Manufacturer: " + android.os.Build.MANUFACTURER), matchWrap());
        root.addView(text("Device: " + android.os.Build.DEVICE), matchWrap());
        root.addView(text("Model: " + android.os.Build.MODEL), matchWrap());
        root.addView(text("App minimum API: 19 (Android 4.4 KitKat)"), matchWrap());
        root.addView(text("Theme: native Android Holo Light"), matchWrap());
        addButton("Show app information", v -> new AlertDialog.Builder(this)
            .setTitle("About this showcase")
            .setMessage("A widget playground inspired by Android 4.4 KitKat. Most controls are native Android views. Media playback and notifications are represented as demos; no data is sent anywhere.")
            .setPositiveButton("Close", null).show());
        TextView footer = text("Android 4.4 UI Showcase • Offline demo • No account required");
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(20), 0, dp(4));
        root.addView(footer, matchWrap());
    }

    private void section(String name) {
        sectionCount++;
        TextView heading = new TextView(this);
        heading.setText(name.toUpperCase(Locale.getDefault()));
        heading.setTextSize(14);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setTextColor(Color.rgb(0, 125, 170));
        heading.setPadding(0, dp(24), 0, dp(8));
        root.addView(heading, matchWrap());
        View line = new View(this);
        line.setBackgroundColor(Color.rgb(210, 210, 210));
        root.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
    }

    private void addButton(String label, View.OnClickListener listener) {
        Button b = new Button(this); b.setText(label); b.setOnClickListener(listener); root.addView(b, matchWrap());
    }
    private TextView text(String value) {
        TextView t = new TextView(this); t.setText(value); t.setTextColor(Color.rgb(45, 45, 45)); t.setTextSize(15); t.setPadding(0, dp(5), 0, dp(5)); return t;
    }
    private void toast(String value) { Toast.makeText(this, value, Toast.LENGTH_SHORT).show(); }
    private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(-1, -2); }
    private LinearLayout.LayoutParams wrapWrap() { return new LinearLayout.LayoutParams(-2, -2); }
    private GradientDrawable panel() {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.rgb(255,255,255)); d.setStroke(dp(1), Color.rgb(215,215,215)); d.setCornerRadius(dp(3)); return d;
    }
}
