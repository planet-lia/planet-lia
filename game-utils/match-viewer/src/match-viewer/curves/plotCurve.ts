import * as echarts from "echarts";
import {Curve} from "./curve";

export class PlotOptions {
    startTime: number | undefined = undefined;
    endTime: number | undefined = undefined;
    width = 580;
    height = 420;
}


/** Plots a curve of numbers on a chart in the div with specified id using ECharts library. */
export function plotNumberCurve(curve: Curve<number>, chartName: string, parentDiv: string,
                                options = new PlotOptions()) {
    let startTime = options.startTime;
    let endTime = options.endTime;
    let height = options.height;
    let width = options.width;

    if (startTime == undefined) {
        startTime = curve.getStartTime();
    }
    if (endTime == undefined) {
        endTime = curve.getEndTime();
    }
    const timeBetweenPoints = 0.01;

    let pointSize = 5;

    // Generate points on the graph
    let data: Array<Array<number>> = [];

    // If there is only one sections then only make a single point on the chart.
    if (curve.numberOfSections() == 1) {
        data.push([startTime, curve.getValue(startTime)]);
        // If the graph consists of only one point, make it bigger.
        pointSize = 15;
    } else {
        for (let time = startTime; time <= endTime; time += timeBetweenPoints) {
            data.push([time, curve.getValue(time)]);
        }
    }

    let chartDiv = createChartDiv(parentDiv, width, height);

    // Add chart to div and draw it
    let myChart = echarts.init(chartDiv);
    myChart.setOption({
        title: {
            left: 'center',
            text: chartName + ` (${curve.numberOfSections()})`
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}s'
            },
            min: startTime,
            max: endTime
        },
        yAxis: {},
        series: [{
            symbolSize: pointSize,
            data: data,
            type: 'scatter',
        }]
    });
}

/** Plots the curve of strings on a chart in the div with specified id using ECharts library. */
export function plotStringCurve(curve: Curve<string>, chartName: string, parentDiv: string,
                                options = new PlotOptions()) {
    let startTime = options.startTime;
    let endTime = options.endTime;
    let height = options.height;
    let width = options.width;

    if (startTime == undefined) {
        startTime = curve.getStartTime();
    }
    if (endTime == undefined) {
        endTime = curve.getEndTime();
    }

    let sectionEndTimes = curve.getSectionsEndTimes();

    // Generate points on the graph
    let data: Array<Array<number>> = [];
    for (let time of sectionEndTimes) {
        data.push([time, 1]);
    }

    let chartDiv = createChartDiv(parentDiv, width, height);

    // Add chart to div and draw it
    let myChart = echarts.init(chartDiv);
    myChart.setOption({
        title: {
            left: 'center',
            text: chartName + ` (${curve.numberOfSections()})`
        },
        xAxis: {
            type: 'value',
            axisLabel: {
                formatter: '{value}s'
            },
            min: startTime,
            max: endTime
        },
        yAxis: {},
        series: [{
            symbolSize: 10,
            data: data,
            type: 'scatter',
            label: {
                normal: {
                    show: true,
                    position: 'inside',
                    formatter: function (data: any) {
                        let v = data.value;
                        return curve.getValue(v[0]);
                    },
                    color: "#000",
                    fontWeight: "bold",
                    offset: [0, -15],
                }
            },
        }]
    });
}

function createChartDiv(parentDiv: string, width: number, height: number): HTMLDivElement {
    let chartDiv = document.createElement('div');
    chartDiv.setAttribute("style", `width:${width}px; height:${height}px; flex-shrink: 0;`);
    (document.getElementById(parentDiv) as HTMLInputElement).appendChild(chartDiv);
    return chartDiv;
}