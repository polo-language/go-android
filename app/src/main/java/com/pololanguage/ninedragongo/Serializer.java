package com.pololanguage.ninedragongo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.security.Key;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Handles all de-/serialization of game state
 * Uses Gson
 */
class Serializer {
  // Stone are registered to serialize to BoxCoords only!
  private static final Type STONE_SET_TYPE = new TypeToken<Set<Stone>>(){}.getType();
  private static final Type LIBERTIES_SET_TYPE = new TypeToken<Set<BoxCoords>>(){}.getType();
  private static final Type CHAIN_LIST_TYPE = new TypeToken<List<Chain>>(){}.getType();
  private static final Type MOVE_LIST_TYPE = new TypeToken<List<Move>>(){}.getType();
  private static Gson gson;

  protected static class KEYS {
    static final String COLOR = "color";
    static final String SIZE = "size";
    static final String HISTORY = "history";
    static final String CHAINS = "chains";
    static final String STONES = "stones";
    static final String STONE = "stone";
    static final String CAPTURED = "captured";
    static final String LIBERTIES = "stones";
    static final String X = "x";
    static final String Y = "y";
  };

  static final String EXTRA_HANDICAP = "handicap";
  static final String SAVED_BOARD_FILENAME = "saved_board";
  static final String NO_SAVE_KEY = "noSave";
  static final String NO_SAVE_JSON = "{\"" + NO_SAVE_KEY + "\":true}";

  /**
   * Serializes the entire board state
   */
  public static String serializeBoard(Context context, StoneColor color, int boardSize, List<Move> history, Set<Chain> chains) {
    gson = new GsonBuilder()
        .registerTypeAdapter(Stone.class, new StoneCoordsAdapter(context))
        .registerTypeAdapter(Chain.class, new ChainSerializer())
        .registerTypeAdapter(Move.class, new MoveAdapter())
        .create();
    StringWriter stringWriter = new StringWriter();
    JsonWriter writer = new JsonWriter(stringWriter);
    try {
      writer.beginObject();
      writer.name(KEYS.COLOR).value(color.toString());
      writer.name(KEYS.SIZE).value(boardSize);
      writer.name(KEYS.HISTORY).jsonValue(gson.toJson(history, MOVE_LIST_TYPE));
      writer.name(KEYS.CHAINS).jsonValue(gson.toJson(chains, CHAIN_LIST_TYPE));
      writer.endObject();
      writer.close();
    } catch (IOException e) {
      return null;
    }
    return stringWriter.toString();
  }

  public static Set<Chain> deserializeChains(Context context, String json) {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Stone.class, new StoneCoordsAdapter(context))
        .registerTypeAdapter(Chain.class, new ChainSerializer())
        .create();
    // TODO
    return new HashSet<>();
  }

  /**
   * De-/Serializes the BoxCoords class
   */
  private static class BoxCoordsAdapter extends TypeAdapter<BoxCoords> {
    @Override
    public BoxCoords read(JsonReader reader) throws IOException {
      Integer x = null, y = null;

      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }

      reader.beginObject();
      while (reader.hasNext()) {
        switch (reader.nextName()) {
          case KEYS.X:
            x = reader.nextInt();
            break;
          case KEYS.Y:
            y = reader.nextInt();
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
      if (x == null || y == null) { /* skip coordinates missing x or y */
        return null;
      }
      return new BoxCoords(x, y);
    }

    @Override
    public void write(JsonWriter writer, BoxCoords coords) throws IOException {
      if (coords == null) {
        writer.nullValue();
        return;
      }
      writer.beginObject();
      writer.name(KEYS.X).value(coords.x);
      writer.name(KEYS.Y).value(coords.y);
      writer.endObject();
    }
  }

  /**
   * De-/Serializes the Stone class's BoxCoords only
   */
  private static class StoneCoordsAdapter extends TypeAdapter<Stone> {

    Context context;
    StoneCoordsAdapter (Context ctxt) {
      context = ctxt;
    }

    @Override
    public Stone read(JsonReader reader) throws IOException{
      /* arbitrarily return stones as black since interface forces returning a Stone */
      return new Stone(context, new BoxCoordsAdapter().read(reader), StoneColor.BLACK);
    }

    @Override
    public void write(JsonWriter writer, Stone stone) throws IOException {
      new BoxCoordsAdapter().write(writer, stone.coords);
    }

  }

  /**
   * De-/Serializes the Move class
   */
  private static class MoveAdapter extends TypeAdapter<Move> {
    @Override
    public Move read(JsonReader reader) throws IOException {
      Stone stone = null;
      Set<Stone> captured = null;

      if (reader.peek() == JsonToken.NULL) {
        reader.nextNull();
        return null;
      }
      reader.beginObject();
      while (reader.hasNext()) {
        switch (reader.nextName()) {
          case KEYS.STONE:
            stone = gson.fromJson(reader, Stone.class);
            break;
          case KEYS.CAPTURED:
            captured = gson.fromJson(reader, STONE_SET_TYPE);
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
      if (stone == null || captured == null) {
        return null;
      }
      return new Move(stone, captured);
    }

    @Override
    public void write(JsonWriter writer, Move move) throws IOException {
      if (move == null) {
        writer.nullValue();
        return;
      }
      writer.beginObject();
      writer.name(KEYS.STONE).jsonValue(gson.toJson(move.getStone(), Stone.class));
      writer.name(KEYS.CAPTURED).jsonValue(gson.toJson(move.getCaptured(), STONE_SET_TYPE));
      writer.endObject();
    }
  }

  /**
   * Serializes the Chain class
   */
  private static class ChainSerializer implements JsonSerializer<Chain> {
    @Override
    public JsonElement serialize(Chain chain, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty(KEYS.COLOR, chain.getColor().toString());
      jsonObject.add(KEYS.STONES, gson.toJsonTree(chain.getStones(), STONE_SET_TYPE));
      jsonObject.add(KEYS.LIBERTIES, gson.toJsonTree(chain.getLiberties(), LIBERTIES_SET_TYPE));
      return jsonObject;
    }
  }

  /**
   * Serializes the Stone class
   */
//  private static class StoneSerializer implements JsonSerializer<Stone> {
//    @Override
//    public JsonElement serialize(Stone stone, Type typofSrc, JsonSerializationContext context) {
//      JsonObject jsonObject = new JsonObject();
//      jsonObject.addProperty("color", stone.color.toString());
//      jsonObject.addProperty("coords", new BoxCoordsSerializer().serialize(stone.coords, BoxCoords.class, context).toString());
//      return jsonObject;
//    }
//  }

  /**
   * Serializes the Move class
   */
//  private static class MoveSerializer implements JsonSerializer<Move> {
//    @Override
//    public JsonElement serialize(Move move, Type typeOfSrc, JsonSerializationContext context) {
//      JsonObject jsonObject = new JsonObject();
//      jsonObject.add("stone", new StoneSerializer().serialize(move.getStone(), Stone.class, context));
//      jsonObject.add("captured", gson.toJsonTree(move.getCaptured(), STONE_SET_TYPE));
//      return jsonObject;
//    }
//  }

//  /**
//   * Serialize one chain
//   */
//  public static String toJson(Chain chain) {
//    return gson.toJson(chain);
//  }
//
//  /**
//   * Serialize a Set of BoxCoords
//   */
//  public static String toJson(Set<BoxCoords> coordsSet) {
//    return gson.toJson(coordsSet);
//  }
}
