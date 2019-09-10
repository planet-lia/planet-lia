import {Section} from "./curves/sections/section";
import {Attachable} from "./attachable";
import {Curve} from "./curves/curve";
import {Assets} from "./assets";

export const HUD_ID_PREFIX = "HUD_";

export function isOnHUD(entityId: string): boolean {
    return entityId.startsWith(HUD_ID_PREFIX)
}

export interface Entity {

    id: string;

    attachable: Attachable;

    // After all sections are loaded finish
    // with setting things up
    finishSetup(assets: Assets): void;

    // Update the entity attributes based
    // on provided time
    update(time: number, delta: number, assets: Assets, screenHeight: number, worldToScreen: number);

    // Add a new section to a specified attribute.
    addSection(attribute: string, section: Section<any>);

    // Finds a curve based on the attribute path
    getCurve(attribute: string): Curve<any>;

    // Get the last section time of all the
    // sections in this entity
    getEndTime(): number;

    // Get the first section time of all the
    // sections in this entity
    getStartTime(): number;

}