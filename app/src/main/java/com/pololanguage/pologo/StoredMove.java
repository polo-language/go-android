package com.pololanguage.pologo;

public class StoredMove {
  // x and y hold game position coordinates (i.e. 1-9, 1-13, or 1-19, not pixel offsets)
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
