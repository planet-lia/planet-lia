import {Curve, findEndTime, findStartTime, throwErrorIfEmpty} from "./curves/curve";
import {Sprite, Texture, Graphics} from "pixi.js";
import {StepSection} from "./curves/sections/step";
import {Section} from "./curves/sections/section";
import {Attach} from "./attachSection";
import {Attachable} from "./attachable";
import {Entity} from "./entity";
import {ShowEntityPathSettings} from "./gameDetails";
import {isTextureAnimation, TextureAnimation} from "./textureAnimation";
import {BooleanSection} from "./curves/sections/boolean";
import {Assets} from "./assets";

export enum TextureEntityAttribute {
    X,
    Y,
    WIDTH,
    HEIGHT,
    ROTATION_DEG,
    LAYER,
    TEXTURE,
    OPACITY,
    VISIBILITY,
    SCALE,
    ATTACH
}

export function stringify(a: TextureEntityAttribute): string {
    return TextureEntityAttribute[a]
}

export class TextureEntity implements Entity {
    id: string;

    sprite = new Sprite();

    x = new Curve<number>();
    y = new Curve<number>();
    rotation = new Curve<number>();
    visibility = new Curve<boolean>();
    scale = new Curve<number>();
    // Defines to which other entity and how this
    // entity is attached (if to any).
    attach = new Curve<Attach>();
    width = new Curve<number>();
    height = new Curve<number>();
    layer = new Curve<number>();
    texture = new Curve<string>();
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
        throwErrorIfEmpty(this.x, 'x');
        throwErrorIfEmpty(this.y, 'y');
        throwErrorIfEmpty(this.width, 'width');
        throwErrorIfEmpty(this.texture, 'texture');

        this.startTime = this.getStartTime();
        let t = this.startTime;

        // Set default values for empty curves.
        if (this.height.isEmpty()) {
            // Set height to match the width.
            this.height.add(new StepSection(t, this.width.getValue(this.width.getStartTime())));
        }
        if (this.attachable.rotation.isEmpty()) {
            this.attachable.rotation.add(new StepSection(t, 0));
        }
        if (this.layer.isEmpty()) {
            this.layer.add(new StepSection(t, 1));
        }
        if (this.opacity.isEmpty()) {
            this.opacity.add(new StepSection(t, 1));
        }
        if (this.attachable.visibility.isEmpty()) {
            this.attachable.visibility.add(new BooleanSection(t, true));
        }
        if (this.attachable.scale.isEmpty()) {
            this.attachable.scale.add(new StepSection(t, 1));
        }
        if (this.attachable.attach.isEmpty()) {
            // Leave empty, it means this entity is not attached to any other entity
        }
    }

    registerOnClickCallback(onClick: (entity: TextureEntity) => void) {
        this.sprite.interactive = true;
        this.sprite.buttonMode = true;

        this.sprite.on('mousedown', () => {
            onClick(this)
        });
        this.sprite.on('touchstart', () => {
            onClick(this)
        });
    }

    update(time: number, delta: number, assets: Assets, screenHeight: number, worldToScreen: number) {
        if (time < this.startTime) {
            this.sprite.visible = false;
            return;
        }

        this.sprite.visible = this.attachable.getVisibility(time);
        if (!this.sprite.visible) {
            return;
        }

        this.sprite.texture = this.getTexture(time, assets.loadedTextures, assets.loadedAnimations);
        [this.sprite.x, this.sprite.y] = this.attachable.getXY(time, screenHeight, worldToScreen);
        this.sprite.width = this.width.getValue(time) * worldToScreen;
        this.sprite.height = this.height.getValue(time) * worldToScreen;
        this.sprite.angle = -this.attachable.getAngle(time);
        this.sprite.alpha = this.opacity.getValue(time);
        this.sprite.anchor.set(0.5, 0.5);
        this.sprite.zIndex = this.layer.getValue(time);

        let scale = this.attachable.getScale(time);
        this.sprite.scale.x = (this.sprite.width / this.sprite.texture.width) * scale;
        this.sprite.scale.y = (this.sprite.height / this.sprite.texture.height) * scale;
    }

    getTexture(time: number,
               loadedTextures: Map<string, Texture>,
               loadedAnimations: Map<string, TextureAnimation>): Texture {

        let textureName = this.texture.getValue(time);
        let prevSectionEndTime = this.texture.getPreviousSectionEndTime(time);
        let deltaTime = time - prevSectionEndTime;

        if (isTextureAnimation(textureName)) {
            return loadedAnimations.get(textureName)!.getTexture(deltaTime);
        } else {
            return loadedTextures.get(textureName)!;
        }
    }

    addSection(attribute: string, section: Section<any>) {
        switch (attribute) {
            case stringify(TextureEntityAttribute.X):
                this.x.add(section);
                return;
            case stringify(TextureEntityAttribute.Y):
                this.y.add(section);
                return;
            case stringify(TextureEntityAttribute.WIDTH):
                this.width.add(section);
                return;
            case stringify(TextureEntityAttribute.HEIGHT):
                this.height.add(section);
                return;
            case stringify(TextureEntityAttribute.ROTATION_DEG):
                this.attachable.rotation.add(section);
                return;
            case stringify(TextureEntityAttribute.TEXTURE):
                this.texture.add(section);
                return;
            case stringify(TextureEntityAttribute.LAYER):
                this.layer.add(section);
                return;
            case stringify(TextureEntityAttribute.OPACITY):
                this.opacity.add(section);
                return;
            case stringify(TextureEntityAttribute.VISIBILITY):
                this.attachable.visibility.add(section);
                return;
            case stringify(TextureEntityAttribute.SCALE):
                this.attachable.scale.add(section);
                return;
            case stringify(TextureEntityAttribute.ATTACH):
                this.attachable.attach.add(section);
                return;
        }
    }

    getCurve(attribute: string): Curve<any> {
        switch (attribute) {
            case stringify(TextureEntityAttribute.X):
                return this.x;
            case stringify(TextureEntityAttribute.Y):
                return this.y;
            case stringify(TextureEntityAttribute.WIDTH):
                return this.width;
            case stringify(TextureEntityAttribute.HEIGHT):
                return this.height;
            case stringify(TextureEntityAttribute.ROTATION_DEG):
                return this.rotation;
            case stringify(TextureEntityAttribute.TEXTURE):
                return this.texture;
            case stringify(TextureEntityAttribute.LAYER):
                return this.layer;
            case stringify(TextureEntityAttribute.OPACITY):
                return this.opacity;
            case stringify(TextureEntityAttribute.VISIBILITY):
                return this.visibility;
            case stringify(TextureEntityAttribute.SCALE):
                return this.scale;
            case stringify(TextureEntityAttribute.ATTACH):
                return this.attach;
            default:
                throw Error(`attribute ${attribute} is not valid`)
        }
    }

    private getAttributes(): Curve<any>[] {
        return [this.attachable.x, this.attachable.y, this.width, this.height, this.attachable.rotation,
            this.texture, this.layer, this.opacity, this.attachable.visibility, this.attachable.scale,
            this.attachable.attach];
    }

    getEndTime(): number {
        return findEndTime(this.getAttributes());
    }

    getStartTime(): number {
        return findStartTime(this.getAttributes());
    }
}

export function registerOnEntityClickShowPath(settings: ShowEntityPathSettings,
                                              entities: Map<string, Entity>,
                                              graphics: Graphics,
                                              screenHeight: number,
                                              worldToScreen: number) {
    let clickedEntity: TextureEntity | null;

    // Register onClick callback on all TextureEntities that can draw entity path
    entities.forEach((entity: Entity) => {
        if (entity instanceof TextureEntity) {

            entity.registerOnClickCallback((entity: TextureEntity) => {
                if (clickedEntity != null) {
                    // Remove the path and tint
                    clickedEntity.sprite.tint = 0xFFFFFF;
                    graphics.clear();
                    if (clickedEntity!.id === entity.id) {
                        clickedEntity = null;
                        return;
                    }
                }
                clickedEntity = entity;

                // Tint newly clicked entity
                clickedEntity.sprite.tint = settings.clickedEntityTint;

                // Draw entity path
                graphics.clear();
                graphics.beginFill(settings.pathColor);
                graphics.alpha = settings.pathAlpha;

                let startTime = clickedEntity.getStartTime();
                let endTime = clickedEntity.getEndTime();

                for (let t = startTime; t < endTime; t += settings.drawingTimeInterval) {
                    let [x, y] = clickedEntity.attachable.getXY(t, screenHeight, worldToScreen);
                    let size = settings.pathWidth * worldToScreen;
                    graphics.drawCircle(x, y, size);
                }
                graphics.endFill();
            });
        }
    });
}

