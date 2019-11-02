import * as React from "react";
import {Component} from "react";
import ReactEcharts from "echarts-for-react";
import {Chart, ChartSeriesElement} from "../match-viewer/chart";
import {round} from "../match-viewer/math/round";

interface StatisticChartProps {
    stat: Chart;
    matchDuration: number;
}

interface StatisticChartState {
    time: number;
}


export class StatisticChart extends Component<StatisticChartProps, StatisticChartState> {

    render() {
        const stat = this.props.stat;

        // Create series of data
        const series: Object[] = [];
        stat.series.forEach(((s: ChartSeriesElement) => {

            let data: [number, number][] = [];
            for (let time = 0; time <= this.props.matchDuration; time += 0.1) {
                data.push([time, round(s.curve!.getValue(time), 3)]);
            }
            series.push({
                name: s.name,
                type: "line",
                itemStyle: {
                    color: s.color
                },
                lineStyle: {
                    width: 3
                },
                showSymbol: false,
                data: data
            });
        }));

        return (
            <ReactEcharts style={{minWidth: "300px"}}
                option={{
                    // title: {
                    //     top: "4%",
                    //     text: stat.name + ""
                    // },
                    tooltip: {
                        trigger: "axis"
                    },
                    legend: {
                        type: "scroll",
                        // top: "15%",
                        data: stat.series.map((s: ChartSeriesElement) => {
                            return s.name;
                        })
                    },
                    grid: {
                        left: "0%",
                        right: "0%",
                        bottom: "0%",
                        top: "12%", // top: "27%",
                        containLabel: true
                    },
                    xAxis: {
                        type: "value",
                        axisLabel: {
                            showMaxLabel: false,
                            formatter: '{value} s'
                        },
                        min: 0,
                        max: this.props.matchDuration,
                    },
                    yAxis: {
                        type: "value"
                    },
                    series: series
                }}
            />
        )
    }
}