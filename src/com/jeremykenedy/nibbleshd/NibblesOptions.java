package com.jeremykenedy.nibbleshd;

import java.util.Random;

public final class NibblesOptions {
  public static final String[][] SCHEMA = {
    {"speed", "Game speed", "steady", "relaxed", "steady", "brisk"},
    {"snakes", "Snakes", "duet", "solo", "duet", "trio"},
    {"palette", "Palette", "mineral", "mineral", "citrus", "tide", "orchid"},
    {"stage", "Stage", "random", "open", "islands", "channels"},
    {"board", "Board scale", "standard", "wide", "standard", "close"},
    {"effects", "Effects", "soft", "clean", "soft", "radiant"},
    {"brightness", "Lighting", "balanced", "dim", "balanced", "bright"}
  };

  public final int snakeCount, palette, stage, columns, rows, effects;
  public final float stepsPerSecond, brightness;

  public NibblesOptions(String[] values, Random random, boolean randomize) {
    if (values == null || values.length != SCHEMA.length)
      throw new IllegalArgumentException("One value per setting is required");
    int[] selected = new int[SCHEMA.length];
    for (int i = 0; i < selected.length; i++) {
      String value = randomize ? "random" : values[i];
      selected[i] = choice(i, value, random);
    }
    stepsPerSecond = new float[] {5.0f, 8.0f, 12.0f}[selected[0]];
    snakeCount = selected[1] + 1;
    palette = selected[2];
    stage =
        "random".equals(randomize ? "random" : validated("stage", values[3])) ? -1 : selected[3];
    columns = new int[] {56, 48, 38}[selected[4]];
    rows = new int[] {30, 26, 22}[selected[4]];
    effects = selected[5];
    brightness = new float[] {.58f, .84f, 1.0f}[selected[6]];
  }

  public static int row(String key) {
    for (int i = 0; i < SCHEMA.length; i++) if (SCHEMA[i][0].equals(key)) return i;
    return -1;
  }

  public static boolean supported(String key, String value) {
    if ("randomize_all".equals(key)) return "true".equals(value) || "false".equals(value);
    int row = row(key);
    if (row < 0 || value == null) return false;
    if ("random".equals(value)) return true;
    for (int i = 3; i < SCHEMA[row].length; i++) if (SCHEMA[row][i].equals(value)) return true;
    return false;
  }

  public static String validated(String key, String value) {
    int row = row(key);
    if (row < 0) throw new IllegalArgumentException("Unknown setting");
    return supported(key, value) ? value : SCHEMA[row][2];
  }

  private static int choice(int row, String value, Random random) {
    String selected = validated(SCHEMA[row][0], value);
    if ("random".equals(selected)) return random.nextInt(SCHEMA[row].length - 3);
    return java.util.Arrays.asList(SCHEMA[row]).subList(3, SCHEMA[row].length).indexOf(selected);
  }
}
