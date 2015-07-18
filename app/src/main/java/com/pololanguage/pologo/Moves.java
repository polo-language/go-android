package com.pololanguage.pologo;


import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Moves {
  private ArrayList<Stone> stones = new ArrayList<>();
  private Set<BoxCoords> filledCoords = new HashSet<>();
//  private int boardSize;
  Context context;

  Moves(Context context, int boardSize) {
    this.context = context;
//    this.boardSize = boardSize;
  }

  void add(Stone stone) {
    stones.add(stone);
    filledCoords.add(stone.coords);
  }

  Stone pop() {
    Stone lastStone = stones.remove(stones.size()-1);
    filledCoords.remove(lastStone);
    return lastStone;
  }

  void reset() {
    stones.clear();
    filledCoords.clear();
  }

  boolean isEmpty() {
    return filledCoords.isEmpty();
  }

  boolean contains(BoxCoords coords) {
    return filledCoords.contains(coords);
  }

  String toJson() {
    int numMoves = stones.size();
    StoredMove[] moveArray = new StoredMove[numMoves];
    Stone stone;

    for (int i = 0; i < numMoves; ++i) {
      stone = stones.get(i);
      moveArray[i] = new StoredMove(i, stone.coords.x, stone.coords.y, stone.color);
    }
    return (new Gson()).toJson(moveArray);
  }

  //////////////////////////////////
  // INNER CLASSES:
  public class Stone extends View {
    BoxCoords coords;
    StoneColor color;
    // Group group;

    Stone(BoxCoords coords, StoneColor color) {
      super(context);
      this.coords = coords;
      this.color = color;
      setBackgroundResource(color.getResource());
    }

    void move(int x, int y) {
      RelativeLayout.LayoutParams layoutParams =
          (RelativeLayout.LayoutParams) getLayoutParams();
      layoutParams.leftMargin = x;
      layoutParams.topMargin = y;
      layoutParams.rightMargin = -250;
      layoutParams.bottomMargin = -250;
      setLayoutParams(layoutParams);
    }
  }
}
