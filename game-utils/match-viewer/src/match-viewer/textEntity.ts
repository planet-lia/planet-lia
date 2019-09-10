import {Curve, findEndTime, findStartTime, throwErrorIfEmpty} from "./curves/curve";
import {Text} from "pixi.js";
import {StepSection} from "./curves/sections/step";
import {Section} from "./curves/sections/section";
import {Attach} from "./attachSection";
import {Attachable} from "./attachable";
import {TextSection} from "./curves/sections/text";
import {Entity, HUD_ID_PREFIX} from "./entity";
import {BooleanSection} from "./curves/sections/boolean";
import {Assets} from "./assets";

export const TEXT_ID_PREFIX = "TEXT_";

export enum TextEntityAttribute {
    TEXT,
    NUMBER_TEXT,
    NUMBER_TEXT_DEC,
    X,
    Y,
    COLOR,
    ANCHOR_X,
    ANCHOR_Y,
    FONT_SIZE,
    FONT_FAMILY,
    FONT_STYLE,
    FONT_WEIGHT,
    ROTATION_DEG,
    OPACITY,
    VISIBILITY,
    SCALE,
    LAYER,
    ATTACH
}

export function stringify(a: TextEntityAttribute): string {
    return TextEntityAttribute[a]
}

export function isTextEntity(entityId: string): boolean {
    return entityId.startsWith(TEXT_ID_PREFIX) || entityId.startsWith(HUD_ID_PREFIX + TEXT_ID_PREFIX)
}

export class TextEntity implements Entity {
    id: string;

    textDisplay = new Text("");

    text = new Curve<string>();
    // If not empty it is used instead of text
    numberText = new Curve<number>();
    numberTextDecimals = new Curve<number>();

    x = new Curve<number>();
    y = new Curve<number>();
    color = new Curve<string>();
    anchorX = new Curve<number>();
    anchorY = new Curve<number>();
    fontSize = new Curve<number>();
    fontFamily = new Curve<string>();
    fontStyle = new Curve<string>();
    fontWeight = new Curve<string>();
    rotation = new Curve<number>();
    visibility = new Curve<boolean>();
    scale = new Curve<number>();
    // Defines to which other entity and how this
    // entity is attached (if to any).
    attach = new Curve<Attach>();
    layer = new Curve<number>();
    opacity = new Curve<number>();

    // If this entity is attached to another entity
    // it can calculate the attributes of this entity
    // based on the entity it is attached to.
    attachable: Attachable;

    startTime: number = 0;

    constructor(id: string, getEntity: (id: string) => (Attachable | undefined)) {
        this.id = id;
        this.attachable = new Attachable(this.x, this.y, this.rotation,
            this.visibility, this.scale, this.attach, getEntity);
    }

    /**
     * Call after all curves are set. This will setup all necessary
     * things such as initialize empty curves to default values.
     *
     * if the mandatory curves don't have at least one Section,
     * this method will throw an error. */
    finishSetup(_assets: Assets) {
        // Some curves should not be empty when initialized.
        // At least one of either text or numberText should not be empty.
        try {
            throwErrorIfEmpty(this.text, 'text');
        } catch (e) {
            throwErrorIfEmpty(this.numberText, 'numberText');
        }
        throwErrorIfEmpty(this.x, 'x');
        throwErrorIfEmpty(this.y, 'y');

        this.startTime = this.getStartTime();
        let t = this.startTime;

        // Set default values for empty curves.
        if (!this.numberText.isEmpty() && this.numberTextDecimals.isEmpty()) {
            this.numberTextDecimals.add(new StepSection(t, 2));
        }
        if (this.color.isEmpty()) {
            this.color.add(new TextSection(t, "#FFFFFF"));
        }
        if (this.anchorX.isEmpty()) {
            this.anchorX.add(new StepSection(t, 0.5));
        }
        if (this.anchorY.isEmpty()) {
            this.anchorY.add(new StepSection(t, 0.5));
        }
        if (this.fontSize.isEmpty()) {
            this.fontSize.add(new StepSection(t, 26));
        }
        if (this.fontFamily.isEmpty()) {
            this.fontFamily.add(new TextSection(t, "Arial"));
        }
        if (this.fontWeight.isEmpty()) {
            this.fontWeight.add(new TextSection(t, "normal"));
        }
        if (this.fontStyle.isEmpty()) {
            this.fontStyle.add(new TextSection(t, "normal"));
        }
        if (this.rotation.isEmpty()) {
            this.rotation.add(new StepSection(t, 0));
        }
        if (this.visibility.isEmpty()) {
            this.visibility.add(new BooleanSection(t, true));
        }
        if (this.scale.isEmpty()) {
            this.scale.add(new StepSection(t, 1));
        }
        if (this.layer.isEmpty()) {
            this.layer.add(new StepSection(t, 1));
        }
        if (this.opacity.isEmpty()) {
            this.opacity.add(new StepSection(this.startTime, 1));
        }
    }

    update(time: number, delta: number, assets: Assets, screenHeight: number, worldToScreen: number) {
        if (time < this.startTime) {
            this.textDisplay.visible = false;
            return;
        }

        this.textDisplay.visible = this.attachable.getVisibility(time);
        if (!this.textDisplay.visible) {
            return;
        }

        this.textDisplay.text = (this.numberText.isEmpty())
            ? this.text.getValue(time)
            : this.numberText.getValue(time).toFixed(this.numberTextDecimals.getValue(time));
        [this.textDisplay.x, this.textDisplay.y] = this.attachable.getXY(time, screenHeight, worldToScreen);
        this.textDisplay.style.fill = this.color.getValue(time);
        this.textDisplay.anchor.x = this.anchorX.getValue(time);
        this.textDisplay.anchor.y = this.anchorY.getValue(time);
        this.textDisplay.style.fontSize = this.fontSize.getValue(time);
        this.textDisplay.style.fontFamily = this.fontFamily.getValue(time);
        this.textDisplay.style.fontWeight = this.fontWeight.getValue(time);
        this.textDisplay.angle = -this.attachable.getAngle(time);
        this.textDisplay.alpha = this.opacity.getValue(time);
        this.textDisplay.zIndex = this.layer.getValue(time);
        this.textDisplay.scale.set(this.attachable.getScale(time));
    }

    addSection(attribute: string, section: Section<any>) {
        switch (attribute) {
            case stringify(TextEntityAttribute.TEXT):
                this.text.add(section);
                return;
            case stringify(TextEntityAttribute.NUMBER_TEXT):
                this.numberText.add(section);
                return;
            case stringify(TextEntityAttribute.NUMBER_TEXT_DEC):
                this.numberTextDecimals.add(section);
                return;
            case stringify(TextEntityAttribute.X):
                this.x.add(section);
                return;
            case stringify(TextEntityAttribute.Y):
                this.y.add(section);
                return;
            case stringify(TextEntityAttribute.COLOR):
                this.color.add(section);
                return;
            case stringify(TextEntityAttribute.ANCHOR_X):
                this.anchorX.add(section);
                return;
            case stringify(TextEntityAttribute.ANCHOR_Y):
                this.anchorY.add(section);
                return;
            case stringify(TextEntityAttribute.FONT_SIZE):
                this.fontSize.add(section);
                return;
            case stringify(TextEntityAttribute.FONT_FAMILY):
                this.fontFamily.add(section);
                return;
            case stringify(TextEntityAttribute.FONT_STYLE):
                this.fontStyle.add(section);
                return;
            case stringify(TextEntityAttribute.FONT_WEIGHT):
                this.fontWeight.add(section);
                return;
            case stringify(TextEntityAttribute.ROTATION_DEG):
                this.rotation.add(section);
                return;
            case stringify(TextEntityAttribute.OPACITY):
                this.opacity.add(section);
                return;
            case stringify(TextEntityAttribute.VISIBILITY):
                this.visibility.add(section);
                return;
            case stringify(TextEntityAttribute.SCALE):
                this.scale.add(section);
                return;
            case stringify(TextEntityAttribute.LAYER):
                this.layer.add(section);
                return;
            case stringify(TextEntityAttribute.ATTACH):
                this.attach.add(section);
                return;
        }
    }

    getCurve(attribute: string): Curve<any> {
        switch (attribute) {
            case stringify(TextEntityAttribute.TEXT):
                return this.text;
            case stringify(TextEntityAttribute.NUMBER_TEXT):
                return this.numberText;
            case stringify(TextEntityAttribute.NUMBER_TEXT_DEC):
                return this.numberTextDecimals;
            case stringify(TextEntityAttribute.X):
                return this.x;
            case stringify(TextEntityAttribute.Y):
                return this.y;
            case stringify(TextEntityAttribute.COLOR):
                return this.color;
            case stringify(TextEntityAttribute.ANCHOR_X):
                return this.anchorX;
            case stringify(TextEntityAttribute.ANCHOR_Y):
                return this.anchorY;
            case stringify(TextEntityAttribute.FONT_SIZE):
                return this.fontSize;
            case stringify(TextEntityAttribute.FONT_FAMILY):
                return this.fontFamily;
            case stringify(TextEntityAttribute.FONT_STYLE):
                return this.fontStyle;
            case stringify(TextEntityAttribute.FONT_WEIGHT):
                return this.fontWeight;
            case stringify(TextEntityAttribute.ROTATION_DEG):
                return this.rotation;
            case stringify(TextEntityAttribute.OPACITY):
                return this.opacity;
            case stringify(TextEntityAttribute.VISIBILITY):
                return this.visibility;
            case stringify(TextEntityAttribute.SCALE):
                return this.scale;
            case stringify(TextEntityAttribute.LAYER):
                return this.layer;
            case stringify(TextEntityAttribute.ATTACH):
                return this.attach;
            default:
                throw Error(`attribute ${attribute} is not valid`)
        }
    }

    private getAttributes(): Curve<any>[] {
        return [this.text, this.x, this.y, this.color, this.anchorX, this.numberText, this.numberTextDecimals,
            this.anchorY, this.fontSize, this.fontFamily, this.fontStyle, this.fontWeight, this.rotation,
            this.visibility, this.scale, this.attach, this.layer, this.opacity];
    }

    getEndTime(): number {
        return findEndTime(this.getAttributes());
    }

    getStartTime(): number {
        return findStartTime(this.getAttributes());
    }
}