import {errorIfUndefined} from "./curves/sections/section";
import {Curve} from "./curves/curve";


export class Chart {

    name: string;
    series: ChartSeriesElement[];

    constructor(name: string, series: ChartSeriesElement[]) {
        this.name = name;
        this.series = series;
    }

    static parse(dict: any): Chart {
        errorIfUndefined([dict.name, dict.series]);
        return new Chart(dict.name, dict.series);
    }
}

export class ChartSeriesElement {
    name: string;
    color: string;
    curveRef: CurveRef;
    curve: Curve<number> | null = null;

    constructor(name: string, color: string, curveRef: CurveRef) {
        this.name = name;
        this.color = color;
        this.curveRef = curveRef;
    }

    static parse(dict: any): ChartSeriesElement {
        errorIfUndefined([dict.color, dict.name, dict.curveRef]);
        return new ChartSeriesElement(dict.name, dict.color, dict.curveRef);
    }
}

export class CurveRef {
    entityId: string;
    attribute: string;

    constructor(entityId: string, attribute: string) {
        this.entityId = entityId;
        this.attribute = attribute;
    }

    static parse(dict: any): CurveRef {
        errorIfUndefined([dict.entityId, dict.attribute]);
        return new CurveRef(dict.entityId, dict.attribute);
    }
}