package com.pololanguage.pologo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    File file = new File(getFilesDir(), BoardActivity.SAVED_BOARD_FILENAME);
    try {
      FileInputStream fileInputStream = new FileInputStream(file);

      try {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(new InputStreamReader(fileInputStream, "UTF-8"));
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement noSave = jsonObject.get(BoardActivity.NO_SAVE_NAME);
        String colorString;
        int boardSizeFromJson;
        String boardString;
        JsonArray boardArray;

        if (noSave != null) {
          loadSelectorView();
        } else {
          colorString = jsonObject.get(BoardActivity.CURRENT_COLOR_NAME).getAsString();
          boardSizeFromJson = jsonObject.get(BoardActivity.BOARD_SIZE_NAME).getAsInt();
          boardArray = jsonObject.getAsJsonArray(BoardActivity.BOARD_NAME);
          boardString = gson.toJson(boardArray);

          // TODO: check loaded values - initialize or load selector if 'too many' are 'bad'
          loadBoard(colorString, boardSizeFromJson, boardString);
        }
      } catch (Exception err) { // Just start a new game
        loadSelectorView();
      } finally {
        fileInputStream.close();
      }
    } catch (FileNotFoundException fnfExc) {
      loadSelectorView();
    } catch (IOException ioExc) {
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

  private void loadBoard(String currentColor, int boardSizeToUse, String boardString) {
    Intent boardIntent = new Intent(this, BoardActivity.class);

    boardIntent.putExtra(BoardActivity.BOARD_SIZE_NAME, boardSizeToUse);
    boardIntent.putExtra(BoardActivity.EXTRA_HANDICAP, handicap);
    boardIntent.putExtra(BoardActivity.CURRENT_COLOR_NAME, currentColor);
    boardIntent.putExtra(BoardActivity.BOARD_NAME, boardString);

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
    //boolean checked = ((RadioButton) view).isChecked();
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
    loadBoard("BLACK", boardSize, null); // check for null boardString in BoardActivity
  }
}
