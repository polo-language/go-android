package com.pololanguage.pologo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BoardView extends View {
  public static final float BOARD_LINE_WIDTH = 10.0f;

  Paint paint = new Paint();

  // to be called with 'this' from an Activity (Activity extends context)
  // TODO: or from Fragment with getActivity()

  protected BoardView newInstance(Context context, int[] xCoords, int[] yCoords) {
    BoardView boardView = new BoardView(context);
    int boardSize = xCoords.length;

    // TODO:
    // don't actually use loop and save to separate variables
    //  just use the coords as outlined below

    /*
    for (int i = 0; i < boardSize; ++i) {
      vertStarts[i] = xCoords[i], yCoords[0];
      vertEnds = xCoords[i], yCoords[boardSize];
      horizStarts = xCoords[0], yCoords[i]
      horizEnds = xCoords[boardSize], yCoords[i]
    }
    */

    return boardView;
  }

  public BoardView(Context context) { // for programmatic instantiation
    super(context);
    paint.setColor(Color.BLACK);
  }

  // for XML instantiation
  public BoardView (Context context, AttributeSet attrs) {
    super(context, attrs);
    paint.setColor(Color.BLACK);
    paint.setStrokeWidth(BOARD_LINE_WIDTH);
  }

  @Override
  public void onDraw(Canvas canvas) {
    canvas.drawLine(110, 110, 20, 80, paint);
    canvas.drawLine(20, 0, 0, 20, paint);
  }
}
