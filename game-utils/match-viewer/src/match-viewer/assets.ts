import {MIPMAP_MODES, SCALE_MODES, Texture} from "pixi.js";
import {isTextureAnimation, TextureAnimation} from "./textureAnimation";
import {ParticleEffect} from "./particleEffect";
import {ParsedReplay} from "./parsedReplay";

export class Assets {
    path: string;
    loadedTextures = new Map<string, Texture>();
    loadedAnimations = new Map<string, TextureAnimation>();
    loadedParticleEffects = new Map<string, ParticleEffect>();

    constructor(path: string) {
        this.path = path;
    }
}

export function loadTexture(path: string): Texture {
    let texture = Texture.from(path, {scaleMode: SCALE_MODES.LINEAR});
    // Turning off mipmap helps to reduce flickering of textures
    texture.baseTexture.mipmap = MIPMAP_MODES.OFF;
    return texture;
}

export let loadAssets = async (replay: ParsedReplay,
                               assets: Assets,
                               callback: () => void) => {

    let texturesToLoad = replay.getUniqueTexturePaths();

    // Load textures & animations
    for (let textureName of Array.from(texturesToLoad.values())) {
        // Load animation
        if (isTextureAnimation(textureName)) {
            let animationPath = `${assets.path}/animations/${textureName}`;
            let request = await fetch(`${animationPath}/anim.json`);
            let data = await request.json();
            assets.loadedAnimations.set(textureName, TextureAnimation.parse(data, animationPath));
        }
        // Load texture
        else {
            let texturePath = `${assets.path}/images/${textureName}`;
            let texture = loadTexture(texturePath);
            assets.loadedTextures.set(textureName, texture);
        }
    }

    let particleEffectsToLoad = replay.getUniqueParticleEffects();

    // Load particle effects
    for (let effectName of Array.from(particleEffectsToLoad.values())) {
        let effectPath = `${assets.path}/particles`;
        let r = await fetch(`${effectPath}/${effectName}.json`);
        let data = await r.json();
        assets.loadedParticleEffects.set(effectName, ParticleEffect.parse(data, effectPath));
    }

    callback();
};
