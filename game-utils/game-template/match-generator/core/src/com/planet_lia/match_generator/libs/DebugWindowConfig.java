package com.planet_lia.match_generator.libs;

public class DebugWindowConfig {
    /**
     * What should be the ratio between game part of
     * the debug window and log part
     */
    public float logsToGameRatio;

    /**
     * @param height of the screen
     * @return width of the game view section
     */
    public int getGameViewWidth(int height) {
        return (int) (height * (16f / 9));
    }

    /**
     * @param height of the screen
     * @return width of the log view section
     */
    public int getLogsViewWidth(int height) {
        return (int) (getGameViewWidth(height) * logsToGameRatio);
    }

    /**
     * @param height of the screen
     * @return width of the full debug window
     */
    public int getWindowWidth(int height) {
        return getGameViewWidth(height) + getLogsViewWidth(height);
    }
}
