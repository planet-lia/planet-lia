package com.planet_lia.match_generator.libs.replays;

import java.util.ArrayList;

public class Chart {
    public String name;
    public ArrayList<ChartSeriesElement> series = new ArrayList<>();

    public Chart(String name) {
        this.name = name;
    }
}
