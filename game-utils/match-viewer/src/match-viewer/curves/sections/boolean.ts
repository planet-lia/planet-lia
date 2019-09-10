import {errorIfUndefined, Section} from "./section";
import {round} from "../../math/round";

/**
 * A sections representing a boolean function.
 */
export class BooleanSection extends Section<boolean> {

    getValue(_time: number, prevSection: Section<boolean>): boolean {
        return prevSection.endRangeValue;
    }

    static parse(dict: any): BooleanSection {
        errorIfUndefined([dict.endTime, dict.endRangeValue]);
        return new BooleanSection(dict.endTime, dict.endRangeValue);
    }

    static toDict(entityId: string, attribute: string, endTime: number, endRangeValue: boolean): object {
        return {
            "type": BooleanSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "endRangeValue": endRangeValue
        };
    }
}
