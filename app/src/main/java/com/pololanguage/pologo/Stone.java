package com.pololanguage.pologo;


import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

/** Holds the coordinates and color of individual stones */
public class Stone extends View {
  BoxCoords coords;
  StoneColor color;

  Stone(Context context, BoxCoords coords, StoneColor color) {
    super(context);
    this.coords = coords;
    this.color = color;
    setBackgroundResource(color.getResource());
  }

  void move(int x, int y) {
    RelativeLayout.LayoutParams layoutParams =
        (RelativeLayout.LayoutParams) getLayoutParams();
    layoutParams.leftMargin = x;
    layoutParams.topMargin = y;
    layoutParams.rightMargin = -250;
    layoutParams.bottomMargin = -250;
    setLayoutParams(layoutParams);
  }
}
