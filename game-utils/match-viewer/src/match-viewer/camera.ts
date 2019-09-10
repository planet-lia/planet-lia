import {Curve, findEndTime, findStartTime, throwErrorIfEmpty} from "./curves/curve";
import {StepSection} from "./curves/sections/step";
import {Section} from "./curves/sections/section";
import {Container} from "pixi.js";

export const CAMERA_ID_PREFIX = "CAMERA_";
export const CAMERA_MANUAL_ID = "CAMERA_MANUAL";
export const CAMERA_HUD_ID = "CAMERA_HUD";

export enum CameraAttribute {
    X,
    Y,
    ROTATION_DEG,
    ZOOM,
}

export function stringify(a: CameraAttribute): string {
    return CameraAttribute[a]
}

export function isCamera(entityId: string): boolean {
    return entityId.startsWith(CAMERA_ID_PREFIX)
}

export class Camera {
    id: string;
    width: number;
    height: number;

    x = new Curve<number>();
    y = new Curve<number>();
    rotation = new Curve<number>();
    zoom = new Curve<number>();


    startTime: number = 0;

    constructor(id: string, width: number, height: number) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    /**
     * Call after all curves are set. This will setup all necessary
     * things such as initialize empty curves to default values.
     *
     * if the mandatory curves don't have at least one Section,
     * this method will throw an error. */
    finishSetup() {
        // Some curves should not be empty when initialized.
        throwErrorIfEmpty(this.x, 'x');
        throwErrorIfEmpty(this.y, 'y');

        this.startTime = this.getStartTime();

        // Set default values for empty curves.
        if (this.rotation.isEmpty()) {
            this.rotation.add(new StepSection(this.startTime, 0));
        }
        if (this.zoom.isEmpty()) {
            this.zoom.add(new StepSection(this.startTime, 1));
        }
    }

    update(time: number, viewport: Container, screenHeight: number, worldToScreen: number) {
        let x = this.x.getValue(time) * worldToScreen;
        let y = screenHeight - this.y.getValue(time) * worldToScreen;
        let width = this.width * worldToScreen;
        let height = this.height * worldToScreen;
        viewport.position.set(width / 2, height / 2);
        viewport.pivot.set(x, y);
        viewport.angle = this.rotation.getValue(time);
        viewport.scale.set(this.zoom.getValue(time));
    }

    addSection(attribute: string, section: Section<any>) {
        switch (attribute) {
            case stringify(CameraAttribute.X):
                this.x.add(section);
                return;
            case stringify(CameraAttribute.Y):
                this.y.add(section);
                return;
            case stringify(CameraAttribute.ROTATION_DEG):
                this.rotation.add(section);
                return;
            case stringify(CameraAttribute.ZOOM):
                this.zoom.add(section);
                return;
        }
    }

    private getAttributes(): Curve<any>[] {
        return [this.x, this.y, this.rotation, this.zoom];
    }

    getEndTime(): number {
        return findEndTime(this.getAttributes());
    }

    getStartTime(): number {
        return findStartTime(this.getAttributes());
    }
}

export function createCenteredCamera(id: string, width: number, height: number): Camera {
    let camera = new Camera(id, width, height);
    camera.addSection(stringify(CameraAttribute.X), new StepSection(0, width / 2));
    camera.addSection(stringify(CameraAttribute.Y), new StepSection(0, height / 2));
    camera.finishSetup();
    return camera;
}