package com.example.pololanguage.gotest;

import android.app.Activity;
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
    //View view = super.onCreateView(inflater, container, icicle);

    /*Activity parent = getActivity();
    View board = parent.findViewById(R.id.board_view);
    DisplayMetrics displayMetrics = parent.getResources().getDisplayMetrics();
    //DisplayMetrics displayMetrics = new DisplayMetrics();
    //parent.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    int boardWidth = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    Log.d("BoardFragment#onCreateView", Integer.toString(boardWidth));

    board.setLayoutParams(new RelativeLayout.LayoutParams(boardWidth, boardWidth));
    board.setBackgroundColor(0xFF00FF00);*/
    //return view;
    return inflater.inflate(R.layout.game, container, false);
  }
}
