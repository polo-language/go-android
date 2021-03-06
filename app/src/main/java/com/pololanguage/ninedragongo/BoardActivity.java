package com.pololanguage.ninedragongo;

import android.app.ActionBar;
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

  private BoardFragment boardFrag;
  boolean saveOnDestroy = true;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
      getActionBar().setCustomView(R.layout.action_bar);
    } catch (NullPointerException e) {
      getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    setContentView(R.layout.game);

    if (findViewById(R.id.board_container) != null) {
      if (savedInstanceState != null) { return; }

      boardFrag = BoardFragment.newInstance(getIntent()); //boardSize, handicap, colorString, historyJson, chainsJson);
      getFragmentManager().beginTransaction()
                          .add(R.id.board_container, boardFrag).commit();
    }
  }

  @Override
  protected void onDestroy() {
    if (saveOnDestroy) {
      saveBoardToFile(true);
    }
    super.onDestroy();
  }

  private void saveBoardToFile(Boolean saveGameState) {
    if (boardFrag == null) { return; }

    String json;
    if (saveGameState) {
      json = boardFrag.toJson();
      // TODO: (eventually) test here if boardFrag was actually empty (or had only handicap stones) and should use NO_SAVE_STRING anyway
    } else {
      json = Serializer.NO_SAVE_JSON;
    }
    File file = new File(getFilesDir(), Serializer.SAVED_BOARD_FILENAME);
    try {
      if (json == null) {
        throw new IOException("JSON returned as null from board#toJson");
      }
      saveStringToFile(file, json);
      Log.i("saved: ", json);   // DEBUG:
    } catch (IOException e) {
      Log.e("Board state not saved", e.toString());
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

  void lockButtons() {
    findViewById(R.id.undo_button).setEnabled(false);
    findViewById(R.id.redo_button).setEnabled(false);
    findViewById(R.id.pass_button).setEnabled(false);
  }

  void unlockButtons() {
    findViewById(R.id.undo_button).setEnabled(true);
    findViewById(R.id.redo_button).setEnabled(true);
    findViewById(R.id.pass_button).setEnabled(true);
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
  public void onDialogNegativeClick() {
    // "New Game"
    boardFrag.reset();
    saveOnDestroy = false;
    saveBoardToFile(false);
    // Restart selector activity
    Intent selector = new Intent(this, SelectorActivity.class);
    startActivity(selector);
  }

  /* Button click handlers */
  public void undo(View view) {
    if (boardFrag != null) {
      lockButtons();
      boardFrag.undo();
      unlockButtons();
    }
  }

  public void redo(View view) {
    if (boardFrag != null) {
      lockButtons();
      boardFrag.redo();
      unlockButtons();
    }
  }

  public void pass(View view) {
    if (boardFrag != null) {
      lockButtons();
      boolean quit = boardFrag.pass();
      if (!quit) {
        unlockButtons();
      }
    }
  }

  public void reset(View view) {
    if (boardFrag != null) {
      lockButtons();
      showNoticeDialog();
      unlockButtons();
    }
  }
}
