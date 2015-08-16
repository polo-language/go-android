package com.pololanguage.pologo;

import java.util.ArrayList;
import java.util.Iterator;

/** Tracks a sequence of events */
public class History<E> extends ArrayList<E> {
  /** Index of the latest event in the past */
  private int head;

  /** Index of the event farthest in the future */
  private int max;

  History() {
    head = max = -1;
  }

  @Override
  public boolean add(E element) {
    ++head;

    if (size() < head + 1) {
      super.add(element);
    } else {
      set(head, element);
    }

    clearFuture();
    return true;
  }

  /** Reset the head and max pointers to zero to wipe out all history */
  @Override
  public void clear() {
    head = max = -1;
  }

  /** Returns an iterator over the full (valid) history up to and including max */
  @Override
  public Iterator<E> iterator() {
    return subList(0, max + 1).iterator();
  }

  /** Returns head to enable persistence operations */
  public int getHead() {
    return head;
  }

  /** Returns the event under head (or null if at the beginning of time) and decrements head */
  public E stepBack() {
    E latest = readLatest();
    if (latest != null) {
      --head;
    }
    return latest;
  }

  /** Returns the event under head (or null if at the beginning of time) */
  public E readLatest() {
    if (head < 0) {
      return null;
    }
    return get(head);
  }

  /** Returns the next element in the future (or null if at the end of time) and increments head */
  public E stepForward() {
    if (head == max) {
      return null;
    }
    ++head;
    return get(head);
  }

  /** Clears previously undone events in the now future */
  public void clearFuture() {
    max = head;
  }

  /** Returns true if there is history before the present */
  public boolean hasPast() {
    return head > -1;
  }

  /** Returns true if there is history after the present */
  public boolean hasFuture() {
    return head < max;
  }
}
