import {errorIfUndefined, Section} from "./section";
import {round} from "../../math/round";

/**
 * A sections representing a simple linear function.
 */
export class LinearSection extends Section<number> {

    getValue(time: number, prevSection: Section<number>): number {
        let sectionDuration = this.endTime - prevSection.endTime;
        let elapsedDuration = time - prevSection.endTime;

        let subsectionValue = (this.endRangeValue - prevSection.endRangeValue) * (elapsedDuration / sectionDuration);

        return prevSection.endRangeValue + subsectionValue;
    }

    static parse(dict: any): LinearSection {
        errorIfUndefined([dict.endTime, dict.endRangeValue]);
        return new LinearSection(dict.endTime, dict.endRangeValue);
    }

    static toDict(entityId: string, attribute: string, endTime: number, endRangeValue: number): object {
        return {
            "type": LinearSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "endRangeValue": round(endRangeValue, 3)
        };
    }
}