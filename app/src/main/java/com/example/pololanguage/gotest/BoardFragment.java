package com.example.pololanguage.gotest;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.HashMap;

public class BoardFragment extends Fragment {
  private int boardSize;
  private int handicap;
  private int boardWidth;
  private int stoneWidth;
  private int marginTop;
  private int marginLeft;
  private int boardImage;
  private int[] xCoords;
  private int[] yCoords;
  private ArrayList<Coordinate> moveOrder;
  private HashMap<Coordinate, Stone> stoneMap;
  private Stone cursor;
  private RelativeLayout container;

  private static final float NINE_MARGINMODIFIER = (float)10/615;
  private static final float NINE_STONEMODIFIER = (float)65/615;
  private static final float THIRTEEN_MARGINMODIFIER = (float)10/615;
  private static final float THIRTEEN_STONEMODIFIER = (float)44/615;
  private static final float NINETEEN_MARGINMODIFIER = (float)20/615;
  private static final float NINETEEN_STONEMODIFIER = (float)29/615;
  private static final int PADDING = 2; // should be relative as well

  protected static BoardFragment newInstance(int boardSize, int handicap) {
    BoardFragment frag = new BoardFragment();
    //frag.setRetainInstance(true); // need to move to onCreate() ?
    frag.boardSize = boardSize;
    frag.handicap = handicap;
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    DisplayMetrics displayMetrics = getActivity().getResources()
            .getDisplayMetrics();
    boardWidth = Math.min(displayMetrics.widthPixels,
            displayMetrics.heightPixels);
    switch (boardSize) {
      case 9:
        stoneWidth = Math.round(boardWidth * NINE_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * NINE_MARGINMODIFIER);
        marginTop = marginLeft;
        boardImage = R.drawable.board_9x9;
        break;
      case 13:
        stoneWidth = Math.round(boardWidth * THIRTEEN_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * THIRTEEN_MARGINMODIFIER);
        marginTop = marginLeft;
        boardImage = R.drawable.board_13x13;
        break;
      case 19:
        stoneWidth = Math.round(boardWidth * NINETEEN_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * NINETEEN_MARGINMODIFIER);
        marginTop = marginLeft - 5;
        boardImage = R.drawable.board_19x19;
        break;
      default:
        Log.e("Board#onCreateView", "Incorrect board size (not 9, 13, or 19).");
    }
    setCoordinateArrays();
  }

  private void setCoordinateArrays() {
    xCoords = new int[boardSize];
    yCoords = new int[boardSize];
    for(int i = 0; i < boardSize; ++i) {
      xCoords[i] = marginLeft + i*(PADDING + stoneWidth);
      yCoords[i] = marginTop + i*(PADDING + stoneWidth);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.board, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    View boardContainer = getActivity().findViewById(R.id.board_container);
    RelativeLayout.LayoutParams layoutParams =
            new RelativeLayout.LayoutParams(boardWidth, boardWidth);
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    boardContainer.setLayoutParams(layoutParams);
    boardContainer.setBackgroundResource(boardImage);
    container = (RelativeLayout)getActivity().findViewById(R.id.board_rel_layout);

    for(int i = 0; i < boardSize; ++i) {
      for(int j = 0; j < boardSize; ++j) {
        StoneColor color = ((boardSize*i + j) % 2 == 0)? StoneColor.BLACK : StoneColor.WHITE;
        // TEST: fill board with stones
        new Stone(xCoords[i], yCoords[i], color);
      }
    }

    cursor = new Stone(200, 100, StoneColor.BLACK);
    cursor.setAlpha(0.7f);
    CursorListener cursorListener = new CursorListener();
    cursor.setOnTouchListener(cursorListener);
    cursor.setOnLongClickListener(cursorListener);
  }

  enum StoneColor {
    BLACK (R.drawable.stone_black_55x55),
    WHITE (R.drawable.stone_white_55x55);

    int resource;
    StoneColor(int resource) { this.resource = resource; }
    int getResource() { return resource; }
    StoneColor getOther() {
      if (this == BLACK) return WHITE;
      else return BLACK;
    }
  }

  private class Coordinate {
    int x;
    int y;
  }

  private class Stone extends View {
    int x;
    int y;
    StoneColor color;

    Stone(int x, int y, StoneColor color) {
      super(BoardFragment.this.getActivity());
      RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
      this.x = x;
      this.y = y;
      this.color = color;
      layout.leftMargin = x;
      layout.topMargin = y;
      setLayoutParams(layout);
      setBackgroundResource(color.getResource());
      container.addView(this);
    }

    void toggleColor() {
      color = color.getOther();
      setBackgroundResource(color.getResource());
    }
  }

  private class CursorListener implements View.OnTouchListener, View.OnLongClickListener {
    private int xDelta;
    private int yDelta;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
      final int X = (int) event.getRawX();
      final int Y = (int) event.getRawY();
      switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
          RelativeLayout.LayoutParams lParams =
                  (RelativeLayout.LayoutParams) view.getLayoutParams();
          xDelta = X - lParams.leftMargin;
          yDelta = Y - lParams.topMargin;
          break;
        case MotionEvent.ACTION_MOVE:
          RelativeLayout.LayoutParams layoutParams =
                  (RelativeLayout.LayoutParams) view.getLayoutParams();
          layoutParams.leftMargin = X - xDelta;
          layoutParams.topMargin = Y - yDelta;
          layoutParams.rightMargin = -250;
          layoutParams.bottomMargin = -250;
          view.setLayoutParams(layoutParams);
          break;
      }
      return false;
    }

    @Override
    public boolean onLongClick(View view) {
      ((Stone) view).toggleColor();
      return true;
    }
  }
}
