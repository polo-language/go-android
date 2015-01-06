package com.pololanguage.pologo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    String savedGameString;
    File file = new File(getFilesDir(), BoardActivity.SAVED_BOARD_FILENAME);

    try { // This leaks a FileInputStream if it throws...
      FileInputStream fis = new FileInputStream(file);
      savedGameString = convertStreamToString(fis);
      fis.close();

      // TODO: check value of saved board on first start and loadSelectorView there as well
      if (savedGameString.trim().equals(BoardActivity.NO_SAVE_STRING)) {
        loadSelectorView();
      } else {
        loadBoard(savedGameString);
      }
    } catch (Exception err) {
      Log.e("SelectorActivity load file: ", err.toString());
      loadSelectorView();
    }
  }

  public static String convertStreamToString(InputStream is) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      sb.append(line).append("\n");
    }
    reader.close();
    return sb.toString();
  }

  private void loadSelectorView() {
    setContentView(R.layout.selector);
    Spinner spinner = (Spinner)findViewById(R.id.handicap_spinner);
    spinner.setOnItemSelectedListener(new HandicapSpinner());
    ArrayAdapter<Integer> aa = new ArrayAdapter<Integer>(this,
        android.R.layout.simple_spinner_item,
        handicaps);
    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(aa);
  }

  private void loadBoard(String savedGameString) {
    Intent boardIntent = new Intent(this, BoardActivity.class);
    boardIntent.putExtra(BoardActivity.EXTRA_BOARD_SIZE, boardSize);
    boardIntent.putExtra(BoardActivity.EXTRA_HANDICAP, handicap);
    if (savedGameString != null) { // TODO: delete this check, should be fine checking in BoardActivity#onCreate() alone
      boardIntent.putExtra(BoardActivity.EXTRA_SAVED_GAME, savedGameString);
    }
    startActivity(boardIntent);
  }

  public class HandicapSpinner implements AdapterView.OnItemSelectedListener {
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
    loadBoard(null);
  }
}
