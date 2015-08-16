package com.pololanguage.pologo;

import java.util.ArrayList;

/** Tracks the game's move history */
public class History<T> extends ArrayList<T> {
  /** Index of the current move */
  private int head;

  /** Index of the move farthest in the future */
  private int max;

  History() {
    head = 0;
    max = 0;
  }

  /** Returns the last move taken (or null if no previous move) and decrements head */
  public T getPrevious() {
    if (head == 0) {
      return null;
    }
    --head;
    return get(head);
  }

  /** Returns the next move in the future (or null if no future move) and increments head */
  public T getNext() {
    if (head == max) {
      return null;
    }
    ++head;
    return get(head);
  }

  /** Clears previously undone moves (for when a new move has been taken from an undone state) */
  public void clearFuture() {
    max = head;
  }
}
