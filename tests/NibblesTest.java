package com.jeremykenedy.nibbleshd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public final class NibblesTest {
  private static int assertions;

  public static void main(String[] args) {
    options();
    movementAndGrowth();
    collisions();
    resetAndPlacement();
    autonomousRoutes();
    System.out.println("Nibbles behavioral tests passed: " + assertions + " assertions");
  }

  private static void check(boolean condition, String message) {
    assertions++;
    if (!condition) throw new AssertionError(message);
  }

  private static void rejects(Runnable action) {
    try {
      action.run();
      throw new AssertionError("Expected rejection");
    } catch (IllegalArgumentException expected) {
      assertions++;
    }
  }

  private static String[] defaults() {
    String[] result = new String[NibblesOptions.SCHEMA.length];
    for (int i = 0; i < result.length; i++) result[i] = NibblesOptions.SCHEMA[i][2];
    return result;
  }

  private static void options() {
    String[] values = defaults();
    NibblesOptions defaults = new NibblesOptions(values, new Random(0), false);
    check(
        defaults.snakeCount == 2 && defaults.columns == 48 && defaults.rows == 26,
        "Default composition");
    check(defaults.stepsPerSecond == 8 && defaults.stage == -1, "Default pacing and varied rounds");
    for (String[] spec : NibblesOptions.SCHEMA) {
      check(NibblesOptions.row(spec[0]) >= 0, "Declared option is discoverable");
      check(NibblesOptions.supported(spec[0], "random"), "Per-option random is supported");
      check(!NibblesOptions.supported(spec[0], "invalid"), "Reject invalid persisted value");
      check(!NibblesOptions.supported(spec[0], null), "Reject null persisted value");
      check(
          NibblesOptions.validated(spec[0], null).equals(spec[2]), "Recover corrupted preference");
      for (int i = 3; i < spec.length; i++) {
        check(NibblesOptions.supported(spec[0], spec[i]), "Every public choice is accepted");
        String[] selected = defaults();
        selected[NibblesOptions.row(spec[0])] = spec[i];
        NibblesOptions option = new NibblesOptions(selected, new Random(2), false);
        check(option.rows > 0 && option.stepsPerSecond > 0, "All choices construct a scene");
      }
    }
    check(!NibblesOptions.supported("unknown", "x"), "Unknown keys rejected");
    check(!NibblesOptions.supported("randomize_all", "1"), "Boolean is strict");
    check(NibblesOptions.supported("randomize_all", "true"), "Boolean on");
    check(NibblesOptions.supported("randomize_all", "false"), "Boolean off");
    rejects(() -> NibblesOptions.validated("unknown", "x"));
    rejects(() -> new NibblesOptions(null, new Random(), false));
    rejects(() -> new NibblesOptions(new String[0], new Random(), false));
    Arrays.fill(values, "bad preference");
    check(
        new NibblesOptions(values, new Random(1), false).stage == -1,
        "Invalid stage retains per-round random default");
    Arrays.fill(values, "random");
    HashSet<String> combinations = new HashSet<String>();
    for (int seed = 0; seed < 50; seed++) {
      NibblesOptions option = new NibblesOptions(values, new Random(seed), seed % 2 == 0);
      check(
          option.snakeCount >= 1
              && option.snakeCount <= 3
              && option.palette >= 0
              && option.palette < 4
              && option.stage == -1,
          "Random values stay in declared bounds");
      combinations.add(option.snakeCount + ":" + option.palette + ":" + option.effects);
    }
    check(combinations.size() > 10, "Random choices vary composition");
  }

  private static NibblesGame game(int count) {
    return new NibblesGame(24, 18, count, 0, new Random(13));
  }

  private static void body(NibblesGame game, int snake, int... cells) {
    NibblesGame.Snake target = game.snakes.get(snake);
    target.body.clear();
    for (int cell : cells) target.body.add(cell);
    target.previous.clear();
    target.previous.addAll(target.body);
    target.growth = 0;
  }

  private static void movementAndGrowth() {
    NibblesGame game = game(1);
    NibblesGame.Snake snake = game.snakes.get(0);
    game.food.clear();
    game.food.add(game.cell(9, 3));
    int length = snake.body.size(), tail = snake.body.get(length - 1);
    game.advance(new int[] {0});
    check(
        snake.eaten == 1 && snake.body.size() == length + 1 && snake.growth == 2,
        "Eating starts real body growth");
    check(
        snake.previous.get(0) == game.cell(8, 3) && snake.body.get(0) == game.cell(9, 3),
        "Previous and current paths support interpolation");
    check(snake.body.contains(tail), "Tail remains while growing");
    game.food.clear();
    game.food.add(game.cell(20, 12));
    game.advance(new int[] {0});
    game.advance(new int[] {0});
    check(
        snake.body.size() == length + 3 && snake.growth == 0, "One food grows exactly three cells");
    game.advance(new int[] {0});
    check(
        snake.body.size() == length + 3 && !snake.body.contains(tail), "Tail resumes after growth");
    check(game.food.size() == 3, "Food replenishes to the configured board population");
    check(
        game.next(game.cell(23, 5), 0) == -1 && game.next(game.cell(0, 5), 2) == -1,
        "Horizontal moves cannot wrap");
    check(
        game.next(game.cell(5, 0), 3) == -1 && game.next(game.cell(5, 17), 1) == -1,
        "Vertical moves cannot wrap");
    check(!game.safe(0, 10000), "Out-of-range routes are unsafe");
    rejects(() -> game.advance(new int[0]));
    rejects(() -> game.advance(new int[] {-1}));
    rejects(() -> game.advance(new int[] {4}));
  }

  private static void collisions() {
    NibblesGame wall = game(1);
    body(wall, 0, wall.cell(23, 4), wall.cell(22, 4));
    wall.advance(new int[] {0});
    check(wall.ended && wall.snakes.get(0).collided, "Wall collision ends round");
    ArrayList<Integer> stopped = new ArrayList<Integer>(wall.snakes.get(0).body);
    wall.tick();
    wall.advance(new int[] {2});
    check(stopped.equals(wall.snakes.get(0).body), "Ended rounds cannot continue moving");
    NibblesGame self = game(1);
    body(
        self,
        0,
        self.cell(4, 4),
        self.cell(4, 5),
        self.cell(5, 5),
        self.cell(5, 4),
        self.cell(6, 4));
    self.advance(new int[] {0});
    check(self.ended, "Body collision ends round");
    NibblesGame tail = game(1);
    body(tail, 0, tail.cell(4, 4), tail.cell(4, 5), tail.cell(5, 5), tail.cell(5, 4));
    tail.food.clear();
    check(tail.safe(0, tail.cell(5, 4)), "Own departing tail is safe");
    tail.snakes.get(0).growth = 1;
    check(!tail.safe(0, tail.cell(5, 4)), "Growing tail is occupied");
    tail.snakes.get(0).growth = 0;
    tail.food.add(tail.cell(5, 4));
    check(!tail.safe(0, tail.cell(5, 4)), "Eating cannot free the tail");
    NibblesGame island = game(1);
    island.walls[island.cell(9, 3)] = true;
    island.advance(new int[] {0});
    check(island.ended, "Obstacle collision ends round");
    NibblesGame headOn = game(2);
    body(headOn, 0, headOn.cell(5, 5), headOn.cell(4, 5));
    body(headOn, 1, headOn.cell(7, 5), headOn.cell(8, 5));
    headOn.advance(new int[] {0, 2});
    check(
        headOn.ended && headOn.snakes.get(0).collided && headOn.snakes.get(1).collided,
        "Simultaneous head collision stops both snakes");
    NibblesGame swap = game(2);
    body(swap, 0, swap.cell(5, 5), swap.cell(4, 5));
    body(swap, 1, swap.cell(6, 5), swap.cell(7, 5));
    swap.advance(new int[] {0, 2});
    check(swap.ended, "Heads cannot pass through one another");
  }

  private static void resetAndPlacement() {
    for (int stage = -1; stage <= 2; stage++) {
      NibblesGame game = new NibblesGame(24, 18, 3, stage, new Random(18));
      for (int round = 0; round < 8; round++) {
        HashSet<Integer> occupied = new HashSet<Integer>();
        for (NibblesGame.Snake snake : game.snakes) {
          check(
              snake.body.size() == 7 && snake.eaten == 0 && snake.growth == 0 && !snake.collided,
              "Round restores initial bodies and counters");
          for (int cell : snake.body)
            check(occupied.add(cell) && !game.walls[cell], "Spawn is unobstructed");
        }
        for (int cell : game.food)
          check(occupied.add(cell) && !game.walls[cell], "Food is distinct and free");
        game.ended = true;
        game.snakes.get(0).growth = 9;
        int previousRound = game.round;
        game.reset();
        check(
            !game.ended && game.ticks == 0 && game.round == previousRound + 1,
            "Reset starts a new playable round");
      }
    }
    NibblesGame full = game(1);
    Arrays.fill(full.walls, true);
    full.food.clear();
    full.replenish();
    check(full.food.isEmpty(), "A full board does not place impossible food or loop");
    NibblesGame filled = game(1);
    Arrays.fill(filled.walls, true);
    filled.walls[0] = false;
    filled.walls[1] = false;
    filled.walls[24] = false;
    body(filled, 0, 0, 24);
    filled.snakes.get(0).growth = 1;
    filled.food.clear();
    filled.advance(new int[] {0});
    check(filled.ended && filled.food.isEmpty(), "Filling the last free cell completes the round");
    NibblesGame timeout = game(1);
    timeout.ticks = 1599;
    timeout.advance(new int[] {0});
    check(timeout.ended, "Long rounds recover with a fresh game");
    rejects(() -> new NibblesGame(19, 18, 1, 0, new Random()));
    rejects(() -> new NibblesGame(24, 15, 1, 0, new Random()));
    rejects(() -> new NibblesGame(24, 18, 0, 0, new Random()));
    rejects(() -> new NibblesGame(24, 18, 4, 0, new Random()));
    rejects(() -> new NibblesGame(24, 18, 1, -2, new Random()));
    rejects(() -> new NibblesGame(24, 18, 1, 3, new Random()));
  }

  private static void autonomousRoutes() {
    int eaten = 0, completed = 0;
    HashSet<String> paths = new HashSet<String>();
    long started = System.nanoTime();
    for (int seed = 0; seed < 12; seed++) {
      NibblesGame game = new NibblesGame(38, 22, 1 + seed % 3, seed % 3, new Random(seed));
      StringBuilder path = new StringBuilder();
      for (int step = 0; step < 300; step++) {
        if (game.ended) {
          completed++;
          for (NibblesGame.Snake snake : game.snakes) eaten += snake.eaten;
          game.reset();
        }
        for (int i = 0; i < game.snakes.size(); i++) {
          NibblesGame.Snake snake = game.snakes.get(i);
          boolean hasSafeMove = false;
          for (int direction = 0; direction < 4; direction++)
            hasSafeMove |= game.safe(i, game.next(snake.body.get(0), direction));
          int direction = game.chooseDirection(i);
          check(
              !hasSafeMove || game.safe(i, game.next(snake.body.get(0), direction)),
              "Planner chooses a legal route whenever one exists");
        }
        game.tick();
        if (step < 100) path.append(game.snakes.get(0).body.get(0)).append(',');
        if (!game.ended) {
          HashSet<Integer> occupied = new HashSet<Integer>();
          for (NibblesGame.Snake snake : game.snakes)
            for (int cell : snake.body)
              check(
                  !game.walls[cell] && occupied.add(cell),
                  "Autonomous moves never overlap accepted bodies or obstacles");
          for (int cell : game.food) check(!occupied.contains(cell), "Food remains outside snakes");
        }
      }
      for (NibblesGame.Snake snake : game.snakes) eaten += snake.eaten;
      paths.add(path.toString());
    }
    check(paths.size() == 12, "Seeds produce different routes, not one fixed demonstration");
    check(eaten > 100, "Autonomous snakes actually seek food and grow");
    System.out.println(
        "Autonomous simulation: food="
            + eaten
            + ", collision/recovery rounds="
            + completed
            + ", host milliseconds="
            + (System.nanoTime() - started) / 1000000);
  }
}
