package com.pololanguage.ninedragongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * Handles all de-/serialization of game state
 * Uses Gson
 */
class Serializer {
  private Gson gson;
  private Type stoneSetType = new TypeToken<Set<Stone>>(){}.getType();
  private Type libertiesSetType = new TypeToken<Set<BoxCoords>>(){}.getType();
  private Type chainListType = new TypeToken<List<Chain>>(){}.getType();
  private Type moveListType = new TypeToken<List<Move>>(){}.getType();

  Serializer() {
    // Stone are registered to serialize to BoxCoords only!
    gson = new GsonBuilder().registerTypeAdapter(Stone.class, new StoneCoordsSerializer())
                            .registerTypeAdapter(BoxCoords.class, new BoxCoordsSerializer())
                            .registerTypeAdapter(Chain.class, new ChainSerializer())
                            .create();
  }

  /**
   * Serializes the entire board state
   */
  public String serializeBoard(StoneColor color, int boardSize, History<Move> history, Set<Chain> chains) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("color", color.toString());
    jsonObject.addProperty("boardSize", boardSize);
    jsonObject.add("history", gson.toJsonTree(history, moveListType));
    jsonObject.add("chains", gson.toJsonTree(chains, chainListType));
    return jsonObject.toString();
  }

  /**
   * Serialize one chain
   */
  public String toJson(Chain chain) {
    return gson.toJson(chain);
  }

  /**
   * Serializes the BoxCoords class
   */
  private class BoxCoordsSerializer implements JsonSerializer<BoxCoords> {
    @Override
    public JsonElement serialize(BoxCoords coords, Type typofSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", coords.x);
      jsonObject.addProperty("y", coords.y);
      return jsonObject;
    }
  }

  /**
   * Serializes the Stone class
   */
  private class StoneSerializer implements JsonSerializer<Stone> {
    @Override
    public JsonElement serialize(Stone stone, Type typofSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("color", stone.color.toString());
      jsonObject.addProperty("coords", new BoxCoordsSerializer().serialize(stone.coords, BoxCoords.class, context).toString());
      return jsonObject;
    }
  }

  /**
   * Serializes the Stone class's BoxCoords only
   */
  private class StoneCoordsSerializer implements JsonSerializer<Stone> {
    @Override
    public JsonElement serialize(Stone stone, Type typofSrc, JsonSerializationContext context) {
      return new BoxCoordsSerializer().serialize(stone.coords, BoxCoords.class, context);
    }
  }

  /**
   * Serializes the Move class
   */
  private class MoveSerializer implements JsonSerializer<Move> {
    @Override
    public JsonElement serialize(Move move, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("stone", new StoneSerializer().serialize(move.getStone(), Stone.class, context));
      jsonObject.add("captured", gson.toJsonTree(move.getCaptured(), stoneSetType));
      return jsonObject;
    }
  }

  /**
   * Serializes the Chain class
   */
  private class ChainSerializer implements JsonSerializer<Chain> {
    @Override
    public JsonElement serialize(Chain chain, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("color", chain.getColor().toString());
      jsonObject.add("stones", gson.toJsonTree(chain.getStones(), stoneSetType));
      jsonObject.add("liberties", gson.toJsonTree(chain.getLiberties(), libertiesSetType));
      return jsonObject;
    }
  }

//  /**
//   * Serializes the History class
//   */
//  private class HistorySerializer implements JsonSerializer<Move> {
//    @Override
//    public JsonElement serialize(Move , Type typeOfSrc, JsonSerializationContext context) {
//      JsonObject jsonObject = new JsonObject();
//
//      jsonObject.addProperty("color", chain.getColor().toString());
//      jsonObject.add("stones", gson.toJsonTree(chain.getStones(), stoneSetType));
//      jsonObject.add("liberties", gson.toJsonTree(chain.getLiberties(), libertiesSetType));
//
//      return jsonObject;
//    }
//  }
}
