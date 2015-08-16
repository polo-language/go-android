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
  /** Holds all chains on the board */
  private Set<Chain> chains;

  /** Track filled coordinates and map them to the chain covering that location */
  private Map<BoxCoords, Chain> filled;

  /** Track moves in order */
  private Moves moves;

  /** Need to know board size to track liberties at edge of board */
  private int boardSize;

  /** Track captured stones */
  private int whitesCaptures;
  private int blacksCaptures;


  ChainManager(int size) {
    chains = new HashSet<>();
    filled = new HashMap<>();
    moves = new Moves();
    boardSize = size;
    whitesCaptures = 0;
    blacksCaptures = 0;
  }

  /** Returns true if the supplied coordinates are occupied by a stone */
  public boolean taken(BoxCoords coords) {
    return filled.containsKey(coords);
  }

  /** Returns true if board contains no stones */
  public boolean hasNoMoves() {
    return filled.isEmpty();
  }

  /**
   * Add stone to board, killing opposing groups as necessary
   * @param stone     the stone to add
   * @param captured  fills this set with the stones captured
   * @return          false if move would be suicide
   */
  public boolean addStone(Stone stone, Set<Stone> captured) {
    capture(stone, captured);
    if (!isSuicide(stone)) {
      incorporateIntoChains(stone);
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
        if (taken(coords) && filled.get(coords).color == criterion) {
          found.add(filled.get(coords));
        }
      }
    }, coords, color);
  }

  /** Checks for and removes opposing chains with this stone as their last liberty
   * @param stone     the stone doing the capturing
   * @param captured  fills this set with the stones captured
   */
  private void capture(Stone stone, Set<Stone> captured) {
    ArrayList<Chain> opposingChains = getNeighborChains(stone.coords, stone.color.getOther());
    for (Chain chain : opposingChains) {
      if (chain.isLastLiberty(stone.coords)) {
        captureChain(chain, captured);
      }
    }
  }

  /**
   * Removes stones in chain from board and increments captureChain count of opposing player
   * @param chain     the chain of stones to captureChain
   * @param captured  fills this set with the stones captured
   */
  private void captureChain(Chain chain, Set<Stone> captured) {
    Set<Stone> stones = chain.getStones();
    if (chain.color == StoneColor.BLACK) {
      whitesCaptures += chain.size();
    } else {
      blacksCaptures += chain.size();
    }
    captured.addAll(stones);
    moves.removeAll(stones);
    for (Stone stone : stones) {
      filled.remove(stone.coords);
      addToOpposingLiberties(stone);
    }
    chains.remove(chain);
  }

  /** Returns true if stone can be added; false if move would be suicide */
  private boolean isSuicide(Stone stone) {
    for (Chain chain : getNeighborChains(stone.coords, stone.color)) {
      if (!chain.isLastLiberty(stone.coords)) {
        return false;
      }
    }
    return getLiberties(stone.coords).size() == 0;
  }

  /** Returns an ArrayList containing coordinates of any liberties of the given position */
  private Set<BoxCoords> getLiberties(BoxCoords coords) {
    NeighborChecker<BoxCoords> neighborChecker = new NeighborChecker<>();

    return new HashSet(neighborChecker.getMatchingNeighbors(new CheckCoords<BoxCoords>() {
      public void check(ArrayList<BoxCoords> neighbors, BoxCoords coords, Object dummy) {
        if (!taken(coords)) {
          neighbors.add(coords);
        }
      }
    }, coords, null));
  }

  /**
   * Add stone to existing chain or creates new chain if not connected to any like-colored chains
   * Merges chains as necessary if new stone connects like-colored chains
   */
  private void incorporateIntoChains(Stone stone) {
    ArrayList<Chain> friends = getNeighborChains(stone.coords, stone.color);
    Chain merged;
    if (friends.size() == 0) {
      merged = new Chain(stone.color);
      chains.add(merged);
    } else {
      Chain friend;
      merged = friends.get(0);
      for (int i = 1; i < friends.size(); ++i) { // start with the second chain
        friend = friends.get(i);
        merged.merge(friend);
        updateFilled(merged, friend.getStones());
        chains.remove(friend);
      }
    }
    addToChain(merged, stone);
  }

  /** Adds the stone to the chain */
  private void addToChain(Chain chain, Stone stone) {
    chain.add(stone, getLiberties(stone.coords));
    removeFromOpposingLiberties(stone);
    filled.put(stone.coords, chain);
  }

  /** Sets chain as the value for all stones in filled */
  private void updateFilled(Chain chain, Set<Stone> stones) {
    for (Stone stone : stones) {
      filled.put(stone.coords, chain);
    }
  }

  /** Removes and returns last move from board */
  public Stone popStone() {
    Stone stone =  moves.pop();
    Chain chain = filled.remove(stone.coords);
    if (chain == null) {
      throw new IllegalStateException("Popped stone keyed to null chain");
    }
    addToOpposingLiberties(stone);

    // remove and rebuild the current chain (without modifying moves) since it could split, etc.
    chains.remove(chain);
    for (Stone s : chain.getStones()) {
      if (s != stone) {
        incorporateIntoChains(s);
      }
    }
    return stone;
  }

  private void addToOpposingLiberties(Stone stone) {
    for (Chain chain : getNeighborChains(stone.coords, stone.color.getOther())) {
      chain.addLiberty(stone.coords);
    }
  }

  private void removeFromOpposingLiberties(Stone stone) {
    for (Chain chain : getNeighborChains(stone.coords, stone.color.getOther())) {
      chain.removeLiberty(stone.coords);
    }
  }

  /** Reset to empty board */
  public void reset() {
    chains.clear();
    moves.clear();
    filled.clear();
  }

  public String toJson() {
    return moves.toJson();
  }

  ////////////////////////////////////////////////////////
  /** Class enabling checking of neighboring coordinates based on some criterion */
  private class NeighborChecker<T> {
    /** Returns an ArrayList of the neighbors that matched the criterion */
    private ArrayList<T> getMatchingNeighbors(CheckCoords<T> checker, BoxCoords coords, Object criterion) {
      ArrayList<T> neighbors = new ArrayList<>();
      if (coords.x - 1 > -1) {
        checker.check(neighbors, new BoxCoords(coords.x - 1, coords.y), criterion);
      }
      if (coords.x + 1 < boardSize) {
        checker.check(neighbors, new BoxCoords(coords.x + 1, coords.y), criterion);
      }
      if (coords.y - 1 > -1) {
        checker.check(neighbors, new BoxCoords(coords.x, coords.y - 1), criterion);
      }
      if (coords.y + 1 < boardSize) {
        checker.check(neighbors, new BoxCoords(coords.x, coords.y + 1), criterion);
      }
      return neighbors;
    }
  }

  /** Interface to be used as method argument to NeighborChecker class */
  private interface CheckCoords<T> {
    void check(ArrayList<T> found, BoxCoords coords, Object criterion);
  }
}

