package com.pololanguage.ninedragongo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

/**
 * Manages all game-play (i.e. non-visual) aspects of stone handling
 * Manages creation, updating, and removal of chains of like-colored stones
 */
public class MoveManager {
  /** Holds all chains on the board */
  private Set<Chain> chains;

  /** Track filled coordinates and map them to the chain covering that location */
  private Map<BoxCoords, Chain> filled;

  /** Track moves in order */
  private History<Move> history;

  /** Need to know board size to track liberties at edge of board */
  private int boardSize;

  /** Track captured stones */
  private int whitesCaptures;
  private int blacksCaptures;


  MoveManager(int size) {
    chains = new HashSet<>();
    filled = new HashMap<>();
    history = new History<>();
    boardSize = size;
    whitesCaptures = 0;
    blacksCaptures = 0;
  }

  Set<Chain> getChains() {
    return chains;
  }

  History<Move> getHistory() {
    return history;
  }

  /** Returns true if the supplied coordinates are occupied by a stone */
  public boolean taken(BoxCoords coords) {
    return filled.containsKey(coords);
  }

//  /** Returns true if there is history after the present */
//  public boolean hasFuture() {
//    return history.hasFuture();
//  }
//
//  /** Returns true if there is history before the present */
//  public boolean hasPast() {
//    return history.hasPast();
//  }

  /** Removes and returns last move from board */
  public Move undo() {
    Move move = history.stepBack();
    if (move == null) {
      return null;
    }
    Stone stone = move.getStone();
    Set<Stone> captured = move.getCaptured();
    Chain chain = filled.get(stone.coords);
    addToOpposingLiberties(stone);

    /* Remove and rebuild the current chain since it could have split, etc. */
    if (chain == null) {
      throw new IllegalStateException("Popped stone keyed to null chain");
    }
    chains.remove(chain);
    Set<Stone> stones = chain.getStones();
    for (Stone s : stones) {
      filled.remove(s.coords);
    }
    for (Stone s : stones) {
      if (s != stone) {
          incorporateIntoChains(s);
      }
    }
    /* Return captured stones to board */
    for (Stone s : captured) {
      incorporateIntoChains(s);
    }

    updateCaptureCount(stone.color, captured.size(), false);
    return move;
  }

  public Move redo() {
    Move move = history.stepForward();
    if (move != null) {
      addStoneWithHistory(move.getStone(), move.getCaptured(), false);
    }
    return move;
  }

  /**
   * Add stone to board, killing opposing groups as necessary
   * @param stone     the stone to add
   * @param captured  fills this set with the stones captured
   * @return          false if move would be suicide
   */
  public boolean addStone(Stone stone, Set<Stone> captured) {
    return addStoneWithHistory(stone, captured, true);
  }

  private boolean addStoneWithHistory(Stone stone, Set<Stone> captured, boolean modifyHistory) {
    capture(stone, captured);
    if (!isSuicide(stone)) {
      incorporateIntoChains(stone);
      if (modifyHistory) {
        history.add(new Move(stone, captured));
      }
      return true;
    } else {
      return false;
    }
  }

  /** Returns up to 4 chains neighboring the supplied coordinates and of the given color */
  private Set<Chain> getNeighborChains(BoxCoords coords, StoneColor color) {
    return new NeighborChecker<Chain>().getMatchingNeighbors(new CheckCoords<Chain>() {
      public void check(Set<Chain> found, BoxCoords coords, Object criterion) {
        if (taken(coords) && filled.get(coords).getColor() == criterion) {
          found.add(filled.get(coords));
        }
      }
    }, coords, color);
  }

  /**
   * Checks for and removes opposing chains with this stone as their last liberty
   * @param stone     the stone doing the capturing
   * @param captured  fills this set with the stones captured
   */
  private void capture(Stone stone, Set<Stone> captured) {
    Set<Chain> opposingChains = getNeighborChains(stone.coords, stone.color.getOther());
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
    updateCaptureCount(chain.getColor(), chain.size(), true);
    captured.addAll(stones);
    for (Stone stone : stones) {
      filled.remove(stone.coords);
      addToOpposingLiberties(stone);
    }
    chains.remove(chain);
  }

  /** Increments/decrements count of opposing colors captures */
  private void updateCaptureCount(StoneColor color, int count, boolean increment) {
    if (color == StoneColor.BLACK) {
      whitesCaptures += increment ? count : count * -1;
    } else {
      blacksCaptures += increment ? count : count * -1;
    }
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
      public void check(Set<BoxCoords> neighbors, BoxCoords coords, Object dummy) {
        if (!taken(coords)) {
          neighbors.add(coords);
        }
      }
    }, coords, null));
  }

  /**
   * Adds stone to existing chain or creates new chain if not connected to any like-colored chains
   * Merges chains as necessary if new stone connects two or more like-colored chains
   */
  private void incorporateIntoChains(Stone stone) {
    Set<Chain> friends = getNeighborChains(stone.coords, stone.color);
    Chain merged;
    Iterator<Chain> iterator = friends.iterator();
    if (!iterator.hasNext()) {
      merged = new Chain(stone.color);
      chains.add(merged);
    } else {
      Chain friend;
      merged = iterator.next();
      while (iterator.hasNext()) {
        friend = iterator.next();
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
    history.clear();
    filled.clear();
  }

  /* Inner classes */
  /** Class enabling checking of neighboring coordinates based on some criterion */
  private class NeighborChecker<T> {
    /** Returns an Set of the neighbors that matched the criterion */
    private Set<T> getMatchingNeighbors(CheckCoords<T> checker, BoxCoords coords, Object criterion) {
      Set<T> neighbors = new HashSet<>();
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
    void check(Set<T> found, BoxCoords coords, Object criterion);
  }
}

