package com.pololanguage.pologo;


import java.util.HashSet;
import java.util.Set;


/** Track a single-color chain of stones */
public class Chain {
  StoneColor color;
  private Set<Stone> stones;
  private Set<BoxCoords> liberties;

  Chain(StoneColor c) {
    color = c;
    stones = new HashSet<>();
    liberties = new HashSet<>();
  }

  /** Public access to the set of stones */
  public Set<Stone> getStones() {
    return stones;
  }

  /** Adds the supplied stone to this chain, updating liberties */
  public void add(Stone stone, Set<BoxCoords> newLiberties) {
    if (color != stone.color) {
      throw new IllegalArgumentException("Can not add stone of color " + stone.color +
                                         " to chain of color " + color);
    }
    liberties.remove(stone.coords);
    liberties.addAll(newLiberties);
    stones.add(stone);
  }

  /** Copies all stones and liberties from the supplied chain to this; does not modify argument */
  public void merge(Chain chain) {
    if (color != chain.color) {
      throw new IllegalArgumentException("Can not merge chain of color " + chain.color +
                                         " to chain of color " + color);
    }
    stones.addAll(chain.stones);
    liberties.addAll(chain.liberties);
  }

//  /** Removes the supplied stone and liberties */
//  public void remove(Stone stone, Set<BoxCoords> libertiesToRemove) {
//    if (!stones.remove(stone)) {
//      throw new IllegalArgumentException("Stone not in chain so can't be removed");
//    }
//    liberties.removeAll(libertiesToRemove);
//  }

  public void addLiberty(BoxCoords coords) {
    liberties.add(coords);
  }

  /** Removes the supplied liberty */
  public void removeLiberty(BoxCoords coords) {
    liberties.remove(coords);
  }

  /** Check if the supplied coords are the last liberty for this group (i.e. a stone here kills) */
  boolean isLastLiberty(BoxCoords coords) {
    return liberties.size() == 1 && liberties.contains(coords);
  }
}
