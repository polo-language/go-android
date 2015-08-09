package com.pololanguage.pologo;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


/**
 * Manages all game-play (i.e. non-visual) aspects of stone handling
 * Manages creation, updating, and removal of chains of like-colored stones
 */
public class ChainManager {
  /** Holds all chains on the board */
  private Set<Chain> chains;

  /** Track filled coordinates and map them to the chain covering that location*/
  private Map<BoxCoords, Chain> filled;

  /** Track moves */
  private Moves moves;

  ChainManager() {
    chains = new HashSet<>();
    filled = new HashMap<>();
    moves = new Moves();
  }

  /**
   * Captures chains of opposing color neighboring the given stone
   */
  //void capture(Stone stone) {}

  /**
   * Sets the stone's chain field and creates/updates the appropriate chain
   */
  //Chain setNewChain(Stone stone) {}

  /**
   * @return true if the supplied coordinates are occupied by a stone
   */
  boolean taken(BoxCoords coords) {
    return filled.containsKey(coords);
  }

  /**
   * @return true if board contains no stones
   */
  boolean hasNoMoves() {
    return filled.isEmpty();
  }

  /**
   * Add stone to board
   */
  void addStone(Stone stone) {
    moves.add(stone);
    filled.put(stone.coords, new Chain()); // TODO: dummy chain used here
  }

  /**
   * Remove last move from board
   * @return popped Stone
   */
  Stone popStone() {
    Stone stone =  moves.pop();
    filled.remove(stone.coords);
    return stone;
  }

  /**
   * Reset to empty board
   */
  void reset() {
    moves.clear();
    filled.clear();
  }

  String toJson() {
    return moves.toJson();
  }
}

