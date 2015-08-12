package com.pololanguage.pologo;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/** Track a single-color chain of stones */
public class Chain {
  StoneColor color;
  private Set<Stone> stones;
  private Set<BoxCoords> liberties;

  Chain(StoneColor c) {
    stones = new HashSet<>();
    liberties = new HashSet<>();
    color = c;
  }

  /** Public access to the set of stones */
  public Set<Stone> getStones() {
    return stones;
  }

  /** Adds the supplied stone to this chain, updating liberties */
  public void add(Stone stone, ArrayList<BoxCoords> newLiberties) {
    if (color != stone.color) {
      throw new IllegalArgumentException("Can not add stone of color " + stone.color +
                                         " to chain of color " + color);
    }
    liberties.remove(stone.coords);
    liberties.addAll(newLiberties);
    stones.add(stone);
  }

  /** Check if the supplied coords are the last liberty for this group (i.e. a stone here kills) */
  boolean isLastLiberty(BoxCoords coords) {
    return liberties.size() == 1 && liberties.contains(coords);
  }
}
