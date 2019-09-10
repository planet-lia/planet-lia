import {Curve, findEndTime, findStartTime, throwErrorIfEmpty} from "./curves/curve";
import {Container} from "pixi.js";
import {StepSection} from "./curves/sections/step";
import {Section} from "./curves/sections/section";
import {Attach} from "./attachSection";
import {Attachable} from "./attachable";
import {Entity, HUD_ID_PREFIX} from "./entity";
import {Emitter} from "pixi-particles";
import {BooleanSection} from "./curves/sections/boolean";
import {Assets} from "./assets";

export const PARTICLE_ID_PREFIX = "PARTICLE_";

export enum ParticleEntityAttribute {
    X,
    Y,
    ROTATION_DEG,
    LAYER,
    OPACITY,
    VISIBILITY,
    SCALE,
    ATTACH,
    EFFECT,
    EMIT
}

export function stringify(a: ParticleEntityAttribute): string {
    return ParticleEntityAttribute[a]
}

export function isParticleEntity(entityId: string): boolean {
    return entityId.startsWith(PARTICLE_ID_PREFIX) || entityId.startsWith(HUD_ID_PREFIX + PARTICLE_ID_PREFIX)
}

export class ParticleEntity implements Entity {
    id: string;

    container = new Container();
    emitter: Emitter | undefined;
    effectDuration = 0;
    defaultScale = 1;

    x = new Curve<number>();
    y = new Curve<number>();
    rotation = new Curve<number>();
    visibility = new Curve<boolean>();
    scale = new Curve<number>();
    // Defines to which other entity and how this
    // entity is attached (if to any).
    attach = new Curve<Attach>();
    layer = new Curve<number>();
    opacity = new Curve<number>();
    effect = new Curve<string>();
    emit = new Curve<boolean>();

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
    finishSetup(assets: Assets) {
        // Some curves should not be empty when initialized.
        throwErrorIfEmpty(this.x, 'x');
        throwErrorIfEmpty(this.y, 'y');
        throwErrorIfEmpty(this.effect, 'effect');
        throwErrorIfEmpty(this.emit, 'emit');

        this.startTime = this.getStartTime();
        let t = this.startTime;

        // Set default values for empty curves.
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

        // Use only the first effect in effect curve
        let effectName = this.effect.getValue(this.effect.getStartTime());
        let effect = assets.loadedParticleEffects.get(effectName)!;
        this.emitter = new Emitter(this.container, effect.textures, effect.settings);
        this.defaultScale = effect.scale;
        this.effectDuration = (this.emitter.emitterLifetime === -1)
            ? -1
            : this.emitter.emitterLifetime;
    }

    update(time: number, delta: number, assets: Assets, screenHeight: number, worldToScreen: number) {
        if (time < this.startTime) {
            this.container.visible = false;
            return;
        }
        this.container.visible = this.attachable.getVisibility(time);
        if (!this.container.visible) {
            return;
        }

        [this.container.x, this.container.y] = this.attachable.getXY(time, screenHeight, worldToScreen);
        this.container.angle = -this.attachable.getAngle(time);
        this.container.alpha = this.opacity.getValue(time);
        this.container.zIndex = this.layer.getValue(time);
        this.container.scale.set(this.attachable.getScale(time) * this.defaultScale);

        this.updateEmitter(time, delta);
    }

    updateEmitter(time: number, delta: number) {
        let isEmitting = this.emit.getValue(time);
        if (!isEmitting) {
            // Cleanup particles if they exist
            if (this.emitter!.particleCount > 0) {
                this.emitter!.cleanup();
                this.emitter!.emit = false;
            }
            return;
        }

        let emitTime = this.emit.getPreviousSectionEndTime(time);
        let emitEndTime = this.emit.getCurrentSectionEndTime(time);
        let previousTime = time - delta;
        let elapsedTime = time - emitTime;

        // Reconfigure emitter on big time jumps
        if (previousTime < emitTime || (emitEndTime !== null && previousTime >= emitEndTime)) {
            this.emitter!.cleanup();
            this.emitter!.emit = false;
            this.emitter!.playOnce();
            this.emitter!.update(time - emitTime);
        } else {
            // Reconfigure emitter when it is already finished but need to emmit again
            // (when playing backwards)
            if (!this.emitter!.emit && elapsedTime < this.emitter!.emitterLifetime) {
                this.emitter!.cleanup();
                this.emitter!.emit = false;
                this.emitter!.playOnce();
                this.emitter!.update(elapsedTime);
            } else {
                // When effect is continuous don't play it in reverse as
                // it will only play in reverse the existing particles
                // and not the ones emitted before and that were
                // already removed. Simply play it forwards.
                let updateDelta = (this.effectDuration === -1)
                    ? Math.abs(delta)
                    : delta;
                this.emitter!.update(updateDelta);
            }
        }
    }

    addSection(attribute: string, section: Section<any>) {
        switch (attribute) {
            case stringify(ParticleEntityAttribute.X):
                this.x.add(section);
                return;
            case stringify(ParticleEntityAttribute.Y):
                this.y.add(section);
                return;
            case stringify(ParticleEntityAttribute.ROTATION_DEG):
                this.attachable.rotation.add(section);
                return;
            case stringify(ParticleEntityAttribute.LAYER):
                this.layer.add(section);
                return;
            case stringify(ParticleEntityAttribute.OPACITY):
                this.opacity.add(section);
                return;
            case stringify(ParticleEntityAttribute.VISIBILITY):
                this.attachable.visibility.add(section);
                return;
            case stringify(ParticleEntityAttribute.SCALE):
                this.attachable.scale.add(section);
                return;
            case stringify(ParticleEntityAttribute.ATTACH):
                this.attachable.attach.add(section);
                return;
            case stringify(ParticleEntityAttribute.EMIT):
                this.emit.add(section);
                return;
            case stringify(ParticleEntityAttribute.EFFECT):
                this.effect.add(section);
                return;
        }
    }

    getCurve(attribute: string): Curve<any> {
        switch (attribute) {
            case stringify(ParticleEntityAttribute.X):
                return this.x;
            case stringify(ParticleEntityAttribute.Y):
                return this.y;
            case stringify(ParticleEntityAttribute.ROTATION_DEG):
                return this.rotation;
            case stringify(ParticleEntityAttribute.LAYER):
                return this.layer;
            case stringify(ParticleEntityAttribute.OPACITY):
                return this.opacity;
            case stringify(ParticleEntityAttribute.VISIBILITY):
                return this.visibility;
            case stringify(ParticleEntityAttribute.SCALE):
                return this.scale;
            case stringify(ParticleEntityAttribute.ATTACH):
                return this.attach;
            case stringify(ParticleEntityAttribute.EMIT):
                return this.emit;
            case stringify(ParticleEntityAttribute.EFFECT):
                return this.effect;
            default:
                throw Error(`attribute ${attribute} is not valid`)
        }
    }

    private getAttributes(): Curve<any>[] {
        return [this.attachable.x, this.attachable.y, this.attachable.rotation, this.effect, this.layer, this.opacity,
            this.attachable.visibility, this.attachable.scale, this.attachable.attach, this.effect, this.emit];
    }

    getEndTime(): number {
        return findEndTime(this.getAttributes());
    }

    getStartTime(): number {
        return findStartTime(this.getAttributes());
    }
}
