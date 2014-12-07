package com.example.pololanguage.pologo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SelectorActivity extends Activity {
  static final int DEFAULT_BOARD_SIZE = 9;    // default checked radio button
  static final int DEFAULT_HANDICAP = 0;      // default in spinner
  private int boardSize = DEFAULT_BOARD_SIZE;
  private int handicap = DEFAULT_HANDICAP;
  private static Integer[] handicaps = {0, 1, 2, 3, 4, 5};

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.selector);
    Spinner spinner = (Spinner)findViewById(R.id.handicap_spinner);
    spinner.setOnItemSelectedListener(new HandicapSpinner());
    ArrayAdapter<Integer> aa = new ArrayAdapter<Integer>(this,
                            android.R.layout.simple_spinner_item,
                            handicaps);
    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(aa);
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
    Intent boardIntent = new Intent(this, BoardActivity.class);
    boardIntent.putExtra(BoardActivity.EXTRA_BOARD_SIZE, boardSize);
    boardIntent.putExtra(BoardActivity.EXTRA_HANDICAP, handicap);
    startActivity(boardIntent);
  }
}
