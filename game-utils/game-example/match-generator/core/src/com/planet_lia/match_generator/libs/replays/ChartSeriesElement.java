package com.planet_lia.match_generator.libs.replays;

public class ChartSeriesElement {
    public String name;
    public String color;
    public CurveRef curveRef;

    public ChartSeriesElement(String name, String color, CurveRef curveRef) {
        this.name = name;
        this.color = color;
        this.curveRef = curveRef;
    }
}
