package com.pololanguage.ninedragongo;

/**
 * Singleton serializer instance
 */
public class Serializer {
  static GsonSerializer serializer = new GsonSerializer();

  static final String EXTRA_HANDICAP = "handicap";

  static final String SAVED_BOARD_FILENAME = "saved_board";
  static final String NO_SAVE_NAME = "noSave";
  static final String NO_SAVE_JSON = "{\"" + NO_SAVE_NAME + "\":true}";
  static final String CURRENT_COLOR_NAME = "currentColor";
  static final String BOARD_SIZE_NAME = "boardSize";
  static final String BOARD_NAME = "boardFrag";
}
