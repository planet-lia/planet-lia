import {Curve} from "./curves/curve";
import {Attach} from "./attachSection";
import {normalizeAngleRad, toRadians} from "./math/circle";

/**
 * Given that an instance of this class holds curves
 * of an entity that is attached to another entity it
 * can calculate specified attributes of an entity based
 * on the attributes of the entity it is attached to.
 */
export class Attachable {

    x: Curve<number>;
    y: Curve<number>;
    rotation: Curve<number>;
    visibility: Curve<boolean>;
    scale: Curve<number>;
    // Defines to which other entity and how this
    // entity is attached (if to any).
    attach: Curve<Attach>;
    // Creator of this entity instance can define
    // how this instance can access other entities
    // in the replay using their id
    getEntity: (id: string) => (Attachable | undefined);

    constructor(x: Curve<number>,
                y: Curve<number>,
                rotation: Curve<number>,
                visibility: Curve<boolean>,
                scale: Curve<number>,
                attach: Curve<Attach>,
                getEntity: (id: string) => (Attachable | undefined)) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.visibility = visibility;
        this.scale = scale;
        this.attach = attach;
        this.getEntity = getEntity;
    }

    // Returns [x,y] taking into account the base entity it is attached to.
    getXY(time: number, screenHeight: number, worldToScreen: number): [number, number] {
        // Get a scale as inherited from the entity it is attached to but
        // ignoring its own scale (we don't want it to affects its position.
        let scale = this.getScale(time) / this.scale.getValue(time);

        let x = this.x.getValue(time) * worldToScreen * scale;
        let y = screenHeight - this.y.getValue(time) * worldToScreen * scale;

        // If x and/or y coordinate is attached to other entity, add
        // its x and/or y coordinate to this entity.
        // If attachRotation is set to true then also modify x and/or y
        // coordinate to rotate around the entity it is attached to.
        if (!this.attach.isEmpty()) {
            let attach = this.attach.getValue(time);
            let baseEntity = this.getEntity(attach.attachToEntityId)!;

            if (attach.attachRotation) {
                // First calculate x and y positions in a local
                // coordinate system of this entity disregarding
                // where baseEntity is.
                x = this.x.getValue(time) * scale;
                y = this.y.getValue(time) * scale;

                // Subtract 90 degrees to attached entity angle so that
                // the x axis of local coordinate system faces the same
                // way the base entity is facing.
                let attachedEntityAngle = normalizeAngleRad(Math.atan2(x, y) - toRadians(90));

                let baseEntityAngle = normalizeAngleRad(toRadians(baseEntity.getRotationDeg(time)));
                let angle = baseEntityAngle - attachedEntityAngle;

                // Turn x and y around based on the angle
                let xTmp = x;
                let yTmp = y;
                x = xTmp * Math.cos(angle) - yTmp * Math.sin(angle);
                y = yTmp * Math.cos(angle) + xTmp * Math.sin(angle);

                // Convert world coordinates to screen coordinates
                x *= worldToScreen;
                y = screenHeight - y * worldToScreen;
            }

            let [baseEntityX, baseEntityY] = baseEntity.getXY(time, screenHeight, worldToScreen);

            if (attach.attachX) {
                x += baseEntityX
            }
            if (attach.attachY) {
                y += baseEntityY - screenHeight;
            }
        }

        return [x, y];
    }

    // Returns true if the entity is visible taking into account its
    // base entity if attachVisibility is set to true. If base entity
    // is not visible, then this entity is also not visible.
    getVisibility(time: number): boolean {
        let isVisible = this.visibility.getValue(time);

        if (!this.attach.isEmpty()) {
            let attach = this.attach.getValue(time);

            if (attach.attachVisibility) {
                let entityToAttachTo = this.getEntity(attach.attachToEntityId)!;
                let isBaseVisible = entityToAttachTo.getVisibility(time);
                return isVisible && isBaseVisible;
            }
        }

        return isVisible;
    }

    // Get a scale of this object taking into account the scale
    // of the entity it is attached to if attachScale is true.
    getScale(time: number): number {
        let scale = this.scale.getValue(time);

        if (!this.attach.isEmpty()) {
            let attach = this.attach.getValue(time);

            if (attach.attachScale) {
                let entityToAttachTo = this.getEntity(attach.attachToEntityId)!;
                let baseScale = entityToAttachTo.getScale(time);
                scale *= baseScale;
            }
        }

        return scale;
    }

    // Get the angle of this entity taking into account all consecutive entities
    // it is attached to (it is attached to one, that is attached to another one etc.)
    // that have attachAngle field set to true.
    getAngle(time: number): number {
        let rotation = this.rotation.getValue(time);

        // If the attachAngle is allowed then this entity
        // will change its angle together with the entity it
        // is attached to
        if (!this.attach.isEmpty()) {
            let attach = this.attach.getValue(time);

            if (attach.attachAngle) {
                let entityToAttachTo = this.getEntity(attach.attachToEntityId)!;
                let baseRotation = entityToAttachTo.getAngle(time);
                rotation += baseRotation;
            }
        }

        return rotation
    }

    // Get the angle of this entity taking into account all consecutive entities
    // it is attached to (it is attached to one, that is attached to another one etc.)
    // that have attachRotation field set to true.
    getRotationDeg(time: number): number {
        let rotation = this.rotation.getValue(time);

        if (!this.attach.isEmpty()) {
            let attach = this.attach.getValue(time);

            if (attach.attachRotation) {
                let entityToAttachTo = this.getEntity(attach.attachToEntityId)!;
                let baseRotation = entityToAttachTo.getRotationDeg(time);
                rotation += baseRotation;
            }
        }

        return rotation
    }
}