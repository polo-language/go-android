package com.pololanguage.pologo;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Manages the coordinates where the current stone would be if placed on the board
 */
public class Box extends View {
  BoxCoords coords;

  Box(Context context, int stoneWidth) {
    super(context);
    coords = new BoxCoords(-1, -1);
    RelativeLayout.LayoutParams boxParams =
        new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    boxParams.leftMargin = -200;
    boxParams.topMargin = -200;
    setLayoutParams(boxParams);
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
