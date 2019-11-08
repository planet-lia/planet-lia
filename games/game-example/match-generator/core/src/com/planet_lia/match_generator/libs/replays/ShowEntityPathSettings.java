package com.planet_lia.match_generator.libs.replays;

public class ShowEntityPathSettings {
    public String pathColor;
    public float pathAlpha;
    public String clickedEntityTint;
    public float pathWidth;
    public float drawingTimeInterval;

    public ShowEntityPathSettings(String pathColor,
                                  float pathAlpha,
                                  String clickedEntityTint,
                                  float pathWidth,
                                  float drawingTimeInterval) {
        this.pathColor = pathColor;
        this.pathAlpha = pathAlpha;
        this.clickedEntityTint = clickedEntityTint;
        this.pathWidth = pathWidth;
        this.drawingTimeInterval = drawingTimeInterval;
    }
}
