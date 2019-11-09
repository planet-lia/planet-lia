package com.planet_lia.match_generator.libs.replays;

public class CurveRef {
    public String entityId;
    public Attribute attribute;

    public CurveRef(String entityId, Attribute attribute) {
        this.entityId = entityId;
        this.attribute = attribute;
    }
}
