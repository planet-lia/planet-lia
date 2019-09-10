import {errorIfUndefined, Section} from "./section";
import {round} from "../../math/round";

/**
 * A sections representing a simple textDisplay function,
 * where the values are strings.
 */
export class TextSection extends Section<string> {

    getValue(_time: number, prevSection: Section<string>): string {
        return prevSection.endRangeValue;
    }

    static parse(dict: any): TextSection {
        errorIfUndefined([dict.endTime, dict.text]);
        return new TextSection(dict.endTime, dict.text);
    }

    static toDict(entityId: string, attribute: string, endTime: number, text: string): object {
        return {
            "type": TextSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "text": text
        };
    }
}