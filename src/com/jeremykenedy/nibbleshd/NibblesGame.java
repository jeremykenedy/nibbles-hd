package com.jeremykenedy.nibbleshd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/** A simultaneous, bounded snake game. Rendering never changes its rules. */
public final class NibblesGame {
  public static final int[] DX = {1, 0, -1, 0};
  public static final int[] DY = {0, 1, 0, -1};

  public static final class Snake {
    public final ArrayList<Integer> body = new ArrayList<Integer>();
    public final ArrayList<Integer> previous = new ArrayList<Integer>();
    public int direction, growth, eaten;
    public boolean collided;
  }

  public final int columns, rows;
  public final ArrayList<Snake> snakes = new ArrayList<Snake>();
  public final ArrayList<Integer> food = new ArrayList<Integer>();
  public final boolean[] walls;
  public final Random random;
  public int round, ticks, stage;
  public boolean ended;
  private final int stageChoice;
  private final boolean[] occupied;
  private final int[] queue, distances, available;

  public NibblesGame(int columns, int rows, int count, int stage, Random random) {
    if (columns < 24 || rows < 16 || count < 1 || count > 3 || stage < -1 || stage > 2)
      throw new IllegalArgumentException("Unsupported board");
    this.columns = columns;
    this.rows = rows;
    this.stageChoice = stage;
    this.random = random;
    walls = new boolean[columns * rows];
    occupied = new boolean[walls.length];
    queue = new int[walls.length];
    distances = new int[walls.length];
    available = new int[walls.length];
    for (int i = 0; i < count; i++) snakes.add(new Snake());
    reset();
  }

  public int cell(int x, int y) {
    return y * columns + x;
  }

  public int next(int cell, int direction) {
    int x = cell % columns + DX[direction], y = cell / columns + DY[direction];
    return x < 0 || y < 0 || x >= columns || y >= rows ? -1 : cell(x, y);
  }

  public void reset() {
    round++;
    ticks = 0;
    ended = false;
    stage = stageChoice < 0 ? random.nextInt(3) : stageChoice;
    Arrays.fill(walls, false);
    if (stage == 1) {
      block(columns / 3 - 1, rows / 3 - 1, 3, 3);
      block(columns * 2 / 3 - 1, rows * 2 / 3 - 1, 3, 3);
      block(columns * 2 / 3 - 1, rows / 3 - 1, 3, 3);
      block(columns / 3 - 1, rows * 2 / 3 - 1, 3, 3);
    } else if (stage == 2) {
      block(columns / 3, rows / 3, 2, rows / 3);
      block(columns * 2 / 3, rows / 3, 2, rows / 3);
    }
    for (int i = 0; i < snakes.size(); i++) {
      Snake snake = snakes.get(i);
      snake.body.clear();
      snake.previous.clear();
      snake.direction = i == 1 ? 2 : 0;
      int headX = i == 1 ? columns - 9 : i == 2 ? Math.min(8, columns / 3 - 2) : 8;
      int y = i == 0 ? 3 : i == 1 ? rows - 4 : rows / 2;
      for (int j = 0; j < 7; j++) snake.body.add(cell(headX - DX[snake.direction] * j, y));
      snake.previous.addAll(snake.body);
      snake.growth = 0;
      snake.eaten = 0;
      snake.collided = false;
    }
    food.clear();
    replenish();
  }

  private void block(int x, int y, int width, int height) {
    for (int yy = y; yy < y + height; yy++)
      for (int xx = x; xx < x + width; xx++) walls[cell(xx, yy)] = true;
  }

  public void tick() {
    if (ended) return;
    int[] directions = new int[snakes.size()];
    for (int i = 0; i < directions.length; i++) directions[i] = chooseDirection(i);
    advance(directions);
  }

  public void advance(int[] directions) {
    if (ended) return;
    if (directions.length != snakes.size())
      throw new IllegalArgumentException("One move per snake");
    int[] heads = new int[snakes.size()];
    for (int i = 0; i < heads.length; i++) {
      if (directions[i] < 0 || directions[i] > 3)
        throw new IllegalArgumentException("Invalid move");
      heads[i] = next(snakes.get(i).body.get(0), directions[i]);
    }
    for (int i = 0; i < heads.length; i++) {
      Snake snake = snakes.get(i);
      snake.previous.clear();
      snake.previous.addAll(snake.body);
      boolean collision = !safe(i, heads[i]);
      for (int j = 0; j < heads.length; j++) {
        if (i != j && heads[i] == heads[j]) collision = true;
      }
      snake.collided = collision;
      if (collision) ended = true;
    }
    if (ended) return;
    for (int i = 0; i < heads.length; i++) {
      Snake snake = snakes.get(i);
      snake.direction = directions[i];
      boolean ate = food.remove(Integer.valueOf(heads[i]));
      if (ate) {
        snake.growth += 3;
        snake.eaten++;
      }
      snake.body.add(0, heads[i]);
      if (snake.growth > 0) snake.growth--;
      else snake.body.remove(snake.body.size() - 1);
    }
    ticks++;
    replenish();
    if (ticks >= 1600 || food.isEmpty()) ended = true;
  }

  public boolean safe(int snakeIndex, int target) {
    if (target < 0 || target >= walls.length || walls[target]) return false;
    for (int i = 0; i < snakes.size(); i++) {
      Snake snake = snakes.get(i);
      int limit = snake.body.size();
      // Only the moving snake's own tail is guaranteed to leave its cell.
      if (i == snakeIndex && snake.growth == 0 && !food.contains(target)) limit--;
      for (int j = 0; j < limit; j++) if (snake.body.get(j) == target) return false;
    }
    return true;
  }

  public int chooseDirection(int index) {
    Snake snake = snakes.get(index);
    int best = snake.direction;
    double bestScore = -Double.MAX_VALUE;
    int offset = random.nextInt(4);
    for (int n = 0; n < 4; n++) {
      int direction = (n + offset) % 4;
      int target = next(snake.body.get(0), direction);
      if (!safe(index, target)) continue;
      boolean threat = false;
      for (int i = 0; i < snakes.size(); i++) {
        if (i != index) {
          int other = snakes.get(i).body.get(0);
          if (Math.abs(target % columns - other % columns)
                  + Math.abs(target / columns - other / columns)
              == 1) threat = true;
        }
      }
      int[] route = reachable(index, target);
      int area = route[0], distance = route[1];
      double score = Math.min(area, snake.body.size() + 25) * 3.0;
      if (area <= snake.body.size() + snake.growth + 3) score -= 500;
      if (threat) score -= 350;
      score -= distance < 0 ? 200 : distance * 5;
      if (direction == snake.direction) score += 1.2;
      score += random.nextDouble() * 2.3;
      if (score > bestScore) {
        best = direction;
        bestScore = score;
      }
    }
    return best;
  }

  private int[] reachable(int index, int start) {
    System.arraycopy(walls, 0, occupied, 0, walls.length);
    for (int i = 0; i < snakes.size(); i++) {
      Snake snake = snakes.get(i);
      int limit = snake.body.size();
      if (i == index && snake.growth == 0) limit--;
      for (int j = 0; j < limit; j++) occupied[snake.body.get(j)] = true;
    }
    int read = 0, write = 1, foodDistance = -1;
    queue[0] = start;
    occupied[start] = true;
    distances[start] = 0;
    while (read < write) {
      int current = queue[read++];
      if (foodDistance < 0 && food.contains(current)) foodDistance = distances[current];
      for (int direction = 0; direction < 4; direction++) {
        int target = next(current, direction);
        if (target >= 0 && !occupied[target]) {
          occupied[target] = true;
          distances[target] = distances[current] + 1;
          queue[write++] = target;
        }
      }
    }
    return new int[] {write, foodDistance};
  }

  public void replenish() {
    System.arraycopy(walls, 0, occupied, 0, walls.length);
    for (Snake snake : snakes) for (int cell : snake.body) occupied[cell] = true;
    for (int cell : food) occupied[cell] = true;
    int count = 0;
    for (int cell = 0; cell < occupied.length; cell++)
      if (!occupied[cell]) available[count++] = cell;
    while (food.size() < snakes.size() + 2 && count > 0) {
      int choice = random.nextInt(count);
      food.add(available[choice]);
      available[choice] = available[--count];
    }
  }
}
