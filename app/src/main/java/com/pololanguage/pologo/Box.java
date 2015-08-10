package com.pololanguage.pologo;


import android.content.Context;
import android.view.View;


/**
 * Manages the coordinates and view showing where the next stone would be if placed on the board
 */
public class Box extends View {
  BoxCoords coords;

  Box(Context context) {
    super(context);
    coords = new BoxCoords(-1, -1);
    setBackgroundResource(R.drawable.box);
  }

  BoxCoords getCoords() {
    return coords;
  }

  void setCoords(BoxCoords boxCoords) {
    coords.x = boxCoords.x;
    coords.y = boxCoords.y;
  }


}
