package com.pololanguage.pologo;


import com.google.gson.Gson;
import java.util.ArrayList;

/**
 * Holds all moves made in a given game
 */
public class Moves {
  /**
   * Stores stones in the order they were placed on the board
   */
  private ArrayList<Stone> stones;

//  private int boardSize;
//  Context context;

//   Moves(Context context, int boardSize) {
//     this.context = context;
//     this.boardSize = boardSize;
//   }

  Moves() {
    stones = new ArrayList<>();
  }

  void add(Stone stone) {
    stones.add(stone);
  }

  Stone pop() {
    return stones.remove(stones.size()-1);
  }

  void reset() {
    stones.clear();
  }

  /**
   * Serializes current state of stones on the board
   * @return JSON array of StoredMove(s)
   */
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
}
