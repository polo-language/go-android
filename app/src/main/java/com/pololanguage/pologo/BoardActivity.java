package com.pololanguage.pologo;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BoardActivity extends Activity
                           implements  QuitDialogFragment.QuitDialogListener  {
  public static final String EXTRA_BOARD_SIZE = "board_size";
  public static final String EXTRA_HANDICAP = "handicap";
  public static final String EXTRA_SAVED_GAME = "saved_game";
  static final String SAVED_BOARD_FILENAME = "saved_board";
  public static final String NO_SAVE_NAME = "noSave";
  public static final String NO_SAVE_JSON = "{\"" + NO_SAVE_NAME + "\":true}";
  public static final String CURRENT_COLOR_NAME = "currentColor";
  public static final String BOARD_SIZE_NAME = "boardSize";
  public static final String BOARD_NAME = "board";
  private BoardFragment board;
  boolean saveOnDestroy = true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    int boardSize = getIntent().getIntExtra(EXTRA_BOARD_SIZE,
                                           SelectorActivity.DEFAULT_BOARD_SIZE);
    int handicap = getIntent().getIntExtra(EXTRA_HANDICAP,
                                           SelectorActivity.DEFAULT_HANDICAP);
    String boardJson = getIntent().getStringExtra(EXTRA_SAVED_GAME);
    // TODO: get EXTRA_SAVED_GAME if it's not null

    setContentView(R.layout.game);

    if (findViewById(R.id.board_container) != null) {
      if (savedInstanceState != null) { return; }

      board = BoardFragment.newInstance(boardSize, handicap);
      getFragmentManager().beginTransaction()
                          .add(R.id.board_container, board).commit();
    }
  }

  @Override
  protected void onDestroy() {
    if (saveOnDestroy) {
      saveBoardToFile(true);
    }
    super.onDestroy();
  }

  void saveBoardToFile(Boolean saveGameState) {
    // TODO: verify this is working
    if (board == null) { return; }

    String json;
    if (saveGameState) {
      json = board.getJson();
      // TODO: test here if board was actually empty (or had only handicap stones) and should use NO_SAVE_STRING anyway
    } else {
      json = NO_SAVE_JSON;
    }
    File file = new File(getFilesDir(), SAVED_BOARD_FILENAME);
    try {
      saveStringToFile(file, json);
      // DEBUG:
      Log.i("saved: ", json);
    } catch (IOException ioE) {
      Log.e("BoardActivity#onStop: board state not saved", ioE.toString());
    }
  }

  private void saveStringToFile(File file, String string) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF8");
    writer.write(string);
    writer.flush();
    fos.getFD().sync();
    writer.close();
  }

  @Override
  protected void onStart() {
    super.onStart();
    // TODO: check for a saved game state - reload if found
  }

  //////////////////////////////////
  // Quit dialog implementation
  public void showNoticeDialog() {
    DialogFragment dialog = new QuitDialogFragment();
    dialog.show(getFragmentManager(), "QuitDialogFragment");
  }

  @Override
  public void onDialogPositiveClick() {
    // Close the app; saves state in onDestroy
    moveTaskToBack(true);
  }

  @Override
  public void onDialogNeutralClick() {
    // "New Game"
    board.reset();
    saveOnDestroy = false;
    saveBoardToFile(false);
    // Restart selector activity
    Intent selector = new Intent(this, SelectorActivity.class);
    startActivity(selector);
  }

  //////////////////////////////////
  // Button click handlers

  public void undo(View view) {
    if (board != null) board.undo();
  }

  public void pass(View view) {
    if (board != null) board.pass();
  }

  public void reset(View view) {
    if (board != null) {
      showNoticeDialog();
    }
  }
}
