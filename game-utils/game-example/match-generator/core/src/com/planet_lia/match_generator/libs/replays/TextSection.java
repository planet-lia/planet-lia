package com.planet_lia.match_generator.libs.replays;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class TextSection implements Section {
    public String type = this.getClass().getSimpleName();
    public String entityId;
    public Attribute attribute;
    public float endTime;
    public String text;

    public TextSection(String entityId, Attribute attribute, float endTime, String text) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.endTime = round(endTime, 3);
        this.text = text;
    }
}
