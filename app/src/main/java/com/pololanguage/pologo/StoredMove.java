package com.pololanguage.pologo;

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
