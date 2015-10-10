package com.pololanguage.ninedragongo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class SelectorActivity extends Activity {
  static final int DEFAULT_BOARD_SIZE = 9;    // default checked radio button
  static final int DEFAULT_HANDICAP = 0;      // default in spinner
  private int boardSize = DEFAULT_BOARD_SIZE;
  private int handicap = DEFAULT_HANDICAP;
  private static Integer[] handicaps = {0, 1, 2, 3, 4, 5};

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    try {
      getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
      getActionBar().setCustomView(R.layout.action_bar);
    } catch (NullPointerException e) {
      getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    File file = new File(getFilesDir(), Serializer.SAVED_BOARD_FILENAME);
    try {
      FileInputStream fileInputStream = new FileInputStream(file);

      try {
        JsonObject jsonObject = (new JsonParser())
            .parse(new InputStreamReader(fileInputStream, "UTF-8"))
            .getAsJsonObject();

        if (jsonObject.get(Serializer.NO_SAVE_KEY) != null) {
          loadSelectorView();
        } else {
          // TODO: validate loaded values
          loadBoard(jsonObject);
        }
      } catch (Exception err) { // Just start a new game
        loadSelectorView();
      } finally {
        fileInputStream.close();
      }
    } catch (FileNotFoundException fnfExc) {
      loadSelectorView();
    }
    catch (IOException ioExc) {
      // Do nothing, just for closing fileInputStream - game state loaded already
    }
  }

  private void loadSelectorView() {
    setContentView(R.layout.selector);
    Spinner spinner = (Spinner)findViewById(R.id.handicap_spinner);
    spinner.setOnItemSelectedListener(new HandicapSpinnerListener());

    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item,
        handicaps);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }

  /**
   * Add saved board settings to intent extras, then launch a new BoardActivity.
   * null argument used for sending selections from selector rather than saved state on disk
   */
  private void loadBoard(JsonObject jsonObject) {
    Intent boardIntent = new Intent(this, BoardActivity.class);

    if (jsonObject == null) {   /* Use values from selector */
      boardIntent.putExtra(Serializer.KEYS.SIZE, boardSize);
      boardIntent.putExtra(Serializer.EXTRA_HANDICAP, handicap);
      boardIntent.putExtra(Serializer.KEYS.COLOR, StoneColor.BLACK.toString());
      boardIntent.putExtra(Serializer.KEYS.HISTORY, "");
      boardIntent.putExtra(Serializer.KEYS.CHAINS, "");
    } else {                    /* parse and use values read from disk */
      boardIntent.putExtra(Serializer.KEYS.SIZE, jsonObject.get(Serializer.KEYS.SIZE).getAsInt());
      boardIntent.putExtra(Serializer.EXTRA_HANDICAP, handicap);
      boardIntent.putExtra(Serializer.KEYS.COLOR, jsonObject.get(Serializer.KEYS.COLOR).getAsString());
      boardIntent.putExtra(Serializer.KEYS.HISTORY,
                           new Gson().toJson(jsonObject.getAsJsonArray(Serializer.KEYS.HISTORY)));
      boardIntent.putExtra(Serializer.KEYS.CHAINS,
          new Gson().toJson(jsonObject.getAsJsonArray(Serializer.KEYS.CHAINS)));
    }

    startActivity(boardIntent);
  }

  public class HandicapSpinnerListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
      handicap = handicaps[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* do nothing */ }
  }

  public void onBoardSizeRadioClicked(View view) {
    switch (view.getId()) {
      case R.id.board_size_radio_9:
        boardSize = 9;
        break;
      case R.id.board_size_radio_13:
        boardSize = 13;
        break;
      case R.id.board_size_radio_19:
        boardSize = 19;
        break;
    }
  }

  public void onGoClicked(View view) {
    loadBoard(null);
  }
}
