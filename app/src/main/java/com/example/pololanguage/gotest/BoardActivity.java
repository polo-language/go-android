package com.example.pololanguage.gotest;

import android.app.Activity;
import android.os.Bundle;

public class BoardActivity extends Activity {
  public static final String EXTRA_BOARD_SIZE = "board_size";
  public static final String EXTRA_HANDICAP = "handicap";

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    int boardSize = getIntent().getIntExtra(EXTRA_BOARD_SIZE,
                                           SelectorActivity.DEFAULT_BOARD_SIZE);
    int handicap = getIntent().getIntExtra(EXTRA_HANDICAP,
                                           SelectorActivity.DEFAULT_HANDICAP);
    if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
      getFragmentManager().beginTransaction()
                          .add(android.R.id.content,
                               BoardFragment.newInstance(boardSize, handicap))
                          .commit();
    }
  }
}
