package com.pololanguage.pologo;

import java.util.Set;

/** Stores a single move: a stone and any stones captured by it */
public class Move {
  private Stone stone;
  private Stone[] captured;

  private static Stone[] typeArray = new Stone[0];

  Move(Stone s, Set<Stone> c) {
    stone = s;
    captured = c.toArray(typeArray);
  }

  public Stone getStone() {
    return stone;
  }

  public Stone[] getCaptured() {
    return captured;
  }
}
