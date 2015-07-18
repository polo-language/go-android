package com.pololanguage.pologo;

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
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BoardFragment extends Fragment
                           implements View.OnTouchListener {
  private int boardSize;
  private int handicap;
  private int boardWidth;
  private int stoneWidth;
  private int margin;
  private StoredMove[] storedMoves;
  private int[] xCoords;
  private int[] yCoords;
  private boolean firstTouch = true;
  private boolean firstPass = false;
  private StoneColor currentColor;
  ArrayList<Stone> stones = new ArrayList<>();
  private Set<BoxCoords> moves = new HashSet<>();
  private RelativeLayout board;
  private Stone cursor;
  private Box box;

  private static final float NINE_STONEMODIFIER = (float)67/615;
  private static final float THIRTEEN_STONEMODIFIER = (float)46/615;
  private static final float NINETEEN_STONEMODIFIER = (float)31/615;

  private static final BoxCoords[] NINE_HANDICAPS = {new BoxCoords(6, 2), new BoxCoords(2, 6), new BoxCoords(6, 6), new BoxCoords(2, 2), new BoxCoords(4, 4)};
  private static final BoxCoords[] THIRTEEN_HANDICAPS = {new BoxCoords(9, 3), new BoxCoords(3, 9), new BoxCoords(9, 9), new BoxCoords(3, 3), new BoxCoords(6, 6)};
  private static final BoxCoords[] NINETEEN_HANDICAPS = {new BoxCoords(15, 3), new BoxCoords(3, 15), new BoxCoords(15, 15), new BoxCoords(3, 3), new BoxCoords(9, 9)};


  protected static BoardFragment newInstance(int boardSize, int handicap, String colorString, String boardString) {
    BoardFragment frag = new BoardFragment();
    frag.boardSize = boardSize;
    frag.handicap = handicap;
    try {
      frag.currentColor = StoneColor.valueOf(colorString);
    } catch (IllegalArgumentException e) {
      frag.currentColor = StoneColor.BLACK;
    }

    if (boardString != null) {
      frag.storedMoves = (new Gson()).fromJson(boardString, StoredMove[].class);
    }
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
    boardWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    handleBoardSize();
    setCoordinateArrays();
  }

  private void handleBoardSize() {
    switch (boardSize) {
      case 9:
        stoneWidth = Math.round(boardWidth * NINE_STONEMODIFIER);
        break;
      case 13:
        stoneWidth = Math.round(boardWidth * THIRTEEN_STONEMODIFIER);
        break;
      case 19:
        stoneWidth = Math.round(boardWidth * NINETEEN_STONEMODIFIER);
        break;
      default:
        Log.e("Board#handleBoardSize", "Incorrect board size (not 9, 13, or 19).");
    }
    margin = Math.round((boardWidth - boardSize*stoneWidth) / 2);
  }

  private void setCoordinateArrays() {
    xCoords = new int[boardSize];
    yCoords = new int[boardSize];
    for(int i = 0; i < boardSize; ++i) {
      xCoords[i] = margin + i*stoneWidth;
      yCoords[i] = margin + i*stoneWidth;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    return BoardView.newInstance(getActivity(), xCoords, yCoords);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    layoutBoard();
    if (storedMoves == null) {
      addHandicapStones();
    } else {
      placeStoredMoves();
    }
    // DEBUG: testStoneLayout();
  }

  // DEBUG:
//  public void testStoneLayout() {
//    for (int i = 0; i < boardSize; ++i) {
//      for (int j = 0; j < boardSize; ++j) {
//        placeStone(new BoxCoords(i, j));
//      }
//    }
//  }

  private void layoutBoard() {
    View boardContainer = getActivity().findViewById(R.id.board_container);

    RelativeLayout.LayoutParams layoutParams =
            new RelativeLayout.LayoutParams(boardWidth, boardWidth);
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    boardContainer.setLayoutParams(layoutParams);

    board = (RelativeLayout)getActivity().findViewById(R.id.board);
    board.setOnTouchListener(this);
  }

  private void addHandicapStones() {
    if (handicap == 0) { return; }

    BoxCoords[] handicaps;
    switch(boardSize) {
      case 9:
        handicaps = NINE_HANDICAPS;
        break;
      case 13:
        handicaps =  THIRTEEN_HANDICAPS;
        break;
      case 19:
        handicaps = NINETEEN_HANDICAPS;
        break;
      default: // shouldn't get here
        return;
    }

    for (int i = 0; i < handicap; ++i) {
      placeStone(handicaps[i]);
    }
    currentColor = StoneColor.WHITE;
  }

  private void placeStoredMoves () {
    for (StoredMove move : storedMoves) {
      currentColor = move.color;
      placeStone(new BoxCoords(move.x, move.y));
    }
    currentColor = currentColor.getOther();
  }

  private void placeStoneAtBoxCoords() {
    placeStone(box.getCoords());
  }

  private void placeStone(BoxCoords coords) {
    moves.add(coords);
    stones.add(new Stone(coords, currentColor));
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    final int X = (int) event.getX();
    final int Y = (int) event.getY();
    int newX, newY;

    if (firstTouch) { addCursor(); }

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

  private void addCursor() {
    cursor = new Stone(null, currentColor);
    cursor.setAlpha(0.7f);

    box = new Box();
    firstTouch = false;
  }

  private void removeCursor() {
    if (cursor != null) board.removeView(cursor);
    if (box != null) board.removeView(box);
    firstTouch = true;
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

  //////////////////////////////////
  // BUTTON onClick LISTENERS
  public void undo() {
    if (stones.isEmpty()) {
      Toast.makeText(getActivity(), R.string.no_moves_to_undo, Toast.LENGTH_LONG)
              .show();
      return;
    }
    Stone lastStone = stones.remove(stones.size() - 1);
    moves.remove(lastStone.coords);
    board.removeView(lastStone);
    Toast.makeText(getActivity(),
            lastStone.color == StoneColor.BLACK ?
                    R.string.undo_black : R.string.undo_white,
            Toast.LENGTH_SHORT).show();
  }

  public void pass() {
    if (firstPass) {
      endGame();
      return;
    }
    removeCursor();
    Toast.makeText(getActivity(),
            currentColor == StoneColor.BLACK ?
                    R.string.pass_black : R.string.pass_white,
            Toast.LENGTH_SHORT).show();
    currentColor = currentColor.getOther();
    firstPass = true;
  }

  public void endGame() {
    removeCursor();
    board.setOnTouchListener(null);
    board.setClickable(false);
    getActivity().findViewById(R.id.undo_button).setEnabled(false);
    getActivity().findViewById(R.id.pass_button).setEnabled(false);
    getActivity().findViewById(R.id.game_over).setVisibility(View.VISIBLE);
  }

  public void reset() {
    board.removeAllViews();
    moves.clear();
    stones.clear();
    board.setOnTouchListener(this);
    getActivity().findViewById(R.id.undo_button).setEnabled(true);
    getActivity().findViewById(R.id.pass_button).setEnabled(true);
    getActivity().findViewById(R.id.game_over).setVisibility(View.INVISIBLE);
    Toast.makeText(getActivity(), R.string.reset, Toast.LENGTH_LONG).show();
  }

  //////////////////////////////////
  // SERIALIZATION:
  String getJson() {
    int numMoves = stones.size();
    StoredMove[] moveArray = new StoredMove[numMoves];
    Stone stone;

    for (int i = 0; i < numMoves; ++i) {
      stone = stones.get(i);
      moveArray[i] = new StoredMove(i, stone.coords.x, stone.coords.y, stone.color);
    }

    return "{\"" + BoardActivity.CURRENT_COLOR_NAME + "\":\"" + currentColor + "\",\"" +
        BoardActivity.BOARD_SIZE_NAME + "\":" + boardSize + ",\"" +
        BoardActivity.BOARD_NAME + "\":" + (new Gson()).toJson(moveArray) + "}";
  }

  //////////////////////////////////
  // INNER CLASSES:
  private class Stone extends View {
    BoxCoords coords;
    StoneColor color;

    Stone(BoxCoords coords, StoneColor color) {
      super(BoardFragment.this.getActivity());
      this.color = color;

      if (coords == null) { // used for cursor only
        setUpStoneView(-200, -200, color);
      } else {
        this.coords = coords;
        setUpStoneView(xCoords[coords.x], yCoords[coords.y], color);
      }
    }

    private void setUpStoneView(int x, int y, StoneColor color) {
      RelativeLayout.LayoutParams layout =
              new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
      layout.leftMargin = x;
      layout.topMargin = y;
      setLayoutParams(layout);
      setBackgroundResource(color.getResource());
      board.addView(this);
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

    Box() {
      super(BoardFragment.this.getActivity());
      coords = new BoxCoords(-1, -1);
      RelativeLayout.LayoutParams boxParams =
              new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
      boxParams.leftMargin = -200;
      boxParams.topMargin = -200;
      setLayoutParams(boxParams);
      setBackgroundResource(R.drawable.box);
      setOnClickListener(new CursorListener());
      board.addView(this);
    }

    BoxCoords getCoords() {
      return coords;
    }

    private void setCoords(BoxCoords boxCoords) {
      coords.x = boxCoords.x;
      coords.y = boxCoords.y;
      RelativeLayout.LayoutParams boxParams =
              (RelativeLayout.LayoutParams) getLayoutParams();
      boxParams.leftMargin = xCoords[boxCoords.x];
      boxParams.topMargin = yCoords[boxCoords.y];
      setLayoutParams(boxParams);
    }

    private void snapToGrid(int x, int y) {
      BoxCoords newBoxCoords = getNearestGridCoords(x, y);
      if (moves.contains(newBoxCoords)) { return; }
      setCoords(newBoxCoords);
    }
  }

  private class CursorListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      placeStoneAtBoxCoords();
      removeCursor();
      currentColor = currentColor.getOther();
      firstPass = false;
    }
  }
}
