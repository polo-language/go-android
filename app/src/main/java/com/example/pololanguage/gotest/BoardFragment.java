package com.example.pololanguage.gotest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BoardFragment extends Fragment {
  private int boardSize;
  private int handicap;
  protected static BoardFragment newInstance(int boardSize, int handicap) {
    BoardFragment frag = new BoardFragment();
    frag.setRetainInstance(true);
    frag.boardSize = boardSize;
    frag.handicap = handicap;
    return frag;
  }

  /* Need to move setRetainInstance here?
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setRetainInstance(true);
  }*/

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle icicle) {
    View view = super.onCreateView(inflater, container, icicle);
    // TODO
    return view;
  }
}
