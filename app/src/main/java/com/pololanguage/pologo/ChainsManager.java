package com.pololanguage.pologo;

/**
 * Manages creation, updating, and removal of chains of like-colored stones
 */
public class ChainsManager {
  private Set<Chain> chains;

  ChainsManager() {
    chains = new HashSet<>();
  }

  /**
   * Captures chains of opposing color neighboring the given stone
   */
  static void capture(Stone stone) {}

  /**
   * Sets the stone's chain field and creates/updates the appropriate chain
   */
  static Chain setNewChain(Stone stone) {}
}

