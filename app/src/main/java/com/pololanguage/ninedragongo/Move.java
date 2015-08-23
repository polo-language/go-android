package com.pololanguage.ninedragongo;

import java.util.Set;

/** Stores a single move: a stone and any stones captured by it */
public class Move {
  private Stone stone;
  private Set<Stone> captured;

  Move(Stone s, Set<Stone> c) {
    stone = s;
    captured = c;
  }

  public Stone getStone() {
    return stone;
  }

  public Set<Stone> getCaptured() {
    return captured;
  }
}
