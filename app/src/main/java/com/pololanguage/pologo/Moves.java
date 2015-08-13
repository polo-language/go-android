package com.pololanguage.pologo;


import com.google.gson.Gson;
import java.util.ArrayList;

/** Stores stones in the order they were placed on the board */
public class Moves extends ArrayList<Stone> {
  /** Removes and returns the last element */
  public Stone pop() {
    return remove(size()-1);
  }

  /**
   * Serializes current state of stones on the board
   * @return JSON array of StoredMove(s)
   */
  public String toJson() {
    int numMoves = size();
    StoredMove[] moveArray = new StoredMove[numMoves];
    Stone stone;

    for (int i = 0; i < numMoves; ++i) {
      stone = get(i);
      moveArray[i] = new StoredMove(i, stone.coords.x, stone.coords.y, stone.color);
    }
    return (new Gson()).toJson(moveArray);
  }
}
