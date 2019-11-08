package com.planet_lia.match_generator.libs.replays;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class BooleanSection implements Section {
    public String type = this.getClass().getSimpleName();
    public String entityId;
    public Attribute attribute;
    public float endTime;
    public boolean endRangeValue;

    public BooleanSection(String entityId, Attribute attribute, float endTime, boolean endRangeValue) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.endTime = round(endTime, 3);
        this.endRangeValue = endRangeValue;
    }
}
