package com.pololanguage.pologo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BoardActivity extends Activity {
  public static final String EXTRA_BOARD_SIZE = "board_size";
  public static final String EXTRA_HANDICAP = "handicap";
  private static final String SAVED_BOARD_FILENAME = "saved_board";
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
    // TODO: save board state to disk
    if (board == null) { return; }

    String json = board.getJson();
    File file = new File(getFilesDir(), SAVED_BOARD_FILENAME);
    try {
      saveStringToFile(file, json);
    } catch (IOException ioE) {
      Log.e("onStop: board state not saved", ioE.toString());
    } finally {
      super.onStop();
    }
  }

  private void saveStringToFile(File file, String string) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(fos);
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
