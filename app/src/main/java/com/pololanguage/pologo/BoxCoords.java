package com.pololanguage.pologo;

public class BoxCoords {
  // Holds game position coordinates (i.e. 1-9, 1-13, or 1-19, not pixel offsets)
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
