package com.planet_lia.match_generator.libs.math;

public class MathHelpers {
    public static float round(float value, int numDecimals) {
        float factor = 1;
        for (int i = 0; i < numDecimals; i++) {
            factor *= 10;
        }
        return Math.round(value * factor) / factor;
    }


}
