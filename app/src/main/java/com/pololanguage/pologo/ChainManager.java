package com.pololanguage.pologo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


/**
 * Manages all game-play (i.e. non-visual) aspects of stone handling
 * Manages creation, updating, and removal of chains of like-colored stones
 */
public class ChainManager {
  /**
   * Holds all chains on the board
   */
  private Set<Chain> chains;

  /**
   * Track filled coordinates and map them to the chain covering that location
   */
  private Map<BoxCoords, Chain> filled;

  /**
   * Track moves in order
   */
  private Moves moves;

  ChainManager() {
    chains = new HashSet<>();
    filled = new HashMap<>();
    moves = new Moves();
  }

  /**
   * Returns true if the supplied coordinates are occupied by a stone
   */
  public boolean taken(BoxCoords coords) {
    return filled.containsKey(coords);
  }

  /**
   * Returns true if board contains no stones
   */
  public boolean hasNoMoves() {
    return filled.isEmpty();
  }

  /**
   * Add stone to board, killing opposing groups as necessary
   * Returns false if move would be suicide
   */
  public boolean addStone(Stone stone) {
    killOpponents(stone);
    if (!isSuicide(stone)) {
      filled.put(stone.coords, mergeToChains(stone));
      moves.add(stone);
      return true;
    } else {
      return false;
    }
  }

  /** Returns up to 4 chains neighboring the supplied coordinates and of the given color */
  private ArrayList<Chain> getNeighborChains(BoxCoords coords, StoneColor color) {
    NeighborChecker<Chain> neighborChecker = new NeighborChecker<>();

    return neighborChecker.getMatchingNeighbors(new CheckCoords<Chain>() {
      public void check(ArrayList<Chain> found, BoxCoords coords, Object criterion) {
        try {
          if (taken(coords) && filled.get(coords).color == criterion) {
            found.add(filled.get(coords));
          }
        } catch (IndexOutOfBoundsException e) { /* Do nothing: must be off the end of the board */ }
      }
    }, coords, color);
  }

  /** Checks for and removes opposing chains with this stone as their last liberty */
  private void killOpponents(Stone stone) {
    ArrayList<Chain> opposingChains = getNeighborChains(stone.coords, stone.color.getOther());
    for (Chain chain : opposingChains) {
      if (chain.isLastLiberty(stone.coords)) {
        kill(chain);
      }
    }
  }

  /** Removes stones in chain from board and increments capture count of opposing player */
  private void kill(Chain chain) {
    // TODO
  }

  /** Returns true if stone can be added; false if move would be suicide */
  private boolean isSuicide(Stone stone) {
    ArrayList<Chain> friends = getNeighborChains(stone.coords, stone.color);
    for (Chain chain : friends) {
      if (!chain.isLastLiberty(stone.coords)) {
        return false;
      }
    }
    return getLiberties(stone.coords).size() == 0;
  }

  /** Returns an ArrayList containing coordinates of any liberties of the given position */
  private ArrayList<BoxCoords> getLiberties(BoxCoords coords) {
    NeighborChecker<BoxCoords> neighborChecker = new NeighborChecker<>();

    return neighborChecker.getMatchingNeighbors(new CheckCoords<BoxCoords>() {
      public void check(ArrayList<BoxCoords> neighbors, BoxCoords coords, Object dummy) {
        if (!taken(coords)) {
          neighbors.add(coords);
        }
      }
    }, coords, null);
  }

  /**
   * Add stone to existing chain or creates new chain if not connected to any like-colored chains
   * Merges chains as necessary if new stone connects like-colored chains
   * @param   stone to potentially add to the board
   * @return  chain to which stone has been added
   */
  private Chain mergeToChains(Stone stone) {
    // TODO
    return new Chain();
  }

  /** Removes and returns last move from board */
  Stone popStone() {
    Stone stone =  moves.pop();
    filled.remove(stone.coords);
    // TODO: update chains
    return stone;
  }

  /** Reset to empty board */
  void reset() {
    chains.clear();
    moves.clear();
    filled.clear();
  }

  public String toJson() {
    return moves.toJson();
  }

  /** Class enabling checking of neighboring coordinates based on some criterion */
  private class NeighborChecker<T> {
    /** Returns an ArrayList of the neighbors that matched the criterion */
    private ArrayList<T> getMatchingNeighbors(CheckCoords<T> checker, BoxCoords coords, Object criterion) {
      ArrayList<T> neighbors = new ArrayList<>();
      checker.check(neighbors, new BoxCoords(coords.x - 1, coords.y), criterion);
      checker.check(neighbors, new BoxCoords(coords.x + 1, coords.y), criterion);
      checker.check(neighbors, new BoxCoords(coords.x, coords.y - 1), criterion);
      checker.check(neighbors, new BoxCoords(coords.x, coords.y + 1), criterion);
      return neighbors;
    }
  }

  /** Interface to be used as method argument to NeighborChecker class */
  private interface CheckCoords<T> {
    void check(ArrayList<T> found, BoxCoords coords, Object criterion);
  }
}

