package com.pololanguage.pologo;

/**
 * Stone color class; includes reference to drawable resource
 */
enum StoneColor {
  BLACK (R.drawable.stone_black),
  WHITE (R.drawable.stone_white);

  int resource;

  StoneColor(int resource) { this.resource = resource; }

  int getResource() { return resource; }

  StoneColor getOther() {
    return (this == BLACK) ? WHITE : BLACK;
  }
}
