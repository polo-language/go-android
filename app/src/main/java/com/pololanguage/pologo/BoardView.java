package com.pololanguage.pologo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * RelativeLayout managing the board's visual state
 */
public class BoardView extends RelativeLayout {
  float lineWidth;
  private int[] xCoords;
  private int[] yCoords;
  Paint paint = new Paint();
  private int stoneWidth;
  private int margin;

  protected static BoardView newInstance(Context context, int boardSize,
                                         int boardWidth, int stoneWidth) {
    BoardView boardView = new BoardView(context);
    boardView.margin = Math.round((boardWidth - boardSize*stoneWidth) / 2);
    boardView.stoneWidth = stoneWidth;

    boardView.setId(R.id.board);
    boardView.buildCoordinateArrays(boardSize, stoneWidth);
    switch (boardSize) {
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
   * Create arrays containing top left corner coordinates of stone positions
   * @param boardSize   Width of board in stones: 9, 13, 19
   * @param stoneWidth  pixel width of a stone
   */
  private void buildCoordinateArrays(int boardSize, int stoneWidth) {
    xCoords = new int[boardSize];
    yCoords = new int[boardSize];
    for(int i = 0; i < boardSize; ++i) {
      xCoords[i] = margin + i*stoneWidth;
      yCoords[i] = margin + i*stoneWidth;
    }
  }

  /**
   * Draws grid lines on the board. Lines are shifted half a stone's width to center under stones.
   */
  @Override
  public void onDraw(Canvas canvas) {
    int stoneHalfWidth = stoneWidth / 2;
    for (int i = 0; i < xCoords.length; ++i) {
      canvas.drawLine(xCoords[i] + stoneHalfWidth, yCoords[0] + stoneHalfWidth,
                      xCoords[i] + stoneHalfWidth, yCoords[xCoords.length - 1] + stoneHalfWidth,
                      paint);
      canvas.drawLine(xCoords[0] + stoneHalfWidth, yCoords[i] + stoneHalfWidth,
                      xCoords[xCoords.length - 1] + stoneHalfWidth, yCoords[i] + stoneHalfWidth,
                      paint);
    }
  }

  /** Add the supplied stone to the view off-screen */
  void renderStone(Stone stone) {
    RelativeLayout.LayoutParams layout =
        new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);

    stone.setLayoutParams(layout);

    if (stone.coords.x < 0 || stone.coords.y < 0) {
      stone.move(-200, -200); // place stone off-board
    } else {
      stone.move(xCoords[stone.coords.x], yCoords[stone.coords.y]);
    }

    addView(stone);
  }

  void renderBox(Box box) {
    addView(box);
  }

  void setViewMargins(View view, BoxCoords coords) {
    RelativeLayout.LayoutParams boxParams =
        (RelativeLayout.LayoutParams) view.getLayoutParams();
    boxParams.leftMargin = xCoords[coords.x];
    boxParams.topMargin = yCoords[coords.y];
    view.setLayoutParams(boxParams);
  }

  /** Returns the board position closest to the supplied pixel coordinates */
  BoxCoords getNearestGridCoords(int x, int y) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int argminX = 0;
    int argminY = 0;
    for (int i = 0; i < xCoords.length; ++i) {
      int diffX = Math.abs(xCoords[i] - x);
      int diffY = Math.abs(yCoords[i] - y);
      if (diffX < minX) {
        minX = diffX;
        argminX = i;
      }
      if (diffY < minY) {
        minY = diffY;
        argminY = i;
      }
    }
    return new BoxCoords(argminX, argminY);
  }
}