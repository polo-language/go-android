package com.pololanguage.pologo;

/**
 * Saves relevant information for a given move
 * <p>
 *   index stores move number
 *   x and y hold game position coordinates (i.e. 1-9, 1-13, or 1-19, not pixel offsets)
 * </p>
 */
public class StoredMove {
  int index;
  int x;
  int y;
  StoneColor color;

  StoredMove(int index, int x, int y, StoneColor color) {
    this.index = index;
    this.x = x;
    this.y = y;
    this.color = color;
  }
}
