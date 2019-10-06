package com.planet_lia.match_generator.libs;

public class DebugWindowConfig {

    /**
     * The FPS of the debug window
     */
    public float framesPerSecond;

    /**
     * The ratio between game part of the debug
     * window and log part
     */
    public float logsToGameRatio;

    /**
     * The ratio between the height of the controls
     * and the window height
     */
    public float controlsToWindowHeightRatio;

    /**
     * @param windowHeight of the screen
     * @return width of the game view section
     */
    public int getGameViewWidth(int windowHeight) {
        return (int) (getGameViewHeight(windowHeight) * (16f / 9));
    }

    public int getGameViewHeight(int windowHeight) {
        return windowHeight - getControlsViewHeight(windowHeight);
    }

    public int getControlsViewHeight(int windowHeight) {
        return (int) (windowHeight * controlsToWindowHeightRatio);
    }

    /**
     * @param windowHeight height of the screen
     * @return width of the log view section
     */
    public int getLogsViewWidth(int windowHeight) {
        return (int) (getGameViewWidth(windowHeight) * logsToGameRatio);
    }

    /**
     * @param windowHeight height of the screen
     * @return width of the full debug window
     */
    public int getWindowWidth(int windowHeight) {
        return getGameViewWidth(windowHeight) + getLogsViewWidth(windowHeight);
    }
}
