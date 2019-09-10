import {Texture} from "pixi.js";
import {errorIfUndefined} from "./curves/sections/section";
import {loadTexture} from "./assets";
import {EmitterConfig} from "pixi-particles";

export class ParticleEffect {
    textures: Texture[];
    scale: number;
    settings: EmitterConfig;

    constructor(textures: PIXI.Texture[], scale: number, settings: EmitterConfig) {
        this.textures = textures;
        this.scale = scale;
        this.settings = settings;
    }

    static parse(dict: any, effectPath: string): ParticleEffect {
        errorIfUndefined([dict.images, dict.scale, dict.settings]);

        let textures: Texture[] = [];
        dict.images.forEach(image => {
            textures.push(loadTexture(`${effectPath}/${image}`))
        });

        return new ParticleEffect(textures, dict.scale, dict.settings);
    }
}