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
  private Paint paint = new Paint();
  private int stoneWidth;
  private int margin;

  /** Stones scaling factors by board size */
  private static final float NINE_STONE_MODIFIER = (float)67/615;
  private static final float THIRTEEN_STONE_MODIFIER = (float)46/615;
  private static final float NINETEEN_STONE_MODIFIER = (float)31/615;

  protected static BoardView newInstance(Context context, int boardSize, int boardWidth) {
    BoardView boardView = new BoardView(context);
    boardView.setId(R.id.board);

    switch (boardSize) {
      case 9:
        boardView.lineWidth = 5.0f;
        boardView.stoneWidth = Math.round(boardWidth * NINE_STONE_MODIFIER);
        break;
      case 13:
        boardView.lineWidth = 4.0f;
        boardView.stoneWidth = Math.round(boardWidth * THIRTEEN_STONE_MODIFIER);
        break;
      case 19:
        boardView.lineWidth = 3.0f;
        boardView.stoneWidth = Math.round(boardWidth * NINETEEN_STONE_MODIFIER);
        break;
      default:
        throw new IllegalArgumentException("Incorrect board size (not 9, 13, or 19).");
    }

    boardView.margin = Math.round((boardWidth - boardSize*boardView.stoneWidth) / 2);
    boardView.buildCoordinateArrays(boardSize, boardView.stoneWidth);

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

  void renderBox(View box) {
    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    params.leftMargin = -200;
    params.topMargin = -200;
    box.setLayoutParams(params);

    addView(box);
  }

  /** Pushes supplied view out to the given coordinates */
  void setViewMargins(View view, BoxCoords coords) {
    RelativeLayout.LayoutParams boxParams =
        (RelativeLayout.LayoutParams) view.getLayoutParams();
    boxParams.leftMargin = xCoords[coords.x];
    boxParams.topMargin = yCoords[coords.y];
    view.setLayoutParams(boxParams);
  }

  /** Shifts top left of supplied stone's coordinates by half a stone width from x and y */
  void centerStoneOnClick(Stone stone, int x, int y) {
    stone.move(x - stoneWidth / 2, y - stoneWidth / 2);
  }

  /**
   * Returns the board position closest to the supplied pixel coordinates
   * Does so by finding index of largest grid coordinate smaller than the supplied click for x and y
   * */
  BoxCoords getNearestGridCoords(int x, int y) {
    int argminX = 0; // default
    int argminY = 0; // default
    for (int i = xCoords.length - 1; i > 0; --i) {
      if (x > xCoords[i]) {
        argminX = i;
        break;
      }
    }
    for (int i = yCoords.length - 1; i > 0; --i) {
      if (y > yCoords[i]) {
        argminY = i;
        break;
      }
    }
    return new BoxCoords(argminX, argminY);
  }
}