package com.pololanguage.pologo;


import java.util.HashSet;
import java.util.Set;

public class Chain {
  private Set<Stone> stones;
  private StoneColor color;
  private Set<BoxCoords> liberties;

  Chain() {
    stones = new HashSet<>();
    liberties = new HashSet<>();
  }

  boolean isLastLiberty(BoxCoords coords) {
    return liberties.size() == 1 && liberties.contains(coords);
  }
}
