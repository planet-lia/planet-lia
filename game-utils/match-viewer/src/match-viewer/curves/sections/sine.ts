/**
 * A sections representing a sine function.
 */
import {errorIfUndefined, Section} from "./section";
import {round} from "../../math/round";
import {SineEquation} from "../../math/sineEquation";

export class SineSection extends Section<number> {

    static readonly NAME: string = "SineSection";

    sine: SineEquation;

    constructor(endTime: number, sine: SineEquation) {
        super(endTime, sine.getValue(endTime));

        this.sine = sine;
    }

    getValue(time: number, _prevSection: Section<number>): number {
        return this.sine.getValue(time);
    }

    static parse(dict: any): SineSection {
        errorIfUndefined([dict.endTime, dict.t0, dict.A, dict.r, dict.B, dict.C]);

        let sine = new SineEquation(dict.t0, dict.A, dict.r, dict.B, dict.C);
        return new SineSection(dict.endTime, sine);
    }

    static toDict(entityId: string, attribute: string, endTime: number, sine: SineEquation): object {
        return {
            "type": SineSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "t0": round(sine.t0, 3),
            "A": round(sine.A, 3),
            "r": round(sine.r, 3),
            "B": round(sine.B, 3),
            "C": round(sine.C, 3),
        };
    }
}