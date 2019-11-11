package com.planet_lia.match_generator.game;

import com.badlogic.gdx.graphics.Color;
import com.planet_lia.match_generator.game.entities.Owner;
import com.planet_lia.match_generator.game.hud.Hud;
import com.planet_lia.match_generator.libs.BotDetails;
import com.planet_lia.match_generator.libs.replays.*;

public class ChartManager {

    Replay replay;
    Hud hud;

    String greenBotName;
    String redBotName;

    String greenHex = GameConfig.values.general.botColors[0];
    String redHex =  GameConfig.values.general.botColors[1];
    Color green = Color.valueOf(greenHex);
    Color red =  Color.valueOf(redHex);

    String nGreenUnitsSumCurveId = "CURVE_green_units_sum";
    int nGreenUnitsSum = 0;
    String nRedUnitsSumCurveId = "CURVE_red_units_sum";
    int nRedUnitsSum = 0;

    String nGreenPlanetsCurveId = "CURVE_green_planets";
    int nGreenPlanetsCurrent = 0;
    String nRedPlanetsCurveId = "CURVE_red_planets";
    int nRedPlanetsCurrent = 0;

    String nGreenPlanetsTakenSumCurveId = "CURVE_green_planets_taken";
    int nGreenPlanetsTakenSum = 0;
    String nRedPlanetsTakenSumCurveId = "CURVE_red_planets_taken";
    int nRedPlanetsTakenSum = 0;

    String nGreenUnitsDestroyedCurveId = "CURVE_green_units_destroyed";
    int nGreenUnitsDestroyed = 0;
    String nRedUnitsDestroyedCurveId = "CURVE_red_units_destroyed";
    int nRedUnitsDestroyed = 0;


    public ChartManager(Replay replay, Hud hud, BotDetails[] botDetails) {
        this.replay = replay;
        this.hud = hud;
        this.greenBotName = botDetails[0].botName;
        this.redBotName = botDetails[1].botName;

        replay.sections.add(new StepSection(nGreenUnitsSumCurveId, EmptyAttribute.NONE, 0, nGreenUnitsSum));
        replay.sections.add(new StepSection(nRedUnitsSumCurveId, EmptyAttribute.NONE, 0, nRedUnitsSum));

        replay.sections.add(new StepSection(nGreenPlanetsCurveId, EmptyAttribute.NONE, 0, nGreenPlanetsCurrent));
        replay.sections.add(new StepSection(nRedPlanetsCurveId, EmptyAttribute.NONE, 0, nRedPlanetsCurrent));

        replay.sections.add(new StepSection(nGreenPlanetsTakenSumCurveId, EmptyAttribute.NONE, 0, nGreenPlanetsTakenSum));
        replay.sections.add(new StepSection(nRedPlanetsTakenSumCurveId, EmptyAttribute.NONE, 0, nRedPlanetsTakenSum));
    }

    public void newUnitCreated(Owner owner, float time) {
        if (owner == Owner.GREEN) {
            nGreenUnitsSum++;
            replay.sections.add(new StepSection(nGreenUnitsSumCurveId, EmptyAttribute.NONE, time, nGreenUnitsSum));
        }
        else {
            nRedUnitsSum++;
            replay.sections.add(new StepSection(nRedUnitsSumCurveId, EmptyAttribute.NONE, time, nRedUnitsSum));
        }
    }

    public void unitsDestroyed(int nUnitsDestroyed, Owner owner, float time) {
        if (owner == Owner.GREEN) {
            nGreenUnitsDestroyed += nUnitsDestroyed;
            replay.sections.add(new StepSection(nGreenUnitsDestroyedCurveId, EmptyAttribute.NONE, time, nGreenUnitsDestroyed));
        }
        else {
            nRedUnitsDestroyed += nUnitsDestroyed;
            replay.sections.add(new StepSection(nRedUnitsDestroyedCurveId, EmptyAttribute.NONE, time, nRedUnitsDestroyed));
        }
    }

    public void planetTaken(Owner newOwner, float time) {
        if (newOwner == Owner.GREEN) {
            nGreenPlanetsCurrent++;
            replay.sections.add(new StepSection(nGreenPlanetsCurveId, EmptyAttribute.NONE, time, nGreenPlanetsCurrent));
            nGreenPlanetsTakenSum++;
            replay.sections.add(new StepSection(nGreenPlanetsTakenSumCurveId, EmptyAttribute.NONE, time, nGreenPlanetsTakenSum));
        }
        else if (newOwner == Owner.RED){
            nRedPlanetsCurrent++;
            replay.sections.add(new StepSection(nRedPlanetsCurveId, EmptyAttribute.NONE, time, nRedPlanetsCurrent));
            nRedPlanetsTakenSum++;
            replay.sections.add(new StepSection(nRedPlanetsTakenSumCurveId, EmptyAttribute.NONE, time, nRedPlanetsTakenSum));
        }
    }

    public void planetLost(Owner owner, float time) {
        if (owner == Owner.GREEN) {
            nGreenPlanetsCurrent--;
            replay.sections.add(new StepSection(nGreenPlanetsCurveId, EmptyAttribute.NONE, time, nGreenPlanetsCurrent));
        }
        else {
            nRedPlanetsCurrent--;
            replay.sections.add(new StepSection(nRedPlanetsCurveId, EmptyAttribute.NONE, time, nRedPlanetsCurrent));
        }
    }

    public void addChartsToReplayFile() {
        // Units
        Chart chart = new Chart("Units");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(hud.nGreenUnitsCurveId, EmptyAttribute.NONE)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(hud.nRedUnitsCurveId, EmptyAttribute.NONE)
        ));
        replay.charts.add(chart);

        // Planets
        chart = new Chart("Planets");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(nGreenPlanetsCurveId, EmptyAttribute.NONE)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(nRedPlanetsCurveId, EmptyAttribute.NONE)
        ));
        replay.charts.add(chart);

        // Workers
        chart = new Chart("Workers");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(hud.greenWorkerTextId, TextEntityAttribute.NUMBER_TEXT)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(hud.redWorkerTextId, TextEntityAttribute.NUMBER_TEXT)
        ));
        replay.charts.add(chart);

        // Warriors
        chart = new Chart("Warriors");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(hud.greenWarriorTextId, TextEntityAttribute.NUMBER_TEXT)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(hud.redWarriorTextId, TextEntityAttribute.NUMBER_TEXT)
        ));
        replay.charts.add(chart);

        // Units destroyed
        chart = new Chart("Opponent units destroyed");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(nRedUnitsDestroyedCurveId, TextEntityAttribute.NUMBER_TEXT)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(nGreenUnitsDestroyedCurveId, TextEntityAttribute.NUMBER_TEXT)
        ));
        replay.charts.add(chart);

        // Created units
        chart = new Chart("Created Units Total");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(nGreenUnitsSumCurveId, EmptyAttribute.NONE)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(nRedUnitsSumCurveId, EmptyAttribute.NONE)
        ));
        replay.charts.add(chart);

        // Planets
        chart = new Chart("Conquered Planets Total");
        chart.series.add(new ChartSeriesElement(
                greenBotName, greenHex,
                new CurveRef(nGreenPlanetsTakenSumCurveId, EmptyAttribute.NONE)
        ));
        chart.series.add(new ChartSeriesElement(
                redBotName, redHex,
                new CurveRef(nRedPlanetsTakenSumCurveId, EmptyAttribute.NONE)
        ));
        replay.charts.add(chart);
    }
}
