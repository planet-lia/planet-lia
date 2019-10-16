package com.planet_lia.match_generator.libs.replays;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class StepSection implements Section {
    public String type = this.getClass().getSimpleName();
    public String entityId;
    public Attribute attribute;
    public float endTime;
    public float endRangeValue;

    public StepSection(String entityId, Attribute attribute, float endTime, float endRangeValue) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.endTime = round(endTime, 3);
        this.endRangeValue = round(endRangeValue, 3);
    }
}
