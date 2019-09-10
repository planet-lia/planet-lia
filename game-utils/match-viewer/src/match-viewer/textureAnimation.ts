import {errorIfUndefined} from "./curves/sections/section";
import {Texture} from "pixi.js";
import {loadTexture} from "./assets";

export class TextureAnimation {
    frames: AnimationFrame[];
    loop: boolean;
    duration: number;

    constructor(frames: AnimationFrame[], loop: boolean) {
        this.frames = frames;
        this.loop = loop;

        this.duration = 0;
        this.frames.forEach((frame: AnimationFrame) => {
            this.duration += frame.duration;
        });
    }

    getTexture(deltaTime: number): Texture {
        // When animation that does not loop ends, use last frame
        if (!this.loop && deltaTime > this.duration) {
            return this.frames[this.frames.length - 1].texture;
        }

        // Find intermediate frame
        deltaTime = deltaTime % this.duration;
        let elapsed = 0;
        let texture = this.frames[0].texture;

        this.frames.forEach((frame: AnimationFrame) => {
            if (elapsed <= deltaTime) {
                texture = frame.texture;
            } else {
                return texture;
            }
            elapsed += frame.duration;
        });
        return texture;
    }

    static parse(dict: any, animationPath: string): TextureAnimation {
        errorIfUndefined([dict.frames, dict.loop]);

        let frames: AnimationFrame[] = [];
        dict.frames.forEach(frame => {
            frames.push(AnimationFrame.parse(frame, animationPath))
        });

        return new TextureAnimation(frames, dict.loop);
    }
}

export class AnimationFrame {
    path: string;
    texture: Texture;
    duration: number;

    constructor(path: string, duration: number, animationPath: string) {
        this.path = path;
        this.duration = duration;
        this.texture = loadTexture(`${animationPath}/${path}`);
    }

    static parse(dict: any, animationPath: string): AnimationFrame {
        errorIfUndefined([dict.path, dict.duration]);
        return new AnimationFrame(dict.path, dict.duration, animationPath);
    }
}

export function isTextureAnimation(path: string): boolean {
    return !path.includes(".");
}