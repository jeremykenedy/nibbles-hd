package com.jeremykenedy.nibbleshd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public final class SettingsActivity extends Activity {
  private SharedPreferences prefs;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    LinearLayout content = new LinearLayout(this);
    content.setOrientation(LinearLayout.VERTICAL);
    content.setPadding(dp(48), dp(28), dp(48), dp(32));
    content.setBackgroundColor(0xff0c171f);
    TextView title = new TextView(this);
    title.setText("Nibbles HD");
    title.setTextColor(0xffe4eff0);
    title.setTextSize(32);
    content.addView(title);
    TextView help = new TextView(this);
    help.setText(
        "Use Left, Right or Select to change an option. Changes apply to the next preview or"
            + " dream.");
    help.setTextColor(0xff9cb3bb);
    help.setTextSize(18);
    help.setPadding(0, dp(8), 0, dp(16));
    content.addView(help);
    Button preview = button("Preview animation");
    preview.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startActivity(new Intent(SettingsActivity.this, PreviewActivity.class));
          }
        });
    content.addView(preview);
    for (String[] spec : NibblesOptions.SCHEMA) addChoice(content, spec);
    final Button randomize = button("");
    updateRandom(randomize);
    randomize.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            prefs
                .edit()
                .putBoolean("randomize_all", !prefs.getBoolean("randomize_all", false))
                .apply();
            updateRandom(randomize);
          }
        });
    randomize.setOnKeyListener(
        new View.OnKeyListener() {
          @Override
          public boolean onKey(View view, int key, KeyEvent event) {
            if (key != KeyEvent.KEYCODE_DPAD_LEFT && key != KeyEvent.KEYCODE_DPAD_RIGHT)
              return false;
            if (event.getAction() == KeyEvent.ACTION_DOWN) randomize.performClick();
            return true;
          }
        });
    content.addView(randomize);
    ScrollView scroll = new ScrollView(this);
    scroll.setFillViewport(true);
    scroll.addView(content);
    setContentView(scroll);
    preview.requestFocus();
  }

  private void updateRandom(Button button) {
    button.setText(
        "Randomize all on start    " + (prefs.getBoolean("randomize_all", false) ? "On" : "Off"));
  }

  private void addChoice(LinearLayout content, final String[] spec) {
    final ArrayList<String> choices = new ArrayList<String>();
    for (int i = 3; i < spec.length; i++) choices.add(spec[i]);
    choices.add("random");
    final Button option = button("");
    updateChoice(option, spec);
    option.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            cycle(option, spec, choices, 1);
          }
        });
    option.setOnKeyListener(
        new View.OnKeyListener() {
          @Override
          public boolean onKey(View view, int key, KeyEvent event) {
            if (key != KeyEvent.KEYCODE_DPAD_LEFT && key != KeyEvent.KEYCODE_DPAD_RIGHT)
              return false;
            if (event.getAction() == KeyEvent.ACTION_DOWN)
              cycle(option, spec, choices, key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
            return true;
          }
        });
    content.addView(option);
  }

  private void cycle(Button button, String[] spec, ArrayList<String> choices, int direction) {
    String current = NibblesOptions.validated(spec[0], prefs.getString(spec[0], spec[2]));
    int next = (choices.indexOf(current) + direction + choices.size()) % choices.size();
    prefs.edit().putString(spec[0], choices.get(next)).apply();
    updateChoice(button, spec);
  }

  private void updateChoice(Button button, String[] spec) {
    String value = NibblesOptions.validated(spec[0], prefs.getString(spec[0], spec[2]));
    button.setText(
        spec[1] + "    " + value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1));
  }

  private Button button(String label) {
    Button button = new Button(this);
    button.setText(label);
    button.setTextSize(21);
    button.setAllCaps(false);
    button.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.LEFT);
    button.setPadding(dp(22), 0, dp(22), 0);
    button.setMinHeight(dp(62));
    button.setTextColor(
        new ColorStateList(
            new int[][] {new int[] {android.R.attr.state_focused}, new int[0]},
            new int[] {0xff07151b, 0xffd8e5e7}));
    StateListDrawable backgrounds = new StateListDrawable();
    backgrounds.addState(new int[] {android.R.attr.state_focused}, background(0xff90c6bf));
    backgrounds.addState(new int[0], background(0xff192a33));
    button.setBackground(backgrounds);
    LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(-1, dp(62));
    layout.bottomMargin = dp(8);
    button.setLayoutParams(layout);
    return button;
  }

  private GradientDrawable background(int color) {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(color);
    drawable.setCornerRadius(dp(8));
    return drawable;
  }

  private int dp(int value) {
    return (int) (value * getResources().getDisplayMetrics().density + .5f);
  }
}
