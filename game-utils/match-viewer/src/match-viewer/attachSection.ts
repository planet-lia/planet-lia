// NOTE: This curves section is not in curves library as it is specific
//       to this replay viewer.


import {errorIfUndefined, Section} from "./curves/sections/section";
import {round} from "./math/round";

/**
 * A sections representing an attach attribute which
 * makes one entity 'attach' to another.
 */
export class AttachSection extends Section<Attach> {

    static readonly NAME: string = "AttachSection";

    getValue(_time: number, prevSection: Section<Attach>): Attach {
        return prevSection.endRangeValue;
    }

    static parse(dict: any): AttachSection {
        errorIfUndefined([dict.endTime, dict.entityId, dict.attachToEntityId, dict.attachX, dict.attachY,
            dict.attachRotation, dict.attachAngle, dict.attachScale, dict.attachVisibility]);

        let attach = new Attach(dict.attachToEntityId, dict.attachX, dict.attachY, dict.attachRotation,
            dict.attachAngle, dict.attachScale, dict.attachVisibility);
        return new AttachSection(dict.endTime, attach);
    }

    static toDict(entityId: string, attribute: string, endTime: number, attach: Attach): object {
        return {
            "type": AttachSection.name,
            "entityId": entityId,
            "attribute": attribute,
            "endTime": round(endTime, 3),
            "attachToEntityId": attach.attachToEntityId,
            "attachX": attach.attachX,
            "attachY": attach.attachY,
            "attachRotation": attach.attachRotation,
            "attachAngle": attach.attachAngle,
            "attachScale": attach.attachScale,
            "attachVisibility": attach.attachVisibility
        };
    }
}

export class Attach {
    // TextureEntity to which to attach
    attachToEntityId: string;

    // Define which attributes to attach
    attachX: boolean;
    attachY: boolean;
    attachRotation: boolean;
    attachAngle: boolean;
    attachScale: boolean;
    attachVisibility: boolean;

    constructor(entityId: string, attachX: boolean, attachY: boolean, attachRotation: boolean,
                attachAngle: boolean, attachScale: boolean, attachVisibility: boolean) {
        this.attachToEntityId = entityId;
        this.attachX = attachX;
        this.attachY = attachY;
        this.attachRotation = attachRotation;
        this.attachAngle = attachAngle;
        this.attachScale = attachScale;
        this.attachVisibility = attachVisibility;
    }
}