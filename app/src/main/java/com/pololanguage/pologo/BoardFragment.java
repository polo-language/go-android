package com.pololanguage.pologo;


import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;


public class BoardFragment extends Fragment
                           implements View.OnTouchListener {
  private int boardSize;
  private int handicap;
  private int boardWidth;

  /**
   * Holds saved state read from file during board setup
   */
  private StoredMove[] storedMoves;

  /**
   * Tracks sequential passes (two passes in a row ends the game)
   */
  private boolean firstPass = false;

  /**
   * Used to add cursor and box only if not already on board
   */
  private boolean firstTouch = true;

  private StoneColor currentColor;
  private ChainManager chainManager;
  private BoardView board;
  private Stone cursor;
  private Box box;

  /**
   * Standard handicap board coordinates
   */
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
    chainManager = new ChainManager();
    boardWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    try {
      board = BoardView.newInstance(getActivity(), boardSize, boardWidth);
    } catch (IllegalArgumentException e) {
      Toast.makeText(getActivity(), R.string.application_error, Toast.LENGTH_LONG).show();
      getActivity().finish();
    }
    return board;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    layoutBoard();
    board.setOnTouchListener(this);

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
  }

  private void addHandicapStones() {
    if (handicap == 0) {
      return;
    }

    BoxCoords[] handicaps;
    switch (boardSize) {
      case 9:
        handicaps = NINE_HANDICAPS;
        break;
      case 13:
        handicaps = THIRTEEN_HANDICAPS;
        break;
      case 19:
        handicaps = NINETEEN_HANDICAPS;
        break;
      default: // shouldn't get here
        return;
    }

    for (int i = 0; i < handicap; ++i) {
      currentColor = StoneColor.BLACK;
      placeStone(handicaps[i]);
    }
  }

  private void placeStoredMoves() {
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
    Stone stone = new Stone(getActivity(), coords, currentColor);
    if (chainManager.addStone(stone)) {
      board.renderStone(stone);
      toggleColor();
      removeCursor();
      firstPass = false;
    } else {
      Toast.makeText(getActivity(), R.string.no_suicide, Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    final int x = (int) event.getX();
    final int y = (int) event.getY();

    if (firstTouch) {
      addCursor();
    }

    switch (event.getActionMasked()) {
      case MotionEvent.ACTION_DOWN: // intentional fall-through
      case MotionEvent.ACTION_MOVE:
        board.centerStoneOnClick(cursor, x, y);
        snapBoxToGrid(x, y);
        break;
    }
    return true;
  }

  private void snapBoxToGrid(int x, int y) {
    BoxCoords coords = board.getNearestGridCoords(x, y);
    if (!chainManager.taken(coords)) {
      box.setCoords(coords);
      board.setViewMargins(box, coords);
    }
  }

  private void addCursor() {
    cursor = new Stone(getActivity(), new BoxCoords(-1, -1), currentColor);
    board.renderStone(cursor);
    cursor.setAlpha(0.7f);

    box = new Box(getActivity());
    board.renderBox(box);
    box.setOnClickListener(new BoxClickListener());

    firstTouch = false;
  }

  private void removeCursor() {
    if (cursor != null) board.removeView(cursor);
    if (box != null) board.removeView(box);
    firstTouch = true;
  }

  void toggleColor() {
    currentColor = currentColor.getOther();
  }

  //////////////////////////////////
  // BUTTON onClick LISTENERS
  public void undo() {
    if (chainManager.hasNoMoves()) {
      Toast.makeText(getActivity(), R.string.no_moves_to_undo, Toast.LENGTH_LONG).show();
      return;
    }
    Stone stone = chainManager.popStone();
    board.removeView(stone);
    removeCursor();
    toggleColor();
    Toast.makeText(getActivity(),
            stone.color == StoneColor.BLACK ?
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
    toggleColor();
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
    chainManager.reset();
    board.setOnTouchListener(this);
    getActivity().findViewById(R.id.undo_button).setEnabled(true);
    getActivity().findViewById(R.id.pass_button).setEnabled(true);
    getActivity().findViewById(R.id.game_over).setVisibility(View.INVISIBLE);
    Toast.makeText(getActivity(), R.string.reset, Toast.LENGTH_LONG).show();
  }

  //////////////////////////////////
  // SERIALIZATION:
  String getJson() {
    return "{\"" + BoardActivity.CURRENT_COLOR_NAME + "\":\"" + currentColor + "\",\"" +
        BoardActivity.BOARD_SIZE_NAME + "\":" + boardSize + ",\"" +
        BoardActivity.BOARD_NAME + "\":" + chainManager.toJson() + "}";
  }

  //////////////////////////////////
  // INNER CLASSES:
  private class BoxClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
      placeStoneAtBoxCoords();
    }
  }
}
