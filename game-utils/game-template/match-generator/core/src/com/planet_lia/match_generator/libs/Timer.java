package com.planet_lia.match_generator.libs;

/**
 * Timer holds time in seconds.
 */
public class Timer {
    public float time = 0;

    public float getTime(int numDecimals) {
        float factor = (int) Math.pow(10, numDecimals);
        return Math.round(this.time * factor) / factor;
    }
}
