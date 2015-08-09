package com.pololanguage.pologo;


import java.util.HashSet;
import java.util.Set;

/** Track a single-color chain of stones */
public class Chain {
  private Set<Stone> stones;
  private StoneColor color;
  private Set<BoxCoords> liberties;

  Chain() {
    stones = new HashSet<>();
    liberties = new HashSet<>();
  }

  /** Check if the supplied coords are the last liberty for this group (i.e. a stone here kills) */
  boolean isLastLiberty(BoxCoords coords) {
    return liberties.size() == 1 && liberties.contains(coords);
  }
}
