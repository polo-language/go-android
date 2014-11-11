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
  private int boardWidth;
  private int stoneWidth;
  private int marginTop;
  private int marginLeft;
  private int boardImage;
  private static final int STONE_BLACK = R.drawable.stone_black_55x55;
  private static final int STONE_WHITE = R.drawable.stone_white_55x55;

  private static final float NINE_MARGINMODIFIER = (float)10/615;
  private static final float NINE_STONEMODIFIER = (float)65/615;
  private static final float THIRTEEN_MARGINMODIFIER = (float)10/615;
  private static final float THIRTEEN_STONEMODIFIER = (float)44/615;
  private static final float NINETEEN_MARGINMODIFIER = (float)20/615;
  private static final float NINETEEN_STONEMODIFIER = (float)29/615;
  private static final int PADDING = 2;

  protected static BoardFragment newInstance(int boardSize, int handicap) {
    BoardFragment frag = new BoardFragment();
    //frag.setRetainInstance(true); // need to move to onCreate() ?
    frag.boardSize = boardSize;
    frag.handicap = handicap;
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    DisplayMetrics displayMetrics = getActivity().getResources()
            .getDisplayMetrics();
    boardWidth = Math.min(displayMetrics.widthPixels,
            displayMetrics.heightPixels);
    switch (boardSize) {
      case 9:
        stoneWidth = Math.round(boardWidth * NINE_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * NINE_MARGINMODIFIER);
        marginTop = marginLeft;
        boardImage = R.drawable.board_9x9;
        break;
      case 13:
        stoneWidth = Math.round(boardWidth * THIRTEEN_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * THIRTEEN_MARGINMODIFIER);
        marginTop = marginLeft;
        boardImage = R.drawable.board_13x13;
        break;
      case 19:
        stoneWidth = Math.round(boardWidth * NINETEEN_STONEMODIFIER);
        marginLeft = Math.round(boardWidth * NINETEEN_MARGINMODIFIER);
        marginTop = marginLeft - 5;
        boardImage = R.drawable.board_19x19;
        break;
      default:
        Log.e("Board#onCreateView", "Incorrect board size (not 9, 13, or 19).");
    }

    Log.i("Board#onCreateView:", "boardWidth: " + boardWidth);
    Log.i("Board#onCreateView:", "stoneWidth: " + stoneWidth);
    Log.i("Board#onCreateView:", "marginLeft: " + marginLeft);
    Log.i("Board#onCreateView:", "marginLeft: " + marginTop);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.board, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    View boardContainer = getActivity().findViewById(R.id.board_container);
    RelativeLayout.LayoutParams layoutParams =
            new RelativeLayout.LayoutParams(boardWidth, boardWidth);
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
    boardContainer.setLayoutParams(layoutParams);
    boardContainer.setBackgroundResource(boardImage);
    //boardContainer.setBackgroundColor(0xFF0000FF);

    /*
    View testView = getActivity().findViewById(R.id.test_view_on_board);
    testView.setBackgroundColor(0xFFFF0000);
    RelativeLayout.LayoutParams testLayoutParams = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    testLayoutParams.leftMargin = marginLeft;
    testLayoutParams.topMargin = marginTop;
    testView.setLayoutParams(testLayoutParams);

    View test2 = getActivity().findViewById(R.id.test_view_2);
    test2.setBackgroundColor(0xFFA000C0);
    RelativeLayout.LayoutParams testLayoutParams2 = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    testLayoutParams2.leftMargin = marginLeft + 6*stoneWidth;
    testLayoutParams2.topMargin =  marginTop + 3*stoneWidth;
    test2.setLayoutParams(testLayoutParams2);

    View test3 = getActivity().findViewById(R.id.test_view_3);
    test3.setBackgroundColor(0xFF0064C0);
    RelativeLayout.LayoutParams testLayoutParams3 = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    testLayoutParams3.leftMargin = marginLeft + 5*stoneWidth;
    testLayoutParams3.topMargin =  marginTop + 3*stoneWidth;
    test3.setLayoutParams(testLayoutParams3);

    View test4 = getActivity().findViewById(R.id.test_view_4);
    test4.setBackgroundColor(0xFFA6D267);
    RelativeLayout.LayoutParams testLayoutParams4 = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
    testLayoutParams4.leftMargin = marginLeft + 9*stoneWidth;
    testLayoutParams4.topMargin =  marginTop + 9*stoneWidth;
    test4.setLayoutParams(testLayoutParams4);
    */
    RelativeLayout container = (RelativeLayout) getActivity().findViewById(R.id.board_rel_layout);
    for(int i = 0; i < boardSize; ++i) {
      for(int j = 0; j < boardSize; ++j) {
        View newView = new View(getActivity());
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(stoneWidth, stoneWidth);
        layout.leftMargin = marginLeft + i*(PADDING + stoneWidth);
        layout.topMargin =  marginTop + j*(PADDING + stoneWidth);
        newView.setLayoutParams(layout);
        newView.setBackgroundResource(((boardSize*i + j) % 2 == 0)? STONE_BLACK : STONE_WHITE);
        container.addView(newView);
      }
    }
  }
}
