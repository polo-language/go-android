package com.pololanguage.ninedragongo;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

public class BoardFragment extends Fragment
                           implements View.OnTouchListener {
  private int boardSize;
  private int handicap;
  private int boardWidth;

  /** Tracks sequential passes (two passes in a row ends the game) */
  private boolean firstPass = false;

  /** Used to add cursor and box only if not already on board */
  private boolean firstTouch = true;

  private StoneColor currentColor;
  private MoveManager moveManager;
  private BoardView board;
  private Stone cursor;
  private Box box;

  private Intent intent;

  /** Standard handicap board coordinates */
  private static final BoxCoords[] NINE_HANDICAPS = {new BoxCoords(6, 2), new BoxCoords(2, 6), new BoxCoords(6, 6), new BoxCoords(2, 2), new BoxCoords(4, 4)};
  private static final BoxCoords[] THIRTEEN_HANDICAPS = {new BoxCoords(9, 3), new BoxCoords(3, 9), new BoxCoords(9, 9), new BoxCoords(3, 3), new BoxCoords(6, 6)};
  private static final BoxCoords[] NINETEEN_HANDICAPS = {new BoxCoords(15, 3), new BoxCoords(3, 15), new BoxCoords(15, 15), new BoxCoords(3, 3), new BoxCoords(9, 9)};

  /** BoardFragment factory */
  protected static BoardFragment newInstance(Intent intent) { //int boardSize, int handicap, String colorString, String boardString) {
    BoardFragment frag = new BoardFragment();
    frag.intent = intent;
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    String chainJson;
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
    boardWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    /* board size validated in BoardView constructor in switch on size */
    boardSize = intent.getIntExtra(Serializer.KEYS.SIZE, SelectorActivity.DEFAULT_BOARD_SIZE);
    handicap = intent.getIntExtra(Serializer.EXTRA_HANDICAP, SelectorActivity.DEFAULT_HANDICAP);
    try {
      currentColor = StoneColor.valueOf(intent.getStringExtra(Serializer.KEYS.COLOR));
    } catch (IllegalArgumentException e) {
      currentColor = StoneColor.BLACK;
    }

    chainJson = intent.getStringExtra(Serializer.KEYS.CHAINS);
    if (chainJson.isEmpty()) {  /* checks if we're getting saved state data or a new game */
      moveManager = new MoveManager(boardSize);
    } else {                    /* saved state data */
      moveManager = new MoveManager(boardSize,
                                    Serializer.deserializeChains(getActivity(), chainJson),
                                    Serializer.deserializeHistory(getActivity(), intent.getStringExtra(Serializer.KEYS.HISTORY)));
      intent = null; /* manual garbage collection */
    }
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

    if (moveManager.getChains().size() == 0) {
      addHandicapStones();
    } else {
      placeStoredMoves();
    }
  }

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
    for (Chain chain : moveManager.getChains()) {
      for (Stone stone : chain.getStones()) {
        board.renderStone(stone);
      }
    }
  }

  private void placeStoneAtBoxCoords() {
    placeStone(box.getCoords());
  }

  private void placeStone(BoxCoords coords) {
    board.setClickable(false);

    Stone stone = new Stone(getActivity(), coords, currentColor);
    Set<Stone> toKill = new HashSet<>();

    if (moveManager.addStone(stone, toKill)) {
      board.renderStone(stone);
      toggleColor();
      removeCursor();
      firstPass = false;
      for (Stone s : toKill) {
        board.removeView(s);
      }
    } else {
      Toast.makeText(getActivity(), R.string.no_suicide, Toast.LENGTH_LONG).show();
    }
    board.setClickable(true);
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
    if (!moveManager.taken(coords)) {
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

  /* BUTTON onClick LISTENERS */
  /**
   * Undo previous move if past moves are available
   * Removes stone and adds back any chains captured by this move
   */
  public void undo() {
    Move move = moveManager.undo();
    if (move == null) {
      Toast.makeText(getActivity(), R.string.no_moves_to_undo, Toast.LENGTH_SHORT).show();
    } else {
      board.removeView(move.getStone());
      for (Stone stone : move.getCaptured()) {
        board.addView(stone);
      }
      removeCursor();
      toggleColor();
    }
  }

  /**
   * Redo next move if future moves are available
   * Adds back stone and removes any chains captured by it
   */
  public void redo() {
    Move move = moveManager.redo();
    if (move == null) {
      Toast.makeText(getActivity(), R.string.no_future_to_redo, Toast.LENGTH_SHORT).show();
    } else {
      for (Stone stone : move.getCaptured()) {
        board.removeView(stone);
      }
      board.addView(move.getStone());
      removeCursor();
      toggleColor();
    }
  }

  /**
   * Pass this player's move and toggle to next next player's color
   * @return true if this pass quits the game (second pass in a row)
   */
  public boolean pass() {
    if (firstPass) {
      endGame();
      return true;
    }
    removeCursor();
    Toast.makeText(getActivity(),
            currentColor == StoneColor.BLACK ?
                    R.string.pass_black : R.string.pass_white,
            Toast.LENGTH_SHORT).show();
    toggleColor();
    firstPass = true;
    return false;
  }

  public void endGame() {
    removeCursor();
    board.setOnTouchListener(null);
    board.setClickable(false);
    getActivity().findViewById(R.id.game_over).setVisibility(View.VISIBLE);
  }

  public void reset() {
    board.removeAllViews();
    moveManager.reset();
    board.setOnTouchListener(this);
    getActivity().findViewById(R.id.game_over).setVisibility(View.INVISIBLE);
  }

  //////////////////////////////////
  // SERIALIZATION:
  String toJson() {
    if (currentColor == null) {   /* return null if we haven't been initialized yet */
      return null;
    }
    return Serializer.serializeBoard(getActivity(),
                                     currentColor,
                                     boardSize,
                                     moveManager.getHistory(),
                                     moveManager.getChains());
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
