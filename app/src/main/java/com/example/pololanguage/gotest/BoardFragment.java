package com.example.pololanguage.gotest;

import android.app.Fragment;
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

public class BoardFragment extends Fragment implements View.OnTouchListener {
  private int boardSize;
  private int handicap;
  private int boardWidth;
  private int stoneWidth;
  private int marginTop;
  private int marginLeft;
  private int boardImage;
  private int[] xCoords;
  private int[] yCoords;
  private boolean firstTouch = true;
  private StoneColor currentColor = StoneColor.BLACK;
  private ArrayList<BoxCoords> moves = new ArrayList<BoxCoords>();
  private HashMap<BoxCoords, Stone> stoneMap = new HashMap<BoxCoords, Stone>();
  private RelativeLayout container;
  private Stone cursor;
  private Box box;

  private static final float NINE_MARGINMODIFIER = (float)10/615;
  private static final float NINE_STONEMODIFIER = (float)65/615;
  private static final float THIRTEEN_MARGINMODIFIER = (float)10/615;
  private static final float THIRTEEN_STONEMODIFIER = (float)44/615;
  private static final float NINETEEN_MARGINMODIFIER = (float)20/615;
  private static final float NINETEEN_STONEMODIFIER = (float)29/615;
  // TODO: use padding as view attribute instead
  private static final int PADDING = 2;

  protected static BoardFragment newInstance(int boardSize, int handicap) {
    BoardFragment frag = new BoardFragment();
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
    container.setOnTouchListener(this);

    for(int i = 0; i < boardSize; ++i) {
      for(int j = 0; j < boardSize; ++j) {
        StoneColor color = ((boardSize*i + j) % 2 == 0)?
                StoneColor.BLACK : StoneColor.WHITE;
        // TEST: fill board with stones
        new Stone(xCoords[i], yCoords[i], color);
      }
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    final int X = (int) event.getX();
    final int Y = (int) event.getY();
    int newX, newY;

    if (firstTouch) { addCursor(X, Y); }

    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN: // intentional fall-through
      case MotionEvent.ACTION_MOVE:
        newX = X - stoneWidth/2;
        newY = Y - stoneWidth/2;
        cursor.move(newX, newY);
        box.snapToGrid(newX, newY);
        break;
    }
    return true;
  }

  private void addCursor(int x, int y) {
    cursor = new Stone(-200, -200, currentColor);
    cursor.setAlpha(0.7f);

    box = new Box(-200, -200);
    firstTouch = false;
  }

  private BoxCoords getNearestGridCoords(int x, int y) {
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

  // INNER CLASSES:

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

  private class BoxCoords {
    int x, y;
    BoxCoords(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }

  private class Stone extends View {
    int x, y;
    StoneColor color;

    Stone(int x, int y, StoneColor color) {
      super(BoardFragment.this.getActivity());
      RelativeLayout.LayoutParams layout =
              new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
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

    private void move(int x, int y) {
      RelativeLayout.LayoutParams layoutParams =
              (RelativeLayout.LayoutParams) getLayoutParams();
      layoutParams.leftMargin = x;
      layoutParams.topMargin = y;
      layoutParams.rightMargin = -250;
      layoutParams.bottomMargin = -250;
      setLayoutParams(layoutParams);
    }
  }

  private class Box extends View {
    BoxCoords coords;

    Box(int x, int y) {
      super(BoardFragment.this.getActivity());
      coords = new BoxCoords(x, y);
      RelativeLayout.LayoutParams boxParams =
              new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
      boxParams.leftMargin = x;
      boxParams.topMargin = y;
      setLayoutParams(boxParams);
      setBackgroundResource(R.drawable.box);
      setOnClickListener(new CursorListener());
      container.addView(this);
    }

    BoxCoords getCoords() {
      return coords;
    }

    private void setCoords(int x, int y) {
      coords.x = x;
      coords.y = y;
      RelativeLayout.LayoutParams boxParams =
              (RelativeLayout.LayoutParams) getLayoutParams();
      boxParams.leftMargin = xCoords[x];
      boxParams.topMargin = yCoords[y];
      setLayoutParams(boxParams);
    }

    private void snapToGrid(int x, int y) {
      BoxCoords newBoxCoords = getNearestGridCoords(x, y);
      // TODO:
      // check if space is occupied by a stone in stoneMap
      // if so, return;
      setCoords(newBoxCoords.x, newBoxCoords.y);
    }
  }

  private class CursorListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      // TODO:
      placeStone();
      currentColor = currentColor.getOther();
      firstTouch = true;
      container.removeView(cursor);
      container.removeView(box);
    }

    void placeStone() {
      BoxCoords coords = box.getCoords();
      stoneMap.put(coords, new Stone(xCoords[coords.x],
                                     yCoords[coords.y], currentColor));
      moves.add(coords);
    }
  }
}
