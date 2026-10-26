package com.jeremykenedy.nibbleshd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.preference.PreferenceManager;
import android.view.Choreographer;
import android.view.View;
import java.util.Random;

/** Native resolution, continuously interpolated gameplay without scene overlays. */
public final class NibblesSceneView extends View implements Choreographer.FrameCallback {
  private static final int[][] COLORS = {
    {0xff73cfb2, 0xffedb66e, 0xffa5a3e1},
    {0xffefb956, 0xffd77c60, 0xffa5bd78},
    {0xff70b7d1, 0xff75c7b2, 0xffc8b99c},
    {0xffbe9cda, 0xffdb9ba4, 0xff83b9c7}
  };
  private final NibblesOptions options;
  private final NibblesGame game;
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Path ribbon = new Path();
  private final RectF rectangle = new RectF();
  private final float[] px, py;
  private final int[] colors;
  private final Shader[] ribbonShaders = new Shader[3];
  private Shader background;
  private boolean running;
  private long previousFrame;
  private float seconds, progress, endingAge, birthAge;
  private float cell, left, top, boardWidth, boardHeight;

  public NibblesSceneView(Context context) {
    super(context);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String[] values = new String[NibblesOptions.SCHEMA.length];
    for (int i = 0; i < values.length; i++) {
      String[] spec = NibblesOptions.SCHEMA[i];
      values[i] = prefs.getString(spec[0], spec[2]);
    }
    Random random = new Random();
    options = new NibblesOptions(values, random, prefs.getBoolean("randomize_all", false));
    game =
        new NibblesGame(options.columns, options.rows, options.snakeCount, options.stage, random);
    colors = COLORS[options.palette];
    px = new float[options.columns * options.rows + 1];
    py = new float[px.length];
    setFocusable(false);
    setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
  }

  public void start() {
    if (running) return;
    running = true;
    previousFrame = 0;
    Choreographer.getInstance().postFrameCallback(this);
  }

  public void stop() {
    running = false;
    Choreographer.getInstance().removeFrameCallback(this);
    previousFrame = 0;
  }

  public void release() {
    stop();
  }

  @Override
  protected void onDetachedFromWindow() {
    stop();
    super.onDetachedFromWindow();
  }

  @Override
  public void doFrame(long now) {
    if (!running) return;
    float delta = previousFrame == 0 ? 0 : Math.min(.05f, (now - previousFrame) * .000000001f);
    previousFrame = now;
    seconds += delta;
    birthAge += delta;
    if (game.ended) {
      endingAge += delta;
      if (endingAge > 1.5f) {
        game.reset();
        endingAge = 0;
        birthAge = 0;
        progress = 0;
      }
    } else {
      progress += delta * options.stepsPerSecond;
      while (progress >= 1 && !game.ended) {
        progress--;
        game.tick();
      }
    }
    invalidate();
    Choreographer.getInstance().postFrameCallback(this);
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    cell = Math.min(width * .90f / options.columns, height * .83f / options.rows);
    boardWidth = cell * options.columns;
    boardHeight = cell * options.rows;
    left = (width - boardWidth) * .5f;
    top = (height - boardHeight) * .5f;
    background =
        new RadialGradient(
            width * .46f,
            height * .4f,
            width * .7f,
            new int[] {0xff18272d, 0xff0d171e, 0xff060b12},
            null,
            Shader.TileMode.CLAMP);
    for (int i = 0; i < ribbonShaders.length; i++) {
      ribbonShaders[i] =
          new LinearGradient(
              left,
              top,
              left + boardWidth,
              top + boardHeight,
              new int[] {lighten(colors[i], .17f), colors[i], darken(colors[i], .72f)},
              null,
              Shader.TileMode.CLAMP);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    paint.setStyle(Paint.Style.FILL);
    paint.setShader(background);
    paint.setAlpha(255);
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    paint.setShader(null);
    if (cell <= 0) return;
    canvas.save();
    canvas.translate(
        (float) Math.sin(seconds * .019) * cell * .42f,
        (float) Math.cos(seconds * .023) * cell * .34f);
    drawTable(canvas);
    float visibility = Math.min(1, birthAge / .65f);
    if (game.ended) visibility *= Math.max(0, 1 - endingAge / 1.3f);
    drawFood(canvas, visibility);
    for (int i = 0; i < game.snakes.size(); i++) drawSnake(canvas, i, visibility);
    if (game.ended && options.effects > 0) drawCollision(canvas);
    canvas.restore();
    if (options.brightness < 1) {
      paint.setStyle(Paint.Style.FILL);
      paint.setColor(Color.BLACK);
      paint.setAlpha((int) ((1 - options.brightness) * 255));
      canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
  }

  private void drawTable(Canvas canvas) {
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(0xff101e25);
    rectangle.set(
        left - cell * .7f,
        top - cell * .7f,
        left + boardWidth + cell * .7f,
        top + boardHeight + cell * .7f);
    canvas.drawRoundRect(rectangle, cell * 1.2f, cell * 1.2f, paint);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(cell * .035f);
    paint.setColor(0xff26363d);
    canvas.drawRoundRect(rectangle, cell * 1.2f, cell * 1.2f, paint);
    paint.setStyle(Paint.Style.FILL);
    for (int y = 0; y < game.rows; y++) {
      for (int x = 0; x < game.columns; x++) {
        if (game.walls[game.cell(x, y)]) continue;
        float wave = .5f + .5f * (float) Math.sin(x * .12 + y * .17 + seconds * .13);
        paint.setColor(0xff759097);
        paint.setAlpha((int) (10 + wave * 12));
        canvas.drawCircle(cx(x), cy(y), cell * .033f, paint);
      }
    }
    paint.setAlpha(255);
    if (game.stage == 1) {
      drawIsland(canvas, game.columns / 3 - 1, game.rows / 3 - 1, 3, 3);
      drawIsland(canvas, game.columns * 2 / 3 - 1, game.rows * 2 / 3 - 1, 3, 3);
      drawIsland(canvas, game.columns * 2 / 3 - 1, game.rows / 3 - 1, 3, 3);
      drawIsland(canvas, game.columns / 3 - 1, game.rows * 2 / 3 - 1, 3, 3);
    } else if (game.stage == 2) {
      drawIsland(canvas, game.columns / 3, game.rows / 3, 2, game.rows / 3);
      drawIsland(canvas, game.columns * 2 / 3, game.rows / 3, 2, game.rows / 3);
    }
    if (options.effects > 0) {
      for (int i = 0; i < 24; i++) {
        float x = left + ((i * .618034f + seconds * .0012f) % 1) * boardWidth;
        float y = top + ((i * .414214f + seconds * .0023f) % 1) * boardHeight;
        paint.setColor(0xffa9c7ce);
        paint.setAlpha((int) (12 + 12 * Math.sin(seconds * .4 + i)));
        canvas.drawCircle(x, y, cell * .025f, paint);
      }
    }
  }

  private void drawIsland(Canvas canvas, int x, int y, int width, int height) {
    float inset = cell * .13f;
    rectangle.set(
        left + x * cell + inset,
        top + y * cell + inset + cell * .1f,
        left + (x + width) * cell - inset,
        top + (y + height) * cell - inset + cell * .1f);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(0xff070f15);
    paint.setAlpha(255);
    canvas.drawRoundRect(rectangle, cell * .36f, cell * .36f, paint);
    rectangle.offset(0, -cell * .1f);
    paint.setColor(0xff24383f);
    canvas.drawRoundRect(rectangle, cell * .36f, cell * .36f, paint);
    rectangle.inset(cell * .07f, cell * .07f);
    paint.setColor(0xff2c4148);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(cell * .035f);
    canvas.drawRoundRect(rectangle, cell * .3f, cell * .3f, paint);
    paint.setStyle(Paint.Style.FILL);
  }

  private void drawFood(Canvas canvas, float visibility) {
    for (int i = 0; i < game.food.size(); i++) {
      int food = game.food.get(i);
      float x = cx(food % game.columns), y = cy(food / game.columns);
      float pulse = 1 + .10f * (float) Math.sin(seconds * 2.7f + food);
      paint.setStyle(Paint.Style.FILL);
      if (options.effects > 0) {
        paint.setColor(0xffffcd88);
        paint.setAlpha((int) (7 * visibility));
        canvas.drawCircle(x, y, cell * .9f * pulse, paint);
        paint.setAlpha((int) (17 * visibility));
        canvas.drawCircle(x, y, cell * .57f * pulse, paint);
      }
      paint.setColor(0xffb9783c);
      paint.setAlpha((int) (240 * visibility));
      canvas.drawCircle(x, y + cell * .045f, cell * .23f, paint);
      paint.setColor(0xffffd493);
      paint.setAlpha((int) (255 * visibility));
      canvas.drawCircle(x, y, cell * .2f * pulse, paint);
      paint.setColor(0xfffff0d2);
      canvas.drawCircle(x - cell * .055f, y - cell * .055f, cell * .075f, paint);
      if (options.effects == 2) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(cell * .025f);
        paint.setColor(0xffffd493);
        paint.setAlpha((int) (45 * visibility));
        canvas.drawCircle(x, y, cell * (.45f + .09f * pulse), paint);
      }
    }
  }

  private void drawSnake(Canvas canvas, int index, float visibility) {
    NibblesGame.Snake snake = game.snakes.get(index);
    int length = snake.body.size();
    float fraction = game.ended ? 1 : progress;
    for (int i = 0; i < length; i++) {
      int current = snake.body.get(i);
      int previous = snake.previous.get(Math.min(i, snake.previous.size() - 1));
      px[i] =
          cx(
              previous % game.columns
                  + (current % game.columns - previous % game.columns) * fraction);
      py[i] =
          cy(
              previous / game.columns
                  + (current / game.columns - previous / game.columns) * fraction);
    }
    ribbon.reset();
    ribbon.moveTo(px[length - 1], py[length - 1]);
    for (int i = length - 2; i >= 1; i--) {
      ribbon.quadTo(px[i], py[i], (px[i] + px[i - 1]) * .5f, (py[i] + py[i - 1]) * .5f);
    }
    ribbon.lineTo(px[0], py[0]);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    canvas.save();
    canvas.translate(0, cell * .13f);
    paint.setStrokeWidth(cell * .74f);
    paint.setColor(0xff030a0d);
    paint.setAlpha((int) (200 * visibility));
    canvas.drawPath(ribbon, paint);
    canvas.restore();
    if (options.effects > 0) {
      paint.setColor(colors[index]);
      paint.setAlpha((int) ((options.effects == 2 ? 15 : 7) * visibility));
      paint.setStrokeWidth(cell * 1.1f);
      canvas.drawPath(ribbon, paint);
    }
    paint.setColor(darken(colors[index], .50f));
    paint.setAlpha((int) (255 * visibility));
    paint.setStrokeWidth(cell * .68f);
    canvas.drawPath(ribbon, paint);
    paint.setShader(ribbonShaders[index]);
    paint.setAlpha((int) (255 * visibility));
    paint.setStrokeWidth(cell * .54f);
    canvas.drawPath(ribbon, paint);
    paint.setShader(null);
    canvas.save();
    canvas.translate(0, -cell * .12f);
    paint.setColor(lighten(colors[index], .62f));
    paint.setAlpha((int) (70 * visibility));
    paint.setStrokeWidth(cell * .085f);
    canvas.drawPath(ribbon, paint);
    canvas.restore();
    float dx = px[0] - px[1], dy = py[0] - py[1];
    float distance = Math.max(.001f, (float) Math.sqrt(dx * dx + dy * dy));
    dx /= distance;
    dy /= distance;
    paint.setStyle(Paint.Style.FILL);
    for (int side = -1; side <= 1; side += 2) {
      float x = px[0] + dx * cell * .1f - dy * cell * .12f * side;
      float y = py[0] + dy * cell * .1f + dx * cell * .12f * side;
      paint.setColor(0xffeaf5ee);
      paint.setAlpha((int) (220 * visibility));
      canvas.drawCircle(x, y, cell * .058f, paint);
      paint.setColor(0xff15232b);
      canvas.drawCircle(x + dx * cell * .018f, y + dy * cell * .018f, cell * .033f, paint);
    }
  }

  private void drawCollision(Canvas canvas) {
    for (int i = 0; i < game.snakes.size(); i++) {
      NibblesGame.Snake snake = game.snakes.get(i);
      if (!snake.collided) continue;
      int head = snake.body.get(0);
      float alpha = Math.max(0, 1 - endingAge);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeWidth(cell * .05f);
      paint.setColor(colors[i]);
      paint.setAlpha((int) (140 * alpha));
      canvas.drawCircle(
          cx(head % game.columns), cy(head / game.columns), cell * (.6f + endingAge * 3), paint);
    }
  }

  private float cx(float x) {
    return left + (x + .5f) * cell;
  }

  private float cy(float y) {
    return top + (y + .5f) * cell;
  }

  private static int darken(int color, float factor) {
    return Color.rgb(
        (int) (Color.red(color) * factor),
        (int) (Color.green(color) * factor),
        (int) (Color.blue(color) * factor));
  }

  private static int lighten(int color, float factor) {
    return Color.rgb(
        (int) (Color.red(color) + (255 - Color.red(color)) * factor),
        (int) (Color.green(color) + (255 - Color.green(color)) * factor),
        (int) (Color.blue(color) + (255 - Color.blue(color)) * factor));
  }
}
