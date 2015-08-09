package com.pololanguage.pologo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * RelativeLayout managing the board's visual state
 */
public class BoardView extends RelativeLayout {
  float lineWidth;
  int[] xCoords;
  int[] yCoords;
  Paint paint = new Paint();
  private int stoneHalfWidth;

  protected static BoardView newInstance(Context context, int[] xCoords, int[] yCoords) {
    BoardView boardView = new BoardView(context);
    boardView.stoneHalfWidth = (xCoords[1] - xCoords[0]) / 2;

    boardView.setId(R.id.board);
    boardView.xCoords = new int[xCoords.length];
    boardView.yCoords = new int[xCoords.length];
    for (int i = 0; i < boardView.xCoords.length; ++i) {
      boardView.xCoords[i] = xCoords[i];
      boardView.yCoords[i] = yCoords[i];
    }
    switch (xCoords.length) {
      case 9:
        boardView.lineWidth = 5.0f;
        break;
      case 13:
        boardView.lineWidth = 4.0f;
        break;
      case 19:
        boardView.lineWidth = 3.0f;
        break;
      default:
        boardView.lineWidth = 4.0f;
        break;
    }

    boardView.paint.setColor(Color.BLACK);
    boardView.paint.setStrokeWidth(boardView.lineWidth);

    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                                                                     LayoutParams.MATCH_PARENT);
    boardView.setLayoutParams(layoutParams);
    boardView.setBackgroundResource(R.drawable.board_01);

    return boardView;
  }

  public BoardView(Context context) {
    super(context);
  }

  /**
   * Draws grid lines on the board. Lines are shifted half a stone's width to center under stones.
   * @param canvas
   */
  @Override
  public void onDraw(Canvas canvas) {
    for (int i = 0; i < xCoords.length; ++i) {
      canvas.drawLine(xCoords[i] + stoneHalfWidth, yCoords[0] + stoneHalfWidth,
                      xCoords[i] + stoneHalfWidth, yCoords[xCoords.length - 1] + stoneHalfWidth, paint);
      canvas.drawLine(xCoords[0] + stoneHalfWidth, yCoords[i] + stoneHalfWidth,
                      xCoords[xCoords.length - 1] + stoneHalfWidth, yCoords[i] + stoneHalfWidth, paint);
    }
  }
}