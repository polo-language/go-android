package com.pololanguage.pologo;

public class BoxCoords {
  // Holds zero-indexed game position coordinates (i.e. 0-8, 0-12, or 0-18, not pixel offsets)
  // Coordinates of -1 indicates off-board
  int x, y;

  BoxCoords(int x, int y) {
    if (x < -1 || 19 < x || y < -1 || 19 < y) {
      throw new IndexOutOfBoundsException("Invalid BoxCoords parameters.");
    }
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object other) {
    return !(other == null || this.getClass() != other.getClass())
        && (x == ((BoxCoords)other).x && y == ((BoxCoords)other).y);
  }

  @Override
  public int hashCode() { return ((Integer) (x + 19*y)).hashCode(); }
}
