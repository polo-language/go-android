package com.example.pololanguage.pologo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class BoardActivity extends Activity {
  public static final String EXTRA_BOARD_SIZE = "board_size";
  public static final String EXTRA_HANDICAP = "handicap";
  BoardFragment board;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int boardSize = getIntent().getIntExtra(EXTRA_BOARD_SIZE,
                                           SelectorActivity.DEFAULT_BOARD_SIZE);
    int handicap = getIntent().getIntExtra(EXTRA_HANDICAP,
                                           SelectorActivity.DEFAULT_HANDICAP);
    setContentView(R.layout.game);

    if (findViewById(R.id.board_container) != null) {
      if (savedInstanceState != null) { return; }

      board = BoardFragment.newInstance(boardSize, handicap);
      getFragmentManager().beginTransaction()
                          .add(R.id.board_container, board).commit();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    // TODO: save board state to disk
  }

  @Override
  protected void onStart() {
    super.onStart();
    // TODO: check for a saved game state - reload if found
  }

  public void undo(View view) {
    if (board != null) board.undo();
  }

  public void pass(View view) {
    if (board != null) board.pass();
  }

  public void reset(View view) {
    if (board != null) board.reset();
  }
}
