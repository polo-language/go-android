package com.pololanguage.pologo;

/**
 * Holds zero-indexed game position coordinates
 * <p>
 *   (i.e. 0-8, 0-12, or 0-18, not pixel offsets)
 *   Coordinate of -1 used to indicate stone is off of the board
 * </p>
 */
public class BoxCoords {
  public int x;
  public int y;

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
