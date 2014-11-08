package com.example.pololanguage.gotest;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class BoardFragment extends Fragment {
  private int boardSize;
  private int handicap;

  protected static BoardFragment newInstance(int boardSize, int handicap) {
    BoardFragment frag = new BoardFragment();
    frag.setRetainInstance(true); // need to move to onCreate() ?
    frag.boardSize = boardSize;
    frag.handicap = handicap;
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle icicle) {
    return inflater.inflate(R.layout.board, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
    int boardWidth = Math.min(displayMetrics.widthPixels,
                              displayMetrics.heightPixels);
    View boardContainer = getActivity().findViewById(R.id.board_container);
    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(boardWidth, boardWidth);
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    boardContainer.setLayoutParams(layoutParams);
    boardContainer.setBackgroundColor(0xFF0000FF);
    View testView = getActivity().findViewById(R.id.test_view_on_board);
    testView.setBackgroundColor(0xFFFF0000);
  }
}
