package com.planet_lia.match_generator.libs.replays;

import static com.planet_lia.match_generator.libs.math.MathHelpers.round;

public class AttachSection implements Section {
    public String type = this.getClass().getSimpleName();
    public String entityId;
    public Attribute attribute;
    public float endTime;
    public String attachToEntityId;
    public boolean attachX;
    public boolean attachY;
    public boolean attachRotation;
    public boolean attachAngle;
    public boolean attachScale;
    public boolean attachVisibility;

    public AttachSection(String entityId,
                         Attribute attribute,
                         float endTime,
                         String attachToEntityId,
                         boolean attachX,
                         boolean attachY,
                         boolean attachRotation,
                         boolean attachAngle,
                         boolean attachScale,
                         boolean attachVisibility) {
        this.entityId = entityId;
        this.attribute = attribute;
        this.endTime = round(endTime, 3);
        this.attachToEntityId = attachToEntityId;
        this.attachX = attachX;
        this.attachY = attachY;
        this.attachRotation = attachRotation;
        this.attachAngle = attachAngle;
        this.attachScale = attachScale;
        this.attachVisibility = attachVisibility;
    }
}
