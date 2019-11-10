package com.planet_lia.match_generator.libs;

/**
 * Timer holds time in seconds.
 */
public class Timer {
    private double time = 0;
    private float numDecimals = 3;

    public float getTime() {
        float factor = (int) Math.pow(10, numDecimals);
        return Math.round(this.time * factor) / factor;
    }

    public float getTime(int numDecimals) {
        float factor = (int) Math.pow(10, numDecimals);
        return Math.round(this.time * factor) / factor;
    }

    /**
     * @return raw time without cutting off decimals
     */
    public double getTimeRaw() {
        return time;
    }

    public void add(double delta) {
        time += delta;
    }

    public void set(double time) {
        this.time = time;
    }

    public void setNumDecimals(int numDecimals) {
        this.numDecimals = numDecimals;
    }
}
