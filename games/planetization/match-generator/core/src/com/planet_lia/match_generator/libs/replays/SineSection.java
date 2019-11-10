package com.planet_lia.match_generator.libs.replays;

import com.planet_lia.match_generator.libs.math.SineEquation;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class SineSection implements Section {
    public String type = this.getClass().getSimpleName();
    public String entityId;
    public Attribute attribute;
    public float endTime;
    public float t0;
    public float A;
    public float r;
    public float B;
    public float C;

    public SineSection(String entityId, Attribute attribute, float endTime, SineEquation sine) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.endTime = round(endTime, 3);
        this.t0 = round(sine.t0, 3);
        this.A = round(sine.A, 3);
        this.r = round(sine.r, 3);
        this.B = round(sine.B, 3);
        this.C = round(sine.C, 3);
    }
}
