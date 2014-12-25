package com.pololanguage.pologo;

enum StoneColor {
  BLACK (R.drawable.stone_black),
  WHITE (R.drawable.stone_white);

  int resource;

  StoneColor(int resource) { this.resource = resource; }

  int getResource() { return resource; }

  StoneColor getOther() {
    if (this == BLACK) return WHITE;
    else return BLACK;
  }
}
