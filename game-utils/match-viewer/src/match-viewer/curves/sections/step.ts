import {errorIfUndefined, Section} from "./section";
import {round} from "../../math/round";

/**
 * A sections representing a simple step function,
 * where the value in one sections is always the same.
 */
export class StepSection extends Section<number> {

    getValue(_time: number, prevSection: Section<number>): number {
        return prevSection.endRangeValue;
    }

    static parse(dict: any): StepSection {
        errorIfUndefined([dict.endTime, dict.endRangeValue]);
        return new StepSection(dict.endTime, dict.endRangeValue);
    }

    static toDict(entityId: string, attribute: string, endTime: number, endRangeValue: number): object {
        return {
            "type": StepSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "endRangeValue": round(endRangeValue, 3)
        };
    }
}
